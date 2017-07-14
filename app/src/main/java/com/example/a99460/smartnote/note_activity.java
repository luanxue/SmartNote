
package com.example.a99460.smartnote;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
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
import com.gongyunhaoyyy.password.BuilderManager;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class note_activity extends AppCompatActivity {
    EditText editText;
    String wordfirst;
    BoomMenuButton bmb_note;
    int myid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_activity);

        //BoomMenuButton相关配置
        bmb_note = (BoomMenuButton) findViewById(R.id.bmb_note);
        assert bmb_note != null;
        bmb_note.setShadowEffect( false );
        bmb_note.setButtonPlaceEnum( ButtonPlaceEnum.Custom );
        for (int i = 0; i < bmb_note.getPiecePlaceEnum().pieceNumber(); i++) addBuilder( i+3 );
        bmb_note.getCustomButtonPlacePositions().add(new PointF( Util.dp2px(-50), Util.dp2px(-240)));
        bmb_note.getCustomButtonPlacePositions().add(new PointF(Util.dp2px(+30), Util.dp2px(-160)));
        bmb_note.getCustomButtonPlacePositions().add(new PointF(Util.dp2px(+110), Util.dp2px(-80)));

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
                                    notedata.setDate(GetDate());
                                    notedata.setNote(word1);
                                    notedata.save();
                                }
                            }
                            else {
                                Notedata notedata = DataSupport.find(Notedata.class,myid);
                                String word1 = editText.getText().toString();
                                if(word1!=null&&Issave(word1))
                                {
                                    notedata.setDate(GetDate());
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

    //设置menu的监听功能
    private void addBuilder(int i) {
        bmb_note.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(BuilderManager.getImageResourcenote(i))
                .pieceColor( Color.WHITE)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        switch (index){
                            case 0:
                                Toast.makeText( note_activity.this,"拍照(待完成)",Toast.LENGTH_SHORT ).show();
                                break;
                            case 1:
                                Toast.makeText( note_activity.this,"选择照片(待完成)",Toast.LENGTH_SHORT ).show();
                                break;
                            case 2:
                                Toast.makeText( note_activity.this,"录音(待完成)",Toast.LENGTH_SHORT ).show();
                                break;
                            default:
                        }
                    }
                }));
    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
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
                            notedata.setDate(GetDate());
                            notedata.setNote(word1);
                            notedata.save();
                        }
                    }
                    else {
                        Notedata notedata = DataSupport.find(Notedata.class,myid);
                        String word1 = editText.getText().toString();
                        if(word1!=null&&Issave(word1))
                        {
                            notedata.setDate(GetDate());
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

    protected String GetDate(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String date = sDateFormat.format(new java.util.Date());
        Toast.makeText(note_activity.this,date,Toast.LENGTH_SHORT).show();
        return date;
    }
}
