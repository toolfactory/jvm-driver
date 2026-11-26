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
package io.github.toolfactory.jvm.function;

/**
 * Thrown when the JNI engine cannot be created: {@link io.github.toolfactory.jvm.NativeEngine#getInstance()} throws
 * it, and so do the constructors of the functions of the catalog that use the engine.
 * <p>
 * A driver that cannot be initialized for this reason throws a
 * {@link io.github.toolfactory.jvm.Driver.InitializeException}, which is a different, unchecked exception, whose
 * chain of causes contains this one.
 */
public class InitializeException extends Exception {

	private static final long serialVersionUID = 5712614840976686057L;

	/**
	 * Creates the exception.
	 *
	 * @param message the message of the exception
	 * @param cause the cause of the exception
	 */
	public InitializeException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * Creates the exception.
	 *
	 * @param message the message of the exception
	 */
	public InitializeException(String message) {
        super(message);
    }

}
