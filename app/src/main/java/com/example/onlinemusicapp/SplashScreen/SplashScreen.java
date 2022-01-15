package com.example.onlinemusicapp.SplashScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.KeyEventDispatcher;

import com.airbnb.lottie.LottieAnimationView;
import com.example.onlinemusicapp.MainActivity;
import com.example.onlinemusicapp.MusicServices.MusicService;
import com.example.onlinemusicapp.NavigationMainActivity;
import com.example.onlinemusicapp.R;
import com.example.onlinemusicapp.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SplashScreen extends AppCompatActivity {
    private Animation animation,bottom_anim;
    private ActivitySplashScreenBinding mBinding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaPlayer.create(getApplicationContext(),R.raw.welcomesong).start();
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding=ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view=mBinding.getRoot();
        setContentView(view);
        Log.d("STARTED","STARTED");
        try {
           KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
           SecretKey secretKey = keyGenerator.generateKey();
          Cipher cipher = Cipher.getInstance("AES");
          SecretKeySpec  keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            cipher.init(cipher.ENCRYPT_MODE, keySpec);
          byte[] encryptedmsg = cipher.doFinal("Hello mr Mandeep".getBytes());
             Log.d("MEESSSAGE",new String(encryptedmsg));
            KeyGenerator keyGenerator2 = KeyGenerator.getInstance("AES");
            SecretKey secretKey2 = keyGenerator.generateKey();
            Cipher cipher2 = Cipher.getInstance("AES");
            SecretKeySpec  keySpec2 = new SecretKeySpec(secretKey.getEncoded(), "AES");
            cipher2.init(cipher2.DECRYPT_MODE, keySpec2);
            byte[] decryptedmsg = cipher2.doFinal(encryptedmsg);
            Log.d("MEESSSAGE",new String(decryptedmsg));
        }catch (Exception e) {
            Log.d("CRYPTION", e.getMessage());
        }






      /*  BroadcastReceiver receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };*/


      /*  DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
       ComponentName mAdminName = new ComponentName(this, receiver.getClass());

       *//* Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
        launcher.launch(intent);
*/
      //  Log.d("kffjdkfjd",mDPM.isAdminActive(mAdminName)+"");
       /* try {
           if (!mDPM.isAdminActive(mAdminName)) {

               Log.d("dfddfdfdfdfd","Admin is not active");
               Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
               intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
               intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");

           } else {
           }
       }
     catch (Exception e) {
        e.printStackTrace();
    }*/
    LottieAnimationView lottieAnimationView = (LottieAnimationView) findViewById(R.id.lottie);
        lottieAnimationView.setImageAssetsFolder("images/");
        //lottieAnimationView.setAnimation(R.raw.girlmusiclottie);
        lottieAnimationView.setAnimation(R.raw.music_symb_lottie);

        lottieAnimationView.loop(false);
        lottieAnimationView.playAnimation();
       // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1000);

        int permission=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d("etiurtir",permission+"");
        if(permission!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);

        }
        else{
            Intent intenttt=new Intent(this, MusicService.class);
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
                    if(user!=null && user.getEmail()!=null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SplashScreen.this,"user is not null"+user.getEmail(),Toast.LENGTH_SHORT).show();

                            }
                        });
                        Intent i = new Intent(SplashScreen.this, NavigationMainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SplashScreen.this,"user is  null",Toast.LENGTH_SHORT).show();

                            }
                        });
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }

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
        finish();
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

                    Intent intenttt = new Intent(this, MusicService.class);
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
                    if(user!=null && user.getEmail()!=null) {
                        Log.d("UUnnnnnnnnnnID",user.getUid()+"d");
                        Intent i = new Intent(SplashScreen.this, NavigationMainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        Log.d("UUnnnnnnnnnnID","null"+"d");

                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
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