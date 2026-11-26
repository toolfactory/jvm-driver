package io.github.toolfactory.jvm.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.github.toolfactory.jvm.NativeEngine;


//It tests the engine chosen through the jvm-driver.properties files: the java module and the narcissus-plugin module
//run it with different engines and tell it which one to expect through a system property
public class NativeEngineTest {

	//For JDK 7 testing
	public static void main(String[] args) throws Throwable {
		new NativeEngineTest().executeTests();
	}

	void executeTests() throws Throwable {
		selectedEngineTest();
		allocateInstanceTest();
		getDeclaredFieldTest();
		instanceFieldsTest();
		staticFieldsTest();
		finalFieldTest();
		accessibleObjectOverrideTest();
	}


	@Test
	public void selectedEngineTest() throws Throwable {
		NativeEngine nativeEngine = NativeEngine.getInstance();
		log("Native engine: " + nativeEngine.getClass().getName());
		assertSame(nativeEngine, NativeEngine.getInstance());
		String expectedClassName = System.getProperty("jvm-driver.test.native-engine.class");
		if (expectedClassName != null) {
			assertEquals(expectedClassName, nativeEngine.getClass().getName());
		}
	}


	@Test
	public void allocateInstanceTest() throws Throwable {
		Object object = NativeEngine.getInstance().allocateInstance(FieldsHolder.class);
		assertTrue(object instanceof FieldsHolder);
		//The constructor has not been called
		assertEquals(0, ((FieldsHolder)object).initializedByConstructor);
	}


	@Test
	public void getDeclaredFieldTest() throws Throwable {
		NativeEngine nativeEngine = NativeEngine.getInstance();
		//The reflection filters of the JDK hide this field from JDK 12
		Field field = nativeEngine.getDeclaredField(AccessibleObject.class, "override", boolean.class);
		assertEquals("override", field.getName());
		assertEquals(AccessibleObject.class, field.getDeclaringClass());
		field = nativeEngine.getDeclaredField(FieldsHolder.class, "staticIntValue", int.class);
		assertTrue(Modifier.isStatic(field.getModifiers()));
		field = nativeEngine.getDeclaredField(FieldsHolder.class, "objectValue", Object.class);
		assertEquals(Object.class, field.getType());
		field = nativeEngine.getDeclaredField(FieldsHolder.class, "intArrayValue", int[].class);
		assertEquals(int[].class, field.getType());
		//A field declared by a superclass
		field = nativeEngine.getDeclaredField(FieldsHolderSubclass.class, "intValue", int.class);
		assertEquals(FieldsHolder.class, field.getDeclaringClass());
		try {
			nativeEngine.getDeclaredField(FieldsHolder.class, "missingField", int.class);
			fail("A missing field has been found");
		} catch (NoSuchFieldException exc) {

		}
		try {
			nativeEngine.getDeclaredField(FieldsHolder.class, "intValue", long.class);
			fail("A field with a different type has been found");
		} catch (NoSuchFieldException exc) {

		}
	}


	@Test
	public void instanceFieldsTest() throws Throwable {
		NativeEngine nativeEngine = NativeEngine.getInstance();
		FieldsHolder target = new FieldsHolder();
		List<Object> objectValue = new ArrayList<>();
		setAndCheck(nativeEngine, target, "objectValue", objectValue);
		setAndCheck(nativeEngine, target, "objectValue", null);
		setAndCheck(nativeEngine, target, "shortValue", (short)1);
		setAndCheck(nativeEngine, target, "intValue", 2);
		setAndCheck(nativeEngine, target, "longValue", 3L);
		setAndCheck(nativeEngine, target, "floatValue", 4f);
		setAndCheck(nativeEngine, target, "doubleValue", 5.1d);
		setAndCheck(nativeEngine, target, "booleanValue", true);
		setAndCheck(nativeEngine, target, "byteValue", (byte)6);
		setAndCheck(nativeEngine, target, "charValue", 'a');
		setAndCheck(nativeEngine, target, "intArrayValue", new int[] {7});
		assertEquals(2, target.intValue);
		//A field declared by a superclass, read through an instance of the subclass
		FieldsHolderSubclass subclassTarget = new FieldsHolderSubclass();
		setAndCheck(nativeEngine, subclassTarget, "longValue", 8L);
	}


