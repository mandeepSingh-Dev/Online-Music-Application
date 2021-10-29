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

class MyViewModel2 : ViewModel {

    private var liveUri:MutableLiveData<Uri>?=null
    private var liveListResult:MutableLiveData<ListResult>?=null

    //we use CON_NUM argument here
    constructor(playlist: String, folder: String,artistImageName:String/*,reference: StorageReference*/) {


        //getting StorageReference instance from StorageRefernceSinglton Class
        var mrefernceSingleton = StorageReferenceSingleton().getStroageReference()
        liveUri=MutableLiveData<Uri>()

        Log.d("djfgd", "folder = "+folder)
        liveListResult=MutableLiveData()
        mrefernceSingleton?.child(playlist)?.child(folder)?.listAll()?.addOnSuccessListener(object : OnSuccessListener<ListResult>{
            override fun onSuccess(it: ListResult?) {
                liveListResult?.value=it
                var list=it?.items
                 for(i in 0..list?.size?.minus(1)!!)
                 {
                  var name = list.get(i).name
                     if(name.endsWith(".jpg"))
                     {
                         Log.d("HelloJPGG",name)
                     }
                 }

            }
        })



    }

    public fun getLiveUri():LiveData<Uri>?
    {
        return liveUri
    }
    fun getLiveListResult():LiveData<ListResult>?
    {
        return liveListResult
    }

}