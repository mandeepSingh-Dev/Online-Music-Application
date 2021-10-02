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
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicService
import com.example.myapp.R
import com.example.myapp.databinding.FragmentContentUploadBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import android.app.Activity
import android.media.MediaPlayer
import androidx.activity.OnBackPressedCallback
import kotlinx.coroutines.*
import java.time.Duration


class ContentUpload_Fragment : Fragment()
{
    var binding:FragmentContentUploadBinding?=null

    var songName:String="Null Name"
    var artistName="Null Artist"
    var songUri:Uri?=null
    var duration:Long=44564545
    var songSize:Long=89787887
    var bitmap:Bitmap?=null

    lateinit var bottomDialog:BottomSheetDialog

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
            //FOR  ABOVE OR EQUAL ANDROID Q
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                var resolver = activity?.contentResolver
                var cursor = resolver?.query(it, null, null, null, null, null)
                if (cursor!!.moveToFirst()) {
                    songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    songUri= it  //uri
                    duration =cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    songSize =cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))

                    try {
                        bitmap = resolver?.loadThumbnail(it, Size(180, 180), null)
                        Log.d("kdfhj", bitmap.toString())
                        binding?.songImageUpload?.setImageBitmap(bitmap)

                    } catch (e: Exception) {
                    }
                }
            }
            //FOR  BELOW ANDROID Q
            else{
                var retriever:MediaMetadataRetriever= MediaMetadataRetriever()
                retriever.setDataSource(requireContext(),it)
                songName=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)!!
                artistName=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)!!
                songUri=it
                //TODO duration and songSize isnot getting correct Long datatype form.Correct it..
                duration= retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
                songSize= retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_EXIF_LENGTH)!!.toLong()

                Log.d("hello",retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong().toString())
                Log.d("hello",retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_EXIF_LENGTH)!!.toLong().toString())

                // duration=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
               // songSize=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_EXIF_LENGTH)!!.toLong()
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

//if(Build.VERSION.SDK_INT<Build.VERSION_CODES.P) {
    binding?.uploadImage?.setOnClickListener {

        var i = Intent()
        i.setType("audio/*")
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
            i.setAction(Intent.ACTION_PICK)
        }
        else {
            i.setAction(Intent.ACTION_GET_CONTENT)
        }
        luancher.launch(i)
        //startActivityForResult(i, 100)

    }
