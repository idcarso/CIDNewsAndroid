package com.amco.cidnews.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amco.cidnews.Fragments.AboutFragment;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static com.amco.cidnews.Fragments.HomeFragment.SWIPESTACK_SCROLLING;
import static com.amco.cidnews.Fragments.HomeFragment.estadoDrawer;

//region Informacion Base de datos

/**
 * Nombre de la base de datos: db_noticias
 *
 * Tabla Noticias (Clase Utilidades): Guarda las noticias favoritas (Favorite)
 * ----------------------------------
 * |    noticias (TABLA_NOTICIA)    |
 * ----------------------------------
 * | titulo (TITULO) > TEXT         |
 * | imagen (IMAGEN) > TEXT         |
 * | url (URL) > TEXT               |
 * | autor (AUTOR) > TEXT           |
 * | categoria (CATEGORIA) > TEXT   |
 * | tiempo (TIEMPO) > LONG         |
 * ----------------------------------
 *
 * Tabla Recuperar (Clase Utilidades): Guarda las noticias eliminadas (Recover)
 * ----------------------------------
 * |   recuperar (TABLA_RECUPERAR)  |
 * ----------------------------------
 * | titulo (TITULO) > TEXT         |
 * | imagen (IMAGEN) > TEXT         |
 * | url (URL) > TEXT               |
 * | autor (AUTOR) > TEXT           |
 * | categoria (CATEGORIA) > TEXT   |
 * | tiempo (TIEMPO) > LONG         |
 * ----------------------------------
 *
 * Tabla Preferencia (Clase Utilidades): Guarda el estado de las preferencias (Setting)
 * ------------------------------------
 * | preferencias (TABLA_PREFERENCIA) |
 * ------------------------------------
 * | categoria (CATEGORIA) > TEXT     |
 * | estado (ESTADO) > INT            |
 * ------------------------------------
 */

