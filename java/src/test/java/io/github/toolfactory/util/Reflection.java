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
package io.github.toolfactory.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import io.github.toolfactory.jvm.Driver;
import io.github.toolfactory.jvm.function.template.Function;

public class Reflection {
	private Driver driver;


	private Reflection(Driver driver) {
		this.driver = driver;
	}

	public Driver getDriver() {
		return this.driver;
	}

	public Collection<Method> getDeclaredMethods(Class<?> cls) {
		Set<Method> declaredMembers = new LinkedHashSet<>();
		for (Method method : driver.getDeclaredMethods(cls)) {
			declaredMembers.add(method);
		}
		return declaredMembers;
	}


	public Collection<Method> getAllMethods(Class<?> cls) {
		return getAll(
			cls,
			new Function<Class<?>, Method[]>() {
				@Override
				public Method[] apply(Class<?> input) {
					return driver.getDeclaredMethods(input);
				}
			},
			new HashSet<Class<?>>(),
			new LinkedHashSet<Method>()
		);
	}

	public <T> T getFieldValue(Object target, Field field) {
		return driver.getFieldValue(target, field);
	}

	public void setFieldValue(Object target, Field field, Object value) {
		driver.setFieldValue(target, field, value);
	}


	public Field getDeclaredField(Class<?> cls, String name) {
		for (Field member : driver.getDeclaredFields(cls)) {
			if (member.getName().equals(name)) {
				return member;
			}
		}
		return null;
	}

	public Collection<Field> getDeclaredFields(Class<?> cls) {
		Set<Field> declaredMembers = new LinkedHashSet<>();
		for (Field member : driver.getDeclaredFields(cls)) {
			declaredMembers.add(member);
		}
		return declaredMembers;
	}


	public Collection<Field> getAllFields(Class<?> cls) {
		return getAll(
			cls,
			new Function<Class<?>, Field[]>() {
				@Override
				public Field[] apply(Class<?> input) {
					return driver.getDeclaredFields(input);
				}
			},
			new HashSet<Class<?>>(),
			new LinkedHashSet<Field>()
		);
	}


	public Collection<Constructor<?>> getDeclaredConstructors(Class<?> cls) {
		Set<Constructor<?>> declaredMembers = new LinkedHashSet<>();
		for (Constructor<?> member : driver.getDeclaredConstructors(cls)) {
			declaredMembers.add(member);
		}
		return declaredMembers;
	}


	public Collection<Constructor<?>> getAllConstructors(Class<?> cls) {
		return getAll(
			cls,
			new Function<Class<?>, Constructor<?>[]>() {
				@Override
				public Constructor<?>[] apply(Class<?> input) {
					return driver.getDeclaredConstructors(input);
				}
			},
			new HashSet<Class<?>>(),
			new LinkedHashSet<Constructor<?>>()
		);
	}

	private <M extends Member> Collection<M> getAll(
		Class<?> cls,
		Function<Class<?>, M[]> memberSupplier,
		Collection<Class<?>> visitedInterfaces,
		Collection<M> collection
	) {
		for (M member : memberSupplier.apply(cls)) {
			collection.add(member);
		}
		for (Class<?> interf : cls.getInterfaces()) {
			if (visitedInterfaces.add(interf)) {
				getAll(interf, memberSupplier, visitedInterfaces, collection);
			}
		}
		Class<?> superClass = cls.getSuperclass();
		return superClass != null ?
			getAll(
				superClass,
				memberSupplier,
				visitedInterfaces,
				collection
			) :
			collection;
	}

	public static class Factory {

		public static Reflection getNew() {
			return getNewWith(Driver.Factory.getNew());
		}

		public static Reflection getNewWith(Driver driver) {
			return new Reflection(driver);
		}

		public static Reflection getNewWithDynamicDriver() {
			return getNewWith(Driver.Factory.getNewDynamic());
		}

		public static Reflection getNewWithDefaultDriver() {
			return getNewWith(Driver.Factory.getNewDefault());
		}

		public static Reflection getNewWithHybridDriver() {
			return getNewWith(Driver.Factory.getNewHybrid());
		}

		public static Reflection getNewWithNativeDriver() {
			return getNewWith(Driver.Factory.getNewNative());
		}
	}
}
