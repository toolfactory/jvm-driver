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
package io.github.toolfactory.jvm;


import java.util.Map;

import io.github.toolfactory.jvm.function.catalog.ConsulterSupplier;
import io.github.toolfactory.jvm.function.catalog.ConsulterSupplyFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.ObjectProvider.BuildingException;


@SuppressWarnings("unchecked")
public class HybridDriver extends DefaultDriver {


	@Override
	protected Map<Object, Object> functionsToMap() {
		Map<Object, Object> context = super.functionsToMap();
		ObjectProvider.get(context).markToBeInitializedViaExceptionHandler(ConsulterSupplier.class, context);
		ObjectProvider.get(context).markToBeInitializedViaExceptionHandler(ConsulterSupplyFunction.class, context);
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
						}
						throw exception;
					}
				}
			);
		return context;
	}


	protected Class<? extends ConsulterSupplier> getConsulterSupplierFunctionClass() {
		return ConsulterSupplier.Hybrid.class;
	}


	@Override
	protected Class<? extends ConsulterSupplyFunction> getConsulterSupplyFunctionClass() {
		return ConsulterSupplyFunction.Hybrid.class;
	}

}
