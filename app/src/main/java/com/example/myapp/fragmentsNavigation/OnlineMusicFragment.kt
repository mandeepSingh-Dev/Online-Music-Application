package com.example.myapp.fragmentsNavigation

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapp.MusicRecylerView.MyAdapter3
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicRecylerView.Songs_FireBase
import com.example.myapp.MusicService
import com.example.myapp.MyViewModel.*
import com.example.myapp.R
import com.example.myapp.StorageReferenceSingleton
import com.example.myapp.databinding.FolderFeaturedArtistLayoutBinding
import com.example.myapp.databinding.FolderLayoutNormalBinding
import com.example.myapp.databinding.FragmentOnlineMusicBinding
import com.example.myapp.databinding.ListTrendingLayoutBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.util.jar.JarOutputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class OnlineMusicFragment : Fragment()  {

    //view binding
    private var _binding: FragmentOnlineMusicBinding? = null

    var storage: FirebaseStorage? = null
    var mRefernce: StorageReference? = null

    //trending Punjabi Arraylist
    var trendingPunjabiArraylist: ArrayList<Songs>? = null
    var trendingEnglishArraylist: ArrayList<Songs>? = null
    var trendingHindiArraylist: ArrayList<Songs>? = null

    var navController:NavController?=null
    var navigationView:NavigationView?=null
    var drawerLayout: DrawerLayout? = null
    var sharedPrefrence:SharedPreferences?=null
    var editor:SharedPreferences.Editor?=null

    var sharedPrefArtist:SharedPreferences?=null
    var editorArtists:SharedPreferences.Editor?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_online_music, container, false)
        _binding = FragmentOnlineMusicBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        //getting storage INstance and refernce from storage..
        storage = FirebaseStorage.getInstance()
        mRefernce = StorageReferenceSingleton().getStroageReference()



        //set color of status bar..
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity?.window?.statusBarColor = resources.getColor(R.color.Mint_green, null)
        } else {
            activity?.window?.statusBarColor = resources.getColor(R.color.Mint_green)

        }

        navigationView=activity?.findViewById(R.id.navdrawer)
        navigationView?.setBackgroundColor(Color.WHITE)
       // onNavigationItemClickListened()
      //  drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer)

        //getting arraylist of local(device) songs from Music Service..

        //getting nav controller to navigate to other fragments..
        navController = Navigation.findNavController(view)
        sharedPrefrence=activity?.getSharedPreferences("SHAREDD", Context.MODE_PRIVATE)
        editor=sharedPrefrence?.edit()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            _binding?.scrollview?.setOnScrollChangeListener(object:View.OnScrollChangeListener{
                override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                    Log.d("jgghghghgd",oldScrollY.toString())
                    editor?.putInt("YPOSITION",oldScrollY)

                }
            })
        }

        _binding?.uploadSongText?.setOnClickListener {
            navController?.navigate(R.id.contentUpload_Fragment)

        }

        _binding?.includeTrendingEnglish?.rootLayoutTrending?.setOnClickListener {
            var bundle =Bundle()
           bundle.putString("Playlist","Trending_Playlist")
           bundle.putString("Folder","English")
           navController?.navigate(R.id.action_onlineMusicFragment_to_songsFragment,bundle)
       }
        _binding?.includeTrendingPunjabi?.rootLayoutTrending?.setOnClickListener {
            var bundle =Bundle()
            bundle.putString("Playlist","Trending_Playlist")
            bundle.putString("Folder","Punjabi")
            navController?.navigate(R.id.action_onlineMusicFragment_to_songsFragment,bundle)
        }
        _binding?.includeTrendingHindi?.rootLayoutTrending?.setOnClickListener {
            var bundle =Bundle()
            bundle.putString("Playlist","Trending_Playlist")
            bundle.putString("Folder","Hindi")
            navController?.navigate(R.id.action_onlineMusicFragment_to_songsFragment,bundle)
        }

        set_Arraylist_ToTrendingLayoutsDUPLICATE(mRefernce!!, "Trending_Playlist", "English",_binding?.includeTrendingEnglish!!)
        set_Arraylist_ToTrendingLayoutsDUPLICATE(mRefernce!!, "Trending_Playlist", "Punjabi", _binding?.includeTrendingPunjabi!!)
        set_Arraylist_ToTrendingLayoutsDUPLICATE(mRefernce!!, "Trending_Playlist", "Hindi", _binding?.includeTrendingHindi!!)

        //sharedPrefernce for setImageToFeaturedArtists
        sharedPrefArtist=activity?.getSharedPreferences("ArtistsShared",Context.MODE_PRIVATE)
        editorArtists=sharedPrefArtist?.edit()

      /*  setImageToFeaturedArtists("Featured_Artists", "Amrit Maan","amrit_maan-6.jpg",_binding?.includeAmritMaan!!,view)
        setImageToFeaturedArtists("Featured_Artists", "Amrit Maan","amrit_maan-6.jpg",_binding?.includeAmritMaan!!,view)
       */// setImageToFeaturedArtists22("Featured_Artists", "Annie Marie","amrit_maan-6.jpg","Amrit Maan")

        //coroutine scope to launch suspend function in it
        CoroutineScope(Dispatchers.IO).launch {}

            //these functions is to set Data in Artist layouts
      setImageToFeaturedArtist( "Featured_Artists","Ammy Virk",_binding?.includeAmmyVirk!!)
      setImageToFeaturedArtist( "Featured_Artists","Amrit Maan",_binding?.includeAmritMaan!!)
      setImageToFeaturedArtist("Featured_Artists","Annie Marie",_binding?.includeAnnieMarie!!)
      setImageToFeaturedArtist( "Featured_Artists","Ap Dhillon",_binding?.includeApdhillon!!)
      setImageToFeaturedArtist( "Featured_Artists","Arijit_Singh",_binding?.includeArijitSingh!!)
      setImageToFeaturedArtist("Featured_Artists","Camila Cabillo",_binding?.includeCamilacabillo!!)
      setImageToFeaturedArtist("Featured_Artists","Diljit Dosanjh",_binding?.includeDiljitDosanjh!!)
      setImageToFeaturedArtist("Featured_Artists","Dilpreet Dhillon",_binding?.includeDilpreetdhillon!!)
      setImageToFeaturedArtist("Featured_Artists","Dua Lipa",_binding?.includeDuaLipa!!)
      setImageToFeaturedArtist("Featured_Artists","Gurnaam Bhullar",_binding?.includeGurnaamBhullar!!)
      setImageToFeaturedArtist("Featured_Artists","Jason Derulo",_binding?.includeJasonDerulo!!)
      setImageToFeaturedArtist("Featured_Artists","Justin Beiber",_binding?.includeJustinBeiber!!)
      setImageToFeaturedArtist("Featured_Artists","Kishore Kumar",_binding?.includeKishoreKumar!!)
      setImageToFeaturedArtist("Featured_Artists","Kygo",_binding?.includeKygo!!)
      setImageToFeaturedArtist("Featured_Artists","Maninder Buttar",_binding?.includeManinderButtar!!)
      setImageToFeaturedArtist("Featured_Artists","Neha_Kakkar",_binding?.includeNehaKakkar!!)
      setImageToFeaturedArtist("Featured_Artists","Post_Malone",_binding?.includePostMalone!!)
      setImageToFeaturedArtist("Featured_Artists","Shivjot",_binding?.includeShivjot!!)
      setImageToFeaturedArtist("Featured_Artists","The_Weekend",_binding?.includeTheWeeknd!!)
      setImageToFeaturedArtist("Featured_Artists","Tones and I",_binding?.includeTonesAndI!!)

