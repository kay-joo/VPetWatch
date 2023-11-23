package com.example.watchtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.watchtest.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    private ActivityMainBinding binding;
    private ImageView digimon0, digimonUpP1, digimonDownP2, digimonDownP3, digimonDownP4, digimonEmotionP4, digimonUpM1, digimonDownM2, digimonDownM3, digimonDownM4, digimonEmotionM4;
    private ImageView uiBlackStatus, uiBlackFood, uiBlackTraining, uiBlackBattle, uiBlackPoop, uiBlackLight, uiBlackCure, uiBlackCall;
    private Button button1, button2, button3;

    private Intent intent;

    private int index = 0;//탭별 인덱스 제어 변수

    //게임 내에서 사용될 변수들
    private int age, weight, hungry, strength, effort, health, winrate;//상태창에서 사용될 변수들
    private int mistake, overfeed, sleepdis, scarrate, poop;//게임 내부에서 동작할 변수들
    private boolean cure;//상처입었는지 판단용 변수

    //SharedPreferences 데이터 저장 관련 선언
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeImageViews();//이미지뷰 초기화
        initializeButton();//버튼 초기화
        MySoundPlayer.initSounds(getApplicationContext());//사운드 플레이어 초기화
        resetUiViewsVisibility();//액티비티 시작과 동시에 검은색 ui 안보이게 설정
        initializePreferences();//SharedPreferences 초기화 메소드

        Intent intent = getIntent();
        index = intent.getIntExtra("INT_VALUE_KEY", 0);//특정 탭에서 나왔을때 탭의 커서를 받아오기 위한 인텐트

        if (!ServiceUtils.isServiceRunning(this, TimerService.class)) {
            Log.d("MainActivity", "startService");
            Intent serviceIntent = new Intent(this, TimerService.class);
            startService(serviceIntent);
        }
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

    //이미지뷰 초기화 부분
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

        uiBlackStatus = findViewById(R.id.UI_black_status);
        uiBlackFood = findViewById(R.id.UI_black_food);
        uiBlackTraining = findViewById(R.id.UI_black_training);
        uiBlackBattle = findViewById(R.id.UI_black_battle);
        uiBlackPoop = findViewById(R.id.UI_black_poop);
        uiBlackLight = findViewById(R.id.UI_black_light);
        uiBlackCure = findViewById(R.id.UI_black_cure);
        uiBlackCall = findViewById(R.id.UI_black_call);
    }

    //버튼 초기화 부분
    private void initializeButton() {
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySoundPlayer.play(MySoundPlayer.sound1);
                //1번 버튼을 누를때 마다 인덱스 상승
                if (index < 7) {//인덱스가 7이 넘어가면
                    index++;
                } else {
                    index = 0;//0으로 다시 설정
                }
                moveTap(index);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index != 0) {
                    MySoundPlayer.play(MySoundPlayer.sound1);
                    changeActivity(index);
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index != 0) {
                    MySoundPlayer.play(MySoundPlayer.sound1);
                    index = 0;
                    moveTap(index);
                }
            }
        });
    }

    private void changeActivity(int index) {
        switch (index) {
            case 1:
                intent = new Intent(MainActivity.this, StatusActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//액티비티 전환시 애니메이션 없애기
                startActivity(intent);
                finish();//현재 액티비티 종료
                break;
            case 2:
                intent = new Intent(MainActivity.this, FoodActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//액티비티 전환시 애니메이션 없애기
                startActivity(intent);
                finish();//현재 액티비티 종료
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        startDigimonStatusUpdates();
        moveTap(index);
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopDigimonStatusUpdates();
        index = 0;
    }

    private void startDigimonStatusUpdates() {
        // 모든 콜백 제거 후 다시 등록
        handler.removeCallbacksAndMessages(null);

        // Runnable을 생성하고 postDelayed를 호출하여 반복 수행
        runnable = new Runnable() {
            @Override
            public void run() {
                index = 0;
                moveTap(index);

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
        resetDigimonViewsVisibility();
        imageView.setVisibility(View.VISIBLE);
        imageView.setScaleX(1); // 방향 초기화
    }

    private void animateDigimon(ImageView imageView, boolean isInverted) {
        animateDigimon(imageView);
        if (isInverted) {
            imageView.setScaleX(-1);
        }
    }

    private void moveTap(int index) {
        resetUiViewsVisibility();

        switch (index) {
            case 1:
                uiBlackStatus.setVisibility(View.VISIBLE);
                break;
            case 2:
                uiBlackFood.setVisibility(View.VISIBLE);
                break;
            case 3:
                uiBlackTraining.setVisibility(View.VISIBLE);
                break;
            case 4:
                uiBlackBattle.setVisibility(View.VISIBLE);
                break;
            case 5:
                uiBlackPoop.setVisibility(View.VISIBLE);
                break;
            case 6:
                uiBlackLight.setVisibility(View.VISIBLE);
                break;
            case 7:
                uiBlackCure.setVisibility(View.VISIBLE);
                break;
            default:
                resetUiViewsVisibility();
                break;
        }
    }

    private void resetDigimonViewsVisibility() {
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

    private void resetUiViewsVisibility() {
        uiBlackStatus.setVisibility(View.INVISIBLE);
        uiBlackFood.setVisibility(View.INVISIBLE);
        uiBlackTraining.setVisibility(View.INVISIBLE);
        uiBlackBattle.setVisibility(View.INVISIBLE);
        uiBlackPoop.setVisibility(View.INVISIBLE);
        uiBlackLight.setVisibility(View.INVISIBLE);
        uiBlackCure.setVisibility(View.INVISIBLE);
        uiBlackCall.setVisibility(View.INVISIBLE);
    }
}
