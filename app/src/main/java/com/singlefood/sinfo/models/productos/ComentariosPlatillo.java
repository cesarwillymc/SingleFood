
package com.singlefood.sinfo.models.productos;


public class ComentariosPlatillo {
    private String id_comentarios;
    private Double rating;
    private String texto;

    public String id_comentarios() {
        return id_comentarios;
    }

    public void setIdComentarios(String id_comentarios) {
        this.id_comentarios = id_comentarios;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

}
