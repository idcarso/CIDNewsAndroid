package com.amco.cidnews.Fragments;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.opengl.GLES20;
import android.opengl.GLES32;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;


import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.*;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amco.cidnews.Activities.MainActivity;
import com.amco.cidnews.Adapters.DrawerAdapter;
import com.amco.cidnews.Adapters.SwipAdapterNScrolling;
import com.amco.cidnews.Utilities.DrawerItemNavBar;
import com.amco.cidnews.Utilities.ImagePassingAdapter;
import com.amco.cidnews.Utilities.ListenFromActivity;
import com.amco.cidnews.Utilities.OnSwipeTouchListener;
import com.amco.cidnews.R;
import com.amco.cidnews.Adapters.SwipAdapter;
import com.amco.cidnews.Adapters.SwipAdapterBackCard;
import com.amco.cidnews.Utilities.ConexionSQLiteHelper;
import com.amco.cidnews.Utilities.MyObject;
import com.amco.cidnews.Utilities.Noticia;
import com.amco.cidnews.Utilities.Utilidades;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import cz.msebera.android.httpclient.Header;
import link.fls.swipestack.SwipeStack;
import android.animation.ObjectAnimator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.percentlayout.widget.PercentFrameLayout;
import androidx.percentlayout.widget.PercentRelativeLayout;

import javax.microedition.khronos.opengles.GL;

import static com.amco.cidnews.Activities.MainActivity.getRunningTime;
import static com.amco.cidnews.Activities.MainActivity.isNetworkStatusAvailable;
import static com.facebook.FacebookSdk.getApplicationContext;





public class HomeFragment extends Fragment implements ListenFromActivity,ImagePassingAdapter, SwipAdapter.RequestImage {

    public static Boolean SWIPESTACK_SCROLLING = false;  //Change R.layout.frame_home -- R.layout.frame_home_withoutscroll
    static private String TAG = "HomeFragment";
    static private String TAGTIME = "TIMEHomeFragment";

    // [START declare_analytics]
    private FirebaseAnalytics mFirebaseAnalytics;
    // [END declare_analytics]

    //////////////////////////////// MAIN VAR
    static public SwipeStack sp;
    static public SwipAdapterNScrolling SwipadaptadorNScroll;

    static public SwipAdapter Swipadaptador;
    static public SwipAdapterBackCard Swipadaptadoraux;
    static public CardView cardviewContainer,cardviewtest1, spaux, swipNoNews;

    //MENU SLIDE
    DrawerLayout drawerLayout;


    //DATA API FROM HOMEACTIVITY
    ArrayList<Noticia> allNewsFromAct = new ArrayList<>();
    ArrayList<Noticia> specificNewsFromAct = new ArrayList<>();


    ///////
    ArrayList<Noticia> allNewsHelper;
    ArrayList<Noticia> allNewsDefault = new ArrayList<>();
    ArrayList<Noticia> noNewsForShow = new ArrayList<>();

    ArrayList<MyObject> newsApiSaved = new ArrayList<>();
    ScrollView scrollView;
    ScrollView scrollViewShow;
    View viewBckgrnd;
    View viewBckgrndNoNews;
    /////////////////////////////////// TEST CONNECTION FB
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;
    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private ConnectionClassManager.ConnectionClassStateChangeListener mListener;
    private Request requestImg;


    /////////////////////////////////// UI
    PercentFrameLayout lay;
    RelativeLayout containerLoaderGif;
    LinearLayout llmenu,ddmenu, frnointernet, bottomFabs,mMenuSlide;
    ImageView  mano1,basura,paloma,heightscroll,bottomFabFB,bottomFabTwitter,bottomFabWhats,imgLoaderGif;
    ImageButton btnSupDer;
    WebView web;
    FloatingActionButton shareFabMain;
     AsyncHttpClient masterClient;
    ProgressBar progressBar;


    RequestHandle requestTopHeadlines;
    //////////////////////////////// STRINGS
    String cateNews[] = {"HEALTH", "CONSTRUCTION", "RETAIL", "EDUCATION", "ENTERTAINMENT", "ENVIRONMENT", "FINANCE", "ENERGY", "TELECOM"};
    //
    final String labelsNews[] = {"salud", "construcción", "retail", "educación", "entretenimiento", "ambiente", "banca", "energía", "telecom"};
    String categoriesNews[] = {"SALUD", "CONSTRUCCIÓN", "RETAIL", "EDUCACIÓN", "ENTRETENIMIENTO", "AMBIENTE", "BANCA", "ENERGÍA", "TELECOM"};
    String urlHeadlines[] = {
            "https://newsapi.org/v2/top-headlines?q=health&apiKey=99237f17c0b540fdac4d8367e206f5b2",                 //Headline Health API
            "https://newsapi.org/v2/top-headlines?q=construction&apiKey=83bff4ded3954c35862369983b88c41b",           //Headline Construction API
            "https://newsapi.org/v2/top-headlines?q=retail&apiKey=b23937d4bc7a475299e110d007318d28",                 //Headline Retail API
            "https://newsapi.org/v2/top-headlines?q=education&apiKey=a4d2bb68bcae4cb9beb49105c53020f2",              //Headline Education API
            "https://newsapi.org/v2/top-headlines?q=entertainment&apiKey=177c6f87857545c18844e9e4b886dd69",          //Headline Entertainment API
            "https://newsapi.org/v2/top-headlines?q=environment&apiKey=009aac2e199d434ebae570555e96f198",            //Headline Environment API
            "https://newsapi.org/v2/top-headlines?q=economy&apiKey=23c8740aadb64619a124f1506393fb98",                //Headline Finance API
            "https://newsapi.org/v2/top-headlines?q=energy&apiKey=8de9910a613a49289729a8725a0b9fcb",                 //Headline Energy API
            "https://newsapi.org/v2/top-headlines?q=telecom&apiKey=4d3e24219543475eb1cdab5b79d29efd"                 //Headline Telecom API
    };


    String urlDefaultSettings[] = {
            "https://newsapi.org/v2/everything?q=health+technology&sortBy=popularity&apiKey=4d3e24219543475eb1cdab5b79d29efd",                //Default Health API
            "https://newsapi.org/v2/everything?q=construction+technology&sortBy=popularity&apiKey=8de9910a613a49289729a8725a0b9fcb",          //Default Construction API
            "https://newsapi.org/v2/everything?q=retail+technology&sortBy=popularity&apiKey=23c8740aadb64619a124f1506393fb98",                //Default Retail API
            "https://newsapi.org/v2/everything?q=education+technology&sortBy=popularity&apiKey=009aac2e199d434ebae570555e96f198",             //Default Education API
            "https://newsapi.org/v2/everything?q=entertainment+technology&sortBy=popularity&apiKey=177c6f87857545c18844e9e4b886dd69",         //Default Entertainment API
            "https://newsapi.org/v2/everything?q=environment+technology&sortBy=popularity&apiKey=a4d2bb68bcae4cb9beb49105c53020f2",           //Default Environment API
            "https://newsapi.org/v2/everything?q=technology+economy&sortBy=popularity&apiKey=b23937d4bc7a475299e110d007318d28",               //Default Finance API
            "https://newsapi.org/v2/everything?q=energy+power+technology&sortBy=popularity&language=en&apiKey=83bff4ded3954c35862369983b88c41b",//Default Energy API
            "https://newsapi.org/v2/everything?q=communications+technology&sortBy=popularity&apiKey=99237f17c0b540fdac4d8367e206f5b2"         //Default Telecom API
    };

    String watchingNews = "NO";

    //////////////////////////////// INT
    int menuSelectedIndex = 0;
    int MemoryLoadIndex = 100;
    int Start = 100;
    int mainPosition = 0;
    int position = 0;
    int indexHelperGetNews = 0;
    int indexHelperRemoveNews = 0;
    int indexBackgroundBackup = 0;
    int[] stateArray = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    //////
    int x_cord, y_cord, x, y, topOffset;
    int Likes = 0;
    int currentIndex = 0;
    int memoryIndex = 0;
    ////////
    int startPointX, startPointY,posY, posX,dps, dpX, dpY,windowwidth, screenCenter,height, width;
    int oldX = 0;
    int oldY = 0;
    ///////////////////////////////  FLOAT
    float setAxisXCardView, setAxisYCardView,currentProgressSwiping;
    float diffFastGesture = 0;
    float diffPosY = 0;
    float diffPosX = 0;
    float scale = 0;
    /////////////////////////////////// BOOLEAN
    static boolean scrollIsAllowed = true;
    boolean flagMarginColors = false;
    boolean flagFirstNews = false;
    boolean flagWebView = false;
    boolean urlNewsLoaded = false;
    boolean urlNewsLoadedOption = false;
    boolean flagMenuSlideTapped = false;
    /////// Animation Option 2
    boolean isSwiping = false;
    boolean isScrolling = false;
    boolean cardSwipeRight = false;
    boolean firstInitialCard = false;
    boolean showNewsInCardView = true;
    ///
    boolean mCardSwiping = false;
    /////////////////////////////////// HELPERS
    Animation fabOpen, fabClose, rotateFoward, rotateBackward;
    ArrayList<String> MemoryCard = new ArrayList<String>();
    RelativeLayout.LayoutParams layoutParamsNews;


    //TEST
    Bitmap mNextBitmapLoaded = null;
    ImageView mImg;




    private ImagePassingAdapter mPassingData = new ImagePassingAdapter() {
        @Override
        public void sendingImage(Bitmap bitmap, String url) {
          //  Log.d(TAG,"sendingImage -- url: "+url);
           // Log.d(TAG,"sendingImage -- getUrl: "+allNewsDefault.get(sp.getCurrentPosition() + 1).getImagen());



            /*
            if (allNewsDefault.get(sp.getCurrentPosition() + 1).getImagen().contentEquals(url)){
                mNextBitmapLoaded = bitmap;
                Log.d(TAG,"sendingImage -- mNextBitmapLoaded = bitmap");


                if(bitmap == null)
                    Log.d(TAG,"sendingImage -- mNextBitmapLoaded,bitmap:null");
                else
                    Log.d(TAG,"sendingImage -- mNextBitmapLoaded,bitmap != null");
            }else{
                mNextBitmapLoaded =  null;
                Log.d(TAG,"sendingImage -- mNextBitmapLoaded = null");
            }

            mImg.setImageBitmap(mNextBitmapLoaded);*/

        }
    };


    @Override
    public void onRequestImage(String urlRequest) {
        Log.d(TAG,"sendingImage -- urlRequest:"+urlRequest);
            if(mNextBitmapLoaded != null) {

                if(sp.getTopView() == null){
                    Log.d(TAG, "sendingImage -- sp.getTopView == null");
                }else {
                    Log.d(TAG, "sendingImage -- sp.getTopView != null");
                    sp.getRootView().setBackground(mImg.getDrawable());
                }
            }
    }




    @Override
    public void onReloadNextImage() {
        mNextBitmapLoaded = null;
        mImg.setImageBitmap(null);
    }







   /* @Override
    public void imageLoaded(Bitmap bitmap,String currentUrl){
        Log.d("HomeFrag","imageLoaded");

       if (allNewsDefault.get(sp.getCurrentPosition() + 1).getUrl().contentEquals(currentUrl)){
           mNextBitmapLoaded = bitmap;

           Log.d("HomeFrag","imageLoaded -- mNextBitmapLoaded = bitmap");

       }else{
           mNextBitmapLoaded =  null;
           Log.d("HomeFrag","imageLoaded -- mNextBitmapLoaded = null");

       }
    }*/


   /*
     @Override
     public void requestImage(){

         Log.d("HomeFrag","requestImage");

         if (mNextBitmapLoaded != null){
                Log.d("HomeFrag","requestImage -- mNextBitmapLoaded != null");
               // mPassingData.sendingImage();

             }
     }*/

    ///////////////////////////////////
    /// MARK:
    @Override
    public void setGeneralNews(){
        Log.d(TAG,"setGeneralNews");
        if (SWIPESTACK_SCROLLING) {
            if ( getActivity() != null) {
                allNewsFromAct = ((MainActivity) getActivity()).allNews;
                allNewsDefault = allNewsFromAct;
                if (allNewsDefault.size() != 0)
                    showNews(allNewsDefault, false, 0);

                Log.d(TAG, "setGeneralNews allNews(MainActivity).size: " +
                        allNewsFromAct);

            }
        }
        else {
                if (getActivity() != null) {
                    allNewsFromAct = ((MainActivity) getActivity()).allNews;
                    allNewsDefault = allNewsFromAct;
                    if (allNewsDefault.size() != 0)
                        showNewsNScrolling(allNewsDefault, false, 0);

                }
        }
    }


    ///////////////////////////////////
    /// MARK:
    @Override
    public void msjWeakSignal(){
        if(frnointernet != null)
            frnointernet.setVisibility(View.VISIBLE);   //Muestra el letrero de Weak Signal
    }


    ///////////////////////////////////
    /// MARK:
    @Override
    public void setSpeficicNewsFromMenu(int mIndex) {
        Log.d(TAG, "setSpeficicNewsFromMenu");
        if (SWIPESTACK_SCROLLING){
            if ((MainActivity) getActivity() != null) {
                specificNewsFromAct = ((MainActivity) getActivity()).allNewsMenu;
                allNewsDefault = new ArrayList<>();
                allNewsDefault = specificNewsFromAct;
                setupForChangedNews();
                showNews(allNewsDefault, true, mIndex);
            }
        }

        else {
            if ((MainActivity) getActivity() != null) {
                specificNewsFromAct = ((MainActivity) getActivity()).allNewsMenu;
                allNewsDefault = new ArrayList<>();
                allNewsDefault = specificNewsFromAct;
                setupForChangedNewsNScrolling();
                showNewsNScrolling(allNewsDefault, true, mIndex);
            }
        }

    }



    ///////////////////////////////////
    /// MARK:
    @Override
    public void sendingImage(Bitmap bitmap,String url) {
        Log.d(TAG,"sendingImage?");
    }


    ///////////////////////////////////
    /// MARK:
    @Override
    public void onSaveInstanceState(Bundle outState) {   //No se utiliza
        Log.e(TAGTIME, "onSaveInstanceState DELAY:" + String.valueOf(getRunningTime()));
        outState.putInt("PositionKey", currentIndex);
        outState.putInt("MemoryKey", memoryIndex);
        outState.putStringArrayList("MemoryCardKey", MemoryCard);  //Array de String de las Urls a borrar
        outState.putInt("MenuSelectedKey", MemoryLoadIndex);
        outState.putString("watchingNewsKey", watchingNews);
     //   outState.putParcelableArrayList("myArrayList", newsApiSaved);
        super.onSaveInstanceState(outState);
    }


    /****************************************** LIFECYCLE *****************************************/


