package com.example.watchtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.watchtest.databinding.ActivityTrainingBinding;

public class TrainingActivity extends Activity {
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    boolean isHandlerRunning = false;

    private ActivityTrainingBinding binding;
    private ImageView training_power, training_power_first, training_power_second, training_power_third, training_percentage;
    private ImageView digimon_training_open, training_sandbag_full, training_sandbag_half, training_attack_effect_first, training_attack_effect_second;
    private ImageView training_hit_effect_first, training_hit_effect_second;
    private ImageView digimon_training_success_down, digimon_training_success_up, digimon_training_success_sun;
    private ImageView digimon_training_fail_down, digimon_training_fail_up, digimon_training_fail_first, digimon_training_fail_second;


    private Button button1, button2, button3;

    int sendTapIndex = 3;  // 메인 액티비티로 전달할 값, 다시 메인 화면으로 돌아가도 이전 들어갔던 탭에 불이 들어오게 하기위한 변수

    //게임 내에서 사용될 변수들
    private int age, weight, hungry, strength, effort, health, winrate, winnum, fightnum;//상태창에서 사용될 변수들
    private int mistake, overfeed, sleepdis, scarrate, poop, pwr, heffort;//게임 내부에서 동작할 변수들
    private boolean cure;//상처입었는지 판단용 변수

