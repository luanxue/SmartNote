package com.example.a99460.smartnote;

/**
 * Created by 99460 on 2017/7/3.
 */

public class Note {
    public String note;


    public long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Note(String note, long id){
        this.note=note;
        this.id=id;
    }


}
