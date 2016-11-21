package org.jumpingshooter.juegos.feralva.jumpingshooter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

    /**
     * Created by Alvaro on 22/02/2015.
     */
    public class Juego extends Activity {
        private VistaJuego vistaJuego;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.juego);
            vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
            vistaJuego.setPadre(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            vistaJuego.getThread().reanudar();
        }
        @Override
        public void onPause() {
            super.onPause();
            vistaJuego.getThread().pausar();
        }
        @Override
        public void onDestroy() {
            super.onResume();
            vistaJuego.getThread().detener();
            super.onDestroy();

        }
    }

