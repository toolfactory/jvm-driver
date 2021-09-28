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
package io.github.toolfactory.jvm.function.catalog;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import io.github.toolfactory.jvm.function.Provider;
import io.github.toolfactory.jvm.function.template.TriConsumer;
import io.github.toolfactory.jvm.function.util.Classes;


@SuppressWarnings({"restriction", "null"})
public abstract class SetFieldValueFunction implements TriConsumer<Object, Field, Object> {
	ThrowExceptionFunction throwExceptionFunction;
	
	public SetFieldValueFunction(Map<Object, Object> context) {
		Provider functionProvider = Provider.get(context);
		throwExceptionFunction =
			functionProvider.getOrBuildFunction(ThrowExceptionFunction.class, context); 
	}
	
	public static class ForJava7 extends SetFieldValueFunction {
		final sun.misc.Unsafe unsafe;
		
		public ForJava7(Map<Object, Object> context) {
			super(context);
			unsafe = Provider.get(context).getOrBuildFunction(UnsafeSupplier.class, context).get();
		}

		@Override
		public void accept(Object origTarget, Field field, Object value) {
			if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
				throwExceptionFunction.apply("Value {} is not assignable to {}", value , field.getName());
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
		
	}
	
	
	public static abstract class Native extends SetFieldValueFunction{
		
		public Native(Map<Object, Object> context) {
			super(context);
		}

		public static class ForJava7 extends Native {
			
			public ForJava7(Map<Object, Object> context) {
				super(context);
			}

			@Override
			public void accept(Object target, Field field, Object value) {
				if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
					throwExceptionFunction.apply("Value {} is not assignable to {}", value , field.getName());
				}
				if (Modifier.isStatic(field.getModifiers())) {
					io.github.toolfactory.narcissus.Narcissus.setStaticField(field, value);
				} else {
					io.github.toolfactory.narcissus.Narcissus.setField(target, field, value);
				}	
			}
		}
	}
}
