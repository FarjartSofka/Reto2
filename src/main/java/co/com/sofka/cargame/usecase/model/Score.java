package co.com.sofka.cargame.usecase.model;

import java.util.Date;

public class Score {

    private String jugadorId;
    private String nombre;
    private Integer puntaje;


    public Score() {
    }

    public Score(String jugadorId, String nombre, Integer puntaje) {
        this.jugadorId = jugadorId;
        this.nombre = nombre;
        this.puntaje = puntaje;
    }


    public String getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(String jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public Integer getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Integer puntaje) {
        this.puntaje = puntaje;
    }

    @Override
    public String toString() {
        return "Score{" +
                "jugadorId='" + jugadorId + '\'' +
                ", nombre='" + nombre + '\'' +
                ", puntaje=" + puntaje +
                '}';
    }
}
