package org.jumpingshooter.juegos.feralva.jumpingshooter;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.Vector;



public class VistaJuego extends View {

    //truco para generar la respuesta a las puntuaciones desde aquí en vistaJuego
    private  Activity padre;
    public  void setPadre (Activity padre){
        this.padre = padre;
    }

    ////// ROCA //////
    private Vector<Grafico> Rocas; // Vector con los Rocas del juego
    ////// MONIGOTE //////
    private Grafico monigote;
    private Grafico misil;
    // variable booleana que permite saber si el monigote está saltando
    private boolean salto = false;
    private boolean misilActivo = false;
    //Variables que he añadido para la dificultad y para la reproduccion de la musica
    private MediaPlayer mp;

    private int valor = 3;
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;
    //Vector de flags que indican si las rocas del vector están activas
    private boolean[] rocasactivas = new boolean[8];
    //Distancia que define el espacio mínimo que debe de haber entre rocas
    private double distancia_minima;

    /*Variable donde almacenaremos los puntos de cada partida*/
    private double puntos=0;

    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drawableRoca, drawableMonigote, drawableZombie, drawableZombie2, drawableZombie3, drawableZombie4, drawableMisil;
        drawableMisil = context.getResources().getDrawable(R.drawable.misil);//Falta cambiarlo para que sea la balaa
        drawableMonigote = context.getResources().getDrawable(R.drawable.monigoteapp);
        drawableRoca = context.getResources().getDrawable(R.drawable.roca);
        drawableZombie = context.getResources().getDrawable(R.drawable.zombiecubo);
        drawableZombie2 = context.getResources().getDrawable(R.drawable.zombirugby);
        drawableZombie3 = context.getResources().getDrawable(R.drawable.zombimega);
        drawableZombie4 = context.getResources().getDrawable(R.drawable.zombienazi);


        SharedPreferences pref = context.getSharedPreferences("org.jumpingshooter.juegos.feralva.jumpingshooter_preferences", Context.MODE_PRIVATE);
        if (pref.getString("dificultad", "0").equals("0")) { //para dificultad facil
            valor = 0;

        }
        if (pref.getString("dificultad", "1").equals("1")) { //para dificultad medio
            valor = 5;
        }

        if (pref.getString("dificultad", "2").equals("2")) { //para dificultad dificil
            valor = 10;

        }

        mp = MediaPlayer.create(context, R.raw.musica);

        if( pref.getBoolean("musica", true) == true )
        {
            mp.start();
        }



