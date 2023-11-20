package com.example.watchtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.watchtest.databinding.ActivityFoodBinding;

public class FoodActivity extends Activity {

    private ActivityFoodBinding binding;
    ImageView food_meat_protein, food_arrow_food, food_arrow_protein;

    private Button button1, button2, button3;

    private int index = 0;//탭별 인덱스 제어 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeImageViews();
        initializeButton();
    }

    private void initializeImageViews() {
        food_meat_protein = findViewById(R.id.food_meat_protein);
        food_arrow_food = findViewById(R.id.food_arrow_food);
        food_arrow_protein = findViewById(R.id.food_arrow_protein);
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
                if (index < 1) {//인덱스가 5가 넘어가면
                    index++;
                } else {
                    index = 0;//0으로 다시 설정
                }
                arrowChange(index);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySoundPlayer.play(MySoundPlayer.sound1);

            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySoundPlayer.play(MySoundPlayer.sound1);
                Intent intent = new Intent(FoodActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//액티비티 전환시 애니메이션 없애기
                startActivity(intent);
                finish();//현재 액티비티 종료
            }
        });
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


    private void resetFoodViewsVisibility() {
        food_meat_protein.setVisibility(View.INVISIBLE);
        food_arrow_food.setVisibility(View.INVISIBLE);
        food_arrow_protein.setVisibility(View.INVISIBLE);
    }
}