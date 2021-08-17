package com.example.myapp;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class NavigationMainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView  navigationView;
    public static Handler mHandler;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDatabase database;
    DatabaseReference mreference;


    String databaseBitmapSTR;
    MotionLayout motionLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_navigation_main);


        if(getActionBar()!=null) {
            getSupportActionBar().hide();
        }

        toolbar = findViewById(R.id.toolbarr);
        drawerLayout = findViewById(R.id.drawer);



        setSupportActionBar(toolbar);

        navigationView=findViewById(R.id.navdrawer);





        //ActionBarDrawerToggle to integrate toolbar and drawerlayout(with NavigationBar)
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_open,R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


       // View bottomLayoutView= findViewById(R.id.bottom_sheet_Include);
       // SeekBar seekBar=bottomLayoutView.findViewById(R.id.seekbar);
       // TextView textView=bottomLayoutView.findViewById(R.id.hellotext);


        //getting motion layout from <include/>
         motionLayout=findViewById(R.id.inccluddeMotion);
         TextView textView=motionLayout.findViewById(R.id.song_name);


          //header layout views..
           View view=navigationView.getHeaderView(0);
        TextView profileName = view.findViewById(R.id.Profile_Name);
        ImageView profilePic = view.findViewById(R.id.profile_pic);;

        //Handler to receieve message from myHandler inner class..
        mHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
               // textView.setText(msg.obj.toString()
               // );
                    Bundle bundle=msg.getData();
                    String songName=bundle.getString("songname","Empty");
                    Long duration=bundle.getLong("duration",100L);
                    int currentPosition=bundle.getInt("currentposition");
                   // Toast.makeText(NavigationMainActivity.this,songName,Toast.LENGTH_SHORT).show();
                    textView.setText(songName);
                   // songNameText.setText(songName);

                Log.d("buueouwoeuw",songName+"\n"+duration);

              //  seekBar.setMax(Integer.parseInt(String.valueOf(duration)));
               // seekBar.setProgress(currentPosition);

                Log.d("currentPosition",String.valueOf(currentPosition));
                return true;
            }
        });



        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();



        try {
            String currentUID = firebaseUser.getUid();
            Log.d("pioioi",currentUID);

            database = FirebaseDatabase.getInstance();
            mreference = database.getReference("Users").child(currentUID);

        }catch (Exception e){}


         //getting list of emails from realtime datbase

       try{
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

                   Glide.with(NavigationMainActivity.this).asBitmap().load(uri).into(new CustomTarget<Bitmap>() {
                       @Override
                       public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                           Log.d("ggggglide",resource.toString());
                           profilePic.setImageBitmap(resource);
                       }

                       @Override
                       public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
                       }
                   });
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });  //addListenerForSingleValueEvent closed here.

               }
           });  //Executor closed 131 number.a
        }catch (Exception e){
           Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    //MyHandler class for receivemessage from musicFragment..
    public static class MyHandler extends Handler
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

}