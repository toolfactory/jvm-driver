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
import io.github.toolfactory.jvm.util.Resources;
import io.github.toolfactory.jvm.util.Streams;


@SuppressWarnings("unchecked")
public abstract class SetAccessibleFunction<B> extends BiConsumerAdapter<B, AccessibleObject, Boolean>{
	protected ThrowExceptionFunction throwExceptionFunction;
	
	public SetAccessibleFunction(Map<Object, Object> context) {
		ObjectProvider functionProvider = ObjectProvider.get(context);
		throwExceptionFunction =
			functionProvider.getOrBuildObject(ThrowExceptionFunction.class, context); 
	}
	
	public static class ForJava7 extends SetAccessibleFunction<BiConsumer<AccessibleObject, Boolean>> {
		
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
							accessibleSetterMethodHandle.invoke(accessibleObject, flag);
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
	
	
	public static class ForJava9 extends SetAccessibleFunction<java.util.function.BiConsumer<AccessibleObject, Boolean>> {
		
		public ForJava9(Map<Object, Object> context) throws NoSuchMethodException, SecurityException, IllegalAccessException, IOException, NoSuchFieldException {			
			super(context);
			try (
				InputStream inputStream =
					Resources.getAsInputStream(this.getClass().getClassLoader(), this.getClass().getPackage().getName().replace(".", "/") + "/AccessibleSetterInvokerForJDK9.bwc"
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
	
	public static abstract class Native<B> extends SetAccessibleFunction<B>{		
		
		public Native(Map<Object, Object> context) {
			super(context);
		}

		public static class ForJava7 extends Native<BiConsumer<AccessibleObject, Boolean>> {
			
			public ForJava7(Map<Object, Object> context) {
				super(context);
				setFunction(new BiConsumer<AccessibleObject, Boolean>() {
					@Override
					public void accept(AccessibleObject accessibleObject, Boolean flag) {
						io.github.toolfactory.narcissus.Narcissus.setAccessible(accessibleObject, flag);
					}
				});
			}
			
			@Override
			public void accept(AccessibleObject accessibleObject, Boolean flag) {
				function.accept(accessibleObject, flag);
			}
		}
	}

}
