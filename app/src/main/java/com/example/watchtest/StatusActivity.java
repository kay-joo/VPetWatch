package com.example.watchtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.watchtest.databinding.ActivityStatusBinding;

public class StatusActivity extends Activity {

    private ActivityStatusBinding binding;
    private ImageView status_scale, status_age_gram, status_age_first, status_age_second, status_gram_first, status_gram_second;
    private ImageView status_hungry, status_hungry_heart_one_empty, status_hungry_heart_two_empty, status_hungry_heart_three_empty, status_hungry_heart_four_empty;
    private ImageView status_strength, status_strength_heart_one_empty, status_strength_heart_two_empty, status_strength_heart_three_empty, status_strength_heart_four_empty;
    private ImageView status_effort, status_effort_heart_one_empty, status_effort_heart_two_empty, status_effort_heart_three_empty, status_effort_heart_four_empty;
    private ImageView status_health, status_progressbar_bottom, status_progressbar_mid_left, status_progressbar_mid_right, status_progressbar_top;
    private ImageView status_progressbar_gauge[] = new ImageView[16];
    private ImageView status_winrate, status_winrate_first_one, status_winrate_second_one, status_winrate_percentage;

    private Button button1, button2, button3;

    private int index = 0;//탭별 인덱스 제어 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeImageViews();
        initializeButton();
        pageOne();
    }

    private void initializeImageViews() {
        status_scale = findViewById(R.id.status_scale);
        status_age_gram = findViewById(R.id.status_age_gram);
        status_age_first = findViewById(R.id.status_age_first_one);
        status_age_second = findViewById(R.id.status_age_second_one);
        status_gram_first = findViewById(R.id.status_gram_first_one);
        status_gram_second = findViewById(R.id.status_gram_second_one);

        status_hungry = findViewById(R.id.status_hungry);
        status_hungry_heart_one_empty = findViewById(R.id.status_hungry_heart_one_empty);
        status_hungry_heart_two_empty = findViewById(R.id.status_hungry_heart_two_empty);
        status_hungry_heart_three_empty = findViewById(R.id.status_hungry_heart_three_empty);
        status_hungry_heart_four_empty = findViewById(R.id.status_hungry_heart_four_empty);

        status_strength = findViewById(R.id.status_strength);
        status_strength_heart_one_empty = findViewById(R.id.status_strength_heart_one_empty);
        status_strength_heart_two_empty = findViewById(R.id.status_strength_heart_two_empty);
        status_strength_heart_three_empty = findViewById(R.id.status_strength_heart_three_empty);
        status_strength_heart_four_empty = findViewById(R.id.status_strength_heart_four_empty);

        status_effort = findViewById(R.id.status_effort);
        status_effort_heart_one_empty = findViewById(R.id.status_effort_heart_one_empty);
        status_effort_heart_two_empty = findViewById(R.id.status_effort_heart_two_empty);
        status_effort_heart_three_empty = findViewById(R.id.status_effort_heart_three_empty);
        status_effort_heart_four_empty = findViewById(R.id.status_effort_heart_four_empty);

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
                if (index < 5) {//인덱스가 8이 넘어가면
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
                if (index < 5) {//인덱스가 8이 넘어가면
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
                startActivity(intent);
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
                pageOne();
                break;
        }
    }

    private void pageOne() {
        resetStatusViewsVisibility();
        status_scale.setVisibility(View.VISIBLE);
        status_age_gram.setVisibility(View.VISIBLE);
        status_age_first.setVisibility(View.VISIBLE);
        status_age_second.setVisibility(View.VISIBLE);
        status_gram_first.setVisibility(View.VISIBLE);
        status_gram_second.setVisibility(View.VISIBLE);
    }

    private void pageTwo() {
        resetStatusViewsVisibility();
        status_hungry.setVisibility(View.VISIBLE);
        status_hungry_heart_one_empty.setVisibility(View.VISIBLE);
        status_hungry_heart_two_empty.setVisibility(View.VISIBLE);
        status_hungry_heart_three_empty.setVisibility(View.VISIBLE);
        status_hungry_heart_four_empty.setVisibility(View.VISIBLE);
    }

    private void pageThree() {
        resetStatusViewsVisibility();
        status_strength.setVisibility(View.VISIBLE);
        status_strength_heart_one_empty.setVisibility(View.VISIBLE);
        status_strength_heart_two_empty.setVisibility(View.VISIBLE);
        status_strength_heart_three_empty.setVisibility(View.VISIBLE);
        status_strength_heart_four_empty.setVisibility(View.VISIBLE);
    }

    private void pageFour() {
        resetStatusViewsVisibility();
        status_effort.setVisibility(View.VISIBLE);
        status_effort_heart_one_empty.setVisibility(View.VISIBLE);
        status_effort_heart_two_empty.setVisibility(View.VISIBLE);
        status_effort_heart_three_empty.setVisibility(View.VISIBLE);
        status_effort_heart_four_empty.setVisibility(View.VISIBLE);
    }

    private void pageFive() {
        resetStatusViewsVisibility();
        status_health.setVisibility(View.VISIBLE);
        status_progressbar_bottom.setVisibility(View.VISIBLE);
        status_progressbar_mid_left.setVisibility(View.VISIBLE);
        status_progressbar_mid_right.setVisibility(View.VISIBLE);
        status_progressbar_top.setVisibility(View.VISIBLE);
        for (int i = 0; i < 16; i++) {
            status_progressbar_gauge[i].setVisibility(View.VISIBLE);
            ;
        }
    }

    private void pageSix() {
        resetStatusViewsVisibility();
        status_winrate.setVisibility(View.VISIBLE);
        status_winrate_first_one.setVisibility(View.VISIBLE);
        status_winrate_second_one.setVisibility(View.VISIBLE);
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
        status_hungry_heart_one_empty.setVisibility(View.INVISIBLE);
        status_hungry_heart_two_empty.setVisibility(View.INVISIBLE);
        status_hungry_heart_three_empty.setVisibility(View.INVISIBLE);
        status_hungry_heart_four_empty.setVisibility(View.INVISIBLE);

        status_strength.setVisibility(View.INVISIBLE);
        status_strength_heart_one_empty.setVisibility(View.INVISIBLE);
        status_strength_heart_two_empty.setVisibility(View.INVISIBLE);
        status_strength_heart_three_empty.setVisibility(View.INVISIBLE);
        status_strength_heart_four_empty.setVisibility(View.INVISIBLE);

        status_effort.setVisibility(View.INVISIBLE);
        status_effort_heart_one_empty.setVisibility(View.INVISIBLE);
        status_effort_heart_two_empty.setVisibility(View.INVISIBLE);
        status_effort_heart_three_empty.setVisibility(View.INVISIBLE);
        status_effort_heart_four_empty.setVisibility(View.INVISIBLE);

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