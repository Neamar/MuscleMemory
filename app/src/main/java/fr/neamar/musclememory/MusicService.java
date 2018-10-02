package fr.neamar.musclememory;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {
    private static final String TAG = "MusicService";

    public final static String MUSIC_KEY = "music_enabled";
    public static final String ACTION_KEY = "action";
    public static final String PLAY_MUSIC = "PLAY_MUSIC";
    public static final String STOP_MUSIC = "STOP_MUSIC";
    public static final String STOP_MUSIC_IF_LAST = "STOP_MUSIC_MAYBE";

    private boolean shouldStopMusic = false;
    MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra(ACTION_KEY);
        switch (action) {
            case PLAY_MUSIC:
                playMusicIfNotPlaying();
                break;
            case STOP_MUSIC:
                cleanUpMediaPlayer();
                break;
            case STOP_MUSIC_IF_LAST:
                shouldStopMusic = true;
                // If, within a couple milliseconds, no one asks to start playing music
                // it means the player left the game.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (shouldStopMusic) {
                            cleanUpMediaPlayer();
                        }
                    }
                }, 500);
                break;
            default:
                throw new RuntimeException("Unknown intent action");
        }

        return START_NOT_STICKY;
    }

    public void playMusicIfNotPlaying() {
        shouldStopMusic = false;
        if (mediaPlayer != null) {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean(MUSIC_KEY, true)) {
            return;
        }


        try {
            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor afd = getAssets().openFd("background.mp3");
            //sets the data source of audio file
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            //prepares the player for playback synchronously
            mediaPlayer.prepare();
            //sets the player for looping
            mediaPlayer.setLooping(true);
            //starts or resumes the playback
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "Error: " + e.toString());
        }

        Log.i(TAG, "Started playing music");
    }

    public void onDestroy() {
        cleanUpMediaPlayer();
    }

    private void cleanUpMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}