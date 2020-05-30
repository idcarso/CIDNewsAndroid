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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amco.cidnews.Activities.MainActivity;
import com.amco.cidnews.Utilities.ConexionSQLiteHelper;
import com.amco.cidnews.R;
import com.amco.cidnews.Utilities.Utilidades;

import org.jetbrains.annotations.NotNull;

/**
 * Colores en RGB para los labels referenciando los estados de las opciones:
 * - Activo = Red: 18 Green: 169 Blue: 236
 * - Inactivo = Red: 169 Green: 169: Blue: 169
 */

public class ConfigFragment extends Fragment implements ImageView.OnClickListener{

    //region VARIABLES

    private static String TAG = "ConfigFragment.java";
    private int statusOption[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String categorias[] = {"SALUD", "RETAIL", "CONSTRUCCIÓN", "ENTRETENIMIENTO", "AMBIENTE", "EDUCACIÓN", "ENERGÍA", "BANCA", "TELECOM"};
    private ConexionSQLiteHelper databaseConnection;
    private int indexHealth = 0;
    private int indexRetail = 1;
    private int indexConstruction = 2;
    private int indexEntertainment = 3;
    private int indexEnvironmnet = 4;
    private int indexEducation = 5;
    private int indexEnergy = 6;
    private int indexFinance = 7;
    private int indexTelecom = 8;
    //endregion

    //region WIDGETS

    ImageView imageViewHealth;
    ImageView imageViewRetail;
    ImageView imageViewConstruction;
    ImageView imageViewEntertainment;
    ImageView imageViewEnvironment;
    ImageView imageViewEducation;
    ImageView imageViewEnergy;
    ImageView imageViewFinance;
    ImageView imageViewTelecom;
    TextView textViewHealth;
    TextView textViewRetail;
    TextView textViewConstruction;
    TextView textViewEntertainment;
    TextView textViewEnvironment;
    TextView textViewEducation;
    TextView textViewEnergy;
    TextView textViewFinance;
    TextView textViewTelecom;

    //endregion



    ImageView image_salud,image_retail,image_cons,image_entre,image_amb,image_edu,image_ene,image_ban,image_tel;

    Button saved;

    int estado_actual,estado_siguiente;

    TextView txenvio,txhealth,txretail,txconstruc,txentertain,txeducation,txenergy,txtelecom,txfinance;

    public static boolean isAnyConfiguration = false; // TRUE: HAY AUN QUE SEA UNA OPCION CONFIGURADA || FALSE: NO SE ESCOGIO NINGUNA OPCION

    //region LIFECYCLE FRAGMENT

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.frame_config2,container,false);
        /*
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
        //config_inicial();
        //config_obtenerValores();
        //botones();

         */

        setupUI(view);
        setStatusOptionValue(statusOption);
        setStatusOptionImage(statusOption, 0);