        puntos = 0.0;
        monigote = new Grafico(this, drawableMonigote);
        misil = new Grafico(this, drawableMisil);
        Rocas = new Vector<Grafico>(8);
        //Añadimos 8 objetos roca al vector
        for (int i = 0; i < 8; i++) {
            if (i <= 3) {
                Grafico roca = new Grafico(this, drawableRoca);
                roca.setIncX(-0.8 * roca.MAX_VELOCIDAD - (valor));
                Rocas.add(roca);
            }
            if (i == 4) {
                Grafico zombie = new Grafico(this, drawableZombie);
                zombie.setIncX(-0.8 * zombie.MAX_VELOCIDAD - (valor));
                Rocas.add(zombie);
            }
            if (i == 5) {
                Grafico zombie2 = new Grafico(this, drawableZombie2);
                zombie2.setIncX(-0.8 * zombie2.MAX_VELOCIDAD - (valor));
                Rocas.add(zombie2);

            }
            if (i == 6) {
                Grafico zombie3 = new Grafico(this, drawableZombie3);
                zombie3.setIncX(-0.8 * zombie3.MAX_VELOCIDAD - (valor));
                Rocas.add(zombie3);
            }
            if (i == 7) {
                Grafico zombie4 = new Grafico(this, drawableZombie4);
                zombie4.setIncX(-0.8 * zombie4.MAX_VELOCIDAD - (valor));
                Rocas.add(zombie4);
            }

        }

    }

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        // Una vez que conocemos nuestro ancho y alto.
        monigote.setPosX((monigote.getAncho()) / 2);
        monigote.setPosY(alto - (monigote.getAlto() * 1.2));
        int i = 0;
        //Establecemos el ancho y alto de todas las rocas del vector
        for (Grafico roca : Rocas) {

            roca.setPosX(ancho - roca.getAncho());
            if (i <= 3) {
                roca.setPosY(alto - (roca.getAlto() * 1.2));
            } else {
                roca.setPosY(alto - (roca.getAlto() * 1));
            }
            i++;
        }
        ultimoProceso = System.currentTimeMillis();
        thread.start();

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        monigote.dibujaGrafico(canvas);
        //Variable que sirve como indice del vector de rocas para saber que roca estamos en
        // el for y asociarla con si flag del vector de rocasactivas
        int count = 0;
        //Dibujamos las rocas que tengan su flag de activo.
        for (Grafico roca : Rocas) {

            if (rocasactivas[count]) {
                roca.dibujaGrafico(canvas);
            }
            count++;
        }
        if (misilActivo) {
            misil.dibujaGrafico(canvas);
        }
    }

    synchronized protected void actualizaFisica() {
        //Variable que sirve como indice del vector de rocas para saber que roca estamos en
        // el for y asociarla con si flag del vector de rocasactivas
        int count = 0;
        //Variable que nos dice si se cumple las condiciones necesarias para dibujar una nueva roca
        boolean flag = true;
        long ahora = System.currentTimeMillis();
        /*Si el tiempo que ha pasado es menor que el Periodo Proceso no hace nada*/
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;
        }
        puntos = puntos + 0.05;
        // Para una ejecución en tiempo real calculamos retardo
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez
        //Si la variable salto está activa significa que el monigote ha de moverse ya sea hacia arriba
        // o hacia abajo dependiendo de la posición donde se encuentre
        if (salto) {
            monigote.incrementaPos(retardo);
            if (monigote.getPosY() < (getHeight() - (monigote.getAlto() * 2.7))) {
                salto = false;
                //Cuando llegamos a la posición máxima llamamos al metodo descenso
                descenso();
            }
            if (monigote.getPosY() > (getHeight() - (monigote.getAlto() * 1.2))) {
                // Al llegar a la posición inicial después de saltar decimos que el salto es false
                salto = false;
            }


        }

        if (misilActivo) {
            misil.incrementaPos(retardo);
            if (misil.getPosX() > (getWidth() - (monigote.getAlto()))) {
                misilActivo = false;
            } else {
                for (int i = 4; i < Rocas.size(); i++)
                    if (misil.verificaDisparo(Rocas.elementAt(i))) {
                        DesactivaRoca(i);
                        misilActivo = false;
                        puntos=puntos+5;
                        break;
                    }
            }
        }

        //En este for haremos el movimiento de cada roca
        for (Grafico roca : Rocas) {
            //La distancia mínima entre rocas debe de ser 5.5 veces el ancho de la roca para que el
            //monigote pueda subir y bajar.
            distancia_minima = getWidth() - 5.5 * roca.getAncho();
            //Si la roca llega al final se desactiva.
            if (roca.getPosX() + roca.getAncho() / 2 < 0) {
                DesactivaRoca(count);

            }
            //Si la roca no ha sido desactivada debe actualizarse su posicion
            if (rocasactivas[count]) {
                roca.incrementaPos(retardo);
            }
            //Suponemos en principio que la roca se puede dibujar y hemos de comprobar si se cumplen
            //todas las condiciones
            flag = true;
          /*En primer lugar para dibujar una roca esta ha de estar desactivada, y hacemos que esto
          lo calcule cada tiempo aleatorio
          */
            if (!rocasactivas[count] && Math.random() < 0.017) {
                //Si entramos aquí suponemos que tya se puede dibujar la roca y la activamos
                rocasactivas[count] = true;
                /*Pero hemos de comprobar que el resto de rocas activas no estén cerca de ahi dos
                bucles for para excluir la roca que acabamos de activar para dibujar
                */
                for (int e = 0; e < count; e++) {
                    double posx = (int) Rocas.get(e).getPosX();
                    /*Si la posicion de las otras rocas es menor a la distancia minima no podremos
                    dibujar la roca
                    */
                    if ((posx > distancia_minima) && rocasactivas[e]) {
                        flag = false;
                        rocasactivas[count] = false;
                    }
                }
                //idem for anterior
                for (int e = count + 1; e < Rocas.capacity(); e++) {
                    double posx = (int) Rocas.get(e).getPosX();

                    if ((posx > distancia_minima) && rocasactivas[e]) {
                        flag = false;
                        rocasactivas[count] = false;
                    }
                }
                /*Al final del proceso si flag está a true esq se cumplirarn todas las condiciones
                 * para poder dibujar la roca
                */
                if (flag) {
                    rocasactivas[count] = true;
                }

            }
            count++;

            for (int i = 0; i < Rocas.size(); i++)
                if (monigote.verificaColision(Rocas.elementAt(i))) {
                    AcabaJuego();
                    break;
                }
        }


    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            /*Al pulsar la pantalla activamos el proceso salto*/
            case MotionEvent.ACTION_UP: {
                if (!salto && !misilActivo) {
                    if (x < getWidth() / 2) {
                        saltar();
                        salto = true;
                    }
                    if (x > getWidth() / 2) {
                        disparar();
                        misilActivo = true;
                    }

                }
                break;
            }
        }
        return true;

    }

    class ThreadJuego extends Thread {
        private boolean pausa;
        private boolean corriendo;
        public synchronized void pausar(){
            pausa=true;
        }
        public synchronized void reanudar(){
            pausa=false;
            notify();

        }

        public void detener(){
            corriendo=false;
            if(pausa){
                reanudar();
            }
        }
        @Override
        public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this){
                    while(pausa){
                        try{
                            wait();

                        }catch (Exception e){

                        }
                    }
                }
            }
        }
    }

    // metodo saltar activa la condición de disparo
    private void disparar() {
        misil.setIncX(40); // VELOCIDAD DE LA BALA
        misil.setPosX(1.5 * (monigote.getAncho()));
        misil.setPosY(getHeight() - (monigote.getAlto() / 1.5));
        misilActivo = true;
    }


    // metodo saltar activa la condición de salto y la velocidad del monigote para ir hacia arriba
    private void saltar() {
        monigote.setIncY(-40 );
        salto = true;
    }

    /* el metodo descenso es activado una vez el monigote ha llegado a la maxima altura en su salto
    * entonces la velocidad del monigote se pone para ir hacia abajo y se situa un punto mas a bajo
    * de la posición máxima
    */
    private void descenso() {
        monigote.setIncY(15+valor );
        monigote.setPosY(getHeight() - (monigote.getAlto() * 2.7) - 1);
        salto = true;
    }

    /*El metodo desactiva roca es llamado cuando una roca sale de pantalla y lo que hace es desactivar
    * el flag correspondiente a esa roca y situar la roca desactivada en la posición inical para que
    * esté preparada para cuando sea activada
    */
    private void DesactivaRoca(int i) {

        rocasactivas[i] = false;
        Rocas.get(i).setPosX(getWidth() - Rocas.get(i).getAncho());
        if(i>=4){
            Rocas.get(i).setPosY(getHeight() - Rocas.get(i).getAlto() * 1);
        }
        else {
            Rocas.get(i).setPosY(getHeight() - Rocas.get(i).getAlto() * 1.2);
        }

    }

    private void AcabaJuego() {
        mp.pause();

        Bundle bundle = new Bundle();
        bundle.putDouble("puntuacion",puntos);
        puntos = 0;
        Intent intent = new Intent();
        intent.putExtras(bundle);
        padre.setResult(Activity.RESULT_OK , intent);
        //thread.stop();
        padre.finish();
    }

    public ThreadJuego getThread() {
        return thread;
    }
}