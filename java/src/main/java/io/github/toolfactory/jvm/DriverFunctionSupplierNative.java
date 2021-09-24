/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2021 Luke Hutchison, Roberto Gentili
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
package io.github.toolfactory.jvm;


import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.toolfactory.narcissus.Narcissus;


class DriverFunctionSupplierNative {

	BiFunction<Object, Field, Object> getFieldValueFunction() {
		return (target, field) -> {
			if (Modifier.isStatic(field.getModifiers())) {
				return Narcissus.getStaticField(field);
			} else {
				return Narcissus.getField(target, field);
			}
		};
	}

	
	Function<Object, BiConsumer<Field, Object>> getSetFieldValueFunction() {
		return origTarget -> (field, value) -> {
			if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
				Throwables.throwException("Value {} is not assignable to {}", value , field.getName());
			}
			if (Modifier.isStatic(field.getModifiers())) {
				Narcissus.setStaticField(field, value);
			} else {
				Narcissus.setField(origTarget, field, value);
			}
		};
	}


	BiConsumer<AccessibleObject, Boolean> getSetAccessibleFunction() {
		return Narcissus::setAccessible;
	}


	Function<Class<?>, Object> getAllocateInstanceFunction() {
		return Narcissus::allocateInstance;
	}
	
	Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
		return () -> {
			MethodHandles.Lookup consulter = MethodHandles.lookup();
			Narcissus.setAllowedModes(consulter, -1);
			return consulter;
		};
	}

}
