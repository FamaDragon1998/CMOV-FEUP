package org.feup.apm.lunchlist4;

public class Voucher {

    private int id;
    private boolean used;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Voucher(int id, boolean used) {
        this.id = id;
        this.used = used;
    }
}
