//this code  mainly refers to  https://github.com/nihui/ncnn-android-squeezenet

package com.tencent.posencnn;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

public class PoseNcnn
{
    public native boolean Init(AssetManager mgr);

    public native String Detect(Bitmap bitmap, boolean mul_threads);

    static {
        System.loadLibrary("posencnn");
    }
}
