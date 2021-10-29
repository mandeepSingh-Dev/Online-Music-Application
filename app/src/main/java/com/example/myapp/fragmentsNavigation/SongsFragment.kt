package com.example.myapp.fragmentsNavigation

import android.content.*
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.media.MediaCodec.MetricsConstants.MODE
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.myapp.MusicRecylerView.MyAdapter
import com.example.myapp.MusicRecylerView.MyAdapter2
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicRecylerView.Songs_FireBase
import com.example.myapp.MyViewModel.MyViewModel
import com.example.myapp.MyViewModel.MyViewModelFactory
import com.example.myapp.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.StorageReferenceSingleton
import android.os.Parcelable
import com.example.myapp.databinding.FragmentSongsBinding


class SongsFragment : Fragment() {

    var con: Boolean? = null
    private var songsList: ArrayList<Songs>? = null
    var recylrView: RecyclerView? = null
    var adpter:MyAdapter?=null

    var addpter:MyAdapter2?=null

    var songsFireList:ArrayList<Songs_FireBase>?=null

    var toolbarimageview:ImageView?=null
    var toolbarimageview2:ImageView?=null
    var toolbarimageview3:ImageView?=null
    var toolbarimageview4:ImageView?=null
    var toolbar:Toolbar?=null
    var lottie:LottieAnimationView?=null
    var lottie2:LottieAnimationView?=null

    var storage: FirebaseStorage? = null
    var mReference: StorageReference? = null
    var songName:String?=null
    var artistName:String?=null
    var  bitmapStr:String?=null
    var songSizeStr:String?=null
    var durationStr:String?=null
    var songUriStr:String?=null
    var creationDate:Long?=null
    var duration:Long?=null
    var size:Long?=null
    var uri: Uri?=null
    var bitmap:Bitmap?=null
    var binding:FragmentSongsBinding?=null

    var playlist:String?=null
    var folder:String?=null







