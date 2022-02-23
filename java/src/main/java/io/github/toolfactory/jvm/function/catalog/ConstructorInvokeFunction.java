/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2021-2022 Luke Hutchison, Roberto Gentili
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.ThrowingBiFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;


public interface ConstructorInvokeFunction extends ThrowingBiFunction<Constructor<?>, Object[], Object, Throwable> {

	public static class Abst implements ConstructorInvokeFunction {

		protected MethodHandle methodHandle;

		@Override
		public Object apply(Constructor<?> ctor, Object[] params) throws Throwable {
			return methodHandle.invokeWithArguments(ctor, params);
		}
	}

	public static class ForJava7 extends Abst {

		public ForJava7(Map<Object, Object> context) throws Throwable {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			Class<?> nativeAccessorImplClass = Class.forName("sun.reflect.NativeConstructorAccessorImpl");
			Method method = nativeAccessorImplClass.getDeclaredMethod("newInstance0", Constructor.class, Object[].class);
			ConsulterSupplyFunction getConsulterFunction = functionProvider.getOrBuildObject(ConsulterSupplyFunction.class, context);
			MethodHandles.Lookup consulter = getConsulterFunction.apply(nativeAccessorImplClass);
			method.setAccessible(true);
			methodHandle = consulter.unreflect(method);
		}

	}

	public static class ForJava9 extends Abst {

		public ForJava9(Map<Object, Object> context) throws Throwable {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			Class<?> nativeAccessorImplClass = Class.forName("jdk.internal.reflect.NativeConstructorAccessorImpl");
			Method method = nativeAccessorImplClass.getDeclaredMethod("newInstance0", Constructor.class, Object[].class);
			ConsulterSupplyFunction getConsulterFunction = functionProvider.getOrBuildObject(ConsulterSupplyFunction.class, context);
			MethodHandles.Lookup consulter = getConsulterFunction.apply(nativeAccessorImplClass);
			methodHandle = consulter.unreflect(method);
		}

	}

}