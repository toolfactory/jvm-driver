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
import java.lang.reflect.Method;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.ThrowingBiConsumer;
import io.github.toolfactory.jvm.util.ObjectProvider;

public interface StopThreadFunction extends ThrowingBiConsumer<Thread, Throwable, Throwable> {

	public abstract static class Abst implements StopThreadFunction {

		protected MethodHandle methodHandle;

		@Override
		public void accept(Thread thread, Throwable threadDeath) throws Throwable {
			methodHandle.invokeWithArguments(thread, threadDeath);
		}

	}

	public static class ForJava7 extends Abst {

		public ForJava7(Map<Object, Object> context) throws Throwable {
			final Method stopThreadMethod = retrieveStopThreadMethod();
			ObjectProvider functionProvider = ObjectProvider.get(context);
			functionProvider.getOrBuildObject(SetAccessibleFunction.class, context).accept (stopThreadMethod, true);
			methodHandle = functionProvider.getOrBuildObject(
				ConsulterSupplier.class, context
			).get().unreflect(stopThreadMethod);
		}

		protected Method retrieveStopThreadMethod() throws NoSuchMethodException, SecurityException {
			return Thread.class.getDeclaredMethod("stop0", Object.class);
		}

		public static class ForSemeru extends ForJava7 {

			public ForSemeru(Map<Object, Object> context) throws Throwable {
				super(context);
			}

			@Override
			protected Method retrieveStopThreadMethod() throws NoSuchMethodException, SecurityException {
				return Thread.class.getDeclaredMethod("stopImpl", Throwable.class);
			}

		}

	}

	public static class ForJava20 extends ForJava7 {

		public ForJava20(Map<Object, Object> context) throws Throwable {
			super(context);
		}

		@Override
		protected Method retrieveStopThreadMethod() throws NoSuchMethodException, SecurityException {
			return Thread.class.getDeclaredMethod("interrupt0");
		}

		@Override
		public void accept(Thread thread, Throwable threadDeath) throws Throwable {
			methodHandle.invokeWithArguments(thread);
		}
		
		public static class ForSemeru extends ForJava7.ForSemeru {

			public ForSemeru(Map<Object, Object> context) throws Throwable {
				super(context);
			}

			@Override
			protected Method retrieveStopThreadMethod() throws NoSuchMethodException, SecurityException {
				return Thread.class.getDeclaredMethod("interrupt0");
			}

			@Override
			public void accept(Thread thread, Throwable threadDeath) throws Throwable {
				methodHandle.invokeWithArguments(thread);
			}
		}

	}

}
