package com.example.a99460.smartnote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.List;

public class note_activity extends AppCompatActivity {
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_activity);
        editText = (EditText)findViewById(R.id.edit_note);
        //Intent intent = getIntent();
       // int id = intent.getIntExtra("in_data",1);
    //    Notedata notedata = DataSupport.find(Notedata.class,id);
      //  final String  word = notedata.getNote();
       // if(!TextUtils.isEmpty(word)) {
       // editText.setText(word);
       // editText.setSelection(word.length());}
        Button yes = (Button)findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = editText.getText().toString();
                if(word!=null&&Issave(word)) {
                    Notedata notedata = new Notedata();
                    notedata.setNote(word);
                    notedata.save();
                }
                finish();
            }
        });
   /*     Button back = (Button)findViewById(R.id.cancle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(word!=null&&Issave(word)) {
                    Notedata notedata = new Notedata();
                    notedata.setNote(word);
                    notedata.save();
                }
                finish();
            }
        });*/
    }
/*    public void onBackPressed(){
        Intent intent = getIntent();
        int id = intent.getIntExtra("in_data",1);
        Notedata notedata = DataSupport.find(Notedata.class,id);
        final String  word = notedata.getNote();
        if(word!=null&&Issave(word)){
        Notedata notedata1 = new Notedata();
        notedata1.setNote(word);
        notedata1.save();
        }
        finish();
    }*/

    protected boolean Issave(String word){
        int length = word.length();
        int i,flag=0;
        for (i=0;i<length;i++){
            if(word.charAt(i)!=' '&&word.charAt(i)!='\n'){
                flag=1;
            }
        }
        if (flag==1){
            return true;
        }
        return false;
    }

}


