package com.example.watchtest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private ImageView digimon_battle_lose_down, digimon_battle_lose_up, digimon_battle_lose_first, digimon_battle_lose_second;

    private Button button1, button2, button3;

    int sendTapIndex = 4;  // 메인 액티비티로 전달할 값, 다시 메인 화면으로 돌아가도 이전 들어갔던 탭에 불이 들어오게 하기위한 변수

    private int index = 0;//탭별 인덱스 제어 변수

    //게임 내에서 사용될 변수들
    private int age, weight, hungry, strength, effort, health, winrate, winnum, fightnum;//상태창에서 사용될 변수들
    private int mistake, overfeed, sleepdis, scarrate, poop, pwr, heffort, scarnum;//게임 내부에서 동작할 변수들
    private boolean cure;//상처입었는지 판단용 변수

    //SharedPreferences 데이터 저장 관련 선언
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    //5판 3선에 사용될 변수
    int winTimes = 0;
    int loseTimes = 0;

    //블루투스 사용 용도
    private BluetoothManager bluetoothManager;
    //권한 요청 코드
    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT = 1;
    //블루투스 서버 기기 이름
    private static final String SERVER_DEVICE_NAME = "VPetWatch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBattleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //블루투스 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 권한을 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT);
        } else {
            // 권한이 이미 부여된 경우 블루투스 기능 사용
            // 여기에서 Bluetooth 기능을 초기화하고 사용할 수 있습니다.
        }

        //BluetoothManager 초기화
        bluetoothManager = new BluetoothManager(this, handler);
        //Bluetooth 지원 여부 확인
        if (bluetoothManager == null) {
            Toast.makeText(this, "Bluetooth를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeImageViews();
        initializeButton();
        initializePreferences();//SharedPreferences 초기화 메소드
        arrowChange(index);
    }

    private void connectToDevice(BluetoothDevice device) {
        bluetoothManager.connectToDevice(device);
    }

    private final Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothManager.MESSAGE_RECEIVED:
                    String receivedMessage = (String) msg.obj;
                    // TODO: Handle the received message
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //액티비티가 사라질 때 블루투스 및 서버 소켓 종료
        bluetoothManager.cancel();
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
        scarnum = preferences.getInt("scarnum", 0);
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

        digimon_battle_lose_down = findViewById(R.id.digimon_battle_lose_down);
        digimon_battle_lose_up = findViewById(R.id.digimon_battle_lose_up);
        digimon_battle_lose_first = findViewById(R.id.digimon_battle_lose_first);
        digimon_battle_lose_second = findViewById(R.id.digimon_battle_lose_second);
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
                if (isHandlerRunning) {
                    //핸들러가 동작 중일 때는 버튼 동작 중지
                } else {
                    MySoundPlayer.play(MySoundPlayer.sound1);
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
                isHandlerRunning = true;
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
        int tmpDelay;

        resetBattleViewsVisibility();

        MySoundPlayer.play(MySoundPlayer.sound7);

        runDelayedAnimation(0, enemy_digimon_battle_normal);
        runDelayedAnimation(800, enemy_digimon_battle_open);

        runDelayedAnimation(1600, digimon_battle_normal);
        runDelayedAnimation(2400, digimon_battle_open);

        runDelayedAnimation(3200, battle_screen);

        tmpDelay = 4000;

        //5판 3선
        for (int i = 0; i < 5; i++) {
            if (resultQuestFight()) {
                tmpDelay = attackSuccessMotion(tmpDelay);
                tmpDelay = defenseSuccessMotion(tmpDelay);
            } else {
                tmpDelay = attackFailMotion(tmpDelay);
                tmpDelay = defenseFailMotion(tmpDelay);
            }
        }
    }

    private void pageTwo() {
        resetBattleViewsVisibility();

        //Bluetooth 활성화 확인
        bluetoothManager.enableBluetooth();
        Log.d("bluetoothManager", "enableBluetooth");

        //Bluetooth 서버 시작
        bluetoothManager.startServer(SERVER_DEVICE_NAME);
        Log.d("bluetoothManager", "startServer");

        //Bluetooth 검색 및 시작
        bluetoothManager.startDiscovery(SERVER_DEVICE_NAME);
        Log.d("bluetoothManager", "startDiscovery : " + bluetoothManager.startDiscovery(SERVER_DEVICE_NAME));
        ;
    }

    private int attackSuccessMotion(int delayTime) {
        //공격 성공 모션
        runDelayedAnimation(delayTime, digimon_battle_close);
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound3), delayTime + 500);
        handler.postDelayed(() -> effectChange(true), delayTime + 500);
        runDelayedAnimation(delayTime + 500, digimon_battle_open, battle_attack_effect_first);
        runDelayedAnimation(delayTime + 1000, digimon_battle_open, battle_attack_effect_second);
        runDelayedAnimation(delayTime + 1500, digimon_battle_open, battle_attack_effect_third);

        runDelayedAnimation(delayTime + 2000, enemy_digimon_battle_close, enemy_battle_attack_effect_third);
        runDelayedAnimation(delayTime + 2500, enemy_digimon_battle_close, enemy_battle_attack_effect_second);
        runDelayedAnimation(delayTime + 3000, enemy_digimon_battle_close, enemy_battle_attack_effect_first);

        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound4), delayTime + 3200);
        runDelayedAnimation(delayTime + 3500, battle_hit_effect_first);
        runDelayedAnimation(delayTime + 3700, battle_hit_effect_second);
        runDelayedAnimation(delayTime + 3900, battle_hit_effect_first);
        runDelayedAnimation(delayTime + 4100, battle_hit_effect_second);
        runDelayedAnimation(delayTime + 4300, battle_hit_effect_first);

        handler.postDelayed(() -> winTimes++, delayTime + 4500);
        handler.postDelayed(() -> Log.d("win", "winTimes : " + winTimes), delayTime + 4500);
        handler.postDelayed(() -> {
            if (winTimes == 3) {
                Log.d("win", "승리 분기진입");
                handler.removeCallbacksAndMessages(null);
                MySoundPlayer.stop();

                finalWinMotion(0);
            }
        }, delayTime + 4500);

        return delayTime + 4500;
    }

    private int defenseSuccessMotion(int delayTime) {
        //방어 성공 모션
        runDelayedAnimation(delayTime, enemy_digimon_battle_close);
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound3), delayTime + 500);
        handler.postDelayed(() -> effectChange(false), delayTime + 500);
        runDelayedAnimation(delayTime + 500, enemy_digimon_battle_open, enemy_battle_attack_effect_first);
        runDelayedAnimation(delayTime + 1000, enemy_digimon_battle_open, enemy_battle_attack_effect_second);
        runDelayedAnimation(delayTime + 1500, enemy_digimon_battle_open, enemy_battle_attack_effect_third);

        runDelayedAnimation(delayTime + 2000, digimon_battle_close, battle_attack_effect_third);
        runDelayedAnimation(delayTime + 2500, digimon_battle_close, battle_attack_effect_second);
        runDelayedAnimation(delayTime + 3000, digimon_battle_close, battle_attack_effect_first);
        runDelayedAnimation(delayTime + 3500, digimon_battle_close, true);

        return delayTime + 4000;
    }

    private int attackFailMotion(int delayTime) {
        //공격 실패 모션
        runDelayedAnimation(delayTime, digimon_battle_close);
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound3), delayTime + 500);
        handler.postDelayed(() -> effectChange(true), delayTime + 500);
        runDelayedAnimation(delayTime + 500, digimon_battle_open, battle_attack_effect_first);
        runDelayedAnimation(delayTime + 1000, digimon_battle_open, battle_attack_effect_second);
        runDelayedAnimation(delayTime + 1500, digimon_battle_open, battle_attack_effect_third);

        runDelayedAnimation(delayTime + 2000, enemy_digimon_battle_close, enemy_battle_attack_effect_third);
        runDelayedAnimation(delayTime + 2500, enemy_digimon_battle_close, enemy_battle_attack_effect_second);
        runDelayedAnimation(delayTime + 3000, enemy_digimon_battle_close, enemy_battle_attack_effect_first);
        runDelayedAnimation(delayTime + 3500, enemy_digimon_battle_close, true);

        return delayTime + 4000;
    }

    private int defenseFailMotion(int delayTime) {
        //방어 실패 모션
        runDelayedAnimation(delayTime, enemy_digimon_battle_close);
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound3), delayTime + 500);
        handler.postDelayed(() -> effectChange(false), delayTime + 500);
        runDelayedAnimation(delayTime + 500, enemy_digimon_battle_open, enemy_battle_attack_effect_first);
        runDelayedAnimation(delayTime + 1000, enemy_digimon_battle_open, enemy_battle_attack_effect_second);
        runDelayedAnimation(delayTime + 1500, enemy_digimon_battle_open, enemy_battle_attack_effect_third);

        runDelayedAnimation(delayTime + 2000, digimon_battle_close, battle_attack_effect_third);
        runDelayedAnimation(delayTime + 2500, digimon_battle_close, battle_attack_effect_second);
        runDelayedAnimation(delayTime + 3000, digimon_battle_close, battle_attack_effect_first);

        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound4), delayTime + 3200);
        runDelayedAnimation(delayTime + 3500, battle_hit_effect_first);
        runDelayedAnimation(delayTime + 3700, battle_hit_effect_second);
        runDelayedAnimation(delayTime + 3900, battle_hit_effect_first);
        runDelayedAnimation(delayTime + 4100, battle_hit_effect_second);
        runDelayedAnimation(delayTime + 4300, battle_hit_effect_first);

        handler.postDelayed(() -> loseTimes++, delayTime + 4500);
        handler.postDelayed(() -> Log.d("win", "loseTimes : " + loseTimes), delayTime + 4500);
        handler.postDelayed(() -> {
            if (loseTimes == 3) {
                Log.d("win", "패배 분기진입");
                handler.removeCallbacksAndMessages(null);
                MySoundPlayer.stop();

                finalLoseMotion(0);
            }
        }, delayTime + 4500);

        return delayTime + 4500;
    }

    private void finalWinMotion(int delayTime) {
        //결과 계산 후 기입
        health--;
        fightnum++;
        winnum++;
        winrate = winRateResult(fightnum, winnum);

        if ((scarrate / 10) > getRandomValue()) {//상처확률에 따른 상처 결과
            scarResult();
        }

        editor.putInt("health", health);
        editor.putInt("fightnum", fightnum);
        editor.putInt("winnum", winnum);
        editor.putInt("winrate", winrate);
        editor.apply();

        //승리 모션
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound5), delayTime);
        runDelayedAnimation(delayTime, digimon_battle_win_down);
        runDelayedAnimation(delayTime + 500, digimon_battle_win_up, digimon_battle_win_sun);
        runDelayedAnimation(delayTime + 1000, digimon_battle_win_down);
        runDelayedAnimation(delayTime + 1500, digimon_battle_win_up, digimon_battle_win_sun);
        runDelayedAnimation(delayTime + 2000, digimon_battle_win_down);
        runDelayedAnimation(delayTime + 2500, digimon_battle_win_up, digimon_battle_win_sun);
        runDelayedAnimation(delayTime + 3000, digimon_battle_win_down);
        runDelayedAnimation(delayTime + 3500, digimon_battle_win_up, digimon_battle_win_sun);

        handler.postDelayed(() -> closeActivity(), 4000);
    }

    private void finalLoseMotion(int delayTime) {
        //결과 계산 후 기입
        health--;
        fightnum++;
        winrate = winRateResult(fightnum, winnum);

        if (scarrate > getRandomValue()) {//상처확률에 따른 상처 결과
            scarResult();
        }

        editor.putInt("health", health);
        editor.putInt("fightnum", fightnum);
        editor.putInt("winrate", winrate);
        editor.apply();

        //패배 모션
        handler.postDelayed(() -> MySoundPlayer.play(MySoundPlayer.sound6), delayTime);
        runDelayedAnimation(delayTime, digimon_battle_lose_down, digimon_battle_lose_first);
        runDelayedAnimation(delayTime + 500, digimon_battle_lose_up, digimon_battle_lose_second);
        runDelayedAnimation(delayTime + 1000, digimon_battle_lose_down, digimon_battle_lose_first);
        runDelayedAnimation(delayTime + 1500, digimon_battle_lose_up, digimon_battle_lose_second);
        runDelayedAnimation(delayTime + 2000, digimon_battle_lose_down, digimon_battle_lose_first);
        runDelayedAnimation(delayTime + 2500, digimon_battle_lose_up, digimon_battle_lose_second);
        runDelayedAnimation(delayTime + 3000, digimon_battle_lose_down, digimon_battle_lose_first);
        runDelayedAnimation(delayTime + 3500, digimon_battle_lose_up, digimon_battle_lose_second);

        handler.postDelayed(() -> closeActivity(), 4000);
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

    private int winRateResult(int fightnum, int winnum) {
        return (int) ((winnum / (float) fightnum) * 100);
    }

    private void scarResult() {
        cure = true;
        scarnum++;
        editor.putBoolean("cure", cure);
        editor.putInt("scarnum", scarnum);
        editor.apply();
    }

    private boolean resultQuestFight() {
        int finalValue = getRandomValue() + pwr + (heffort / 100);//랜덤값에서 보정치값을 더해 승부 결과 계산
        boolean win;
        if (finalValue > 40) {//최종값이 40을 넘으면
            win = true;//승리
        } else {
            win = false;//패배
        }
        Log.d("win", String.valueOf(win));
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

        digimon_battle_lose_down.setVisibility(View.INVISIBLE);
        digimon_battle_lose_up.setVisibility(View.INVISIBLE);
        digimon_battle_lose_first.setVisibility(View.INVISIBLE);
        digimon_battle_lose_second.setVisibility(View.INVISIBLE);
    }
}