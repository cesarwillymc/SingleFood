package com.singlefood.sinfo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class places {


  @SerializedName("idUser")
  @Expose
  private String idUser;
  @SerializedName("latitud")
  @Expose
  private Double latitud;
  @SerializedName("longitud")
  @Expose
  private Double longitud;


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