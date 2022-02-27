/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Luke Hutchison, Roberto Gentili
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
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Properties {


	public static Map<BigDecimal, java.util.Properties> loadFromResources(String resRelPath, String propertyName, ClassLoader... classLoaders) throws IOException, ParseException {
		Map<URL, InputStream> resources = Resources.getAsInputStreams(resRelPath, classLoaders);
		Map<BigDecimal, java.util.Properties> orderedProperties = new TreeMap<>();
		if (resources.isEmpty()) {
			return orderedProperties;
		}
		DecimalFormat decimalFormat = getNewDecimalFormat();
		Collection<java.util.Properties> propertiesWithoutPriority = new ArrayList<>();
		for (Entry<URL, InputStream> entry : resources.entrySet()) {
		    java.util.Properties props = new java.util.Properties();
		    try (InputStream inputStream = entry.getValue()) {
			    props.load(entry.getValue());
			    String propValue = props.getProperty(propertyName);
			    if (propValue != null && !propValue.trim().isEmpty()) {
			    	try {
						orderedProperties.put(stringToBigDecimal(propValue, decimalFormat), props);
					} catch (ParseException exc) {
						throw new IllegalArgumentException(
							Strings.compile(
								"The value '{}' of property named '{}' inside the file {} is incorrect",
								propValue, propertyName, entry.getKey().getPath()
							), exc
						);
					}
			    } else {
			    	propertiesWithoutPriority.add(props);
			    }
		    }
		}
		setPriorities((TreeMap<BigDecimal, java.util.Properties>) orderedProperties, propertiesWithoutPriority, decimalFormat);
		return orderedProperties;
	}


	public static java.util.Properties loadFromResourceWithHigherPropertyValue(
		String resRelPath,
		String propertyName,
		Collection<ClassLoader> classLoaders,
		Map<?, ?>... otherMaps
	) throws IOException, ParseException {
		TreeMap<BigDecimal, java.util.Properties> orderedProperties = ((TreeMap<BigDecimal, java.util.Properties>)loadFromResources(
			resRelPath,
			propertyName,
			classLoaders != null? classLoaders.toArray(new ClassLoader[classLoaders.size()]) : null
		));
		addOtherMaps(propertyName, orderedProperties, otherMaps);
		return orderedProperties.entrySet().iterator().next().getValue();
	}

	public static java.util.Properties loadFromResourcesAndMerge(
		String resRelPath,
		String propertyName,
		Collection<ClassLoader> classLoaders,
		Map<?, ?>... otherMaps
	) throws IOException, ParseException {
		Map<BigDecimal, java.util.Properties> orderedProperties = loadFromResources(
			resRelPath,
			propertyName,
			classLoaders != null? classLoaders.toArray(new ClassLoader[classLoaders.size()]) : null
		);
		addOtherMaps(propertyName, orderedProperties, otherMaps);
		java.util.Properties properties = new java.util.Properties();
		for (Entry<BigDecimal, java.util.Properties> entry : orderedProperties.entrySet()) {
			properties.putAll(entry.getValue());
		}
		properties.remove(propertyName);
		return properties;
	}


	private static void addOtherMaps(String propertyName, Map<BigDecimal, java.util.Properties> orderedProperties, Map<?, ?>... otherValueMaps) throws ParseException {
		if (otherValueMaps != null && otherValueMaps.length > 0) {
			DecimalFormat decimalFormat = getNewDecimalFormat();
			Collection<java.util.Properties> propertiesWithoutPriority = new ArrayList<>();
			for (Map<?, ?> otherValueMap : otherValueMaps) {
				String propValue = (String)otherValueMap.get(propertyName);
	    		java.util.Properties properties = new java.util.Properties();
	    		properties.putAll(otherValueMap);
			    if (propValue != null && !propValue.trim().isEmpty()) {
			    	try {
						orderedProperties.put(stringToBigDecimal(propValue, decimalFormat), properties);
					} catch (ParseException exc) {
						throw new IllegalArgumentException(
							Strings.compile(
								"The value '{}' of property named '{}' inside the map {} is incorrect",
								propValue, propertyName, otherValueMap
							), exc
						);
					}
			    } else {
			    	propertiesWithoutPriority.add(properties);
			    }
			}
			setPriorities((TreeMap<BigDecimal, java.util.Properties>) orderedProperties, propertiesWithoutPriority, decimalFormat);
		}
	}


	private static void setPriorities(TreeMap<BigDecimal, java.util.Properties> orderedProperties,
			Collection<java.util.Properties> propertiesWithoutPriority, DecimalFormat decimalFormat
	) throws ParseException {
		if (!propertiesWithoutPriority.isEmpty()) {
			BigDecimal currentMinPriority = stringToBigDecimal("1", decimalFormat);
			if (!orderedProperties.isEmpty()) {
				currentMinPriority = orderedProperties.firstKey();
			}
			for (java.util.Properties props : propertiesWithoutPriority) {
				orderedProperties.put(currentMinPriority.subtract(new BigDecimal(1)), props);
			}
		}
	}


	private static DecimalFormat getNewDecimalFormat() {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		DecimalFormat decimalFormat = new DecimalFormat("#.#", symbols);
		decimalFormat.setParseBigDecimal(true);
		return decimalFormat;
	}


	private static BigDecimal stringToBigDecimal(String value, DecimalFormat decimalFormat) throws ParseException {
		value =	value.trim();
		if (value.contains(".")) {
			String wholeNumber = value.substring(0, value.indexOf("."));
			String fractionalPart = value.substring(value.indexOf(".") + 1, value.length());
			fractionalPart = fractionalPart.replace(".", "");
			value = wholeNumber + "." + fractionalPart;
		}
		return ((BigDecimal)decimalFormat.parse(value));
	}


}
