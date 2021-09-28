package io.github.toolfactory.jvm;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
abstract class BaseTest {
	
	abstract Driver getDriver();
	

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
			
			Driver driver = getDriver();
			Field field = driver.getDeclaredField(obj.getClass(), "objectValue");
			List<Object> objectValue = new ArrayList<>();
			driver.setFieldValue(obj, field, objectValue);
			List<Object> objectValue2Var = driver.getFieldValue(obj, field);
			assertTrue(objectValue2Var == objectValue);
			field = driver.getDeclaredField(obj.getClass(), "intValue");
			driver.setFieldValue(obj, field, 1);
			int intValue = driver.getFieldValue(obj, field);
			assertTrue(intValue == 1);
			field = driver.getDeclaredField(obj.getClass(), "longValue");
			driver.setFieldValue(obj, field, 2L);
			long longValue = driver.getFieldValue(obj, field);
			assertTrue(longValue == 2L);
			field = driver.getDeclaredField(obj.getClass(), "floatValue");
			driver.setFieldValue(obj, field, 3f);
			float floatValue = driver.getFieldValue(obj, field);
			assertTrue(floatValue == 3f);
			field = driver.getDeclaredField(obj.getClass(), "doubleValue");
			driver.setFieldValue(obj, field, 4.1d);
			double doubleValue = driver.getFieldValue(obj, field);
			assertTrue(doubleValue == 4.1d);
			field = driver.getDeclaredField(obj.getClass(), "booleanValue");
			driver.setFieldValue(obj, field, true);
			boolean booleanValue = driver.getFieldValue(obj, field);
			assertTrue(booleanValue);
			field = driver.getDeclaredField(obj.getClass(), "byteValue");
			driver.setFieldValue(obj, field, (byte)5);
			byte byteValue = driver.getFieldValue(obj, field);
			assertTrue(byteValue == 5);
			field = driver.getDeclaredField(obj.getClass(), "charValue");
			driver.setFieldValue(obj, field, 'a');
			char charValue = driver.getFieldValue(obj, field);
			assertTrue(charValue == 'a');

			obj = new ClassForTest();
			objectValue = new ArrayList<>();
			field = driver.getDeclaredField(obj.getClass(), "objectValue");
			driver.setFieldValue(obj, field, objectValue);
			objectValue2Var = driver.getFieldValue(obj, field);
			assertTrue(objectValue2Var == objectValue);
			field = driver.getDeclaredField(obj.getClass(), "intValue");
			driver.setFieldValue(obj, field, 1);
			intValue = driver.getFieldValue(obj, field);
			assertTrue(intValue == 1);
			field = driver.getDeclaredField(obj.getClass(), "longValue");
			driver.setFieldValue(obj, field, 2L);
			longValue = driver.getFieldValue(obj, field);
			assertTrue(longValue == 2L);
			field = driver.getDeclaredField(obj.getClass(), "floatValue");
			driver.setFieldValue(obj, field, 3f);
			floatValue = driver.getFieldValue(obj, field);
			assertTrue(floatValue == 3f);
			field = driver.getDeclaredField(obj.getClass(), "doubleValue");
			driver.setFieldValue(obj, field, 4.1d);
			doubleValue = driver.getFieldValue(obj, field);
			assertTrue(doubleValue == 4.1d);
			field = driver.getDeclaredField(obj.getClass(), "booleanValue");
			driver.setFieldValue(obj, field, true);
			booleanValue = driver.getFieldValue(obj, field);
			assertTrue(booleanValue);
			field = driver.getDeclaredField(obj.getClass(), "byteValue");
			driver.setFieldValue(obj, field, (byte)5);
			byteValue = driver.getFieldValue(obj, field);
			assertTrue(byteValue == 5);
			field = driver.getDeclaredField(obj.getClass(), "charValue");
			driver.setFieldValue(obj, field, 'a');
			charValue = driver.getFieldValue(obj, field);
			assertTrue(charValue == 'a');
		} catch (Throwable exc) {
			exc.printStackTrace();
			getDriver().throwException(exc);
		}
	}
	
	private static class ClassForTest {
		
		static volatile List<Object> objectValue;
		static volatile int intValue;
		static volatile long longValue;
		static volatile float floatValue;
		static volatile double doubleValue;
		static volatile boolean booleanValue;
		static volatile byte byteValue;
		static volatile char charValue;
	};
	
}
