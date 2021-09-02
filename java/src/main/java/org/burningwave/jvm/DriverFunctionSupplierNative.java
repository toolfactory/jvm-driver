/*
 * This file is part of Burningwave JVM driver.
 *
 * Author: Roberto Gentili
 *
 * Hosted at: https://github.com/burningwave/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2021 Roberto Gentili
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
package org.burningwave.jvm;

import java.lang.invoke.MethodHandles;
import java.util.function.Supplier;

class DriverFunctionSupplierNative {
	
	static {
		Libraries.getInstance().loadFor(DriverFunctionSupplierNative.class);
	}
	
	static DriverFunctionSupplierNative create() {
		return new DriverFunctionSupplierNative();
	}
	
	static DriverFunctionSupplierNative getInstance() {
		return Holder.getWithinInstance();
	}
	
	Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
		return () -> {
			MethodHandles.Lookup consulter = MethodHandles.lookup();
			setAllowedModes(consulter, -1);
			return consulter;
		};
	}
	
	static native void setAllowedModes(MethodHandles.Lookup consulter, int modes);
	
	private static class Holder {
		private static final DriverFunctionSupplierNative INSTANCE = DriverFunctionSupplierNative.create();
		
		private static DriverFunctionSupplierNative getWithinInstance() {
			return INSTANCE;
		}
	}

}
