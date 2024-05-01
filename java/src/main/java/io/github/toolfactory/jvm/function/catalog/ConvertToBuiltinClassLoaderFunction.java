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
import java.util.Map;

import io.github.toolfactory.jvm.function.template.ThrowingFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;


public interface ConvertToBuiltinClassLoaderFunction extends ThrowingFunction<ClassLoader, ClassLoader, Throwable> {
	
	public static class ForJava7 implements ConvertToBuiltinClassLoaderFunction {

		public ForJava7(Map<Object, Object> context) {}

		@Override
		public ClassLoader apply(ClassLoader classLoader) {
			return null;
		}

	}
	
	public static class ForJava9 implements ConvertToBuiltinClassLoaderFunction {
		protected MethodHandles.Lookup consulter;
		protected Class<?> builtinClassLoaderClass;
		protected MethodHandle classLoaderDelegateConstructor;

		public ForJava9(Map<Object, Object> context) throws Throwable {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			Class<?> classLoaderDelegateClass = functionProvider.getOrBuildObject(ClassLoaderDelegateClassSupplier.class, context).get();
			consulter = functionProvider.getOrBuildObject(DeepConsulterSupplyFunction.class, context).apply(classLoaderDelegateClass);
			Class<?> builtinClassLoaderDelegateClass = functionProvider.getOrBuildObject(BuiltinClassLoaderClassSupplier.class, context).get();
			classLoaderDelegateConstructor = consulter.findConstructor(
				classLoaderDelegateClass,
				MethodType.methodType(
					void.class, builtinClassLoaderDelegateClass, ClassLoader.class, MethodHandle.class
				)
			);
			builtinClassLoaderClass = functionProvider.getOrBuildObject(BuiltinClassLoaderClassSupplier.class, context).get();
		}

		@Override
		public ClassLoader apply(ClassLoader classLoader) throws Throwable {
			if (builtinClassLoaderClass.isAssignableFrom(classLoader.getClass())) {
				return classLoader;
			}
			MethodHandle loadClassMethodHandle = consulter.findVirtual(
				classLoader.getClass(),
				"loadClass",
				MethodType.methodType(
					Class.class, String.class, boolean.class
				)
			);
			return (ClassLoader)classLoaderDelegateConstructor.invokeWithArguments(null, classLoader, loadClassMethodHandle);
		}

	}


}
