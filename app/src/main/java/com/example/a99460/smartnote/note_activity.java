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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_activity);
        editText = (EditText)findViewById(R.id.edit_note);
        Intent intent = getIntent();
        long myid = intent.getLongExtra("in_data",1);

            Notedata notedata = DataSupport.find(Notedata.class, myid);
        if (notedata!=null) {

          word  = notedata.getNote();

            if (!TextUtils.isEmpty(word)) {

                editText.setText(word);
                editText.setSelection(word.length());
                DataSupport.deleteAll(Notedata.class,"note == ?" ,word);
            }


        }

        Button yes = (Button)findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String word = editText.getText().toString();
                intent.putExtra("data_return",word);
                setResult(RESULT_OK,intent);
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
                        Intent intent = new Intent();
                        String word = editText.getText().toString();
                        intent.putExtra("data_return",word);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                });
                dialog.setNegativeButton("否",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        Intent intent = new Intent();

                        intent.putExtra("data_return",word);
                    setResult(RESULT_OK,intent);
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
                Intent intent = new Intent();
                String word = editText.getText().toString();
                intent.putExtra("data_return",word);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        dialog.setNegativeButton("否",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                Intent intent = new Intent();

                intent.putExtra("data_return",word);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        dialog.show();
    }

}
