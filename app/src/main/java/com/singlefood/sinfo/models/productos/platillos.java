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
  @SerializedName("id_user")
  @Expose
  private String id_user;
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


  public String getDireccion() {
    return direccion;
  }


  public String getId_user() {
    return id_user;
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