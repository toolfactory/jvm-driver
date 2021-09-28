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


import java.lang.reflect.AccessibleObject;
import java.util.Map;

import io.github.toolfactory.jvm.function.Provider;
import io.github.toolfactory.jvm.function.catalog.AllocateInstanceFunction;
import io.github.toolfactory.jvm.function.catalog.GetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedClassesFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedPackagesFunction;
import io.github.toolfactory.jvm.function.catalog.SetAccessibleFunction;
import io.github.toolfactory.jvm.function.catalog.SetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.ThrowExceptionFunction;
import io.github.toolfactory.jvm.function.util.BiConsumerAdapter;


@SuppressWarnings("unchecked")
public class NativeDriver extends HybridDriver {
	
	
	void initExceptionThrower(
		Provider functionProvider,
		Map<Object, Object> initializationContext
	) {
		exceptionThrower = functionProvider.getOrBuildFunction(
			ThrowExceptionFunction.Native.class, initializationContext
		);
	}
	
	
	void initLoadedPackagesRetriever(
		Provider functionProvider,
		Map<Object, Object> initializationContext
	) {
		loadedPackagesRetriever = functionProvider.getOrBuildFunction(
			GetLoadedPackagesFunction.Native.class, initializationContext
		);
	}

	
	@Override
	void initLoadedClassesRetriever(
		Provider functionProvider,
		Map<Object, Object> initializationContext
	) {
		loadedClassesRetriever = functionProvider.getOrBuildFunction(
			GetLoadedClassesFunction.Native.class, initializationContext
		);
	}

	
	@Override
	void initFieldValueSetter(
		Provider functionProvider,
		Map<Object, Object> initializationContext
	) {
		fieldValueSetter = functionProvider.getOrBuildFunction(
			SetFieldValueFunction.Native.class, initializationContext
		);
	}

	
	@Override
	void initFieldValueRetriever(
		Provider functionProvider,
		Map<Object, Object> initializationContext
	) {
		fieldValueRetriever = functionProvider.getOrBuildFunction(
			GetFieldValueFunction.Native.class, initializationContext
		);
	}

	
	@Override		
	void initAllocateInstanceInvoker(
		Provider functionProvider,
		Map<Object, Object> initializationContext
	) {
		allocateInstanceInvoker = functionProvider.getOrBuildFunction(
			AllocateInstanceFunction.Native.class, initializationContext
		);
	}
	
	
	@Override
	void initAccessibleSetter(
		Provider functionProvider,
		Map<Object, Object> initializationContext
	) {
		//this cast is necessary to avoid the incompatible types error (no unique maximal instance exists for type variable)
		accessibleSetter = (BiConsumerAdapter<?, AccessibleObject, Boolean>)functionProvider.getOrBuildFunction(
			SetAccessibleFunction.Native.class, initializationContext
		);
	}

}
