package com.singlefood.sinfo.models.productos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class places {

  @SerializedName("ciudad")
  @Expose
  private String ciudad;
  @SerializedName("direccion")
  @Expose
  private String direccion;
  @SerializedName("id_user")
  @Expose
  private String idUser;
  @SerializedName("latitud")
  @Expose
  private Double latitud;
  @SerializedName("longitud")
  @Expose
  private Double longitud;

  public String getCiudad() {
    return ciudad;
  }

  public void setCiudad(String ciudad) {
    this.ciudad = ciudad;
  }

  public String getDireccion() {
    return direccion;
  }

  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }

  public Double getLatitud() {
    return latitud;
  }

  public void setLatitud(Double latitud) {
    this.latitud = latitud;
  }

  public Double getLongitud() {
    return longitud;
  }

  public void setLongitud(Double longitud) {
    this.longitud = longitud;
  }

}