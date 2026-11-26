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


import java.io.Closeable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.toolfactory.jvm.util.CleanableSupplier;
import io.github.toolfactory.jvm.util.Properties;


/**
 * A driver that allows a deep interaction with the JVM without the restrictions of the reflection API and of the
 * module system: it reads and writes fields, invokes methods and constructors, allocates instances, defines classes
 * and inspects the class loaders, whatever the access modifiers of the members and the encapsulation of the modules.
 * <p>
 * There are four kinds of driver, created by the {@link Factory}:
 * <ul>
 * <li>the {@linkplain DefaultDriver default driver}, completely based on Java APIs;</li>
 * <li>the {@linkplain DynamicDriver dynamic driver}, which uses a JNI function only if a Java based function of the
 * default driver cannot be initialized, or if the memory-access methods of {@code sun.misc.Unsafe} are not
 * available;</li>
 * <li>the {@linkplain HybridDriver hybrid driver}, which uses some JNI functions only when run on JVM 17 and
 * later;</li>
 * <li>the {@linkplain NativeDriver native driver}, which uses JNI functions more consistently regardless of the Java
 * version it is running on.</li>
 * </ul>
 * All the JNI functions are supplied by the {@link NativeEngine}. Prefer the default driver, which is faster than the
 * drivers that use JNI functions, and use the other ones only when it cannot be initialized on your JVM:
 * {@link Factory#getNew()} already works this way.
 * <p>
 * Each functionality of a driver is initialized the first time that a method that needs it is called, and that call
 * throws the exception that prevents its initialization, if any: {@link #init()} initializes all of them at once.
 * <p>
 * The methods throw the exceptions that they encounter as they are, even the checked ones that they do not declare:
 * for example {@link #getClassByName(String, Boolean, ClassLoader, Class)} throws a {@link ClassNotFoundException}
 * if the class does not exist.
 */
public interface Driver extends Closeable {

	/**
	 * Initializes all the functionalities of this driver at once: otherwise each of them is initialized the first
	 * time that it is used.
	 *
	 * @param <D> the type of this driver
	 * @return this driver
	 * @throws InitializeException if a functionality cannot be initialized on the running JVM
	 */
	public <D extends Driver> D init();

	/**
	 * Allocates an instance of a class without calling any of its constructors: its fields have their default values.
	 * <p>
	 * If the class cannot be instantiated, for example because it is abstract, this method throws an
	 * {@link InstantiationException}, even though it does not declare it.
	 *
	 * @param <T> the type of the instance
	 * @param cls the class to instantiate
	 * @return the new instance
	 */
	public <T> T allocateInstance(Class<?> cls);

	/**
	 * Converts a class loader into a built-in class loader of the JDK, that is an instance of
	 * {@code jdk.internal.loader.BuiltinClassLoader}.
	 * <p>
	 * From Java 9 it returns the class loader itself if it is already a built-in class loader, otherwise a new class
	 * loader delegate, an instance of the class returned by {@link #getClassLoaderDelegateClass()}, which delegates
	 * the loading of the classes and of the resources to the class loader. On Java 7 and 8, which have no built-in
	 * class loaders, it returns {@code null}.
	 *
	 * @param classLoader the class loader to convert
	 * @return the class loader itself, a class loader delegate of it, or {@code null} on Java 7 and 8
	 */
	public ClassLoader convertToBuiltinClassLoader(ClassLoader classLoader);

