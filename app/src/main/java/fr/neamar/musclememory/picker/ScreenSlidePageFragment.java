package fr.neamar.musclememory.picker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.neamar.musclememory.R;

public class ScreenSlidePageFragment extends Fragment {
    private int universe = 0;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        assert args != null;
        universe = args.getInt("universe", 0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_level_picker, container, false);

        final RecyclerView mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                PackAdapter mAdapter = new PackAdapter((LevelPickerActivity) getActivity(), universe);
                mAdapter.setHasStableIds(true);
                mRecyclerView.setAdapter(mAdapter);

                int nextUnlocked = mAdapter.getFirstUnlocked();
                if (nextUnlocked != -1) {
                    mLayoutManager.scrollToPosition(nextUnlocked);
                }
            }
        });
        return rootView;
    }
}
