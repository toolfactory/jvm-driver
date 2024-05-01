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
package io.github.toolfactory.jvm;


import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.github.toolfactory.jvm.function.catalog.AllocateInstanceFunction;
import io.github.toolfactory.jvm.function.catalog.BuiltinClassLoaderClassSupplier;
import io.github.toolfactory.jvm.function.catalog.ClassLoaderDelegateClassSupplier;
import io.github.toolfactory.jvm.function.catalog.ConstructorInvokeFunction;
import io.github.toolfactory.jvm.function.catalog.ConsulterSupplyFunction;
import io.github.toolfactory.jvm.function.catalog.ConvertToBuiltinClassLoaderFunction;
import io.github.toolfactory.jvm.function.catalog.DeepConsulterSupplyFunction;
import io.github.toolfactory.jvm.function.catalog.DefineHookClassFunction;
import io.github.toolfactory.jvm.function.catalog.GetClassByNameFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredConstructorsFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredFieldsFunction;
import io.github.toolfactory.jvm.function.catalog.GetDeclaredMethodsFunction;
import io.github.toolfactory.jvm.function.catalog.GetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedClassesRetrieverFunction;
import io.github.toolfactory.jvm.function.catalog.GetLoadedPackagesFunction;
import io.github.toolfactory.jvm.function.catalog.GetPackageFunction;
import io.github.toolfactory.jvm.function.catalog.GetResourcesFunction;
import io.github.toolfactory.jvm.function.catalog.MethodInvokeFunction;
import io.github.toolfactory.jvm.function.catalog.SetAccessibleFunction;
import io.github.toolfactory.jvm.function.catalog.SetFieldValueFunction;
import io.github.toolfactory.jvm.function.catalog.StopThreadFunction;
import io.github.toolfactory.jvm.function.catalog.ThrowExceptionFunction;
import io.github.toolfactory.jvm.function.template.BiFunction;
import io.github.toolfactory.jvm.function.template.Function;
import io.github.toolfactory.jvm.function.template.Supplier;
import io.github.toolfactory.jvm.function.template.ThrowingBiConsumer;
import io.github.toolfactory.jvm.function.template.ThrowingBiFunction;
import io.github.toolfactory.jvm.function.template.ThrowingFunction;
import io.github.toolfactory.jvm.function.template.ThrowingQuadFunction;
import io.github.toolfactory.jvm.function.template.ThrowingTriFunction;
import io.github.toolfactory.jvm.function.template.TriConsumer;
import io.github.toolfactory.jvm.util.CleanableSupplier;
import io.github.toolfactory.jvm.util.ObjectProvider;


@SuppressWarnings({"unchecked"})
public abstract class DriverAbst implements Driver {

	protected ThrowExceptionFunction exceptionThrower;
	protected ThrowingFunction<Class<?>, Object, Throwable> allocateInstanceInvoker;
	protected BiFunction<Object, Field, Object> fieldValueRetriever;
	protected TriConsumer<Object, Field, Object> fieldValueSetter;
	protected ThrowingBiFunction<Class<?>, byte[], Class<?>, Throwable> hookClassDefiner;
	protected ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable> consulterRetriever;
	protected ThrowingFunction<Class<?>, Field[], Throwable> declaredFieldsRetriever;
	protected ThrowingFunction<Class<?>, Method[], Throwable> declaredMethodsRetriever;
	protected ThrowingFunction<Class<?>, Constructor<?>[], Throwable> declaredConstructorsRetriever;
	protected ThrowingBiConsumer<AccessibleObject, Boolean, Throwable> accessibleSetter;
	protected ThrowingBiFunction<Constructor<?>, Object[], Object, Throwable> constructorInvoker;
	protected ThrowingBiFunction<ClassLoader, String, Package, Throwable> packageRetriever;
	protected ThrowingTriFunction<Method, Object, Object[], Object, Throwable> methodInvoker;
	protected ThrowingQuadFunction<String, Boolean, ClassLoader, Class<?>, Class<?>, Throwable> classByNameRetriever;
	protected GetResourcesFunction resourcesRetriver;
	protected Supplier<Class<?>> builtinClassLoaderClassSupplier;
	protected Supplier<Class<?>> classLoaderDelegateClassSupplier;
	protected Function<ClassLoader, CleanableSupplier<Collection<Class<?>>>> loadedClassesRetrieverSupplier;
	protected Function<ClassLoader, Map<String, ?>> loadedPackagesRetriever;
	protected ThrowingFunction<ClassLoader, ClassLoader, Throwable> classLoaderToBuiltinClassLoaderConverter;
	protected ThrowingBiConsumer<Thread, Throwable, Throwable> threadStopper;


