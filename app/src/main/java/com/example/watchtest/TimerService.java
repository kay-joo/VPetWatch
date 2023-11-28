package com.example.watchtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

// Foreground Service로 변환된 TimerService 클래스
public class TimerService extends android.app.Service {

    private static final String CHANNEL_ID = "timer_channel";

    private int age, weight, hungry, strength, effort, health, winrate;
    private int mistake, overfeed, sleepdis, scarrate, poop;
    private boolean cure;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private long startTime;
    private final IBinder binder = new LocalBinder();
    private Handler handler = new Handler(Looper.getMainLooper());

    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializePreferences();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Foreground Service로 시작
        startForeground(1, createNotification());

        // 서비스 시작 시간 기록
        startTime = SystemClock.elapsedRealtime();
        Log.d("TimerService", "Service start");

        // 경과 시간을 주기적으로 처리
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long elapsedTime = SystemClock.elapsedRealtime() - startTime;
                initializePreferences();
                if (hungry > 0) {
                    hungry--;
                    editor.putInt("hungry", hungry);
                    if (hungry == 0) {
                        NotificationHelper.showNotification(getApplicationContext(), "VPetWatch", "Hungry decreased");
                    }
                } else {
                    NotificationHelper.showNotification(getApplicationContext(), "VPetWatch", "Hungry decreased");
                }
                if (strength > 0) {
                    strength--;
                    editor.putInt("strength", strength);
                    if (strength == 0) {
                        NotificationHelper.showNotification(getApplicationContext(), "VPetWatch", "Strength decreased");
                    }
                } else {
                    NotificationHelper.showNotification(getApplicationContext(), "VPetWatch", "Strength decreased");
                }
                editor.apply();
                handler.postDelayed(this, 3600000); // 1시간마다 실행
            }
        }, 3600000); // 1시간 후에 실행

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스 종료 시 핸들러 콜백 제거
        handler.removeCallbacksAndMessages(null);
    }

    // SharedPreferences 초기화 부분
    private void initializePreferences() {
        preferences = getSharedPreferences("VPetWatch", Context.MODE_PRIVATE);
        editor = preferences.edit();

        age = preferences.getInt("age", 0);
        weight = preferences.getInt("weight", 5);
        hungry = preferences.getInt("hungry", 0);
        strength = preferences.getInt("strength", 0);
        effort = preferences.getInt("effort", 0);
        health = preferences.getInt("health", 0);
        winrate = preferences.getInt("winrate", 0);
        mistake = preferences.getInt("mistake", 0);
        overfeed = preferences.getInt("overfeed", 0);
        sleepdis = preferences.getInt("sleepdis", 0);
        scarrate = preferences.getInt("scarrate", 0);
        poop = preferences.getInt("poop", 0);
        cure = preferences.getBoolean("cure", false);
    }

    // Foreground 알림 생성
    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Timer Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("VPetWatch")
                .setContentText("VPetWatch is running in the foreground.")
                .setSmallIcon(R.drawable.digimon_normal_up)
                .build();
    }
}