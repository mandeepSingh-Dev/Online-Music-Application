package com.example.myapp;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MusicService extends Service
{
    private MyBinder mBinder=new MyBinder();
    public MediaPlayer mediaPlayer;
    Intent i;
    LocalBroadcastManager manager;
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("uuuu","onCrearte");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LUUU","onStartCommand");

      String songs=intent.getStringExtra("SONG_URI");
        Log.d("JJJJ","kk"+ songs);
      mediaPlayer=MediaPlayer.create(this, Uri.parse(songs));
     if(mediaPlayer!=null) {
         mediaPlayer.start();
          i=new Intent("ACTION_HULLI");
         i.putExtra("DURATION",mediaPlayer.getDuration());
      manager=LocalBroadcastManager.getInstance(this);
     manager.sendBroadcast(i);
     }
     else{
         Toast.makeText(this,"Media is null",Toast.LENGTH_SHORT).show();
     }

         new Thread(new Runnable() {
             @Override
             public void run() {
                while (mediaPlayer != null) {
                     try {
//                        Log.i("Thread ", "Thread Called");
                         // create new message to send to handler
                         if (mediaPlayer.isPlaying()) {
                             i.putExtra("CURRENTDURATION", mediaPlayer.getCurrentPosition());
                             manager.sendBroadcast(i);
                             Thread.sleep(1000);

                         }
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }).start();

     if(mediaPlayer!=null)
     {
         mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
             @Override
             public void onCompletion(MediaPlayer mp) {
                 Boolean con=true;
                 i.putExtra("BooleanCOMPLETION",con);
                 manager.sendBroadcast(i);
             }
         });
     }

    return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

   public void stop()
   {
       Log.d("OOO","ONSTOP MUSICSERVICE");
       mediaPlayer.stop();
   }

  public  void reset()
   {
       mediaPlayer.reset();
   }
   public int getDuration()
   {
     return mediaPlayer.getDuration();
   }
   public boolean isPlaying()
   {
       return mediaPlayer.isPlaying();
   }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }



    public void pause() {
        mediaPlayer.pause();
    }

    public void start() {
        mediaPlayer.start();
    }

    public void seekTo(int progress)
    {
        mediaPlayer.seekTo(progress);
    }








    //MyBinder inner class for getting MusicService refernce Through
    //// IBinder of onBind and onServiceConnccted
    //OR creating this inner class for returning MyBinder object through onBind() method.
    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
