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


import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import sun.misc.Unsafe;


@SuppressWarnings("all")
public class DefaultDriver implements Driver {
	
	MethodHandle getDeclaredFieldsRetriever;
	MethodHandle getDeclaredMethodsRetriever;
	MethodHandle getDeclaredConstructorsRetriever;
	MethodHandle methodInvoker;
	MethodHandle constructorInvoker;
	
	Function<ClassLoader, Collection<Class<?>>> loadedClassesRetriever;
	Function<ClassLoader, Map<String, ?>> loadedPackagesRetriever;
	Function<Class<?>, Object> allocateInstanceInvoker;
	BiFunction<Object, Field, Object> fieldValueRetriever;
	Function<Object, BiConsumer<Field, Object>> fieldValueSetter;
	BiConsumer<AccessibleObject, Boolean> accessibleSetter;
	Function<Class<?>, MethodHandles.Lookup> consulterRetriever;
	BiFunction<ClassLoader, Object, Function<String, Package>> packageRetriever;
	BiFunction<Class<?>, byte[], Class<?>> hookClassDefiner;
	
	Class<?> classLoaderDelegateClass;
	Class<?> builtinClassLoaderClass;
	
	public DefaultDriver() {
		retrieveInitializer().start().close();
	}
	
	protected Initializer retrieveInitializer() {
		JVMInfo jVMInfo = JVMInfo.create();
		return 
			(jVMInfo.getVersion() > 8 ?
				jVMInfo.getVersion() > 16 ?
					new Initializer.ForJava17(this):
				new Initializer.ForJava9(this):
			new Initializer.ForJava8(this));
	}
	
	@Override
	public void setAccessible(AccessibleObject object, boolean flag) {
		try {
			accessibleSetter.accept(object, flag);
		} catch (Throwable exc) {
			Throwables.throwException(exc);
		}
	}
	
