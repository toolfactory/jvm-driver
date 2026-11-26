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
 * The catalog of the functions on which the drivers are based: its classes are implementation details of the
 * drivers, so use the methods of {@link io.github.toolfactory.jvm.Driver} instead of calling them.
 * <p>
 * Each function is an interface with a nested class for each Java version from which its implementation changes,
 * such as {@code ForJava7}, {@code ForJava9} or {@code ForJava17}, and, where needed, with nested classes named
 * {@code ForSemeru} for IBM Semeru and with a nested interface {@code Native} that contains the implementations based
 * on the {@link io.github.toolfactory.jvm.NativeEngine}. These classes are public because
 * {@link io.github.toolfactory.jvm.util.ObjectProvider} instantiates them by name: for each function it builds the
 * implementation written for the highest version, among {@link io.github.toolfactory.jvm.Info#CRITICAL_VERSIONS},
 * that is not greater than the running one, preferring the implementations for IBM Semeru on that JVM.
 * <p>
 * Each driver chooses which functions to build, for example {@code AllocateInstanceFunction} or
 * {@code AllocateInstanceFunction.Native}, and can replace a function that cannot be built through an
 * {@link io.github.toolfactory.jvm.util.ObjectProvider.ExceptionHandler}.
 */
package io.github.toolfactory.jvm.function.catalog;
