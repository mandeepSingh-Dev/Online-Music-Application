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
import android.os.IBinder
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
import com.example.myapp.MusicService
import com.example.myapp.MusicService.MyBinder
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

    var sharedPreferences:SharedPreferences?=null
    var editor:SharedPreferences.Editor?=null
    var localBroadcastManager:LocalBroadcastManager?=null
    var songsfirelist:ArrayList<Songs>?=null

    companion object{
        var staticSonglistFirebase:ArrayList<Songs_FireBase>?=null
    }

    lateinit var musicService:MusicService

    //getting MusicService instance by connecting this class to MusicService class
    var serviceConnection=object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("KPKPKP", "onServiceConnected 101")
            val myBinder = service as MyBinder
            musicService = myBinder.service
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("KPKPKP", "onServiceDisconnected 110")
        }
    }

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
        staticSonglistFirebase=ArrayList()
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

        if (arguments != null) {
             playlist = arguments?.get("Playlist").toString()
             folder = arguments?.getString("Folder").toString()
            toolbar?.setTitle(folder)

            CoroutineScope(Dispatchers.Main).launch {
               // gettingSongsListfromFireBase(playlist, folder)
           gettingSongsListfromFireBase(playlist!!,folder!!)
            }

        }
        else{
            toolbar?.setTitle("Music")
        }

    } //onViewCreated Finished

    suspend fun gettingSongsListfromFireBase(playlist: String ="Trending_Playlist", folder: String="English")= withContext(Dispatchers.Main)
    {
        localBroadcastManager= LocalBroadcastManager.getInstance(requireContext())
        var i=Intent("Send_SongsList")
        //initStorageReference(storage!!) //initialization of mReference
        var factory:MyViewModelFactory= MyViewModelFactory(playlist!!,folder!!/*,mReference!!*/)
        var myviewmodel=ViewModelProvider(this@SongsFragment,factory).get(MyViewModel::class.java)


        myviewmodel.getLiveList()?.observe(viewLifecycleOwner,Observer {

            lottie?.visibility=View.GONE
            lottie2?.visibility=View.GONE
            staticSonglistFirebase=it
            //putting arraylist and sending to Music Service
            i.putParcelableArrayListExtra("songslIst",it)
            localBroadcastManager?.sendBroadcast(i)

            addpter=MyAdapter2(context,it)
            recylrView?.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
            recylrView?.adapter = addpter


            addpter?.setOnClickListener(object:MyAdapter2.CustomItemClickListener2{
                override fun customOnItemClick(position: Int) {
                    var i2=Intent("FIREPOSITION")
                    i2.putExtra("positionfire",position)
                    localBroadcastManager?.sendBroadcast(i2)
                }
            })


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

    override fun onResume() {
        super.onResume()
        var intent=Intent(context,MusicService::class.java)
        activity?.bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE)
    }
}