package com.example.watchtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.watchtest.databinding.ActivityFoodBinding;

public class FoodActivity extends Activity {
    private Handler handler = new Handler(Looper.getMainLooper());
    boolean isHandlerRunning = false;

    private ActivityFoodBinding binding;
    private ImageView digimon_normal_up0, digimon_food_open, digimon_food_close;
    private ImageView food_meat_protein, food_arrow_food, food_arrow_protein;
    private ImageView food_meat_one_up, food_meat_one_down, food_meat_two, food_meat_three, food_meat_four;
    private ImageView food_protein_one_up, food_protein_one_down, food_protein_two, food_protein_three, food_protein_four;

    private Button button1, button2, button3;

    int sendTapIndex = 2;  // 메인 액티비티로 전달할 값, 다시 메인 화면으로 돌아가도 이전 들어갔던 탭에 불이 들어오게 하기위한 변수

    private int index = 0;//탭별 인덱스 제어 변수

    //게임 내에서 사용될 변수들
    private int age, weight, hungry, strength, effort, health, winrate, winnum, fightnum;//상태창에서 사용될 변수들
    private int mistake, overfeed, sleepdis, scarrate, poop, pwr, heffort;//게임 내부에서 동작할 변수들
    private boolean cure;//상처입었는지 판단용 변수

    private int tmpHealth;//프로틴 4개 먹일 때마다 체력 1을 상승시키기 위한 임시 변수

    //SharedPreferences 데이터 저장 관련 선언
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeImageViews();
        initializeButton();
        initializePreferences();//SharedPreferences 초기화 메소드
        arrowChange(index);
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

