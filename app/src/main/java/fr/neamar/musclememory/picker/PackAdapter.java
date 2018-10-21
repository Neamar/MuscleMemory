package fr.neamar.musclememory.picker;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fr.neamar.musclememory.LevelStore;
import fr.neamar.musclememory.R;
import fr.neamar.musclememory.level.GamePath;
import fr.neamar.musclememory.level.Invalidatable;
import fr.neamar.musclememory.level.LevelActivity;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder> {
    private final int screenWidth;
    private final int screenHeight;
    private final Paint pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillCirclePaint;

    private final SharedPreferences prefs;
    private final int universe;

    private final WeakReference<LevelPickerActivity> activity;

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
        private final ImageView lockImageView;
        private final TextView levelName;
        private final ImageView firstSubLevel;
        private final ImageView secondSubLevel;
        private final SharedPreferences prefs;
        private final int universe;

        PackViewHolder(View v, SharedPreferences prefs, int universe) {
            super(v);
            this.lockImageView = v.findViewById(R.id.lockImageView);
            this.levelName = v.findViewById(R.id.levelTitle);
            this.firstSubLevel = v.findViewById(R.id.firstSubLevel);
            this.secondSubLevel = v.findViewById(R.id.secondSubLevel);
            this.prefs = prefs;
            this.universe = universe;
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (LevelStore.getLevelStatus(prefs, universe, position) != LevelStore.LEVEL_LOCKED) {
                Intent i = new Intent(v.getContext(), LevelActivity.class);
                i.putExtra("universe", universe);
                i.putExtra("level", position);
                i.putExtra("subLevel", 0);
                LevelPickerActivity a = activity.get();
                if(a != null) {
                    ActivityOptions options = ActivityOptions
                            .makeScaleUpAnimation(firstSubLevel, 0, 0, firstSubLevel.getWidth(), firstSubLevel.getHeight());
                    // start the new activity
                    a.startActivity(i, options.toBundle());
                    a.notifyLevelStarted();
                }
                else {
                    // Should never happen!
                    v.getContext().startActivity(i);
                }
            }
            else {
                ScaleAnimation anim = new ScaleAnimation(1, 1.5f, 1, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setDuration(200);
                anim.setRepeatMode(ScaleAnimation.REVERSE);
                anim.setRepeatCount(1);
                lockImageView.startAnimation(anim);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            LevelStore.unlockLevel(prefs, universe, getAdapterPosition());
            PackAdapter.this.notifyDataSetChanged();
            Toast.makeText(v.getContext(), "You little cheater ;) Here you go, it's unlocked.", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    PackAdapter(LevelPickerActivity activity, int universe) {
        this.screenWidth = Math.max(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
        this.screenHeight = Math.min(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        this.universe = universe;

        pathPaint.setColor(GamePath.UNSELECTED_CIRCLE_COLOR);
        pathPaint.setDither(true);
        pathPaint.setStrokeWidth(10f);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);

        fillCirclePaint = new Paint();
        fillCirclePaint.setColor(Color.BLACK);
        fillCirclePaint.setStyle(Paint.Style.FILL);

        this.activity = new WeakReference<>(activity);
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public PackViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pack, parent, false);
        return new PackViewHolder(v, prefs, universe);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final PackViewHolder holder, int position) {
        int status = LevelStore.getLevelStatus(prefs, universe, position);
        if (status == LevelStore.LEVEL_LOCKED) {
            holder.lockImageView.setImageResource(R.drawable.outline_lock_black_36);
            holder.lockImageView.setColorFilter(holder.lockImageView.getContext().getResources().getColor(R.color.redLocked));
        } else if (status == LevelStore.LEVEL_UNLOCKED) {
            holder.lockImageView.setImageResource(R.drawable.outline_lock_open_black_36);
            holder.lockImageView.setColorFilter(null);
        } else {
            holder.lockImageView.setImageResource(R.drawable.outline_check_circle_black_36);
            holder.lockImageView.setColorFilter(holder.lockImageView.getContext().getResources().getColor(R.color.greenUnlocked));
        }


        holder.levelName.setText(String.format(holder.levelName.getContext().getString(R.string.level_number), LevelStore.UNIVERSES_NAME[universe], position + 1));

        drawLevel(universe, position, 0, holder.firstSubLevel);
        drawLevel(universe, position, 1, holder.secondSubLevel);
    }

    private void drawLevel(int universe, int level, int subLevel, ImageView imageView) {
        Bitmap bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Pair<String, ArrayList<GamePath>> levelData = LevelStore.getPathsForLevel(dummyInvalidatable, screenWidth, screenHeight, universe, level, subLevel);

        // Draw path
        for (GamePath p : levelData.second) {
            canvas.drawPath(p, pathPaint);
        }

        // Draw circle on top (after the path)
        for (GamePath p : levelData.second) {
            PointF start = p.getStartingPoint();
            canvas.drawCircle(start.x, start.y, p.circleRadius / 2, fillCirclePaint);
            canvas.drawCircle(start.x, start.y, p.circleRadius / 2, pathPaint);
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, screenWidth / 3, screenHeight / 3, false);
        imageView.setImageBitmap(scaledBitmap);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return LevelStore.getLevelCount(universe);
    }

    int getFirstUnlocked() {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (LevelStore.getLevelStatus(prefs, universe, i) == LevelStore.LEVEL_UNLOCKED) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}