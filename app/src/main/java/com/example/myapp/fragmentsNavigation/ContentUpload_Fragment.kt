package com.example.myapp.fragmentsNavigation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicService
import com.example.myapp.R
import com.example.myapp.databinding.FragmentContentUploadBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File


class ContentUpload_Fragment : Fragment()
{
    var binding:FragmentContentUploadBinding?=null

    var songName:String="Null Name"
    var artistName="Null Artist"
    var songUri:Uri?=null
    var duration:Long=44564545
    var songSize:Long=89787887
    var bitmap:Bitmap?=null

    var defaultUri = "content://media/external_primary/images/media/457"

    var storage:FirebaseStorage?=null
    var mRefernce:StorageReference?=null

    //using RegisterForActivityResult
    var contract: ActivityResultContract<Intent,Uri> = object: ActivityResultContract<Intent,Uri>() {
        override fun createIntent(context: Context, input: Intent?): Intent {
            return input!!
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri
        {
            var uri1:Uri?
            if(intent?.data!=null)
            {
                uri1=intent.data
            }
            else{
                uri1=Uri.parse(defaultUri)
            }
            return uri1!!
        }
    } //ActivityForResultContract finish here
    @RequiresApi(Build.VERSION_CODES.Q)
    var luancher:ActivityResultLauncher<Intent> = registerForActivityResult(contract,
        ActivityResultCallback
        {
            Log.d("HOLOLOPOLO",it.toString())
            var resolver=activity?.contentResolver
            var cursor=resolver?.query(it,null,null,null,null,null)
           if(cursor!!.moveToFirst())
           {
               songName=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                artistName=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
               songUri=it  //uri
               duration=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
               songSize=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))

               try {
                   bitmap = resolver?.loadThumbnail(it, Size(180, 180), null)
                   Log.d("kdfhj",bitmap.toString())
                   binding?.songImageUpload?.setImageBitmap(bitmap)

               }catch (e:Exception){}
               }
            binding?.uploadImage?.visibility=View.GONE
            binding?.clickPlus?.visibility=View.GONE


            binding?.cardViewForHidenView?.visibility=View.VISIBLE
            binding?.smallUploadImage?.visibility=View.VISIBLE



        })

    //.............................

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
           binding=FragmentContentUploadBinding.inflate(inflater,container,false)
        storage= FirebaseStorage.getInstance()
        mRefernce=storage?.getReference()

       // return inflater.inflate(R.layout.fragment_content_upload_, container, false)
    return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity?.window?.statusBarColor = resources.getColor(R.color.Orangee, null)
            activity?.window?.navigationBarColor=resources.getColor(R.color.Orangee, null)
        } else {
            activity?.window?.statusBarColor = resources.getColor(R.color.Orangee)
            activity?.window?.navigationBarColor=resources.getColor(R.color.Orangee)

        }

        var arraylist=MusicService.songsList
        CoroutineScope(Dispatchers.Main).launch {

            /*uploadSongs("Trending_Playlist", "Hindi", arraylist)
             uploadSongs("Trending_Playlist", "Punjabi", arraylist)
             uploadSongs("Trending_Playlist", "English", arraylist)

             uploadSongs("Top_Charts", "Punjabi_Top10", arraylist)
             uploadSongs("Top_Charts", "International_Top10", arraylist)
             uploadSongs("Top_Charts", "Hindi_Top10", arraylist)
             uploadSongs("Top_Charts", "US_Top10", arraylist)
             uploadSongs("Top_Charts", "Dance_Top10", arraylist)
             uploadSongs("Top_Charts", "Hindi_90s", arraylist)

             uploadSongs("Moods&Collection", "Romance", arraylist)
             uploadSongs("Moods&Collection", "90s&Early2000s", arraylist)
             uploadSongs("Moods&Collection", "Party", arraylist)
             uploadSongs("Moods&Collection", "Hip-Hop", arraylist)
             uploadSongs("Moods&Collection", "Bhakti", arraylist)
             uploadSongs("Moods&Collection", "Music", arraylist)  //that are enough
            uploadSongs("Moods&Collection", "Heals", arraylist)
            uploadSongs("Moods&Collection", "Workout", arraylist)

            uploadSongs("Featured_Artists", "The_Weekend", arraylist)
            uploadSongs("Featured_Artists", "Diljit Dosanjh", arraylist)
            uploadSongs("Featured_Artists", "Justin Beiber", arraylist)
            uploadSongs("Featured_Artists", "Ammy Virk", arraylist)
            uploadSongs("Featured_Artists", "Kygo", arraylist)
            uploadSongs("Featured_Artists", "Ap Dhillon", arraylist)
            uploadSongs("Featured_Artists", "Camila Cabillo", arraylist)
            uploadSongs("Featured_Artists", "Dilpreet Dhillon", arraylist)
            uploadSongs("Featured_Artists", "Jason Derulo", arraylist)
            uploadSongs("Featured_Artists", "Shivjot", arraylist)
            uploadSongs("Featured_Artists", "Tones and I", arraylist)
            uploadSongs("Featured_Artists", "Gurnaam Bhullar", arraylist)
            uploadSongs("Featured_Artists", "Dua Lipa", arraylist)
            uploadSongs("Featured_Artists", "Maninder Buttar", arraylist)
            uploadSongs("Featured_Artists", "Annie Marie", arraylist)
            uploadSongs("Featured_Artists", "Amrit Maan", arraylist)
            uploadSongs("Featured_Artists", "Post_Malone", arraylist)
            uploadSongs("Featured_Artists", "Neha_Kakkar", arraylist)
            uploadSongs("Featured_Artists", "Kishore Kumar", arraylist)
            uploadSongs("Featured_Artists", "Arijit_Singh", arraylist)

            uploadSongs("Discover", "RETRO", arraylist)
            uploadSongs("Discover", "INDIE", arraylist)
            uploadSongs("Discover", "DANCE", arraylist)
            uploadSongs("Discover", "ROCK", arraylist)
            uploadSongs("Discover", "EDM", arraylist)
            uploadSongs("Discover", "KIDS", arraylist)
            uploadSongs("Discover", "WEDDING", arraylist)
            uploadSongs("Discover", "GHAZAL", arraylist)
*/


        }


        binding?.uploadImage?.setOnClickListener {
            var i= Intent()
            i.setType("audio/*")
            i.setAction(Intent.ACTION_PICK )
            luancher.launch(i)

        }
        binding?.smallUploadImage?.setOnClickListener {
            var i= Intent()
            i.setType("audio/*")
            i.setAction(Intent.ACTION_PICK )
            luancher.launch(i)
        }

        binding?.uploadButton?.setOnClickListener {
            var metaDataBuilder=StorageMetadata.Builder()
          var metaDat=metaDataBuilder.setCustomMetadata("Name",songName).build()

            mRefernce?.child("New Playlist")?.child("New Folder")?.putFile(songUri!!,metaDat)?.addOnSuccessListener {
                Log.d("YoPo",it.toString()+"HOGYA")
            }?.addOnProgressListener {
                binding?.cardViewForHidenView?.visibility=View.VISIBLE

                var doublee=(100*it.bytesTransferred)/it.totalByteCount
                binding?.progressTextViewUpload?.setText("${doublee.toInt()}%")
                binding?.progressBarUpload?.setProgress(doublee.toInt())

                binding?.uploadButton?.visibility=View.GONE
                binding?.progressBarUpload?.visibility=View.VISIBLE
                binding?.progressTextViewUpload?.visibility=View.VISIBLE

            }?.addOnSuccessListener {
                binding?.uploadButton?.visibility=View.VISIBLE
                binding?.cardViewForHidenView?.visibility=View.VISIBLE

                binding?.progressBarUpload?.visibility=View.VISIBLE
                binding?.progressTextViewUpload?.visibility=View.VISIBLE


            }
        }

    }  //onViewCreated finshes


    suspend fun uploadSongs(playlist:String,folder:String,songsList:ArrayList<Songs>)= withContext(Dispatchers.Default)
    {
        for(i in 0..songsList.size)
        {
            if(i>=15)
            {}
            else {
                try {
                    var songs=songsList.get(i)
                    var songName=songs.songName
                    var artist=songs.artist
                    var duration=songs.duration
                    var songuri=songs.songuri
                    var songSize=songs.songSize
                    var bitmap=songs.bitmap


                    var metaDataBuilder = StorageMetadata.Builder()
                    var songUri: Uri = songsList.get(i).songuri
                    metaDataBuilder.setCustomMetadata("SongName", songName)
                    metaDataBuilder.setCustomMetadata("Artist",artist)
                    metaDataBuilder.setCustomMetadata("Duration",duration.toString()
                    )
                    metaDataBuilder.setCustomMetadata("Uri",songuri.toString())

                    //

                   /* if (bitmap)
                    {
                        metaDataBuilder.setCustomMetadata("Bitmap", songsList.get(i).bitmap.toString()
                        )
                    } else
                    {
                        metaDataBuilder.setCustomMetadata("Bitmap", "bitmap")
                    }*/
                    try{
                        if(bitmap!=null) {
                            var bitmapstring = encodeBitmapToString(bitmap)
                            metaDataBuilder.setCustomMetadata("Bitmap", bitmapstring)
                        }else
                        {
                            metaDataBuilder.setCustomMetadata("Bitmap", "bitmap")
                        }
                    }catch (e:Exception){}

                    metaDataBuilder.setCustomMetadata("Size", songSize.toString())

                    var metaData = metaDataBuilder.build()

                    mRefernce?.child(playlist)?.child(folder)?.child(songsList.get(i).songName)?.putFile(songUri, metaData)?.addOnProgressListener {
                        var double=(100*it.bytesTransferred)/it.totalByteCount

                        binding?.progressBarUpload?.visibility=View.VISIBLE
                        binding?.progressTextViewUpload?.visibility=View.VISIBLE

                        binding?.songNameUpload?.setText("$playlist-->$folder-->$songName")
                        binding?.progressBarUpload?.setProgress(double.toInt())
                        binding?.progressTextViewUpload?.setText("${double.toInt()} %")

                    }

                }catch (e:Exception){}
                }
            }
        }

    suspend fun uploadSongs(playlist:String,folder:String,songsList:ArrayList<Songs>,view:View)= withContext(Dispatchers.Default)
    {
       var songs:Songs=songsList.get(0)
        var bitmap=songs.bitmap
        var songName=songs.songName
        var songUriii=songs.songuri

       /* var byteArrayOutputStream=ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)

        var bitmapString=Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT)*/

        var bitmapString=encodeBitmapToString(bitmap) //encode bitmap to string usign Base64

        var builder=StorageMetadata.Builder()
        builder.setCustomMetadata("Bitmap",bitmapString)
        var metaData=builder.build()


        mRefernce?.child(playlist)?.child(folder)?.putFile(songUriii,metaData)?.addOnSuccessListener {
            Log.d("OPKFIEUF","UPLOADED")
        }
    }

    suspend fun encodeBitmapToString(bitmap:Bitmap):String= withContext(Dispatchers.Default)
    {

        var bitmapString:String?=null
        try {
            var byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

            bitmapString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        }catch (e:java.lang.Exception){}
        return@withContext bitmapString!!
    }


}
    

