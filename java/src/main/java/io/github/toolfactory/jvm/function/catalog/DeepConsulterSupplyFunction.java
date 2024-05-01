/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
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


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.ThrowingFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.ThrowingFunctionAdapter;


@SuppressWarnings("unchecked")
public interface DeepConsulterSupplyFunction extends ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable> {

	public abstract static class Abst<F> extends ThrowingFunctionAdapter<F, Class<?>, MethodHandles.Lookup, Throwable> implements DeepConsulterSupplyFunction {

	}

	public static class ForJava7 extends Abst<ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>> {
		public ForJava7(Map<Object, Object> context) throws Throwable {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			//Check if allowedModes exists if not throw NoSuchFieldException (for Semeru JDK compatibility)
			functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context).apply(MethodHandles.Lookup.class, "allowedModes");
			setFunction(
				((ThrowingFunctionAdapter<ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>, ?, ?, Throwable>)
					functionProvider.getOrBuildObject(ConsulterSupplyFunction.class, context)).getFunction()
			);
		}

		@Override
		public MethodHandles.Lookup apply(Class<?> input) throws Throwable {
			return function.apply(input);
		}

		public static class ForSemeru extends Abst<ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>> {

			public ForSemeru(Map<Object, Object> context) throws Throwable {
				Constructor<MethodHandles.Lookup> lookupCtor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
				ObjectProvider functionProvider = ObjectProvider.get(context);
				functionProvider.getOrBuildObject(SetAccessibleFunction.class, context).accept (lookupCtor, true);
				final MethodHandle methodHandle = lookupCtor.newInstance(
					MethodHandles.Lookup.class, io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.INTERNAL_PRIVILEGED
				).findConstructor(
					MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class, int.class)
				);
				setFunction(
					new ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>() {
						@Override
						public MethodHandles.Lookup apply(Class<?> cls) throws Throwable {
							return (MethodHandles.Lookup)methodHandle.invokeWithArguments(
								cls,
								ConsulterSupplier.ForJava7.ForSemeru.isInternal(cls.getPackage()) ?
								io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.INTERNAL_PRIVILEGED :
								io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.FULL_ACCESS_MASK
							);
						}
					}
				);

			}

			@Override
			public MethodHandles.Lookup apply(Class<?> input) throws Throwable {
				return function.apply(input);
			}
		}

	}

	public static class ForJava9 extends Abst<ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>> {

		public ForJava9(Map<Object, Object> context) throws Throwable {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			//Check if allowedModes exists if not throw NoSuchFieldException (for Semeru JDK compatibility)
			functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context).apply(MethodHandles.Lookup.class, "allowedModes");
			Constructor<MethodHandles.Lookup> lookupCtor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
			functionProvider.getOrBuildObject(SetAccessibleFunction.class, context).accept (lookupCtor, true);
			final MethodHandle methodHandle = lookupCtor.newInstance(MethodHandles.Lookup.class, ConsulterSupplier.ForJava7.TRUSTED).findConstructor(
				MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class, int.class)
			);
			setFunction(
				new ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>() {
					@Override
					public MethodHandles.Lookup apply(Class<?> cls) throws Throwable {
						return (MethodHandles.Lookup)methodHandle.invokeWithArguments(cls, ConsulterSupplier.ForJava7.TRUSTED);
					}
				}
			);

		}


		@Override
		public MethodHandles.Lookup apply(Class<?> input) throws Throwable {
			return function.apply(input);
		}

		public static class ForSemeru extends Abst<ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>> {
			public ForSemeru(Map<Object, Object> context) throws Throwable {
				Constructor<MethodHandles.Lookup> lookupCtor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
				ObjectProvider functionProvider = ObjectProvider.get(context);
				functionProvider.getOrBuildObject(SetAccessibleFunction.class, context).accept (lookupCtor, true);
				final MethodHandle methodHandle = lookupCtor.newInstance(
					MethodHandles.Lookup.class,
					io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.INTERNAL_PRIVILEGED
				).findConstructor(
					MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class, int.class)
				);
				setFunction(
					new ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>() {
						@Override
						public MethodHandles.Lookup apply(Class<?> cls) throws Throwable {
							return (MethodHandles.Lookup)methodHandle.invokeWithArguments(
								cls,
								ConsulterSupplier.ForJava9.ForSemeru.isInternal(cls.getPackage()) ?
								io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.INTERNAL_PRIVILEGED :
								io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava9.ForSemeru.FULL_ACCESS_MASK
							);
						}
					}
				);

			}

			@Override
			public MethodHandles.Lookup apply(Class<?> input) throws Throwable {
				return function.apply(input);
			}
		}

	}


	public static class ForJava14 extends Abst<ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>> {

		public ForJava14(Map<Object, Object> context) throws Throwable {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			//Check if allowedModes exists if not throw NoSuchFieldException (for Semeru JDK compatibility)
			functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context).apply(MethodHandles.Lookup.class, "allowedModes");
			Constructor<?> lookupCtor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Class.class, int.class);
			functionProvider.getOrBuildObject(SetAccessibleFunction.class, context).accept (lookupCtor, true);
			final MethodHandle mthHandle = ((MethodHandles.Lookup)lookupCtor.newInstance(MethodHandles.Lookup.class, null, ConsulterSupplier.ForJava7.TRUSTED)).findConstructor(
				MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class, Class.class, int.class)
			);
			setFunction(
				new ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>() {
					@Override
					public MethodHandles.Lookup apply(Class<?> cls) throws Throwable {
						return (MethodHandles.Lookup)mthHandle.invokeWithArguments(cls, null, ConsulterSupplier.ForJava7.TRUSTED);
					}
				}
			);

		}


		@Override
		public MethodHandles.Lookup apply(Class<?> input) throws Throwable {
			return function.apply(input);
		}

	}

	public static interface ForJava17 extends DeepConsulterSupplyFunction {

		public static class ForSemeru extends Abst<ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>> implements ForJava17 {
			public ForSemeru(Map<Object, Object> context) throws Throwable {
				Constructor<MethodHandles.Lookup> lookupCtor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Class.class, int.class);
				ObjectProvider functionProvider = ObjectProvider.get(context);
				functionProvider.getOrBuildObject(SetAccessibleFunction.class, context).accept (lookupCtor, true);
				final MethodHandle methodHandle = lookupCtor.newInstance(
					MethodHandles.Lookup.class, null, ConsulterSupplier.ForJava7.TRUSTED
				).findConstructor(
					MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class, Class.class, int.class)
				);
				setFunction(
					new ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable>() {
						@Override
						public MethodHandles.Lookup apply(Class<?> cls) throws Throwable {
							return (MethodHandles.Lookup)methodHandle.invokeWithArguments(
								cls,
								null,
								ConsulterSupplier.ForJava7.TRUSTED
							);
						}
					}
				);

			}

			@Override
			public MethodHandles.Lookup apply(Class<?> input) throws Throwable {
				return function.apply(input);
			}
		}

	}
}
