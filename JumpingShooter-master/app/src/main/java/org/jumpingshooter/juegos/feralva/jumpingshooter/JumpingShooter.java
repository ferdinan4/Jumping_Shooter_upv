package org.jumpingshooter.juegos.feralva.jumpingshooter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;



public class JumpingShooter extends ActionBarActivity {
    double puntuacion=0;
   
    private MediaPlayer mp;
   public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionesArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jumping_shooter);
        //faltaria conicion para iniciar

    }

    public void lanzarJuego(View view) {
        Intent i = new Intent(this, Juego.class);
        startActivityForResult(i, 1234);
    }
    @Override protected void onActivityResult(int requestCode,int resultCode, Intent data){

    
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234 && resultCode == RESULT_OK && data != null) {

            puntuacion = data.getExtras().getDouble("puntuacion");
            lanzarNombreJugador(null);

            //Mejor leerlo desde un AlertDialog.Builder

        }
        if (requestCode == 1233 && resultCode == RESULT_OK && data != null) {

             String nombre = data.getExtras().getString("nombre") ;
            //Mejor leerlo desde un AlertDialog.Builder
            int d = (int) Math.ceil(puntuacion);
            almacen.guardarPuntuacion(d, nombre);
            lanzarPuntuaciones(null);

        }
    }

    public void lanzarAyuda(View view) {
        Intent i = new Intent(this, Ayuda.class);
        startActivity(i);
    }

    public void lanzarPreferencias(View view) {
        Intent i = new Intent(this, Preferencias.class);
        startActivity(i);
    }

    public void lanzarPuntuaciones(View view) {
        Intent i = new Intent(this, Puntuaciones.class);
        startActivity(i);
    }

    public void lanzarNombreJugador(View view) {
        Intent i = new Intent(this, NombreJugador.class);
        startActivityForResult(i, 1233);

    }


}


