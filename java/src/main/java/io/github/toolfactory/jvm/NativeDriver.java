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


import java.util.Map;

import io.github.toolfactory.jvm.function.catalog.AllocateInstanceFunction;
import io.github.toolfactory.jvm.function.catalog.ConsulterSupplier;
import io.github.toolfactory.jvm.function.catalog.DefineHookClassFunction;
import io.github.toolfactory.jvm.function.catalog.GetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedClassesFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedPackagesFunction;
import io.github.toolfactory.jvm.function.catalog.SetAccessibleFunction;
import io.github.toolfactory.jvm.function.catalog.SetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.ThrowExceptionFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;


//@SuppressWarnings("unchecked")
public class NativeDriver extends DefaultDriver {
	
	
	@Override
	protected DefineHookClassFunction initHookClassDefiner(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		functionProvider.getOrBuildObject(ConsulterSupplier.Native.class, initializationContext);
		return super.initHookClassDefiner(functionProvider, initializationContext);
	}
	
	
	@Override
	protected ThrowExceptionFunction initExceptionThrower(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		return functionProvider.getOrBuildObject(
			ThrowExceptionFunction.Native.class, initializationContext
		);
	}
	
	
	@Override
	protected GetLoadedPackagesFunction initLoadedPackagesRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		return functionProvider.getOrBuildObject(
			GetLoadedPackagesFunction.Native.class, initializationContext
		);
	}

	
	@Override
	protected GetLoadedClassesFunction initLoadedClassesRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		return functionProvider.getOrBuildObject(
			GetLoadedClassesFunction.Native.class, initializationContext
		);
	}

	
	@Override
	protected SetFieldValueFunction initFieldValueSetter(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {	
        if (!io.github.toolfactory.narcissus.Narcissus.libraryLoaded) {
            throw new InitializeException("Could not load Narcissus native library");
        }
		return functionProvider.getOrBuildObject(
			SetFieldValueFunction.Native.class, initializationContext
		);
	}

	
	@Override
	protected GetFieldValueFunction initFieldValueRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		return functionProvider.getOrBuildObject(
			GetFieldValueFunction.Native.class, initializationContext
		);
	}

	
	@Override		
	protected AllocateInstanceFunction initAllocateInstanceInvoker(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		return functionProvider.getOrBuildObject(
			AllocateInstanceFunction.Native.class, initializationContext
		);
	}
	
	
	@Override
	protected SetAccessibleFunction<?> initAccessibleSetter(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		return functionProvider.getOrBuildObject(
			SetAccessibleFunction.Native.class, initializationContext
		);
	}

}
