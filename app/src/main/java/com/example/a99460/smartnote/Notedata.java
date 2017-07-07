package com.example.a99460.smartnote;

import org.litepal.crud.DataSupport;

/**
 * Created by 99460 on 2017/7/3.
 */

public class Notedata extends DataSupport{

    public long getId() {
        return id;
    }

    public void setId(long id) {

        this.id = id;

    }

    private  long id;
    private boolean isLock=false;

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    private String note;

}