//endregion

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //region VARIABLES
    FragmentManager fragmentManager;
    androidx.fragment.app.FragmentTransaction fragmentTransaction;
    private static final String TAG = "MainActivity.java";
    public static long startTime;
    private boolean isActiveHome = false;
    private boolean isActiveFavorites = false;
    private boolean isActiveRecover = false;
    private boolean isActiveSettings = false;
    private boolean isActiveAboutUs = false;
    boolean isChangeSetting;
    boolean isChangeNetworkAvailable;
    private SharedPreferences sharedPreferences;
    public static boolean currentStatusNetwork;

    //Fragments
    HomeFragment homeFragment;
    FavFragment favoriteFragment;
    RecoverFragment recoverFragment;
    ConfigFragment settingFragment;

    //Array list
    public ArrayList<Noticia> allNews = new ArrayList<>();
    public ArrayList<Noticia> allNewsMenu = new ArrayList<>();
    public ArrayList<Noticia> allNewsDeleted = new ArrayList<>();

    //Interfaces
    public ListenFromActivity activityListener;

    //Array
    String [] typeUrl;
    final String labelsForNews[] = {"salud", "construcción", "retail", "educación", "entretenimiento", "ambiente", "banca", "energía", "telecom"};
    final String labelsForNewsSettings[] = {"salud", "retail", "construcción", "entretenimiento","ambiente","educación", "energía",  "banca",  "telecom"};
    String categoria[] = {"HEALTH", "RETAIL", "CONSTRUCTION", "ENTERTAINMENT", "ENVIRONMENT", "EDUCATION", "ENERGY", "FINANCE", "TELECOM"};

    //Flags
    public static boolean mFlagHome=true;
    public boolean [] stateArrayMain = {false,false,false ,false,false,false ,false,false,false};

    //Database SQLite
    ConexionSQLiteHelper conn;

    //Data API
    String urlBase = "https://newsapi.org/v2/everything?";
    String urlTypeSettings [] = {
            "q=healthy+technology",
            "q=retail+technology",
            "q=construction+technology",
            "q=entertainment+technology",
            "q=environment+technology",
            "q=education+technology",
            "q=energy+power+technology",
            "q=economy+technology",
            "q=telecom+technology"
    };
    String urlTypeMenuSlide [] = {
            "q=healthy+technology",
            "q=construction+technology",
            "q=retail+technology",
            "q=education+technology",
            "q=entertainment+technology",
            "q=environment+technology",
            "q=economy+technology",
            "q=energy+power+technology",
            "q=telecom+technology"
    };
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
    String urlSortBy = "&sortBy=popular";
    String urlLanguage = "&language=en";
    String urlDate = "&from=";

    //region URL API
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
    //endregion

    //Firebase analytics
    private FirebaseAnalytics mFirebaseAnalytics;

    //UI
    int marginScrollMenuBottom = 0;
    int sizeWidth = 0;
    int sizeHeight = 0;
    //endregion

    //region WIDGETS
    public static BottomNavigationView menuNavigation;
    public ImageButton imgBtnCross;
    public RelativeLayout.LayoutParams layoutParamsNewsBackUp;
    public static ImageView scrollMenuPosition; //Barra de scroll blanca
    public static DrawerLayout drawerLayout; //Menu Home
    //endregion

    //region API Key News API
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
    //endregion

    //region CICLO DE VIDA ACTIVITY
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Oculta el teclado
        hideSoftKeyboard();

        //Verifica la instalacion de la app
        sharedPreferences = getSharedPreferences("com.amco.cidnews", MODE_PRIVATE);
        verifyFirstRunApp(sharedPreferences);

        startTime = SystemClock.elapsedRealtime(); //Returns milliseconds since boot

        FirebaseApp.initializeApp(getApplicationContext());

        //Verifica la configuracion del
        getSettingsPreferences();

        consultNewsRecover();

        //Inflacion de la activity
        setContentView(R.layout.activity_main);

        //Fecha del dia
        urlDate = urlDate.concat(getCurrentDate());

        //Actualiza las ligas de peticion a la API
        setUpdateUrlApi();

        //Enlace de widgets
        scrollMenuPosition = findViewById(R.id.scroll_menu_inferior);
        menuNavigation = findViewById(R.id.menu_navegation);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        imgBtnCross = findViewById(R.id.config_back);

        //Tamaño de pantalla
        sizeWidth = getWidthCurrentScreen();
        sizeHeight = getHeightCurrentScreen();
        Log.d(TAG, "onCreate() --> Screen width = " + sizeWidth + " || Screen height = " + sizeHeight);

        //Listener Bottom Navigation View
        menuNavigation.setOnNavigationItemSelectedListener(this);

        //Muestra el fragment Home al iniciar la app
        setStartFragment();

        //Verifica si hay alguna opcion activa en Setting
        if (getEnableOptionsSetting() == 0) {
            menuNavigation.setSelectedItemId(R.id.config_nav);
        }

        //listener Image Button
        imgBtnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getEnableOptionsSetting() == 0) {
                    Log.i(TAG, "imgBtnCross.setOnClickListener() --> Activa una opcion en Setting"); //Se puede eliminar
                    showAlertSetting();
                } else {
                    Log.i(TAG, "imgBtnCross.setOnClickListener() --> Opciones activas = " + getEnableOptionsSetting()); //Se puede eliminar
                    menuNavigation.setSelectedItemId(R.id.home_nav);
                }
            }
        });
    }

    @Override
    protected  void onRestart(){
        super.onRestart();
        if (HomeFragment.cardviewContainer != null) {
            HomeFragment.cardviewContainer.setScrollY(0);
        }
    }

    //endregion

    //region METHODS

    /**
     * Método que responde desde el fragment HOME para instanciar la Interface ListenFromActivity.
     * @param activityListener Objeto de la clase ListenFromActivity.
     */
    public void setActivityListener(ListenFromActivity activityListener) {
        this.activityListener = activityListener;
    }

    /**
     * Método que oculta el teclado.
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Método que muestra el tutorial cuando la app se instala por primera vez.
     */
    private void showTutorialApplication() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragmentAlertSetting = fragmentManager.findFragmentByTag("dialog");
        if (fragmentAlertSetting != null) {
            fragmentTransaction.remove(fragmentAlertSetting);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment dialogFragment = new MyDialogFragment();
        dialogFragment.show(fragmentTransaction, "dialog");
    }

    /**
     * Método que muestra en pantalla Toast personalizado para notificar que se debe seleccionar al menos una opcion en Setting.
     */
    public void showAlertSetting(){
        Toast toast = new Toast(MainActivity.this);
        View viewToastLayout = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.lytLayout));
        toast.setView(viewToastLayout);

        //Se puede acceder a los widgets del Toast
        TextView textView = viewToastLayout.findViewById(R.id.toastMessage);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 548);
        toast.show();
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 21/05/2020</b></h2></p>
     * <br>
     *     Método que verifica si se instala por primera vez la aplicaciòn o no.
     *     <br>
     *         Elimina registros anteriores de las tablas por instalaciones previas.
     *         <br>
     *             Muestra el tutorial de la app.
     * @param sharedPreferences Variable de persistencia de datos.
     */
    private void verifyFirstRunApp(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getInt("FIRST_RUN_APP", 0) == 0) {
            showTutorialApplication();
            editor.putInt("FIRST_RUN_APP", 1);
            editor.commit();
            Log.i(TAG, "verifyFirstRunApp() --> App instalada por primera vez");
            clearTable("Noticias");
            clearTable("Recuperar");
            clearTable("Preferencias");
            getSettingsPreferences();
        } else if (sharedPreferences.getInt("FIRST_RUN_APP", 0) == 1) {
            Log.i(TAG, "verifyFirstRunApp() --> Ya instalada");
        }
    }

    /**
     * <p><b><h2>Created by Alejandro Jiménez on 18/05/2020</h2></b></p>
     * <br>
     *      Método que obtiene la configuración de PREFERENCIAS.
     *      <br>Si la tabla no tiene datos, llena la tabla por default con 1.
     *      <br>Si la tabla ya tiene datos, obtiene la configuración del usuario.
     */
    private  void getSettingsPreferences() {
        Log.i(TAG, "setSettingsPreferences()");
        String[] categoria  =  {"SALUD", "RETAIL", "CONSTRUCCIÓN",  "ENTRETENIMIENTO", "AMBIENTE", "EDUCACIÓN", "ENERGÍA", "BANCA", "TELECOM"};
        ConexionSQLiteHelper c = new ConexionSQLiteHelper(MainActivity.this, "db_noticias", null, 1);
        SQLiteDatabase database = c.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + Utilidades.TABLA_PREFERENCIA + ";", null);
        ContentValues contentValues = new ContentValues();
        if (cursor.getCount() == 0) { //No hay nada registrado en la tabla PREFERENCIAS, se llena por default
            Log.i(TAG, "setSettingsPreferences() --> Tabla PREFERENCIAS llenada por default");
            for (int i = 0;  i < categoria.length ;  i++) {
                contentValues.put(Utilidades.ESTADO, 1); //1 = Activo || 0 = Inactivo
                contentValues.put(Utilidades.CATEGORIA, categoria[i]);
                database.insert(Utilidades.TABLA_PREFERENCIA, null, contentValues);
                stateArrayMain[i] = true;
            }
            database.close();
        } else { //Se obtiene la configuracion del usuario
            Log.i(TAG, "setSettingsPreferences() --> Configuracion de preferencias del usuario");
            cursor.moveToFirst();
            for (int i = 0; i < categoria.length; i++) {
                if (cursor.getInt(1) == 0) {
                    stateArrayMain[i] = false;
                } else {
                    stateArrayMain[i] =  true;
                }
                cursor.moveToNext();
            }
        }
        database.close();
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 18/05/2020</b></h2></p>
     * <br>
     *     Método que obtiene las noticias almacenadas en la tabla de RECOVER.
     *     <br>Si no hay noticias en la tabla de RECOVER, hace nulo allNewsDeleted.
     *     <br>Si hay noticias en la tabla de Recover, obtiene los datos de cata noticia y los almacena en allNewsDeleted.
     */
    private void consultNewsRecover() {
        Log.i(TAG, "consultNewsRecover()");
        Noticia noticia = null;
        ConexionSQLiteHelper c = new ConexionSQLiteHelper(this, "db_noticias", null, 1);
        SQLiteDatabase database = c.getReadableDatabase();
        String wildcard = "%";
        Cursor cursor = database.rawQuery("SELECT * FROM " + Utilidades.TABLA_RECUPERAR + " WHERE " + Utilidades.CATEGORIA + " LIKE '" + wildcard + "';", null);
        if (cursor.getCount() == 0) {
            Log.i(TAG, "consultNewsRecover() --> No hay noticias en RECOVER");
            allNewsDeleted = null;
        } else {
            Log.i(TAG, "consultNewsRecover() --> Total de noticias en RECOVER = " + cursor.getCount());
            while(cursor.moveToNext()) {
                noticia = new Noticia(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        "",
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getLong(5));
                allNewsDeleted.add(noticia);
                //allNewsDeletedHelper = allNewsDeleted; Hasta el momento no hace nada. Fecha de verificacion 18/05/2020
            }
        }
        cursor.close();
        database.close();
        c.close();
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 18/05/2020</b></h2></p>
     * <br>
     *     Método que obtiene la fecha actual de sistema.
     * @return Fecha actual en formato yyyy-MM-dd en tipo String.
     */
    private String  getCurrentDate() {
        Date date = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        Log.i(TAG, "getCurrentDate() --> Fecha actual de sistema = " + currentDate); //Se puede eliminar la line
        return currentDate;
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 19/05/2020</b></h2></p>
     * <br>
     *     Método que actualiza al día la URL de la API que son base para consultar por parte de HOME y de MENU.
     */
    private void setUpdateUrlApi() {
        for (int i = 0; i < 9; i++) {
            Log.i(TAG, "setUpdateUrlApi()");
            urlTopNewsSettings[i] = urlBase + urlTypeSettings[i] + urlSortBy + urlAPIKey[i] + urlLanguage + urlDate;
            urlTopNewsMenu[i] = urlBase + urlTypeMenuSlide[i] + urlSortBy + urlAPIKey[i] + urlLanguage + urlDate;
            urlDefaultNewsSettings = urlTopNewsSettings;
            urlDefaultNewsMenu = urlTopNewsMenu;
            Log.i(TAG, "setUpdateUrlApi() --> urlDefaultNewsSettings[" + i + "] = " + urlDefaultNewsSettings[i]);//Se puede borrar
            Log.i(TAG, "setUpdateUrlApi() --> urlDefaultNewsMenu[" + i + "] = " + urlDefaultNewsMenu[i]);//Se puede borrar
        }
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 19/05/2020</b></he></p>
     * <br>
     *     Método que obtiene el ancho de pantalla.
     * @return Ancho de pantalla en tipo int
     */
    private int getWidthCurrentScreen() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        return point.x;
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 19/05/2020</b></he></p>
     * <br>
     *     Método que obtiene el alto de pantalla.
     * @return Alto de pantalla en tipo int
     */
    private int getHeightCurrentScreen() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        return point.y;
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 20/05/2020</b></h2></p>
     * <br>
     *     Método que coloca el fragment inicial que es HOME al iniciar la aplicación.
     */
    private void setStartFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.contendor, homeFragment, "HomeFragment");
        fragmentTransaction.commit();
        isActiveHome = true;
        Log.w(TAG, "onNavigationItemSelected() --> getFragments = " + fragmentManager.getFragments());
        setAnimationButtonCross(false);
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 20/05/2020</b></h2></p>
     * <br>
     *     Método que responde a la notificación desde Recover solo si el usuario modifico alguna de sus opciones en Setting.
     */
    public void setNotifyChangeSettings() {
        Log.i(TAG, "setNotifyChangeSettings()");
        isChangeSetting = true;
        Log.i(TAG, "setNotifyChangeSettings() --> isChangeSetting = " + isChangeSetting);
    }

    /**
     * <p><h2><b>Created by Alejandro Jimenez on 26/05/2020</b></h2></p>
     * <br>
     *     Método que monitorea el estado del internet para realizar acciones pendientes.
     *     En acciones pendientes. Si existe algun cambio en Settings y no hay internet,
     *     cuando tenga internet el dispositivo, cargará las noticias modificadas en Settings.
     *     Si no hay ninguna noticia en allNews y no hay internet, cuando tenga internet,
     *     realiza la petición y muestra de tarjeas de noticias.
     * @param isNetworkAvailable Boolean que muestra el estado del internet, true = Hay internet | false = No hay internet.
     */
    public void setNotifyNetworkAvailable(boolean isNetworkAvailable) {
        Log.i(TAG, "setNotifyNetworkAvailable()");
        isChangeNetworkAvailable = isNetworkAvailable;
        currentStatusNetwork = isChangeNetworkAvailable;
        Log.i(TAG, "setNotifyNetworkAvailable() --> isChangeNetworkAvailable = " + isChangeNetworkAvailable);
        if (isNetworkAvailable) {
            if (allNews != null) {
                if (getEnableOptionsSetting() == 0) {
                    Log.e(TAG, "setNotifyNetworkAvailable() --> No hay opciones activas en Setting");
                } else {
                    if (isChangeSetting == true) {
                        isChangeSetting = false;
                        Log.i(TAG, "setNotifyNetworkAvaible() --> allNews.size = " + allNews.size());
                        allNews.clear();
                        getRequestApi(urlTopNewsSettings, 0);
                    } else {
                        if (allNews.size() == 0) {
                            getRequestApi(urlTopNewsSettings, 0);
                        } else {
                            Log.i(TAG, "setNotifyNetworkAvailable() --> Ya hay noticias");
                        }
                    }
                }
            } else {
                Log.e(TAG, "setNotifyNetworkAvailable() --> El Array List allNews no esta instanciado");
            }
        } else {
            Log.e(TAG, "setNotifyNetworkAvailable() --> No hay internet");
        }
    }

    /**
     * <p><h2><b>Created by Alejandro Jimenez on 26/05/2020</b></h2></p>
     * <br>
     *     Método que monitorea el estado de visibilidad de About Us.
     * @param isActiveAboutUs Booleano que notifica el estado, true = Visible | false = No visible.
     */
    public void setNotifyIsActiveAboutUs(boolean isActiveAboutUs) {
        Log.i(TAG, "setNotifyIsActiveAboutUs()");
        this.isActiveAboutUs = isActiveAboutUs;
        Log.i(TAG, "setNotifyIsActiveAboutUs() --> isActiveAboutUs = " + isActiveAboutUs);
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 20/05/2020</b></h2></p>
     * <br>
     *     Método que busca respuesta en la base de datos para saber si existen o no, noticias en el Recover.
     * @return True = existen noticias para mostrar en recover || false = no existen noticias para mostrar en recover.
     */
    private boolean isNewsRecover() {
        conn = new ConexionSQLiteHelper(this, "db_noticias", null, 1);
        SQLiteDatabase sqLiteDatabase = conn.getReadableDatabase();
        String parameter = "%"; //Comodin en base de datos
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Utilidades.TABLA_RECUPERAR + " WHERE " + Utilidades.CATEGORIA + " LIKE '" + parameter + "';", null);
        if (cursor.getCount() <= 0) {
            Log.v(TAG, "No hay noticias en Recover");
            cursor.close();
            return false;
        } else {
            Log.v(TAG, "Numero de noticias en Recover = " + cursor.getCount());
            cursor.close();
            return true;
        }
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 21/05/2020</b></h2></p>
     * <br>
     *     Método que busca respuesta en la base de datos para saber si existen o no, noticias en Favorite.
     * @return True = existen noticias para mostrar en favorite || false = no existen noticias para mostrar en recover.
     */
    private boolean isNewsFavorite() {
        ConexionSQLiteHelper c = new ConexionSQLiteHelper(this, "db_noticias", null, 1);
        SQLiteDatabase database = c.getReadableDatabase();
        String parameter = "%";
        Cursor cursor = database.rawQuery("SELECT * FROM '" + Utilidades.TABLA_NOTICIA + "' WHERE '" + Utilidades.CATEGORIA + "' LIKE '" + parameter + "';", null);
        if (cursor.getCount() == 0) {
            Log.i(TAG, "isNewFavorite() --> No hay noticias en Favorite");
            return false;
        } else {
            Log.i(TAG, "isNewFavorites() --> Noticias en Favorite = " + cursor.getCount());
            return true;
        }
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 21/05/2020</b></h2></p>
     * <br>
     *     Método que cuenta cuantas opciones activas hay en Setting.
     * @return int - Total de opciones activas en Settings
     */
    public int getEnableOptionsSetting() {
        Log.i(TAG, "getEnableOptionsSetting()");
        int optionsEnableSetting = 0;
        for (int i = 0; i < 9; i++) {
            if (stateArrayMain[i]) {
                optionsEnableSetting += 1;
            }
        }
        Log.i(TAG, "getEnableOptionsSetting() --> Opciones activadas en Setting = " + optionsEnableSetting);
        return  optionsEnableSetting;
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 20/05/2020</b></h2></p>
     * <br>
     *     Método que controla la visibilidad del imageButtonCross en cada uno de los fragments. Tambien coloca la animacion Fade In cuando aparece.
     * @param isShowButton variable de tipo boolean, true = visible || false = invisible.
     */
    private void setAnimationButtonCross(boolean isShowButton) {
        if (isShowButton) {
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(800);
            imgBtnCross.setAnimation(fadeIn);
            imgBtnCross.setVisibility(View.VISIBLE);
            imgBtnCross.setEnabled(true);
        } else {
            imgBtnCross.setVisibility(View.INVISIBLE);
            imgBtnCross.setEnabled(true);
        }
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 20/05/2020</b></h2></p>
     * <br>
     *     Método que muestra el fragment sobre el contenedor REMPLAZANDO fragment tras fragment.
     * @param tagFragment TAG que identifica que fragment se tiene que cargar.
     */
    private void loadFragment(String tagFragment) {
        switch (tagFragment) {
            case "HomeFragment":
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //Elimina lo que hay en pila
                imgBtnCross.setVisibility(View.INVISIBLE);
                currentStatusNetwork = isNetworkStatusAvailable(MainActivity.this);
                Log.i(TAG, "loadFragment() --> isChangeSetting = " + isChangeSetting + " | currentStatusNetwork = " + currentStatusNetwork);
                if (isChangeSetting && currentStatusNetwork) {
                    isChangeSetting = false;
                    Log.i(TAG, "loadFragment() --> allNews.size = " + allNews.size());
                    allNews.clear();
                    getRequestApi(urlTopNewsSettings, 0);
                }
                break;
            case "FavoriteFragment":
                //Muestra el fragment
                fragmentTransaction = fragmentManager.beginTransaction();
                favoriteFragment = new FavFragment();
                fragmentTransaction.replace(R.id.contendor, favoriteFragment, tagFragment);
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack(null);
                Log.i(TAG, "loadFragment() --> Fragment en pila = " + fragmentManager.getFragments().size());

                //Visibilidad del boton de tache
                if (isNewsFavorite()) {
                    setAnimationButtonCross(false);
                } else {
                    setAnimationButtonCross(true);
                }

                //Estado de activo About Us
                if (isActiveAboutUs) {
                    isActiveAboutUs = false;
                    scrollMenuPosition.setVisibility(View.VISIBLE);
                }

                //Estado del menu en Home
                if (estadoDrawer) {
                    scrollMenuPosition.setVisibility(View.VISIBLE);
                }
                break;
            case "RecoverFragment":
                //Muestra el fragment
                fragmentTransaction = fragmentManager.beginTransaction();
                recoverFragment = new RecoverFragment();
                fragmentTransaction.replace(R.id.contendor, recoverFragment, tagFragment);
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack(null);
                Log.i(TAG, "loadFragment() --> Fragment en pila = " + fragmentManager.getFragments().size());

                //Visibilidad del boton de tache
                if (isNewsRecover()) {
                    setAnimationButtonCross(false);
                } else {
                    setAnimationButtonCross(true);
                }

                //Estado de activo About Us
                if (isActiveAboutUs) {
                    isActiveAboutUs = false;
                    scrollMenuPosition.setVisibility(View.VISIBLE);
                }

                //Estado del menu en Home
                if (estadoDrawer) {
                    scrollMenuPosition.setVisibility(View.VISIBLE);
                }
                break;
            case "SettingFragment":
                //Muestra el fragment
                fragmentTransaction = fragmentManager.beginTransaction();
                settingFragment = new ConfigFragment();
                fragmentTransaction.replace(R.id.contendor, settingFragment, tagFragment);
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack(null);
                Log.i(TAG, "loadFragment() --> Fragment en pila = " + fragmentManager.getFragments().size());

                //Visibilidad del boton de tache
                setAnimationButtonCross(true);

                //Estado de activo About Us
                if (isActiveAboutUs) {
                    isActiveAboutUs = false;
                    scrollMenuPosition.setVisibility(View.VISIBLE);
                }

                //Estado del menu en Home
                if (estadoDrawer) {
                    scrollMenuPosition.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 20/05/2020</b></h2></p>
     * <br>
     *     Método que modifica la posicion de la barra scroll blanca, asigna la duracion de la animacion y controla cual fragment se va a presentar.
     * @param marginStart Margen inicial para reposicionar el scroll.
     * @param marginEnd Margen final para reposicionar el scroll.
     * @param indexFragment Index para saber que fragment se va a presenter.
     */
    private void setAnimationScrollMenuBottom(final int marginStart, final int marginEnd, final int indexFragment) {
        Log.i(TAG, "setAnimationScrollMenuBottom() --> marginStart = " + marginStart);
        Log.i(TAG, "setAnimationScrollMenuBottom() --> marginEnd = " + marginEnd);

        scrollMenuPosition.setLayerType(View.LAYER_TYPE_HARDWARE, null); //Realiza animacion 2D con Hardware del dispositivo

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) scrollMenuPosition.getLayoutParams();
                marginLayoutParams.setMarginStart(marginStart + (int)((marginEnd - marginStart) * interpolatedTime));
                scrollMenuPosition.setLayoutParams(marginLayoutParams);
            }
        };

        animation.setDuration(75); //Medida en ms
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                scrollMenuPosition.setLayerType(View.LAYER_TYPE_NONE, null);
                switch (indexFragment) {
                    case 1:
                        loadFragment("HomeFragment");
                        break;
                    case 2:
                        loadFragment("FavoriteFragment");
                        break;
                    case 3:
                        loadFragment("RecoverFragment");
                        break;
                    case 4:
                        loadFragment("SettingFragment");
                        break;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        scrollMenuPosition.startAnimation(animation);
    }

    /**
     * Método que verifica al cargar la vista la conexión a internet.
     * @param context Contexto de la clase.
     * @return true: si hay internet || false: no hay internet.
     */
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

    //endregion

    //region POR LIMPIAR PARA TERMINAR LA NUEVA IMPLEMENTACION

    public static long getRunningTime(){
        return SystemClock.elapsedRealtime() - startTime;
    }

    //endregion

    //region NOTICIAS

    private void getRequestApi(final String[] urlAPI, final int indexRecursive) {
        Log.i(TAG, "getRequestApi()");
        AsyncHttpClient httpClient = requestHttpClient();
        RequestParams requestParams = new RequestParams();
        if ((indexRecursive + 1) <= stateArrayMain.length) {
            if (stateArrayMain[indexRecursive] == true) {
                Log.i(TAG, "getRequestApi() --> stateArrayMain[" + indexRecursive + "] = " + stateArrayMain[indexRecursive]);
                Log.w(TAG, "getRequestApi() --> urlAPI[" + indexRecursive + "] = " + urlAPI[indexRecursive]);
                RequestHandle requestHandle = httpClient.get(urlAPI[indexRecursive], requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject response = new JSONObject(new String(responseBody));
                            Log.i(TAG, "getRequestApi() -- onSuccess() --> indexRecursive = " + indexRecursive);
                            Log.w(TAG, "getRequestApi() -- onSuccess() --> Resultado de peticion desde API = " + response.getInt("totalResults"));
                            Log.i(TAG, "getRequestApi() -- onSuccess() --> Categoria = " + categoria[indexRecursive]);

                            if (response.getInt("totalResults") == 0) {
                                getRequestApi(urlAPI, indexRecursive + 1);
                            } else {
                                allNews = addMoreNews(new String(responseBody), labelsForNewsSettings[indexRecursive], allNews);
                                if (allNews.size() == 0 ){
                                    getRequestApi(urlAPI, indexRecursive + 1);
                                } else {
                                    if (activityListener != null) {
                                        activityListener.setGeneralNews();
                                    } else {
                                        generalRecursive(indexRecursive + 1, false, true);
                                    }
                                }
                            }
                        } catch (JSONException ex) {
                            Log.e(TAG, "recursividad() --> Error en response: " + ex.getLocalizedMessage());
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e(TAG, "getRequestApi() -- onFailure() --> statusCode = " + statusCode + " -- Error = " + error);
                        if (statusCode == 0) {
                            if (activityListener != null) {
                                activityListener.msjWeakSignal();
                            }
                        }
                        if (responseBody != null) {
                            if (responseBody.length != 0) {
                                try {
                                    JSONObject response = new JSONObject(new String(responseBody));
                                    Log.i(TAG, "getRequestApi() -- onFailure() --> names: " + response.names());
                                    Log.i(TAG, "getRequestApi() -- onFailure() --> toString: " + response.toString());
                                } catch (JSONException ex) {
                                    Log.e(TAG, "getRequestApi() -- onFailure() --> Error en solicitud a la API: " + ex.getLocalizedMessage());
                                }
                            }
                        }
                    }
                });
                getRequestApi(urlAPI, (indexRecursive + 1));
            } else {
                getRequestApi(urlAPI, (indexRecursive + 1));
            }
        } else {
            Log.i(TAG, "getRequestApi() --> Evento terminado");
        }
    }

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
                    if (SWIPESTACK_SCROLLING) {
                        if (HomeFragment.Swipadaptador.getCount() == 0) {
                            if (null != activityListener) {
                                activityListener.setGeneralNews();
                            }
                        }
                    }else{
                        if (HomeFragment.SwipadaptadorNScroll.getCount() == 0) {
                            if (null != activityListener) {
                                activityListener.setGeneralNews();
                            }
                        }
                    }
                    Log.d(TAG,"generalRecursive  -- FinishALL allNews.size:"+allNews.size());
                }else
                    generalRecursive(index + 1, defNews, topNews);
            }
        }else
            generalRecursive(0,true,false);
    }

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
                if (statusCode == 0){
                    //Toast.makeText(MainActivity.this, "Weak Signal", Toast.LENGTH_SHORT).show();
                    if(activityListener != null){
                        activityListener.msjWeakSignal();
                    }
                }
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
                        if (statusCode == 0){
                            //Toast.makeText(MainActivity.this, "Weak Signal", Toast.LENGTH_SHORT).show();
                            if(activityListener != null){
                                activityListener.msjWeakSignal();
                            }
                        }


                    }

                    @Override
                    public void onFinish(){
                        Log.d(TAG,"specificRecursive -- onFinish, ALLNewsMenu.size:" +allNewsMenu.size());
                        //Make the change for homeFrag
                        if(allNewsMenu.size() != 0) {
                            if (null != activityListener) {
                                Log.d(TAG, "generalRecursive -- onFinish, allNews.size(after) onFinish:" + allNews.size());

                                activityListener.setSpeficicNewsFromMenu(index);
                            }
                        }
                    }
                });
            }
        });
    }

    public AsyncHttpClient requestHttpClient() {
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(10000); //Establezca el límite de tiempo de espera de conexión (milisegundos).
        client.setResponseTimeout(10000); //Establezca el límite de tiempo de espera de respuesta (milisegundos).
        client.setMaxRetriesAndTimeout(2, 10000); //Establece el número máximo de reintentos y el tiempo de espera para una solicitud particular.
        return client;
    }

    public ArrayList<Noticia> addMoreNews(String response, String categoria, ArrayList<Noticia> list) {
        ArrayList<Noticia> mHelperList = new ArrayList<>();
        if (allNewsDeleted != null) {
            mHelperList.addAll(allNewsDeleted);
        } else {
            mHelperList = null;
        }
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

    //endregion

    //region LISTENERS

    //Lister del Menu Bottom
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home_nav:
                Log.i(TAG, "onNavigationItemSelected() --> Selected: Home || isActiveHome = " + isActiveHome);
                if (getEnableOptionsSetting() == 0) {
                    showAlertSetting();
                } else {
                    if (!isActiveHome) {
                        //Cambia el estado de las banderas
                        isActiveHome = true;
                        isActiveFavorites = false;
                        isActiveRecover = false;
                        isActiveSettings = false;

                        //Firebase Analytics
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Home");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Main Menu");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        //Animacion de scroll
                        setAnimationScrollMenuBottom(marginScrollMenuBottom, 0, 1);
                        marginScrollMenuBottom = 0;
                        menuNavigation.getMenu().getItem(0).setCheckable(true);
                    } else {
                        if (isActiveAboutUs) {
                            Log.i(TAG, "onNavigationItemSelected() --> About Us esta activo");
                            AboutFragment.imageButtonRegresar.performClick();
                        } else {
                            Log.i(TAG, "onNavigationItemSelected() --> About Us no esta activo");
                            menuNavigation.getMenu().getItem(0).setCheckable(true);
                            if (estadoDrawer) {
                                HomeFragment.drawerLayout.closeDrawer(GravityCompat.END);
                            }
                        }
                    }
                }
                break;
            case R.id.fav_nav:
                Log.i(TAG, "onNavigationItemSelected() --> Selected: Favorites");
                if (getEnableOptionsSetting() == 0) {
                    showAlertSetting();
                } else {
                    if (!isActiveFavorites) {
                        //Cambia el estado de las banderas
                        isActiveHome = false;
                        isActiveFavorites = true;
                        isActiveRecover = false;
                        isActiveSettings = false;

                        //Firebase Analytics
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Favorites");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Main Menu");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        //Animacion de scroll
                        setAnimationScrollMenuBottom(marginScrollMenuBottom, sizeWidth / 4, 2);
                        marginScrollMenuBottom = sizeWidth / 4;
                    }
                }
                break;
            case R.id.recover_nav:
                Log.i(TAG, "onNavigationItemSelected() --> Selected: Recover");
                if (getEnableOptionsSetting() == 0) {
                    showAlertSetting();
                } else {
                    if (!isActiveRecover) {
                        //Cambia el estado de las banderas
                        isActiveHome = false;
                        isActiveFavorites = false;
                        isActiveRecover = true;
                        isActiveSettings = false;

                        //Animacion de scroll
                        setAnimationScrollMenuBottom(marginScrollMenuBottom, (sizeWidth / 4) * 2, 3);
                        marginScrollMenuBottom = (sizeWidth / 4) * 2;
                    }
                }
                break;
            case R.id.config_nav:
                Log.i(TAG, "onNavigationItemSelected() --> Selected: Settings");
                    if (!isActiveSettings) {
                        //Cambia el estado de las banderas
                        isActiveHome = false;
                        isActiveFavorites = false;
                        isActiveRecover = false;
                        isActiveSettings = true;

                        //Animacion de scroll
                        setAnimationScrollMenuBottom(marginScrollMenuBottom, (sizeWidth / 4) * 3, 4);
                        marginScrollMenuBottom = (sizeWidth / 4) * 3;
                    }
                break;
        }
        return true;
    }

    //endregion

    //region SYSTEM METHODS

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed()");
        if (estadoDrawer) {
            HomeFragment.drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            Fragment currentFragment = fragmentManager.findFragmentByTag("HomeFragment");
            if (currentFragment != null) {
                if (currentFragment.isVisible()) {
                    super.onBackPressed();
                } else {
                    moveTaskToBack(true);
                }
            }
        }
    }

    //endregion

    //region OTHERS

    /**
     * <p><h2><b>Created by Alejandro Jimenez on 28/05/2020</b></h2></p>
     * <br>
     *     Método que vacia la tabla de la base de datos.
     * @param table String que recibe el nombre de la tabla que se quiere vaciar.
     */
    public void clearTable(String table) {
        ConexionSQLiteHelper c = new ConexionSQLiteHelper(this, "db_noticias", null, 1);
        SQLiteDatabase database = c.getWritableDatabase();
        int affectedRows = 0;
        if (table.contentEquals("Noticias")) {
            affectedRows = database.delete(Utilidades.TABLA_NOTICIA, "1", null);
            Log.i(TAG, "clearTable() --> Filas afectadas en tabla noticias = " + affectedRows);
        } else if (table.contentEquals("Recuperar")) {
            affectedRows = database.delete(Utilidades.TABLA_RECUPERAR, "1", null);
            Log.i(TAG, "clearTable() --> Filas afectadas en tabla recuperar = " + affectedRows);
        } else if (table.contentEquals("Preferencias")) {
            affectedRows = database.delete(Utilidades.TABLA_PREFERENCIA, "1", null);
            Log.i(TAG, "clearTable() --> Filas afectadas en tabla preferencias = " + affectedRows);
        }
    }

    //endregion

}