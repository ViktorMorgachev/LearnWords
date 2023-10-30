#include <jni.h>
#include <string>


extern "C"
JNIEXPORT _jstring * JNICALL
Java_com_learn_worlds_data_dataSource_remote_DatabaseRefKt_getFirebaseRef(JNIEnv *env,
                                                                          jclass clazz) {
    std::string masterAlias = "https://learnwords-69696-default-rtdb.firebaseio.com";
    return env->NewStringUTF(masterAlias.c_str());
}