package fr.neamar.musclememory.picker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashSet;

import fr.neamar.musclememory.MusicService;
import fr.neamar.musclememory.R;

public class LevelPickerActivity extends AppCompatActivity {
    private final static String TAG = "LevelPicker";

    private RecyclerView mRecyclerView;
    private PackAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
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
                prefs.edit().putBoolean(MusicService.MUSIC_KEY, !prefs.getBoolean(MusicService.MUSIC_KEY, true)).apply();
                displayCorrectVolumeIcon();

                if (prefs.getBoolean(MusicService.MUSIC_KEY, true)) {
                    startMusic();
                } else {
                    Intent i = new Intent(LevelPickerActivity.this, MusicService.class);
                    i.putExtra(MusicService.ACTION_KEY, MusicService.STOP_MUSIC);
                    startService(i);
                }
            }
        });
        displayCorrectVolumeIcon();

        findViewById(R.id.nextUniverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LevelPickerActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.previousUniverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LevelPickerActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onPostResume() {
        if (musicShouldPlayOnLobby()) {
            // If you've played before and haven't muted volume, start music!
            startMusic();
        }
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        Intent i = new Intent(this, MusicService.class);
        i.putExtra(MusicService.ACTION_KEY, MusicService.STOP_MUSIC_IF_LAST);
        startService(i);
        super.onPause();
    }

    private boolean musicShouldPlayOnLobby() {
        return prefs.getStringSet("finished_sublevels", new HashSet<String>()).size() > 0;
    }

    private void displayCorrectVolumeIcon() {
        if (prefs.getBoolean(MusicService.MUSIC_KEY, true)) {
            ((ImageView) findViewById(R.id.toggleVolume)).setImageResource(R.drawable.ic_volume_on);
        } else {
            ((ImageView) findViewById(R.id.toggleVolume)).setImageResource(R.drawable.ic_volume_off);
        }
    }

    private void startMusic() {
        Intent i = new Intent(this, MusicService.class);
        i.putExtra(MusicService.ACTION_KEY, MusicService.PLAY_MUSIC);
        startService(i);
    }

    public void notifyLevelStarted() {
        startMusic();
    }
}
