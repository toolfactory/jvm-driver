# ToolFactory JVM Driver [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=%40ToolFactory_fw%20JVM%20driver%2C%20a%20%23Java%20driver%20to%20allow%20deep%20interaction%20with%20the%20JVM%20without%20any%20restrictions&url=https://toolfactory.github.io/jvm-driver/)

<a href="https://github.com/toolfactory">
<img src="https://raw.githubusercontent.com/toolfactory/jvm-driver/master/docs/logo.png" alt="logo.png" height="180px" align="right"/>
</a>

[![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/io.github.toolfactory/jvm-driver/10)](https://central.sonatype.com/artifact/io.github.toolfactory/jvm-driver)
[![GitHub](https://img.shields.io/github/license/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/blob/main/LICENSE)

[![Platforms](https://img.shields.io/badge/platforms-Windows%2C%20Mac%20OS%2C%20Linux-orange)](https://github.com/toolfactory/jvm-driver/actions/runs/23859879109)

[![Supported JVM](https://img.shields.io/badge/supported%20JVM-7%2C%208%2C%209+%20(26)-blueviolet)](https://github.com/toolfactory/jvm-driver/actions/runs/23859879109)

[![GitHub open issues](https://img.shields.io/github/issues/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/issues?q=is%3Aissue+is%3Aclosed)

[![Repository dependents](https://badgen.net/github/dependents-repo/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/network/dependents)

Extracted from [**Burningwave Core 8**](https://github.com/burningwave/core/releases/tag/core-8.22.0), fully refactored and with an **extensible architecture** the JVM Driver allows a deep interaction with the JVM **without any restrictions**.

<br/>

To include ToolFactory JVM Driver in your projects simply use with **Apache Maven**:
```xml
<dependency>
    <groupId>io.github.toolfactory</groupId>
    <artifactId>jvm-driver</artifactId>
    <version>10.0.0</version>
</dependency>	
```
### Requiring the ToolFactory JVM Driver module

To use ToolFactory JMV Driver as a Java module, add the following to your `module-info.java`: 

```java
//Mandatory if you use the dynamic, hybrid or native driver with the default JNI engine
requires org.burningwave.jni;

//Mandatory if you use the default, dynamic or hybrid driver
requires jdk.unsupported;

requires io.github.toolfactory.jvm;
```

<br/>

## What's new in version 10.0.0

* The dynamic, hybrid and native drivers use a new default JNI engine: [the one of **Burningwave**](https://github.com/burningwave/jvm-driver/tree/main/jni-executor), supplied by the `org.burningwave:jni-executor` artifact, which supports more platforms than Narcissus and [is faster](https://github.com/toolfactory/narcissus/discussions/11). [**Narcissus**](https://toolfactory.github.io/narcissus/) is no longer a dependency of ToolFactory JVM Driver: to keep using it, [add the Narcissus plugin](#going-back-to-narcissus).
* The JNI engine is no longer bound to the drivers: the drivers use the engine chosen through the `native-engine.class` property of the `jvm-driver.properties` files, an implementation of the new abstract class [`io.github.toolfactory.jvm.NativeEngine`](https://github.com/toolfactory/jvm-driver/blob/main/java/src/main/java/io/github/toolfactory/jvm/NativeEngine.java). If the chosen engine cannot be loaded, the drivers that need it cannot be initialized: no other engine is tried.
* On the module path, the dynamic, hybrid and native drivers need `requires org.burningwave.jni;` instead of `requires io.github.toolfactory.narcissus;`.
* The `jvm-driver.properties` files with the same value of `priority-of-this-configuration` are merged: before, only one of them was used.
* The module `io.github.toolfactory.jvm` exports the package `io.github.toolfactory.jvm.function`, which contains the `InitializeException` thrown by the catalog of the functions and by `NativeEngine.getInstance()`.

### Going back to Narcissus

To make the drivers use Narcissus, the JNI engine used before version 10.0.0, add the **Narcissus plugin**:
```xml
<dependency>
    <groupId>io.github.toolfactory</groupId>
    <artifactId>jvm-driver-narcissus-plugin</artifactId>
    <version>10.0.0</version>
</dependency>
```
Nothing else is needed: the plugin contains a `jvm-driver.properties` file with priority 1 that sets the engine to `io.github.toolfactory.jvm.narcissus.NarcissusEngine`. The plugin depends on ToolFactory JVM Driver and on Narcissus 1.0.13: to leave out the Burningwave engine, exclude `org.burningwave:jni-executor` from its dependencies.

On the module path, require the module of the plugin, which in turn requires the modules of ToolFactory JVM Driver and of Narcissus:
```java
requires io.github.toolfactory.jvm.narcissus;
```

### Choosing the JNI engine

The engine is the class named by the `native-engine.class` property of the `jvm-driver.properties` file with the highest priority that sets it: ToolFactory JVM Driver sets it to `io.github.toolfactory.jvm.BurningwaveEngine` with priority 0 and the Narcissus plugin to `io.github.toolfactory.jvm.narcissus.NarcissusEngine` with priority 1. To choose an engine regardless of the plugins in the class path, add to your project a `jvm-driver.properties` file with a higher priority, for example:
```properties
priority-of-this-configuration=2
native-engine.class=io.github.toolfactory.jvm.BurningwaveEngine
```
It can also name your own engine: a public class that extends `io.github.toolfactory.jvm.NativeEngine` and has a public constructor without parameters.

<br/>

## Overview

There are four kinds of driver:

* the **default driver** completely based on Java api
* the **dynamic driver** that extends the default driver and uses a JNI function only if a Java based function offered by the default driver cannot be initialized
* the **hybrid driver** that extends the default driver and uses some JNI functions only when run on JVM 17 and later
* the **native driver** that extends the hybrid driver and uses JNI functions more consistently regardless of the Java version it is running on

By default all JNI methods used by the dynamic, hybrid and native driver are supplied by [the JNI engine of **Burningwave**](https://github.com/burningwave/jvm-driver/tree/main/jni-executor) that works on the following system configurations:
* Windows (x86, x64)
* Linux (x86, x64, aarch64)
* macOS (x64, aarch64)

They can be supplied by [**Narcissus**](https://toolfactory.github.io/narcissus/) instead, through [the Narcissus plugin](#going-back-to-narcissus): Narcissus 1.0.13 works on Windows (x64), Linux (x64, arm64) and macOS (x64, arm64).

### Prefer the default driver

The default driver only uses Java APIs and it is faster than the drivers that use JNI functions: the JIT compiles its operations like any other Java code, while every operation that the dynamic, hybrid and native drivers delegate to a JNI function pays the cost of a native call. In [a JMH benchmark run on JDK 18](https://github.com/toolfactory/narcissus/discussions/11) the default driver read a field at 41,581 ops/ms against the 16,982 ops/ms of the native driver, and wrote it at 37,480 ops/ms against 19,672 ops/ms.

The JNI functions exist to get around the strong encapsulation of the JDK, not to be faster: use the dynamic, hybrid or native driver only when the default driver cannot be initialized on your JVM. `Driver.Factory.getNew()` already works this way, since it returns the first driver that can be initialized among the default, hybrid and native drivers.

<br/>

## Usage

To create a driver instance you should use this code:
```java
io.github.toolfactory.jvm.Driver driver = io.github.toolfactory.jvm.Driver.getNew();
```

The driver type returned by the method `io.github.toolfactory.jvm.Driver.Factory.getNew()` is **the first driver that can be initialized among the default, hybrid and native drivers respectively**.

If you need to create a specific driver type you should use:

* this code to create a default driver instance:

```java
io.github.toolfactory.jvm.Driver driver = io.github.toolfactory.jvm.Driver.Factory.getNewDefault();
```

* this code to create a dynamic driver instance:

```java
io.github.toolfactory.jvm.Driver driver = io.github.toolfactory.jvm.Driver.Factory.getNewDynamic();
```

* this code to create an hybrid driver instance:

```java
io.github.toolfactory.jvm.Driver driver = io.github.toolfactory.jvm.Driver.Factory.getNewHybrid();
```

* this code to create a native driver instance:

```java
io.github.toolfactory.jvm.Driver driver = io.github.toolfactory.jvm.Driver.Factory.getNewNative();
```

<br/>

Each functionality offered by the driver is **initialized in deferred way** at the first call if the driver is not obtained through the method `io.github.toolfactory.jvm.Driver.getNew()`. However, it is possible to initialize all of the functionalities at once by calling the method `Driver.init()`.

The methods exposed by the Driver interface are the following:
```java
public <D extends Driver> D init();

public <T> T allocateInstance(Class<?> cls);

// Return a ClassLoaderDelegate or the input itself if the input is a 
// BuiltinClassLoader or null if the JVM version is less than 9
public ClassLoader convertToBuiltinClassLoader(ClassLoader classLoader);

public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode);

public Class<?> getBuiltinClassLoaderClass();

public Class<?> getClassLoaderDelegateClass();

public Class<?> getClassByName(String className, Boolean initialize, ClassLoader classLoader, Class<?> caller);

public MethodHandles.Lookup getConsulter(Class<?> cls);

public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);

public Field[] getDeclaredFields(Class<?> cls);

public Method[] getDeclaredMethods(Class<?> cls);

public <T> T getFieldValue(Object target, Field field);

public Package getPackage(ClassLoader classLoader, String packageName);

public Collection<URL> getResources(String resourceRelativePath, boolean findFirst, ClassLoader... classLoaders);

public Collection<URL> getResources(String resourceRelativePath, boolean findFirst, Collection<ClassLoader> classLoaders);

public <T> T invoke(Object target, Method method, Object[] params);

public boolean isBuiltinClassLoader(ClassLoader classLoader);

public boolean isClassLoaderDelegate(ClassLoader classLoader);

public <T> T newInstance(Constructor<T> ctor, Object[] params);

public CleanableSupplier<Collection<Class<?>>> getLoadedClassesRetriever(ClassLoader classLoader);

public Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader);

public void setAccessible(AccessibleObject object, boolean flag);

public void setFieldValue(Object target, Field field, Object value);

public void stop(Thread thread);

public <T> T throwException(String message, Object... placeHolderReplacements);

public <T> T throwException(Throwable exception);
```

<br/>


In the [test folder](https://github.com/toolfactory/jvm-driver/tree/main/java/src/test/java/io/github/toolfactory/util) is also present a little utility class named [`io.github.toolfactory.util.Reflection`](https://github.com/toolfactory/jvm-driver/blob/main/java/src/test/java/io/github/toolfactory/util/Reflection.java) that can be copied into your project and that can be instantiated through the factory methods exposed by the inner static class `io.github.toolfactory.util.Reflection.Factory`:

```java
public static Reflection getNew();

public static Reflection getNewWithDefaultDriver();

public static Reflection getNewWithDynamicDriver();

public static Reflection getNewWithHybridDriver();

public static Reflection getNewWithNativeDriver();

public static Reflection getNewWith(Driver driver);
```

The methods exposed by the `io.github.toolfactory.util.Reflection` component are the following:
```java
public Driver getDriver();

public Collection<Method> getDeclaredMethods(Class<?> cls);

public Collection<Method> getAllMethods(Class<?> cls);

public <T> T getFieldValue(Object target, Field field);

public void setFieldValue(Object target, Field field, Object value);

public Field getDeclaredField(Class<?> cls, String name);

public Collection<Field> getDeclaredFields(Class<?> cls);

public Collection<Field> getAllFields(Class<?> cls);

public Collection<Constructor<?>> getDeclaredConstructors(Class<?> cls);

public Collection<Constructor<?>> getAllConstructors(Class<?> cls);
```

<br />

## Compilation requirements

**A JDK version 9 or higher is required to compile the project**.
<br />

# <a name="Ask-for-assistance"></a>Ask for assistance
**For assistance you can**:
* [open a discussion](https://github.com/toolfactory/jvm-driver/discussions) here on GitHub
* [report a bug](https://github.com/toolfactory/jvm-driver/issues) here on GitHub
* ask on [Stack Overflow](https://stackoverflow.com/questions/ask)
