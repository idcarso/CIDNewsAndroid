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


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;


public class NoticiasAdapter extends ArrayAdapter<Noticia> {

    //region VARIABLES
    private static String TAG = "NoticiasAdapter.java";
    private ArrayList<Noticia> noticias;
    Context mContext;
    List<Noticia> linkedList;
    public boolean[] checkBoxState = null;
    private HashMap<Noticia, Boolean> checkedForCountry = new HashMap<>();
    private Activity activity;
    private Noticia noticia;
    private int bandera;
    private int bandera2; //Controla el estado en el que esta la vista. 0 = Eliminar || 1 = Navegar en las noticias
    ConexionSQLiteHelper conn;
    Bundle args = new Bundle();
    //endregion

    //region VIEWS
    static  WebView webViewMain;
    //endregion

    //region FRAGMENTS
    Fragment fragment = new FavFragment();
    Fragment vistaWeb = new VistaWeb();
    //endregion


    public NoticiasAdapter(@NonNull Activity activity, ArrayList<Noticia> noticias, int bandera, int bandera2) {
        super(activity, 0, noticias);
        this.activity = activity;
        this.noticias = noticias;
        this.linkedList = noticias;
        this.mContext = activity;
        this.bandera = bandera;
        this.bandera2 = bandera2;
        checkBoxState = new boolean[noticias.size()];

    }

    //Viewholder
    public static class ViewHolder {
        ArrayList<Noticia> noticiasrespaldo;
        RelativeLayout Lista;
        CheckBox box;
        ImageButton btn;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final ViewHolder holder;
        final Noticia country = linkedList.get(position);


        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.lista_favoritos, parent, false);
            holder = new ViewHolder();
            //Vinculacion de vistas
            holder.noticiasrespaldo = new ArrayList<Noticia>();
            holder.box = convertView.findViewById(R.id.checkBox);
            holder.Lista = (RelativeLayout) convertView.findViewById(R.id.listaPapa);
            holder.btn = (ImageButton) convertView.findViewById(R.id.borrar_uno);

            //Configuracion de vistas
            holder.box.setVisibility(View.INVISIBLE);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        checkBoxState = new boolean[linkedList.size()];
        final ImageView avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
        final TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        TextView title = (TextView) convertView.findViewById(R.id.tv_title);
        TextView url = (TextView) convertView.findViewById(R.id.edit_url);

        if (checkBoxState != null)
            holder.box.setChecked(checkBoxState[position]);
        if (bandera == 0)
            holder.btn.setVisibility(convertView.VISIBLE); //Bandera es 0 significa que no se eliminara ninguna noticia, es visible el image button
        if (bandera == 1)
            holder.btn.setVisibility(convertView.VISIBLE);

        final Noticia noticia = getItem(position);
        int drawableResourceId = activity.getResources().getIdentifier("ic_cidnews_avatar", "drawable", activity.getPackageName());

        // Si la url de la imagen de la noticia no tiene null o si no empieza la url diferente de https, descarga la imagen y la muestra en el avatar
        if (noticia.getImagen().equalsIgnoreCase("null") || !(noticia.getImagen().startsWith("https"))) {
            Log.e(TAG, "Noticia.getImagen:" + String.valueOf(noticia.getImagen()));
            Glide.with(activity)
                    .load(drawableResourceId)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(avatar);
        } else { // Si no, coloca una imagen por default por experiencia de usuario
            Log.e(TAG, "Noticia.getImagen:" + String.valueOf(noticia.getImagen()));
            Glide.with(activity)
                    .load(noticia.getImagen())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(avatar);
        }

