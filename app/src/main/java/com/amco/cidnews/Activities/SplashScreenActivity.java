package com.amco.cidnews.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.amco.cidnews.R;
import com.uxcam.UXCam;

public class SplashScreenActivity extends AppCompatActivity {

    //region VARIABLES
    private static String TAG = "SplashScreenActivity.java";
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Log.i(TAG, "Mostrando Splash Screen");

        UXCam.startWithKey("mr10kb29coxhprz");

        hideSoftKeyboard();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }, 1500);

    }

    //region METHODS

    /**
     * @author: Alejandro Jiménez (14 / 05 / 2020)
     * Método que oculta el teclado.
     */
    private  void  hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    //endregion
}