	@Override
	public <D extends Driver> D init() {
		Map<Object, Object> initializationContext = functionsToMap();
		try {
			if (exceptionThrower == null) {
				exceptionThrower = getOrBuildExceptionThrower(initializationContext);
			}
			if (allocateInstanceInvoker == null) {
				allocateInstanceInvoker = getOrBuildAllocateInstanceInvoker(initializationContext);
			}
			if (fieldValueRetriever == null) {
				fieldValueRetriever = getOrBuildFieldValueRetriever(initializationContext);
			}
			if (fieldValueSetter == null) {
				fieldValueSetter = getOrBuildFieldValueSetter(initializationContext);
			}
			if (hookClassDefiner == null) {
				hookClassDefiner = getOrBuildHookClassDefiner(initializationContext);
			}

			if (declaredFieldsRetriever == null) {
				declaredFieldsRetriever = getOrBuildDeclaredFieldsRetriever(initializationContext);
			}
			if (declaredMethodsRetriever == null) {
				declaredMethodsRetriever = getOrBuildDeclaredMethodsRetriever(initializationContext);
			}
			if (declaredConstructorsRetriever == null) {
				declaredConstructorsRetriever = getOrBuildDeclaredConstructorsRetriever(initializationContext);
			}
			if (accessibleSetter == null) {
				accessibleSetter = getOrBuildAccessibleSetter(initializationContext);
			}
			if (constructorInvoker == null) {
				constructorInvoker = getOrBuildConstructorInvoker(initializationContext);
			}

			if (methodInvoker == null) {
				methodInvoker = getOrBuildMethodInvoker(initializationContext);
			}
			if (packageRetriever == null) {
				packageRetriever = getOrBuildPackageRetriever(initializationContext);
			}
			if (classByNameRetriever == null) {
				classByNameRetriever = getOrBuildClassByNameRetriever(initializationContext);
			}
			if (resourcesRetriver == null) {
				resourcesRetriver = getOrBuildResourcesRetriever(initializationContext);
			}
			if (builtinClassLoaderClassSupplier == null) {
				builtinClassLoaderClassSupplier = getOrBuildBuiltinClassLoaderClassSupplier(initializationContext);
			}

			if (classLoaderDelegateClassSupplier == null) {
				classLoaderDelegateClassSupplier = getOrBuildClassLoaderDelegateClassSupplier(initializationContext);
			}
			if (consulterRetriever == null) {
				consulterRetriever = getOrBuildDeepConsulterRetriever(initializationContext);
			}
			if (loadedClassesRetrieverSupplier == null) {
				loadedClassesRetrieverSupplier = getOrBuildLoadedClassesRetrieverFunction(initializationContext);
			}
			if (loadedPackagesRetriever == null) {
				loadedPackagesRetriever = getOrBuildLoadedPackagesRetriever(initializationContext);
			}
			if (classLoaderToBuiltinClassLoaderConverter == null) {
				classLoaderToBuiltinClassLoaderConverter = getOrBuildClassLoaderToBuiltinClassLoaderConverter(initializationContext);
			}

			if (threadStopper == null) {
				threadStopper = getOrBuildThreadStopper(initializationContext);
			}
		} catch (Throwable exc) {
			throwException(
				new InitializeException(
					"Could not initialize " + this.getClass().getSimpleName(), exc
				)
			);
		}
		return (D)this;
	}


	protected <D extends Driver> D refresh(Map<Object, Object> initializationContext) {
		try {
			if (exceptionThrower == null) {
				exceptionThrower = getExceptionThrower(initializationContext);
			}
			if (allocateInstanceInvoker == null) {
				allocateInstanceInvoker = getAllocateInstanceInvoker(initializationContext);
			}
			if (fieldValueRetriever == null) {
				fieldValueRetriever = getFieldValueRetriever(initializationContext);
			}
			if (fieldValueSetter == null) {
				fieldValueSetter = getFieldValueSetter(initializationContext);
			}
			if (hookClassDefiner == null) {
				hookClassDefiner = getHookClassDefiner(initializationContext);
			}

			if (declaredFieldsRetriever == null) {
				declaredFieldsRetriever = getDeclaredFieldsRetriever(initializationContext);
			}
			if (declaredMethodsRetriever == null) {
				declaredMethodsRetriever = getDeclaredMethodsRetriever(initializationContext);
			}
			if (declaredConstructorsRetriever == null) {
				declaredConstructorsRetriever = getDeclaredConstructorsRetriever(initializationContext);
			}
			if (accessibleSetter == null) {
				accessibleSetter = getAccessibleSetter(initializationContext);
			}
			if (constructorInvoker == null) {
				constructorInvoker = getConstructorInvoker(initializationContext);
			}

			if (methodInvoker == null) {
				methodInvoker = getMethodInvoker(initializationContext);
			}
			if (packageRetriever == null) {
				packageRetriever = getPackageRetriever(initializationContext);
			}
			if (classByNameRetriever == null) {
				classByNameRetriever = getOrBuildClassByNameRetriever(initializationContext);
			}
			if (resourcesRetriver == null) {
				resourcesRetriver = getOrBuildResourcesRetriever(initializationContext);
			}
			if (builtinClassLoaderClassSupplier == null) {
				builtinClassLoaderClassSupplier = getBuiltinClassLoaderClassSupplier(initializationContext);
			}

			if (classLoaderDelegateClassSupplier == null) {
				classLoaderDelegateClassSupplier = getClassLoaderDelegateClassSupplier(initializationContext);
			}
			if (consulterRetriever == null) {
				consulterRetriever = getDeepConsulterRetriever(initializationContext);
			}
			if (loadedClassesRetrieverSupplier == null) {
				loadedClassesRetrieverSupplier = getLoadedClassesRetrieverFunction(initializationContext);
			}
			if (loadedPackagesRetriever == null) {
				loadedPackagesRetriever = getLoadedPackagesRetriever(initializationContext);
			}
			if (classLoaderToBuiltinClassLoaderConverter == null) {
				classLoaderToBuiltinClassLoaderConverter = getClassLoaderToBuiltinClassLoaderConverter(initializationContext);
			}
			if (threadStopper == null) {
				threadStopper = getThreadStopper(initializationContext);
			}
			putNewObjectProviderIfAbsent(initializationContext);
		} catch (Throwable exc) {
			throwException(
				new InitializeException(
					"Could not initialize " + this.getClass().getSimpleName(), exc
				)
			);
		}
		return (D)this;
	}


