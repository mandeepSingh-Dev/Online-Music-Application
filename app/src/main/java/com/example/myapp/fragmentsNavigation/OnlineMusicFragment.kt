package com.example.myapp.fragmentsNavigation

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.MusicRecylerView.MyAdapter
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicService
import com.example.myapp.R
import com.example.myapp.databinding.ListTrendingLayoutBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class OnlineMusicFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_online_music, container, false)
     return view
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arrlist:ArrayList<Songs>?=MusicService.songsList
        if (arrlist != null) {
            setDataToRecentPlayedList(arrlist, view)

            setDataToTopChartsPlayedList(arrlist, view)
            setDataToMoodsPlayedList(arrlist,view)
            setDataToFeaturedPlayedList(arrlist,view)
        }

        val includeView: View =view.findViewById(R.id.include_TrendingLay11)
        val includeView2:View=view.findViewById(R.id.include_TrendingLay111)
        val includeView3:View=view.findViewById(R.id.include_TrendingLay1111)

        Log.d("IIDD",includeView.id.toString())
        Log.d("IIDD",R.id.include_TrendingLay11.toString()+" __" +
                "BASE")

        if (arrlist != null) {
            setDataToTrendingLayout(includeView,arrlist," In English (10)")
        }
        if (arrlist != null) {
            setDataToTrendingLayout(includeView2,arrlist,"In Punjabi (10)")
        }
        if (arrlist != null) {
            setDataToTrendingLayout(includeView3, arrlist,"In Hindi (10)")

        }
        includeView2.background=resources.getDrawable(R.drawable.gradient_includetrending_1)
        includeView3.background=resources.getDrawable(R.drawable.gradient_includetrending_2,null)
        includeView.background=resources.getDrawable(R.drawable.gradient_includetrending_3,null)

        //var textview:TextView=includeView.findViewById(R.id.InLanguage_TextView)

        //textview.setText("In English (10)")


    }
    fun setDataToTrendingLayout(includeview: View ,songlist:ArrayList<Songs>, language:String):Unit
    {
        //if(includeview.id==R.id.include_TrendingLay11) {
        val layoutTrending=ListTrendingLayoutBinding.inflate(layoutInflater)

           val textview: TextView = includeview.findViewById(R.id.InLanguage_TextView)
           val songName1:TextView=includeview.findViewById(R.id.song_name1)
           val songName2:TextView=includeview.findViewById(R.id.song_name2)
           val songName3:TextView=includeview.findViewById(R.id.song_name3)
           val songName4:TextView=includeview.findViewById(R.id.song_name4)
           val songName5:TextView=includeview.findViewById(R.id.song_name5)

        textview.setText(language)
        songName1.setText(songlist.get(0).songName)
        songName2.setText(songlist.get(1).songName)
        songName3.setText(songlist.get(2).songName)
        songName4.setText(songlist.get(3).songName)
        songName5.setText(songlist.get(4).songName)

        layoutTrending.artistName1.setText(songlist.get(0).songName)
        layoutTrending.artistName2.setText(songlist.get(1).artist)
        layoutTrending.artistName3.setText(songlist.get(2).artist)
        layoutTrending.artistName4.setText(songlist.get(3).artist)
        layoutTrending.artistName5.setText(songlist.get(4).artist)
        //}
    }
    fun setDataToRecentPlayedList(arraylist:ArrayList<Songs>,view: View)
    {
        val recycelrView=view.findViewById<RecyclerView>(R.id.Recently_RecylerView)
        val myAdapter:MyAdapter= MyAdapter(context,arraylist)
        recycelrView.layoutManager=GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
        recycelrView.adapter=myAdapter

    }
    fun setDataToTopChartsPlayedList(arraylist:ArrayList<Songs>,view: View)
    {
        val recycelrView=view.findViewById<RecyclerView>(R.id.TopCharts_RecylerView)
        val myAdapter:MyAdapter= MyAdapter(context,arraylist)
        recycelrView.layoutManager=GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
        recycelrView.adapter=myAdapter

    }
    fun setDataToMoodsPlayedList(arraylist:ArrayList<Songs>,view: View)
    {
        val recycelrView=view.findViewById<RecyclerView>(R.id.Moods_RecylerView)
        val myAdapter:MyAdapter= MyAdapter(context,arraylist)
        recycelrView.layoutManager=GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
        recycelrView.adapter=myAdapter

    }
    fun setDataToFeaturedPlayedList(arraylist:ArrayList<Songs>,view: View)
    {
        val recycelrView=view.findViewById<RecyclerView>(R.id.Artists_RecylerView)
        val myAdapter:MyAdapter= MyAdapter(context,arraylist)
        recycelrView.layoutManager=GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
        recycelrView.adapter=myAdapter

    }

}