	@Override
	public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode) {
		try {
			return hookClassDefiner.apply(clientClass, byteCode);
		} catch (Throwable exc) {
			return Throwables.throwException(exc);
		}
	}
	
	@Override
	public Package retrieveLoadedPackage(ClassLoader classLoader, Object packageToFind, String packageName) {
		return packageRetriever.apply(classLoader, packageToFind).apply(packageName);
	}
	
	@Override
	public Collection<Class<?>> retrieveLoadedClasses(ClassLoader classLoader) {
		return loadedClassesRetriever.apply(classLoader);
	}
	
	@Override
	public Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader) {
		return loadedPackagesRetriever.apply(classLoader);
	}
	
	@Override
	public boolean isBuiltinClassLoader(ClassLoader classLoader) {
		return builtinClassLoaderClass != null && builtinClassLoaderClass.isAssignableFrom(classLoader.getClass());
	}
	
	@Override
	public boolean isClassLoaderDelegate(ClassLoader classLoader) {
		return classLoaderDelegateClass != null && classLoaderDelegateClass.isAssignableFrom(classLoader.getClass());
	}
	
	@Override
	public Class<?> getBuiltinClassLoaderClass() {
		return builtinClassLoaderClass;
	}
	
	@Override
	public Class getClassLoaderDelegateClass() {
		return classLoaderDelegateClass;
	}
	
	@Override
	public Lookup getConsulter(Class<?> cls) {
		return consulterRetriever.apply(cls);
	}
	
	@Override
	public Object invoke(Method method, Object target, Object[] params) {
		try {
			return methodInvoker.invoke(method, target, params);
		} catch (Throwable exc) {
			return Throwables.throwException(exc);
		}			
	}
	
	@Override
	public <T> T newInstance(Constructor<T> ctor, Object[] params) {
		try {
			return (T)constructorInvoker.invoke(ctor, params);
		} catch (Throwable exc) {
			return Throwables.throwException(exc);
		}			
	}
	
	@Override
	public Field getDeclaredField(Class<?> cls, String name) {
		for (Field field : getDeclaredFields(cls)) {
			if (field.getName().equals(name)) {
				return field;
			}
		}
		return null;
	}
	
	@Override
	public Field[] getDeclaredFields(Class<?> cls)  {
		try {
			return (Field[])getDeclaredFieldsRetriever.invoke(cls, false);
		} catch (Throwable exc) {
			return Throwables.throwException(exc);
		}		
	}
	
	@Override
	public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls) {
		try {
			return (Constructor<T>[])getDeclaredConstructorsRetriever.invoke(cls, false);
		} catch (Throwable exc) {
			return Throwables.throwException(exc);
		}
	}
	
	@Override
	public Method[] getDeclaredMethods(Class<?> cls) {
		try {
			return (Method[])getDeclaredMethodsRetriever.invoke(cls, false);
		} catch (Throwable exc) {
			return Throwables.throwException(exc);
		}
	}
	
	@Override
	public <T> T getFieldValue(Object target, Field field) {
		return (T)fieldValueRetriever.apply(target, field);
	}
	
	@Override
	public void setFieldValue(Object target, Field field, Object value) {
		fieldValueSetter.apply(target).accept(field, value);
	}
	
	public <T> T allocateInstance(Class<?> cls) {
		return (T)allocateInstanceInvoker.apply(cls);
	}
	
	@Override
	public void close() {
		getDeclaredFieldsRetriever = null;
		getDeclaredMethodsRetriever = null;
		getDeclaredConstructorsRetriever = null;
		packageRetriever = null;	
		methodInvoker = null;
		constructorInvoker = null;
		accessibleSetter = null;	
		consulterRetriever = null;
		classLoaderDelegateClass = null;
		builtinClassLoaderClass = null;
		hookClassDefiner = null;
	}
	
	protected abstract static class Initializer implements Closeable {
		DefaultDriver driver;
		NativeFunctionSupplier nativeFunctionSupplier;
		
		protected Initializer(DefaultDriver driver) {
			this.driver = driver;
			initNativeFunctionSupplier();
		}	

		protected Initializer start() {
			driver.allocateInstanceInvoker = nativeFunctionSupplier.getAllocateInstanceFunction();
			driver.fieldValueRetriever = nativeFunctionSupplier.getFieldValueFunction();
			driver.fieldValueSetter = nativeFunctionSupplier.getSetFieldValueFunction();
			initConsulterRetriever();
			initDefineHookClassFunction();
			initMembersRetrievers();
			initAccessibleSetter();
			initConstructorInvoker();
			initMethodInvoker();
			initSpecificElements();			
			driver.loadedClassesRetriever = nativeFunctionSupplier.getRetrieveLoadedClassesFunction();
			driver.loadedPackagesRetriever = nativeFunctionSupplier.getRetrieveLoadedPackagesFunction();
			return this;
		}
		
		protected abstract void initNativeFunctionSupplier();
		
		protected abstract void initDefineHookClassFunction();

		protected abstract void initConsulterRetriever();
		
		protected abstract void initAccessibleSetter();
		
		protected abstract void initSpecificElements();
		
		protected abstract void initConstructorInvoker();	
		
		protected abstract void initMethodInvoker();			
		
		protected void initMembersRetrievers() {
			try {
				MethodHandles.Lookup consulter = driver.getConsulter(Class.class);
				driver.getDeclaredFieldsRetriever = consulter.findSpecial(
					Class.class,
					"getDeclaredFields0",
					MethodType.methodType(Field[].class, boolean.class),
					Class.class
				);
				
				driver.getDeclaredMethodsRetriever = consulter.findSpecial(
					Class.class,
					"getDeclaredMethods0",
					MethodType.methodType(Method[].class, boolean.class),
					Class.class
				);

				driver.getDeclaredConstructorsRetriever = consulter.findSpecial(
					Class.class,
					"getDeclaredConstructors0",
					MethodType.methodType(Constructor[].class, boolean.class),
					Class.class
				);
			} catch (Throwable exc) {
				Throwables.throwException(exc);
			}
		}
		
		@Override
		public void close() {
			nativeFunctionSupplier.close();
			nativeFunctionSupplier = null;
			driver = null;
		}
		
		protected static class ForJava8 extends Initializer {
			MethodHandle consulterClassConstructor;			
			
			protected ForJava8(DefaultDriver driver) {
				super(driver);
				try {
					consulterClassConstructor = nativeFunctionSupplier.getMethodHandlesLookupSupplyingFunction().get().findConstructor(
						MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class, int.class)
					);
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize consulter retriever", exc));
				}
			}
			
			protected void initNativeFunctionSupplier() {
				this.nativeFunctionSupplier = new UnsafeNativeFunctionSupplier.ForJava8(this.driver);
			}	

			protected void initConsulterRetriever() {
				try {
					MethodHandle consulterClassConstructor = this.consulterClassConstructor;
					driver.consulterRetriever = (cls) -> {
						try {
							return (Lookup) consulterClassConstructor.invoke(cls, -1);
						} catch (Throwable exc) {
							return Throwables.throwException(exc);
						}
					};
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize consulter retriever", exc));
				}
			}
			
			@Override
			protected void initDefineHookClassFunction() {
				try {
					driver.hookClassDefiner = nativeFunctionSupplier.getDefineHookClassFunction(driver.consulterRetriever);
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize consulter retriever", exc));
				}
								
			}
			
			protected void initAccessibleSetter() {
				try {
					final Method accessibleSetterMethod = AccessibleObject.class.getDeclaredMethod("setAccessible0", AccessibleObject.class, boolean.class);
					MethodHandle accessibleSetterMethodHandle = driver.getConsulter(AccessibleObject.class).unreflect(accessibleSetterMethod);
					driver.accessibleSetter = (accessibleObject, flag) -> {
						try {
							accessibleSetterMethodHandle.invoke(accessibleObject, flag);
						} catch (Throwable exc) {
							Throwables.throwException(exc);
						}
					};
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize accessible setter", exc));
				}
			}
			
			@Override
			protected void initConstructorInvoker() {
				try {
					Class<?> nativeAccessorImplClass = Class.forName("sun.reflect.NativeConstructorAccessorImpl");
					Method method = nativeAccessorImplClass.getDeclaredMethod("newInstance0", Constructor.class, Object[].class);
					MethodHandles.Lookup consulter = driver.getConsulter(nativeAccessorImplClass);
					driver.constructorInvoker = consulter.unreflect(method);
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize constructor invoker", exc));
				}				
			}			
			
			protected void initMethodInvoker() {
				try {
					Class<?> nativeAccessorImplClass = Class.forName("sun.reflect.NativeMethodAccessorImpl");
					Method method = nativeAccessorImplClass.getDeclaredMethod("invoke0", Method.class, Object.class, Object[].class);
					MethodHandles.Lookup consulter = driver.getConsulter(nativeAccessorImplClass);
					driver.methodInvoker = consulter.unreflect(method);
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize method invoker", exc));
				}
			}
			
			@Override
			protected void initSpecificElements() {
				driver.packageRetriever = (classLoader, object) -> (packageName) -> (Package)object;	
			}
			
			@Override
			public void close() {
				super.close();
				this.consulterClassConstructor = null;
			}
			
		}
		
		protected static class ForJava9 extends Initializer {
			MethodHandle consulterClassConstructor;
			
			protected ForJava9(DefaultDriver driver) {
				super(driver);
				try {
					consulterClassConstructor = nativeFunctionSupplier.getMethodHandlesLookupSupplyingFunction().get().findConstructor(
						MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class)
					);
				} catch (Throwable exc) {
					Throwables.throwException(exc);
				}
			}
			
			@Override
			protected void initNativeFunctionSupplier() {
				this.nativeFunctionSupplier = new UnsafeNativeFunctionSupplier.ForJava9(this.driver);
			}	
			
			protected void initDefineHookClassFunction() {
				MethodHandle consulterClassConstructor = this.consulterClassConstructor;
				driver.hookClassDefiner = nativeFunctionSupplier.getDefineHookClassFunction(cls -> {
					try {
						return (MethodHandles.Lookup)consulterClassConstructor.invoke(cls);
					} catch (Throwable exc) {
						return Throwables.throwException(exc);
					}
				});
			}
			
			protected void initConsulterRetriever() {
				try {
					MethodHandle consulterClassConstructor = this.consulterClassConstructor;
					driver.consulterRetriever = (cls) -> {
						try {
							return (MethodHandles.Lookup)consulterClassConstructor.invoke(cls);
						} catch (Throwable exc) {
							return Throwables.throwException(exc);
						}
					};
				} catch (Throwable exc) {
					Throwables.throwException(exc);
				}
				
			}
			
			protected void initAccessibleSetter() {
				try {
					MethodHandle accessibleSetter = driver.getConsulter(AccessibleObject.class).findSpecial(
						AccessibleObject.class, "setAccessible0",  
						MethodType.methodType(boolean.class, boolean.class), 
						AccessibleObject.class
					);
					driver.accessibleSetter = (accessibleObject, flag) -> {
						try {
							accessibleSetter.invoke(accessibleObject, flag);
						} catch (Throwable exc) {
							Throwables.throwException(exc);
						}
					};
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize accessible setter", exc));
				}
			}
			

			@Override
			protected void initConstructorInvoker() {
				try {
					Class<?> cls = Class.forName("jdk.internal.reflect.NativeConstructorAccessorImpl");
					driver.constructorInvoker = driver.getConsulter(cls).findStatic(
						cls, "newInstance0", MethodType.methodType(Object.class, Constructor.class, Object[].class)						
					);
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize constructor invoker", exc));
				}	
			}
			
			protected void initMethodInvoker() {
				try {
					Class<?> cls = Class.forName("jdk.internal.reflect.NativeMethodAccessorImpl");
					driver.methodInvoker =  driver.getConsulter(cls).findStatic(
						cls, 
						"invoke0", MethodType.methodType(Object.class, Method.class, Object.class, Object[].class)						
					);
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize method invoker", exc));
				}
			}

			
			@Override
			protected void initSpecificElements() {
				try {
					MethodHandles.Lookup classLoaderConsulter = driver.consulterRetriever.apply(ClassLoader.class);
					MethodHandle methodHandle = classLoaderConsulter.findSpecial(
						ClassLoader.class, "getDefinedPackage",
						MethodType.methodType(Package.class, String.class),
						ClassLoader.class
					);
					driver.packageRetriever = (classLoader, object) -> (packageName) -> {
						try {
							return (Package)methodHandle.invokeExact(classLoader, packageName);
						} catch (Throwable exc) {
							return Throwables.throwException(exc);
						}
					};
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize package retriever", exc));
				}
				try {
					driver.builtinClassLoaderClass = Class.forName("jdk.internal.loader.BuiltinClassLoader");
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize builtin class loader class", exc));
				}
				try (
					InputStream inputStream =
						Resources.getAsInputStream(this.getClass().getClassLoader(), this.getClass().getPackage().getName().replace(".", "/") + "/ClassLoaderDelegateForJDK9.bwc"
					);
				) {
					driver.classLoaderDelegateClass = driver.defineHookClass(
						driver.builtinClassLoaderClass, Streams.toByteArray(inputStream)
					);
				} catch (Throwable exc) {
					Throwables.throwException(new InitializationException("Could not initialize class loader delegate class", exc));
				}
			}

			
			@Override
			public void close() {
				super.close();
				this.consulterClassConstructor = null;
			}

		}
		
		protected static class ForJava17 extends ForJava9 {
			
			protected ForJava17(DefaultDriver driver) {
				super(driver);
			}
			
			@Override
			protected void initDefineHookClassFunction() {
				try {
					MethodHandle defineClassMethodHandle = driver.getConsulter(MethodHandles.Lookup.class).findSpecial(
						MethodHandles.Lookup.class,
						"defineClass",
						MethodType.methodType(Class.class, byte[].class),
						MethodHandles.Lookup.class
					);
					driver.hookClassDefiner = (clientClass, byteCode) -> {
						try {
							try {
								return (Class<?>) defineClassMethodHandle.invoke(driver.getConsulter(clientClass), byteCode);
							} catch (LinkageError exc) {
								return JavaClass.extractByUsing(ByteBuffer.wrap(byteCode), javaClass -> {
									try {
										return Class.forName(javaClass.getName());
									} catch (Throwable inExc) {
										return Throwables.throwException(inExc);
									}
								});
							}
						} catch (Throwable exc) {
							return Throwables.throwException(exc);
						}						
					};
				} catch (Throwable exc) {
					Throwables.throwException(exc);
				}
			}
		}
	
		
	}

}