/*  setImageToFeaturedArtists("Featured_Artists", "Ammy Virk","ammyvirkimage.jpg","Ammy Virk",_binding?.includeAmmyVirk!!,view)
  setgeToFeaturedArtists("Featured_Artists", "Amrit Maan","amrit_maan-6.jpg","Amrit Maan",_binding?.includeAmritMaan!!,view)
  setImageToFeaturedArtists("Featured_Artists", "Annie Marie","annie marie.jpg","Annie Marie",_binding?.includeAnnieMarie!!,view)
  setImageToFeaturedArtists("Featured_Artists", "Ap Dhillon","AP-dhillon-wallpapers-7.jpg","Ap Dhillon",_binding?.includeApdhillon!!,view)
  setImageToFeaturedArtists("Featured_Artists", "Arijit_Singh","arijit singh.jpg","Arijit Singh",_binding?.includeArijitSingh!!,view)
  setImageToFeaturedArtists("Featured_Artists","Camila Cabillo","camilla cabillo.jpg","Camila Cabillo",_binding?.includeCamilacabillo!!,view)
  setImageToFeaturedArtists("Featured_Artists","Diljit Dosanjh","Diljit-Dosanjh13.jpg","Diljit Dosanjh",_binding?.includeDiljitDosanjh!!,view)
  setImageToFeaturedArtists("Featured_Artists","Dilpreet Dhillon","dilpreet dhilon.jpg","Dilpreet Dhillon",_binding?.includeDilpreetdhillon!!,view)
  setImageToFeaturedArtists("Featured_Artists","Dua Lipa","dua lipa.jpg","Dua Lipa",_binding?.includeDuaLipa!!,view)
  setImageToFeaturedArtists("Featured_Artists","Gurnaam Bhullar","gurnaam bhullar.jpg","Gurnaam Bhullar",_binding?.includeGurnaamBhullar!!,view)
  setImageToFeaturedArtists("Featured_Artists","Jason Derulo","jason derulo.jpg","Jason Derulo",_binding?.includeJasonDerulo!!,view)
  setImageToFeaturedArtists("Featured_Artists","Justin Beiber","justin beiber.jpg","Justin Beiber",_binding?.includeJustinBeiber!!,view)
  setImageToFeaturedArtists("Featured_Artists","Kishore Kumar","kishore_Kumar.jpg","Kishore Kumar",_binding?.includeKishoreKumar!!,view)
  setImageToFeaturedArtists("Featured_Artists","Kygo","kygo.jpg","Kygo",_binding?.includeKygo!!,view)
  setImageToFeaturedArtists("Featured_Artists","Maninder Buttar","maninder.jpg","Maninder Buttar",_binding?.includeManinderButtar!!,view)
  setImageToFeaturedArtists("Featured_Artists","Neha_Kakkar","neha kakkr.jpg","Neha Kakkar",_binding?.includeNehaKakkar!!,view)
  setImageToFeaturedArtists("Featured_Artists","Post_Malone","post malone.jpg","Post Malone",_binding?.includePostMalone!!,view)
  setImageToFeaturedArtists("Featured_Artists","Shivjot","shivjot.jpg","Shivjot",_binding?.includeShivjot!!,view)
  setImageToFeaturedArtists("Featured_Artists","The_Weekend","the weekend.jpg","The Weeknd",_binding?.includeTheWeeknd!!,view)
  setImageToFeaturedArtists("Featured_Artists","Tones and I","tones and i.jpg","Tones and I",_binding?.includeTonesAndI!!,view)
*/     Log.d("YOTHREAD", Thread.currentThread().toString())





