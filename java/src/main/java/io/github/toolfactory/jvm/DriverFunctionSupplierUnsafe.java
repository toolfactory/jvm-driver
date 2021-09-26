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


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

import io.github.toolfactory.jvm.Driver.InitializationException;
import sun.misc.Unsafe;



@SuppressWarnings("all")
abstract class DriverFunctionSupplierUnsafe extends DriverFunctionSupplier {

	sun.misc.Unsafe unsafe;
	Driver driver;
	JVMInfo jVMInfo;

	DriverFunctionSupplierUnsafe(Driver driver) {
		try {
			this.driver = driver;
			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			this.unsafe = (Unsafe)theUnsafeField.get(null);
			jVMInfo = JVMInfo.getInstance();
		} catch (Throwable exc) {
			Throwables.getInstance().throwException(new InitializationException("Exception while retrieving unsafe", exc));
		}
	}


	abstract MethodHandles.Lookup retrieveConsulter(MethodHandles.Lookup consulter, MethodHandle privateLookupInMethodHandle) throws Throwable;

	@Override
	BiFunction<Class<?>, byte[], Class<?>> getDefineHookClassFunction(MethodHandles.Lookup consulter, MethodHandle privateLookupInMethodHandle) {
		try {
			final MethodHandle defineHookClassMethodHandle = retrieveConsulter(consulter, privateLookupInMethodHandle).findSpecial(
				unsafe.getClass(),
				"defineAnonymousClass",
				MethodType.methodType(Class.class, Class.class, byte[].class, Object[].class),
				unsafe.getClass()
			);
			final sun.misc.Unsafe unsafe = this.unsafe;
			return new BiFunction<Class<?>, byte[], Class<?>> () {
				@Override
				public Class<?> apply(Class<?> clientClass, byte[] byteCode) {
					try {
						return (Class<?>) defineHookClassMethodHandle.invoke(unsafe, clientClass, byteCode, null);
					} catch (Throwable exc) {
						return Throwables.getInstance().throwException(exc);
					}
				}
				
			};
		} catch (Throwable exc) {
			return Throwables.getInstance().throwException(exc);
		}
	}

	@Override
	Function<ClassLoader, Collection<Class<?>>> getRetrieveLoadedClassesFunction() {
		try {
			final sun.misc.Unsafe unsafe = this.unsafe;
			final Long loadedClassesVectorMemoryOffset = unsafe.objectFieldOffset(
				this.driver.getDeclaredField(ClassLoader.class, "classes")
			);
			return new Function<ClassLoader, Collection<Class<?>>>() {
				public java.util.Collection<java.lang.Class<?>> apply(ClassLoader classLoader) {
					return (Collection<Class<?>>)unsafe.getObject(classLoader, loadedClassesVectorMemoryOffset);
				}
			};				
		} catch (Throwable exc) {
			return Throwables.getInstance().throwException(new InitializationException("Could not initialize field memory offset of packages map", exc));
		}
	}

	@Override
	Function<ClassLoader, Map<String, ?>> getRetrieveLoadedPackagesFunction() {
		try {
			final sun.misc.Unsafe unsafe = this.unsafe;
			final Long loadedPackagesMapMemoryOffset = unsafe.objectFieldOffset(
				this.driver.getDeclaredField(ClassLoader.class, "packages")
			);
			return new Function<ClassLoader, Map<String, ?>>() {
				public java.util.Map<String,?> apply(ClassLoader classLoader) {
					return (Map<String, ?>)unsafe.getObject(classLoader, loadedPackagesMapMemoryOffset);
				}
			};				
		} catch (Throwable exc) {
			return Throwables.getInstance().throwException(new InitializationException("Could not initialize field memory offset of loaded classes vector", exc));
		}
	}

