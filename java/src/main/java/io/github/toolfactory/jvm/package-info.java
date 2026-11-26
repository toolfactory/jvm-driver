/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
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
/**
 * The drivers, which allow a deep interaction with the JVM without the restrictions of the reflection API and of the
 * module system, and the JNI engine on which some of their functions are based.
 * <p>
 * {@link Driver} is the interface of the drivers and {@link Driver.Factory} creates them:
 * {@link Driver.Factory#getNew()} returns the first driver that can be initialized among the default, the hybrid and
 * the native drivers. For example, to read a field that the module system does not allow to access through
 * reflection:
 * <pre>{@code
 * Driver driver = Driver.Factory.getNew();
 * Field valueField = String.class.getDeclaredField("value");
 * // A char[] up to Java 8, a byte[] from Java 9
 * Object value = driver.getFieldValue("Hello", valueField);
 * }</pre>
 * <p>
 * The JNI functions of the dynamic, hybrid and native drivers are supplied by the {@link NativeEngine} chosen through
 * the {@code native-engine.class} property of the {@code jvm-driver.properties} files: {@link BurningwaveEngine} by
 * default, or the engine based on Narcissus when the {@code jvm-driver-narcissus-plugin} artifact is in the class
 * path.
 */
package io.github.toolfactory.jvm;
