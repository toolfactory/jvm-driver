/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2023 Luke Hutchison, Roberto Gentili
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
import java.util.Map;

import io.github.toolfactory.jvm.function.template.ThrowingQuadFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;


public interface GetClassByNameFunction extends ThrowingQuadFunction<String, Boolean, ClassLoader, Class<?>, Class<?>, Throwable> {

	public abstract class Abst  implements GetClassByNameFunction {
		protected MethodHandle classFinder;

		public Abst(Map<Object, Object> context) throws Throwable {
			classFinder = retrieveClassFinder(context);
		}

		protected abstract MethodHandle retrieveClassFinder(final Map<Object, Object> context) throws NoSuchMethodException, IllegalAccessException, Throwable;

	}

	public static class ForJava7 extends Abst {

		public ForJava7(Map<Object, Object> context) throws Throwable {
			super(context);
		}

		@Override
		protected MethodHandle retrieveClassFinder(Map<Object, Object> context) throws Throwable {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			MethodHandles.Lookup consulter = functionProvider.getOrBuildObject(DeepConsulterSupplyFunction.class, context).apply(Class.class);
			return consulter.findStatic(
				Class.class, "forName0",
				MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class)
			);
		}

		@Override
		public Class<?> apply(String className, Boolean initialize, ClassLoader classLoader, Class<?> caller) throws Throwable {
			return (Class<?>)classFinder.invokeWithArguments(className, initialize.booleanValue(), classLoader, caller);
		}

		public static class ForSemeru extends Abst {

			public ForSemeru(Map<Object, Object> context) throws Throwable {
				super(context);
			}

			@Override
			protected MethodHandle retrieveClassFinder(Map<Object, Object> context) throws Throwable {
				ObjectProvider functionProvider = ObjectProvider.get(context);
				MethodHandles.Lookup consulter = functionProvider.getOrBuildObject(DeepConsulterSupplyFunction.class, context).apply(Class.class);
				return consulter.findStatic(
					Class.class, "forNameImpl",
					MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class)
				);
			}

			@Override
			public Class<?> apply(String className, Boolean initialize, ClassLoader classLoader, Class<?> caller) throws Throwable {
				return (Class<?>)classFinder.invokeWithArguments(className, initialize, classLoader);
			}

		}

	}

}
