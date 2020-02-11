package com.amco.cidnews.Utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class MyObject implements Parcelable {
    private String titulo;
    private String imagen;
    private String url;
    private String autor;
    private String categoria;


    public MyObject(String titulo, String imagen, String url, String autor, String categoria) {
        this.titulo = titulo;
        this.imagen = imagen;
        this.url = url;
        this.autor = autor;
        this.categoria = categoria;
    }

    private MyObject(Parcel in) {
        titulo = in.readString();
        imagen = in.readString();
        url = in.readString();
        autor = in.readString();
        categoria = in.readString();
    }

    public int describeContents() {
        return 0;
    }



    public void writeToParcel(Parcel out, int flags) {
        out.writeString(titulo);
        out.writeString(imagen);
        out.writeString(url);
        out.writeString(autor);
        out.writeString(categoria);
    }

    public static final Creator<MyObject> CREATOR = new Creator<MyObject>() {
        public MyObject createFromParcel(Parcel in) {
            return new MyObject(in);
        }

        public MyObject[] newArray(int size) {
            return new MyObject[size];
        }
    };

    public String getTitulo() {
        return titulo;
    }

    public String getImagen() {
        return imagen;
    }

    public String getUrl() {
        return url;
    }

    public String getAutor() {
        return autor;
    }


    public String getCategoria(){
        return categoria;
    }
}