Log.d("YOTHREAD", Thread.currentThread().toString())

var arrlist: ArrayList<Songs>? = MusicService.songsList
setDataToRecentPlayedList(arrlist!!, view)
    setDataToNewReleasePlayedList(arrlist!!, view)

//setting Top Charts Folders(include layouts)
setUp_TopChartsFolder(view)
//for setting Top Charts Folders(include layouts)

// CtentResolver resolver=getContext().getContentResolver();
setUp_DiscoverFolders(view)
setUp_MoodFolders(view)
}  //onViewClosed


//function to set data to Recent Played
/*suspend fun*/ fun setDataToRecentPlayedList(arraylist: ArrayList<Songs>, view: View)/*= withContext(Dispatchers.IO)*/ {

val myAdapter3: MyAdapter3 = MyAdapter3(context, arraylist)
// withContext(Dispatchers.Main){
val recycelrView = view.findViewById<RecyclerView>(R.id.Recently_RecylerView)
recycelrView.layoutManager =
    GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
recycelrView.adapter = myAdapter3
// }
}

//function to set data to Trending include layouts
/*suspend*/ fun setDataToNewReleasePlayedList(arraylist: ArrayList<Songs>, view: View)/*= withContext(Dispatchers.IO)*/ {
val myAdapter3: MyAdapter3 = MyAdapter3(context, arraylist)
// withContext(Dispatchers.Main){
val recycelrView = view.findViewById<RecyclerView>(R.id.Recently_RecylerView)
recycelrView.layoutManager =
    GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
recycelrView.adapter = myAdapter3
// }
}


