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
package io.github.toolfactory.jvm.function.catalog;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.Supplier;
import io.github.toolfactory.jvm.util.Classes;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Streams;


public interface ClassLoaderDelegateClassSupplier extends Supplier<Class<?>> {

	public static class ForJava7 implements ClassLoaderDelegateClassSupplier {

		public ForJava7(Map<Object, Object> context) {}

		@Override
		public Class<?> get() {
			return null;
		}

	}

	public static class ForJava9 implements ClassLoaderDelegateClassSupplier {
		protected Class<?> cls;

		public ForJava9(Map<Object, Object> context) throws Throwable {
			loadClass(context);
		}

		protected void loadClass(Map<Object, Object> context) throws Throwable, IOException {
			try (
				InputStream inputStream = Classes.class.getResourceAsStream(
					"ClassLoaderDelegateForJDK9.bwc"
				);
			) {
				ObjectProvider functionProvider = ObjectProvider.get(context);
				cls = functionProvider.getOrBuildObject(
					DefineHookClassFunction.class, context
				).apply(
					functionProvider.getOrBuildObject(BuiltinClassLoaderClassSupplier.class, context).get(),
					Streams.toByteArray(inputStream)
				);
			}
		}

		@Override
		public Class<?> get() {
			return cls;
		}

	}

	public static interface ForJava17 extends ClassLoaderDelegateClassSupplier {

		public static class ForSemeru extends ForJava9 implements ForJava17 {

			public ForSemeru(Map<Object, Object> context) throws Throwable {
				super(context);
			}

			@Override
			protected void loadClass(Map<Object, Object> context) throws Throwable, IOException {
				ObjectProvider functionProvider = ObjectProvider.get(context);
				Class<?> thisClass = getClass();
				ClassLoader thisClassClassLoader = thisClass.getClassLoader();
				GetClassByNameFunction getClassByNameFunction = functionProvider.getOrBuildObject(GetClassByNameFunction.class, context);
				Class<?> sharedSecretsClass = getClassByNameFunction.apply("jdk.internal.access.SharedSecrets", false, thisClassClassLoader, thisClass);
				Method getJavaLangAccessMethod = sharedSecretsClass.getDeclaredMethod("getJavaLangAccess");
				Class<?> javaLangAccessClass = getClassByNameFunction.apply("jdk.internal.access.JavaLangAccess", false, thisClassClassLoader, thisClass);
				Method addExports = javaLangAccessClass.getDeclaredMethod("addExports", java.lang.Module.class, String.class, java.lang.Module.class);
				MethodInvokeFunction methodInvoker = functionProvider.getOrBuildObject(MethodInvokeFunction.class, context);
				methodInvoker.apply(
					addExports,
					methodInvoker.apply(getJavaLangAccessMethod, null, null),
					new Object[] {
						java.lang.ModuleLayer.boot().findModule("java.base").get(),
						"jdk.internal.loader",
						thisClass.getModule()
					}
				);
				super.loadClass(context);
			}

		}


	}


}
