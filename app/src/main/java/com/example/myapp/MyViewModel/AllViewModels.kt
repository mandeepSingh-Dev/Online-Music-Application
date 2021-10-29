package com.example.myapp.MyViewModel

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapp.MusicRecylerView.Songs_FireBase
import com.example.myapp.StorageReferenceSingleton
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference

class AllViewModels
{
    private var trendingEnglishList:MutableLiveData<ArrayList<Songs_FireBase>>?=null
    inner class MyViewModel : AndroidViewModel
    {
        constructor(application: Application) : super(application) {
            var mrefernceSingleton = StorageReferenceSingleton().getStroageReference()
            settingList("Trending_Playlist", "English", mrefernceSingleton)

        }

        fun settingList(playlist:String,folder:String,mReference:StorageReference)
        {
             //mReference = StorageReferenceSingleton().getStroageReference()
            trendingEnglishList = MutableLiveData<ArrayList<Songs_FireBase>>()

            mReference?.child(playlist)?.child(folder)?.listAll()
                ?.addOnSuccessListener(object : OnSuccessListener<ListResult> {
                    override fun onSuccess(listResult: ListResult?) {
                        listResult?.items?.forEach {
                            Log.d("JKDHFD", it.name)
                           // songsFirebase?.add(Songs_FireBase(it.metadata, it.downloadUrl))

                        }
/*
                Log.d("HFHDJ",songsFirebase.get(0).toString())
*/
/*
                        livesongsFirebase?.value = songsFirebase
*/

                    }
                })
        }

    }
}