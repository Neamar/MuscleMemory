package fr.neamar.musclememory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder> {
    private final static int LEVEL_LOCKED = 0;
    private final static int LEVEL_UNLOCKED = 1;
    private final static int LEVEL_FINISHED = 2;

    private final int screenWidth;
    private final int screenHeight;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final SharedPreferences prefs;

    static class DummyInvalidatable implements Invalidatable {
        @Override
        public void invalidate() {

        }

        @Override
        public void onPathCompleted() {

        }
    }

    private final DummyInvalidatable dummyInvalidatable = new DummyInvalidatable();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class PackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // each data item is just a string in this case
        ImageView lockImageView;
        TextView levelName;
        ImageView firstSubLevel;
        ImageView secondSubLevel;
        SharedPreferences prefs;

        PackViewHolder(View v, SharedPreferences prefs) {
            super(v);
            this.lockImageView = v.findViewById(R.id.lockImageView);
            this.levelName = v.findViewById(R.id.textView);
            this.firstSubLevel = v.findViewById(R.id.firstSubLevel);
            this.secondSubLevel = v.findViewById(R.id.secondSubLevel);
            this.prefs = prefs;
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (getLevelStatus(prefs, position) != LEVEL_LOCKED) {
                Intent i = new Intent(v.getContext(), LevelActivity.class);
                i.putExtra("level", position);
                i.putExtra("subLevel", 1);
                v.getContext().startActivity(i);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            Set<String> finishedLevels = prefs.getStringSet("finished_levels", new HashSet<String>());
            finishedLevels.add(Integer.toString(getAdapterPosition()));
            prefs.edit().putStringSet("finished_levels", finishedLevels).apply();
            PackAdapter.this.notifyItemChanged(getAdapterPosition());
            Toast.makeText(v.getContext(), "You little cheater ;) Here you go, it's unlocked.", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    PackAdapter(Context context, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        paint.setColor(GamePath.CIRCLE_ORIGINAL_COLOR);
        paint.setDither(true);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public PackViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pack, parent, false);
        return new PackViewHolder(v, prefs);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final PackViewHolder holder, int position) {
        int status = getLevelStatus(prefs, position);
        if (status == LEVEL_LOCKED) {
            holder.lockImageView.setImageResource(R.drawable.outline_lock_black_36);
            holder.lockImageView.setColorFilter(Color.RED);
        } else if (status == LEVEL_UNLOCKED) {
            holder.lockImageView.setImageResource(R.drawable.outline_lock_open_black_36);
            holder.lockImageView.setColorFilter(null);
        } else {
            holder.lockImageView.setImageResource(R.drawable.outline_check_circle_black_36);
            holder.lockImageView.setColorFilter(Color.GREEN);
        }

        holder.levelName.setText(String.format(holder.levelName.getContext().getString(R.string.level_number), position + 1));

        holder.firstSubLevel.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = drawLevel(holder.getAdapterPosition(), 0);
                int width = holder.firstSubLevel.getWidth();
                int height = holder.firstSubLevel.getHeight();
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width != 0 ? width : 100, height != 0 ? height : 50, false);
                holder.firstSubLevel.setImageBitmap(scaledBitmap);
            }
        });

        holder.secondSubLevel.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = drawLevel(holder.getAdapterPosition(), 1);
                int width = holder.secondSubLevel.getWidth();
                int height = holder.secondSubLevel.getHeight();
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width != 0 ? width : 100, height != 0 ? height : 50, false);
                holder.secondSubLevel.setImageBitmap(scaledBitmap);
            }
        });
    }

    private Bitmap drawLevel(int level, int subLevel) {
        Bitmap bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        Pair<String, ArrayList<GamePath>> levelData = LevelStore.getPathsForLevel(dummyInvalidatable, screenWidth, screenHeight, level, subLevel);
        for (GamePath p : levelData.second) {
            canvas.drawPath(p, paint);
        }

        return bitmap;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return LevelStore.getLevelCount();
    }

    public int getFirstUnlocked() {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (getLevelStatus(prefs, i) == LEVEL_UNLOCKED) {
                return i;
            }
        }

        return -1;
    }

    private static int getLevelStatus(SharedPreferences prefs, int level) {
        boolean isLocked = true;
        Set<String> finishedLevels = prefs.getStringSet("finished_levels", new HashSet<String>());

        // Finished levels are unlocked
        if (finishedLevels.contains(Integer.toString(level))) {
            return LEVEL_FINISHED;
        }
        // Up to two unfinished levels can be played
        // (+1 because level is zero-based)
        else if (finishedLevels.size() + 1 >= level) {
            isLocked = false;
        }
        // First time, you HAVE to play level 0
        if (finishedLevels.size() == 0 && level != 0) {
            isLocked = true;
        }

        return isLocked ? LEVEL_LOCKED : LEVEL_UNLOCKED;
    }
}