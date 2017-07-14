package com.example.a99460.smartnote;

import org.litepal.crud.DataSupport;

/**
 * Created by 99460 on 2017/7/3.
 */




public class Notedata extends DataSupport{

    public int getId() {
        return id;
    }

    public void setId(int id) {

        this.id = id;

    }


    public boolean isAlarm() {
        return isAlarm;
    }

    public void setAlarm(boolean alarm) {
        isAlarm = alarm;
    }

    private boolean isAlarm;



    private  int id;

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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    private int hour;

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    private int minute;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    private String audio;

    public void setRecordTime(int recordTime) {
        RecordTime = recordTime;
    }

    public int getRecordTime() {
        return RecordTime;
    }

    int RecordTime;

}
