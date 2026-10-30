package com.massradio.honduras;

import android.Manifest;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private TextView state;
    private Button play, stop;
    private final BroadcastReceiver updates = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent i) { update(i.getStringExtra("status")); }
    };
    private GradientDrawable bg(int color, int radius) { GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d; }
    private TextView text(String s, int size, int color) { TextView t=new TextView(this);t.setText(s);t.setTextColor(color);t.setTextSize(size);t.setGravity(Gravity.CENTER);return t; }
    private void update(String s) { if (state!=null && s!=null) state.setText(s); }
    private void command(String action) { Intent i=new Intent(this,RadioService.class); i.setAction(action); if (Build.VERSION.SDK_INT>=26 && RadioService.PLAY.equals(action)) startForegroundService(i); else startService(i); }
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},1);
        LinearLayout page=new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setGravity(Gravity.CENTER_HORIZONTAL); page.setPadding(28,42,28,32); page.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{Color.rgb(7,24,43),Color.rgb(3,8,17)}));
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.addView(page);setContentView(scroll);
        ImageView logo=new ImageView(this);logo.setImageResource(R.drawable.logo_massradio);logo.setAdjustViewBounds(true);logo.setScaleType(ImageView.ScaleType.FIT_CENTER);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,0,1);lp.setMargins(0,25,0,12);page.addView(logo,lp);
        TextView title=text("MASSRADIO",30,Color.WHITE);title.setTypeface(null,Typeface.BOLD);page.addView(title);
        TextView slogan=text("Donde quieres estar siempre",16,0xFFBFC8D6);LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);sp.setMargins(0,8,0,25);page.addView(slogan,sp);
        TextView live=text("●  EN VIVO",16,Color.WHITE);live.setTypeface(null,Typeface.BOLD);live.setPadding(22,13,22,13);live.setBackground(bg(0xFFD91624,45));page.addView(live);
        state=text("Listo para escuchar",17,0xFFDCE5F2);LinearLayout.LayoutParams st=new LinearLayout.LayoutParams(-1,-2);st.setMargins(0,24,0,18);page.addView(state,st);
        play=new Button(this);play.setText("▶  ESCUCHAR RADIO");play.setTextSize(19);play.setTextColor(Color.WHITE);play.setAllCaps(false);play.setBackground(bg(0xFFE11929,28));LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,76);page.addView(play,bp);
        stop=new Button(this);stop.setText("■  DETENER");stop.setTextSize(16);stop.setTextColor(Color.WHITE);stop.setAllCaps(false);stop.setBackground(bg(0xFF253448,24));LinearLayout.LayoutParams sb=new LinearLayout.LayoutParams(-1,60);sb.setMargins(0,14,0,20);page.addView(stop,sb);
        play.setOnClickListener(v->{update("Conectando...");command(RadioService.PLAY);});stop.setOnClickListener(v->{command(RadioService.STOP);update("Reproducción detenida");});
        TextView footer=text("massradioh.com  •  Honduras",13,0xFF95A4B7);page.addView(footer);
    }
    @Override protected void onStart() {super.onStart();IntentFilter f=new IntentFilter(RadioService.STATUS);if(Build.VERSION.SDK_INT>=33)registerReceiver(updates,f,Context.RECEIVER_NOT_EXPORTED);else registerReceiver(updates,f);}
    @Override protected void onStop() {unregisterReceiver(updates);super.onStop();}
}
