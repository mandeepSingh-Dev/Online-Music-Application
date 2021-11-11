package com.example.onlinemusicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinemusicapp.MusicRecylerView.Songs;
import com.example.onlinemusicapp.databinding.ActivityMainBinding;
import com.example.onlinemusicapp.signUpActivity.SignUpActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Songs> arrayList=new ArrayList<>();
    ActivityMainBinding binder;
    FirebaseAuth firebaseAuth;
    EditText editTextEmail;
    EditText editTextPassword;
    TextInputLayout textInputLayoutemail;
    TextInputLayout textInputLayoutpassword;
    ProgressBar progressBar;




    Intent intent;
    Boolean firstBackPressed=false;
    int TOAST_LENGTH=500;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        binder = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binder.getRoot();
        setContentView(view);



        if(getActionBar()!=null) {
            getSupportActionBar().hide();
        }

       /*  intent=new Intent(this,MusicService.class);

        arrayList.add(new Songs("Wakke","Mandeep", Uri.parse("file:///storage/emulated/0/Download/astronaut.mp3")));
        arrayList.add(new Songs("Wakke","Mandeep",Uri.parse("file:///storage/emulated/0/Download/Celebrity%20Killer%20-%20Sidhu%20Moose%20Wala%20(DjPunjab.Com).mp3")));
        arrayList.add(new Songs("Wakke","Mandeep",Uri.parse("file:///storage/emulated/0/Download/daughter_calling.mp3")));
        arrayList.add(new Songs("Wakke","Mandeep",Uri.parse("file:///storage/emulated/0/Download/Wakka%20-%20Kulbir%20Jhinjer%20(DjPunjab.Com).mp3")));
        intent.putExtra("ff",arrayList);*/
        // startService(intent);

        SharedPreferences sharedPreferences=getSharedPreferences("hello",MODE_PRIVATE);


        binder.Forgottextpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, com.example.onlinemusicapp.NavigationMainActivity.class);
                startActivity(i);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        /*if(firebaseAuth.getCurrentUser().getEmail()!=null)
        {
            finish();
            Intent i=new Intent(MainActivity.this,NavigationMainActivity.class);
            startActivity(i);
        }*/

        //getting textInputLayouts of email and password.
        textInputLayoutemail = binder.textinputLayout;
        textInputLayoutpassword = binder.textinputLayout2;

        //getting email and password edittexts..
        editTextEmail = textInputLayoutemail.getEditText();
        editTextPassword = textInputLayoutpassword.getEditText();
        //getting progressbar
        progressBar = binder.progressbar;


        binder.loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                if (!validateEmail() | !validatePassword()) {
                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Log.d("kigujghkm", e.getMessage());
                            binder.textnotfound.setVisibility(View.VISIBLE);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            binder.textnotfound.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            Intent i=new Intent(MainActivity.this, com.example.onlinemusicapp.NavigationMainActivity.class);
                            startActivity(i);
                        }
                    });

                }
            }
        });//log in button closed
        binder.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

    }  //onCreate Closed...


    //to validate the email gives boolean if valide or not
    public boolean validateEmail()
    {
        String email=textInputLayoutemail.getEditText().getText().toString();
        if(email.isEmpty()) {
            textInputLayoutemail.setError("Email should not be empty!");
            return false;
        }
       /* else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            textInputLayoutemail.setError("Invalide Email Address! Enter Valid Email Address.");
            return false;
        }*/
        else{
            textInputLayoutemail.setError(null);
            return true;
        }

    }
    //to validate the password gives boolean if valide or not

    public boolean validatePassword()
    {
        String password=textInputLayoutpassword.getEditText().getText().toString();
        if(password.isEmpty())
        {
            textInputLayoutpassword.setError("Password should not ne empty!");
            return false;
        }
        else if(password.length()<8)
        {
            textInputLayoutpassword.setError("password length short! Minimum 8 characters required");
            return false;
        }
        else{
            textInputLayoutpassword.setError(null);
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=firebaseAuth.getCurrentUser();

        if((user!=null) && (user.getEmail()!=null))
        {

            editTextPassword.setEnabled(false);
            editTextEmail.setEnabled(false);
            gotoNavigationActivityAfterSeconds();
        }
        else{
            editTextEmail.setEnabled(true);
            editTextPassword.setEnabled(true);

        }
        /* intent=new Intent(this,MusicService.class);

         *//*  arrayList.add(new Songs("Wakke","Mandeep", Uri.parse("file:///storage/emulated/0/Download/astronaut.mp3")));
        arrayList.add(new Songs("Wakke","Mandeep",Uri.parse("file:///storage/emulated/0/Download/Celebrity%20Killer%20-%20Sidhu%20Moose%20Wala%20(DjPunjab.Com).mp3")));
        arrayList.add(new Songs("Wakke","Mandeep",Uri.parse("file:///storage/emulated/0/Download/daughter_calling.mp3")));
        arrayList.add(new Songs("Wakke","Mandeep",Uri.parse("file:///storage/emulated/0/Download/Wakka%20-%20Kulbir%20Jhinjer%20(DjPunjab.Com).mp3")));
*//*

        intent.setAction("ACTION_PLAY");
        intent.putExtra("ff",arrayList);
       //startService(intent);*/

    }
    public void gotoNavigationActivityAfterSeconds()
    {
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=1;i<3;i++)
                {
                    int k=i;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("hello","dd"+i);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTextEmail.setText("Hello"+k);
                        }
                    });

                }
                // Intent i=new Intent(getApplicationContext(),NavigationMainActivity.class);
                //startActivity(i);

            }
        });
        t.start();
    }

    @Override
    public void onBackPressed() {
        if(firstBackPressed) {
            super.onBackPressed();
            return;
        }
        //when we pressed back first time then firstBackPressed becomes true
        //and to avoid it as true for long time we need to set it false after 1 second
        //it will be false and we need to process this back butoon twice again
        firstBackPressed=true;
        Toast.makeText(this,"Click again to exit",Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                firstBackPressed=false;
            }
        },2000);


    }
}