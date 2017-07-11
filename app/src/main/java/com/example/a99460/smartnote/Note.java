package com.example.a99460.smartnote;

/**
 * Created by 99460 on 2017/7/3.
 */

public class Note {
    public String note;

    public boolean isalarm;

    public int id;

    public int hour;

    public int minute;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Note(String note,int id,boolean isalarm){
        this.note=note;
        this.id=id;
        this.isalarm=isalarm;
    }


}
