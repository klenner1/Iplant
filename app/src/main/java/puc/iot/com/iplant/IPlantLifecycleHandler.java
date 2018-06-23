package puc.iot.com.iplant;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

class IPlantLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    private static final int FOREGROUND = 0,VISIBLE = 1,BACKGROUND = 2;
    private int resumed;
    private int paused;
    private int started;
    private int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        NotificationManager manager =(NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.cancelAll();
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        activity.stopService(new Intent(activity.getApplication(),FireBaseBackgroundService.class));
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        if (getStatus()==BACKGROUND){
            Intent service = new Intent(activity.getApplication(), FireBaseBackgroundService.class);
            activity.startService(service);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        if (getStatus()==BACKGROUND){
            activity.startService(new Intent(activity.getApplication(),FireBaseBackgroundService.class));
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private int getStatus(){
        if (resumed>paused){
            return FOREGROUND;
        }else if (started > stopped){
            return VISIBLE;
        }else {
            return BACKGROUND;
        }
    }
}
