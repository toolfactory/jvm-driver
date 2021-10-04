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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.github.toolfactory.jvm.Info;


@SuppressWarnings("all")
public class ObjectProvider {
	private final List<String> classNameItems;
	private final List<String> classNameOptionalItems;
	private final static String CLASS_NAME;
	
	static {
		CLASS_NAME = ObjectProvider.class.getName();
	}
	
	public ObjectProvider(int... versions) {
		this.classNameItems = new ArrayList();
		this.classNameOptionalItems = new ArrayList();
		String vendor = System.getProperty("java.vendor");
		if (vendor.equals("International Business Machines Corporation")) {
			classNameOptionalItems.add("ForSemeru");
		}
		int jVMVersion = Info.Provider.getInfoInstance().getVersion();
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
		String className = clazz.getName();
		Collection<String> searchedClasses = new LinkedHashSet<>();
		T object = getObject(clazz, context);		
		context.put(CLASS_NAME, this);
		List<String> classNameOptionalItems = new ArrayList<String>(this.classNameOptionalItems);
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
		if (superClass != null && !superClass.equals(Object.class)) {
			try {
				return (T)getOrBuildObject((Class<? super T>)superClass, context);
			} catch (BuildingException exc) {
				throw new BuildingException(
					"Unable to build the related object of " + clazz.getName() + ": " + Strings.join(", ", searchedClasses) + " have been searched without success",
					exc
				);
			}
		} else {
			throw new BuildingException(
				"Unable to build the related object of " + clazz.getName() + ": " + Strings.join(", ", searchedClasses) + " have been searched without success"
			);
		}
	}

	
	private <T> Class<? super T> retrieveClass(
		List<String> classNameItems,
		Collection<String> notFoundClasses,
		String separator
	) throws ClassNotFoundException {
		Collection<String> allClassNameCombinations = retrieveAllClassNameCombinations(classNameItems, separator);
		for (String className : allClassNameCombinations) {
			try {
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
	
//	private  <T> Class<? super T> retrieveClass(String className, Collection<String> notFoundClasses) {
//		try {
//			return (Class<? super T>)Class.forName(className);
//		} catch (ClassNotFoundException exc) {
//			notFoundClasses.add(className);
//		}
//		return null;
//	}
	
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
	
	
	public static class BuildingException extends RuntimeException {

	    public BuildingException(String message, Throwable cause) {
	        super(message, cause);
	    }
	    
	    public BuildingException(String message) {
	        super(message);
	    }

	}
}