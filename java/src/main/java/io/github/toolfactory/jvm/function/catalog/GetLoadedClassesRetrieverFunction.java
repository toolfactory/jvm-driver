/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
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
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.toolfactory.jvm.NativeEngine;
import io.github.toolfactory.jvm.function.InitializeException;
import io.github.toolfactory.jvm.function.template.Function;
import io.github.toolfactory.jvm.util.CleanableSupplier;
import io.github.toolfactory.jvm.util.ObjectProvider;


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
						String[] classNames = {};
						Hashtable<String, Object> lockTable = getClassNameBasedLock();
						if (lockTable != null) {
							//OpenJ9 adds and removes the entries of this table while holding its monitor (an entry is
							//removed once the GC has cleared its lock), and Hashtable.remove sets the value of the
							//removed entry to null: a copy made without holding the monitor can read that null value
							synchronized (lockTable) {
								classNames = lockTable.keySet().toArray(new String[lockTable.size()]);
							}
						}
						for (String className : classNames) {
							try {
								Class<?> cls = getClassByNameFunction.apply(className, false, classLoader, CleanableSupplier.class);
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
			protected NativeEngine nativeEngine;

			public ForJava7(Map<Object, Object> context) throws Throwable {
				checkNativeEngine();
				ObjectProvider functionProvider = ObjectProvider.get(context);
				GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
				classesField = getDeclaredFieldFunction.apply(ClassLoader.class, "classes");
			}
			
			protected void checkNativeEngine() throws InitializeException {
				nativeEngine = NativeEngine.getInstance();
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
						return classes = (Collection<Class<?>>)nativeEngine.getFieldValue(classLoader, classesField);
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
					final NativeEngine nativeEngine = retrieveNativeEngine(context);
					return new ClassNameBasedLockSupplier() {

						@Override
						public Hashtable<String, Object> get(ClassLoader classLoader) {
							return (Hashtable<String, Object>)nativeEngine.getFieldValue(classLoader, classNameBasedLockField);
						}

						@Override
						protected ClassLoader getClassLoader(Class<?> cls) {
							return (ClassLoader)nativeEngine.getFieldValue(cls, classLoaderField);
						}

					};

				}

				//This method is called by the constructor of the superclass, so it cannot store the engine in a field
				protected NativeEngine retrieveNativeEngine(Map<Object, Object> context) {
					try {
						return NativeEngine.getInstance();
					} catch (InitializeException exc) {
						return ObjectProvider.get(context).getOrBuildObject(ThrowExceptionFunction.class, context).apply(exc);
					}
				}

			}
		}

	}

}
