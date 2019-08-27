
package com.singlefood.sinfo.models.productos;


public class Platillos  {
    private String imagenbase64;
    private Imagenbitmap imagenbitmap;
    private String nombrePlatillo;
    private Places places;
    private String precio;
    private String tipo;
    private ComentariosPlatillo comentariosPlatillo;


    public String getImagenbase64() {
        return imagenbase64;
    }

    public void setImagenbase64(String imagenbase64) {
        this.imagenbase64 = imagenbase64;
    }

    public Imagenbitmap getImagenbitmap() {
        return imagenbitmap;
    }

    public void setImagenbitmap(Imagenbitmap imagenbitmap) {
        this.imagenbitmap = imagenbitmap;
    }

    public String getNombrePlatillo() {
        return nombrePlatillo;
    }

    public void setNombrePlatillo(String nombrePlatillo) {
        this.nombrePlatillo = nombrePlatillo;
    }

    public Places getPlaces() {
        return places;
    }

    public void setPlaces(Places places) {
        this.places = places;
    }
    public ComentariosPlatillo getComentariosPlatillo() {
        return comentariosPlatillo;
    }

    public void setComentariosPlatillo(ComentariosPlatillo comentariosPlatillo) {
        this.comentariosPlatillo = comentariosPlatillo;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


}
