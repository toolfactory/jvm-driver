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
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.github.toolfactory.jvm.function.template.Function;
import io.github.toolfactory.jvm.util.ObjectProvider;


@SuppressWarnings("all")
public abstract class GetLoadedClassesFunction implements Function<ClassLoader, Collection<Class<?>>> {
	
	public static class ForJava7 extends GetLoadedClassesFunction {
		protected sun.misc.Unsafe unsafe;
		protected Long loadedClassesVectorMemoryOffset;
		
		public ForJava7(Map<Object, Object> context) {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			unsafe = functionProvider.getOrBuildObject(UnsafeSupplier.class, context).get();
			GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
			loadedClassesVectorMemoryOffset = unsafe.objectFieldOffset(
				getDeclaredFieldFunction.apply(ClassLoader.class, "classes")
			);
		}		
		
		@Override
		public Collection<Class<?>> apply(ClassLoader classLoader) {
			if (classLoader == null) {
				throw new NullPointerException("Input classLoader parameter can't be null");
			}
			return (Collection<Class<?>>)unsafe.getObject(classLoader, loadedClassesVectorMemoryOffset);
		}
		
		public static class ForSemeru extends GetLoadedClassesFunction {
			protected sun.misc.Unsafe unsafe;
			protected Long classNameBasedLockHashTable;
			
			public ForSemeru(Map<Object, Object> context) {
				ObjectProvider functionProvider = ObjectProvider.get(context);
				unsafe = functionProvider.getOrBuildObject(UnsafeSupplier.class, context).get();
				GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
				classNameBasedLockHashTable = unsafe.objectFieldOffset(
					getDeclaredFieldFunction.apply(ClassLoader.class, "classNameBasedLock")
				);
			}	
			
			@Override
			public Collection<Class<?>> apply(ClassLoader classLoader) {
				if (classLoader == null) {
					throw new NullPointerException("Input classLoader parameter can't be null");
				}
				Hashtable<String, ?> loadedClassesHS = new Hashtable<String, Object>((Hashtable<String, ?>)unsafe.getObject(classLoader, classNameBasedLockHashTable));
				Set<Class<?>> loadedClasses = new HashSet<Class<?>>();
				for (Entry<String, ?> classEntry : loadedClassesHS.entrySet()) {
					try {
						loadedClasses.add(Class.forName(classEntry.getKey()));
					} catch (ClassNotFoundException exc) {

					}
				}				
				return loadedClasses;
			}
			
		}
		
	}
	
	public static abstract class Native extends GetLoadedClassesFunction {
		Field classesField;
		
		public static class ForJava7 extends Native {
			
			public ForJava7(Map<Object, Object> context) {
				ObjectProvider functionProvider = ObjectProvider.get(context);
				GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
				classesField = getDeclaredFieldFunction.apply(ClassLoader.class, "classes");
			}

			@Override
			public Collection<Class<?>> apply(ClassLoader classLoader) {
				if (classLoader == null) {
					throw new NullPointerException("Input classLoader parameter can't be null");
				}
				return (Collection<Class<?>>)io.github.toolfactory.narcissus.Narcissus.getField(classLoader, classesField);
			}
			
			public static class ForSemeru extends Native {
				
				public ForSemeru(Map<Object, Object> context) {
					ObjectProvider functionProvider = ObjectProvider.get(context);
					GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
					classesField = getDeclaredFieldFunction.apply(ClassLoader.class, "classNameBasedLock");
				}
				
				@Override
				public Collection<Class<?>> apply(ClassLoader classLoader) {
					if (classLoader == null) {
						throw new NullPointerException("Input classLoader parameter can't be null");
					}
					Hashtable<String, ?> loadedClassesHS = (Hashtable<String, ?>)io.github.toolfactory.narcissus.Narcissus.getField(classLoader, classesField);
					Set<Class<?>> loadedClasses = new HashSet<Class<?>>();
					for (Entry<String, ?> classEntry : loadedClassesHS.entrySet()) {
						try {
							loadedClasses.add(Class.forName(classEntry.getKey()));
						} catch (ClassNotFoundException exc) {

						}
					}				
					return loadedClasses;
				}
				
			}
		}
		
	}
	
}
