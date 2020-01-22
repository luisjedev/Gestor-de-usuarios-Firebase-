package com.example.loggin;

import android.net.Uri;

public class Usuario {

    private String id,nombre,contraseña,email,fecha_creacion;
    private String tipo;
    private Uri url_heroe;

    public Usuario() {

        this.fecha_creacion="";
        this.tipo="normal";
        this.id="";
        this.nombre = "";
        this.contraseña = "";
        this.email = "";
        this.url_heroe = null;
    }

    //REGISTRO
    public Usuario(String nombre, String contraseña, String email, String fecha_creacion) {

        this.fecha_creacion=fecha_creacion;
        this.tipo="normal";
        this.id = "";
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.email = email;
        this.url_heroe = null;
    }


    //GETTERS
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getEmail() {
        return email;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public Uri getUrl_heroe() {
        return url_heroe;
    }

    public String getTipo() {
        return tipo;
    }

    //SETTERS
    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public void setUrl_heroe(Uri url_heroe) {
        this.url_heroe = url_heroe;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
