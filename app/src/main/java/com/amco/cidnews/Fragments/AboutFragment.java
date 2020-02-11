package com.amco.cidnews.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amco.cidnews.Activities.MainActivity;
import com.amco.cidnews.R;

public class AboutFragment extends Fragment {



    //region ATRIBUTOS

    //VISTAS DEL FRAGMENT
    public static ImageButton imageButtonRegresar;
    DrawerLayout drawerLayout;

    //BANDERA DE ACTIVO PARA ESTE FRAGMENT EN ENTERO
    int contadorAbout = 0;

    //BANDERA DE ACTIVO PARA ESTE FRAGMENT EN BOOLEANO
    public static boolean activoAbout = false;

    //endregion




    //region CICLO DE VIDA DEL FRAGMENT

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frame_about, container, false);

        //ENLAZAMOS LAS VISTAS AL FRAGMENT
        EnlazarVistas(view);

        //EVENTO DE CLICK EN EL BOTON DE REGRESAR
        imageButtonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.e("FRAGMENT ABOUT US", "LANZANDO FRAGMENT HOME...");

                //SI EL SCROLL ES INVISIBLE, LO MOSTRAMOS CUANDO REGRESEMOS A HOME
                if (MainActivity.scrollMenuPosition.getVisibility() == View.INVISIBLE) {

                    MainActivity.scrollMenuPosition.setVisibility(View.VISIBLE);
                    Log.w("SCROLL NAVIGATION", "CAMBIO A VISIBLE");

                }

                //ACTIVAS LA PROPIEDAD UNCHECK DEL HOME EN EL BOTTOM NAVIGATION HOME PARA CAMBIAR COLOR DEL ICONO
                MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(true);

                HomeFragment.HabilitarHome();

                //CAMBIAMOS DE ABOUT US A HOME
                Fragment fragmentHome = new HomeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                //transaction.replace(R.id.contendor, fragmentHome);
                //transaction.commit();
                transaction.remove(AboutFragment.this).commit();

                Log.e("FRAGMENT ABOUT US", "¡FRAGMENT HOME LANZADO!");

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        //SI EL SCROLL ES VISIBLE, LO ESCONDEMOS ESTANDO EN ABOUT US
        if (MainActivity.scrollMenuPosition.getVisibility() == View.VISIBLE) {

            MainActivity.scrollMenuPosition.setVisibility(View.INVISIBLE);
            Log.w("SCROLL NAVIGATION", "CAMBIO A INVISIBLE");

        }

        //ACTIVAS LA PROPIEDAD UNCHECK DEL HOME EN EL BOTTOM NAVIGATION HOME PARA CAMBIAR COLOR DEL ICONO
        MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(false);*/

        //CUANDO EL FRAGMENT ESTA EN RESUMEN, ACTIVAMOS LA BANDERA DE ACTIVO
        activoAbout = true;

        Log.w("FRAGMENT ABOUT US", "BANDERA ABOUT US: " + activoAbout);

        //SUMAMOS EN EL CONTADOR CADA QUE EL FRAGMENT PASA POR ONRESUME
        contadorAbout++;

        Log.w("FRAGMENT-ABOUT", "CONTADOR ABOUT: " + contadorAbout);
        //DESTRUIMOS EL FRAGMENT CUANDO SEA MAS DE DOS VECES SU PASO POR RESUME
        if (contadorAbout > 1) {

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            //fragmentTransaction.replace(R.id.contendor_home, new HomeFragment());
            fragmentTransaction.remove(AboutFragment.this).commit();

            //SI EL SCROLL ES INVISIBLE, LO MOSTRAMOS CUANDO REGRESEMOS A HOME
            if (MainActivity.scrollMenuPosition.getVisibility() == View.INVISIBLE) {

                MainActivity.scrollMenuPosition.setVisibility(View.VISIBLE);
                Log.w("SCROLL NAVIGATION", "CAMBIO A VISIBLE");

            } else {

                Log.w("SCROLL NAVIGATION", "SCROLL ES VISIBLE");

            }

            //ACTIVAS LA PROPIEDAD UNCHECK DEL HOME EN EL BOTTOM NAVIGATION HOME PARA CAMBIAR COLOR DEL ICONO
            MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.w("FRAGMENT-ABOUT", "ESTOY EN ON PAUSE");

        //CUANDO EL FRAGMENT PASA POR ONPAUSE, DESACTIVAMOS LA BANDERA DEL FRAGMENT
        activoAbout = false;

        Log.e("FRAGMENT ABOUT US", "BANDERA ABOUT US: " + activoAbout);

        //SI EL SCROLL ES INVISIBLE, LO MOSTRAMOS CUANDO REGRESEMOS A HOME
        if (MainActivity.scrollMenuPosition.getVisibility() == View.INVISIBLE) {

            MainActivity.scrollMenuPosition.setVisibility(View.VISIBLE);
            Log.w("SCROLL NAVIGATION", "CAMBIO A VISIBLE");

        } else {

            Log.w("SCROLL NAVIGATION", "SCROLL ES VISIBLE");

        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //endregion



    //region METODOS

    //ENLACE DE LAS VISTAS
    public void EnlazarVistas(View view){
        imageButtonRegresar = (ImageButton) view.findViewById(R.id.imgbtnRegresarHome);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
    }

    //endregion



}
