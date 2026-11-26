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

import org.burningwave.jni.Executor;


/**
 * The default native engine: it is based on {@code org.burningwave.jni.Executor}, the JNI engine of Burningwave
 * supplied by the {@code org.burningwave:jni-executor} artifact, whose native libraries support Windows (x86, x64,
 * aarch64), Linux (x86, x64, aarch64) and macOS (x64, aarch64).
 * <p>
 * On the module path it needs the module {@code org.burningwave.jni}, which the module of ToolFactory JVM Driver
 * requires only statically: add {@code requires org.burningwave.jni;} to your {@code module-info.java}.
 */
public class BurningwaveEngine extends NativeEngine {
	private final Executor executor;


	/**
	 * Creates the engine, loading the native library of the current platform if it has not been loaded yet.
	 *
	 * @throws LinkageError if the native library of the current platform cannot be loaded: an
	 * {@link ExceptionInInitializerError} the first time, a {@link NoClassDefFoundError} afterwards
	 */
	public BurningwaveEngine() {
		executor = Executor.getInstance();
	}


	@Override
	public Object allocateInstance(Class<?> cls) {
		return executor.allocateInstance(cls);
	}


	@Override
	public Field getDeclaredField(Class<?> cls, String name, Class<?> type) throws NoSuchFieldException {
		if (type == null) {
			throw new IllegalArgumentException("Field type is null");
		}
		String signature = getSignature(type);
		// The JNI functions that find the fields throw NoSuchFieldError if the field does not exist, while if the field
		// is static, the function for the instance fields throws NoSuchFieldError on HotSpot and
		// IncompatibleClassChangeError, the superclass of NoSuchFieldError, on OpenJ9
		try {
			return executor.getDeclaredField(cls, name, signature);
		} catch (IncompatibleClassChangeError exc) {
			try {
				return executor.getDeclaredStaticField(cls, name, signature);
			} catch (IncompatibleClassChangeError exc2) {
				NoSuchFieldException noSuchFieldException = new NoSuchFieldException(name);
				noSuchFieldException.initCause(exc2);
				throw noSuchFieldException;
			}
		}
	}


	@Override
	public Object getFieldValue(Object target, Field field) {
		return executor.getFieldValue(target, field);
	}


	@Override
	public void setFieldValue(Object target, Field field, Object value) {
		executor.setFieldValue(target, field, value);
	}


	private static String getSignature(Class<?> type) {
		if (type.isPrimitive()) {
			if (type == int.class) {
				return "I";
			} else if (type == long.class) {
				return "J";
			} else if (type == boolean.class) {
				return "Z";
			} else if (type == byte.class) {
				return "B";
			} else if (type == char.class) {
				return "C";
			} else if (type == short.class) {
				return "S";
			} else if (type == float.class) {
				return "F";
			} else if (type == double.class) {
				return "D";
			}
			throw new IllegalArgumentException(type + " is not a valid field type");
		} else if (type.isArray()) {
			return type.getName().replace('.', '/');
		}
		return "L" + type.getName().replace('.', '/') + ";";
	}

}
