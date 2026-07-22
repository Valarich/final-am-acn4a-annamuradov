package com.example.myapplication;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Habit {

    @DocumentId
    private String id;

    private String nombre;
    private String usuarioId;
    private boolean completado;

    @ServerTimestamp
    private Date creadoEn;

    public Habit() {
        // Constructor vacío requerido por Firestore
    }

    public Habit(String nombre, String usuarioId) {
        this.nombre = nombre;
        this.usuarioId = usuarioId;
        this.completado = false;
        this.creadoEn = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public Date getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Date creadoEn) {
        this.creadoEn = creadoEn;
    }
}