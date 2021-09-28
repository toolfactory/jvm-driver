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
package io.github.toolfactory.jvm.function;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.Supplier;


public abstract class _PrivateLookupInMethodHandleSupplier implements Supplier<MethodHandle> {
	MethodHandle methodHandle;
	
	public static class ForJava7 extends _PrivateLookupInMethodHandleSupplier {
		
		public ForJava7(Map<Object, Object> context) throws NoSuchMethodException, IllegalAccessException {
			MethodHandles.Lookup consulter = Provider.get(context).getFunctionAdapter(_ConsulterSupplier.class, context).get();
			methodHandle = consulter.findSpecial(
				MethodHandles.Lookup.class, "in",
				MethodType.methodType(MethodHandles.Lookup.class, Class.class),
				MethodHandles.Lookup.class
			);
		}
		
		@Override
		public MethodHandle get() {
			return methodHandle;
		}
		
	}
	
	
	public static class ForJava9 extends _PrivateLookupInMethodHandleSupplier {
		
		public ForJava9(Map<Object, Object> context) throws NoSuchMethodException, IllegalAccessException {
			MethodHandles.Lookup consulter = Provider.get(context).getFunctionAdapter(_ConsulterSupplier.class, context).get();
			methodHandle = consulter.findStatic(
				MethodHandles.class, "privateLookupIn",
				MethodType.methodType(MethodHandles.Lookup.class, Class.class, MethodHandles.Lookup.class)
			);
		}
		
		@Override
		public MethodHandle get() {
			return methodHandle;
		}
		
		
	}
	
}