        tmpHealth = preferences.getInt("tmpHealth", 0);
    }

    private void initializeImageViews() {
        food_meat_protein = findViewById(R.id.food_meat_protein);
        food_arrow_food = findViewById(R.id.food_arrow_food);
        food_arrow_protein = findViewById(R.id.food_arrow_protein);

        digimon_normal_up0 = findViewById(R.id.digimon_normal_up0);
        digimon_food_open = findViewById(R.id.digimon_food_open);
        digimon_food_close = findViewById(R.id.digimon_food_close);

        food_meat_one_up = findViewById(R.id.food_meat_one_up);
        food_meat_one_down = findViewById(R.id.food_meat_one_down);
        food_meat_two = findViewById(R.id.food_meat_two);
        food_meat_three = findViewById(R.id.food_meat_three);
        food_meat_four = findViewById(R.id.food_meat_four);

        food_protein_one_up = findViewById(R.id.food_protein_one_up);
        food_protein_one_down = findViewById(R.id.food_protein_one_down);
        food_protein_two = findViewById(R.id.food_protein_two);
        food_protein_three = findViewById(R.id.food_protein_three);
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
                    MySoundPlayer.play(MySoundPlayer.sound1);
                    //1번 버튼을 누를때 마다 인덱스 상승
                    if (index < 1) {//인덱스가 1을 넘어가면
                        index++;
                    } else {
                        index = 0;//0으로 다시 설정
                    }
                    arrowChange(index);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySoundPlayer.play(MySoundPlayer.sound1);
                if (isHandlerRunning) {
                    //핸들러가 동작 중일 때 2번 버튼을 누르면 애니메이션 스킵, 데이터는 증가
                    //핸들러 강제 정지
                    handler.removeCallbacksAndMessages(null);
                    isHandlerRunning = false;
                    arrowChange(index);
                } else {
                    pageChange(index);
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySoundPlayer.play(MySoundPlayer.sound1);
                Intent intent = new Intent(FoodActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//액티비티 전환시 애니메이션 없애기
                intent.putExtra("INT_VALUE_KEY", sendTapIndex);
                startActivity(intent);
                finish();//현재 액티비티 종료
            }
        });
    }

    private void pageChange(int index) {
        switch (index) {
            case 0:
                pageOne();
                break;
            case 1:
                pageTwo();
                break;
            default:
                pageOne();
                break;
        }
    }

    private void pageOne() {
        resetFoodViewsVisibility();
        if (hungry < 4) {
            hungry++;
        }
        if (weight < 99) {
            weight++;
        }
        editor.putInt("hungry", hungry);//hungry 값 증가
        editor.putInt("weight", weight);//weight 값 증가
        editor.apply();
        handler.removeCallbacksAndMessages(null);
        isHandlerRunning = true;//핸들러의 동작 시작을 세팅
        digimonStatusEatMeat();
    }

    private void pageTwo() {
        resetFoodViewsVisibility();
        if (strength < 4) {
            strength++;
        }
        if (weight < 99) {
            weight += 2;
            if (weight > 99) {
                weight = 99;
            }
        }
        if (tmpHealth < 4) {
            tmpHealth++;
        }
        if (tmpHealth == 4 && health < 16) {
            health++;
            tmpHealth = 0;
        }
        editor.putInt("strength", strength);//strength 값 증가
        editor.putInt("weight", weight);//weight 값 증가
        editor.putInt("tmpHealth", tmpHealth);//tmpHealth 값 증가
        editor.putInt("health", health);//health 값 증가
        editor.apply();
        handler.removeCallbacksAndMessages(null);
        isHandlerRunning = true;//핸들러의 동작 시작을 세팅
        digimonStatusEatProtein();
    }

    private void arrowChange(int index) {
        resetFoodViewsVisibility();

        food_meat_protein.setVisibility(View.VISIBLE);
        switch (index) {
            case 0:
                food_arrow_food.setVisibility(View.VISIBLE);
                break;
            case 1:
                food_arrow_protein.setVisibility(View.VISIBLE);
                break;
            default:
                food_arrow_food.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void digimonStatusEatMeat() {
        runDelayedAnimation(0, digimon_normal_up0, food_meat_one_up);
        runDelayedAnimation(500, digimon_food_open, food_meat_one_down);

        runDelayedAnimation(1000, digimon_food_close, food_meat_two);
        runDelayedAnimation(1500, digimon_food_open, food_meat_two);

        runDelayedAnimation(2000, digimon_food_close, food_meat_three);
        runDelayedAnimation(2500, digimon_food_open, food_meat_three);

        runDelayedAnimation(3000, digimon_food_close, food_meat_four);
        runDelayedAnimation(3500, food_arrow_food, food_meat_protein, false);
    }

    private void digimonStatusEatProtein() {
        runDelayedAnimation(0, digimon_normal_up0, food_protein_one_up);
        runDelayedAnimation(500, digimon_food_open, food_protein_one_down);

        runDelayedAnimation(1000, digimon_food_close, food_protein_two);
        runDelayedAnimation(1500, digimon_food_open, food_protein_two);

        runDelayedAnimation(2000, digimon_food_close, food_protein_three);
        runDelayedAnimation(2500, digimon_food_open, food_protein_three);

        runDelayedAnimation(3000, digimon_food_close);
        runDelayedAnimation(3500, food_arrow_protein, food_meat_protein, false);
    }

    private void runDelayedAnimation(int delay, final ImageView imageView) {
        handler.postDelayed(() -> animateDigimon(imageView), delay);
    }

    private void runDelayedAnimation(int delay, final ImageView imageView, final ImageView imageView2) {
        handler.postDelayed(() -> animateDigimon(imageView, imageView2), delay);
    }

    //핸들러 동작 확인 변수값을 세팅하는 오버로드 메소드
    private void runDelayedAnimation(int delay, final ImageView imageView, final ImageView imageView2, boolean isHandlerRunning) {
        handler.postDelayed(() -> {
            animateDigimon(imageView, imageView2);
            this.isHandlerRunning = isHandlerRunning;
        }, delay);
    }

    private void animateDigimon(ImageView imageView) {
        resetFoodViewsVisibility();
        imageView.setVisibility(View.VISIBLE);
        imageView.setScaleX(1); // 방향 초기화
    }

    private void animateDigimon(ImageView imageView, ImageView imageView2) {
        resetFoodViewsVisibility();
        imageView.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        imageView.setScaleX(1); // 방향 초기화
        imageView2.setScaleX(1);
    }


    private void resetFoodViewsVisibility() {
        food_meat_protein.setVisibility(View.INVISIBLE);
        food_arrow_food.setVisibility(View.INVISIBLE);
        food_arrow_protein.setVisibility(View.INVISIBLE);

        digimon_normal_up0.setVisibility(View.INVISIBLE);
        digimon_food_open.setVisibility(View.INVISIBLE);
        digimon_food_close.setVisibility(View.INVISIBLE);

        food_meat_one_up.setVisibility(View.INVISIBLE);
        food_meat_one_down.setVisibility(View.INVISIBLE);
        food_meat_two.setVisibility(View.INVISIBLE);
        food_meat_three.setVisibility(View.INVISIBLE);
        food_meat_four.setVisibility(View.INVISIBLE);

        food_protein_one_up.setVisibility(View.INVISIBLE);
        food_protein_one_down.setVisibility(View.INVISIBLE);
        food_protein_two.setVisibility(View.INVISIBLE);
        food_protein_three.setVisibility(View.INVISIBLE);
    }
}
