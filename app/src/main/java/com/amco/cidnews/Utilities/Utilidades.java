package com.amco.cidnews.Utilities;

public class Utilidades {
    //Constates de campos
    public static final String TABLA_NOTICIA= "noticias";
    public static final String TABLA_PREFERENCIA= "preferencias";
    public static final String TABLA_NOTICIAS_TEMPORAL= "temporal";
    public static final String TABLA_RECUPERAR= "recuperar";

    public static final String TITULO="titulo";
    public static final String IMAGEN="imagen";
    public static final String URL = "url";
    public static final String AUTOR ="autor";
    public static final String CATEGORIA="categoria";
    public static final String TIEMPO = "tiempo";

    public static final String ESTADO = "estado";
    public static final String URLIMAGEN="urlimagen";
    public static final String GUARDAR = "yes";




    //TABLA DE NOTICIAS
    public static final String CREAR_TABLA_NOTICA = "CREATE TABLE "+TABLA_NOTICIA+" ("+TITULO+" TEXT," +
            " "+IMAGEN+" TEXT, " +
            ""+URL+" TEXT," +
            ""+AUTOR+" TEXT, " +
            ""+CATEGORIA+" TEXT," +
            ""+TIEMPO+" LONG)";

    //TABLA DE REFERENCIAS (CONFIGURACION DE NOTICIAS)
    public static final String CREAR_TABLA_PREFERENCIA = "CREATE TABLE "+TABLA_PREFERENCIA+" ("+CATEGORIA+" TEXT,"+ESTADO+" INT)";

    //TABLA DE RECUPERAR NOTICIAS (TRASH - BASURA)
    public static final String CREAR_TABLA_RECUPERAR = "CREATE TABLE "+TABLA_RECUPERAR+" ("+TITULO+" TEXT," +
            " "+IMAGEN+" TEXT, " +
            ""+URL+" TEXT," +
            ""+AUTOR+" TEXT, " +
            ""+CATEGORIA+" TEXT, " +
            ""+TIEMPO+" LONG)";

    public  static  final String DATABASE_TEMP = "CREATE TABLE "+TABLA_NOTICIAS_TEMPORAL+" ("+CATEGORIA+" TEXT,"+TITULO+" TEXT,"+AUTOR+" TEXT,"+URL+" TEXT,"+URLIMAGEN+" TEXT,"+GUARDAR+" INT)";

}
