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


import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeSet;

import io.github.toolfactory.jvm.Info;


@SuppressWarnings("all")
public class ObjectProvider {
	private final String classSuffix;
	private final static String CLASS_NAME;
	final Integer[] registeredVersions;
	
	static {
		CLASS_NAME = ObjectProvider.class.getName();
	}
	
	public ObjectProvider(String classSuffix, int... versions) {
		this.classSuffix = classSuffix;
		int jVMVersion = Info.Provider.getInfoInstance().getVersion();
		TreeSet<Integer> registeredVersions = new TreeSet<>();
		for (int i = 0; i < versions.length; i++) {
			if (jVMVersion >= versions[i]) {
				registeredVersions.add(versions[i]);
			}
		}
		this.registeredVersions = registeredVersions.descendingSet().toArray(new Integer[registeredVersions.size()]);
	}

	
	public <T> T getOrBuildObject(Class<? super T> clazz, Map<Object, Object> context) {
		String className = clazz.getName();
		Collection<String> searchedClasses = new LinkedHashSet<>();
		T object = getObject(clazz, context);		
		context.put(CLASS_NAME, this);
		for (int version : registeredVersions) {
			String clsName = className + classSuffix + version;
			try {
				Class<?> effectiveClass = null;
				try {
					effectiveClass = Class.forName(clsName);
				} catch (ClassNotFoundException exc) {
					searchedClasses.add(clsName);
					clsName = className + "$" +  classSuffix + version;
					effectiveClass = Class.forName(clsName);
				}
				object = (T) effectiveClass.getDeclaredConstructor(Map.class).newInstance(context);
				context.put(className, object);
				return object;
			} catch (ClassNotFoundException exc) {
				searchedClasses.add(clsName);
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