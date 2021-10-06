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
package io.github.toolfactory.jvm.util;


import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.toolfactory.jvm.Info;
import io.github.toolfactory.jvm.function.template.Supplier;


@SuppressWarnings("all")
public class ObjectProvider {
	private final List<String> classNameItems;
	private final static String CLASS_NAME;
	private int jVMVersion;
	private String vendor;
	
	static {
		CLASS_NAME = ObjectProvider.class.getName();
	}
	
	public ObjectProvider(int... versions) {
		this.classNameItems = new CopyOnWriteArrayList<String>();
		jVMVersion = Info.Provider.getInfoInstance().getVersion();
		vendor = System.getProperty("java.vendor");
		TreeSet<Integer> registeredVersions = new TreeSet<>();
		for (int i = 0; i < versions.length; i++) {
			if (jVMVersion >= versions[i]) {
				registeredVersions.add(versions[i]);
			}
		}
		for (Integer version : registeredVersions.descendingSet().toArray(new Integer[registeredVersions.size()])) {
			classNameItems.add("ForJava" + version);
		}
	}
	
	
	public <T> T getOrBuildObject(Class<? super T> clazz, Map<Object, Object> context) {
		Map<String, Throwable> exceptions = new HashMap<String, Throwable>();
		if (context.get("classNameOptionalItems") == null) {
			synchronized (context) {
				if (context.get("classNameOptionalItems") == null) {
					context.put("classNameOptionalItems", new ArrayList<String>());
					if (vendor.equals("International Business Machines Corporation")) {
						putClassNameOptionalItem((List<String>)context.get("classNameOptionalItems"), "ForSemeru");
					}
				}
			}
		}		
		try {
			try {
				return getOrBuildObjectInternal(clazz, context);
			} catch (BuildingException exc) {
				if (putClassNameOptionalItem((List<String>)context.get("classNameOptionalItems"), "ForSemeru")) {
					exceptions.put("default", exc);
				} else {
					exceptions.put("International Business Machines Corporation", exc);
				}				
			}
			return getOrBuildObjectInternal(clazz, context);
		} catch (BuildingException exc) {
			exceptions.put("International Business Machines Corporation", exc);
		}
		throw new BuildingException(
			Strings.compile(
				"Exception occurred (jvm architecture: {}, jvm version: {}, jvm vendor: {})",
				Info.Provider.getInfoInstance().is64Bit() ? "x64" : "x86",
				jVMVersion, vendor
			),
			(BuildingException)exceptions.getOrDefault(
				vendor,
				exceptions.get("default")
			)
		);
	}


	boolean putClassNameOptionalItem(List<String> classNameOptionalItems, String value) {
		if (!classNameOptionalItems.contains(value)) {
			synchronized (classNameOptionalItems) {
				if (!classNameOptionalItems.contains(value)) {
					classNameOptionalItems.clear();
					classNameOptionalItems.add(value);
					return true;
				}
			}
		}
		return false;
	}
	
	
	private <T> T getOrBuildObjectInternal(Class<? super T> clazz, Map<Object, Object> context) {
		String className = clazz.getName();
		Collection<String> searchedClasses = new LinkedHashSet<>();
		T object = getObject(clazz, context);	
		if (object != null) {
			return object;
		}
		context.put(CLASS_NAME, this);
		List<String> classNameOptionalItems = (List<String>)context.get("classNameOptionalItems");
		List<String> classNameItems = Arrays.asList(new String[classNameOptionalItems.size() + 2]);
		classNameItems.set(0, className);
		for (int i = 0; i < classNameOptionalItems.size(); i++) {
			classNameItems.set(i + 2, classNameOptionalItems.get(i));
		}
		classNameItems = new ArrayList<String>(classNameItems);
		while (classNameItems.size() > 1) {
			for (String classNameItem : this.classNameItems) {
				try {
					classNameItems.set(1, classNameItem);
					object = (T)retrieveClass(classNameItems, searchedClasses, "$").getDeclaredConstructor(Map.class).newInstance(context);
					context.put(className, object);
					return object;
				} catch (ClassNotFoundException exc) {
					continue;
				} catch (Throwable exc) {
					throw new BuildingException("Unable to build the related object of " + clazz.getName(), exc);
				}
			}			
			classNameItems.remove(classNameItems.size() -1);
		}
		
		if (!Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface()) {
			try {
				return (T) clazz.getDeclaredConstructor(Map.class).newInstance(context);
			} catch (Throwable exc) {
				throw new BuildingException("Unable to build the related object of " + clazz.getName(), exc);
			}
		}
		Class<?> superClass = clazz.getSuperclass();
		Class<?>[] interfaces;
		if (superClass != null && !superClass.equals(Object.class)) {
			try {
				return (T)getOrBuildObject((Class<? super T>)superClass, context);
			} catch (BuildingException exc) {
				throw new BuildingException(
					"Unable to build the related object of " + clazz.getName() + ": " + Strings.join(", ", searchedClasses) + " have been searched without success",
					exc
				);
			}
		} else if((interfaces = clazz.getInterfaces()).length > 0) {
			for (Class<?> interf : interfaces) {
				try {
					return (T)getOrBuildObject((Class<? super T>)interf, context);
				} catch (BuildingException exc) {

				}
			}
		}
		throw new BuildingException(
			"Unable to build the related object of " + clazz.getName() + ": " + Strings.join(", ", searchedClasses) + " have been searched without success"
		);
	}

	
	private <T> Class<? super T> retrieveClass(
		List<String> classNameItems,
		Collection<String> notFoundClasses,
		String separator
	) throws ClassNotFoundException {
		Collection<String> allClassNameCombinations = retrieveAllClassNameCombinations(classNameItems, separator);
		for (String className : allClassNameCombinations) {
			try {
				Class<? super T> cls = (Class<? super T>)Class.forName(className);
				if (Modifier.isAbstract(cls.getModifiers()) || cls.isInterface()) {
					notFoundClasses.add(className);
					continue;
				}
				return (Class<? super T>)Class.forName(className);
			} catch (ClassNotFoundException exc) {
				notFoundClasses.add(className);
			}
		}
		throw new ClassNotFoundException();
	}


