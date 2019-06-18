package com.amco.cidnews.Adapters;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amco.cidnews.Fragments.FavFragment;
import com.amco.cidnews.R;
import com.amco.cidnews.Utilities.ConexionSQLiteHelper;
import com.amco.cidnews.Utilities.Noticia;
import com.amco.cidnews.Utilities.Utilidades;
import com.amco.cidnews.Utilities.VistaWeb;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;



/*
* import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
* */

public class NoticiasAdapterRecover extends ArrayAdapter<Noticia> {
    private ArrayList<Noticia> noticias;

    //
    Context mContext;
    List<Noticia> linkedList;
    //
    static  WebView webViewMain;
    public boolean[] checkBoxStateRecover = null;
    private HashMap<Noticia, Boolean> checkedForCountry = new HashMap<>();
    private Activity activity;
    private Noticia noticia;
    private int bandera;
    private int bandera2;
    View ViewBoton;
    RelativeLayout lista;
    private ConexionSQLiteHelper conn;
    Fragment fragment = new FavFragment();
    String urlNoticia;
    static WebView webView;
    Fragment vistaWeb = new VistaWeb();
    Bundle args = new Bundle();
    ArrayList<Noticia> ListaNoticias;
    ArrayList<Noticia> ListaNoticiasRespaldo;
    ViewGroup inclusion;



    public NoticiasAdapterRecover(@NonNull Activity activity, ArrayList<Noticia> noticias, int bandera) {
        super(activity, 0, noticias);
        this.activity = activity;
        this.noticias = noticias;
        this.linkedList = noticias;
        this.mContext = activity;
        this.bandera = bandera;
        this.checkBoxStateRecover = new boolean[noticias.size()];
    }

    public static class ViewHolder {
        private ArrayList<Noticia> noticiasrespaldo;
        private RelativeLayout Lista;
        private CheckBox box;
        private ImageButton btn;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        final ViewHolder holder;
        final Noticia country = linkedList.get(position);


        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.list_recover,
                    parent,
                    false);
            holder = new ViewHolder();
            holder.noticiasrespaldo = new ArrayList<Noticia>();
            holder.box = convertView.findViewById(R.id.checkBoxRecover);
            holder.box.setVisibility(View.INVISIBLE);
            holder.Lista = (RelativeLayout) convertView.findViewById(R.id.listFatherRecover);
            holder.btn = (ImageButton) convertView.findViewById(R.id.delete_oneRecover);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        checkBoxStateRecover = new boolean[linkedList.size()];
        final ImageView avatar = (ImageView) convertView.findViewById(R.id.iv_avatarRecover);
        final TextView name = (TextView) convertView.findViewById(R.id.tv_nameRecover);
        final TextView time = (TextView) convertView.findViewById(R.id.time_end);

        TextView title = (TextView) convertView.findViewById(R.id.tv_titleRecover);
        TextView url = (TextView) convertView.findViewById(R.id.edit_urlRecover);


        if (checkBoxStateRecover != null)
            holder.box.setChecked(checkBoxStateRecover[position]);
        if (bandera == 0)
            holder.btn.setVisibility(convertView.GONE);
        if (bandera == 1)
            holder.btn.setVisibility(convertView.VISIBLE);







        final Noticia noticia = getItem(position);
        int drawableResourceId = activity.getResources().getIdentifier("ic_cidnews_avatar", "drawable", activity.getPackageName());
        if (noticia.getImagen().equalsIgnoreCase("null")   || !(noticia.getImagen().startsWith("https"))  ) {
            Glide.with(activity)
                    .load(drawableResourceId)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(avatar);
        }
        else {
            Glide.with(activity)
                    .load(noticia.getImagen())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(avatar);
        }

