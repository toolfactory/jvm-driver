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
package io.github.toolfactory.jvm.function;


import java.util.Map;

import io.github.toolfactory.jvm.function.template.Consumer;
import io.github.toolfactory.jvm.function.util.Strings;


@SuppressWarnings("all")
public abstract class _ThrowExceptionFunction implements Consumer<Throwable> {
	
	public<T> T apply(Object exceptionOrMessage, Object... placeHolderReplacements) {
		Throwable exception = null;
		StackTraceElement[] stackTraceOfException = null;
		if (exceptionOrMessage instanceof String) {
			if (placeHolderReplacements == null || placeHolderReplacements.length == 0) {
				exception = new Exception((String)exceptionOrMessage);
			} else {
				exception = new Exception(Strings.compile((String)exceptionOrMessage, placeHolderReplacements));
			}
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			stackTraceOfException = new StackTraceElement[stackTrace.length - 2];
			System.arraycopy(stackTrace, 2, stackTraceOfException, 0, stackTraceOfException.length);
		} else {
			exception = (Throwable)exceptionOrMessage;
			StackTraceElement[] stackTrace = exception.getStackTrace();
			stackTraceOfException = new StackTraceElement[stackTrace.length + 1];
			stackTraceOfException[0] = Thread.currentThread().getStackTrace()[2];
			System.arraycopy(stackTrace, 0, stackTraceOfException, 1, stackTrace.length);
		}
		exception.setStackTrace(stackTraceOfException);
		accept(exception);
		return null;
	}
	
	public static class ForJava7 extends _ThrowExceptionFunction {
		final sun.misc.Unsafe unsafe;
		
		public ForJava7(Map<Object, Object> context) {
			unsafe = Provider.get(context).getFunctionAdapter(_UnsafeSupplier.class, context).get();
		}

		@Override
		public void accept(Throwable exception) {
			unsafe.throwException(exception);			
		}

		
	}

	public static abstract class Native extends _ThrowExceptionFunction {
		
		public static class ForJava7 extends Native {
			
			public ForJava7(Map<Object, Object> context) {}
			
			@Override
			public void accept(Throwable exception) {
				io.github.toolfactory.narcissus.Narcissus.sneakyThrow(exception);			
			}
			
		}
	}
	
	
}
