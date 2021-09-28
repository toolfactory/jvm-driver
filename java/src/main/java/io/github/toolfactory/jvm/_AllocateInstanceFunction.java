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


import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;


@SuppressWarnings("all")
interface _AllocateInstanceFunction extends Function<Class<?>, Object> {
	
	static class ForJava7 implements _AllocateInstanceFunction {
		final sun.misc.Unsafe unsafe;
		
		ForJava7(Map<Object, Object> context) {
			unsafe = FunctionProvider.get(context).getFunctionAdapter(_UnsafeSupplier.class, context).get();
		}

		@Override
		public Object apply(Class<?> input) {
			try {
				return unsafe.allocateInstance(input);
			} catch (InstantiationException exc) {
				return Throwables.getInstance().throwException(exc);
			}
		}
		
	}

	static interface Native extends _AllocateInstanceFunction {
		
		static class ForJava7 implements Native {
			
			ForJava7(Map<Object, Object> context) {}
			
			@Override
			public Object apply(Class<?> cls) {
				return io.github.toolfactory.narcissus.Narcissus.allocateInstance(cls);
			}
			
		}
	}
	
}