package puc.iot.com.iplant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;

public class StartFireBaseAtBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth!=null)
        context.startService(new Intent(context,FireBaseBackgroundService.class));
    }
}
