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


#ifndef _Included_org_burningwave_jvm_DriverFunctionSupplierNative
#define _Included_org_burningwave_jvm_DriverFunctionSupplierNative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    allocateInstance
 * Signature: (Ljava/lang/Class;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_allocateInstance
  (JNIEnv *, jobject, jclass);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getIntegerFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Integer;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getIntegerFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getLongFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Long;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getLongFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getFloatFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Float;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getFloatFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getDoubleFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Double;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getDoubleFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getBooleanFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Boolean;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getBooleanFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getByteFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Byte;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getByteFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getCharacterFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Character;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getCharacterFieldValue
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getStaticFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getStaticIntegerFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Integer;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticIntegerFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getStaticLongFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Long;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticLongFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getStaticFloatFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Float;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticFloatFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getStaticDoubleFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Double;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticDoubleFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getStaticBooleanFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Boolean;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticBooleanFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getStaticByteFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Byte;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticByteFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    getStaticCharacterFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;)Ljava/lang/Character;
 */
JNIEXPORT jobject JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_getStaticCharacterFieldValue
  (JNIEnv *, jobject, jclass, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setAccessible
 * Signature: (Ljava/lang/reflect/AccessibleObject;Ljava/lang/Boolean;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setAccessible
  (JNIEnv *, jobject, jobject, jboolean);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setAllowedModes
 * Signature: (Ljava/lang/invoke/MethodHandles/Lookup;I)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setAllowedModes
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setIntegerFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Integer;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setIntegerFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setLongFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Long;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setLongFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setFloatFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Float;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setFloatFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setDoubleFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Double;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setDoubleFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setBooleanFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Boolean;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setBooleanFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setByteFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Byte;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setByteFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setCharacterFieldValue
 * Signature: (Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Character;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setCharacterFieldValue
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setStaticFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setStaticIntegerFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Integer;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticIntegerFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setStaticLongFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Long;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticLongFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setStaticFloatFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Float;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticFloatFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setStaticDoubleFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Double;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticDoubleFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setStaticBooleanFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Boolean;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticBooleanFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setStaticByteFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Byte;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticByteFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

/*
 * Class:     org_burningwave_jvm_DriverFunctionSupplierNative
 * Method:    setStaticCharacterFieldValue
 * Signature: (Ljava/lang/Class;Ljava/lang/reflect/Field;Ljava/lang/Character;)V
 */
JNIEXPORT void JNICALL Java_org_burningwave_jvm_DriverFunctionSupplierNative_setStaticCharacterFieldValue
  (JNIEnv *, jobject, jclass, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif
