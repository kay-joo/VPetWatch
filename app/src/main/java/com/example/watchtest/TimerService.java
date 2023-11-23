package com.example.watchtest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

//백그라운드에서 경과시간을 체크하여 배고픔과 근력을 줄어들게 만들 클래스
public class TimerService extends Service {

    //게임 내에서 사용될 변수들
    private int age, weight, hungry, strength, effort, health, winrate;//상태창에서 사용될 변수들
    private int mistake, overfeed, sleepdis, scarrate, poop;//게임 내부에서 동작할 변수들
    private boolean cure;//상처입었는지 판단용 변수

    //SharedPreferences 데이터 저장 관련 선언
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
        // 서비스 시작 시간 기록
        startTime = SystemClock.elapsedRealtime();
        Log.d("TimerService", "Service start");

        // 경과 시간을 주기적으로 로그에 출력
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long elapsedTime = SystemClock.elapsedRealtime() - startTime;
                //Log.d("TimerService", "Elapsed time: " + elapsedTime + " milliseconds");
                //특정 시간마다 실행될 코드블럭
                initializePreferences();
                if (hungry > 0) {
                    hungry--;
                    editor.putInt("hungry", hungry);//hungry 값 감소
                } else {
                    NotificationHelper.showNotification(getApplicationContext(), "VPetWatch", "Hungry decreased");

                }
                if (strength > 0) {
                    strength--;
                    editor.putInt("strength", strength);//strength 값 감소
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

    //SharedPreferences 초기화 부분
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
}