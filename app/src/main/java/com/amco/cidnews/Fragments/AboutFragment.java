package com.amco.cidnews.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amco.cidnews.Activities.MainActivity;
import com.amco.cidnews.R;

public class AboutFragment extends Fragment {

    //region VARIABLES
    private static String TAG = "AboutFragment.java";
    int contadorAbout = 0;
    public static boolean activoAbout = false;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    //endregion

    //region WIDGETS
    public static ImageButton imageButtonRegresar;
    //endregion

    //region LIFECYCLE FRAGMENT
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frame_about, container, false);

        //Setup Widgets
        setupWidgets(view);

        //On click listener
        imageButtonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "setOnClickListener*()");

                //SI EL SCROLL ES INVISIBLE, LO MOSTRAMOS CUANDO REGRESEMOS A HOME
                if (MainActivity.scrollMenuPosition.getVisibility() == View.INVISIBLE) {
                    MainActivity.scrollMenuPosition.setVisibility(View.VISIBLE);
                    Log.w("SCROLL NAVIGATION", "CAMBIO A VISIBLE");
                }

                //ACTIVAS LA PROPIEDAD UNCHECK DEL HOME EN EL BOTTOM NAVIGATION HOME PARA CAMBIAR COLOR DEL ICONO
                MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(true);

                HomeFragment.setEnableWidgetsHome();

                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.remove(AboutFragment.this);
                fragmentTransaction.commit();
                fragmentManager.popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //CUANDO EL FRAGMENT ESTA EN RESUMEN, ACTIVAMOS LA BANDERA DE ACTIVO
        activoAbout = true;
        Log.w(TAG, "onResume() --> activoAbout = " + activoAbout);

        //SUMAMOS EN EL CONTADOR CADA QUE EL FRAGMENT PASA POR ONRESUME
        contadorAbout++;
        Log.w(TAG, "onResume() --> contadorAbout = " + contadorAbout);

        //DESTRUIMOS EL FRAGMENT CUANDO SEA MAS DE DOS VECES SU PASO POR RESUME
        if (contadorAbout > 1) {
            HomeFragment.setEnableWidgetsHome();

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.remove(getActivity().getSupportFragmentManager().findFragmentByTag("AboutFragment"));
            fragmentTransaction.commit();
            fragmentManager.popBackStack();

            //SI EL SCROLL ES INVISIBLE, LO MOSTRAMOS CUANDO REGRESEMOS A HOME
            if (MainActivity.scrollMenuPosition.getVisibility() == View.INVISIBLE) {
                MainActivity.scrollMenuPosition.setVisibility(View.VISIBLE);
            } else {}

            //ACTIVAS LA PROPIEDAD UNCHECK DEL HOME EN EL BOTTOM NAVIGATION HOME PARA CAMBIAR COLOR DEL ICONO
            MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //CUANDO EL FRAGMENT PASA POR ONPAUSE, DESACTIVAMOS LA BANDERA DEL FRAGMENT
        activoAbout = false;
        Log.e(TAG, "onPause() --> activoAbout = " + activoAbout);

        //SI EL SCROLL ES INVISIBLE, LO MOSTRAMOS CUANDO REGRESEMOS A HOME
        if (MainActivity.scrollMenuPosition.getVisibility() == View.INVISIBLE) {
            MainActivity.scrollMenuPosition.setVisibility(View.VISIBLE);
        } else {}
    }

    //endregion



    //region METHODS

    /**
     * <p>Created by Alejandro Jimenez on 16/05/2020</p>
     * <br>
     * Método que configura los widgets de la vista.
     * @param view Objeto de la clase View, que ayuda a enlazar con los widgets en un fragment. (Verificar que se infle la View)
     */
    private void setupWidgets(View view) {
        imageButtonRegresar = view.findViewById(R.id.imageButtonAtras);
    }

    //endregion



}