	/**
	 * Defines a class, from its byte code, in the package of another class, the client class, so that the new class
	 * can access the members of that package that are not public: the package of the new class must be the one of the
	 * client class.
	 * <p>
	 * From Java 7 to 16 the class is an anonymous class whose host is the client class, defined through
	 * {@code sun.misc.Unsafe.defineAnonymousClass}: no class loader can find it and it can also access the private
	 * members of the client class. From Java 17 it is a class defined in the class loader of the client class,
	 * through {@code MethodHandles.Lookup.defineClass}: if the definition fails with a {@link LinkageError}, for
	 * example because a class with the same name is already defined, this method returns the class with that name
	 * that the class loader of the driver finds, or throws the error if there is none.
	 *
	 * @param clientClass the class in whose package the new class is defined
	 * @param byteCode the byte code of the new class
	 * @return the new class
	 */
	public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode);

	/**
	 * Returns the class {@code jdk.internal.loader.BuiltinClassLoader}, the superclass of the built-in class loaders
	 * of the JDK, such as the platform and the application class loaders, which exists from Java 9.
	 *
	 * @return the class, or {@code null} on Java 7 and 8
	 */
	public Class<?> getBuiltinClassLoaderClass();

	/**
	 * Returns the class of the class loader delegates created by {@link #convertToBuiltinClassLoader(ClassLoader)}:
	 * a subclass of {@code jdk.internal.loader.BuiltinClassLoader}, which the driver defines from Java 9 as a hook
	 * class of {@code BuiltinClassLoader} (see {@link #defineHookClass(Class, byte[])}).
	 *
	 * @return the class, or {@code null} on Java 7 and 8
	 */
	public Class<?> getClassLoaderDelegateClass();

	/**
	 * Finds a class by its name through the private native method of {@link Class} on which
	 * {@link Class#forName(String, boolean, ClassLoader)} is based, without the checks of that method.
	 * <p>
	 * The private method is {@code Class.forName0(String, boolean, ClassLoader, Class)} from Java 7 to 25 and
	 * {@code Class.forName0(String, boolean, ClassLoader)} from Java 26, while on IBM Semeru it is
	 * {@code Class.forNameImpl(String, boolean, ClassLoader)} on every version. If the class is not found, this method
	 * throws a {@link ClassNotFoundException}, even though it does not declare it.
	 *
	 * @param className the binary name of the class, as for {@link Class#forName(String, boolean, ClassLoader)}
	 * @param initialize {@code true} to initialize the class: it must not be {@code null}
	 * @param classLoader the class loader that loads the class, or {@code null} for the bootstrap class loader
	 * @param caller the class on whose behalf the class is looked up: it is passed to the JDK only from Java 7 to 25,
	 * and it is ignored on IBM Semeru
	 * @return the class
	 */
	public Class<?> getClassByName(String className, Boolean initialize, ClassLoader classLoader, Class<?> caller);

	/**
	 * Returns a lookup on a class with full access to its members, to create method handles for them whatever their
	 * access modifiers.
	 * <p>
	 * On Java 7 and 8, and on IBM Semeru before Java 17, the lookup has all the access modes that
	 * {@link MethodHandles#lookup()} has when it is called inside the class. Otherwise, from Java 9, it is a trusted
	 * lookup, like the one the JDK uses internally, which is not subject to access checks or to the encapsulation of
	 * the modules.
	 *
	 * @param cls the lookup class
	 * @return the lookup
	 */
	public MethodHandles.Lookup getConsulter(Class<?> cls);

	/**
	 * Returns all the constructors declared by a class, whatever their access modifiers, through the private native
	 * method of {@link Class} on which {@link Class#getDeclaredConstructors()} is based.
	 *
	 * @param <T> the type of the class
	 * @param cls the class whose constructors are returned
	 * @return the constructors declared by the class: an empty array if it has none, as for an interface
	 */
	public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);

	/**
	 * Returns all the fields declared by a class, through the private native method of {@link Class} on which
	 * {@link Class#getDeclaredFields()} is based: unlike that method, it also returns the fields that the reflection
	 * filters of the JDK hide, such as the field {@code allowedModes} of {@code MethodHandles.Lookup} from Java 12.
	 * The fields inherited from the superclasses are not returned.
	 *
	 * @param cls the class whose fields are returned
	 * @return the fields declared by the class
	 */
	public Field[] getDeclaredFields(Class<?> cls);

	/**
	 * Returns all the methods declared by a class, whatever their access modifiers, through the private native method
	 * of {@link Class} on which {@link Class#getDeclaredMethods()} is based. The constructors, the static initializer
	 * and the methods inherited from the superclasses are not returned.
	 *
	 * @param cls the class whose methods are returned
	 * @return the methods declared by the class
	 */
	public Method[] getDeclaredMethods(Class<?> cls);

	/**
	 * Reads the value of a field, whatever its access modifiers.
	 * <p>
	 * The native driver reads the value through the {@link NativeEngine}, as the dynamic driver does when it uses the
	 * JNI functions. The other drivers read it through {@code sun.misc.Unsafe}, honoring the {@code volatile}
	 * modifier: from Java 25, if {@code sun.misc.Unsafe} throws an {@link UnsupportedOperationException}, they make
	 * the field accessible and read it through reflection.
	 *
	 * @param <T> the type of the value
	 * @param target the object whose field is read: it is ignored if the field is static
	 * @param field the field to read
	 * @return the value of the field: the value of a primitive field is wrapped in its wrapper class
	 * @throws IllegalArgumentException if {@code target} is {@code null} and the field is not static
	 */
	public <T> T getFieldValue(Object target, Field field);

	/**
	 * Returns a package defined by a class loader.
	 * <p>
	 * From Java 9 it returns the package that the class loader has defined, without searching its ancestors, as
	 * {@code ClassLoader.getDefinedPackage(String)} does. On Java 7 and 8 it returns
	 * {@link Package#getPackage(String)}, which ignores {@code classLoader} and searches the package among the ones
	 * defined by the class loader of the driver and by its ancestors.
	 *
	 * @param classLoader the class loader that defines the package: from Java 9 it must not be {@code null}
	 * @param packageName the name of the package
	 * @return the package, or {@code null} if it is not found
	 */
	public Package getPackage(ClassLoader classLoader, String packageName);

	/**
	 * Finds the resources with a given path in one or more class loaders.
	 * <p>
	 * For each class loader it calls {@link ClassLoader#getResources(String)}, or
	 * {@link ClassLoader#getResource(String)} if {@code findFirst} is {@code true}, so it also searches the ancestors
	 * of the class loader. From Java 9 the built-in class loaders of the JDK (see
	 * {@link #isBuiltinClassLoader(ClassLoader)}) are searched without delegating to their parents and without the
	 * restrictions of the module system: first the module that contains the package of the resource, then all their
	 * modules and finally their class path.
	 *
	 * @param resourceRelativePath the path of the resources, relative to the roots of the class path, such as
	 * {@code META-INF/MANIFEST.MF}
	 * @param findFirst {@code true} to take only the first resource found in each class loader
	 * @param classLoaders the class loaders: if none is passed, the context class loader of the current thread
	 * @return the URLs of the resources found, without duplicates and in the order in which they are found
	 */
	public Collection<URL> getResources(String resourceRelativePath, boolean findFirst, ClassLoader... classLoaders);

	/**
	 * Finds the resources with a given path in one or more class loaders, as
	 * {@link #getResources(String, boolean, ClassLoader...)} does.
	 *
	 * @param resourceRelativePath the path of the resources, relative to the roots of the class path, such as
	 * {@code META-INF/MANIFEST.MF}
	 * @param findFirst {@code true} to take only the first resource found in each class loader
	 * @param classLoaders the class loaders: if the collection is {@code null} or empty, the context class loader of
	 * the current thread
	 * @return the URLs of the resources found, without duplicates and in the order in which they are found
	 */
	public Collection<URL> getResources(String resourceRelativePath, boolean findFirst, Collection<ClassLoader> classLoaders);

	/**
	 * Invokes a method, whatever its access modifiers and without the access checks of
	 * {@link Method#invoke(Object, Object...)}.
	 * <p>
	 * The method is invoked through the native method accessor of the JDK, the method {@code invoke0} of
	 * {@code NativeMethodAccessorImpl} up to Java 21 and of {@code DirectMethodHandleAccessor.NativeAccessor} from
	 * Java 22. If the invoked method throws an exception, this method throws an {@link InvocationTargetException}
	 * that wraps it, even though it does not declare it.
	 *
	 * @param <T> the type of the returned value
	 * @param target the object on which the method is invoked: it is ignored if the method is static
	 * @param method the method to invoke
	 * @param params the arguments of the method: for a method without parameters it can be {@code null}
	 * @return the value returned by the method, with a primitive value wrapped in its wrapper class, or {@code null}
	 * if the method is {@code void}
	 */
	public <T> T invoke(Object target, Method method, Object[] params);

	/**
	 * Tells whether a class loader is a built-in class loader of the JDK, that is an instance of
	 * {@code jdk.internal.loader.BuiltinClassLoader}, as the platform and the application class loaders and the class
	 * loader delegates are.
	 *
	 * @param classLoader the class loader: it must not be {@code null}
	 * @return {@code true} if the class loader is a built-in class loader: always {@code false} on Java 7 and 8
	 */
	public boolean isBuiltinClassLoader(ClassLoader classLoader);

	/**
	 * Tells whether a class loader is a class loader delegate created by
	 * {@link #convertToBuiltinClassLoader(ClassLoader)}.
	 *
	 * @param classLoader the class loader: it must not be {@code null}
	 * @return {@code true} if the class loader is a class loader delegate: always {@code false} on Java 7 and 8
	 */
	public boolean isClassLoaderDelegate(ClassLoader classLoader);

	/**
	 * Creates an instance by invoking a constructor, whatever its access modifiers and without the access checks of
	 * {@link Constructor#newInstance(Object...)}.
	 * <p>
	 * The constructor is invoked through the native constructor accessor of the JDK, the method
	 * {@code newInstance0} of {@code NativeConstructorAccessorImpl} up to Java 21 and of
	 * {@code DirectConstructorHandleAccessor.NativeAccessor} from Java 22. If the constructor throws an exception,
	 * this method throws an {@link InvocationTargetException} that wraps it, even though it does not declare it.
	 *
	 * @param <T> the type of the instance
	 * @param ctor the constructor to invoke
	 * @param params the arguments of the constructor: for a constructor without parameters it can be {@code null}
	 * @return the new instance
	 */
	public <T> T newInstance(Constructor<T> ctor, Object[] params);

	/**
	 * Returns a supplier of the classes loaded by a class loader.
	 * <p>
	 * On the JVMs based on HotSpot, {@link CleanableSupplier#get()} returns the collection in which the class loader
	 * keeps the classes that it has defined, its private field {@code classes}: it is not a copy, so it changes while
	 * the class loader defines new classes, and {@link CleanableSupplier#clear()} empties it.
	 * <p>
	 * On IBM Semeru, whose class loaders have no such collection, {@link CleanableSupplier#get()} finds by name the
	 * classes listed in the table of the class loading locks of the class loader, its private field
	 * {@code classNameBasedLock}, and returns the ones defined by the class loader. The JVM removes a name from that
	 * table once the garbage collector has cleared its lock, so the result may not contain all the classes loaded by
	 * the class loader. The returned collection keeps the classes found by every call:
	 * {@link CleanableSupplier#clear()} empties it and the table.
	 *
	 * @param classLoader the class loader: it must not be {@code null}
	 * @return the supplier of the classes
	 */
	public CleanableSupplier<Collection<Class<?>>> getLoadedClassesRetriever(ClassLoader classLoader);

	/**
	 * Returns the packages defined by a class loader, read from its private field {@code packages}.
	 * <p>
	 * The returned map is the one of the class loader, not a copy: it maps the names of the packages to
	 * {@link Package} objects on Java 7 and 8 and to {@code java.lang.NamedPackage} objects, the superclass of
	 * {@link Package}, from Java 9.
	 *
	 * @param classLoader the class loader
	 * @return the map of the packages defined by the class loader
	 */
	public Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader);

	/**
	 * Sets the accessible flag of a field, a method or a constructor, as
	 * {@link AccessibleObject#setAccessible(boolean)} does, but without its checks: it also works on the members of
	 * the classes of the modules that do not open their packages.
	 * <p>
	 * On Java 7 and 8 it invokes the private method {@code AccessibleObject.setAccessible0(AccessibleObject, boolean)}.
	 * From Java 9 the native driver, and the dynamic driver when it uses the JNI functions, write the private field
	 * {@code AccessibleObject.override} through the {@link NativeEngine}, while the other drivers invoke the private
	 * method {@code AccessibleObject.setAccessible0(boolean)} through a hook class defined in the package
	 * {@code java.lang.reflect}.
	 *
	 * @param object the field, the method or the constructor
	 * @param flag the new value of the accessible flag
	 */
	public void setAccessible(AccessibleObject object, boolean flag);

	/**
	 * Writes the value of a field, whatever its access modifiers and even if it is final.
	 * <p>
	 * The native driver writes the value through the {@link NativeEngine}, as the dynamic driver does when it uses
	 * the JNI functions. The other drivers write it through {@code sun.misc.Unsafe}, honoring the {@code volatile}
	 * modifier. Writing a final field does not change the copies of its value that the compiler may have already
	 * made, such as the ones of the constants.
	 *
	 * @param target the object whose field is written: it is ignored if the field is static
	 * @param field the field to write
	 * @param value the new value: for a primitive field, a non-{@code null} instance of its wrapper class
	 * @throws IllegalArgumentException if {@code target} is {@code null} and the field is not static, or if the value
	 * cannot be assigned to the field
	 */
	public void setFieldValue(Object target, Field field, Object value);

	/**
	 * Throws a new {@link Exception}, which is checked, even though this method does not declare it.
	 * <p>
	 * The message of the exception is {@code message} with each {@code {}} placeholder replaced, in order, by the
	 * string value of the corresponding replacement, and its stack trace starts from the method that calls this one.
	 *
	 * @param <T> the type that the caller expects
	 * @param message the message, with a {@code {}} placeholder for each replacement
	 * @param placeHolderReplacements the replacements of the placeholders
	 * @return nothing, since this method always throws an exception: the return type allows a method that returns
	 * a value to end with {@code return driver.throwException(...)}
	 */
	public <T> T throwException(String message, Object... placeHolderReplacements);

	/**
	 * Throws an exception as it is, even if it is a checked exception that the calling method does not declare.
	 *
	 * @param <T> the type that the caller expects
	 * @param exception the exception to throw
	 * @return nothing, since this method always throws an exception: the return type allows a method that returns
	 * a value to end with {@code return driver.throwException(exception)}
	 * @throws NullPointerException if {@code exception} is {@code null}
	 */
	public <T> T throwException(Throwable exception);

	/**
	 * Stops a thread.
	 * <p>
	 * Up to Java 19 it invokes the private method {@code Thread.stop0(Object)}, or {@code Thread.stopImpl(Throwable)}
	 * on IBM Semeru, with a {@code ThreadDeath}, as {@code Thread.stop()} did. From Java 20, where that method no
	 * longer exists, it invokes the private method {@code Thread.interrupt0()}, which neither stops the thread nor
	 * sets its interrupt status.
	 *
	 * @param thread the thread to stop
	 * @deprecated stopping a thread is inherently unsafe, and from Java 20 this method does not stop it
	 */
	@Deprecated(since="9.4.0")
	public void stop(Thread thread);


	/**
	 * Releases the functions that this driver has initialized. The driver can still be used: each method called
	 * afterwards initializes again the functions it needs.
	 */
	@Override
	public void close();


	/**
	 * Creates the drivers.
	 * <p>
	 * The classes of the four kinds of driver are read, the first time that this class is used, from the
	 * {@code driver-factory.default-driver.class}, {@code driver-factory.dynamic-driver.class},
	 * {@code driver-factory.hybrid-driver.class} and {@code driver-factory.native-driver.class} properties of the
	 * {@code jvm-driver.properties} files found through the class loader of this class and the context class loader
	 * of the current thread. The files are merged according to their {@code priority-of-this-configuration} property:
	 * each property takes its value from the file with the highest priority that sets it. ToolFactory JVM Driver sets
	 * them, with priority 0, to {@link DefaultDriver}, {@link DynamicDriver}, {@link HybridDriver} and
	 * {@link NativeDriver}; the setters of this class change them at runtime.
	 */
	@SuppressWarnings("unchecked")
	public static class Factory {
		private static Map<String, Constructor<? extends Driver>> driverConstructors;


		static {
			try {
				Set<ClassLoader> classLoaders = new HashSet<>();
				classLoaders.add(Factory.class.getClassLoader());
				classLoaders.add(Thread.currentThread().getContextClassLoader());
				java.util.Properties configuration = Properties.loadFromResourcesAndMerge(
					"jvm-driver.properties",
					"priority-of-this-configuration",
					classLoaders
				);
				driverConstructors = new ConcurrentHashMap<>();
				setDriverClass("defaultDriverClass", configuration.getProperty("driver-factory.default-driver.class"));
				setDriverClass("dynamicDriverClass", configuration.getProperty("driver-factory.dynamic-driver.class"));
				setDriverClass("hybridDriverClass", configuration.getProperty("driver-factory.hybrid-driver.class"));
				setDriverClass("nativeDriverClass", configuration.getProperty("driver-factory.native-driver.class"));
			} catch (Throwable exc) {
				throw new FactoryException(exc);
			}
		}


		/**
		 * Returns the first driver that can be initialized among the default, the hybrid and the native drivers,
		 * tried in this order: the dynamic driver is not tried.
		 *
		 * @return a driver already initialized (see {@link Driver#init()})
		 * @throws InitializeException if not even the native driver can be initialized
		 * @throws InstantiateException if a driver cannot be instantiated
		 */
		public static Driver getNew() {
			try {
				try {
					return getNewDefault().init();
				} catch (InitializeException exc) {
					return getNewHybrid().init();
				}
			} catch (InitializeException exc) {
				return getNewNative().init();
			}
		}

		/**
		 * Creates a driver of a given class, loaded through the class loader of this class, by invoking its
		 * constructor without parameters.
		 *
		 * @param <D> the type of the driver
		 * @param className the name of the class of the driver
		 * @return the new driver, not initialized
		 * @throws Throwable if the class cannot be loaded or instantiated
		 */
		public static <D extends Driver> D getNew(String className) throws Throwable {
			return (D)Class.forName(className).getDeclaredConstructor().newInstance();
		}

		private static void setDriverClass(String name, String className) {
			try {
				setDriverClass(name, Class.forName(className));
			} catch (ClassNotFoundException exc) {
				throw new FactoryException(exc);
			}
		}

		private static void setDriverClass(String name, Class<?> cls) {
			try {
				driverConstructors.put(
					name,
					(Constructor<? extends Driver>)cls.getDeclaredConstructor()
				);
			} catch (NoSuchMethodException | SecurityException exc) {
				throw new FactoryException(exc);
			}
		}

		/**
		 * Sets the class of the default drivers, which {@link #getNewDefault()} and {@link #getNew()} create from now
		 * on.
		 *
		 * @param cls the class of the driver, with a public constructor without parameters
		 * @throws FactoryException if the class has no constructor without parameters
		 */
		public static void setDefaultDriverClass(Class<? extends Driver> cls) {
			setDriverClass("defaultDriverClass", cls);
		}

		/**
		 * Sets the class of the default drivers, which {@link #getNewDefault()} and {@link #getNew()} create from now
		 * on, loading it through the class loader of this class.
		 *
		 * @param className the name of the class of the driver, with a public constructor without parameters
		 * @throws FactoryException if the class cannot be found or has no constructor without parameters
		 */
		public static void setDefaultDriverClass(String className) {
			setDriverClass("defaultDriverClass", className);
		}

		/**
		 * Sets the class of the hybrid drivers, which {@link #getNewHybrid()} and {@link #getNew()} create from now
		 * on.
		 *
		 * @param cls the class of the driver, with a public constructor without parameters
		 * @throws FactoryException if the class has no constructor without parameters
		 */
		public static void setHybridDriverClass(Class<? extends Driver> cls) {
			setDriverClass("hybridDriverClass", cls);
		}

		/**
		 * Sets the class of the hybrid drivers, which {@link #getNewHybrid()} and {@link #getNew()} create from now
		 * on, loading it through the class loader of this class.
		 *
		 * @param className the name of the class of the driver, with a public constructor without parameters
		 * @throws FactoryException if the class cannot be found or has no constructor without parameters
		 */
		public static void setHybridDriverClass(String className) {
			setDriverClass("hybridDriverClass", className);
		}

		/**
		 * Sets the class of the dynamic drivers, which {@link #getNewDynamic()} creates from now on.
		 *
		 * @param cls the class of the driver, with a public constructor without parameters
		 * @throws FactoryException if the class has no constructor without parameters
		 */
		public static void setDynamicDriverClass(Class<? extends Driver> cls) {
			setDriverClass("dynamicDriverClass", cls);
		}

		/**
		 * Sets the class of the dynamic drivers, which {@link #getNewDynamic()} creates from now on, loading it
		 * through the class loader of this class.
		 *
		 * @param className the name of the class of the driver, with a public constructor without parameters
		 * @throws FactoryException if the class cannot be found or has no constructor without parameters
		 */
		public static void setDynamicDriverClass(String className) {
			setDriverClass("dynamicDriverClass", className);
		}

		/**
		 * Sets the class of the native drivers, which {@link #getNewNative()} and {@link #getNew()} create from now
		 * on.
		 *
		 * @param cls the class of the driver, with a public constructor without parameters
		 * @throws FactoryException if the class has no constructor without parameters
		 */
		public static void setNativeDriverClass(Class<? extends Driver> cls) {
			setDriverClass("nativeDriverClass", cls);
		}

		/**
		 * Sets the class of the native drivers, which {@link #getNewNative()} and {@link #getNew()} create from now
		 * on, loading it through the class loader of this class.
		 *
		 * @param className the name of the class of the driver, with a public constructor without parameters
		 * @throws FactoryException if the class cannot be found or has no constructor without parameters
		 */
		public static void setNativeDriverClass(String className) {
			setDriverClass("nativeDriverClass", className);
		}

		/**
		 * Creates a default driver, an instance of the class set by the {@code driver-factory.default-driver.class}
		 * property or by {@link #setDefaultDriverClass(Class)}.
		 *
		 * @param <D> the type of the driver
		 * @return the new driver, not initialized: each of its functionalities is initialized the first time that it
		 * is used, unless {@link Driver#init()} is called
		 * @throws InstantiateException if the driver cannot be instantiated
		 */
		public static <D extends Driver> D getNewDefault() {
			try {
				return ((D)driverConstructors.get("defaultDriverClass").newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException exc) {
				throw new InstantiateException(exc);
			}
		}

		/**
		 * Creates a dynamic driver, an instance of the class set by the {@code driver-factory.dynamic-driver.class}
		 * property or by {@link #setDynamicDriverClass(Class)}.
		 *
		 * @param <D> the type of the driver
		 * @return the new driver, not initialized: each of its functionalities is initialized the first time that it
		 * is used, unless {@link Driver#init()} is called
		 * @throws InstantiateException if the driver cannot be instantiated
		 */
		public static <D extends Driver> D getNewDynamic() {
			try {
				return ((D)driverConstructors.get("dynamicDriverClass").newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException exc) {
				throw new InstantiateException(exc);
			}
		}

		/**
		 * Creates a hybrid driver, an instance of the class set by the {@code driver-factory.hybrid-driver.class}
		 * property or by {@link #setHybridDriverClass(Class)}.
		 *
		 * @param <D> the type of the driver
		 * @return the new driver, not initialized: each of its functionalities is initialized the first time that it
		 * is used, unless {@link Driver#init()} is called
		 * @throws InstantiateException if the driver cannot be instantiated
		 */
		public static <D extends Driver> D getNewHybrid() {
			try {
				return ((D)driverConstructors.get("hybridDriverClass").newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException exc) {
				throw new InstantiateException(exc);
			}
		}

		/**
		 * Creates a native driver, an instance of the class set by the {@code driver-factory.native-driver.class}
		 * property or by {@link #setNativeDriverClass(Class)}.
		 *
		 * @param <D> the type of the driver
		 * @return the new driver, not initialized: each of its functionalities is initialized the first time that it
		 * is used, unless {@link Driver#init()} is called
		 * @throws InstantiateException if the driver cannot be instantiated
		 */
		public static <D extends Driver> D getNewNative() {
			try {
				return ((D)driverConstructors.get("nativeDriverClass").newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException exc) {
				throw new InstantiateException(exc);
			}
		}

		/**
		 * Thrown when the factory cannot read its configuration or cannot use a class of driver: when it is thrown
		 * while the factory reads its configuration, the JVM wraps it in an {@link ExceptionInInitializerError}.
		 */
		public static class	FactoryException extends RuntimeException {

			private static final long serialVersionUID = 6332920978175279534L;

			/**
			 * Creates the exception.
			 *
			 * @param cause the cause of the exception
			 */
			public FactoryException(Throwable cause) {
		        super(cause);
		    }

		}
	}

	/**
	 * Thrown when a driver cannot be initialized, for example by {@link Driver#init()}: its cause is the exception
	 * that prevents the initialization. If a driver needs the {@link NativeEngine} and the engine cannot be created,
	 * the chain of the causes contains the {@link io.github.toolfactory.jvm.function.InitializeException} thrown by
	 * {@link NativeEngine#getInstance()}.
	 */
	public static class InitializeException extends RuntimeException {

		private static final long serialVersionUID = -1351844562568567842L;

		/**
		 * Creates the exception.
		 *
		 * @param message the message of the exception
		 * @param cause the cause of the exception
		 */
		public InitializeException(String message, Throwable cause) {
	        super(message, cause);
	    }

		/**
		 * Creates the exception.
		 *
		 * @param message the message of the exception
		 */
		public InitializeException(String message) {
	        super(message);
	    }

	}

	/**
	 * Thrown by the {@link Factory} when it cannot instantiate a driver: its cause is the exception thrown by the
	 * reflection API, such as the {@link InvocationTargetException} that wraps the exception thrown by the constructor
	 * of the driver.
	 */
	public static class	InstantiateException extends RuntimeException {

		private static final long serialVersionUID = 558903509767014098L;

		/**
		 * Creates the exception.
		 *
		 * @param cause the cause of the exception
		 */
		public InstantiateException(Throwable cause) {
	        super(cause);
	    }

	}

}