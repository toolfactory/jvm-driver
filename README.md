# ToolFactory JVM Driver [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=%40ToolFactory_fw%20JVM%20driver%2C%20a%20%23driver%20to%20allow%20deep%20interaction%20with%20the%20JVM%20without%20any%20restrictions%20%28works%20on%20%23Java7%20%23Java8%20%23Java9%20%23Java10%20%23Java11%20%23Java12%20%23Java13%20%23Java14%20%23Java15%20%23Java16%20%23Java17%29&url=https://toolfactory.github.io/jvm-driver/)

<a href="https://github.com/toolfactory">
<img src="https://raw.githubusercontent.com/toolfactory/jvm-driver/master/docs/logo.png" alt="logo.png" height="180px" align="right"/>
</a>

[![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/io.github.toolfactory/jvm-driver/8)](https://maven-badges.herokuapp.com/maven-central/io.github.toolfactory/jvm-driver/)
[![GitHub](https://img.shields.io/github/license/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/blob/main/LICENSE)

[![Platforms](https://img.shields.io/badge/platforms-Windows%2C%20Mac%20OS%2C%20Linux-orange)](https://github.com/toolfactory/jvm-driver/actions/runs/1375290074)

[![Supported JVM](https://img.shields.io/badge/supported%20JVM-7%2C%208%2C%209+%20(17)-blueviolet)](https://github.com/toolfactory/jvm-driver/actions/runs/1375290074)

[![GitHub open issues](https://img.shields.io/github/issues/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/issues?q=is%3Aissue+is%3Aclosed)

[![Repository dependents](https://badgen.net/github/dependents-repo/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/network/dependents)

A driver with a **fully extensible architecture** to allow deep interaction with the JVM **without any restrictions**.

<br/>

To include ToolFactory JVM Driver in your projects simply use with **Apache Maven**:
```xml
<dependency>
    <groupId>io.github.toolfactory</groupId>
    <artifactId>jvm-driver</artifactId>
    <version>8.2.2</version>
</dependency>	
```
### Requiring the ToolFactory JMV Driver module

To use ToolFactory JMV Driver as a Java module, add the following to your `module-info.java`: 

```
requires io.github.toolfactory.jvm;
```

<br/>

## Overview

There are four kinds of driver:

* the **default driver** completely based on Java api
* the **dynamic driver** that extends the default driver and uses a JNI function only if a Java based function offered by the default driver cannot be initialized
* the **hybrid driver** that extends the default driver and uses some JNI functions only when run on JVM 17 and later
* the **native driver** that extends the hybrid driver and uses JNI functions more consistently regardless of the Java version it is running on

All JNI methods used by the dynamik, hybrid and native driver are supplied by [**narcissus**](https://toolfactory.github.io/narcissus/) that works on the following system configurations:
* Windows (x86, x64)
* Linux (x86, x64)
* MacOs (x64) 

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

public <T> T throwException(Object exceptionOrMessage, Object... placeHolderReplacements);                                                         
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
