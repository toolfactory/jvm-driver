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
import io.github.toolfactory.jvm.function.template.Supplier;
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
		final sun.misc.Unsafe unsafe;

		public ForJava7(Map<Object, Object> context) {
			super(context);
			unsafe = ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
		}

		@Override
		public void accept(Object origTarget, Field field, Object value) {
			if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
				throw new IllegalArgumentException(Strings.compile("Value {} is not assignable to {}", value , field.getName()));
			}
			Class<?> fieldDeclaringClass = field.getDeclaringClass();
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			long fieldOffset;
			Object target;
			if (isStatic) {
				fieldOffset = unsafe.staticFieldOffset(field);
				target = fieldDeclaringClass;
			} else {
				if ((target = origTarget) == null) {
					throw new IllegalArgumentException("Target object is null");
				}
				Class<?> targetObjectClass = target.getClass();
	 			if (!Classes.isAssignableFrom(fieldDeclaringClass, targetObjectClass)) {
					throw new IllegalArgumentException("Target object class " + targetObjectClass + " is not assignable to " + fieldDeclaringClass);
				}
				fieldOffset = unsafe.objectFieldOffset(field);

			}
			setByUnsafe(field, value, fieldOffset, target, field.getType());
		}

		protected void setByUnsafe(Field field, Object value, long fieldOffset, Object target, Class<?> cls) {
			if(!cls.isPrimitive()) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafe.putObject(target, fieldOffset, value);
				} else {
					unsafe.putObjectVolatile(target, fieldOffset, value);
				}
			} else if (cls == short.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafe.putShort(target, fieldOffset, ((Short)value).shortValue());
				} else {
					unsafe.putShortVolatile(target, fieldOffset, ((Short)value).shortValue());
				}
			} else if (cls == int.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafe.putInt(target, fieldOffset, ((Integer)value).intValue());
				} else {
					unsafe.putIntVolatile(target, fieldOffset, ((Integer)value).intValue());
				}
			} else if (cls == long.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafe.putLong(target, fieldOffset, ((Long)value).longValue());
				} else {
					unsafe.putLongVolatile(target, fieldOffset, ((Long)value).longValue());
				}
			} else if (cls == float.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafe.putFloat(target, fieldOffset, ((Float)value).floatValue());
				} else {
					unsafe.putFloatVolatile(target, fieldOffset, ((Float)value).floatValue());
				}
			} else if (cls == double.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafe.putDouble(target, fieldOffset, ((Double)value).doubleValue());
				} else {
					unsafe.putDoubleVolatile(target, fieldOffset, ((Double)value).doubleValue());
				}
			} else if (cls == boolean.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafe.putBoolean(target, fieldOffset, ((Boolean)value).booleanValue());
				} else {
					unsafe.putBooleanVolatile(target, fieldOffset, ((Boolean)value).booleanValue());
				}
			} else if (cls == byte.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafe.putByte(target, fieldOffset, ((Byte)value).byteValue());
				} else {
					unsafe.putByteVolatile(target, fieldOffset, ((Byte)value).byteValue());
				}
			} else if (cls == char.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					unsafe.putChar(target, fieldOffset, ((Character)value).charValue());
				} else {
					unsafe.putCharVolatile(target, fieldOffset, ((Character)value).charValue());
				}
			}
		}

	}

	public static class ForJava25 extends ForJava7 {
		protected ThrowExceptionFunction throwExceptionFunction;
		protected Field modifiersField;
		protected Field fieldFlags;
		protected GetDeclaredFieldFunction getDeclaredFieldFunction;
		protected Supplier<GetDeclaredFieldFunction> getDeclaredFieldFunctionSupplier;
		protected SetAccessibleFunction setAccessibleFunction;
		protected Supplier<SetAccessibleFunction> setAccessibleFunctionSupplier;

		public ForJava25(final Map<Object, Object> context) throws Throwable {
			super(context);
			throwExceptionFunction = ObjectProvider.get(context).getOrBuildObject(ThrowExceptionFunction.class, context);
			setAccessibleFunctionSupplier = new Supplier<SetAccessibleFunction>() {
				@Override
				public SetAccessibleFunction get() {
					return ObjectProvider.get(context).getOrBuildObject(SetAccessibleFunction.class, context);
				}
			};
			getDeclaredFieldFunctionSupplier = new Supplier<GetDeclaredFieldFunction>() {
				@Override
				public GetDeclaredFieldFunction get() {
					return ObjectProvider.get(context).getOrBuildObject(GetDeclaredFieldFunction.class, context);
				}
			};
		}

		@Override
		public void accept(Object origTarget, Field field, Object value) {
			Class<?> fieldType = field.getType();
			if(value != null && !Classes.isAssignableFrom(fieldType, value.getClass())) {
				throw new IllegalArgumentException(Strings.compile("Value {} is not assignable to {}", value , field.getName()));
			}
			Class<?> fieldDeclaringClass = field.getDeclaringClass();
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			Object target;
			if (isStatic) {
				target = fieldDeclaringClass;
			} else {
				if ((target = origTarget) == null) {
					throw new IllegalArgumentException("Target object is null");
				}
				Class<?> targetObjectClass = target.getClass();
	 			if (!Classes.isAssignableFrom(fieldDeclaringClass, targetObjectClass)) {
					throw new IllegalArgumentException("Target object class " + targetObjectClass + " is not assignable to " + fieldDeclaringClass);
				}
			}
			try {
				Long fieldOffset;
				if (isStatic) {
					fieldOffset = unsafe.staticFieldOffset(field);
				} else {
					fieldOffset = unsafe.objectFieldOffset(field);
				}
				setByUnsafe(field, value, fieldOffset, target, field.getType());
			} catch (UnsupportedOperationException exc) {
				try {
					setByReflection(field, fieldType, isStatic, target, value);
				} catch (Throwable exc2) {
					throwExceptionFunction.accept(exc2);
				}
			}
		}

		protected void setByReflection(Field field, Class<?> fieldType, boolean isStatic, Object target, Object value) throws Throwable {
			setAccessible(field);
			int modifiers = field.getModifiers();
			Field modifiersField = null;
			if (Modifier.isFinal(modifiers)) {
				modifiersField = removeFinalFlag(field, modifiers);
			}
			Field fieldFlags = null;
			int readOnlyFlag = this.fieldFlags.getInt(field);
			if ((readOnlyFlag & getReadOnlyBit()) != 0) {
				fieldFlags = removeReadOnlyFlag(field, modifiers);
			}
			if (isStatic) {
				field.set(null, value);
			} else {
				field.set(target, value);
			}
			if (modifiersField != null) {
				modifiersField.setInt(field, modifiers);
			}
			if (fieldFlags != null) {
				fieldFlags.setInt(field, readOnlyFlag);
			}
		}

		protected void setAccessible(Field field) throws Throwable {
			try {
				setAccessibleFunction.accept(field, true);
			} catch (NullPointerException exc) {
				if (setAccessibleFunction == null) {
					synchronized (this) {
						if (setAccessibleFunction == null) {
							setAccessibleFunction = setAccessibleFunctionSupplier.get();
							getDeclaredFieldFunction = getDeclaredFieldFunctionSupplier.get();
							setAccessible(this.modifiersField = getDeclaredFieldFunction.apply(Field.class, "modifiers"));
							setAccessible(this.fieldFlags = getDeclaredFieldFunction.apply(
								Class.forName(getFieldFlagsDeclaringClassName()),
								"fieldFlags")
							);
						}
					}
					setAccessible(field);
				} else {
					throwExceptionFunction.accept(exc);
				}
			}
		}

		protected String getFieldFlagsDeclaringClassName() {
			return "jdk.internal.reflect.MethodHandleFieldAccessorImpl";
		}

		protected int getReadOnlyBit() {
			return 0x0001;
		}

		protected Field removeFinalFlag(Field field, int currentValue) throws IllegalAccessException {
			this.modifiersField.setInt(field, currentValue & ~Modifier.FINAL);
			return modifiersField;
		}

		protected Field removeReadOnlyFlag(Field field, int currentValue) throws IllegalAccessException {
			this.fieldFlags.setInt(field, currentValue & ~getReadOnlyBit());
			return fieldFlags;
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
