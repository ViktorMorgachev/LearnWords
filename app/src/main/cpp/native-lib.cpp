#include <jni.h>
#include <string>


extern "C"
JNIEXPORT jstring JNICALL
Java_com_learn_worlds_utils_Keys_eidenTokenDev(JNIEnv *env, jobject thiz) {
    std::string masterAlias = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiYWYxZWY0OTUtYWJiYS00ZThhLWEwNDAtY2ZjZTJiYTkwNTVhIiwidHlwZSI6InNhbmRib3hfYXBpX3Rva2VuIn0.KWrSW61GZfmz1SXEH6NPnEGLf7_uUMYs8CC0VUHfazE";
    return env->NewStringUTF(masterAlias.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_learn_worlds_utils_Keys_eidenTokenProd(JNIEnv *env, jobject thiz) {
    std::string masterAlias = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiYWYxZWY0OTUtYWJiYS00ZThhLWEwNDAtY2ZjZTJiYTkwNTVhIiwidHlwZSI6ImFwaV90b2tlbiJ9.9Zigc45yDeXfiPqqTCFcW44aeIyUOajPfld2yRaetMs";
    return env->NewStringUTF(masterAlias.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_learn_worlds_utils_Keys_eidenFileUriTestProd(JNIEnv *env, jobject thiz) {
    std::string masterAlias = "https://d14uq1pz7dzsdq.cloudfront.net/db44186f-f5e9-4a3a-aea8-62752ed638b4_.mp3?Expires=1700325213&Signature=dFbuXSiafQB-Y5HN0sY~YFXkPw1YZuGnZ-AXFhtcsdx30IVY3payGjf-sDX2WSNiO12ujv~Nm09HhPH2NvDFRoTI4ADX~YJ8DI-ftBPeQZTmSojrjGqrROBmKhVPU7bIgecaFtwZxzEc0HgKY-P44ixQcir3m-ItMJs6sy6PlblCIBgTmfOHWHozCWIVQRFMdiA9E9lpRSBemTuRmu1OehgyGwq0EzATTSHJrRhmRqp9iRvzRsr7k4gpBHnzmwpJdcILenQjm~jUdiyC7zgiRqp9Og6cVCAjWYh5fTMjafvdcfD2hfKE0oo2Qrm8vIk-sclHM1p5OCa-nU5kZ~3T6Q__&Key-Pair-Id=K1F55BTI9AHGIK";
    return env->NewStringUTF(masterAlias.c_str());
}