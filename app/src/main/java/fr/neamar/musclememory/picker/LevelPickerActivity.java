package fr.neamar.musclememory.picker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import fr.neamar.musclememory.MusicService;
import fr.neamar.musclememory.R;

public class LevelPickerActivity extends AppCompatActivity {
    private final static String TAG = "LevelPicker";

    private RecyclerView mRecyclerView;
    private PackAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MusicService musicService;
    private boolean serviceIsBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_picker);

        mRecyclerView = findViewById(R.id.my_recycler_view);
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
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has
            // been established, giving us the service object we can use
            // to interact with the service.  Because we have bound to a
            // explicit service that we know is running in our own
            // process, we can cast its IBinder to a concrete class and
            // directly access it.
            musicService = ((MusicService.LocalBinder)service).getService();
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
