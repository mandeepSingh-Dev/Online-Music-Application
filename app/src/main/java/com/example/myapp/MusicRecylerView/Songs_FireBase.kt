package com.example.myapp.MusicRecylerView

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageMetadata

data class Songs_FireBase(val storageMetadataa: Task<StorageMetadata>,val downloadedUri:Task<Uri>) {
}