package com.example.myapp.fragmentsNavigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.MusicRecylerView.MyAdapter
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicService
import com.example.myapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class OnlineMusicFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_online_music, container, false)
     return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var arrlist:ArrayList<Songs>?=MusicService.songsList
          var recycelrView=view.findViewById<RecyclerView>(R.id.Recently_RecylerView)
        var myAdapter:MyAdapter= MyAdapter(context,arrlist)
        recycelrView.layoutManager=GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
        recycelrView.adapter=myAdapter

        var includeView: View =view.findViewById(R.id.include_TrendingLay1111)
        var textview:TextView=includeView.findViewById(R.id.InLanguage_TextView)

        textview.setText("In English (10)")


    }

}