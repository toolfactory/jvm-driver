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


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Map;

import io.github.toolfactory.jvm.util.ObjectProvider;

@SuppressWarnings("all")
public abstract class UnsafeWrapper implements io.github.toolfactory.jvm.function.template.Supplier<Object>{

	protected static Object unsafe;
	protected static Class<?> unsafeClass;

	protected static MethodHandle allocateInstance;
	protected static MethodHandle objectFieldOffset;
	protected static MethodHandle staticFieldOffset;

	protected static MethodHandle getObject;
	protected static MethodHandle getObjectVolatile;
	protected static MethodHandle getShort;
	protected static MethodHandle getShortVolatile;
	protected static MethodHandle getInt;
	protected static MethodHandle getIntVolatile;
	protected static MethodHandle getLong;
	protected static MethodHandle getLongVolatile;
	protected static MethodHandle getFloat;
	protected static MethodHandle getFloatVolatile;
	protected static MethodHandle getDouble;
	protected static MethodHandle getDoubleVolatile;
	protected static MethodHandle getBoolean;
	protected static MethodHandle getBooleanVolatile;
	protected static MethodHandle getByte;
	protected static MethodHandle getByteVolatile;
	protected static MethodHandle getChar;
	protected static MethodHandle getCharVolatile;

	protected static MethodHandle putObject;
	protected static MethodHandle putObjectVolatile;
	protected static MethodHandle putShort;
	protected static MethodHandle putShortVolatile;
	protected static MethodHandle putInt;
	protected static MethodHandle putIntVolatile;
	protected static MethodHandle putLong;
	protected static MethodHandle putLongVolatile;
	protected static MethodHandle putFloat;
	protected static MethodHandle putFloatVolatile;
	protected static MethodHandle putDouble;
	protected static MethodHandle putDoubleVolatile;
	protected static MethodHandle putBoolean;
	protected static MethodHandle putBooleanVolatile;
	protected static MethodHandle putByte;
	protected static MethodHandle putByteVolatile;
	protected static MethodHandle putChar;
	protected static MethodHandle putCharVolatile;

	protected synchronized boolean init(Map<Object, Object> context, String unsafeClassName) throws Throwable {
		if (unsafeClass != null && unsafeClass.getName().equals(unsafeClassName)) {
			return false;
		}
		Field unsafeField = Class.forName(unsafeClassName).getDeclaredField("theUnsafe");
		unsafeField.setAccessible(true);
		init(unsafeField.get(null), MethodHandles.lookup());
		return true;
	}

