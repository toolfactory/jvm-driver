/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2021 Luke Hutchison, Roberto Gentili
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


import java.io.Closeable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.toolfactory.jvm.util.CleanableSupplier;
import io.github.toolfactory.jvm.util.Properties;


public interface Driver extends Closeable {
	
	public <T> T allocateInstance(Class<?> cls);

	public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode);

	public Class<?> getBuiltinClassLoaderClass();

	public Class<?> getClassLoaderDelegateClass();

	public MethodHandles.Lookup getConsulter(Class<?> cls);

	public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);

	public Field[] getDeclaredFields(Class<?> cls);

	public Method[] getDeclaredMethods(Class<?> cls);

	public <T> T getFieldValue(Object target, Field field);

	public Package getPackage(ClassLoader classLoader, String packageName);

	public <T> T invoke(Method method, Object target, Object[] params);

	public boolean isBuiltinClassLoader(ClassLoader classLoader);

	public boolean isClassLoaderDelegate(ClassLoader classLoader);

	public <T> T newInstance(Constructor<T> ctor, Object[] params);

	public CleanableSupplier<Collection<Class<?>>> getLoadedClassesRetriever(ClassLoader classLoader);

	public Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader);
	
	public void setAccessible(AccessibleObject object, boolean flag);
	
	public void setFieldValue(Object target, Field field, Object value);

	public <T> T throwException(Object exceptionOrMessage, Object... placeHolderReplacements);

	@Override
	public void close();

	
	@SuppressWarnings("unchecked")
	public static class Factory {
		private static Map<String, Constructor<? extends Driver>> driverConstructors;

		
		static {
			try {
				Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
				classLoaders.add(Factory.class.getClassLoader());
				classLoaders.add(Thread.currentThread().getContextClassLoader());
				java.util.Properties configuration = Properties.loadFromResourcesAndMerge(
					"jvm-driver.properties",
					"priority-of-this-configuration-file",
					classLoaders.toArray(new ClassLoader[classLoaders.size()])
				);
				driverConstructors = new ConcurrentHashMap<String, Constructor<? extends Driver>>();
				setDriverClass("defaultDriverClass", configuration.getProperty("default-driver.class"));
				setDriverClass("hybridDriverClass", configuration.getProperty("hybrid-driver.class"));
				setDriverClass("nativeDriverClass", configuration.getProperty("native-driver.class"));
			} catch (Throwable exc) {
				throw new FactoryException(exc);
			}
		}
		
		
		public static Driver getNew() {
			try {
				try {
					return getNewDefault();
				} catch (InitializeException exc) {
					return getNewHybrid(); 
				}
			} catch (InitializeException exc) {
				return getNewNative();
			}
		}
		
		public static <D extends Driver> D getNew(String className) throws Throwable {
			return (D)Class.forName(className).getDeclaredConstructor().newInstance();
		}
		
		private static void setDriverClass(String name, String className) {
			try {
				setDriverClass(name, Class.forName(className));
			} catch (ClassNotFoundException exc) {
				throw new FactoryException(exc);
			}
		}
		
		private static void setDriverClass(String name, Class<?> cls) {
			try {
				driverConstructors.put(
					name, 
					(Constructor<? extends Driver>)cls.getDeclaredConstructor()
				);
			} catch (NoSuchMethodException | SecurityException exc) {
				throw new FactoryException(exc);
			}
		}
		
		public static void setDefaultDriverClass(Class<? extends Driver> cls) {
			setDriverClass("defaultDriverClass", cls);
		}
		
		public static void setDefaultDriverClass(String className) {
			setDriverClass("defaultDriverClass", className);
		}
		
		public static void setHybridDriverClass(Class<? extends Driver> cls) {
			setDriverClass("hybridDriverClass", cls);
		}
		
		public static void setHybridDriverClass(String className) {
			setDriverClass("hybridDriverClass", className);
		}
		
		public static void setNativeDriverClass(Class<? extends Driver> cls) {
			setDriverClass("nativeDriverClass", cls);
		}
		
		public static void setNativeDriverClass(String className) {
			setDriverClass("nativeDriverClass", className);
		}
		
		public static <D extends Driver> D getNewDefault() {
			try {
				return ((D)driverConstructors.get("defaultDriverClass").newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException exc) {
				throw new InstantiateException(exc);
			}
		}
		
		public static <D extends Driver> D getNewHybrid() {
			try {
				return ((D)driverConstructors.get("hybridDriverClass").newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException exc) {
				throw new InstantiateException(exc);
			}
		}
		
		public static <D extends Driver> D getNewNative() {
			try {
				return ((D)driverConstructors.get("nativeDriverClass").newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException exc) {
				throw new InstantiateException(exc);
			}
		}
		
		public static class	FactoryException extends RuntimeException {

			private static final long serialVersionUID = 6332920978175279534L;
			
			public FactoryException(Throwable cause) {
		        super(cause);
		    }

		}
	}
	
	public static class InitializeException extends RuntimeException {

		private static final long serialVersionUID = -1351844562568567842L;

		public InitializeException(String message, Throwable cause) {
	        super(message, cause);
	    }
		
		public InitializeException(String message) {
	        super(message);
	    }

	}
	
	public static class	InstantiateException extends RuntimeException {

		private static final long serialVersionUID = 558903509767014098L;

		public InstantiateException(Throwable cause) {
	        super(cause);
	    }

	}
	
}