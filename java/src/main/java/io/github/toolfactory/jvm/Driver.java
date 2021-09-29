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
package io.github.toolfactory.jvm;


import java.io.Closeable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;


public interface Driver extends Closeable {

	public <T> T allocateInstance(Class<?> cls);

	@Override
	public void close();

	public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode);

	public Class<?> getBuiltinClassLoaderClass();

	public Class<?> getClassLoaderDelegateClass();

	public MethodHandles.Lookup getConsulter(Class<?> cls);

	public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);

	public Field getDeclaredField(Class<?> cls, String name);

	public Field[] getDeclaredFields(Class<?> cls);

	public Method[] getDeclaredMethods(Class<?> cls);

	public <T> T getFieldValue(Object target, Field field);

	public Package getPackage(ClassLoader classLoader, String packageName);

	public <T> T invoke(Method method, Object target, Object[] params);

	public boolean isBuiltinClassLoader(ClassLoader classLoader);

	public boolean isClassLoaderDelegate(ClassLoader classLoader);

	public <T> T newInstance(Constructor<T> ctor, Object[] params);

	public Collection<Class<?>> retrieveLoadedClasses(ClassLoader classLoader);

	public Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader);
	
	public void setAccessible(AccessibleObject object, boolean flag);
	
	public void setFieldValue(Object target, Field field, Object value);

	public <T> T throwException(Object exceptionOrMessage, Object... placeHolderReplacements);
	
	
	public static class Factory {
		
		public static Driver getNew() {
			try {
				try {
					return getNewDefault();
				} catch (InitializeException exc) {
					return getNewHybrid(); 
				}
			} catch (InitializeException exc) {
				return getNewNative();
			}
		}
		
		public static Driver getNewDefault() {
			return new DefaultDriver();
		}
		
		public static Driver getNewHybrid() {
			return new HybridDriver();
		}
		
		public static Driver getNewNative() {
			return new NativeDriver();
		}
	}
	
	public static class InitializeException extends RuntimeException {

		private static final long serialVersionUID = -3348641464676904231L;

	    public InitializeException(String message, Throwable cause) {
	        super(message, cause);
	    }

	}
}