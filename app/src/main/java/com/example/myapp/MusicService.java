package com.example.myapp;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.util.Size;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapp.MusicRecylerView.Songs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;


public class MusicService extends Service {
    private final MyBinder mBinder = new MyBinder();
    public static ArrayList<Songs> songsList;

    MediaSessionCompat mediaSessionCompat;



    LocalBroadcastManager manager;
    int position = 1;
    MediaPlayer player;
    Intent intent = new Intent("ACTION_SEND");

    boolean play_pauseConn=true;
    int play_pause_notification=R.drawable.play_notification;



   public  BroadcastReceiver receiver=new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           if(intent.getAction().equals("ACTION_POSITION"))
           {
               int pos=intent.getIntExtra("Position",1);
            //   Log.d("POSiTION",pos+"__");
               position=pos;

               changeMusic(pos);
           }
       }
   };


    @Override
    public void onCreate() {
        super.onCreate();
        if (player == null){
            player = new MediaPlayer();
    }
           songsList = getSongArrayList();

           //Initialize LocalBroadcastManager object here
        manager=LocalBroadcastManager.getInstance(getApplicationContext());

        //register broadcastReceiver by checking intentFilter "ACTION_POSITION"
       LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("ACTION_POSITION"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("ACTION_DESTROY"));

        mediaSessionCompat=new MediaSessionCompat(getBaseContext(),"Music Service");
       // Log.d("KPKPKP", "onCrearte");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // songs1=getSongArrayList();

     //  Log.d("KPKPKP", "onStartCommand 34");
       // songs = MainActivity.arrayList;
   // if (!songs1.isEmpty()) {
       // Log.d("djpkhfdj", songsList.size()+"");

        //if(intent.getAction().equals("ACTION_START_FROM_MUSICFRAGMENT")) {
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("HJO", "HDFDIHFN");
                // NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
               // mediaSessionCompat = new MediaSession(getBaseContext(), "MusicService");
               // mediaSessionCompat.setActive(true);


                Log.d("MEDIAAA", mediaSessionCompat.getSessionToken().toString() + mediaSessionCompat.isActive() + "1");

               NotificationChannel channel = new NotificationChannel("channelid", "foregroundservice", NotificationManager.IMPORTANCE_HIGH);
              //  channel.setDescription("GHello");
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
                // }
                Log.d("MEDIAAA",mediaSessionCompat.getSessionToken().toString()+mediaSessionCompat.isActive()+"2");

                PendingIntent pendingIntentPrevious;
                int drw_previous;
               // if (position == 0){
                  //  pendingIntentPrevious = null;
                  //  drw_previous = 0;
              //  } else {
                    Intent intentPrevious = new Intent(this, MusicService.class)
                            .setAction("ACTION_PREVIUOS");
                    pendingIntentPrevious = PendingIntent.getService(this, 0,
                            intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
                    drw_previous = R.drawable.music_two_tonne;

                Intent intentPlay = new Intent(this, MusicService.class)
                        .setAction("ACTION_PLAY");
                PendingIntent pendingIntentPlay = PendingIntent.getService(this, 0,
                        intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent intentNext = new Intent(this, MusicService.class)
                        .setAction("ACTION_NEXT");
              PendingIntent  pendingIntentNext = PendingIntent.getService(this, 0,
                        intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
               // }
                Log.d("MEDIAAA",mediaSessionCompat.getSessionToken().toString()+mediaSessionCompat.isActive()+"3");

                Bitmap bitmap=BitmapFactory.decodeFile("android.graphics.Bitmap@ffeece5");

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channelid");
                builder.setContentTitle("MUSIC")
                       .setSmallIcon(R.drawable.ic_launcher_background)
                      // .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                       // .setLargeIcon(bitmap)
                       .setColor(Color.BLUE)
                        .setContentTitle(songsList.get(position).getSongName())
                        .setContentText("Kulbir ")
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.music_two_tonne))
                        .setOnlyAlertOnce(true)
                        .setShowWhen(false)
                        .setProgress(10000,4000,true)

                        .addAction(R.drawable.music_two_tonne,"Previous",pendingIntentPrevious)
                        .addAction(R.drawable.ic_launcher_background,"Previous",pendingIntentPlay)
                        .addAction(R.drawable.ic_launcher_background,"Previous",pendingIntentNext)
                 .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()))
                        .setPriority(NotificationCompat.PRIORITY_MAX);
                Log.d("MEDIAAA",mediaSessionCompat.getSessionToken().toString()+mediaSessionCompat.isActive()+"4");

                Notification notification = builder.build();

               // notificationManagerCompat.notify(1, notification);
                Log.d("MEDIAAA",mediaSessionCompat.getSessionToken().toString()+mediaSessionCompat.isActive()+"5");
         //   NotificationManagerCompat.from(MusicService.this).notify(1, builder.build());

                startForeground(101,notification);
       }
