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


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.module.ModuleReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.QuadFunction;
import io.github.toolfactory.jvm.function.template.TriFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;


public interface GetResourcesFunction extends TriFunction<String, Boolean, ClassLoader[], Collection<URL>>{
	
	public Collection<URL> apply(String resourceRelativePath, Boolean findFirst, Collection<ClassLoader> resourceClassLoaders);
	
	public abstract class Abst  implements GetResourcesFunction {
		protected ThrowExceptionFunction throwExceptionFunction;
		protected QuadFunction<ClassLoader, String, Boolean, Collection<URL>, Collection<URL>> resourceFinder;
		
		public Abst(Map<Object, Object> context) throws Throwable {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			throwExceptionFunction =
				functionProvider.getOrBuildObject(ThrowExceptionFunction.class, context);
			resourceFinder = buildResourceFinder(context);
		}

		protected abstract QuadFunction<ClassLoader, String, Boolean, Collection<URL>, Collection<URL>> buildResourceFinder(final Map<Object, Object> context) throws Throwable;
		
		
		@Override
		public Collection<URL> apply(String resourceRelativePath, Boolean findFirst, ClassLoader[] resourceClassLoaders) {
			Collection<URL> resources = new LinkedHashSet<>();
			if (resourceClassLoaders == null || resourceClassLoaders.length == 0) {
				return resourceFinder.apply(Thread.currentThread().getContextClassLoader(), resourceRelativePath, findFirst, resources);
			}			
			for (ClassLoader classLoader : resourceClassLoaders) {
				resourceFinder.apply(classLoader, resourceRelativePath, findFirst, resources);
			}
			return resources;
		}
		
		@Override
		public Collection<URL> apply(String resourceRelativePath, Boolean findFirst, Collection<ClassLoader> resourceClassLoaders) {
			Collection<URL> resources = new LinkedHashSet<>();
			if (resourceClassLoaders == null || resourceClassLoaders.isEmpty()) {
				return resourceFinder.apply(Thread.currentThread().getContextClassLoader(), resourceRelativePath, findFirst, resources);
			}
			for (ClassLoader classLoader : resourceClassLoaders) {
				resourceFinder.apply(classLoader, resourceRelativePath, findFirst, resources);
			}
			return resources;
		}
		
	}
	
	public static class ForJava7 extends Abst {
		
		public ForJava7(Map<Object, Object> context) throws Throwable {
			super(context);
		}

