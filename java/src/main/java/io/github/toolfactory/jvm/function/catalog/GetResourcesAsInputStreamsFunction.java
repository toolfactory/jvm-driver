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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.BiFunction;
import io.github.toolfactory.jvm.util.ObjectProvider;


@SuppressWarnings("unchecked")
public interface GetResourcesAsInputStreamsFunction extends BiFunction<String, ClassLoader[], Map<URL, InputStream>>{
	
	public abstract class Abst  implements GetResourcesAsInputStreamsFunction {
		protected ThrowExceptionFunction throwExceptionFunction;
		protected BiFunction<ClassLoader, String, Collection<URL>> resourceFinder;
		
		public Abst(Map<Object, Object> context) {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			throwExceptionFunction =
				functionProvider.getOrBuildObject(ThrowExceptionFunction.class, context);
		}
		
	}
	
	public static class ForJava7 extends Abst {
		
		public ForJava7(Map<Object, Object> context) {
			super(context);
			resourceFinder = new BiFunction<ClassLoader, String, Collection<URL>>() {

				@Override
				public Collection<URL> apply(ClassLoader classLoader, String resourceRelativePath) {
					try {
						return Collections.list(classLoader.getResources(resourceRelativePath));
					
					} catch (Throwable exc) {
						return throwExceptionFunction.apply(exc);
					}	
				}
			};
		}

		@Override
		public Map<URL, InputStream> apply(String resourceRelativePath, ClassLoader[] resourceClassLoaders) {
			if (resourceClassLoaders == null || resourceClassLoaders.length == 0) {
				resourceClassLoaders = new ClassLoader[]{Thread.currentThread().getContextClassLoader()};
			}
			Map<URL, InputStream> streams = new LinkedHashMap<>();
			for (ClassLoader classLoader : resourceClassLoaders) {
				Collection<URL> resources = resourceFinder.apply(classLoader, resourceRelativePath);
				Iterator<URL> itr = resources.iterator();
				while (resources != null && itr.hasNext()) {
					URL resourceURL = itr.next();
					try {
						streams.put(
							resourceURL,
							resourceURL.openStream()
						);
					} catch (IOException exc) {
						return throwExceptionFunction.apply(exc);
					}
				}
			}
			return streams;
		}
		
	}
	
	public static class ForJava9 extends ForJava7 {

		public ForJava9(final Map<Object, Object> context) {
			super(context);
			final ObjectProvider functionProvider = ObjectProvider.get(context);
			final GetDeclaredMethodFunction getDeclaredMethodFunction =
				functionProvider.getOrBuildObject(GetDeclaredMethodFunction.class, context);
			resourceFinder = new BiFunction<ClassLoader, String, Collection<URL>>() {
				Class<?> builtinClassLoaderClass = functionProvider.getOrBuildObject(BuiltinClassLoaderClassSupplier.class, context).get();
				MethodInvokeFunction methodInvokeFunction = functionProvider.getOrBuildObject(MethodInvokeFunction.class, context);
				Method findResourcesOnClassPathMethod = getDeclaredMethodFunction.apply(builtinClassLoaderClass, "findResourcesOnClassPath", new Class<?>[]{String.class});
				Method findMiscResourceMethod = getDeclaredMethodFunction.apply(builtinClassLoaderClass, "findMiscResource", new Class<?>[]{String.class});
				

				@Override
				public Collection<URL> apply(ClassLoader classLoader, String resourceRelativePath) {
					try {
						if (builtinClassLoaderClass.isAssignableFrom(classLoader.getClass())) {
							Collection<URL> resources = new ArrayList<>();
							resources.addAll(Collections.list(
								(Enumeration<URL>)methodInvokeFunction.apply(findResourcesOnClassPathMethod, classLoader, new Object[]{resourceRelativePath}))
							);
							resources.addAll(
								(Collection<? extends URL>) methodInvokeFunction.apply(findMiscResourceMethod, classLoader, new Object[]{resourceRelativePath})
							);
							return resources;
						} else {
							return Collections.list(classLoader.getResources(resourceRelativePath));
						}					
					} catch (Throwable exc) {
						return throwExceptionFunction.apply(exc);
					}	
				}
			};
		}

	}
}
