package com.massradio.honduras;

import android.app.*;
import android.content.*;
import android.content.pm.ServiceInfo;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

public class RadioService extends Service {
 public static final String PLAY="com.massradio.honduras.PLAY", STOP="com.massradio.honduras.STOP", STATUS="com.massradio.honduras.STATUS";
 private static final String STREAM="https://radiostreaming.pro/8430/stream", CHANNEL="massradio_live";
 private MediaPlayer player;
 private boolean started=false;
 @Override public void onCreate(){super.onCreate();NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel(CHANNEL,"Radio en vivo",NotificationManager.IMPORTANCE_LOW));}
 private Notification notification(String msg){Intent open=new Intent(this,MainActivity.class);PendingIntent pi=PendingIntent.getActivity(this,0,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);Intent stop=new Intent(this,RadioService.class).setAction(STOP);PendingIntent si=PendingIntent.getService(this,1,stop,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,CHANNEL):new Notification.Builder(this);return b.setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("MassRadio • En vivo").setContentText(msg).setContentIntent(pi).setOngoing(true).addAction(android.R.drawable.ic_media_pause,"Detener",si).build();}
 private void foreground(String msg){if(Build.VERSION.SDK_INT>=29)startForeground(17,notification(msg),ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);else startForeground(17,notification(msg));}
 private void status(String s){Intent i=new Intent(STATUS);i.setPackage(getPackageName());i.putExtra("status",s);sendBroadcast(i);}
 @Override public int onStartCommand(Intent i,int flags,int id){String action=i==null?null:i.getAction();if(STOP.equals(action)){stopRadio();stopSelf();return START_NOT_STICKY;}if(PLAY.equals(action)){if(!started)startRadio();return START_STICKY;}return START_NOT_STICKY;}
 private void startRadio(){started=true;foreground("Conectando...");status("Conectando...");try{player=new MediaPlayer();player.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());player.setDataSource(STREAM);player.setOnPreparedListener(mp->{if(started){mp.start();foreground("Reproduciendo ahora");status("Reproduciendo MassRadio");}});player.setOnErrorListener((mp,what,extra)->{status("No se pudo reproducir. Intentá nuevamente.");stopRadio();stopSelf();return true;});player.prepareAsync();}catch(Exception e){status("Error al conectar. Intentá nuevamente.");stopRadio();stopSelf();}}
 private void stopRadio(){started=false;if(player!=null){try{player.stop();}catch(Exception ignored){}player.release();player=null;}stopForeground(STOP_FOREGROUND_REMOVE);status("Reproducción detenida");}
 @Override public void onDestroy(){stopRadio();super.onDestroy();}
 @Override public IBinder onBind(Intent i){return null;}
}
