package com.amco.cidnews.Fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amco.cidnews.Activities.MainActivity;
import com.amco.cidnews.Utilities.ConexionSQLiteHelper;
import com.amco.cidnews.R;
import com.amco.cidnews.Utilities.Utilidades;


public class ConfigFragment extends Fragment {

    ImageView image_salud,image_retail,image_cons,image_entre,image_amb,image_edu,image_ene,image_ban,image_tel;

    Button saved;

    int estado_actual,estado_siguiente;

    TextView txenvio,txhealth,txretail,txconstruc,txentertain,txeducation,txenergy,txtelecom,txfinance;

    public static boolean isAnyConfiguration = false; // TRUE: HAY AUN QUE SEA UNA OPCION CONFIGURADA || FALSE: NO SE ESCOGIO NINGUNA OPCION

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.frame_config2,container,false);
        image_salud =  view.findViewById(R.id.img_salud);
        image_retail =  view.findViewById(R.id.img_retail);
        image_cons =  view.findViewById(R.id.img_cons);
        image_entre =  view.findViewById(R.id.img_entretenimiento);
        image_amb =  view.findViewById(R.id.img_ambiente);
        image_edu =  view.findViewById(R.id.img_educacion);
        image_ene =  view.findViewById(R.id.img_energia);
        image_ban =  view.findViewById(R.id.img_banca);
        image_tel =  view.findViewById(R.id.img_telecom);

        txhealth = view.findViewById(R.id.config_salud);
        txretail = view.findViewById(R.id.config_retail);
        txconstruc = view.findViewById(R.id.config_construccion);
        txentertain = view.findViewById(R.id.confi_entrenimiento);
        txeducation = view.findViewById(R.id.config_educacion);
        txenergy = view.findViewById(R.id.config_energia);
        txenvio = view.findViewById(R.id.config_ambiente);
        txtelecom =  view.findViewById(R.id.config_telecom);
        txfinance = view.findViewById(R.id.config_banca);

        config_inicial();
        config_obtenerValores();
        botones();

        return view;

    }

    private void recargar() {
        Fragment fragment = new ConfigFragment();
        ((FragmentActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.contendor,fragment).addToBackStack("Settings").commit();
    }

    private void actualizar(String categoria, int estado) {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getWritableDatabase();
        String sentencias = "UPDATE "+Utilidades.TABLA_PREFERENCIA+" SET estado = "+estado+"  where "+Utilidades.CATEGORIA+" = '"+categoria+"'";
        Cursor cursor = db.rawQuery(sentencias,null);
        cursor.moveToFirst();
        cursor.close();

    }

    private int estado(String categoria) {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getWritableDatabase();
        String [] parametros = {categoria};
        Cursor cursor = db.rawQuery("SELECT estado FROM "+Utilidades.TABLA_PREFERENCIA+" where "+Utilidades.CATEGORIA+" =?",parametros);
        cursor.moveToFirst();
        db.close();  //////// ************* CLOSE13SEP
        return cursor.getInt(0);
    }

    private void config_obtenerValores() {
        String [] categorias = {"SALUD","RETAIL","CONSTRUCCIÓN","ENTRETENIMIENTO","AMBIENTE","EDUCACIÓN","ENERGÍA","BANCA","TELECOM"};
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Utilidades.TABLA_PREFERENCIA+"",null);
        int estado;
        String categoria;
        while (cursor.moveToNext()) {
            categoria = cursor.getString(0);
            estado = cursor.getInt(1);
            cambioDeImagen(categoria,estado);

        }
        db.close();  ////*************************   CLOSE 13SEP
    }

    private void cambioDeImagen(String categoria, int estado) {
        switch (categoria)
        {
            case "SALUD":
                if(estado==1)  image_salud.setImageResource(R.drawable.salud_on_);
                else {
                    image_salud.setImageResource(R.drawable.salud_off);
                    txhealth.setTextColor(Color.rgb(169,169,169)); }  //DPS? 03 -Octubre
                break;

            case "RETAIL":
                if(estado==1)  image_retail.setImageResource(R.drawable.retail_on);
                else {
                    image_retail.setImageResource(R.drawable.retail_off);
                    txretail.setTextColor(Color.rgb(169,169,169));}
                break;

            case "CONSTRUCCIÓN":
                if(estado==1)  image_cons.setImageResource(R.drawable.construction_on);
                else {
                    image_cons.setImageResource(R.drawable.construccion_off);
                    txconstruc.setTextColor(Color.rgb(169,169,169));}
                break;

            case "ENTRETENIMIENTO":
                if(estado==1)  image_entre.setImageResource(R.drawable.entretenimiento_on);
                else {
                    image_entre.setImageResource(R.drawable.entretenimiento_off);
                    txentertain.setTextColor(Color.rgb(169,169,169));}
                break;

            case "AMBIENTE":
                if(estado==1)  image_amb.setImageResource(R.drawable.ambiente_on);
                else {
                    image_amb.setImageResource(R.drawable.ambiente_off);
                    txenvio.setTextColor(Color.rgb(169,169,169));}
                break;

            case "EDUCACIÓN":
                if(estado==1)  image_edu.setImageResource(R.drawable.education_on);
                else{
                    image_edu.setImageResource(R.drawable.educacion_off);
                    txeducation.setTextColor(Color.rgb(169,169,169));}
                break;

            case "ENERGÍA":
                if(estado==1)  image_ene.setImageResource(R.drawable.energia_on);
                else{
                    image_ene.setImageResource(R.drawable.energia_off);
                    txenergy.setTextColor(Color.rgb(169,169,169));}
                break;

            case "BANCA":
                if(estado==1)  image_ban.setImageResource(R.drawable.bancaria_on);
                else {
                    image_ban.setImageResource(R.drawable.bancaria_off);
                    txfinance.setTextColor(Color.rgb(169,169,169));}
                break;
            case "TELECOM":
                if(estado==1)  image_tel.setImageResource(R.drawable.telecom_on);
                else {
                    image_tel.setImageResource(R.drawable.telecom_off);
                    txtelecom.setTextColor(Color.rgb(169,169,169));}
                break;

        }
    }

    public void config_inicial()
    {
        String [] categorias = {"SALUD","RETAIL","CONSTRUCCIÓN","ENTRETENIMIENTO","AMBIENTE","EDUCACIÓN","ENERGÍA","BANCA","TELECOM"};
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getActivity(),"db_noticias",null,1);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Utilidades.TABLA_PREFERENCIA+"",null);

            if(cursor.getCount()==0)
            {
                ContentValues valores = new ContentValues();
                for(int i=0;i<categorias.length;i++)
                {
                    valores.put(Utilidades.ESTADO,1);
                    valores.put(Utilidades.CATEGORIA,categorias[i]);
                    db.insert(Utilidades.TABLA_PREFERENCIA,null,valores);
                }
                Log.d("------------>>>>","<------ CARGANDO BASE UNA UNICA VEZ");
            }
            else
            {
                Log.d("------------>>>>","<------ lista la configuracion");
                /*
                isAnyConfiguration = FinalConfigurationStatus();

                if (isAnyConfiguration == false) {
                    // ENABLE
                    MainActivity.menuNavigation.getMenu().getItem(0).setEnabled(false);
                    MainActivity.menuNavigation.getMenu().getItem(1).setEnabled(false);
                    MainActivity.menuNavigation.getMenu().getItem(2).setEnabled(false);
                    MainActivity.menuNavigation.getMenu().getItem(3).setEnabled(true);
                    // CHECKABLE
                    MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(false);
                    MainActivity.menuNavigation.getMenu().getItem(1).setCheckable(false);
                    MainActivity.menuNavigation.getMenu().getItem(2).setCheckable(false);
                    MainActivity.menuNavigation.getMenu().getItem(3).setCheckable(true);
                } else {
                    // ENABLE
                    MainActivity.menuNavigation.getMenu().getItem(0).setEnabled(true);
                    MainActivity.menuNavigation.getMenu().getItem(1).setEnabled(true);
                    MainActivity.menuNavigation.getMenu().getItem(2).setEnabled(true);
                    MainActivity.menuNavigation.getMenu().getItem(3).setEnabled(true);
                    // CHECKABLE
                    MainActivity.menuNavigation.getMenu().getItem(0).setCheckable(true);
                    MainActivity.menuNavigation.getMenu().getItem(1).setCheckable(true);
                    MainActivity.menuNavigation.getMenu().getItem(2).setCheckable(true);
                    MainActivity.menuNavigation.getMenu().getItem(3).setCheckable(true);
                }

                 */
            }

        db.close();
    }



    public  void AuxEstado(String cat,int mIndex){
        if (estado(cat)==0) {
            estado_siguiente = 1;
            ((MainActivity)getActivity()).stateArrayMain[mIndex] = true;

        }
        if (estado(cat) == 1) {
            estado_siguiente = 0;
            ((MainActivity)getActivity()).stateArrayMain[mIndex] = false;

        }

        ((MainActivity)getActivity()).setNotifyChangeSettings();
        //MainActivity instanceMainActivity = new MainActivity();
        //instanceMainActivity.setNotifyChangeSettings();


        actualizar(cat, estado_siguiente);
        recargar();
    }

    /**
     * Sirve para verificar cuando el usuario no seleccionó ninguna opción.
     * @return
     */
    public boolean FinalConfigurationStatus() {
        boolean flagConfiguration = false;
        int counter = 0;
        String [] categorias = {"SALUD","RETAIL","CONSTRUCCIÓN","ENTRETENIMIENTO","AMBIENTE","EDUCACIÓN","ENERGÍA","BANCA","TELECOM"};
        for (int i = 0; i < categorias.length; i++) {
            if (estado(categorias[i]) == 1) {
                counter++;
            }
        }
        if (counter > 0) {
            flagConfiguration = true;
        } else {
            flagConfiguration = false;
        }
        return flagConfiguration;
    }


    public void botones()
    {
        image_salud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuxEstado("SALUD",0);
            }
        });

        image_retail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuxEstado("RETAIL",1);
            }
        });

        image_cons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuxEstado("CONSTRUCCIÓN",2);
            }
        });

        image_entre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuxEstado("ENTRETENIMIENTO",3);
            }
        });

        image_amb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuxEstado("AMBIENTE",4);
            }
        });

        image_edu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuxEstado("EDUCACIÓN",5);

            }
        });

        image_ene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuxEstado("ENERGÍA",6);

            }
        });

        image_ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuxEstado("BANCA",7);
            }
        });

        image_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuxEstado("TELECOM",8);
            }
        });
    }

}
