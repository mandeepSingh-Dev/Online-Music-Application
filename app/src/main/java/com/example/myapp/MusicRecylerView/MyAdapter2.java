package com.example.myapp.MusicRecylerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageMetadata;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder2>
{

    ArrayList<Songs_FireBase> arrayList;
    Context context;
    LocalBroadcastManager manager;
    Intent intent;


    public MyAdapter2(Context context, ArrayList<Songs_FireBase> arrayList)
    {
        super();
        this.arrayList=arrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public MyAdapter2.MyViewHolder2 onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Log.d("SIIZE",arrayList.size()+"SIZE");
        View view=LayoutInflater.from(context).inflate(R.layout.list_items_for_songsfragment,parent,false);
        MyViewHolder2 viewHolder=new MyViewHolder2(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter2.MyViewHolder2 holder,int position) {
        Songs_FireBase songs_fireBase=arrayList.get(position);
        Log.d("SIIZE",arrayList.size()+"SIZE");
        int positiondup=position;

        manager=LocalBroadcastManager.getInstance(context);
        intent=new Intent("SENDING_BITMAPSTR");

          songs_fireBase.getStorageMetadataa().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
              @Override
              public void onSuccess(StorageMetadata storageMetadata) {
              String name=storageMetadata.getCustomMetadata("SongName");
              String bitmapstr=storageMetadata.getCustomMetadata("Bitmap");
              Bitmap bitmap=convertToBitmap(bitmapstr);
                  holder.songName.setText(name);
                  holder.songImagee.setImageBitmap(bitmap);
                  if(positiondup==0)
                  {
                      intent.putExtra("BITMAPSTR",bitmapstr);
                    manager.sendBroadcast(intent);

                  }
              }
          });
        /*Log.d("MYADAPTER_NNAME",name+"f,lhb");
        holder.songName.setText(name);*/

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


    @Override
    public int getItemCount() {
        Log.d("SIIZE",arrayList.size()+"SIZE");

        return arrayList.size();
    }

    public class MyViewHolder2 extends RecyclerView.ViewHolder
    {
        TextView songName;
        ImageView songImagee;

        public MyViewHolder2(@NonNull @NotNull View itemView) {
            super(itemView);
            Log.d("SIIZE",arrayList.size()+"SIZE");
            songName=itemView.findViewById(R.id.songNamee);
            songImagee=itemView.findViewById(R.id.songImagee);

            Animation animation= AnimationUtils.loadAnimation(context,R.anim.opening_anim);
            itemView.setAnimation(animation);

        }
    }


}
