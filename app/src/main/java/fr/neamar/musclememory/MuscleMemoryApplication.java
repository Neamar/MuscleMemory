package fr.neamar.musclememory;

import android.app.Application;

import com.amplitude.api.Amplitude;

public class MuscleMemoryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Amplitude.getInstance().initialize(this, "407e3d1d0d116eaf181242cb5e2a7e3a").enableForegroundTracking(this);
    }
}
