package com.amco.cidnews.Fragments;


import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amco.cidnews.Activities.MainActivity;
import com.amco.cidnews.Adapters.NoticiasAdapterRecover;
import com.amco.cidnews.R;
import com.amco.cidnews.Utilities.ConexionSQLiteHelper;
import com.amco.cidnews.Utilities.ListenRecoverFAB;
import com.amco.cidnews.Utilities.Noticia;
import com.amco.cidnews.Utilities.Utilidades;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/*
*
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
* */

public class RecoverFragment extends Fragment implements ListenRecoverFAB {
    public static final String TAG = "RecoverFragment";

    //Views
    ImageButton btnGoingHome, btnFilter;
    Button frnonoticia;
    TextView tx;
    FrameLayout frameToolbar;
    private ListView listRecoverView;
    FloatingActionButton bottonRecover;
    PopupWindow popupWindowDogs,popupWindowDogsHelper;

    //Adapter
    NoticiasAdapterRecover nA;


    //List
    private ArrayList<Noticia> ListaNoticias;
    LinkedList<String> ListaAux;

    String urlNoticia;
    private int indexForSave = 0;
    String popUpContents[];
    ImageButton btn_menu;

    //Vars
    float scale=0;
    int dps,pxX,pxY;

    //Animation
    Animation fadeIn;

    ConexionSQLiteHelper conn;




