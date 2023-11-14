package com.example.watchtest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.watchtest.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    private ActivityMainBinding binding;
    private ImageView digimon0, digimonUpP1, digimonDownP2, digimonDownP3, digimonDownP4, digimonEmotionP4, digimonUpM1, digimonDownM2, digimonDownM3, digimonDownM4, digimonEmotionM4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeImageViews();
    }

    private void initializeImageViews() {
        digimon0 = findViewById(R.id.digimon_normal_up0);
        digimonUpP1 = findViewById(R.id.digimon_normal_up_p1);
        digimonDownP2 = findViewById(R.id.digimon_normal_down_p2);
        digimonDownP3 = findViewById(R.id.digimon_normal_down_p3);
        digimonDownP4 = findViewById(R.id.digimon_normal_down_p4);
        digimonEmotionP4 = findViewById(R.id.digimon_normal_emotion_p4);
        digimonUpM1 = findViewById(R.id.digimon_normal_up_m1);
        digimonDownM2 = findViewById(R.id.digimon_normal_down_m2);
        digimonDownM3 = findViewById(R.id.digimon_normal_down_m3);
        digimonDownM4 = findViewById(R.id.digimon_normal_down_m4);
        digimonEmotionM4 = findViewById(R.id.digimon_normal_emotion_m4);
    }

    @Override
    protected void onResume() {
        super.onResume();

        startDigimonStatusUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopDigimonStatusUpdates();
    }

    private void startDigimonStatusUpdates() {
        // 모든 콜백 제거 후 다시 등록
        handler.removeCallbacksAndMessages(null);

        // Runnable을 생성하고 postDelayed를 호출하여 반복 수행
        runnable = new Runnable() {
            @Override
            public void run() {
                digimonStatusNormal();
                handler.postDelayed(this, 19000);
            }
        };

        // 최초 실행
        digimonStatusNormal();

        // 반복 실행 시작
        handler.postDelayed(runnable, 19000);
    }

    private void stopDigimonStatusUpdates() {
        // 액티비티가 일시 정지되면 핸들러 콜백 제거
        handler.removeCallbacksAndMessages(null);
        runnable = null;
    }

    private void digimonStatusNormal() {
        //Log.d("DigimonStatus", "Start digimonStatusNormal()");

        runDelayedAnimation(0, digimon0);

        runDelayedAnimation(500, digimonUpP1);
        runDelayedAnimation(1000, digimonDownP2);
        runDelayedAnimation(1500, digimonDownP2, true);
        runDelayedAnimation(2000, digimonUpP1, true);
        runDelayedAnimation(2500, digimonUpP1);
        runDelayedAnimation(3000, digimonDownP2);
        runDelayedAnimation(3500, digimonDownP3);
        runDelayedAnimation(4000, digimonDownP4);
        runDelayedAnimation(4500, digimonDownP4, true);
        runDelayedAnimation(5000, digimonDownP4);
        runDelayedAnimation(5500, digimonEmotionP4);
        runDelayedAnimation(6000, digimonDownP4);
        runDelayedAnimation(6500, digimonEmotionP4);
        runDelayedAnimation(7000, digimonDownP4);
        runDelayedAnimation(7500, digimonDownP4, true);
        runDelayedAnimation(8000, digimonDownP3, true);
        runDelayedAnimation(8500, digimonDownP2, true);
        runDelayedAnimation(9000, digimonUpP1, true);
        runDelayedAnimation(9500, digimon0, true);

        runDelayedAnimation(10000, digimonUpM1, true);
        runDelayedAnimation(10500, digimonDownM2, true);
        runDelayedAnimation(11000, digimonDownM2);
        runDelayedAnimation(11500, digimonUpM1);
        runDelayedAnimation(12000, digimonUpM1, true);
        runDelayedAnimation(12500, digimonDownM2, true);
        runDelayedAnimation(13000, digimonDownM3, true);
        runDelayedAnimation(13500, digimonDownM4, true);
        runDelayedAnimation(14000, digimonDownM4);
        runDelayedAnimation(14500, digimonDownM4, true);
        runDelayedAnimation(15000, digimonEmotionM4, true);
        runDelayedAnimation(15500, digimonDownM4, true);
        runDelayedAnimation(16000, digimonEmotionM4, true);
        runDelayedAnimation(16500, digimonDownM4, true);
        runDelayedAnimation(17000, digimonDownM4);
        runDelayedAnimation(17500, digimonDownM3);
        runDelayedAnimation(18000, digimonDownM2);
        runDelayedAnimation(18500, digimonUpM1);
    }

    private void runDelayedAnimation(int delay, final ImageView imageView) {
        handler.postDelayed(() -> animateDigimon(imageView), delay);
    }

    private void runDelayedAnimation(int delay, final ImageView imageView, final boolean isInverted) {
        handler.postDelayed(() -> animateDigimon(imageView, isInverted), delay);
    }

    private void animateDigimon(ImageView imageView) {
        resetImageViewsVisibility();
        imageView.setVisibility(View.VISIBLE);
        imageView.setScaleX(1); // 방향 초기화
    }

    private void animateDigimon(ImageView imageView, boolean isInverted) {
        animateDigimon(imageView);
        if (isInverted) {
            imageView.setScaleX(-1);
        }
    }

    private void resetImageViewsVisibility() {
        digimon0.setVisibility(View.INVISIBLE);
        digimonUpP1.setVisibility(View.INVISIBLE);
        digimonDownP2.setVisibility(View.INVISIBLE);
        digimonDownP3.setVisibility(View.INVISIBLE);
        digimonDownP4.setVisibility(View.INVISIBLE);
        digimonEmotionP4.setVisibility(View.INVISIBLE);
        digimonUpM1.setVisibility(View.INVISIBLE);
        digimonDownM2.setVisibility(View.INVISIBLE);
        digimonDownM3.setVisibility(View.INVISIBLE);
        digimonDownM4.setVisibility(View.INVISIBLE);
        digimonEmotionM4.setVisibility(View.INVISIBLE);
    }
}
