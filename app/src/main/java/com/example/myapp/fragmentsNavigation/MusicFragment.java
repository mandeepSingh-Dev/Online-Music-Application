package com.example.myapp.fragmentsNavigation;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.ExecutorSingleton;
import com.example.myapp.Handler_Looper_Thread.MyThread;
import com.example.myapp.MusicRecylerView.MyAdapter;
import com.example.myapp.MusicRecylerView.Songs;
import com.example.myapp.MusicService;
import com.example.myapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment implements MediaPlayer.OnCompletionListener {
    public static ArrayList<Songs> songsArrayList;
    public static ArrayList<Songs> songsArrayList2;
    MediaPlayer mediaPlayer;

    MyThread myThread;
    SeekBar seekBar;
    ProgressBar progressBar;

    int CurrentPosition=0;
    View MmotionLayout;

    TextView motiontextView;
    ImageView motionImagevIew;
    TextView motionCurrentDuration;
    TextView motionTotalDuration;
    CardView motionCardView;
    ImageView motionCollapseImageview;

    CircleImageView playPauseButton;
    ImageView prev_Button;
    ImageView next_Button;

    //variable for timeDuration method.
    int minutes = 0;
    int secondposition = 19;
    int value = 0;

    AlphaAnimation buttonanimation;
    Intent intent;

    MusicService musicService;

    Boolean completionCON;

    public LocalBroadcastManager broadcastManager;

   public static int mposition = 0;  //for use it globally and for changeMusic method call repeatdely after song comletion/

    ServiceConnection sConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder)
        {
            Log.d("KPKPKP","onServiceConnected 101");
            MusicService.MyBinder myBinder=(MusicService.MyBinder)binder;
            musicService=myBinder.getService();


            //setting total duration to motion total duration textview
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("KPKPKP","onServiceDisconnected 110");
           // musicService=null;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("KPKPKP","onCreateView 119");
         songsArrayList = new ArrayList<>();
        songsArrayList2 = new ArrayList<>();


        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    //BROADCAST RECEIVER FOR GETTING DATA FROM SERVICE IN THIS FRAGMENT
    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("KPKPKP","onReceive 127");
            if(intent.getAction().equals("ACTION_HULLI"))
            {
                Log.d("KPKPKP","IF STAEMENT OF ONRECEIVE 130");
                int duratiiion=intent.getIntExtra("DURATION",10000);
                Log.d("YTYTYTY",duratiiion+"Hello");
                seekBar.setMax(duratiiion);
                String timelabel=createTimeLabel(duratiiion);
                motionTotalDuration.setText(timelabel);

                CurrentPosition= intent.getIntExtra("CURRENTDURATION",1000);
                Log.d("YYYYY",CurrentPosition+"fgfgf");
                motionCurrentDuration.setText(createTimeLabel(CurrentPosition));
                seekBar.setProgress(CurrentPosition);

                completionCON= intent.getBooleanExtra("BooleanCOMPLETION",false);
                  Log.d("VVBBBVV",completionCON.toString());
                   if(completionCON)
                   {
                       Log.d("KPKPKP","IF STAEMENT FOR GETTING BOOLEAN COMPELTION OF SONG 146");
                       playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                       int bPosition=intent.getIntExtra("POSITIONN",1);
                      Log.d("POPOPO",bPosition+"ggh");
                       changeMusic(bPosition);



                   }


            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intenttt=new Intent(getActivity(), MusicService.class);
     intenttt.setAction("ACTION_START_FROM_MUSICFRAGMENT");
        getActivity().startService(intenttt);

        //getting arraylist from service but after starting the service
        songsArrayList2=MusicService.songsList;
        Log.d("LLIISSTT",songsArrayList2.size()+"__");

        Log.d("YAYA","ONVIEWCREATE");

        Log.d("KPKPKP","onViewCreated 164");
        //set defaultbitmap because of no bitmap available in song album image..

        //register LocalBroadcastManager to receive data from MusicService.
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,new IntentFilter("ACTION_HULLI"));

        //Initialize broadcastManager to send data to MusicService.
       broadcastManager=LocalBroadcastManager.getInstance(getContext());


          buttonanimation = new AlphaAnimation(1F, 0F);

        RecyclerView recyclerView = view.findViewById(R.id.rexcylerviewMusic);
        progressBar = view.findViewById(R.id.progressbar);

         // songsArrayList = new ArrayList<>();
        myThread = new MyThread();
        myThread.start();

        seekBar = getActivity().findViewById(R.id.seekbar);

        //getting motion layout from <include/> navigationActivity.
        MotionLayout motionLayoutt = getActivity().findViewById(R.id.inccluddeMotion);
        motionCardView =motionLayoutt.findViewById(R.id.cardview);
        motiontextView = motionLayoutt.findViewById(R.id.song_name);
        motionImagevIew = motionLayoutt.findViewById(R.id.album_art_image_view);
        playPauseButton = motionLayoutt.findViewById(R.id.play_pause_button);
        motionCurrentDuration = motionLayoutt.findViewById(R.id.currentDurationText);
        motionTotalDuration = motionLayoutt.findViewById(R.id.totalDurationText);
        prev_Button = motionLayoutt.findViewById(R.id.prev_image_view);
        next_Button = motionLayoutt.findViewById(R.id.next_image_view);
        motionLayoutt.findViewById(R.id.collapse_image_view);

        //To set duration of song to motion layout textviews


        //To control song on play pause button
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseButton();
            }
        });

        //To control prev and next button
        prev_Next_Button();

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        //if android version is above or equal to Q.. then using MEDIASTORE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ExecutorSingleton.getInstance().execute(new Runnable() {
                @Override
                public void run() {


                    /*ContentResolver resolver = getContext().getContentResolver();
                    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                    Cursor cursor = resolver.query(uri, null, null, null);

                    while (cursor.moveToNext()) {

                        Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                        Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                        Uri uridata = Uri.withAppendedPath(uri, String.valueOf(id));//getting Uri from _id..
                        Log.d("dfdf", uridata.toString());
                        Log.d("dfdf", artist + duration + name);

                        Bitmap bitmap = null;
                        try {
                            Size size = new Size(300, 300);
                            //  Size size = new Size(100, 100);

                            bitmap = resolver.loadThumbnail(uridata, size, null);  //getting Bitmap

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null && artist != null) {
                            songsArrayList.add(new Songs(uridata, name, artist, duration, bitmap));
                        } else {
                            artist = "No Artist";
                            songsArrayList.add(new Songs(uridata, name, artist, duration));
                        }

                    }*/ //while loop closed


                    MyAdapter myAdapter = new MyAdapter(getContext(), songsArrayList2);
try {
    getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(myAdapter);

        }
    });
}catch(Exception e){
    Log.d("djkh",e.getMessage());
}


                    //user defined setOnItemClickListener Method and Interface in MyAdapter.
                    myAdapter.setOnItemClickListener(new MyAdapter.CustomItemClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            Log.d("KPKPKP","onItemClick 275");
                            motionLayoutt.setVisibility(View.VISIBLE);
                            mposition = position;
                            //SENDING POSITION TO MUSIC SERVICE THROUGH BROADCAST MANAGER.
                            Intent i=new Intent("ACTION_POSITION");
                            i.putExtra("Position",position);
                            broadcastManager.sendBroadcast(i);

                            changeMusic(position);

                        }
                    });
                }
            });
        }  //if statemnet closed of android version above or equal to Q

        //if statement for android version below Q
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {


           //File file = Environment.getExternalStorageDirectory();

            // File file = Environment.getRootDirectory();
            ExecutorSingleton.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });

                   /* ArrayList<File> fileAL = getDirectories(file);  //getting files in Thread handler...

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
                                songsArrayList.add(new Songs(uri, name, artist, duration, bitmap));
                            } else {
                                songsArrayList.add(new Songs(uri, name, artist, duration));
                            }
                        } catch (Exception e) {
                        }
                    }  //for loop closed
*/

                    MyAdapter myAdapter = new MyAdapter(getContext(), songsArrayList2);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                            recyclerView.setAdapter(myAdapter);
                        }
                    });
                    myAdapter.setOnItemClickListener(new MyAdapter.CustomItemClickListener() {
                        @Override
                        public void onItemClick(int position, View imageview) {
                            motionLayoutt.setVisibility(View.VISIBLE);
                            mposition = position;
                            Intent i=new Intent("ACTION_POSITION");
                            i.putExtra("Position",position);
                            broadcastManager.sendBroadcast(i);

                            changeMusic(position);
                        }
                    });
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            });


        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
           seekBar.setBackground(null);
            }
        });

     /*   motionImagevIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motionLayoutt.transitionToEnd();
            }
        });*/

    }    //onViewCreated closed


    // get all files..
    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<File> getDirectories(File file) {


        ArrayList<File> arrayList;
        Log.d("dddddddddd", Thread.currentThread().toString());


        File[] filesarr = file.listFiles();
        arrayList = new ArrayList<>();
        for (File singlefile : filesarr) {

            if (singlefile.isDirectory() && !singlefile.isHidden()) {
                arrayList.add(singlefile);
                ArrayList<File> filesss = getDirectories(singlefile);
                arrayList.addAll(filesss);
            } else {
                if (singlefile.getName().endsWith(".mp3")) {

                    arrayList.add(singlefile.getAbsoluteFile());
                    Log.d("sfdfdf", singlefile.getName());

                    // CtentResolver resolver=getContext().getContentResolver();
                    // Size size=new Size(100,100);
                    //  Bitmap bitmap=  ThumbnailUtils.createAudioThumbnail(singlefile.getAbsoluteFile(),size,null);
                    // BitmapFactory.decodeFile()
                }
            }
        }
        return arrayList;

    }


    public void playMusic(Songs song) {

        if (mediaPlayer == null) {  //it calls only first time at the time of creating mediaplayer object
            //and  we click another item then this will not call ,mediaplayer  reset() means clear everything and play current song by setSourceData().
            //determine mediaplayer is empty or not

            mediaPlayer = new MediaPlayer();
        }
        try {
            Log.d("KPKPKP","playMusic 438");
            //When we call mediaPlayer.reset() mediaPlayer goes to IDLE state
            musicService.reset(); //now mediaplayer in uninitialized  state  Means mediaplayer ke andar ka sara source mal pani nikal jaeyga mtlab reset ho jaeyga  lekin mediaplayer object delete nhi hoga bs.
            //and every tym we click item this method
            // reset the mediaplayer .you don’t need to do this operation for the first time, but I choose to use it for reusability
            //sending message to naviagtionactivity through Handler.

Log.d("pHLE","PPHHLLEE");
            String songName = songsArrayList.get(mposition).getSongName();
           /* Bitmap bitmap = songsArrayList.get(mposition).getBitmap();

            //setting songname and album imageview in motion layout views
          // motiontextView.setText(songName);
            if (bitmap != null) {
                motionImagevIew.setImageBitmap(bitmap);
            } else {
                motionImagevIew.setImageDrawable(getResources().getDrawable(R.drawable.musictwo_tone));
            }*/

            Long duration = songsArrayList.get(mposition).getDuration();

           /* NavigationMainActivity.MyHandler myHandler = new NavigationMainActivity.MyHandler();
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("songname", songName);
            bundle.putLong("duration", duration);*/

            // bundle.putInt("currentposition",mediaPlayer.getCurrentPosition());

            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
            //  mediaPlayer.setDataSource(getContext(), song.getSonguri());
            //  mediaPlayer.prepare();

            // mediaPlayer.start();

            //  songDurationOnMotionLayout();
          /* String totalTime=createTimeLabel(musicService.getDuration());
           motionTotalDuration.setText(totalTime);*/
            // timeDuration();

           // seekBar.setMax(musicService.getDuration());

            //sending currentposition to Handler after every 1 second
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    while (musicService != null) {
                        try {
//                        Log.i("Thread ", "Thread Called");
                            // create new message to send to handler
                            if (musicService.isPlaying()) {
                                Message msg = new Message();
                                // msg.what = musicService.getCurrentPosition();
                                msg.what = CurrentPosition;
                                //handler.sendMessage(msg);
                                Thread.sleep(1000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();*/
           /*     //  Handler mHandler = new Handler();

          //  message.setData(bundle);
          //  myHandler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**musicService.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                changeMusic(++mposition);
            }
        });*/

                  } catch (Exception e) {
        }
    }//playMusic() method finished here

    //Handler to get currentPosition of song to set in timer textview and SeekBar

    public void changeMusic(int position) {
        Log.d("KPKPKP","changeMusic 525");


        try {
            if (position <= songsArrayList.size() - 1) {
                Songs songs = songsArrayList.get(position);
                intent.putExtra("SONG_URI", songs.getSonguri().toString());
                intent.putExtra("POSITION",position);

                Log.d("JPJP",songs.getSongName());
                Log.d("JPJP",position+"DDF");
              //setting songName in motion song name textview
                String songName = songs.getSongName();
                motiontextView.setText(songName);

                 //setting bitmap to album image of motion layout
                Bitmap bitmap = songs.getBitmap();
                if (bitmap != null) {
                    motionImagevIew.setImageBitmap(bitmap);
                } else {
                    motionImagevIew.setImageDrawable(getResources().getDrawable(R.drawable.musictwo_tone));
                }

                Log.d("KPKPKP","changeMusic 2ND LOG MSG 545");
                //starting service evrytym when song change.
             //  getActivity().stopService(intent);
                Log.d("KPKPKP","changeMusic 3rD LOG MSG 548");

                //getActivity().startService(intent);
                Log.d("KPKPKP","changeMusic 4th LOG MSG 551");

                playMusic(songs);

                //instead of creating playMusic we can recursivly used the changeMusic method with the playMusic code
            }
        }catch (Exception e){}

    }



    //motion playPause button
    public void playPauseButton() {
        if (musicService != null) {
            if (musicService.isPlaying()) {
                musicService.pause();
                playPauseButton.startAnimation(buttonanimation);
                playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);

            }
           
            else {
                musicService.start();
                playPauseButton.startAnimation(buttonanimation);
                playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
            }
        } else {
            Toast.makeText(getContext(), "Music is not playing", Toast.LENGTH_SHORT).show();
        }
    }// playPauseButton() closed



    //create timeduration format for total duration and currentduartion also
    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) {timeLabel += "0";}

        timeLabel += sec;
        return timeLabel;
    }

    void prev_Next_Button()
    {
        next_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("sssiize",songsArrayList.size()+"");
                next_Button.startAnimation(buttonanimation);
                //this if() block when se select last
                // song from listview then after click
                // next the first song will play
                if(mposition==songsArrayList.size()-1)
                {
                    mposition=-1;

                    changeMusic(mposition);
                }

                //this if block for continue clicking
                // the next button to next the song
                if(mposition>=-1 ) {
                    musicService.reset();
                    playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
                    changeMusic(++mposition);
                    //this nested if() block play first song when
                    // we click next when last song on display
                    if(mposition==songsArrayList.size()-1)
                    {
                        mposition=-1;
                    }

                }


            }
        });
        prev_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev_Button.startAnimation(buttonanimation);
                if(mposition>=1) {
                    musicService.reset();
                    playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
                    changeMusic(--mposition);
                }
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("KPKPKP","onCompletion 642LINE METHOD 642");
        playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        changeMusic(++mposition);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("KPKPKP","onStart 642LINE METHOD 650");

    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent1=new Intent(getContext(),MusicService.class);
        getActivity().bindService(intent1,sConnection, Context.BIND_AUTO_CREATE);

 /*if(!songsArrayList.isEmpty())
 {
     getActivity().startService(intent);
     Intent intent1=new Intent(getContext(),MusicService.class);
     getActivity().bindService(intent1,sConnection, Context.BIND_AUTO_CREATE);

 }
 else{
     Log.d("fff","not start service");
 }*/
    }


    /* @Override
    public void onStop() {
        super.onStop();
        if (musicService != null) {
            musicService.stop();
        }
    }*/

    public void sendPosition(int position)
    {
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("DETRO","StopmUSICFRAGMENT");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DETRO","DESTROYmUSICFRAGMENT");
      /*  Intent inntent=new Intent(getContext(),MusicService.class);
        inntent.setAction("ACTION_STOP");
      getActivity().startService(inntent);
*/
    }
}