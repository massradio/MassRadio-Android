
package com.massradio.honduras;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView status;
    private Button playButton;
    private Button stopButton;

    private final BroadcastReceiver receiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("status");

            if (message != null && status != null) {
                status.setText(message);
            }
        }
    };

    private GradientDrawable background(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(28);
        return drawable;
    }

    private Button button(String label, int color) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(17);
        b.setAllCaps(false);
        b.setBackground(background(color));
        return b;
    }

    private void sendRadioCommand(String action) {
        Intent intent = new Intent(this, RadioService.class);
        intent.setAction(action);

        if (RadioService.PLAY.equals(action)) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(7, 14, 30));
        getWindow().setNavigationBarColor(Color.rgb(7, 14, 30));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(35, 45, 35, 30);
        root.setBackgroundColor(Color.rgb(9, 20, 43));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_massradio);
        logo.setAdjustViewBounds(true);
        logo.setMaxHeight(250);

        root.addView(logo, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 230));

        TextView title = new TextView(this);
        title.setText("MASSRADIO");
        title.setTextSize(29);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView slogan = new TextView(this);
        slogan.setText("Donde quieres estar siempre");
        slogan.setTextColor(Color.LTGRAY);
        slogan.setTextSize(15);
        slogan.setGravity(Gravity.CENTER);
        root.addView(slogan);

        TextView live = new TextView(this);
        live.setText("● EN VIVO");
        live.setTextColor(Color.RED);
        live.setTextSize(20);
        live.setGravity(Gravity.CENTER);
        live.setPadding(0, 35, 0, 20);
        root.addView(live);

        status = new TextView(this);
        status.setText("Listo para escuchar");
        status.setTextColor(Color.WHITE);
        status.setGravity(Gravity.CENTER);
        status.setTextSize(16);
        root.addView(status);

        playButton = button("▶ REPRODUCIR",
                Color.rgb(194, 24, 42));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 65);
        params.setMargins(0, 30, 0, 12);

        root.addView(playButton, params);

        playButton.setOnClickListener(v -> {
            sendRadioCommand(RadioService.PLAY);
            status.setText("Conectando...");
        });

        stopButton = button("■ DETENER",
                Color.rgb(75, 85, 105));

        root.addView(stopButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 65));

        stopButton.setOnClickListener(v -> {
            sendRadioCommand(RadioService.STOP);
            status.setText("Reproducción detenida");
        });

        TextView volumeLabel = new TextView(this);
        volumeLabel.setText("VOLUMEN");
        volumeLabel.setTextColor(Color.WHITE);
        volumeLabel.setPadding(0, 30, 0, 5);
        root.addView(volumeLabel);

        AudioManager audio =
                (AudioManager) getSystemService(AUDIO_SERVICE);

        SeekBar volume = new SeekBar(this);
        int max = audio.getStreamMaxVolume(
                AudioManager.STREAM_MUSIC);
        volume.setMax(max);
        volume.setProgress(audio.getStreamVolume(
                AudioManager.STREAM_MUSIC));

        volume.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(
                    SeekBar bar, int value, boolean fromUser) {
                if (fromUser) {
                    audio.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            value, 0);
                }
            }
            public void onStartTrackingTouch(SeekBar bar) {}
            public void onStopTrackingTouch(SeekBar bar) {}
        });

        root.addView(volume);

        Button exit = button("SALIR Y APAGAR RADIO",
                Color.rgb(35, 45, 65));

        LinearLayout.LayoutParams exitParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 65);
        exitParams.setMargins(0, 25, 0, 0);
        root.addView(exit, exitParams);

        exit.setOnClickListener(v -> {
            sendRadioCommand(RadioService.STOP);
            finish();
        });

        setContentView(root);
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter =
                new IntentFilter(RadioService.STATUS);

        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(receiver, filter,
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(receiver, filter);
        }
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }
}
