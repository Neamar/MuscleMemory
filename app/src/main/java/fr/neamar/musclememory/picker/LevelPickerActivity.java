package fr.neamar.musclememory.picker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import fr.neamar.musclememory.R;

public class LevelPickerActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private PackAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
                mRecyclerView.setAdapter(mAdapter);

                int nextUnlocked = mAdapter.getFirstUnlocked();
                if (nextUnlocked != -1) {
                    mLayoutManager.scrollToPosition(nextUnlocked);
                }
            }
        });

    }
}
