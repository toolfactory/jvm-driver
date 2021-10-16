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
package io.github.toolfactory.jvm.function.catalog;


import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.BiConsumer;
import io.github.toolfactory.jvm.util.BiConsumerAdapter;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Streams;


@SuppressWarnings("unchecked")
public interface SetAccessibleFunction extends BiConsumer<AccessibleObject, Boolean> {
	
	public static abstract class Abst<B> extends BiConsumerAdapter<B, AccessibleObject, Boolean> implements SetAccessibleFunction {
		protected ThrowExceptionFunction throwExceptionFunction;
		
		public Abst(Map<Object, Object> context) {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			throwExceptionFunction =
				functionProvider.getOrBuildObject(ThrowExceptionFunction.class, context); 
		}
	}
	
	public static class ForJava7 extends Abst<BiConsumer<AccessibleObject, Boolean>> {
		
		public ForJava7(Map<Object, Object> context) throws NoSuchMethodException, SecurityException, IllegalAccessException {
			super(context);
			final Method accessibleSetterMethod = AccessibleObject.class.getDeclaredMethod("setAccessible0", AccessibleObject.class, boolean.class);
			ObjectProvider functionProvider = ObjectProvider.get(context);
			final MethodHandle accessibleSetterMethodHandle = functionProvider.getOrBuildObject(
				ConsulterSupplier.class, context
			).get().unreflect(accessibleSetterMethod);
			setFunction(
				new BiConsumer<AccessibleObject, Boolean>() {
					@Override
					public void accept(AccessibleObject accessibleObject, Boolean flag) {
						try {
							accessibleSetterMethodHandle.invokeWithArguments(accessibleObject, flag);
						} catch (Throwable exc) {
							throwExceptionFunction.apply(exc);
						}
					}
				}
			);
		}
		
		@Override
		public void accept(AccessibleObject accessibleObject, Boolean flag) {
			function.accept(accessibleObject, flag);
		}
		
	}
	
	
	public static class ForJava9 extends Abst<java.util.function.BiConsumer<AccessibleObject, Boolean>> {
		
		public ForJava9(Map<Object, Object> context) throws NoSuchMethodException, SecurityException, IllegalAccessException, IOException, NoSuchFieldException {			
			super(context);
			try (
				InputStream inputStream = this.getClass().getResourceAsStream(
					"AccessibleSetterInvokerForJDK9.bwc"
				);
			) {	
				ObjectProvider functionProvider = ObjectProvider.get(context);
				Class<?> methodHandleWrapperClass = functionProvider.getOrBuildObject(
					DefineHookClassFunction.class, context
				).apply(AccessibleObject.class, Streams.toByteArray(inputStream));
				functionProvider.getOrBuildObject(SetFieldValueFunction.class, context).accept(
					methodHandleWrapperClass, methodHandleWrapperClass.getDeclaredField("methodHandleRetriever"),
					functionProvider.getOrBuildObject(ConsulterSupplyFunction.class, context).apply(methodHandleWrapperClass)
				);
				setFunction((java.util.function.BiConsumer<AccessibleObject, Boolean>)
					functionProvider.getOrBuildObject(AllocateInstanceFunction.class, context).apply(methodHandleWrapperClass));
			}
		}
		
		@Override
		public void accept(AccessibleObject accessibleObject, Boolean flag) {
			function.accept(accessibleObject, flag);
		}
		
	}
	
	public static interface Native extends SetAccessibleFunction {		
		
		public static class ForJava7 extends Abst<BiConsumer<AccessibleObject, Boolean>> implements Native {
			
			public ForJava7(Map<Object, Object> context) throws IllegalAccessException {
				super(context);
				ObjectProvider functionProvider = ObjectProvider.get(context);
				final GetDeclaredMethodFunction getDeclaredMethodFunction = functionProvider.getOrBuildObject(GetDeclaredMethodFunction.class, context);		
				Method accessibleSetterMethod = getDeclaredMethodFunction.apply(AccessibleObject.class, "setAccessible0", new Class<?>[]{AccessibleObject.class, boolean.class});
				final MethodHandle accessibleSetterMethodHandle = functionProvider.getOrBuildObject(
					ConsulterSupplier.class, context
				).get().unreflect(accessibleSetterMethod);
				setFunction(
					new BiConsumer<AccessibleObject, Boolean>() {
						@Override
						public void accept(AccessibleObject accessibleObject, Boolean flag) {
							try {
								accessibleSetterMethodHandle.invokeWithArguments(accessibleObject, flag);
							} catch (Throwable exc) {
								throwExceptionFunction.apply(exc);
							}
						}
					}
				);
			}
			
			@Override
			public void accept(AccessibleObject accessibleObject, Boolean flag) {
				function.accept(accessibleObject, flag);
			}
		}
		
		public static class ForJava9 extends Abst<BiConsumer<AccessibleObject, Boolean>> implements Native {
			
			public ForJava9(Map<Object, Object> context) throws IllegalAccessException {
				super(context);
				ObjectProvider functionProvider = ObjectProvider.get(context);
				GetDeclaredMethodFunction getDeclaredMethodFunction = functionProvider.getOrBuildObject(GetDeclaredMethodFunction.class, context);		
				final Method accessibleSetterMethod = getDeclaredMethodFunction.apply(AccessibleObject.class, "setAccessible0", new Class<?>[]{boolean.class});
				setFunction(
					new BiConsumer<AccessibleObject, Boolean>() {
						@Override
						public void accept(AccessibleObject target, Boolean flag) {
							try {
								io.github.toolfactory.narcissus.Narcissus.invokeMethod(target, accessibleSetterMethod, flag);
							} catch (Throwable exc) {
								throwExceptionFunction.apply(exc);
							}
						}
					}
				);
			}
			
			@Override
			public void accept(AccessibleObject accessibleObject, Boolean flag) {
				function.accept(accessibleObject, flag);
			}
		}
	}

}
