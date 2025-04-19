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
import java.util.Map;

@SuppressWarnings("all")
public interface UnsafeWrapper extends io.github.toolfactory.jvm.function.template.Supplier<Object>{

	public static class ForJava7 implements UnsafeWrapper {
		protected sun.misc.Unsafe unsafe;

		public ForJava7(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			Field theUnsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			this.unsafe = (sun.misc.Unsafe)theUnsafeField.get(null);
		}

		@Override
		public Object get() {
			return unsafe;
		}

		@Override
		public Object allocateInstance(Class<?> cls) throws InstantiationException {
			return unsafe.allocateInstance(cls);
		}

		@Override
		public Class<?> getUnsafeClass() {
			return unsafe.getClass();
		}

		@Override
		public long objectFieldOffset(Field field) {
			return unsafe.objectFieldOffset(field);
		}

		@Override
		public long staticFieldOffset(Field field) {
			return unsafe.staticFieldOffset(field);
		}

		@Override
		public Object getObject(Object target, long fieldOffset) {
			return unsafe.getObject(target, fieldOffset);
		}

		@Override
		public Object getObjectVolatile(Object target, long fieldOffset) {
			return unsafe.getObjectVolatile(target, fieldOffset);
		}

		@Override
		public short getShort(Object target, long fieldOffset) {
			return unsafe.getShort(target, fieldOffset);
		}

		@Override
		public short getShortVolatile(Object target, long fieldOffset) {
			return unsafe.getShortVolatile(target, fieldOffset);
		}

		@Override
		public int getInt(Object target, long fieldOffset) {
			return unsafe.getInt(target, fieldOffset);
		}

		@Override
		public int getIntVolatile(Object target, long fieldOffset) {
			return unsafe.getIntVolatile(target, fieldOffset);
		}

		@Override
		public long getLong(Object target, long fieldOffset) {
			return unsafe.getLong(target, fieldOffset);
		}

		@Override
		public long getLongVolatile(Object target, long fieldOffset) {
			return unsafe.getLongVolatile(target, fieldOffset);
		}

		@Override
		public float getFloat(Object target, long fieldOffset) {
			return unsafe.getFloat(target, fieldOffset);
		}

		@Override
		public float getFloatVolatile(Object target, long fieldOffset) {
			return unsafe.getFloatVolatile(target, fieldOffset);
		}

		@Override
		public double getDouble(Object target, long fieldOffset) {
			return unsafe.getDouble(target, fieldOffset);
		}

		@Override
		public double getDoubleVolatile(Object target, long fieldOffset) {
			return unsafe.getDoubleVolatile(target, fieldOffset);
		}

		@Override
		public boolean getBoolean(Object target, long fieldOffset) {
			return unsafe.getBoolean(target, fieldOffset);
		}

		@Override
		public boolean getBooleanVolatile(Object target, long fieldOffset) {
			return unsafe.getBooleanVolatile(target, fieldOffset);
		}

		@Override
		public byte getByte(Object target, long fieldOffset) {
			return unsafe.getByte(target, fieldOffset);
		}

		@Override
		public byte getByteVolatile(Object target, long fieldOffset) {
			return unsafe.getByteVolatile(target, fieldOffset);
		}

		@Override
		public char getChar(Object target, long fieldOffset) {
			return unsafe.getChar(target, fieldOffset);
		}

		@Override
		public char getCharVolatile(Object target, long fieldOffset) {
			return unsafe.getCharVolatile(target, fieldOffset);
		}

		@Override
		public void putObject(Object target, long fieldOffset, Object value) {
			unsafe.putObject(target, fieldOffset, value);

		}

		@Override
		public void putObjectVolatile(Object target, long fieldOffset, Object value) {
			unsafe.putObjectVolatile(target, fieldOffset, value);

		}

		@Override
		public void putShort(Object target, long fieldOffset, short value) {
			unsafe.putShort(target, fieldOffset, value);

		}

		@Override
		public void putShortVolatile(Object target, long fieldOffset, short value) {
			unsafe.putShortVolatile(target, fieldOffset, value);
		}

		@Override
		public void putInt(Object target, long fieldOffset, int value) {
			unsafe.putInt(target, fieldOffset, value);
		}

		@Override
		public void putIntVolatile(Object target, long fieldOffset, int value) {
			unsafe.putIntVolatile(target, fieldOffset, value);
		}

		@Override
		public void putLong(Object target, long fieldOffset, long value) {
			unsafe.putLong(target, fieldOffset, value);
		}

