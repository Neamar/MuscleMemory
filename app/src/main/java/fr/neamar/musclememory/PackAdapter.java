package fr.neamar.musclememory;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder> {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class PackViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView;
        PackViewHolder(View v) {
            super(v);
            this.textView = v.findViewById(R.id.textView);
        }
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
    public void onBindViewHolder(@NonNull PackViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText("P" + position);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 30;
    }
}