        return view;
    }

    //endregion

    //region METHODS

    private void setupUI(@NotNull View view) {
        //ImageView
        imageViewHealth = view.findViewById(R.id.img_salud);
        imageViewRetail = view.findViewById(R.id.img_retail);
        imageViewConstruction = view.findViewById(R.id.img_cons);
        imageViewEntertainment = view.findViewById(R.id.img_entretenimiento);
        imageViewEnvironment = view.findViewById(R.id.img_ambiente);
        imageViewEducation = view.findViewById(R.id.img_educacion);
        imageViewEnergy = view.findViewById(R.id.img_energia);
        imageViewFinance = view.findViewById(R.id.img_banca);
        imageViewTelecom = view.findViewById(R.id.img_telecom);

        //TextView
        textViewHealth = view.findViewById(R.id.config_salud);
        textViewRetail = view.findViewById(R.id.config_retail);
        textViewConstruction = view.findViewById(R.id.config_construccion);
        textViewEntertainment = view.findViewById(R.id.confi_entrenimiento);
        textViewEnvironment = view.findViewById(R.id.config_ambiente);
        textViewEducation = view.findViewById(R.id.config_educacion);
        textViewEnergy = view.findViewById(R.id.config_energia);
        textViewFinance = view.findViewById(R.id.config_banca);
        textViewTelecom = view.findViewById(R.id.config_telecom);

        //Implements
        imageViewHealth.setOnClickListener(this);
        imageViewRetail.setOnClickListener(this);
        imageViewConstruction.setOnClickListener(this);
        imageViewEntertainment.setOnClickListener(this);
        imageViewEnvironment.setOnClickListener(this);
        imageViewEducation.setOnClickListener(this);
        imageViewEnergy.setOnClickListener(this);
        imageViewFinance.setOnClickListener(this);
        imageViewTelecom.setOnClickListener(this);
    }

    private void setStatusOptionValue(int statusOption[]) {
        Log.w(TAG, "setStatusOptionValue()");
        int index = 0;
        databaseConnection = new ConexionSQLiteHelper(getActivity().getApplicationContext(), "db_noticias", null, 1);
        SQLiteDatabase database = databaseConnection.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + Utilidades.TABLA_PREFERENCIA + ";", null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                statusOption[index] = cursor.getInt(1);
                index += 1;
            }
        } else {
            Log.e(TAG, "setStatusOptionValue() --> No hay registros en la tabla: " + Utilidades.TABLA_PREFERENCIA);
        }
        database.close();
        databaseConnection.close();
    }

    private void setStatusOptionImage(int statusOption[], int index) {
        Log.w(TAG, "setStatusOptionImage()");
        while (index < 9) {
            switch (index) {
                case 0:
                    if (statusOption[index] == 0) {
                        imageViewHealth.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.salud_off));
                        textViewHealth.setTextColor(Color.rgb(169, 169, 169));
                        setStatusOptionImage(statusOption, (index + 1));
                    } else if (statusOption[index] == 1) {
                        imageViewHealth.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.salud_on_));
                        textViewHealth.setTextColor(Color.rgb(18, 169, 236));
                        setStatusOptionImage(statusOption, (index + 1));
                    }
                    break;
                case 1:
                    if (statusOption[index] == 0) {
                        imageViewRetail.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.retail_off));
                        textViewRetail.setTextColor(Color.rgb(169, 169, 169));
                        setStatusOptionImage(statusOption, (index + 1));
                    } else if (statusOption[index] == 1) {
                        imageViewRetail.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.retail_on));
                        textViewRetail.setTextColor(Color.rgb(18, 169, 236));
                        setStatusOptionImage(statusOption, (index + 1));
                    }
                    break;
                case 2:
                    if (statusOption[index] == 0) {
                        imageViewConstruction.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.construccion_off));
                        textViewConstruction.setTextColor(Color.rgb(169, 169, 169));
                        setStatusOptionImage(statusOption, (index + 1));
                    } else if (statusOption[index] == 1) {
                        imageViewConstruction.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.construction_on));
                        textViewConstruction.setTextColor(Color.rgb(18, 169, 236));
                        setStatusOptionImage(statusOption, (index + 1));
                    }
                    break;
                case 3:
                    if (statusOption[index] == 0) {
                        imageViewEntertainment.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.entretenimiento_off));
                        textViewEntertainment.setTextColor(Color.rgb(169, 169, 169));
                        setStatusOptionImage(statusOption, (index + 1));
                    } else if (statusOption[index] == 1) {
                        imageViewEntertainment.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.entretenimiento_on));
                        textViewEntertainment.setTextColor(Color.rgb(18, 169, 236));
                        setStatusOptionImage(statusOption, (index + 1));
                    }
                    break;
                case 4:
                    if (statusOption[index] == 0) {
                        imageViewEnvironment.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ambiente_off));
                        textViewEnvironment.setTextColor(Color.rgb(169, 169, 169));
                        setStatusOptionImage(statusOption, (index + 1));
                    } else if (statusOption[index] == 1) {
                        imageViewEnvironment.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ambiente_on));
                        textViewEnvironment.setTextColor(Color.rgb(18, 169, 236));
                        setStatusOptionImage(statusOption, (index + 1));
                    }
                    break;
                case 5:
                    if (statusOption[index] == 0) {
                        imageViewEducation.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.educacion_off));
                        textViewEducation.setTextColor(Color.rgb(169, 169, 169));
                        setStatusOptionImage(statusOption, (index + 1));
                    } else if (statusOption[index] == 1) {
                        imageViewEducation.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.education_on));
                        textViewEducation.setTextColor(Color.rgb(18, 169, 236));
                        setStatusOptionImage(statusOption, (index + 1));
                    }
                    break;
                case 6:
                    if (statusOption[index] == 0) {
                        imageViewEnergy.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.energia_off));
                        textViewEnergy.setTextColor(Color.rgb(169, 169, 169));
                        setStatusOptionImage(statusOption, (index + 1));
                    } else if (statusOption[index] == 1) {
                        imageViewEnergy.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.energia_on));
                        textViewEnergy.setTextColor(Color.rgb(18, 169, 236));
                        setStatusOptionImage(statusOption, (index + 1));
                    }
                    break;
                case 7:
                    if (statusOption[index] == 0) {
                        imageViewFinance.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.bancaria_off));
                        textViewFinance.setTextColor(Color.rgb(169, 169, 169));
                        setStatusOptionImage(statusOption, (index + 1));
                    } else if (statusOption[index] == 1) {
                        imageViewFinance.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.bancaria_on));
                        textViewFinance.setTextColor(Color.rgb(18, 169, 236));
                        setStatusOptionImage(statusOption, (index + 1));
                    }
                    break;
                case 8:
                    if (statusOption[index] == 0) {
                        imageViewTelecom.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.telecom_off));
                        textViewTelecom.setTextColor(Color.rgb(169, 169, 169));
                        setStatusOptionImage(statusOption, (index + 1));
                    } else if (statusOption[index] == 1) {
                        imageViewTelecom.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.telecom_on));
                        textViewTelecom.setTextColor(Color.rgb(18, 169, 236));
                        setStatusOptionImage(statusOption, (index + 1));
                    }
                    break;
            }
            break;
        }
    }

    private void updateStatusOptionValue(String categoria, int currentStatus) {
        Log.i(TAG, "updateStatusOptionValue()");
        databaseConnection = new ConexionSQLiteHelper(getActivity().getApplicationContext(), "db_noticias", null, 1);
        SQLiteDatabase database = databaseConnection.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("estado", currentStatus);
        String argument[] = {categoria};
        database.update(Utilidades.TABLA_PREFERENCIA, contentValues, "categoria = ?", argument);
        database.close();
        databaseConnection.close();
    }

    private void changeStatusOptionHome(int indexCategoria, int currentStatus) {
        if (currentStatus == 0) {
            ((MainActivity)getActivity()).stateArrayMain[indexCategoria] = false;
        } else if (currentStatus == 1) {
            ((MainActivity)getActivity()).stateArrayMain[indexCategoria] = true;
        }
        ((MainActivity)getActivity()).setNotifyChangeSettings();
    }

    private boolean isActiveAnyOption(int currentOption[]) {
        boolean answer = false;
        int countActiveOption = 0;
        for (int i = 0; i < currentOption.length; i++) {
            if (currentOption[i] == 1) {
                countActiveOption += 1;
            }
        }
        if (countActiveOption == 0) {
            Log.w(TAG, "isActiveAnyOption() --> No hay ninguna opcion activa");
            return false;
        } else {
            return true;
        }
    }

    //endregion

    //region LISTENERS

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_salud:
                if (statusOption[0] == 0) {
                    imageViewHealth.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.salud_on_));
                    textViewHealth.setTextColor(Color.rgb(18, 169, 236));
                    statusOption[0] = 1;
                    updateStatusOptionValue(categorias[indexHealth], statusOption[indexHealth]);
                    changeStatusOptionHome(indexHealth, 1);
                } else if (statusOption[0] == 1) {
                    imageViewHealth.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.salud_off));
                    textViewHealth.setTextColor(Color.rgb(169, 169, 169));
                    statusOption[0] = 0;
                    updateStatusOptionValue(categorias[indexHealth], statusOption[indexHealth]);
                    changeStatusOptionHome(indexHealth, 0);
                }
                break;
            case R.id.img_retail:
                if (statusOption[1] == 0) {
                    imageViewRetail.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.retail_on));
                    textViewRetail.setTextColor(Color.rgb(18, 169, 236));
                    statusOption[1] = 1;
                    updateStatusOptionValue(categorias[indexRetail], statusOption[indexRetail]);
                    changeStatusOptionHome(indexRetail, 1);
                } else if (statusOption[1] == 1) {
                    imageViewRetail.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.retail_off));
                    textViewRetail.setTextColor(Color.rgb(169, 169, 169));
                    statusOption[1] = 0;
                    updateStatusOptionValue(categorias[indexRetail], statusOption[indexRetail]);
                    changeStatusOptionHome(indexRetail, 0);
                }
                break;
            case R.id.img_cons:
                if (statusOption[2] == 0) {
                    imageViewConstruction.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.construction_on));
                    textViewConstruction.setTextColor(Color.rgb(18, 169, 236));
                    statusOption[2] = 1;
                    updateStatusOptionValue(categorias[indexConstruction], statusOption[indexConstruction]);
                    changeStatusOptionHome(indexConstruction, 1);
                } else if (statusOption[2] == 1) {
                    imageViewConstruction.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.construccion_off));
                    textViewConstruction.setTextColor(Color.rgb(169, 169, 169));
                    statusOption[2] = 0;
                    updateStatusOptionValue(categorias[indexConstruction], statusOption[indexConstruction]);
                    changeStatusOptionHome(indexConstruction, 0);
                }
                break;
            case R.id.img_entretenimiento:
                if (statusOption[3] == 0) {
                    imageViewEntertainment.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.entretenimiento_on));
                    textViewEntertainment.setTextColor(Color.rgb(18, 169, 236));
                    statusOption[3] = 1;
                    updateStatusOptionValue(categorias[indexEntertainment], statusOption[indexEntertainment]);
                    changeStatusOptionHome(indexEntertainment, 1);
                } else if (statusOption[3] == 1) {
                    imageViewEntertainment.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.entretenimiento_off));
                    textViewEntertainment.setTextColor(Color.rgb(169, 169, 169));
                    statusOption[3] = 0;
                    updateStatusOptionValue(categorias[indexEntertainment], statusOption[indexEntertainment]);
                    changeStatusOptionHome(indexEntertainment, 0);
                }
                break;
            case R.id.img_ambiente:
                if (statusOption[4] == 0) {
                    imageViewEnvironment.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ambiente_on));
                    textViewEnvironment.setTextColor(Color.rgb(18, 169, 236));
                    statusOption[4] = 1;
                    updateStatusOptionValue(categorias[indexEnvironmnet], statusOption[indexEnvironmnet]);
                    changeStatusOptionHome(indexEnvironmnet, 1);
                } else if (statusOption[4] == 1) {
                    imageViewEnvironment.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ambiente_off));
                    textViewEnvironment.setTextColor(Color.rgb(169, 169, 169));
                    statusOption[4] = 0;
                    updateStatusOptionValue(categorias[indexEnvironmnet], statusOption[indexEnvironmnet]);
                    changeStatusOptionHome(indexEnvironmnet, 0);
                }
                break;
            case R.id.img_educacion:
                if (statusOption[5] == 0) {
                    imageViewEducation.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.education_on));
                    textViewEducation.setTextColor(Color.rgb(18, 169, 236));
                    statusOption[5] = 1;
                    updateStatusOptionValue(categorias[indexEducation], statusOption[indexEducation]);
                    changeStatusOptionHome(indexEducation, 1);
                } else if (statusOption[5] == 1) {
                    imageViewEducation.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.educacion_off));
                    textViewEducation.setTextColor(Color.rgb(169, 169, 169));
                    statusOption[5] = 0;
                    updateStatusOptionValue(categorias[indexEducation], statusOption[indexEducation]);
                    changeStatusOptionHome(indexEducation, 0);
                }
                break;
            case R.id.img_energia:
                if (statusOption[6] == 0) {
                    imageViewEnergy.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.energia_on));
                    textViewEnergy.setTextColor(Color.rgb(18, 169, 236));
                    statusOption[6] = 1;
                    updateStatusOptionValue(categorias[indexEnergy], statusOption[indexEnergy]);
                    changeStatusOptionHome(indexEnergy, 1);
                } else if (statusOption[6] == 1) {
                    imageViewEnergy.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.energia_off));
                    textViewEnergy.setTextColor(Color.rgb(169, 169, 169));
                    statusOption[6] = 0;
                    updateStatusOptionValue(categorias[indexEnergy], statusOption[indexEnergy]);
                    changeStatusOptionHome(indexEnergy, 0);
                }
                break;
            case R.id.img_banca:
                if (statusOption[7] == 0) {
                    imageViewFinance.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.bancaria_on));
                    textViewFinance.setTextColor(Color.rgb(18, 169, 236));
                    statusOption[7] = 1;
                    updateStatusOptionValue(categorias[indexFinance], statusOption[indexFinance]);
                    changeStatusOptionHome(indexFinance, 1);
                } else if (statusOption[7] == 1) {
                    imageViewFinance.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.bancaria_off));
                    textViewFinance.setTextColor(Color.rgb(169, 169, 169));
                    statusOption[7] = 0;
                    updateStatusOptionValue(categorias[indexFinance], statusOption[indexFinance]);
                    changeStatusOptionHome(indexFinance, 0);
                }
                break;
            case R.id.img_telecom:
                if (statusOption[8] == 0) {
                    imageViewTelecom.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.telecom_on));
                    textViewTelecom.setTextColor(Color.rgb(18, 169, 236));
                    statusOption[8] = 1;
                    updateStatusOptionValue(categorias[indexTelecom], statusOption[indexTelecom]);
                    changeStatusOptionHome(indexTelecom, 1);
                } else if (statusOption[8] == 1) {
                    imageViewTelecom.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.telecom_off));
                    textViewTelecom.setTextColor(Color.rgb(169, 169, 169));
                    statusOption[8] = 0;
                    updateStatusOptionValue(categorias[indexTelecom], statusOption[indexTelecom]);
                    changeStatusOptionHome(indexTelecom, 0);
                }
                break;
        }
    }


    //endregion





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
        switch (categoria) {
            case "SALUD":
                if(estado==1)  image_salud.setImageResource(R.drawable.salud_on_);
                else {
                    image_salud.setImageResource(R.drawable.salud_off);
                    txhealth.setTextColor(Color.rgb(169,169,169));
                }  //DPS? 03 -Octubre
                break;
            case "RETAIL":
                if(estado==1)  image_retail.setImageResource(R.drawable.retail_on);
                else {
                    image_retail.setImageResource(R.drawable.retail_off);
                    txretail.setTextColor(Color.rgb(169,169,169));
                }
                break;
            case "CONSTRUCCIÓN":
                if(estado==1)  image_cons.setImageResource(R.drawable.construction_on);
                else {
                    image_cons.setImageResource(R.drawable.construccion_off);
                    txconstruc.setTextColor(Color.rgb(169,169,169));
                }
                break;
            case "ENTRETENIMIENTO":
                if(estado==1)  image_entre.setImageResource(R.drawable.entretenimiento_on);
                else {
                    image_entre.setImageResource(R.drawable.entretenimiento_off);
                    txentertain.setTextColor(Color.rgb(169,169,169));
                }
                break;
            case "AMBIENTE":
                if(estado==1)  image_amb.setImageResource(R.drawable.ambiente_on);
                else {
                    image_amb.setImageResource(R.drawable.ambiente_off);
                    txenvio.setTextColor(Color.rgb(169,169,169));
                }
                break;
            case "EDUCACIÓN":
                if(estado==1)  image_edu.setImageResource(R.drawable.education_on);
                else {
                    image_edu.setImageResource(R.drawable.educacion_off);
                    txeducation.setTextColor(Color.rgb(169,169,169));
                }
                break;
            case "ENERGÍA":
                if(estado==1)  image_ene.setImageResource(R.drawable.energia_on);
                else {
                    image_ene.setImageResource(R.drawable.energia_off);
                    txenergy.setTextColor(Color.rgb(169,169,169));
                }
                break;
            case "BANCA":
                if(estado==1)  image_ban.setImageResource(R.drawable.bancaria_on);
                else {
                    image_ban.setImageResource(R.drawable.bancaria_off);
                    txfinance.setTextColor(Color.rgb(169,169,169));
                }
                break;
            case "TELECOM":
                if(estado==1)  image_tel.setImageResource(R.drawable.telecom_on);
                else {
                    image_tel.setImageResource(R.drawable.telecom_off);
                    txtelecom.setTextColor(Color.rgb(169,169,169));
                }
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
