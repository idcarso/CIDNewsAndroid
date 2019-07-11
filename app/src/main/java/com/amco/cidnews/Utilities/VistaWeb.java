package com.amco.cidnews.Utilities;



import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
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

    public static final String TAG = "VistaWeb";

    FrameLayout mFrameToolbar;
     static  WebView webView;
    ImageButton btnMoreInfo;
    ImageButton btnBack;
    ProgressBar progressBar;
    WebSettings webSettings;
    FloatingActionButton fav,share,trash;


    String  url="";

    float scale=0;
    int pxX,pxY;

    //top Margin Value ( Webview)
    int topMarginWebview = 0;

    //Menu
    PopupWindow popupWindowDogs;
    String popUpContents[];


    //Int
    int bottomMargin = 0;
    int X=0;

    //Boolean
    boolean flagWebView = false;
    boolean flagSave = false;
    boolean flagDeleted = false;
    boolean toolbarIsInAnimation = false;

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
        mFrameToolbar = view.findViewById(R.id.toolbar_fav);
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
                Log.d(TAG, "configUIListeners -- webView.setOnScrollChangeListener -- X:"
                        +"oldScrollY:"+oldScrollY
                        +",ScrollY:"+scrollY);

                if (scrollY - oldScrollY > 10  && !toolbarIsInAnimation && mFrameToolbar.getVisibility() == View.VISIBLE){ //HIDE BAR
                    toolbarIsInAnimation = true;
                    slideUp(mFrameToolbar);
                }

                if(scrollY - oldScrollY < -10  && !toolbarIsInAnimation && mFrameToolbar.getVisibility() == View.GONE){
                    toolbarIsInAnimation = true;
                    slideDown(mFrameToolbar);
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


        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
        topMarginWebview = params.topMargin;

        url= getArguments().getString("url");
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.clearCache(true);
        Log.d( TAG,"setupUI --  URL:"+ url);
        webView.loadUrl(url);




        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v(TAG,"setupUI -- webView.setWebViewClient, onPageFinished");
                progressBar.setVisibility(View.GONE);
                progressBar.setAlpha(0f);
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
                        progressBar.setAlpha(1f);
                        webView.reload();
                        break;
                    case 1:
                        sendNewsByIntent();
                        break;
                    case 2:
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Url News", url);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getContext(),"Url Copied!",Toast.LENGTH_SHORT).show();
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

    /**
     * Animación para remover la barra
     * @param view       Vista a animar.
     */
    ///////////////////////////////////
    /// MARK:Animación para ocultar la barra
    public void slideUp(final View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                -view.getHeight());                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFrameToolbar.setVisibility(View.GONE);
                btnBack.setEnabled(false);
                btnMoreInfo.setEnabled(false);
                toolbarIsInAnimation = false;
                progressBar.setAlpha(0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        progressBar.startAnimation(animate);
        view.startAnimation(animate);

        //Animation Webview
        final int topMarginEnd = 0;//
        final int topMarginStart = topMarginWebview; //
        Animation mAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
                params.topMargin = topMarginStart + (int) ((topMarginEnd - topMarginStart) * interpolatedTime);
                webView.setLayoutParams(params);
            }
        };

        mAnimation.setDuration(450); // in ms
        webView.startAnimation(mAnimation);
    }

    /**
     * Animación para mostrar la barrar
     * @param view       Vista a animar.
     */
    //
    public void slideDown(View view){

        //Animation Toolbar
        mFrameToolbar.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                -view.getHeight(),                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toolbarIsInAnimation = false;
                progressBar.setAlpha(1f);
                btnBack.setEnabled(true);
                btnMoreInfo.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        progressBar.startAnimation(animate);
        view.startAnimation(animate);





        //Animation WebView
        final int topMarginStart = 0;// your start value
        final int topMarginEnd = topMarginWebview; // where to animate to
        Animation mAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
                params.topMargin = topMarginStart + (int) ((topMarginEnd - topMarginStart) * interpolatedTime);
                webView.setLayoutParams(params);
            }
        };
        mAnimation.setDuration(500); // in ms
        webView.startAnimation(mAnimation);
    }
}