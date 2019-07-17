package com.amco.cidnews.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

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

import com.amco.cidnews.R;
import com.amco.cidnews.Utilities.ImagePassingAdapter;
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
*
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
* */

public class SwipAdapterBackCard extends ArrayAdapter<Noticia> {
    private ArrayList<Noticia> noticias;
    private Activity activity;
    private Noticia noticia;
    private Noticia noticiaUrlFacebook;
    private Context NewsContext;
    private Request requestImg;

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

   // private ImagePassingAdapter mPassingData;

    ///////
    public SwipAdapterBackCard(@NonNull Activity activity, ArrayList<Noticia> noticias
            , Context myContext) {
        super(activity, 0, noticias);
        try {
            this.activity = activity;
            this.noticias = noticias;
            this.NewsContext = myContext;
          //  this.mPassingData = mPassingInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

   /*     convertView.getLocationInWindow(location);
        setAxisXCardView = location[0];
        setAxisYCardView = location[1];

        DisplayMetrics dm = new DisplayMetrics();
        (activity).getWindowManager().getDefaultDisplay().getMetrics(dm);
        topOffset = dm.heightPixels - convertView.getMeasuredHeight();    //topOffset: La diferencia el screen del movil y el view de la aplicacion (bar screen)
        View tempView = convertView; // the view you'd like to locate
        int[] loc = new int[2];
        tempView.getLocationOnScreen(loc);
        final int yaux = loc[1] - topOffset;
        setAxisYCardView = yaux;
        containerBackView.setY(setAxisYCardView);
       // View convertViewExtended = convertView;*/

        final View convertViewBack = convertView;
        FacebookSdk.sdkInitialize(this.getContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this.activity);


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

            convertView = activity.getLayoutInflater().inflate(R.layout.backcardview, parent, false);

        }


        final ImageView img = convertView.findViewById(R.id.img_noticia);


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



        final int drawableResourceId = activity.getResources().getIdentifier("vacio", "drawable", activity.getPackageName());
        if (noticia.getImagen() == "null"   || !noticia.getImagen().startsWith("https")) {
            Log.d("SwipAdapterBackCard", "noticia image == null");
            Glide.with(activity).load(drawableResourceId).into(img);
        }
        else {
            requestImg = (Glide.with(activity).load(noticia.getImagen()).priority(Priority.LOW)
                    .thumbnail(Glide.with(getContext()).load(R.drawable.loadingblocks)).into(img)).getRequest();
            //            requestImg = (Glide.with(activity).load(noticia.getImagen()).thumbnail(Glide.with(getContext()).load(R.drawable.loading)).into(img)).getRequest();

            Log.d("SwipAdapterBackCard", "requestImg");


            /*

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Log.d("SwipAdapterBackCard", "Handler -- run()");

                    if (requestImg.isComplete()) {
                        Log.d("SwipAdapterBackCard", "requestImg Complete!");



                       // if(mPassingData != null){
                       //     Log.d("SwipAdapterBackCard","mPassingData.sendingImage");
                       //     mPassingData.sendingImage(img.getDrawingCache(),noticia.getImagen());
                       // }else
                       // {
                       //     Log.d("SwipAdapterBackCard","mPassingData == null");
                       // }
                        //mAdapterCallback.imageLoaded(img.getDrawingCache(),noticia.getImagen());


                    }

                    if (requestImg.isRunning()) {
                        requestImg.clear();
                        Log.e("SwipAdapterBackCard", "requestImg Running");
                        Glide.with(activity).load(drawableResourceId).into(img);


                    }
                }
            }, 3000);*/
        }
        return convertView;
    }

    ///////////////////////////////////
    /// MARK:
    public void setImageListener(ImagePassingAdapter ImageListener) {
        Log.d("SwipAdapterBackCard","setImageListener");
       // this.mPassingData = ImageListener;
    }

}

