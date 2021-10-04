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


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.github.toolfactory.jvm.function.catalog.AllocateInstanceFunction;
import io.github.toolfactory.jvm.function.catalog.BuiltinClassLoaderClassSupplier;
import io.github.toolfactory.jvm.function.catalog.ClassLoaderDelegateClassSupplier;
import io.github.toolfactory.jvm.function.catalog.ConstructorInvokeMethodHandleSupplier;
import io.github.toolfactory.jvm.function.catalog.ConsulterSupplyFunction;
import io.github.toolfactory.jvm.function.catalog.DeepConsulterSupplyFunction;
import io.github.toolfactory.jvm.function.catalog.DefineHookClassFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredConstructorsFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredFieldsFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredMethodsFunction;
import io.github.toolfactory.jvm.function.catalog.GetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedClassesFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedPackagesFunction;
import io.github.toolfactory.jvm.function.catalog.GetPackageFunction;
import io.github.toolfactory.jvm.function.catalog.MethodInvokeMethodHandleSupplier;
import io.github.toolfactory.jvm.function.catalog.SetAccessibleFunction;
import io.github.toolfactory.jvm.function.catalog.SetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.ThrowExceptionFunction;
import io.github.toolfactory.jvm.function.template.BiFunction;
import io.github.toolfactory.jvm.function.template.Function;
import io.github.toolfactory.jvm.function.template.TriConsumer;
import io.github.toolfactory.jvm.util.BiConsumerAdapter;
import io.github.toolfactory.jvm.util.FunctionAdapter;
import io.github.toolfactory.jvm.util.ObjectProvider;



@SuppressWarnings("unchecked")
public class DefaultDriver implements Driver {	
	
	protected ThrowExceptionFunction exceptionThrower; 
	protected Function<Class<?>, Object> allocateInstanceInvoker;	
	protected BiFunction<Object, Field, Object> fieldValueRetriever;
	protected TriConsumer<Object, Field, Object> fieldValueSetter;
	protected BiFunction<Class<?>, byte[], Class<?>> hookClassDefiner;
	protected FunctionAdapter<?, Class<?>, MethodHandles.Lookup> consulterRetriever;
	protected Function<Class<?>, Field[]> declaredFieldsRetriever;
	protected Function<Class<?>, Method[]> declaredMethodsRetriever;
	protected Function<Class<?>, Constructor<?>[]> declaredConstructorsRetriever;
	protected BiConsumerAdapter<?, AccessibleObject, Boolean> accessibleSetter;
	protected MethodHandle constructorInvoker;
	protected BiFunction<ClassLoader, String, Package> packageRetriever;
	protected MethodHandle methodInvoker;
	protected Class<?> builtinClassLoaderClass;
	protected Class<?> classLoaderDelegateClass;
	protected Function<ClassLoader, Collection<Class<?>>> loadedClassesRetriever;
	protected Function<ClassLoader, Map<String, ?>> loadedPackagesRetriever;	


	public DefaultDriver() {
		ObjectProvider functionProvider = new ObjectProvider(
			Info.CRITICAL_VERSIONS
		);
		
		Map<Object, Object> initializationContext = new HashMap<>();
		
		initExceptionThrower(functionProvider, initializationContext);	
		try {
			initAllocateInstanceInvoker(functionProvider, initializationContext);	
			initFieldValueRetriever(functionProvider, initializationContext);
			initFieldValueSetter(functionProvider, initializationContext);
			initHookClassDefiner(functionProvider, initializationContext);
			initConsulterRetriever(functionProvider, initializationContext);
			initDeclaredFieldsRetriever(functionProvider, initializationContext);
			initDeclaredMethodsRetriever(functionProvider, initializationContext);
			initDeclaredConstructorsRetriever(functionProvider, initializationContext);
			initAccessibleSetter(functionProvider, initializationContext);
			initConstructorInvoker(functionProvider, initializationContext);
			initMethodInvoker(functionProvider, initializationContext);
			initPackageRetriever(functionProvider, initializationContext);		
			initBuiltinClassLoaderClass(functionProvider, initializationContext);	
			initClassLoaderDelegateClass(functionProvider, initializationContext);
			replaceConsulterWithDeepConsulter(functionProvider, initializationContext);
			initLoadedClassesRetriever(functionProvider, initializationContext);
			initLoadedPackagesRetriever(functionProvider, initializationContext);
		} catch (Throwable exc) {
			throwException(
				new InitializeException(
					"Could not initiliaze " + this.getClass().getSimpleName(), exc
				)
			);
		}
	}
	
