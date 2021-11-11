package com.example.onlinemusicapp.MusicRecylerView

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageMetadata

data class Songs_FireBase(val storageMetadataa: Task<StorageMetadata>,val downloadedUri:Task<Uri>):Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("storageMetadataa"),
        TODO("downloadedUri")
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Songs_FireBase> {
        override fun createFromParcel(parcel: Parcel): Songs_FireBase {
            return Songs_FireBase(parcel)
        }

        override fun newArray(size: Int): Array<Songs_FireBase?> {
            return arrayOfNulls(size)
        }
    }
}