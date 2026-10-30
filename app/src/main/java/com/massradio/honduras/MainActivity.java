
package com.massradio.honduras;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView estado;
    private TextView botonPlay;
    private boolean reproduciendo = false;
    private AudioManager audio;

    private final BroadcastReceiver receptor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String mensaje = intent.getStringExtra("status");
            if (mensaje == null) return;

            estado.setText(mensaje);

            if (mensaje.equals("Reproduciendo en vivo")) {
                reproduciendo = true;
                actualizarBoton();
            } else if (mensaje.equals("Reproducción detenida")) {
                reproduciendo = false;
                actualizarBoton();
            }
        }
    };

    private GradientDrawable fondo(int color, int radio) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radio);
        return d;
    }

    private TextView texto(String valor, int tamano, int color) {
        TextView t = new TextView(this);
        t.setText(valor);
        t.setTextSize(tamano);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        return t;
    }

    private void comando(String accion) {
        Intent i = new Intent(this, RadioService.class);
        i.setAction(accion);

        if (RadioService.PLAY.equals(accion)) {
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(i);
            } else {
                startService(i);
            }
        } else {
            startService(i);
        }
    }

    private void actualizarBoton() {
        botonPlay.setText(reproduciendo ? "Ⅱ" : "▶");
    }

    private void abrir(String direccion) {
        try {
            startActivity(new Intent(
                    Intent.ACTION_VIEW, Uri.parse(direccion)));
        } catch (Exception e) {
            estado.setText("No se pudo abrir el enlace");
        }
    }

    private void espacio(LinearLayout contenedor, int alto) {
        View v = new View(this);
        contenedor.addView(v,
                new LinearLayout.LayoutParams(1, alto));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        audio = (AudioManager) getSystemService(AUDIO_SERVICE);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.BLACK);

        LinearLayout pagina = new LinearLayout(this);
        pagina.setOrientation(LinearLayout.VERTICAL);
        pagina.setGravity(Gravity.CENTER_HORIZONTAL);
        pagina.setPadding(24, 18, 24, 30);

        scroll.addView(pagina);
        setContentView(scroll);

        TextView titulo = texto(
                "MassRadio", 29, Color.WHITE);
        titulo.setTypeface(null, Typeface.BOLD);
        pagina.addView(titulo);

        espacio(pagina, 35);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_massradio);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setBackground(fondo(Color.WHITE, 24));
        logo.setPadding(12, 12, 12, 12);

        LinearLayout.LayoutParams lpLogo =
                new LinearLayout.LayoutParams(270, 270);
        lpLogo.gravity = Gravity.CENTER;
        pagina.addView(logo, lpLogo);

        espacio(pagina, 30);

        TextView lema = texto(
                "Donde quieres estar siempre",
                16, 0xFFBBBBBB);
        pagina.addView(lema);

        espacio(pagina, 15);

        estado = texto(
                "Listo para escuchar",
                18, Color.WHITE);
        estado.setTypeface(null, Typeface.BOLD);
        pagina.addView(estado);

        espacio(pagina, 35);

        LinearLayout filaVolumen = new LinearLayout(this);
        filaVolumen.setGravity(Gravity.CENTER_VERTICAL);
        filaVolumen.setOrientation(LinearLayout.HORIZONTAL);

        TextView menos = texto("◀", 20, Color.WHITE);
        filaVolumen.addView(menos);

        SeekBar volumen = new SeekBar(this);
        volumen.setMax(audio.getStreamMaxVolume(
                AudioManager.STREAM_MUSIC));
        volumen.setProgress(audio.getStreamVolume(
                AudioManager.STREAM_MUSIC));

        volumen.setProgressTintList(
                android.content.res.ColorStateList.valueOf(
                        0xFF1877F2));
        volumen.setThumbTintList(
                android.content.res.ColorStateList.valueOf(
                        0xFF1877F2));

        filaVolumen.addView(volumen,
                new LinearLayout.LayoutParams(0, 55, 1));

        TextView mas = texto("♪", 25, Color.WHITE);
        filaVolumen.addView(mas);

        volumen.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(
                    SeekBar bar, int valor, boolean usuario) {
                if (usuario) {
                    audio.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            valor, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {}

            @Override
            public void onStopTrackingTouch(SeekBar bar) {}
        });

        pagina.addView(filaVolumen);

        espacio(pagina, 30);

        LinearLayout redes = new LinearLayout(this);
        redes.setGravity(Gravity.CENTER);
        redes.setOrientation(LinearLayout.HORIZONTAL);

        String[] nombres = {
                "f", "𝕏", "◎", "✆", "▶", "🌐"
        };

        String[] enlaces = {
                "https://www.facebook.com/massradiohn",
                "https://x.com/Mass_Radiohn",
                "https://www.instagram.com/massradiohn/",
                "https://wa.me/",
                "https://www.youtube.com/@MassRadiohn",
                "https://massradioh.com"
        };

        int[] colores = {
                0xFF1877F2, 0xFF222222,
                0xFFC13584, 0xFF25D366,
                0xFFFF0000, 0xFF1685D9
        };

        for (int n = 0; n < nombres.length; n++) {
            final String enlace = enlaces[n];

            TextView red = texto(
                    nombres[n], 21, Color.WHITE);
            red.setTypeface(null, Typeface.BOLD);
            red.setBackground(fondo(colores[n], 100));

            LinearLayout.LayoutParams rp =
                    new LinearLayout.LayoutParams(48, 48);
            rp.setMargins(4, 0, 4, 0);

            redes.addView(red, rp);
            red.setOnClickListener(v -> abrir(enlace));
        }

        pagina.addView(redes);

        espacio(pagina, 45);

        botonPlay = texto("▶", 48, Color.WHITE);
        botonPlay.setBackground(
                fondo(0xFF1469F5, 200));

        LinearLayout.LayoutParams bp =
                new LinearLayout.LayoutParams(112, 112);
        bp.gravity = Gravity.CENTER;
        pagina.addView(botonPlay, bp);

        botonPlay.setOnClickListener(v -> {
            if (reproduciendo) {
                comando(RadioService.STOP);
                reproduciendo = false;
                estado.setText("Reproducción detenida");
            } else {
                comando(RadioService.PLAY);
                reproduciendo = true;
                estado.setText("Conectando...");
            }
            actualizarBoton();
        });

        espacio(pagina, 45);

        TextView salir = texto(
                "⏻  SALIR Y APAGAR RADIO",
                15, Color.WHITE);
        salir.setBackground(
                fondo(0xFF303030, 24));
        salir.setPadding(20, 16, 20, 16);

        pagina.addView(salir);

        salir.setOnClickListener(v -> {
            comando(RadioService.STOP);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filtro =
                new IntentFilter(RadioService.STATUS);

        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(receptor, filtro,
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(receptor, filtro);
        }
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receptor);
        super.onStop();
    }
}
