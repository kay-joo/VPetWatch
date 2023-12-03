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

import com.example.watchtest.databinding.ActivityBattleBinding;

import java.util.Random;

public class BattleActivity extends Activity {
    private Handler handler = new Handler(Looper.getMainLooper());
    boolean isHandlerRunning = false;

    private ActivityBattleBinding binding;
    private ImageView battle_quest_com, battle_arrow_quest, battle_arrow_com;
    private ImageView enemy_digimon_battle_normal, enemy_digimon_battle_open, enemy_digimon_battle_close;
    private ImageView digimon_battle_normal, digimon_battle_open, digimon_battle_close, battle_screen;
    private ImageView battle_attack_effect_first, battle_attack_effect_second, battle_attack_effect_third;
    private ImageView enemy_battle_attack_effect_first, enemy_battle_attack_effect_second, enemy_battle_attack_effect_third;
    private ImageView battle_hit_effect_first, battle_hit_effect_second;
    private ImageView digimon_battle_win_down, digimon_battle_win_up, digimon_battle_win_sun;

    private Button button1, button2, button3;

    int sendTapIndex = 4;  // 메인 액티비티로 전달할 값, 다시 메인 화면으로 돌아가도 이전 들어갔던 탭에 불이 들어오게 하기위한 변수

    private int index = 0;//탭별 인덱스 제어 변수

    //게임 내에서 사용될 변수들
    private int age, weight, hungry, strength, effort, health, winrate, winnum, fightnum;//상태창에서 사용될 변수들
    private int mistake, overfeed, sleepdis, scarrate, poop, pwr, heffort;//게임 내부에서 동작할 변수들
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

    private void initializeImageViews() {
        battle_quest_com = findViewById(R.id.battle_quest_com);
        battle_arrow_quest = findViewById(R.id.battle_arrow_quest);
        battle_arrow_com = findViewById(R.id.battle_arrow_com);

        enemy_digimon_battle_normal = findViewById(R.id.enemy_digimon_battle_normal);
        enemy_digimon_battle_open = findViewById(R.id.enemy_digimon_battle_open);
        enemy_digimon_battle_close = findViewById(R.id.enemy_digimon_battle_close);

        digimon_battle_normal = findViewById(R.id.digimon_battle_normal);
        digimon_battle_open = findViewById(R.id.digimon_battle_open);
        digimon_battle_close = findViewById(R.id.digimon_battle_close);

        battle_screen = findViewById(R.id.battle_screen);

        battle_attack_effect_first = findViewById(R.id.battle_attack_effect_first);
        battle_attack_effect_second = findViewById(R.id.battle_attack_effect_second);
        battle_attack_effect_third = findViewById(R.id.battle_attack_effect_third);

        enemy_battle_attack_effect_first = findViewById(R.id.enemy_battle_attack_effect_first);
        enemy_battle_attack_effect_second = findViewById(R.id.enemy_battle_attack_effect_second);
        enemy_battle_attack_effect_third = findViewById(R.id.enemy_battle_attack_effect_third);

        battle_hit_effect_first = findViewById(R.id.battle_hit_effect_first);
        battle_hit_effect_second = findViewById(R.id.battle_hit_effect_second);

        digimon_battle_win_down = findViewById(R.id.digimon_battle_win_down);
        digimon_battle_win_up = findViewById(R.id.digimon_battle_win_up);
        digimon_battle_win_sun = findViewById(R.id.digimon_battle_win_sun);
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
                handler.removeCallbacksAndMessages(null);
                MySoundPlayer.stop();

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
        winMotion();
    }

    private void pageTwo() {
        resetBattleViewsVisibility();
    }

