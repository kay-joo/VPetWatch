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

import com.example.watchtest.databinding.ActivityBattleBinding;

public class BattleActivity extends Activity {
    private Handler handler = new Handler(Looper.getMainLooper());
    boolean isHandlerRunning = false;

    private ActivityBattleBinding binding;
    private ImageView battle_quest_com, battle_arrow_quest, battle_arrow_com;

    private Button button1, button2, button3;

    int sendTapIndex = 4;  // 메인 액티비티로 전달할 값, 다시 메인 화면으로 돌아가도 이전 들어갔던 탭에 불이 들어오게 하기위한 변수

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

        binding = ActivityBattleBinding.inflate(getLayoutInflater());
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
        mistake = preferences.getInt("mistake", 0);
        overfeed = preferences.getInt("overfeed", 0);
        sleepdis = preferences.getInt("sleepdis", 0);
        scarrate = preferences.getInt("scarrate", 0);
        poop = preferences.getInt("poop", 0);
        cure = preferences.getBoolean("cure", false);
    }

    private void initializeImageViews() {
        battle_quest_com = findViewById(R.id.battle_quest_com);
        battle_arrow_quest = findViewById(R.id.battle_arrow_quest);
        battle_arrow_com = findViewById(R.id.battle_arrow_com);
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
                Intent intent = new Intent(BattleActivity.this, MainActivity.class);
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
        resetBattleViewsVisibility();
    }

    private void pageTwo() {
        resetBattleViewsVisibility();
    }

    private void arrowChange(int index) {
        resetBattleViewsVisibility();

        battle_quest_com.setVisibility(View.VISIBLE);
        switch (index) {
            case 0:
                battle_arrow_quest.setVisibility(View.VISIBLE);
                break;
            case 1:
                battle_arrow_com.setVisibility(View.VISIBLE);
                break;
            default:
                battle_arrow_quest.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void resetBattleViewsVisibility() {
        battle_quest_com.setVisibility(View.INVISIBLE);
        battle_arrow_quest.setVisibility(View.INVISIBLE);
        battle_arrow_com.setVisibility(View.INVISIBLE);
    }
}