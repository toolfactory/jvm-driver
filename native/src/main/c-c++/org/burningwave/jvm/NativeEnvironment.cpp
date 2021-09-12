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
#include "NativeEnvironment.h"


NativeEnvironment::NativeEnvironment(JNIEnv* env) {
	this->init(env);
}


void NativeEnvironment::destroy(JNIEnv* jNIEnv){
	delete(this->objectFieldAccessor);
	this->objectFieldAccessor = NULL;

	this->jintFieldAccessor->destroy(jNIEnv);
	delete(this->jintFieldAccessor);
	this->jintFieldAccessor = NULL;

	this->jlongFieldAccessor->destroy(jNIEnv);
	delete(this->jlongFieldAccessor);
	this->jlongFieldAccessor = NULL;

	this->jfloatFieldAccessor->destroy(jNIEnv);
	delete(this->jfloatFieldAccessor);
	this->jfloatFieldAccessor = NULL;

	this->jdoubleFieldAccessor->destroy(jNIEnv);
	delete(this->jdoubleFieldAccessor);
	this->jdoubleFieldAccessor = NULL;

	this->jbooleanFieldAccessor->destroy(jNIEnv);
	delete(this->jbooleanFieldAccessor);
	this->jbooleanFieldAccessor = NULL;

	this->jbyteFieldAccessor->destroy(jNIEnv);
	delete(this->jbyteFieldAccessor);
	this->jbyteFieldAccessor = NULL;

	this->jcharFieldAccessor->destroy(jNIEnv);
	delete(this->jcharFieldAccessor);
	this->jcharFieldAccessor = NULL;

	jNIEnv->DeleteGlobalRef(this->java_lang_NullPointerExceptionClass);
}

void NativeEnvironment::init(JNIEnv* jNIEnv) {
	this->java_lang_NullPointerExceptionClass = (jclass)jNIEnv->NewGlobalRef(jNIEnv->FindClass("java/lang/NullPointerException"));
	this->objectFieldAccessor = new ObjectFieldAccessor(jNIEnv);
	this->jintFieldAccessor = new PrimitiveFieldAccessor<jint>(
		jNIEnv, "java/lang/Integer", "(I)Ljava/lang/Integer;",
		&JNIEnv::CallIntMethod, "intValue", "()I",
		&JNIEnv::GetIntField, &JNIEnv::GetStaticIntField,
		&JNIEnv::SetIntField, &JNIEnv::SetStaticIntField
	);
	this->jlongFieldAccessor = new PrimitiveFieldAccessor<jlong>(
		jNIEnv, "java/lang/Long", "(J)Ljava/lang/Long;",
		&JNIEnv::CallLongMethod, "longValue", "()J",
		&JNIEnv::GetLongField, &JNIEnv::GetStaticLongField,
		&JNIEnv::SetLongField, &JNIEnv::SetStaticLongField
	);
	this->jfloatFieldAccessor = new PrimitiveFieldAccessor<jfloat>(
		jNIEnv, "java/lang/Float", "(F)Ljava/lang/Float;",
		&JNIEnv::CallFloatMethod, "floatValue", "()F",
		&JNIEnv::GetFloatField, &JNIEnv::GetStaticFloatField,
		&JNIEnv::SetFloatField, &JNIEnv::SetStaticFloatField
	);
	this->jdoubleFieldAccessor = new PrimitiveFieldAccessor<jdouble>(
		jNIEnv, "java/lang/Double", "(D)Ljava/lang/Double;",
		&JNIEnv::CallDoubleMethod, "doubleValue", "()D",
		&JNIEnv::GetDoubleField, &JNIEnv::GetStaticDoubleField,
		&JNIEnv::SetDoubleField, &JNIEnv::SetStaticDoubleField
	);
	this->jbooleanFieldAccessor = new PrimitiveFieldAccessor<jboolean>(
		jNIEnv, "java/lang/Boolean", "(Z)Ljava/lang/Boolean;",
		&JNIEnv::CallBooleanMethod, "booleanValue", "()Z",
		&JNIEnv::GetBooleanField, &JNIEnv::GetStaticBooleanField,
		&JNIEnv::SetBooleanField, &JNIEnv::SetStaticBooleanField
	);
	this->jbyteFieldAccessor = new PrimitiveFieldAccessor<jbyte>(
		jNIEnv, "java/lang/Byte", "(B)Ljava/lang/Byte;",
		&JNIEnv::CallByteMethod, "byteValue", "()B",
		&JNIEnv::GetByteField, &JNIEnv::GetStaticByteField,
		&JNIEnv::SetByteField, &JNIEnv::SetStaticByteField
	);
	this->jcharFieldAccessor = new PrimitiveFieldAccessor<jchar>(
		jNIEnv, "java/lang/Character", "(C)Ljava/lang/Character;",
		&JNIEnv::CallCharMethod, "charValue", "()C",
		&JNIEnv::GetCharField, &JNIEnv::GetStaticCharField,
		&JNIEnv::SetCharField, &JNIEnv::SetStaticCharField
	);
	this->java_lang_invoke_MethodHandles$Lookup_allowedModesFieldId =
		jNIEnv->GetFieldID(
			jNIEnv->FindClass("java/lang/invoke/MethodHandles$Lookup"),
			"allowedModes", "I"
		);
	this->java_lang_reflect_AccessibleObject_overrideFieldId =
		jNIEnv->GetFieldID(
			jNIEnv->FindClass("java/lang/reflect/AccessibleObject"),
			"override", "Z"
		);
}


