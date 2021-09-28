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


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;


@SuppressWarnings("unchecked")
abstract class _DeepConsulterSupplyFunction<F> extends FunctionAdapter<F, Class<?>, MethodHandles.Lookup> {

	
	static class ForJava7 extends _DeepConsulterSupplyFunction<Function<Class<?>, MethodHandles.Lookup>> {
		ForJava7(Map<Object, Object> context) {
			FunctionProvider functionProvider = FunctionProvider.get(context);
			setFunction(
				(Function<Class<?>, MethodHandles.Lookup>)functionProvider.getFunctionAdapter(_ConsulterSupplyFunction.class, context).function
			);
		}
		
		@Override
		MethodHandles.Lookup apply(Class<?> input) {
			return function.apply(input);
		}
		
	}
	
	static class ForJava9 extends _DeepConsulterSupplyFunction<Function<Class<?>, MethodHandles.Lookup>> {
		ForJava9(Map<Object, Object> context) throws NoSuchMethodException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
			Constructor<MethodHandles.Lookup> lookupCtor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
			FunctionProvider functionProvider = FunctionProvider.get(context);
			functionProvider.getFunctionAdapter(_SetAccessibleFunction.class, context).accept (lookupCtor, true);
			final MethodHandle methodHandle = lookupCtor.newInstance(MethodHandles.Lookup.class, -1).findConstructor(
				MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class, int.class)
			);
			setFunction(
				new Function<Class<?>, MethodHandles.Lookup>() {
					@Override
					public Lookup apply(Class<?> cls) {
						try {
							return (MethodHandles.Lookup)methodHandle.invoke(cls, -1);
						} catch (Throwable exc) {
							return Throwables.getInstance().throwException(exc);
						}
					}
				}
			);

		}

		
		@Override
		MethodHandles.Lookup apply(Class<?> input) {
			return function.apply(input);
		}
		
	}
	
	static class ForJava14 extends _DeepConsulterSupplyFunction<Function<Class<?>, MethodHandles.Lookup>> {
		ForJava14(Map<Object, Object> context) throws NoSuchMethodException, SecurityException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
			Constructor<?> lookupCtor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Class.class, int.class);
			FunctionProvider functionProvider = FunctionProvider.get(context);
			functionProvider.getFunctionAdapter(_SetAccessibleFunction.class, context).accept (lookupCtor, true);
			final MethodHandle mthHandle = ((MethodHandles.Lookup)lookupCtor.newInstance(MethodHandles.Lookup.class, null, -1)).findConstructor(
				MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class, Class.class, int.class)
			);
			setFunction(
				new Function<Class<?>, MethodHandles.Lookup>() {
					@Override
					public Lookup apply(Class<?> cls) {
						try {
							return (MethodHandles.Lookup)mthHandle.invoke(cls, null, -1);
						} catch (Throwable exc) {
							return Throwables.getInstance().throwException(exc);
						}
					}
				}
			);
			
		}

		
		@Override
		MethodHandles.Lookup apply(Class<?> input) {
			return function.apply(input);
		}
		
	}
}