        // Evento del image button basura para eliminar noticia
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("holder btn", "onClick: bandera2 =" + String.valueOf(bandera2));
                if (bandera2 == 0) {  //Permite seleccionar a eliminar
                    //Primer btn que haga click debe hacer llamar a consulta
                    if (!checkBoxState[position]) {
                        Log.e(TAG, "holder.box onClick: Checked To FALSE || " + checkBoxState[position]);
                        holder.btn.setBackgroundResource(R.drawable.fabcornertrash);
                        holder.btn.setAlpha(1f);
                        checkBoxState[position] = true;
                        ischecked(position, true);
                    } else {
                        Log.e("NoticiasAdapter", "holder.box onClick: Checked ToTRUE");
                        holder.btn.setBackgroundResource(R.drawable.roundcorner_adapter);
                        holder.btn.setAlpha(0.75f);
                        checkBoxState[position] = false;
                        ischecked(position, false);
                    }
                    if (areAllTrue(checkBoxState)) {
                        Log.e("areAllTrue", "onClick: YES");
                    } else {
                        Log.e("areAllTrue", "onClick: NO?");
                    }
                    Log.e("holderBtn", "onClick: ");
                } else {
                    if (FavFragment.positionFlagIcon < 0) {
                        FavFragment.positionFlagIcon = position;
                        notifyDataSetChanged();
                    }
                }
            }
        });


        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Log.e("NoticiasAdapter", "holder.box onClick: Cheked TRUE");

                    checkBoxState[position] = true;
                    ischecked(position, true);
                    //holder.btn.setVisibility(View.INVISIBLE);
                    holder.btn.setBackgroundResource(R.drawable.fabcornertrash);
                    holder.btn.setAlpha(1f);


                } else {
                    Log.e("NoticiasAdapter", "holder.box onClick: Cheked FALSE");
                    checkBoxState[position] = false;
                    ischecked(position, false);
                    //holder.btn.setVisibility(View.VISIBLE);
                    holder.btn.setBackgroundResource(R.drawable.roundcorner_adapter);
                    holder.btn.setAlpha(0.75f);
                }
            }
        });

        if (checkedForCountry.get(country) != null) {
            holder.box.setChecked(checkedForCountry.get(country));
        }


        ///MARK: DONE HERE
        if (holder.box.isChecked()) {
            holder.btn.setBackgroundResource(R.drawable.fabcornertrash);
            holder.btn.setAlpha(1f);

            //holder.btn.setVisibility(View.GONE);
        } else {
            holder.btn.setBackgroundResource(R.drawable.roundcorner_adapter);
            holder.btn.setAlpha(0.75f);

        }

        holder.Lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!holder.box.isChecked()) {
                    Log.e("NoticiasAdapter", "holder.Lista onClick: Cheked FALSE");
                    holder.btn.setBackgroundResource(R.drawable.fabcornertrash);
                    holder.btn.setAlpha(1f);

                    //  holder.btn.setEnabled(false);
                    holder.box.setChecked(true);
                    ischecked(position, true);
                } else {
                    Log.e("NoticiasAdapter", "holder.Lista onClick: Cheked TRUE");

                    holder.btn.setBackgroundResource(R.drawable.roundcorner_adapter);
                    holder.btn.setAlpha(0.75f);

                    //holder.btn.setEnabled(true);
                    holder.box.setChecked(false);
                    ischecked(position, false);
                }
                if (bandera2 == 1) {
                    holder.btn.setBackgroundResource(R.drawable.roundcorner_adapter);
                    holder.btn.setAlpha(0.75f);

                    showNewsInWebView(position);
                }
            }

        });


        name.setText(noticia.getTitulo());
        title.setText(noticia.getAutor());
        url.setText(noticia.getUrl());
        holder.box.setTag(country);
        /////////////////////////////
        webViewMain = (WebView) convertView.findViewById(R.id.webviewMain1);

        if (position == FavFragment.positionFlagIcon) {
            Log.e("NoticiasAdapter", "getView: positionFlag:" + String.valueOf(position));
            holder.btn.performClick();
            FavFragment.positionFlagIcon = -1;
        }
        return convertView;
    }


    public void showNewsInWebView(int position){
        args.putString("url", noticias.get(position).getUrl());
        vistaWeb.setArguments(args);
        ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.contendor, vistaWeb, null).addToBackStack(null).commit();
    }

    public static boolean areAllTrue(boolean[] array) {
        for(boolean b : array) if(!b) return false;
        return true;
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

    public void ischecked(int position,boolean flag ) {
        checkedForCountry.put(this.linkedList.get(position), flag);
    }

    public void eliminarTodos(String who) {
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
}
