package com.amco.cidnews.Utilities;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amco.cidnews.Activities.MainActivity;
import com.amco.cidnews.Fragments.HomeFragment;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class InternetVerify extends AsyncTask<String, Void, Integer> {

    //region VIEW
    LinearLayout linearLayoutAnuncioNoIntener;
    //endregion

    //region VARIABLES
    private static final String TAG = "InternetVerify.java";
    Context context;
    //endregion

    //region CONSTRUCTOR

    public InternetVerify(Context context, LinearLayout linearLayoutAnuncioNoIntener) {
        this.linearLayoutAnuncioNoIntener = linearLayoutAnuncioNoIntener;
        this.context = context;
    }

    //endregion

    //region METHODS

    /**
     * Método que realiza ping al servidor de Google para verificar si hay internet (necesita estar dentro de una clase asynctask).
     * @return 0: error en el ping | 1: ping correcto
     */
    private int pingSocketInternet() {
        int result = 0;
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);
            socket.connect(socketAddress, 0);
            result = 1;
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error haciendo ping: " + e.getLocalizedMessage().toString());
            result = 0;
        }
        return result;
    }

    /**
     * Método que compara si esta conectado o no a una red.
     * @return true: si esta conectado | false: no esta conectado
     */
    private boolean areConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Método que verifica el estado actual de la red, si esta conectado a una o no y si tiene internet o no.
     * @param resultPing
     * @return
     */
    private boolean currentStatusConnected(int resultPing) {
        boolean respuesta = false;
        if (areConnected()) {
            if (resultPing == 1) {
                respuesta = true;
            } else if (resultPing == 0){
                respuesta = false;
            }
        } else {
            respuesta = false;
        }
        return respuesta;
    }
    //endregion

    //region OVERRIDES ASYNCTASK
    @Override
    protected Integer doInBackground(String... strings) {
        return pingSocketInternet();
    }

    @Override
    protected void onPostExecute(Integer resultPing) {
        super.onPostExecute(resultPing);

        if (currentStatusConnected(resultPing) == true) {
            linearLayoutAnuncioNoIntener.setVisibility(View.INVISIBLE);
            HomeFragment.isNetworkAvailable = true;
        } else {
            linearLayoutAnuncioNoIntener.setVisibility(View.VISIBLE);
            HomeFragment.isNetworkAvailable = false;
        }
    }

    //endregion
}
