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


import java.util.Map;

import io.github.toolfactory.jvm.function.InitializeException;
import io.github.toolfactory.jvm.function.template.ThrowingFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Strings;
import io.github.toolfactory.narcissus.Narcissus;


@SuppressWarnings("all")
public interface AllocateInstanceFunction extends ThrowingFunction<Class<?>, Object, Throwable> {

	public static class ForJava7 implements AllocateInstanceFunction {
		final UnsafeWrapper unsafeWrapper;

		public ForJava7(Map<Object, Object> context) {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			unsafeWrapper = functionProvider.getOrBuildObject(UnsafeWrapper.class, context);
		}

		@Override
		public Object apply(Class<?> input) throws Throwable {
			return unsafeWrapper.allocateInstance(input);
		}

	}

	public static interface Native extends AllocateInstanceFunction {

		public static class ForJava7 implements Native {

			public ForJava7(Map<Object, Object> context) throws InitializeException {
				checkNativeEngine();
			}

			protected void checkNativeEngine() throws InitializeException {
				if (!Narcissus.libraryLoaded) {
					throw new InitializeException(
						Strings.compile(
							"Could not initialize the native engine {}",
							io.github.toolfactory.narcissus.Narcissus.class.getName()
						)
					);
				}
			}

			@Override
			public Object apply(Class<?> cls) {
				return io.github.toolfactory.narcissus.Narcissus.allocateInstance(cls);
			}

		}
	}

}
