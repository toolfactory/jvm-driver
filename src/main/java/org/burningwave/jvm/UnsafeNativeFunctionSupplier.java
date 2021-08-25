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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.burningwave.jvm.Driver.InitializationException;

import sun.misc.Unsafe;

@SuppressWarnings({"all"})
abstract class UnsafeNativeFunctionSupplier implements NativeFunctionSupplier {
	sun.misc.Unsafe unsafe;
	Driver driver;
	
	public UnsafeNativeFunctionSupplier(Driver driver) {
		try {
			this.driver = driver;
			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			this.unsafe = (Unsafe)theUnsafeField.get(null);
		} catch (Throwable exc) {
			Throwables.throwException(new InitializationException("Exception while retrieving unsafe", exc));
		}
	}
	
	@Override
	public BiFunction<Class<?>, byte[], Class<?>> getDefineHookClassFunction(Function<Class<?>, MethodHandles.Lookup>  consulterSupplier) {
		try {
			MethodHandle defineHookClassMethodHandle = consulterSupplier.apply(unsafe.getClass()).findSpecial(
				unsafe.getClass(),
				"defineAnonymousClass",
				MethodType.methodType(Class.class, Class.class, byte[].class, Object[].class),
				unsafe.getClass()
			);
			return (clientClass, byteCode) -> {
				try {
					return (Class<?>) defineHookClassMethodHandle.invoke(unsafe, clientClass, byteCode, null);
				} catch (Throwable exc) {
					return Throwables.throwException(exc);
				}
			};
		} catch (Throwable exc) {
			return Throwables.throwException(exc);
		}
		
	}
	
	@Override
	public Function<ClassLoader, Collection<Class<?>>> getRetrieveLoadedClassesFunction() {
		sun.misc.Unsafe unsafe = this.unsafe;
		try {
			Long loadedClassesVectorMemoryOffset = unsafe.objectFieldOffset(
				this.driver.getDeclaredField(ClassLoader.class, "classes")
			);
			return classLoader -> 
				(Collection<Class<?>>)unsafe.getObject(classLoader, loadedClassesVectorMemoryOffset);
		} catch (Throwable exc) {
			return Throwables.throwException(new InitializationException("Could not initialize field memory offset of packages map", exc));
		}
	}
	
	@Override
	public Function<ClassLoader, Map<String, ?>> getRetrieveLoadedPackagesFunction() {
		sun.misc.Unsafe unsafe = this.unsafe;
		try {
			Long loadedPackagesMapMemoryOffset = unsafe.objectFieldOffset(
				this.driver.getDeclaredField(ClassLoader.class, "packages")
			);
			return classLoader -> 
				(Map<String, ?>)unsafe.getObject(classLoader, loadedPackagesMapMemoryOffset);
		} catch (Throwable exc) {
			return Throwables.throwException(new InitializationException("Could not initialize field memory offset of loaded classes vector", exc));
		}
	}
	
	@Override
	public BiFunction<Object, Field, Object> getFieldValueFunction() {
		sun.misc.Unsafe unsafe = this.unsafe;
		return (target, field) -> {
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
		};
	}
	
	@Override
	public Function<Object, BiConsumer<Field, Object>> getSetFieldValueFunction() {
		sun.misc.Unsafe unsafe = this.unsafe;
		return origTarget -> (field, value) -> {
			if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
				Throwables.throwException("Value {} is not assignable to {}", value , field.getName());
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
		};
	}


	@Override
	public Function<Class<?>, Object> getAllocateInstanceFunction() {
		sun.misc.Unsafe unsafe = this.unsafe;
		return cls -> {
			try {
				return unsafe.allocateInstance(cls);
			} catch (InstantiationException exc) {
				return Throwables.throwException(exc);
			}
		};
	}
	
	@Override
	public Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
		return MethodHandles::lookup;
	}
	
	@Override
	public void close() {
		unsafe = null;
		this.driver = null;
	}
	
	static class ForJava8 extends UnsafeNativeFunctionSupplier {
		
		public ForJava8(Driver driver) {
			super(driver);
		}

		@Override
		public Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
			try {
				Field modes = MethodHandles.Lookup.class.getDeclaredField("allowedModes");
				modes.setAccessible(true);
				MethodHandles.Lookup mainConsulter = MethodHandles.lookup();
				modes.setInt(mainConsulter, -1);
				return () -> mainConsulter;
			} catch (Throwable exc) {
				return Throwables.throwException(exc);
			}
		}
		
	}
	
	static class ForJava9 extends UnsafeNativeFunctionSupplier {
		
		public ForJava9(Driver driver) {
			super(driver);
		}
		
		@Override
		public Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
			sun.misc.Unsafe unsafe = this.unsafe;
			return () -> {
				MethodHandles.Lookup consulter = MethodHandles.lookup();
				unsafe.putInt(consulter, 12L, -1);
				return consulter;
			};
		}
		
	}

}
