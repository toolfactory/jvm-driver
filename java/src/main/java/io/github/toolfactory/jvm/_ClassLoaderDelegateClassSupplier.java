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


import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


interface _ClassLoaderDelegateClassSupplier extends Supplier<Class<?>> {
	
	static class ForJava7 implements _ClassLoaderDelegateClassSupplier{
		
		ForJava7(Map<Object, Object> context) {}
		
		@Override
		public Class<?> get() {
			return null;
		}
		
	}
	
	static class ForJava9 implements _ClassLoaderDelegateClassSupplier{
		Class<?> cls;
		ForJava9(Map<Object, Object> context) throws ClassNotFoundException, IOException {
			try (
				InputStream inputStream =
					Resources.getAsInputStream(this.getClass().getClassLoader(), this.getClass().getPackage().getName().replace(".", "/") + "/ClassLoaderDelegateForJDK9.bwc"
				);
			) {
				FunctionProvider functionProvider = FunctionProvider.get(context);
				cls = functionProvider.getFunctionAdapter(
					_DefineHookClassFunction.class, context
				).apply(
					functionProvider.getFunctionAdapter(_BuiltinClassLoaderClassSupplier.class, context).get(), 
					Streams.toByteArray(inputStream)
				);
			}
		}
		
		@Override
		public Class<?> get() {
			return cls;
		}
		
	}
	
}