# NCNN_Android_SinglePoseEstimation
## Introduction 
The simple project mainly provides Single Pose Estimation demo in Android though NCNN .

## Usage

### S1:

1. Download ncnn-android-vulkan-lib.zip for [NCNN Releases](https://github.com/Tencent/ncnn/releases)


### S2:

```bash
git clonehttps://github.com/ZtoYtoQ/NCNN_Android_SinglePoseEstimation.git

unzip ncnn-android-vulkan-lib.zip  { clone root-dir}/app/src/main/jni 

```

### S3:

Open this project with Android Studio, build it and run.

**NOTE**: this project with  SDK version: 26 , Gradle model version=5.6.4 NDK version: 20.0.5594570


## Running Time

|               **Device**               | Thread : 1 | Threads : 2 |
| :------------------------------------: | :--------: | :---------: |
|      Mi 10 ( Snapdragon 865 CPU )      |  ~640 ms   |   ~350 ms   |
| Samsung S7 edge ( Snapdragon 820 CPU ) |  ~1400 ms  |   ~900 ms   |

## Demo

![demo_result](https://github.com/ZtoYtoQ/NCNN_Android_SinglePoseEstimation/blob/master/demo_result.jpg)

## Reference

NCNN-android-image classification :   https://github.com/nihui/ncnn-android-squeezenet

## License
[MIT](https://choosealicense.com/licenses/mit/)
