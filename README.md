# ToolFactory JVM driver [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=%40ToolFactory_fw%20JVM%20driver%2C%20a%20%23driver%20to%20allow%20deep%20interaction%20with%20the%20JVM%20without%20any%20restrictions%20%28works%20on%20%23Java8%20%23Java9%20%23Java10%20%23Java11%20%23Java12%20%23Java13%20%23Java14%20%23Java15%20%23Java16%20%23Java17%29&url=https://toolfactory.github.io/jvm-driver/)

<a href="https://github.com/toolfactory">
<img src="https://raw.githubusercontent.com/toolfactory/jvm-driver/master/docs/logo.png" alt="logo.png" height="180px" align="right"/>
</a>

[![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/io.github.toolfactory/jvm-driver/2)](https://maven-badges.herokuapp.com/maven-central/io.github.toolfactory/jvm-driver/)
[![GitHub](https://img.shields.io/github/license/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/blob/main/LICENSE)

[![Platforms](https://img.shields.io/badge/platforms-Windows%2C%20Mac%20OS%2C%20Linux-orange)](https://github.com/toolfactory/jvm-driver/actions/runs/1267691358)

[![Supported JVM](https://img.shields.io/badge/supported%20JVM-8%2C%209%2C%2010%2C%2011%2C%2012%2C%2013%2C%2014%2C%2015%2C%2016%2C%2017-blueviolet)](https://github.com/toolfactory/jvm-driver/actions/runs/1267691358)

[![GitHub open issues](https://img.shields.io/github/issues/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/issues?q=is%3Aissue+is%3Aclosed)

[![Repository dependents](https://badgen.net/github/dependents-repo/toolfactory/jvm-driver)](https://github.com/toolfactory/jvm-driver/network/dependents)

A driver to allow deep interaction with the JVM **without any restrictions**.

<br/>

To include ToolFactory JVM driver in your projects simply use with **Apache Maven**:
```xml
<dependency>
    <groupId>io.github.toolfactory</groupId>
    <artifactId>jvm-driver</artifactId>
    <version>2.0.4</version>
</dependency>	
```

<br/>

## Overview

There are two kinds of driver:

* the **default driver** completely based on Java api
* the **hybrid driver** that extends the default driver and uses some custom JNI methods through [narcissus](https://toolfactory.github.io/narcissus/) that works on the following system configurations:
    * Windows (x86, x64)
    * Linux (x86, x64)
    * MacOs (x64)

<br/>

## Using

To create a default driver instance you should use this code:
```java

io.github.toolfactory.jvm.Driver driver = new io.github.toolfactory.jvm.DefaultDriver();
```

To create a hybrid driver instance you should use this code:
```java

io.github.toolfactory.jvm.Driver driver = new io.github.toolfactory.jvm.HybridDriver();
```

The methods exposed by the Driver interface are the following:
```java                                                                                                     
public void setFieldValue(Object target, Field field, Object value);                                    
                                                                                                        
public <T> T getFieldValue(Object target, Field field);                                                 
                                                                                                        
public Method[] getDeclaredMethods(Class<?> cls);                                                       
                                                                                                        
public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);                                      
                                                                                                        
public Field[] getDeclaredFields(Class<?> cls);                                                         
                                                                                                        
public Field getDeclaredField(Class<?> cls, String name);                                               
                                                                                                        
public <T> T newInstance(Constructor<T> ctor, Object[] params);                                         
                                                                                                        
public Object invoke(Method method, Object target, Object[] params);                                    
                                                                                                        
public MethodHandles.Lookup getConsulter(Class<?> cls);                                                               
                                                                                                        
public Class<?> getClassLoaderDelegateClass();                                                          
                                                                                                        
public Class<?> getBuiltinClassLoaderClass();                                                           
                                                                                                        
public boolean isClassLoaderDelegate(ClassLoader classLoader);                                          
                                                                                                        
public boolean isBuiltinClassLoader(ClassLoader classLoader);                                           
                                                                                                        
public Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader);                                  
                                                                                                        
public Collection<Class<?>> retrieveLoadedClasses(ClassLoader classLoader);                             
                                                                                                        
public Package getPackage(ClassLoader classLoader, String packageName);
                                                                                                        
public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode);                                 
                                                                                                        
public void setAccessible(AccessibleObject object, boolean flag);                                       
                                                                                                        
public <T> T allocateInstance(Class<?> cls);                                                            
```

<br/>

## Compilation requirements

**A JDK version 9 or higher is required to compile the project**.
<br />

# <a name="Ask-for-assistance"></a>Ask for assistance
**For assistance you can**:
* [open a discussion](https://github.com/toolfactory/jvm-driver/discussions) here on GitHub
* [report a bug](https://github.com/toolfactory/jvm-driver/issues) here on GitHub
* ask on [Stack Overflow](https://stackoverflow.com/search?q=toolfactory)
