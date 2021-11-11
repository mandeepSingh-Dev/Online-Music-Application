package com.example.onlinemusicapp

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StorageReferenceSingleton
{
    private var storage:FirebaseStorage?=null
    private var mRefernce:StorageReference?=null

    init {
        storage= FirebaseStorage.getInstance()
        mRefernce=storage?.getReference()
    }

    public fun getStroageReference(): StorageReference {
        return mRefernce!!
    }

}