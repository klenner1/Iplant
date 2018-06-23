package puc.iot.com.iplant.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import puc.iot.com.iplant.models.Plant;
import puc.iot.com.iplant.models.Tap;

public class PlantsNotificationsManager {

    private String mUserName;
    private List<Plant> mNeedWaterList,mCanTurnOffList,mNeedTurnOffList;
    private List<Tap> tapOpenedList, tapClosedList;
    private Context context;

    public PlantsNotificationsManager(Context context,String userName) {
        this.mNeedWaterList = new ArrayList<>();
        this.mCanTurnOffList = new ArrayList<>();
        this.mNeedTurnOffList = new ArrayList<>();
        this.tapOpenedList = new ArrayList<>();
        this.tapClosedList = new ArrayList<>();
        this.context = context;
        mUserName = userName;
    }

    public void updatePlant(Plant plant){
        if(plant!=null) {
            switch (plant.getStatus()) {
                case Plant.DRY:
                    updateDry(plant);
                    break;
                case Plant.NORMAL:
                    removerFromLists(plant);
                    break;
                case Plant.HUMID:
                    removerFromLists(plant);
                    break;
                case Plant.WATERING_DRY:
                    removerFromLists(plant);
                    break;
                case Plant.WATERING_NORMAL:
                    updateWateringNormal(plant);
                    break;
                case Plant.WATERING_HUMID:
                    updateWateringHumid(plant);
                    break;
            }
        }
    }

    private void updateDry(Plant plant) {
        if (!mNeedWaterList.contains(plant)) {
            mNeedWaterList.add(plant);
            Notifications.needTurnOnWater(context,getPlantsIds(mNeedWaterList));
            if (mNeedTurnOffList.contains(plant)){
                mNeedTurnOffList.remove(plant);
                Notifications.needTurnOffWater(context,getPlantsIds(mNeedTurnOffList));
            }
            if (mCanTurnOffList.contains(plant)){
                mCanTurnOffList.remove(plant);
                Notifications.canTurnOffWater(context,getPlantsIds(mCanTurnOffList));
            }
        }
    }

    private void updateWateringNormal(Plant plant) {
        if (!mCanTurnOffList.contains(plant)) {
            mCanTurnOffList.add(plant);
            Notifications.canTurnOffWater(context,getPlantsIds(mCanTurnOffList));
            if (mNeedTurnOffList.contains(plant)){
                mNeedTurnOffList.remove(plant);
                Notifications.needTurnOffWater(context,getPlantsIds(mNeedTurnOffList));
            }
            if (mNeedWaterList.contains(plant)){
                mNeedWaterList.remove(plant);
                Notifications.needTurnOnWater(context,getPlantsIds(mNeedWaterList));
            }
        }
    }

    private void updateWateringHumid(Plant plant) {
        if (!mNeedTurnOffList.contains(plant)) {
            mNeedTurnOffList.add(plant);
            Notifications.needTurnOffWater(context,getPlantsIds(mNeedTurnOffList));
            if (mNeedWaterList.contains(plant)){
                mNeedWaterList.remove(plant);
                Notifications.needTurnOnWater(context,getPlantsIds(mNeedWaterList));
            }
            if (mCanTurnOffList.contains(plant)){
                mCanTurnOffList.remove(plant);
                Notifications.canTurnOffWater(context,getPlantsIds(mCanTurnOffList));
            }
        }
    }
    private void removerFromLists(Plant plant) {
        if (mNeedWaterList.contains(plant)) {
            mNeedWaterList.remove(plant);
            Notifications.needTurnOnWater(context,getPlantsIds(mNeedWaterList));
        }else if (mNeedTurnOffList.contains(plant)){
            mNeedTurnOffList.remove(plant);
            Notifications.needTurnOffWater(context,getPlantsIds(mNeedTurnOffList));
        }else if (mCanTurnOffList.contains(plant)){
            mCanTurnOffList.remove(plant);
            Notifications.canTurnOffWater(context,getPlantsIds(mCanTurnOffList));
        }
    }

    private String[] getPlantsIds(List<Plant> mNeedWaterList) {
        String[] plantNames  = new String[mNeedWaterList.size()];
        int i =0;
        for (Plant plant : mNeedWaterList) {
            plantNames[i]=plant.getName();
            i++;
        }
        return plantNames;
    }

    public void updateTap(Tap tap){
        if (tap.isOpen()){
            if (!tapOpenedList.contains(tap)){
                if (!mUserName.equals(tap.getWhoStirred()))
                tapOpenedList.add(tap);
                Notifications.tapOpenedBy(context,getTapIds(tapOpenedList));
                if (tapClosedList.contains(tap)){
                    tapClosedList.remove(tap);
                    Notifications.tapClosedBy(context,getTapIds(tapClosedList));
                }
            }
        }else {
            if (!tapClosedList.contains(tap)){
                if (!mUserName.equals(tap.getWhoStirred()))
                tapClosedList.add(tap);
                Notifications.tapClosedBy(context,getTapIds(tapClosedList));
                if (tapOpenedList.contains(tap)){
                    tapOpenedList.remove(tap);
                    Notifications.tapOpenedBy(context,getTapIds(tapOpenedList));
                }
            }
        }
    }

    @NonNull
    private String[] getTapIds(List<Tap> tapList) {
        String[] plantNames  = new String[tapList.size()];
        int i =0;
        for (Tap t : tapList) {
            plantNames[i]=t.getName();
            i++;
        }
        return plantNames;
    }
}