	protected Map<Object, Object> functionsToMap() {
		Map<Object, Object> initializationContext = new HashMap<>();
		putIfNotNull(initializationContext, getThrowExceptionFunctionClass(), exceptionThrower);
		putIfNotNull(initializationContext, getAllocateInstanceFunctionClass(), allocateInstanceInvoker);
		putIfNotNull(initializationContext, getGetFieldValueFunctionClass(), fieldValueRetriever);
		putIfNotNull(initializationContext, getSetFieldValueFunctionClass(), fieldValueSetter);
		putIfNotNull(initializationContext, getDefineHookClassFunctionClass(), hookClassDefiner);
		putIfNotNull(initializationContext, getGetDeclaredFieldsFunctionClass(), declaredFieldsRetriever);
		putIfNotNull(initializationContext, getGetDeclaredMethodsFunctionClass(), declaredMethodsRetriever);
		putIfNotNull(initializationContext, getGetDeclaredConstructorsFunctionClass(), declaredConstructorsRetriever);
		putIfNotNull(initializationContext, getSetAccessibleFunctionClass(), accessibleSetter);
		putIfNotNull(initializationContext, getConstructorInvokeFunctionClass(), constructorInvoker);
		putIfNotNull(initializationContext, getMethodInvokeFunctionClass(), methodInvoker);
		putIfNotNull(initializationContext, getGetPackageFunctionClass(), packageRetriever);
		putIfNotNull(initializationContext, getGetClassByNameFunctionClass(), classByNameRetriever);
		putIfNotNull(initializationContext, getGetResourcesFunctionClass(), resourcesRetriver);
		putIfNotNull(initializationContext, getBuiltinClassLoaderClassSupplierClass(), builtinClassLoaderClassSupplier);
		putIfNotNull(initializationContext, getClassLoaderDelegateClassSupplierClass(), classLoaderDelegateClassSupplier);
		putIfNotNull(initializationContext, getDeepConsulterSupplyFunctionClass(),  consulterRetriever);
		putIfNotNull(initializationContext, getGetLoadedClassesRetrieverFunctionClass(), loadedClassesRetrieverSupplier);
		putIfNotNull(initializationContext, getGetLoadedPackagesFunctionClass(), loadedPackagesRetriever);
		putIfNotNull(initializationContext, getConvertToBuiltinClassLoaderFunctionClass(), classLoaderToBuiltinClassLoaderConverter);
		putIfNotNull(initializationContext, getStopThreadFunctionClass(), threadStopper);
		putNewObjectProviderIfAbsent(initializationContext);
		return initializationContext;
	}


	protected ObjectProvider putNewObjectProviderIfAbsent(Map<Object, Object> context) {
		ObjectProvider.putIfAbsent(context, new Supplier<ObjectProvider>() {
			@Override
			public ObjectProvider get() {
				return new ObjectProvider(
					Info.CRITICAL_VERSIONS
				);
			}
		});
		return ObjectProvider.get(context);
	}

	protected abstract Class<? extends ThrowExceptionFunction> getThrowExceptionFunctionClass();

	protected abstract Class<? extends AllocateInstanceFunction> getAllocateInstanceFunctionClass();

	protected abstract Class<? extends GetFieldValueFunction> getGetFieldValueFunctionClass();

	protected abstract Class<? extends SetFieldValueFunction> getSetFieldValueFunctionClass();

	protected abstract Class<? extends DefineHookClassFunction> getDefineHookClassFunctionClass();

	protected abstract Class<? extends ConsulterSupplyFunction> getConsulterSupplyFunctionClass();

	protected abstract Class<? extends GetDeclaredFieldsFunction> getGetDeclaredFieldsFunctionClass();

