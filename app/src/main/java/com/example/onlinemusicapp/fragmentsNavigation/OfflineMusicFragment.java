package com.example.onlinemusicapp.fragmentsNavigation;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
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
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinemusicapp.ExecutorSingleton;
import com.example.onlinemusicapp.Handler_Looper_Thread.MyThread;
import com.example.onlinemusicapp.MusicRecylerView.MyAdapter;
import com.example.onlinemusicapp.MusicRecylerView.MyAdapter2;
import com.example.onlinemusicapp.MusicRecylerView.Songs;
import com.example.onlinemusicapp.MusicServices.MusicService;
import com.example.onlinemusicapp.R;
import com.example.onlinemusicapp.databinding.FragmentSongsBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class OfflineMusicFragment extends Fragment implements MediaPlayer.OnCompletionListener {
    public static ArrayList<Songs> songsArrayList;
    public static ArrayList<Songs> songsArrayList2;


    MediaPlayer mediaPlayer;

    MyThread myThread;
    ProgressBar progressBar;

    int CurrentPosition = 0;

    //Motion Layout Variables
    MotionLayout motionLayoutt;
    TextView motionSongName;
    ImageView motionImagevIew;
    TextView motionCurrentDuration;
    TextView motionTotalDuration;
    TextView motionartistName;
    CardView motionCardView;
    View lastSpace;
    ImageButton dotsButton;
    CircleImageView playPauseButton;
    ImageView prev_Button;
    ImageView next_Button;
    TextView positionTextview;
    SeekBar seekBar;
    BottomNavigationView bottomNavigationView;
    RecyclerView motionRecyclerView;

    //bottom sheet dialog views..
    BottomSheetDialog bottomSheetDialog;
    View bottomsheetLayout;
    TextView artistSheetText;
    TextView albumSheetText;
    TextView durationSheetText;
    TextView songSizeSheet;
    TextView shareSong;
    View bottomSHEET;

    com.example.onlinemusicapp.databinding.FragmentSongsBinding binding;


    AlphaAnimation buttonanimation;
    Intent intent;

    MusicService musicService;

    Boolean completionCON;

    String receivedsongName;

    public LocalBroadcastManager broadcastManager;

    BroadcastReceiver receiver;
    Bitmap recievedBitmap = null;

    // RecyclerView recyclerView;

    Intent i = new Intent("ACTION_POSITION");
    Window window;
    CollapsingToolbarLayout toolbarLayout;

    public static int mposition = 0;  //for use it globally and for changeMusic method call repeatdely after song comletion/

    ServiceConnection sConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d("KPKPKP", "onServiceConnected 101");
            MusicService.MyBinder myBinder = (MusicService.MyBinder) binder;
            musicService = myBinder.getService();

            Intent serviceIntent = new Intent("musicservice_instance");
            serviceIntent.putExtra("musicService", new MusicService());
            broadcastManager.sendBroadcast(serviceIntent);


            //setting total duration to motion total duration textview
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("KPKPKP", "onServiceDisconnected 110");
            // musicService=null;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("KPKPKP", "onCreateView 119");
        songsArrayList = new ArrayList<>();
        songsArrayList2 = new ArrayList<>();


        // View view = inflater.inflate(R.layout.fragment_offline_music, container, false);
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        binding = FragmentSongsBinding.inflate(inflater, container, false);

        // recyclerView = view.findViewById(R.id.songsRecyclerView);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TO START MusicService check this start Sevice code later
        //bcoz this code may be useless because MusicService already started from SplashSecreen
        Log.d("offFragment", "OFFLINEFRAGMENT");
        Intent intenttt = new Intent(getActivity(), MusicService.class);
        intenttt.setAction("ACTION_START_FROM_MUSICFRAGMENT");
        getActivity().startService(intenttt);

        //getting arraylist from service but after starting the service
        songsArrayList2 = MusicService.songsList;


        //this block is for Fullscreen,color the status bar.
        if (Build.VERSION.SDK_INT >= 21) {
            window = this.getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.Green));
        }

        //setting Bitmap from songArrayList2 to 4 images on Toolbar
        settingBitmapOnToolbarImages();

        //initialize  motionLayout views from <include/> navigationActivity.
        motionlayoutViews();

        //getting\Initialize bottomSheetLayout views
        bottomSheetViews();


        //On 3 dots button clicked then bottom sheet dialog will open
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.Theme_Design_BottomSheetDialog);
        bottomSheetDialog.setContentView(bottomSHEET);
        dotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.show();

            }
        });
        //In this Receiver we r getting song position, currentduration position aftr evry 1 second
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("ACTION_SEND")) {
                    try {
                        Log.d("PPOOK", String.valueOf(intent.getIntExtra("receivedPosition", 1)));
                        int receivedPosition = intent.getIntExtra("receivedPosition", 1);

                        //To send Song Uri to anothher app..
                        shareSongUri(receivedPosition);

                        int receivedCurrentPosition = intent.getIntExtra("CURRENTDURATION", 1000);
                        positionTextview.setText((receivedPosition + 1) + "/" + songsArrayList2.size());
                        //setting current position to textview and seekbar
                        seekBar.setProgress(receivedCurrentPosition);

                        String currentTimeLabel = createTimeLabel(receivedCurrentPosition);
                        motionCurrentDuration.setText(currentTimeLabel);

                        //SETTING Total duration to seekbar and TextView
                        seekBar.setMax(musicService.getDuration());
                        motionTotalDuration.setText(createTimeLabel(musicService.getDuration()));
                        // Log.d("DDUURATION", musicService.getDuration() + "__dud");

                        motionSongName.setText(songsArrayList2.get(receivedPosition).getSongName());
                        motionartistName.setText(songsArrayList2.get(receivedPosition).getArtist());

                        artistSheetText.setText(songsArrayList2.get(receivedPosition).getArtist());
                        albumSheetText.setText(songsArrayList2.get(receivedPosition).getSongName());
                        durationSheetText.setText(createTimeLabel(musicService.getDuration()));
                        long songSize = songsArrayList2.get(receivedPosition).getSongSize();
                        Log.d("soongssize", String.valueOf(songSize));
                        songSizeSheet.setText(byteToMB(songSize) + "mb");

                        recievedBitmap = songsArrayList2.get(receivedPosition).getBitmap();

                        if (recievedBitmap != null) {
                            // Glide.with(getContext()).asBitmap().load(recievedBitmap).into(motionImagevIew);
                            motionImagevIew.setImageBitmap(recievedBitmap);
                            Log.d("revicerdBitmap", recievedBitmap.toString());
                            //setting pallette color to motionlayout
                            try {
                                Palette.from(recievedBitmap).generate(new Palette.PaletteAsyncListener() {
                                    public void onGenerated(Palette p) {
                                        try {
                                            setPaletteColor(p);
                                            motionCardView.setBackgroundColor(p.getMutedColor(getActivity().getResources().getColor(R.color.paletteDEFAULT)));
                                            //  lastSpace.setBackgroundColor(p.getMutedColor(getActivity().getResources().getColor(R.color.paletteDEFAULT)));
                                        } catch (Exception e) {
                                            //Toast.makeText(context,e.getCause().toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                            }
                        } else {
                            motionImagevIew.setImageDrawable(getResources().getDrawable(R.drawable.music_two_tonne));
                        }
                    } catch (Exception e) {
                    }
                }
            }
        };
        //register LocalBroadcastManager to receive data from MusicService.
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("ACTION_SEND"));

        //Initialize broadcastManager to send data to MusicService.
        broadcastManager = LocalBroadcastManager.getInstance(getContext());

        buttonanimation = new AlphaAnimation(1F, 0.5F);

        //recyclerView = view.findViewById(R.id.rexcylerviewMusic);
        //  progressBar = view.findViewById(R.id.progressbar);

        // songsArrayList = new ArrayList<>();
        myThread = new MyThread();
        myThread.start();


        //To set duration of song to motion layout textview

        //To control song on play pause button
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseButton();
            }
        });

        //To control prev and next button
        prev_Next_Button();
        //when music stoped from notification then change this play button to pause

        //  sortingArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ExecutorSingleton.getInstance().execute(new Runnable() {
                @Override
                public void run() {


                  /*  ContentResolver resolver = getContext().getContentResolver();
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

                   /* try {
                        if (!songsArrayList2.isEmpty()) {
                            songsArrayList2.sort(new Comparator<Songs>() {
                                @Override
                                public int compare(Songs lhs, Songs rhs) {
                                    String lhsName = lhs.getSongName();
                                    String rhsName = rhs.getSongName();
                                    byte[] lhsByteArray = lhsName.getBytes();
                                    byte[] rhsByteArray = rhsName.getBytes();

                                    Log.d("compsuyrtddys", lhsName.compareTo(rhsName) + "djfkd");
                                    return rhs.getDuration().compareTo(lhs.getDuration());

                                }
                            });
                        }
                    } catch (Exception e) {}*/

                    MyAdapter2 myAdapter = new MyAdapter2(getContext(), songsArrayList2);
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // ImageView imageOnToolbar = view.findViewById(R.id.imageOnToolBar);
                                binding.songsRecyclerView.setVisibility(View.VISIBLE);
                                //   progressBar.setVisibility(View.GONE);
                                binding.lottieSongsFragment.setVisibility(View.GONE);

                                binding.songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                binding.songsRecyclerView.setAdapter(myAdapter);

                                motionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                motionRecyclerView.setAdapter(myAdapter);
                                //To give different view look to recyleview list
                              /*  imageOnToolbar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));

                                    }
                                });*/
                            }
                        });

                    } catch (Exception e) {
                        Log.d("djkh", e.getMessage());
                    }
                    //user defined setOnItemClickListener Method and Interface in MyAdapter.
                    myAdapter.setOnClickListener(new MyAdapter2.CustomItemClickListener2() {
                        @Override
                        public void customOnItemClick(int position) {
                            Log.d("KPKPKP", "onItemClick 275");

                            mposition = position;
                            Log.d("clickOffFragmnetSong", "clickedOfflineSong");
                            //SENDING POSITION TO MUSIC SERVICE THROUGH BROADCAST MANAGER.
                            // Intent i2=new Intent("ACTION_POSITION");
                            i.putExtra("Position", position);
                            i.putExtra("OFFLINE_CONDITION", "OFFLINE");
                            broadcastManager.sendBroadcast(i);
                            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);

                            Intent intentttt = new Intent("STOP KAR_ONLINE");
                            broadcastManager.sendBroadcast(intentttt);
                            //  changeMusic(position);

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
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                   /* songsArrayList2.sort(new Comparator<Songs>() {
                        @Override
                        public int compare(Songs lhs, Songs rhs) {
                            String lhsName = lhs.getSongName();
                            String rhsName = rhs.getSongName();
                            byte[] lhsByteArray = lhsName.getBytes();
                            byte[] rhsByteArray = rhsName.getBytes();

                            Log.d("compsuyrtddys", lhsName.compareTo(rhsName) + "djfkd");
                            return rhs.getDuration().compareTo(lhs.getDuration());

                        }
                    });*/
                    MyAdapter myAdapter = new MyAdapter(getContext(), songsArrayList2);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.songsRecyclerView.setVisibility(View.VISIBLE);
                            binding.lottieSongsFragment.setVisibility(View.GONE);

                            //    progressBar.setVisibility(View.GONE);
                            binding.songsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                            binding.songsRecyclerView.setAdapter(myAdapter);

                            motionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            motionRecyclerView.setAdapter(myAdapter);
                        }
                    });
                    myAdapter.setOnItemClickListener(new MyAdapter.CustomItemClickListener() {
                        @Override
                        public void onItemClick(int position, View imageview) {
                            Log.d("clickedPosition", position + "position clicked");
                            motionLayoutt.setVisibility(View.VISIBLE);
                            mposition = position;
                            // Intent i=new Intent("ACTION_POSITION");  //setting this globally..

                            i.putExtra("Position", position);
                            i.putExtra("OFFLINE_CONDITION", "OFFLINE");

                            Intent intentttt = new Intent("STOP KAR_ONLINE");
                            broadcastManager.sendBroadcast(intentttt);

                            broadcastManager.sendBroadcast(i);
                            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);


                            // changeMusic(position);
                        }
                    });
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //   progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }

        //binding

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
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

      /*  lastSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Lastspace", "lastSpace view clicked");
            }
        });*/
    }    //onViewCreated closed


    public void playMusic(Songs song) {

        if (mediaPlayer == null) {  //it calls only first time at the time of creating mediaplayer object
            //and  we click another item then this will not call ,mediaplayer  reset() means clear everything and play current song by setSourceData().
            //determine mediaplayer is empty or not

            mediaPlayer = new MediaPlayer();
        }
        try {
            Log.d("KPKPKP", "playMusic 438");
            //When we call mediaPlayer.reset() mediaPlayer goes to IDLE state
            musicService.reset(); //now mediaplayer in uninitialized  state  Means mediaplayer ke andar ka sara source mal pani nikal jaeyga mtlab reset ho jaeyga  lekin mediaplayer object delete nhi hoga bs.
            //and every tym we click item this method
            // reset the mediaplayer .you don’t need to do this operation for the first time, but I choose to use it for reusability
            //sending message to naviagtionactivity through Handler.

            Log.d("pHLE", "PPHHLLEE");
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
        Log.d("KPKPKHUPHUPP", "changeMusic 525");


        try {
            if (position <= songsArrayList.size() - 1) {
                Songs songs = songsArrayList.get(position);
                intent.putExtra("SONG_URI", songs.getSonguri().toString());
                intent.putExtra("POSITION", position);

                Log.d("JPJP", songs.getSongName());
                Log.d("JPJP", position + "DDF");
                //setting songName in motion song name textview
                String songName = songs.getSongName();
                motionSongName.setText(songName);

                //setting bitmap to album image of motion layout
                Bitmap bitmap = songs.getBitmap();
                if (bitmap != null) {
                    motionImagevIew.setImageBitmap(bitmap);
                } else {
                    motionImagevIew.setImageDrawable(getResources().getDrawable(R.drawable.musictwo_ton));
                }

                Log.d("KPKPKP", "changeMusic 2ND LOG MSG 545");
                //starting service evrytym when song change.
                //  getActivity().stopService(intent);
                Log.d("KPKPKP", "changeMusic 3rD LOG MSG 548");

                //getActivity().startService(intent);
                Log.d("KPKPKP", "changeMusic 4th LOG MSG 551");

                playMusic(songs);

                //instead of creating playMusic we can recursivly used the changeMusic method with the playMusic code
            }
        } catch (Exception e) {
        }

    }

    //motion playPause button
    public void playPauseButton() {
        if (musicService != null) {
            if (musicService.isPlaying()) {
                musicService.pause();
                playPauseButton.startAnimation(buttonanimation);
                playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);

            } else {
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
        if (sec < 10) {
            timeLabel += "0";
        }

        timeLabel += sec;
        return timeLabel;
    }

    void prev_Next_Button() {
        next_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("sssiize", songsArrayList2.size() + "");
                next_Button.startAnimation(buttonanimation);
                Log.d("SSIIZE", songsArrayList.size() + "HAPU__");

                //this else if block for continue clicking
                // the next button to next the song
                if (mposition >= songsArrayList2.size() - 1) {
                    i.putExtra("Position", mposition);
                    broadcastManager.sendBroadcast(i);
                    mposition = -1;
                } else if ((mposition >= -1) && (mposition < songsArrayList2.size() - 1)) {
                    playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
                    Log.d("mposition", mposition + "_");
                    int size = songsArrayList2.size() - 1;
                    Log.d("HAALO", size + "" + mposition + "_");
                    ++mposition;
                    i.putExtra("Position", mposition);
                    broadcastManager.sendBroadcast(i);
                    Log.d("Nichewala", "else if condition");
                }


            }
        });
        prev_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev_Button.startAnimation(buttonanimation);
                if (mposition >= 1) {
                    musicService.reset();
                    playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);

                    i.putExtra("Position", --mposition);
                    broadcastManager.sendBroadcast(i);

                    // changeMusic(--mposition);
                }
            }
        });
    }

    //BY ANALYIS OF ALLCODE AGAIN I THINK this onCompletion function is USELESS.
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("KPKPKPCOMPLETE", "onCompletion 642LINE METHOD 642");
        playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        changeMusic(++mposition);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("KPKPKP", "onStart 642LINE METHOD 650");

    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent1 = new Intent(getContext(), MusicService.class);
        getActivity().bindService(intent1, sConnection, Context.BIND_AUTO_CREATE);


    }

    public void setPaletteColor(Palette p) {
        try {
            motionCardView.setBackgroundColor(p.getDarkMutedColor(getActivity().getResources().getColor(R.color.Green)));
            //   lastSpace.setBackgroundColor(p.getDarkMutedColor(getActivity().getResources().getColor(R.color.Green)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(p.getDarkMutedColor(getActivity().getResources().getColor(R.color.Green)));
                window.setNavigationBarColor(p.getDarkMutedColor(getActivity().getResources().getColor(R.color.Green)));
            }

        } catch (Exception e) {
        }
    }

    public void bottomSheetViews() {
        bottomSHEET = LayoutInflater.from(getContext()).inflate(R.layout.detailssong_bottom_sheetlayout, getActivity().findViewById(R.id.sheetConstraintLayout));
        artistSheetText = bottomSHEET.findViewById(R.id.artist_textview);
        albumSheetText = bottomSHEET.findViewById(R.id.album_textview);
        durationSheetText = bottomSHEET.findViewById(R.id.Duration_textview);
        songSizeSheet = bottomSHEET.findViewById(R.id.songSize_textview);
        shareSong = bottomSHEET.findViewById(R.id.share_textview);
    }

    public void motionlayoutViews() {
        //getting motion layout from <include/> navigationActivity.
        motionLayoutt = getActivity().findViewById(R.id.inccluddeMotion);
        motionCardView = motionLayoutt.findViewById(R.id.cardview);
        motionSongName = motionLayoutt.findViewById(R.id.song_name);
        motionartistName = motionLayoutt.findViewById(R.id.artist_name_text_view);
        motionImagevIew = motionLayoutt.findViewById(R.id.album_art_image_view);
        playPauseButton = motionLayoutt.findViewById(R.id.play_pause_button);
        motionCurrentDuration = motionLayoutt.findViewById(R.id.currentDurationText);
        motionTotalDuration = motionLayoutt.findViewById(R.id.totalDurationText);
        prev_Button = motionLayoutt.findViewById(R.id.prev_image_view);
        next_Button = motionLayoutt.findViewById(R.id.next_image_view);
        motionLayoutt.findViewById(R.id.collapse_image_view);
        positionTextview = motionLayoutt.findViewById(R.id.position);
        // lastSpace = motionLayoutt.findViewById(R.id.lastspace);
        dotsButton = motionLayoutt.findViewById(R.id.DotsButton);
        bottomNavigationView = motionLayoutt.findViewById(R.id.bottomNavigation);
        seekBar = getActivity().findViewById(R.id.seekbar);
        motionRecyclerView = motionLayoutt.findViewById(R.id.lastspace);

    }

    public String byteToMB(long bytes) {
        Log.d("SONGSIZE", String.valueOf(bytes));
        Float bytesfloat = Float.valueOf(String.valueOf(bytes));
        float mb = bytesfloat / 1000000;
        String mbStr = String.valueOf(mb);
        String mbsstr = String.valueOf(new DecimalFormat("###.#").format(mb));

        //TODO make mb showing in perfect way.
        return mbsstr;
    }

    public void shareSongUri(int receivedPosition) {
        shareSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = songsArrayList2.get(receivedPosition).getSonguri();
                File file = new File(uri.toString());
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("audio/*");
                // if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                //this if block also work for android 7 api 24 but not in 27 ..
                Log.d("POPLUPEHLE", file.toString());
                intentShare.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intentShare, "Share Sound File"));

                 /* }else{
                     // Uri urifromProvider=FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider",file);
                     try {
                         Uri urifromProvider = FileProvider.getUriForFile(requireContext(),
                                 "com.example.myapp.provider", file);
                         intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                         intentShare.setDataAndType(urifromProvider,getActivity().getContentResolver().getType(urifromProvider));

                         startActivity(intentShare);
                     }catch(Exception e){e.printStackTrace();
                         Log.d("exceeption",e.getMessage());
                     }
                  }*/
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortingArrayList() {
        try {
            if (!songsArrayList2.isEmpty()) {
                songsArrayList2.sort(new Comparator<Songs>() {
                    @Override
                    public int compare(Songs lhs, Songs rhs) {
                        String lhsName = lhs.getSongName();
                        String rhsName = rhs.getSongName();
                        byte[] lhsByteArray = lhsName.getBytes();
                        byte[] rhsByteArray = rhsName.getBytes();

                        Log.d("compsuyrtddys", lhsName.compareTo(rhsName) + "djfkd");
                        return rhs.getDuration().compareTo(lhs.getDuration());

                    }
                });
            }
        } catch (Exception e) {
        }
    }

    public void settingBitmapOnToolbarImages() {
        try {
            {
                Bitmap bitmap1 = songsArrayList2.get(0).getBitmap();
                Bitmap bitmap2 = songsArrayList2.get(1).getBitmap();
                Bitmap bitmap3 = songsArrayList2.get(2).getBitmap();
                Bitmap bitmap4 = songsArrayList2.get(3).getBitmap();

                if (bitmap1 != null) {
                    //    Log.d("NOT_NULL",bitmap1.toString());
                    binding.songImageOnToolbar1.setImageBitmap(bitmap1);
                } else {
                    // Log.d("NOT_NULL","bitmap1 is null");
                    binding.songImageOnToolbar1.setScaleType(ImageView.ScaleType.CENTER);
                    binding.songImageOnToolbar1.setImageResource(R.drawable.ic_baseline_music_note_24);

                }

                if (bitmap2 != null) {
                    binding.songImageOnToolbar2.setImageBitmap(bitmap2);
                    // Log.d("NOT_NULL",bitmap2.toString());
                } else {
                    // Log.d("NOT_NULL","bitmap2 is null");
                    binding.songImageOnToolbar1.setScaleType(ImageView.ScaleType.CENTER);
                    binding.songImageOnToolbar1.setImageResource(R.drawable.ic_baseline_music_note_24);
                }

                if (bitmap3 != null) {
                    // Log.d("NOT_NULL",bitmap3.toString());
                    binding.songImageOnToolbar3.setImageBitmap(bitmap3);
                } else {
                    // Log.d("NOT_NULL","bitmap3 is null");
                    binding.songImageOnToolbar1.setScaleType(ImageView.ScaleType.CENTER);
                    binding.songImageOnToolbar1.setImageResource(R.drawable.ic_baseline_music_note_24);
                }

                if (bitmap4 != null) {
                    //  Log.d("NOT_NULL",bitmap4.toString());
                    binding.songImageOnToolbar4.setImageBitmap(bitmap4);
                } else {
                    // Log.d("NOT_NULL","bitmap4 is null");
                    binding.songImageOnToolbar1.setScaleType(ImageView.ScaleType.CENTER);
                    binding.songImageOnToolbar1.setImageResource(R.drawable.ic_baseline_music_note_24);
                }
            }
        }catch (Exception e){}
    }

}