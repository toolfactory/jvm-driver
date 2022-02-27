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


import java.util.Map;

import io.github.toolfactory.jvm.function.InitializeException;
import io.github.toolfactory.jvm.function.template.Consumer;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Strings;
import io.github.toolfactory.narcissus.Narcissus;


@SuppressWarnings("all")
public interface ThrowExceptionFunction extends Consumer<Throwable> {
	
	public <T> T apply(Throwable exception);
	
	public <T> T apply(String message, Object... placeHolderReplacements);
	
	public <T> T apply(int startingLevel, String message, Object... placeHolderReplacements);

	public static abstract class Abst implements ThrowExceptionFunction {
		
		@Override
		public <T> T apply(Throwable exception) {
			if (exception == null) {
				throw new NullPointerException("Input exception is null");
			}
			accept(exception);
			return null;
		}
		
		@Override
		public <T> T apply(String message, Object... placeHolderReplacements) {
			return apply(3, message, placeHolderReplacements);
		}
		
		@Override
		public<T> T apply(int stackTraceStartingLevel, String message, Object... placeHolderReplacements) {
			Throwable exception = null;
			StackTraceElement[] stackTraceOfException = null;
			if (placeHolderReplacements == null || placeHolderReplacements.length == 0) {
				exception = new Exception(message);
			} else {
				exception = new Exception(Strings.compile(message, placeHolderReplacements));
			}
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			stackTraceOfException = new StackTraceElement[stackTrace.length - stackTraceStartingLevel];
			System.arraycopy(stackTrace, stackTraceStartingLevel, stackTraceOfException, 0, stackTraceOfException.length);
			exception.setStackTrace(stackTraceOfException);
			accept(exception);
			return null;
		}

	}

	public static class ForJava7 extends Abst {
		protected sun.misc.Unsafe unsafe;

		public ForJava7(Map<Object, Object> context) {
			unsafe = ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
		}

		@Override
		public void accept(Throwable exception) {
			unsafe.throwException(exception);
		}


	}

	public static interface Native extends ThrowExceptionFunction {

		public static class ForJava7 extends Abst implements Native {

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
			public void accept(Throwable exception) {
				if (exception == null) {
					throw new NullPointerException("Input exception is null");
				}
				io.github.toolfactory.narcissus.Narcissus.sneakyThrow(exception);
			}

		}
	}


}
