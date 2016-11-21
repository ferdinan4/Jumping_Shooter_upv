package org.jumpingshooter.juegos.feralva.jumpingshooter;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * Created by Packard Bell on 08/03/2015.
 */
public class Puntuaciones extends ListActivity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puntuaciones);
        setListAdapter(
                new ArrayAdapter<String>(this,
                        R.layout.elemento_lista,
                        R.id.titulo,
                        JumpingShooter.almacen.listaPuntuaciones(10)));
    }
}
