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

import io.github.toolfactory.jvm.function._AllocateInstanceFunction;
import io.github.toolfactory.jvm.function._BuiltinClassLoaderClassSupplier;
import io.github.toolfactory.jvm.function._ClassLoaderDelegateClassSupplier;
import io.github.toolfactory.jvm.function._ConstructorInvokeMethodHandleSupplier;
import io.github.toolfactory.jvm.function._ConsulterSupplyFunction;
import io.github.toolfactory.jvm.function._DeepConsulterSupplyFunction;
import io.github.toolfactory.jvm.function._DefineHookClassFunction;
import io.github.toolfactory.jvm.function._GetDeclaredConstructorsMethodHandleSupplier;
import io.github.toolfactory.jvm.function._GetDeclaredFieldFunction;
import io.github.toolfactory.jvm.function._GetDeclaredFieldsMethodHandleSupplier;
import io.github.toolfactory.jvm.function._GetDeclaredMethodsMethodHandleSupplier;
import io.github.toolfactory.jvm.function._GetFieldValueFunction;
import io.github.toolfactory.jvm.function._GetLoadedClassesFunction;
import io.github.toolfactory.jvm.function._GetLoadedPackagesFunction;
import io.github.toolfactory.jvm.function._GetPackageFunction;
import io.github.toolfactory.jvm.function._MethodInvokeMethodHandleSupplier;
import io.github.toolfactory.jvm.function._SetAccessibleFunction;
import io.github.toolfactory.jvm.function._SetFieldValueFunction;
import io.github.toolfactory.jvm.function._ThrowExceptionFunction;



@SuppressWarnings("unchecked")
public class DefaultDriver implements Driver {	
	
	_ThrowExceptionFunction exceptionThrower; 
	Function<Class<?>, Object> allocateInstanceInvoker;	
	BiFunction<Object, Field, Object> fieldValueRetriever;
	TriConsumer<Object, Field, Object> fieldValueSetter;
	BiFunction<Class<?>, byte[], Class<?>> hookClassDefiner;
	FunctionAdapter<?, Class<?>, MethodHandles.Lookup> consulterRetriever;
	MethodHandle declaredFieldsRetriever;
	MethodHandle declaredMethodsRetriever;
	MethodHandle declaredConstructorsRetriever;
	BiFunction<Class<?>, String, Field> declaredFieldRetriever;
	BiConsumerAdapter<?, AccessibleObject, Boolean> accessibleSetter;
	MethodHandle constructorInvoker;
	BiFunction<ClassLoader, String, Package> packageRetriever;
	MethodHandle methodInvoker;
	Class<?> builtinClassLoaderClass;
	Class<?> classLoaderDelegateClass;
	Function<ClassLoader, Collection<Class<?>>> loadedClassesRetriever;
	Function<ClassLoader, Map<String, ?>> loadedPackagesRetriever;	