*/
//}
//this block for first time starting of service from SplashScreen
// Because at First tym starting service our songsList will be 0
// But at second tym songlist will be not empty so
// in above if block we get startservice from MusicFragment.
 /*else*/ if(intent.getAction().equals("ACTION_START_FROM_SPLASHSCREEN"))
 {//Log.d("SplashStartSERVICE","SERVICE_START_FROM_SPLASH");
     }
         // changeMusic(position);
        //this else if block for pending intent.
else if(intent.getAction().equals("ACTION_PLAY"))
        {
            if(player.isPlaying())
            {
                player.pause();
            }
            else if(!player.isPlaying())
            {
                player.start();
            }
        }
else if(intent.getAction().equals("ACTION_PREVIUOS"))
        {
            changeMusic(--position);
           // Log.d("pendingintentposition",position+"_previous");
        }
        else if(intent.getAction().equals("ACTION_NEXT"))
        {
            changeMusic(++position);
          //  Log.d("pendingintentposition",position+"_next");
        }





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
     }*/  FirebaseStorage mstorage=FirebaseStorage.getInstance();
        StorageReference mreference=mstorage.getReference();
        mreference.child("majhail.mp3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               // MediaPlayer.create(getApplicationContext(),uri).start();

            }
        });
      /*  mreference.child("majhail.mp3").getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Log.d("METADATAAFIREBASE", storageMetadata.getName()+"\n"+storageMetadata.getContentType()+"\n"+storageMetadata.getSizeBytes()+"\n"+storageMetadata.getBucket()+storageMetadata.getContentLanguage());

            }
        });*//*.getBytes(1000000*10).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    String s = new String(bytes, "UTF-8");
                    Uri uri = Uri.parse(s);
                    MediaPlayer.create(getApplicationContext(),uri).start();
                }catch (Exception e){}
                }
        });*/

    return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
        }

   public void stop()
   {
     //  Log.d("OOO","ONSTOP MUSICSERVICE");

   }



  public  void reset()
   {
       player.reset();
   }
   public int getDuration()
   {
     return player.getDuration();
   }
   public boolean isPlaying()
   {
       return player.isPlaying();
   }

    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }



    public void pause() {
        player.pause();
    }

    public void start() {
        player.start();
    }

    public void seekTo(int progress)
    {
        player.seekTo(progress);
    }




    //MyBinder inner class for getting MusicService refernce Through
    //// IBinder of onBind and onServiceConnccted
    //OR creating this inner class for returning MyBinder object through onBind() method.
    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
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

                        long songSize=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                         Uri uridata = Uri.withAppendedPath(uri, String.valueOf(id));//getting Uri from _id..
                      //  Log.d("dfdYYYY", uridata.toString());
                       // Log.d("TYTYTYT", artist + duration + name);

                       // Log.d("416line_feature adding",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED))+name);
                        String dateModified=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));

                        Bitmap bitmap = null;
                        try {
                            Size size = new Size(300, 300);
                            //  Size size = new Size(100, 100);
                            bitmap = resolver.loadThumbnail(uridata, size,null);
//GITHUB
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null && artist != null) {
                            arrayList.add(new Songs(uridata, name, artist, duration, bitmap,dateModified,songSize));
                        } else {
                            artist = "No Artist";
                            arrayList.add(new Songs(uridata, name, artist, duration,dateModified,songSize));
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
                          //  Log.d("nameofSONG", uri.toString());//get file and parse into Uri.

                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();   //For getting Bitmap image from song.
                            retriever.setDataSource(fileAL.get(i).toString());
                          //  Log.d("HUMBAHOOHUMBA",fileAL.get(i).toString());


                            byte[] bytes = retriever.getEmbeddedPicture();
                            Bitmap bitmap = null;
                            if (bytes != null) {
                                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);   //geting Bitmap
                                Log.d("Hjei",bitmap.toString());
                            } else {
                                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.music_two_tonne);
                            }
                            //To get metadata(Artist and Duration)
                            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            Long duration = Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                            //getting last modified date of file/song.
                            Long dateModified=fileAL.get(i).lastModified();


                            long songSize=fileAL.get(i).length();
                            // now adding data to Arraylist..
                            if (bitmap != null) {
                                arrayList.add(new Songs(uri, name, artist, duration, bitmap,String.valueOf(dateModified),songSize));
                            } else {
                                arrayList.add(new Songs(uri, name, artist, duration,String.valueOf(dateModified),songSize));
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
       // Log.d("dddddddddd", Thread.currentThread().toString());

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
                  //  Log.d("sfdfdf", singlefile.getName());

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
  // position=positionn;
  // Log.d("LOLO",position+"_+");
   /* if(!songsList.isEmpty())
    {*/
    if(positionn<=-1)
    {
      //  Log.d("reset_position",positionn+"_");
        player.reset();
        position=0;
       // player=null;
    }
    else if (positionn <= songsList.size() - 1) {


        // Intent intent = new Intent("ACTION_SEND");
        intent.putExtra("receivedPosition", positionn);
        intent.putExtra("TOTAL_DURATION", player.getDuration());
        intent.putExtra("CURRENT_DURATION", player.getCurrentPosition());


       // Log.d("Hello", positionn + "hello");
        manager.sendBroadcast(intent);
        player.reset();

        try {
           // Log.d("SONGUURRII", songsList.get(positionn).getSonguri().toString());



            player.setDataSource(this, songsList.get(position).getSonguri());
            player.prepare();
            player.start();

            getCurrentPosiotnnn();
        } catch (Exception e) {
        }
        showNotification(positionn,play_pause_notification);

      /*  if(play_pauseConn) {
            showNotification(positionn,play_pause_notification);
        }
        else{
            play_pause_notification=R.drawable.ic_baseline_pause_24;
            showNotification(positionn,play_pause_notification);
        }*/
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                changeMusic(++position);

            }
        });
    }
    else if(positionn>songsList.size()-1)
    {
        position=0;
    }

       // startForeground(100,notification);

   // }

}




    public int getCurrentPosiotnnn() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (player != null) {
                    try {
                        if (player.isPlaying()) {
                             intent.putExtra("CURRENTDURATION", player.getCurrentPosition());
                             manager.sendBroadcast(intent);
                          //  Log.d("HEJO", player.getCurrentPosition() + "__position");
                            Thread.sleep(1000);

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return  player.getCurrentPosition();

    }


public void showNotification(int possition,int play_pauseICON) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      //  Log.d("HJO", "HDFDIHFN");
        // NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        // mediaSessionCompat = new MediaSession(getBaseContext(), "MusicService");
        // mediaSessionCompat.setActive(true);


        mediaSessionCompat.setMetadata(
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadata.METADATA_KEY_TITLE, songsList.get(possition).getSongName())
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, songsList.get(possition).getArtist())
                        .putBitmap(MediaMetadata.METADATA_KEY_ART, songsList.get(possition).getBitmap())
                        .build());


        NotificationChannel channel = new NotificationChannel("channelid", "foregroundservice", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null,null);
        //  channel.setDescription("GHello");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        // }
      //  Log.d("MEDIAAA", mediaSessionCompat.getSessionToken().toString() + mediaSessionCompat.isActive() + "2");
    }
    PendingIntent pendingIntentPrevious;
    int drw_previous;
    // if (position == 0){
    //  pendingIntentPrevious = null;
    //  drw_previous = 0;
    //  } else {
    Intent intentPrevious = new Intent(this, MusicService.class)
            .setAction("ACTION_PREVIUOS");
    pendingIntentPrevious = PendingIntent.getService(this, 0,
            intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

    Intent intentPlay = new Intent(this, MusicService.class)
            .setAction("ACTION_PLAY");
    PendingIntent pendingIntentPlay = PendingIntent.getService(this, 0,
            intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

    Intent intentNext = new Intent(this, MusicService.class)
            .setAction("ACTION_NEXT");
    PendingIntent pendingIntentNext = PendingIntent.getService(this, 0,
            intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
    // }
   // Log.d("MEDIAAA", mediaSessionCompat.getSessionToken().toString() + mediaSessionCompat.isActive() + "3");


    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channelid");
    builder.setContentTitle("MUSIC")
            .setSmallIcon(R.drawable.musictwo_ton)
            // .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setLargeIcon(songsList.get(possition).getBitmap())
            .setColor(getResources().getColor(R.color.Green))
            .setContentTitle(songsList.get(possition).getSongName())
            .setContentText(songsList.get(possition).getArtist())
            .setLargeIcon(songsList.get(possition).getBitmap())
            .setSilent(true)
            .setOnlyAlertOnce(true)
            //.setShowWhen(false)
            .addAction(R.drawable.ic_baseline_navigate_before_24, "Previous", pendingIntentPrevious)
            .addAction(play_pauseICON, "Play", pendingIntentPlay)
            .addAction(R.drawable.ic_baseline_navigate_next_24, "Next", pendingIntentNext)
            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()).setShowCancelButton(true).setShowActionsInCompactView(0, 1, 2))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);


    Notification notification = builder.build();
    startForeground(101, notification);
}

//}

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  Log.d("DDEESSTROY","DESTROY");
       /* Intent inntent=new Intent(this,MusicService.class);
        inntent.setAction("ACTION_STOP");
        startService(inntent);*/
        if(player!=null)
        {
            player.reset();
            player.release();
        }
       // changeMusic(-1);
        stopForeground(true);
        stopSelf();

    }




}
