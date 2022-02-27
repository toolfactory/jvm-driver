/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Luke Hutchison, Roberto Gentili
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


import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.ThrowingFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Streams;
import io.github.toolfactory.jvm.util.ThrowingFunctionAdapter;


@SuppressWarnings("unchecked")
public interface ConsulterSupplyFunction extends ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable> {

	public static abstract class Abst<F> extends ThrowingFunctionAdapter<F, Class<?>, MethodHandles.Lookup, Throwable> implements ConsulterSupplyFunction {

	}

	public static class ForJava7 extends Abst<ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>> {
		public ForJava7(Map<Object, Object> context) {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			final MethodHandles.Lookup consulter = functionProvider.getOrBuildObject(ConsulterSupplier.class, context).get();
			final MethodHandle privateLookupInMethodHandle = functionProvider.getOrBuildObject(PrivateLookupInMethodHandleSupplier.class, context).get();
			setFunction(
				new ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>() {
					@Override
					public MethodHandles.Lookup apply(Class<?> cls) throws Throwable {
						return (MethodHandles.Lookup) privateLookupInMethodHandle.invokeWithArguments(consulter, cls);
					}
				}
			);
		}

		@Override
		public MethodHandles.Lookup apply(Class<?> input) throws Throwable {
			return function.apply(input);
		}

	}

	public static class ForJava9 extends Abst<java.util.function.Function<Class<?>, MethodHandles.Lookup>>  {

		public ForJava9(Map<Object, Object> context) throws Throwable {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			try (
				InputStream inputStream = ConsulterSupplyFunction.class.getResourceAsStream(
					"ConsulterRetrieverForJDK9.bwc"
				);
			) {
				MethodHandle privateLookupInMethodHandle = functionProvider.getOrBuildObject(PrivateLookupInMethodHandleSupplier.class, context).get();
				Class<?> methodHandleWrapperClass = functionProvider.getOrBuildObject(
					DefineHookClassFunction.class, context
				).apply(Class.class, Streams.toByteArray(inputStream));
				functionProvider.getOrBuildObject(SetFieldValueFunction.class, context).accept(
					methodHandleWrapperClass, methodHandleWrapperClass.getDeclaredField("consulterRetriever"),
					privateLookupInMethodHandle
				);
				empowerMainConsulter((MethodHandles.Lookup)methodHandleWrapperClass.getDeclaredField("mainConsulter").get(null), context);
				setFunction((java.util.function.Function<Class<?>, MethodHandles.Lookup>)
					functionProvider.getOrBuildObject(AllocateInstanceFunction.class, context).apply(methodHandleWrapperClass));
			}
		}

		protected void empowerMainConsulter(MethodHandles.Lookup consulter, Map<Object, Object> context) throws Throwable {

		}

		@Override
		public MethodHandles.Lookup apply(Class<?> input) {
			return function.apply(input);
		}

	}

	public static interface ForJava17 extends ConsulterSupplyFunction {

		public static class ForSemeru extends ConsulterSupplyFunction.ForJava9 implements ForJava17 {

			public ForSemeru(Map<Object, Object> context) throws Throwable {
				super(context);
			}

			@Override
			protected void empowerMainConsulter(MethodHandles.Lookup consulter, Map<Object, Object> context) throws Throwable {
				sun.misc.Unsafe unsafe = ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
				unsafe.putInt(consulter, 20, -1);
			}


			@Override
			public MethodHandles.Lookup apply(Class<?> input) {
				return function.apply(input);
			}
		}

	}

	public static interface Native extends ConsulterSupplyFunction {

		public static interface ForJava17 extends Native {

			public static class ForSemeru extends ConsulterSupplyFunction.ForJava17.ForSemeru implements ForJava17 {

				public ForSemeru(Map<Object, Object> context) throws Throwable {
					super(context);
				}

				@Override
				protected void empowerMainConsulter(MethodHandles.Lookup consulter, Map<Object, Object> context) throws Throwable {
					io.github.toolfactory.narcissus.Narcissus.setField(
						consulter,
						io.github.toolfactory.narcissus.Narcissus.findField(consulter.getClass(), "allowedModes"),
						-1
					);
				}

			}

		}

	}

	public static interface Hybrid extends ConsulterSupplyFunction {

		public static interface ForJava17 extends Hybrid {

			public static class ForSemeru extends Native.ForJava17.ForSemeru implements Hybrid.ForJava17 {

				public ForSemeru(Map<Object, Object> context) throws Throwable {
					super(context);
				}
			}
		}
	}
}