	@Test
	public void staticFieldsTest() throws Throwable {
		NativeEngine nativeEngine = NativeEngine.getInstance();
		setAndCheck(nativeEngine, null, "staticObjectValue", "value");
		setAndCheck(nativeEngine, null, "staticShortValue", (short)1);
		setAndCheck(nativeEngine, null, "staticIntValue", 2);
		setAndCheck(nativeEngine, null, "staticLongValue", 3L);
		setAndCheck(nativeEngine, null, "staticFloatValue", 4f);
		setAndCheck(nativeEngine, null, "staticDoubleValue", 5.1d);
		setAndCheck(nativeEngine, null, "staticBooleanValue", true);
		setAndCheck(nativeEngine, null, "staticByteValue", (byte)6);
		setAndCheck(nativeEngine, null, "staticCharValue", 'a');
		assertEquals(2, FieldsHolder.staticIntValue);
	}


	@Test
	public void finalFieldTest() throws Throwable {
		NativeEngine nativeEngine = NativeEngine.getInstance();
		FieldsHolder target = new FieldsHolder();
		Field field = FieldsHolder.class.getDeclaredField("finalIntValue");
		assertEquals(1, nativeEngine.getFieldValue(target, field));
		nativeEngine.setFieldValue(target, field, 9);
		assertEquals(9, nativeEngine.getFieldValue(target, field));
	}


	@Test
	public void accessibleObjectOverrideTest() throws Throwable {
		NativeEngine nativeEngine = NativeEngine.getInstance();
		//A private field of a class of the JDK: from JDK 16 setAccessible fails on it without --add-opens
		Field sizeField = ArrayList.class.getDeclaredField("size");
		List<Object> list = new ArrayList<>();
		list.add("one");
		list.add("two");
		try {
			sizeField.get(list);
			fail("The field is already accessible");
		} catch (IllegalAccessException exc) {

		}
		Field overrideField = nativeEngine.getDeclaredField(AccessibleObject.class, "override", boolean.class);
		assertFalse((Boolean)nativeEngine.getFieldValue(sizeField, overrideField));
		nativeEngine.setFieldValue(sizeField, overrideField, Boolean.TRUE);
		assertTrue(sizeField.isAccessible());
		assertEquals(2, sizeField.get(list));
	}


	private void setAndCheck(NativeEngine nativeEngine, Object target, String fieldName, Object value) throws Throwable {
		Field field = FieldsHolder.class.getDeclaredField(fieldName);
		nativeEngine.setFieldValue(target, field, value);
		Object readValue = nativeEngine.getFieldValue(target, field);
		if (value instanceof int[]) {
			assertSame(value, readValue);
		} else {
			assertEquals(value, readValue);
		}
	}


	private void log(Object value) {
		System.out.println(value != null ? value.toString() : "null");
	}


	@SuppressWarnings("unused")
	private static class FieldsHolder {
		private static Object staticObjectValue;
		private static short staticShortValue;
		private static int staticIntValue;
		private static long staticLongValue;
		private static float staticFloatValue;
		private static double staticDoubleValue;
		private static boolean staticBooleanValue;
		private static byte staticByteValue;
		private static char staticCharValue;

		private Object objectValue;
		private short shortValue;
		private int intValue;
		private long longValue;
		private float floatValue;
		private double doubleValue;
		private boolean booleanValue;
		private byte byteValue;
		private char charValue;
		private int[] intArrayValue;
		private final int finalIntValue;
		private int initializedByConstructor;

		private FieldsHolder() {
			finalIntValue = 1;
			initializedByConstructor = 1;
		}

	}


	private static class FieldsHolderSubclass extends FieldsHolder {

	}

}
