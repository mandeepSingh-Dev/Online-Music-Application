package com.example.myapp.fragmentsNavigation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.annotation.Size
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.MusicRecylerView.MyAdapter
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicService
import com.example.myapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class OnlineMusicFragment : Fragment() {

   lateinit var  includeEnglishTrendingLayout:View
    lateinit var includePunjabiTrendingLayout:View
    lateinit var includeHindiTrendingLayout:View

    lateinit var includeEnglishTopLayout:View
    lateinit var includePunjabiTopLayout:View
    lateinit var includeHindiTopLayout:View
    lateinit var includeUSTopLayout:View
    lateinit var includeDanceTopLayout:View
    lateinit var includeHindi90sTopLayout:View

    lateinit var include_RetroLayout:View
    lateinit var include_IndieLayout:View
    lateinit var include_EDMLayout:View
    lateinit var include_WeddingLayout:View
    lateinit var include_DanceLayout:View
    lateinit var include_RockLayout:View
    lateinit var include_KidsLayout:View
    lateinit var include_GhazalsLayout:View

    lateinit var include_RomanceLayout:View
    lateinit var include_90sAndEarlyLayout:View
    lateinit var include_PartyLayout:View
    lateinit var include_HipHopLayout:View
    lateinit var include_BhaktiLayout:View
    lateinit var include_MusicLayout:View
    lateinit var include_HealsLayout:View
    lateinit var include_PunjabiHitsLayout:View
    lateinit var include_WorkoutLayout:View








    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_online_music, container, false)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.statusBarColor=resources.getColor(R.color.Dark_Red,null)
        }
        else{
            activity?.window?.statusBarColor=resources.getColor(R.color.Dark_Red)

        }

        val arrlist:ArrayList<Songs>?=MusicService.songsList
        if (arrlist != null) {
            setDataToRecentPlayedList(arrlist, view)
            //setDataToMoodsPlayedList(arrlist,view)
            //setDataToFeaturedArtistPlayedList(arrlist,view)
            setDataToNewReleasePlayedList(arrlist,view)
        }


        //for setting Trending layout & its data
        if(arrlist!=null) {
            setUp_TrendingFolder(view, arrlist)
        }

        //for setting Top Charts Folders(include layouts)
        setUp_TopChartsFolder(view)
        //for setting Top Charts Folders(include layouts)

        // CtentResolver resolver=getContext().getContentResolver();
        setUp_DiscoverFolders(view)
        setUp_MoodFolders(view)

        includeEnglishTrendingLayout.background=resources.getDrawable(R.drawable.gradient_includetrending_eng,null)
        includePunjabiTrendingLayout.background=resources.getDrawable(R.drawable.gradient_includetrending_punjabi)
        includeHindiTrendingLayout.background=resources.getDrawable(R.drawable.gradient_includetrending_hindi,null)
    }  //onViewClosed

    //function to set data to Trending include layouts
    fun setDataToTrendingLayout(includeview: View ,songlist:ArrayList<Songs>, language:String):Unit
    {
        //if(includeview.id==R.id.include_TrendingLay11) {

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

        //}
    }

    //function to set data to Recent Played
    fun setDataToRecentPlayedList(arraylist:ArrayList<Songs>,view: View)
    {
        val recycelrView=view.findViewById<RecyclerView>(R.id.Recently_RecylerView)
        val myAdapter:MyAdapter= MyAdapter(context,arraylist)
        recycelrView.layoutManager=GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
        recycelrView.adapter=myAdapter

    }

    /*//function to set data to FeaturedArtists folder(include layouts)
    fun setDataToFeaturedArtistPlayedList(arraylist:ArrayList<Songs>, view: View)
    {
        val recycelrView=view.findViewById<RecyclerView>(R.id.Artists_RecylerView)
        val myAdapter:MyAdapter= MyAdapter(context,arraylist)
        recycelrView.layoutManager=GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
        recycelrView.adapter=myAdapter

    }*/

    //function to set data to Trending include layouts
    fun setDataToNewReleasePlayedList(arraylist:ArrayList<Songs>,view: View)
    {
        val recycelrView=view.findViewById<RecyclerView>(R.id.newRelease_RecylerView)
        val myAdapter:MyAdapter= MyAdapter(context,arraylist)
        recycelrView.layoutManager=GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
        recycelrView.adapter=myAdapter

    }

    //function to set data to Trending include layouts  (i think it will be universal for set name to folders)
    fun setDataToTopChartsLayout(view:View,language:String)  //bitmap is default arguments
    {
        val languageTextView=view.findViewById<TextView>(R.id.language_Textview_folder)
        languageTextView.setText(language)


    }

    //function to setUp Trending folders(include layouts)
    fun setUp_TrendingFolder(view:View,arrlist:ArrayList<Songs>)
    {
        includeEnglishTrendingLayout=view.findViewById(R.id.include_TrendingLay11)
        includePunjabiTrendingLayout=view.findViewById(R.id.include_TrendingLay111)
        includeHindiTrendingLayout=view.findViewById(R.id.include_TrendingLay1111)


        if (arrlist != null) {
            setDataToTrendingLayout(includeEnglishTrendingLayout,arrlist," In English (10)")
        }
        if (arrlist != null) {
            setDataToTrendingLayout(includePunjabiTrendingLayout,arrlist,"In Punjabi (10)")
        }
        if (arrlist != null) {
            setDataToTrendingLayout(includeHindiTrendingLayout, arrlist,"In Hindi (10)")

        }
    }
    //function to setUp Top Charts folders(include layouts)
    fun setUp_TopChartsFolder(view:View)
    {
        includeEnglishTopLayout=view.findViewById(R.id.include_EnglishTop10)
        includePunjabiTopLayout=view.findViewById(R.id.include_PunjabiTop10)
        includeHindiTopLayout=view.findViewById(R.id.include_HindiTop10)
        includeUSTopLayout=view.findViewById(R.id.include_USTop10)
        includeDanceTopLayout=view.findViewById(R.id.include_DanceTop10)
        includeHindi90sTopLayout=view.findViewById(R.id.include_Hindi90sTop10)

        setDataToTopChartsLayout(includePunjabiTopLayout,"Punjabi")
        setDataToTopChartsLayout(includeEnglishTopLayout,"International")
        setDataToTopChartsLayout(includeHindiTopLayout,"Hindi")
        setDataToTopChartsLayout(includeUSTopLayout,"US")
        setDataToTopChartsLayout(includeDanceTopLayout,"Dance")
        setDataToTopChartsLayout(includeHindi90sTopLayout,"Hindi 90s")
    }

    //function to set data to Trending include layouts  (i think it will be universal for set name to folders)

    fun setDataToDiscoverLayout(view:View, language:String, )  //bitmap is default arguments
    {
        val languageTextView=view.findViewById<TextView>(R.id.language_Textview_folder)
        languageTextView.setText(language)

        val imagefolder=view.findViewById<ImageView>(R.id.folder_image)
       // imagefolder.setImageDrawable(resources.getDrawable(R.drawable.jeancarlo,null))

    }

    //function to setUp Top Charts folders(include layouts)
    fun setUp_DiscoverFolders(view:View)
    {
        include_RetroLayout=view.findViewById(R.id.include_Retro)
        include_IndieLayout=view.findViewById(R.id.include_Indie)
        include_EDMLayout=view.findViewById(R.id.include_EDM)
        include_WeddingLayout=view.findViewById(R.id.include_Wedding)
        include_DanceLayout=view.findViewById(R.id.include_Dance)
        include_RockLayout=view.findViewById(R.id.include_Rock)
        include_KidsLayout=view.findViewById(R.id.include_Kids)
        include_GhazalsLayout=view.findViewById(R.id.include_Ghazals)



        setDataToDiscoverLayout(include_RetroLayout,"RETRO")
        setDataToDiscoverLayout(include_IndieLayout,"INDIE")
        setDataToDiscoverLayout(include_EDMLayout,"EDM")
        setDataToDiscoverLayout(include_WeddingLayout,"WEDDING")
        setDataToDiscoverLayout(include_DanceLayout,"DANCE")
        setDataToDiscoverLayout(include_RockLayout,"ROCK")
        setDataToDiscoverLayout(include_KidsLayout,"KIDS")
        setDataToDiscoverLayout(include_GhazalsLayout,"GHAZAL")


    }
    //function to setUp Moods&Collection folders(include layouts)
    fun setUp_MoodFolders(view:View)
    {
        include_RomanceLayout=view.findViewById(R.id.include_Romance)
        include_90sAndEarlyLayout=view.findViewById(R.id.include_90sAndEarly)
        include_PartyLayout=view.findViewById(R.id.include_Party)
        include_HipHopLayout=view.findViewById(R.id.include_HipHop)
        include_BhaktiLayout=view.findViewById(R.id.include_Bhakti)
        include_MusicLayout=view.findViewById(R.id.include_Music)
        include_HealsLayout=view.findViewById(R.id.include_Heals)
        include_PunjabiHitsLayout=view.findViewById(R.id.include_PunjabiHits)
        include_WorkoutLayout=view.findViewById(R.id.include_Workout)




        setDataToDiscoverLayout(include_RomanceLayout,"Romance")
        setDataToDiscoverLayout(include_90sAndEarlyLayout,"90s & Early 2000s")
        setDataToDiscoverLayout(include_PartyLayout,"Party")
        setDataToDiscoverLayout(include_HipHopLayout,"Hip-Hop")
        setDataToDiscoverLayout(include_BhaktiLayout,"Bhakti")
        setDataToDiscoverLayout(include_MusicLayout,"Music")
        setDataToDiscoverLayout(include_HealsLayout,"Heals")
        setDataToDiscoverLayout(include_PunjabiHitsLayout,"Punjabi Top Hits")
        setDataToDiscoverLayout(include_WorkoutLayout,"Workout")



    }


    //function to set data in Discover include layouts
    /*fun setDataToDiscoverLayouts(view:View,language:String)
    {
        val languageflder=view.findViewById<TextView>(R.id.language_Textview_folder)
        languageflder.setText(language)

    }
*/


}