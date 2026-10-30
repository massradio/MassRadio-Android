
package com.massradio.honduras;

import android.app.*;
import android.content.*;
import android.content.pm.ServiceInfo;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

public class RadioService extends Service {

    public static final String PLAY =
            "com.massradio.honduras.PLAY";
    public static final String STOP =
            "com.massradio.honduras.STOP";
    public static final String STATUS =
            "com.massradio.honduras.STATUS";

    private static final String STREAM =
            "https://radiostreaming.pro/8430/stream";
    private static final String CHANNEL = "massradio_live";

    private MediaPlayer player;
    private boolean playing = false;
    private int generation = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL,
                            "MassRadio en vivo",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    (NotificationManager) getSystemService(
                            NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);
        }
    }

    private Notification notification(String message) {
        Intent open = new Intent(this, MainActivity.class);

        PendingIntent openIntent = PendingIntent.getActivity(
                this, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT |
                PendingIntent.FLAG_IMMUTABLE
        );

        Intent stop = new Intent(this, RadioService.class);
        stop.setAction(STOP);

        PendingIntent stopIntent = PendingIntent.getService(
                this, 1, stop,
                PendingIntent.FLAG_UPDATE_CURRENT |
                PendingIntent.FLAG_IMMUTABLE
        );

        Notification.Builder builder =
                Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, CHANNEL)
                : new Notification.Builder(this);

        return builder
                .setContentTitle("MassRadio")
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(openIntent)
                .setOngoing(true)
                .addAction(
                        android.R.drawable.ic_media_pause,
                        "DETENER",
                        stopIntent
                )
                .build();
    }

    private void foreground(String message) {
        Notification n = notification(message);

        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(
                    17, n,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            );
        } else {
            startForeground(17, n);
        }
    }

    private void status(String message) {
        Intent intent = new Intent(STATUS);
        intent.setPackage(getPackageName());
        intent.putExtra("status", message);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(
            Intent intent, int flags, int startId) {

        String action = intent == null
                ? null : intent.getAction();

        if (STOP.equals(action)) {
            stopRadio();
            stopSelf();
            return START_NOT_STICKY;
        }

        if (PLAY.equals(action)) {
            if (!playing) {
                startRadio();
            }
        }

        return START_NOT_STICKY;
    }

    private void startRadio() {
        playing = true;
        final int currentGeneration = ++generation;

        foreground("Conectando...");
        status("Conectando...");

        try {
            MediaPlayer newPlayer = new MediaPlayer();
            player = newPlayer;

            newPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(
                                    AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
            );

            newPlayer.setDataSource(STREAM);

            newPlayer.setOnPreparedListener(mp -> {
                if (playing &&
                        currentGeneration == generation &&
                        player == mp) {
                    mp.start();
                    foreground("Radio en vivo");
                    status("Reproduciendo en vivo");
                }
            });

            newPlayer.setOnErrorListener((mp, what, extra) -> {
                if (currentGeneration == generation) {
                    stopRadio();
                    status("Error de conexión");
                    stopSelf();
                }
                return true;
            });

            newPlayer.prepareAsync();

        } catch (Exception e) {
            stopRadio();
            status("Error de conexión");
            stopSelf();
        }
    }

    private void stopRadio() {
        playing = false;
        generation++;

        MediaPlayer oldPlayer = player;
        player = null;

        if (oldPlayer != null) {
            try {
                oldPlayer.reset();
            } catch (Exception ignored) {
            }

            try {
                oldPlayer.release();
            } catch (Exception ignored) {
            }
        }

        stopForeground(STOP_FOREGROUND_REMOVE);
        status("Reproducción detenida");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopRadio();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        stopRadio();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
