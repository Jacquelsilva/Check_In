package com.example.mylocation.models;

import java.util.Date;

public class RegistroCheckin {

    public enum Tipo { CHECKIN, CHECKOUT }

    private Tipo tipo;
    private Date horario;
    private double latitude;
    private double longitude;
    private String enderecoObtido;

    public RegistroCheckin(Tipo tipo, Date horario, double latitude, double longitude, String enderecoObtido) {
        this.tipo = tipo;
        this.horario = horario;
        this.latitude = latitude;
        this.longitude = longitude;
        this.enderecoObtido = enderecoObtido;
    }

    public Tipo getTipo() { return tipo; }
    public Date getHorario() { return horario; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getEnderecoObtido() { return enderecoObtido; }

    public String getCoordenadas() {
        return String.format("%.5f, %.5f", latitude, longitude);
    }
}
