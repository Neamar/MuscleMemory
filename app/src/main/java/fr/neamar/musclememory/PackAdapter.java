package fr.neamar.musclememory;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder> {
    private final int screenWidth;
    private final int screenHeight;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

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
    static class PackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView levelName;
        ImageView firstSubLevel;
        ImageView secondSubLevel;
        int position;

        PackViewHolder(View v) {
            super(v);
            this.levelName = v.findViewById(R.id.textView);
            this.firstSubLevel = v.findViewById(R.id.firstSubLevel);
            this.secondSubLevel = v.findViewById(R.id.secondSubLevel);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e("WTF", "Clicjked on " + position);
        }
    }

    PackAdapter(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

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
        return new PackViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final PackViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.levelName.setText(String.format("#%s", position + 1));
        final int positionToDraw = position;

        holder.firstSubLevel.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = drawLevel(positionToDraw, 0);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, holder.firstSubLevel.getWidth(), holder.firstSubLevel.getHeight(), false);
                holder.firstSubLevel.setImageBitmap(scaledBitmap);
            }
        });

        holder.secondSubLevel.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = drawLevel(positionToDraw, 1);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, holder.secondSubLevel.getWidth(), holder.secondSubLevel.getHeight(), false);
                holder.secondSubLevel.setImageBitmap(scaledBitmap);
            }
        });
        holder.position = position;
    }

    public Bitmap drawLevel(int level, int subLevel) {
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
}