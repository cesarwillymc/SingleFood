
package com.singlefood.sinfo.models.productos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comentarios {

    @SerializedName("id_comentarios")
    @Expose
    private String idComentarios;
    @SerializedName("rating")
    @Expose
    private float rating;
    @SerializedName("texto")
    @Expose
    private String texto;

    public String getIdComentarios() {
        return idComentarios;
    }

    public void setIdComentarios(String idComentarios) {
        this.idComentarios = idComentarios;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

}
