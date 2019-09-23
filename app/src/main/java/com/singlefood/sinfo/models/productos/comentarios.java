package com.singlefood.sinfo.models.productos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class comentarios {
  @SerializedName("prioridad")
  @Expose
  private int prioridad;
  @SerializedName("id_comentarios")
  @Expose
  private String idComentarios;
  @SerializedName("rating")
  @Expose
  private float rating;
  @SerializedName("texto")
  @Expose
  private String texto;





  public int getPrioridad() {
    return prioridad;
  }

  public void setPrioridad(int prioridad) {
    this.prioridad = prioridad;
  }

  public void setRating(float rating) {
    this.rating = rating;
  }

  public String getIdComentarios() {
    return idComentarios;
  }

  public void setIdComentarios(String idComentarios) {
    this.idComentarios = idComentarios;
  }

  public float getRating() {
    return rating;
  }


  public String getTexto() {
    return texto;
  }

  public void setTexto(String texto) {
    this.texto = texto;
  }

}