package com.example.watchtest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.example.watchtest.databinding.ActivityMainBinding;

import java.util.Calendar;

public class MainActivity extends Activity {
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    boolean isHandlerRunning = false;

    private ActivityMainBinding binding;
    private ImageView digimon0, digimonUpP1, digimonDownP2, digimonDownP3, digimonDownP4, digimonEmotionP4, digimonUpM1, digimonDownM2, digimonDownM3, digimonDownM4, digimonEmotionM4;
    private ImageView digimon_hate_emotion0, digimon_sick_emotion_down, digimon_sick_emotion_up, effect_sick_first, effect_sick_second;
    private ImageView digimon_cure_emotion_down, digimon_cure_emotion_up, effect_cure_first, effect_cure_second;
    private ImageView digimon_sleep_down, digimon_sleep_up, effect_sleep_on_first, effect_sleep_on_second, effect_sleep_off, effect_sleep_off_first, effect_sleep_off_second;
    private ImageView digimon_dead;
    private ImageView uiBlackStatus, uiBlackFood, uiBlackTraining, uiBlackBattle, uiBlackPoop, uiBlackLight, uiBlackCure, uiBlackCall;
    private Button button1, button2, button3;

    private Intent intent;

    private int index = 0;//탭별 인덱스 제어 변수

    //게임 내에서 사용될 변수들
    private int age, weight, hungry, strength, effort, health, winrate, winnum, fightnum;//상태창에서 사용될 변수들
    private int mistake, overfeed, sleepdis, scarrate, poop, pwr, heffort, scarnum;//게임 내부에서 동작할 변수들
    private boolean cure, sleep, lightoff;//상처입었는지 판단용 변수, 잠자는지 아닌지 판단용 변수, 불을 껐는지 안껐는지 판단용 변수

    //SharedPreferences 데이터 저장 관련 선언
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static final int JOB_ID = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeImageViews();//이미지뷰 초기화
        initializeButton();//버튼 초기화
        MySoundPlayer.initSounds(getApplicationContext());//사운드 플레이어 초기화
        resetUiViewsVisibility();//액티비티 시작과 동시에 검은색 ui 안보이게 설정
        resetDigimonViewsVisibility();//액티비티 시작과 동시에 이미지뷰 초기화
        initializePreferences();//SharedPreferences 초기화 메소드

        Intent intent = getIntent();
        index = intent.getIntExtra("INT_VALUE_KEY", 0);//특정 탭에서 나왔을때 탭의 커서를 받아오기 위한 인텐트

        checkNotificationPermission();//알림 허용체크
        checkLocationPermission();//위치 허용체크

        if (!ServiceUtils.isServiceRunning(this, TimerService.class)) {
            Log.d("MainActivity", "startService");
            Intent serviceIntent = new Intent(this, TimerService.class);
            startService(serviceIntent);
        }

