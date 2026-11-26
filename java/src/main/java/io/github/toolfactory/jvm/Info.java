/*
 * This file is derived from ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * Modified by: Roberto Gentili
 *
 * Modifications hosted at: https://github.com/burningwave/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
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
package io.github.toolfactory.jvm;

/**
 * Information about the running JVM, read from its system properties and, for the compressed ordinary object
 * pointers, from {@code com.sun.management.HotSpotDiagnosticMXBean}. The instance is supplied by {@link Provider}.
 */
public interface Info {
	/**
	 * The Java versions from which the functions of the drivers can change implementation: for each function the
	 * drivers use the implementation written for the highest of these versions that is not greater than the running
	 * one.
	 */
	public final static int[] CRITICAL_VERSIONS = {7, 9, 14, 17, 20, 21, 22, 24, 25, 26};


	/**
	 * Tells whether the JVM is a 64-bit HotSpot JVM with the compressed ordinary object pointers disabled.
	 *
	 * @return {@code true} if the JVM is a 64-bit HotSpot JVM whose {@code UseCompressedOops} option is
	 * {@code false}
	 */
	public boolean isCompressedOopsOffOn64BitHotspot();

	/**
	 * Tells whether the JVM is a 64-bit JVM whose compressed ordinary object pointers are not known to be enabled.
	 *
	 * @return {@code true} if the JVM is a 64-bit JVM and {@code HotSpotDiagnosticMXBean} does not report the
	 * {@code UseCompressedOops} option as {@code true}, which also happens if the JVM does not supply the option
	 */
	public boolean isCompressedOopsOffOn64Bit();

	/**
	 * Tells whether the JVM is a 32-bit JVM.
	 *
	 * @return {@code true} if the {@code sun.arch.data.model} system property contains {@code 32}: if the property
	 * is not set, {@code false}
	 */
	public boolean is32Bit();

	/**
	 * Tells whether the JVM is a 64-bit JVM.
	 *
	 * @return {@code true} if the {@code sun.arch.data.model} system property contains {@code 64} or, if the
	 * property is not set, if the {@code os.arch} system property contains {@code 64}
	 */
	public boolean is64Bit();

	/**
	 * Returns the major version of the running Java, read from the {@code java.version} system property.
	 *
	 * @return the major version, such as 8 for Java 1.8 or 21 for Java 21
	 */
	public int getVersion();


	/**
	 * Supplies the information about the running JVM.
	 */
	public static class Provider {

		/**
		 * Returns the information about the running JVM, read the first time that this method is called.
		 *
		 * @return the information, always the same instance
		 */
		public static Info getInfoInstance() {
			return InfoImpl.getInstance();
		}

	}
}
