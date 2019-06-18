package com.amco.cidnews.Utilities;



import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.amco.cidnews.Activities.MainActivity;
import com.amco.cidnews.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;



/*
* import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
*/

public class VistaWeb extends Fragment {

     static  WebView webView;
    ImageButton btnMoreInfo;
    ImageButton btnBack;
    ProgressBar progressBar;
    WebSettings webSettings;
    FloatingActionButton fav,share,trash;


    String  url="";
    boolean flagWebView = false;
    boolean flagSave = false;
    boolean flagDeleted = false;
    float scale=0;
    int pxX,pxY;


    //Menu
    PopupWindow popupWindowDogs;
    String popUpContents[];


    int bottomMargin = 0;
    int X=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_web, container, false);
        configUI(view);
        configUIListeners();

        setupMenu();
        return view;
    }

    public void configUI(View view){
        webView = view.findViewById(R.id.webView1);
        progressBar = view.findViewById(R.id.progressBarLoader);
        btnBack = view.findViewById(R.id.btn_atras_webView);
        btnMoreInfo = view.findViewById(R.id.btn_more_info);
        setupUI();
    }

    public void configUIListeners(){

        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (webView.getProgress() == 100){
                    Log.d("THEWEBVIEW", "X:"+String.valueOf(X)+"Bottom:"+String.valueOf(webView.getBottom())+"Height: "+String.valueOf(webView.getHeight())+"ContentHeight:"+String.valueOf(webView.getContentHeight())+"ScrollBarSize:"+String.valueOf(webView.getScrollBarSize())+"ScrollY:"+String.valueOf(scrollY));
                    if((scrollY > X*(0.95)&&(X != 0))){
                        if(!flagWebView) {
                            final RelativeLayout.MarginLayoutParams lpt = (RelativeLayout.MarginLayoutParams) webView.getLayoutParams();
                            Log.d("THEWEBVIEW", "ParamsTopMargin: "+String.valueOf( lpt.topMargin));


                            Log.d("THEWEBVIEW", "Height: "+String.valueOf(webView.getHeight())+"ContentHeight:"+String.valueOf(webView.getContentHeight()));
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT);
                            params.setMargins(lpt.leftMargin, lpt.topMargin, lpt.rightMargin, bottomMargin);
                            //webView.setLayoutParams(params);
                            //mframeLayout.setVisibility(View.VISIBLE);
                            flagWebView = true;
                        }
                    }else {
                        if(flagWebView) {
                            final RelativeLayout.MarginLayoutParams lpt = (RelativeLayout.MarginLayoutParams) webView.getLayoutParams();
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT);
                            params.setMargins(lpt.leftMargin, lpt.topMargin, lpt.rightMargin, 0);
                            //webView.setLayoutParams(params);
                            //mframeLayout.setVisibility(View.INVISIBLE);
                            flagWebView = false;
                        }
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearCache(true);
                webView.destroy();
                onBackPressed();
            }
        });

        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowDogs.showAsDropDown(view, -5, Math.round(0), 1);
            }
        });
    }

    public void setupUI(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        pxX = size.x;
        pxY = size.y;
        scale =  getResources().getDisplayMetrics().density;

        progressBar.setMax(100);
        progressBar.setProgress(1);

        url= getArguments().getString("url");
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.clearCache(true);
        Log.d( "LA URL ES: ", url);
        webView.loadUrl(url);


        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v("VistaWeb","setupUI -- webView.setWebViewClient, onPageFinished");
                progressBar.setVisibility(View.GONE);
                X = view.getContentHeight();
            }
        });
    }

    public void sendNewsByIntent(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(url));
        sendIntent.setType("text/plain");
        getActivity().startActivity(Intent.createChooser(sendIntent,"Powered by" + " Cidnews"));
    }

    public void onBackPressed() {
        // super.onBackPressed();
        webView.onPause();
        webView.destroy();
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            getActivity().finish();
        } else { //si no manda al fragment anterior.
            getFragmentManager().popBackStack();
        }
    }

    private void setupMenu () {
        List<String> dogsList = new ArrayList<String>();
        dogsList.add("REFRESH");
        dogsList.add("SHARE");
        dogsList.add("COPY LINK");
        dogsList.add("OPEN WITH");

        popUpContents = new String[dogsList.size()];
        dogsList.toArray(popUpContents);
        popupWindowDogs = popupWindowDogs();
    }

    public PopupWindow popupWindowDogs() {
        MainActivity mainActivity = (MainActivity) getActivity();
        final PopupWindow popupWindow = new PopupWindow(mainActivity);
        ListView listView = new ListView(mainActivity);
        listView.setAdapter(dogsAdapter(popUpContents));
        popupWindow.setFocusable(true);
        popupWindow.setWidth((int) (Math.round(pxX*0.5)));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.salud));
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
                    case 0 :
                        webView.reload();
                        break;
                    case 1:
                        sendNewsByIntent();
                        break;
                    default:
                        break;
                }
                popupWindow.dismiss();
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
                    case "REFRESH":
                        id = "menu_sup_salud";
                        size = porcentaje;
                        color = "#235784";
                        break;
                    case "SHARE":
                        id = "menu_sup_cons";
                        size = porcentaje;
                        color = "#235784";
                        break;

                    case "COPY LINK":
                        id = "menu_sup_ret";
                        size = porcentaje;
                        color = "#235784";
                        break;

                    case "OPEN WITH":
                        id = "menu_sup_edu";
                        size = porcentaje;
                        color = "#235784";
                        break;
                }
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
//
//   Loading = view.findViewById(R.id.cargando1);
//final FrameLayout mframeLayout = view.findViewById(R.id.fabs_frame);
        /*fav = view.findViewById(R.id.fav_frame_end_fab);
        share = view.findViewById(R.id.share_frame_end_fab);
        trash = view.findViewById(R.id.trash_frame_end_fab);*/
//  webSettings.setSupportMultipleWindows(true); * * *  * * *
        /*fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("THEWEBVIEW", "FAVORITES CLICKED");
                fav.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mainBlue)));
                flagSave = true;
                flagDeleted = false;
            }
        });
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("THEWEBVIEW", "DELETE CLICKED");
                if (flagSave) {
                    flagSave = false;
                    fav.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fabNews)));
                }
                trash.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mainBlue)));
                flagDeleted = true;
                onBackPressed();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("THEWEBVIEW", "SHARE CLICKED");
                sendNewsByIntent();
            }
        });*/
       /* if(webView.getProgress() < 30) {
          //  Glide.with(this).load(webView.getProgress()).thumbnail(Glide.with(getContext()).load(R.drawable.loading)).into(Loading);
            Glide.with(VistaWeb.this).load(R.drawable.loading).into(Loading);

        }*/
        /*mframeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mframeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                bottomMargin = mframeLayout.getHeight() + ((MainActivity)getActivity()).botones.getHeight();
                Log.d("THEWEBVIEW", "MarginB:"+String.valueOf(bottomMargin));
            }
        });*/