	protected void init(
		Object unsafeObject,
		MethodHandles.Lookup lookup
	) throws NoSuchMethodException, IllegalAccessException {
		allocateInstance = lookup.bind(unsafeObject, "allocateInstance", MethodType.methodType(Object.class, Class.class));
		objectFieldOffset = lookup.bind(unsafeObject, "objectFieldOffset", MethodType.methodType(long.class, Field.class));
		staticFieldOffset = lookup.bind(unsafeObject, "staticFieldOffset", MethodType.methodType(long.class, Field.class));

		getObject = lookup.bind(unsafeObject, "getObject", MethodType.methodType(Object.class, Object.class, long.class));
		getObjectVolatile = lookup.bind(unsafeObject, "getObjectVolatile", MethodType.methodType(Object.class, Object.class, long.class));
		getShort = lookup.bind(unsafeObject, "getShort", MethodType.methodType(short.class, Object.class, long.class));
		getShortVolatile = lookup.bind(unsafeObject, "getShortVolatile", MethodType.methodType(short.class, Object.class, long.class));
		getInt = lookup.bind(unsafeObject, "getInt", MethodType.methodType(int.class, Object.class, long.class));
		getIntVolatile = lookup.bind(unsafeObject, "getIntVolatile", MethodType.methodType(int.class, Object.class, long.class));
		getLong = lookup.bind(unsafeObject, "getLong", MethodType.methodType(long.class, Object.class, long.class));
		getLongVolatile = lookup.bind(unsafeObject, "getLongVolatile", MethodType.methodType(long.class, Object.class, long.class));
		getFloat = lookup.bind(unsafeObject, "getFloat", MethodType.methodType(float.class, Object.class, long.class));
		getFloatVolatile = lookup.bind(unsafeObject, "getFloatVolatile", MethodType.methodType(float.class, Object.class, long.class));
		getDouble = lookup.bind(unsafeObject, "getDouble", MethodType.methodType(double.class, Object.class, long.class));
		getDoubleVolatile = lookup.bind(unsafeObject, "getDoubleVolatile", MethodType.methodType(double.class, Object.class, long.class));
		getBoolean = lookup.bind(unsafeObject, "getBoolean", MethodType.methodType(boolean.class, Object.class, long.class));
		getBooleanVolatile = lookup.bind(unsafeObject, "getBooleanVolatile", MethodType.methodType(boolean.class, Object.class, long.class));
		getByte = lookup.bind(unsafeObject, "getByte", MethodType.methodType(byte.class, Object.class, long.class));
		getByteVolatile = lookup.bind(unsafeObject, "getByteVolatile", MethodType.methodType(byte.class, Object.class, long.class));
		getChar = lookup.bind(unsafeObject, "getChar", MethodType.methodType(char.class, Object.class, long.class));
		getCharVolatile = lookup.bind(unsafeObject, "getCharVolatile", MethodType.methodType(char.class, Object.class, long.class));

		putObject = lookup.bind(unsafeObject, "putObject", MethodType.methodType(void.class, Object.class, long.class, Object.class));
		putObjectVolatile = lookup.bind(unsafeObject, "putObjectVolatile", MethodType.methodType(void.class, Object.class, long.class, Object.class));
		putShort = lookup.bind(unsafeObject, "putShort", MethodType.methodType(void.class, Object.class, long.class, short.class));
		putShortVolatile = lookup.bind(unsafeObject, "putShortVolatile", MethodType.methodType(void.class, Object.class, long.class, short.class));
		putInt = lookup.bind(unsafeObject, "putInt", MethodType.methodType(void.class, Object.class, long.class, int.class));
		putIntVolatile = lookup.bind(unsafeObject, "putIntVolatile", MethodType.methodType(void.class, Object.class, long.class, int.class));
		putLong = lookup.bind(unsafeObject, "putLong", MethodType.methodType(void.class, Object.class, long.class, long.class));
		putLongVolatile = lookup.bind(unsafeObject, "putLongVolatile", MethodType.methodType(void.class, Object.class, long.class, long.class));
		putFloat = lookup.bind(unsafeObject, "putFloat", MethodType.methodType(void.class, Object.class, long.class, float.class));
		putFloatVolatile = lookup.bind(unsafeObject, "putFloatVolatile", MethodType.methodType(void.class, Object.class, long.class, float.class));
		putDouble = lookup.bind(unsafeObject, "putDouble", MethodType.methodType(void.class, Object.class, long.class, double.class));
		putDoubleVolatile = lookup.bind(unsafeObject, "putDoubleVolatile", MethodType.methodType(void.class, Object.class, long.class, double.class));
		putBoolean = lookup.bind(unsafeObject, "putBoolean", MethodType.methodType(void.class, Object.class, long.class, boolean.class));
		putBooleanVolatile = lookup.bind(unsafeObject, "putBooleanVolatile", MethodType.methodType(void.class, Object.class, long.class, boolean.class));
		putByte = lookup.bind(unsafeObject, "putByte", MethodType.methodType(void.class, Object.class, long.class, byte.class));
		putByteVolatile = lookup.bind(unsafeObject, "putByteVolatile", MethodType.methodType(void.class, Object.class, long.class, byte.class));
		putChar = lookup.bind(unsafeObject, "putChar", MethodType.methodType(void.class, Object.class, long.class, char.class));
		putCharVolatile = lookup.bind(unsafeObject, "putCharVolatile", MethodType.methodType(void.class, Object.class, long.class, char.class));

		unsafe = unsafeObject;
		unsafeClass = unsafeObject.getClass();
	}


	@Override
	public Object get() {
		return unsafe;
	}


	public Class<?> getUnsafeClass() {
		return unsafeClass;
	}


