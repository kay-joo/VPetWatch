package com.example.watchtest;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.HashMap;

public class MySoundPlayer {
    public static final int sound1 = R.raw.sound1;
    public static final int sound2 = R.raw.sound2;
    public static final int sound3 = R.raw.sound3;
    public static final int sound4 = R.raw.sound4;
    public static final int sound5 = R.raw.sound5;
    public static final int sound6 = R.raw.sound6;
    public static final int sound7 = R.raw.sound7;

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

        soundPoolMap = new HashMap(7);
        soundPoolMap.put(sound1, soundPool.load(context, sound1, 1));
        soundPoolMap.put(sound2, soundPool.load(context, sound2, 2));
        soundPoolMap.put(sound3, soundPool.load(context, sound3, 3));
        soundPoolMap.put(sound4, soundPool.load(context, sound4, 4));
        soundPoolMap.put(sound5, soundPool.load(context, sound5, 5));
        soundPoolMap.put(sound6, soundPool.load(context, sound6, 6));
        soundPoolMap.put(sound7, soundPool.load(context, sound7, 7));
    }

    public static void play(int raw_id) {
        if (soundPoolMap.containsKey(raw_id)) {
            soundPool.play(soundPoolMap.get(raw_id), 1, 1, 1, 0, 1f);
        }
    }

    public static void play(int raw_id, boolean loop) {
        if (soundPoolMap.containsKey(raw_id) && loop) {
            soundPool.play(soundPoolMap.get(raw_id), 1, 1, 1, -1, 1f);
        } else {
            soundPool.play(soundPoolMap.get(raw_id), 1, 1, 1, 0, 1f);
        }
    }

    public static void stop() {
        soundPool.autoPause();
    }
}