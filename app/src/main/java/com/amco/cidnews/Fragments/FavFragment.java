package com.amco.cidnews.Fragments;


import android.app.Activity;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
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

import com.amco.cidnews.Utilities.ConexionSQLiteHelper;
import com.amco.cidnews.Activities.MainActivity;
import com.amco.cidnews.Utilities.Noticia;
import com.amco.cidnews.Adapters.NoticiasAdapter;
import com.amco.cidnews.R;
import com.amco.cidnews.Utilities.VistaWeb;
import com.amco.cidnews.Utilities.Utilidades;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;


public class FavFragment extends Fragment {

    static private String TAG = "FavFragment";
    static public int positionFlagIcon = -1 ;

    //Views
    TextView tx;
    PopupWindow popupWindowDogs;
    ImageButton btn_atras,btn_editar,btn_menu;
    FrameLayout fr;
    Button frnonoticia;
    ListView listaView;

    //Fragment
    Fragment fragment = new VistaWeb();

    //Adapters
    NoticiasAdapter nA;


    //Lists
    ArrayList<Noticia> ListaNoticias;
    LinkedList<String> ListaAux;
    FloatingActionButton btnFABeliminar;


    //Animation
   // Animation fadeIn;

    //Vars
    String popUpContents[],urlNoticia,categorySelected,categorySelectedForSave;
    Boolean flagMenuSaved;
    int indexForRemove,dps,pxX,pxY;
    float scale=0;

    ConexionSQLiteHelper conn;
    Bundle args = new Bundle();



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState( outState);
        Log.e(TAG, "onSaveInstanceState: saving");
        if (categorySelected != null) {
            outState.putString("categorySelectedForSaveKey", categorySelectedForSave);
        }
        if (flagMenuSaved != null) {
            outState.putBoolean("flagMenuSavedKey", flagMenuSaved);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.e("FavFragment", "onSaveInstanceState: != null");
            categorySelectedForSave = savedInstanceState.getString("categorySelectedForSaveKey");
            flagMenuSaved = savedInstanceState.getBoolean("flagMenuSavedKey", false);
        }else{
            Log.e(TAG, "onSaveInstanceState: == null");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView True" );
    }