       /* holder.btn.setOnClickListener(new View.OnClickListener() {   ////IMPORTANTISIMO! 20Sep      !!!! El icono de basura pequeño borrra aqui
            @Override
            public void onClick(View v) {
                urlNoticia = noticias.get(position).getUrl();
                eliminarTodos(urlNoticia);
            }
        });*/


        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("NoticiasAdapterRecover", "onClick: holder.box: TRUE");
                if (((CheckBox) v).isChecked()) {
                    checkBoxStateRecover[position] = true;
                    ischecked(position, true);
                    holder.btn.setVisibility(View.INVISIBLE);
                } else {
                    checkBoxStateRecover[position] = false;
                    ischecked(position, false);
                    holder.btn.setVisibility(View.VISIBLE);
                }
            }
        });

        if (checkedForCountry.get(country) != null) {
            holder.box.setChecked(checkedForCountry.get(country));
        }

        if (holder.box.isChecked()) {
            holder.btn.setVisibility(View.VISIBLE);
        }else{
            holder.btn.setVisibility(View.GONE);

        }

        holder.Lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!holder.box.isChecked()) {
                    holder.btn.setVisibility(View.VISIBLE);
                    holder.btn.setEnabled(true);


                    holder.box.setChecked(true);
                    ischecked(position, true);

                } else {
                    holder.btn.setVisibility(View.GONE);
                    holder.btn.setEnabled(false);

                    holder.box.setChecked(false);
                    ischecked(position, false
                    );

                }
            }

        });
        name.setText(noticia.getTitulo());
        title.setText(noticia.getAutor());
        url.setText(noticia.getUrl());



        long millis = (noticia.getTiempo() - System.currentTimeMillis());
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.getTimeInMillis();


        String txtTime;
        int days = (int) TimeUnit.MILLISECONDS.toDays(millis);


        if  (days < 1){
            int hours = (int) TimeUnit.MILLISECONDS.toHours(millis);
            if (hours < 1){
                int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(millis);
                if (minutes < 1){
                    int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millis);
                    txtTime = seconds + " s";
                    time.setText(txtTime);
                }else{
                    txtTime = minutes + " m";
                    time.setText(txtTime);
                }
            }else {
                txtTime = hours + " H";
                time.setText(txtTime);
            }
        }else {
            txtTime = days+" D";
            time.setText(txtTime);
        }



        holder.box.setTag(country);
        Log.e("NoticiasAdapterRecover", "TIME LEFT(MILLIS):  ------>  "+String.valueOf(noticia.getTiempo() - System.currentTimeMillis()));
        Log.e("NoticiasAdapterRecover", "getView:  ------>  "+DateFormat.getInstance().format(noticia.getTiempo()));
        /////////////////////////////
        return convertView;

    }

    public LinkedList<String> getSelectedCountry(){
        LinkedList<String> List = new LinkedList<>();
        for (Map.Entry<Noticia, Boolean> pair : checkedForCountry.entrySet()) {
            if(pair.getValue()) {
                List.add(pair.getKey().getUrl());
            }
        }
        return List;
    }
    public void ischecked(int position,boolean flag )
    {
        checkedForCountry.put(this.linkedList.get(position), flag);
    }
    public void eliminarTodos(String who)
    {
        String tipo = who;
        conn = new ConexionSQLiteHelper(activity,"db_noticias",null,1);
        SQLiteDatabase db = conn.getReadableDatabase();
        if(tipo != "all")
        {
            String [] parametros = {who.toString()};
            db.delete(Utilidades.TABLA_NOTICIA,Utilidades.URL+"=?",parametros);
        }
        else
        {
            db.delete(Utilidades.TABLA_NOTICIA,null,null);
        }
        ((FragmentActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.contendor,fragment,null).addToBackStack(null).commit();
        db.close();



    }
    @Override
    public int getCount() {
        return noticias.size();
    }

    @Nullable
    @Override
    public Noticia getItem(int position) {
        return noticias.get(position);
    }
    public String getTitulo (int position)
    {
        return noticia.getTitulo();
    }
    public String getUrl (int position)
    {
        return noticia.getUrl();
    }
    public String getImg (int position)
    {
        return noticia.getImagen();
    }
    public String getAutor (int position)
    {
        return noticia.getAutor();
    }
    public int getPosition(int position){
        return position;
    }



}