    ///////////////////////////////////
    /// MARK:
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAGTIME, "onCreate:" + String.valueOf(getRunningTime()));
        ((MainActivity) getActivity()).setActivityListener(HomeFragment.this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }

    ///////////////////////////////////
    /// MARK:
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView True" );


    }
    ///////////////////////////////////
    /// MARK:
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAGTIME, "onDestroy:" + String.valueOf(getRunningTime()));
        if  (masterClient != null) {
            masterClient.getThreadPool().shutdown();
        }
        //REQUEST NEW ARRAYLIST! FROM ACT
        ((MainActivity)getActivity()).refreshDeletedNews();
    }


    @Override
    public void onResume(){
        super.onResume();
        Log.e(TAGTIME, "onResume:" + String.valueOf(getRunningTime()));

    }



    ///////////////////////////////////
    /// MARK:
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView True" );
        Log.e(TAGTIME, "onCreateView,  Start Inflating.." + getRunningTime());
        final View view = inflater.inflate(R.layout.frame_home_main, container, false);
        Log.e(TAGTIME, "onCreateView, After Inflating.." + getRunningTime());

        setupMenu(view);

        if (SWIPESTACK_SCROLLING) {

            /////////////////////////////////////  Prioridad en cargar
            setupCreateView(view, inflater, container);
            Log.e(TAGTIME, "onCreateView,  End Setting" + String.valueOf(getRunningTime()));
            if (allNewsDefault.size() != 0) {
                showNews(allNewsDefault, false, 0);
                Log.e(TAGTIME, "onCreate: allNewsFromAct" + allNewsFromAct.size());

            } else {
                if ((MainActivity) getActivity() != null) {
                    allNewsDefault = ((MainActivity) getActivity()).allNews;
                    Log.e(TAGTIME, "onCreate: allNewsDefault = AllNews:" +
                            ((MainActivity) getActivity()).allNews.size());
                    Log.e(TAGTIME, "onCreate: allNewsDefault = AllNewsMenu :" +
                            ((MainActivity) getActivity()).allNewsMenu.size());

                    showNews(allNewsDefault, false, 0);
                }
                Log.e(TAGTIME, "onCreate: allNewsFromAct" + allNewsFromAct.size());
            }

        }else {

            /////////////////////////////////////
            setupCreateViewNoScroll(view);
            Log.e(TAGTIME, "onCreateView,  End Setting" + String.valueOf(getRunningTime()));
            if (allNewsDefault.size() != 0) {
                showNewsNScrolling(allNewsDefault, false, 0);
                Log.e(TAGTIME, "onCreate: allNewsFromAct" + allNewsFromAct.size());

            } else {
                if (getActivity() != null &&  ((MainActivity) getActivity()).allNews.size() != 0){
                    allNewsDefault = ((MainActivity) getActivity()).allNews;
                    Log.e(TAGTIME, "onCreate: allNewsDefault = AllNews:" + ((MainActivity) getActivity()).allNews.size());
                    Log.e(TAGTIME, "onCreate: allNewsDefault = AllNewsMenu :" + ((MainActivity) getActivity()).allNewsMenu.size());
                    showNewsNScrolling(allNewsDefault, false, 0);
                }
            }


        }

        return view;
    }






    /********************************************************************** SwipeStack NOT SCROLLING *********************************************/

    public void setupCreateViewNoScroll(View view){
        /////////////////////////////////////
        sp = view.findViewById(R.id.swipStack);
        frnointernet = view.findViewById(R.id.aviso_no_internet);
        frnointernet.setVisibility(View.INVISIBLE);
        containerLoaderGif = view.findViewById(R.id.loader_gif);
        imgLoaderGif = view.findViewById(R.id.img_loader_gif);

        ////////////
        btnSupDer = view.findViewById(R.id.boton_superior_home);
        basura = view.findViewById(R.id.basuraimg);
        paloma =  view.findViewById(R.id.palomaimg);
        mano1 =  view.findViewById(R.id.icono_mano);
        shareFabMain = view.findViewById(R.id.shareMainFab);

        setupUINScrolling(view);
        configUIListenersNScroll(view);
    }

    /******** LISTENERS NScrolling *****/

    ///////////////////////////////////
    /// MARK: config Listeners UI
    public void configUIListenersNScroll(final View view){

        btnSupDer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"configUIListenersNScroll -- btnSupDer -- onClick.TRUE");
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        ///////////////////////////////////   BOTTOM FABS
        shareFabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewsByIntent();
            }
        });


        sp.setSwipeProgressListener(new SwipeStack.SwipeProgressListener() {
            @Override
            public void onSwipeStart(int position) {

                int[] location = new int[2];
                sp.getTopView().getLocationOnScreen(location);

                oldX = (int) location[0];
                oldY = (int) location[1];
                startPointX = oldX;
                startPointY = oldY;
                x = startPointX;
                y = startPointY;

                Log.d(TAG, "sp.setSwipeProgressListener -- onSwipeStart -- startPointX:"+startPointX);
                Log.d(TAG, "sp.setSwipeProgressListener -- onSwipeStart -- startPointY:"+startPointY);
            }

            @Override
            public void onSwipeProgress(int position, float progress) {
                if (Math.abs(progress*100) > 10){
                    mCardSwiping = true;
                }
                Log.d(TAG,"configUIListenersNScroll -- onSwipeProgress -- progress:"+progress);
                animationSwipeProgress((float) (progress*(0.75)));
                shareFabMain.hide();

            }

            @Override
            public void onSwipeEnd(int position) {
                animationFinish(basura, paloma);

                diffPosY = 0;
                diffPosX = 0;


                int[] location = new int[2];
                sp.getTopView().getLocationOnScreen(location);

                x_cord = (int) location[0];
                y_cord = (int) location[1];

                Log.d(TAG, "sp.setSwipeProgressListener -- onSwipeStart -- x_cord:"+x_cord);
                Log.d(TAG, "sp.setSwipeProgressListener -- onSwipeStart -- y_cord:"+y_cord);

                if ((Math.abs(startPointX  - x_cord) < 8) && (Math.abs(startPointY - y_cord) < 8) && !mCardSwiping) {
                    Log.d(TAG, "eventsActionUp  -- CARD CLICKED! ");
                //    Log.d(TAG, "eventsActionUp  -- CARD CLICKED! Swipadaptador.mostrarNoticiasView("
                  //          +String.valueOf(sp.getCurrentPosition())+")");

                    SwipadaptadorNScroll.mostrarNoticiasView(sp.getCurrentPosition());

                    //Log.d(TAG, "eventsActionUp  -- sp.getCurrentPosition:"+(sp.getCurrentPosition()));
                    //Log.d(TAG, "eventsActionUp  -- currentIndex:"+(currentIndex));
                    urlNewsLoaded = false;
                    indexHelperRemoveNews = 0;
                }else{
                    shareFabMain.show();
                }
                mCardSwiping = false;
            }
        });





        sp.setListener(new SwipeStack.SwipeStackListener() {
            @Override
            public void onViewSwipedToLeft(final int position) {
                Log.d(TAG, "configUIListenersNScroll -- sp.onViewSwipedToLeft Position:" + String.valueOf(position));
                Log.d(TAG, "configUIListenersNScroll -- sp.onViewSwipedToLeft sp.getCurrentPosition: " + String.valueOf(sp.getCurrentPosition()));

                indexHelperRemoveNews = indexHelperRemoveNews + 1;
                Log.d(TAG, "configUIListenersNScroll -- sp.onViewSwipedToLeft indexHelperRemoveNews: " + String.valueOf(indexHelperRemoveNews));
                saveNewsGenericNScroll(SwipadaptadorNScroll,position,false);
            }
            @Override
            public void onViewSwipedToRight(final int position) {
                Log.d(TAG, "configUIListenersNScroll -- sp.onViewSwipedRight Position:" + String.valueOf(position));
                Log.d(TAG, "configUIListenersNScroll -- sp.onViewSwipedRight sp.getCurrentPosition: " + String.valueOf(sp.getCurrentPosition()));

                indexHelperRemoveNews = indexHelperRemoveNews + 1;
                Log.d(TAG, "configUIListenersNScroll -- sp.onViewSwipedToRight indexHelperRemoveNews: " + String.valueOf(indexHelperRemoveNews));

                saveNewsGenericNScroll(SwipadaptadorNScroll,position,true);
            }
            @Override
            public void onStackEmpty() {
                deleteCache(getContext());
                MemoryCard.clear();  //The counting of position is reset.
                sp.resetStack();   //Restart the news.
                Log.d(TAG,  "configUIListenersNScroll -- sp.OnStackEmpty.TRUE");
            }
        });

    }

    ///////////////////////////////////
    /// MARK: initial UI
    public void setupUINScrolling(final View view){
        ////////////////////////////////////
        Glide.with(this).load(R.drawable.loadingbl).into(imgLoaderGif);
        containerLoaderGif.setVisibility(View.VISIBLE);
        imgLoaderGif.setVisibility(View.VISIBLE);
        containerLoaderGif.bringToFront();
        imgLoaderGif.bringToFront();
        /////////////////////////////////////

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);            //Obtiene el valor de height y width
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        windowwidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        screenCenter = windowwidth / 2;


        /////////////////////////////////////
        shareFabMain.setVisibility(View.INVISIBLE);
        shareFabMain.setEnabled(false);
        shareFabMain.hide();
        ///////////////////////////////////  SP
        sp.setEnabled(false);
        ///////////////////////////////////  ScrollViews

        final AnimatorSet anim4 =  (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.anim.share_moving);
        anim4.setTarget(shareFabMain);
        anim4.start();
        anim4.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
              /*  new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        anim4.start();
                    }
                }, 5000); */
            }
        });
        basura.bringToFront();
        paloma.bringToFront();
        basura.setVisibility(View.INVISIBLE);
        paloma.setVisibility(View.INVISIBLE);
    }

    ///////////////////////////////////
    public void showNewsNScrolling(final ArrayList<Noticia> defaultListNews,boolean fromMenuSlide,  int catFromMenuSlide) {
        Log.e(TAGTIME, "showNewsNScrolling -- DELAY:" + String.valueOf(getRunningTime()));
        if (getActivity() == null) {
            Log.d(TAG, "showNewsNScrolling -- getActivity: NULL");
        } else {
            SwipadaptadorNScroll = new SwipAdapterNScrolling(getActivity(), defaultListNews, getContext());


            if (SwipadaptadorNScroll.getCount() == 0) {
                Log.e(TAG, "showNewsNScrolling -- Swipadaptador.getCount: No news");
                if (fromMenuSlide){   //Check if is from Menu for show the card!
                    if (( sp != null) && (sp.getVisibility() == View.VISIBLE)){
                        shareFabMain.setVisibility(View.INVISIBLE);
                        shareFabMain.hide();
                        menuSelectedIndex = catFromMenuSlide; // 0 - 8
                    }
                }
                flagMenuSlideTapped = fromMenuSlide;
            }
            else {
                if(frnointernet != null) {
                    frnointernet.setVisibility(View.INVISIBLE);   //Muestra el letrero de Weak Signal
                }
                sp.setAdapter(SwipadaptadorNScroll);


                    if(sp.getTopView() == null){
                        Log.e(TAG, "showNewsNScrolling --  sp.getTopView.NULL, sp.ResetStack");
                        sp.resetStack();
                    }


                sp.setVisibility(View.VISIBLE);
                    sp.setEnabled(true);


                    shareFabMain.setVisibility(View.VISIBLE);
                    shareFabMain.show();
                    shareFabMain.setEnabled(true);
            }
        }
    }


    ///////////////////////////////////
    /// MARK: Setup when user make a change in Menu.
    public void setupForChangedNewsNScrolling(){
        sp.removeAllViews();
        sp.removeAllViewsInLayout();
        sp.resetStack();
        sp.setVisibility(View.VISIBLE);
        shareFabMain.setVisibility(View.VISIBLE);
        shareFabMain.setEnabled(true);
        shareFabMain.show();
    }

    ///////////////////////////////
    /// MARK: Save news to the Database Favorites or Recover.
    public void saveNewsGenericNScroll(final SwipAdapterNScrolling targetAdapter, final int positionForSave,final boolean forFavorites) {
        final String titulo = targetAdapter.getItem(positionForSave).getTitulo();
        final String url = targetAdapter.getItem(positionForSave).getUrl();
        final String imagen = targetAdapter.getItem(positionForSave).getImagen();
        final String autor = targetAdapter.getItem(positionForSave).getAutor();
        final String categoria = targetAdapter.getItem(positionForSave).getCategoria();



        if(forFavorites) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    registrarNoticias(titulo, imagen, url, autor, categoria);
                }
            }).start();
        }
        else
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    registrarNoticiasRecuperar(titulo, imagen, url, autor, categoria);
                }
            }).start();
    }























































































































    /************************************************************************** SWIPESTACK SCROLLING *****************************************************/




    /**************************************** INITIAL SETUP ***************************************/

    ///////////////////////////////////
    /// MARK: initial params
    public void setupCreateView(View view,LayoutInflater inflater,ViewGroup container){
        mImg = view.findViewById(R.id.ImgTest);
        /////////////////////////////////////
        sp = view.findViewById(R.id.swipStack);
        frnointernet = view.findViewById(R.id.aviso_no_internet);
        frnointernet.setVisibility(View.INVISIBLE);
        cardviewContainer = view.findViewById(R.id.cardviewtest);
        cardviewtest1 = view.findViewById(R.id.cardviewext21);
        swipNoNews = view.findViewById(R.id.swipNoNews);
        spaux = view.findViewById(R.id.swipStackaux);
        spaux.setVisibility(View.INVISIBLE);
        scrollView = view.findViewById(R.id.scrollext1);
        scrollViewShow = view.findViewById(R.id.scrollext2);
        viewBckgrndNoNews = inflater.inflate(R.layout.backnonews, container, false);
        viewBckgrnd = inflater.inflate(R.layout.backcardview, container, false);
        containerLoaderGif = view.findViewById(R.id.loader_gif);
        imgLoaderGif = view.findViewById(R.id.img_loader_gif);
        //mMenuSlide = view.findViewById(R.id.slide_menu);

        ////////////
        bottomFabs = view.findViewById(R.id.content_bottomfabs);
        bottomFabFB = view.findViewById(R.id.bottombutton_fb);
        bottomFabTwitter = view.findViewById(R.id.bottombutton_twitter);
        bottomFabWhats = view.findViewById(R.id.bottombutton_whats);
        heightscroll = view.findViewById(R.id.heightscroll);
        btnSupDer = view.findViewById(R.id.boton_superior_home);
        basura = (ImageView) view.findViewById(R.id.basuraimg);
        paloma = (ImageView) view.findViewById(R.id.palomaimg);
        mano1 = (ImageView) view.findViewById(R.id.icono_mano);
        shareFabMain = view.findViewById(R.id.shareMainFab);

        setupUI(view);
        configUIListeners(view);

    }


    ///////////////////////////////////
    /// MARK: initial UI
    public void setupUI(final View view){
        ////////////////////////////////////
        Glide.with(this).load(R.drawable.loadingbl).into(imgLoaderGif);
        containerLoaderGif.setVisibility(View.VISIBLE);
        imgLoaderGif.setVisibility(View.VISIBLE);
        containerLoaderGif.bringToFront();
        imgLoaderGif.bringToFront();
        /////////////////////////////////////

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);            //Obtiene el valor de height y width
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        windowwidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        screenCenter = windowwidth / 2;
        swipNoNews.addView(viewBckgrndNoNews);


        /*
        global.heightPhone = height;
        global.widthPhone = width;
        global.densityDpi = displaymetrics.densityDpi;
        global.dpx = Math.round(((global.widthPhone) * 160) / global.densityDpi);
        global.dpy = Math.round(((global.heightPhone) * 160) / global.densityDpi);
        global.densityNum = displaymetrics.density;
        dpX = global.dpx;
        dpY = global.dpy;
        scale = global.densityNum;
        float d = Math.round(global.densityNum);
        dps = (int) d;*/
        /////////////////////////////////////
        fabOpen = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.sharebutton_open);
        fabClose = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.sharebutton_close);
        rotateFoward = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotate_foward);
        rotateBackward = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backward);
        /////////////////////////////////////
        cardviewContainer.setVisibility(View.INVISIBLE);
        shareFabMain.setVisibility(View.INVISIBLE);
        shareFabMain.setEnabled(false);
        ///////////////////////////////////  SP
        sp.setEnabled(false);
        ///////////////////////////////////  ScrollViews
        scrollView.setVerticalScrollBarEnabled(false);
        scrollViewShow.setVerticalScrollBarEnabled(true);
        scrollViewShow.bringToFront();

        final AnimatorSet anim4 =  (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.anim.share_moving);
        anim4.setTarget(shareFabMain);
        anim4.start();
        anim4.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        anim4.start();
                    }
                }, 5000);
            }
        });
        basura.bringToFront();
        paloma.bringToFront();
        basura.setVisibility(View.INVISIBLE);
        paloma.setVisibility(View.INVISIBLE);



        cardviewContainer.setEnabled(true);
        sp.setEnabled(false);
        cardviewtest1.setEnabled(true);

        /*if(web!=null) {
            RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, web.getHeight());
            heightscroll.setLayoutParams(layoutParams3);
        }*/

    }

    ///////////////////////////////////
    /// MARK:Setup Menu
    public void setupMenu(View view){
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout); //Obtener drawer
        ListView drawerList = (ListView) view.findViewById(R.id.nav_list); //Obtener listview
        ArrayList<DrawerItemNavBar> items = new ArrayList<DrawerItemNavBar>();

        items.add(new DrawerItemNavBar(getResources().getString(R.string.health), R.drawable.ic_health_menu));
        items.add(new DrawerItemNavBar(getResources().getString(R.string.construction), R.drawable.ic_construction_menu));
        items.add(new DrawerItemNavBar(getResources().getString(R.string.retail), R.drawable.ic_retail_menu));
        items.add(new DrawerItemNavBar(getResources().getString(R.string.education), R.drawable.ic_education_menu));
        items.add(new DrawerItemNavBar(getResources().getString(R.string.entertainment), R.drawable.ic_entertainment_menu));
        items.add(new DrawerItemNavBar(getResources().getString(R.string.environment), R.drawable.ic_environment_menu));
        items.add(new DrawerItemNavBar(getResources().getString(R.string.finance), R.drawable.ic_finance_menu));
        items.add(new DrawerItemNavBar(getResources().getString(R.string.energy), R.drawable.ic_energy_menu));
        items.add(new DrawerItemNavBar(getResources().getString(R.string.telecom), R.drawable.ic_telecom_menu));

        drawerList.setAdapter(new DrawerAdapter(getContext(), items));// Relacionar el adaptador y la escucha de la lista del drawer
        View footer = getLayoutInflater().inflate(R.layout.footer_cidnews, null);
        drawerList.addFooterView(footer);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrawerItemNavBar selected = (DrawerItemNavBar) parent.getItemAtPosition(position);
                Log.d(TAG, "onCreate -- onItemClick: position: " + String.valueOf(position));

                if ((MainActivity) getActivity() != null) {
                    ((MainActivity) getActivity()).specificRecursive(position);
                }

                startChangedNews();

                if (SWIPESTACK_SCROLLING) {
                    if (bottomFabs.getVisibility() == View.VISIBLE) {
                        Log.d(TAG, "Setup UI -- scrollView onScrollChange: Scroll < 25, bottomFabs.INVISIBLE");
                        hideBottomIcons();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bottomFabs.setVisibility(View.INVISIBLE);
                            }
                        }, 300);
                    }

                }


                String typeNews = cateNews[position];
                Log.d(TAG,"setupMenu -- drawerList.setOnItemClickListener -- cateNews["+position
                        +"]: "+typeNews);

                // [START event]
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, typeNews);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
                // [END event]

                drawerLayout.closeDrawers();
            }
        });

    }

    /****************************************** LISTENERS *****************************************/

    ///////////////////////////////////
    /// MARK: config Listeners UI
    public void configUIListeners(final View view){

        btnSupDer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"btnSupDer -- onClick.TRUE");
                drawerLayout.openDrawer(Gravity.END);
            }
        });


        swipNoNews.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "configUIListeners -- swipNoNews --  onTouch: ");
                isSwiping = true;
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                        Log.e("animationSwipe!", "ACTION: CANCEL ");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.e("animationSwipe!", "ACTION DOWN!");
                        eventsActionDown(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("animationSwipe!", "ACTION UP!");
                        eventsActionUpCardNoNews(event,swipNoNews);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("animationSwipe!", "ACTION MOVE!");
                        eventsActionMoveCardNoNews(event,swipNoNews);
                        break;
                }
                return true;
            }
        });

        ///////////////////////////////////   BOTTOM FABS
        shareFabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewsByIntent();
            }
        });

        bottomFabFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: bottomFabFacebook");
                if(facebookInstalled(getActivity())){
                    sendNewsByIntentFB(getActivity());
                }else{
                    Log.e(TAG, "onClick: bottomFabFacebookr NO INSTALLED?");
                    try {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.katana"));
                        startActivity(viewIntent);
                    }catch(Exception error) {
                        Toast.makeText(getApplicationContext(),"Unable to connect, Try again",
                                Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
            }
        });
        bottomFabTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: bottomFabTwitter");
                if(twitterInstalled(getActivity())){
                    sendNewsByIntentTwitter(getActivity());
                }else{
                    try {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse("https://play.google.com/store/apps/details?id=com.twitter.android"));
                        startActivity(viewIntent);
                    }catch(Exception error) {
                        Toast.makeText(getApplicationContext(),"Unable to connect, Try Again",
                                Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
            }
        });
        bottomFabWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: bottomFabWhatsApp");
                sendNewsByIntentWApp(getActivity());
            }
        });

        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (web!=null) {
                    Log.d(TAG, "Setup UI -- scrollView.onLayoutChange, web.getBottom:" + String.valueOf(web.getBottom()));
                    RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, web.getHeight());
                    heightscroll.setLayoutParams(layoutParams3);
                }
            }
        });


        cardviewContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "Setup UI -- cardviewContainer, onTouch: ");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                        Log.d(TAG,"cardviewContainer -- ACTION: CANCEL ");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG,"cardviewContainer -- ACTION: DOWN ");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG,"cardviewContainer -- ACTION: UP ");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG,"cardviewContainer -- ACTION: MOVE ");
                        break;
                }
                return true;
            }
        });


        cardviewtest1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "Setup UI -- cardviewtest1 onTouch: ");
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                        Log.e(TAG, "animationSwipe!-- ACTION: CANCEL ");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.e(TAG, "animationSwipe!-- ACTION: DOWN ");
                        eventsActionDown(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e(TAG, "animationSwipe!-- ACTION: UP ");
                        eventsActionUp(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.e(TAG, "animationSwipe!-- ACTION: MOVE ");
                        eventsActionMove(event, v);
                        break;
                }
                return true;
            }
        });



        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollViewShow.setScrollY(scrollY);
                Log.d(TAG, "Setup UI -- ScrollY: " + String.valueOf(scrollY));
                isScrolling = false;
                if (web != null) {
                    Log.d(TAG, " Setup UI, web.getBottom: " + String.valueOf(web.getBottom()));
                    Log.d(TAG, " Setup UI, web.getHeight:" + String.valueOf(web.getHeight()));
                }

                if ((scrollY > 35)){//(scrollView.getHeight() * 2) / 3)) {  //(scrollY <= web.getContentHeight()) &&
                    if (bottomFabs.getVisibility() == View.INVISIBLE) {
                        Log.d(TAG, "Setup UI -- scrollView onScrollChange: Scroll > 25,  bottomFabs.VISIBLE");
                        showBottomIcons();
                    }
                } else {
                    if (bottomFabs.getVisibility() == View.VISIBLE) {
                        Log.d(TAG, "Setup UI -- scrollView onScrollChange: Scroll < 25, bottomFabs.INVISIBLE");
                        hideBottomIcons();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bottomFabs.setVisibility(View.INVISIBLE);
                            }
                        }, 300);
                    }
                }

                cardviewContainer.getBackground().setColorFilter(ContextCompat.getColor(getContext(),R.color.blanco),PorterDuff.Mode.MULTIPLY);


                if (scrollY >= 1) {

                    float percent = (float) scrollY*2;
                   /* if (percent < 0){
                        percent = 0;

                    }*/
                    if (percent > 255) {
                        percent = 255;
                    }
                    int colorBackground = (int) percent;//(percent*(255));
                    cardviewContainer.getBackground().setAlpha(colorBackground);
                }

                if (scrollY == 0) {
                    cardviewContainer.getBackground().setColorFilter(ContextCompat.getColor(getContext(),R.color.mainBlue),PorterDuff.Mode.MULTIPLY);
                    cardviewContainer.getBackground().setAlpha(255);
                }
                if ((scrollY >= 0) && (scrollY < 20)) {
                    shareFabMain.show();

                    if (!flagMarginColors) {
                        Log.d(TAG, "Setup UI -- scrollView onScrollChange: cardviewContainer: making margin Blue");
                        flagMarginColors = true;
                    }


                } else {
                    shareFabMain.hide();
                    if (flagMarginColors) {
                        Log.d(TAG, "Setup UI -- scrollView onScrollChange: cardviewContainer: making margin White");
                        flagMarginColors = false;
                    }
                }
                if (scrollIsAllowed) {
                    scrollIsAllowed = false;
                    Log.d(TAG, "Setup UI -- scrollView onScrollChange: scrollIsAllow.FALSE");
                    if ((scrollY <= scrollView.getHeight()) && (scrollY > (scrollView.getHeight() * 7) / 8)) {
                        flagWebView = false;
                    }
                    if (scrollY > 0) {
                        if (!urlNewsLoaded) {
                            Log.d(TAG, "Setup UI -- scrollView onScrollChange: swipeToup()");
                            swipeToUp();
                        }
                    }
                }
            }
        });

        cardviewContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int[] location = new int[2];

                cardviewContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                cardviewContainer.getLocationInWindow(location);
                setAxisXCardView = location[0];
                setAxisYCardView = location[1];


                DisplayMetrics dm = new DisplayMetrics();
                if (getActivity() != null) {
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                    topOffset = dm.heightPixels - view.getMeasuredHeight();    //topOffset: La diferencia el screen del movil y el view de la aplicacion (bar screen)
                    Log.d(TAG, "Setup UI -- cardviewContainer.TreeObserver -- cardviewContainer.getMeasuredHeight: " + String.valueOf(cardviewContainer.getMeasuredHeight()) + ", topOffset: " + String.valueOf(topOffset));
                }

                View tempView = cardviewContainer; // the view you'd like to locate
                int[] loc = new int[2];
                tempView.getLocationOnScreen(loc);
                final int yaux = loc[1] - topOffset;
                Log.d(TAG, "Setup UI -- cardviewContainer.TreeObserver -- setAxisYCardView:" + String.valueOf(setAxisYCardView) + ", TopOffset:" + String.valueOf(topOffset));
                setAxisYCardView = yaux;
                Log.d(TAG, "Setup UI -- cardviewContainer.TreeObserver -- setAxisXCardView" + String.valueOf(setAxisXCardView) + ", Y:" + String.valueOf(setAxisYCardView) + ", TopOffset:" + String.valueOf(topOffset));
                layoutParamsNews = new RelativeLayout.LayoutParams(cardviewContainer.getWidth(), cardviewContainer.getHeight() * 8);
                if(((MainActivity)getActivity()) != null)
                    ((MainActivity)getActivity()).layoutParamsNewsBackUp = layoutParamsNews;
                    // cardviewtest1.setLayoutParams(layoutParamsNews);

                if(web!=null) {
                    RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, web.getHeight());
                    heightscroll.setLayoutParams(layoutParams3);
                }
            }
        });



    }


    ///////////////////////////////////
    /// MARK: Start to change Main View.
    public void startChangedNews(){
        if(SWIPESTACK_SCROLLING) {
            cardviewtest1.setVisibility(View.INVISIBLE);
            cardviewContainer.setVisibility(View.INVISIBLE);
            spaux.setVisibility(View.INVISIBLE);
            indexHelperRemoveNews = 0;  //indexHelperRemoveNews es utilizado para saber si el usuario ha movido una Swipecard, ayuda en el inicio de cargar noticias.
        }else{
            sp.setVisibility(View.INVISIBLE);
            shareFabMain.setVisibility(View.INVISIBLE);
            shareFabMain.setEnabled(false);
            shareFabMain.hide();
            frnointernet.setVisibility(View.INVISIBLE);
            imgLoaderGif.setVisibility(View.VISIBLE);
        }
    }



    ///////////////////////////////////
    /// MARK: Setup when user make a change in Menu.
    public void setupForChangedNews(){
        sp.removeAllViews();
        sp.removeAllViewsInLayout();
        sp.resetStack();
        sp.setVisibility(View.VISIBLE);
        spaux.setVisibility(View.VISIBLE);
        shareFabMain.setVisibility(View.VISIBLE);
        cardviewtest1.setVisibility(View.VISIBLE);
        cardviewContainer.setVisibility(View.VISIBLE);
    }




    /****************************************** ANIMATION *****************************************/


    ///////////////////////////////////
    /// MARK: Hide the bottom social media icons
    public void hideBottomIcons(){
        Log.d(TAG,"hideBottomIcons");
        bottomFabFB.setScaleX(1f);
        bottomFabFB.setScaleY(1f);
        bottomFabTwitter.setScaleX(1f);
        bottomFabTwitter.setScaleY(1f);
        bottomFabWhats.setScaleX(1f);
        bottomFabWhats.setScaleY(1f);
        ///////
        bottomFabFB.animate().scaleY(0.01f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        bottomFabFB.animate().scaleX(0.01f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        bottomFabTwitter.animate().scaleY(0.01f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        bottomFabTwitter.animate().scaleX(0.01f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        bottomFabWhats.animate().scaleY(0.01f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        bottomFabWhats.animate().scaleX(0.01f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        ///////
    }
    ///////////////////////////////////
    /// MARK: Show the bottom social media icons
    public void showBottomIcons(){
        Log.d(TAG,"showBottomIcons");
        bottomFabFB.setScaleX(0.01f);
        bottomFabFB.setScaleY(0.01f);
        bottomFabTwitter.setScaleX(0.01f);
        bottomFabTwitter.setScaleY(0.01f);
        bottomFabWhats.setScaleX(0.01f);
        bottomFabWhats.setScaleY(0.01f);

        bottomFabs.setVisibility(View.VISIBLE);
        //4bottomFabFB.setVisibility(View.VISIBLE);

        ///////
        bottomFabFB.animate().scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        bottomFabFB.animate().scaleX(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        bottomFabTwitter.animate().scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        bottomFabTwitter.animate().scaleX(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        bottomFabWhats.animate().scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        bottomFabWhats.animate().scaleX(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        ///////

    }
    ///////////////////////////////////


    ///////////////////////////////
    /// MARK: Animation swipe.
    public void animationSwipeProgress(float progressSwipe) {
        if ((progressSwipe * 100) < 1)
            animationLeftSwipe(progressSwipe);
        if ((progressSwipe * 100) > 1)
            animationRightSwipe(progressSwipe);
    }
    //////////////////////////////
    /// MARK: Animation when swipe finish.
    public void animationFinish(final ImageView targetTrash, final ImageView targetCheck) {
        targetCheck.animate().scaleY(0.01f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        targetCheck.animate().scaleX(0.01f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        targetTrash.animate().scaleY(0.01f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        targetTrash.animate().scaleX(0.01f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);

        targetCheck.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        targetTrash.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(targetTrash, "translationX", -250);
        ObjectAnimator animator2 =  ObjectAnimator.ofFloat(targetCheck, "translationX", 250 );

        animator1.setRepeatCount(0);
        animator1.setDuration(300);
        animator1.start();
        animator2.setRepeatCount(0);
        animator2.setDuration(300);
        animator2.start();


        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                targetTrash.setLayerType(View.LAYER_TYPE_NONE, null);
                targetTrash.setVisibility(View.INVISIBLE);
                targetTrash.animate().scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
                targetTrash.animate().scaleX(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
            }
        });
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                targetCheck.setLayerType(View.LAYER_TYPE_NONE, null);
                targetCheck.setVisibility(View.INVISIBLE);
                targetCheck.animate().scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
                targetCheck.animate().scaleX(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
            }
        });



        // animationRightSwipe(currentProgressSwiping)

        //    targetCheck.setVisibility(View.INVISIBLE);
        //  targetTrash.setVisibility(View.INVISIBLE);
        /////////////////
        btnSupDer.setVisibility(View.VISIBLE);
        btnSupDer.bringToFront();
    }
    //////////////////////////////
    /// MARK: Animation when card is swiping to left direction.
    public void animationLeftSwipe(float currentProgress) {
        /////////////////
        //Log.d(TAG, "animationLeftSwipe -- currentProgress:" + currentProgress);
        Log.d(TAG, "animationLeftSwipe -- MemoryCardIndex:  Value: " + String.valueOf(MemoryCard.size()));
        /////////////////
        Float D = Math.abs((400) * currentProgress) + 120;
        RelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(D.intValue(), D.intValue());
        int marginTopIcons = Double.valueOf(height * 0.3 + currentProgress * 200).intValue();
        params.setMargins(0, marginTopIcons, 0, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        basura.setLayoutParams(params);


        if(-currentProgress > 0.125) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(basura, "translationX", (-currentProgress * 300));
            animator1.setRepeatCount(0);
            animator1.setDuration(0);
            animator1.start();


            basura.setAlpha(-currentProgress * 4f);
            basura.setVisibility(View.VISIBLE);
            paloma.setVisibility(View.INVISIBLE);
        }else{
            basura.setAlpha(0f);
        }
    }
    //////////////////////////////
    /// MARK: Animation when card is swiping to right direction.
    public void animationRightSwipe(float currentProgress) {
        //  Log.d("POSITION ", "animationLeftSwipe: " + String.valueOf(currentProgress));
        //Log.d("HomeFragment", "animationRightSwipe -- allNewsDefault.size: " + String.valueOf(allNewsDefault.size()));

        /////////////////
        Float D = Math.abs(400 * currentProgress) + 120;
        RelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(D.intValue(), D.intValue());
        params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        int marginTopIcons = Double.valueOf(height * 0.3 - currentProgress * 200).intValue();
        params.setMargins(0, marginTopIcons, 0, 0);
        paloma.setLayoutParams(params);

        if(currentProgress > 0.125) {

            Log.d(TAG, "animationRightSwipe -- allNewsDefault.size: " + String.valueOf(allNewsDefault.size()));
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(paloma, "translationX", (-currentProgress * 300));
            animator1.setRepeatCount(0);
            animator1.setDuration(0);
            animator1.start();

            paloma.setAlpha(currentProgress * 4f);
            paloma.setVisibility(View.VISIBLE);
            basura.setVisibility(View.INVISIBLE);
        }else
            paloma.setAlpha(0f);

    }
    //////////////////////////////
    /// MARK: Animation move icon when card is swiping to right direction.
    public void animationMoveIconRight(float currentProgress){
        Float D = Math.abs(400 * currentProgress) + 120;
        RelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(D.intValue(), D.intValue());
        params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        int marginTopIcons = Double.valueOf(height * 0.3 - currentProgress * 200).intValue();
        params.setMargins(0, marginTopIcons, 0, 0);
        paloma.setLayoutParams(params);
        paloma.setAlpha(currentProgress * 2);
        paloma.setVisibility(View.VISIBLE);
        basura.setVisibility(View.INVISIBLE);
    }





    /*************************************** CHECK APPS INSTALLED **********************************/

    /// MARK:
    public  boolean twitterInstalled(Activity activity){
        try{
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo("com.twitter.android", 0 );
            return true;
        } catch( PackageManager.NameNotFoundException e ){
            return false;
        }
    }
    ///////////////////////////////////
    /// MARK:
    public  boolean facebookInstalled(Activity activity){
        try{
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo("com.facebook.katana", 0 );
            return true;
        } catch( PackageManager.NameNotFoundException e ){
            return false;
        }
    }
    ///////////////////////////////////



    /*************************************** BACKGROUND CARD **************************************/

    /// MARK: Put a background for the Card
    public void backgroundSwipeStack(View view, final int position, @Nullable final Activity activity) {

        final int nextPosition = 1; //Aumentamos una posicion mas a swipAdapter
        indexBackgroundBackup = position;

        if (position + nextPosition < Swipadaptador.getCount()) {
            view.setVisibility(View.VISIBLE);
            final ImageView imgBgrnd = view.findViewById(R.id.img_noticia_helper);

            final int drawableResourceId = activity.getResources().getIdentifier("vacio", "drawable", activity.getPackageName());

            if (Swipadaptador.getItem(position + nextPosition).getImagen() == "null")
                Glide.with(activity).load(drawableResourceId).into(imgBgrnd);
            else {
                requestImg = (Glide.with(activity)
                        .load(Swipadaptador.getItem(position + nextPosition).getImagen())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                Glide.with(activity).load(drawableResourceId).into(imgBgrnd);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.e(TAG, "backgroundSwipeStack -- Swipadptador.getItem: onResourceReady");





                                mPassingData.sendingImage(drawableToBitmap(resource),
                                        Swipadaptador.getItem(position + nextPosition).getImagen());


                                //mPassingData.sendingImage(imgBgrnd.getDrawingCache()
                                  //      ,Swipadaptador.getItem(position + nextPosition).getImagen());
                                return false;
                            }
                        })
                        .thumbnail(Glide.with(getContext())
                        .load(R.drawable.vacio))
                        .into(imgBgrnd))

                        .getRequest();

                //imgBgrnd.setOn

                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (requestImg.isComplete()) {


                            mPassingData.sendingImage(imgBgrnd.getDrawingCache(),Swipadaptador.getItem(position + nextPosition).getUrl());


                            Log.e(TAG, "backgrndSwipeStadk -- Swipadptador.getItem: requestImg Complete!");
                        }

                        if (requestImg.isRunning()) {
                            Log.e(TAG, "backgrndSwipeStadk -- Swipadptador.getItem: requestImg Running");
                            Glide.with(activity).load(drawableResourceId).into(imgBgrnd);
                        }
                    }
                }, 4000);*/
            }

            TextView txtBgrndTitle = view.findViewById(R.id.titulo_noticia_helper);
            txtBgrndTitle.setText(Swipadaptador.getItem(position + nextPosition).getTitulo());
            TextView txtBgrndAutor = view.findViewById(R.id.autor_helper);
            txtBgrndAutor.setText(Swipadaptador.getItem(position + nextPosition).getAutor());
            spaux.removeAllViews();
            spaux.addView(view);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }
    ///////////////////////////////////


    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



    /***************************************** REQUESTS *******************************************/



    ///////////////////////////////////
    /// MARK:
    public ArrayList<Noticia> getNoticiasaux(String tituloaux, String imagenaux, String urlaux, String descriptionaux, String autoraux, String categoria_localaux) {
        ArrayList<Noticia> listaux = new ArrayList<>();
        try {
            listaux.add(new Noticia(tituloaux, imagenaux, urlaux, descriptionaux, autoraux, categoria_localaux, 0L));
        } catch (Exception e) {
            Log.d(TAG, "getNoticiasaux -- Error aux: Error" + e);
        }
        return listaux;
    }




    ///////////////////////////////////
    public void showNews(final ArrayList<Noticia> defaultListNews,boolean fromMenuSlide,  int catFromMenuSlide) {
        Log.e(TAGTIME, "showNews -- DELAY:" + String.valueOf(getRunningTime()));
        if (getActivity() == null) {
            Log.d(TAG, "showNews -- getActivity: NULL");
        } else {
            Swipadaptador = new SwipAdapter(getActivity(), defaultListNews, getContext(),this);
            if (Swipadaptador.getCount() == 0) {
                Log.e(TAG, "showNews -- Swipadaptador.getCount: No news");
                if (fromMenuSlide){   //Check if is from Menu for show the card!
                    if (( sp != null) && (sp.getVisibility() == View.VISIBLE)){
                        scrollIsAllowed = false;
                       // sp.addView(View.inflate(getContext(),R.layout.custom_toast,null));
                        spaux.setVisibility(View.INVISIBLE);
                        cardviewContainer.setVisibility(View.INVISIBLE);
                        shareFabMain.setVisibility(View.INVISIBLE);

                        isSwiping = true;
                        swipNoNews.removeAllViews();
                        swipNoNews.setX(setAxisXCardView);
                        swipNoNews.setY(setAxisYCardView);
                        swipNoNews.setRotation(0);
                        menuSelectedIndex = catFromMenuSlide; // 0 - 8
                        TextView mtext = viewBckgrndNoNews.findViewById(R.id.txtCurrent);
                        mtext.setText(cateNews[menuSelectedIndex]);
                        swipNoNews.addView(viewBckgrndNoNews);
                        swipNoNews.setVisibility(View.VISIBLE);
                        imgLoaderGif.setVisibility(View.INVISIBLE);
                    }
                }
                flagMenuSlideTapped = fromMenuSlide;

            }
            else {

                if(layoutParamsNews != null)
                    cardviewtest1.setLayoutParams(layoutParamsNews);
                else
                    if (((MainActivity)getActivity()).layoutParamsNewsBackUp != null)
                        cardviewtest1.setLayoutParams(((MainActivity) getActivity()).layoutParamsNewsBackUp);



                swipNoNews.setVisibility(View.INVISIBLE);

                urlNewsLoaded = false;       //urlNewsLoaded: es la bandera para poder descargar las noticias en la Webview.
                Log.e(TAG, "showNews -- Swipadaptador.getCount: News");
                if (indexHelperRemoveNews == 0) {
                    if(sp.getTopView() == null){
                        Log.e(TAG, "showNews --  sp.getTopView.NULL, sp.ResetStack");
                        sp.resetStack();

                    }


                    sp.setVisibility(View.VISIBLE);
                    spaux.setEnabled(false);
                    spaux.setVisibility(View.VISIBLE);

                    cardviewContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "showNews --  sp.setAdapter");
                    int helperY=scrollView.getScrollY();
                    sp.setAdapter(Swipadaptador);

                    if(helperY != 0) {
                        Log.e(TAG, "showNews --  scrollView.setScrollY:"+String.valueOf(helperY));
                        scrollView.setScrollY(helperY);
                    }


                    if (indexBackgroundBackup != 0 )
                        backgroundSwipeStack(viewBckgrnd, indexBackgroundBackup, getActivity()); //position index: inicio de cardview
                    else
                        backgroundSwipeStack(viewBckgrnd, 0, getActivity()); //position 0: inicio de cardview

                    //allNewsHelper = getNoticiasaux(Swipadaptador.getItem(position).getTitulo(), Swipadaptador.getItem(position).getImagen(), Swipadaptador.getItem(position).getUrl(), Swipadaptador.getItem(position).getDescription(), Swipadaptador.getItem(position).getAutor(), Swipadaptador.getItem(position).getCategoria());
                   // allNewsHelper = getNoticiasaux(Swipadaptador.getItem(mainPosition).getTitulo(), Swipadaptador.getItem(mainPosition).getImagen(), Swipadaptador.getItem(mainPosition).getUrl(), Swipadaptador.getItem(mainPosition).getDescription(), Swipadaptador.getItem(mainPosition).getAutor(), Swipadaptador.getItem(mainPosition).getCategoria());
                    allNewsHelper = getNoticiasaux(Swipadaptador.getItem(sp.getCurrentPosition()).getTitulo(),
                            Swipadaptador.getItem(sp.getCurrentPosition()).getImagen(),
                            Swipadaptador.getItem(sp.getCurrentPosition()).getUrl(),
                            Swipadaptador.getItem(sp.getCurrentPosition()).getDescription(),
                            Swipadaptador.getItem(sp.getCurrentPosition()).getAutor(),
                            Swipadaptador.getItem(sp.getCurrentPosition()).getCategoria());


                    if (getActivity() != null) {
                        Swipadaptadoraux = new SwipAdapterBackCard(getActivity(),allNewsHelper,getApplicationContext());
                        Swipadaptadoraux.setImageListener(this);
                    }
                    final String newsaux = defaultListNews.get(sp.getCurrentPosition()).getUrl();
                    shareFabMain.setVisibility(View.VISIBLE);
                    shareFabMain.setEnabled(true);
                    scrollView.smoothScrollTo(0, 0);
                    sp.setListener(new SwipeStack.SwipeStackListener() {
                        @Override
                        public void onViewSwipedToLeft(final int position) {
                            Log.d(TAG, "showNews -- sp.onViewSwipedToLeft Position:" + String.valueOf(position));
                            Log.d(TAG, "showsNews -- sp.onViewSwipedToLeft sp.getCurrentPosition: " + String.valueOf(sp.getCurrentPosition()));
                            currentIndex = position;
                            //currentIndex = position + 1;
                            mainPosition = position+1;
//                            web.onPause();
                            if (web != null)
                                web.destroy();




                            indexHelperRemoveNews = indexHelperRemoveNews + 1;
                            Log.d(TAG, "showsNews -- sp.onViewSwipedToLeft indexHelperRemoveNews: " + String.valueOf(indexHelperRemoveNews));
                            setupOnViewSwipedToLeft(position);
                        }
                        @Override
                        public void onViewSwipedToRight(final int position) {
                            Log.d(TAG, "showNews -- sp.onViewSwipedRight Position:" + String.valueOf(position));
                            Log.d(TAG, "showsNews -- sp.onViewSwipedRight sp.getCurrentPosition: " + String.valueOf(sp.getCurrentPosition()));
                            //currentIndex = position + 1;
                            currentIndex = position;
                            mainPosition = position +1;
                            //web.onPause();
                            if (web != null)
                                web.destroy();

                            indexHelperRemoveNews = indexHelperRemoveNews + 1;
                            Log.d(TAG, "showsNews -- sp.onViewSwipedToRight indexHelperRemoveNews: " + String.valueOf(indexHelperRemoveNews));
                            setupOnViewSwipedToRight(position);
                        }
                        @Override
                        public void onStackEmpty() {
                            deleteCache(getContext());
                            MemoryCard.clear();  //The counting of position is reset.
                            sp.resetStack();   //Restart the news.
                            mainPosition = 0;
                            backgroundSwipeStack(viewBckgrnd, 0, getActivity()); //position 0: inicio de cardview
                            Log.d(TAG,  "showNews -- sp.OnStackEmpty.TRUE");
                        }
                    });
                }
            }
        }
    }


    ///////////////////////////////////
    /// MARK:
    public AsyncHttpClient requestHttpClient() {
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(5000);
        client.setResponseTimeout(5000);
        client.setTimeout(5000);
        client.setMaxRetriesAndTimeout(1, 100);
        return client;
    }
    ///////////////////////////////////


    /************************************** WEBVIEW CONFIG ****************************************/



    ///////////////////////////////
    /// MARK: Enter new card for slide up    Prueba 1
    public void swipeToUp() {

        urlNewsLoaded = true;
        ///////////////////////////////////  WebView:content_cardview.xml
        web = sp.getTopView().findViewById(R.id.webviewMain1);
        progressBar = sp.getTopView().findViewById(R.id.progressBarLoaderWeb);

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setSupportZoom(true);

        web.setWebViewClient(new WebViewClient());
        web.clearCache(true);
        web.getContentHeight();
        web.setEnabled(true);
        if(web!=null) {
            RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, web.getHeight());
            heightscroll.setLayoutParams(layoutParams3);
        }

        Log.d(TAG, "swipeToUp -- web.isActivated: " + String.valueOf(web.isActivated()) + ",web.isEnable:" + String.valueOf(web.isEnabled()));
        web.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e(TAG, "swipeToUp -- web.onTouch: ");
                v.getParent().requestDisallowInterceptTouchEvent(true);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                        Log.d(TAG,"web: -- ACTION: CANCEL ");
                        eventsActionCancel(event);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG,"web: -- ACTION: DOWN ");
                        eventsActionDown(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG,"web: -- ACTION: UP ");
                        eventsActionUp(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG,"web: -- ACTION: MOVE ");
                        eventsActionMove(event, v);


                        //GLES32.glDisableVertexAttribArray(0);
                        //web.requestFocus();
                        return false;
                }
                return true;
            }
        });


        Log.d(TAG, "swipeToUp -- web.loadUrl, sp.getCurrentPosition: " + String.valueOf(sp.getCurrentPosition()));
        Log.d(TAG, "swipeToUp -- web.loadUrl, allNewsDefault.get(: "
                //+ String.valueOf(position)+").getUrl:"
                //+ String.valueOf(allNewsDefault.get(position).getUrl()));
                + String.valueOf(mainPosition)+").getUrl:"
                + String.valueOf(allNewsDefault.get(mainPosition).getUrl()));

        //web.loadUrl(allNewsDefault.get(position).getUrl());
        //web.loadUrl(allNewsDefault.get(mainPosition).getUrl());

        progressBar.setMax(100);
        progressBar.setProgress(1);

        web.loadUrl(allNewsDefault.get(sp.getCurrentPosition()).getUrl());

        Log.d(TAG, "swipeToUp -- web.loadUrl, sp.getCurrentPosition: " + String.valueOf(sp.getCurrentPosition()));

        //    web.loadUrl(allNewsDefault.get(sp.getCurrentPosition()).getUrl());
        web.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d(TAG, "swipeToUp -- web.onScrollChange, scrollY: " + String.valueOf(scrollY));
                if (scrollY == 0) {
                    flagWebView = true;
                } else {
                }
            }
        });


        web.setVerticalScrollBarEnabled(true);




        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
            }
        });

        web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);

                web.setAlpha(1f);

            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "swipeToUp -- web.WebViewClient, onPageFinished.TRUE! ");
                Log.d(TAG, "swipeToUp -- onPageFinished, web.getHeight:" + String.valueOf(web.getHeight()) + ", web.getBottom:" + String.valueOf(web.getBottom()));
                Log.d(TAG, "swipeToUp -- onPageFinished, Change{ web.getContentHeight:" + String.valueOf(web.getContentHeight()) + ", web.getBottom:" + String.valueOf(web.getBottom()));
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(cardviewContainer.getWidth(), web.getContentHeight());
                web.setLayoutParams(layoutParams);
                web.invalidate();
                Log.d(TAG, "swipeToUp -- onPageFinished, Change{ web.getContentHeight:" + String.valueOf(web.getContentHeight()) + ", web.getBottom:" + String.valueOf(web.getBottom()));
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                web.setAlpha(0.f);
                Toast.makeText(getContext(), "Oh no! Url missing", Toast.LENGTH_SHORT).show();
              //  Toast.makeText(getContext(), "Oh no! Url missing" + description, Toast.LENGTH_SHORT).show();

            }
        });
        Log.d(TAG, "swipeToUp -- web.getBottom:" + String.valueOf(web.getBottom()));
        //scrollView.smoothScrollTo(0, 0);
        float contentHeight = web.getContentHeight();
        //scrollView.smoothScrollTo(0, 0);
        Log.d(TAG, "swipeToUp -- web.getcontentHeight: " + String.valueOf(contentHeight));
    }




    ///////////////////////////////
    /// MARK: Show alert "no news to show"
    public void showAlertNoNews() {

    }



    /***************************************** ACTIONS IN CARD ************************************/

    ///////////////////////////////////
    /// MARK:
    public void setupOnViewSwipedToRight(int position){
        setupOnViewSwiped(position);
        saveNewsGeneric(Swipadaptador,position,true);
    }
    ///////////////////////////////////
    /// MARK:
    public void setupOnViewSwipedToLeft(int position){
        setupOnViewSwiped(position);
        saveNewsGeneric(Swipadaptador,position,false);
    }


    ///////////////////////////////////
    /// MARK:
    public void setupOnViewSwiped(int position){
        urlNewsLoaded = false;
        //saveMemoryCard(position);  //6/Feb/2019
        scrollView.smoothScrollTo(0, 0);
        backgroundSwipeStack(viewBckgrnd, position + 1, getActivity());
    }

    /*********************************** TOUCH EVENTS IN CARD *************************************/

    ///////////////////////////////////
    /// MARK:
    public void eventsActionCancel(MotionEvent ev) {
        Log.e("HomeFragment", "eventsActionCancel -- ACTION CANCELED!");
        animationFinish(basura, paloma);
        cardviewContainer.setY(setAxisYCardView);
        cardviewContainer.setX(setAxisXCardView);
        cardviewContainer.setRotation(0);
        Likes = 0;
    }
    ///////////////////////////////////
    /// MARK:
    public void eventsActionDown(MotionEvent ev) {
        Log.e("HomeFragment", "eventsActionDown -- ACTION DOWN!");
        oldX = (int) ev.getRawX();
        oldY = (int) ev.getRawY();
        startPointX = oldX;
        startPointY = oldY;
        x = startPointX;
        y = startPointY;
        Likes = 0;
    }
    ///////////////////////////////////
    /// MARK:
    public void eventsActionMove(MotionEvent ev, View v) {
        Log.e("HomeFragment", "eventsActionMove -- ACTION MOVE!");

        posY = (int) ev.getRawY();
        posX = (int) ev.getRawX();
        diffPosY = diffPosY + Math.abs(posY - oldY);
        diffPosX =  diffPosX + Math.abs(posX - oldX);
        diffFastGesture =  posX - oldX;


        oldX = posX;
        oldY = posY;
        x_cord = (int) ev.getRawX();
        y_cord = (int) ev.getRawY();

        Log.e(TAG, "eventsActionMove -- ACTION MOVE!  diffPosX:"+String.valueOf(diffPosX));
        Log.e(TAG, "eventsActionMove -- ACTION MOVE!  diffPosY"+String.valueOf(diffPosY));
        Log.e(TAG, "eventsActionMove -- ACTION MOVE!  posX"+String.valueOf(posX));
        Log.e(TAG, "eventsActionMove -- ACTION MOVE!  posY"+String.valueOf(posY));


        if (!isSwiping && !isScrolling && diffPosX!=0 && diffPosY!=0) {
            if  (diffPosX > 3*diffPosY ||  diffPosY > 3*diffPosX ) {

                if (Math.abs(diffPosX) > Math.abs(diffPosY)) {
                    Log.e(TAG, "eventsActionMove -- ACTION MOVE!  SWIPING!");

                    isSwiping = true;
                    isScrolling = false;
                    scrollIsAllowed = false;
                    diffPosY = 0;
                    diffPosX = 0;
                } else {

                    Log.e(TAG, "eventsActionMove -- ACTION MOVE!  SCROLLING!");

                        isSwiping = false;
                        isScrolling = true;
//                          if (allNewsDefault.size() != 0){
                          if (allNewsDefault.size() != 0){
                            scrollIsAllowed = true;
                          }else{
                           scrollIsAllowed = false;
                         }

                        //scrollIsAllowed = true;
                        diffPosY = 0;
                        diffPosX = 0;
                }
            }

            //if ((Math.abs(diffPosY) -  Math.abs(diffPosX)) > 0 && Math.abs(diffPosX) > 12){
            if (Math.abs(diffPosX) > 15){

                Log.e(TAG, "eventsActionMove -- ACTION MOVE!  DIAGONAL!");
                isSwiping = true;
                isScrolling = false;
                scrollIsAllowed = false;

                diffPosY = 0;
                diffPosX = 0;
            }
        }
        if (isSwiping) {
            Log.e(TAG, "eventsActionMove -- isSwiping.TRUE: X WIN");
            scrollIsAllowed = false;
            cardviewContainer.getBackground().setColorFilter(ContextCompat.getColor(getContext(),R.color.mainBlue),PorterDuff.Mode.MULTIPLY);
            cardviewContainer.getBackground().setAlpha(255);
            shareFabMain.hide();
            cardviewContainer.setX(x_cord - x);
            currentProgressSwiping =  (x_cord - x) / 1000;
            animationSwipeProgress((float) (x_cord - x) / 1000);
            cardviewContainer.setY(y_cord - y + topOffset * 4);
            if (x_cord >= screenCenter) {
                cardviewContainer.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 128)));
                if (x_cord > (screenCenter + (screenCenter / 3))) {
                    //   tvLike.setAlpha(1); 
                    if (x_cord > (windowwidth - (screenCenter / 3))) {
                        Likes = 2;
                    } else
                        Likes = 0;
                } else {
                    Likes = 0;
                }
            } else {
                cardviewContainer.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 128)));
                if (x_cord < (screenCenter / 3)) {
                    if (x_cord < screenCenter / 3) {
                        Likes = 1;
                    } else
                        Likes = 0;
                } else {
                    Likes = 0;
                }
            }
            if (( Math.abs(screenCenter - x_cord)) > screenCenter/2) { //Swipe si rebasa 1/4 del screen a cualquier lado
                if (Math.abs(x_cord - startPointX) > (screenCenter)) {
                    if ((x_cord - startPointX) > 0) {
                        Likes = 2;
                    } else
                        Likes = 1;
                }
            }

            if (Math.abs(diffFastGesture) > (110 )) {  //Swipe muy rapido
                if ((x_cord - startPointX) > 0) {
                    Likes = 2;   //Liked
                } else
                    Likes = 1;
            }


            if (Math.abs(x_cord - x) > (150 )) {  //Swipe diferencia de X
                if ((x_cord - x) > 0) {
                    Log.e(TAG, "eventsActionMove -- isSwiping.TRUE: X WIN, Swipe by difference LIKE");
                    Likes = 2;   //Liked
                } else {
                    Log.e(TAG, "eventsActionMove -- isSwiping.TRUE: X WIN, Swipe by difference DISMISS");
                    Likes = 1;
                }
            }




        } else {
            //            if (allNewsDefault.size() != 0){

            if (allNewsDefault.size() != 0){
                scrollIsAllowed = true;
            }else{
                scrollIsAllowed = false;
            }
            Log.e(TAG, "eventsActionMove -- isSwiping.FALSE: Y WIN");
            v.getParent().requestDisallowInterceptTouchEvent(false);
        }
    }
    ///////////////////////////////////
    /// MARK:
    public void eventsActionUp(MotionEvent ev) {
        Log.e(TAG, "eventsActionUp  -- ACTION UP! ");

        animationFinish(basura, paloma);

        diffPosY = 0;
        diffPosX = 0;


        x_cord = (int) ev.getRawX();
        y_cord = (int) ev.getRawY();

        if ((Math.abs(startPointX  - x_cord) < 8) && (Math.abs(startPointY - y_cord) < 8) && (!isSwiping)) {
            Log.e(TAG, "eventsActionUp  -- CARD CLICKED! ");
            Log.e(TAG, "eventsActionUp  -- CARD CLICKED! Swipadaptador.mostrarNoticiasView("
                    +String.valueOf(sp.getCurrentPosition())+")");

            Swipadaptador.mostrarNoticiasView(sp.getCurrentPosition());


           /* if (!firstInitialCard){
                if (sp.getCurrentPosition() - 1 < 0){
                    Swipadaptador.mostrarNoticiasView(0);
                }else {
                    Swipadaptador.mostrarNoticiasView(sp.getCurrentPosition() - 1);
                }
                Log.e("HomeFragment", "eventsActionUp  -- sp.getCurrentPosition - 1 :"+String.valueOf(sp.getCurrentPosition() - 1));
                firstInitialCard = true;
            }else {
                Swipadaptador.mostrarNoticiasView(sp.getCurrentPosition());
            }*/

            Log.e(TAG, "eventsActionUp  -- sp.getCurrentPosition:"+String.valueOf(sp.getCurrentPosition()));
            Log.e(TAG, "eventsActionUp  -- currentIndex:"+String.valueOf(currentIndex));

            cardviewContainer.setScrollY(0);
//          if (allNewsDefault.size() != 0){
            if (allNewsDefault.size() != 0){
                scrollIsAllowed = true;
            }else{
                scrollIsAllowed = false;
            }
            urlNewsLoaded = false;
            indexHelperRemoveNews = 0;
        }

        isSwiping = false;
        isScrolling = false;
        ObjectAnimator moveX;
        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(300);
        if (Likes == 0) {
            Log.e(TAG, "eventsActionUp  -- Nothing");
            cardviewContainer.setX(setAxisXCardView);
            cardviewContainer.setY(setAxisYCardView);
            cardviewContainer.setRotation(0);

            if (bottomFabs.getVisibility() == View.INVISIBLE)
            shareFabMain.show();

        } else if (Likes == 1) {
            Log.e(TAG, "eventsActionUp  -- UNLIKE");

            moveX = ObjectAnimator.ofFloat(cardviewContainer, "x", x_cord - 1000);
            animators.playTogether(moveX);


            cardSwipeRight = false;
        } else if (Likes == 2) {
            Log.e(TAG, "eventsActionUp  -- LIKED");
            moveX = ObjectAnimator.ofFloat(cardviewContainer, "x", x_cord + 1000);
            animators.playTogether(moveX);
            cardSwipeRight = true;
        }
        if (Likes != 0) {
            cardviewtest1.setLayoutParams(layoutParamsNews);
            urlNewsLoadedOption = false;
            urlNewsLoaded = false;
            if (bottomFabs.getVisibility() == View.INVISIBLE)
                shareFabMain.show();


                animators.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation){
                    super.onAnimationStart(animation);
                    scrollView.setScrollY(0);
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    sp.getTopView().setVisibility(View.INVISIBLE);
                    if (cardSwipeRight) {
                        sp.swipeTopViewToRight();
                    } else {
                        sp.swipeTopViewToLeft();
                    }
                    isSwiping = false;
                    cardviewContainer.setRotation(0);
                    cardviewContainer.setY(setAxisYCardView);
                    cardviewContainer.setX(setAxisXCardView);
                }
            });
            animators.start();
        }
    }
    ///////////////////////////////////
    /// MARK:
    public void eventsActionMoveCardNoNews(MotionEvent ev,CardView targetCard) {
        Log.e(TAG, "eventsActionMoveCardNoNews -- ACTION MOVE!");

        posY = (int) ev.getRawY();
        posX = (int) ev.getRawX();
        diffPosY = diffPosY + Math.abs(posY - oldY);
        diffPosX =  diffPosX + Math.abs(posX - oldX);
        diffFastGesture =  posX - oldX;


        oldX = posX;
        oldY = posY;
        x_cord = (int) ev.getRawX();
        y_cord = (int) ev.getRawY();



        if (isSwiping) {
            Log.e(TAG, "eventsActionMove -- isSwiping.TRUE: X WIN");

            targetCard.setX(x_cord - x);
            currentProgressSwiping =  (x_cord - x) / 1000;
            //animationSwipeProgress((float) (x_cord - x) / 1000);
            targetCard.setY(y_cord - y + topOffset * 4);
            if (x_cord >= screenCenter) {
                targetCard.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 128)));
                if (x_cord > (screenCenter + (screenCenter / 3))) {
                    //   tvLike.setAlpha(1); 
                    if (x_cord > (windowwidth - (screenCenter / 3))) {
                        Likes = 2;
                    } else
                        Likes = 0;
                } else {
                    Likes = 0;
                }
            } else {
                targetCard.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 128)));
                if (x_cord < (screenCenter / 3)) {
                    if (x_cord < screenCenter / 3) {
                        Likes = 1;
                    } else
                        Likes = 0;
                } else {
                    Likes = 0;
                }
            }
            if (( Math.abs(screenCenter - x_cord)) > screenCenter/2) { //Swipe si rebasa 1/4 del screen a cualquier lado
                if (Math.abs(x_cord - startPointX) > (screenCenter)) {
                    if ((x_cord - startPointX) > 0) {
                        Likes = 2;
                    } else
                        Likes = 1;
                }
            }

            if (Math.abs(diffFastGesture) > (110 )) {  //Swipe muy rapido
                if ((x_cord - startPointX) > 0) {
                    Likes = 2;   //Liked
                } else
                    Likes = 1;
            }


            if (Math.abs(x_cord - x) > (150 )) {  //Swipe diferencia de X
                if ((x_cord - x) > 0) {
                    Log.e(TAG, "eventsActionMove -- isSwiping.TRUE: X WIN, Swipe by difference LIKE");
                    Likes = 2;   //Liked
                } else {
                    Log.e(TAG, "eventsActionMove -- isSwiping.TRUE: X WIN, Swipe by difference DISMISS");
                    Likes = 1;
                }
            }
        }
    }


    ///////////////////////////////////
    /// MARK: Todo:Checar aqui para hacer un requestSpecific y showMenu despues
    public void eventsActionUpCardNoNews(MotionEvent ev,final CardView targetCard) {
        Log.e(TAG, "eventsActionUpCardNoNews  -- ACTION UP! ");

        diffPosY = 0;
        diffPosX = 0;

        //animationFinish(basura, paloma);
        x_cord = (int) ev.getRawX();
        y_cord = (int) ev.getRawY();

        ObjectAnimator moveX = ObjectAnimator.ofFloat(swipNoNews,"translationX",x_cord);

        if (Likes == 0) {
            Log.e(TAG, "eventsActionUpCardNoNews  -- Nothing");
            targetCard.setX(setAxisXCardView);
            targetCard.setY(setAxisYCardView);
            targetCard.setRotation(0);



        } else if (Likes == 1) {
            Log.e(TAG, "eventsActionUpCardNoNews  -- UNLIKE");
            moveX = ObjectAnimator.ofFloat(swipNoNews, "translationX", x_cord - 1000);
            moveX.setDuration(300);
            moveX.start();
            //animators.playTogether(moveX);ç

            if (menuSelectedIndex - 1 < 0)
                menuSelectedIndex = 8;
            else
                menuSelectedIndex = menuSelectedIndex - 1;

            if((MainActivity) getActivity() != null)
                ((MainActivity) getActivity()).specificRecursive(menuSelectedIndex);
            //showNewsMenuSlide(menuSelectedIndex);
            Log.e(TAG, "eventsActionUpCardNoNews  -- UNLIKE FINISH");

        } else if (Likes == 2) {
            Log.e(TAG, "eventsActionUpCardNoNews  -- LIKED");
            moveX = ObjectAnimator.ofFloat(swipNoNews, "translationX", x_cord + 1000);
            moveX.setDuration(300);
            moveX.start();
            //animators.playTogether(moveX);


            if (menuSelectedIndex + 1 > 8)
                menuSelectedIndex = 0;
            else
                menuSelectedIndex = menuSelectedIndex + 1;

            if((MainActivity) getActivity() != null)
                ((MainActivity) getActivity()).specificRecursive(menuSelectedIndex);
            //showNewsMenuSlide(menuSelectedIndex);
            Log.e(TAG, "eventsActionUpCardNoNews  -- LIKED FINISH");

        }
        if (Likes != 0) {

            moveX.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    swipNoNews.setVisibility(View.INVISIBLE);
                    imgLoaderGif.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /***************************************** DATABASE *******************************************/

    ///////////////////////////////////
    /// MARK: Save news for BackUp
    public void registrarNoticiasRecuperar(String titulo, String imagen, String url, String autor, String categoria) {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getWritableDatabase();
        String [] parametros = {url};
        Cursor cursor = db.rawQuery( "SELECT url FROM "+Utilidades.TABLA_RECUPERAR+" WHERE "+Utilidades.URL+" =?", parametros);
        if(cursor.getCount()==0)
        {
            Log.d(TAG," Registro Noticias Recuperar:");

            Log.d(TAG, " NOTICIA: NO HAY");
            ContentValues valores = new ContentValues();
            valores.put(Utilidades.TITULO,titulo);
            valores.put(Utilidades.IMAGEN,imagen);
            valores.put(Utilidades.URL,url);
            valores.put(Utilidades.AUTOR,autor);
            valores.put(Utilidades.CATEGORIA,categoria);
            valores.put(Utilidades.TIEMPO, addTime(System.currentTimeMillis()));
            db.insert(Utilidades.TABLA_RECUPERAR,null,valores);
            db.close();
        }else{
            Log.d(TAG, "NOTICIA: REPETIDA RECUPERAR"+String.valueOf(titulo));
            db.close();
        }
        conn.close();
    }

    ///////////////////////////////////
    /// MARK: Save news
    public void registrarNoticias(String titulo, String imagen, String url, String autor, String categoria) {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(), "db_noticias", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();

        String[] parametros = {url};

        Cursor cursor = db.rawQuery("SELECT url FROM " + Utilidades.TABLA_NOTICIA + " WHERE " + Utilidades.URL + " =?", parametros);
        if (cursor.getCount() == 0) {
            Log.d(TAG, "registrarNoticias -- cursor.getCount == 0 (No News saved, saving..)");
            ContentValues valores = new ContentValues();
            valores.put(Utilidades.TITULO, titulo);
            valores.put(Utilidades.IMAGEN, imagen);
            valores.put(Utilidades.URL, url);
            valores.put(Utilidades.AUTOR, autor);
            valores.put(Utilidades.CATEGORIA, categoria);
            valores.put(Utilidades.TIEMPO, 0);
            db.insert(Utilidades.TABLA_NOTICIA, null, valores);
            db.close();
        } else {
            Log.d(TAG, "registrarNoticias -- cursor.getCount != 0 (News repeated)");
            db.close();
        }
        conn.close();
    }

    ///////////////////////////////
    /// MARK: Save news to the Database Favorites or Recover.
    public void saveNewsGeneric(final SwipAdapter targetAdapter, final int positionForSave,boolean forFavorites) {
        String titulo = targetAdapter.getItem(positionForSave).getTitulo();
        String url = targetAdapter.getItem(positionForSave).getUrl();
        String imagen = targetAdapter.getItem(positionForSave).getImagen();
        String autor = targetAdapter.getItem(positionForSave).getAutor();
        String categoria = targetAdapter.getItem(positionForSave).getCategoria();

        if(forFavorites)
            registrarNoticias(titulo, imagen, url, autor, categoria);
        else
            registrarNoticiasRecuperar(titulo, imagen, url, autor, categoria);
    }



    /*************************************** SEND NEWS BY INTENT **********************************/

    ///////////////////////////////////
    /// MARK:
    public void sendNewsByIntentFB(Activity mActivity) {
        ShareDialog shareDialog;
        shareDialog = new ShareDialog(mActivity);

        CallbackManager callbackManager;
        callbackManager = CallbackManager.Factory.create();

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getContext(), "Share successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "Share canceled!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Uri uri = Uri.parse(String.valueOf(allNewsDefault.get(sp.getCurrentPosition()).getUrl()));

        Uri uri = Uri.parse(String.valueOf(allNewsDefault.get(sp.getCurrentPosition()).getUrl()));
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote("Cidnews App")
                .setContentUrl(uri)
                .build();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(linkContent);
        }
    }

    ///////////////////////////////////
    /// MARK:
    public void sendNewsByIntentTwitter(Activity mActivity) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        //sendIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(allNewsDefault.get(sp.getCurrentPosition()).getUrl()));

        sendIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(allNewsDefault.get(sp.getCurrentPosition()).getUrl()));
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.twitter.android");
        mActivity.startActivity(sendIntent);
    }

    ///////////////////////////////////
    /// MARK:
    public void sendNewsByIntentWApp(Activity mActivity) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(allNewsDefault.get(sp.getCurrentPosition()).getUrl()));

        sendIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(allNewsDefault.get(sp.getCurrentPosition()).getUrl()));
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        mActivity.startActivity(sendIntent);
    }

    ///////////////////////////////////
    /// MARK:   General Intent
    public void sendNewsByIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        // sendIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(allNewsDefault.get(sp.getCurrentPosition()).getUrl()));

        sendIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(allNewsDefault.get(sp.getCurrentPosition()).getUrl()));
        sendIntent.setType("text/plain");
        getActivity().startActivity(Intent.createChooser(sendIntent, "Powered by" + " Cidnews"));
    }

    /******************************************** HELPERS  *****************************************/

    ///////////////////////////////////
    /// MARK: Add time for recover news
    public long addTime(long startTime){
        Log.e(TAG, "DATETIME: "+String.valueOf(DateFormat.getInstance().format(startTime)));
        //long halfAnHourLater = startTime + 1800000;
        Log.e(TAG, "WAIT UNTIL: "+String.valueOf(DateFormat.getInstance().format(startTime + 1800000)));
        return startTime + 3600000*24*2; //30Min  1 Min: 60'000
    }

    /// MARK: Add news for save in newsApiSaved   ****************!
    public void addNewsForSave() {
        String title, imgUrl, url, autor, cat;
        Log.d(TAG, "addNewsForSave -- Saving..");
        for (int i = 0; i < allNewsDefault.size(); i++) {
            title = allNewsDefault.get(i).getTitulo();
            imgUrl = allNewsDefault.get(i).getImagen();
            url = allNewsDefault.get(i).getUrl();
            autor = allNewsDefault.get(i).getAutor();
            cat = allNewsDefault.get(i).getCategoria();
            MyObject object = new MyObject(title, imgUrl, url, autor, cat);
            newsApiSaved.add(object);
        }
        //firstInitialCard = true;
        Log.d(TAG, "addNewsForSave  -- newsApiSaved.size: "+String.valueOf(newsApiSaved.size()));
    }

    ///////////////////////////////////
    /// MARK:
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////
    /// MARK:
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }




    /* ****************************************** NO USE ******************************************/


    ///////////////////////////////
    /// MARK: Custom snackbar.
    public Snackbar createSnackbar(View target, String direction) {
        Snackbar snackbar = Snackbar.make(target, direction, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.BLUE);
        View snackbarLayout = snackbar.getView();
        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.mainPink));
        FrameLayout.LayoutParams layparams = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
        layparams.gravity = Gravity.BOTTOM;
        MainActivity mainActivity = (MainActivity) getActivity();
        layparams.setMargins(0, 0, 0, mainActivity.menuNavigation.getMeasuredHeight());
        snackbarLayout.setLayoutParams(layparams);
        snackbar.setActionTextColor(Color.BLUE);
        snackbar.setActionTextColor(0);
        return snackbar;
    }

    ///////////////////////////////////
    /// MARK:
    private void consultNoNewsShow(String categoria) {

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getReadableDatabase();
        String [] parametros = {categoria.toString()};
        Cursor cursor = db.rawQuery("SELECT * FROM "+Utilidades.TABLA_RECUPERAR+" WHERE "+Utilidades.CATEGORIA+" LIKE ?",parametros);
        Noticia noticia = null;
        if(cursor.getCount()==0)
        {
            Log.e(TAG, "consultNoNewsShow: EMPTY");
            noNewsForShow = null;
        }
        else
        {
            Log.e(TAG, "consultNoNewsShow: NEWS!");
            while (cursor.moveToNext()) {
                noticia = new Noticia(cursor.getString(0), cursor.getString(1), cursor.getString(2), "", cursor.getString(3), cursor.getString(4), cursor.getLong(5));
                noNewsForShow.add(noticia);
            }
        }
        cursor.close();
        db.close();
        conn.close();
    }

    ///////////////////////////////////
    /// MARK:   SQL
    private int consultarEstado(String categoria) {
        String[] parametros = {categoria};
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(), "db_noticias", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        int c = 0;
        try {
            Cursor cursor = db.rawQuery("SELECT estado FROM " + Utilidades.TABLA_PREFERENCIA + " where " + Utilidades.CATEGORIA + " =?", parametros);
            cursor.moveToFirst();
            db.close();
            conn.close();
            c = cursor.getInt(0);
            return cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }


    ///////////////////////////////////
    /// MARK:   Show news from a chosen category from menu Slide
    private void menuSlide(int position) {
        if((allNewsHelper != null)&&allNewsDefault!=null) {
            allNewsDefault.clear();
            allNewsHelper.clear();
        }
        cardviewtest1.setVisibility(View.INVISIBLE);
        cardviewContainer.setVisibility(View.INVISIBLE);
        sp.setVisibility(View.INVISIBLE);
        spaux.setVisibility(View.INVISIBLE);
        shareFabMain.setVisibility(View.INVISIBLE);

        indexHelperRemoveNews = 0;  //indexHelperRemoveNews es utilizado para saber si el usuario ha movido una Swipecard, ayuda en el inicio de cargar noticias.
        MemoryLoadIndex = position;
        //loadDefaultNews(position);

        Log.e(TAG, "menuSlide -- position:"+String.valueOf(position));
    }

    ///////////////////////////////////
    /// MARK:
    private void downloadAllNews(){
        //getInitialNews();
    }


    ///////////////////////////////////
    /// MARK:
    public void saveMemoryCard(Integer position) {
        Log.d(TAG, "saveMemoryCard -- allnewsDefault.get(position:"
                +String.valueOf(position)
                +").getUrl: "
                + allNewsDefault.get(position).getUrl());

        boolean flagHelper = false;
        for (int i = 0; i < MemoryCard.size(); i++) {
            if (MemoryCard.get(i).equalsIgnoreCase(allNewsDefault.get(position).getUrl())) {
                flagHelper = true;
                Log.d(TAG, "saveMemoryCard -- NewsREPEATED! MemoryCard("+String.valueOf(i)+").getUrl: " + MemoryCard.get(i));
                i = MemoryCard.size();
                //Si es igual MemoryCard con allNewsDefault, ya existe en MemoryCard no hay necesidad de agregarla
            } else {
                flagHelper = false;
            }
        }
        if (!flagHelper){
            Log.d(TAG, "saveMemoryCard -- MemoryCard.add: " + allNewsDefault.get(position).getUrl()+ " (url added)");
            MemoryCard.add(allNewsDefault.get(position).getUrl());
            Log.d(TAG, "saveMemoryCard -- MemoryCard.size: " + MemoryCard.size());
        } else {
            Log.d(TAG, "saveMemoryCard -- MemoryCard.size: " + MemoryCard.size() + "(url NO added)");
        }
    }
    ///////////////////////////////////
    /// MARK:
    public void showNewsWithBackupList(final ArrayList<Noticia> defaultListNews) {
        Log.e(TAGTIME, "showNewsWithBackupList -- DELAY:" + String.valueOf(getRunningTime()));
        if (getActivity() == null) {
            Log.d(TAG, "showNewsWithBackUpList -- getActivity.NULL");

        } else {
            Swipadaptador = new SwipAdapter(getActivity(), defaultListNews, getContext(),this);
            if (Swipadaptador.getCount() == 0) {
                Log.d(TAG, "showNewsWithBackupList -- Swipadaptador.getCount:0");
            } else {
                urlNewsLoaded = false;       //urlNewsLoaded: es la bandera para poder descargar las noticias en la Webview.
                Log.d(TAG, "showNewsWithBackupList -- Swipadaptador.getCount:"+String.valueOf(Swipadaptador.getCount()));
                mainPosition = 0;

                if(sp.getTopView() == null){
                    Log.d(TAG, "showNewsWithBackupList -- sp.getTopView:NULL (sp.resetStack())");
                    sp.resetStack();
                }
                sp.setVisibility(View.VISIBLE);
                spaux.setEnabled(false);
                spaux.setVisibility(View.VISIBLE);
                Log.d(TAG, "showNewsWithBackupList -- sp.resetStack");
                sp.resetStack();

                if(!firstInitialCard) {
                    Log.d(TAG, "showNewsWithBackupList -- firstInitialCard:"+String.valueOf(firstInitialCard));
                    Log.d(TAG, "showNewsWithBackupList --firstInitialCard (sp.resetStack())");
                    sp.resetStack();
                    firstInitialCard = false;
                }
                cardviewContainer.setVisibility(View.VISIBLE);
                Log.d(TAG, "swipeToUp -- (showNewsWithBackupList)sp.resetStack");

                sp.setAdapter(Swipadaptador);
                backgroundSwipeStack(viewBckgrnd, indexBackgroundBackup, getActivity()); //position 0: inicio de cardview
                //allNewsHelper = getNoticiasaux(Swipadaptador.getItem(position).getTitulo(), Swipadaptador.getItem(position).getImagen(), Swipadaptador.getItem(position).getUrl(), Swipadaptador.getItem(position).getDescription(), Swipadaptador.getItem(position).getAutor(), Swipadaptador.getItem(position).getCategoria());


                allNewsHelper = getNoticiasaux(Swipadaptador.getItem( sp.getCurrentPosition() + 1).getTitulo()
                        , Swipadaptador.getItem( sp.getCurrentPosition() + 1).getImagen(),
                        Swipadaptador.getItem(sp.getCurrentPosition() + 1).getUrl(),
                        Swipadaptador.getItem(sp.getCurrentPosition() + 1).getDescription(),
                        Swipadaptador.getItem(sp.getCurrentPosition() + 1).getAutor(),
                        Swipadaptador.getItem(sp.getCurrentPosition() + 1).getCategoria());


                if (getActivity() != null) {
                    Swipadaptadoraux = new SwipAdapterBackCard(getActivity(), allNewsHelper, getContext());
                    Swipadaptadoraux.setImageListener(HomeFragment.this);

                }
                final String newsaux = defaultListNews.get(sp.getCurrentPosition()).getUrl();
                shareFabMain.setVisibility(View.VISIBLE);
                shareFabMain.setEnabled(true);
                scrollView.smoothScrollTo(0, 0);
                sp.setListener(new SwipeStack.SwipeStackListener() {
                    @Override
                    public void onViewSwipedToLeft(final int position) {
                        Log.d(TAG, "showNewsWithBackupList --  sp.onViewSwipedToLeft: Position:" + String.valueOf(position));
                        Log.d(TAG, "showNewsWithBackupList -- sp.onViewSwipedToLeft: Sp.currentPosition: " + String.valueOf(sp.getCurrentPosition()));
                        indexHelperRemoveNews = indexHelperRemoveNews + 1;
                        mainPosition = position;
                        Log.d(TAG, "showNewsWithBackupList -- sp.onViewSwipedToLeft: indexHelperRemoveNews: " + String.valueOf(indexHelperRemoveNews));
                        if (web != null)
                            web.destroy();

                        setupOnViewSwipedToLeft(position);
                    }
                    @Override
                    public void onViewSwipedToRight(final int position) {
                        mainPosition = position;
                        currentIndex = position + 1;
                        indexHelperRemoveNews = indexHelperRemoveNews + 1;
                        if (web != null)
                            web.destroy();

                        setupOnViewSwipedToRight(position);
                    }
                    @Override
                    public void onStackEmpty() {
                        Log.d(TAG, "showNewsWithBackupList -- sp.onStackEmpty.TRUE");
                        deleteCache(getContext());
                        MemoryCard.clear();  //The counting of position is reset.
                        sp.resetStack();   //Restart the news.
                        Log.d(TAG, "showNewsWithBackupList -- sp.resetStack");
                        Log.d(TAG, "showNewsWithBackupList -- backgroundSwipeStack()");
                        backgroundSwipeStack(viewBckgrnd, 0, getActivity()); //position 0: inicio de cardview

                    }
                });

            }
        }
    }

    /// MARK: Check the state of the database turning ON all preferences (first time) or check each state.
    public boolean config_inicial() {
        String[] categorias = {"SALUD", "RETAIL", "CONSTRUCCIÓN", "ENTRETENIMIENTO", "AMBIENTE", "EDUCACIÓN", "ENERGÍA", "BANCA", "TELECOM"};
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(), "db_noticias", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_PREFERENCIA + "", null);

        if (cursor.getCount() == 0) {
            ContentValues valores = new ContentValues();
            for (int i = 0; i < categorias.length; i++) {
                valores.put(Utilidades.ESTADO, 1);
                valores.put(Utilidades.CATEGORIA, categorias[i]);
                db.insert(Utilidades.TABLA_PREFERENCIA, null, valores);
            }
            Log.e(TAG, "config_inicial<------ CARGANDO BASE UNA UNICA VEZ");
            db.close();
            return true;
        } else {
            Log.e(TAG, "config_inicial<------  lista la configuracion");
        }

        db.close();
        return false;
    }

    ///////////////////////////////
    /// MARK: Save news to the Database Favorites.
    public void saveNewsFav(final SwipAdapter targetAdapter, final int positionForSave) {
        String titulo = targetAdapter.getItem(positionForSave).getTitulo();
        String url = targetAdapter.getItem(positionForSave).getUrl();
        String imagen = targetAdapter.getItem(positionForSave).getImagen();
        String autor = targetAdapter.getItem(positionForSave).getAutor();
        String categoria = targetAdapter.getItem(positionForSave).getCategoria();
        registrarNoticias(titulo, imagen, url, autor, categoria);
    }
    ///////////////////////////////
    /// MARK: Save news to the Database Recover.
    public void saveNewsRecover(final SwipAdapter targetAdapter, final int positionForSave) {
        String titulo = targetAdapter.getItem(positionForSave).getTitulo();
        String url = targetAdapter.getItem(positionForSave).getUrl();
        String imagen = targetAdapter.getItem(positionForSave).getImagen();
        String autor = targetAdapter.getItem(positionForSave).getAutor();
        String categoria = targetAdapter.getItem(positionForSave).getCategoria();
        registrarNoticiasRecuperar(titulo, imagen, url, autor, categoria);
    }



    //Requests

    ///////////////////////////////////
    /// MARK:
    private void loadDefaultNews(int loadIndex) {
        Log.e(TAGTIME, "loadDefaultNews -- DELAY:" + String.valueOf(getRunningTime()));

        if (isNetworkStatusAvailable(getContext())) {
            frnointernet.setVisibility(View.INVISIBLE);
            allNewsDefault.clear();
            if ((loadIndex > 9) && (MemoryLoadIndex > 9)) {   //Configuracion inicial
                //getInitialNews();   //getInitialNews: Muestra las primeras news, en lo que carga el resto de las noticias ( DB Settings).
                Log.d(TAG, "loadDefaultNews -- (initial configuration)" + String.valueOf(allNewsDefault.size()));
                Log.d(TAG, "loadDefaultNews -- allNewsDefault.size:" + String.valueOf(allNewsDefault.size()));
            } else {                                          //El usuario ha seleccionado una opcion del menuSlide
                allNewsDefault.clear();
                Log.d(TAG, "loadDefaultNews  --  allNewsDefault.size:" + String.valueOf(allNewsDefault.size()));
                if (MemoryLoadIndex > 9) {     //Cuando regresa de ver las noticias guarda el tipo de noticia que selecciono el usuario del menuSlide
                    Log.d(TAG, "loadDefaultNews -- (Selected an option Menu)");
                    MemoryLoadIndex = loadIndex;
                //    showNewsMenuSlide(loadIndex);
                } else {
                    Log.d(TAG, "loadDefaultNews -- (Selected a saved option from the Menu)");
                 //   showNewsMenuSlide(MemoryLoadIndex);
                }
            }
        } else {
            frnointernet.setVisibility(View.VISIBLE);   //Muestra el letrero de Weak Signal
        }
    }

    ///////////////////////////////////
    /// MARK:   MenuSlide
    public void showNewsMenuSlide(final int index)  {
        mainPosition = 0;
        final RequestParams parametros = new RequestParams();
        //final AsyncHttpClient client = requestHttpClient();
        masterClient = requestHttpClient();
        RequestHandle requestTopHeadlines = masterClient.get(urlHeadlines[index], parametros, new AsyncHttpResponseHandler() {
            //RequestHandle requestTopHeadlines = client.get(urlHeadlines[index], parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               // allNewsDefault = addMoreNews(new String(responseBody), labelsNews[index], allNewsDefault);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("Error TopHeadLines", error.toString());
            }
            @Override
            public void onFinish() {


                RequestHandle requestTopHeadlines = masterClient.get(urlDefaultSettings[index], parametros, new AsyncHttpResponseHandler() {
                    // RequestHandle requestTopHeadlines = client.get(urlDefaultSettings[index], parametros, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                       // allNewsDefault = addMoreNews(new String(responseBody), labelsNews[index], allNewsDefault);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("Error urlDefaultSettings", error.toString());
                    }
                    @Override
                    public void onFinish() {
                        firstInitialCard = false;

                        setupForChangedNews();
                        showNews(allNewsDefault,true,index);
                    }
                });
            }
        });

    }


    ///////////////////////////////////
    /// MARK:
    public void getInitialNews() {
        masterClient = requestHttpClient();
        final RequestParams parametros = new RequestParams();
        for (int i = 0; i < stateArray.length; i++) {
            if (stateArray[i] != 0) {
                final int index = i;

                /*
                RequestHandle requestTopHeadlines = masterClient.get(urlHeadlines[index], parametros, new AsyncHttpResponseHandler() {
                   // RequestHandle requestTopHeadlines = client.get(urlHeadlines[index], parametros, new AsyncHttpResponseHandler() {
                        @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("HomeFragment","getInitialNews -- RequestHandler,urlHeadlines, onSuccess: addMoreNews()");
                        allNewsDefault = addMoreNews(new String(responseBody), labelsNews[index], allNewsDefault);
                        final RequestParams parametros = new RequestParams();
                        //final AsyncHttpClient client = requestHttpClient();
                        masterClient = requestHttpClient();
                        final RequestHandle requestTopHeadlines = masterClient.get(urlDefaultSettings[index], parametros, new AsyncHttpResponseHandler() {
                                @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    Log.d("HomeFragment","getInitialNews -- RequestHandler,urlDefaultSettings, onSuccess: addMoreNews()");
                                    allNewsDefault = addMoreNews(new String(responseBody), labelsNews[index], allNewsDefault);
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.e("HomeFragment","getInitialNews -- RequestHandler,urlDefaultSettings, onFailure, Error urlDefaultSettings:"+ error.toString());
                            }
                            @Override
                            public void onFinish() {
                                Log.d("HomeFragment","getInitialNews -- RequestHandler,urlDefaultSettings, onFinish, allNewsDefualt.size"
                                        +String.valueOf(allNewsDefault.size()) + ", index:" + String.valueOf(index));
                                Log.d("HomeFragment","getInitialNews -- RequestHandler,urlDefaultSettings, onFinish, showNews()");
                                if (allNewsDefault.size() != 0) {
                                    showNews(allNewsDefault,false,0);  //Muestra las primeras noticias;
                                }
                                //Va por las otras (defaultnews) hasta aqui van las de topheadlines
                                //recursiveNews(index + 1);  index es la posicion donde agarra las primeras noticias  +1
                                //mFlag: 1 Para obtener las siguientes noticias (TopHeadlines) y mFlag: 0 Obtiene (DefaultNews)
                                Log.d("HomeFragment","getInitialNews -- RequestHandler,urlDefaultSettings, onFinish, addNewsForSave()");
                                addNewsForSave();

                                recursiveSimple(index + 1, 1);
                                indexHelperGetNews = index + 1;
                            }
                        });
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("HomeFragment","getInitialNews -- RequestHandler, urlHeadlines, onFailure, Error urlHeadlines:"+ error.toString());
                    }
                    @Override
                    public void onFinish() {
                    }
                });
                i = stateArray.length;
                */
                indexHelperGetNews = index;
               // recursiveSimple(index , 1);
            } else {
                Log.d("HomeFragment", "getInitialNews -- stateArray [" + String.valueOf(i) + "] = 0   (Category OFF)");
            }
        }
    }

    ///////////////////////////////////
    /// MARK:
    public void recursiveSimple(final int index, final int mFlag) {
        if (index < 9) {
            AsyncHttpClient masterClient = requestHttpClient();
            final RequestParams parametros = new RequestParams();

            if (mFlag == 1) { //recursiveNews
                if (stateArray[index] != 0) {
                    Log.d(TAG,"recursiveSimple -- stateArray:"+String.valueOf(index));
                    RequestHandle requestTopHeadlines = masterClient.get(urlHeadlines[index], parametros, new AsyncHttpResponseHandler() {
                        //RequestHandle requestTopHeadlines = client.get(urlHeadlines[index], parametros, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Log.d(TAG,"recursiveSimple -- RequestHandler,urlHeadlines, onSuccess: addMoreNews()");
                            if (allNewsDefault.size() == 0) {
                                flagFirstNews = true;
                            }

                            //allNewsDefault = addMoreNews(new String(responseBody), labelsNews[index], allNewsDefault);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d(TAG, "recursiveSimple -- RequestHandler,urlHeadlines, onFailure.error:"+error.toString());
                        }

                        @Override
                        public void onFinish() {
                            Log.d(TAG,"recursiveSimple -- RequestHandler,urlHeadlines, onFinish: recurseSimple("
                                    +String.valueOf(index + 1)+")");

                            if (allNewsDefault.size() != 0) {
                                if (flagFirstNews) {
                                    Log.d("HomeFragment", "recursiveSimple -- RequestHandler,urlHeadlines, onFinish: allNewsDefault != NULL");
                                    Log.d("HomeFragment", "recursiveSimple -- RequestHandler,urlHeadlines, onFinish: showNews (first news)");
                                    showNews(allNewsDefault,false,0);    //Muestra las primeras noticias;
                                    flagFirstNews = false;
                                }
                                Log.d(TAG, "recursiveSimple -- RequestHandler,urlHeadlines, onFinish: allNewsDefault != NULL, flagFirstNews:"
                                        +String.valueOf(flagFirstNews));
                            }
                            recursiveSimple(index + 1, 1);
                        }
                    });
                } else {
                    recursiveSimple(index + 1, 1);
                }
            } else {  //recursiveNewsDefault
                if (stateArray[index] != 0) {
                    RequestHandle requestTopHeadlines = masterClient.get(urlDefaultSettings[index], parametros, new AsyncHttpResponseHandler() {
                        // RequestHandle requestTopHeadlines = client.get(urlDefaultSettings[index], parametros, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Log.d(TAG,"recursiveSimple -- RequestHandler,urlDefaultSettings, onSuccess: addMoreNews()");
                            if (allNewsDefault.size() == 0) {
                                flagFirstNews = true;
                            }

                            //allNewsDefault = addMoreNews(new String(responseBody), labelsNews[index], allNewsDefault);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d(TAG, "recursiveSimple -- RequestHandler,urlDefaultSettings, onFailure.error:"+error.toString());
                        }

                        @Override
                        public void onFinish() {
                            Log.d(TAG,"recursiveSimple -- RequestHandler,urlDefaultSettings, onFinish: recurseSimple("
                                    +String.valueOf(index + 1)+")");


                            if (allNewsDefault.size() != 0) {
                                if (flagFirstNews) {
                                    Log.d(TAG, "recursiveSimple -- RequestHandler,urlDefaultSettings, onFinish: allNewsDefault != NULL");
                                    Log.d(TAG, "recursiveSimple -- RequestHandler,urlDefaultSettings, onFinish: showNews (first news)");
                                    showNews(allNewsDefault,false,0);    //Muestra las primeras noticias;
                                    flagFirstNews = false;
                                }
                                Log.d(TAG, "recursiveSimple -- RequestHandler,urlDefaultSettings, onFinish: allNewsDefault != NULL, flagFirstNews:"
                                        +String.valueOf(flagFirstNews));
                            }

                            recursiveSimple(index + 1, 0);
                        }
                    });
                } else {
                    recursiveSimple(index + 1, 0);
                }
            }
        }
        if (index == 8) {
            if (mFlag == 1) {  //recursiveNews
                recursiveSimple(indexHelperGetNews, 0);   //Las noticias defaults se actualizan con menos regularidad, y las topheadlines su contenido se actualiza con mas frecuencia.
            } else {  //recursiveNewsDefault
                if(showNewsInCardView) {
                    Log.d(TAG, "recursiveSimple -- showNews()  (index == 8), allNewsDefault.size: "+String.valueOf(allNewsDefault.size()));
                    //showNews(allNewsDefault);
                    Log.d(TAG, "recursiveSimple -- addNewsForSave()  (index == 8)");
                    addNewsForSave();
                }else{
                    // showNewsInCardView
                }
            }
        }
    }


    ///////////////////////////////////
    /// MARK:
    public ArrayList<Noticia> addMoreNews(String response, String categoria, ArrayList<Noticia> list) {
        Log.e(TAGTIME, "addMoreNews,  After Start.." + String.valueOf(getRunningTime()));
        try {
            JSONObject jsonArray = new JSONObject(response);
            //  Boolean flagNoAdd = false;
            String titulo;
            String imagen;
            String url;
            String description;
            String autor;
            String categoria_local = categoria;
            JSONArray articulos = jsonArray.getJSONArray("articles");

            for (int i = 0; i < articulos.length(); i++) {
                titulo = articulos.getJSONObject(i).getString("title");
                imagen = articulos.getJSONObject(i).getString("urlToImage");
                url = articulos.getJSONObject(i).getString("url");
                description = articulos.getJSONObject(i).getString("description");
                autor = articulos.getJSONObject(i).getJSONObject("source").getString("name");
                if (autor.equals("TechCrunch") ) {
                } else {
                    if (noNewsForShow != null) {
                        for (int j=0; j < noNewsForShow.size(); j++) {
                            if (url.equals(noNewsForShow.get(j).getUrl())){
                                //   flagNoAdd = true;
                                j = noNewsForShow.size();

                                Log.e(TAG, "addMoreNews -- noNewsForShow != NULL" + String.valueOf(getRunningTime()));
                                Log.e(TAG, "addMoreNews -- url Match: " + String.valueOf(url));

                            }else {

                                if ((j+1) == noNewsForShow.size()) {
                                    list.add(new Noticia(titulo, imagen, url, description, autor, categoria_local, 0L));
                                    Log.e(TAG, "addMoreNews -- url ADD: " + String.valueOf(url));
                                } else {
                                    //  flagNoAdd = false;
                                    Log.e(TAG, "addMoreNews -- url should NO ADD: " + String.valueOf(url));
                                }
                            }

                        }
                    }else{
                        list.add(new Noticia(titulo, imagen, url, description, autor, categoria_local, 0L));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "addMoreNews -- Error:" + e);
        }

        Log.d(TAG, "addMoreNews --  list.size :" + String.valueOf(list.size()));

     /*   if (MemoryCard.size() > 0) {
            for (int j = 0; j < list.size(); j++) {
                for (int i = 0; i < MemoryCard.size(); i++) {
                    //Log.d("HomeFragment", "addMoreNews -- for, list.size: " + String.valueOf(list.size()));
                    //Log.d("HomeFragment:", "addMoreNews -- for, MemoryCard.size:" + String.valueOf(MemoryCard.size()));
                    //Log.d("HomeFragment:", "addMoreNews -- for, value(i): " + String.valueOf(i));
                    if (MemoryCard.get(i).equalsIgnoreCase(list.get(j).getUrl())) {
                        list.remove(j);
                        Log.d("HomeFragment:", "addMoreNews -- list.remove(i) value(i): " + String.valueOf(i));
                        i = MemoryCard.size();
                    }
                }
            }
            Log.d("HomeFragment", "addMoreNews --  list.size :" + String.valueOf(list.size()));
            Log.d("HomeFragment", "addMoreNews -- MemoryCard.size :" + String.valueOf(MemoryCard.size()));
        }*/

        Log.e(TAGTIME, "addMoreNews,  Ending.." + String.valueOf(getRunningTime()));

        return list;
    }

}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////























    /* ******************************************* TRASH ******************************************/





    /*
    ///////////////////////////////////
    /// MARK:
    public void crearMenu(final View view) {
        List<String> dogsList = new ArrayList<String>();
        dogsList.add("S");
        dogsList.add("HEALTH");
        dogsList.add("CONSTRUCTION");
        dogsList.add("RETAIL");
        dogsList.add("EDUCATION");
        dogsList.add("ENTERTAINMENT");
        dogsList.add("ENVIRONMENT");
        dogsList.add("FINANCE");
        dogsList.add("ENERGY");
        dogsList.add("TELECOM");
        dogsList.add("CID NEWS");

        popUpContents = new String[dogsList.size()];
        dogsList.toArray(popUpContents);
        popupWindowDogs = popupWindowDogs();

        btnSupDer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showMenuSlideWithAnimation();

                popupWindowDogs.setAnimationStyle(R.style.popupAnim);
                popupWindowDogs.setElevation(30f);
                popupWindowDogs.showAsDropDown(view, 0, -550);


            }
        });


        llmenu = (LinearLayout) view.findViewById(R.id.swipe_menu_fav);
        llmenu.bringToFront();
        ddmenu = view.findViewById(R.id.swipe_menu_favdismiss);
        ddmenu.bringToFront();
        llmenu.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Log.d("LLMENU", "onSwipeLeft: TRUE");
                popupWindowDogs.setAnimationStyle(R.style.popupAnim);
                popupWindowDogs.setElevation(30f);
                popupWindowDogs.showAsDropDown(view, 20 * dps, -3000);
                //showMenuSlideWithAnimation();
            }
        });
        ddmenu.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();

                popupWindowDogs.dismiss();
                //dismissMenuSlideWithAnimation();


            }
        });
    }

    public void dismissMenuSlideWithAnimation(){

        Log.d("DDMENU", "onSwipeLeft: TRUE");
        Animation animation   =    AnimationUtils.loadAnimation(getContext(), R.anim.anim_dismiss);
        Log.i("animate","Begin Animation");
        //mMenuSlide.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //mMenuSlide.setVisibility(View.INVISIBLE);

                Log.i("animate Dismiss","End Animation");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.start();

            }
        });
        //mMenuSlide.animate();
        animation.start();
        Log.i("animate2","End Animation");


    }

    public void showMenuSlideWithAnimation(){
    //    mMenuSlide.setVisibility(LinearLayout.VISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(getContext(), R.anim.anim_show);
        Log.i("animate","Begin Animation");
      //  mMenuSlide.setAnimation(animation);
     //   mMenuSlide.bringToFront();

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.start();
        Log.i("animate","End Animation");
    }
    */


    /*
    ///////////////////////////////////
    /// MARK:
    public PopupWindow popupWindowDogs() {
        MainActivity mainActivity = (MainActivity) getActivity();
        final PopupWindow popupWindow = new PopupWindow(mainActivity);
        final ListView listView = new ListView(mainActivity);

        //listView.setAdapter(dogsAdapter(popUpContents));

        popupWindow.setFocusable(true);
        popupWindow.setWidth(Math.round(width - 20 * dps));  //width-25*dps
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.blanco));
        popupWindow.setContentView(listView);

        listView.setClickable(true);
        listView.setItemsCanFocus(true);
        listView.setDivider(null);
        listView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                popupWindowDogs.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {   //Tap en el menu Slide
                if (position == 0 || position == 10) {
                } else {
                    view.setBackgroundColor(0xFF29B9E8);
                    Animation animation1 = new AlphaAnimation(0.4f, 1);
                    animation1.setDuration(750);
                    animation1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            popupWindowDogs.dismiss();

                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    view.setAnimation(animation1);
                    String option = "";

                    Log.d("HomeFragment", "popupWindowDogs -- Position:"+String.valueOf(position));
                    menuSlide(position - 1);
                }
            }
        });
        return popupWindow;
    }
    */

///////////////////////////////////
/// MARK:
    /*
    private ArrayAdapter<String> dogsAdapter(String dogsArray[]) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dogsArray) {
            String id;
            int size; Log.d(TAG, "configUIListenersNScroll -- sp.setOnClickListener.TRUE");
                SwipadaptadorNScroll.mostrarNoticiasView(sp.getCurrentPosition());
            String color;

            double d = Math.round(dpY * 0.02);
            int porcentaje = (int) d + 4;

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // setting the ID and text for every items in the list
                String item = getItem(position);

                TextView listItem = new TextView(getActivity());
                listItem.setTextColor(Color.WHITE);
                listItem.setPadding(30 * dps, 8 * dps, 0, 8 * dps);
                listItem.setCompoundDrawablePadding(20 * dps);
                listItem.setLetterSpacing((float) 0.05 * dps);
                int porcentajey = (int) Math.round(height - 72 * scale);

                int espacio = (int) Math.round(((height - 72 * scale) / 12) * (Math.floor(getResources().getDisplayMetrics().scaledDensity) / getResources().getDisplayMetrics().scaledDensity));
                int espacio1 = (int) Math.round(((height - 72 * scale) / 12));
                int porcentaje = (int) Math.round(((espacio1) / 3) / getResources().getDisplayMetrics().scaledDensity);



                switch (item) {

                    case "HEALTH":
                        id = "menu_sup_salud";
                        Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.ic_health_menu);
                        img.setBounds(0, 0, 25 * dps, 25 * dps);
                        listItem.setCompoundDrawables(img, null, null, null);
                        size = porcentaje;
                        color = "#1596C1";
                        break;

                    case "EDUCATION":
                        id = "menu_sup_edu";
                        Drawable img2 = ContextCompat.getDrawable(getContext(), R.drawable.ic_education_menu);
                        img2.setBounds(0, 0, 25 * dps, 25 * dps);
                        listItem.setCompoundDrawables(img2, null, null, null);
                        size = porcentaje;
                        color = "#1596C1";
                        break;

                    case "ENTERTAINMENT":
                        id = "menu_sup_ent";
                        Drawable img3 = ContextCompat.getDrawable(getContext(), R.drawable.ic_entertainment_menu);
                        img3.setBounds(0, 0, 25 * dps, 25 * dps);
                        listItem.setCompoundDrawables(img3, null, null, null);
                        size = porcentaje;
                        color = "#1596C1";
                        break;
                    case "RETAIL":
                        id = "menu_sup_ret";
                        Drawable img1 = ContextCompat.getDrawable(getContext(), R.drawable.ic_retail_menu);
                        img1.setBounds(0, 0, 25 * dps, 25 * dps);
                        listItem.setCompoundDrawables(img1, null, null, null);
                        size = porcentaje;
                        color = "#1596C1";
                        break;

                    case "ENVIRONMENT":
                        id = "menu_sup_amb";
                        Drawable img4 = ContextCompat.getDrawable(getContext(), R.drawable.ic_environment_menu);
                        img4.setBounds(0, 0, 25 * dps, 25 * dps);
                        listItem.setCompoundDrawables(img4, null, null, null);
                        size = porcentaje;
                        color = "#1596C1";
                        break;
                    case "ENERGY":
                        id = "menu_sup_ene";
                        Drawable img6 = ContextCompat.getDrawable(getContext(), R.drawable.ic_energy_menu);
                        img6.setBounds(0, 0, 25 * dps, 25 * dps);
                        listItem.setCompoundDrawables(img6, null, null, null);
                        size = porcentaje;
                        color = "#1596C1";
                        break;
                    case "CONSTRUCTION":
                        id = "menu_sup_cons";
                        Drawable img0 = ContextCompat.getDrawable(getContext(), R.drawable.ic_construction_menu);
                        img0.setBounds(0, 0, 25 * dps, 25 * dps);
                        listItem.setCompoundDrawables(img0, null, null, null);
                        size = porcentaje;
                        color = "#1596C1";
                        break;

                    case "FINANCE":
                        id = "menu_sup_ban";
                        Drawable img5 = ContextCompat.getDrawable(getContext(), R.drawable.ic_finance_menu);
                        img5.setBounds(0, 0, 25 * dps, 25 * dps);
                        listItem.setCompoundDrawables(img5, null, null, null);
                        size = porcentaje;
                        color = "#1596C1";
                        break;


                    case "TELECOM":
                        id = "menu_sup_tel";
                        Drawable img7 = ContextCompat.getDrawable(getContext(), R.drawable.ic_telecom_menu);
                        img7.setBounds(0, 0, 25 * dps, 25 * dps);
                        listItem.setCompoundDrawables(img7, null, null, null);
                        size = porcentaje;
                        color = "#1596C1";
                        break;

                    case "CID NEWS":
                        //listItem.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        listItem.setText("A");
                        listItem.setTextColor(Color.rgb(21, 150, 193));
                        Drawable img8 = ContextCompat.getDrawable(getContext(), R.drawable.logocidnews);
                        img8.setBounds(0, 0, 100 * dps, 50 * dps);
                        listItem.setCompoundDrawables(img8, null, null, null);
                        size = porcentaje;
                        listItem.setPadding(Math.round(((width) / 2) - 50 * dps), espacio, 0, 20 * dps);
                        break;


                    case "S":
                        listItem.setTextColor(Color.rgb(21, 150, 193));
                        size = 0;
                        listItem.setPadding(0, espacio + espacio / 5, 0, 0);
                        color = "#1596C1";
                        break;

                }

                listItem.setText(item);
                listItem.setTag(id);
                listItem.setTextSize(size);
                listItem.setBackgroundColor(Color.parseColor(color));


                return listItem;
            }
        };
        return adapter;
    }

    */





    /*

    ///////////////////////////////////////////////////////////  SQL No usable Temporal
    /// MARK:
    public void saveTempDB(ArrayList<Noticia> allNews) {
        ContentValues valores = new ContentValues();
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(), "db_temporal", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_NOTICIAS_TEMPORAL + "", null);
        for (int i = 0; i < allNews.size(); i++) {
            valores.put(Utilidades.TITULO, allNews.get(i).getTitulo());
            valores.put(Utilidades.URLIMAGEN, allNews.get(i).getImagen());
            valores.put(Utilidades.URL, allNews.get(i).getUrl());
            valores.put(Utilidades.AUTOR, allNews.get(i).getAutor());
            valores.put(Utilidades.CATEGORIA, allNews.get(i).getCategoria());
            valores.put(Utilidades.GUARDAR, "yes");
            db.insert(Utilidades.TABLA_NOTICIAS_TEMPORAL, null, valores);
        }
        cursor.close();
        db.close();
        conn.close();
    }
    public void setupDBTemp() {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(), "db_temp", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_NOTICIAS_TEMPORAL + "", null);
    }
    ///////////////////////////////////////////////////////////  Bitmap
    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }*/






 /*//////////////////////////////////
    /// MARK:
    public void consultPreferences(){
        if (!config_inicial())
            for (int i = 0; i < stateArray.length; i++)
                stateArray[i] = consultarEstado(categoriesNews[i]);
        else
            for (int i = 0; i < stateArray.length; i++)
                stateArray[i] = 1;
    }*/
