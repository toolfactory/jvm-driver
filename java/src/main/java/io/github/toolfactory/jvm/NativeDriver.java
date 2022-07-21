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
package io.github.toolfactory.jvm;


import java.util.Map;

import io.github.toolfactory.jvm.function.catalog.AllocateInstanceFunction;
import io.github.toolfactory.jvm.function.catalog.ConsulterSupplier;
import io.github.toolfactory.jvm.function.catalog.ConsulterSupplyFunction;
import io.github.toolfactory.jvm.function.catalog.GetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedClassesRetrieverFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedPackagesFunction;
import io.github.toolfactory.jvm.function.catalog.SetAccessibleFunction;
import io.github.toolfactory.jvm.function.catalog.SetFieldValueFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.ObjectProvider.BuildingException;


@SuppressWarnings({"unchecked"})
public class NativeDriver extends DefaultDriver {


	@Override
	protected Map<Object, Object> functionsToMap() {
		Map<Object, Object> context = super.functionsToMap();
		ObjectProvider objectProvider = ObjectProvider.get(context);
		objectProvider.markToBeInitializedViaExceptionHandler(ConsulterSupplier.class, context);
		ObjectProvider.get(context).markToBeInitializedViaExceptionHandler(ConsulterSupplyFunction.class, context);
		objectProvider.markToBeInitializedViaExceptionHandler(SetFieldValueFunction.class, context);
		objectProvider.markToBeInitializedViaExceptionHandler(io.github.toolfactory.jvm.function.catalog.AllocateInstanceFunction.class, context);
		objectProvider.markToBeInitializedViaExceptionHandler(GetFieldValueFunction.class, context);
		objectProvider.markToBeInitializedViaExceptionHandler(SetAccessibleFunction.class, context);
		objectProvider.markToBeInitializedViaExceptionHandler(GetLoadedPackagesFunction.class, context);
		objectProvider.markToBeInitializedViaExceptionHandler(GetLoadedClassesRetrieverFunction.class, context);
		ObjectProvider.setExceptionHandler(
				context,
				new ObjectProvider.ExceptionHandler() {
					@Override
					public <T> T handle(ObjectProvider objectProvider, Class<? super T> clazz, Map<Object, Object> context,
						BuildingException exception) {
						if (objectProvider.isMarkedToBeInitializedViaExceptionHandler(exception)) {
							if (clazz.isAssignableFrom(getConsulterSupplierFunctionClass())) {
								return (T)objectProvider.getOrBuildObject(getConsulterSupplierFunctionClass(), context);
							}
							if (clazz.isAssignableFrom(getConsulterSupplyFunctionClass())) {
								return (T)objectProvider.getOrBuildObject(getConsulterSupplyFunctionClass(), context);
							}
							if (clazz.isAssignableFrom(getThrowExceptionFunctionClass())) {
								return (T)objectProvider.getOrBuildObject(getThrowExceptionFunctionClass(), context);
							}
							if (clazz.isAssignableFrom(getSetFieldValueFunctionClass())) {
								return (T)objectProvider.getOrBuildObject(getSetFieldValueFunctionClass(), context);
							}
							if (clazz.isAssignableFrom(getAllocateInstanceFunctionClass())) {
								return (T)objectProvider.getOrBuildObject(getAllocateInstanceFunctionClass(), context);
							}
							if (clazz.isAssignableFrom(getSetAccessibleFunctionClass())) {
								return (T)objectProvider.getOrBuildObject(getSetAccessibleFunctionClass(), context);
							}
							if (clazz.isAssignableFrom(getGetFieldValueFunctionClass())) {
								return (T)objectProvider.getOrBuildObject(getGetFieldValueFunctionClass(), context);
							}
							if (clazz.isAssignableFrom(getGetLoadedClassesRetrieverFunctionClass())) {
								return (T)objectProvider.getOrBuildObject(getGetLoadedClassesRetrieverFunctionClass(), context);
							}
							if (clazz.isAssignableFrom(getGetLoadedPackagesFunctionClass())) {
								return (T)objectProvider.getOrBuildObject(getGetLoadedPackagesFunctionClass(), context);
							}
						}
						throw exception;
					}
				}
			);
		return context;
	}


	protected Class<? extends ConsulterSupplier> getConsulterSupplierFunctionClass() {
		return ConsulterSupplier.Native.class;
	}


	@Override
	protected Class<? extends ConsulterSupplyFunction> getConsulterSupplyFunctionClass() {
		return ConsulterSupplyFunction.Native.class;
	}


	@Override
	protected Class<? extends GetLoadedPackagesFunction> getGetLoadedPackagesFunctionClass() {
		return GetLoadedPackagesFunction.Native.class;
	}


	@Override
	protected Class<? extends GetLoadedClassesRetrieverFunction> getGetLoadedClassesRetrieverFunctionClass() {
		return GetLoadedClassesRetrieverFunction.Native.class;
	}


	@Override
	protected Class<? extends SetFieldValueFunction> getSetFieldValueFunctionClass() {
		return SetFieldValueFunction.Native.class;
	}


	@Override
	protected Class<? extends GetFieldValueFunction> getGetFieldValueFunctionClass() {
		return GetFieldValueFunction.Native.class;
	}


	@Override
	protected Class<? extends AllocateInstanceFunction> getAllocateInstanceFunctionClass() {
		return AllocateInstanceFunction.Native.class;
	}


	@Override
	protected Class<? extends SetAccessibleFunction> getSetAccessibleFunctionClass() {
		return SetAccessibleFunction.Native.class;
	}

}
