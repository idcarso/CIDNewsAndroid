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

    //region LIFECYCLE FRAGMENT

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.frame_config2,container,false);

        //Configuracion de la UI
        setupUI(view);

        //Valores desde la BD
        setStatusOptionValue(statusOption);

        //Estado de la vista
        setStatusOptionImage(statusOption, 0);

        return view;
    }

    //endregion

    //region METHODS

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 01 / 06 / 2020</b></h2></p>
     * <br>
     *     Método que realiza la configuraciones de la UI. Incluye las implementaciones de los listeners para Imagieview.
     * @param view Objeto de tipo View que se alimenta desde onCreate para acceder a los widgets y enlazar las vistas.
     */
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

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 01 / 06 / 2020</b></h2></p>
     * <br>
     *     Método que asigna los valores desde la BD al arreglo estatico que controla o indica el estado de las opciones.
     * @param statusOption Array de Int que ya esta declarado y solo lo alimenta.
     */
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

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 01 / 06 / 2020</b></h2></p>
     * <br>
     *     Método que asigna el estado a los Imageview para colocar activo o inactivo segun sea el caso.
     * @param statusOption Array de Int que tiene los valores de los estados para modificar la vista.
     * @param index Valor de tipo int que sirve como index para saber la posicion de la opción y como bandera para terminar la recursividad.
     */
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

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 01 / 06 / 2020</b></h2></p>
     * <br>
     *     Método que actualiza la BD con la nueva configuracion realizada por el usuario.
     * @param categoria
     * @param currentStatus
     */
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

    /**
     * <p><h2><b>Created by Alejandro Jiménez on 01 / 06 / 2020</b></h2></p>
     * <br>
     *     Método que actualiza el array estatico en MainActivity.java para la busqueda de noticias cuando el estado cambie o no.
     * @param indexCategoria Valor de tipo int para saber la posicion de la categoria deseada.
     * @param currentStatus Valor de tipo int para asignar true/false en el array.
     */
    private void changeStatusOptionHome(int indexCategoria, int currentStatus) {
        if (currentStatus == 0) {
            ((MainActivity)getActivity()).stateArrayMain[indexCategoria] = false;
        } else if (currentStatus == 1) {
            ((MainActivity)getActivity()).stateArrayMain[indexCategoria] = true;
        }
        ((MainActivity)getActivity()).setNotifyChangeSettings();
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
    
}
