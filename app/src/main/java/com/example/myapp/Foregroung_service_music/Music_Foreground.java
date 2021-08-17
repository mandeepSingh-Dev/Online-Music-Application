package com.example.myapp.Foregroung_service_music;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class Music_Foreground extends Service {
    MediaPlayer mediaPlayer;
    @Override
    public void onCreate() {
        super.onCreate();
       // mediaPlayer=new MediaPlayer();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction()=="ACTION_START")
        {
            Log.d("kdhfdj",intent.getStringExtra("hheelloo"));
            try {

            }catch (Exception e){}
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel=new NotificationChannel("channelId",intent.getStringExtra("hheelloo"),NotificationManager.IMPORTANCE_HIGH);
                mChannel.setDescription(intent.getStringExtra("hheelloo"));

                NotificationManager manager=getSystemService(NotificationManager.class);
                manager.createNotificationChannel(mChannel);
            }
            NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"channelId");
            builder.setContentTitle(intent.getStringExtra("hheelloo"));

            Notification notification=builder.build();

            startForeground(10,notification);

        }
return  START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mediaPlayer.reset();
    }
}