	//Initializers
	protected void initExceptionThrower(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		exceptionThrower = functionProvider.getOrBuildObject(
			ThrowExceptionFunction.class, initializationContext
		);
	}
	
	
	protected void initLoadedPackagesRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		loadedPackagesRetriever = functionProvider.getOrBuildObject(
			GetLoadedPackagesFunction.class, initializationContext
		);
	}

	
	protected void initLoadedClassesRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		loadedClassesRetriever = functionProvider.getOrBuildObject(
			GetLoadedClassesFunction.class, initializationContext
		);
	}

	
	protected void replaceConsulterWithDeepConsulter(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {	
		//this cast is necessary to avoid the incompatible types error (no unique maximal instance exists for type variable)
		consulterRetriever = (FunctionAdapter<?, Class<?>, MethodHandles.Lookup>)functionProvider.getOrBuildObject(
			DeepConsulterSupplyFunction.class, initializationContext
		);
	}

	
	protected void initClassLoaderDelegateClass(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		classLoaderDelegateClass = functionProvider.getOrBuildObject(
			ClassLoaderDelegateClassSupplier.class, initializationContext
		).get();
	}

	
	protected void initBuiltinClassLoaderClass(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		builtinClassLoaderClass = functionProvider.getOrBuildObject(
			BuiltinClassLoaderClassSupplier.class, initializationContext
		).get();
	}

	
	protected void initPackageRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		packageRetriever = functionProvider.getOrBuildObject(
			GetPackageFunction.class, initializationContext
		);
	}

	
	protected void initFieldValueSetter(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		fieldValueSetter = functionProvider.getOrBuildObject(
			SetFieldValueFunction.class, initializationContext
		);
	}

	
	protected void initFieldValueRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		fieldValueRetriever = functionProvider.getOrBuildObject(
			GetFieldValueFunction.class, initializationContext
		);
	}

	
	protected void initAllocateInstanceInvoker(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		allocateInstanceInvoker = functionProvider.getOrBuildObject(
			AllocateInstanceFunction.class, initializationContext
		);
	}

	
	protected void initMethodInvoker(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		methodInvoker = functionProvider.getOrBuildObject(
			MethodInvokeMethodHandleSupplier.class, initializationContext
		).get();
	}

	
	protected void initConstructorInvoker(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		constructorInvoker = functionProvider.getOrBuildObject(
			ConstructorInvokeMethodHandleSupplier.class, initializationContext
		).get();
	}

	
	protected void initAccessibleSetter(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		//this cast is necessary to avoid the incompatible types error (no unique maximal instance exists for type variable)
		accessibleSetter = (BiConsumerAdapter<?, AccessibleObject, Boolean>)functionProvider.getOrBuildObject(
			SetAccessibleFunction.class, initializationContext
		);
	}

	
	protected void initDeclaredConstructorsRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		declaredConstructorsRetriever = functionProvider.getOrBuildObject(
			GetDeclaredConstructorsFunction.class, initializationContext
		);
	}

	
	protected void initDeclaredMethodsRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		declaredMethodsRetriever = functionProvider.getOrBuildObject(
			GetDeclaredMethodsFunction.class, initializationContext
		);
	}

	
	protected void initDeclaredFieldsRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		declaredFieldsRetriever = functionProvider.getOrBuildObject(
			GetDeclaredFieldsFunction.class, initializationContext
		);
	}

	
	protected void initHookClassDefiner(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		hookClassDefiner = functionProvider.getOrBuildObject(
			DefineHookClassFunction.class, initializationContext
		);
	}

	
	protected void initConsulterRetriever(
		ObjectProvider functionProvider,
		Map<Object, Object> initializationContext
	) {	
		//this cast is necessary to avoid the incompatible types error (no unique maximal instance exists for type variable)
		consulterRetriever = (FunctionAdapter<?, Class<?>, MethodHandles.Lookup>)functionProvider.getOrBuildObject(
			ConsulterSupplyFunction.class, initializationContext
		);
	}
	
	
	//Exposed methods
	@Override
	public<T> T throwException(Object exceptionOrMessage, Object... placeHolderReplacements) {
		return exceptionThrower.apply(exceptionOrMessage, placeHolderReplacements);
	}
	
	
	@Override
	public void setAccessible(AccessibleObject object, boolean flag) {
		accessibleSetter.accept(object, flag);
	}

	
	@Override
	public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode) {
		return hookClassDefiner.apply(clientClass, byteCode);
	}

	
	@Override
	public Package getPackage(ClassLoader classLoader, String packageName) {
		return packageRetriever.apply(classLoader, packageName);
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
	public <T> T getFieldValue(Object target, Field field) {
		return (T)fieldValueRetriever.apply(target, field);
	}

	@Override
	public void setFieldValue(Object target, Field field, Object value) {
		fieldValueSetter.accept(target, field, value);
	}

	@Override
	public <T> T allocateInstance(Class<?> cls) {
		return (T)allocateInstanceInvoker.apply(cls);
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
	public Class<?> getClassLoaderDelegateClass() {
		return classLoaderDelegateClass;
	}

	@Override
	public Lookup getConsulter(Class<?> cls) {
		return consulterRetriever.apply(cls);
	}

	@Override
	public <T> T invoke(Method method, Object target, Object[] params) {
		try {
			return (T)methodInvoker.invoke(method, target, params);
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public <T> T newInstance(Constructor<T> ctor, Object[] params) {
		try {
			return (T)constructorInvoker.invoke(ctor, params);
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Field[] getDeclaredFields(Class<?> cls)  {
		try {
			return (Field[])declaredFieldsRetriever.apply(cls);
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls) {
		try {
			return (Constructor<T>[])declaredConstructorsRetriever.apply(cls);
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Method[] getDeclaredMethods(Class<?> cls) {
		try {
			return (Method[])declaredMethodsRetriever.apply(cls);
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}
	
	
	@Override
	public void close() {
		exceptionThrower = null;
		allocateInstanceInvoker = null;	
		fieldValueRetriever = null;
		fieldValueSetter = null;
		hookClassDefiner = null;
		consulterRetriever = null;
		declaredFieldsRetriever = null;
		declaredMethodsRetriever = null;
		declaredConstructorsRetriever = null;
		accessibleSetter = null;
		constructorInvoker = null;
		packageRetriever = null;
		methodInvoker = null;
		classLoaderDelegateClass = null;
		builtinClassLoaderClass = null;
		loadedClassesRetriever = null;
		loadedPackagesRetriever = null;	
	}
}