		@Override
		public void putLongVolatile(Object target, long fieldOffset, long value) {
			unsafe.putLongVolatile(target, fieldOffset, value);
		}

		@Override
		public void putFloat(Object target, long fieldOffset, float value) {
			unsafe.putFloat(target, fieldOffset, value);
		}

		@Override
		public void putFloatVolatile(Object target, long fieldOffset, float value) {
			unsafe.putFloatVolatile(target, fieldOffset, value);
		}

		@Override
		public void putDouble(Object target, long fieldOffset, double value) {
			unsafe.putDouble(target, fieldOffset, value);
		}

		@Override
		public void putDoubleVolatile(Object target, long fieldOffset, double value) {
			unsafe.putDoubleVolatile(target, fieldOffset, value);
		}

		@Override
		public void putBoolean(Object target, long fieldOffset, boolean value) {
			unsafe.putBoolean(target, fieldOffset, value);
		}

		@Override
		public void putBooleanVolatile(Object target, long fieldOffset, boolean value) {
			unsafe.putBooleanVolatile(target, fieldOffset, value);
		}

		@Override
		public void putByte(Object target, long fieldOffset, byte value) {
			unsafe.putByte(target, fieldOffset, value);
		}

		@Override
		public void putByteVolatile(Object target, long fieldOffset, byte value) {
			unsafe.putByteVolatile(target, fieldOffset, value);
		}

		@Override
		public void putChar(Object target, long fieldOffset, char value) {
			unsafe.putChar(target, fieldOffset, value);
		}

		@Override
		public void putCharVolatile(Object target, long fieldOffset, char value) {
			unsafe.putCharVolatile(target, fieldOffset, value);
		}

	}

	public abstract Object allocateInstance(Class<?> input) throws InstantiationException;

	public abstract Class<?> getUnsafeClass();

	public abstract long objectFieldOffset(Field f);

	public abstract long staticFieldOffset(Field field);

	public abstract Object getObject(Object target, long fieldOffset);

	public abstract Object getObjectVolatile(Object target, long fieldOffset);

	public abstract short getShort(Object target, long fieldOffset);

	public abstract short getShortVolatile(Object target, long fieldOffset);

	public abstract int getInt(Object target, long fieldOffset);

	public abstract int getIntVolatile(Object target, long fieldOffset);

	public abstract long getLong(Object target, long fieldOffset);

	public abstract long getLongVolatile(Object target, long fieldOffset);

	public abstract float getFloat(Object target, long fieldOffset);

	public abstract float getFloatVolatile(Object target, long fieldOffset);

	public abstract double getDouble(Object target, long fieldOffset);

	public abstract double getDoubleVolatile(Object target, long fieldOffset);

	public abstract boolean getBoolean(Object target, long fieldOffset);

	public abstract boolean getBooleanVolatile(Object target, long fieldOffset);

	public abstract byte getByte(Object target, long fieldOffset);

	public abstract byte getByteVolatile(Object target, long fieldOffset);

	public abstract char getChar(Object target, long fieldOffset);

	public abstract char getCharVolatile(Object target, long fieldOffset);

	public abstract void putObject(Object target, long fieldOffset, Object value);

	public abstract void putObjectVolatile(Object target, long fieldOffset, Object value);

	public abstract void putShort(Object target, long fieldOffset, short value);

	public abstract void putShortVolatile(Object target, long fieldOffset, short value);

	public abstract void putInt(Object target, long offset, int value);

	public abstract void putIntVolatile(Object target, long fieldOffset, int value);

	public abstract void putLong(Object target, long fieldOffset, long value);

	public abstract void putLongVolatile(Object target, long fieldOffset, long value);

	public abstract void putFloat(Object target, long fieldOffset, float value);

	public abstract void putFloatVolatile(Object target, long fieldOffset, float value);

	public abstract void putDouble(Object target, long fieldOffset, double value);

	public abstract void putDoubleVolatile(Object target, long fieldOffset, double value);

	public abstract void putBoolean(Object target, long fieldOffset, boolean value);

	public abstract void putBooleanVolatile(Object target, long fieldOffset, boolean value);

	public abstract void putByte(Object target, long fieldOffset, byte value);

	public abstract void putByteVolatile(Object target, long fieldOffset, byte value);

	public abstract void putChar(Object target, long fieldOffset, char value);

	public abstract void putCharVolatile(Object target, long fieldOffset, char value);
}
