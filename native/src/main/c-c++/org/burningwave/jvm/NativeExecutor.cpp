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
#include "NativeExecutor.h"
#include "NativeEnvironment.h"


NativeEnvironment* environment;


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* jNIEnv = NULL;
	if (vm->GetEnv((void**)&jNIEnv, JNI_VERSION_9) != JNI_OK) {
		return -1;
	}
	environment = new NativeEnvironment(jNIEnv);
	return JNI_VERSION_9;
}


JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
	JNIEnv* jNIEnv = NULL;
	vm->GetEnv((void**)&jNIEnv, JNI_VERSION_9);
	environment->destroy(jNIEnv);
	delete(environment);
	environment = NULL;
}


//Set object value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(environment, jNIEnv, target, field, environment->objectFieldAccessor);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(environment, jNIEnv, target,  field, environment->objectFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(environment, jNIEnv, target,  field, value, environment->objectFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(environment, jNIEnv, target,  field, value, environment->objectFieldAccessor);
}


//Get/set int value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getIntegerFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(environment, jNIEnv, target, field, environment->jintFieldAccessor);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticIntegerFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(environment, jNIEnv, target,  field, environment->jintFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setIntegerFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(environment, jNIEnv, target,  field, value, environment->jintFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticIntegerFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(environment, jNIEnv, target,  field, value, environment->jintFieldAccessor);
}


//Get/set long value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getLongFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(environment, jNIEnv, target, field, environment->jlongFieldAccessor);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticLongFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(environment, jNIEnv, target,  field, environment->jlongFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setLongFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(environment, jNIEnv, target,  field, value, environment->jlongFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticLongFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(environment, jNIEnv, target,  field, value, environment->jlongFieldAccessor);
}


//Get/set float value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getFloatFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(environment, jNIEnv, target, field, environment->jfloatFieldAccessor);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticFloatFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(environment, jNIEnv, target,  field, environment->jfloatFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setFloatFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(environment, jNIEnv, target,  field, value, environment->jfloatFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticFloatFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(environment, jNIEnv, target,  field, value, environment->jfloatFieldAccessor);
}


//Get/set double value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getDoubleFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(environment, jNIEnv, target, field, environment->jdoubleFieldAccessor);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticDoubleFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(environment, jNIEnv, target,  field, environment->jdoubleFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setDoubleFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(environment, jNIEnv, target,  field, value, environment->jdoubleFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticDoubleFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(environment, jNIEnv, target,  field, value, environment->jdoubleFieldAccessor);
}


//Get/set boolean value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getBooleanFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(environment, jNIEnv, target, field, environment->jbooleanFieldAccessor);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticBooleanFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(environment, jNIEnv, target,  field, environment->jbooleanFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setBooleanFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(environment, jNIEnv, target,  field, value, environment->jbooleanFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticBooleanFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(environment, jNIEnv, target,  field, value, environment->jbooleanFieldAccessor);
}


//Get/set byte value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getByteFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(environment, jNIEnv, target, field, environment->jbyteFieldAccessor);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticByteFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(environment, jNIEnv, target,  field, environment->jbyteFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setByteFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(environment, jNIEnv, target,  field, value, environment->jbyteFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticByteFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(environment, jNIEnv, target,  field, value, environment->jbyteFieldAccessor);
}


//Get/set char value
JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getCharacterFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field) {
	return checkAndGetFieldValue(environment, jNIEnv, target, field, environment->jcharFieldAccessor);
}

JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticCharacterFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field) {
	return checkAndGetStaticFieldValue(environment, jNIEnv, target,  field, environment->jcharFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setCharacterFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject target, jobject field, jobject value) {
	checkAndSetFieldValue(environment, jNIEnv, target,  field, value, environment->jcharFieldAccessor);
}

JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticCharacterFieldValue)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass target, jobject field, jobject value) {
	checkAndSetStaticFieldValue(environment, jNIEnv, target,  field, value, environment->jcharFieldAccessor);
}


JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setAllowedModes)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject consulter, jint modes) {
	jNIEnv->SetIntField(consulter, environment->java_lang_invoke_MethodHandles$Lookup_allowedModesFieldId, modes);
}


JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setAccessible)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jobject accessibleObject, jboolean flag) {
	jNIEnv->SetBooleanField(accessibleObject, environment->java_lang_reflect_AccessibleObject_overrideFieldId, flag);
}


JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(allocateInstance)(JNIEnv* jNIEnv, jobject nativeExecutorInstance, jclass instanceType) {
	return jNIEnv->AllocObject(instanceType);
}
