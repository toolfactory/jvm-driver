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
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

class DriverFunctionSupplierNative {

	Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
		NativeExecutor nativeExecutor = NativeExecutor.getInstance();
		return () -> {
			MethodHandles.Lookup consulter = MethodHandles.lookup();
			nativeExecutor.setAllowedModes(consulter, -1);
			return consulter;
		};
	}

	BiFunction<Object, Field, Object> getFieldValueFunction() {
		NativeExecutor nativeExecutor = NativeExecutor.getInstance();
		return (target, field) -> {
			Class<?> fieldType = field.getType();
			if (Modifier.isStatic(field.getModifiers())) {
				target = field.getDeclaringClass();
				if(!fieldType.isPrimitive()) {
					return nativeExecutor.getStaticFieldValue((Class<?>)target, field);
				} else if (fieldType == int.class) {
					return nativeExecutor.getStaticIntegerFieldValue((Class<?>)target, field);
				} else if (fieldType == long.class) {
					return nativeExecutor.getStaticLongFieldValue((Class<?>)target, field);
				} else if (fieldType == float.class) {
					return nativeExecutor.getStaticFloatFieldValue((Class<?>)target, field);
				} else if (fieldType == double.class) {
					return nativeExecutor.getStaticDoubleFieldValue((Class<?>)target, field);
				} else if (fieldType == boolean.class) {
					return nativeExecutor.getStaticBooleanFieldValue((Class<?>)target, field);
				} else if (fieldType == byte.class) {
					return nativeExecutor.getStaticByteFieldValue((Class<?>)target, field);
				} else {
					return nativeExecutor.getStaticCharacterFieldValue((Class<?>)target, field);
				}
			} else {
				if(!fieldType.isPrimitive()) {
					return nativeExecutor.getFieldValue(target, field);
				} else if (fieldType == int.class) {
					return nativeExecutor.getIntegerFieldValue(target, field);
				} else if (fieldType == long.class) {
					return nativeExecutor.getLongFieldValue(target, field);
				} else if (fieldType == float.class) {
					return nativeExecutor.getFloatFieldValue(target, field);
				} else if (fieldType == double.class) {
					return nativeExecutor.getDoubleFieldValue(target, field);
				} else if (fieldType == boolean.class) {
					return nativeExecutor.getBooleanFieldValue(target, field);
				} else if (fieldType == byte.class) {
					return nativeExecutor.getByteFieldValue(target, field);
				} else {
					return nativeExecutor.getCharacterFieldValue(target, field);
				}
			}
		};
	}

	Function<Object, BiConsumer<Field, Object>> getSetFieldValueFunction() {
		NativeExecutor nativeExecutor = NativeExecutor.getInstance();
		return origTarget -> (field, value) -> {
			if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
				Throwables.throwException("Value {} is not assignable to {}", value , field.getName());
			}
			Class<?> fieldType = field.getType();
			Object target = Modifier.isStatic(field.getModifiers())?
				field.getDeclaringClass() :
				origTarget;
			if (Modifier.isStatic(field.getModifiers())) {
				target = field.getDeclaringClass();
				if(!fieldType.isPrimitive()) {
					nativeExecutor.setStaticFieldValue((Class<?>)target, field, value);
				} else if (fieldType == int.class) {
					nativeExecutor.setStaticIntegerFieldValue((Class<?>)target, field, (Integer)value);
				} else if (fieldType == long.class) {
					nativeExecutor.setStaticLongFieldValue((Class<?>)target, field, (Long)value);
				} else if (fieldType == float.class) {
					nativeExecutor.setStaticFloatFieldValue((Class<?>)target, field, (Float)value);
				} else if (fieldType == double.class) {
					nativeExecutor.setStaticDoubleFieldValue((Class<?>)target, field, (Double)value);
				} else if (fieldType == boolean.class) {
					nativeExecutor.setStaticBooleanFieldValue((Class<?>)target, field, (Boolean)value);
				} else if (fieldType == byte.class) {
					nativeExecutor.setStaticByteFieldValue((Class<?>)target, field, (Byte)value);
				} else {
					nativeExecutor.setStaticCharacterFieldValue((Class<?>)target, field, (Character)value);
				}
			} else {
				if(!fieldType.isPrimitive()) {
					nativeExecutor.setFieldValue(target, field, value);
				} else if (fieldType == int.class) {
					nativeExecutor.setIntegerFieldValue(target, field, (Integer)value);
				} else if (fieldType == long.class) {
					nativeExecutor.setLongFieldValue(target, field, (Long)value);
				} else if (fieldType == float.class) {
					nativeExecutor.setFloatFieldValue(target, field, (Float)value);
				} else if (fieldType == double.class) {
					nativeExecutor.setDoubleFieldValue(target, field, (Double)value);
				} else if (fieldType == boolean.class) {
					nativeExecutor.setBooleanFieldValue(target, field, (Boolean)value);
				} else if (fieldType == byte.class) {
					nativeExecutor.setByteFieldValue(target, field, (Byte)value);
				} else {
					nativeExecutor.setCharacterFieldValue(target, field, (Character)value);
				}
			}
		};
	}


	BiConsumer<AccessibleObject, Boolean> getSetAccessibleFunction() {
		return NativeExecutor.getInstance()::setAccessible;
	}


	Function<Class<?>, Object> getAllocateInstanceFunction() {
		return NativeExecutor.getInstance()::allocateInstance;
	}

}