/* //function to setUp Trending folders(include layouts)
fun setUp_TrendingFolder(arrlist: ArrayList<Songs>) {


if (arrlist != null) {
    _binding?.includeTrendingEnglish?.InLanguageTextView?.setText("In English (15)")
    _binding?.includeTrendingEnglish?.songName1?.setText(arrlist.get(0).songName)
    _binding?.includeTrendingEnglish?.songName2?.setText(arrlist.get(1).songName)
    _binding?.includeTrendingEnglish?.songName3?.setText(arrlist.get(2).songName)
    _binding?.includeTrendingEnglish?.songName4?.setText(arrlist.get(3).songName)
    _binding?.includeTrendingEnglish?.songName5?.setText(arrlist.get(4).songName)
}
if (arrlist != null) {
    _binding?.includeTrendingHindi?.InLanguageTextView?.setText("In Hindi (15)")
    _binding?.includeTrendingHindi?.songName1?.setText(arrlist.get(0).songName)
    _binding?.includeTrendingHindi?.songName2?.setText(arrlist.get(1).songName)
    _binding?.includeTrendingHindi?.songName3?.setText(arrlist.get(2).songName)
    _binding?.includeTrendingHindi?.songName4?.setText(arrlist.get(3).songName)
    _binding?.includeTrendingHindi?.songName5?.setText(arrlist.get(4).songName)
}
if (arrlist != null) {
    _binding?.includeTrendingPunjabi?.InLanguageTextView?.setText("In Punjabi (15)")
    _binding?.includeTrendingPunjabi?.songName1?.setText(arrlist.get(0).songName)
    _binding?.includeTrendingPunjabi?.songName2?.setText(arrlist.get(1).songName)
    _binding?.includeTrendingPunjabi?.songName3?.setText(arrlist.get(2).songName)
    _binding?.includeTrendingPunjabi?.songName4?.setText(arrlist.get(3).songName)
    _binding?.includeTrendingPunjabi?.songName5?.setText(arrlist.get(4).songName)
}
}*/

//function to setUp Top Charts folders(include layouts)
fun setUp_TopChartsFolder(view: View) {
_binding?.includeEnglishTop10?.languageTextviewFolder?.setText("English")
_binding?.includePunjabiTop10?.languageTextviewFolder?.setText("Punjabi")
_binding?.includeHindiTop10?.languageTextviewFolder?.setText("Hindi")
_binding?.includeUSTop10?.languageTextviewFolder?.setText("US")
_binding?.includeDanceTop10?.languageTextviewFolder?.setText("Dance")
_binding?.includeHindi90sTop10?.languageTextviewFolder?.setText("Hindi 90s")


foldersOnClickedListened(_binding?.includeDanceTop10!!,"Top_Charts","Dance_Top10")
foldersOnClickedListened(_binding?.includeHindi90sTop10!!,"Top_Charts","Hindi_90s")
foldersOnClickedListened(_binding?.includeHindiTop10!!,"Top_Charts","Hindi_Top10")
foldersOnClickedListened(_binding?.includeEnglishTop10!!,"Top_Charts","International_Top10")
foldersOnClickedListened(_binding?.includePunjabiTop10!!,"Top_Charts","Punjabi_Top10")
foldersOnClickedListened(_binding?.includeUSTop10!!,"Top_Charts","US_Top10")
}
  //navigate to song list ftragment on click top charts folders
