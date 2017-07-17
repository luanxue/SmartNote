package com.example.a99460.smartnote;

/**
 * Created by 99460 on 2017/7/3.
 */

public class Note {

    public boolean isrecord;

    public String note;

    public boolean isalarm;

    public int id;

    public String date;

    public int hour;

    public int minute;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Note(String date,String note,int id,boolean isalarm,boolean isrecord){
        this.date=date;
        this.note=note;
        this.id=id;
        this.isalarm=isalarm;
        this.isrecord=isrecord;
    }


}
