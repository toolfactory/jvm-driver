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
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.github.toolfactory.jvm.function.template.Function;
import io.github.toolfactory.jvm.util.ClenableSupplier;
import io.github.toolfactory.jvm.util.ObjectProvider;


@SuppressWarnings("all")
public interface GetLoadedClassesFunction extends Function<ClassLoader, ClenableSupplier<Collection<Class<?>>>> {
	
	public static class ForJava7 implements GetLoadedClassesFunction {
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
		public ClenableSupplier<Collection<Class<?>>> apply(final ClassLoader classLoader) {
			if (classLoader == null) {
				throw new NullPointerException("Input classLoader parameter can't be null");
			}
			return new ClenableSupplier<Collection<Class<?>>>() {
				Collection<Class<?>> classes;
				
				@Override
				public Collection<Class<?>> get() {
					if (classes != null) {
						return classes;
					}
					return classes = (Collection<Class<?>>)unsafe.getObject(classLoader, loadedClassesVectorMemoryOffset);
				}

				@Override
				public void clear() {
					get();
					if (classes != null) {
						classes.clear();
					}
				}
				
			};
		}
		
		public static class ForSemeru implements GetLoadedClassesFunction {
			Function<ClassLoader, Hashtable<String, Object>> classNameBasedLockSupplier;
			Field classNameBasedLockField;
			
			public ForSemeru(Map<Object, Object> context) {
				ObjectProvider functionProvider = ObjectProvider.get(context);
				GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
				classNameBasedLockField = getDeclaredFieldFunction.apply(ClassLoader.class, "classNameBasedLock");
				classNameBasedLockSupplier = buildClassNameBasedLockSupplierSupplier(context);
			}

			Function<ClassLoader, Hashtable<String, Object>> buildClassNameBasedLockSupplierSupplier(final Map<Object, Object> context) {
				return new Function<ClassLoader, Hashtable<String, Object>>() {
					protected sun.misc.Unsafe unsafe = 
						ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
					protected Long classNameBasedLockHashTable = unsafe.objectFieldOffset(
						classNameBasedLockField
					);
					
					@Override
					public Hashtable<String, Object> apply(ClassLoader classLoader) {
						return (Hashtable<String, Object>)unsafe.getObject(classLoader, classNameBasedLockHashTable);
					}
					
				};
			}	
			
			@Override
			public ClenableSupplier<Collection<Class<?>>> apply(final ClassLoader classLoader) {
				return new ClenableSupplier<Collection<Class<?>>>() {
					Hashtable<String, Object> classNameBasedLock;
					Collection<Class<?>> loadedClasses = ConcurrentHashMap.newKeySet();
					
					@Override
					public Collection<Class<?>> get() {
						Hashtable<String, Object> loadedClassesHS = null;
						Hashtable<String, Object> loadedClassesHSTemp = getClassNameBasedLock();
						while (loadedClassesHS == null) {
							try {
								if (loadedClassesHSTemp != null) {
									loadedClassesHS = new Hashtable<String, Object>(
										loadedClassesHSTemp
									);
								} else {
									loadedClassesHS = new Hashtable<String, Object>();
								}						
							} catch (ConcurrentModificationException exc) {

							}
						}
						
						for (Entry<String, ?> entry : loadedClassesHS.entrySet()) {
							try {
								loadedClasses.add(Class.forName(entry.getKey()));
							} catch (Throwable exc) {

							}
						}	
						return loadedClasses;
					}

					@Override
					public void clear() {
						if (classNameBasedLock != null) {
							classNameBasedLock.clear();
						}
						loadedClasses.clear();
					}
					
					private Hashtable<String, Object> getClassNameBasedLock() {
						if (classNameBasedLock != null) {
							return classNameBasedLock;
						}
						return classNameBasedLock = (Hashtable<String, Object>)classNameBasedLockSupplier.apply(classLoader);
					}
					
				};
			}
			
		}
		
	}
	
	public static interface Native extends GetLoadedClassesFunction {
		
		public static class ForJava7 implements Native {
			Field classesField;
			
			public ForJava7(Map<Object, Object> context) {
				ObjectProvider functionProvider = ObjectProvider.get(context);
				GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
				classesField = getDeclaredFieldFunction.apply(ClassLoader.class, "classes");
			}

			@Override
			public ClenableSupplier<Collection<Class<?>>> apply(final ClassLoader classLoader) {
				if (classLoader == null) {
					throw new NullPointerException("Input classLoader parameter can't be null");
				}
				return new ClenableSupplier<Collection<Class<?>>>() {
					Collection<Class<?>> classes;
					
					@Override
					public Collection<Class<?>> get() {
						if (classes != null) {
							return classes;
						}
						return classes = (Collection<Class<?>>)io.github.toolfactory.narcissus.Narcissus.getField(classLoader, classesField);
					}

					@Override
					public void clear() {
						get();
						if (classes != null) {
							classes.clear();
						}
					}
					
				};
			}
			
			public static class ForSemeru extends GetLoadedClassesFunction.ForJava7.ForSemeru {
				
				public ForSemeru(Map<Object, Object> context) {
					super(context);
				}
				
				@Override
				Function<ClassLoader, Hashtable<String, Object>> buildClassNameBasedLockSupplierSupplier(final Map<Object, Object> context) {
					return new Function<ClassLoader, Hashtable<String, Object>>() {
						protected ThrowExceptionFunction throwExceptionFunction = 
								ObjectProvider.get(context).getOrBuildObject(ThrowExceptionFunction.class, context);
						@Override
						public Hashtable<String, Object> apply(ClassLoader classLoader) {
							try {
								return (Hashtable<String, Object>)io.github.toolfactory.narcissus.Narcissus.getField(classLoader, classNameBasedLockField);
							} catch (Throwable exc) {
								return throwExceptionFunction.apply(exc);
							}
						}
						
					};				
							
				}
				
			}
		}
		
	}
	
}
