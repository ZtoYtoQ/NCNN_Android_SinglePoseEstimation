# NCNN_Android_SinglePoseEstimation
## Introduction 
The simple project mainly provides Single Pose Estimation demo in Android though NCNN .

## Usage

### S1:

1. Download ncnn-android-vulkan-lib.zip for [NCNN Releases](https://github.com/Tencent/ncnn/releases)
2. Download INT8 model from [BaiduCloud](https://pan.baidu.com/s/1QOCGh99e_3jVZgH9XMdhlQ) (Password：22qe)  or [hg_pose-int8.zip](https://github.com/ZtoYtoQ/NCNN-PoseEstimation/releases)

### S2:

```bash
git clone https://github.com/ZtoYtoQ/NCNN_SinglePoseEstimation.git

unzip ncnn-android-vulkan-lib.zip  {your clone root-dir}/app/src/main/jni 
cp { model download path /hg_pose-int8.bin}  {clone root-dir}/app/src/main/assets/ 
cp { model download path /hg_pose-int8.param}  {clone root-dir}/app/src/main/assets/ 
```

### S3:

Open this project with Android Studio, build it and run.

**NOTE**: this project with  SDK version: ,NDK version:


## Running Time

|               **Device**               | Thread : 1 | Threads : 2 |
| :------------------------------------: | :--------: | :---------: |
|      Mi 10 ( Snapdragon 865 CPU )      |  ~640 ms   |   ~350 ms   |
| Samsung S7 edge ( Snapdragon 820 CPU ) |  ~1400 ms  |   ~900 ms   |

## Demo



## Reference

NCNN-android-image classification :   https://github.com/nihui/ncnn-android-squeezenet

## License
[MIT](https://choosealicense.com/licenses/mit/)