FieldAccessor::FieldAccessor(JNIEnv* env) {}


FieldAccessor::~FieldAccessor() {}


void FieldAccessor::destroy(JNIEnv* jNIEnv) {}


template<typename Type>
PrimitiveFieldAccessor<Type>::PrimitiveFieldAccessor (
	JNIEnv* jNIEnv, const char name[],
	const char valueOfMethodSig[],
	Type (JNIEnv::*callTypeMethodFunction) (jobject, jmethodID, ...),
	const char callTypeMethodName[], const char callTypeMethodSig[],
	Type (JNIEnv::*getFieldValueFunction) (jobject, jfieldID),
	Type (JNIEnv::*getStaticFieldValueFunction) (jclass, jfieldID),
	void (JNIEnv::*setValueFunction) (jobject, jfieldID, Type),
	void (JNIEnv::*setStaticValueFunction) (jclass , jfieldID, Type)
) : FieldAccessor(jNIEnv) {
	this->wrapperClass = (jclass)jNIEnv->NewGlobalRef(jNIEnv->FindClass(name));
	this->callStaticObjectMethod = &JNIEnv::CallStaticObjectMethod;
	this->callTypeMethodFunction = callTypeMethodFunction;
	this->valueOfMethodId = jNIEnv->GetStaticMethodID(this->wrapperClass, "valueOf", valueOfMethodSig);
	this->callTypeMethodId = jNIEnv->GetMethodID(this->wrapperClass, callTypeMethodName, callTypeMethodSig);
	this->getFieldValueFunction = getFieldValueFunction;
	this->getStaticFieldValueFunction = getStaticFieldValueFunction;
	this->setValueFunction = setValueFunction;
	this->setStaticValueFunction = setStaticValueFunction;
}

template<typename Type>
PrimitiveFieldAccessor<Type>::~PrimitiveFieldAccessor() {}

template<typename Type>
void PrimitiveFieldAccessor<Type>::destroy(JNIEnv* jNIEnv) {
	jNIEnv->DeleteGlobalRef(this->wrapperClass);
}


template<typename Type>
jobject PrimitiveFieldAccessor<Type>::getValue(JNIEnv* jNIEnv, jobject target, jobject field) {
	jfieldID fieldId = jNIEnv->FromReflectedField(field);
	return (jNIEnv->*callStaticObjectMethod)(
		this->wrapperClass,
		this->valueOfMethodId,
		(jNIEnv->*getFieldValueFunction)(target, fieldId)
	);
}


template<typename Type>
jobject PrimitiveFieldAccessor<Type>::getStaticValue(JNIEnv* jNIEnv, jclass target, jobject field) {
	jfieldID fieldId = jNIEnv->FromReflectedField(field);
	return (jNIEnv->*callStaticObjectMethod)(
		this->wrapperClass,
		this->valueOfMethodId,
		(jNIEnv->*getStaticFieldValueFunction)(target, fieldId)
	);
}


