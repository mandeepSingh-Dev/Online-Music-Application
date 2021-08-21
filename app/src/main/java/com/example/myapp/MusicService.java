package com.example.myapp;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapp.MusicRecylerView.Songs;

import java.io.File;
import java.util.ArrayList;


public class MusicService extends Service {
    private MyBinder mBinder = new MyBinder();
    public MediaPlayer mediaPlayer;
    public static ArrayList<Songs> songsList;

    Intent i;
    LocalBroadcastManager manager;
    int position = 1;
    MediaPlayer player;

   public  BroadcastReceiver receiver=new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           if(intent.getAction().equals("ACTION_POSITION"))
           {
               int pos=intent.getIntExtra("Position",1);
               Log.d("PPOOSSIITTION",pos+"__");
               position=pos;


                /* try {
                     player.reset();
                     player.setDataSource(getApplicationContext(), songsList.get(pos).getSonguri());
                     player.prepare();
                     player.start();
                 }catch (Exception e){}*/
              changeMusic(position);
           }
       }
   };


    @Override
    public void onCreate() {
        super.onCreate();
        player=new MediaPlayer();
        songsList =getSongArrayList();

        //register broadcastReceiver by checking intentFilter "ACTION_POSITION"
       LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("ACTION_POSITION"));


        Log.d("KPKPKP", "onCrearte");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // songs1=getSongArrayList();

       Log.d("KPKPKP", "onStartCommand 34");
       // songs = MainActivity.arrayList;
   // if (!songs1.isEmpty()) {
        Log.d("djkhfdj", songsList.size()+"");



            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("channelid", "foregroundservice", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("GHello");
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"channelid");
            builder.setContentTitle("MUSIC")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("MUSIC PLAY");

        Notification notification= builder.build();

startForeground(101,notification);
         // changeMusic(position);






        // Log.d("djkhfdj", songs.get(position).getSongName() + "   ___" + songs.size());
       /* player = MediaPlayer.create(this, songs.get(position).getSonguri());
        player.start();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                position += 1;
                Log.d("dddPOS", position + " D");
                player = MediaPlayer.create(getApplicationContext(), songs.get(position).getSonguri());
                player.start();
            }
        });*/
    /*} else {
        Log.d("djkhfdj", "song1 arraylist is null");
       // Intent intent1 = new Intent(this, NavigationMainActivity.class);
       // intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
       // startActivity(intent1);
    }*/


