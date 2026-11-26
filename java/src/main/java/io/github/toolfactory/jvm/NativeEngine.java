/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.toolfactory.jvm;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;

import io.github.toolfactory.jvm.function.InitializeException;
import io.github.toolfactory.jvm.util.Properties;
import io.github.toolfactory.jvm.util.Strings;


/**
 * The JNI engine on which the native functions of the dynamic, hybrid and native drivers are based: it allocates
 * instances without calling their constructors, finds fields and reads and writes their values, ignoring the access
 * modifiers and the encapsulation of the modules.
 * <p>
 * The engine is chosen through the {@value #ENGINE_CLASS_PROPERTY} property of the {@code jvm-driver.properties} files
 * found in the class path, merged according to their {@code priority-of-this-configuration} property, as the classes
 * of the drivers are: ToolFactory JVM Driver sets it to {@link BurningwaveEngine}, with priority 0, and the
 * {@code jvm-driver-narcissus-plugin} artifact to the engine based on Narcissus, with priority 1. The engine is
 * created the first time that {@link #getInstance()} is called and the same instance is used by all the drivers.
 * If the chosen engine cannot be created no other engine is tried: {@link #getInstance()} throws an
 * {@link InitializeException}, so the native functions, and the drivers that need them, cannot be initialized.
 * <p>
 * A class that extends this one must be public and must have a public constructor without parameters.
 */
public abstract class NativeEngine {
	/**
	 * The property of the {@code jvm-driver.properties} files that holds the name of the class of the engine.
	 */
	public static final String ENGINE_CLASS_PROPERTY = "native-engine.class";

	private static volatile NativeEngine instance;
	private static InitializeException initializationError;


	/**
	 * Allocates an instance of the class without calling any of its constructors.
	 *
	 * @param cls the class to instantiate
	 * @return the new instance
	 */
	public abstract Object allocateInstance(Class<?> cls);

	/**
	 * Finds a field by its name and its type, even if the reflection filters of the JDK hide it, and also if it is
	 * declared by a superclass of the class.
	 *
	 * @param cls the class that declares the field, or a subclass of it
	 * @param name the name of the field
	 * @param type the type of the field
	 * @return the field, static or not
	 * @throws NoSuchFieldException if the class has no field with that name and that type
	 */
	public abstract Field getDeclaredField(Class<?> cls, String name, Class<?> type) throws NoSuchFieldException;

	/**
	 * Reads the value of a field, static or not, ignoring its access modifiers.
	 *
	 * @param target the object that holds the field: it is ignored if the field is static
	 * @param field the field to read
	 * @return the value of the field: the value of a primitive field is wrapped in its wrapper class
	 */
	public abstract Object getFieldValue(Object target, Field field);

	/**
	 * Writes the value of a field, static or not, ignoring its access modifiers, even if the field is final.
	 *
	 * @param target the object that holds the field: it is ignored if the field is static
	 * @param field the field to write
	 * @param value the new value: for a primitive field, an instance of its wrapper class
	 */
	public abstract void setFieldValue(Object target, Field field, Object value);


	/**
	 * Returns the engine chosen through the {@value #ENGINE_CLASS_PROPERTY} property, creating it the first time.
	 *
	 * @return the engine, always the same instance
	 * @throws InitializeException if the engine cannot be created, for example because its native library cannot be
	 * loaded on this platform: every following call throws an exception with the same cause
	 */
	public static NativeEngine getInstance() throws InitializeException {
		NativeEngine engine = instance;
		if (engine != null) {
			return engine;
		}
		synchronized (NativeEngine.class) {
			if (instance == null) {
				if (initializationError == null) {
					String className = null;
					try {
						className = retrieveClassName();
						instance = create(className);
					} catch (Throwable exc) {
						initializationError = new InitializeException(
							className != null ?
								Strings.compile("Could not initialize the native engine {}", className) :
								"Could not read the name of the native engine from the jvm-driver.properties files",
							exc
						);
					}
				}
				if (initializationError != null) {
					throw new InitializeException(initializationError.getMessage(), initializationError.getCause());
				}
			}
			return instance;
		}
	}


	private static String retrieveClassName() throws Throwable {
		java.util.Properties configuration = Properties.loadFromResourcesAndMerge(
			"jvm-driver.properties",
			"priority-of-this-configuration",
			getClassLoaders()
		);
		String className = configuration.getProperty(ENGINE_CLASS_PROPERTY);
		if (className == null || className.trim().isEmpty()) {
			return BurningwaveEngine.class.getName();
		}
		return className.trim();
	}


	private static NativeEngine create(String className) throws Throwable {
		Class<?> engineClass = null;
		ClassNotFoundException classNotFoundException = null;
		for (ClassLoader classLoader : getClassLoaders()) {
			try {
				engineClass = Class.forName(className, true, classLoader);
				break;
			} catch (ClassNotFoundException exc) {
				if (classNotFoundException == null) {
					classNotFoundException = exc;
				}
			}
		}
		if (engineClass == null) {
			throw classNotFoundException;
		}
		if (!NativeEngine.class.isAssignableFrom(engineClass)) {
			throw new ClassCastException(
				Strings.compile("{} does not extend {}", engineClass.getName(), NativeEngine.class.getName())
			);
		}
		try {
			return (NativeEngine)engineClass.getDeclaredConstructor().newInstance();
		} catch (InvocationTargetException exc) {
			throw exc.getCause();
		}
	}


	private static Set<ClassLoader> getClassLoaders() {
		Set<ClassLoader> classLoaders = new LinkedHashSet<>();
		ClassLoader classLoader = NativeEngine.class.getClassLoader();
		if (classLoader != null) {
			classLoaders.add(classLoader);
		}
		classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			classLoaders.add(classLoader);
		}
		if (classLoaders.isEmpty()) {
			classLoaders.add(ClassLoader.getSystemClassLoader());
		}
		return classLoaders;
	}

}
