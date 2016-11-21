package org.jumpingshooter.juegos.feralva.jumpingshooter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Packard Bell on 09/03/2015.
 */
public class NombreJugador extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nombrepuntuaciones);
    }

    public void LeerTextoySalir (View view){
        EditText mEdit = (EditText)findViewById(R.id.nombrejugador);
        String nombreCompleto =  mEdit.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("nombre", nombreCompleto);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        this.setResult(Activity.RESULT_OK , intent);
        this.finish();
    }
}
