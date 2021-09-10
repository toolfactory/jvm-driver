package org.burningwave.jvm;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

public final class NativeEnvironment {
	private final static NativeEnvironment INSTANCE;
	
	private NativeEnvironment() {}
	
	static {
		Libraries.getInstance().loadFor(NativeEnvironment.class);
		INSTANCE = new NativeEnvironment();
	}
	
	public final static  NativeEnvironment getInstance() {
		return INSTANCE;
	}
	
	
	native Object allocateInstance(Class<?> cls);
	
	
	native Object getFieldValue(Object target, Field field);
	
	native Integer getIntegerFieldValue(Object target, Field field);

	native Long getLongFieldValue(Object target, Field field);	
	
	native Float getFloatFieldValue(Object target, Field field);
	
	native Double getDoubleFieldValue(Object target, Field field);
	
	native Boolean getBooleanFieldValue(Object target, Field field);
	
	native Byte getByteFieldValue(Object target, Field field);
	
	native Character getCharacterFieldValue(Object target, Field field);
	
	
	native Object getStaticFieldValue(Class<?> target, Field field);
	
	native Integer getStaticIntegerFieldValue(Class<?> target, Field field);

	native Long getStaticLongFieldValue(Class<?> target, Field field);	
	
	native Float getStaticFloatFieldValue(Class<?> target, Field field);
	
	native Double getStaticDoubleFieldValue(Class<?> target, Field field);
	
	native Boolean getStaticBooleanFieldValue(Class<?> target, Field field);
	
	native Byte getStaticByteFieldValue(Class<?> target, Field field);
	
	native Character getStaticCharacterFieldValue(Class<?> target, Field field);
	
	
	native void setAccessible(AccessibleObject target, boolean flag);
	
	native void setAllowedModes(MethodHandles.Lookup consulter, int modes);
	
	
	native void setFieldValue(Object target, Field field, Object value);
	
	native void setIntegerFieldValue(Object target, Field field, Integer value);   
	
	native void setLongFieldValue(Object target, Field field, Long value);
	
	native void setFloatFieldValue(Object target, Field field, Float value);
	
	native void setDoubleFieldValue(Object target, Field field, Double value);
	
	native void setBooleanFieldValue(Object target, Field field, Boolean value);
	
	native void setByteFieldValue(Object target, Field field, Byte value);
	
	native void setCharacterFieldValue(Object target, Field field, Character value);
	
	
	native void setStaticFieldValue(Class<?> target, Field field, Object value);
	
	native void setStaticIntegerFieldValue(Class<?> target, Field field, Integer value);   
	
	native void setStaticLongFieldValue(Class<?> target, Field field, Long value);
	
	native void setStaticFloatFieldValue(Class<?> target, Field field, Float value);
	
	native void setStaticDoubleFieldValue(Class<?> target, Field field, Double value);
	
	native void setStaticBooleanFieldValue(Class<?> target, Field field, Boolean value);
	
	native void setStaticByteFieldValue(Class<?> target, Field field, Byte value);
	
	native void setStaticCharacterFieldValue(Class<?> target, Field field, Character value);

}

