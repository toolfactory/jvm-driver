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
package io.github.toolfactory.jvm.function.catalog;


import java.lang.reflect.Field;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.github.toolfactory.jvm.function.InitializeException;
import io.github.toolfactory.jvm.function.template.Function;
import io.github.toolfactory.jvm.util.CleanableSupplier;
import io.github.toolfactory.jvm.util.ObjectProvider;
import io.github.toolfactory.jvm.util.Strings;
import io.github.toolfactory.narcissus.Narcissus;


@SuppressWarnings("all")
public interface GetLoadedClassesRetrieverFunction extends Function<ClassLoader, CleanableSupplier<Collection<Class<?>>>> {

	public static class ForJava7 implements GetLoadedClassesRetrieverFunction {
		protected sun.misc.Unsafe unsafe;
		protected Long loadedClassesVectorMemoryOffset;

		public ForJava7(Map<Object, Object> context) throws Throwable {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			unsafe = functionProvider.getOrBuildObject(UnsafeSupplier.class, context).get();
			GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
			loadedClassesVectorMemoryOffset = unsafe.objectFieldOffset(
				getDeclaredFieldFunction.apply(ClassLoader.class, "classes")
			);
		}

		@Override
		public CleanableSupplier<Collection<Class<?>>> apply(final ClassLoader classLoader) {
			if (classLoader == null) {
				throw new NullPointerException("Input classLoader parameter can't be null");
			}
			return new CleanableSupplier<Collection<Class<?>>>() {
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

		public static class ForSemeru implements GetLoadedClassesRetrieverFunction {
			protected ClassNameBasedLockSupplier classNameBasedLockSupplier;
			protected Field classNameBasedLockField;
			protected Field classLoaderField;
			protected GetClassByNameFunction getClassByNameFunction;

			public ForSemeru(Map<Object, Object> context) throws Throwable {
				ObjectProvider functionProvider = ObjectProvider.get(context);
				GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
				getClassByNameFunction = functionProvider.getOrBuildObject(GetClassByNameFunction.class, context);
				classNameBasedLockField = getDeclaredFieldFunction.apply(ClassLoader.class, "classNameBasedLock");
				classLoaderField = getDeclaredFieldFunction.apply(Class.class, "classLoader");
				classNameBasedLockSupplier = buildClassNameBasedLockSupplier(context);
			}

			protected ClassNameBasedLockSupplier buildClassNameBasedLockSupplier(final Map<Object, Object> context) {
				return new ClassNameBasedLockSupplier() {
					protected sun.misc.Unsafe unsafe =
						ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
					protected Long classNameBasedLockHashTableOffset = unsafe.objectFieldOffset(
						classNameBasedLockField
					);
					protected Long classLoaderFieldOffset = unsafe.objectFieldOffset(
						classLoaderField
					);


					@Override
					public Hashtable<String, Object> get(ClassLoader classLoader) {
						return (Hashtable<String, Object>)unsafe.getObject(classLoader, classNameBasedLockHashTableOffset);
					}

					@Override
					protected ClassLoader getClassLoader(Class<?> cls) {
						return (ClassLoader)unsafe.getObject(cls, classLoaderFieldOffset);
					}

				};
			}

			@Override
			public CleanableSupplier<Collection<Class<?>>> apply(final ClassLoader classLoader) {
				return new CleanableSupplier<Collection<Class<?>>>() {
					Hashtable<String, Object> classNameBasedLock;
					Collection<Class<?>> loadedClasses = ConcurrentHashMap.newKeySet();

					@Override
					public Collection<Class<?>> get() {
						Hashtable<String, Object> loadedClassesHS = null;
						Hashtable<String, Object> loadedClassesHSTemp = getClassNameBasedLock();
						while (loadedClassesHS == null) {
							try {
								if (loadedClassesHSTemp != null) {
									loadedClassesHS = new Hashtable<>(
										loadedClassesHSTemp
									);
								} else {
									loadedClassesHS = new Hashtable<>();
								}
							} catch (ConcurrentModificationException exc) {

							}
						}

						for (Entry<String, ?> entry : loadedClassesHS.entrySet()) {
							try {
								Class<?> cls = getClassByNameFunction.apply(entry.getKey(), false, classLoader, CleanableSupplier.class);
								if (classNameBasedLockSupplier.getClassLoader(cls) == classLoader) {
									loadedClasses.add(cls);
								}
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
						return classNameBasedLock = classNameBasedLockSupplier.get(classLoader);
					}

				};
			}

			protected abstract static class ClassNameBasedLockSupplier {

				protected abstract Hashtable<String, Object> get(ClassLoader classLoader);

				protected abstract ClassLoader getClassLoader(Class<?> cls);

			}
		}

	}

	public static interface Native extends GetLoadedClassesRetrieverFunction {

		public static class ForJava7 implements Native {
			protected Field classesField;

			public ForJava7(Map<Object, Object> context) throws Throwable {
				checkNativeEngine();
				ObjectProvider functionProvider = ObjectProvider.get(context);
				GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
				classesField = getDeclaredFieldFunction.apply(ClassLoader.class, "classes");
			}
			
			protected void checkNativeEngine() throws InitializeException {
				if (!Narcissus.libraryLoaded) {
					throw new InitializeException(
						Strings.compile(
							"Could not initialize the native engine {}", 
							io.github.toolfactory.narcissus.Narcissus.class.getName()
						)
					);
				}
			}
			
			@Override
			public CleanableSupplier<Collection<Class<?>>> apply(final ClassLoader classLoader) {
				if (classLoader == null) {
					throw new NullPointerException("Input classLoader parameter can't be null");
				}
				return new CleanableSupplier<Collection<Class<?>>>() {
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

			public static class ForSemeru extends GetLoadedClassesRetrieverFunction.ForJava7.ForSemeru {

				public ForSemeru(Map<Object, Object> context) throws Throwable {
					super(context);
				}

				@Override
				protected ClassNameBasedLockSupplier buildClassNameBasedLockSupplier(final Map<Object, Object> context) {
					return new ClassNameBasedLockSupplier() {

						@Override
						public Hashtable<String, Object> get(ClassLoader classLoader) {
							return (Hashtable<String, Object>)io.github.toolfactory.narcissus.Narcissus.getField(classLoader, classNameBasedLockField);
						}

						@Override
						protected ClassLoader getClassLoader(Class<?> cls) {
							return (ClassLoader)io.github.toolfactory.narcissus.Narcissus.getField(cls, classLoaderField);
						}

					};

				}

			}
		}

	}

}
