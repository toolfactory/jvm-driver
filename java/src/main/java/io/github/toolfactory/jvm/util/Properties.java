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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Properties {
	
	
	public static Map<Long, java.util.Properties> loadFromResources(String resRelPath, String propertyName, ClassLoader... classLoaders) throws IOException {
		Map<URL, InputStream> resources = new HashMap<URL, InputStream>();
		for (ClassLoader classLoader : classLoaders) {
			resources.putAll(Resources.getAsInputStreams(classLoader, resRelPath));
		}
		TreeMap<Long, java.util.Properties> orderedProperties = new TreeMap<>();
		for (Entry<URL, InputStream> entry : resources.entrySet()) {
		    java.util.Properties props = new java.util.Properties();
		    try (InputStream inputStream = entry.getValue()) {
			    props.load(entry.getValue());
			    String propValue = props.getProperty(propertyName);
			    if (propValue == null) {
			    	propValue = "1";
			    }
			    orderedProperties.put(Long.parseLong(propValue), props);
			   
		    }
		}
		return orderedProperties;
	}
	
	
	public static java.util.Properties loadFromResourceWithHigherPropertyValue(ClassLoader resourceClassLoader, String resRelPath, String propertyName, ClassLoader... classLoaders) throws IOException {
		Map<Long, java.util.Properties> orderedProperties = ((TreeMap<Long, java.util.Properties>)loadFromResources(resRelPath, propertyName, classLoaders)).descendingMap();
		return orderedProperties.entrySet().iterator().next().getValue();
	}
	
	public static java.util.Properties loadFromResourcesAndMerge(String resRelPath, String propertyName, ClassLoader... classLoaders) throws IOException {
		Map<Long, java.util.Properties> orderedProperties = loadFromResources(resRelPath, propertyName, classLoaders);
		java.util.Properties properties = new java.util.Properties();
		for (Entry<Long, java.util.Properties> entry : orderedProperties.entrySet()) {
			properties.putAll(entry.getValue());
		}
		properties.remove(propertyName);
		return properties;
	}
	
}
