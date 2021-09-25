package com.example.myapp.SplashScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapp.MainActivity;
import com.example.myapp.MusicService;
import com.example.myapp.R;
import com.example.myapp.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {
    Animation animation,bottom_anim;
    ActivitySplashScreenBinding mBinding;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBinding=ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view=mBinding.getRoot();
        setContentView(view);

        //TODO GETTING CUSTOM STYLE PERMISSION FOR STORAGE..
        int permission=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,101);

        }
        else{ Intent intenttt=new Intent(this, MusicService.class);
            intenttt.setAction("ACTION_START_FROM_SPLASHSCREEN");
            startService(intenttt);

            //MainActivity will start after seconds
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent i=new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });
            thread.start();
        }
        /*Intent intenttt=new Intent(this, MusicService.class);
        intenttt.setAction("ACTION_START_FROM_SPLASHSCREEN");
        startService(intenttt);
*/
        if(getActionBar()!=null) {
            getSupportActionBar().hide();
        }

        animation= AnimationUtils.loadAnimation(this,R.anim.animsplash);
        bottom_anim= AnimationUtils.loadAnimation(this,R.anim.animwelcome);

        mBinding.constraintlayout.setAnimation(animation);
        mBinding.welcomeText.setAnimation(bottom_anim);



       /* Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent i=new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        thread.start();*/



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDestroy();
        //stopService(intenttt);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case 101:
            {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    Intent intenttt=new Intent(this, MusicService.class);
                    intenttt.setAction("ACTION_START_FROM_SPLASHSCREEN");
                    startService(intenttt);

                    //MainActivity will start after seconds
                   /* Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                            Intent i=new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(i);
                           finish();
                        /*}
                    });
                    thread.start();*/

                }
                else{
                    Toast.makeText(this,"Permission Denied \n Songs Doesn't not show ",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}