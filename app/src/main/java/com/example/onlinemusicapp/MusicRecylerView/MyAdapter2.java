package com.example.onlinemusicapp.MusicRecylerView;

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

import com.example.onlinemusicapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageMetadata;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder2>
{

/*
    ArrayList<Songs_FireBase> arrayList;
*/
    ArrayList<Songs> arrayList;

    Context context;
    LocalBroadcastManager manager;
    Intent intent;

    public CustomItemClickListener2 customItemClickListener2;


   /* public MyAdapter2(Context context, ArrayList<Songs_FireBase> arrayList)
    {
        super();
        this.arrayList=arrayList;
        this.context=context;
    }*/
   public MyAdapter2(Context context, ArrayList<Songs> arrayList)
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
        Songs songs = arrayList.get(position);
        holder.songName.setText(songs.getSongName());
        if (songs.getBitmap()==null)
        {
            holder.songImagee.setImageResource(R.drawable.music_two_tonne);
        }
        else{
            holder.songImagee.setImageBitmap(songs.getBitmap());
        }
        holder.songArtist.setText(songs.getArtist());


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
        TextView songArtist;

        public MyViewHolder2(@NonNull @NotNull View itemView) {
            super(itemView);
            Log.d("SIIZE",arrayList.size()+"SIZE");
            songName=itemView.findViewById(R.id.songNamee);
            songImagee=itemView.findViewById(R.id.songImagee);
            songArtist=itemView.findViewById(R.id.artistNamee);


            Animation animation= AnimationUtils.loadAnimation(context,R.anim.opening_anim);
            itemView.setAnimation(animation);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customItemClickListener2.customOnItemClick(getAdapterPosition());
                }
            });

        }
    }

    public void setOnClickListener(CustomItemClickListener2 customItemClickListener2)
    {
        this.customItemClickListener2=customItemClickListener2;
    }
    //creating interface here
    public interface CustomItemClickListener2
    {
        //creating this function to implement in SongsFrfagment class so when
        //we click a song then position will pass to this function
        public void customOnItemClick(int position);

    }


}