        scheduleJob();
        Log.d("MainActivity", String.valueOf(sleep));
    }

    private void scheduleJob() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        // 특정 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 19);//오후7시
        calendar.set(Calendar.MINUTE, 0);
        long startTime = calendar.getTimeInMillis();

        // JobInfo 생성
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(this, MyJobService.class))
                .setRequiresCharging(false) // 충전 중에만 실행 여부
                .setPersisted(true) // 재부팅 후에도 유지 여부
                .setMinimumLatency(startTime) // 최소 지연 시간 설정
                .build();

        // Job 등록
        jobScheduler.schedule(jobInfo);
    }

    private void checkNotificationPermission() {
        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission();
        } else {
            // 알림 권한이 이미 허용된 경우 실행할 코드
        }
    }

    private void checkLocationPermission() {
        if (!isNotLocationPermissionGranted()) {
            requestLocationPermission();
        } else {
            // 알림 권한이 이미 허용된 경우 실행할 코드
        }
    }

    private boolean isNotificationPermissionGranted() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private boolean isNotLocationPermissionGranted() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestNotificationPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림 권한 필요");
        builder.setMessage("알림 권한이 필요합니다. 설정으로 이동하여 권한을 허용해주세요.");
        builder.setPositiveButton("설정으로 이동", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 권한이 거부된 경우 실행할 코드
                Toast.makeText(MainActivity.this, "알림 권한이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish(); // 앱 종료
            }
        });
        builder.show();
    }

    private void requestLocationPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 권한 필요");
        builder.setMessage("위치 권한이 필요합니다. 설정으로 이동하여 권한을 허용해주세요.");
        builder.setPositiveButton("설정으로 이동", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 권한이 거부된 경우 실행할 코드
                Toast.makeText(MainActivity.this, "위치 권한이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish(); // 앱 종료
            }
        });
        builder.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // 설정에서 돌아온 경우 알림 권한을 다시 확인
            checkNotificationPermission();
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
        sleep = preferences.getBoolean("sleep", false);
        lightoff = preferences.getBoolean("lightoff", false);
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

        digimon_hate_emotion0 = findViewById(R.id.digimon_hate_emotion0);

        digimon_sick_emotion_down = findViewById(R.id.digimon_sick_emotion_down);
        digimon_sick_emotion_up = findViewById(R.id.digimon_sick_emotion_up);
        effect_sick_first = findViewById(R.id.effect_sick_first);
        effect_sick_second = findViewById(R.id.effect_sick_second);

        digimon_cure_emotion_down = findViewById(R.id.digimon_cure_emotion_down);
        digimon_cure_emotion_up = findViewById(R.id.digimon_cure_emotion_up);
        effect_cure_first = findViewById(R.id.effect_cure_first);
        effect_cure_second = findViewById(R.id.effect_cure_second);

        digimon_sleep_down = findViewById(R.id.digimon_sleep_down);
        digimon_sleep_up = findViewById(R.id.digimon_sleep_up);
        effect_sleep_on_first = findViewById(R.id.effect_sleep_on_first);
        effect_sleep_on_second = findViewById(R.id.effect_sleep_on_second);
        effect_sleep_off = findViewById(R.id.effect_sleep_off);
        effect_sleep_off_first = findViewById(R.id.effect_sleep_off_first);
        effect_sleep_off_second = findViewById(R.id.effect_sleep_off_second);

        digimon_dead = findViewById(R.id.digimon_dead);

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
                if (scarnum == 20) {
                    //디지몬 죽음 상태일때는 버튼 동작 중지
                } else if (isHandlerRunning) {
                    //핸들러가 동작 중일 때는 버튼 동작 중지
                } else {
                    MySoundPlayer.play(MySoundPlayer.sound1);
                    //1번 버튼을 누를때 마다 인덱스 상승
                    if (index < 7) {//인덱스가 7이 넘어가면
                        index++;
                    } else {
                        index = 0;//0으로 다시 설정
                    }
                    moveTap(index);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scarnum == 20) {
                    //디지몬 죽음 상태일때는 버튼 동작 중지
                } else if (isHandlerRunning) {
                    //핸들러가 동작 중일 때는 버튼 동작 중지
                } else {
                    if (index != 0) {
                        MySoundPlayer.play(MySoundPlayer.sound1);
                        changeActivity(index);
                    }
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scarnum == 20) {
                    //디지몬 죽음 상태일때는 버튼 동작 중지
                } else if (index != 0) {
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
                if (lightoff) {
                } else if (cure) {
                    digimonStatusHate();
                } else {
                    intent = new Intent(MainActivity.this, FoodActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//액티비티 전환시 애니메이션 없애기
                    editor.putBoolean("sleep", false);
                    editor.apply();
                    startActivity(intent);
                    finish();//현재 액티비티 종료
                }
                break;
            case 3:
                if (lightoff) {
                } else if (cure) {
                    digimonStatusHate();
                } else {
                    intent = new Intent(MainActivity.this, TrainingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//액티비티 전환시 애니메이션 없애기
                    editor.putBoolean("sleep", false);
                    editor.apply();
                    startActivity(intent);
                    finish();//현재 액티비티 종료
                }
                break;
            case 4:
                if (lightoff) {
                } else if (health == 0 || cure) {
                    digimonStatusHate();
                } else {
                    intent = new Intent(MainActivity.this, BattleActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//액티비티 전환시 애니메이션 없애기
                    editor.putBoolean("sleep", false);
                    editor.apply();
                    startActivity(intent);
                    finish();//현재 액티비티 종료
                }
                break;
            case 5:
                if (lightoff) {
                } else {
                    digimonStatusHate();
                }
                break;
            case 6:
                if (sleep && (lightoff == false)) {
                    lightoff = true;
                    editor.putBoolean("lightoff", lightoff);
                    editor.apply();
                    stopDigimonStatusUpdates();
                    startDigimonStatusUpdates();
                } else if (lightoff) {
                    lightoff = false;
                    editor.putBoolean("lightoff", lightoff);
                    editor.apply();
                    stopDigimonStatusUpdates();
                    startDigimonStatusUpdates();
                } else {
                    digimonStatusHate();
                }

                break;
            case 7:
                if (lightoff) {
                } else if (cure) {
                    digimonStatusCure();
                } else {
                    digimonStatusHate();
                }
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
        isHandlerRunning = false;
        if (scarnum == 20) {
            digimon_dead.setVisibility(View.VISIBLE);

            //죽었을 시 모든 정보 리셋
            editor.putInt("age", 0);
            editor.putInt("weight", 5);
            editor.putInt("hungry", 0);
            editor.putInt("strength", 0);
            editor.putInt("effort", 0);
            editor.putInt("health", 0);
            editor.putInt("winrate", 0);
            editor.putInt("winnum", 0);
            editor.putInt("fightnum", 0);
            editor.putInt("mistake", 0);
            editor.putInt("overfeed", 0);
            editor.putInt("sleepdis", 0);
            editor.putInt("scarrate", 0);
            editor.putInt("poop", 0);
            editor.putInt("pwr", 10);
            editor.putInt("heffort", 0);
            editor.putInt("scarnum", 0);
            editor.putBoolean("cure", false);
            editor.putBoolean("sleep", false);
            editor.putBoolean("lightoff", false);
            editor.apply();

        } else if (cure) {
            digimonStatusSick();

        } else if (lightoff) {
            digimonSleepOff();
        } else if (sleep) {
            digimonSleepOn();
        } else {

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
    }

    //불을 끼고 잠든 모션
    private void digimonSleepOff() {
        runDelayedAnimation(0, effect_sleep_off, effect_sleep_off_first);
        runDelayedAnimation(1000, effect_sleep_off, effect_sleep_off_second);
        runDelayedAnimation(2000, effect_sleep_off, effect_sleep_off_first);
        runDelayedAnimation(3000, effect_sleep_off, effect_sleep_off_second);
        runDelayedAnimation(4000, effect_sleep_off, effect_sleep_off_first);
        runDelayedAnimation(5000, effect_sleep_off, effect_sleep_off_second);
        runDelayedAnimation(6000, effect_sleep_off, effect_sleep_off_first);
        runDelayedAnimation(7000, effect_sleep_off, effect_sleep_off_second);
        runDelayedAnimation(8000, effect_sleep_off, effect_sleep_off_first);
        runDelayedAnimation(9000, effect_sleep_off, effect_sleep_off_second);
        runDelayedAnimation(10000, effect_sleep_off, effect_sleep_off_first);
        runDelayedAnimation(11000, effect_sleep_off, effect_sleep_off_second);
        runDelayedAnimation(12000, effect_sleep_off, effect_sleep_off_first);
        runDelayedAnimation(13000, effect_sleep_off, effect_sleep_off_second);
        runDelayedAnimation(14000, effect_sleep_off, effect_sleep_off_first);
        runDelayedAnimation(15000, effect_sleep_off, effect_sleep_off_second);
        runDelayedAnimation(16000, effect_sleep_off, effect_sleep_off_first);
        runDelayedAnimation(17000, effect_sleep_off, effect_sleep_off_second);
        runDelayedAnimation(18000, effect_sleep_off, effect_sleep_off_first);
        runDelayedAnimation(18500, effect_sleep_off, effect_sleep_off_second);


    }

    //불을 키고 잠든 모션
    private void digimonSleepOn() {
        runDelayedAnimation(0, digimon_sleep_down, effect_sleep_on_first);
        runDelayedAnimation(1000, digimon_sleep_down, effect_sleep_on_second);
        runDelayedAnimation(2000, digimon_sleep_up, effect_sleep_on_first);
        runDelayedAnimation(3000, digimon_sleep_up, effect_sleep_on_second);
        runDelayedAnimation(4000, digimon_sleep_down, effect_sleep_on_first);
        runDelayedAnimation(5000, digimon_sleep_down, effect_sleep_on_second);
        runDelayedAnimation(6000, digimon_sleep_up, effect_sleep_on_first);
        runDelayedAnimation(7000, digimon_sleep_up, effect_sleep_on_second);
        runDelayedAnimation(8000, digimon_sleep_down, effect_sleep_on_first);
        runDelayedAnimation(9000, digimon_sleep_down, effect_sleep_on_second);
        runDelayedAnimation(10000, digimon_sleep_up, effect_sleep_on_first);
        runDelayedAnimation(11000, digimon_sleep_up, effect_sleep_on_second);
        runDelayedAnimation(12000, digimon_sleep_down, effect_sleep_on_first);
        runDelayedAnimation(13000, digimon_sleep_down, effect_sleep_on_second);
        runDelayedAnimation(14000, digimon_sleep_up, effect_sleep_on_first);
        runDelayedAnimation(15000, digimon_sleep_up, effect_sleep_on_second);
        runDelayedAnimation(16000, digimon_sleep_down, effect_sleep_on_first);
        runDelayedAnimation(17000, digimon_sleep_down, effect_sleep_on_second);
        runDelayedAnimation(18000, digimon_sleep_up, effect_sleep_on_first);
        runDelayedAnimation(18500, digimon_sleep_up, effect_sleep_on_second);
    }

    private void digimonStatusHate() {
        isHandlerRunning = true;
        stopDigimonStatusUpdates();

        runDelayedAnimation(0, digimon_hate_emotion0);
        runDelayedAnimation(500, digimon_hate_emotion0, true);
        runDelayedAnimation(1000, digimon_hate_emotion0);
        runDelayedAnimation(1500, digimon_hate_emotion0, true);
        runDelayedAnimation(2000, digimon_hate_emotion0);
        runDelayedAnimation(2500, digimon_hate_emotion0, true);
        runDelayedAnimation(3000, digimon_hate_emotion0);

        handler.postDelayed(() -> startDigimonStatusUpdates(), 3500);
    }

    private void digimonStatusCure() {
        isHandlerRunning = true;
        stopDigimonStatusUpdates();

        MySoundPlayer.play(MySoundPlayer.sound6);
        runDelayedAnimation(0, digimon_cure_emotion_down, effect_cure_first);
        runDelayedAnimation(500, digimon_cure_emotion_up, effect_cure_second);
        runDelayedAnimation(1000, digimon_cure_emotion_down, effect_cure_first);
        runDelayedAnimation(1500, digimon_cure_emotion_up, effect_cure_second);
        runDelayedAnimation(2000, digimon_cure_emotion_down, effect_cure_first);
        runDelayedAnimation(2500, digimon_cure_emotion_up, effect_cure_second);
        runDelayedAnimation(3000, digimon_cure_emotion_down, effect_cure_first);
        runDelayedAnimation(3500, digimon_cure_emotion_up, effect_cure_second);

        handler.postDelayed(() -> {
            cure = false;
            editor.putBoolean("cure", cure);
            editor.apply();
        }, 4000);
        handler.postDelayed(() -> startDigimonStatusUpdates(), 4000);
    }

    private void digimonStatusSick() {
        runDelayedAnimation(0, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(1000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(1500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(2000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(2500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(3000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(3500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(4000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(4500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(5000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(5500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(6000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(6500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(7000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(7500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(8000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(8500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(9000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(9500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(10000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(10500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(11000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(11500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(12000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(12500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(13000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(13500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(14000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(14500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(15000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(15500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(16000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(16500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(17000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(17500, digimon_sick_emotion_up, effect_sick_second);
        runDelayedAnimation(18000, digimon_sick_emotion_down, effect_sick_first);
        runDelayedAnimation(18500, digimon_sick_emotion_up, effect_sick_second);
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

    private void animateDigimon(ImageView imageView, ImageView imageView2) {
        resetDigimonViewsVisibility();
        imageView.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        imageView.setScaleX(1); // 방향 초기화
        imageView2.setScaleX(1);
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
        digimon_hate_emotion0.setVisibility(View.INVISIBLE);
        digimon_sick_emotion_down.setVisibility(View.INVISIBLE);
        digimon_sick_emotion_up.setVisibility(View.INVISIBLE);
        effect_sick_first.setVisibility(View.INVISIBLE);
        effect_sick_second.setVisibility(View.INVISIBLE);
        digimon_cure_emotion_down.setVisibility(View.INVISIBLE);
        digimon_cure_emotion_up.setVisibility(View.INVISIBLE);
        effect_cure_first.setVisibility(View.INVISIBLE);
        effect_cure_second.setVisibility(View.INVISIBLE);
        digimon_dead.setVisibility(View.INVISIBLE);
        digimon_sleep_down.setVisibility(View.INVISIBLE);
        digimon_sleep_up.setVisibility(View.INVISIBLE);
        effect_sleep_on_first.setVisibility(View.INVISIBLE);
        effect_sleep_on_second.setVisibility(View.INVISIBLE);
        effect_sleep_off.setVisibility(View.INVISIBLE);
        effect_sleep_off_first.setVisibility(View.INVISIBLE);
        effect_sleep_off_second.setVisibility(View.INVISIBLE);
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