fun  foldersOnClickedListened(folderLayoutNormalBinding: FolderLayoutNormalBinding,playlist:String,folder:String)
{
    folderLayoutNormalBinding?.folderLayout.setOnClickListener{
        var bundle =Bundle()
        bundle.putString("Playlist",playlist)
        bundle.putString("Folder",folder)
        navController?.navigate(R.id.action_onlineMusicFragment_to_songsFragment,bundle)
    }

}
fun  foldersArtistOnClickedListened(folderLayoutNormalBinding: FolderFeaturedArtistLayoutBinding,playlist:String,folder:String)
{
folderLayoutNormalBinding?.folderLayout.setOnClickListener{
    var bundle =Bundle()
    bundle.putString("Playlist",playlist)
    bundle.putString("Folder",folder)
    navController?.navigate(R.id.action_onlineMusicFragment_to_songsFragment,bundle)
}

}






//function to setUp Top Charts folders(include layouts)
fun setUp_DiscoverFolders(view: View) {
_binding?.includeRetro?.languageTextviewFolder?.setText("RETRO")
_binding?.includeIndie?.languageTextviewFolder?.setText("INDIE")
_binding?.includeEDM?.languageTextviewFolder?.setText("EDM")
_binding?.includeWedding?.languageTextviewFolder?.setText("WEDDING")
_binding?.includeDance?.languageTextviewFolder?.setText("DANCE")
_binding?.includeRock?.languageTextviewFolder?.setText("ROCK")
_binding?.includeKids?.languageTextviewFolder?.setText("KIDS")
_binding?.includeGhazals?.languageTextviewFolder?.setText("GHAZAL")
}

//function to setUp Moods&Collection folders(include layouts)
fun setUp_MoodFolders(view: View) {
_binding?.includeRomance?.languageTextviewFolder?.setText("Romance")
_binding?.include90sAndEarly?.languageTextviewFolder?.setText("90s & Early 2000s")
_binding?.includeParty?.languageTextviewFolder?.setText("Party")
_binding?.includeHipHop?.languageTextviewFolder?.setText("Hip-Hop")
_binding?.includeBhakti?.languageTextviewFolder?.setText("Bhakti")
_binding?.includeMusic?.languageTextviewFolder?.setText("Music")
_binding?.includeHeals?.languageTextviewFolder?.setText("Heals")
_binding?.includePunjabiHits?.languageTextviewFolder?.setText("Punjabi Top Hits")
_binding?.includeWorkout?.languageTextviewFolder?.setText("Workout")

foldersOnClickedListened(_binding?.includeRomance!!,"Moods&Collection","Romance")
foldersOnClickedListened(_binding?.include90sAndEarly!!,"Moods&Collection","90s&Early2000s")
foldersOnClickedListened(_binding?.includeParty!!,"Moods&Collection","Party")
foldersOnClickedListened(_binding?.includeHipHop!!,"Moods&Collection","Hip-Hop")
foldersOnClickedListened(_binding?.includeBhakti!!,"Moods&Collection","Bhakti")
foldersOnClickedListened(_binding?.includeMusic!!,"Moods&Collection","Music")
foldersOnClickedListened(_binding?.includeHeals!!,"Moods&Collection","Heals")
foldersOnClickedListened(_binding?.includePunjabiHits!!,"Moods&Collection","Workout")
foldersOnClickedListened(_binding?.includeWorkout!!,"Moods&Collection","Workout")


}