/*
if(intent.getAction().equals("ACTION_PLAY")) {


    Log.d("IUUUI", songs.get(position).getSonguri().toString());

    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel("channelid", "foregroundservice", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("GHello");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
    NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"channelid");
    builder.setContentTitle("MUSIC")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("MUSIC PLAY");

    Notification notification= builder.build();

    startForeground(100,notification);


    player = MediaPlayer.create(getApplicationContext(), songs.get(position).getSonguri());
    player.start();
    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            position+=1;
            player = MediaPlayer.create(getApplicationContext(), songs.get(position).getSonguri());
            Log.d("eee",position+"..");
            player.start();
            notification.notify();
        }
    });

    }*/


     /* String songs=intent.getStringExtra("SONG_URI");
       position=intent.getIntExtra("POSITION",1);
        Log.d("POPOPO",position+"ff");

        Log.d("JJJJ","kk"+ songs);
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songs));

     if(mediaPlayer!=null) {
         Log.d("KPKPKP","IF MEDIAPLAYER NOT NULL 41");
         mediaPlayer.start();
          i=new Intent("ACTION_HULLI");
         i.putExtra("DURATION",mediaPlayer.getDuration());
      manager=LocalBroadcastManager.getInstance(getApplicationContext());
     manager.sendBroadcast(i);
     }
     else{
         Toast.makeText(getApplicationContext(),"Media is null", Toast.LENGTH_SHORT).show();
     }

         new Thread(new Runnable() {
             @Override
             public void run() {
                while (mediaPlayer != null) {
                     try {
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
                 Log.d("KPKPKP","onCompletion IN onStartCommand 75");
                 Boolean con=true;
                 i.putExtra("BooleanCOMPLETION",con);
                 i.putExtra("POSITIONN",++position);
                 Log.d("POPOPO",++position+"ff");
                 manager.sendBroadcast(i);




             }
         });
     }*/

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
       stopSelf();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DDEESSTROY","DESTROY");
      stopSelf();
      stopForeground(true);
    }

    public ArrayList<Songs> getSongArrayList()
    {
           ArrayList<Songs> arrayList=new ArrayList<>();
        //if block for android version above or equal to Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
           new Thread(new Runnable() {
               @Override
               public void run() {

                    ContentResolver resolver = getContentResolver();
                    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                    Cursor cursor = resolver.query(uri, null, null, null);

                    while (cursor.moveToNext()) {

                        Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                        Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                        Uri uridata = Uri.withAppendedPath(uri, String.valueOf(id));//getting Uri from _id..
                        Log.d("dfdYYYY", uridata.toString());
                        Log.d("TYTYTYT", artist + duration + name);

                        Bitmap bitmap = null;
                        try {
                            Size size = new Size(300, 300);
                            //  Size size = new Size(100, 100);

                            bitmap = resolver.loadThumbnail(uridata, size, null);  //getting Bitmap

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null && artist != null) {
                            arrayList.add(new Songs(uridata, name, artist, duration, bitmap));
                        } else {
                            artist = "No Artist";
                            arrayList.add(new Songs(uridata, name, artist, duration));
                        }

                    }

                }
            }).start();


        }

        //if block for android version below Q
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

            File file = Environment.getExternalStorageDirectory();

            // File file = Environment.getRootDirectory();
            ExecutorSingleton.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    ArrayList<File> fileAL = getDirectories(file);  //getting files in Thread handler...

                    for (int i = 0; i < fileAL.size(); i++) {

                        try {
                            String name = fileAL.get(i).getName(); //get all files name...
                            // Log.d("nameofSONG",name);
                            Uri uri = Uri.fromFile(fileAL.get(i));
                            Log.d("nameofSONG", uri.toString());//get file and parse into Uri.

                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();   //For getting Bitmap image from song.
                            retriever.setDataSource(fileAL.get(i).toString());

                            byte[] bytes = retriever.getEmbeddedPicture();
                            Bitmap bitmap = null;
                            if (bytes != null) {
                                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);   //geting Bitmap
                            } else {
                                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.musictwo_tone);
                            }
                            //To get metadata(Artist and Duration)
                            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            Long duration = Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                            // now adding data to Arraylist..
                            if (!(bitmap == null)) {
                                arrayList.add(new Songs(uri, name, artist, duration, bitmap));
                            } else {
                                arrayList.add(new Songs(uri, name, artist, duration));
                            }
                        } catch (Exception e) {
                        }
                    }  //for loop closed
                }
            });
        }

        return arrayList;
    }

    //getting  arraylist of all files from this method.
    // This method for below android Q.
    public ArrayList<File> getDirectories(File file) {

        ArrayList<File> fileArrayList;
        Log.d("dddddddddd", Thread.currentThread().toString());

        File[] filesarr = file.listFiles();
        fileArrayList = new ArrayList<>();
        for (File singlefile : filesarr) {

            if (singlefile.isDirectory() && !singlefile.isHidden()) {
                fileArrayList.add(singlefile);
                ArrayList<File> filesss = getDirectories(singlefile);
                fileArrayList.addAll(filesss);
            } else {
                if (singlefile.getName().endsWith(".mp3")) {

                    fileArrayList.add(singlefile.getAbsoluteFile());
                    Log.d("sfdfdf", singlefile.getName());

                    // CtentResolver resolver=getContext().getContentResolver();
                    // Size size=new Size(100,100);
                    //  Bitmap bitmap=  ThumbnailUtils.createAudioThumbnail(singlefile.getAbsoluteFile(),size,null);
                    // BitmapFactory.decodeFile()
                }
            }
        }
        return fileArrayList;

    }

public void changeMusic(int positionn)
{
   position=positionn;
   Log.d("LOLO",position+"_+");
   /* if(!songsList.isEmpty())
    {*/
        try {
            player.reset();
            player.setDataSource(songsList.get(position).getSonguri().toString());
            player.prepare();
            player.start();
        }catch (Exception e){}

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                position += 1;
                try {
                    player.reset();
                    player.setDataSource(songsList.get(position).getSonguri().toString());
                    player.prepare();
                    player.start();
                }catch(Exception e){}
            }
        });
       // startForeground(100,notification);

   // }

}


}
