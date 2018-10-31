package fr.neamar.musclememory;

import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.amplitude.api.Amplitude;
import com.amplitude.api.Identify;

public class MuscleMemoryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Amplitude.getInstance().initialize(this, "407e3d1d0d116eaf181242cb5e2a7e3a").enableForegroundTracking(this);

        PackageManager pm = getPackageManager();
        int screenWidth = Math.max(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
        int screenHeight = Math.min(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);


        Identify identify = new Identify()
                .set("screen_width", screenWidth)
                .set("screen_height", screenHeight)
                .set("debug", BuildConfig.DEBUG)
                .set("density", getResources().getDisplayMetrics().density)
                .set("multitouch_jazzhand", pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND));
        Amplitude.getInstance().identify(identify);

    }
}
