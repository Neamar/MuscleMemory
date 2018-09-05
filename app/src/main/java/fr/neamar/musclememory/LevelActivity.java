package fr.neamar.musclememory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amplitude.api.Amplitude;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class LevelActivity extends AppCompatActivity {
    protected int level;
    protected int attempts = 0;
    protected SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getPreferences(MODE_PRIVATE);
        level = getIntent().getIntExtra("level", 11);

        setContentView(R.layout.activity_level);

        final LevelView levelView = findViewById(R.id.levelView);

        levelView.post(new Runnable() {
            @Override
            public void run() {
                levelView.setCurrentLevel(level);
            }
        });

        levelView.setOnLevelFinished(new OnLevelFinished() {
            @Override
            public void levelFinished(boolean levelWon, long time) {
                attempts += 1;

                Set<String> finishedLevels = prefs.getStringSet("finished_levels", new HashSet<String>());
                int globalAttemptsCount = prefs.getInt("attempts_" + levelView.title, 0);
                globalAttemptsCount += 1;

                JSONObject props = new JSONObject();
                try {
                    props.put("level_title", levelView.title);
                    props.put("level_number", level);
                    props.put("level_duration", levelView.getLevelDuration());
                    props.put("level_paths_count", levelView.getPathsCount());
                    props.put("time_played_ms", time);
                    props.put("progress_percent", Math.min(100, Math.round(100 * levelView.getProgress())));
                    props.put("number_of_attempts_session", attempts);
                    props.put("number_of_attempts_ever", globalAttemptsCount);
                    props.put("finished_before", finishedLevels.contains(levelView.title));
                    props.put("screen_width", levelView.getWidth());
                    props.put("screen_height", levelView.getHeight());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                prefs.edit().putInt("attempts_" + levelView.title, globalAttemptsCount).apply();

                if (!levelWon) {
                    Amplitude.getInstance().logEvent("Level lost", props);
                } else {
                    // Remember that we did the level!
                    if(!finishedLevels.contains(levelView.title)) {
                        finishedLevels.add(levelView.title);
                        prefs.edit().putStringSet("finished_levels", finishedLevels).apply();
                    }
                    Amplitude.getInstance().logEvent("Level won", props);
                    Intent i = new Intent(LevelActivity.this, LevelActivity.class);
                    i.putExtra("level", level + 1);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Cheat!
        Intent i = new Intent(LevelActivity.this, LevelActivity.class);
        i.putExtra("level", level + 1);
        startActivity(i);
        finish();
    }
}
