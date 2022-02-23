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
package io.github.toolfactory.jvm;


import io.github.toolfactory.jvm.function.catalog.AllocateInstanceFunction;
import io.github.toolfactory.jvm.function.catalog.BuiltinClassLoaderClassSupplier;
import io.github.toolfactory.jvm.function.catalog.ClassLoaderDelegateClassSupplier;
import io.github.toolfactory.jvm.function.catalog.ConstructorInvokeFunction;
import io.github.toolfactory.jvm.function.catalog.ConsulterSupplyFunction;
import io.github.toolfactory.jvm.function.catalog.ConvertToBuiltinClassLoaderFunction;
import io.github.toolfactory.jvm.function.catalog.DeepConsulterSupplyFunction;
import io.github.toolfactory.jvm.function.catalog.DefineHookClassFunction;
import io.github.toolfactory.jvm.function.catalog.GetClassByNameFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredConstructorsFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredFieldsFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredMethodsFunction;
import io.github.toolfactory.jvm.function.catalog.GetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedClassesRetrieverFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedPackagesFunction;
import io.github.toolfactory.jvm.function.catalog.GetPackageFunction;
import io.github.toolfactory.jvm.function.catalog.GetResourcesFunction;
import io.github.toolfactory.jvm.function.catalog.MethodInvokeFunction;
import io.github.toolfactory.jvm.function.catalog.SetAccessibleFunction;
import io.github.toolfactory.jvm.function.catalog.SetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.ThrowExceptionFunction;


public class DefaultDriver extends DriverAbst {

	@Override
	protected Class<? extends ThrowExceptionFunction> getThrowExceptionFunctionClass() {
		return ThrowExceptionFunction.class;
	}


	@Override
	protected Class<? extends AllocateInstanceFunction> getAllocateInstanceFunctionClass() {
		return AllocateInstanceFunction.class;
	}


	@Override
	protected Class<? extends GetFieldValueFunction> getGetFieldValueFunctionClass() {
		return GetFieldValueFunction.class;
	}


	@Override
	protected Class<? extends SetFieldValueFunction> getSetFieldValueFunctionClass() {
		return SetFieldValueFunction.class;
	}


	@Override
	protected Class<? extends DefineHookClassFunction> getDefineHookClassFunctionClass() {
		return DefineHookClassFunction.class;
	}


	@Override
	protected Class<? extends ConsulterSupplyFunction> getConsulterSupplyFunctionClass() {
		return ConsulterSupplyFunction.class;
	}


	@Override
	protected Class<? extends GetDeclaredFieldsFunction> getGetDeclaredFieldsFunctionClass() {
		return GetDeclaredFieldsFunction.class;
	}


	@Override
	protected Class<? extends GetDeclaredMethodsFunction> getGetDeclaredMethodsFunctionClass() {
		return GetDeclaredMethodsFunction.class;
	}


	@Override
	protected Class<? extends GetDeclaredConstructorsFunction> getGetDeclaredConstructorsFunctionClass() {
		return GetDeclaredConstructorsFunction.class;
	}


	@Override
	protected Class<? extends SetAccessibleFunction> getSetAccessibleFunctionClass() {
		return SetAccessibleFunction.class;
	}


	@Override
	protected Class<? extends ConstructorInvokeFunction> getConstructorInvokeFunctionClass() {
		return ConstructorInvokeFunction.class;
	}


	@Override
	protected Class<? extends MethodInvokeFunction> getMethodInvokeFunctionClass() {
		return MethodInvokeFunction.class;
	}


	@Override
	protected Class<? extends GetPackageFunction> getGetPackageFunctionClass() {
		return GetPackageFunction.class;
	}


	@Override
	protected Class<? extends GetResourcesFunction> getGetResourcesFunctionClass() {
		return GetResourcesFunction.class;
	}


	@Override
	protected Class<? extends GetClassByNameFunction> getGetClassByNameFunctionClass() {
		return GetClassByNameFunction.class;
	}


	@Override
	protected Class<? extends BuiltinClassLoaderClassSupplier> getBuiltinClassLoaderClassSupplierClass() {
		return BuiltinClassLoaderClassSupplier.class;
	}


	@Override
	protected Class<? extends ClassLoaderDelegateClassSupplier> getClassLoaderDelegateClassSupplierClass() {
		return ClassLoaderDelegateClassSupplier.class;
	}


	@Override
	protected Class<? extends DeepConsulterSupplyFunction> getDeepConsulterSupplyFunctionClass() {
		return DeepConsulterSupplyFunction.class;
	}


	@Override
	protected Class<? extends GetLoadedClassesRetrieverFunction> getGetLoadedClassesRetrieverFunctionClass() {
		return GetLoadedClassesRetrieverFunction.class;
	}


	@Override
	protected Class<? extends GetLoadedPackagesFunction> getGetLoadedPackagesFunctionClass() {
		return GetLoadedPackagesFunction.class;
	}


	@Override
	protected Class<? extends ConvertToBuiltinClassLoaderFunction> getConvertToBuiltinClassLoaderFunctionClass() {
		return ConvertToBuiltinClassLoaderFunction.class;
	}

	
}
