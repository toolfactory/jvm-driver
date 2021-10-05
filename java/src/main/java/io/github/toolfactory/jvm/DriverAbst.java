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
public abstract class DriverAbst implements Driver {

	private ThrowExceptionFunction exceptionThrower;
	private Function<Class<?>, Object> allocateInstanceInvoker;
	private BiFunction<Object, Field, Object> fieldValueRetriever;
	private TriConsumer<Object, Field, Object> fieldValueSetter;
	private BiFunction<Class<?>, byte[], Class<?>> hookClassDefiner;
	private FunctionAdapter<?, Class<?>, MethodHandles.Lookup> consulterRetriever;
	private Function<Class<?>, Field[]> declaredFieldsRetriever;
	private Function<Class<?>, Method[]> declaredMethodsRetriever;
	private Function<Class<?>, Constructor<?>[]> declaredConstructorsRetriever;
	private BiConsumerAdapter<?, AccessibleObject, Boolean> accessibleSetter;
	private MethodHandle constructorInvoker;
	private BiFunction<ClassLoader, String, Package> packageRetriever;
	private MethodHandle methodInvoker;
	private Class<?> builtinClassLoaderClass;
	private Class<?> classLoaderDelegateClass;
	private Function<ClassLoader, Collection<Class<?>>> loadedClassesRetriever;
	private Function<ClassLoader, Map<String, ?>> loadedPackagesRetriever;

	public DriverAbst() {}

	
	<D extends Driver> D init() {
		ObjectProvider functionProvider = new ObjectProvider(
			Info.CRITICAL_VERSIONS
		);
		
		Map<Object, Object> initializationContext = new HashMap<>();
		
		initExceptionThrower(functionProvider, initializationContext);	
		try {
			exceptionThrower = initExceptionThrower(functionProvider, initializationContext);
			allocateInstanceInvoker = initAllocateInstanceInvoker(functionProvider, initializationContext);	
			fieldValueRetriever = initFieldValueRetriever(functionProvider, initializationContext);
			fieldValueSetter = initFieldValueSetter(functionProvider, initializationContext);
			hookClassDefiner = initHookClassDefiner(functionProvider, initializationContext);
			consulterRetriever = initConsulterRetriever(functionProvider, initializationContext);
			declaredFieldsRetriever = initDeclaredFieldsRetriever(functionProvider, initializationContext);
			declaredMethodsRetriever = initDeclaredMethodsRetriever(functionProvider, initializationContext);
			declaredConstructorsRetriever = initDeclaredConstructorsRetriever(functionProvider, initializationContext);
			accessibleSetter = initAccessibleSetter(functionProvider, initializationContext);
			constructorInvoker = initConstructorInvoker(functionProvider, initializationContext);
			methodInvoker = initMethodInvoker(functionProvider, initializationContext);
			packageRetriever = initPackageRetriever(functionProvider, initializationContext);		
			builtinClassLoaderClass = initBuiltinClassLoaderClass(functionProvider, initializationContext);	
			classLoaderDelegateClass = initClassLoaderDelegateClass(functionProvider, initializationContext);
			consulterRetriever = replaceConsulterWithDeepConsulter(functionProvider, initializationContext);
			loadedClassesRetriever = initLoadedClassesRetriever(functionProvider, initializationContext);
			loadedPackagesRetriever = initLoadedPackagesRetriever(functionProvider, initializationContext);
		} catch (Throwable exc) {
			throwException(
				new InitializeException(
					"Could not initiliaze " + this.getClass().getSimpleName(), exc
				)
			);
		}
		return (D)this;
	}

	protected abstract ThrowExceptionFunction initExceptionThrower(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract AllocateInstanceFunction initAllocateInstanceInvoker(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract GetFieldValueFunction initFieldValueRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract SetFieldValueFunction initFieldValueSetter(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract DefineHookClassFunction initHookClassDefiner(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract ConsulterSupplyFunction<?> initConsulterRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract GetDeclaredFieldsFunction initDeclaredFieldsRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract GetDeclaredMethodsFunction initDeclaredMethodsRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract GetDeclaredConstructorsFunction initDeclaredConstructorsRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract SetAccessibleFunction<?> initAccessibleSetter(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract MethodHandle initConstructorInvoker(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract MethodHandle initMethodInvoker(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract GetPackageFunction initPackageRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract Class<?> initBuiltinClassLoaderClass(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract Class<?> initClassLoaderDelegateClass(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract DeepConsulterSupplyFunction<?> replaceConsulterWithDeepConsulter(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	protected abstract GetLoadedClassesFunction initLoadedClassesRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext);
	
	protected abstract GetLoadedPackagesFunction initLoadedPackagesRetriever(ObjectProvider functionProvider, Map<Object, Object> initializationContext);

	
	@Override
	public <T> T throwException(Object exceptionOrMessage, Object... placeHolderReplacements) {
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
	public Field[] getDeclaredFields(Class<?> cls) {
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