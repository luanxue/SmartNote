
package com.example.a99460.smartnote;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.List;

public class note_activity extends AppCompatActivity {
    EditText editText;
    String wordfirst;
    int myid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_activity);
        editText = (EditText)findViewById(R.id.edit_note);
        Intent intent = getIntent();
        myid=intent.getIntExtra( "in_data",-1 );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        SharedPreferences typef=getSharedPreferences( "typeface",MODE_PRIVATE );
        String tftf=typef.getString( "typefacehaha","" );

        if(tftf.length()<=0){
            editText.setTypeface( Typeface.SANS_SERIF );
        }else {
            Typeface typeface =Typeface.createFromAsset(getAssets(),tftf);
            editText.setTypeface( typeface );
        }

        Notedata notedata = DataSupport.find(Notedata.class, myid);
        if (notedata!=null) {
            wordfirst  = notedata.getNote();
            if (!TextUtils.isEmpty(wordfirst)) {
                editText.setText(wordfirst);
                editText.setSelection(wordfirst.length());
            }
        }

        Button yes = (Button)findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wordfirst==null){
                    String word1 = editText.getText().toString();
                    Notedata notedata = new Notedata();
                    if(Issave(word1))
                    {
                        notedata.setNote(word1);
                        notedata.save();
                    }
                }
                else {
                    Notedata notedata = DataSupport.find(Notedata.class,myid);
                    String word1 = editText.getText().toString();
                    if(Issave(word1))
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
                final String wordsecond = editText.getText().toString();
                //空笔记或者没有改变笔记都不会弹dialog
                if(wordsecond.equals( wordfirst )||wordsecond==null||!Issave( wordsecond )){
                    finish();
                }else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(note_activity.this);
                    dialog.setTitle("提醒");
                    dialog.setMessage("是否保存？");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("是",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog,int which){
                            if(wordfirst==null){
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
            }
        });
    }
    @Override
    public void onBackPressed(){
        final String wordsecond = editText.getText().toString();
        //空笔记或者没有改变笔记都不会弹dialog
        if(wordsecond.equals( wordfirst )||wordsecond==null||!Issave( wordsecond )){
            finish();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(note_activity.this);
            dialog.setTitle("提醒");
            dialog.setMessage("是否保存？");
            dialog.setCancelable(false);
            dialog.setPositiveButton("是",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog,int which){
                    if(wordfirst==null){
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