    @Nullable
    @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frame_recover, container, false);

        configUI(view);

        configUIListeners();

        consultarNoticiasFavoritas("%",0);  /// bandera2=1 puede ver la noticia

        crearMenu(view);

        return view;
    }

    public void configUI(View view){
        listRecoverView =  view.findViewById(R.id.container_recover);
        frameToolbar =  view.findViewById(R.id.toolbar_recover);
        btnGoingHome = view.findViewById(R.id.btn_goto_home);
        tx = view.findViewById(R.id.title_recover);
        btnFilter = view.findViewById(R.id.btn_filter);
        frnonoticia = view.findViewById(R.id.warning_title);
        bottonRecover = view.findViewById(R.id.botonfab_recover);
        setupUI();
    }

    public void setupUI(){

        ((MainActivity)getActivity()).imgBtnCross.setVisibility(View.INVISIBLE);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        pxX = size.x;
        pxY = size.y;

        scale =  getResources().getDisplayMetrics().density;
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);
        bottonRecover.hide();

    }

    public void configUIListeners(){
        frnonoticia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RecoverFragment", "Frame noticia: onClick: ");
                //((MainActivity)getActivity()).back.performClick();
            }
        });


        bottonRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RecoverFragment:", "Botton Recoveron onClick: ");
                bottonRecover.setImageResource(R.drawable.ic_check_white);
                bottonRecover.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.verde)));
                bottonRecover.setAnimation(fadeIn);
                fadeIn.start();
                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        bottonRecover.hide();
                        try {
                            ListaAux=  nA.getSelectedCountry();
                        }catch (Exception e){
                            String aux= String.valueOf(e);
                            Log.d("FAB",aux);
                        }
                        saveNews();
                        consultarNoticiasFavoritas("%", 0);
                        if(ListaAux.size()==0)
                            bottonRecover.setAlpha(0.f);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        bottonRecover.setImageResource(R.drawable.ic_recover_white);
                        bottonRecover.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.undoSnackbar)));
                        //bottonRecover.show();
                        tx.setText("REMOVED");
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RecoverFragment", "onClick: btnFilter");
            }
        });

        btnGoingHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if((MainActivity) getActivity() != null)
                        ((MainActivity) getActivity()).imgBtnCross.performClick();
            }
        });

    }

    public void saveNews(){
        String aux1;
        String aux;
        boolean Banderilla=true;
        for(int i=0; i<ListaNoticias.size();i++) {
            indexForSave = i;
            aux = String.valueOf(i);
            Log.d("TAG1", "remueve: " + aux);
            urlNoticia = ListaNoticias.get(i).getUrl();
            if(ListaAux.size()>0) {
                for (int j = 0; j < ListaAux.size(); j++) {
                    aux1 = ListaAux.get(j);
                    if (urlNoticia.equals(aux1)) {
                        //Banderilla = true;
                        Banderilla = false;

                        j = ListaAux.size();
                    } else {
                        //Banderilla = false;
                        Banderilla = true;

                    }
                }
                if (!Banderilla) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int indexRun = indexForSave;
                            Log.e("FavFrgment", "Indexrun: "+String.valueOf(indexRun) );
                            recoveringNews(ListaNoticias.get(indexRun).getTitulo()
                                    ,ListaNoticias.get(indexRun).getImagen()
                                    ,ListaNoticias.get(indexRun).getUrl()
                                    ,ListaNoticias.get(indexRun).getAutor()
                                    ,ListaNoticias.get(indexRun).getCategoria());
                        }
                    }).start();
                    deleteSelectedOrAll(urlNoticia);
                    Banderilla = false;
                }
            }else {
                //registrarNoticiasRecuperarTodo(ListaNoticias);
                //deleteSelectedOrAll("all");
            }
        }
    }


    private void crearMenu (View view) {
        List<String> dogsList = new ArrayList<String>();
        dogsList.add("HEALTH");
        dogsList.add("CONSTRUCTION");
        dogsList.add("RETAIL");
        dogsList.add("EDUCATION");
        dogsList.add("ENTERTAINMENT");
        dogsList.add("ENVIRONMENT");
        dogsList.add("FINANCE");
        dogsList.add("ENERGY");
        dogsList.add("TELECOM");
        dogsList.add("RESET ALL");



        popUpContents = new String[dogsList.size()];
        dogsList.toArray(popUpContents);
        popupWindowDogs = popupWindowDogs();


        dogsList.remove(dogsList.size()-1);
        popUpContents = new String[dogsList.size()];
        dogsList.toArray(popUpContents);
        popupWindowDogsHelper = popupWindowDogs();

        btn_menu = (ImageButton) view.findViewById(R.id.btn_filter);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popupWindowDogs.setAnimationStyle(R.style.popupAnim);   //*********************************// IMPORTNTE
                if (tx.getText().toString().equalsIgnoreCase("REMOVED"))
                    popupWindowDogsHelper.showAsDropDown(view, -5, Math.round(0), 1);
                else
                    popupWindowDogs.showAsDropDown(view, -5, Math.round(0), 1);
            }
        });
    }


    public PopupWindow popupWindowDogs() {
        MainActivity mainActivity = (MainActivity) getActivity();
        final PopupWindow popupWindow = new PopupWindow(mainActivity);
        ListView listView = new ListView(mainActivity);
        listView.setAdapter(dogsAdapter(popUpContents));
        popupWindow.setFocusable(true);
        popupWindow.setWidth((Math.round(pxX-130*scale)));
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.salud));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listView);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {


                String option="";
                Animation animation1 = new AlphaAnimation(0.3f,1);
                animation1.setDuration(600);
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

                switch (position)
                {
                    case 0 : option="salud";   consultarNoticiasFavoritas(option,0); break;
                    case 1 : option="construcción";   consultarNoticiasFavoritas(option,0); break;
                    case 2 : option="retail";   consultarNoticiasFavoritas(option,0); break;
                    case 3 : option="educación";   consultarNoticiasFavoritas(option,0); break;
                    case 4 : option="entretenimiento";   consultarNoticiasFavoritas(option,0); break;
                    case 5 : option="ambiente";   consultarNoticiasFavoritas(option,0); break;
                    case 6 : option="banca";   consultarNoticiasFavoritas(option,0); break;
                    case 7 : option="energía";   consultarNoticiasFavoritas(option,0); break;
                    case 8 : option="telecom";   consultarNoticiasFavoritas(option,0); break;
                    case 9 :
                        if (!tx.getText().toString().equalsIgnoreCase("REMOVED")){
                            consultarNoticiasFavoritas("%", 0);
                            tx.setText("REMOVED");
                        }
                        break;
                }
                popupWindow.dismiss();


                String cat = getOptionaltitle(option);
                tx.setText(cat);
            }

        });
        return popupWindow;
    }



    private ArrayAdapter<String> dogsAdapter(String dogsArray[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dogsArray) {
            String id;
            int size;
            String color;

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // setting the ID and text for every items in the list
                String item = getItem(position);

                TextView listItem = new TextView(getActivity());
                listItem.setLetterSpacing((float) 0.05*scale);

                int espacio1=(int) Math.round(((pxY - 72*scale)/12));
                int porcentaje = (int) Math.round(((espacio1)/3)/getResources().getDisplayMetrics().scaledDensity);

                switch (item){
                    case "HEALTH":
                        id = "menu_sup_salud";
                        size = porcentaje;
                        color = "#235784";
                        break;
                    case "CONSTRUCTION":
                        id = "menu_sup_cons";
                        size = porcentaje;
                        color = "#235784";
                        break;

                    case "RETAIL":
                        id = "menu_sup_ret";
                        size = porcentaje;
                        color = "#235784";
                        break;

                    case "EDUCATION":
                        id = "menu_sup_edu";
                        size = porcentaje;
                        color = "#235784";
                        break;
                    case "ENTERTAINMENT":
                        id = "menu_sup_ent";
                        size = porcentaje;
                        color = "#235784";
                        break;
                    case "ENVIRONMENT":
                        id = "menu_sup_amb";
                        size = porcentaje;
                        color = "#235784";
                        break;

                    case "FINANCE":
                        id = "menu_sup_ban";
                        size = porcentaje;
                        color = "#235784";
                        break;

                    case "ENERGY":
                        id = "menu_sup_ene";
                        size = porcentaje;
                        color = "#235784";
                        break;
                    case "TELECOM":
                        id = "menu_sup_tel";
                        size = porcentaje;
                        color = "#235784";
                        break;

                    case "RESET ALL":
                        id = "menu_sup_resetall";
                        size = porcentaje;
                        color = "#235784";
                        break;
                }

                if (id.contentEquals("menu_sup_resetall")){
                    SpannableString spannablecontent = new SpannableString(getResources().getString(R.string.menu_reset_all));
                    spannablecontent.setSpan(new StyleSpan(Typeface.BOLD), 0,spannablecontent.length(), 0);
                    listItem.setText(spannablecontent);
                }else
                    listItem.setText(item);

                listItem.setTag(id);
                listItem.setTextSize(size);
                listItem.setPadding(Math.round(15*scale), Math.round(15*scale), Math.round(15*scale), Math.round(15*scale));
                listItem.setTextColor(Color.WHITE);
                listItem.setBackgroundColor(Color.parseColor(color));


                return listItem;
            }
        };

        return adapter;
    }

    private void deleteSelectedOrAll(String who)
    {
        String tipo = who;
        conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getReadableDatabase();
        if(tipo != "all")
        {
            String [] parametros = {who.toString()};
            db.delete(Utilidades.TABLA_RECUPERAR,Utilidades.URL+"=?",parametros);
        }
        else
        {
            db.delete(Utilidades.TABLA_RECUPERAR,null,null);
            consultarNoticiasFavoritas("%",0);
        }
        db.close();

    }


    public void recoveringNews(String titulo, String imagen, String url, String autor, String categoria) {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getWritableDatabase();
        String [] parametros = {url};
        Cursor cursor = db.rawQuery( "SELECT url FROM "+Utilidades.TABLA_NOTICIA+" WHERE "+Utilidades.URL+" =?", parametros);
        Log.d("recoveringNews dice: ", "recovering News... Recuperando:");
        if(cursor.getCount()==0)
        {
            Log.d(" NOTICIA:", "NO HAY");
            ContentValues valores = new ContentValues();
            valores.put(Utilidades.TITULO,titulo);
            valores.put(Utilidades.IMAGEN,imagen);
            valores.put(Utilidades.URL,url);
            valores.put(Utilidades.AUTOR,autor);
            valores.put(Utilidades.CATEGORIA,categoria);
            db.insert(Utilidades.TABLA_NOTICIA,null,valores);
            db.close();
        }else{
            Log.d("NOTICIA:", "REPETIDA NOTICIA -> FAV"+String.valueOf(titulo));
            db.close();
        }
        conn.close();
    }




    private void consultarNoticiasFavoritas(String categoria,int bandera) {

        conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getReadableDatabase();
        String [] parametros = {categoria.toString()};
        Cursor cursor = db.rawQuery("SELECT * FROM "+Utilidades.TABLA_RECUPERAR+" WHERE "+Utilidades.CATEGORIA+" LIKE ?",parametros);
        Noticia noticia = null;
        //ListaNoticias es el Array de la tabla (Que sera Recover o noticias DELETED )
        ListaNoticias = new ArrayList<Noticia>();

        if(cursor.getCount()==0)
        {
            Log.e("RecoverFragment", "consultarNoticiasFavoritas: EMPTY");
        }
        else
        {
            Log.e("RecoverFragment", "consultarNoticiasFavoritas: NEWS!");
            while (cursor.moveToNext()) {
                if((cursor.getLong(5) - System.currentTimeMillis()) < 0){
                    deleteSelectedOrAll(cursor.getString(2));
                }else {
                    noticia = new Noticia(cursor.getString(0), cursor.getString(1), cursor.getString(2), "", cursor.getString(3), cursor.getString(4), cursor.getLong(5));
                    ListaNoticias.add(noticia);
                }
            }
            Collection<Noticia> linkedHashSet = new LinkedHashSet<Noticia>(ListaNoticias);
            linkedHashSet.addAll(ListaNoticias);
            ListaNoticias.clear();
            ListaNoticias.addAll(linkedHashSet);
        }
        nA = new NoticiasAdapterRecover(this,getActivity(),ListaNoticias,bandera);
        listRecoverView.setAdapter(nA);
        cursor.close();
        db.close(); /////***********DATABASE NEW CLOSE 13SEP
        conn.close();   ////////*****
    }


    public String getOptionaltitle(String cat){
        String category;
        switch(cat){
            case "salud":
                category = "HEALTH";
                break;
            case "construcción":
                category = "CONSTRUCTION";
                break;
            case "retail":
                category = "RETAIL";
                break;
            case "educación":
                category = "EDUCATION";
                break;
            case "entretenimiento":
                category = "ENTERTAINMENT";
                break;
            case "ambiente":
                category = "ENVIRONMENT";
                break;
            case "banca":
                category = "FINANCE";
                break;
            case "energía":
                category = "ENERGY";
                break;
            case "telecom":
                category = "TELECOM";
                break;
            default:
                category = "REMOVED";
                break;
        }
        return category;
    }

    @Override
    public void showingRecoverFAB(boolean shouldShow) {
        Log.d(TAG,"showingRecoverFAB -- shouldShow:"+shouldShow);
        if(shouldShow)
            bottonRecover.show();
        else
            bottonRecover.hide();
     }
}


