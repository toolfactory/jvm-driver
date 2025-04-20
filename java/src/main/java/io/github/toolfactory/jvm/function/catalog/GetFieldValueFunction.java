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
import io.github.toolfactory.jvm.function.template.BiFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Strings;
import io.github.toolfactory.narcissus.Narcissus;


@SuppressWarnings("all")
public interface GetFieldValueFunction extends BiFunction<Object, Field, Object> {


	public static class ForJava7 implements GetFieldValueFunction {
		protected UnsafeWrapper unsafeWrapper;
		protected ThrowExceptionFunction throwExceptionFunction;

		public ForJava7(Map<Object, Object> context) {
			unsafeWrapper = ObjectProvider.get(context).getOrBuildObject(UnsafeWrapper.class, context);
			throwExceptionFunction = ObjectProvider.get(context).getOrBuildObject(ThrowExceptionFunction.class, context);
		}

		@Override
		public Object apply(Object target, Field field) {
			try {
				target = Modifier.isStatic(field.getModifiers())?
					field.getDeclaringClass() :
					target;
				long fieldOffset = Modifier.isStatic(field.getModifiers())?
					unsafeWrapper.staticFieldOffset(field) :
					unsafeWrapper.objectFieldOffset(field);
				Class<?> cls = field.getType();
				if(!cls.isPrimitive()) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return unsafeWrapper.getObject(target, fieldOffset);
					} else {
						return unsafeWrapper.getObjectVolatile(target, fieldOffset);
					}
				} else if (cls == short.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Short.valueOf(unsafeWrapper.getShort(target, fieldOffset));
					} else {
						return Short.valueOf(unsafeWrapper.getShortVolatile(target, fieldOffset));
					}
				} else if (cls == int.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Integer.valueOf(unsafeWrapper.getInt(target, fieldOffset));
					} else {
						return Integer.valueOf(unsafeWrapper.getIntVolatile(target, fieldOffset));
					}
				} else if (cls == long.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Long.valueOf(unsafeWrapper.getLong(target, fieldOffset));
					} else {
						return Long.valueOf(unsafeWrapper.getLongVolatile(target, fieldOffset));
					}
				} else if (cls == float.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Float.valueOf(unsafeWrapper.getFloat(target, fieldOffset));
					} else {
						return Float.valueOf(unsafeWrapper.getFloatVolatile(target, fieldOffset));
					}
				} else if (cls == double.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Double.valueOf(unsafeWrapper.getDouble(target, fieldOffset));
					} else {
						return Double.valueOf(unsafeWrapper.getDoubleVolatile(target, fieldOffset));
					}
				} else if (cls == boolean.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Boolean.valueOf(unsafeWrapper.getBoolean(target, fieldOffset));
					} else {
						return Boolean.valueOf(unsafeWrapper.getBooleanVolatile(target, fieldOffset));
					}
				} else if (cls == byte.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Byte.valueOf(unsafeWrapper.getByte(target, fieldOffset));
					} else {
						return Byte.valueOf(unsafeWrapper.getByteVolatile(target, fieldOffset));
					}
				} else {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Character.valueOf(unsafeWrapper.getChar(target, fieldOffset));
					} else {
						return Character.valueOf(unsafeWrapper.getCharVolatile(target, fieldOffset));
					}
				}
			} catch (Throwable exc) {
				return throwExceptionFunction.apply(exc);
			}
		}
	}

	public static interface Native extends GetFieldValueFunction {

		public static class ForJava7 implements Native {

			public ForJava7(Map<Object, Object> context) throws InitializeException {
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
			public Object apply(Object target, Field field) {
				if (Modifier.isStatic(field.getModifiers())) {
					return io.github.toolfactory.narcissus.Narcissus.getStaticField(field);
				} else {
					return io.github.toolfactory.narcissus.Narcissus.getField(target, field);
				}
			}
		}

	}
}
