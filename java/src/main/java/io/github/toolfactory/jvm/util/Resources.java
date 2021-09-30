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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class Resources {

	public static InputStream getAsInputStream(ClassLoader resourceClassLoader, String resourceRelativePath) {
		if (resourceClassLoader == null) {
			resourceClassLoader = ClassLoader.getSystemClassLoader();
		}
		return resourceClassLoader.getResourceAsStream(resourceRelativePath);
	}
	
	public static Map<URL, InputStream> getAsInputStreams(ClassLoader resourceClassLoader, String resourceRelativePath) throws IOException {
		if (resourceClassLoader == null) {
			resourceClassLoader = ClassLoader.getSystemClassLoader();
		}
		Map<URL, InputStream> streams = new HashMap<>();
		Enumeration<URL> resources = resourceClassLoader.getResources(resourceRelativePath);
		while (resources.hasMoreElements()) {
			URL resourceURL = resources.nextElement();
			streams.put(
				resourceURL,
				resourceURL.openStream()
			);
		}
		
		return streams;
	}

}
