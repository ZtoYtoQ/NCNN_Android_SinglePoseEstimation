//this code  mainly refers to  https://github.com/nihui/ncnn-android-squeezenet
#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include <android/log.h>

#include <jni.h>

#include <string>
#include <vector>

// ncnn
#include "net.h"
#include "benchmark.h"



static ncnn::UnlockedPoolAllocator g_blob_pool_allocator;
static ncnn::PoolAllocator g_workspace_pool_allocator;


static ncnn::Net posenet;


extern "C" {

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "PoseNcnn", "JNI_OnLoad");

    ncnn::create_gpu_instance();

    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "PoseNcnn", "JNI_OnUnload");

    ncnn::destroy_gpu_instance();
}

// public native boolean Init(AssetManager mgr);
JNIEXPORT jboolean JNICALL Java_com_tencent_posencnn_PoseNcnn_Init(JNIEnv* env, jobject thiz, jobject assetManager)
{
    ncnn::Option opt;
    opt.lightmode = true;
    opt.num_threads = 1;
    opt.blob_allocator = &g_blob_pool_allocator;
    opt.workspace_allocator = &g_workspace_pool_allocator;

    // use vulkan compute
    if (ncnn::get_gpu_count() != 0)
        opt.use_vulkan_compute = true;

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);

    posenet.opt = opt;

    // init param
    {

        int ret = posenet.load_param(mgr, "hg_pose_int8.param");
        if (ret != 0)
        {
            __android_log_print(ANDROID_LOG_DEBUG, "PoseNcnn", "load_param_bin failed");
            return JNI_FALSE;
        }
    }

    // init bin
    {

        int ret = posenet.load_model(mgr, "hg_pose_int8.bin");

        if (ret != 0)
        {
            __android_log_print(ANDROID_LOG_DEBUG, "PoseNcnn", "load_model failed");
            return JNI_FALSE;
        }
    }

    return JNI_TRUE;

}

// public native String Detect(Bitmap bitmap, boolean use_gpu);
JNIEXPORT jstring JNICALL Java_com_tencent_posencnn_PoseNcnn_Detect(JNIEnv* env, jobject thiz, jobject bitmap, jboolean  mul_threads)
{
    //if (mul_threads == JNI_TRUE && ncnn::get_gpu_count() == 0)
    //{
    //    return env->NewStringUTF("no vulkan capable gpu");
    //}

    double start_time = ncnn::get_current_time();

    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);
    int width = info.width;
    int height = info.height;
    if (width != 256 || height != 256)
        return NULL;
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
        return NULL;

    // ncnn from bitmap
    ncnn::Mat in = ncnn::Mat::from_android_bitmap(env, bitmap, ncnn::Mat::PIXEL_BGR);

    if(mul_threads == true) {
        posenet.opt.num_threads = 2;
        __android_log_print(ANDROID_LOG_DEBUG, "PoseNcnn", "  mul_threads");
    }
    else
    {
        posenet.opt.num_threads = 1;
        __android_log_print(ANDROID_LOG_DEBUG, "PoseNcnn", "  single_thread");
    }
    std::string x_y_prob_str;

    {

        const float mean_vals[3] = {112.f, 113.f, 110.f};
        const float norm_vals[3] = {1/255.f, 1/255.f, 1/255.f};

        in.substract_mean_normalize(mean_vals, norm_vals);

        ncnn::Extractor ex = posenet.create_extractor();

        //ex.set_vulkan_compute(use_gpu);
        ex.set_vulkan_compute(false);


        ex.input("0", in);
        ncnn::Mat out;

        ex.extract("940", out);

        for (int p = 0; p < out.c; p++)
        {
            const ncnn::Mat m = out.channel(p);

            float max_prob = 0.f;
            int max_x = 0;
            int max_y = 0;
            for (int y = 0; y < out.h; y++)
            {
                const float* ptr = m.row(y);
                for (int x = 0; x < out.w; x++)
                {
                    float prob = ptr[x];
                    if (prob > max_prob)
                    {
                        max_prob = prob;
                        max_x = x;
                        max_y = y;
                    }
                }
            }

            x_y_prob_str+=std::to_string(float(max_x));
            x_y_prob_str+=',';
            x_y_prob_str+=std::to_string(float(max_y));
            x_y_prob_str+=',';
            x_y_prob_str+=std::to_string(max_prob);
            if(p<out.c-1)
            {
                x_y_prob_str+=',';
            }

        }


    }



    double elasped = ncnn::get_current_time() - start_time;
    __android_log_print(ANDROID_LOG_DEBUG, "PoseNcnn", "%.2fms   detect", elasped);

    char tmp_time[64];
    sprintf(tmp_time, "%.2f", elasped);
    std::string result_str = "Inference time: ";
    result_str += tmp_time;
    result_str +=" ms,";
    result_str += x_y_prob_str;
    const char* str = &result_str[0];
    __android_log_print(ANDROID_LOG_DEBUG, "PoseNcnn",str," length: %.d ", result_str.length());
    jstring result = env->NewStringUTF(result_str.c_str());

    return result;
}

}