	public DefaultDriver() {
		FunctionProvider functionProvider =
			new FunctionProvider(
				"ForJava", 7, 9, 14, 17
			);
		Map<Object, Object> initializationContext = new HashMap<>();
		initExceptionThrower(functionProvider, initializationContext);	
		initAllocateInstanceInvoker(functionProvider, initializationContext);	
		initFieldValueRetriever(functionProvider, initializationContext);
		initFieldValueSetter(functionProvider, initializationContext);
		initHookClassDefiner(functionProvider, initializationContext);
		initConsulterRetriever(functionProvider, initializationContext);
		initDeclaredFieldsRetriever(functionProvider, initializationContext);
		initDeclaredMethodsRetriever(functionProvider, initializationContext);
		initDeclaredConstructorsRetriever(functionProvider, initializationContext);
		initDeclaredFieldRetriever(functionProvider, initializationContext);
		initAccessibleSetter(functionProvider, initializationContext);
		initConstructorInvoker(functionProvider, initializationContext);
		initMethodInvoker(functionProvider, initializationContext);
		initPackageRetriever(functionProvider, initializationContext);		
		initBuiltinClassLoaderClass(functionProvider, initializationContext);	
		initClassLoaderDelegateClass(functionProvider, initializationContext);
		replaceConsulterWithDeepConsulter(functionProvider, initializationContext);
		initLoadedClassesRetriever(functionProvider, initializationContext);
		initLoadedPackagesRetriever(functionProvider, initializationContext);
	}
	
	
	void initExceptionThrower(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		exceptionThrower = functionProvider.getFunctionAdapter(
			_ThrowExceptionFunction.class, initializationContext
		);
	}
	
	
	void initLoadedPackagesRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		loadedPackagesRetriever = functionProvider.getFunctionAdapter(
			_GetLoadedPackagesFunction.class, initializationContext
		);
	}

	
	void initLoadedClassesRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		loadedClassesRetriever = functionProvider.getFunctionAdapter(
			_GetLoadedClassesFunction.class, initializationContext
		);
	}

	
	void replaceConsulterWithDeepConsulter(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {	
		//this cast is necessary to avoid the incompatible types error (no unique maximal instance exists for type variable)
		consulterRetriever = (FunctionAdapter<?, Class<?>, MethodHandles.Lookup>)functionProvider.getFunctionAdapter(
			_DeepConsulterSupplyFunction.class, initializationContext
		);
	}

	
	void initClassLoaderDelegateClass(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		classLoaderDelegateClass = functionProvider.getFunctionAdapter(
			_ClassLoaderDelegateClassSupplier.class, initializationContext
		).get();
	}

	
	void initBuiltinClassLoaderClass(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		builtinClassLoaderClass = functionProvider.getFunctionAdapter(
			_BuiltinClassLoaderClassSupplier.class, initializationContext
		).get();
	}

	
	void initPackageRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		packageRetriever = functionProvider.getFunctionAdapter(
			_GetPackageFunction.class, initializationContext
		);
	}

	
	void initFieldValueSetter(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		fieldValueSetter = functionProvider.getFunctionAdapter(
			_SetFieldValueFunction.class, initializationContext
		);
	}

	
	void initFieldValueRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		fieldValueRetriever = functionProvider.getFunctionAdapter(
			_GetFieldValueFunction.class, initializationContext
		);
	}

	
	void initAllocateInstanceInvoker(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		allocateInstanceInvoker = functionProvider.getFunctionAdapter(
			_AllocateInstanceFunction.class, initializationContext
		);
	}

	
	void initMethodInvoker(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		methodInvoker = functionProvider.getFunctionAdapter(
			_MethodInvokeMethodHandleSupplier.class, initializationContext
		).get();
	}

	
	void initConstructorInvoker(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		constructorInvoker = functionProvider.getFunctionAdapter(
			_ConstructorInvokeMethodHandleSupplier.class, initializationContext
		).get();
	}

	
	void initAccessibleSetter(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		//this cast is necessary to avoid the incompatible types error (no unique maximal instance exists for type variable)
		accessibleSetter = (BiConsumerAdapter<?, AccessibleObject, Boolean>)functionProvider.getFunctionAdapter(
			_SetAccessibleFunction.class, initializationContext
		);
	}

	
	void initDeclaredFieldRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		declaredFieldRetriever = functionProvider.getFunctionAdapter(
			_GetDeclaredFieldFunction.class, initializationContext
		);
	}

	
	void initDeclaredConstructorsRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		declaredConstructorsRetriever = functionProvider.getFunctionAdapter(
			_GetDeclaredConstructorsMethodHandleSupplier.class, initializationContext
		).get();
	}

	
	void initDeclaredMethodsRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		declaredMethodsRetriever = functionProvider.getFunctionAdapter(
			_GetDeclaredMethodsMethodHandleSupplier.class, initializationContext
		).get();
	}

	
	void initDeclaredFieldsRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		declaredFieldsRetriever = functionProvider.getFunctionAdapter(
			_GetDeclaredFieldsMethodHandleSupplier.class, initializationContext
		).get();
	}

	
	void initHookClassDefiner(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {
		hookClassDefiner = functionProvider.getFunctionAdapter(
			_DefineHookClassFunction.class, initializationContext
		);
	}

	
	void initConsulterRetriever(
		FunctionProvider functionProvider,
		Map<Object, Object> initializationContext
	) {	
		//this cast is necessary to avoid the incompatible types error (no unique maximal instance exists for type variable)
		consulterRetriever = (FunctionAdapter<?, Class<?>, MethodHandles.Lookup>)functionProvider.getFunctionAdapter(
			_ConsulterSupplyFunction.class, initializationContext
		);
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
	public Field getDeclaredField(Class<?> cls, String name) {
		return declaredFieldRetriever.apply(cls, name);
	}

	@Override
	public Field[] getDeclaredFields(Class<?> cls)  {
		try {
			return (Field[])declaredFieldsRetriever.invoke(cls, false);
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls) {
		try {
			return (Constructor<T>[])declaredConstructorsRetriever.invoke(cls, false);
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Method[] getDeclaredMethods(Class<?> cls) {
		try {
			return (Method[])declaredMethodsRetriever.invoke(cls, false);
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}
	
	@Override
	public<T> T throwException(Object exceptionOrMessage, Object... placeHolderReplacements) {
		return exceptionThrower.apply(exceptionOrMessage, placeHolderReplacements);
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
		declaredFieldRetriever = null;
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
