package io.github.toolfactory.jvm.test;


import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.toolfactory.util.Reflection;


@SuppressWarnings("unused")
abstract class BaseTest {
	protected Reflection reflection;

	abstract Reflection getReflection();


	void getAndSetDirectVolatileTestOne() {
		try {
			Object obj = new Object() {
				volatile List<Object> objectValue;
				volatile int intValue;
				volatile long longValue;
				volatile float floatValue;
				volatile double doubleValue;
				volatile boolean booleanValue;
				volatile byte byteValue;
				volatile char charValue;
			};

			Reflection reflection = getReflection();
			Field field = reflection.getDeclaredField(obj.getClass(), "objectValue");
			List<Object> objectValue = new ArrayList<>();
			reflection.setFieldValue(obj, field, objectValue);
			List<Object> objectValue2Var = reflection.getFieldValue(obj, field);
			assertTrue(objectValue2Var == objectValue);
			field = reflection.getDeclaredField(obj.getClass(), "intValue");
			reflection.setFieldValue(obj, field, 1);
			int intValue = reflection.getFieldValue(obj, field);
			assertTrue(intValue == 1);
			field = reflection.getDeclaredField(obj.getClass(), "longValue");
			reflection.setFieldValue(obj, field, 2L);
			long longValue = reflection.getFieldValue(obj, field);
			assertTrue(longValue == 2L);
			field = reflection.getDeclaredField(obj.getClass(), "floatValue");
			reflection.setFieldValue(obj, field, 3f);
			float floatValue = reflection.getFieldValue(obj, field);
			assertTrue(floatValue == 3f);
			field = reflection.getDeclaredField(obj.getClass(), "doubleValue");
			reflection.setFieldValue(obj, field, 4.1d);
			double doubleValue = reflection.getFieldValue(obj, field);
			assertTrue(doubleValue == 4.1d);
			field = reflection.getDeclaredField(obj.getClass(), "booleanValue");
			reflection.setFieldValue(obj, field, true);
			boolean booleanValue = reflection.getFieldValue(obj, field);
			assertTrue(booleanValue);
			field = reflection.getDeclaredField(obj.getClass(), "byteValue");
			reflection.setFieldValue(obj, field, (byte)5);
			byte byteValue = reflection.getFieldValue(obj, field);
			assertTrue(byteValue == 5);
			field = reflection.getDeclaredField(obj.getClass(), "charValue");
			reflection.setFieldValue(obj, field, 'a');
			char charValue = reflection.getFieldValue(obj, field);
			assertTrue(charValue == 'a');

			obj = new ClassForTest();
			objectValue = new ArrayList<>();
			field = reflection.getDeclaredField(obj.getClass(), "objectValue");
			reflection.setFieldValue(obj, field, objectValue);
			objectValue2Var = reflection.getFieldValue(obj, field);
			assertTrue(objectValue2Var == objectValue);
			field = reflection.getDeclaredField(obj.getClass(), "intValue");
			reflection.setFieldValue(obj, field, 1);
			intValue = reflection.getFieldValue(obj, field);
			assertTrue(intValue == 1);
			field = reflection.getDeclaredField(obj.getClass(), "longValue");
			reflection.setFieldValue(obj, field, 2L);
			longValue = reflection.getFieldValue(obj, field);
			assertTrue(longValue == 2L);
			field = reflection.getDeclaredField(obj.getClass(), "floatValue");
			reflection.setFieldValue(obj, field, 3f);
			floatValue = reflection.getFieldValue(obj, field);
			assertTrue(floatValue == 3f);
			field = reflection.getDeclaredField(obj.getClass(), "doubleValue");
			reflection.setFieldValue(obj, field, 4.1d);
			doubleValue = reflection.getFieldValue(obj, field);
			assertTrue(doubleValue == 4.1d);
			field = reflection.getDeclaredField(obj.getClass(), "booleanValue");
			reflection.setFieldValue(obj, field, true);
			booleanValue = reflection.getFieldValue(obj, field);
			assertTrue(booleanValue);
			field = reflection.getDeclaredField(obj.getClass(), "byteValue");
			reflection.setFieldValue(obj, field, (byte)5);
			byteValue = reflection.getFieldValue(obj, field);
			assertTrue(byteValue == 5);
			field = reflection.getDeclaredField(obj.getClass(), "charValue");
			reflection.setFieldValue(obj, field, 'a');
			charValue = reflection.getFieldValue(obj, field);
			assertTrue(charValue == 'a');
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}


	void getConsulterTestOne() {
		try {
			getReflection().getDriver().getConsulter(Class.class);
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}

	void getDeclaredFieldsTestOne() {
		try {
			for (Member member : getReflection().getDriver().getDeclaredFields(Class.class)) {
				log(member);
			}
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}

	void getDeclaredMethodsTestOne() {
		try {
			for (Member member : getReflection().getDriver().getDeclaredMethods(Class.class)) {
				log(member);
			}
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}

	void getDeclaredConstructorsTestOne() {
		try {
			for (Member member : getReflection().getDriver().getDeclaredConstructors(Class.class)) {
				log(member);
			}
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}


	void allocateInstanceTestOne() {
		try {
			log(getReflection().getDriver().allocateInstance(ClassForTest.class).toString());
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}


	void setAccessibleTestOne() {
		try {
			ClassForTest object = new ClassForTest();
			Field field = ClassForTest.class.getDeclaredField("intValue");
			getReflection().getDriver().setAccessible(field, true);
			log(field.get(object));
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}


	void setInvokeTestOne() {
		try {
			int newValue = 10;
			getReflection().getDriver().invoke(
				null,
				ClassForTest.class.getDeclaredMethod("setIntValue", int.class),
				new Object[] {newValue}
			);
			assertTrue(
				(Integer)getReflection().getDriver().getFieldValue(null, ClassForTest.class.getDeclaredField("intValue")) == newValue
			);
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}


	void newInstanceTestOne() {
		try {
			int newValue = 20;
			getReflection().getDriver().newInstance(
				ClassForTest.class.getDeclaredConstructor(int.class),
				new Object[] {newValue}
			);
			assertTrue(
				(Integer)getReflection().getDriver().getFieldValue(null, ClassForTest.class.getDeclaredField("intValue")) == newValue
			);
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}


	void retrieveLoadedClassesTestOne() {
		try {
			Collection<Class<?>> loadedClasses = getReflection().getDriver().getLoadedClassesRetriever(Thread.currentThread().getContextClassLoader()).get();
			for (Class<?> cls : loadedClasses) {
				log(cls.getName());
			}
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}


	void retrieveLoadedPackagesTestOne() {
		try {
			Map<String, ?> loadedClasses = getReflection().getDriver().retrieveLoadedPackages(Thread.currentThread().getContextClassLoader());
			for (Entry<String, ?> cls : loadedClasses.entrySet()) {
				log(cls.getValue().toString());
			}
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}

	public void getClassByNameTestOne() {
		try {
			Class<?> cls = getReflection().getDriver().getClassByName(
				"java.lang.AssertionStatusDirectives", false,
				this.getClass().getClassLoader(), this.getClass());
			log(cls);
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}

	public void retrieveResourcesTestOne() {
		try {
			Collection<URL> resource = getReflection().getDriver().getResources("com/sun/source/util/JavacTask.class", false);
			for (URL resourceURL : resource) {
				log(resourceURL.getPath());
			}
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}
	}
	
	public void convertToBuiltinClassLoader() {
		try {
			log(getReflection().getDriver().convertToBuiltinClassLoader(new ClassLoader() {}));
		} catch (Throwable exc) {
			exc.printStackTrace();
			getReflection().getDriver().throwException(exc);
		}		
	}
	

	private void log(Object value) {
		System.out.println(value != null ? value.toString() : "null");
	}

	private static class ClassForTest {

		private static volatile List<Object> objectValue;
		private static volatile int intValue;
		private static volatile long longValue;
		private static volatile float floatValue;
		private static volatile double doubleValue;
		private static volatile boolean booleanValue;
		private static volatile byte byteValue;
		private static volatile char charValue;

		private ClassForTest() {}

		private ClassForTest(int value) {
			setIntValue(value);
		}

		private static void setIntValue(int value) {
			intValue = value;
		}
	}

}
