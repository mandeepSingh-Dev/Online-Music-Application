package com.example.myapp.MusicRecylerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
{

    ArrayList<Songs> arrayList;
    Context context;

     CustomItemClickListener itemClickListener;

    public MyAdapter(Context context, ArrayList<Songs> arrayList)
    {
        super();
        this.arrayList=arrayList;
        this.context=context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Log.d("SIIZE",arrayList.size()+"SIZE");
        View view=LayoutInflater.from(context).inflate(R.layout.list_items_music,parent,false);
          MyViewHolder viewHolder=new MyViewHolder(view);
          return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Songs song=arrayList.get(position);
        Log.d("SIIZE",arrayList.size()+"SIZE");

        String name=song.getSongName();

       String artist=song.getArtist();
       Bitmap bitmap=song.getBitmap();
        Uri uri=song.getSonguri();

      if(!(bitmap ==null))
        {
            //Glide.with(context).asBitmap().load(bitmap).into(holder.imageView);
           holder.imageView.setImageBitmap(bitmap);
           Log.d("NAMEandBitmap",name+bitmap.toString());
        }
      else{
          holder.imageView.setBackgroundResource(R.drawable.musictwo_ton);
          Log.d("NAMEandBitmap",name+"kfhvfjk");

      }

          holder.songName.setText(name);

         // holder.artist.setText(artist);
    }




    @Override
    public int getItemCount() {
        Log.d("SIIZE",arrayList.size()+"SIZE");

        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
          ImageView imageView;
          TextView songName;
          TextView artist;
          View view;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            Log.d("SIIZE",arrayList.size()+"SIZE");
            imageView=itemView.findViewById(R.id.thumbnailimageview);
            songName=itemView.findViewById(R.id.songNametext);
            artist=itemView.findViewById(R.id.artistNametext);
            view=itemView;





            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

           itemClickListener.onItemClick(getAdapterPosition(),imageView);
           Log.d("ugfugf","rrr");

                }
            });
        }
    }
   public void setOnItemClickListener(CustomItemClickListener customItemClickListener)
   {
       itemClickListener=customItemClickListener;
   }
   public interface CustomItemClickListener
   {
     void onItemClick(int position,View imageview);
   }


}
