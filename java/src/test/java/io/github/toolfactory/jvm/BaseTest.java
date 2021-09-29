package io.github.toolfactory.jvm;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.github.toolfactory.util.Reflection;


@SuppressWarnings("unused")
abstract class BaseTest {
	protected static Reflection reflection;
	
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
