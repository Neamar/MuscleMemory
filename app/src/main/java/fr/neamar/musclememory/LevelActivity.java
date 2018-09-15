package fr.neamar.musclememory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.amplitude.api.Amplitude;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class LevelActivity extends AppCompatActivity {
    protected int level;
    protected int subLevel;

    protected int attemptsCountDuringSession = 0;
    protected SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        level = getIntent().getIntExtra("level", 0);
        subLevel = getIntent().getIntExtra("subLevel", 0);

        setContentView(R.layout.activity_level);

        final LevelView levelView = findViewById(R.id.levelView);

        levelView.post(new Runnable() {
            @Override
            public void run() {
                levelView.setCurrentLevel(level, subLevel);
            }
        });

        levelView.setOnLevelFinished(new OnLevelFinished() {
            @Override
            public void levelFinished(boolean levelWon, long time) {
                attemptsCountDuringSession += 1;

                Set<String> finishedLevels = prefs.getStringSet("finished_levels", new HashSet<String>());
                int attemptsCountGlobal = prefs.getInt("attempts_" + levelView.title, 0);
                attemptsCountGlobal += 1;

                int attemptsCountAllLevel = prefs.getInt("attempts", 0);
                attemptsCountAllLevel += 1;

                JSONObject props = new JSONObject();
                try {
                    props.put("level_number", level);
                    props.put("subLevel_number", subLevel);
                    props.put("subLevel_title", levelView.title);
                    props.put("subLevel_duration", levelView.getLevelDuration());
                    props.put("subLevel_paths_count", levelView.getPathsCount());
                    props.put("time_played_ms", time);
                    props.put("progress_percent", Math.min(100, Math.round(100 * levelView.getProgress())));
                    props.put("number_of_attempts_session", attemptsCountDuringSession);
                    props.put("number_of_attempts_ever", attemptsCountGlobal);
                    props.put("alltime_number_of_games_played", attemptsCountAllLevel);
                    props.put("alltime_number_of_levels_finished", finishedLevels.size());
                    props.put("finished_before", finishedLevels.contains(levelView.title));
                    props.put("screen_width", levelView.getWidth());
                    props.put("screen_height", levelView.getHeight());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                prefs
                        .edit()
                        .putInt("attempts_" + levelView.title, attemptsCountGlobal)
                        .putInt("attempts", attemptsCountAllLevel)
                        .apply();

                if (!levelWon) {
                    Amplitude.getInstance().logEvent("subLevel lost", props);
                } else {
                    Amplitude.getInstance().logEvent("subLevel won", props);

                    JSONObject userProperties = new JSONObject();
                    try {
                        userProperties.put("screen_width", levelView.getWidth());
                        userProperties.put("screen_height", levelView.getHeight());
                        userProperties.put("number_of_games_played", attemptsCountAllLevel);
                        userProperties.put("number_of_levels_finished", finishedLevels.size());
                        PackageManager pm = getPackageManager();
                        userProperties.put("multitouch_jazzhand", pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Amplitude.getInstance().setUserProperties(userProperties);

                    if (subLevel == 0) {
                        // Move on to next sublevel
                        Intent i = new Intent(LevelActivity.this, LevelActivity.class);
                        i.putExtra("level", level);
                        i.putExtra("subLevel", subLevel + 1);

                        startActivity(i);
                        finish();
                    } else {
                        // Remember that we did the level!
                        if (!finishedLevels.contains(Integer.toString(level))) {
                            finishedLevels.add(Integer.toString(level));
                            prefs.edit().putStringSet("finished_levels", finishedLevels).apply();
                        }
                        // Move back to level picker
                        Intent i = new Intent(LevelActivity.this, LevelPickerActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });
    }
}
