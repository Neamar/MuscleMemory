package fr.neamar.musclememory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LevelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_level);

        final LevelView levelView = findViewById(R.id.levelView);

        levelView.post(new Runnable() {
            @Override
            public void run() {
                levelView.setCurrentLevel(0);
            }
        });
    }
}