//*********************************** TRASH *******************************************************/
/*
*   public void registrarNoticiasRecuperarTodo(ArrayList<Noticia> mListNews) {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(), "db_noticias", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor;


        for (int i = 0; i < ListaNoticias.size(); i++) {
            String url = mListNews.get(i).getUrl();
            String[] parametros = {url};
            cursor = db.rawQuery("SELECT url FROM " + Utilidades.TABLA_NOTICIA + " WHERE " + Utilidades.URL + " =?", parametros);
            if (cursor.getCount() == 0) {
                Log.d(" Registro Noticias Recuperar:", "");
                Log.d(" NOTICIA:", "NO HAY");
                ContentValues valores = new ContentValues();
                valores.put(Utilidades.TITULO, mListNews.get(i).getTitulo());
                valores.put(Utilidades.IMAGEN, mListNews.get(i).getImagen());
                valores.put(Utilidades.URL, url);
                valores.put(Utilidades.AUTOR, mListNews.get(i).getAutor());
                valores.put(Utilidades.CATEGORIA, mListNews.get(i).getCategoria());
                valores.put(Utilidades.TIEMPO, 0);  //Utilidades.Tiempo (long) = 0, No hay uso para el tiempo en Favoritos.
                db.insert(Utilidades.TABLA_NOTICIA, null, valores);
            } else {
                Log.d("NOTICIA:", "REPETIDA RECUPERAR" + String.valueOf(mListNews.get(i).getTitulo()));
            }

            if(i==ListaNoticias.size()){
                cursor.close();
            }
        }
        db.close();
        conn.close();
    }


    public String getParamDeleteFromTxvw(String catTxvw){
        String cat;
        switch (catTxvw){
            case "HEALTH":
                cat = "salud";
                break;
            case "CONSTRUCTION":
                cat = "construcción";
                break;
            case "RETAIL":
                cat = "retail";
                break;
            case "EDUCATION":
                cat = "educación";
                break;
            case "ENTERTAINMENT":
                cat = "entretenimiento";
                break;
            case "ENVIRONMENT":
                cat = "ambiente";
                break;
            case "FINANCE":
                cat = "banca";
                break;
            case "ENERGY":
                cat = "energía";
                break;
            case "TELECOM":
                cat = "telecom";
                break;

            default: cat = "%";
                break;

        }
        return cat;
    }
* */