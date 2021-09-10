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
#include <jni.h>


#ifndef _Included_org_burningwave_jvm_JavaNativeEnvironment
#define _Included_org_burningwave_jvm_JavaNativeEnvironment
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    allocateInstance
 * Signature: (Ljava/lang/Class;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_allocateInstance
  (JNIEnv *, jobject, jclass);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getIntegerFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Integer;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getIntegerFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getLongFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Long;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getLongFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getFloatFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Float;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getFloatFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getDoubleFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Double;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getDoubleFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getBooleanFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Boolean;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getBooleanFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getByteFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Byte;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getByteFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getCharacterFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Character;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getCharacterFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getStaticFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getStaticFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getStaticIntegerFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Integer;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getStaticIntegerFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getStaticLongFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Long;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getStaticLongFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getStaticFloatFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Float;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getStaticFloatFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getStaticDoubleFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Double;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getStaticDoubleFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getStaticBooleanFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Boolean;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getStaticBooleanFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getStaticByteFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Byte;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getStaticByteFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    getStaticCharacterFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Character;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_getStaticCharacterFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setAccessible
 * Signature: (Ljava/lang/reflect/AccessibleObject;Ljava/lang/Boolean;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setAccessible
  (JNIEnv *, jobject, jobject, jboolean);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setAllowedModes
 * Signature: (Ljava/lang/invoke/MethodHandles/Lookup;I)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setAllowedModes
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setIntegerFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Integer;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setIntegerFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setLongFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Long;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setLongFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setFloatFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Float;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setFloatFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setDoubleFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Double;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setDoubleFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setBooleanFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Boolean;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setBooleanFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setByteFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Byte;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setByteFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setCharacterFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Character;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setCharacterFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setStaticFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setStaticFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setStaticIntegerFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Integer;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setStaticIntegerFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setStaticLongFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Long;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setStaticLongFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setStaticFloatFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Float;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setStaticFloatFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setStaticDoubleFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Double;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setStaticDoubleFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setStaticBooleanFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Boolean;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setStaticBooleanFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setStaticByteFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Byte;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setStaticByteFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_JavaNativeEnvironment
 * Method:    setStaticCharacterFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Character;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_JavaNativeEnvironment_setStaticCharacterFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif
