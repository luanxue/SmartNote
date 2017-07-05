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

    private  int id;


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    private String note;

}