    //SharedPreferences 데이터 저장 관련 선언
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    //트레이닝 변수
    private int tCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeImageViews();
        initializeButton();
        initializePreferences();//SharedPreferences 초기화 메소드
        trainingStart();
    }

    private void initializeImageNumber(int num, ImageView imageView1, ImageView imageView2, ImageView imageView3) {
        // 초기 숫자 이미지 표시
        // 세 자리 숫자를 각 자릿수로 분리
        int initialDigit1 = num / 100;           // 백의 자릿수
        int initialDigit2 = (num % 100) / 10;   // 십의 자릿수
        int initialDigit3 = num % 10;           // 일의 자릿수
        if (num > 99) {
            imageView1.setVisibility(View.VISIBLE);
            imageView1.setImageResource(getImageResourceId(initialDigit1));
        } else {
            imageView1.setVisibility(View.INVISIBLE);
        }
        if (num > 9) {
            imageView2.setVisibility(View.VISIBLE);
            imageView2.setImageResource(getImageResourceId(initialDigit2));
        } else {
            imageView2.setVisibility(View.INVISIBLE);
        }
        imageView3.setVisibility(View.VISIBLE);
        imageView3.setImageResource(getImageResourceId(initialDigit3));
    }

    private int getImageResourceId(int digit) {
        String imageName = "";
        switch (digit) {
            case 0:
                imageName = "ui_number_zero";
                break;
            case 1:
                imageName = "ui_number_one";
                break;
            case 2:
                imageName = "ui_number_two";
                break;
            case 3:
                imageName = "ui_number_three";
                break;
            case 4:
                imageName = "ui_number_four";
                break;
            case 5:
                imageName = "ui_number_five";
                break;
            case 6:
                imageName = "ui_number_six";
                break;
            case 7:
                imageName = "ui_number_seven";
                break;
            case 8:
                imageName = "ui_number_eight";
                break;
            case 9:
                imageName = "ui_number_nine";
                break;
            default:
                break;
        }
        return getResources().getIdentifier(imageName, "drawable", getPackageName());
    }

    private void initializeImageViews() {
        training_power = findViewById(R.id.training_power);
        training_power_first = findViewById(R.id.training_power_first);
        training_power_second = findViewById(R.id.training_power_second);
        training_power_third = findViewById(R.id.training_power_third);
        training_percentage = findViewById(R.id.training_percentage);

        digimon_training_open = findViewById(R.id.digimon_training_open);
        training_sandbag_full = findViewById(R.id.training_sandbag_full);
        training_sandbag_half = findViewById(R.id.training_sandbag_half);
        training_attack_effect_first = findViewById(R.id.training_attack_effect_first);
        training_attack_effect_second = findViewById(R.id.training_attack_effect_second);

        training_hit_effect_first = findViewById(R.id.training_hit_effect_first);
        training_hit_effect_second = findViewById(R.id.training_hit_effect_second);

        digimon_training_success_down = findViewById(R.id.digimon_training_success_down);
        digimon_training_success_up = findViewById(R.id.digimon_training_success_up);
        digimon_training_success_sun = findViewById(R.id.digimon_training_success_sun);

        digimon_training_fail_down = findViewById(R.id.digimon_training_fail_down);
        digimon_training_fail_up = findViewById(R.id.digimon_training_fail_up);
        digimon_training_fail_first = findViewById(R.id.digimon_training_fail_first);
        digimon_training_fail_second = findViewById(R.id.digimon_training_fail_second);
    }


    //버튼 초기화 부분
    private void initializeButton() {
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isHandlerRunning) {
                    //핸들러가 동작 중일 때는 버튼 동작 중지
                } else {
                    MySoundPlayer.stop();
                    handler.removeCallbacksAndMessages(null);
                    runnable = null;
                    handler.postDelayed(() -> resultStart(tCnt), 300);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isHandlerRunning) {
                    //핸들러가 동작 중일 때 2번 버튼을 누르면 애니메이션 스킵, 데이터는 증가
                    //핸들러 강제 정지
                    MySoundPlayer.stop();
                    handler.removeCallbacksAndMessages(null);
                    runnable = null;
                    isHandlerRunning = false;
                    trainingStart();
                } else {
                    MySoundPlayer.stop();
                    handler.removeCallbacksAndMessages(null);
                    runnable = null;
                    handler.postDelayed(() -> resultStart(tCnt), 300);
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacksAndMessages(null);
                runnable = null;
                MySoundPlayer.stop();

                MySoundPlayer.play(MySoundPlayer.sound1);
                Intent intent = new Intent(TrainingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//액티비티 전환시 애니메이션 없애기
                intent.putExtra("INT_VALUE_KEY", sendTapIndex);
                startActivity(intent);
                finish();//현재 액티비티 종료
            }
        });
    }

    private void resultStart(int tCnt) {
        if (tCnt == 100) {
            isHandlerRunning = true;
            if (effort < 16) {
                effort++;
            }
            if (heffort < 4000) {//배틀 공식에서 사용될 히든 노력치 상한값은 4000
                heffort++;
            }
            if (weight > 5) {
                weight--;
            }
            if (strength < 4) {
                strength++;
            }
            editor.putInt("effort", effort);
            editor.putInt("heffort", heffort);
            editor.putInt("weight", weight);
            editor.putInt("strength", strength);
            editor.apply();

            successMotion();
        } else {
            isHandlerRunning = true;
            if (effort < 16) {
                effort++;
            }
            if (weight > 5) {
                weight--;
            }
            editor.putInt("effort", effort);
            editor.putInt("weight", weight);
            editor.apply();

            failMotion();
        }
    }

    private void successMotion() {
        MySoundPlayer.play(MySoundPlayer.sound3);
        runDelayedAnimation(0, digimon_training_open, training_sandbag_full, training_attack_effect_first);
        runDelayedAnimation(700, digimon_training_open, training_sandbag_full, training_attack_effect_second);

        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound4), 1000);
        runDelayedAnimation(1500, training_hit_effect_first);
        runDelayedAnimation(1700, training_hit_effect_second);
        runDelayedAnimation(1900, training_hit_effect_first);
        runDelayedAnimation(2100, training_hit_effect_second);
        runDelayedAnimation(2300, training_hit_effect_first);

        runDelayedAnimation(2500, digimon_training_open, training_sandbag_half);

        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound5), 3000);
        runDelayedAnimation(3000, digimon_training_success_down);
        runDelayedAnimation(3500, digimon_training_success_up, digimon_training_success_sun);
        runDelayedAnimation(4000, digimon_training_success_down);
        runDelayedAnimation(4500, digimon_training_success_up, digimon_training_success_sun);
        runDelayedAnimation(5000, digimon_training_success_down);
        runDelayedAnimation(5500, digimon_training_success_up, digimon_training_success_sun);
        runDelayedAnimation(6000, digimon_training_success_down);
        runDelayedAnimation(6500, digimon_training_success_up, digimon_training_success_sun);
        handler.postDelayed(() -> trainingStart(), 7000);//람다 7초 뒤 trainingStart()메소드 실행
        handler.postDelayed(() -> isHandlerRunning = false, 7000);//람다 7초 뒤 실행
    }

    private void failMotion() {
        MySoundPlayer.play(MySoundPlayer.sound3);
        runDelayedAnimation(0, digimon_training_open, training_sandbag_full, training_attack_effect_first);
        runDelayedAnimation(700, digimon_training_open, training_sandbag_full, training_attack_effect_second);

        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound4), 1000);
        runDelayedAnimation(1500, training_hit_effect_first);
        runDelayedAnimation(1700, training_hit_effect_second);
        runDelayedAnimation(1900, training_hit_effect_first);
        runDelayedAnimation(2100, training_hit_effect_second);
        runDelayedAnimation(2300, training_hit_effect_first);

        runDelayedAnimation(2500, digimon_training_open, training_sandbag_full);

        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound6), 3000);
        runDelayedAnimation(3000, digimon_training_fail_down, digimon_training_fail_first);
        runDelayedAnimation(3500, digimon_training_fail_up, digimon_training_fail_second);
        runDelayedAnimation(4000, digimon_training_fail_down, digimon_training_fail_first);
        runDelayedAnimation(4500, digimon_training_fail_up, digimon_training_fail_second);
        runDelayedAnimation(5000, digimon_training_fail_down, digimon_training_fail_first);
        runDelayedAnimation(5500, digimon_training_fail_up, digimon_training_fail_second);
        runDelayedAnimation(6000, digimon_training_fail_down, digimon_training_fail_first);
        runDelayedAnimation(6500, digimon_training_fail_up, digimon_training_fail_second);
        handler.postDelayed(() -> trainingStart(), 7000);//람다 7초 뒤 trainingStart()메소드 실행
        handler.postDelayed(() -> isHandlerRunning = false, 7000);//람다 7초 뒤 실행
    }

    private void runDelayedAnimation(int delay, final ImageView imageView) {
        handler.postDelayed(() -> animateDigimon(imageView), delay);
    }

    private void runDelayedAnimation(int delay, final ImageView imageView, final ImageView imageView2) {
        handler.postDelayed(() -> animateDigimon(imageView, imageView2), delay);
    }

    private void runDelayedAnimation(int delay, final ImageView imageView, final ImageView imageView2, final ImageView imageView3) {
        handler.postDelayed(() -> animateDigimon(imageView, imageView2, imageView3), delay);
    }


    private void animateDigimon(ImageView imageView) {
        resetTrainingViewsVisibility();
        imageView.setVisibility(View.VISIBLE);
        imageView.setScaleX(1); // 방향 초기화
    }

    private void animateDigimon(ImageView imageView, ImageView imageView2) {
        resetTrainingViewsVisibility();
        imageView.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        imageView.setScaleX(1); // 방향 초기화
        imageView2.setScaleX(1);
    }

    private void animateDigimon(ImageView imageView, ImageView imageView2, ImageView imageView3) {
        resetTrainingViewsVisibility();
        imageView.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        imageView3.setVisibility(View.VISIBLE);
        imageView.setScaleX(1); // 방향 초기화
        imageView2.setScaleX(1);
        imageView3.setScaleX(1);
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
        winnum = preferences.getInt("winnum", 0);
        fightnum = preferences.getInt("fightnum", 0);
        mistake = preferences.getInt("mistake", 0);
        overfeed = preferences.getInt("overfeed", 0);
        sleepdis = preferences.getInt("sleepdis", 0);
        scarrate = preferences.getInt("scarrate", 0);
        poop = preferences.getInt("poop", 0);
        pwr = preferences.getInt("pwr", 10);
        heffort = preferences.getInt("heffort", 0);
        cure = preferences.getBoolean("cure", false);
    }

    private void trainingStart() {
        //사운드 플레이
        MySoundPlayer.play(MySoundPlayer.sound2, true);

        // 초기화
        tCnt = 0;
        resetTrainingViewsVisibility();
        training_power.setVisibility(View.VISIBLE);
        training_percentage.setVisibility(View.VISIBLE);

        // 초기 숫자 표시
        initializeImageNumber(tCnt, training_power_first, training_power_second, training_power_third);

        // 0.5초마다 tCnt 증가 및 업데이트 실행
        runnable = new Runnable() {
            @Override
            public void run() {
                tCnt += 10;

                // tCnt가 100을 넘지 않는 경우에만 업데이트
                if (tCnt <= 100) {
                    initializeImageNumber(tCnt, training_power_first, training_power_second, training_power_third);
                    handler.postDelayed(this, 300); // 0.3초 후에 다시 실행
                } else {
                    // tCnt가 100을 넘으면 초기화하고 다시 시작
                    tCnt = 0;
                    initializeImageNumber(tCnt, training_power_first, training_power_second, training_power_third);
                    handler.postDelayed(this, 300); // 0.3초 후에 다시 실행
                }
            }
        };
        handler.postDelayed(runnable, 300); // 처음은 0.3초 후에 실행
    }


    private void resetTrainingViewsVisibility() {
        training_power.setVisibility(View.INVISIBLE);
        training_power_first.setVisibility(View.INVISIBLE);
        training_power_second.setVisibility(View.INVISIBLE);
        training_power_third.setVisibility(View.INVISIBLE);
        training_percentage.setVisibility(View.INVISIBLE);

        digimon_training_open.setVisibility(View.INVISIBLE);
        training_sandbag_full.setVisibility(View.INVISIBLE);
        training_sandbag_half.setVisibility(View.INVISIBLE);
        training_attack_effect_first.setVisibility(View.INVISIBLE);
        training_attack_effect_second.setVisibility(View.INVISIBLE);

        training_hit_effect_first.setVisibility(View.INVISIBLE);
        training_hit_effect_second.setVisibility(View.INVISIBLE);

        digimon_training_success_down.setVisibility(View.INVISIBLE);
        digimon_training_success_up.setVisibility(View.INVISIBLE);
        digimon_training_success_sun.setVisibility(View.INVISIBLE);

        digimon_training_fail_down.setVisibility(View.INVISIBLE);
        digimon_training_fail_up.setVisibility(View.INVISIBLE);
        digimon_training_fail_first.setVisibility(View.INVISIBLE);
        digimon_training_fail_second.setVisibility(View.INVISIBLE);
    }
}