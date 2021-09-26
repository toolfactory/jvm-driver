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


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


import io.github.toolfactory.narcissus.Narcissus;


class DriverFunctionSupplierNative {

	BiFunction<Object, Field, Object> getFieldValueFunction() {
		return new BiFunction<Object, Field, Object>() {
			@Override
			public Object apply(Object target, Field field) {
				if (Modifier.isStatic(field.getModifiers())) {
					return Narcissus.getStaticField(field);
				} else {
					return Narcissus.getField(target, field);
				}
			}			
		};
	}

	
	TriConsumer<Object, Field, Object> getSetFieldValueFunction() {
		return new TriConsumer<Object, Field, Object>() {
			@Override
			public void accept(Object target, Field field, Object value) {
				if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
					Throwables.getInstance().throwException("Value {} is not assignable to {}", value , field.getName());
				}
				if (Modifier.isStatic(field.getModifiers())) {
					Narcissus.setStaticField(field, value);
				} else {
					Narcissus.setField(target, field, value);
				}	
				
			}
		};
	}


	BiConsumer<AccessibleObject, Boolean> getSetAccessibleFunction() {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			CallSite callSite = java.lang.invoke.LambdaMetafactory.metafactory(
				lookup, "accept", MethodType.methodType(BiConsumer.class),
				MethodType.methodType(void.class, Object.class, Object.class),
				lookup.findStatic(
					Narcissus.class, "setAccessible",
					MethodType.methodType(void.class, AccessibleObject.class, boolean.class)
				),
				MethodType.methodType(void.class, AccessibleObject.class, Boolean.class)
			);
		return (BiConsumer<AccessibleObject, Boolean>) callSite.getTarget().invoke();
		} catch (Throwable exc) {
			return Throwables.getInstance().throwException(exc);
		}
	}


	Function<Class<?>, Object> getAllocateInstanceFunction() {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			CallSite callSite = java.lang.invoke.LambdaMetafactory.metafactory(
				lookup, "apply", MethodType.methodType(Function.class),
				MethodType.methodType(Object.class, Object.class),
				lookup.findStatic(
					Narcissus.class, "allocateInstance",
					MethodType.methodType(Object.class, Class.class)
				),
				MethodType.methodType(Object.class, Class.class)
			);
			return (Function<Class<?>, Object>) callSite.getTarget().invoke();
		} catch (Throwable exc) {
			return Throwables.getInstance().throwException(exc);
		}
	}
	
	Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
		return new Supplier<MethodHandles.Lookup>() {
			@Override
			public Lookup get() {
				MethodHandles.Lookup consulter = MethodHandles.lookup();
				Narcissus.setAllowedModes(consulter, -1);
				return consulter;
			}
		};
	}

}
