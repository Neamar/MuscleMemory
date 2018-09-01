package fr.neamar.musclememory;

import android.app.Application;

import com.amplitude.api.Amplitude;

public class MuscleMemoryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Amplitude.getInstance().initialize(this, "341b2bf343fde05ddffbb561b700c412").enableForegroundTracking(this);
    }
}
