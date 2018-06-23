package puc.iot.com.iplant.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Plant {

    public final static String _ID ="id";
    public final static int DRY =20, NORMAL = 30 , HUMID=41; //MAX 45
    public static final String NAME = "name";
    public static final String HUMIDITY = "HUMIDITY";
    public static final String IS_OPEN_TAP = "is_open_tap";
    public static final int WATERING_DRY = -1, WATERING_NORMAL =-2,WATERING_HUMID=-3 ;

    private String id, name, type,species;
    private Date lastWater;
    private float humidity;
    private boolean open_tap;
    private List<String> listeners;

    public Plant() {}

    public Plant(String id) {
        this.id = id;
    }

    public Plant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Date getLastWater() {
        return lastWater;
    }

    public void setLastWater(Date lastWater) {
        this.lastWater = lastWater;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public boolean isOpen_tap() {
        return open_tap;
    }

    public void setOpen_tap(boolean open_tap) {
        this.open_tap = open_tap;
    }

    public List<String> getListeners() {
        return listeners;
    }

    public void setListeners(List<String> listeners) {
        this.listeners = listeners;
    }

    public void addListener(String listener) {
        if (listeners==null)
            this.listeners=new ArrayList<>();
        listeners.add(listener);

    }

    public boolean removerListener(String listener) {
        if (listeners!=null) {
            try {
                listeners.remove(listener);
                return true;
            }catch (Exception e){
                return false;
            }
        }else
            return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass()==this.getClass()) {
            Plant plant = (Plant) obj;
            return getId().equals(plant.getId());
        }else
            return super.equals(obj);
    }

    @Exclude
    public int getStatus() {
        if (isOpen_tap()&&getHumidity()>=HUMID){
            return WATERING_HUMID;
        }else if (isOpen_tap()&&getHumidity()<=DRY){
            return WATERING_DRY;
        }else if (isOpen_tap()){
            return WATERING_NORMAL;
        }else if (getHumidity()<DRY){
            return DRY;
        }else if (getHumidity()>HUMID){
            return HUMID;
        } else
            return NORMAL;
    }

    public void update(Plant newPlant) {
        if (newPlant!=null){
        id = newPlant.getId();
        name = newPlant.getName();
        type = newPlant.getType();
        species = newPlant.getSpecies();
        lastWater = newPlant.getLastWater();
        humidity= newPlant.getHumidity();
        open_tap = newPlant.isOpen_tap();
        listeners=newPlant.getListeners();
        }
    }

}