//new fundtion
fun set_Arraylist_ToTrendingLayoutsDUPLICATE(refernce: StorageReference, playlistName: String, folderName: String, view: ListTrendingLayoutBinding)/* = withContext(Dispatchers.Default)*/
{
activity?.runOnUiThread {
    view.InLanguageTextView.setText("In "+folderName +"(15)")
}


var viewmodelEnglish:ViewModel_TrendEnglish?=null
var viewmodePunjabi:ViewModel_TrendPunjab?=null
var viewmodelHindi:ViewModel_TrendHindi?=null

if(folderName.equals("English")) {
    viewmodelEnglish = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModel_TrendEnglish::class.java)
    viewmodelEnglish?.getLiveList()?.observe(viewLifecycleOwner, Observer {
         settingTrendPlaylists(it,view)
    })
}
else if(folderName.equals("Punjabi"))
{
   viewmodePunjabi = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModel_TrendPunjab::class.java)
   viewmodePunjabi?.getLiveList()?.observe(viewLifecycleOwner, Observer {
       settingTrendPlaylists(it,view)
  })
}
else{
    viewmodelHindi = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModel_TrendHindi::class.java)
    viewmodelHindi?.getLiveList()?.observe(viewLifecycleOwner, Observer {
          settingTrendPlaylists(it,view)
    })
}

}// function closed here
fun settingTrendPlaylists(it:ArrayList<Songs_FireBase>,view: ListTrendingLayoutBinding)
{
for(i in 0..it.size-1)
{

    //getting five five  songs for every tredning playlist
    it.get(i).storageMetadataa.addOnSuccessListener {
        var songName=it.getCustomMetadata("SongName")!!
        var artist:String=it.getCustomMetadata("Artist")!!
        var bitmapstr:String=it.getCustomMetadata("Bitmap")!!

        val byteArray=Base64.decode(bitmapstr,Base64.DEFAULT)
        val finalBitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)

        when(i)
        {
            0 -> {view.songName1.setText(songName)
                view.artistName1.setText(artist)

                view.songImage1.setImageBitmap(finalBitmap)
            }
            1 -> {
                view.songName2.setText(songName)
                view.artistName2.setText(artist)
                view.songImage2.setImageBitmap( finalBitmap)
            }
            2 -> {
                view.songName3.setText(songName)
                view.artistName3.setText(artist)
                view.songImage3.setImageBitmap( finalBitmap  )
            }
            3 -> {
                view.songName4.setText(songName)
                view.artistName4.setText(artist)
                view.songImage4.setImageBitmap(finalBitmap)
            }
            4 -> {
                view.songName5.setText(songName)
                view.artistName5.setText(artist)
                view.songImage5.setImageBitmap(finalBitmap)
            }
        }
    }
}
}




//this method is for online data for artist folders that are fetched from ViewModels
fun setImageToFeaturedArtist(playlistName: String,artistName:String, artistlayout:FolderFeaturedArtistLayoutBinding)/*= withContext(Dispatchers.Main)*/
{
    if(artistName.equals("Ammy Virk")) {
        var viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelAmmyVirk::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Amrit Maan")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelAmritMaan::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Annie Marie")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelAnnieMarie::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Ap Dhillon")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelApDhillon::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Arijit_Singh")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelArijit::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Camila Cabillo")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelCamila::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Diljit Dosanjh")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelDiljit::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Dilpreet Dhillon")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelDilpreet::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Dua Lipa")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelDuaLipa::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Gurnaam Bhullar")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelGurnaamBhullar::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Jason Derulo")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelJasonDeurlo::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Justin Beiber")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelJustinBeiber::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Kishore Kumar")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelKishoreKumar::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Kygo")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelKygo::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Maninder Buttar")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelManinder::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Neha_Kakkar")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelNehaKakkar::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Post_Malone")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelPostMalone::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Shivjot")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelShivjot::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("The_Weekend")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelWeeknd::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }
    else if(artistName.equals("Tones and I")){
        var viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ViewModelTonesAndI::class.java)
        viewModel.getLiveBitmap()?.observe(viewLifecycleOwner, Observer {
            /*_binding?.includeAmmyVirk?.singerImage?.setImageBitmap(it)*/
            artistlayout?.singerImage?.setImageBitmap(it)
        })
        viewModel?.getLiveArtistName()?.observe(viewLifecycleOwner, Observer {
            artistlayout?.singerName?.setText(it)
        })
        foldersArtistOnClickedListened(artistlayout,playlistName,artistName)

    }

}



override fun onPause() {
super.onPause()
editor?.apply()
editor?.commit()

}

override fun onStart() {
super.onStart()
var sharedPreferences=activity?.getSharedPreferences("SHAREDD",Context.MODE_PRIVATE)
Log.d("JHDJHDFD",sharedPreferences?.getInt("YPOSITION",0).toString())
_binding?.scrollview?.scrollY=sharedPreferences?.getInt("YPOSITION",0)!!
}

} //class finished here




