/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2023 Luke Hutchison, Roberto Gentili
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
import java.util.LinkedHashMap;
import java.util.Map;


public class Resources {

	public static Map<URL, InputStream> getAsInputStreams(String resourceRelativePath, ClassLoader... resourceClassLoaders) throws IOException {
		if (resourceClassLoaders == null || resourceClassLoaders.length == 0) {
			resourceClassLoaders = new ClassLoader[]{Thread.currentThread().getContextClassLoader()};
		}
		Map<URL, InputStream> streams = new LinkedHashMap<>();
		for (ClassLoader classLoader : resourceClassLoaders) {
			Enumeration<URL> resources = classLoader.getResources(resourceRelativePath);
			while (resources.hasMoreElements()) {
				URL resourceURL = resources.nextElement();
				streams.put(
					resourceURL,
					resourceURL.openStream()
				);
			}
		}
		return streams;
	}

}