	protected abstract Class<? extends GetDeclaredMethodsFunction> getGetDeclaredMethodsFunctionClass();

	protected abstract Class<? extends GetDeclaredConstructorsFunction> getGetDeclaredConstructorsFunctionClass();

	protected abstract Class<? extends SetAccessibleFunction> getSetAccessibleFunctionClass();

	protected abstract Class<? extends ConstructorInvokeFunction> getConstructorInvokeFunctionClass();

	protected abstract Class<? extends MethodInvokeFunction> getMethodInvokeFunctionClass();

	protected abstract Class<? extends GetPackageFunction> getGetPackageFunctionClass();

	protected abstract Class<? extends GetClassByNameFunction>  getGetClassByNameFunctionClass();

	protected abstract Class<? extends GetResourcesFunction> getGetResourcesFunctionClass();

	protected abstract Class<? extends BuiltinClassLoaderClassSupplier> getBuiltinClassLoaderClassSupplierClass();

	protected abstract Class<? extends ClassLoaderDelegateClassSupplier> getClassLoaderDelegateClassSupplierClass();

	protected abstract Class<? extends DeepConsulterSupplyFunction> getDeepConsulterSupplyFunctionClass();

	protected abstract Class<? extends GetLoadedClassesRetrieverFunction> getGetLoadedClassesRetrieverFunctionClass();

	protected abstract Class<? extends GetLoadedPackagesFunction> getGetLoadedPackagesFunctionClass();

	protected abstract Class<? extends ConvertToBuiltinClassLoaderFunction> getConvertToBuiltinClassLoaderFunctionClass();

	protected abstract Class<? extends StopThreadFunction> getStopThreadFunctionClass();


	protected ThrowExceptionFunction getOrBuildExceptionThrower(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getThrowExceptionFunctionClass(), initializationContext
		);
	}

	protected AllocateInstanceFunction getOrBuildAllocateInstanceInvoker(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getAllocateInstanceFunctionClass(), initializationContext
		);
	}

	protected GetFieldValueFunction getOrBuildFieldValueRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getGetFieldValueFunctionClass(), initializationContext
		);
	}

	protected SetFieldValueFunction getOrBuildFieldValueSetter(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getSetFieldValueFunctionClass(), initializationContext
		);
	}

	protected DefineHookClassFunction getOrBuildHookClassDefiner(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getDefineHookClassFunctionClass(), initializationContext
		);
	}

	protected ConsulterSupplyFunction getOrBuildConsulterRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getConsulterSupplyFunctionClass(), initializationContext
		);
	}

	protected GetDeclaredFieldsFunction getOrBuildDeclaredFieldsRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getGetDeclaredFieldsFunctionClass(), initializationContext
		);
	}

	protected GetDeclaredMethodsFunction getOrBuildDeclaredMethodsRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getGetDeclaredMethodsFunctionClass(), initializationContext
		);
	}

	protected GetDeclaredConstructorsFunction getOrBuildDeclaredConstructorsRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getGetDeclaredConstructorsFunctionClass(), initializationContext
		);
	}

	protected SetAccessibleFunction getOrBuildAccessibleSetter(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getSetAccessibleFunctionClass(), initializationContext
		);
	}

	protected ConstructorInvokeFunction getOrBuildConstructorInvoker(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getConstructorInvokeFunctionClass(), initializationContext
		);
	}

	protected MethodInvokeFunction getOrBuildMethodInvoker(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getMethodInvokeFunctionClass(), initializationContext
		);
	}

	protected GetPackageFunction getOrBuildPackageRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getGetPackageFunctionClass(), initializationContext
		);
	}

	protected GetClassByNameFunction getOrBuildClassByNameRetriever(
			Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getGetClassByNameFunctionClass(), initializationContext
		);
	}

	protected GetResourcesFunction getOrBuildResourcesRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getGetResourcesFunctionClass(), initializationContext
		);
	}

	protected BuiltinClassLoaderClassSupplier getOrBuildBuiltinClassLoaderClassSupplier(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getBuiltinClassLoaderClassSupplierClass(), initializationContext
		);
	}

	protected ClassLoaderDelegateClassSupplier getOrBuildClassLoaderDelegateClassSupplier(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getClassLoaderDelegateClassSupplierClass(), initializationContext
		);
	}

	protected DeepConsulterSupplyFunction getOrBuildDeepConsulterRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getDeepConsulterSupplyFunctionClass(), initializationContext
		);
	}

	protected GetLoadedClassesRetrieverFunction getOrBuildLoadedClassesRetrieverFunction(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getGetLoadedClassesRetrieverFunctionClass(), initializationContext
		);
	}

	protected GetLoadedPackagesFunction getOrBuildLoadedPackagesRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getGetLoadedPackagesFunctionClass(), initializationContext
		);
	}

	protected ConvertToBuiltinClassLoaderFunction getOrBuildClassLoaderToBuiltinClassLoaderConverter(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getConvertToBuiltinClassLoaderFunctionClass(), initializationContext
		);
	}

	protected StopThreadFunction getOrBuildThreadStopper(Map<Object, Object> initializationContext) {
		return ObjectProvider.get(initializationContext).getOrBuildObject(
			getStopThreadFunctionClass(), initializationContext
		);
	}

