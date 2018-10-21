package fr.neamar.musclememory.picker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fr.neamar.musclememory.LevelStore;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();
        Bundle b = new Bundle();
        b.putInt("universe", position);
        screenSlidePageFragment.setArguments(b);
        return screenSlidePageFragment;
    }

    @Override
    public int getCount() {
        return LevelStore.getUniverseCount();
    }
}