package com.amco.cidnews.Fragments;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.net.Uri;

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
import com.amco.cidnews.Utilities.InternetVerify;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.percentlayout.widget.PercentFrameLayout;
import androidx.percentlayout.widget.PercentRelativeLayout;

import javax.microedition.khronos.opengles.GL;

import static com.amco.cidnews.Activities.MainActivity.getRunningTime;
import static com.amco.cidnews.Activities.MainActivity.isNetworkStatusAvailable;
import static com.amco.cidnews.Activities.MainActivity.mFlagHome;
import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment implements ListenFromActivity, ImagePassingAdapter, SwipAdapter.RequestImage {

    //region VARIABLES
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    boolean banderaAbout = false;
    public static boolean estadoDrawer = false;
    private int TIME_CHECK_CONNECTION = 5000;
    Handler handlerCheckIntener;
    //endregion

    public static Boolean SWIPESTACK_SCROLLING = false;  //Change R.layout.frame_home -- R.layout.frame_home_withoutscroll
    static private String TAG = "HomeFragment.java";
    static private String TAGTIME = "TIMEHomeFragment";

    // [START declare_analytics]
    private FirebaseAnalytics mFirebaseAnalytics;
    // [END declare_analytics]

    //////////////////////////////// MAIN VAR
    static public SwipeStack sp;

    static public SwipAdapterNScrolling SwipadaptadorNScroll;

    static public SwipAdapter Swipadaptador;
    static public SwipAdapterBackCard Swipadaptadoraux;
    static public CardView cardviewContainer, cardviewtest1, spaux, swipNoNews;

    //MENU SLIDE
    //DRAWER LAYOUT
    public static DrawerLayout drawerLayout;


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
    public static LinearLayout llmenu, ddmenu, frnointernet, bottomFabs, mMenuSlide;
    static ImageView mano1, basura, paloma, heightscroll, bottomFabFB, bottomFabTwitter, bottomFabWhats, imgLoaderGif;
    public static ImageButton btnSupDer;
    WebView web;
    public static FloatingActionButton shareFabMain;
    AsyncHttpClient masterClient;
    ProgressBar progressBar;


    RequestHandle requestTopHeadlines;
    //////////////////////////////// STRINGS
    String cateNews[] = {"HEALTH", "CONSTRUCTION", "RETAIL", "EDUCATION", "ENTERTAINMENT", "ENVIRONMENT", "FINANCE", "ENERGY", "TELECOM", "ABOUT US"};
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
    int startPointX, startPointY, posY, posX, dps, dpX, dpY, windowwidth, screenCenter, height, width;
    int oldX = 0;
    int oldY = 0;
    ///////////////////////////////  FLOAT
    float setAxisXCardView, setAxisYCardView, currentProgressSwiping;
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

    TextView txt_cid;

    private ImagePassingAdapter mPassingData = new ImagePassingAdapter() {
        @Override
        public void sendingImage(Bitmap bitmap, String url) {
        }
    };

    @Override
    public void onRequestImage(String urlRequest) {
        Log.d(TAG, "sendingImage -- urlRequest:" + urlRequest);
        if (mNextBitmapLoaded != null) {

            if (sp.getTopView() == null) {
                Log.d(TAG, "sendingImage -- sp.getTopView == null");
            } else {
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

    @Override
    public void setGeneralNews() {
        Log.d(TAG, "setGeneralNews");
        if (SWIPESTACK_SCROLLING) {
            if (getActivity() != null) {
                allNewsFromAct = ((MainActivity) getActivity()).allNews;
                allNewsDefault = allNewsFromAct;
                if (allNewsDefault.size() != 0) {
                    showNews(allNewsDefault, false, 0);
                }
                Log.d(TAG, "setGeneralNews allNews(MainActivity).size: " + allNewsFromAct);
            }
        } else {
            if (getActivity() != null) {
                allNewsFromAct = ((MainActivity) getActivity()).allNews;
                allNewsDefault = allNewsFromAct;
                if (allNewsDefault.size() != 0) {
                    showNewsNScrolling(allNewsDefault, false, 0);
                }
            }
        }
    }

    @Override
    public void msjWeakSignal() {
        if (frnointernet != null) {
            frnointernet.setVisibility(View.VISIBLE);   //Muestra el letrero de Weak Signal
        }
    }

    public void setSpeficicNewsFromMenu(int mIndex) {
        Log.d(TAG, "setSpeficicNewsFromMenu");
        if (SWIPESTACK_SCROLLING) {
            if ((MainActivity) getActivity() != null) {
                specificNewsFromAct = ((MainActivity) getActivity()).allNewsMenu;
                allNewsDefault = new ArrayList<>();
                allNewsDefault = specificNewsFromAct;
                setupForChangedNews();
                showNews(allNewsDefault, true, mIndex);
            }
        } else {
            if ((MainActivity) getActivity() != null) {
                specificNewsFromAct = ((MainActivity) getActivity()).allNewsMenu;
                allNewsDefault = new ArrayList<>();
                allNewsDefault = specificNewsFromAct;
                setupForChangedNewsNScrolling();
                showNewsNScrolling(allNewsDefault, true, mIndex);
            }
        }
    }

    @Override
    public void sendingImage(Bitmap bitmap, String url) {
        Log.d(TAG, "sendingImage?");
    }

    //region
    /**
     * <p><b>Created by Alejandro Jimenez on 16/05/2020</b></p>
     * <br>
     * Runnable que ejecuta la asynctask cada n tiempo.
     */
    Runnable runnableCheckInternet = new Runnable() {
        @Override
        public void run() {
            new InternetVerify(getActivity().getApplicationContext(), frnointernet, new HomeFragment()).execute();
            handlerCheckIntener.postDelayed(this, TIME_CHECK_CONNECTION);
        }
    };

    /**
     * <p><b>Created by Alejandro Jimenez on 16/05/2020</b></p>
     * <br>
     * Método que ejecuta el handler para monitorear la conectividad a internet.
     */
    private void verifyInternetConnection() {
        handlerCheckIntener = new Handler();
        handlerCheckIntener.postDelayed(runnableCheckInternet, TIME_CHECK_CONNECTION);
    }

    //endregion

    //region LIFECYCLE FRAGMENT

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActivityListener(HomeFragment.this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frame_home_main, container, false);
        setupMenu(view);
        if (SWIPESTACK_SCROLLING) {
            // PRIORIDAD EN LA CARGA
            setupCreateView(view, inflater, container);
            Log.e(TAGTIME, "onCreateView,  End Setting" + String.valueOf(getRunningTime()));
            if (allNewsDefault.size() != 0) {
                showNews(allNewsDefault, false, 0);
                Log.e(TAGTIME, "onCreate: allNewsFromAct" + allNewsFromAct.size());
            } else {
                if ((MainActivity) getActivity() != null) {
                    allNewsDefault = ((MainActivity) getActivity()).allNews;
                    Log.e(TAGTIME, "onCreate: allNewsDefault = AllNews:" + ((MainActivity) getActivity()).allNews.size());
                    Log.e(TAGTIME, "onCreate: allNewsDefault = AllNewsMenu :" + ((MainActivity) getActivity()).allNewsMenu.size());
                    showNews(allNewsDefault, false, 0);
                }
                Log.e(TAGTIME, "onCreate: allNewsFromAct" + allNewsFromAct.size());
            }
        } else {
            setupCreateViewNoScroll(view);
            Log.e(TAGTIME, "onCreateView,  End Setting" + String.valueOf(getRunningTime()));
            if (allNewsDefault.size() != 0) {
                showNewsNScrolling(allNewsDefault, false, 0);
                Log.e(TAGTIME, "onCreate: allNewsFromAct" + allNewsFromAct.size());
            } else {
                if (getActivity() != null && ((MainActivity) getActivity()).allNews.size() != 0) {
                    allNewsDefault = ((MainActivity) getActivity()).allNews;
                    Log.e(TAGTIME, "onCreate: allNewsDefault = AllNews:" + ((MainActivity) getActivity()).allNews.size());
                    Log.e(TAGTIME, "onCreate: allNewsDefault = AllNewsMenu :" + ((MainActivity) getActivity()).allNewsMenu.size());
                    showNewsNScrolling(allNewsDefault, false, 0);
                }
            }
        }

        //INICIAMOS EL HANDLER PARA VERIFICAR LA CONECTIVIDAD A INTERNET
        verifyInternetConnection();

        //EVENTO QUE ESCUCHA LAS ACCIONES DEL DRAWER LAYOUT (MENU DEL HOME)
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (banderaAbout == false) {
                    if (slideOffset < 0.8) {
                        MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(true);
                        MainActivity.scrollMenuPosition.setVisibility(View.VISIBLE);

                    } else if (slideOffset > 0.4) {
                        MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(false);
                        MainActivity.scrollMenuPosition.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //CADA QUE SE ABRE EL DRAWER LAYOUT SE COLOCA LA BANDERA EN FALSO PARA NO ACTIVAR
                //EL LANZAMIENTO DEL FRAGMENT HASTA QUE SE SELECCIONA ABOUT US DEL DRAWER LAYOUT
                banderaAbout = false;

                //SIRVE PARA SABER EN QUE ESTADO SE ENCUENTRA EL DRAWER
                //TRUE: ABIERTO
                //FALSE: CERRADO
                estadoDrawer = true;
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                //CADA QUE SE ABRE EL DRAWER LAYOUT SE COLOCA LA BANDERA EN FALSO PARA NO ACTIVAR
                //EL LANZAMIENTO DEL FRAGMENT HASTA QUE SE SELECCIONA ABOUT US DEL DRAWER LAYOUT
                banderaAbout = false;

                //SIRVE PARA SABER EN QUE ESTADO SE ENCUENTRA EL DRAWER
                //TRUE: ABIERTO
                //FALSE: CERRADO
                estadoDrawer = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        //SE ROMPE EL HILO PASANDO A OnPause() EN EL FRAGMENT
        handlerCheckIntener.removeCallbacks(runnableCheckInternet);
        //CUANDO PASE A ONPAUSE ESTE FRAGMENT, CERRAMOS EL DRAWER LAYOUT
        if (estadoDrawer == true) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
        Log.e(TAGTIME, "onDestroy:" + String.valueOf(getRunningTime()));
        if (masterClient != null) {
            masterClient.getThreadPool().shutdown();
        }
        //REQUEST NEW ARRAYLIST! FROM ACT
        ((MainActivity) getActivity()).refreshDeletedNews();
    }

    //endregion

    //region SWIPE STACK NOT SCROLLING
    public void setupCreateViewNoScroll(View view) {
        /////////////////////////////////////
        sp = view.findViewById(R.id.swipStack);
        frnointernet = view.findViewById(R.id.aviso_no_internet);
        frnointernet.setVisibility(View.INVISIBLE);
        containerLoaderGif = view.findViewById(R.id.loader_gif);
        imgLoaderGif = view.findViewById(R.id.img_loader_gif);

        //VISTA RESTANTE
        txt_cid = (TextView) view.findViewById(R.id.txt_cid);

        ////////////
        btnSupDer = view.findViewById(R.id.boton_superior_home);
        basura = view.findViewById(R.id.basuraimg);
        paloma = view.findViewById(R.id.palomaimg);
        mano1 = view.findViewById(R.id.icono_mano);
        shareFabMain = view.findViewById(R.id.shareMainFab);

        setupUINScrolling(view);
        configUIListenersNScroll(view);
    }
    //endregion

    /************************************* LISTENERS NScrolling ***********************************/

    public void configUIListenersNScroll(final View view) {

        btnSupDer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "configUIListenersNScroll -- btnSupDer -- onClick.TRUE");
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        //BUTTON FABS
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
                Log.d(TAG, "sp.setSwipeProgressListener -- onSwipeStart -- startPointX:" + startPointX);
                Log.d(TAG, "sp.setSwipeProgressListener -- onSwipeStart -- startPointY:" + startPointY);
            }

            @Override
            public void onSwipeProgress(int position, float progress) {
                if (Math.abs(progress * 100) > 10) {
                    mCardSwiping = true;
                }
                Log.d(TAG, "configUIListenersNScroll -- onSwipeProgress -- progress:" + progress);
                animationSwipeProgress((float) (progress * (0.75)));
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
                Log.d(TAG, "sp.setSwipeProgressListener -- onSwipeStart -- x_cord:" + x_cord);
                Log.d(TAG, "sp.setSwipeProgressListener -- onSwipeStart -- y_cord:" + y_cord);
                if ((Math.abs(startPointX - x_cord) < 8) && (Math.abs(startPointY - y_cord) < 8) && !mCardSwiping) {
                    Log.d(TAG, "eventsActionUp  -- CARD CLICKED! ");
                    SwipadaptadorNScroll.mostrarNoticiasView(sp.getCurrentPosition());
                    urlNewsLoaded = false;
                    indexHelperRemoveNews = 0;
                } else {
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
                saveNewsGenericNScroll(SwipadaptadorNScroll, position, false);
            }

            @Override
            public void onViewSwipedToRight(final int position) {
                Log.d(TAG, "configUIListenersNScroll -- sp.onViewSwipedRight Position:" + String.valueOf(position));
                Log.d(TAG, "configUIListenersNScroll -- sp.onViewSwipedRight sp.getCurrentPosition: " + String.valueOf(sp.getCurrentPosition()));
                indexHelperRemoveNews = indexHelperRemoveNews + 1;
                Log.d(TAG, "configUIListenersNScroll -- sp.onViewSwipedToRight indexHelperRemoveNews: " + String.valueOf(indexHelperRemoveNews));
                saveNewsGenericNScroll(SwipadaptadorNScroll, position, true);
            }

            @Override
            public void onStackEmpty() {
                deleteCache(getContext());
                MemoryCard.clear();  //The counting of position is reset.
                sp.resetStack();   //Restart the news.
                Log.d(TAG, "configUIListenersNScroll -- sp.OnStackEmpty.TRUE");
            }
        });

    }

    public void setupUINScrolling(final View view) {
        ////////////////////////////////////
        Glide.with(this).load(R.drawable.loadingbl).into(imgLoaderGif);
        containerLoaderGif.setVisibility(View.VISIBLE);
        imgLoaderGif.setVisibility(View.VISIBLE);
        containerLoaderGif.bringToFront();
        imgLoaderGif.bringToFront();

        /////////////////////////////////////
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);//Obtiene el valor de height y width
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        windowwidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        screenCenter = windowwidth / 2;

        /////////////////////////////////////
        shareFabMain.hide();
        shareFabMain.setEnabled(false);
        shareFabMain.hide();

        //SP
        sp.setEnabled(false);

        //SCROLL VIEWS
        final AnimatorSet anim4 = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.anim.share_moving);
        anim4.setTarget(shareFabMain);
        anim4.start();
        anim4.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        basura.bringToFront();
        paloma.bringToFront();
        basura.setVisibility(View.INVISIBLE);
        paloma.setVisibility(View.INVISIBLE);
    }

    public void showNewsNScrolling(final ArrayList<Noticia> defaultListNews, boolean fromMenuSlide, int catFromMenuSlide) {
        Log.e(TAGTIME, "showNewsNScrolling -- DELAY:" + String.valueOf(getRunningTime()));
        if (getActivity() == null) {
            Log.d(TAG, "showNewsNScrolling -- getActivity: NULL");
        } else {
            SwipadaptadorNScroll = new SwipAdapterNScrolling(getActivity(), defaultListNews, getContext());
            if (SwipadaptadorNScroll.getCount() == 0) {
                Log.e(TAG, "showNewsNScrolling -- Swipadaptador.getCount: No news");
                if (fromMenuSlide) {   //Check if is from Menu for show the card!
                    if ((sp != null) && (sp.getVisibility() == View.VISIBLE)) {
                        shareFabMain.hide();
                        shareFabMain.hide();
                        menuSelectedIndex = catFromMenuSlide; // 0 - 8
                    }
                }
                flagMenuSlideTapped = fromMenuSlide;
            } else {
                if (frnointernet != null) {
                    frnointernet.setVisibility(View.INVISIBLE);//Muestra el letrero de Weak Signal
                }
                sp.setAdapter(SwipadaptadorNScroll);
                if (sp.getTopView() == null) {
                    Log.e(TAG, "showNewsNScrolling --  sp.getTopView.NULL, sp.ResetStack");
                    sp.resetStack();
                }
                sp.setVisibility(View.VISIBLE);
                sp.setEnabled(true);
                shareFabMain.show();
                shareFabMain.show();
                shareFabMain.setEnabled(true);
            }
        }
    }

    public void setupForChangedNewsNScrolling() {
        sp.removeAllViews();
        sp.removeAllViewsInLayout();
        sp.resetStack();
        sp.setVisibility(View.VISIBLE);
        shareFabMain.show();
        shareFabMain.setEnabled(true);
        shareFabMain.show();
    }

    /**
     * Método que guarda las noticias en la base de datos ya sea para Favorites o Recover
     *
     * @param targetAdapter
     * @param positionForSave
     * @param forFavorites
     */
    public void saveNewsGenericNScroll(final SwipAdapterNScrolling targetAdapter, final int positionForSave, final boolean forFavorites) {
        final String titulo = targetAdapter.getItem(positionForSave).getTitulo();
        final String url = targetAdapter.getItem(positionForSave).getUrl();
        final String imagen = targetAdapter.getItem(positionForSave).getImagen();
        final String autor = targetAdapter.getItem(positionForSave).getAutor();
        final String categoria = targetAdapter.getItem(positionForSave).getCategoria();
        if (forFavorites) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    registrarNoticias(titulo, imagen, url, autor, categoria);

                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    registrarNoticiasRecuperar(titulo, imagen, url, autor, categoria);

                }
            }).start();
        }
    }


    //region SETUP
    public void setupCreateView(View view, LayoutInflater inflater, ViewGroup container) {
        mImg = view.findViewById(R.id.ImgTest);

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

    public void setupUI(final View view) {
        Glide.with(this).load(R.drawable.loadingbl).into(imgLoaderGif);
        containerLoaderGif.setVisibility(View.VISIBLE);
        imgLoaderGif.setVisibility(View.VISIBLE);
        containerLoaderGif.bringToFront();
        imgLoaderGif.bringToFront();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);            //Obtiene el valor de height y width
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        windowwidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        screenCenter = windowwidth / 2;
        swipNoNews.addView(viewBckgrndNoNews);

        fabOpen = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.sharebutton_open);
        fabClose = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.sharebutton_close);
        rotateFoward = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotate_foward);
        rotateBackward = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backward);

        cardviewContainer.setVisibility(View.INVISIBLE);
        shareFabMain.hide();
        shareFabMain.setEnabled(false);

        //SP
        sp.setEnabled(false);

        //SCROLL VIEWS
        scrollView.setVerticalScrollBarEnabled(false);
        scrollViewShow.setVerticalScrollBarEnabled(true);
        scrollViewShow.bringToFront();

        final AnimatorSet anim4 = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.anim.share_moving);
        anim4.setTarget(shareFabMain);
        anim4.start();
        anim4.addListener(new AnimatorListenerAdapter() {
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
    }

    /**
     * <p><b>Created by Alejandro Jimenez on 16/05/2020</b></p>
     * <br>
     * Método que inhabilita los widgets cuando aparece About Us
     */
    public static void setDisableWidgetsHome() {
        //Action
        btnSupDer.setEnabled(false);
        sp.setEnabled(false);
        shareFabMain.setEnabled(false);

        //Visibility
        shareFabMain.hide();
    }

    /**
     * <p><b>Created by Alejandro Jimenez on 16/05/2020</b></p>
     * <br>
     *     Método que habilita los widgets  cuando se regresa al home desde about us.
     */
    public static void setEnableWidgetsHome() {
        //Action
        btnSupDer.setEnabled(true);
        sp.setEnabled(true);
        shareFabMain.setEnabled(true);

        //Visibility
        shareFabMain.show();
    }

    //endregion

    /**
     * Método que configura el drawer layout que es el menu en home.
     * @param view Objeto de la clase View para enlazar con los widgets de la vista.
     */
    public void setupMenu(View view) {
        //VINCULAR EL DRAWER LAYOUT
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        //VINCULAR LA LIST VIEW
        final ListView drawerList = (ListView) view.findViewById(R.id.nav_list);
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
        items.add(new DrawerItemNavBar(getResources().getString(R.string.about_us), R.drawable.ic_about_menu));
        //INCRUSTA UN ADAPTER CON EL LIST VIEW
        drawerList.setAdapter(new DrawerAdapter(getContext(), items));
        View footer = getLayoutInflater().inflate(R.layout.footer_cidnews, null);
        drawerList.addFooterView(footer);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrawerItemNavBar selected = (DrawerItemNavBar) parent.getItemAtPosition(position);
                Log.d(TAG, "onCreate -- onItemClick: position: " + String.valueOf(position));
                String typeNews = "";
                if (position < 9) {
                    typeNews = cateNews[position];
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
                    drawerLayout.closeDrawers();
                } else if (position == 9) {
                    setDisableWidgetsHome();

                    //ACTIVAMOS BANDERA DE QUE SE SELECCIONO ABOUT US
                    banderaAbout = true;
                    //HACEMOS INVISIBLES CUANDO ENTRA ABOUT US SCROLL Y SELECCION DE BOTTOM NAVIGATION VIEW
                    MainActivity.scrollMenuPosition.setVisibility(View.INVISIBLE);
                    MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(false);

                    fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    AboutFragment aboutFragment = new AboutFragment();
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.contendor_home, aboutFragment, "AboutFragment");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    //CERRAMOS EL DRAWER LAYOUT CUANDO SE HACE CLIC EN ABOUT US
                    drawerLayout.closeDrawer(GravityCompat.END);
                }

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, typeNews);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);

            }
        });
    }

    //endregion

    //region LISTENERS
    public void configUIListeners(final View view) {
        btnSupDer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "btnSupDer -- onClick.TRUE");
                drawerLayout.openDrawer(GravityCompat.END);
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
                        eventsActionUpCardNoNews(event, swipNoNews);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("animationSwipe!", "ACTION MOVE!");
                        eventsActionMoveCardNoNews(event, swipNoNews);
                        break;
                }
                return true;
            }
        });

        //BUTTON FABS
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
                if (facebookInstalled(getActivity())) {
                    sendNewsByIntentFB(getActivity());
                } else {
                    Log.e(TAG, "onClick: bottomFabFacebookr NO INSTALLED?");
                    try {
                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.katana"));
                        startActivity(viewIntent);
                    } catch (Exception error) {
                        Toast.makeText(getApplicationContext(), "Unable to connect, Try again", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
            }
        });

        bottomFabTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: bottomFabTwitter");
                if (twitterInstalled(getActivity())) {
                    sendNewsByIntentTwitter(getActivity());
                } else {
                    try {
                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.twitter.android"));
                        startActivity(viewIntent);
                    } catch (Exception error) {
                        Toast.makeText(getApplicationContext(), "Unable to connect, Try Again", Toast.LENGTH_LONG).show();
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
                if (web != null) {
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
                        Log.d(TAG, "cardviewContainer -- ACTION: CANCEL ");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "cardviewContainer -- ACTION: DOWN ");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "cardviewContainer -- ACTION: UP ");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "cardviewContainer -- ACTION: MOVE ");
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
                if ((scrollY > 35)) {
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

                cardviewContainer.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.blanco), PorterDuff.Mode.MULTIPLY);

                if (scrollY >= 1) {
                    float percent = (float) scrollY * 2;
                    if (percent > 255) {
                        percent = 255;
                    }
                    int colorBackground = (int) percent;//(percent*(255));
                    cardviewContainer.getBackground().setAlpha(colorBackground);
                }
                if (scrollY == 0) {
                    cardviewContainer.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.mainBlue), PorterDuff.Mode.MULTIPLY);
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
                if (((MainActivity) getActivity()) != null)
                    ((MainActivity) getActivity()).layoutParamsNewsBackUp = layoutParamsNews;
                if (web != null) {
                    RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, web.getHeight());
                    heightscroll.setLayoutParams(layoutParams3);
                }
            }
        });
    }

    //endregion

    ///////////////////////////////////
    /// MARK: Start to change Main View.
    public void startChangedNews() {
        if (SWIPESTACK_SCROLLING) {
            cardviewtest1.setVisibility(View.INVISIBLE);
            cardviewContainer.setVisibility(View.INVISIBLE);
            spaux.setVisibility(View.INVISIBLE);
            indexHelperRemoveNews = 0;  //indexHelperRemoveNews es utilizado para saber si el usuario ha movido una Swipecard, ayuda en el inicio de cargar noticias.
        } else {
            sp.setVisibility(View.INVISIBLE);
            shareFabMain.hide();
            shareFabMain.setEnabled(false);
            shareFabMain.hide();
            frnointernet.setVisibility(View.INVISIBLE);
            imgLoaderGif.setVisibility(View.VISIBLE);
        }
    }

    ///////////////////////////////////
    /// MARK: Setup when user make a change in Menu.
    public void setupForChangedNews() {
        sp.removeAllViews();
        sp.removeAllViewsInLayout();
        sp.resetStack();
        sp.setVisibility(View.VISIBLE);
        spaux.setVisibility(View.VISIBLE);
        shareFabMain.show();
        cardviewtest1.setVisibility(View.VISIBLE);
        cardviewContainer.setVisibility(View.VISIBLE);
    }

    /****************************************** ANIMATION *****************************************/

    ///////////////////////////////////
    /// MARK: Hide the bottom social media icons
    public void hideBottomIcons() {
        Log.d(TAG, "hideBottomIcons");
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
    public void showBottomIcons() {
        Log.d(TAG, "showBottomIcons");
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
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(targetCheck, "translationX", 250);

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


        if (-currentProgress > 0.125) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(basura, "translationX", (-currentProgress * 300));
            animator1.setRepeatCount(0);
            animator1.setDuration(0);
            animator1.start();


            basura.setAlpha(-currentProgress * 4f);
            basura.setVisibility(View.VISIBLE);
            paloma.setVisibility(View.INVISIBLE);
        } else {
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

        if (currentProgress > 0.125) {

            Log.d(TAG, "animationRightSwipe -- allNewsDefault.size: " + String.valueOf(allNewsDefault.size()));
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(paloma, "translationX", (-currentProgress * 300));
            animator1.setRepeatCount(0);
            animator1.setDuration(0);
            animator1.start();

            paloma.setAlpha(currentProgress * 4f);
            paloma.setVisibility(View.VISIBLE);
            basura.setVisibility(View.INVISIBLE);
        } else
            paloma.setAlpha(0f);

    }

    //////////////////////////////
    /// MARK: Animation move icon when card is swiping to right direction.
    public void animationMoveIconRight(float currentProgress) {
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
    public boolean twitterInstalled(Activity activity) {
        try {
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo("com.twitter.android", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    ///////////////////////////////////
    /// MARK:
    public boolean facebookInstalled(Activity activity) {
        try {
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
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


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
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
    public void showNews(final ArrayList<Noticia> defaultListNews, boolean fromMenuSlide, int catFromMenuSlide) {
        Log.e(TAGTIME, "showNews -- DELAY:" + String.valueOf(getRunningTime()));
        if (getActivity() == null) {
            Log.d(TAG, "showNews -- getActivity: NULL");
        } else {
            Swipadaptador = new SwipAdapter(getActivity(), defaultListNews, getContext(), this);
            if (Swipadaptador.getCount() == 0) {
                Log.e(TAG, "showNews -- Swipadaptador.getCount: No news");
                if (fromMenuSlide) {   //Check if is from Menu for show the card!
                    if ((sp != null) && (sp.getVisibility() == View.VISIBLE)) {
                        scrollIsAllowed = false;
                        // sp.addView(View.inflate(getContext(),R.layout.custom_toast,null));
                        spaux.setVisibility(View.INVISIBLE);
                        cardviewContainer.setVisibility(View.INVISIBLE);
                        shareFabMain.hide();

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

            } else {

                if (layoutParamsNews != null)
                    cardviewtest1.setLayoutParams(layoutParamsNews);
                else if (((MainActivity) getActivity()).layoutParamsNewsBackUp != null)
                    cardviewtest1.setLayoutParams(((MainActivity) getActivity()).layoutParamsNewsBackUp);


                swipNoNews.setVisibility(View.INVISIBLE);

                urlNewsLoaded = false;       //urlNewsLoaded: es la bandera para poder descargar las noticias en la Webview.
                Log.e(TAG, "showNews -- Swipadaptador.getCount: News");
                if (indexHelperRemoveNews == 0) {
                    if (sp.getTopView() == null) {
                        Log.e(TAG, "showNews --  sp.getTopView.NULL, sp.ResetStack");
                        sp.resetStack();

                    }


                    sp.setVisibility(View.VISIBLE);
                    spaux.setEnabled(false);
                    spaux.setVisibility(View.VISIBLE);

                    cardviewContainer.setVisibility(View.VISIBLE);
                    Log.e(TAG, "showNews --  sp.setAdapter");
                    int helperY = scrollView.getScrollY();
                    sp.setAdapter(Swipadaptador);

                    if (helperY != 0) {
                        Log.e(TAG, "showNews --  scrollView.setScrollY:" + String.valueOf(helperY));
                        scrollView.setScrollY(helperY);
                    }


                    if (indexBackgroundBackup != 0)
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
                        Swipadaptadoraux = new SwipAdapterBackCard(getActivity(), allNewsHelper, getApplicationContext());
                        Swipadaptadoraux.setImageListener(this);
                    }
                    final String newsaux = defaultListNews.get(sp.getCurrentPosition()).getUrl();
                    shareFabMain.show();
                    shareFabMain.setEnabled(true);
                    scrollView.smoothScrollTo(0, 0);
                    sp.setListener(new SwipeStack.SwipeStackListener() {
                        @Override
                        public void onViewSwipedToLeft(final int position) {
                            Log.d(TAG, "showNews -- sp.onViewSwipedToLeft Position:" + String.valueOf(position));
                            Log.d(TAG, "showsNews -- sp.onViewSwipedToLeft sp.getCurrentPosition: " + String.valueOf(sp.getCurrentPosition()));
                            currentIndex = position;
                            //currentIndex = position + 1;
                            mainPosition = position + 1;
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
                            mainPosition = position + 1;
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
                            Log.d(TAG, "showNews -- sp.OnStackEmpty.TRUE");
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
        if (web != null) {
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
                        Log.d(TAG, "web: -- ACTION: CANCEL ");
                        eventsActionCancel(event);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "web: -- ACTION: DOWN ");
                        eventsActionDown(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "web: -- ACTION: UP ");
                        eventsActionUp(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "web: -- ACTION: MOVE ");
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
                + String.valueOf(mainPosition) + ").getUrl:"
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
    public void setupOnViewSwipedToRight(int position) {
        setupOnViewSwiped(position);
        saveNewsGeneric(Swipadaptador, position, true);
    }

    ///////////////////////////////////
    /// MARK:
    public void setupOnViewSwipedToLeft(int position) {
        setupOnViewSwiped(position);
        saveNewsGeneric(Swipadaptador, position, false);
    }


    ///////////////////////////////////
    /// MARK:
    public void setupOnViewSwiped(int position) {
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
        diffPosX = diffPosX + Math.abs(posX - oldX);
        diffFastGesture = posX - oldX;


        oldX = posX;
        oldY = posY;
        x_cord = (int) ev.getRawX();
        y_cord = (int) ev.getRawY();

        Log.e(TAG, "eventsActionMove -- ACTION MOVE!  diffPosX:" + String.valueOf(diffPosX));
        Log.e(TAG, "eventsActionMove -- ACTION MOVE!  diffPosY" + String.valueOf(diffPosY));
        Log.e(TAG, "eventsActionMove -- ACTION MOVE!  posX" + String.valueOf(posX));
        Log.e(TAG, "eventsActionMove -- ACTION MOVE!  posY" + String.valueOf(posY));


        if (!isSwiping && !isScrolling && diffPosX != 0 && diffPosY != 0) {
            if (diffPosX > 3 * diffPosY || diffPosY > 3 * diffPosX) {

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
                    if (allNewsDefault.size() != 0) {
                        scrollIsAllowed = true;
                    } else {
                        scrollIsAllowed = false;
                    }

                    //scrollIsAllowed = true;
                    diffPosY = 0;
                    diffPosX = 0;
                }
            }

            //if ((Math.abs(diffPosY) -  Math.abs(diffPosX)) > 0 && Math.abs(diffPosX) > 12){
            if (Math.abs(diffPosX) > 15) {

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
            cardviewContainer.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.mainBlue), PorterDuff.Mode.MULTIPLY);
            cardviewContainer.getBackground().setAlpha(255);
            shareFabMain.hide();
            cardviewContainer.setX(x_cord - x);
            currentProgressSwiping = (x_cord - x) / 1000;
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
            if ((Math.abs(screenCenter - x_cord)) > screenCenter / 2) { //Swipe si rebasa 1/4 del screen a cualquier lado
                if (Math.abs(x_cord - startPointX) > (screenCenter)) {
                    if ((x_cord - startPointX) > 0) {
                        Likes = 2;
                    } else
                        Likes = 1;
                }
            }

            if (Math.abs(diffFastGesture) > (110)) {  //Swipe muy rapido
                if ((x_cord - startPointX) > 0) {
                    Likes = 2;   //Liked
                } else
                    Likes = 1;
            }


            if (Math.abs(x_cord - x) > (150)) {  //Swipe diferencia de X
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

            if (allNewsDefault.size() != 0) {
                scrollIsAllowed = true;
            } else {
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

        if ((Math.abs(startPointX - x_cord) < 8) && (Math.abs(startPointY - y_cord) < 8) && (!isSwiping)) {
            Log.e(TAG, "eventsActionUp  -- CARD CLICKED! ");
            Log.e(TAG, "eventsActionUp  -- CARD CLICKED! Swipadaptador.mostrarNoticiasView("
                    + String.valueOf(sp.getCurrentPosition()) + ")");

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

            Log.e(TAG, "eventsActionUp  -- sp.getCurrentPosition:" + String.valueOf(sp.getCurrentPosition()));
            Log.e(TAG, "eventsActionUp  -- currentIndex:" + String.valueOf(currentIndex));

            cardviewContainer.setScrollY(0);
//          if (allNewsDefault.size() != 0){
            if (allNewsDefault.size() != 0) {
                scrollIsAllowed = true;
            } else {
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
                public void onAnimationStart(Animator animation) {
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
    public void eventsActionMoveCardNoNews(MotionEvent ev, CardView targetCard) {
        Log.e(TAG, "eventsActionMoveCardNoNews -- ACTION MOVE!");

        posY = (int) ev.getRawY();
        posX = (int) ev.getRawX();
        diffPosY = diffPosY + Math.abs(posY - oldY);
        diffPosX = diffPosX + Math.abs(posX - oldX);
        diffFastGesture = posX - oldX;


        oldX = posX;
        oldY = posY;
        x_cord = (int) ev.getRawX();
        y_cord = (int) ev.getRawY();


        if (isSwiping) {
            Log.e(TAG, "eventsActionMove -- isSwiping.TRUE: X WIN");

            targetCard.setX(x_cord - x);
            currentProgressSwiping = (x_cord - x) / 1000;
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
            if ((Math.abs(screenCenter - x_cord)) > screenCenter / 2) { //Swipe si rebasa 1/4 del screen a cualquier lado
                if (Math.abs(x_cord - startPointX) > (screenCenter)) {
                    if ((x_cord - startPointX) > 0) {
                        Likes = 2;
                    } else
                        Likes = 1;
                }
            }

            if (Math.abs(diffFastGesture) > (110)) {  //Swipe muy rapido
                if ((x_cord - startPointX) > 0) {
                    Likes = 2;   //Liked
                } else
                    Likes = 1;
            }


            if (Math.abs(x_cord - x) > (150)) {  //Swipe diferencia de X
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
    public void eventsActionUpCardNoNews(MotionEvent ev, final CardView targetCard) {
        Log.e(TAG, "eventsActionUpCardNoNews  -- ACTION UP! ");

        diffPosY = 0;
        diffPosX = 0;

        //animationFinish(basura, paloma);
        x_cord = (int) ev.getRawX();
        y_cord = (int) ev.getRawY();

        ObjectAnimator moveX = ObjectAnimator.ofFloat(swipNoNews, "translationX", x_cord);

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

            if ((MainActivity) getActivity() != null)
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

            if ((MainActivity) getActivity() != null)
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
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(), "db_noticias", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        String[] parametros = {url};
        Cursor cursor = db.rawQuery("SELECT url FROM " + Utilidades.TABLA_RECUPERAR + " WHERE " + Utilidades.URL + " =?", parametros);
        if (cursor.getCount() == 0) {
            Log.d(TAG, " Registro Noticias Recuperar:");

            Log.d(TAG, " NOTICIA: NO HAY");
            ContentValues valores = new ContentValues();
            valores.put(Utilidades.TITULO, titulo);
            valores.put(Utilidades.IMAGEN, imagen);
            valores.put(Utilidades.URL, url);
            valores.put(Utilidades.AUTOR, autor);
            valores.put(Utilidades.CATEGORIA, categoria);
            valores.put(Utilidades.TIEMPO, addTime(System.currentTimeMillis()));
            db.insert(Utilidades.TABLA_RECUPERAR, null, valores);
            db.close();
        } else {
            Log.d(TAG, "NOTICIA: REPETIDA RECUPERAR" + String.valueOf(titulo));
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
    public void saveNewsGeneric(final SwipAdapter targetAdapter, final int positionForSave, boolean forFavorites) {
        String titulo = targetAdapter.getItem(positionForSave).getTitulo();
        String url = targetAdapter.getItem(positionForSave).getUrl();
        String imagen = targetAdapter.getItem(positionForSave).getImagen();
        String autor = targetAdapter.getItem(positionForSave).getAutor();
        String categoria = targetAdapter.getItem(positionForSave).getCategoria();

        if (forFavorites)
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
    public long addTime(long startTime) {
        Log.e(TAG, "DATETIME: " + String.valueOf(DateFormat.getInstance().format(startTime)));
        //long halfAnHourLater = startTime + 1800000;
        Log.e(TAG, "WAIT UNTIL: " + String.valueOf(DateFormat.getInstance().format(startTime + 1800000)));
        return startTime + 3600000 * 24 * 2; //30Min  1 Min: 60'000
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
        Log.d(TAG, "addNewsForSave  -- newsApiSaved.size: " + String.valueOf(newsApiSaved.size()));
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

}