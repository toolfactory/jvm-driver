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


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import java.util.Map;


@SuppressWarnings("restriction")
abstract class _DefineHookClassFunction implements BiFunction<Class<?>, byte[], Class<?>> {
	MethodHandle defineHookClassMethodHandle;

	
	static class ForJava7 extends _DefineHookClassFunction {
		sun.misc.Unsafe unsafe;
		
		ForJava7(Map<Object, Object> context) throws NoSuchMethodException, IllegalAccessException, Throwable {
			FunctionProvider functionProvider = FunctionProvider.get(context);
			unsafe = functionProvider.getFunctionAdapter(_UnsafeSupplier.class, context).get();
			defineHookClassMethodHandle = retrieveConsulter(
				functionProvider.getFunctionAdapter(_ConsulterSupplier.class, context).get(),
				functionProvider.getFunctionAdapter(_PrivateLookupInMethodHandleSupplier.class, context).get()
			).findSpecial(
				unsafe.getClass(),
				"defineAnonymousClass",
				MethodType.methodType(Class.class, Class.class, byte[].class, Object[].class),
				unsafe.getClass()
			);
		}
		
		MethodHandles.Lookup retrieveConsulter(MethodHandles.Lookup consulter, MethodHandle privateLookupInMethodHandle) throws Throwable {
			return (MethodHandles.Lookup)privateLookupInMethodHandle.invoke(consulter, unsafe.getClass());
		}
		
		@Override
		public Class<?> apply(Class<?> clientClass, byte[] byteCode) {
			try {
				return (Class<?>) defineHookClassMethodHandle.invoke(unsafe, clientClass, byteCode, null);
			} catch (Throwable exc) {
				return Throwables.getInstance().throwException(exc);
			}
		}
		
	}
	
	static class ForJava9 extends ForJava7 {
		
		ForJava9(Map<Object, Object> context) throws NoSuchMethodException, IllegalAccessException, Throwable {
			super(context);
		}
		
		@Override
		MethodHandles.Lookup retrieveConsulter(MethodHandles.Lookup consulter, MethodHandle lookupMethod) throws Throwable {
			return (MethodHandles.Lookup)lookupMethod.invoke(unsafe.getClass(), consulter);
		}
		
	}
	
	static class ForJava17 extends _DefineHookClassFunction {
		private MethodHandle privateLookupInMethodHandle;
		private MethodHandles.Lookup consulter;
		
		ForJava17(Map<Object, Object> context) throws NoSuchMethodException, IllegalAccessException {
			FunctionProvider functionProvider = FunctionProvider.get(context);
			consulter = functionProvider.getFunctionAdapter(_ConsulterSupplier.class, context).get();
			defineHookClassMethodHandle = consulter.findSpecial(
				MethodHandles.Lookup.class,
				"defineClass",
				MethodType.methodType(Class.class, byte[].class),
				MethodHandles.Lookup.class
			);
			privateLookupInMethodHandle = functionProvider.getFunctionAdapter(_PrivateLookupInMethodHandleSupplier.class, context).get();
		}

		
		@Override
		public Class<?> apply(Class<?> clientClass, byte[] byteCode) {
			try {
				MethodHandles.Lookup lookup = (MethodHandles.Lookup)privateLookupInMethodHandle.invoke(clientClass, consulter);
				try {
					return (Class<?>) defineHookClassMethodHandle.invoke(lookup, byteCode);
				} catch (LinkageError exc) {
					return JavaClass.extractByUsing(ByteBuffer.wrap(byteCode), new Function<JavaClass, Class<?>>() {
						public Class<?> apply(JavaClass javaClass) {
							try {
								return Class.forName(javaClass.getName());
							} catch (Throwable inExc) {
								return Throwables.getInstance().throwException(inExc);
							}
						};
					});
				}
			} catch (Throwable exc) {
				return Throwables.getInstance().throwException(exc);
			}
		}
		
	}
	
}
