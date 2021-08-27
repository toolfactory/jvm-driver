# Burningwave JVM driver [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=%40Burningwave_fw%20JVM%20driver%2C%20a%20%23driver%20to%20allow%20deep%20interaction%20with%20the%20JVM%20%28works%20on%20%23Java8%20%23Java9%20%23Java10%20%23Java11%20%23Java12%20%23Java13%20%23Java14%20%23Java15%20%23Java16%20%23Java17%29&url=https://github.com/burningwave/jvm-driver#burningwave-jvm-driver-)

<a href="https://www.burningwave.org">
<img src="https://raw.githubusercontent.com/burningwave/core/master/Burningwave-logo.png" alt="Burningwave-logo.png" height="180px" align="right"/>
</a>

[![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/org.burningwave/jvm-driver/0)](https://maven-badges.herokuapp.com/maven-central/org.burningwave/jvm-driver/)
[![GitHub](https://img.shields.io/github/license/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/blob/main/LICENSE)

[![Platforms](https://img.shields.io/badge/platforms-Windows%2C%20Max%20OS%2C%20Linux-orange)](https://github.com/burningwave/jvm-driver/actions/runs/1161537104)

[![Supported JVM](https://img.shields.io/badge/supported%20JVM-8%2C%209%2C%2010%2C%2011%2C%2012%2C%2013%2C%2014%2C%2015%2C%2016%2C%2017-blueviolet)](https://github.com/burningwave/jvm-driver/actions/runs/1161537104)


A driver to allow deep interaction with the JVM **without any restrictions**.

## Compilation requirements

**A JDK version 9 or higher is required to compile the project** and the property 'project_jdk_version' inside pom.xml must be set to 8.

## Using

To create a Driver instance you should use this code
```java
Driver driver = new DefaultDriver();
```

The methods exposed by the driver are the following:
```java                                                                                                     
public void setFieldValue(Object target, Field field, Object value);                                    
                                                                                                        
public <T> T getFieldValue(Object target, Field field);                                                 
                                                                                                        
public Method[] getDeclaredMethods(Class<?> cls);                                                       
                                                                                                        
public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);                                      
                                                                                                        
public Field[] getDeclaredFields(Class<?> cls);                                                         
                                                                                                        
public Field getDeclaredField(Class<?> cls, String name);                                               
                                                                                                        
public <T> T newInstance(Constructor<T> ctor, Object[] params);                                         
                                                                                                        
public Object invoke(Method method, Object target, Object[] params);                                    
                                                                                                        
public Lookup getConsulter(Class<?> cls);                                                               
                                                                                                        
public Class<?> getClassLoaderDelegateClass();                                                          
                                                                                                        
public Class<?> getBuiltinClassLoaderClass();                                                           
                                                                                                        
public boolean isClassLoaderDelegate(ClassLoader classLoader);                                          
                                                                                                        
public boolean isBuiltinClassLoader(ClassLoader classLoader);                                           
                                                                                                        
public Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader);                                  
                                                                                                        
public Collection<Class<?>> retrieveLoadedClasses(ClassLoader classLoader);                             
                                                                                                        
public Package retrieveLoadedPackage(ClassLoader classLoader, Object packageToFind, String packageName);
                                                                                                        
public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode);                                 
                                                                                                        
public void setAccessible(AccessibleObject object, boolean flag);                                       
                                                                                                        
public <T> T allocateInstance(Class<?> cls);                                                            
```

