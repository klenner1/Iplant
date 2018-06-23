package puc.iot.com.iplant;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import puc.iot.com.iplant.models.Plant;
import puc.iot.com.iplant.models.Tap;
import puc.iot.com.iplant.utils.PlantsNotificationsManager;
import puc.iot.com.iplant.utils.UtilsFireBase;

public class FireBaseBackgroundService extends Service {
    private final static String TAG ="BackgroundService";
    private PlantsNotificationsManager mNotificationsManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");

        FirebaseApp.initializeApp(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        mNotificationsManager = new PlantsNotificationsManager(getApplicationContext(),user.getDisplayName());
        UtilsFireBase.getUserPlantsReference(user.getUid()).addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               String id;
               try {
                   Tap tap= dataSnapshot.getValue(Tap.class);
                   assert tap != null;
                   id=tap.getId();
                   getPlant(id);
               }catch (Exception e) {
                   Log.e(TAG,dataSnapshot.getValue(HashMap.class)+" :..: "+e.getMessage(),e);
               }

           }

           @Override
           public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               try {
                   Tap tap= dataSnapshot.getValue(Tap.class);
                   assert tap != null;
                   mNotificationsManager.updateTap(tap);
               }catch (Exception e) {
                   Log.e(TAG,e.getMessage(),e);
               }

           }

           @Override
           public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
           }

           @Override
           public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Log.e(TAG,databaseError.getMessage(),databaseError.toException());
           }
       });
    }

    private void getPlant(final String plantId) {
        DatabaseReference plantReference = UtilsFireBase.getPlantReference(plantId);
        plantReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Plant plant = dataSnapshot.getValue(Plant.class);
                assert plant != null;
                mNotificationsManager.updatePlant(plant);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"stop");
        stopSelf();
    }
}
