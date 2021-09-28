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


import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;

import io.github.toolfactory.jvm.Driver.InitializationException;


@SuppressWarnings("unchecked")
abstract class _ConsulterSupplyFunction<F> extends FunctionAdapter<F, Class<?>, MethodHandles.Lookup> {

	
	static class ForJava7 extends _ConsulterSupplyFunction<Function<Class<?>, MethodHandles.Lookup>> {
		ForJava7(Map<Object, Object> context) {
			FunctionProvider functionProvider = FunctionProvider.get(context);
			final MethodHandles.Lookup consulter = functionProvider.getFunctionAdapter(_ConsulterSupplier.class, context).get();
			final MethodHandle privateLookupInMethodHandle = functionProvider.getFunctionAdapter(_PrivateLookupInMethodHandleSupplier.class, context).get();
			final _ThrowExceptionFunction throwExceptionFunction =
				functionProvider.getFunctionAdapter(_ThrowExceptionFunction.class, context); 
			setFunction(
				new Function<Class<?>, MethodHandles.Lookup>() { 
					@Override
					public MethodHandles.Lookup apply(Class<?> cls) {
						try {
							return (MethodHandles.Lookup) privateLookupInMethodHandle.invoke(consulter, cls);
						} catch (Throwable exc) {
							return throwExceptionFunction.apply(exc);
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
	
	static class ForJava9 extends _ConsulterSupplyFunction<java.util.function.Function<Class<?>, MethodHandles.Lookup>> {
		ForJava9(Map<Object, Object> context) {
			FunctionProvider functionProvider = FunctionProvider.get(context);
			final _ThrowExceptionFunction throwExceptionFunction =
				functionProvider.getFunctionAdapter(_ThrowExceptionFunction.class, context); 
			try (
				InputStream inputStream =
					Resources.getAsInputStream(this.getClass().getClassLoader(), this.getClass().getPackage().getName().replace(".", "/") + "/ConsulterRetrieverForJDK9.bwc"
				);
			) {
				MethodHandle privateLookupInMethodHandle = functionProvider.getFunctionAdapter(_PrivateLookupInMethodHandleSupplier.class, context).get();
				Class<?> methodHandleWrapperClass = functionProvider.getFunctionAdapter(
					_DefineHookClassFunction.class, context
				).apply(Class.class, Streams.toByteArray(inputStream));
				functionProvider.getFunctionAdapter(_SetFieldValueFunction.class, context).accept(
					methodHandleWrapperClass, methodHandleWrapperClass.getDeclaredField("consulterRetriever"),
					privateLookupInMethodHandle
				);
				setFunction((java.util.function.Function<Class<?>, MethodHandles.Lookup>)
					functionProvider.getFunctionAdapter(_AllocateInstanceFunction.class, context).apply(methodHandleWrapperClass));
			} catch (Throwable exc) {
				throwExceptionFunction.apply(new InitializationException("Could not initialize consulter retriever", exc));
			}
		}

		
		@Override
		MethodHandles.Lookup apply(Class<?> input) {
			return function.apply(input);
		}
		
	}
}
