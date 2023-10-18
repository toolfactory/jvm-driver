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
package io.github.toolfactory.jvm.function.catalog;


import java.lang.reflect.Field;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.Supplier;
import sun.misc.Unsafe;


@SuppressWarnings("all")
public interface UnsafeSupplier extends Supplier<sun.misc.Unsafe> {

	public static class ForJava7 implements UnsafeSupplier {
		protected sun.misc.Unsafe unsafe;

		public ForJava7(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			this.unsafe = (sun.misc.Unsafe)theUnsafeField.get(null);
		}

		@Override
		public sun.misc.Unsafe get() {
			return unsafe;
		}

	}
}