	Collection<String> retrieveAllClassNameCombinations(List<String> classNameItems, String separator) {
		List<String> finalStringColl = new ArrayList<String>();
		Set<String> classNames = new LinkedHashSet<String>();
		Collection<List<String>> combinationsToBeReprocessed = new ArrayList<>(); 
		for (int i = classNameItems.size(); i > 0; i--) {
			List<String> firstPartList = classNameItems.subList(0, i);
			String firstPart = Strings.join("", firstPartList);
			if (!firstPartList.isEmpty()) {
				finalStringColl.add(firstPart);
			}
			List<String> secondPartList = classNameItems.subList(i, classNameItems.size());
			String secondPart = Strings.join("", secondPartList);
			if (!secondPartList.isEmpty()) {
				finalStringColl.add(secondPart);
			}
			classNames.add(Strings.join(separator, finalStringColl));
			if (secondPartList.size() > 1) {
				List<String> toBeReprocessed = new ArrayList<String>();
				String firstPartOfSecondPart = secondPartList.get(0);
				toBeReprocessed.add(firstPart + separator + firstPartOfSecondPart);
				toBeReprocessed.addAll(secondPartList.subList(1, secondPartList.size()));
				combinationsToBeReprocessed.add(toBeReprocessed);
			}
			finalStringColl.clear();
		}
		for (List<String> toBeReprocessed : combinationsToBeReprocessed) {
			classNames.addAll(retrieveAllClassNameCombinations(toBeReprocessed, separator));
		}
		return classNames;
	}
	
	public static <F> F getObject(Class<? super F> clazz, Map<Object, Object> context) {
		F objectFound = (F) context.get(clazz.getName());
		if (objectFound != null) {
			return objectFound;
		} else {
			for (Object object : context.values()) {
				if (clazz.isAssignableFrom(object.getClass())) {
					return (F)object;
				}
			}
		}
		return null;
	}
	
	public static ObjectProvider get(Map<Object, Object> context) {
		return (ObjectProvider)context.get(CLASS_NAME);
	}
	
	public static void putIfAbsent(Map<Object, Object> context, Supplier<ObjectProvider> objectProvider) {
		ObjectProvider objectProviderInMap = (ObjectProvider) context.get(CLASS_NAME);
		if (objectProviderInMap == null) {
			synchronized(context) {
				objectProviderInMap = (ObjectProvider)context.get(CLASS_NAME);
				if (objectProviderInMap == null) {
					context.put(CLASS_NAME, objectProvider.get());
				}				
			}
		}
	}
	
	
	public static class BuildingException extends RuntimeException {

	    public BuildingException(String message, Throwable cause) {
	        super(message, cause);
	    }
	    
	    public BuildingException(String message) {
	        super(message);
	    }

	}
}