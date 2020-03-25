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

public class SwipAdapter extends ArrayAdapter<Noticia> {
    private ArrayList<Noticia> noticias;
    private Activity activity;
    private Noticia noticia;
    private Noticia noticiaUrlFacebook;
    private Context NewsContext;
    private Request requestImg;

    private ImageView img;  //ImageBackground


    static WebView webView;
    int heightAux;
    int widthAux;
    ImageView fondo_azul;
    Animation fabOpen, fabClose, rotateFoward, rotateBackward;
    boolean isOpen = false;
    Fragment fragment = new VistaWeb();
    Bundle args = new Bundle();

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    FloatingActionButton shareButtonHelper, fbButtonHelper, whatsButtonHelper;
    String currentUrl;
    ///////


    public interface RequestImage {
        void onRequestImage(String currentUrl);

        void onReloadNextImage();
    }

    private RequestImage mRequest;


    public SwipAdapter(@NonNull Activity activity, ArrayList<Noticia> noticias, Context myContext
            , RequestImage currentRequest) {
        super(activity, 0, noticias);
        try {
            this.activity = activity;
            this.noticias = noticias;
            this.NewsContext = myContext;
            this.mRequest = currentRequest;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ViewHolder {
        FloatingActionButton shareButton, facebookButton, whatsappButton;
    }

    @Nullable
    @Override
    public Noticia getItem(int position) {
        return noticias.get(position);
    }

    public void mostrarNoticiasView(final int position) {
        args.putString("url", noticias.get(position).getUrl());
        fragment.setArguments(args);
        ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.contendor, fragment, null).addToBackStack(null).commit();
    }

    public String getTitulo(int position) {
        return noticia.getTitulo();
    }

    public String getUrl(int position) {
        return noticia.getUrl();
    }

    public String getImg(int position) {
        return noticia.getImagen();
    }

    public void resetButtons(View v) {

    }

    public String getAutor(int position) {
        return noticia.getAutor();
    }

    public int getPosition(int position) {
        return position;
    }

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final View convertViewBack = convertView;
        FacebookSdk.sdkInitialize(this.getContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this.activity);


        final ViewHolder holder;

        noticia = noticias.get(position);
        if (position <= 1) {
            noticiaUrlFacebook = noticias.get(0);
        }
        if ((position > 1) && (position < noticias.size() - 1)) {
            noticiaUrlFacebook = noticias.get(position - 1);
        }

        if ((position >= noticias.size() - 1)) {
            noticiaUrlFacebook = noticias.get(position);
        }


        if (null == convertView) {

            convertView = activity.getLayoutInflater().inflate(R.layout.content_cardview, parent, false);

        }


        img = convertView.findViewById(R.id.img_noticia);


        int W = img.getWidth();
        int H = img.getHeight();
        Log.d("Imagen Noticia", " Width:" + W);
        Log.d("Imagen Noticia", " Height:" + H);
        TextView titulo = convertView.findViewById(R.id.titulo_noticia);
        titulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        TextView autor = convertView.findViewById(R.id.autor);
        titulo.setText(noticia.getTitulo());
        String textForNews = "- " + noticia.getAutor();
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

        return convertView;
    }


    @Override
    public int getCount() {
        return noticias.size();
    }
}
