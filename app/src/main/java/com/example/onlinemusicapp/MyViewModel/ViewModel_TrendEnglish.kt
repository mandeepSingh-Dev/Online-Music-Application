package com.example.onlinemusicapp.MyViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.onlinemusicapp.MusicRecylerView.Songs_FireBase
import com.example.onlinemusicapp.StorageReferenceSingleton
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference

class ViewModel_TrendEnglish(application: Application) : AndroidViewModel(application)
{
    var trendingEnglishList:MutableLiveData<ArrayList<Songs_FireBase>>?=null
    var mrefernceSingleton:StorageReference?=null
    var songsFirebase:ArrayList<Songs_FireBase>?=null

    init {
        trendingEnglishList = MutableLiveData<ArrayList<Songs_FireBase>>()
        songsFirebase = ArrayList()
        mrefernceSingleton = StorageReferenceSingleton().getStroageReference()
        mrefernceSingleton?.child("Trending_Playlist")?.child("English")?.listAll()
            ?.addOnSuccessListener(object : OnSuccessListener<ListResult> {
                override fun onSuccess(listResult: ListResult?) {
                    listResult?.items?.forEach {
                        Log.d("JKDHfffffffffffffffFD", it.name)
                        songsFirebase?.add(Songs_FireBase(it.metadata, it.downloadUrl))
                    }
                    trendingEnglishList?.value = songsFirebase
                }
            })
    }
    fun getLiveList():LiveData<ArrayList<Songs_FireBase>>?
    {
        return trendingEnglishList
    }

}