    @Nullable
    @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_fav, container, false);
        configUI(view);
        configUIListeners();

        if (flagMenuSaved!= null) {
            if (flagMenuSaved) {
                Log.e(TAG, "onCreateView: " + String.valueOf(categorySelectedForSave));
                consultarNoticiasFavoritas(categorySelectedForSave, 0, 1);  /// bandera2=1 puede ver la noticia
            }
        }else{
            consultarNoticiasFavoritas("%", 0, 1);  /// bandera2=1 puede ver la noticia
        }
        crearMenu(view);
        return view;
    }

    ///////////////////////////////////
    /// MARK:
    public void configUI(View mView){
        listaView = mView.findViewById(R.id.contenedor_favs);
        fr = mView.findViewById(R.id.toolbar_fav);
        btn_atras = mView.findViewById(R.id.btn_atras_fav);
        frnonoticia = mView.findViewById(R.id.aviso_noticia);
        tx = mView.findViewById(R.id.title_menu_fav);
        btnFABeliminar = mView.findViewById(R.id.botonfab);
        btn_menu = mView.findViewById(R.id.boton_superior_fav);
        setupUI();
    }

    ///////////////////////////////////
    /// MARK:
    public void setupUI(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        pxX = size.x;
        pxY = size.y;
        scale =  getResources().getDisplayMetrics().density;

        btnFABeliminar.hide();
        frnonoticia.setVisibility(View.INVISIBLE);
        frnonoticia.setEnabled(true);
        //fadeIn = new AlphaAnimation(0, 1);
        //fadeIn.setInterpolator(new DecelerateInterpolator());
        //fadeIn.setDuration(1000);
        //botoneliminar.setTag(botoneliminar.getVisibility());
    }

    ///////////////////////////////////
    /// MARK:
    public void configUIListeners(){
        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnFABeliminar.isShown()){

                    if (flagMenuSaved!= null) {
                        if (flagMenuSaved) {
                            Log.e(TAG, "onCreateView: " + String.valueOf(categorySelectedForSave));
                            showFilterBar();
                            consultarNoticiasFavoritas(categorySelectedForSave, 0, 1);  /// bandera2=1 puede ver la noticia
                        }
                    }else {
                        showFilterBar();
                        consultarNoticiasFavoritas("%", 0, 1);
                    }
                }else{
                    if((MainActivity) getActivity() != null)
                        ((MainActivity) getActivity()).imgBtnCross.performClick();
                }
            }
        });

        frnonoticia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG , "frnonoticia -- onClick: ");
                if((MainActivity) getActivity() != null)
                    ((MainActivity) getActivity()).imgBtnCross.performClick();
                    //((MainActivity) getActivity()).botones.getMenu().findItem(R.id.home_nav).setChecked(true);
            }
        });


        btnFABeliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ListaAux=  nA.getSelectedCountry();
                }catch (Exception e){
                    String aux= String.valueOf(e);
                    Log.d("FAB",aux);
                }
                removeNews();
            }
        });
    }

    ///////////////////////////////////
    /// MARK:
    public void showFilterBar(){
        btnFABeliminar.hide();
        btn_menu.setVisibility(View.VISIBLE);
        btn_menu.setEnabled(true);
        fr.setBackgroundColor(getResources().getColor(R.color.toolbar_fav));
        tx.setText(R.string.favorites_title_fav);
    }

    ///////////////////////////////////
    /// MARK:
    public void registrarNoticiasRecuperar(String titulo, String imagen, String url, String autor, String categoria) {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getWritableDatabase();
        String [] parametros = {url};
        Cursor cursor = db.rawQuery( "SELECT url FROM "+Utilidades.TABLA_RECUPERAR+" WHERE "+Utilidades.URL+" =?", parametros);
        if(cursor.getCount()==0)
        {
            Log.d(TAG, " Registro Noticias Recuperar:");

            Log.d(" NOTICIA:", "NO HAY");
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
            Log.d(TAG, "REPETIDA RECUPERAR"+String.valueOf(titulo));
            db.close();
        }
        conn.close();
    }

    ///////////////////////////////////
    /// MARK:
    public void showRemoveBar(){
        Log.e(TAG, "showRemoveBar: " );
        tx.setText(R.string.remove_title_fav);
        fr.setBackgroundColor(getContext().getResources().getColor(R.color.red));


        btn_menu.setVisibility(View.GONE);
        btn_menu.setEnabled(false);
    }

    ///////////////////////////////////
    /// MARK:
    public long addTime(long startTime){
        Log.e(TAG, "DATETIME: "+String.valueOf(DateFormat.getInstance().format(startTime)));
        //long halfAnHourLater = startTime + 1800000;
        Log.e(TAG, "WAIT UNTIL: "+String.valueOf(DateFormat.getInstance().format(startTime + 1800000)));
        return startTime + 3600000*24*3; //30Min  1 Min: 60'000 //1 Hr*24Hrs*Dias
    }

    ///////////////////////////////////
    /// MARK:
    public void removeNews(){
        String aux1;
        String aux;
        boolean Banderilla=true;
        for(int i=0; i<ListaNoticias.size();i++) {
            indexForRemove = i;
            aux = String.valueOf(i);
            Log.d(TAG, "remueve: " + aux);
            urlNoticia = ListaNoticias.get(i).getUrl();
            if(ListaAux.size()>0) {
                for (int j = 0; j < ListaAux.size(); j++) {
                    aux1 = ListaAux.get(j);
                    if (urlNoticia.equals(aux1)) {
                       // Banderilla = true;
                        Banderilla = false;
                        j = ListaAux.size();
                    } else {
                        // Banderilla = false;

                        Banderilla = true;
                    }
                }
                if (!Banderilla) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int indexRun = indexForRemove;
                            Log.e(TAG, "Indexrun: "+String.valueOf(indexRun) );
                            registrarNoticiasRecuperar(ListaNoticias.get(indexRun).getTitulo()
                                    ,ListaNoticias.get(indexRun).getImagen()
                                    ,ListaNoticias.get(indexRun).getUrl()
                                    ,ListaNoticias.get(indexRun).getAutor()
                                    ,ListaNoticias.get(indexRun).getCategoria());
                        }
                    }).start();
                    eliminarTodos(urlNoticia);
                    Banderilla = false;
                }
            }else {
               // registrarNoticiasRecuperarTodo(ListaNoticias);
               // eliminarTodos("all");
            }
        }
        animationFABDeleting();
    }


    public void animationFABDeleting(){
        //botoneliminar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.verde)));
        //btnFABeliminar.setAnimation(fadeIn);
       // fadeIn.start();
        btn_atras.performClick();
    }

    ///////////////////////////////////
    /// MARK:
    private void eliminarTodos(String who){
        String tipo = who;
        conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getReadableDatabase();
        if(tipo != "all")
        {
            String [] parametros = {who.toString()};
            db.delete(Utilidades.TABLA_NOTICIA,Utilidades.URL+"=?",parametros);
        }
        else {

            db.delete(Utilidades.TABLA_NOTICIA,null,null);
            consultarNoticiasFavoritas("%",0,0);

        }

        db.close();
    }

    ///////////////////////////////////
    /// MARK:
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
                category = "FAVORITES";
                break;
        }
        return category;
    }

    ///////////////////////////////////
    /// MARK:
    private void consultarNoticiasFavoritas(final String categoria, int bandera, int bandera2) {
        Log.e(TAG ,"consultarNoticias -- consultarNoticiasFavoritas: 1");

        conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getReadableDatabase();
        String [] parametros = {categoria.toString()};
        Cursor cursor = db.rawQuery("SELECT * FROM "+Utilidades.TABLA_NOTICIA+" WHERE "+Utilidades.CATEGORIA+" LIKE ?",parametros);
        Noticia noticia = null;
        ListaNoticias = new ArrayList<Noticia>();
        if(cursor.getCount()==0)
        {
            final Animation  fadeIn = new AlphaAnimation(0.3f, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(600);
            frnonoticia.setAnimation(fadeIn);
            Log.d(TAG , "PARAMETROS: consultarNoticiasFavoritas: "+parametros[0]);
            if (!parametros[0].equals("%")){
                    showFilterBar();
                    String titleHelper = getOptionaltitle(categoria);
                    tx.setText(titleHelper);

            }else {
                tx.setText(R.string.favorites_title_fav);
                frnonoticia.setVisibility(View.VISIBLE);
                btn_menu.setEnabled(false);
                btn_menu.setVisibility(View.INVISIBLE);
            }
            frnonoticia.setVisibility(View.VISIBLE);

            ((MainActivity)getActivity()).imgBtnCross.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).imgBtnCross.setEnabled(true);
        }

        else
        {
            categorySelected = categoria;

            ((MainActivity)getActivity()).imgBtnCross.setVisibility(View.INVISIBLE);
            ((MainActivity)getActivity()).imgBtnCross.setEnabled(false);
            frnonoticia.setVisibility(View.INVISIBLE);



            if (cursor.moveToLast()){
                noticia = new Noticia(cursor.getString(0),cursor.getString(1),cursor.getString(2),"",cursor.getString(3),cursor.getString(4),cursor.getLong(5));
                ListaNoticias.add(noticia);
            }

            while (cursor.moveToPrevious()) {
                noticia = new Noticia(cursor.getString(0),cursor.getString(1),cursor.getString(2),"",cursor.getString(3),cursor.getString(4),cursor.getLong(5));
                ListaNoticias.add(noticia);
            }
            Collection<Noticia> linkedHashSet = new LinkedHashSet<Noticia>(ListaNoticias);
            linkedHashSet.addAll(ListaNoticias);
            ListaNoticias.clear();
            ListaNoticias.addAll(linkedHashSet);
            if (!parametros[0].equals("%")) {
                String titleHelper = getOptionaltitle(categoria);
                tx.setText(titleHelper);
            }else{
                tx.setText(R.string.favorites_title_fav);
            }
        }

        nA = new NoticiasAdapter(getActivity(),ListaNoticias,bandera,bandera2);
        nA.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                Log.e(TAG , "nA onChanged");

                if(!btnFABeliminar.isShown()){
                    btnFABeliminar.show();


                    if (!tx.getText().toString().equalsIgnoreCase("FAVORITES")) {
                        String catToDelete = getParamDeleteFromTxvw(categoria);
                        consultarNoticiasFavoritas(categorySelected,0,0);
                    }else{
                        consultarNoticiasFavoritas("%",0,0);
                    }
                    showRemoveBar();

                }
            }
        });
        listaView.setAdapter(nA);

        listaView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "listaView -- onItemSelected: Selected:"+String.valueOf(position) );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e(TAG , "listaView -- onItemSelected: NONE" );
            }
        });


        cursor.close();
        db.close();
        conn.close();
    }

    ///////////////////////////////////
    /// MARK:
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

    ///////////////////////////////////
    /// MARK:
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

        btn_menu = (ImageButton) view.findViewById(R.id.boton_superior_fav);

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowDogs.showAsDropDown(view, -5, Math.round(0), 1);
            }
        });
    }

    ///////////////////////////////////
    /// MARK:
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
                    case 0 : option="salud";   consultarNoticiasFavoritas(option,0,1); break;
                    case 1 : option="construcción";   consultarNoticiasFavoritas(option,0,1); break;
                    case 2 : option="retail";   consultarNoticiasFavoritas(option,0,1); break;
                    case 3 : option="educación";   consultarNoticiasFavoritas(option,0,1); break;
                    case 4 : option="entretenimiento";   consultarNoticiasFavoritas(option,0,1); break;
                    case 5 : option="ambiente";   consultarNoticiasFavoritas(option,0,1); break;
                    case 6 : option="banca";   consultarNoticiasFavoritas(option,0,1); break;
                    case 7 : option="energía";   consultarNoticiasFavoritas(option,0,1); break;
                    case 8 : option="telecom";   consultarNoticiasFavoritas(option,0,1); break;
                    case 9 :
                        if (!tx.getText().toString().equalsIgnoreCase("FAVORITES")){
                            consultarNoticiasFavoritas("%", 0, 1);
                            flagMenuSaved = false;
                        }
                        break;
                }

                categorySelectedForSave = option;
                flagMenuSaved = true;

                popupWindow.dismiss();
            }

        });
        return popupWindow;
    }

    ///////////////////////////////////
    /// MARK:
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
}

