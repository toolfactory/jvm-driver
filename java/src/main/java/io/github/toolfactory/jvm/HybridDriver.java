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


import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;


public class HybridDriver extends DefaultDriver {

	@Override
	Initializer newInitializerForJava17() {
		return new ForJava17(this);
	}

	static class ForJava17 extends DefaultDriver.Initializer.ForJava17 {
		private DriverFunctionSupplierNative driverFunctionSupplierNative;

		ForJava17(DefaultDriver driver) {
			super(driver);
		}

		@Override
		void initNativeFunctionSupplier() {
			final DriverFunctionSupplierNative driverFunctionSupplierNative = this.driverFunctionSupplierNative = new DriverFunctionSupplierNative();
			this.driverFunctionSupplier = new DriverFunctionSupplierUnsafe.ForJava17(this.driver) {

				@Override
				java.util.function.Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
					return driverFunctionSupplierNative.getMethodHandlesLookupSupplyingFunction();
				}

				@Override
				java.util.function.BiFunction<Object, Field, Object> getFieldValueFunction() {
					return driverFunctionSupplierNative.getFieldValueFunction();
				}

				@Override
				java.util.function.Function<Object, java.util.function.BiConsumer<Field, Object>> getSetFieldValueFunction() {
					return driverFunctionSupplierNative.getSetFieldValueFunction();
				}

				@Override
				java.util.function.Function<Class<?>, Object> getAllocateInstanceFunction() {
					return driverFunctionSupplierNative.getAllocateInstanceFunction();
				}

			};
		}

		@Override
		void initAccessibleSetter() {
			driver.accessibleSetter = driverFunctionSupplierNative.getSetAccessibleFunction();
		}

		@Override
		public void close() {
			driverFunctionSupplierNative = null;
			super.close();
		}

	}

}
