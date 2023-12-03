package com.example.watchtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.watchtest.databinding.ActivityStatusBinding;

public class StatusActivity extends Activity {

    private ActivityStatusBinding binding;
    private ImageView status_scale, status_age_gram, status_age_first, status_age_second, status_gram_first, status_gram_second;
    private ImageView status_hungry, status_hungry_heart_one, status_hungry_heart_two, status_hungry_heart_three, status_hungry_heart_four;
    private ImageView status_strength, status_strength_heart_one, status_strength_heart_two, status_strength_heart_three, status_strength_heart_four;
    private ImageView status_effort, status_effort_heart_one, status_effort_heart_two, status_effort_heart_three, status_effort_heart_four;
    private ImageView status_health, status_progressbar_bottom, status_progressbar_mid_left, status_progressbar_mid_right, status_progressbar_top;
    private ImageView status_progressbar_gauge[] = new ImageView[16];
    private ImageView status_winrate, status_winrate_first_one, status_winrate_second_one, status_winrate_percentage;

    private Button button1, button2, button3;

    int sendTapIndex = 1;  // 메인 액티비티로 전달할 값, 다시 메인 화면으로 돌아가도 이전 들어갔던 탭에 불이 들어오게 하기위한 변수

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

        binding = ActivityStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeImageViews();
        initializeButton();
        initializePreferences();//SharedPreferences 초기화 메소드
        pageOne();
    }

    private void initializeImageNumber(int num, ImageView imageView1, ImageView imageView2) {
        // 초기 숫자 이미지 표시
        int initialDigit1 = num / 10;
        int initialDigit2 = num % 10;
        if (num > 9) {
            imageView1.setVisibility(View.VISIBLE);
            imageView1.setImageResource(getImageResourceId(initialDigit1));
        } else {
            imageView1.setVisibility(View.INVISIBLE);
        }
        imageView2.setVisibility(View.VISIBLE);
        imageView2.setImageResource(getImageResourceId(initialDigit2));
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
        status_scale = findViewById(R.id.status_scale);
        status_age_gram = findViewById(R.id.status_age_gram);
        status_age_first = findViewById(R.id.status_age_first_one);
        status_age_second = findViewById(R.id.status_age_second_one);
        status_gram_first = findViewById(R.id.status_gram_first_one);
        status_gram_second = findViewById(R.id.status_gram_second_one);

        status_hungry = findViewById(R.id.status_hungry);
        status_hungry_heart_one = findViewById(R.id.status_hungry_heart_one);
        status_hungry_heart_two = findViewById(R.id.status_hungry_heart_two);
        status_hungry_heart_three = findViewById(R.id.status_hungry_heart_three);
        status_hungry_heart_four = findViewById(R.id.status_hungry_heart_four);

        status_strength = findViewById(R.id.status_strength);
        status_strength_heart_one = findViewById(R.id.status_strength_heart_one);
        status_strength_heart_two = findViewById(R.id.status_strength_heart_two);
        status_strength_heart_three = findViewById(R.id.status_strength_heart_three);
        status_strength_heart_four = findViewById(R.id.status_strength_heart_four);

        status_effort = findViewById(R.id.status_effort);
        status_effort_heart_one = findViewById(R.id.status_effort_heart_one);
        status_effort_heart_two = findViewById(R.id.status_effort_heart_two);
        status_effort_heart_three = findViewById(R.id.status_effort_heart_three);
        status_effort_heart_four = findViewById(R.id.status_effort_heart_four);

        status_health = findViewById(R.id.status_health);
        status_progressbar_bottom = findViewById(R.id.status_progressbar_bottom);
        status_progressbar_mid_left = findViewById(R.id.status_progressbar_mid_left);
        status_progressbar_mid_right = findViewById(R.id.status_progressbar_mid_right);
        status_progressbar_top = findViewById(R.id.status_progressbar_top);
        for (int i = 0; i < 16; i++) {//프로그래스바 아이디 매칭
            int resID = getResources().getIdentifier("status_progressbar_gauge_" + (i + 1), "id", getPackageName());
            status_progressbar_gauge[i] = findViewById(resID);
        }

        status_winrate = findViewById(R.id.status_winrate);
        status_winrate_first_one = findViewById(R.id.status_winrate_first_one);
        status_winrate_second_one = findViewById(R.id.status_winrate_second_one);
        status_winrate_percentage = findViewById(R.id.status_winrate_percentage);
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
                if (index < 5) {//인덱스가 5가 넘어가면
                    index++;
                } else {
                    index = 0;//0으로 다시 설정
                }
                pageChange(index);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySoundPlayer.play(MySoundPlayer.sound1);
                //1번 버튼을 누를때 마다 인덱스 상승
                if (index < 5) {//인덱스가 5가 넘어가면
                    index++;
                } else {
                    index = 0;//0으로 다시 설정
                }
                pageChange(index);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySoundPlayer.play(MySoundPlayer.sound1);
                Intent intent = new Intent(StatusActivity.this, MainActivity.class);
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
            case 2:
                pageThree();
                break;
            case 3:
                pageFour();
                break;
            case 4:
                pageFive();
                break;
            case 5:
                pageSix();
                break;
            default:
                break;
        }
    }

    private void pageOne() {
        resetStatusViewsVisibility();
        status_scale.setVisibility(View.VISIBLE);
        status_age_gram.setVisibility(View.VISIBLE);
        initializeImageNumber(age, status_age_first, status_age_second);
        initializeImageNumber(weight, status_gram_first, status_gram_second);
    }

    private void pageTwo() {
        resetStatusViewsVisibility();
        status_hungry.setVisibility(View.VISIBLE);
        status_hungry_heart_one.setVisibility(View.VISIBLE);
        status_hungry_heart_two.setVisibility(View.VISIBLE);
        status_hungry_heart_three.setVisibility(View.VISIBLE);
        status_hungry_heart_four.setVisibility(View.VISIBLE);
        switch (hungry) {
            case 0:
                status_hungry_heart_one.setImageResource(R.drawable.status_heart_empty);
                status_hungry_heart_two.setImageResource(R.drawable.status_heart_empty);
                status_hungry_heart_three.setImageResource(R.drawable.status_heart_empty);
                status_hungry_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 1:
                status_hungry_heart_one.setImageResource(R.drawable.status_heart_full);
                status_hungry_heart_two.setImageResource(R.drawable.status_heart_empty);
                status_hungry_heart_three.setImageResource(R.drawable.status_heart_empty);
                status_hungry_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 2:
                status_hungry_heart_one.setImageResource(R.drawable.status_heart_full);
                status_hungry_heart_two.setImageResource(R.drawable.status_heart_full);
                status_hungry_heart_three.setImageResource(R.drawable.status_heart_empty);
                status_hungry_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 3:
                status_hungry_heart_one.setImageResource(R.drawable.status_heart_full);
                status_hungry_heart_two.setImageResource(R.drawable.status_heart_full);
                status_hungry_heart_three.setImageResource(R.drawable.status_heart_full);
                status_hungry_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 4:
                status_hungry_heart_one.setImageResource(R.drawable.status_heart_full);
                status_hungry_heart_two.setImageResource(R.drawable.status_heart_full);
                status_hungry_heart_three.setImageResource(R.drawable.status_heart_full);
                status_hungry_heart_four.setImageResource(R.drawable.status_heart_full);
                break;
            default:
                break;
        }
    }

    private void pageThree() {
        resetStatusViewsVisibility();
        status_strength.setVisibility(View.VISIBLE);
        status_strength_heart_one.setVisibility(View.VISIBLE);
        status_strength_heart_two.setVisibility(View.VISIBLE);
        status_strength_heart_three.setVisibility(View.VISIBLE);
        status_strength_heart_four.setVisibility(View.VISIBLE);
        switch (strength) {
            case 0:
                status_strength_heart_one.setImageResource(R.drawable.status_heart_empty);
                status_strength_heart_two.setImageResource(R.drawable.status_heart_empty);
                status_strength_heart_three.setImageResource(R.drawable.status_heart_empty);
                status_strength_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 1:
                status_strength_heart_one.setImageResource(R.drawable.status_heart_full);
                status_strength_heart_two.setImageResource(R.drawable.status_heart_empty);
                status_strength_heart_three.setImageResource(R.drawable.status_heart_empty);
                status_strength_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 2:
                status_strength_heart_one.setImageResource(R.drawable.status_heart_full);
                status_strength_heart_two.setImageResource(R.drawable.status_heart_full);
                status_strength_heart_three.setImageResource(R.drawable.status_heart_empty);
                status_strength_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 3:
                status_strength_heart_one.setImageResource(R.drawable.status_heart_full);
                status_strength_heart_two.setImageResource(R.drawable.status_heart_full);
                status_strength_heart_three.setImageResource(R.drawable.status_heart_full);
                status_strength_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 4:
                status_strength_heart_one.setImageResource(R.drawable.status_heart_full);
                status_strength_heart_two.setImageResource(R.drawable.status_heart_full);
                status_strength_heart_three.setImageResource(R.drawable.status_heart_full);
                status_strength_heart_four.setImageResource(R.drawable.status_heart_full);
                break;
            default:
                break;
        }
    }

    private void pageFour() {
        resetStatusViewsVisibility();
        status_effort.setVisibility(View.VISIBLE);
        status_effort_heart_one.setVisibility(View.VISIBLE);
        status_effort_heart_two.setVisibility(View.VISIBLE);
        status_effort_heart_three.setVisibility(View.VISIBLE);
        status_effort_heart_four.setVisibility(View.VISIBLE);
        switch (effort) {
            case 0:
            case 1:
            case 2:
            case 3:
                status_effort_heart_one.setImageResource(R.drawable.status_heart_empty);
                status_effort_heart_two.setImageResource(R.drawable.status_heart_empty);
                status_effort_heart_three.setImageResource(R.drawable.status_heart_empty);
                status_effort_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                status_effort_heart_one.setImageResource(R.drawable.status_heart_full);
                status_effort_heart_two.setImageResource(R.drawable.status_heart_empty);
                status_effort_heart_three.setImageResource(R.drawable.status_heart_empty);
                status_effort_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 8:
            case 9:
            case 10:
            case 11:
                status_effort_heart_one.setImageResource(R.drawable.status_heart_full);
                status_effort_heart_two.setImageResource(R.drawable.status_heart_full);
                status_effort_heart_three.setImageResource(R.drawable.status_heart_empty);
                status_effort_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 12:
            case 13:
            case 14:
            case 15:
                status_effort_heart_one.setImageResource(R.drawable.status_heart_full);
                status_effort_heart_two.setImageResource(R.drawable.status_heart_full);
                status_effort_heart_three.setImageResource(R.drawable.status_heart_full);
                status_effort_heart_four.setImageResource(R.drawable.status_heart_empty);
                break;
            case 16:
                status_effort_heart_one.setImageResource(R.drawable.status_heart_full);
                status_effort_heart_two.setImageResource(R.drawable.status_heart_full);
                status_effort_heart_three.setImageResource(R.drawable.status_heart_full);
                status_effort_heart_four.setImageResource(R.drawable.status_heart_full);
                break;
            default:
                break;
        }
    }

    private void pageFive() {
        resetStatusViewsVisibility();
        status_health.setVisibility(View.VISIBLE);
        status_progressbar_bottom.setVisibility(View.VISIBLE);
        status_progressbar_mid_left.setVisibility(View.VISIBLE);
        status_progressbar_mid_right.setVisibility(View.VISIBLE);
        status_progressbar_top.setVisibility(View.VISIBLE);

        for (int i = 1; i <= health; i++) {
            // 이미지뷰의 리소스 ID를 가져와서 활성화
            int imageViewId = getResources().getIdentifier("status_progressbar_gauge_" + i, "id", getPackageName());
            ImageView imageView = findViewById(imageViewId);
            imageView.setVisibility(View.VISIBLE);
        }

        for (int i = health + 1; i <= 16; i++) {
            // 이미지뷰의 리소스 ID를 가져와서 비활성화
            int imageViewId = getResources().getIdentifier("status_progressbar_gauge_" + i, "id", getPackageName());
            ImageView imageView = findViewById(imageViewId);
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    private void pageSix() {
        resetStatusViewsVisibility();
        status_winrate.setVisibility(View.VISIBLE);
        initializeImageNumber(winrate, status_winrate_first_one, status_winrate_second_one);
        status_winrate_percentage.setVisibility(View.VISIBLE);
    }

    private void resetStatusViewsVisibility() {
        status_scale.setVisibility(View.INVISIBLE);
        status_age_gram.setVisibility(View.INVISIBLE);
        status_age_first.setVisibility(View.INVISIBLE);
        status_age_second.setVisibility(View.INVISIBLE);
        status_gram_first.setVisibility(View.INVISIBLE);
        status_gram_second.setVisibility(View.INVISIBLE);

        status_hungry.setVisibility(View.INVISIBLE);
        status_hungry_heart_one.setVisibility(View.INVISIBLE);
        status_hungry_heart_two.setVisibility(View.INVISIBLE);
        status_hungry_heart_three.setVisibility(View.INVISIBLE);
        status_hungry_heart_four.setVisibility(View.INVISIBLE);

        status_strength.setVisibility(View.INVISIBLE);
        status_strength_heart_one.setVisibility(View.INVISIBLE);
        status_strength_heart_two.setVisibility(View.INVISIBLE);
        status_strength_heart_three.setVisibility(View.INVISIBLE);
        status_strength_heart_four.setVisibility(View.INVISIBLE);

        status_effort.setVisibility(View.INVISIBLE);
        status_effort_heart_one.setVisibility(View.INVISIBLE);
        status_effort_heart_two.setVisibility(View.INVISIBLE);
        status_effort_heart_three.setVisibility(View.INVISIBLE);
        status_effort_heart_four.setVisibility(View.INVISIBLE);

        status_health.setVisibility(View.INVISIBLE);
        status_progressbar_bottom.setVisibility(View.INVISIBLE);
        status_progressbar_mid_left.setVisibility(View.INVISIBLE);
        status_progressbar_mid_right.setVisibility(View.INVISIBLE);
        status_progressbar_top.setVisibility(View.INVISIBLE);
        for (int i = 0; i < 16; i++) {
            status_progressbar_gauge[i].setVisibility(View.INVISIBLE);
            ;
        }

        status_winrate.setVisibility(View.INVISIBLE);
        status_winrate_first_one.setVisibility(View.INVISIBLE);
        status_winrate_second_one.setVisibility(View.INVISIBLE);
        status_winrate_percentage.setVisibility(View.INVISIBLE);
    }
}