package com.example.myapp.FoldersStuff

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.R

class TrendingAdapterRecycleView():RecyclerView.Adapter<TrendingAdapterRecycleView.MyViewHolder>()
{
     var context:Context? = null
    var arraylist= ArrayList<Songs>()
    var arraylist2= ArrayList<Folders_dataClass>()

    constructor(context: Context?,arraylist:ArrayList<Folders_dataClass>):this()
    {
        this.context=context
       arraylist2=arraylist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
     val view=LayoutInflater.from(context).inflate(R.layout.list_trending_layout,parent,false)

        val myViewHolder=MyViewHolder(view)
        return myViewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

     var foldersDataclass:Folders_dataClass=arraylist2.get(0)

        var textview=holder.itemView.findViewById<TextView>(R.id.InLanguage_TextView)
        textview.setText(foldersDataclass.folderNamee)
    }

    override fun getItemCount(): Int {
     return arraylist2.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)



}