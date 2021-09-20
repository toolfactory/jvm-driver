/*
 * This file is part of Burningwave JVM driver.
 *
 * Author: Roberto Gentili
 *
 * Hosted at: https://github.com/burningwave/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2021 Roberto Gentili
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
package org.burningwave.jvm;


import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;


class JavaClass {
	private String classNameSlashed;
	private String className;

	private JavaClass(String className, ByteBuffer byteCode) {
		this.classNameSlashed = className;
	}

	JavaClass(ByteBuffer byteCode) {
		this(Classes.retrieveName(byteCode), BufferHandler.shareContent(byteCode));
	}

	static JavaClass create(ByteBuffer byteCode) {
		return new JavaClass(byteCode);
	}

	static void use(ByteBuffer byteCode, Consumer<JavaClass> javaClassConsumer) {
		javaClassConsumer.accept(JavaClass.create(byteCode));
	}

	static <T, E extends Throwable> T extractByUsing(ByteBuffer byteCode, Function<JavaClass, T> javaClassConsumer) throws E {
		return javaClassConsumer.apply(JavaClass.create(byteCode));
	}

	private  String _getPackageName() {
		return classNameSlashed.contains("/") ?
			classNameSlashed.substring(0, classNameSlashed.lastIndexOf("/")) :
			null;
	}

	private String _getSimpleName() {
		return classNameSlashed.contains("/") ?
			classNameSlashed.substring(classNameSlashed.lastIndexOf("/") + 1) :
			classNameSlashed;
	}

	String getPackageName() {
		return Optional.ofNullable(_getPackageName()).map(value -> value.replace("/", ".")).orElse(null);
	}

	String getSimpleName() {
		return Optional.ofNullable(_getSimpleName()).orElse(null);
	}


	String getName() {
		if (className == null) {
			String packageName = getPackageName();
			String classSimpleName = getSimpleName();
			String name = null;
			if (packageName != null) {
				name = packageName;
			}
			if (classSimpleName != null) {
				if (packageName == null) {
					name = "";
				} else {
					name += ".";
				}
				name += classSimpleName;
			}
			className = name;
		}
		return className;
	}

}