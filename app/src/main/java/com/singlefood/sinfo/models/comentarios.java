package com.singlefood.sinfo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class comentarios {
  @SerializedName("prioridad")
  @Expose
  private int prioridad;
  @SerializedName("idComentarios")
  @Expose
  private String idComentarios;
  @SerializedName("rating")
  @Expose
  private float rating;
  @SerializedName("texto")
  @Expose
  private String texto;
  @SerializedName("fecha")
  @Expose
  private String fecha;
  @SerializedName("user")
  @Expose
  private String user;
  @SerializedName("hora")
  @Expose
  private String hora;

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getFecha() {
    return fecha;
  }

  public void setFecha(String fecha) {
    this.fecha = fecha;
  }

  public String getHora() {
    return hora;
  }

  public void setHora(String hora) {
    this.hora = hora;
  }







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