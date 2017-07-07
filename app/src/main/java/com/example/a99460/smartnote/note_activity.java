
package com.example.a99460.smartnote;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
    String word;
    long myid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_activity);
        editText = (EditText)findViewById(R.id.edit_note);
        Intent intent = getIntent();
        myid = intent.getLongExtra("in_data",-1);

        Notedata notedata = DataSupport.find(Notedata.class, myid);
        if (notedata!=null) {

            word  = notedata.getNote();

            if (!TextUtils.isEmpty(word)) {

                editText.setText(word);
                editText.setSelection(word.length());

            }

        }

        Button yes = (Button)findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(word==null){
                    String word1 = editText.getText().toString();
                    Notedata notedata = new Notedata();
                    if(word1!=null&&Issave(word1))
                    {
                        notedata.setNote(word1);
                        notedata.save();
                    }
                }
                else {
                    Notedata notedata = DataSupport.find(Notedata.class,myid);
                    String word1 = editText.getText().toString();

                    if(word1!=null&&Issave(word1))
                    {
                        notedata.setNote(word1);
                        notedata.save();
                    }

                }

                finish();
            }
        });
        Button back = (Button)findViewById(R.id.cancle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(note_activity.this);
                dialog.setTitle("提醒");
                dialog.setMessage("是否保存？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        if(word==null){
                            String word1 = editText.getText().toString();
                            Notedata notedata = new Notedata();
                            if(word1!=null&&Issave(word1))
                            {
                                notedata.setNote(word1);
                                notedata.save();
                            }
                        }
                        else {
                            Notedata notedata = DataSupport.find(Notedata.class,myid);
                            String word1 = editText.getText().toString();

                            if(word1!=null&&Issave(word1))
                            {
                                notedata.setNote(word1);
                                notedata.save();
                            }

                        }

                        finish();
                    }
                });
                dialog.setNegativeButton("否",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){

                        finish();
                    }
                });
                dialog.show();
            }
        });
    }
    public void onBackPressed(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(note_activity.this);
        dialog.setTitle("提醒");
        dialog.setMessage("是否保存？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                if(word==null){
                    String word1 = editText.getText().toString();
                    Notedata notedata = new Notedata();
                    if(word1!=null&&Issave(word1))
                    {
                        notedata.setNote(word1);
                        notedata.save();
                    }
                }
                else {
                    Notedata notedata = DataSupport.find(Notedata.class,myid);
                    String word1 = editText.getText().toString();
                    if(word1!=null&&Issave(word1))
                    {
                        notedata.setNote(word1);
                        notedata.save();
                    }

                }

                finish();
            }
        });
        dialog.setNegativeButton("否",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){

                finish();
            }
        });
        dialog.show();
    }
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
