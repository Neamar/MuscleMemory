package fr.neamar.musclememory.picker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;

import fr.neamar.musclememory.LevelStore;
import fr.neamar.musclememory.MusicService;
import fr.neamar.musclememory.R;

public class LevelPickerActivity extends AppCompatActivity {
    private final static String TAG = "LevelPicker";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_picker);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

        final TextView universeTitle = findViewById(R.id.universe_title);
        final View nextUniverse = findViewById(R.id.nextUniverse);
        nextUniverse.setTag(1);
        final View previousUniverse = findViewById(R.id.previousUniverse);
        previousUniverse.setTag(-1);
        previousUniverse.setVisibility(View.INVISIBLE);

        final ViewPager pager = findViewById(R.id.pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                universeTitle.setText(LevelStore.UNIVERSES_TITLE[position]);

                previousUniverse.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                nextUniverse.setVisibility(position == LevelStore.getUniverseCount() - 1 ? View.INVISIBLE : View.VISIBLE);

                prefs.edit().putInt("current_universe", position).apply();
            }
        });
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);


        View.OnClickListener universeChangeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentUniverse = pager.getCurrentItem();
                int delta = (int) view.getTag();
                int newUniverse = currentUniverse + delta;

                if(newUniverse >= 0 && newUniverse < LevelStore.getUniverseCount()) {
                    pager.setCurrentItem(newUniverse);
                }
            }
        };

        nextUniverse.setOnClickListener(universeChangeListener);
        previousUniverse.setOnClickListener(universeChangeListener);

        pager.setCurrentItem(prefs.getInt("current_universe", 0));
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
