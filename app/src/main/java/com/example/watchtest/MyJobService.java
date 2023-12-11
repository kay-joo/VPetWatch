package com.example.watchtest;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class MyJobService extends JobService {
    private boolean sleep;//잠자는지 아닌지 판당용 변수

    //SharedPreferences 데이터 저장 관련 선언
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public boolean onStartJob(JobParameters params) {
        // 특정 시간에 실행되는 코드
        initializePreferences();
        sleep = true;
        editor.putBoolean("sleep", sleep);//슬립값 저장
        editor.apply();

        NotificationHelper.showNotification(getApplicationContext(), "VPetWatch", "VPet sleep");

        // 작업이 완료되면 true 반환
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // 작업 중단 시 호출됨
        return false; // true로 설정하면 재스케줄링됨
    }

    //SharedPreferences 초기화 부분
    private void initializePreferences() {
        preferences = getSharedPreferences("VPetWatch", Context.MODE_PRIVATE);
        editor = preferences.edit();

        sleep = preferences.getBoolean("sleep", false);
    }
}
