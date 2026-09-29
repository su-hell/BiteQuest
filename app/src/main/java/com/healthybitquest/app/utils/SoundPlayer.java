package com.healthybitquest.app.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.healthybitquest.app.R;

/**
 * Lightweight sound helper. Sounds are optional; missing files never crash the game.
 */
public class SoundPlayer {

    private SoundPool soundPool;
    private int successId;
    private int incorrectId;
    private boolean loadedSuccess;
    private boolean loadedIncorrect;
    private boolean muted;

    public void load(Context context) {
        release();
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(attributes)
                .build();
        soundPool.setOnLoadCompleteListener((pool, sampleId, status) -> {
            if (status == 0) {
                if (sampleId == successId) {
                    loadedSuccess = true;
                } else if (sampleId == incorrectId) {
                    loadedIncorrect = true;
                }
            }
        });
        try {
            successId = soundPool.load(context, R.raw.success, 1);
            incorrectId = soundPool.load(context, R.raw.incorrect, 1);
        } catch (RuntimeException ignored) {
            successId = 0;
            incorrectId = 0;
        }
    }

    public void playSuccess() {
        if (!muted) {
            play(successId, loadedSuccess);
        }
    }

    public void playIncorrect() {
        if (!muted) {
            play(incorrectId, loadedIncorrect);
        }
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        loadedSuccess = false;
        loadedIncorrect = false;
    }

    private void play(int soundId, boolean loaded) {
        if (soundPool != null && soundId != 0 && loaded) {
            soundPool.play(soundId, 0.8f, 0.8f, 1, 0, 1f);
        }
    }
}