//
	protected ThrowExceptionFunction getExceptionThrower(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getThrowExceptionFunctionClass(), initializationContext
		);
	}

	protected AllocateInstanceFunction getAllocateInstanceInvoker(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getAllocateInstanceFunctionClass(), initializationContext
		);
	}

	protected GetFieldValueFunction getFieldValueRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getGetFieldValueFunctionClass(), initializationContext
		);
	}

	protected SetFieldValueFunction getFieldValueSetter(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getSetFieldValueFunctionClass(), initializationContext
		);
	}

	protected DefineHookClassFunction getHookClassDefiner(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getDefineHookClassFunctionClass(), initializationContext
		);
	}

	protected ConsulterSupplyFunction getConsulterRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getConsulterSupplyFunctionClass(), initializationContext
		);
	}

	protected GetDeclaredFieldsFunction getDeclaredFieldsRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getGetDeclaredFieldsFunctionClass(), initializationContext
		);
	}

	protected GetDeclaredMethodsFunction getDeclaredMethodsRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getGetDeclaredMethodsFunctionClass(), initializationContext
		);
	}

	protected GetDeclaredConstructorsFunction getDeclaredConstructorsRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getGetDeclaredConstructorsFunctionClass(), initializationContext
		);
	}

	protected SetAccessibleFunction getAccessibleSetter(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getSetAccessibleFunctionClass(), initializationContext
		);
	}

	protected ConstructorInvokeFunction getConstructorInvoker(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getConstructorInvokeFunctionClass(), initializationContext
		);
	}

	protected MethodInvokeFunction getMethodInvoker(Map<Object, Object> initializationContext) {
		return  ObjectProvider.getObject(
			getMethodInvokeFunctionClass(), initializationContext
		);
	}

	protected GetPackageFunction getPackageRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getGetPackageFunctionClass(), initializationContext
		);
	}

	protected GetClassByNameFunction getClassByNameRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getGetClassByNameFunctionClass(), initializationContext
		);
	}

	protected GetResourcesFunction getResourcesRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getGetResourcesFunctionClass(), initializationContext
		);
	}

	protected BuiltinClassLoaderClassSupplier getBuiltinClassLoaderClassSupplier(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getBuiltinClassLoaderClassSupplierClass(), initializationContext
		);
	}

	protected ClassLoaderDelegateClassSupplier getClassLoaderDelegateClassSupplier(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getClassLoaderDelegateClassSupplierClass(), initializationContext
		);
	}

	protected DeepConsulterSupplyFunction getDeepConsulterRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getDeepConsulterSupplyFunctionClass(), initializationContext
		);
	}

	protected GetLoadedClassesRetrieverFunction getLoadedClassesRetrieverFunction(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getGetLoadedClassesRetrieverFunctionClass(), initializationContext
		);
	}

	protected GetLoadedPackagesFunction getLoadedPackagesRetriever(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getGetLoadedPackagesFunctionClass(), initializationContext
		);
	}

	protected ConvertToBuiltinClassLoaderFunction getClassLoaderToBuiltinClassLoaderConverter(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getConvertToBuiltinClassLoaderFunctionClass(), initializationContext
		);
	}

	protected StopThreadFunction getThreadStopper(Map<Object, Object> initializationContext) {
		return ObjectProvider.getObject(
			getStopThreadFunctionClass(), initializationContext
		);
	}


	protected void putIfNotNull(Map<Object, Object> map, Class<?> cls, Object object) {
		if (object != null) {
			map.put(cls.getName(), object);
		}
	}


	@Override
	public <T> T throwException(Throwable exception) {
		ThrowExceptionFunction exceptionThrower = this.exceptionThrower;
		try {
			return exceptionThrower.apply(exception);
		} catch (NullPointerException exc) {
			if (exceptionThrower != null) {
				throw exc;
			}
			synchronized (this) {
				if (this.exceptionThrower == null) {
					Map<Object, Object> initContext = functionsToMap();
					this.exceptionThrower = getOrBuildExceptionThrower(initContext);
					refresh(initContext);
				}
			}
			return this.exceptionThrower.apply(exception);
		}
	}

	@Override
	public <T> T throwException(String message, Object... placeHolderReplacements) {
		ThrowExceptionFunction exceptionThrower = this.exceptionThrower;
		try {
			return exceptionThrower.apply(3, message, placeHolderReplacements);
		} catch (NullPointerException exc) {
			if (exceptionThrower != null) {
				throw exc;
			}
			synchronized (this) {
				if (this.exceptionThrower == null) {
					Map<Object, Object> initContext = functionsToMap();
					this.exceptionThrower = getOrBuildExceptionThrower(initContext);
					refresh(initContext);
				}
			}
			return this.exceptionThrower.apply(3, message, placeHolderReplacements);
		}
	}

	@Override
	public void setAccessible(AccessibleObject object, boolean flag) {
		try {
			ThrowingBiConsumer<AccessibleObject, Boolean, Throwable> accessibleSetter = this.accessibleSetter;
			try {
				accessibleSetter.accept(object, flag);
			} catch (NullPointerException exc) {
				if (accessibleSetter != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.accessibleSetter == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.accessibleSetter = getOrBuildAccessibleSetter(initContext);
						refresh(initContext);
					}
				}
				this.accessibleSetter.accept(object, flag);
			}
		} catch (Throwable exc) {
			throwException(exc);
		}
	}

	@Override
	public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode) {
		try {
			ThrowingBiFunction<Class<?>, byte[], Class<?>, Throwable> hookClassDefiner = this.hookClassDefiner;
			try {
				return hookClassDefiner.apply(clientClass, byteCode);
			} catch (NullPointerException exc) {
				if (hookClassDefiner != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.hookClassDefiner == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.hookClassDefiner = getOrBuildHookClassDefiner(initContext);
						refresh(initContext);
					}
				}
				return this.hookClassDefiner.apply(clientClass, byteCode);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Package getPackage(ClassLoader classLoader, String packageName) {
		try {
			ThrowingBiFunction<ClassLoader, String, Package, Throwable> packageRetriever = this.packageRetriever;
			try {
				return packageRetriever.apply(classLoader, packageName);
			} catch (NullPointerException exc) {
				if (packageRetriever != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.packageRetriever == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.packageRetriever = getOrBuildPackageRetriever(initContext);
						refresh(initContext);
					}
				}
				return this.packageRetriever.apply(classLoader, packageName);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public CleanableSupplier<Collection<Class<?>>> getLoadedClassesRetriever(ClassLoader classLoader) {
		Function<ClassLoader, CleanableSupplier<Collection<Class<?>>>> loadedClassesRetrieverSupplier = this.loadedClassesRetrieverSupplier;
		try {
			return loadedClassesRetrieverSupplier.apply(classLoader);
		} catch (NullPointerException exc) {
			if (loadedClassesRetrieverSupplier != null) {
				throw exc;
			}
			synchronized (this) {
				if (this.loadedClassesRetrieverSupplier == null) {
					Map<Object, Object> initContext = functionsToMap();
					this.loadedClassesRetrieverSupplier = getOrBuildLoadedClassesRetrieverFunction(initContext);
					refresh(initContext);
				}
			}

			return this.loadedClassesRetrieverSupplier.apply(classLoader);
		}
	}

	@Override
	public Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader) {
		Function<ClassLoader, Map<String, ?>> loadedPackagesRetriever = this.loadedPackagesRetriever;
		try {
			return loadedPackagesRetriever.apply(classLoader);
		} catch (NullPointerException exc) {
			if (loadedPackagesRetriever != null) {
				throw exc;
			}
			synchronized (this) {
				if (this.loadedPackagesRetriever == null) {
					Map<Object, Object> initContext = functionsToMap();
					this.loadedPackagesRetriever = getOrBuildLoadedPackagesRetriever(initContext);
					refresh(initContext);
				}
			}
			return this.loadedPackagesRetriever.apply(classLoader);
		}
	}

	@Override
	public <T> T getFieldValue(Object target, Field field) {
		BiFunction<Object, Field, Object> fieldValueRetriever = this.fieldValueRetriever;
		if (target == null && !Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("Target cannot be null when the field is not static");
		}
		try {
			return (T)fieldValueRetriever.apply(target, field);
		} catch (NullPointerException exc) {
			if (fieldValueRetriever != null) {
				throw exc;
			}
			synchronized (this) {
				if (this.fieldValueRetriever == null) {
					Map<Object, Object> initContext = functionsToMap();
					this.fieldValueRetriever = getOrBuildFieldValueRetriever(initContext);
					refresh(initContext);
				}
			}
			return (T)this.fieldValueRetriever.apply(target, field);
		}
	}

	@Override
	public void setFieldValue(Object target, Field field, Object value) {
		TriConsumer<Object, Field, Object> fieldValueSetter = this.fieldValueSetter;
		if (target == null && !Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("Target cannot be null when the field is not static");
		}
		try {
			fieldValueSetter.accept(target, field, value);
		} catch (NullPointerException exc) {
			if (fieldValueSetter != null) {
				throw exc;
			}
			synchronized (this) {
				if (this.fieldValueSetter == null) {
					Map<Object, Object> initContext = functionsToMap();
					this.fieldValueSetter = getOrBuildFieldValueSetter(initContext);
					refresh(initContext);
				}
			}
			this.fieldValueSetter.accept(target, field, value);
		}
	}

	@Override
	public <T> T allocateInstance(Class<?> cls) {
		try {
			ThrowingFunction<Class<?>, Object, Throwable> allocateInstanceInvoker = this.allocateInstanceInvoker;
			try {
				return (T)allocateInstanceInvoker.apply(cls);
			} catch (NullPointerException exc) {
				if (allocateInstanceInvoker != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.allocateInstanceInvoker == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.allocateInstanceInvoker = getOrBuildAllocateInstanceInvoker(initContext);
						refresh(initContext);
					}
				}
				return (T)this.allocateInstanceInvoker.apply(cls);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Class<?> getClassByName(String className, Boolean initialize, ClassLoader classLoader, Class<?> caller) {
		try {
			ThrowingQuadFunction<String, Boolean, ClassLoader, Class<?>, Class<?>, Throwable> classByNameRetriever = this.classByNameRetriever;
			try {
				return classByNameRetriever.apply(className, initialize, classLoader, caller);
			} catch (NullPointerException exc) {
				if (classByNameRetriever != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.classByNameRetriever == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.classByNameRetriever = getOrBuildClassByNameRetriever(initContext);
						refresh(initContext);
					}
				}
				return this.classByNameRetriever.apply(className, initialize, classLoader, caller);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Collection<URL> getResources(String resourceRelativePath, boolean findFirst, ClassLoader... classLoaders) {
		try {
			GetResourcesFunction resourcesRetriver = this.resourcesRetriver;
			try {
				return resourcesRetriver.apply(resourceRelativePath, findFirst, classLoaders);
			} catch (NullPointerException exc) {
				if (resourcesRetriver != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.resourcesRetriver == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.resourcesRetriver = getOrBuildResourcesRetriever(initContext);
						refresh(initContext);
					}
				}
				return this.resourcesRetriver.apply(resourceRelativePath, findFirst, classLoaders);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Collection<URL> getResources(String resourceRelativePath, boolean findFirst, Collection<ClassLoader> classLoaders) {
		try {
			GetResourcesFunction resourcesRetriver = this.resourcesRetriver;
			try {
				return resourcesRetriver.apply(resourceRelativePath, findFirst, classLoaders);
			} catch (NullPointerException exc) {
				if (resourcesRetriver != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.resourcesRetriver == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.resourcesRetriver = getOrBuildResourcesRetriever(initContext);
						refresh(initContext);
					}
				}
				return this.resourcesRetriver.apply(resourceRelativePath, findFirst, classLoaders);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public boolean isBuiltinClassLoader(ClassLoader classLoader) {
		Class<?> builtinClassLoaderClass = getBuiltinClassLoaderClass();
		return builtinClassLoaderClass != null && builtinClassLoaderClass.isAssignableFrom(classLoader.getClass());
	}

	@Override
	public boolean isClassLoaderDelegate(ClassLoader classLoader) {
		Class<?> classLoaderDelegateClass = getClassLoaderDelegateClass();
		return classLoaderDelegateClass != null && classLoaderDelegateClass.isAssignableFrom(classLoader.getClass());
	}

	@Override
	public Class<?> getBuiltinClassLoaderClass() {
		Supplier<Class<?>> builtinClassLoaderClassSupplier = this.builtinClassLoaderClassSupplier;
		try {
			return builtinClassLoaderClassSupplier.get();
		} catch (NullPointerException exc) {
			if (builtinClassLoaderClassSupplier != null) {
				throw exc;
			}
			synchronized (this) {
				if (this.builtinClassLoaderClassSupplier == null) {
					Map<Object, Object> initContext = functionsToMap();
					this.builtinClassLoaderClassSupplier = getOrBuildBuiltinClassLoaderClassSupplier(initContext);
					refresh(initContext);
				}
			}
			return this.builtinClassLoaderClassSupplier.get();
		}
	}

	@Override
	public Class<?> getClassLoaderDelegateClass() {
		Supplier<Class<?>> classLoaderDelegateClassSupplier = this.classLoaderDelegateClassSupplier;
		try {
			return classLoaderDelegateClassSupplier.get();
		} catch (NullPointerException exc) {
			if (classLoaderDelegateClassSupplier != null) {
				throw exc;
			}
			synchronized (this) {
				if (this.classLoaderDelegateClassSupplier == null) {
					Map<Object, Object> initContext = functionsToMap();
					this.classLoaderDelegateClassSupplier = getOrBuildClassLoaderDelegateClassSupplier(initContext);
					refresh(initContext);
				}
			}
			return this.classLoaderDelegateClassSupplier.get();
		}
	}

	@Override
	public Lookup getConsulter(Class<?> cls) {
		try {
			ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable> consulterRetriever = this.consulterRetriever;
			try {
				return consulterRetriever.apply(cls);
			} catch (NullPointerException exc) {
				if (consulterRetriever != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.consulterRetriever == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.consulterRetriever = getOrBuildDeepConsulterRetriever(initContext);
						refresh(initContext);
					}
				}
				return this.consulterRetriever.apply(cls);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public <T> T invoke(Object target, Method method, Object[] params) {
		try {
			ThrowingTriFunction<Method, Object, Object[], Object, Throwable> methodInvoker = this.methodInvoker;
			try {
				return (T)methodInvoker.apply(method, target, params);
			} catch (NullPointerException exc) {
				if (methodInvoker != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.methodInvoker == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.methodInvoker = getOrBuildMethodInvoker(initContext);
						refresh(initContext);
					}
				}
				return (T)this.methodInvoker.apply(method, target, params);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public <T> T newInstance(Constructor<T> ctor, Object[] params) {
		try {
			ThrowingBiFunction<Constructor<?>, Object[], Object, Throwable> constructorInvoker = this.constructorInvoker;
			try {
				return (T)constructorInvoker.apply(ctor, params);
			} catch (NullPointerException exc) {
				if (constructorInvoker != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.constructorInvoker == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.constructorInvoker = getOrBuildConstructorInvoker(initContext);
						refresh(initContext);
					}
				}
				return (T)this.constructorInvoker.apply(ctor, params);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Field[] getDeclaredFields(Class<?> cls) {
		try {
			ThrowingFunction<Class<?>, Field[], Throwable> declaredFieldsRetriever = this.declaredFieldsRetriever;
			try {
				return declaredFieldsRetriever.apply(cls);
			} catch (NullPointerException exc) {
				if (declaredFieldsRetriever != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.declaredFieldsRetriever == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.declaredFieldsRetriever = getOrBuildDeclaredFieldsRetriever(initContext);
						refresh(initContext);
					}
				}
				return this.declaredFieldsRetriever.apply(cls);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls) {
		try {
			ThrowingFunction<Class<?>, Constructor<?>[], Throwable> declaredConstructorsRetriever = this.declaredConstructorsRetriever;
			try {
				return (Constructor<T>[])declaredConstructorsRetriever.apply(cls);
			} catch (NullPointerException exc) {
				if (declaredConstructorsRetriever != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.declaredConstructorsRetriever == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.declaredConstructorsRetriever = getOrBuildDeclaredConstructorsRetriever(initContext);
						refresh(initContext);
					}
				}
				return (Constructor<T>[])this.declaredConstructorsRetriever.apply(cls);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Method[] getDeclaredMethods(Class<?> cls) {
		try {
			ThrowingFunction<Class<?>, Method[], Throwable> declaredMethodsRetriever = this.declaredMethodsRetriever;
			try {
				return declaredMethodsRetriever.apply(cls);
			} catch (NullPointerException exc) {
				if (declaredMethodsRetriever != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.declaredMethodsRetriever == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.declaredMethodsRetriever = getOrBuildDeclaredMethodsRetriever(initContext);
						refresh(initContext);
					}
				}
				return this.declaredMethodsRetriever.apply(cls);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}



	@Override
	@Deprecated(since="9.4.0")
	public void stop(Thread thread) {
		try {
			ThrowingBiConsumer<Thread, Throwable, Throwable> threadStopper = this.threadStopper;
			try {
				threadStopper.accept(thread, new ThreadDeath());
			} catch (NullPointerException exc) {
				if (threadStopper != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.threadStopper == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.threadStopper = getOrBuildThreadStopper(initContext);
						refresh(initContext);
					}
				}
				this.threadStopper.accept(thread, new ThreadDeath());
			}
		} catch (Throwable exc) {
			throwException(exc);
		}
	}

	@Override
	public ClassLoader convertToBuiltinClassLoader(ClassLoader classLoader) {
		try {
			ThrowingFunction<ClassLoader, ClassLoader, Throwable> classLoaderToBuiltinClassLoaderConverter = this.classLoaderToBuiltinClassLoaderConverter;
			try {
				return classLoaderToBuiltinClassLoaderConverter.apply(classLoader);
			} catch (NullPointerException exc) {
				if (classLoaderToBuiltinClassLoaderConverter != null) {
					throw exc;
				}
				synchronized (this) {
					if (this.classLoaderToBuiltinClassLoaderConverter == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.classLoaderToBuiltinClassLoaderConverter = getOrBuildClassLoaderToBuiltinClassLoaderConverter(initContext);
						refresh(initContext);
					}
				}
				return this.classLoaderToBuiltinClassLoaderConverter.apply(classLoader);
			}
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
		declaredFieldsRetriever = null;
		declaredMethodsRetriever = null;
		declaredConstructorsRetriever = null;
		accessibleSetter = null;
		constructorInvoker = null;
		methodInvoker = null;
		packageRetriever = null;
		classByNameRetriever = null;
		resourcesRetriver = null;
		builtinClassLoaderClassSupplier = null;
		classLoaderDelegateClassSupplier = null;
		consulterRetriever = null;
		loadedClassesRetrieverSupplier = null;
		loadedPackagesRetriever = null;
		classLoaderToBuiltinClassLoaderConverter = null;
		threadStopper = null;
	}

}
