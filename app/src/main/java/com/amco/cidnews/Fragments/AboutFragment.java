package com.amco.cidnews.Fragments;

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
                Log.e(TAG, "setOnClickListener()");

                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.remove(AboutFragment.this);
                fragmentTransaction.commit();
                fragmentManager.popBackStack();

                MainActivity.scrollMenuPosition.setVisibility(View.VISIBLE);

                //ACTIVAS LA PROPIEDAD UNCHECK DEL HOME EN EL BOTTOM NAVIGATION HOME PARA CAMBIAR COLOR DEL ICONO
                MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(true);

                ((MainActivity)getActivity()).setNotifyIsActiveAboutUs(false);
            }
        });
        return view;
    }

    //endregion

    //region METHODS

    /**
     * <p><h2><b>Created by Alejandro Jimenez on 16/05/2020</b></h2></p>
     * <br>
     * Método que configura los widgets de la vista.
     * @param view Objeto de la clase View, que ayuda a enlazar con los widgets en un fragment. (Verificar que se infle la View)
     */
    private void setupWidgets(View view) {
        imageButtonRegresar = view.findViewById(R.id.imageButtonAtras);
    }

    //endregion
}
