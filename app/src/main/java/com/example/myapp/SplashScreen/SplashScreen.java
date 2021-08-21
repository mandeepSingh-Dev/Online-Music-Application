package com.example.myapp.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.MainActivity;
import com.example.myapp.MusicService;
import com.example.myapp.R;
import com.example.myapp.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {
    Animation animation,bottom_anim;
    ActivitySplashScreenBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBinding=ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view=mBinding.getRoot();
        setContentView(view);

        Intent intenttt=new Intent(this, MusicService.class);
        startService(intenttt);

        if(getActionBar()!=null) {
            getSupportActionBar().hide();
        }

        animation= AnimationUtils.loadAnimation(this,R.anim.animsplash);
        bottom_anim= AnimationUtils.loadAnimation(this,R.anim.animwelcome);

        mBinding.constraintlayout.setAnimation(animation);
        mBinding.welcomeText.setAnimation(bottom_anim);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDestroy();
        //stopService(intenttt);
    }


}