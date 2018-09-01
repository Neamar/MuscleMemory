package fr.neamar.musclememory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LevelActivity extends AppCompatActivity {
    protected int level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        level = getIntent().getIntExtra("level", 0);

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
            public void levelFinished(boolean levelWon) {
                if(levelWon) {
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