	public Object allocateInstance(Class<?> cls) throws Throwable {
		return allocateInstance.invoke(cls);
	}


	public long objectFieldOffset(Field field) throws Throwable {
		return (long)objectFieldOffset.invoke(field);
	}


	public long staticFieldOffset(Field field) throws Throwable {
		return (long)staticFieldOffset.invoke(field);
	}


	public Object getObject(Object target, long fieldOffset) throws Throwable {
		return getObject.invoke(target, fieldOffset);
	}


	public Object getObjectVolatile(Object target, long fieldOffset) throws Throwable {
		return getObjectVolatile.invoke(target, fieldOffset);
	}


	public short getShort(Object target, long fieldOffset) throws Throwable {
		return (short) getShort.invoke(target, fieldOffset);
	}


	public short getShortVolatile(Object target, long fieldOffset) throws Throwable {
		return (short) getShortVolatile.invoke(target, fieldOffset);
	}


	public int getInt(Object target, long fieldOffset) throws Throwable {
		return (int) getInt.invoke(target, fieldOffset);
	}


	public int getIntVolatile(Object target, long fieldOffset) throws Throwable {
		return (int) getIntVolatile.invoke(target, fieldOffset);
	}


	public long getLong(Object target, long fieldOffset) throws Throwable {
		return (long) getLong.invoke(target, fieldOffset);
	}


	public long getLongVolatile(Object target, long fieldOffset) throws Throwable {
		return (long) getLongVolatile.invoke(target, fieldOffset);
	}


	public float getFloat(Object target, long fieldOffset) throws Throwable {
		return (float) getFloat.invoke(target, fieldOffset);
	}


	public float getFloatVolatile(Object target, long fieldOffset) throws Throwable {
		return (float) getFloatVolatile.invoke(target, fieldOffset);
	}


	public double getDouble(Object target, long fieldOffset) throws Throwable {
		return (double) getDouble.invoke(target, fieldOffset);
	}


	public double getDoubleVolatile(Object target, long fieldOffset) throws Throwable {
		return (double) getDoubleVolatile.invoke(target, fieldOffset);
	}


	public boolean getBoolean(Object target, long fieldOffset) throws Throwable {
		return (boolean) getBoolean.invoke(target, fieldOffset);
	}


	public boolean getBooleanVolatile(Object target, long fieldOffset) throws Throwable {
		return (boolean) getBooleanVolatile.invoke(target, fieldOffset);
	}


	public byte getByte(Object target, long fieldOffset) throws Throwable {
		return (byte) getByte.invoke(target, fieldOffset);
	}


	public byte getByteVolatile(Object target, long fieldOffset) throws Throwable {
		return (byte) getByteVolatile.invoke(target, fieldOffset);
	}


	public char getChar(Object target, long fieldOffset) throws Throwable {
		return (char) getChar.invoke(target, fieldOffset);
	}


	public char getCharVolatile(Object target, long fieldOffset) throws Throwable {
		return (char) getCharVolatile.invoke(target, fieldOffset);
	}


	public void putObject(Object target, long fieldOffset, Object value) throws Throwable {
		putObject.invoke(target, fieldOffset, value);
	}


	public void putObjectVolatile(Object target, long fieldOffset, Object value) throws Throwable {
		putObjectVolatile.invoke(target, fieldOffset, value);
	}


	public void putShort(Object target, long fieldOffset, short value) throws Throwable {
		putShort.invoke(target, fieldOffset, value);
	}


	public void putShortVolatile(Object target, long fieldOffset, short value) throws Throwable {
		putShortVolatile.invoke(target, fieldOffset, value);
	}


	public void putInt(Object target, long fieldOffset, int value) throws Throwable {
		putInt.invoke(target, fieldOffset, value);
	}


	public void putIntVolatile(Object target, long fieldOffset, int value) throws Throwable {
		putIntVolatile.invoke(target, fieldOffset, value);
	}


	public void putLong(Object target, long fieldOffset, long value) throws Throwable {
		putLong.invoke(target, fieldOffset, value);	}


	public void putLongVolatile(Object target, long fieldOffset, long value) throws Throwable {
		putLongVolatile.invoke(target, fieldOffset, value);
	}


