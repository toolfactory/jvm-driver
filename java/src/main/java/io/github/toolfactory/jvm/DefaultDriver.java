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


import java.lang.invoke.MethodHandle;
import java.util.Map;

import io.github.toolfactory.jvm.function.catalog.AllocateInstanceFunction;
import io.github.toolfactory.jvm.function.catalog.BuiltinClassLoaderClassSupplier;
import io.github.toolfactory.jvm.function.catalog.ClassLoaderDelegateClassSupplier;
import io.github.toolfactory.jvm.function.catalog.ConstructorInvokeMethodHandleSupplier;
import io.github.toolfactory.jvm.function.catalog.ConsulterSupplyFunction;
import io.github.toolfactory.jvm.function.catalog.DeepConsulterSupplyFunction;
import io.github.toolfactory.jvm.function.catalog.DefineHookClassFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredConstructorsFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredFieldsFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredMethodsFunction;
import io.github.toolfactory.jvm.function.catalog.GetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedClassesFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedPackagesFunction;
import io.github.toolfactory.jvm.function.catalog.GetPackageFunction;
import io.github.toolfactory.jvm.function.catalog.MethodInvokeMethodHandleSupplier;
import io.github.toolfactory.jvm.function.catalog.SetAccessibleFunction;
import io.github.toolfactory.jvm.function.catalog.SetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.ThrowExceptionFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;


public class DefaultDriver extends DriverAbst {	
	
	protected ThrowExceptionFunction initExceptionThrower(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			ThrowExceptionFunction.class, initializationContext
		);
	}

	protected AllocateInstanceFunction initAllocateInstanceInvoker(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			AllocateInstanceFunction.class, initializationContext
		);
	}

	protected GetFieldValueFunction initFieldValueRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			GetFieldValueFunction.class, initializationContext
		);
	}

	protected SetFieldValueFunction initFieldValueSetter(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			SetFieldValueFunction.class, initializationContext
		);
	}

	protected DefineHookClassFunction initHookClassDefiner(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			DefineHookClassFunction.class, initializationContext
		);
	}

	protected ConsulterSupplyFunction<?> initConsulterRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {	
		return functionProvider.getOrBuildObject(
			ConsulterSupplyFunction.class, initializationContext
		);
	}

	protected GetDeclaredFieldsFunction initDeclaredFieldsRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			GetDeclaredFieldsFunction.class, initializationContext
		);
	}

	protected GetDeclaredMethodsFunction initDeclaredMethodsRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			GetDeclaredMethodsFunction.class, initializationContext
		);
	}

	protected GetDeclaredConstructorsFunction initDeclaredConstructorsRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			GetDeclaredConstructorsFunction.class, initializationContext
		);
	}

	protected SetAccessibleFunction<?> initAccessibleSetter(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			SetAccessibleFunction.class, initializationContext
		);
	}

	protected MethodHandle initConstructorInvoker(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			ConstructorInvokeMethodHandleSupplier.class, initializationContext
		).get();
	}

	protected MethodHandle initMethodInvoker(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			MethodInvokeMethodHandleSupplier.class, initializationContext
		).get();
	}

	protected GetPackageFunction initPackageRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			GetPackageFunction.class, initializationContext
		);
	}

	protected Class<?> initBuiltinClassLoaderClass(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			BuiltinClassLoaderClassSupplier.class, initializationContext
		).get();
	}

	protected Class<?> initClassLoaderDelegateClass(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			ClassLoaderDelegateClassSupplier.class, initializationContext
		).get();
	}

	protected DeepConsulterSupplyFunction<?> replaceConsulterWithDeepConsulter(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {	
		return functionProvider.getOrBuildObject(
			DeepConsulterSupplyFunction.class, initializationContext
		);
	}

	protected GetLoadedClassesFunction initLoadedClassesRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			GetLoadedClassesFunction.class, initializationContext
		);
	}

	protected GetLoadedPackagesFunction initLoadedPackagesRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext) {
		return functionProvider.getOrBuildObject(
			GetLoadedPackagesFunction.class, initializationContext
		);
	}
	
}
