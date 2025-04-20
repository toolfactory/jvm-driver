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


	UnsafeWrapper(Map<Object, Object> context) throws Throwable {
		init(context, "sun.misc.Unsafe", true);
	}

	protected synchronized void init(Map<Object, Object> context, String unsafeClassName, boolean accessibleFlag) throws Throwable {
		if (unsafeClass != null && unsafeClass.getName().equals(unsafeClassName)) {
			return;
		}
		Class<?> unsafeClassTemp = Class.forName(unsafeClassName);
		Field unsafeField = unsafeClassTemp.getDeclaredField("theUnsafe");
		Object unsafeTemp = null;
		if (accessibleFlag) {
			unsafeField.setAccessible(accessibleFlag);
			unsafeTemp = unsafeField.get(null);
		} else {
			unsafeTemp = getObject(unsafeClassTemp, staticFieldOffset(unsafeField));
		}
		ConsulterSupplyFunction consulterRetriever = ObjectProvider.getObject(ConsulterSupplyFunction.class, context);
		MethodHandles.Lookup lookup = null;
		if (consulterRetriever != null) {
			lookup = consulterRetriever.apply(unsafeTemp.getClass());
		} else {
			lookup = MethodHandles.lookup();
		}
		allocateInstance = lookup.bind(unsafeTemp, "allocateInstance", MethodType.methodType(Object.class, Class.class));
		objectFieldOffset = lookup.bind(unsafeTemp, "objectFieldOffset", MethodType.methodType(long.class, Field.class));
		staticFieldOffset = lookup.bind(unsafeTemp, "staticFieldOffset", MethodType.methodType(long.class, Field.class));

		getObject = lookup.bind(unsafeTemp, "getObject", MethodType.methodType(Object.class, Object.class, long.class));
		getObjectVolatile = lookup.bind(unsafeTemp, "getObjectVolatile", MethodType.methodType(Object.class, Object.class, long.class));
		getShort = lookup.bind(unsafeTemp, "getShort", MethodType.methodType(short.class, Object.class, long.class));
		getShortVolatile = lookup.bind(unsafeTemp, "getShortVolatile", MethodType.methodType(short.class, Object.class, long.class));
		getInt = lookup.bind(unsafeTemp, "getInt", MethodType.methodType(int.class, Object.class, long.class));
		getIntVolatile = lookup.bind(unsafeTemp, "getIntVolatile", MethodType.methodType(int.class, Object.class, long.class));
		getLong = lookup.bind(unsafeTemp, "getLong", MethodType.methodType(long.class, Object.class, long.class));
		getLongVolatile = lookup.bind(unsafeTemp, "getLongVolatile", MethodType.methodType(long.class, Object.class, long.class));
		getFloat = lookup.bind(unsafeTemp, "getFloat", MethodType.methodType(float.class, Object.class, long.class));
		getFloatVolatile = lookup.bind(unsafeTemp, "getFloatVolatile", MethodType.methodType(float.class, Object.class, long.class));
		getDouble = lookup.bind(unsafeTemp, "getDouble", MethodType.methodType(double.class, Object.class, long.class));
		getDoubleVolatile = lookup.bind(unsafeTemp, "getDoubleVolatile", MethodType.methodType(double.class, Object.class, long.class));
		getBoolean = lookup.bind(unsafeTemp, "getBoolean", MethodType.methodType(boolean.class, Object.class, long.class));
		getBooleanVolatile = lookup.bind(unsafeTemp, "getBooleanVolatile", MethodType.methodType(boolean.class, Object.class, long.class));
		getByte = lookup.bind(unsafeTemp, "getByte", MethodType.methodType(byte.class, Object.class, long.class));
		getByteVolatile = lookup.bind(unsafeTemp, "getByteVolatile", MethodType.methodType(byte.class, Object.class, long.class));
		getChar = lookup.bind(unsafeTemp, "getChar", MethodType.methodType(char.class, Object.class, long.class));
		getCharVolatile = lookup.bind(unsafeTemp, "getCharVolatile", MethodType.methodType(char.class, Object.class, long.class));

		putObject = lookup.bind(unsafeTemp, "putObject", MethodType.methodType(void.class, Object.class, long.class, Object.class));
		putObjectVolatile = lookup.bind(unsafeTemp, "putObjectVolatile", MethodType.methodType(void.class, Object.class, long.class, Object.class));
		putShort = lookup.bind(unsafeTemp, "putShort", MethodType.methodType(void.class, Object.class, long.class, short.class));
		putShortVolatile = lookup.bind(unsafeTemp, "putShortVolatile", MethodType.methodType(void.class, Object.class, long.class, short.class));
		putInt = lookup.bind(unsafeTemp, "putInt", MethodType.methodType(void.class, Object.class, long.class, int.class));
		putIntVolatile = lookup.bind(unsafeTemp, "putIntVolatile", MethodType.methodType(void.class, Object.class, long.class, int.class));
		putLong = lookup.bind(unsafeTemp, "putLong", MethodType.methodType(void.class, Object.class, long.class, long.class));
		putLongVolatile = lookup.bind(unsafeTemp, "putLongVolatile", MethodType.methodType(void.class, Object.class, long.class, long.class));
		putFloat = lookup.bind(unsafeTemp, "putFloat", MethodType.methodType(void.class, Object.class, long.class, float.class));
		putFloatVolatile = lookup.bind(unsafeTemp, "putFloatVolatile", MethodType.methodType(void.class, Object.class, long.class, float.class));
		putDouble = lookup.bind(unsafeTemp, "putDouble", MethodType.methodType(void.class, Object.class, long.class, double.class));
		putDoubleVolatile = lookup.bind(unsafeTemp, "putDoubleVolatile", MethodType.methodType(void.class, Object.class, long.class, double.class));
		putBoolean = lookup.bind(unsafeTemp, "putBoolean", MethodType.methodType(void.class, Object.class, long.class, boolean.class));
		putBooleanVolatile = lookup.bind(unsafeTemp, "putBooleanVolatile", MethodType.methodType(void.class, Object.class, long.class, boolean.class));
		putByte = lookup.bind(unsafeTemp, "putByte", MethodType.methodType(void.class, Object.class, long.class, byte.class));
		putByteVolatile = lookup.bind(unsafeTemp, "putByteVolatile", MethodType.methodType(void.class, Object.class, long.class, byte.class));
		putChar = lookup.bind(unsafeTemp, "putChar", MethodType.methodType(void.class, Object.class, long.class, char.class));
		putCharVolatile = lookup.bind(unsafeTemp, "putCharVolatile", MethodType.methodType(void.class, Object.class, long.class, char.class));

		unsafe = unsafeTemp;
		unsafeClass = unsafeClassTemp;
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

		public ForJava7(final Map<Object, Object> context) throws Throwable {
			super(context);
		}

	}

	public static class ForJava14 extends UnsafeWrapper {

		public ForJava14(final Map<Object, Object> context) throws Throwable {
			super(context);
			final String unsafeClassName = "jdk.internal.misc.Unsafe";
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (!getUnsafeClass().equals(unsafeClassName) || ObjectProvider.getObject(ConsulterSupplyFunction.class, context) == null) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {

						}
					}
					try {
						init(context, "jdk.internal.misc.Unsafe", false);
					} catch (Throwable e) {

					}
				}
			}, "UnsafeWrapperInitializer").start();
		}

	}
}
