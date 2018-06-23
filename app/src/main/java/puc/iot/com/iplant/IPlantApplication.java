package puc.iot.com.iplant;

import android.app.Application;

public class IPlantApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new IPlantLifecycleHandler());
    }

}
