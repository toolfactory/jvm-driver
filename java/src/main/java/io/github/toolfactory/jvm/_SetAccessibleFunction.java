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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Map;

import io.github.toolfactory.jvm.Driver.InitializationException;


@SuppressWarnings("unchecked")
abstract class _SetAccessibleFunction<B> extends BiConsumerAdapter<B, AccessibleObject, Boolean>{

	static class ForJava7 extends _SetAccessibleFunction<BiConsumer<AccessibleObject, Boolean>> {
		
		ForJava7(Map<Object, Object> context) throws NoSuchMethodException, SecurityException, IllegalAccessException {
			final Method accessibleSetterMethod = AccessibleObject.class.getDeclaredMethod("setAccessible0", AccessibleObject.class, boolean.class);
			FunctionProvider functionProvider = FunctionProvider.get(context);
			final MethodHandle accessibleSetterMethodHandle = functionProvider.getFunctionAdapter(
				_ConsulterSupplier.class, context
			).get().unreflect(accessibleSetterMethod);
			setFunction(
				new BiConsumer<AccessibleObject, Boolean>() {
					@Override
					public void accept(AccessibleObject accessibleObject, Boolean flag) {
						try {
							accessibleSetterMethodHandle.invoke(accessibleObject, flag);
						} catch (Throwable exc) {
							Throwables.getInstance().throwException(exc);
						}
					}
				}
			);
		}
		
		@Override
		void accept(AccessibleObject accessibleObject, Boolean flag) {
			function.accept(accessibleObject, flag);
		}
		
	}
	
	
	static class ForJava9 extends _SetAccessibleFunction<java.util.function.BiConsumer<AccessibleObject, Boolean>> {
		
		ForJava9(Map<Object, Object> context) throws NoSuchMethodException, SecurityException, IllegalAccessException {			
			try (
				InputStream inputStream =
					Resources.getAsInputStream(this.getClass().getClassLoader(), this.getClass().getPackage().getName().replace(".", "/") + "/AccessibleSetterInvokerForJDK9.bwc"
				);
			) {	
				FunctionProvider functionProvider = FunctionProvider.get(context);
				Class<?> methodHandleWrapperClass = functionProvider.getFunctionAdapter(
					_DefineHookClassFunction.class, context
				).apply(AccessibleObject.class, Streams.toByteArray(inputStream));
				functionProvider.getFunctionAdapter(_SetFieldValueFunction.class, context).accept(
					methodHandleWrapperClass, methodHandleWrapperClass.getDeclaredField("methodHandleRetriever"),
					functionProvider.getFunctionAdapter(_ConsulterSupplyFunction.class, context).apply(methodHandleWrapperClass)
				);
				setFunction((java.util.function.BiConsumer<AccessibleObject, Boolean>)
					functionProvider.getFunctionAdapter(_AllocateInstanceFunction.class, context).apply(methodHandleWrapperClass));
			} catch (Throwable exc) {
				Throwables.getInstance().throwException(new InitializationException("Could not initialize accessible setter", exc));
			}
		}
		
		@Override
		void accept(AccessibleObject accessibleObject, Boolean flag) {
			function.accept(accessibleObject, flag);
		}
		
	}
	
	static abstract class Native<B> extends _SetAccessibleFunction<B>{
		
		static class ForJava7 extends Native<BiConsumer<AccessibleObject, Boolean>> {
			
			ForJava7(Map<Object, Object> context) {
				setFunction(new BiConsumer<AccessibleObject, Boolean>() {
					@Override
					public void accept(AccessibleObject accessibleObject, Boolean flag) {
						io.github.toolfactory.narcissus.Narcissus.setAccessible(accessibleObject, flag);
					}
				});
			}
			
			@Override
			void accept(AccessibleObject accessibleObject, Boolean flag) {
				function.accept(accessibleObject, flag);
			}
		}
	}

}