	@Override
	BiFunction<Object, Field, Object> getFieldValueFunction() {
		final sun.misc.Unsafe unsafe = this.unsafe;
		return new BiFunction<Object, Field, Object>() {
			@Override
			public Object apply(Object target, Field field) {
				target = Modifier.isStatic(field.getModifiers())?
					field.getDeclaringClass() :
					target;
				long fieldOffset = Modifier.isStatic(field.getModifiers())?
					unsafe.staticFieldOffset(field) :
					unsafe.objectFieldOffset(field);
				Class<?> cls = field.getType();
				if(!cls.isPrimitive()) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return unsafe.getObject(target, fieldOffset);
					} else {
						return unsafe.getObjectVolatile(target, fieldOffset);
					}
				} else if (cls == int.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Integer.valueOf(unsafe.getInt(target, fieldOffset));
					} else {
						return Integer.valueOf(unsafe.getIntVolatile(target, fieldOffset));
					}
				} else if (cls == long.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Long.valueOf(unsafe.getLong(target, fieldOffset));
					} else {
						return Long.valueOf(unsafe.getLongVolatile(target, fieldOffset));
					}
				} else if (cls == float.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Float.valueOf(unsafe.getFloat(target, fieldOffset));
					} else {
						return Float.valueOf(unsafe.getFloatVolatile(target, fieldOffset));
					}
				} else if (cls == double.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Double.valueOf(unsafe.getDouble(target, fieldOffset));
					} else {
						return Double.valueOf(unsafe.getDoubleVolatile(target, fieldOffset));
					}
				} else if (cls == boolean.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Boolean.valueOf(unsafe.getBoolean(target, fieldOffset));
					} else {
						return Boolean.valueOf(unsafe.getBooleanVolatile(target, fieldOffset));
					}
				} else if (cls == byte.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Byte.valueOf(unsafe.getByte(target, fieldOffset));
					} else {
						return Byte.valueOf(unsafe.getByteVolatile(target, fieldOffset));
					}
				} else {
					if (!Modifier.isVolatile(field.getModifiers())) {
						return Character.valueOf(unsafe.getChar(target, fieldOffset));
					} else {
						return Character.valueOf(unsafe.getCharVolatile(target, fieldOffset));
					}
				}
			}
		};
	}

	@Override
	TriConsumer<Object, Field, Object> getSetFieldValueFunction() {
		final sun.misc.Unsafe unsafe = this.unsafe;
		return new TriConsumer<Object, Field, Object>() {
			@Override
			public void accept(Object origTarget, Field field, Object value) {
				if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
					Throwables.getInstance().throwException("Value {} is not assignable to {}", value , field.getName());
				}
				Object target = Modifier.isStatic(field.getModifiers())?
					field.getDeclaringClass() :
					origTarget;
				long fieldOffset = Modifier.isStatic(field.getModifiers())?
					unsafe.staticFieldOffset(field) :
					unsafe.objectFieldOffset(field);
				Class<?> cls = field.getType();
				if(!cls.isPrimitive()) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						unsafe.putObject(target, fieldOffset, value);
					} else {
						unsafe.putObjectVolatile(target, fieldOffset, value);
					}
				} else if (cls == int.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						unsafe.putInt(target, fieldOffset, ((Integer)value).intValue());
					} else {
						unsafe.putIntVolatile(target, fieldOffset, ((Integer)value).intValue());
					}
				} else if (cls == long.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						unsafe.putLong(target, fieldOffset, ((Long)value).longValue());
					} else {
						unsafe.putLongVolatile(target, fieldOffset, ((Long)value).longValue());
					}
				} else if (cls == float.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						unsafe.putFloat(target, fieldOffset, ((Float)value).floatValue());
					} else {
						unsafe.putFloatVolatile(target, fieldOffset, ((Float)value).floatValue());
					}
				} else if (cls == double.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						unsafe.putDouble(target, fieldOffset, ((Double)value).doubleValue());
					} else {
						unsafe.putDoubleVolatile(target, fieldOffset, ((Double)value).doubleValue());
					}
				} else if (cls == boolean.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						unsafe.putBoolean(target, fieldOffset, ((Boolean)value).booleanValue());
					} else {
						unsafe.putBooleanVolatile(target, fieldOffset, ((Boolean)value).booleanValue());
					}
				} else if (cls == byte.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						unsafe.putByte(target, fieldOffset, ((Byte)value).byteValue());
					} else {
						unsafe.putByteVolatile(target, fieldOffset, ((Byte)value).byteValue());
					}
				} else if (cls == char.class) {
					if (!Modifier.isVolatile(field.getModifiers())) {
						unsafe.putChar(target, fieldOffset, ((Character)value).charValue());
					} else {
						unsafe.putCharVolatile(target, fieldOffset, ((Character)value).charValue());
					}
				}	
				
			}
		};
	}

	@Override
	Function<Class<?>, Object> getAllocateInstanceFunction() {
		final sun.misc.Unsafe unsafe = this.unsafe;
		return new Function<Class<?>, Object>() {
			@Override
			public Object apply(Class<?> cls) {
				try {
					return unsafe.allocateInstance(cls);
				} catch (InstantiationException exc) {
					return Throwables.getInstance().throwException(exc);
				}
			}
		};
	}
	

	@Override
	public void close() {
		unsafe = null;
		this.driver = null;
	}


	static class ForJava7 extends DriverFunctionSupplierUnsafe {

		ForJava7(Driver driver) {
			super(driver);
		}

		@Override
		MethodHandles.Lookup retrieveConsulter(MethodHandles.Lookup consulter, MethodHandle privateLookupInMethodHandle)
				throws Throwable {
			return (MethodHandles.Lookup) privateLookupInMethodHandle.invoke(consulter, unsafe.getClass());
		}

		@Override
		Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
			return new Supplier<MethodHandles.Lookup>() {
				@Override
				public MethodHandles.Lookup get() {
					return MethodHandles.lookup();
				}
			};
		}

	}


	static class ForJava9 extends DriverFunctionSupplierUnsafe {

		ForJava9(Driver driver) {
			super(driver);
		}

		@Override
		MethodHandles.Lookup retrieveConsulter(MethodHandles.Lookup consulter, MethodHandle lookupMethod)
				throws Throwable {
			return (MethodHandles.Lookup)lookupMethod.invoke(unsafe.getClass(), consulter);
		}
		
		@Override
		Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
			try {
				MethodHandles.Lookup lookup = MethodHandles.lookup();
				CallSite callSite = java.lang.invoke.LambdaMetafactory.metafactory(
				   lookup, "get", MethodType.methodType(Supplier.class), 
				   MethodType.methodType(Object.class),
				   lookup.findStatic(MethodHandles.class,"lookup", MethodType.methodType(MethodHandles.Lookup.class)),
				   MethodType.methodType(MethodHandles.Lookup.class)
				);
				return (Supplier<MethodHandles.Lookup>)callSite.getTarget().invoke();
			} catch (Throwable exc) {
				return Throwables.getInstance().throwException(exc);
			}
		}

	}


	static class ForJava17 extends ForJava9 {

		ForJava17(Driver driver) {
			super(driver);
		}

		@Override
		BiFunction<Class<?>, byte[], Class<?>> getDefineHookClassFunction(
			final MethodHandles.Lookup consulter,
			final MethodHandle privateLookupInMethodHandle
		) {
			try {
				final MethodHandle defineClassMethodHandle = consulter.findSpecial(
					MethodHandles.Lookup.class,
					"defineClass",
					MethodType.methodType(Class.class, byte[].class),
					MethodHandles.Lookup.class
				);
				return new BiFunction<Class<?>, byte[], Class<?>>() {
					@Override
					public Class<?> apply(Class<?> clientClass, byte[] byteCode) {
						try {
							MethodHandles.Lookup lookup = (MethodHandles.Lookup)privateLookupInMethodHandle.invoke(clientClass, consulter);
							try {
								return (Class<?>) defineClassMethodHandle.invoke(lookup, byteCode);
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
				};
			} catch (Throwable exc) {
				return Throwables.getInstance().throwException(exc);
			}
		}

		@Override
		Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
			final sun.misc.Unsafe unsafe = this.unsafe;
			final long allowedModesFieldMemoryOffset = jVMInfo.is64Bit() ? 12L : 8L;
			return new Supplier<MethodHandles.Lookup>() {
				@Override
				public MethodHandles.Lookup get() {
					MethodHandles.Lookup consulter = MethodHandles.lookup();
					unsafe.putInt(consulter, allowedModesFieldMemoryOffset, -1);
					return consulter;
				}		

			};
		}

	}


}
