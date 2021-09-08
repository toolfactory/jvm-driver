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
#include "./DriverFunctionSupplierNative.h"
#include "./Environment.h"


Environment* environment;


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* jNIEnv = NULL;
	if (vm->GetEnv((void**)&jNIEnv, JNI_VERSION_9) != JNI_OK) {
		return -1;
	}
	environment = new Environment(jNIEnv);
	return JNI_VERSION_9;
}


JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
	JNIEnv* jNIEnv = NULL;
	vm->GetEnv((void**)&jNIEnv, JNI_VERSION_9);
	environment->destroy(jNIEnv);
	delete(environment);
	environment = NULL;
}


int throwNullPointerExceptionIfNull(JNIEnv* jNIEnv, jobject object, const char message[]) {
	if (object == NULL) {
		jNIEnv->ThrowNew(environment->java_lang_NullPointerExceptionClass, message);
		return -1;
	}
	return 0;
}


jobject checkAndGetFieldValue(
	JNIEnv* jNIEnv,
	jobject target, jobject field,
	FieldAccessor* fieldAccessor
) {
	if (throwNullPointerExceptionIfNull(jNIEnv, target, "Target is null") != 0) {
		return NULL;
	}
	if (throwNullPointerExceptionIfNull(jNIEnv, field, "Field is null") != 0) {
		return NULL;
	}
    return fieldAccessor->getValue(jNIEnv, target, field);
}


jobject checkAndGetStaticFieldValue(
	JNIEnv* jNIEnv,
	jclass target, jobject field,
	FieldAccessor* fieldAccessor
) {
	if (throwNullPointerExceptionIfNull(jNIEnv, target, "Target is null") != 0) {
		return NULL;
	}
	if (throwNullPointerExceptionIfNull(jNIEnv, field, "Field is null") != 0) {
		return NULL;
	}
    return fieldAccessor->getStaticValue(jNIEnv, target, field);
}


void checkAndSetFieldValue(
	JNIEnv* jNIEnv,
	jobject target, jobject field, jobject value,
	FieldAccessor* fieldAccessor
) {
	if (throwNullPointerExceptionIfNull(jNIEnv, target, "Target is null") == 0) {
		fieldAccessor->setValue(jNIEnv, target, field, value);
	}
}


void checkAndSetStaticFieldValue(
	JNIEnv* jNIEnv,
	jclass target, jobject field, jobject value,
	FieldAccessor* fieldAccessor
) {
	if (throwNullPointerExceptionIfNull(jNIEnv, target, "Target is null") == 0) {
		fieldAccessor->setStaticValue(jNIEnv, target, field, value);
	}
}

//Set object value
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(jNIEnv, target, field, environment->objectFieldAccessor);
}

JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(jNIEnv, target,  field, environment->objectFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(jNIEnv, target,  field, value, environment->objectFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(jNIEnv, target,  field, value, environment->objectFieldAccessor);
}


//Get/set int value
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getIntegerFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(jNIEnv, target, field, environment->jintFieldAccessor);
}

JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticIntegerFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(jNIEnv, target,  field, environment->jintFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setIntegerFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(jNIEnv, target,  field, value, environment->jintFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticIntegerFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(jNIEnv, target,  field, value, environment->jintFieldAccessor);
}


//Get/set long value
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getLongFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(jNIEnv, target, field, environment->jlongFieldAccessor);
}

JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticLongFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(jNIEnv, target,  field, environment->jlongFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setLongFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(jNIEnv, target,  field, value, environment->jlongFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticLongFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(jNIEnv, target,  field, value, environment->jlongFieldAccessor);
}


//Get/set float value
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getFloatFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(jNIEnv, target, field, environment->jfloatFieldAccessor);
}

JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticFloatFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(jNIEnv, target,  field, environment->jfloatFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setFloatFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(jNIEnv, target,  field, value, environment->jfloatFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticFloatFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(jNIEnv, target,  field, value, environment->jfloatFieldAccessor);
}


//Get/set double value
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getDoubleFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(jNIEnv, target, field, environment->jdoubleFieldAccessor);
}

JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticDoubleFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(jNIEnv, target,  field, environment->jdoubleFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setDoubleFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(jNIEnv, target,  field, value, environment->jdoubleFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticDoubleFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(jNIEnv, target,  field, value, environment->jdoubleFieldAccessor);
}


//Get/set boolean value
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getBooleanFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(jNIEnv, target, field, environment->jbooleanFieldAccessor);
}

JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticBooleanFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(jNIEnv, target,  field, environment->jbooleanFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setBooleanFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(jNIEnv, target,  field, value, environment->jbooleanFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticBooleanFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(jNIEnv, target,  field, value, environment->jbooleanFieldAccessor);
}


//Get/set byte value
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getByteFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(jNIEnv, target, field, environment->jbyteFieldAccessor);
}

JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticByteFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(jNIEnv, target,  field, environment->jbyteFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setByteFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(jNIEnv, target,  field, value, environment->jbyteFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticByteFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(jNIEnv, target,  field, value, environment->jbyteFieldAccessor);
}


//Get/set char value
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getCharacterFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(jNIEnv, target, field, environment->jcharFieldAccessor);
}

JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticCharacterFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(jNIEnv, target,  field, environment->jcharFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setCharacterFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(jNIEnv, target,  field, value, environment->jcharFieldAccessor);
}

JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticCharacterFieldValue(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(jNIEnv, target,  field, value, environment->jcharFieldAccessor);
}


JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setAllowedModes(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject consulter, jint modes) {
	jNIEnv->SetIntField(consulter, environment->java_lang_invoke_MethodHandles$Lookup_allowedModesFieldId, modes);
}


JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setAccessible(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jobject accessibleObject, jboolean flag) {
	jNIEnv->SetBooleanField(accessibleObject, environment->java_lang_reflect_AccessibleObject_overrideFieldId, flag);
}


JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_allocateInstance(JNIEnv* jNIEnv, jobject driverFunctionSupplierNativeInstance, jclass instanceType) {
	return jNIEnv->AllocObject(instanceType);
}
