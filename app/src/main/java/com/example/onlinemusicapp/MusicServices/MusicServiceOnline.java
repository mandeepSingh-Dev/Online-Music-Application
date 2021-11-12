package com.example.onlinemusicapp.MusicServices;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.onlinemusicapp.MusicRecylerView.Songs;
import com.example.onlinemusicapp.MusicRecylerView.Songs_FireBase;
import com.example.onlinemusicapp.MyViewModel.MyViewModel;
import com.example.onlinemusicapp.MyViewModel.MyViewModelFactory;
import com.example.onlinemusicapp.R;
import com.example.onlinemusicapp.fragmentsNavigation.SongsFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageMetadata;

import java.util.ArrayList;


public class MusicServiceOnline extends Service {
    private final MyBinder2 mBinder = new MyBinder2();

    public static ArrayList<Songs_FireBase> songListFirebase;
    public ArrayList<Songs> songsFireList;
    public ArrayList<Songs> songslistextra;

    MediaSessionCompat mediaSessionCompat;


    private LocalBroadcastManager broadcastmanager;
    int position = 0;
    MediaPlayer player;
    Intent intent = new Intent("ACTION_SEND_ONLINE");
    int y;

    String offline_Online;
    int play_pause_notification= R.drawable.ic_baseline_pause_24;

    //this receiver is to get firebase songlist from SongsFragment
    public BroadcastReceiver receiver2=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals("Send_SongsList")) {
                // position = intent.getIntExtra("positionfire", 0);
                Log.d("HelloARRPARC", intent.getParcelableArrayListExtra("songslIst").size() + "jjdh");
                //songListFirebase = intent.getParcelableArrayListExtra("songslIst");
                songslistextra = intent.getParcelableArrayListExtra("songslIst");

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

    // In this receiver we r getting position of song by clicking on song in SongsFragment
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //here we get position of Online songslist item position
            // to pass in changeMusic() method
            if(intent.getAction().equals("FIREPOSITION"))
            {
                Log.d("POSTIONFIRE",intent.getIntExtra("positionfire",0)+"k");
                int pos = intent.getIntExtra("positionfire",0);
                // offline_Online=intent.getStringExtra("ONLINE_CONDITION");

                position = pos;
                changeMusic(pos/*,songsFireList*/);

            }
            else if(intent.getAction().equals("STOP KAR_ONLINE"))
            {
                Log.d("HELLDDSTOP","STOP");
                if(player!=null)
                {
                    if(player.isPlaying())
                    {
                        player.pause();
                        // player.release();

                    }
                }
            }
            else if(intent.getAction().equals("SEND_URI"))
            {
                Log.d("uriii",intent.getStringExtra("uriString"));
                String uriString=intent.getStringExtra("uriString");
                try {
                    if(player.isPlaying())
                    {
                        player.reset();
                    }
                    player.setDataSource(uriString);
                    player.prepare();
                    player.start();
                }catch (Exception e){}
                }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SertviceStartONLINE","MUSICSERVICESTART");
        if (player == null) {
            player = new MediaPlayer();
        }
        //songsList = getSongArrayList();
        songsFireList = new ArrayList();
        songListFirebase=new ArrayList();
        songslistextra=new ArrayList<Songs>();

        //Initialize LocalBroadcastManager object here
        broadcastmanager = LocalBroadcastManager.getInstance(getApplicationContext());

        //register broadcastReceiver by checking intentFilter "Send_SongsList" & "FIREPOSITION"
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver2,new IntentFilter("Send_SongsList"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("FIREPOSITION"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("STOP KAR_ONLINE"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("SEND_URI"));


        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "Music Service");

        // Log.d("KPKPKP", "onCrearte");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals("START_FROM_SONGSFRAGMENT"))
        {
            //Log.d("SplashStartSERVICE","SERVICE_START_FROM_SPLASH");
        }
        else if (intent.getAction().equals("ACTION_PLAYY")) {
            if (player.isPlaying()) {
                player.pause();
                play_pause_notification=R.drawable.play_notification;
            } else if (!player.isPlaying()) {
                player.start();
                play_pause_notification=R.drawable.ic_baseline_pause_24;
            }
        } else if (intent.getAction().equals("ACTION_PREVIUOS"))
        {
            changeMusic(--position/*, songsFireList*/);

        } else if (intent.getAction().equals("ACTION_NEXT"))
        {
            changeMusic(++position/*, songsFireList*/);
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void stop() {
        player.stop();

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
    public class MyBinder2 extends Binder {
        public MusicServiceOnline getService() {
            return MusicServiceOnline.this;
        }
    }


    public void changeMusic(int positionn/*, ArrayList<Songs> songsListttt*/) {
         position=positionn;
         Log.d("LOLO",position+"_+");
   /* if(!songsList.isEmpty())
    {*/
//        Log.d("changeMusicPOSITION",positionn+"\n"+songsListttt.size()+songsListttt.get(positionn).getSongName());

        if (positionn <= -1) {
            //  Log.d("reset_position",positionn+"_");
            player.reset();
            position = 0;
            // player=null;
        } else if (positionn <= songslistextra.size() - 1) {


            /**now basically below three lines again useless hahaha*/
            intent.putExtra("receivedPosition_ONLINE", positionn);
            intent.putExtra("TOTAL_DURATION_ONLINE", player.getDuration());
            intent.putExtra("CURRENT_DURATION_ONLINE", player.getCurrentPosition());


            // Log.d("Hello", positionn + "hello");
            broadcastmanager.sendBroadcast(intent);
            player.reset();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);


            try {
                // Log.d("SONGUURRII", songsList.get(positionn).getSonguri().toString());
                Log.d("ONNLINECHNAGEMUSIC","ONNlineChangemUSIC()");
                Log.d("nameee",/*songsFireList*/songslistextra.get(position).getSongName()+"__POSTION="+positionn);
                player.setDataSource(getApplicationContext(), /*songsFireList*/songslistextra.get(positionn).getSonguri());
                player.prepare();
                player.start();
              /*  player.setDataSource(this,songsFireList.get(positionn).getSonguri());
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        player.start();
                    }
                });*/
                Log.d("helssdslo","play");

                showNotification(position, play_pause_notification,songslistextra);


                getCurrentPosiotnnn();
            } catch (Exception e) {
            }
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("CCOOMPL", "COMPJGKFG");
                    changeMusic(++position);

                }
            });
        } else if (positionn > /*songsFireList*/songslistextra.size() - 1) {
            position = 0;
            changeMusic(position);
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
                            intent.putExtra("CURRENTDURATION_ONLINE", player.getCurrentPosition());
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
                    .putLong(MediaMetadata.METADATA_KEY_DURATION,songsList.get(possition).getDuration())
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
        Intent intentPrevious = new Intent(this, MusicServiceOnline.class)
                .setAction("ACTION_PREVIUOS");
        pendingIntentPrevious = PendingIntent.getService(this, 0,
                intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPlay = new Intent(this, MusicServiceOnline.class)
                .setAction("ACTION_PLAYY");
        PendingIntent pendingIntentPlay = PendingIntent.getService(this, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentNext = new Intent(this, MusicServiceOnline.class)
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
