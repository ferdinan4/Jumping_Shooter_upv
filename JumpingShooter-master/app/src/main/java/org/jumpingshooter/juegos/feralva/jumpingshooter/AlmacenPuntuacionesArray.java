package org.jumpingshooter.juegos.feralva.jumpingshooter;

import java.util.Vector;

/**
 * Created by Packard Bell on 08/03/2015.
 */
public class AlmacenPuntuacionesArray implements AlmacenPuntuaciones {
    private Vector<String> puntuaciones;

    public AlmacenPuntuacionesArray(){
        puntuaciones = new Vector<String>();
    }
    @Override
    public void guardarPuntuacion(int puntos,String nombre ){
        puntuaciones.add( nombre + ": " + puntos  );
    }
    @Override
    public Vector<String> listaPuntuaciones(int Cantdad){
        return puntuaciones;
    }
}