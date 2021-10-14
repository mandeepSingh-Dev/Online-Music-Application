package com.example.myapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NavigationMainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    public static Handler mHandler;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDatabase database;
    DatabaseReference mreference;


    String databaseBitmapSTR;
    MotionLayout motionLayout;
BottomNavigationView bottomNavigationMotion;



LocalBroadcastManager broadcastManager;
    private FirebaseStorage storage;
    private StorageReference mReference;
    private String songName;
    private String artistName;
    private String bitmapStr;
    private String songSizeStr;
    private String durationStr;
    private String songUriStr;
    private long creationDate;
    private long duration;
    private long size;
    private Uri urii;

    private NavController navController;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_navigation_main);

        storage=FirebaseStorage.getInstance();


       // gettingSongsListfromFireBase("Trending_Playlist", "Punjabi");

        if (getActionBar() != null) {
            getSupportActionBar().hide();
        }



        toolbar = findViewById(R.id.toolbarr);
        drawerLayout = findViewById(R.id.drawer);


        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.navdrawer);


        //ActionBarDrawerToggle to integrate toolbar and drawerlayout(with NavigationBar)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*intenttt=new Intent(this, MusicService.class);
        startService(intenttt);*/

        // View bottomLayoutView= findViewById(R.id.bottom_sheet_Include);
        // SeekBar seekBar=bottomLayoutView.findViewById(R.id.seekbar);
        // TextView textView=bottomLayoutView.findViewById(R.id.hellotext);


        //getting motion layout from <include/>

       /* FragmentManager manager=getSupportFragmentManager();
        NavHostFragment navHostFragment=(NavHostFragment)manager.findFragmentById(R.id.fragmentContainerView);
        NavController navController=navHostFragment.getNavController();*/



        motionLayout = findViewById(R.id.inccluddeMotion);
        bottomNavigationMotion=motionLayout.findViewById(R.id.bottomNavigation);


        //header layout views..
        View view = navigationView.getHeaderView(0);
        TextView profileName = view.findViewById(R.id.Profile_Name);
        ImageView profilePic = view.findViewById(R.id.profile_pic);


        Log.d("helFIREBASElo", "lfjv");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

          if(firebaseUser!=null){
        try {
            String currentUID = firebaseUser.getUid();
            Log.d("pioioi", currentUID + "null");

            database = FirebaseDatabase.getInstance();
            mreference = database.getReference("Users").child(currentUID);

        } catch (Exception e) {
        }
          }else{
              Toast.makeText(this,"User not logged",Toast.LENGTH_SHORT).show();}


        //getting list of emails from realtime datbase

        if (mreference != null) {
            try {
                ExecutorSingleton.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        mreference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                //  if (email.equals(currentemail)) {
                                String databaseBitmap = snapshot.child("ProfilePicString").getValue().toString();
                                String currentName = snapshot.child("Name").getValue().toString();
                                String currentLastName = snapshot.child("LastName").getValue().toString();
                                databaseBitmapSTR = databaseBitmap;
                                profileName.setText(currentName + " " + currentLastName);
                                //Toast.makeText(getApplicationContext(),databaseBitmapSTR,Toast.LENGTH_LONG).show();
                                Log.d("rrrrrrrr", databaseBitmapSTR);
                                Uri uri = Uri.parse(databaseBitmapSTR);

                                try {
                                    Glide.with(NavigationMainActivity.this).asBitmap().load(uri).into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                            Log.d("ggggglide", resource.toString());
                                            profilePic.setImageBitmap(resource);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
                                        }
                                    });
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });  //addListenerForSingleValueEvent closed here.

                    }
                });  //Executor closed 131 number.a
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }//if block closed..
        else{Toast.makeText(this,"User data not available",Toast.LENGTH_LONG).show();}
          //for navigating the fragments..using NavigationView and BottomNavigationBar
        FragmentManager fragmentManager=getSupportFragmentManager();
        NavHostFragment navHostFragment=(NavHostFragment)fragmentManager.findFragmentById(R.id.fragmentContainerView);
         navController=navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNavigationMotion,navController);
        NavigationUI.setupWithNavController(navigationView, navController);
        //onNavigationItemClickListened();

    }// onCreate closed here
    //MyHandler class for receivemessage from musicFragment..
    public static class MyHandler extends android.os.Handler
    {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Message msgg=Message.obtain();
            Bundle bundle=msg.getData();
            msgg.setData(bundle);
            mHandler.sendMessage(msgg);

        }
    }

  /*  public void gettingSongsListfromFireBase( String playlist, String folder)
    {
        initStorageReference(storage);  //initialization of mReference
     LocalBroadcastManager manager=  LocalBroadcastManager.getInstance(NavigationMainActivity.this);
        mReference.child(playlist).child(folder).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {

            @Override
            public void onSuccess(ListResult listResult) {

                List<StorageReference> list = listResult.getItems();

                for (int i = 0; i < list.size() - 1; i++) {
                    list.get(i).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {

                            songName = storageMetadata.getCustomMetadata("SongName").toString();
                            artistName = storageMetadata.getCustomMetadata("Artist").toString();
                            bitmapStr = storageMetadata.getCustomMetadata("Bitmap").toString();
                            songSizeStr = storageMetadata.getCustomMetadata("Size").toString();
                            durationStr = storageMetadata.getCustomMetadata("Duration").toString();
                            songUriStr = storageMetadata.getCustomMetadata("Uri").toString();
                            creationDate = storageMetadata.getCreationTimeMillis();

                            duration = Long.parseLong(durationStr);
                            size = Long.parseLong(songSizeStr);
                            // bitmap=convertToBitmap(bitmapStr)



                            Intent intent=new Intent("SENDINGFIRESONGDATA");
                            intent.putExtra("songName",songName);
                          manager.sendBroadcast(intent);



                            Log.d("HEJKKGO", storageMetadata.getCustomMetadata("SongName").toString() + folder);
                        }
                    });
                    //getting metadata of song 1 by 1
                    list.get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri)
                        {//now getting downloadedUri of song 1 by 1
                            urii = uri;
                        }
                    });

                } //for loop finishes here
            }
        });
    } //gettingSon.....function closed


    public void  initStorageReference(FirebaseStorage storage1)
    {
        mReference = storage1.getReference();
    }

    public Bitmap convertToBitmap(String bitmapStr)
    {
        byte[] byteArray= Base64.decode(bitmapStr,Base64.DEFAULT);
         Bitmap bitmap= BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        return bitmap;
    }*/



    @Override
    public void onBackPressed() {
        long endState=motionLayout.getEndState();
        Toast.makeText(getApplicationContext(),String.valueOf(endState),Toast.LENGTH_SHORT).show();

        //if(endState==2131361981L)
        if(endState==motionLayout.getCurrentState()) {
            motionLayout.transitionToStart();


        }
            else{
            super.onBackPressed();


        }
    }

    public void  onNavigationItemClickListened()
    {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NavOptions navOptions=new NavOptions.Builder().setPopUpTo(R.id.onlineMusicFragment,true).build();

                switch(item.getItemId())
                {
                    case R.id.OfflineMusicFragment :
                    {


                        //here songsFragment id is navHost id of songfragmnet
                        navController.navigate(R.id.OfflineMusicFragment);
                        drawerLayout.closeDrawer(Gravity.START);
                        break;

                    }
                    case R.id.contentUpload_Fragment :
                    {
                        navController.navigate(R.id.contentUpload_Fragment,null,navOptions);
                        drawerLayout.closeDrawer(Gravity.START);

                        break;

                    }
                    case R.id.songsFragment :
                    {
                        navController.navigate(R.id.songsFragment,null,navOptions);
                        drawerLayout.closeDrawer(Gravity.START);


                        break;

                    }
                    case R.id.splashScreen :
                    {
                        navController.navigate(R.id.splashScreen,null,navOptions);
                        Toast.makeText(NavigationMainActivity.this, "No Activity Assigned", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(Gravity.START);

                        break;

                    }
                    case R.id.mainActivity :
                    {
                        navController.navigate(R.id.mainActivity,null,navOptions);
                        drawerLayout.closeDrawer(Gravity.START);


                        break;

                    }
                    case R.id.signUpActivity :
                    {
                        navController.navigate(R.id.signUpActivity,null,navOptions);
                        drawerLayout.closeDrawer(Gravity.START);

                        break;

                    }
                    default:
                        {
                            Toast.makeText(NavigationMainActivity.this, "Deafult Cond", Toast.LENGTH_SHORT).show();
                            drawerLayout.closeDrawer(Gravity.START);
                        }

                }
               // drawerLayout.closeDrawer(Gravity.CENTER);
                //  return true;

                return false;
            }
        });

    }




}