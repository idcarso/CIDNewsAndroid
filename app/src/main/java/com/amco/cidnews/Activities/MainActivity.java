package com.amco.cidnews.Activities;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amco.cidnews.Fragments.ConfigFragment;
import com.amco.cidnews.Fragments.FavFragment;
import com.amco.cidnews.Fragments.HomeFragment;
import com.amco.cidnews.Fragments.RecoverFragment;
import com.amco.cidnews.Utilities.ListenFromActivity;
import com.amco.cidnews.R;
import com.amco.cidnews.Utilities.ConexionSQLiteHelper;
import com.amco.cidnews.Utilities.MyDialogFragment;
import com.amco.cidnews.Utilities.Noticia;
import com.amco.cidnews.Utilities.Utilidades;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // [START declare_analytics]
    private FirebaseAnalytics mFirebaseAnalytics;
    // [END declare_analytics]

    SharedPreferences prefs = null;
    static private String TAG = "MainActivity";

    public static long startTime;

    //Lists
    public ArrayList<Noticia> allNews = new ArrayList<>();
    public ArrayList<Noticia> allNewsMenu = new ArrayList<>();
    public ArrayList<Noticia> allNewsDeleted = new ArrayList<>();
    public ArrayList<Noticia> allNewsDeletedHelper = new ArrayList<>();

    //Listener
    public ListenFromActivity activityListener;

    //View
    public BottomNavigationView menuNavigation;
    public ImageButton imgBtnCross;
    public RelativeLayout.LayoutParams layoutParamsNewsBackUp;
    public ImageView scrollMenuPosition;

    String [] typeUrl;
    //int mFirstNewsIndex = 0;

    boolean mFlagHome=true;
    boolean mFlagSettings=true;
    boolean mFlagFavorites=true;
    boolean mFlagRecover=true;
    public boolean animateCrossButton=false;
    public boolean changeStateSetting = false;
    public boolean [] stateArrayMain = {false,false,false ,false,false,false ,false,false,false};

    //DB Connection
    ConexionSQLiteHelper conn;

    //APIS KEY
    /*
     *                           99237f17c0b540fdac4d8367e206f5b2
     *                           83bff4ded3954c35862369983b88c41b
     *   cidnewsapp@gmail.com:   b23937d4bc7a475299e110d007318d28
     *   cidnewsapp1@gmail.com:  a4d2bb68bcae4cb9beb49105c53020f2
     *   cidnewsapp3@outlook.es: 177c6f87857545c18844e9e4b886dd69
     *   cidnewsapp4@outlook.es: 009aac2e199d434ebae570555e96f198
     *   cidnewsapp5@outlook.es: 23c8740aadb64619a124f1506393fb98
     *   cidnewsapp6@outlook.es: 8de9910a613a49289729a8725a0b9fcb
     *   cidnewsapp7@outlook.es: 4d3e24219543475eb1cdab5b79d29efd
     * */


    String urlBase = "https://newsapi.org/v2/everything?";
    String urlTypeSettings [] = {
            "q=healthy+technology","q=retail+technology","q=construction+technology",
            "q=entertainment+technology","q=environment+technology","q=education+technology",
            "q=energy+power+technology","q=economy+technology","q=telecom+technology"
    };


    String urlTypeMenuSlide [] = {
            "q=healthy+technology","q=construction+technology","q=retail+technology",
            "q=education+technology","q=entertainment+technology","q=environment+technology",
            "q=economy+technology","q=energy+power+technology","q=telecom+technology"
    };

    String urlSortBy = "&sortBy=popular";

    String urlAPIKey [] = {
            "&apiKey=99237f17c0b540fdac4d8367e206f5b2",
            "&apiKey=83bff4ded3954c35862369983b88c41b",
            "&apiKey=b23937d4bc7a475299e110d007318d28",
            "&apiKey=a4d2bb68bcae4cb9beb49105c53020f2",
            "&apiKey=177c6f87857545c18844e9e4b886dd69",
            "&apiKey=009aac2e199d434ebae570555e96f198",
            "&apiKey=23c8740aadb64619a124f1506393fb98",
            "&apiKey=8de9910a613a49289729a8725a0b9fcb",
            "&apiKey=4d3e24219543475eb1cdab5b79d29efd"
    };

    String urlLanguage = "&language=en";

    String urlDate = "&from=";


    /********************************  URL          ***********************************************/

    String urlTopNewsMenu[] = {
            "https://newsapi.org/v2/top-headlines?q=health&apiKey=99237f17c0b540fdac4d8367e206f5b2&language=en",                 //Headline Health API
            "https://newsapi.org/v2/top-headlines?q=construction&apiKey=83bff4ded3954c35862369983b88c41b&language=en",           //Headline Construction API
            "https://newsapi.org/v2/top-headlines?q=retail&apiKey=b23937d4bc7a475299e110d007318d28&language=en",                 //Headline Retail API
            "https://newsapi.org/v2/top-headlines?q=education&apiKey=a4d2bb68bcae4cb9beb49105c53020f2&language=en",              //Headline Education API
            "https://newsapi.org/v2/top-headlines?q=entertainment&apiKey=177c6f87857545c18844e9e4b886dd69&language=en",          //Headline Entertainment API
            "https://newsapi.org/v2/top-headlines?q=environment&apiKey=009aac2e199d434ebae570555e96f198&language=en",            //Headline Environment API
            "https://newsapi.org/v2/top-headlines?q=economy&apiKey=23c8740aadb64619a124f1506393fb98&language=en",                //Headline Finance API
            "https://newsapi.org/v2/top-headlines?q=energy&apiKey=8de9910a613a49289729a8725a0b9fcb&language=en",                 //Headline Energy API
            "https://newsapi.org/v2/top-headlines?q=telecom&apiKey=4d3e24219543475eb1cdab5b79d29efd&language=en"                 //Headline Telecom API
    };
    String urlDefaultNewsMenu[] = {
            "https://newsapi.org/v2/everything?q=health+technology&sortBy=popularity&apiKey=4d3e24219543475eb1cdab5b79d29efd&language=en",                //Default Health API
            "https://newsapi.org/v2/everything?q=construction+technology&sortBy=popularity&apiKey=8de9910a613a49289729a8725a0b9fcb&language=en",          //Default Construction API
            "https://newsapi.org/v2/everything?q=retail+technology&sortBy=popularity&apiKey=23c8740aadb64619a124f1506393fb98&language=en",                //Default Retail API
            "https://newsapi.org/v2/everything?q=education+technology&sortBy=popularity&apiKey=009aac2e199d434ebae570555e96f198&language=en",             //Default Education API
            "https://newsapi.org/v2/everything?q=entertainment+technology&sortBy=popularity&apiKey=177c6f87857545c18844e9e4b886dd69&language=en",         //Default Entertainment API
            "https://newsapi.org/v2/everything?q=environment+technology&sortBy=popularity&apiKey=a4d2bb68bcae4cb9beb49105c53020f2&language=en",           //Default Environment API
            "https://newsapi.org/v2/everything?q=technology+economy&sortBy=popularity&apiKey=b23937d4bc7a475299e110d007318d28&language=en",               //Default Finance API
            "https://newsapi.org/v2/everything?q=energy+power+technology&sortBy=popularity&language=en&apiKey=83bff4ded3954c35862369983b88c41b&language=en",//Default Energy API
            "https://newsapi.org/v2/everything?q=communications+technology&sortBy=popularity&apiKey=99237f17c0b540fdac4d8367e206f5b2&language=en"         //Default Telecom API
    };


    String urlTopNewsSettings[] = {
            "https://newsapi.org/v2/top-headlines?q=health&apiKey=99237f17c0b540fdac4d8367e206f5b2&language=en",                 //Headline Health API
            "https://newsapi.org/v2/top-headlines?q=retail&apiKey=b23937d4bc7a475299e110d007318d28&language=en",                 //Headline Retail API
            "https://newsapi.org/v2/top-headlines?q=construction&apiKey=83bff4ded3954c35862369983b88c41b&language=en",           //Headline Construction API
            "https://newsapi.org/v2/top-headlines?q=entertainment&apiKey=177c6f87857545c18844e9e4b886dd69&language=en",          //Headline Entertainment API
            "https://newsapi.org/v2/top-headlines?q=environment&apiKey=009aac2e199d434ebae570555e96f198&language=en",            //Headline Environment API
            "https://newsapi.org/v2/top-headlines?q=education&apiKey=a4d2bb68bcae4cb9beb49105c53020f2&language=en",              //Headline Education API
            "https://newsapi.org/v2/top-headlines?q=energy&apiKey=8de9910a613a49289729a8725a0b9fcb&language=en",                 //Headline Energy API
            "https://newsapi.org/v2/top-headlines?q=economy&apiKey=23c8740aadb64619a124f1506393fb98&language=en",                //Headline Finance API
            "https://newsapi.org/v2/top-headlines?q=telecom&apiKey=4d3e24219543475eb1cdab5b79d29efd&language=en"                 //Headline Telecom API
    };
    String urlDefaultNewsSettings[] = {
            "https://newsapi.org/v2/everything?q=health+technology&sortBy=popularity&apiKey=4d3e24219543475eb1cdab5b79d29efd&language=en",                //Default Health API
            "https://newsapi.org/v2/everything?q=retail+technology&sortBy=popularity&apiKey=23c8740aadb64619a124f1506393fb98&language=en",                //Default Retail API
            "https://newsapi.org/v2/everything?q=construction+technology&sortBy=popularity&apiKey=8de9910a613a49289729a8725a0b9fcb&language=en",          //Default Construction API
            "https://newsapi.org/v2/everything?q=entertainment+technology&sortBy=popularity&apiKey=177c6f87857545c18844e9e4b886dd69&language=en",         //Default Entertainment API
            "https://newsapi.org/v2/everything?q=environment+technology&sortBy=popularity&apiKey=a4d2bb68bcae4cb9beb49105c53020f2&language=en",           //Default Environment API
            "https://newsapi.org/v2/everything?q=education+technology&sortBy=popularity&apiKey=009aac2e199d434ebae570555e96f198&language=en",             //Default Education API
            "https://newsapi.org/v2/everything?q=energy+power+technology&sortBy=popularity&language=en&apiKey=83bff4ded3954c35862369983b88c41b&language=en",//Default Energy API
            "https://newsapi.org/v2/everything?q=technology+economy&sortBy=popularity&apiKey=b23937d4bc7a475299e110d007318d28&language=en",               //Default Finance API
            "https://newsapi.org/v2/everything?q=communications+technology&sortBy=popularity&apiKey=99237f17c0b540fdac4d8367e206f5b2&language=en"         //Default Telecom API
    };



    final String labelsForNews[] = {"salud", "construcción", "retail", "educación", "entretenimiento", "ambiente", "banca", "energía", "telecom"};
    final String labelsForNewsSettings[] = {"salud", "retail", "construcción", "entretenimiento","ambiente","educación", "energía",  "banca",  "telecom"};


    int marginScrollMenuBottom = 0;
    int sizeWidth = 0;
    int sizeHeight = 0;


    public MainActivity(){

    }

    /****************************************** LIFECYCLE *****************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Create!");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        hideSoftKeyboard();
        prefs = getSharedPreferences("com.amco.cidnews", MODE_PRIVATE);
        startTime = SystemClock.elapsedRealtime(); //Returns milliseconds since boot
        FirebaseApp.initializeApp(getApplicationContext());
        config_inicial();
        consultNoNewsShow("%");

        setContentView(R.layout.activity_main);

        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        urlDate = urlDate.concat(fDate);

        for(int i=0;i<9;i++) {
            urlTopNewsSettings[i] = urlBase.concat(urlTypeSettings[i].concat(urlSortBy.concat(urlAPIKey[i].concat(urlLanguage.concat(urlDate)))));
            urlTopNewsMenu[i] = urlBase+urlTypeMenuSlide[i]+urlSortBy+urlAPIKey[i]+urlLanguage+urlDate;

            Log.d(TAG,"onCreate -- urlTopNewsSettings["+i+"]:"+urlTopNewsSettings[i]);
            urlDefaultNewsSettings = urlTopNewsSettings;
            urlDefaultNewsMenu = urlTopNewsMenu;
        }

        //Scroll BottomNavigationView
        scrollMenuPosition = findViewById(R.id.scroll_menu_inferior);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        sizeWidth = size.x;
        sizeHeight = size.y;


        Log.d(TAG, "onCreate: YES -- sizeWidth:"+sizeWidth+"sizeHeight:"+sizeHeight);
        menuNavigation = findViewById(R.id.menu_navegation);
        menuNavigation.setOnNavigationItemSelectedListener(this);
        //BottomNavigationViewHelper.disableShiftMode(menuNavigation);

        imgBtnCross = findViewById(R.id.config_back);
        imgBtnCross.setVisibility(View.INVISIBLE);
        imgBtnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (databaseCountHelper() == 0) {
                    mensaje();
                }else{
                    menuNavigation.setSelectedItemId(R.id.home_nav);
                    imgBtnCross.setVisibility(View.INVISIBLE);

                    mFlagHome=false;
                    mFlagFavorites=true;
                    mFlagRecover=true;
                    mFlagSettings=true;
                }
            }
        });


        if (prefs.getBoolean("firstrun", true)) {
            ventanaEmergente();
            prefs.edit().putBoolean("firstrun", false).apply();
        }


        if(isNetworkStatusAvailable(this)){
            Log.d(TAG,"onCreate -- isNetworkStatusAvailable!");
            firstGetRequestAPI(urlTopNewsSettings,0);

        }else{
            Log.d(TAG,"onCreate -- NoInternetAvailable!");
            //  slideDown(mMsjInternet);
        }

        menuNavigation.setSelectedItemId(R.id.home_nav);
    }


    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected  void onResume(){
        super.onResume();
        Log.d(TAG, "onResume: TRUE");
        Log.e(TAG, "onResume:" + String.valueOf(getRunningTime()));

        mFlagFavorites=true;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Log.d(TAG,"onBackPressed");
        moveTaskToBack(true); // I don't think you're looking for this.
        super.onBackPressed();
    }


    @Override
    public void finish() {
        //super.finish();
        moveTaskToBack(true);
    }

    @Override
    protected  void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart: TRUE");

        if(HomeFragment.cardviewContainer != null) {
            HomeFragment.cardviewContainer.setScrollY(0);
        }

    }

    ///////////////////////////////////
    /// MARK:
    @Override
    public  boolean isFinishing(){
        super.isFinishing();
        Log.d(TAG, "Finishing: TRUE ");
        return false;
    }

    ///////////////////////////////////
    /// MARK:
    private void ventanaEmergente() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        final DialogFragment dialogFragment = new MyDialogFragment();
        dialogFragment.show(ft, "dialog");
    }




    ///////////////////////////////////
    /// MARK:
    public int databaseCountHelper() {

        int allStatesSum = 0;

        for(int i=0;i<9;i++){
            if(stateArrayMain[i])
                allStatesSum += 1;
        }

        return allStatesSum;
    }

    ///////////////////////////////////
    /// MARK:
    private boolean loadFragmant(androidx.fragment.app.Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contendor, fragment).commit();
            return true;
        }
        return false;
    }

    ///////////////////////////////////
    /// MARK:
    public void mensaje(){

        Toast toast = new Toast(this);
        View toast_layout = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.lytLayout));
        toast.setView(toast_layout);

        //tenemos acceso a cualquier widget del layout del Toast
        TextView textView = (TextView) toast_layout.findViewById(R.id.toastMessage);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,548);
        toast.show();

    }

    ///////////////////////////////////
    /// MARK: Main Bottom Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        androidx.fragment.app.Fragment fragment = null;

        boolean itemSelected;
        if (databaseCountHelper() == 0) {
            mensaje();
            itemSelected = false;
        } else {
            switch (item.getItemId()) {
                case R.id.home_nav:
                    if (mFlagHome){
                        Log.d(TAG, "onNavigationItemSelected: HomeFragment");

                        // [START Menu_selected_event]
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Home");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Main Menu");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        // [END Menu_selected_event]

                        //
                        animationScrollMenuBottom(marginScrollMenuBottom,0);
                        marginScrollMenuBottom = 0;
                        //

                        mFlagHome=false;
                        mFlagFavorites=true;
                        mFlagRecover=true;
                        mFlagSettings=true;
                        animateCrossButton = false;

                        item.setEnabled(true);
                        fragment = new HomeFragment();
                        imgBtnCross.setVisibility(View.INVISIBLE);


                        if(changeStateSetting){
                            Log.d(TAG, "onNavigationItemSelected: HomeFragment changeStateSetting: TRUE");
                            changeStateSetting = false;
                            allNews.clear();
                            allNews = new ArrayList<>();
                            firstGetRequestAPI(urlTopNewsSettings,0);
                        }
                    }
                    break;

                case R.id.fav_nav:   //Favoritos
                    if(mFlagFavorites){
                        // [START Menu_selected_event]
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Favorites");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Main Menu");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        // [END Menu_selected_event]

                        //
                        animationScrollMenuBottom(marginScrollMenuBottom,sizeWidth/4);
                        marginScrollMenuBottom = sizeWidth/4;
                        //
                        mFlagHome = true;
                        mFlagFavorites=false;
                        mFlagRecover=true;
                        mFlagSettings=true;

                        fragment = new FavFragment();
                        imgBtnCross.setVisibility(View.INVISIBLE);
                        animateCrossButton = !consultarNoticiasFavoritas("%");
                    }else
                        animateCrossButton=false;
                    break;



                case R.id.recover_nav:
                    if(mFlagRecover){
                        // [START Menu_selected_event]
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Recover");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Main Menu");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        // [END Menu_selected_event]
                        //
                        animationScrollMenuBottom(marginScrollMenuBottom,sizeWidth/2);
                        marginScrollMenuBottom = sizeWidth/2;
                        //

                        mFlagHome = true;
                        mFlagFavorites=true;
                        mFlagRecover=false;
                        mFlagSettings=true;
                        fragment = new RecoverFragment();

                        animateCrossButton = false;
                        imgBtnCross.setVisibility(View.INVISIBLE);
                    }
                    break;


                case R.id.config_nav:  //Preferencias
                    if(mFlagSettings) {
                        // [START Menu_selected_event]
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Settings");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Main Menu");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        // [END Menu_selected_event]
                        //
                        animationScrollMenuBottom(marginScrollMenuBottom,sizeWidth/2 + sizeWidth/4);
                        marginScrollMenuBottom = sizeWidth/2 + sizeWidth/4;
                        //
                        mFlagHome = true;
                        mFlagFavorites = true;
                        mFlagRecover=true;
                        mFlagSettings = false;

                        fragment = new ConfigFragment();
                        animateCrossButton = true;
                    }
                    break;

            }
            itemSelected = loadFragmant(fragment);

            if(animateCrossButton) {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(800);
                imgBtnCross.setAnimation(fadeIn);
                imgBtnCross.setVisibility(View.VISIBLE);
                imgBtnCross.setEnabled(true);

                mFlagHome=true;
            }

        }
        /*Fin de menu inferior*/
        return itemSelected;
    }


    ///////////////////////////////////
    /// MARK:
    private boolean consultarNoticiasFavoritas(String categoria) {
        conn = new ConexionSQLiteHelper(this,"db_noticias",null,1);
        SQLiteDatabase db = conn.getReadableDatabase();
        String [] parametros = {categoria.toString()};
        Cursor cursor = db.rawQuery("SELECT * FROM "+Utilidades.TABLA_NOTICIA+" WHERE "+Utilidades.CATEGORIA+" LIKE ?",parametros);
       if(cursor.getCount()==0) {
            Log.d(TAG, "consultarNoticiasFavoritas --  cursor.getCount = 0");
            cursor.close();
            return false;
        }
        else {
           cursor.close();
           return true;
        }
    }

    ///////////////////////////////////
    /// MARK:
    public static long getRunningTime(){
        return SystemClock.elapsedRealtime() - startTime;
    }




    ///////////////////////////////////
    /// MARK: Check the state of the database turning ON all preferences (first time) or check each state.
    public boolean config_inicial() {
        String[] categorias = {"SALUD", "RETAIL", "CONSTRUCCIÓN", "ENTRETENIMIENTO", "AMBIENTE", "EDUCACIÓN", "ENERGÍA", "BANCA", "TELECOM"};
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "db_noticias", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_PREFERENCIA + "", null);
        ContentValues valores = new ContentValues();

        if (cursor.getCount() == 0) {  //There is no DB existing
            Log.e(TAG, "config_inicial<------ CARGANDO BASE UNA UNICA VEZ");
            for (int i = 0; i < categorias.length; i++) {
                valores.put(Utilidades.ESTADO, 1);
                valores.put(Utilidades.CATEGORIA, categorias[i]);
                db.insert(Utilidades.TABLA_PREFERENCIA, null, valores);
                stateArrayMain[i] = true;
            }
            db.close();
            return true;
        } else {//If exist a configuration take the data from db.
            Log.e(TAG, "config_inicial<------  lista la configuracion");
            cursor.moveToFirst();
            for (int i = 0; i < categorias.length; i++) {
                if(cursor.getInt(1) == 0)
                    stateArrayMain[i] = false;
                else
                    stateArrayMain[i] = true;
                cursor.moveToNext();
            }
        }
        db.close();
        return false;
    }

    ///////////////////////////////////
    /// MARK: This is a first Request to the API NEWS for a better UX. After a Success if the is no news
    //for show, it would go for the next URL
    public void firstGetRequestAPI(final String [] typeUrl, final int mNewsIndex){
        final AsyncHttpClient mClient = requestHttpClient();
        final RequestParams mParams = new RequestParams();


            if (stateArrayMain[mNewsIndex]) {
                Log.d(TAG, "firstGetRequestAPI -- onSuccess, stateArrayMain:[" +
                        mNewsIndex + "]:" + stateArrayMain[mNewsIndex]);

                RequestHandle mRequest = mClient.get(typeUrl[mNewsIndex], mParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject mResponse = new JSONObject(new String(responseBody));
                            Log.d(TAG, "firstGetRequestAPI -- onSuccess, mFirstNewsIndex:" + mNewsIndex);
                            Log.d(TAG, "firstGetRequestAPI -- onSuccess, totalResults:" + mResponse.getInt("totalResults"));
                            Log.d(TAG, "firstGetRequestAPI -- onSuccess, labelsForNews:" + labelsForNewsSettings[mNewsIndex]);

                            if (mResponse.getInt("totalResults") == 0) {

                                if (mNewsIndex + 1 < 9) {
                                    firstGetRequestAPI(typeUrl, mNewsIndex + 1); //???
                                }else
                                    Toast.makeText(getApplicationContext(),
                                            "All news have been seen",Toast.LENGTH_SHORT).show();
                            } else {

                                //Let know to the HomeFrag that we have news!
                                allNews = addMoreNews(new String(responseBody), labelsForNewsSettings[mNewsIndex], allNews);

                                if (allNews.size() == 0 ){

                                    if (mNewsIndex + 1 < 9) {
                                        firstGetRequestAPI(typeUrl, mNewsIndex + 1); //???
                                    }else
                                        Toast.makeText(getApplicationContext(),
                                                "All news have been seen",Toast.LENGTH_SHORT).show();

                                }else {
                                    if (activityListener != null) {
                                        activityListener.setGeneralNews();
                                    }


                                    if (mNewsIndex + 1 < 9) {
                                        generalRecursive(mNewsIndex + 1, false, true);
                                    }else
                                        Toast.makeText(getApplicationContext(),
                                                "All news have been seen",Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "firstGetRequestAPI -- onFailure:" + e.toString());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {


                        Log.d(TAG, "firstGetRequestAPI -- onFailure: statusCode:" + statusCode);


                        if(responseBody != null) {
                            if (responseBody.length != 0) {
                                try {
                                    JSONObject mResponse = new JSONObject(new String(responseBody));

                                    Log.d(TAG, "firstGetRequestAPI -- onFailure: names:" + mResponse.names());
                                    Log.d(TAG, "firstGetRequestAPI -- onFailure: toString:" + mResponse.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "firstGetRequestAPI -- onFailure Failure:" + e.toString());
                                }
                            }

                        }
                    }
                });

            }else {

                if (mNewsIndex + 1 < 9) {
                    firstGetRequestAPI(typeUrl, mNewsIndex + 1); //???
                }else
                    Toast.makeText(getApplicationContext(),
                            "All news requested?",Toast.LENGTH_SHORT).show();
            }

        //}
    }

    ///////////////////////////////////
    /// MARK:
    public void generalRecursive(final int index, final boolean defNews, final boolean topNews){
        if(topNews)
            typeUrl = urlTopNewsSettings;
        else
            typeUrl = urlDefaultNewsSettings;

        AsyncHttpClient mClient = requestHttpClient();
        final RequestParams mParams = new RequestParams();
        if(index < 9) {
            if (stateArrayMain[index]) {
                RequestHandle mRequest = mClient.get(typeUrl[index], mParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(TAG,"generalRecursive -- onSuccess, Index:" +index);
                        Log.d(TAG,"generalRecursive -- onSuccess, allNews.size(before):" +allNews.size());
                        Log.d(TAG,"generalRecursive -- onSuccess, labelsForNews:" +labelsForNewsSettings[index]);
                        allNews = addMoreNews(new String(responseBody), labelsForNewsSettings[index], allNews);
                        Log.d(TAG,"generalRecursive -- onSuccess, allNews.size(after):" +allNews.size());
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                    @Override
                    public void onFinish() {
                        if(index == 8 && defNews){
                            //FINISH
                            Log.d(TAG,"generalRecursive  -- FinishALL allNews.size:"+allNews.size());
                            if (null != activityListener) {
                                activityListener.setGeneralNews();
                            }
                        }else {
                            Log.d(TAG,"generalRecursive  -- onFinish, generalRecursive, defNews:"+defNews);
                            Log.d(TAG,"generalRecursive  -- onFinish, generalRecursive, topNews:"+topNews);

                            generalRecursive(index + 1, defNews, topNews);
                        }
                    }
                });
            }else {
                if(index == 8 && defNews){
                    //FINISH
                    if(HomeFragment.Swipadaptador.getCount() == 0){
                        if (null != activityListener) {
                            activityListener.setGeneralNews();
                        }
                    }
                    Log.d(TAG,"generalRecursive  -- FinishALL allNews.size:"+allNews.size());
                }else
                    generalRecursive(index + 1, defNews, topNews);
            }
        }else
            generalRecursive(0,true,false);
    }


    ///////////////////////////////////
    /// MARK:
    public void specificRecursive(final int index){
        Log.d(TAG,"specificRecursive, allNews.size(before):" +allNews.size());
        allNewsMenu.clear();
        Log.d(TAG,"specificRecursive, allNews.size(after):" +allNews.size());

        final AsyncHttpClient mClient = requestHttpClient();
        final RequestParams mParams = new RequestParams();
                final RequestHandle mRequestTop = mClient.get(urlTopNewsMenu[index], mParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(TAG,"specificRecursive -- onSuccess, TopNews Index:" +index);
                        Log.d(TAG,"specificRecursive -- onSuccess, allNewsMenu.size(before):" +allNewsMenu.size());
                        Log.d(TAG,"specificRecursive -- onSuccess, labelsForNews:" +labelsForNews[index]);
                        Log.d(TAG,"specificRecursive -- onSuccess, allNews.size(after)!!:" +allNews.size());

                        allNewsMenu = addMoreNews(new String(responseBody), labelsForNews[index], allNewsMenu);
                        Log.d(TAG,"generalRecursive -- onSuccess, allNewsMenu.size(after):" +allNewsMenu.size());
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                    @Override
                    public void onFinish() {
                        RequestHandle mRequestDefault = mClient.get(urlDefaultNewsMenu[index], mParams, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Log.d(TAG,"specificRecursive -- onSuccess, DefaultNews");
                                Log.d(TAG,"specificRecursive -- onSuccess, allNewsMenu.size(before):" +allNewsMenu.size());
                                Log.d(TAG,"specificRecursive -- onSuccess, labelsForNews:" +labelsForNews[index]);
                                allNewsMenu = addMoreNews(new String(responseBody), labelsForNews[index], allNewsMenu);
                                Log.d(TAG,"specificRecursive -- onSuccess, allNewsMenu.size(after):" +allNewsMenu.size());
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }

                            @Override
                            public void onFinish(){
                                Log.d(TAG,"specificRecursive -- onFinish, ALLNewsMenu.size:" +allNewsMenu.size());
                                //Make the change for homeFrag
                                if (null != activityListener) {
                                    Log.d(TAG,"generalRecursive -- onFinish, allNews.size(after) onFinish:" +allNews.size());

                                    activityListener.setSpeficicNewsFromMenu(index);
                                }
                            }
                        });
                    }
                });
    }


    ///////////////////////////////////
    /// MARK: Add more news to the arrayList
    public ArrayList<Noticia> addMoreNews(String response, String categoria, ArrayList<Noticia> list) {
        ArrayList<Noticia> mHelperList = new ArrayList<>();

        if(allNewsDeleted != null)
            mHelperList.addAll(allNewsDeleted);
        else
            mHelperList = null;

        try {
            JSONObject jsonArray = new JSONObject(response);
            Boolean flagNoAdd = false;
            String titulo;
            String imagen;
            String url;
            String description;
            String autor;
            String categoria_local = categoria;
            Noticia mNewsHelper;


            JSONArray articulos = jsonArray.getJSONArray("articles");

            for (int i = 0; i < articulos.length(); i++) {
                titulo = articulos.getJSONObject(i).getString("title");
                imagen = articulos.getJSONObject(i).getString("urlToImage");
                url = articulos.getJSONObject(i).getString("url");
                description = articulos.getJSONObject(i).getString("description");
                autor = articulos.getJSONObject(i).getJSONObject("source").getString("name");
                if (!autor.equals("TechCrunch")) {
                    if(mHelperList == null) {
                        list.add(new Noticia(titulo, imagen, url, description, autor, categoria_local, 0L));
                    }else{
                        if (mHelperList.size() != 0) {
                        /*
                        mNewsHelper = new Noticia(titulo, imagen, url, description, autor, categoria_local, 0L);

                        if (allNewsDeleted.contains(mNewsHelper)) {
                            Log.d("MainActivity", "addMoreNEWS. allNEWSDELETED CONTAINS IN:" + i);
                            // duplicateList.add(item);
                        } else {
                            list.add(new Noticia(titulo, imagen, url, description, autor, categoria_local, 0L));
                        }
                        //
                        for (int j = 0; j < mHelperList.size(); j++) {
                                if (url.equals(mHelperList.get(j).getUrl())) {
                                    flagNoAdd = true;
                                    j = mHelperList.size();
                                    mHelperList.remove(j);
                                    //allNewsDeletedHelper.remove(j);
                                    Log.e("HomeFragment", "addMoreNews -- mHelperList.size: " +mHelperList.size());

                                    //Log.e("HomeFragment", "addMoreNews -- noNewsForShow != NULL" + String.valueOf(getRunningTime()));
                                    Log.e("HomeFragment", "addMoreNews -- url Match: " + String.valueOf(url));

                                } else {
                                    if ((j + 1) == mHelperList.size()) {
                                        list.add(new Noticia(titulo, imagen, url, description, autor, categoria_local, 0L));
                                        Log.e("HomeFragment", "addMoreNews -- url ADD: " + String.valueOf(url));
                                    } else {
                                        flagNoAdd = false;
                                       // Log.e("HomeFragment", "addMoreNews -- url should NO ADD: " + String.valueOf(url));
                                    }
                                }
                            }
                        */
                            for (int j = mHelperList.size() - 1; j >= 0; j--) {
                                if (url.equals(mHelperList.get(j).getUrl())) {
                                    mHelperList.remove(j);
                                    j = -1;
                                    //allNewsDeletedHelper.remove(j);
                                    Log.e(TAG, "addMoreNews -- mHelperList.size: " +mHelperList.size());
                                    //Log.e("HomeFragment", "addMoreNews -- noNewsForShow != NULL" + String.valueOf(getRunningTime()));
                                    Log.e(TAG, "addMoreNews -- url Match: " + url);

                                } else {
                                    if (j == 0) {
                                        list.add(new Noticia(titulo, imagen, url, description, autor, categoria_local, 0L));
                                        Log.e(TAG, "addMoreNews -- url ADD: " + String.valueOf(url));
                                    }
                                }
                            }
                        } else
                            list.add(new Noticia(titulo, imagen, url, description, autor, categoria_local, 0L));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "addMoreNews -- Error:" + e);
        }
        return list;
    }

    ///////////////////////////////////
    /// MARK: HttpClient
    public AsyncHttpClient requestHttpClient() {
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(5000);
        client.setResponseTimeout(5000);
        client.setTimeout(5000);
        client.setMaxRetriesAndTimeout(1, 100);
        return client;
    }


    ///////////////////////////////////
    /// MARK:
    public void setActivityListener(ListenFromActivity activityListener) {
        this.activityListener = activityListener;
    }


    ///////////////////////////////////
    /// MARK:
    public static boolean isNetworkStatusAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }


    ///////////////////////////////////
    /// MARK:
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    ///////////////////////////////////
    /// MARK:
    public void setupChangeState(){
        Log.d("MainActivity","setupChangeState");
        changeStateSetting = true;
    }

    ///////////////////////////////////
    /// MARK:
    public void refreshDeletedNews(){

    }
    ///////////////////////////////////
    /// MARK:
    private void consultNoNewsShow(String categoria) {

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this,"db_noticias",null,1);
        SQLiteDatabase db = conn.getReadableDatabase();
        String [] parametros = {categoria.toString()};
        Cursor cursor = db.rawQuery("SELECT * FROM "+Utilidades.TABLA_RECUPERAR+" WHERE "+Utilidades.CATEGORIA+" LIKE ?",parametros);
        Noticia noticia = null;
        if(cursor.getCount()==0)
        {
            Log.e("RecoverFragment", "consultNoNewsShow: EMPTY");
            allNewsDeleted = null;
        }
        else
        {
            Log.e("RecoverFragment", "consultNoNewsShow: NEWS!");
            while (cursor.moveToNext()) {
                noticia = new Noticia(cursor.getString(0), cursor.getString(1), cursor.getString(2), "", cursor.getString(3), cursor.getString(4), cursor.getLong(5));
                allNewsDeleted.add(noticia);
                allNewsDeletedHelper =allNewsDeleted;
            }
        }
        cursor.close();
        db.close();
        conn.close();
    }


    /********************************************* NO USE ******************************************/

    public void deleteTempDB() {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "db_noticias", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_NOTICIA + "", null);
        db.delete(Utilidades.TABLA_NOTICIA, null, null);
        cursor.close();
        db.close();
        conn.close();
    }
    public void getConfigDefaultAPINews(){
    }
    private void printKeyHash(){
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.amco.cidnews",PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:",Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    ///////////////////////////////////
    /// MARK:
    private int estado(String categoria) {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "db_noticias", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        String[] parametros = {categoria};
        Cursor cursor = db.rawQuery("SELECT estado FROM " + Utilidades.TABLA_PREFERENCIA + " where " + Utilidades.CATEGORIA + " =?", parametros);
        cursor.moveToFirst();
        db.close();///////////*******************   CLOSE 13SEP

        return cursor.getInt(0);
    }

    public void animationScrollMenuBottom(int marginOrigin, int marginDestiny){
        //Animation WebView
        final int marginStart = marginOrigin;// your start value
        final int marginEnd = marginDestiny; // where to animate to
        Log.d(TAG,"animationScrollMenuBottom --  marginEnd:"+marginEnd);
        Log.d(TAG,"animationScrollMenuBottom --  marginStart:"+marginStart);


        Animation mAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                /*ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) scrollMenuPosition.getLayoutParams();
                params.setMarginStart(marginEnd);
                scrollMenuPosition.setLayoutParams(params);*/

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) scrollMenuPosition.getLayoutParams();
                params.setMarginStart(marginStart + (int) ((marginEnd - marginStart) * interpolatedTime));

                //params.leftMargin = marginStart + (int) ((marginEnd - marginStart) * interpolatedTime);
                scrollMenuPosition.setLayoutParams(params);
            }
        };
        mAnimation.setDuration(600); // in ms
        scrollMenuPosition.startAnimation(mAnimation);
    }

}

    /* ******************************************* TRASH ******************************************/
