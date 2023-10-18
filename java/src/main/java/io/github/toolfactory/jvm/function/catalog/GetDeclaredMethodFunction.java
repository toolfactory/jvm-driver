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


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.ThrowingTriFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Strings;


public interface GetDeclaredMethodFunction extends ThrowingTriFunction<Class<?>, String, Class<?>[], Method, Throwable> {

	public static class ForJava7 implements GetDeclaredMethodFunction {
		protected GetDeclaredMethodsFunction getDeclaredMethodsFunction;

		public ForJava7(Map<Object, Object> context) {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			getDeclaredMethodsFunction = functionProvider.getOrBuildObject(GetDeclaredMethodsFunction.class, context);
		}

		@Override
		public Method apply(Class<?> cls, String name, Class<?>[] paramTypes) throws Throwable {
			if (paramTypes == null) {
				paramTypes = new Class<?>[0];
			}
			for (Method method : getDeclaredMethodsFunction.apply(cls)) {
				if (method.getName().equals(name)) {
					if (paramTypes.length == method.getParameterTypes().length) {
						Method toRet = method;
						Class<?>[] parameterTypes = method.getParameterTypes();
						for (int i = 0; i < parameterTypes.length; i++) {
							if (!parameterTypes[i].equals(paramTypes[i])) {
								toRet = null;
								break;
							}
						}
						if (toRet != null) {
							return toRet;
						}
					}
				}
			}
			Collection<String> classNames = new ArrayList<>();
			for (Class<?> paramType : paramTypes) {
				classNames.add(paramType.getName());
			}
			throw new NoSuchMethodException(
				Strings.compile("Method {}({}) not found in the class {}", name, Strings.join(", ", classNames), cls.getName())
			);
		}


	}
}
