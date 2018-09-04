package fr.neamar.musclememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amplitude.api.Amplitude;

import org.json.JSONException;
import org.json.JSONObject;

public class LevelActivity extends AppCompatActivity {
    protected int level;
    protected int attempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        level = getIntent().getIntExtra("level", 7);

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

                JSONObject props = new JSONObject();
                try {
                    props.put("title", levelView.title);
                    props.put("time", time);
                    props.put("attempts", attempts);
                    props.put("level_number", level);
                    props.put("paths_counts", levelView.getPathsCount());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!levelWon) {
                    Amplitude.getInstance().logEvent("Level lost", props);
                } else {
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
