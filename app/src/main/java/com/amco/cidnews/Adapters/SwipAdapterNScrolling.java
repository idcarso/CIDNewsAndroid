package com.amco.cidnews.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amco.cidnews.R;
import com.amco.cidnews.Utilities.Noticia;
import com.amco.cidnews.Utilities.VistaWeb;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.Request;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/*
* import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
*/

public class SwipAdapterNScrolling extends ArrayAdapter<Noticia>  {
    private ArrayList<Noticia> noticias;
    private Activity activity;
    private Noticia noticia;
    private Context NewsContext;
    private Request requestImg;

    private ImageView img;  //ImageBackground


    Fragment fragment = new VistaWeb();
    Bundle args = new Bundle();




    public SwipAdapterNScrolling(@NonNull Activity activity, ArrayList<Noticia> noticias, Context myContext) {
        super(activity, 0, noticias);
        try {
            this.activity = activity;
            this.noticias = noticias;
            this.NewsContext = myContext;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public Noticia getItem(int position) {
        return noticias.get(position);
    }

    public void mostrarNoticiasView(final int position){
        args.putString("url",noticias.get(position).getUrl());
        fragment.setArguments(args);
        ((FragmentActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.contendor,fragment,null).addToBackStack(null).commit();
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
    public void resetButtons (View v)
    {

    }

    public String getAutor (int position)
    {
        return noticia.getAutor();
    }
    public int getPosition(int position){
        return position;
    }



    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        noticia = noticias.get(position);

        if (null == convertView)
                convertView = activity.getLayoutInflater().inflate(R.layout.content_cardview_nscrolling, parent, false);

        img = convertView.findViewById(R.id.img_noticia);




        TextView titulo = convertView.findViewById(R.id.titulo_noticia);
        titulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView autor = convertView.findViewById(R.id.autor);
        titulo.setText(noticia.getTitulo());

        String textForNews = "- "+noticia.getAutor();
        autor.setText(textForNews);



        Log.d("SwipAdapter", "getView: == null");
        final int drawableResourceId = activity.getResources().getIdentifier("vacio", "drawable", activity.getPackageName());
        if (noticia.getImagen() == "null" || !noticia.getImagen().startsWith("https"))
                Glide.with(activity).load(drawableResourceId).into(img);
        else {
            requestImg = (Glide.with(activity)
                        .load(noticia.getImagen()).priority(Priority.HIGH)
                        .thumbnail(Glide.with(getContext())
                                .load(R.drawable.loadingblocks))
                        .into(img)).getRequest();
            }

        return  convertView;
    }


    @Override
    public int getCount() {
        return noticias.size();
    }
}