/* *********************************************** TRASH ******************************************/


   /* private void mostrarDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_empty,null);
        Button bAceptar = (Button) v.findViewById(R.id.aceptar_dialog_empty);
        alert.setView(v);
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getWindow().setLayout(800, 800);
        bAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });
    }*/


    /*
    public void mostrarNoticiasView(final int position){

        args.putString("url",ListaNoticias.get(position).getUrl());

        fragment.setArguments(args);
        ((FragmentActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.contendor,fragment,null).addToBackStack(null).commit();
    }*/

        /*
    public void showListAdapter(Activity activity, int flagVisibility,int flagAvailableForOpenNews){
        Collection<Noticia> linkedHashSet = new LinkedHashSet<Noticia>(ListaNoticias);
        linkedHashSet.addAll(ListaNoticias);
        ListaNoticias.clear();
        ListaNoticias.addAll(linkedHashSet);
        nA = new NoticiasAdapter(activity,ListaNoticias,flagVisibility,flagAvailableForOpenNews); //0:false 1:true
        listaView.setAdapter(nA);
    }*/

         /*
    public void registrarNoticiasRecuperarTodo(ArrayList<Noticia> mListNews) {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(), "db_noticias", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor;

        for (int i = 0; i < ListaNoticias.size(); i++) {
            String url = mListNews.get(i).getUrl();
            String[] parametros = {url};
            cursor = db.rawQuery("SELECT url FROM " + Utilidades.TABLA_RECUPERAR + " WHERE " + Utilidades.URL + " =?", parametros);
            if (cursor.getCount() == 0) {
                Log.d(TAG ,"Registro Noticias Recuperar: ");
                Log.d(TAG, " NOTICIA: NO HAY");
                ContentValues valores = new ContentValues();
                valores.put(Utilidades.TITULO, mListNews.get(i).getTitulo());
                valores.put(Utilidades.IMAGEN, mListNews.get(i).getImagen());
                valores.put(Utilidades.URL, url);
                valores.put(Utilidades.AUTOR, mListNews.get(i).getAutor());
                valores.put(Utilidades.CATEGORIA, mListNews.get(i).getCategoria());
                valores.put(Utilidades.TIEMPO, addTime(System.currentTimeMillis()));
                db.insert(Utilidades.TABLA_RECUPERAR, null, valores);
            } else {
                Log.d(TAG, "REPETIDA RECUPERAR" + String.valueOf(mListNews.get(i).getTitulo()));
            }

            if(i==ListaNoticias.size()){
                cursor.close();
            }
        }
        db.close();
        conn.close();
    }*/