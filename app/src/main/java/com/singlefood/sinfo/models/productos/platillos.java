package com.singlefood.sinfo.models.productos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class platillos {

  @SerializedName("comentarios")
  @Expose
  private comentarios comentarios;
  @SerializedName("direccion")
  @Expose
  private String direccion;
  @SerializedName("idUser")
  @Expose
  private String idUser;
  @SerializedName("imagenbase64")
  @Expose
  private String imagenbase64;
  @SerializedName("nombrePlatillo")
  @Expose
  private String nombrePlatillo;
  @SerializedName("places")
  @Expose
  private places places;
  @SerializedName("precio")
  @Expose
  private String precio;
  @SerializedName("tipo")
  @Expose
  private String tipo;
  @SerializedName("comentariosCount")
  @Expose
  private int comentariosCount;
  @SerializedName("ratingPromedio")
  @Expose
  private float ratingPromedio;

  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  public void setId_user(String idUser) {
    this.idUser = idUser;
  }

  public int getComentarioscount() {
    return comentariosCount;
  }

  public void setComentarioscount(int comentariosCount) {
    this.comentariosCount = comentariosCount;
  }

  public float getRatingPromedio() {
    return ratingPromedio;
  }

  public void setRatingPromedio(float ratingPromedio) {
    this.ratingPromedio = ratingPromedio;
  }

  public String getDireccion() {
    return direccion;
  }


  public String getId_user() {
    return idUser;
  }

  public comentarios getComentarios() {
    return comentarios;
  }

  public void setComentarios(comentarios comentarios) {
    this.comentarios = comentarios;
  }

  public String getImagenbase64() {
    return imagenbase64;
  }

  public void setImagenbase64(String imagenbase64) {
    this.imagenbase64 = imagenbase64;
  }

  public String getNombrePlatillo() {
    return nombrePlatillo;
  }

  public void setNombrePlatillo(String nombrePlatillo) {
    this.nombrePlatillo = nombrePlatillo;
  }

  public places getPlaces() {
    return places;
  }

  public void setPlaces(places places) {
    this.places = places;
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