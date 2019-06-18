package com.amco.cidnews.Utilities;

public class Noticia {
    private String titulo;
    private String imagen;
    private String url;
    private String description;
    private String autor;
    private String categoria;
    private Long tiempo;




    public Noticia (String titulo1, String imagen1 , String url1 , String description1, String autor1, String categoria1,Long tiempo1)
    {
        this.titulo = titulo1;
        this.imagen = imagen1;
        this.url = url1;
        this.description= description1;
        this.autor= autor1;
        this.categoria=categoria1;
        this.tiempo = tiempo1;
    }

    public String getTitulo() {
        return titulo;
    }

    public Long getTiempo() {
        return tiempo;
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


    public String getCategoria() { return categoria; }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }


    public void setUrl(String url) {
        this.url = url;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setAutor(String autor) {
        this.autor = autor;
    }


    public void setCategoria(String categoria) { this.categoria = categoria; }
}