		@Override
		protected QuadFunction<ClassLoader, String, Boolean, Collection<URL>, Collection<URL>> buildResourceFinder(final Map<Object, Object> context) throws Throwable {
			return new QuadFunction<ClassLoader, String, Boolean, Collection<URL>, Collection<URL>> () {

				@Override
				public Collection<URL> apply(ClassLoader classLoader, String resourceRelativePath, Boolean findFirst, Collection<URL> resources) {
					try {
						if (findFirst) {
							resources.add(classLoader.getResource(resourceRelativePath));
						} else {
							Enumeration<URL> resourceURLS = classLoader.getResources(resourceRelativePath);
							while (resourceURLS.hasMoreElements()) {
								resources.add(resourceURLS.nextElement());
							}
						}
						return resources;
					} catch (Throwable exc) {
						return throwExceptionFunction.apply(exc);
					}	
				}
			};
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static class ForJava9 extends ForJava7 {
		
		public ForJava9(final Map<Object, Object> context) throws Throwable {
			super(context);
		}
		
		@Override
		protected QuadFunction<ClassLoader, String, Boolean, Collection<URL>, Collection<URL>> buildResourceFinder(final Map<Object, Object> context) throws Throwable {
			final ObjectProvider functionProvider = ObjectProvider.get(context);
			final MethodHandles.Lookup consulter = functionProvider.getOrBuildObject(DeepConsulterSupplyFunction.class, context).apply(Class.class);
			final GetClassByNameFunction getClassByNameFunction = functionProvider.getOrBuildObject(GetClassByNameFunction.class, context);
			final GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
			final QuadFunction<ClassLoader, String, Boolean, Collection<URL>, Collection<URL>> superResourceFinder = super.buildResourceFinder(context);
			
			return new QuadFunction<ClassLoader, String, Boolean, Collection<URL>, Collection<URL>>() {
				
				final Class<?> jdk_internal_loader_BuiltinClassLoaderClass = functionProvider.getOrBuildObject(BuiltinClassLoaderClassSupplier.class, context).get();
				final Class<?> java_lang_module_ModuleReferenceClass = getClassByNameFunction.apply(
					"java.lang.module.ModuleReference", false, this.getClass().getClassLoader(), this.getClass()
				);
				final Class<?> jdk_internal_loader_URLClassPathClass = getClassByNameFunction.apply(
					"jdk.internal.loader.URLClassPath", false, this.getClass().getClassLoader(), this.getClass()
				);
				final Class<?> jdk_internal_loader_BuiltinClassLoader$loadedModuleClass = getClassByNameFunction.apply(
					"jdk.internal.loader.BuiltinClassLoader$LoadedModule", false, this.getClass().getClassLoader(), this.getClass()
				);
				final Field jdk_internal_loader_BuiltinClassLoader_nameToModuleField = getDeclaredFieldFunction.apply(jdk_internal_loader_BuiltinClassLoaderClass, "nameToModule");				
				final Field jdk_internal_loader_BuiltinClassLoader_ucpField = getDeclaredFieldFunction.apply(jdk_internal_loader_BuiltinClassLoaderClass, "ucp");
				final Field jdk_internal_loader_BuiltinClassLoader_packageToModuleField = getDeclaredFieldFunction.apply(jdk_internal_loader_BuiltinClassLoaderClass, "packageToModule");
				final GetFieldValueFunction getFieldValueFunction = functionProvider.getOrBuildObject(GetFieldValueFunction.class, context);
				final MethodHandle jdk_internal_loader_BuiltinClassLoader$loadedModule_name = consulter.findVirtual(
					jdk_internal_loader_BuiltinClassLoader$loadedModuleClass,
					"name",
					MethodType.methodType(
						String.class
					)
				);
				final MethodHandle jdk_internal_loader_BuiltinClassLoader_moduleReaderFor = consulter.findVirtual(
					jdk_internal_loader_BuiltinClassLoaderClass,
					"moduleReaderFor",
					MethodType.methodType(
						ModuleReader.class, java_lang_module_ModuleReferenceClass
					)
				);
				final MethodHandle jdk_internal_loader_URLClassPath_findResources = consulter.findVirtual(
					jdk_internal_loader_URLClassPathClass,
					"findResources",
					MethodType.methodType(
						Enumeration.class, String.class, boolean.class
					)
				);	
				@Override
				public Collection<URL> apply(ClassLoader classLoader, String resourceRelativePath, Boolean findFirst, Collection<URL> resources) {
					try {
						if (jdk_internal_loader_BuiltinClassLoaderClass.isAssignableFrom(classLoader.getClass())) {
							Map<String, ?> packageToModule = (Map<String, ?>)getFieldValueFunction.apply(classLoader, jdk_internal_loader_BuiltinClassLoader_packageToModuleField);
							String packageName = toPackageName(resourceRelativePath);
							Object loadedModule = packageToModule.get(packageName);
							Map<String, ?> nameToModule = (Map<String, ?>)getFieldValueFunction.apply(classLoader, jdk_internal_loader_BuiltinClassLoader_nameToModuleField);
							if (loadedModule != null) {
								String moduleName = (String)jdk_internal_loader_BuiltinClassLoader$loadedModule_name.invokeWithArguments(loadedModule);
								Object moduleReference = nameToModule.get(moduleName);
								if (moduleReference != null) {
									URL resource = findResourceInModule(classLoader, moduleReference, resourceRelativePath);
									if (resource != null) {
										resources.add(resource);
										if (findFirst) {
											return resources;
										}
									}
								}
							}					
							for (Object moduleReference : nameToModule.values()) {
								URL resource = findResourceInModule(classLoader, moduleReference, resourceRelativePath);
								if (resource != null) {
									resources.add(resource);
									if (findFirst) {
										return resources;
									}
								}
	                        }
							Object ucp = getFieldValueFunction.apply(classLoader, jdk_internal_loader_BuiltinClassLoader_ucpField);
							Enumeration<URL> resourceURLS;
							try {
								resourceURLS = (Enumeration<URL>)jdk_internal_loader_URLClassPath_findResources.invokeWithArguments(ucp, resourceRelativePath, false);
							} catch (NullPointerException exc) {
								if (ucp != null) {
									return throwExceptionFunction.apply(exc);
								}
								return resources;
							}
							while (resourceURLS.hasMoreElements()) {
								resources.add(resourceURLS.nextElement());
								if (findFirst) {
									return resources;
								}
							}
							return resources;
						} else {
							return superResourceFinder.apply(classLoader, resourceRelativePath, findFirst, resources);
						}					
					} catch (Throwable exc) {
						return throwExceptionFunction.apply(exc);
					}	
				}
				
			    private String toPackageName(String name) {
			        int index = name.lastIndexOf('/');
			        if (index == -1 || index == name.length()-1) {
			            return "";
			        } else {
			            return name.substring(0, index).replace("/", ".");
			        }
			    }
					
				private URL findResourceInModule(ClassLoader classLoader, Object moduleReference, String resourceRelativePath) throws Throwable  {
					ModuleReader moduleReader = (ModuleReader)jdk_internal_loader_BuiltinClassLoader_moduleReaderFor.invokeWithArguments(classLoader, moduleReference);
					URI resourceURI = moduleReader.find(resourceRelativePath).orElse(null);
					if (resourceURI != null) {
						try {
							return resourceURI.toURL();
						} catch (MalformedURLException exc) {
							return throwExceptionFunction.apply(exc);
						}
					}
					return null;
				}
			};
		}

	}
}
