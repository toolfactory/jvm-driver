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
		NativeExecutor javaNativeEnvironment = NativeExecutor.getInstance();
		return () -> {
			MethodHandles.Lookup consulter = MethodHandles.lookup();
			javaNativeEnvironment.setAllowedModes(consulter, -1);
			return consulter;
		};
	}
	
	BiFunction<Object, Field, Object> getFieldValueFunction() {
		NativeExecutor javaNativeEnvironment = NativeExecutor.getInstance();
		return (target, field) -> {
			Class<?> fieldType = field.getType();
			if (Modifier.isStatic(field.getModifiers())) {
				target = field.getDeclaringClass();
				if(!fieldType.isPrimitive()) {
					return javaNativeEnvironment.getStaticFieldValue((Class<?>)target, field);
				} else if (fieldType == int.class) {
					return javaNativeEnvironment.getStaticIntegerFieldValue((Class<?>)target, field);
				} else if (fieldType == long.class) {
					return javaNativeEnvironment.getStaticLongFieldValue((Class<?>)target, field);
				} else if (fieldType == float.class) {
					return javaNativeEnvironment.getStaticFloatFieldValue((Class<?>)target, field);
				} else if (fieldType == double.class) {
					return javaNativeEnvironment.getStaticDoubleFieldValue((Class<?>)target, field);
				} else if (fieldType == boolean.class) {
					return javaNativeEnvironment.getStaticBooleanFieldValue((Class<?>)target, field);
				} else if (fieldType == byte.class) {
					return javaNativeEnvironment.getStaticByteFieldValue((Class<?>)target, field);
				} else {
					return javaNativeEnvironment.getStaticCharacterFieldValue((Class<?>)target, field);
				}
			} else {
				if(!fieldType.isPrimitive()) {
					return javaNativeEnvironment.getFieldValue(target, field);
				} else if (fieldType == int.class) {
					return javaNativeEnvironment.getIntegerFieldValue(target, field);
				} else if (fieldType == long.class) {
					return javaNativeEnvironment.getLongFieldValue(target, field);
				} else if (fieldType == float.class) {
					return javaNativeEnvironment.getFloatFieldValue(target, field);
				} else if (fieldType == double.class) {
					return javaNativeEnvironment.getDoubleFieldValue(target, field);
				} else if (fieldType == boolean.class) {
					return javaNativeEnvironment.getBooleanFieldValue(target, field);
				} else if (fieldType == byte.class) {
					return javaNativeEnvironment.getByteFieldValue(target, field);
				} else {
					return javaNativeEnvironment.getCharacterFieldValue(target, field);
				}
			}
		};
	}

	Function<Object, BiConsumer<Field, Object>> getSetFieldValueFunction() {
		NativeExecutor javaNativeEnvironment = NativeExecutor.getInstance();
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
					javaNativeEnvironment.setStaticFieldValue((Class<?>)target, field, value);
				} else if (fieldType == int.class) {
					javaNativeEnvironment.setStaticIntegerFieldValue((Class<?>)target, field, (Integer)value);
				} else if (fieldType == long.class) {
					javaNativeEnvironment.setStaticLongFieldValue((Class<?>)target, field, (Long)value);
				} else if (fieldType == float.class) {
					javaNativeEnvironment.setStaticFloatFieldValue((Class<?>)target, field, (Float)value);
				} else if (fieldType == double.class) {
					javaNativeEnvironment.setStaticDoubleFieldValue((Class<?>)target, field, (Double)value);
				} else if (fieldType == boolean.class) {
					javaNativeEnvironment.setStaticBooleanFieldValue((Class<?>)target, field, (Boolean)value);
				} else if (fieldType == byte.class) {
					javaNativeEnvironment.setStaticByteFieldValue((Class<?>)target, field, (Byte)value);
				} else {
					javaNativeEnvironment.setStaticCharacterFieldValue((Class<?>)target, field, (Character)value);
				}
			} else {
				if(!fieldType.isPrimitive()) {
					javaNativeEnvironment.setFieldValue(target, field, value);
				} else if (fieldType == int.class) {
					javaNativeEnvironment.setIntegerFieldValue(target, field, (Integer)value);
				} else if (fieldType == long.class) {
					javaNativeEnvironment.setLongFieldValue(target, field, (Long)value);
				} else if (fieldType == float.class) {
					javaNativeEnvironment.setFloatFieldValue(target, field, (Float)value);
				} else if (fieldType == double.class) {
					javaNativeEnvironment.setDoubleFieldValue(target, field, (Double)value);
				} else if (fieldType == boolean.class) {
					javaNativeEnvironment.setBooleanFieldValue(target, field, (Boolean)value);
				} else if (fieldType == byte.class) {
					javaNativeEnvironment.setByteFieldValue(target, field, (Byte)value);
				} else {
					javaNativeEnvironment.setCharacterFieldValue(target, field, (Character)value);
				}
			}
		};
	}
	
	
	BiConsumer<AccessibleObject, Boolean> getSetAccessibleFunction() {
		return NativeExecutor.getInstance()::setAccessible;
	}
	
	
	public Function<Class<?>, Object> getAllocateInstanceFunction() {
		return NativeExecutor.getInstance()::allocateInstance;
	}

}
