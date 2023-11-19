package com.example.watchtest;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.HashMap;

public class MySoundPlayer {
    public static final int sound1 = R.raw.sound1;
    //public static final int SUCCESS = R.raw.success;

    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;

    // sound media initialize
    public static void initSounds(Context context) {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();

        soundPoolMap = new HashMap(2);
        soundPoolMap.put(sound1, soundPool.load(context, sound1, 1));
        //soundPoolMap.put(SUCCESS, soundPool.load(context, SUCCESS, 2));
    }

    public static void play(int raw_id) {
        if (soundPoolMap.containsKey(raw_id)) {
            soundPool.play(soundPoolMap.get(raw_id), 1, 1, 1, 0, 1f);
        }
    }
}