//}

            binding?.smallUploadImage?.setOnClickListener {
                var i = Intent()
                i.setType("audio/*")
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                    i.setAction(Intent.ACTION_PICK)
                }
                else {
                    i.setAction(Intent.ACTION_GET_CONTENT)
                }
                luancher.launch(i)
        }

        binding?.uploadButton?.setOnClickListener {
            showBottomSheetDialog()
        }

        gettingSongsfromFireBase("Trending_Playlist","English")




    }  //onViewCreated finshes


    //for Uploading songs in bunch to firebase Storage
  /* suspend fun uploadSongs(playlist:String,folder:String,songsList:ArrayList<Songs>)= withContext(Dispatchers.Default)
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

                   *//**//* if (bitmap)
                    {
                        metaDataBuilder.setCustomMetadata("Bitmap", songsList.get(i).bitmap.toString()
                        )
                    } else
                    {
                        metaDataBuilder.setCustomMetadata("Bitmap", "bitmap")
                    }*//**//*
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
*/
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

   suspend fun  uploadASong(playlist:String,folder:String)= withContext(Dispatchers.Default)
       {
           var metaDataBuilder = StorageMetadata.Builder()
           metaDataBuilder.setCustomMetadata("SongName", songName)
           metaDataBuilder.setCustomMetadata("Artist", artistName)
           metaDataBuilder.setCustomMetadata("Bitmap", encodeBitmapToString(bitmap!!))
           metaDataBuilder.setCustomMetadata("Duration",duration.toString())
           metaDataBuilder.setCustomMetadata("Size",songSize.toString())
                   metaDataBuilder.setCustomMetadata("Uri",songUri.toString())
           val metaData=metaDataBuilder.build()


           mRefernce?.child(playlist)?.child(folder)?.child(songName)?.putFile(songUri!!, metaData)
               ?.addOnSuccessListener {
                   Log.d("YoPo", it.toString() + "HOGYA")
               }?.addOnProgressListener {
               binding?.cardViewForHidenView?.visibility = View.VISIBLE

               var doublee = (100 * it.bytesTransferred) / it.totalByteCount
               binding?.progressTextViewUpload?.setText("${doublee.toInt()}%")
               binding?.progressBarUpload?.setProgress(doublee.toInt())

               binding?.uploadButton?.visibility = View.GONE
               binding?.progressBarUpload?.visibility = View.VISIBLE
               binding?.progressTextViewUpload?.visibility = View.VISIBLE

           }?.addOnSuccessListener {
               binding?.uploadButton?.visibility = View.VISIBLE
               binding?.cardViewForHidenView?.visibility = View.VISIBLE

               binding?.progressBarUpload?.visibility = View.VISIBLE
               binding?.progressTextViewUpload?.visibility = View.VISIBLE


           }


       } //function finished

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

    fun showBottomSheetDialog() {
        bottomDialog = BottomSheetDialog(requireContext(), R.style.Theme_Design_BottomSheetDialog)
        val bottomview = layoutInflater.inflate(
            R.layout.bottom_sheet_for_contentupload,
            activity?.findViewById(R.id.mainConstraintLayout),
            false
        )
        bottomDialog.setContentView(bottomview)

        bottomDialog.show()

              uploadOnItemSelected(bottomDialog,"Trending_Playlist")
             uploadOnItemSelected(bottomDialog,"Top_Charts")
             uploadOnItemSelected(bottomDialog,"Discover")
             uploadOnItemSelected(bottomDialog,"Moods&Collection")
             uploadOnItemSelected(bottomDialog,"Featured_Artists")

    }

        fun uploadOnItemSelected(bottomSheetDialog: BottomSheetDialog, playlist: String)
       {
            when (playlist) {
                "Trending_Playlist" -> {

                    val trendingspinner =
                        bottomSheetDialog.findViewById<Spinner>(R.id.TrendingSpinner)
                    trendingspinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
                            ) {
                                Log.d("hello", parent?.selectedItem.toString())
                                if (parent?.selectedItem?.equals(playlist) == true)  //whenever we open bottom
                                // sheet then default selected item will be 1st mean Trending
                                {
                                } else {
                                    if (parent?.selectedItem?.equals(playlist) == true) {
                                    } else {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            uploadASong(playlist, parent?.selectedItem.toString())
                                        }
                                    }
                                    val itemName: String = parent?.selectedItem.toString()
                                    bottomSheetDialog.hide()
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                }
                "Top_Charts" -> {
                    val topchartsSpinner =
                        bottomSheetDialog.findViewById<Spinner>(R.id.topChartsSpinner)
                    topchartsSpinner?.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                Log.d("hello", parent?.selectedItem.toString())
                                if (parent?.selectedItem?.equals(playlist) == true)  //whenever we open bottom
                                // sheet then default selected item will be 1st mean Trending
                                {
                                } else {
                                    if (parent?.selectedItem?.equals(playlist) == true) {
                                    } else {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            uploadASong(playlist, parent?.selectedItem.toString())
                                        }
                                    }
                                    val itemName: String = parent?.selectedItem.toString()
                                    bottomSheetDialog.hide()
                                }
                                if (parent?.selectedItem?.equals("English") == true) {
                                    Toast.makeText(
                                        requireContext(),
                                        parent?.selectedItem?.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    bottomSheetDialog.hide()
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                }
                "Discover" -> {
                    val DiscoverSpinner =
                        bottomSheetDialog.findViewById<Spinner>(R.id.discoverSpinner)
                    DiscoverSpinner?.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                Log.d("hello", parent?.selectedItem.toString())
                                if (parent?.selectedItem?.equals(playlist) == true)  //whenever we open bottom
                                // sheet then default selected item will be 1st mean Trending
                                {
                                } else {
                                    if (parent?.selectedItem?.equals(playlist) == true) {
                                    } else {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            uploadASong(playlist, parent?.selectedItem.toString())
                                        }
                                        }
                                    val itemName: String = parent?.selectedItem.toString()
                                    bottomSheetDialog.hide()
                                }
                                if (parent?.selectedItem?.equals("English") == true) {
                                    Toast.makeText(
                                        requireContext(),
                                        parent?.selectedItem?.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    bottomSheetDialog.hide()
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                }
                "Moods&Collection" -> {
                    val MoodsSpinner = bottomSheetDialog.findViewById<Spinner>(R.id.MoodsSpinner)
                    MoodsSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                Log.d("hello", parent?.selectedItem.toString())
                                if (parent?.selectedItem?.equals(playlist) == true)  //whenever we open bottom
                                // sheet then default selected item will be 1st mean Trending
                                {   Log.d("SPINNER",parent?.selectedItem.toString()+"1")
                                } else {
                                    if (parent?.selectedItem?.equals(playlist) == true) {
                                        Log.d("SPINNER",parent?.selectedItem.toString()+"2")

                                    } else {
                                        Log.d("SPINNER",parent?.selectedItem.toString()+"3")
                                        CoroutineScope(Dispatchers.Main).launch {
                                            uploadASong("Moods&Collection", parent?.selectedItem.toString())
                                        }
                                        }
                                    val itemName: String = parent?.selectedItem.toString()
                                    bottomSheetDialog.hide()
                                }
                                if (parent?.selectedItem?.equals("English") == true) {
                                    Toast.makeText(
                                        requireContext(),
                                        parent?.selectedItem?.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    bottomSheetDialog.hide()
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                }
                "Featured_Artists" -> {
                    val featuredArtistSpinner =
                        bottomSheetDialog.findViewById<Spinner>(R.id.featuredArtistSpinner)
                    featuredArtistSpinner?.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                Log.d("hello", parent?.selectedItem.toString())
                                if (parent?.selectedItem?.equals(playlist) == true)  //whenever we open bottom
                                // sheet then default selected item will be 1st mean Trending
                                {
                                } else {
                                    if (parent?.selectedItem?.equals(playlist) == true) {
                                    } else {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            uploadASong(playlist, parent?.selectedItem.toString())
                                        }
                                        }
                                    val itemName: String = parent?.selectedItem.toString()
                                    bottomSheetDialog.hide()
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                }
            }


        }

    /*override fun onStop() {
        super.onStop()
        bottomDialog.hide()
    }*/
    fun gettingSongsfromFireBase(playlist: String,folder: String)
    {
        mRefernce?.child(playlist)?.child(folder)?.listAll()?.addOnSuccessListener {

            for(item in it.items)
            {
              item.metadata.addOnSuccessListener {
                  songName=it.getCustomMetadata("SongName").toString()
                  artistName=it.getCustomMetadata("Artist").toString()
                  bitmapStr=it.getCustomMetadata("Bitmap").toString()
                  songSizeStr=it.getCustomMetadata("Size").toString()
                  durationStr=it.getCustomMetadata("Duration").toString()
                  songUriStr=it.getCustomMetadata("Uri").toString()

                  Log.d("HEJKKGO",it.getCustomMetadata("SongName").toString())

               }

           }
            }
        }
    lateinit var bitmapStr:String
    lateinit var songSizeStr:String
    lateinit var durationStr:String
    lateinit var songUriStr:String
    }









    

