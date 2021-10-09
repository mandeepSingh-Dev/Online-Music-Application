package com.example.myapp.MusicRecylerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder2>
{

    ArrayList<Songs> arrayList;
    Context context;


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
        View view=LayoutInflater.from(context).inflate(R.layout.list_practise_items,parent,false);
        MyViewHolder2 viewHolder=new MyViewHolder2(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter2.MyViewHolder2 holder, int position) {
        Songs song=arrayList.get(position);
        Log.d("SIIZE",arrayList.size()+"SIZE");

        String name=song.getSongName();
        Log.d("MYADAPTER_NNAME",name+"f,lhb");
        holder.songName.setText(name);

    }

   /* @Override
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
            holder.imageView.setBackgroundResource(R.drawable.artist_person_icon);
            Log.d("NAMEandBitmap",name+"kfhvfjk");

        }

        holder.songName.setText(name);

        // holder.artist.setText(artist);
    }

*/


    @Override
    public int getItemCount() {
        Log.d("SIIZE",arrayList.size()+"SIZE");

        return arrayList.size();
    }

    public class MyViewHolder2 extends RecyclerView.ViewHolder
    {
        TextView songName;

        public MyViewHolder2(@NonNull @NotNull View itemView) {
            super(itemView);
            Log.d("SIIZE",arrayList.size()+"SIZE");
            songName=itemView.findViewById(R.id.songNametext22);

        }
    }


}
