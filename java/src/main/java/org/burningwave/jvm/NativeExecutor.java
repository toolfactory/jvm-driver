/*
 * This file is part of Burningwave JVM driver.
 *
 * Author: Roberto Gentili
 *
 * Hosted at: https://github.com/burningwave/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2021 Roberto Gentili
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
package org.burningwave.jvm;


import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;


class NativeExecutor {
	private final static NativeExecutor INSTANCE;

	private NativeExecutor() {}

	static {
		Libraries.getInstance().loadFor(NativeExecutor.class);
		INSTANCE = new NativeExecutor();
	}

	final static  NativeExecutor getInstance() {
		return INSTANCE;
	}


	native Object allocateInstance(Class<?> cls);


	native Object getFieldValue(Object target, Field field);

	native Integer getIntegerFieldValue(Object target, Field field);

	native Long getLongFieldValue(Object target, Field field);

	native Float getFloatFieldValue(Object target, Field field);

	native Double getDoubleFieldValue(Object target, Field field);

	native Boolean getBooleanFieldValue(Object target, Field field);

	native Byte getByteFieldValue(Object target, Field field);

	native Character getCharacterFieldValue(Object target, Field field);


	native Object getStaticFieldValue(Class<?> target, Field field);

	native Integer getStaticIntegerFieldValue(Class<?> target, Field field);

	native Long getStaticLongFieldValue(Class<?> target, Field field);

	native Float getStaticFloatFieldValue(Class<?> target, Field field);

	native Double getStaticDoubleFieldValue(Class<?> target, Field field);

	native Boolean getStaticBooleanFieldValue(Class<?> target, Field field);

	native Byte getStaticByteFieldValue(Class<?> target, Field field);

	native Character getStaticCharacterFieldValue(Class<?> target, Field field);


	native void setAccessible(AccessibleObject target, boolean flag);

	native void setAllowedModes(MethodHandles.Lookup consulter, int modes);


	native void setFieldValue(Object target, Field field, Object value);

	native void setIntegerFieldValue(Object target, Field field, Integer value);

	native void setLongFieldValue(Object target, Field field, Long value);

	native void setFloatFieldValue(Object target, Field field, Float value);

	native void setDoubleFieldValue(Object target, Field field, Double value);

	native void setBooleanFieldValue(Object target, Field field, Boolean value);

	native void setByteFieldValue(Object target, Field field, Byte value);

	native void setCharacterFieldValue(Object target, Field field, Character value);


	native void setStaticFieldValue(Class<?> target, Field field, Object value);

	native void setStaticIntegerFieldValue(Class<?> target, Field field, Integer value);

	native void setStaticLongFieldValue(Class<?> target, Field field, Long value);

	native void setStaticFloatFieldValue(Class<?> target, Field field, Float value);

	native void setStaticDoubleFieldValue(Class<?> target, Field field, Double value);

	native void setStaticBooleanFieldValue(Class<?> target, Field field, Boolean value);

	native void setStaticByteFieldValue(Class<?> target, Field field, Byte value);

	native void setStaticCharacterFieldValue(Class<?> target, Field field, Character value);

}

