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
import android.media.AudioManager;
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
import android.util.Base64;
import android.util.Log;
import android.util.Size;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapp.MusicRecylerView.Songs;
import com.example.myapp.MusicRecylerView.Songs_FireBase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageMetadata;

import java.io.File;
import java.util.ArrayList;


public class MusicService extends Service {
    private final MyBinder mBinder = new MyBinder();
    public static ArrayList<Songs> songsList;

    public static ArrayList<Songs_FireBase> songListFirebase;
    public ArrayList<Songs> songsFireList;

    MediaSessionCompat mediaSessionCompat;


    private LocalBroadcastManager broadcastmanager;
    int position = 0;
    MediaPlayer player;
    Intent intent = new Intent("ACTION_SEND");

    boolean play_pauseConn = true;
    String offline_Online;
    int play_pause_notification=R.drawable.ic_baseline_pause_24;


 public BroadcastReceiver receiver2=new BroadcastReceiver() {
     @Override
     public void onReceive(Context context, Intent intent)
     {
         if (intent.getAction().equals("Send_SongsList")) {
            // position = intent.getIntExtra("positionfire", 0);


             Log.d("HelloARRPARC", intent.getParcelableArrayListExtra("songslIst").size() + "jjdh");
             songListFirebase = intent.getParcelableArrayListExtra("songslIst");
             //foreach loop for getting metadata and downloaded uri from Songs_FireBase songListFirebase list
             // to set in ArrayList<Songs>  songsFireList

             for (Songs_FireBase list : songListFirebase) {
                 list.getStorageMetadataa().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                     @Override
                     public void onSuccess(StorageMetadata storageMetadata) {
                         String songName = storageMetadata.getCustomMetadata("SongName");
                         String bitmapstr = storageMetadata.getCustomMetadata("Bitmap");
                         String dateModifiedd = String.valueOf(storageMetadata.getCreationTimeMillis());
                         String artist = storageMetadata.getCustomMetadata("Artist");
                         long durationn = Long.parseLong(storageMetadata.getCustomMetadata("Duration"));
                         long songSize = Long.parseLong(storageMetadata.getCustomMetadata("Size"));
                         //converting bitmapstr to set in arraylist as a bitmap
                         Bitmap bitmap = convertToBitmap(bitmapstr);

                         list.getDownloadedUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {
                                 songsFireList.add(new Songs(uri, songName, artist, durationn, bitmap, dateModifiedd, songSize));

                             }
                         });
                         //checking elements in songFireList is added or not
                         Log.d("HELEJ", songsFireList.size() + "fjk");
                         //changeMusic(position,songsFireList);
                     }
                 });
             }
         }
     }};


    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //here in this condition we get position of item song
            // of Offline Local songsList and pass position to changeMusic method
            if (intent.getAction().equals("ACTION_POSITION")) {
                int pos = intent.getIntExtra("Position",0);
                offline_Online=intent.getStringExtra("OFFLINE_CONDITION");
                Log.d("offlineePOSITION",offline_Online+pos+"");

               // position = pos;
                changeMusic(pos,songsList);
            }

            //here we get position of Online songslist item position
            // to pass in changeMusic() method
            else if(intent.getAction().equals("FIREPOSITION"))
            {
                Log.d("POSTIONFIRE",intent.getIntExtra("positionfire",0)+"k");
                int pos = intent.getIntExtra("positionfire",0);
                offline_Online=intent.getStringExtra("ONLINE_CONDITION");
                Log.d("onlineePOSITION",offline_Online+pos+"");

                //changeMusic(position,songsList);
                changeMusic(pos,songsFireList);

            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SertviceStart","MUSICSERVICESTART");
        if (player == null) {
            player = new MediaPlayer();
        }
        songsList = getSongArrayList();
        songsFireList = new ArrayList();
        songListFirebase=new ArrayList();

        //Initialize LocalBroadcastManager object here
        broadcastmanager = LocalBroadcastManager.getInstance(getApplicationContext());

        //register broadcastReceiver by checking intentFilter "ACTION_POSITION"
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("ACTION_POSITION"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("ACTION_DESTROY"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver2,new IntentFilter("Send_SongsList"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("FIREPOSITION"));

        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "Music Service");
        // Log.d("KPKPKP", "onCrearte");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals("ACTION_START_FROM_SPLASHSCREEN"))
        {
            //Log.d("SplashStartSERVICE","SERVICE_START_FROM_SPLASH");
        }
        else if (intent.getAction().equals("ACTION_PLAY")) {
            if (player.isPlaying()) {
                player.pause();
                play_pause_notification=R.drawable.play_notification;
            } else if (!player.isPlaying()) {
                player.start();
                play_pause_notification=R.drawable.ic_baseline_pause_24;
            }
        } else if (intent.getAction().equals("ACTION_PREVIUOS"))
        {
            if(offline_Online.equals("ONLINE")) {
                Log.d("HELLOPOSS",position+"");
                changeMusic(--position, songsFireList);
            }
            else  if(offline_Online.equals("OFFLINE")){
                Log.d("HELLOPOSS",position+"");
                changeMusic(--position, songsList);

            }
        } else if (intent.getAction().equals("ACTION_NEXT"))
        {
            if (offline_Online.equals("ONLINE")) {
                Log.d("HELLOPOSS", position + "");
                changeMusic(++position, songsFireList);
            } else if (offline_Online.equals("OFFLINE")) {
                Log.d("HELLOPOSS", position + "");
                changeMusic(++position, songsList);
            }
                //  Log.d("pendingintentposition",position+"_next");
            }

    return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void stop() {
        //  Log.d("OOO","ONSTOP MUSICSERVICE");

    }

    public void reset() {
        player.reset();
    }

    public int getDuration() {
        return player.getDuration();
    }

    public boolean isPlaying() {
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

    public void seekTo(int progress) {
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

    public ArrayList<Songs> getSongArrayList() {
        ArrayList<Songs> arrayList = new ArrayList<>();

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

                        long songSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                        Uri uridata = Uri.withAppendedPath(uri, String.valueOf(id));//getting Uri from _id..
                        //  Log.d("dfdYYYY", uridata.toString());
                        // Log.d("TYTYTYT", artist + duration + name);

                        // Log.d("416line_feature adding",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED))+name);
                        String dateModified = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));

                        Bitmap bitmap = null;
                        try {
                            Size size = new Size(300, 300);
                            //  Size size = new Size(100, 100);
                            bitmap = resolver.loadThumbnail(uridata, size, null);
//GITHUB
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null && artist != null) {
                            arrayList.add(new Songs(uridata, name, artist, duration, bitmap, dateModified, songSize));
                        } else {
                            artist = "No Artist";
                            arrayList.add(new Songs(uridata, name, artist, duration, dateModified, songSize));
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
                                Log.d("Hjei", bitmap.toString());
                            } else {
                                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.music_two_tonne);
                            }
                            //To get metadata(Artist and Duration)
                            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            Long duration = Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                            //getting last modified date of file/song.
                            Long dateModified = fileAL.get(i).lastModified();


                            long songSize = fileAL.get(i).length();
                            // now adding data to Arraylist..
                            if (bitmap != null) {
                                arrayList.add(new Songs(uri, name, artist, duration, bitmap, String.valueOf(dateModified), songSize));
                            } else {
                                arrayList.add(new Songs(uri, name, artist, duration, String.valueOf(dateModified), songSize));
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

    public void changeMusic(int positionn, ArrayList<Songs> songsListttt) {
        // position=positionn;
        // Log.d("LOLO",position+"_+");
   /* if(!songsList.isEmpty())
    {*/
        Log.d("changeMusicPOSITION",positionn+"\n"+songsListttt.size()+songsListttt.get(positionn).getSongName());


                if (positionn <= -1) {
                    //  Log.d("reset_position",positionn+"_");
                    player.reset();
                    position = 0;
                    // player=null;
                } else if (positionn <= songsListttt.size() - 1) {
                    intent.putExtra("receivedPosition", positionn);
                    intent.putExtra("TOTAL_DURATION", player.getDuration());
                    intent.putExtra("CURRENT_DURATION", player.getCurrentPosition());


                    // Log.d("Hello", positionn + "hello");
                    broadcastmanager.sendBroadcast(intent);
                    player.reset();

                    try {
                        // Log.d("SONGUURRII", songsList.get(positionn).getSonguri().toString());
                        if (offline_Online.equals("ONLINE")) {
                            Log.d("ONNLINECHNAGEMUSIC","ONNlineChangemUSIC()");
                            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            player.setDataSource(this,songsListttt.get(positionn).getSonguri());
                           // player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                            player.prepareAsync();
                            // player.start();
                            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    player.start();
                                }
                            });
                            Log.d("helssdslo","play");


                        }
                        else if (offline_Online.equals("OFFLINE"))
                       {
                           Log.d("OFFLINECHNAGEMUSIC","OFFlineChangemUSIC()");
                            player.setDataSource(getApplicationContext(), songsListttt.get(positionn).getSonguri());
                         player.prepare();
                         player.start();
                         Log.d("hello","play");
                       }
                        showNotification(positionn, play_pause_notification,songsListttt);


                        getCurrentPosiotnnn();
                    } catch (Exception e) {
                    }


                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.d("CCOOMPL", "COMPJGKFG");
                            changeMusic(++position, songsListttt);

                        }
                    });
                } else if (positionn > songsListttt.size() - 1) {
                    position = 0;
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
                            broadcastmanager.sendBroadcast(intent);
                            //  Log.d("HEJO", player.getCurrentPosition() + "__position");
                            Thread.sleep(1000);

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return player.getCurrentPosition();

    }


    public void showNotification(int possition, int play_pauseICON, ArrayList<Songs> songsList)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                            .putString(MediaMetadata.METADATA_KEY_TITLE, songsList.get(possition).getSongName())
                            .putString(MediaMetadata.METADATA_KEY_ARTIST, songsList.get(possition).getArtist())
                            .putBitmap(MediaMetadata.METADATA_KEY_ART, songsList.get(possition).getBitmap())
                            .build());


            NotificationChannel channel = new NotificationChannel("channelid", "foregroundservice", NotificationManager.IMPORTANCE_HIGH);//after debugging set IMPORATNCE_DEFAULT HERE
            channel.setSound(null, null);
            //  channel.setDescription("GHello");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            // }
            //  Log.d("MEDIAAA", mediaSessionCompat.getSessionToken().toString() + mediaSessionCompat.isActive() + "2");
        }
        PendingIntent pendingIntentPrevious;
        int drw_previous;
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
                .setPriority(NotificationCompat.PRIORITY_MAX); //after debugging set PRIORITY_DEFAULT HERE


        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(100, builder.build());


        startForeground(101, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  Log.d("DDEESSTROY","DESTROY");
       /* Intent inntent=new Intent(this,MusicService.class);
        inntent.setAction("ACTION_STOP");
        startService(inntent);*/
        if (player != null) {
            player.reset();
            player.release();
        }
        // changeMusic(-1);
        stopForeground(true);
        stopSelf();

    }

    public Bitmap convertToBitmap(String bitmapstr)
    {
        Bitmap bitmap1=null;
        try {
            // Log.d("bitMAPSTR",bitmapstr);
            byte[] byteArray = Base64.decode(bitmapstr, Base64.DEFAULT);
            bitmap1= BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //Log.d("BITMMAP",bitmap1.toString());
        }catch (Exception e){}
//    Log.d("HHELO",bitmap1.toString());
        return bitmap1;
    }

}