	public void putFloat(Object target, long fieldOffset, float value) throws Throwable {
		putFloat.invoke(target, fieldOffset, value);
	}


	public void putFloatVolatile(Object target, long fieldOffset, float value) throws Throwable {
		putFloatVolatile.invoke(target, fieldOffset, value);
	}


	public void putDouble(Object target, long fieldOffset, double value) throws Throwable {
		putDouble.invoke(target, fieldOffset, value);
	}


	public void putDoubleVolatile(Object target, long fieldOffset, double value) throws Throwable {
		putDoubleVolatile.invoke(target, fieldOffset, value);
	}


	public void putBoolean(Object target, long fieldOffset, boolean value) throws Throwable {
		putBoolean.invoke(target, fieldOffset, value);
	}


	public void putBooleanVolatile(Object target, long fieldOffset, boolean value) throws Throwable {
		putBooleanVolatile.invoke(target, fieldOffset, value);
	}


	public void putByte(Object target, long fieldOffset, byte value) throws Throwable {
		putByte.invoke(target, fieldOffset, value);
	}


	public void putByteVolatile(Object target, long fieldOffset, byte value) throws Throwable {
		putByteVolatile.invoke(target, fieldOffset, value);
	}


	public void putChar(Object target, long fieldOffset, char value) throws Throwable {
		putChar.invoke(target, fieldOffset, value);
	}


	public void putCharVolatile(Object target, long fieldOffset, char value) throws Throwable {
		putCharVolatile.invoke(target, fieldOffset, value);
	}

	public static class ForJava7 extends UnsafeWrapper {
		private final static String UNSAFE_CLASS_NAME = "sun.misc.Unsafe";

		public ForJava7(final Map<Object, Object> context) throws Throwable {
			super.init(context, UNSAFE_CLASS_NAME);
		}

	}

	public static class ForJava14 extends UnsafeWrapper {
		private final static String UNSAFE_CLASS_NAME = "jdk.internal.misc.Unsafe";
		private static Thread switcher;


		public ForJava14(final Map<Object, Object> context) throws Throwable {
			if (!super.init(context, ForJava7.UNSAFE_CLASS_NAME) && !unsafeClass.getName().equals(ForJava14.UNSAFE_CLASS_NAME) && switcher == null) {
				synchronized (ForJava14.class) {
					if (!super.init(context, "sun.misc.Unsafe") && !unsafeClass.getName().equals(ForJava14.UNSAFE_CLASS_NAME) && switcher == null) {
						switcher = new Thread(new Runnable() {
							@Override
							public void run() {
								while (!getUnsafeClass().equals(ForJava14.UNSAFE_CLASS_NAME) &&
									ObjectProvider.getObject(ConsulterSupplyFunction.class, context) == null &&
									ObjectProvider.getObject(GetClassByNameFunction.class, context) == null
								) {
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {

									}
								}
								try {
									init(context, ForJava14.UNSAFE_CLASS_NAME);
								} catch (Throwable exc) {
									ObjectProvider.getObject(ThrowExceptionFunction.class, context).accept(exc);
								}
							}
						}, "UnsafeWrapperInitializer");
						switcher.start();
					}
				}
			}
		}

		@Override
		protected synchronized boolean init(Map<Object, Object> context, String unsafeClassName) throws Throwable {
			if (unsafeClass != null && unsafeClass.getName().equals(unsafeClassName)) {
				return false;
			}
			GetClassByNameFunction getClassByNameFunction = ObjectProvider.getObject(GetClassByNameFunction.class, context);
			Class<?> unsafeClassTemp = getClassByNameFunction.apply(unsafeClassName, true, getClass().getClassLoader(), getClass());
			Field unsafeField = unsafeClassTemp.getDeclaredField("theUnsafe");
			Object unsafeTemp = getObject(unsafeClassTemp, staticFieldOffset(unsafeField));
			ConsulterSupplyFunction consulterRetriever = ObjectProvider.getObject(ConsulterSupplyFunction.class, context);
			init(unsafeTemp, consulterRetriever.apply(unsafeTemp.getClass()));
			switcher = null;
			return true;
		}
	}
}
