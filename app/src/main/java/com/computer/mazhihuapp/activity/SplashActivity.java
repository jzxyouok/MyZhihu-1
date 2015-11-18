package com.computer.mazhihuapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.computer.mazhihuapp.MainActivity;
import com.computer.mazhihuapp.R;
import com.computer.mazhihuapp.utils.net.Constant;
import com.computer.mazhihuapp.utils.net.HttpUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by computer on 2015/9/14.
 */
public class SplashActivity extends Activity {
    private ImageView iv_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        iv_start = (ImageView) findViewById(R.id.iv_start);
        initImage();
    }

    private void initImage(){
        File dir = getFilesDir();
        final File imgFile = new File(dir,"start.jpg");
        if(imgFile.exists()){
            iv_start.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        }else {
            iv_start.setImageResource(R.mipmap.start);
        }
        final ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,1.2f,1.0f,1.2f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(3000);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (HttpUtils.isNetworkConnected(SplashActivity.this)) {
                    HttpUtils.get(Constant.START, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, final byte[] bytes) {
                            try {
                                JSONObject jsonObject = new JSONObject(new String(bytes));
                                String url = jsonObject.getString("img");
                                HttpUtils.getImage(url, new BinaryHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                                        saveImage(imgFile, bytes);
                                        startActivity();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable error) {
                                        startActivity();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            startActivity();
                        }
                    });


                } else {
                    Toast.makeText(SplashActivity.this, "没有网络连接！", Toast.LENGTH_LONG).show();
                    startActivity();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv_start.startAnimation(scaleAnimation);


    }
    private void startActivity(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();

    }


    public void saveImage(File file, byte[] bytes) {
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
