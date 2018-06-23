package puc.iot.com.iplant.models;

import java.util.Date;

public class Tap {

    private String id,name;
    private boolean open;
    private String whoStirred;
    private Date lastStirred;

    public Tap() { }

    public Tap(String id, String name, boolean open) {
        this.id = id;
        this.name = name;
        this.open = open;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getWhoStirred() {
        return whoStirred;
    }

    public void setWhoStirred(String whoStirred) {
        this.whoStirred = whoStirred;
    }

    public Date getLastStirred() {
        return lastStirred;
    }

    public void setLastStirred(Date lastStirred) {
        this.lastStirred = lastStirred;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass()==this.getClass()) {
            Tap tap = (Tap) obj;
            return tap.getId().equals(this.getId());
        }

        return super.equals(obj);
    }
}