    private void winMotion() {
        resetBattleViewsVisibility();

        MySoundPlayer.play(MySoundPlayer.sound7);

        runDelayedAnimation(0, enemy_digimon_battle_normal);
        runDelayedAnimation(800, enemy_digimon_battle_open);

        runDelayedAnimation(1600, digimon_battle_normal);
        runDelayedAnimation(2400, digimon_battle_open);

        runDelayedAnimation(3200, battle_screen);

        //첫번째 공격
        runDelayedAnimation(4200, digimon_battle_close);
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound3), 4700);
        handler.postDelayed(() -> effectChange(true), 4700);
        runDelayedAnimation(4700, digimon_battle_open, battle_attack_effect_first);
        runDelayedAnimation(5200, digimon_battle_open, battle_attack_effect_second);
        runDelayedAnimation(5700, digimon_battle_open, battle_attack_effect_third);

        runDelayedAnimation(6200, enemy_digimon_battle_close, enemy_battle_attack_effect_third);
        runDelayedAnimation(6700, enemy_digimon_battle_close, enemy_battle_attack_effect_second);
        runDelayedAnimation(7200, enemy_digimon_battle_close, enemy_battle_attack_effect_first);

        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound4), 7400);
        runDelayedAnimation(7700, battle_hit_effect_first);
        runDelayedAnimation(7900, battle_hit_effect_second);
        runDelayedAnimation(8100, battle_hit_effect_first);
        runDelayedAnimation(8300, battle_hit_effect_second);
        runDelayedAnimation(8500, battle_hit_effect_first);

        //적 첫번째 공격
        runDelayedAnimation(8700, enemy_digimon_battle_close);
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound3), 9200);
        handler.postDelayed(() -> effectChange(false), 9200);
        runDelayedAnimation(9200, enemy_digimon_battle_open, enemy_battle_attack_effect_first);
        runDelayedAnimation(9700, enemy_digimon_battle_open, enemy_battle_attack_effect_second);
        runDelayedAnimation(10200, enemy_digimon_battle_open, enemy_battle_attack_effect_third);

        runDelayedAnimation(10700, digimon_battle_close, battle_attack_effect_third);
        runDelayedAnimation(11200, digimon_battle_close, battle_attack_effect_second);
        runDelayedAnimation(11700, digimon_battle_close, battle_attack_effect_first);
        runDelayedAnimation(12200, digimon_battle_close, true);

        //두번째 공격
        runDelayedAnimation(12700, digimon_battle_close);
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound3), 12700);
        handler.postDelayed(() -> effectChange(true), 12700);
        runDelayedAnimation(13200, digimon_battle_open, battle_attack_effect_first);
        runDelayedAnimation(13700, digimon_battle_open, battle_attack_effect_second);
        runDelayedAnimation(14200, digimon_battle_open, battle_attack_effect_third);

        runDelayedAnimation(14700, enemy_digimon_battle_close, enemy_battle_attack_effect_third);
        runDelayedAnimation(15200, enemy_digimon_battle_close, enemy_battle_attack_effect_second);
        runDelayedAnimation(15700, enemy_digimon_battle_close, enemy_battle_attack_effect_first);

        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound4), 15900);
        runDelayedAnimation(16200, battle_hit_effect_first);
        runDelayedAnimation(16400, battle_hit_effect_second);
        runDelayedAnimation(16600, battle_hit_effect_first);
        runDelayedAnimation(16800, battle_hit_effect_second);
        runDelayedAnimation(17000, battle_hit_effect_first);

        //적 두번째 공격
        runDelayedAnimation(17200, enemy_digimon_battle_close);
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound3), 17700);
        handler.postDelayed(() -> effectChange(false), 17700);
        runDelayedAnimation(17700, enemy_digimon_battle_open, enemy_battle_attack_effect_first);
        runDelayedAnimation(18200, enemy_digimon_battle_open, enemy_battle_attack_effect_second);
        runDelayedAnimation(18700, enemy_digimon_battle_open, enemy_battle_attack_effect_third);

        runDelayedAnimation(19200, digimon_battle_close, battle_attack_effect_third);
        runDelayedAnimation(19700, digimon_battle_close, battle_attack_effect_second);
        runDelayedAnimation(20200, digimon_battle_close, battle_attack_effect_first);
        runDelayedAnimation(20700, digimon_battle_close, true);

        //세번째 공격
        runDelayedAnimation(21200, digimon_battle_close);
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound3), 21200);
        handler.postDelayed(() -> effectChange(true), 21200);
        runDelayedAnimation(21700, digimon_battle_open, battle_attack_effect_first);
        runDelayedAnimation(22200, digimon_battle_open, battle_attack_effect_second);
        runDelayedAnimation(22700, digimon_battle_open, battle_attack_effect_third);

        runDelayedAnimation(23200, enemy_digimon_battle_close, enemy_battle_attack_effect_third);
        runDelayedAnimation(23700, enemy_digimon_battle_close, enemy_battle_attack_effect_second);
        runDelayedAnimation(24200, enemy_digimon_battle_close, enemy_battle_attack_effect_first);

        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound4), 24400);
        runDelayedAnimation(24700, battle_hit_effect_first);
        runDelayedAnimation(24900, battle_hit_effect_second);
        runDelayedAnimation(25100, battle_hit_effect_first);
        runDelayedAnimation(25300, battle_hit_effect_second);
        runDelayedAnimation(25500, battle_hit_effect_first);

        //승리 모션
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound5), 25700);
        runDelayedAnimation(25700, digimon_battle_win_down);
        runDelayedAnimation(26200, digimon_battle_win_up, digimon_battle_win_sun);
        runDelayedAnimation(26700, digimon_battle_win_down);
        runDelayedAnimation(27200, digimon_battle_win_up, digimon_battle_win_sun);
        runDelayedAnimation(27700, digimon_battle_win_down);
        runDelayedAnimation(28200, digimon_battle_win_up, digimon_battle_win_sun);
        runDelayedAnimation(28700, digimon_battle_win_down);
        runDelayedAnimation(29200, digimon_battle_win_up, digimon_battle_win_sun);

        handler.postDelayed(() -> closeActivity(), 29700);
    }

    private void closeActivity() {
        handler.removeCallbacksAndMessages(null);
        MySoundPlayer.stop();

        Intent intent = new Intent(BattleActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//액티비티 전환시 애니메이션 없애기
        intent.putExtra("INT_VALUE_KEY", sendTapIndex);
        startActivity(intent);
        finish();//현재 액티비티 종료
    }

    private boolean resultQuestFight() {
        int finalValue = getRandomValue() + pwr + (heffort / 100);//랜덤값에서 보정치값을 더해 승부 결과 계산
        boolean win;
        if (finalValue > 30) {//최종값이 30을 넘으면
            win = true;//승리
        } else {
            win = false;//패배
        }
        return win;
    }

    // 랜덤 값을 생성하는 함수
    private static int getRandomValue() {
        Random random = new Random();
        return random.nextInt(101); // 0부터 100까지의 값을 랜덤으로 생성
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

    private void runDelayedAnimation(int delay, final ImageView imageView) {
        handler.postDelayed(() -> animateDigimon(imageView), delay);
    }

    private void runDelayedAnimation(int delay, final ImageView imageView, final boolean isInverted) {
        handler.postDelayed(() -> animateDigimon(imageView, isInverted), delay);
    }

    private void runDelayedAnimation(int delay, final ImageView imageView, final ImageView imageView2) {
        handler.postDelayed(() -> animateDigimon(imageView, imageView2), delay);
    }

    private void animateDigimon(ImageView imageView) {
        resetBattleViewsVisibility();
        imageView.setVisibility(View.VISIBLE);
        imageView.setScaleX(1); // 방향 초기화
    }

    private void animateDigimon(ImageView imageView, boolean isInverted) {
        animateDigimon(imageView);
        if (isInverted) {
            imageView.setScaleX(-1);
        }
    }

    private void animateDigimon(ImageView imageView, ImageView imageView2) {
        resetBattleViewsVisibility();
        imageView.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        imageView.setScaleX(1); // 방향 초기화
        imageView2.setScaleX(1);
    }

    private void effectChange(boolean effect) {
        if (effect) {
            battle_attack_effect_first.setImageResource(getResources().getIdentifier("effect_attack_bubble", "drawable", getPackageName()));
            battle_attack_effect_second.setImageResource(getResources().getIdentifier("effect_attack_bubble", "drawable", getPackageName()));
            battle_attack_effect_third.setImageResource(getResources().getIdentifier("effect_attack_bubble", "drawable", getPackageName()));

            enemy_battle_attack_effect_first.setImageResource(getResources().getIdentifier("effect_attack_bubble", "drawable", getPackageName()));
            enemy_battle_attack_effect_second.setImageResource(getResources().getIdentifier("effect_attack_bubble", "drawable", getPackageName()));
            enemy_battle_attack_effect_third.setImageResource(getResources().getIdentifier("effect_attack_bubble", "drawable", getPackageName()));
        } else {
            battle_attack_effect_first.setImageResource(getResources().getIdentifier("effect_attack_thunder", "drawable", getPackageName()));
            battle_attack_effect_second.setImageResource(getResources().getIdentifier("effect_attack_thunder", "drawable", getPackageName()));
            battle_attack_effect_third.setImageResource(getResources().getIdentifier("effect_attack_thunder", "drawable", getPackageName()));

            enemy_battle_attack_effect_first.setImageResource(getResources().getIdentifier("effect_attack_thunder", "drawable", getPackageName()));
            enemy_battle_attack_effect_second.setImageResource(getResources().getIdentifier("effect_attack_thunder", "drawable", getPackageName()));
            enemy_battle_attack_effect_third.setImageResource(getResources().getIdentifier("effect_attack_thunder", "drawable", getPackageName()));
        }
    }

    private void resetBattleViewsVisibility() {
        battle_quest_com.setVisibility(View.INVISIBLE);
        battle_arrow_quest.setVisibility(View.INVISIBLE);
        battle_arrow_com.setVisibility(View.INVISIBLE);

        enemy_digimon_battle_normal.setVisibility(View.INVISIBLE);
        enemy_digimon_battle_open.setVisibility(View.INVISIBLE);
        enemy_digimon_battle_close.setVisibility(View.INVISIBLE);

        digimon_battle_normal.setVisibility(View.INVISIBLE);
        digimon_battle_open.setVisibility(View.INVISIBLE);
        digimon_battle_close.setVisibility(View.INVISIBLE);

        battle_screen.setVisibility(View.INVISIBLE);

        battle_attack_effect_first.setVisibility(View.INVISIBLE);
        battle_attack_effect_second.setVisibility(View.INVISIBLE);
        battle_attack_effect_third.setVisibility(View.INVISIBLE);

        enemy_battle_attack_effect_first.setVisibility(View.INVISIBLE);
        enemy_battle_attack_effect_second.setVisibility(View.INVISIBLE);
        enemy_battle_attack_effect_third.setVisibility(View.INVISIBLE);

        battle_hit_effect_first.setVisibility(View.INVISIBLE);
        battle_hit_effect_second.setVisibility(View.INVISIBLE);

        digimon_battle_win_down.setVisibility(View.INVISIBLE);
        digimon_battle_win_up.setVisibility(View.INVISIBLE);
        digimon_battle_win_sun.setVisibility(View.INVISIBLE);
    }
}