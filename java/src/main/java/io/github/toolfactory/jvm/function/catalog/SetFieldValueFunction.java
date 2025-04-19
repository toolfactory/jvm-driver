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
package io.github.toolfactory.jvm.function.catalog;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import io.github.toolfactory.jvm.function.InitializeException;
import io.github.toolfactory.jvm.function.template.TriConsumer;
import io.github.toolfactory.jvm.util.Classes;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Strings;
import io.github.toolfactory.narcissus.Narcissus;


@SuppressWarnings("all")
public interface SetFieldValueFunction extends TriConsumer<Object, Field, Object> {

	public abstract static class Abst implements SetFieldValueFunction {

		public Abst(Map<Object, Object> context) {
			ObjectProvider functionProvider = ObjectProvider.get(context);
		}

	}

	public static class ForJava7 extends Abst {
		final UnsafeWrapper unsafeWrapper;

		public ForJava7(Map<Object, Object> context) {
			super(context);
			unsafeWrapper = ObjectProvider.get(context).getOrBuildObject(UnsafeWrapper.class, context);
		}

		@Override
		public void accept(Object origTarget, Field field, Object value) {
			if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
				throw new IllegalArgumentException(Strings.compile("Value {} is not assignable to {}", value , field.getName()));
			}
			Class<?> fieldDeclaringClass = field.getDeclaringClass();
			long fieldOffset;
			Object target;
			if (Modifier.isStatic(field.getModifiers())) {
				fieldOffset = unsafeWrapper.staticFieldOffset(field);
				target = fieldDeclaringClass;
			} else {
				if ((target = origTarget) == null) {
					throw new IllegalArgumentException("Target object is null");
				}
				Class<?> targetObjectClass = target.getClass();
	 			if (!Classes.isAssignableFrom(fieldDeclaringClass, targetObjectClass)) {
					throw new IllegalArgumentException("Target object class " + targetObjectClass + " is not assignable to " + fieldDeclaringClass);
				}
				fieldOffset = unsafeWrapper.objectFieldOffset(field);

			}
			Class<?> cls = field.getType();
			if(!cls.isPrimitive()) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafeWrapper.putObject(target, fieldOffset, value);
				} else {
					unsafeWrapper.putObjectVolatile(target, fieldOffset, value);
				}
			} else if (cls == short.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafeWrapper.putShort(target, fieldOffset, ((Short)value).shortValue());
				} else {
					unsafeWrapper.putShortVolatile(target, fieldOffset, ((Short)value).shortValue());
				}
			} else if (cls == int.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafeWrapper.putInt(target, fieldOffset, ((Integer)value).intValue());
				} else {
					unsafeWrapper.putIntVolatile(target, fieldOffset, ((Integer)value).intValue());
				}
			} else if (cls == long.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafeWrapper.putLong(target, fieldOffset, ((Long)value).longValue());
				} else {
					unsafeWrapper.putLongVolatile(target, fieldOffset, ((Long)value).longValue());
				}
			} else if (cls == float.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafeWrapper.putFloat(target, fieldOffset, ((Float)value).floatValue());
				} else {
					unsafeWrapper.putFloatVolatile(target, fieldOffset, ((Float)value).floatValue());
				}
			} else if (cls == double.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafeWrapper.putDouble(target, fieldOffset, ((Double)value).doubleValue());
				} else {
					unsafeWrapper.putDoubleVolatile(target, fieldOffset, ((Double)value).doubleValue());
				}
			} else if (cls == boolean.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafeWrapper.putBoolean(target, fieldOffset, ((Boolean)value).booleanValue());
				} else {
					unsafeWrapper.putBooleanVolatile(target, fieldOffset, ((Boolean)value).booleanValue());
				}
			} else if (cls == byte.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafeWrapper.putByte(target, fieldOffset, ((Byte)value).byteValue());
				} else {
					unsafeWrapper.putByteVolatile(target, fieldOffset, ((Byte)value).byteValue());
				}
			} else if (cls == char.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafeWrapper.putChar(target, fieldOffset, ((Character)value).charValue());
				} else {
					unsafeWrapper.putCharVolatile(target, fieldOffset, ((Character)value).charValue());
				}
			}
		}

	}


	public interface Native extends SetFieldValueFunction{

		public static class ForJava7 extends Abst implements Native {

			public ForJava7(Map<Object, Object> context) throws InitializeException {
				super(context);
				checkNativeEngine();
			}

			protected void checkNativeEngine() throws InitializeException {
				if (!Narcissus.libraryLoaded) {
					throw new InitializeException(
						Strings.compile(
							"Could not initialize the native engine {}",
							io.github.toolfactory.narcissus.Narcissus.class.getName()
						)
					);
				}
			}

			@Override
			public void accept(Object target, Field field, Object value) {
				if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
					throw new IllegalArgumentException(Strings.compile("Value {} is not assignable to {}", value , field.getName()));
				}
				if (Modifier.isStatic(field.getModifiers())) {
					io.github.toolfactory.narcissus.Narcissus.setStaticField(field, value);
				} else {
					io.github.toolfactory.narcissus.Narcissus.setField(target, field, value);
				}
			}
		}
	}
}
