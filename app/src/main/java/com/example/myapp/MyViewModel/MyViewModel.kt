package com.example.myapp.MyViewModel

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.myapp.MusicRecylerView.MyAdapter2
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicRecylerView.Songs_FireBase
import com.example.myapp.StorageReferenceSingleton
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference

class MyViewModel : ViewModel {


    private var livesongsFirebase: MutableLiveData<ArrayList<Songs_FireBase>>? = null
    private var liveListResult:MutableLiveData<ListResult>?=null
    private var liveSongList:MutableLiveData<ArrayList<Songs>>?=null

    private var songsFirebase: ArrayList<Songs_FireBase>? = null
    private var songsList:ArrayList<Songs>?=null

    //we use CON_NUM argument here
    constructor(playlist: String, folder: String/*,reference: StorageReference*/) {


        //getting StorageReference instance from StorageRefernceSinglton Class
        var mrefernceSingleton = StorageReferenceSingleton().getStroageReference()

        Log.d("djfgd", ".vjjdf")

        songsFirebase = ArrayList()
        livesongsFirebase = MutableLiveData<ArrayList<Songs_FireBase>>()


        mrefernceSingleton?.child(playlist)?.child(folder)?.listAll()
            ?.addOnSuccessListener(object : OnSuccessListener<ListResult> {
                override fun onSuccess(listResult: ListResult?) {
                    listResult?.items?.forEach {
                        Log.d("JKDHFD", it.name)
                        songsFirebase?.add(Songs_FireBase(it.metadata, it.downloadUrl))

                    }
                    livesongsFirebase?.value = songsFirebase
                }
            })?.addOnFailureListener {
                Log.d("FIREBASEexception",it.message+"\n"+it.localizedMessage+"\n"+it.cause+"\n"+it.printStackTrace())
            }
        //getting listAll() list
        liveListResult=MutableLiveData()
        mrefernceSingleton?.child(playlist)?.child(folder)?.listAll()?.addOnSuccessListener(object : OnSuccessListener<ListResult>{
            override fun onSuccess(it: ListResult?) {
                liveListResult?.value=it
            }
        })


        // }
    }





    public fun getLiveList(): LiveData<ArrayList<Songs_FireBase>>?
    {
        return livesongsFirebase
    }
    fun getLiveListResult():LiveData<ListResult>?
    {
        return liveListResult
    }


}