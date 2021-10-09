package com.example.myapp.signUpActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.ExecutorSingleton;
import com.example.myapp.NavigationMainActivity;
import com.example.myapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binder;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mreference;

    ProgressBar progressBar;
    String defaultUri= "content://media/external_primary/images/media/457";


    TextInputLayout textinputlayoutEmail;
    TextInputLayout textinputlayoutpassword;



    //Getting Intent input in createIntent() and return to parseResult
    //Then getting Uri in parseResult from intent activity and returns to onActivityResault Callback
    ActivityResultContract<Intent, Uri> contract=new ActivityResultContract<Intent, Uri>() {
        @NonNull
        @NotNull
        @Override
        public Intent createIntent(@NonNull @NotNull Context context, Intent input) {
            return input;
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent intent) {
           Uri uri1=null;
           try {
               Uri uri = intent.getData();

               if (uri != null) {
                   uri1 = uri;
               }
               else{
                   uri1=Uri.parse(defaultUri);
               }

           }catch (Exception e)
           {}
                return uri1;
        }
    };
    ActivityResultLauncher<Intent> launcher=registerForActivityResult(contract, new ActivityResultCallback<Uri>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onActivityResult(Uri result) {

                  ExecutorSingleton.getInstance().execute(new Runnable() {
                      @RequiresApi(api = Build.VERSION_CODES.P)
                      @Override
                      public void run() {

                            try {
                                showProgressBar();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);



                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    //Base64 is used for SharedPreference only which
                    //will encode byteArray to String and decode to byteArray
                    String bitmapstr = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                    Log.d("SIGNUPBITMAP",bitmapstr);

                    SharedPreferences sharedPreferences = getSharedPreferences("demo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("bitmap", bitmapstr);
                    editor.putString("Uristring", result.toString());
                    editor.apply();

                        } catch (Exception e) {
                        }
                    //here In this Method we'r getting string (bitmap) from sharedprefrence
                    // then Base64 will decode to bytesArray
                    //then BitmapFactory will decode this bytesArray into bitmap
                    // to set in imageView.

                          setBitmapFromSharedPrefernce();
                        }
                    });





        }
    });  //registerForActivityResult()  method is closed.

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binder = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view=binder.getRoot();
        setContentView(view);


        textinputlayoutEmail = binder.textinputlayoutemail;
        textinputlayoutpassword = binder.textinputlayoutpassword;
        progressBar=binder.progressbar;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mreference=firebaseDatabase.getReference("Users");


        //we have to get this encoded string(bitmap) in
        // onCreate Method because onCreate is lifecycle method
        //whenever we restart activity this image will be same due to onCreate method

        setBitmapFromSharedPrefernce();

        //profilepic button click to open Intent action Pick
        binder.profileCircularImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImageUsingIntent();

            }
        });

        //Signup and store Details into Realtime Database..
        signUpAndStoreDetails();

    }  //onCreate() Closed..
     //Pickup the Image from gallery using Intent..
    public void selectImageUsingIntent()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_PICK);
        launcher.launch(i);
    }

    public boolean validateEmail()
    {
        String email=textinputlayoutEmail.getEditText().getText().toString();
        if(email.isEmpty()) {
            textinputlayoutEmail.setError("Email should not be empty!");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            textinputlayoutEmail.setError("Invalide Email Address! Enter Valid Email Address.");
            return false;
        }
        else{
            textinputlayoutEmail.setError(null);
            return true;
        }

    }
    //to validate the password gives boolean if valide or not

    public boolean validatePassword()
    {
        String password=textinputlayoutpassword.getEditText().getText().toString();
        if(password.isEmpty())
        {
            textinputlayoutpassword.setError("Password should not ne empty!");
            return false;
        }
        else if(password.length()<8)
        {
            textinputlayoutpassword.setError("password length short! Minimum 8 characters required");
            return false;
        }
        else{
            textinputlayoutpassword.setError(null);
            return true;
        }
    }

    public void signUpAndStoreDetails()
    {
        // TODO: 01-07-2021 getting all emails strings from Realtime database to check email is exist or not
        binder.signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setIndeterminate(true);
                progressBar.setVisibility(View.VISIBLE);

                String Name=binder.Name.getText().toString();
                String lastname=binder.Lastname.getText().toString();
                String email=binder.textinputlayoutemail.getEditText().getText().toString();
                String password=binder.textinputlayoutpassword.getEditText().getText().toString();
                String confirmpassword=binder.textinputlayoutConfirmPassword.getEditText().getText().toString();
                //getting bitmap string of profile pic
                String bitmapStr=getPicStringFromShrdPrfnce();


                if(!validateEmail() | !validatePassword())
                {
                    progressBar.setVisibility(View.GONE);
                   Toast.makeText(getApplicationContext(),"Details Incorrect",Toast.LENGTH_LONG).show();
                }
                else{
                    //This if() block tells if everything is correct in textboxes..then
                      if(password.equals(confirmpassword) && (!Name.isEmpty())) {

                          //checking Entered email with database Email :: if email deont match
                          // means email is new and ready for signup
                                  mreference.addValueEventListener(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                          // String currentEmail = firebaseUser.getEmail();

                                          Boolean con1 = false;
                                          //getting list of all emails..
                                          for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                              Map<String, Object> map = (Map<String, Object>) snapshot1.getValue();
                                              String emails = map.get("Email").toString();

                                              if (email.equals(emails)) {
                                                  con1 = true;

                                                  Snackbar snackbar = Snackbar.make(progressBar, "Account Already Exist", BaseTransientBottomBar.LENGTH_LONG);
                                                  snackbar.getView().setBackgroundColor(Color.RED);
                                                  snackbar.setTextColor(Color.WHITE);
                                                  snackbar.show();
                                                  progressBar.setVisibility(View.GONE);
                                              }
                                              Log.d("rrrrrrrr", map.get("Email").toString());

                                          }//forEach loop closed.....
                                          Log.d("rrtt", con1.toString());
                                          if (!con1) {
                                              Toast toast = Toast.makeText(getApplicationContext(), "Please Wait", Toast.LENGTH_LONG);
                                              toast.show();
                                              //After checking email now we do SignUp
                                              Task<AuthResult> task = firebaseAuth.createUserWithEmailAndPassword(email, password);

                                              task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                  @Override
                                                  public void onSuccess(AuthResult authResult) {
                                                      firebaseUser = firebaseAuth.getCurrentUser();
                                                      String Uid = firebaseUser.getUid();


                                                      //now storing details in HashMap as keyValue pairs
                                                      Map<String, Object> map = new HashMap<>();
                                                      map.put("Name", Name);
                                                      map.put("LastName", lastname);
                                                      map.put("Email", email);
                                                      map.put("password", password);
                                                      map.put("ProfilePicString", bitmapStr);
                                                      //now set map into realtime database
                                                      mreference.child(Uid).setValue(map);

                                                      progressBar.setVisibility(View.GONE);

                                                      Intent intent = new Intent(SignUpActivity.this, NavigationMainActivity.class);
                                                      startActivity(intent);

                                                      //onFailure of signup account
                                                      task.addOnFailureListener(new OnFailureListener() {
                                                          @Override
                                                          public void onFailure(@NonNull @NotNull Exception e) {
                                                              Log.d("failure", e.getMessage() + "\n" + e.getCause());
                                                              Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                          }
                                                      });
                                                  }
                                              });  //signup process finished here...
                                          }
                                      }

                                      @Override
                                      public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                      }
                                  });  //getting emails, checking and signup  finished here.

                              } //password checking if{} block closed
                    else{
                        if(!password.equals(confirmpassword)) {
                            binder.textinputlayoutConfirmPassword.setError("password doesn't match");
                        progressBar.setVisibility(View.GONE);
                        }
                        else if(Name.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(),"Name cannot be empty",Toast.LENGTH_LONG).show();

                           progressBar.setVisibility(View.GONE);
                        }
                        }
                }//parent else block closed
            }
        });

    }
    //getting profile pic bitmap string from Sharedprefernce to store this string realtime database
    public String getPicStringFromShrdPrfnce()
    {

        SharedPreferences sharedPreferences1 = getSharedPreferences("demo", MODE_PRIVATE);
        String bitmapstr1 = sharedPreferences1.getString("Uristring", "hello");

        return  bitmapstr1;
    }


    //this method get String(Bitmap) from sharedfPrefernces
    // and Base64 decode this string bytesArray and Bitmapfactry
    //converts bytesarray into bitmap to set imageview
    public void setBitmapFromSharedPrefernce()
    {
/*Thread thread=new Thread(new Runnable() {
    @Override
    public void run() {*/
        ExecutorSingleton.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Log.d("dfd",Thread.currentThread().toString());

        try {
            SharedPreferences sharedPreferences1 = getSharedPreferences("demo", MODE_PRIVATE);
            String bitmapstr1 = sharedPreferences1.getString("bitmap", "hello");

            byte[] bytes = Base64.decode(bitmapstr1, Base64.DEFAULT);


            Bitmap bitmapp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
              Log.d("SongupBitmap",bitmapp.toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    binder.profileCircularImage.setImageBitmap(bitmapp);

                       }
                      });
            closeProgressBar();

        }catch(Exception e){}
   }
});
//thread.start();

    }  //setBitmapfromShareprefrence method closed here.


public void showProgressBar()
{
    runOnUiThread(new Runnable() {
        @Override
        public void run() {

            progressBar.setVisibility(View.VISIBLE);

        }
    });
}
public void closeProgressBar()
{
    progressBar.setVisibility(View.GONE);
}

}