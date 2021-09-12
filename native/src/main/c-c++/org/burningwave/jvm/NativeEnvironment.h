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

#ifndef org_burningwave_jvm_Environment_H
	#define org_burningwave_jvm_Environment_H
	#ifndef CLASS_00002_NAME
		#define CLASS_00002_NAME org_burningwave_jvm_Environment
	#endif


	class FieldAccessor {

		public:
			FieldAccessor(JNIEnv* env);

			virtual ~FieldAccessor();

			virtual void destroy(JNIEnv* env);

			virtual jobject getValue(JNIEnv* env, jobject, jobject) = 0;

			virtual jobject getStaticValue(JNIEnv* env, jclass, jobject) = 0;

			virtual void setValue(JNIEnv* env, jobject, jobject, jobject) = 0;

			virtual void setStaticValue(JNIEnv* env, jclass, jobject, jobject) = 0;

	};


	template<typename Type>
	class PrimitiveFieldAccessor : public FieldAccessor {
		public :
			PrimitiveFieldAccessor (
				JNIEnv* env, const char name[], const char valueOfMethodSig[],
				Type (JNIEnv::*callTypeMethodFunction) (jobject, jmethodID, ...),
				const char callTypeMethodName[], const char callTypeMethodSig[],
				Type (JNIEnv::*getFieldValueFunction) (jobject, jfieldID),
				Type (JNIEnv::*getStaticFieldValueFunction) (jclass, jfieldID),
				void (JNIEnv::*setValueFunction) (jobject, jfieldID, Type),
				void (JNIEnv::*setStaticValueFunction) (jclass , jfieldID, Type)
			);

			~PrimitiveFieldAccessor();

			void destroy(JNIEnv* env);

			jobject getValue(JNIEnv* env, jobject, jobject);

			jobject getStaticValue(JNIEnv* env, jclass, jobject);

			void setValue(JNIEnv* env, jobject, jobject, jobject);

			void setStaticValue(JNIEnv* env, jclass, jobject, jobject);

		private:
			jclass wrapperClass;
			jmethodID valueOfMethodId;
			jmethodID callTypeMethodId;
			jobject (JNIEnv::*callStaticObjectMethod) (jclass, jmethodID, ...);
			Type (JNIEnv::*callTypeMethodFunction) (jobject, jmethodID, ...);
			Type (JNIEnv::*getFieldValueFunction) (jobject, jfieldID);
			Type (JNIEnv::*getStaticFieldValueFunction) (jclass, jfieldID);
			void (JNIEnv::*setValueFunction) (jobject, jfieldID, Type);
			void (JNIEnv::*setStaticValueFunction) (jclass, jfieldID, Type);
	};


	class ObjectFieldAccessor : public FieldAccessor {
		public :
			ObjectFieldAccessor (JNIEnv* env);

			~ObjectFieldAccessor();

			void destroy(JNIEnv* env);

			jobject getValue(JNIEnv* env, jobject, jobject);

			jobject getStaticValue(JNIEnv* env, jclass, jobject);

			void setValue(JNIEnv* env, jobject, jobject, jobject);

			void setStaticValue(JNIEnv* env, jclass, jobject, jobject);

	};


	class NativeEnvironment {
		public:
			NativeEnvironment(JNIEnv* env);

			void destroy(JNIEnv* env);

		public:
			ObjectFieldAccessor* objectFieldAccessor;
			PrimitiveFieldAccessor<jint>* jintFieldAccessor;
			PrimitiveFieldAccessor<jlong>* jlongFieldAccessor;
			PrimitiveFieldAccessor<jfloat>* jfloatFieldAccessor;
			PrimitiveFieldAccessor<jdouble>* jdoubleFieldAccessor;
			PrimitiveFieldAccessor<jboolean>* jbooleanFieldAccessor;
			PrimitiveFieldAccessor<jbyte>* jbyteFieldAccessor;
			PrimitiveFieldAccessor<jchar>* jcharFieldAccessor;
			jfieldID java_lang_invoke_MethodHandles$Lookup_allowedModesFieldId;
			jfieldID java_lang_reflect_AccessibleObject_overrideFieldId;
			jclass java_lang_NullPointerExceptionClass;

			void init(JNIEnv*);
	};


	int throwNullPointerExceptionIfNull(NativeEnvironment*, JNIEnv*, jobject, const char[]);


	jobject checkAndGetFieldValue(NativeEnvironment*, JNIEnv* jNIEnv, jobject, jobject,	FieldAccessor*);


	jobject checkAndGetStaticFieldValue(NativeEnvironment*, JNIEnv*, jclass, jobject, FieldAccessor*);


	void checkAndSetFieldValue(NativeEnvironment*, JNIEnv*, jobject, jobject, jobject, FieldAccessor*);


	void checkAndSetStaticFieldValue(NativeEnvironment*, JNIEnv*, jclass, jobject, jobject,	FieldAccessor*);

#endif
