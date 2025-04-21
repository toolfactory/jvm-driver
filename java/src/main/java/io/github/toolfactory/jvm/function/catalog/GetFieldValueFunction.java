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
import io.github.toolfactory.jvm.function.template.Supplier;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Strings;
import io.github.toolfactory.narcissus.Narcissus;


@SuppressWarnings("all")
public interface GetFieldValueFunction extends BiFunction<Object, Field, Object> {


	public static class ForJava7 implements GetFieldValueFunction {
		protected sun.misc.Unsafe unsafe;

		public ForJava7(Map<Object, Object> context) {
			unsafe = ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
		}

		@Override
		public Object apply(Object target, Field field) {
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			target = isStatic?
				field.getDeclaringClass() :
				target;
			Long fieldOffset = isStatic?
				unsafe.staticFieldOffset(field) :
				unsafe.objectFieldOffset(field);
			return getByUnsafe(target, field, fieldOffset, field.getType());
		}

		protected Object getByUnsafe(Object target, Field field, long fieldOffset, Class<?> cls) {
			if(!cls.isPrimitive()) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return unsafe.getObject(target, fieldOffset);
				} else {
					return unsafe.getObjectVolatile(target, fieldOffset);
				}
			} else if (cls == short.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Short.valueOf(unsafe.getShort(target, fieldOffset));
				} else {
					return Short.valueOf(unsafe.getShortVolatile(target, fieldOffset));
				}
			} else if (cls == int.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Integer.valueOf(unsafe.getInt(target, fieldOffset));
				} else {
					return Integer.valueOf(unsafe.getIntVolatile(target, fieldOffset));
				}
			} else if (cls == long.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Long.valueOf(unsafe.getLong(target, fieldOffset));
				} else {
					return Long.valueOf(unsafe.getLongVolatile(target, fieldOffset));
				}
			} else if (cls == float.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Float.valueOf(unsafe.getFloat(target, fieldOffset));
				} else {
					return Float.valueOf(unsafe.getFloatVolatile(target, fieldOffset));
				}
			} else if (cls == double.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Double.valueOf(unsafe.getDouble(target, fieldOffset));
				} else {
					return Double.valueOf(unsafe.getDoubleVolatile(target, fieldOffset));
				}
			} else if (cls == boolean.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Boolean.valueOf(unsafe.getBoolean(target, fieldOffset));
				} else {
					return Boolean.valueOf(unsafe.getBooleanVolatile(target, fieldOffset));
				}
			} else if (cls == byte.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Byte.valueOf(unsafe.getByte(target, fieldOffset));
				} else {
					return Byte.valueOf(unsafe.getByteVolatile(target, fieldOffset));
				}
			} else {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Character.valueOf(unsafe.getChar(target, fieldOffset));
				} else {
					return Character.valueOf(unsafe.getCharVolatile(target, fieldOffset));
				}
			}
		}
	}

	public static class ForJava25 extends ForJava7 {
		protected SetAccessibleFunction setAccessibleFunction;
		protected ThrowExceptionFunction throwExceptionFunction;
		protected Supplier<SetAccessibleFunction> setAccessibleFunctionSupplier;

		public ForJava25(final Map<Object, Object> context) {
			super(context);
			throwExceptionFunction = ObjectProvider.get(context).getOrBuildObject(ThrowExceptionFunction.class, context);
			setAccessibleFunctionSupplier = new Supplier<SetAccessibleFunction>() {
				@Override
				public SetAccessibleFunction get() {
					return ObjectProvider.get(context).getOrBuildObject(SetAccessibleFunction.class, context);
				}
			};
		}

		@Override
		public Object apply(Object target, Field field) {
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			try {
				target = isStatic?
					field.getDeclaringClass() :
					target;
				Long fieldOffset = isStatic?
					unsafe.staticFieldOffset(field) :
					unsafe.objectFieldOffset(field);
				return getByUnsafe(target, field, fieldOffset,  field.getType());
			} catch (UnsupportedOperationException exc) {
				try {
					setAccessible(field);
					if (isStatic) {
						return field.get(null);
					} else {
						return field.get(target);
					}
				} catch (Throwable exc2) {
					return throwExceptionFunction.apply(exc2);
				}

			}
		}

		protected void setAccessible(Field field) throws Throwable {
			try {
				setAccessibleFunction.accept(field, true);
			} catch (NullPointerException exc) {
				if (setAccessibleFunction == null) {
					setAccessibleFunction = setAccessibleFunctionSupplier.get();
					setAccessible(field);
				} else {
					throwExceptionFunction.accept(exc);
				}
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
