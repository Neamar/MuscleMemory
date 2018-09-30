package fr.neamar.musclememory;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {
    MediaPlayer mediaPlayer = new MediaPlayer();

    // This is the object that receives interactions from clients.
    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playMusicIfNotPlaying() {
        if(mediaPlayer.isPlaying()) {
            return;
        }

        try {
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
            Log.i("show", "Error: " + e.toString());
        }
    }

    public void onDestroy() {
        //stops the playback
        mediaPlayer.stop();
        //releases any resource attached with MediaPlayer object
        mediaPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}