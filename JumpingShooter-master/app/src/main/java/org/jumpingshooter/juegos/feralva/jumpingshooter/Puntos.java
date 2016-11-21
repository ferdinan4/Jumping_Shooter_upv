package org.jumpingshooter.juegos.feralva.jumpingshooter;

/**
 * Created by Packard Bell on 09/03/2015.
 */
public class Puntos  {
    private int puntos;
    private String nombre;

    public Puntos (int puntos, String nombre){
        this.puntos=puntos;
        this.nombre=nombre;
    }
    public Puntos (Puntos puntuacion ){
        this.puntos=puntuacion.getPuntos();
        this.nombre=puntuacion.getNombre();
    }

    public String toString(){
      String result="" +this.getNombre() + ": "+ this.getPuntos();
        return (result);
    }

    public int getPuntos() {
        return puntos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
