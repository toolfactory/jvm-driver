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
 * Copyright (c) 2021 Roberto Gentili
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
 * Copyright (c) 2021 Roberto Gentili
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

import java.io.Closeable;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;


public interface Driver extends Closeable {

	public abstract void setFieldValue(Object target, Field field, Object value);

	public abstract <T> T getFieldValue(Object target, Field field);

	public abstract Method[] getDeclaredMethods(Class<?> cls);

	public abstract <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);

	public abstract Field[] getDeclaredFields(Class<?> cls);

	public abstract Field getDeclaredField(Class<?> cls, String name);

	public abstract <T> T newInstance(Constructor<T> ctor, Object[] params);

	public abstract Object invoke(Method method, Object target, Object[] params);

	public abstract Lookup getConsulter(Class<?> cls);

	public abstract Class<?> getClassLoaderDelegateClass();

	public abstract Class<?> getBuiltinClassLoaderClass();

	public abstract boolean isClassLoaderDelegate(ClassLoader classLoader);

	public abstract boolean isBuiltinClassLoader(ClassLoader classLoader);

	public abstract Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader);

	public abstract Collection<Class<?>> retrieveLoadedClasses(ClassLoader classLoader);

	public abstract Package retrieveLoadedPackage(ClassLoader classLoader, Object packageToFind, String packageName);

	public abstract Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode);

	public abstract void setAccessible(AccessibleObject object, boolean flag);
	
	public abstract void close();
	
	public static class InitializationException extends Exception {

		private static final long serialVersionUID = -3348641464676904231L;
		
	    public InitializationException(String message, Throwable cause) {
	        super(message, cause);
	    }
		
	}
	
}