template<typename Type>
void PrimitiveFieldAccessor<Type>::setValue(JNIEnv* jNIEnv, jobject target, jobject field, jobject value){
	jfieldID fieldId = jNIEnv->FromReflectedField(field);
	(jNIEnv->*setValueFunction)(
		target, fieldId,
		//(*env)->Call<Type>Method
		(jNIEnv->*callTypeMethodFunction)(
			//java/lang/<Type>.<type>Value method
			value, this->callTypeMethodId
		)
	);
}


template<typename Type>
void PrimitiveFieldAccessor<Type>::setStaticValue(JNIEnv* jNIEnv, jclass target, jobject field, jobject value){
	jfieldID fieldId = jNIEnv->FromReflectedField(field);
	(jNIEnv->*setStaticValueFunction)(
		target, fieldId,
		//(*env)->Call<Type>Method
		(jNIEnv->*callTypeMethodFunction)(
			//java/lang/<Type>.<type>Value method
			value, this->callTypeMethodId
		)
	);
}


ObjectFieldAccessor::ObjectFieldAccessor(JNIEnv* env) : FieldAccessor(env)  {}


ObjectFieldAccessor::~ObjectFieldAccessor(){}


void ObjectFieldAccessor::destroy(JNIEnv* jNIEnv) {}


jobject ObjectFieldAccessor::getValue(JNIEnv* jNIEnv, jobject target, jobject field) {
	jfieldID fieldId = jNIEnv->FromReflectedField(field);
	return jNIEnv->GetObjectField(target, fieldId);
}


jobject ObjectFieldAccessor::getStaticValue(JNIEnv* jNIEnv, jclass target, jobject field) {
	jfieldID fieldId = jNIEnv->FromReflectedField(field);
	return jNIEnv->GetStaticObjectField(target, fieldId);
}


void ObjectFieldAccessor::setValue(JNIEnv* jNIEnv, jobject target, jobject field, jobject value){
	jfieldID fieldId = jNIEnv->FromReflectedField(field);
	jNIEnv->SetObjectField(target, fieldId, value);
}


void ObjectFieldAccessor::setStaticValue(JNIEnv* jNIEnv, jclass target, jobject field, jobject value){
	jfieldID fieldId = jNIEnv->FromReflectedField(field);
	jNIEnv->SetStaticObjectField(target, fieldId, value);
}


int throwNullPointerExceptionIfNull(NativeEnvironment* environment, JNIEnv* jNIEnv, jobject object, const char message[]) {
	if (object == NULL) {
		jNIEnv->ThrowNew(environment->java_lang_NullPointerExceptionClass, message);
		return -1;
	}
	return 0;
}


jobject checkAndGetFieldValue(
	NativeEnvironment* environment,
	JNIEnv* jNIEnv,
	jobject target, jobject field,
	FieldAccessor* fieldAccessor
) {
	if (throwNullPointerExceptionIfNull(environment, jNIEnv, target, "Target is null") != 0) {
		return NULL;
	}
	if (throwNullPointerExceptionIfNull(environment, jNIEnv, field, "Field is null") != 0) {
		return NULL;
	}
    return fieldAccessor->getValue(jNIEnv, target, field);
}


jobject checkAndGetStaticFieldValue(
	NativeEnvironment* environment,
	JNIEnv* jNIEnv,
	jclass target, jobject field,
	FieldAccessor* fieldAccessor
) {
	if (throwNullPointerExceptionIfNull(environment, jNIEnv, target, "Target is null") != 0) {
		return NULL;
	}
	if (throwNullPointerExceptionIfNull(environment, jNIEnv, field, "Field is null") != 0) {
		return NULL;
	}
    return fieldAccessor->getStaticValue(jNIEnv, target, field);
}


void checkAndSetFieldValue(
	NativeEnvironment* environment,
	JNIEnv* jNIEnv,
	jobject target, jobject field, jobject value,
	FieldAccessor* fieldAccessor
) {
	if (throwNullPointerExceptionIfNull(environment, jNIEnv, target, "Target is null") == 0) {
		fieldAccessor->setValue(jNIEnv, target, field, value);
	}
}


void checkAndSetStaticFieldValue(
	NativeEnvironment* environment,
	JNIEnv* jNIEnv,
	jclass target, jobject field, jobject value,
	FieldAccessor* fieldAccessor
) {
	if (throwNullPointerExceptionIfNull(environment, jNIEnv, target, "Target is null") == 0) {
		fieldAccessor->setStaticValue(jNIEnv, target, field, value);
	}
}
