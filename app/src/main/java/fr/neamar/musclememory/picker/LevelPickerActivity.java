package fr.neamar.musclememory.picker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.HashSet;

import fr.neamar.musclememory.MusicService;
import fr.neamar.musclememory.R;

public class LevelPickerActivity extends AppCompatActivity {
    private final static String TAG = "LevelPicker";
    public final static String MUSIC_KEY = "music_enabled";

    private RecyclerView mRecyclerView;
    private PackAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MusicService musicService;
    private boolean serviceIsBound;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_picker);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mAdapter = new PackAdapter(LevelPickerActivity.this, mRecyclerView.getWidth(), mRecyclerView.getHeight());
                mAdapter.setHasStableIds(true);
                mRecyclerView.setAdapter(mAdapter);

                int nextUnlocked = mAdapter.getFirstUnlocked();
                if (nextUnlocked != -1) {
                    mLayoutManager.scrollToPosition(nextUnlocked);
                }
            }
        });

        findViewById(R.id.toggleVolume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit().putBoolean(MUSIC_KEY, !prefs.getBoolean(MUSIC_KEY, true)).apply();
                displayCorrectVolumeIcon();

                if (serviceIsBound) {
                    if (prefs.getBoolean(MUSIC_KEY, true)) {
                        musicService.stopPlaying();
                    } else {
                        musicService.playMusicIfNotPlaying();
                    }
                }
            }
        });
        displayCorrectVolumeIcon();

        if(prefs.getStringSet("finished_sublevels", new HashSet<String>()).size() > 0 && prefs.getBoolean(MUSIC_KEY, true)) {
            // If you've played before and haven't muted volume, start music!
            doBindService();
        }

    }

    private void displayCorrectVolumeIcon() {
        if (prefs.getBoolean(MUSIC_KEY, true)) {
            ((ImageView) findViewById(R.id.toggleVolume)).setImageResource(R.drawable.ic_volume_on);
        } else {
            ((ImageView) findViewById(R.id.toggleVolume)).setImageResource(R.drawable.ic_volume_off);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has
            // been established, giving us the service object we can use
            // to interact with the service.  Because we have bound to a
            // explicit service that we know is running in our own
            // process, we can cast its IBinder to a concrete class and
            // directly access it.
            musicService = ((MusicService.LocalBinder) service).getService();
            musicService.playMusicIfNotPlaying();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has
            // been unexpectedly disconnected -- that is, its process
            // crashed. Because it is running in our same process, we
            // should never see this happen.
            musicService = null;
            Log.e(TAG, "Service unexpectedly disconnected");
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation
        // that we know will be running in our own process (and thus
        // won't be supporting component replacement by other
        // applications).
        bindService(new Intent(this, MusicService.class),
                mConnection,
                Context.BIND_AUTO_CREATE);
        serviceIsBound = true;
    }

    void doUnbindService() {
        if (serviceIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            serviceIsBound = false;
        }
    }

    public void notifyLevelStarted() {
        doBindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
