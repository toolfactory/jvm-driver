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
package io.github.toolfactory.jvm.narcissus;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import io.github.toolfactory.jvm.NativeEngine;
import io.github.toolfactory.narcissus.Narcissus;


/**
 * The native engine based on <a href="https://github.com/toolfactory/narcissus">Narcissus</a>, the JNI engine used
 * by ToolFactory JVM Driver before version 10.0.0, whose native libraries support Windows (x64), Linux (x64, arm64)
 * and macOS (x64, arm64).
 * <p>
 * The {@code jvm-driver.properties} file of this artifact makes the drivers use this engine instead of the default
 * one: putting the artifact in the class path, or requiring its module, is enough.
 */
public class NarcissusEngine extends NativeEngine {

	/**
	 * Creates the engine.
	 *
	 * @throws UnsatisfiedLinkError if Narcissus could not load its native library on this platform
	 */
	public NarcissusEngine() {
		if (!Narcissus.libraryLoaded) {
			throw new UnsatisfiedLinkError("Narcissus could not load its native library");
		}
	}


	@Override
	public Object allocateInstance(Class<?> cls) {
		return Narcissus.allocateInstance(cls);
	}


	@Override
	public Field getDeclaredField(Class<?> cls, String name, Class<?> type) throws NoSuchFieldException {
		if (type == null) {
			throw new IllegalArgumentException("Field type is null");
		}
		Field field = Narcissus.findField(cls, name);
		if (!field.getType().equals(type)) {
			throw new NoSuchFieldException(name);
		}
		return field;
	}


	@Override
	public Object getFieldValue(Object target, Field field) {
		if (Modifier.isStatic(field.getModifiers())) {
			return Narcissus.getStaticField(field);
		}
		return Narcissus.getField(target, field);
	}


	@Override
	public void setFieldValue(Object target, Field field, Object value) {
		if (Modifier.isStatic(field.getModifiers())) {
			Narcissus.setStaticField(field, value);
		} else {
			Narcissus.setField(target, field, value);
		}
	}

}
