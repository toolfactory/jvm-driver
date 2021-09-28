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


@SuppressWarnings("unchecked")
public class NativeDriver extends HybridDriver {
	
	
	void initLoadedPackagesRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		loadedPackagesRetriever = functionProvider.getFunctionAdapter(
			_GetLoadedPackagesFunction.Native.class, initializationContext
		);
	}

	
	@Override
	void initLoadedClassesRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		loadedClassesRetriever = functionProvider.getFunctionAdapter(
			_GetLoadedClassesFunction.Native.class, initializationContext
		);
	}

	
	@Override
	void initFieldValueSetter(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		fieldValueSetter = functionProvider.getFunctionAdapter(
			_SetFieldValueFunction.Native.class, initializationContext
		);
	}

	
	@Override
	void initFieldValueRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		fieldValueRetriever = functionProvider.getFunctionAdapter(
			_GetFieldValueFunction.Native.class, initializationContext
		);
	}

	
	@Override		
	void initAllocateInstanceInvoker(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		allocateInstanceInvoker = functionProvider.getFunctionAdapter(
			_AllocateInstanceFunction.Native.class, initializationContext
		);
	}
	
	
	@Override
	void initAccessibleSetter(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		//this cast is necessary to avoid the incompatible types error (no unique maximal instance exists for type variable)
		accessibleSetter = (BiConsumerAdapter<?, AccessibleObject, Boolean>)functionProvider.getFunctionAdapter(
			_SetAccessibleFunction.Native.class, initializationContext
		);
	}

}