    var receiver=object: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent?.action.equals("SENDING_BITMAPSTR"))
        {
                var bitmapstrr=intent?.getStringExtra("BITMAPSTR")

            CoroutineScope(Dispatchers.Main).launch {
                var bitmap = convertToBitmap(bitmapstrr)
                toolbarimageview?.setImageBitmap(bitmap)
                toolbarimageview2?.setImageBitmap(bitmap)
                toolbarimageview3?.setImageBitmap(bitmap)
                toolbarimageview4?.setImageBitmap(bitmap)
            }


        }
        else if(intent?.action.equals("SENDING_FIRENAME"))
        {
           /* Log.d("kfgjkfjkjfk","OOOPS"+intent?.getStringExtra("metasongName"))
            var str=intent?.getStringExtra("metasongName")
            songsList?.add(Songs(str))

            //Log.d("LIISTDFDFD",songsList?.get(0)?.songName!!)

            var sognfirelist:ArrayList<Songs_FireBase> = intent?.getSerializableExtra("songFirebaseList") as ArrayList<Songs_FireBase>
                Log.d("sizeRECIERFIRELIST",songsFireList?.size.toString()+"f;blhf")*/
        }

    }

}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Log.d("Hellodjd",savedInstanceState?.getString("HELLO").toString())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        songsList = ArrayList<Songs>()
        songsFireList= ArrayList()
        storage = FirebaseStorage.getInstance()

        binding = FragmentSongsBinding.inflate(inflater, container, false)

         LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, IntentFilter("SENDING_BITMAPSTR"))
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    var sharedPreferences:SharedPreferences?=null
    var editor:SharedPreferences.Editor?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       var animation = AnimationUtils.loadAnimation(context, R.anim.opening_anim)
        var rootLayout=view.findViewById<CoordinatorLayout>(R.id.songFragmentRootLay)
        rootLayout.animation=animation

       lottie=view.findViewById(R.id.lottie_songs_fragment)
        //initialise storageRefernce here..
        mReference= StorageReferenceSingleton().getStroageReference()

        recylrView = view?.findViewById<RecyclerView>(R.id.songsRecyclerView)
        toolbarimageview = view.findViewById(R.id.songImageOnToolbar1)
        toolbarimageview2 = view.findViewById(R.id.songImageOnToolbar2)
        toolbarimageview3 = view.findViewById(R.id.songImageOnToolbar3)
        toolbarimageview4 = view.findViewById(R.id.songImageOnToolbar4)

        toolbar = view.findViewById<Toolbar>(R.id.toolbarSongsFragment)


       // recylrView?.scrollToPosition(position!!)

         sharedPreferences=activity?.getSharedPreferences("SHAREE",Context.MODE_PRIVATE)
         editor=sharedPreferences?.edit()
        editor?.putInt("INTT",87)

        //getting recyclerview current state position to resume this list when come back to this list
        recylrView?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)
                editor?.putInt("STATeee",dy)


            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                editor?.putInt("STATEE",newState)

            }
        })
      //  mlistState2=savedInstanceState?.getParcelable("LISTSTATE")
        //Log.d("jdjf",mlistState2.toString())

        if (arguments != null) {
             playlist = arguments?.get("Playlist").toString()
             folder = arguments?.getString("Folder").toString()
            toolbar?.setTitle(folder)

            CoroutineScope(Dispatchers.Main).launch {
               // gettingSongsListfromFireBase(playlist, folder)
            }
        }
        else{
            toolbar?.setTitle("Music")
        }

        var factory:MyViewModelFactory= MyViewModelFactory(playlist!!,folder!!/*,mReference!!*/)
        var myviewmodel=ViewModelProvider(this,factory).get(MyViewModel::class.java)


       /*Log.d("VIEWWWMODEL",*/myviewmodel.getLiveList()?.observe(viewLifecycleOwner,Observer {
             it.get(0).storageMetadataa.addOnCompleteListener {
               it.addOnSuccessListener {
                   Log.d("HELLOJBJHDF",it.getCustomMetadata("SongName").toString())

               }
           }
            addpter=MyAdapter2(context,it)
            recylrView?.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
            recylrView?.adapter = addpter


       })






    } //onViewCreated Finished




    suspend fun gettingSongsListfromFireBase(playlist: String ="Trending_Playlist", folder: String="English")= withContext(Dispatchers.Main)
    {
        //initStorageReference(storage!!) //initialization of mReference


        mReference?.child(playlist)?.child(folder)?.listAll()?.addOnSuccessListener(object:OnSuccessListener<ListResult> {
            override fun onSuccess(listResult: ListResult?) {
                listResult?.items?.forEach{
                    songsFireList?.add(Songs_FireBase(it.metadata, it.downloadUrl))
                  //  Log.d("SIFIRE", songsFireList?.size.toString())
                   // intent.putExtra("songFirebaseList", songsFireList)
                   // manager.sendBroadcast(intent)
                    }
                lottie?.visibility=View.GONE
                lottie2?.visibility=View.GONE


                addpter=MyAdapter2(context,songsFireList)
                recylrView?.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
                recylrView?.adapter = addpter

            }
        })

    } //gettingSon.....function closed

   suspend fun convertToBitmap(bitmapStr: String?): Bitmap?= withContext(Dispatchers.Default) {
        var bitmap1:Bitmap?=null
        try {
           // Log.d("bitMAPSTR",bitmapStr!!)
            val byteArray = Base64.decode(bitmapStr!!, Base64.DEFAULT)
             bitmap1= BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            Log.d("BITMMAP",bitmap1.toString())
        }catch (e:Exception){}
        Log.d("HHELO",bitmap1.toString())
    return@withContext bitmap1
   }

    //globally declared
    private var mlistState:Parcelable?=null
    private var mlistState2:Parcelable?=null
   /* override fun onSaveInstanceState(outState: Bundle)
    {  super.onSaveInstanceState(outState)

      //  mlistState=recylrView?.layoutManager?.onSaveInstanceState()
       // outState.putParcelable("LISTSTATE",mlistState)
recylrView?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
    {
        super.onScrolled(recyclerView, dx, dy)
        Log.d("JJKJG",dy.toString())
        outState.putString("HELLO",dy.toString())

    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        outState.putString("HEL",newState.toString())

    }
})
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
          *//*  mlistState2=savedInstanceState?.getParcelable("LISTSTATE")
            Log.d("FJGKJF",mlistState2.toString())

            recylrView?.getLayoutManager()?.onRestoreInstanceState(mlistState2)*//*
            Log.d("DJFKDJFKD",savedInstanceState.getString("HEL").toString())
        }    }*/






}