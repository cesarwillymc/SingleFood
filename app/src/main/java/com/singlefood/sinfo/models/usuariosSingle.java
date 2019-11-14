package com.singlefood.sinfo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class usuariosSingle {
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("imagen")
    @Expose
    private String imagen;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("phone")
    @Expose
    private String celular;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }
}
