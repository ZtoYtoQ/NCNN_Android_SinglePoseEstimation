//this code  mainly refers to  https://github.com/nihui/ncnn-android-squeezenet

package com.tencent.posencnn;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;

public class MainActivity extends Activity
{
    private static final int SELECT_IMAGE = 1;

    private TextView infoResult;
    private ImageView imageView;
    private Bitmap yourSelectedImage = null;

    private PoseNcnn posencnn = new PoseNcnn();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        boolean ret_init = posencnn.Init(getAssets());
        if (!ret_init)
        {
            Log.e("MainActivity", "posencnn Init failed");
        }

        infoResult = (TextView) findViewById(R.id.infoResult);
        imageView = (ImageView) findViewById(R.id.imageView);

        Button buttonImage = (Button) findViewById(R.id.buttonImage);
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, SELECT_IMAGE);
            }
        });

        Button buttonSingle_Thread = (Button) findViewById(R.id.buttonSingle_Thread);
        buttonSingle_Thread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (yourSelectedImage == null)
                    return;
                // resize to 256x256
                Bitmap yourSelectedImage_ = Bitmap.createScaledBitmap(yourSelectedImage, 256, 256, false);
                String result = posencnn.Detect(yourSelectedImage_, false);

                if (result == null)
                {
                    infoResult.setText("detect failed");
                }
                else
                {
                    //infoResult.setText(result);
                    String[] split = result.split(",");
                    Log.i("MainActivity","result lengh："+split.length);
                    infoResult.setText(split[0]);
                    Bitmap drawBitmap = yourSelectedImage.copy(Bitmap.Config.ARGB_8888, true);
                    //
                    int width = drawBitmap.getWidth();
                    int height = drawBitmap.getHeight();
                    Log.i("MainActivity","图宽："+width+"高："+height);

                    float r_width = width /64.0f;
                    float r_height = height /64.0f;
                    Log.i("MainActivity","r_width："+r_width+"r_height："+r_height);

                    int j=0;
                    float[]  x_y = new float[32];
                    for(int i =1;i<=16;i++)
                    {
                        if(Float.parseFloat(split[i*3])>0.2)
                        {
                            x_y[j]= r_width * Float.parseFloat(split[i*3-2]);
                            j++;
                            x_y[j]= r_height * Float.parseFloat(split[i*3-1]);
                            j++;
                        }
                        else
                        {
                            x_y[j]= 0.0f;
                            j++;
                            x_y[j]= 0.0f;
                            j++;
                        }
                    }
                    //Log.i("MainActivity","x_y:"+x_y);
                    Canvas canvas = new Canvas(drawBitmap);
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.STROKE);//不填充
                    paint.setStrokeWidth(8);  //线的宽度
                    canvas.drawPoints(x_y,paint);
                    imageView.setImageBitmap(drawBitmap);

                }
            }
        });

        Button buttonMul_Threads = (Button) findViewById(R.id.buttonMul_Threads);
        buttonMul_Threads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (yourSelectedImage == null)
                    return;
                // resize to 256x256
                Bitmap yourSelectedImage_ = Bitmap.createScaledBitmap(yourSelectedImage, 256, 256, false);

                String result = posencnn.Detect(yourSelectedImage_, true);

                if (result == null)
                {
                    infoResult.setText("detect failed");
                }
                else
                {


                    String[] split = result.split(",");
                    Log.i("MainActivity","result lengh："+split.length);
                    infoResult.setText(split[0]);
                    Bitmap drawBitmap = yourSelectedImage.copy(Bitmap.Config.ARGB_8888, true);
                    //
                    int width = drawBitmap.getWidth();
                    int height = drawBitmap.getHeight();
                    Log.i("MainActivity","宽："+width+"高："+height);

                    float r_width = width /64.0f;
                    float r_height = height /64.0f;
                    Log.i("MainActivity","r_width："+r_width+"r_height："+r_height);

                    int j=0;
                    float[]  x_y_ = new float[32];
                    for(int i =1;i<=16;i++)
                    {
                        if(Float.parseFloat(split[i*3])>0.2)
                        {
                            x_y_[j]= r_width * Float.parseFloat(split[i*3-2]);
                            j++;
                            x_y_[j]= r_height * Float.parseFloat(split[i*3-1]);
                            j++;
                        }
                        else
                        {
                            x_y_[j]= 0.0f;
                            j++;
                            x_y_[j]= 0.0f;
                            j++;
                        }
                    }
                    //Log.i("MainActivity","x_y:"+x_y_);
                    Canvas canvas = new Canvas(drawBitmap);
                    Paint paint = new Paint();
                    paint.setColor(Color.YELLOW);
                    paint.setStyle(Paint.Style.STROKE);//不填充
                    paint.setStrokeWidth(8);  //线的宽度
                    canvas.drawPoints(x_y_,paint);
                    //canvas.drawPoints(new float[]{100,100,200,100,100,200,200,200},paint);
                    imageView.setImageBitmap(drawBitmap);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            try
            {
                if (requestCode == SELECT_IMAGE) {
                    Bitmap bitmap = decodeUri(selectedImage);

                    Bitmap rgba = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                    yourSelectedImage = rgba;
                    // resize to 256x256
                    //yourSelectedImage = Bitmap.createScaledBitmap(rgba, 256, 256, false);

                    //rgba.recycle();

                    //imageView.setImageBitmap(bitmap);
                    imageView.setImageBitmap(rgba);
                    //imageView.setImageBitmap(yourSelectedImage);
                }
            }
            catch (FileNotFoundException e)
            {
                Log.e("MainActivity", "FileNotFoundException");
                return;
            }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException
    {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 400;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
               || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

}
