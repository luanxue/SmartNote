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

public class note_activity extends AppCompatActivity {
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_activity);
        editText = (EditText)findViewById(R.id.edit_note);
        Intent intent = getIntent();
        int id = intent.getIntExtra("in_data",1);
        Notedata notedata = DataSupport.find(Notedata.class,id);
        String word = notedata.getNote();
        if(!TextUtils.isEmpty(word)) {
        editText.setText(word);
        editText.setSelection(word.length());}
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
                Intent intent = new Intent();
                String word = editText.getText().toString();
                intent.putExtra("data_return",word);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    public void onBackPressed(){
        Intent intent = new Intent();
        String word = editText.getText().toString();
        intent.putExtra("data_return",word);
        setResult(RESULT_OK,intent);
        finish();
    }

}
