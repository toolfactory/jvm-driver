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


import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

	private ThrowExceptionFunction exceptionThrower;
	private ThrowingFunction<Class<?>, Object, Throwable> allocateInstanceInvoker;
	private BiFunction<Object, Field, Object> fieldValueRetriever;
	private TriConsumer<Object, Field, Object> fieldValueSetter;
	private ThrowingBiFunction<Class<?>, byte[], Class<?>, Throwable> hookClassDefiner;
	private ThrowingFunction<Class<?>, MethodHandles.Lookup, Throwable> consulterRetriever;
	private ThrowingFunction<Class<?>, Field[], Throwable> declaredFieldsRetriever;
	private ThrowingFunction<Class<?>, Method[], Throwable> declaredMethodsRetriever;
	private ThrowingFunction<Class<?>, Constructor<?>[], Throwable> declaredConstructorsRetriever;
	private ThrowingBiConsumer<AccessibleObject, Boolean, Throwable> accessibleSetter;
	private ThrowingBiFunction<Constructor<?>, Object[], Object, Throwable> constructorInvoker;
	private ThrowingBiFunction<ClassLoader, String, Package, Throwable> packageRetriever;
	private ThrowingTriFunction<Method, Object, Object[], Object, Throwable> methodInvoker;
	private ThrowingQuadFunction<String, Boolean, ClassLoader, Class<?>, Class<?>, Throwable> classByNameRetriever;
	private GetResourcesFunction resourcesRetriver;
	private Supplier<Class<?>> builtinClassLoaderClassSupplier;
	private Supplier<Class<?>> classLoaderDelegateClassSupplier;
	private Function<ClassLoader, CleanableSupplier<Collection<Class<?>>>> loadedClassesRetrieverSupplier;
	private Function<ClassLoader, Map<String, ?>> loadedPackagesRetriever;
	private ThrowingFunction<ClassLoader, ClassLoader, Throwable> classLoaderToBuiltinClassLoaderConverter;

	public DriverAbst() {}


	@Override
	public <D extends Driver> D init() {
		Map<Object, Object> initializationContext = new HashMap<>();
		putNewObjectProviderIfAbsent(initializationContext);
		getOrBuildExceptionThrower(initializationContext);
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
			if (classLoaderToBuiltinClassLoaderConverter != null) {
				classLoaderToBuiltinClassLoaderConverter = getOrBuildClassLoaderToBuiltinClassLoaderConverter(initializationContext);
			}
		} catch (Throwable exc) {
			throwException(
				new InitializeException(
					"Could not initiliaze " + this.getClass().getSimpleName(), exc
				)
			);
		}
		return (D)this;
	}


	<D extends Driver> D refresh(Map<Object, Object> initializationContext) {
		try {
			exceptionThrower = getExceptionThrower(initializationContext);
			allocateInstanceInvoker = getAllocateInstanceInvoker(initializationContext);
			fieldValueRetriever = getFieldValueRetriever(initializationContext);
			fieldValueSetter = getFieldValueSetter(initializationContext);
			hookClassDefiner = getHookClassDefiner(initializationContext);
			declaredFieldsRetriever = getDeclaredFieldsRetriever(initializationContext);
			declaredMethodsRetriever = getDeclaredMethodsRetriever(initializationContext);
			declaredConstructorsRetriever = getDeclaredConstructorsRetriever(initializationContext);
			accessibleSetter = getAccessibleSetter(initializationContext);
			constructorInvoker = getConstructorInvoker(initializationContext);
			methodInvoker = getMethodInvoker(initializationContext);
			packageRetriever = getPackageRetriever(initializationContext);
			classByNameRetriever = getOrBuildClassByNameRetriever(initializationContext);
			resourcesRetriver = getOrBuildResourcesRetriever(initializationContext);
			builtinClassLoaderClassSupplier = getBuiltinClassLoaderClassSupplier(initializationContext);
			classLoaderDelegateClassSupplier = getClassLoaderDelegateClassSupplier(initializationContext);
			consulterRetriever = getDeepConsulterRetriever(initializationContext);
			loadedClassesRetrieverSupplier = getLoadedClassesRetrieverFunction(initializationContext);
			loadedPackagesRetriever = getLoadedPackagesRetriever(initializationContext);
			classLoaderToBuiltinClassLoaderConverter = getClassLoaderToBuiltinClassLoaderConverter(initializationContext);
			putNewObjectProviderIfAbsent(initializationContext);
		} catch (Throwable exc) {
			throwException(
				new InitializeException(
					"Could not initiliaze " + this.getClass().getSimpleName(), exc
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
		putNewObjectProviderIfAbsent(initializationContext);
		return initializationContext;
	}


	ObjectProvider putNewObjectProviderIfAbsent(Map<Object, Object> context) {
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
			if (exceptionThrower == null) {
				synchronized (this) {
					if (this.exceptionThrower == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.exceptionThrower = getOrBuildExceptionThrower(initContext);
						refresh(initContext);
					}
				}
				return this.exceptionThrower.apply(exception);
			}
			throw exc;
		}
	}
	
	@Override
	public <T> T throwException(String message, Object... placeHolderReplacements) {
		ThrowExceptionFunction exceptionThrower = this.exceptionThrower;
		try {
			return exceptionThrower.apply(3, message, placeHolderReplacements);
		} catch (NullPointerException exc) {
			if (exceptionThrower == null) {
				synchronized (this) {
					if (this.exceptionThrower == null) {
						Map<Object, Object> initContext = functionsToMap();
						this.exceptionThrower = getOrBuildExceptionThrower(initContext);
						refresh(initContext);
					}
				}
				return this.exceptionThrower.apply(3, message, placeHolderReplacements);
			}
			throw exc;
		}
	}

	@Override
	public void setAccessible(AccessibleObject object, boolean flag) {
		ThrowingBiConsumer<AccessibleObject, Boolean, Throwable> accessibleSetter = this.accessibleSetter;
		try {
			try {
				accessibleSetter.accept(object, flag);
			} catch (NullPointerException exc) {
				if (accessibleSetter == null) {
					synchronized (this) {
						if (this.accessibleSetter == null) {
							Map<Object, Object> initContext = functionsToMap();
							this.accessibleSetter = getOrBuildAccessibleSetter(initContext);
							refresh(initContext);
						}
					}
					this.accessibleSetter.accept(object, flag);
					return;
				}
				throw exc;
			}			
		} catch (Throwable exc) {
			throwException(exc);
		}
	}

	@Override
	public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode) {
		try {		
			try {
				return hookClassDefiner.apply(clientClass, byteCode);
			} catch (NullPointerException exc) {
				if (hookClassDefiner == null) {
					synchronized (this) {
						if (hookClassDefiner == null) {
							Map<Object, Object> initContext = functionsToMap();
							hookClassDefiner = getOrBuildHookClassDefiner(initContext);
							refresh(initContext);
						}
					}
				}
				return hookClassDefiner.apply(clientClass, byteCode);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Package getPackage(ClassLoader classLoader, String packageName) {
		try {
			try {
				return packageRetriever.apply(classLoader, packageName);
			} catch (NullPointerException exc) {
				if (packageRetriever == null) {
					synchronized (this) {
						if (packageRetriever == null) {
							Map<Object, Object> initContext = functionsToMap();
							packageRetriever = getOrBuildPackageRetriever(initContext);
							refresh(initContext);
						}
					}
				}
				return packageRetriever.apply(classLoader, packageName);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public CleanableSupplier<Collection<Class<?>>> getLoadedClassesRetriever(ClassLoader classLoader) {
		try {
			return loadedClassesRetrieverSupplier.apply(classLoader);
		} catch (NullPointerException exc) {
			if (loadedClassesRetrieverSupplier == null) {
				synchronized (this) {
					if (loadedClassesRetrieverSupplier == null) {
						Map<Object, Object> initContext = functionsToMap();
						loadedClassesRetrieverSupplier = getOrBuildLoadedClassesRetrieverFunction(initContext);
						refresh(initContext);
					}
				}
			}
			return loadedClassesRetrieverSupplier.apply(classLoader);
		}
	}

	@Override
	public Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader) {
		try {
			return loadedPackagesRetriever.apply(classLoader);
		} catch (NullPointerException exc) {
			if (loadedPackagesRetriever == null) {
				synchronized (this) {
					if (loadedPackagesRetriever == null) {
						Map<Object, Object> initContext = functionsToMap();
						loadedPackagesRetriever = getOrBuildLoadedPackagesRetriever(initContext);
						refresh(initContext);
					}
				}
			}
			return loadedPackagesRetriever.apply(classLoader);
		}
	}

	@Override
	public <T> T getFieldValue(Object target, Field field) {
		try {
			return (T)fieldValueRetriever.apply(target, field);
		} catch (NullPointerException exc) {
			if (fieldValueRetriever == null) {
				synchronized (this) {
					if (fieldValueRetriever == null) {
						Map<Object, Object> initContext = functionsToMap();
						fieldValueRetriever = getOrBuildFieldValueRetriever(initContext);
						refresh(initContext);
					}
				}
			}
			return (T)fieldValueRetriever.apply(target, field);
		}
	}

	@Override
	public void setFieldValue(Object target, Field field, Object value) {
		try {
			fieldValueSetter.accept(target, field, value);
		} catch (NullPointerException exc) {
			if (fieldValueSetter == null) {
				synchronized (this) {
					if (fieldValueSetter == null) {
						Map<Object, Object> initContext = functionsToMap();
						fieldValueSetter = getOrBuildFieldValueSetter(initContext);
						refresh(initContext);
					}
				}
			}
			fieldValueSetter.accept(target, field, value);
		}
	}

	@Override
	public <T> T allocateInstance(Class<?> cls) {
		try {
			try {
				return (T)allocateInstanceInvoker.apply(cls);
			} catch (NullPointerException exc) {
				if (allocateInstanceInvoker == null) {
					synchronized (this) {
						if (allocateInstanceInvoker == null) {
							Map<Object, Object> initContext = functionsToMap();
							allocateInstanceInvoker = getOrBuildAllocateInstanceInvoker(initContext);
							refresh(initContext);
						}
					}
				}
				return (T)allocateInstanceInvoker.apply(cls);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Class<?> getClassByName(String className, Boolean initialize, ClassLoader classLoader, Class<?> caller) {
		try {
			try {
				return classByNameRetriever.apply(className, initialize, classLoader, caller);
			} catch (NullPointerException exc) {
				if (classByNameRetriever == null) {
					synchronized (this) {
						if (classByNameRetriever == null) {
							Map<Object, Object> initContext = functionsToMap();
							classByNameRetriever = getOrBuildClassByNameRetriever(initContext);
							refresh(initContext);
						}
					}
				}
				return classByNameRetriever.apply(className, initialize, classLoader, caller);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Collection<URL> getResources(String resourceRelativePath, boolean findFirst, ClassLoader... classLoaders) {
		try {
			try {
				return resourcesRetriver.apply(resourceRelativePath, findFirst, classLoaders);
			} catch (NullPointerException exc) {
				if (resourcesRetriver == null) {
					synchronized (this) {
						if (resourcesRetriver == null) {
							Map<Object, Object> initContext = functionsToMap();
							resourcesRetriver = getOrBuildResourcesRetriever(initContext);
							refresh(initContext);
						}
					}
				}
				return resourcesRetriver.apply(resourceRelativePath, findFirst, classLoaders);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Collection<URL> getResources(String resourceRelativePath, boolean findFirst, Collection<ClassLoader> classLoaders) {
		try {
			try {
				return resourcesRetriver.apply(resourceRelativePath, findFirst, classLoaders);
			} catch (NullPointerException exc) {
				if (resourcesRetriver == null) {
					synchronized (this) {
						if (resourcesRetriver == null) {
							Map<Object, Object> initContext = functionsToMap();
							resourcesRetriver = getOrBuildResourcesRetriever(initContext);
							refresh(initContext);
						}
					}
				}
				return resourcesRetriver.apply(resourceRelativePath, findFirst, classLoaders);
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
		try {
			return builtinClassLoaderClassSupplier.get();
		} catch (NullPointerException exc) {
			if (builtinClassLoaderClassSupplier == null) {
				synchronized (this) {
					if (builtinClassLoaderClassSupplier == null) {
						Map<Object, Object> initContext = functionsToMap();
						builtinClassLoaderClassSupplier = getOrBuildBuiltinClassLoaderClassSupplier(initContext);
						refresh(initContext);
					}
				}
			}
			return builtinClassLoaderClassSupplier.get();
		}
	}

	@Override
	public Class<?> getClassLoaderDelegateClass() {
		try {
			return classLoaderDelegateClassSupplier.get();
		} catch (NullPointerException exc) {
			if (classLoaderDelegateClassSupplier == null) {
				synchronized (this) {
					if (classLoaderDelegateClassSupplier == null) {
						Map<Object, Object> initContext = functionsToMap();
						classLoaderDelegateClassSupplier = getOrBuildClassLoaderDelegateClassSupplier(initContext);
						refresh(initContext);
					}
				}
			}
			return classLoaderDelegateClassSupplier.get();
		}
	}

	@Override
	public Lookup getConsulter(Class<?> cls) {
		try {
			try {
				return consulterRetriever.apply(cls);
			} catch (NullPointerException exc) {
				if (consulterRetriever == null) {
					synchronized (this) {
						if (consulterRetriever == null) {
							Map<Object, Object> initContext = functionsToMap();
							consulterRetriever = getOrBuildDeepConsulterRetriever(initContext);
							refresh(initContext);
						}
					}
				}
				return consulterRetriever.apply(cls);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public <T> T invoke(Object target, Method method, Object[] params) {
		try {
			try {
				return (T)methodInvoker.apply(method, target, params);
			} catch (NullPointerException exc) {
				if (methodInvoker == null) {
					synchronized (this) {
						if (methodInvoker == null) {
							Map<Object, Object> initContext = functionsToMap();
							methodInvoker = getOrBuildMethodInvoker(initContext);
							refresh(initContext);
						}
					}
				}
				return (T)methodInvoker.apply(method, target, params);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public <T> T newInstance(Constructor<T> ctor, Object[] params) {
		try {
			try {
				return (T)constructorInvoker.apply(ctor, params);
			} catch (NullPointerException exc) {
				if (constructorInvoker == null) {
					synchronized (this) {
						if (constructorInvoker == null) {
							Map<Object, Object> initContext = functionsToMap();
							constructorInvoker = getOrBuildConstructorInvoker(initContext);
							refresh(initContext);
						}
					}
				}
				return (T)constructorInvoker.apply(ctor, params);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Field[] getDeclaredFields(Class<?> cls) {
		try {
			try {
				return declaredFieldsRetriever.apply(cls);
			} catch (NullPointerException exc) {
				if (declaredFieldsRetriever == null) {
					synchronized (this) {
						if (declaredFieldsRetriever == null) {
							Map<Object, Object> initContext = functionsToMap();
							declaredFieldsRetriever = getOrBuildDeclaredFieldsRetriever(initContext);
							refresh(initContext);
						}
					}
				}
				return declaredFieldsRetriever.apply(cls);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls) {
		try {
			try {
				return (Constructor<T>[])declaredConstructorsRetriever.apply(cls);
			} catch (NullPointerException exc) {
				if (declaredConstructorsRetriever == null) {
					synchronized (this) {
						if (declaredConstructorsRetriever == null) {
							Map<Object, Object> initContext = functionsToMap();
							declaredConstructorsRetriever = getOrBuildDeclaredConstructorsRetriever(initContext);
							refresh(initContext);
						}
					}
				}
				return (Constructor<T>[])declaredConstructorsRetriever.apply(cls);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}

	@Override
	public Method[] getDeclaredMethods(Class<?> cls) {
		try {
			try {
				return declaredMethodsRetriever.apply(cls);
			} catch (NullPointerException exc) {
				if (declaredMethodsRetriever == null) {
					synchronized (this) {
						if (declaredMethodsRetriever == null) {
							Map<Object, Object> initContext = functionsToMap();
							declaredMethodsRetriever = getOrBuildDeclaredMethodsRetriever(initContext);
							refresh(initContext);
						}
					}
				}
				return declaredMethodsRetriever.apply(cls);
			}
		} catch (Throwable exc) {
			return throwException(exc);
		}
	}
	


	@Override
	public ClassLoader convertToBuiltinClassLoader(ClassLoader classLoader) {
		try {
			try {
				return classLoaderToBuiltinClassLoaderConverter.apply(classLoader);
			} catch (NullPointerException exc) {
				if (classLoaderToBuiltinClassLoaderConverter == null) {
					synchronized (this) {
						if (classLoaderToBuiltinClassLoaderConverter == null) {
							Map<Object, Object> initContext = functionsToMap();
							classLoaderToBuiltinClassLoaderConverter = getOrBuildClassLoaderToBuiltinClassLoaderConverter(initContext);
							refresh(initContext);
						}
					}
				}
				return classLoaderToBuiltinClassLoaderConverter.apply(classLoader);
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
	}

}