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
import io.github.toolfactory.jvm.function.catalog.UnsafeSupplier;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.ObjectProvider.BuildingException;


public class DynamicDriver extends DefaultDriver {

	@Override
	protected Map<Object, Object> functionsToMap() {
		Map<Object, Object> context = super.functionsToMap();
		if (!isUnsafeMemoryAccessAvailable(context)) {
			//Some Java based functions call the memory-access methods of sun.misc.Unsafe only when they are used, so their
			//initialization does not fail when those methods are disabled (--sun-misc-unsafe-memory-access=deny) or
			//removed (JEP 471): in that case the functions that use them are initialized via the exception handler, which
			//returns their native version, as the native driver does
			ObjectProvider objectProvider = ObjectProvider.get(context);
			objectProvider.markToBeInitializedViaExceptionHandler(ConsulterSupplier.class, context);
			objectProvider.markToBeInitializedViaExceptionHandler(ConsulterSupplyFunction.class, context);
			objectProvider.markToBeInitializedViaExceptionHandler(SetFieldValueFunction.class, context);
			objectProvider.markToBeInitializedViaExceptionHandler(AllocateInstanceFunction.class, context);
			objectProvider.markToBeInitializedViaExceptionHandler(GetFieldValueFunction.class, context);
			objectProvider.markToBeInitializedViaExceptionHandler(SetAccessibleFunction.class, context);
			objectProvider.markToBeInitializedViaExceptionHandler(GetLoadedPackagesFunction.class, context);
			objectProvider.markToBeInitializedViaExceptionHandler(GetLoadedClassesRetrieverFunction.class, context);
		}
		ObjectProvider.setExceptionHandler(
			context,
			new ObjectProvider.ExceptionHandler() {
				@Override
				public <T> T handle(ObjectProvider objectProvider, Class<? super T> clazz, Map<Object, Object> context,
						BuildingException exc) {
					if (clazz.equals(ConsulterSupplier.class)) {
						return objectProvider.getOrBuildObject(ConsulterSupplier.Native.class, context);
					}
					if (clazz.equals(ConsulterSupplyFunction.class)) {
						return objectProvider.getOrBuildObject(ConsulterSupplyFunction.Native.class, context);
					}
					if (clazz.equals(SetFieldValueFunction.class)) {
						return objectProvider.getOrBuildObject(SetFieldValueFunction.Native.class, context);
					}
					if (clazz.equals(AllocateInstanceFunction.class)) {
						return objectProvider.getOrBuildObject(AllocateInstanceFunction.Native.class, context);
					}
					if (clazz.equals(SetAccessibleFunction.class)) {
						return objectProvider.getOrBuildObject(SetAccessibleFunction.Native.class, context);
					}
					if (clazz.equals(GetFieldValueFunction.class)) {
						return objectProvider.getOrBuildObject(GetFieldValueFunction.Native.class, context);
					}
					if (clazz.equals(GetLoadedClassesRetrieverFunction.class)) {
						return objectProvider.getOrBuildObject(GetLoadedClassesRetrieverFunction.Native.class, context);
					}
					if (clazz.equals(GetLoadedPackagesFunction.class)) {
						return objectProvider.getOrBuildObject(GetLoadedPackagesFunction.Native.class, context);
					}
					throw exc;
				}
			}
		);
		return context;
	}

	protected boolean isUnsafeMemoryAccessAvailable(Map<Object, Object> context) {
		try {
			sun.misc.Unsafe unsafe = ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
			unsafe.objectFieldOffset(UnsafeMemoryAccessProbe.class.getDeclaredField("field"));
			return true;
		} catch (Throwable exc) {
			return false;
		}
	}


	private static class UnsafeMemoryAccessProbe {

		@SuppressWarnings("unused")
		private int field;

	}

}
