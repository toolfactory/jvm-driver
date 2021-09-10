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
#include "./../common.h"

#ifndef org_burningwave_jvm_NativeExecutor_H
	#define org_burningwave_jvm_NativeExecutor_H
	#ifndef CLASS_00001_NAME
		#define CLASS_00001_NAME org_burningwave_jvm_NativeExecutor
	#endif

	#define JNI_FUNCTION_NAME_OF_CLASS_00001(functionName) JNI_FUNCTION_NAME_OF(CLASS_00001_NAME, functionName)

	#ifdef __cplusplus
		extern "C" {
	#endif

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(allocateInstance)
	  (JNIEnv *, jobject, jclass);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getFieldValue)
	  (JNIEnv *, jobject, jobject, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getIntegerFieldValue)
	  (JNIEnv *, jobject, jobject, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getLongFieldValue)
	  (JNIEnv *, jobject, jobject, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getFloatFieldValue)
	  (JNIEnv *, jobject, jobject, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getDoubleFieldValue)
	  (JNIEnv *, jobject, jobject, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getBooleanFieldValue)
	  (JNIEnv *, jobject, jobject, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getByteFieldValue)
	  (JNIEnv *, jobject, jobject, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getCharacterFieldValue)
	  (JNIEnv *, jobject, jobject, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticFieldValue)
	  (JNIEnv *, jobject, jclass, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticIntegerFieldValue)
	  (JNIEnv *, jobject, jclass, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticLongFieldValue)
	  (JNIEnv *, jobject, jclass, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticFloatFieldValue)
	  (JNIEnv *, jobject, jclass, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticDoubleFieldValue)
	  (JNIEnv *, jobject, jclass, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticBooleanFieldValue)
	  (JNIEnv *, jobject, jclass, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticByteFieldValue)
	  (JNIEnv *, jobject, jclass, jobject);

	JNIEXPORT jobject JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(getStaticCharacterFieldValue)
	  (JNIEnv *, jobject, jclass, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setAccessible)
	  (JNIEnv *, jobject, jobject, jboolean);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setAllowedModes)
	  (JNIEnv *, jobject, jobject, jint);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setFieldValue)
	  (JNIEnv *, jobject, jobject, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setIntegerFieldValue)
	  (JNIEnv *, jobject, jobject, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setLongFieldValue)
	  (JNIEnv *, jobject, jobject, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setFloatFieldValue)
	  (JNIEnv *, jobject, jobject, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setDoubleFieldValue)
	  (JNIEnv *, jobject, jobject, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setBooleanFieldValue)
	  (JNIEnv *, jobject, jobject, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setByteFieldValue)
	  (JNIEnv *, jobject, jobject, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setCharacterFieldValue)
	  (JNIEnv *, jobject, jobject, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticFieldValue)
	  (JNIEnv *, jobject, jclass, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticIntegerFieldValue)
	  (JNIEnv *, jobject, jclass, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticLongFieldValue)
	  (JNIEnv *, jobject, jclass, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticFloatFieldValue)
	  (JNIEnv *, jobject, jclass, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticDoubleFieldValue)
	  (JNIEnv *, jobject, jclass, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticBooleanFieldValue)
	  (JNIEnv *, jobject, jclass, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticByteFieldValue)
	  (JNIEnv *, jobject, jclass, jobject, jobject);

	JNIEXPORT void JNICALL JNI_FUNCTION_NAME_OF_CLASS_00001(setStaticCharacterFieldValue)
	  (JNIEnv *, jobject, jclass, jobject, jobject);

	#ifdef __cplusplus
		}
	#endif

#endif
