
package com.example.a99460.smartnote;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gongyunhaoyyy.password.BuilderManager;
import com.gongyunhaoyyy.password.ThemeSelectActivity;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;
import org.litepal.crud.DataSupport;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class note_activity extends AppCompatActivity {

    private WaveView mWaveView;
    EditText editText;
    String wordfirst;
    BoomMenuButton bmb_note;
    int myid;
    boolean Isrecording;
    static final int START = 0;
    static final int DISPLAY = 1;
    static final int PLAY = 2;
    static final int RECORDING = 3;
    static final int STOPRECORDING = 4;
    static int STATUS=START;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String PATH_NAME;
    ImageButton change;
    ImageButton delete;
    ImageButton ok_record;
    Thread timeThread; // 记录录音时长的线程
    int timeCount;
    final int TIME_COUNT = 0x101;
    TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_activity);

        delete = (ImageButton) findViewById(R.id.delete);
        change = (ImageButton) findViewById(R.id.change);
        ok_record = (ImageButton) findViewById(R.id.ok_record);
        STATUS = START;
        change.setBackgroundResource(R.drawable.record1);
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
        PATH_NAME = "/data/data/com.example.a99460.smartnote" + "/smartnote" + myid + ".mp3";
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

        ImageButton record_ok=(ImageButton)findViewById( R.id.ok_record );
        record_ok.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                RelativeLayout recordlayoutfi = (RelativeLayout)findViewById(R.id.record_layout);
                // 从原位置下滑到底部的动画
                //从当前的位置向下移动700px
                TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 700.0f);
                animation.setDuration(400);
                recordlayoutfi.startAnimation(animation);
                recordlayoutfi.setVisibility(View.GONE);
            }
        } );

        Button back = (Button)findViewById(R.id.cancle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String wordsecond = editText.getText().toString();
                //空笔记或者没有改变笔记都不会弹dialog
                if(wordsecond.equals( wordfirst )||wordsecond==null||!Issave( wordsecond )){
                    finish();
                }else {
                    if (wordfirst == null && Issave( wordsecond )) {
                        String word1 = editText.getText( ).toString( );
                        Notedata notedata = new Notedata( );
                        notedata.setDate( GetDate( ) );
                        notedata.setNote( word1 );
                        notedata.save( );
                        finish();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder( note_activity.this );
                        dialog.setTitle( "提醒" );
                        dialog.setMessage( "是否保存？" );
                        dialog.setCancelable( false );
                        dialog.setPositiveButton( "是", new DialogInterface.OnClickListener( ) {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (wordfirst == null) {
                                    String word1 = editText.getText( ).toString( );
                                    Notedata notedata = new Notedata( );
                                    if (word1 != null && Issave( word1 )) {
                                        notedata.setDate( GetDate( ) );
                                        notedata.setNote( word1 );
                                        notedata.save( );
                                    }
                                } else {
                                    Notedata notedata = DataSupport.find( Notedata.class, myid );
                                    String word1 = editText.getText( ).toString( );
                                    if (word1 != null && Issave( word1 )) {
                                        notedata.setDate( GetDate( ) );
                                        notedata.setNote( word1 );
                                        notedata.save( );
                                    }
                                }
                                finish( );
                            }
                        } );
                        dialog.setNegativeButton( "否", new DialogInterface.OnClickListener( ) {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish( );
                            }
                        } );
                        dialog.show( );
                    }
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
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(bmb_note.getWindowToken(),0);
                        switch (index){
                            case 0:
                                Toast.makeText( note_activity.this,"拍照(待完成)",Toast.LENGTH_SHORT ).show();
                                break;
                            case 1:
                                Toast.makeText( note_activity.this,"选择照片(待完成)",Toast.LENGTH_SHORT ).show();
                                break;
                            case 2:

                                RelativeLayout recordlayout = (RelativeLayout)findViewById(R.id.record_layout);
                                if(recordlayout.getVisibility()==View.VISIBLE){
                                }else {
                                    // 从屏幕底部进入的动画
                                    TranslateAnimation animation = new TranslateAnimation(
                                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                                            Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
                                    );
                                    animation.setDuration(600);
                                    recordlayout.setVisibility(View.VISIBLE);
                                    recordlayout.startAnimation(animation);
                                }
                                if (ContextCompat.checkSelfPermission(note_activity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                                        ContextCompat.checkSelfPermission(note_activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    init();
                                } else {
                                    ActivityCompat.requestPermissions(note_activity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }            
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

        RelativeLayout recordlayoutback = (RelativeLayout)findViewById(R.id.record_layout);
        if(recordlayoutback.getVisibility()==View.VISIBLE){
            TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 700.0f);
            animation.setDuration(400);
            recordlayoutback.startAnimation(animation);
            recordlayoutback.setVisibility(View.GONE);
        }else {
            //空笔记或者没有改变笔记都不会弹dialog
            if (wordsecond.equals( wordfirst ) || wordsecond == null || !Issave( wordsecond )) {
                finish( );
            } else {
                if (wordfirst == null && Issave( wordsecond )) {
                    String word1 = editText.getText( ).toString( );
                    Notedata notedata = new Notedata( );
                    notedata.setDate( GetDate( ) );
                    notedata.setNote( word1 );
                    notedata.save( );
                    finish( );
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder( note_activity.this );
                    dialog.setTitle( "提醒" );
                    dialog.setMessage( "是否保存？" );
                    dialog.setCancelable( false );
                    dialog.setPositiveButton( "是", new DialogInterface.OnClickListener( ) {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (wordfirst == null) {
                                String word1 = editText.getText( ).toString( );
                                Notedata notedata = new Notedata( );
                                if (word1 != null && Issave( word1 )) {
                                    notedata.setDate( GetDate( ) );
                                    notedata.setNote( word1 );
                                    notedata.save( );
                                }
                            } else {
                                Notedata notedata = DataSupport.find( Notedata.class, myid );
                                String word1 = editText.getText( ).toString( );
                                if (word1 != null && Issave( word1 )) {
                                    notedata.setDate( GetDate( ) );
                                    notedata.setNote( word1 );
                                    notedata.save( );
                                }
                            }
                            finish( );
                        }
                    } );
                    dialog.setNegativeButton( "否", new DialogInterface.OnClickListener( ) {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish( );
                        }
                    } );
                    dialog.show( );
                }
            }
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
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分");
        String date = sDateFormat.format(new java.util.Date());
<<<<<<< HEAD
=======

>>>>>>> d66a0263cd108ec59d9475ced5372c2f11540887
        return date;
    }

    public void startRecording() {
        STATUS = RECORDING;
        //设置为录制状态
        change.setBackgroundResource(R.drawable.record2);
        //开始录制的设置
        Isrecording = true;
        timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                countTime();
            }
        });
        timeThread.start();
        mWaveView.setVisibility(View.VISIBLE);
        mediaRecorder.reset();  // You can reuse the object by going back to setAudioSource() step
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            //设置储存路径
            mediaRecorder.setOutputFile(PATH_NAME);
            mediaRecorder.prepare();
            mediaRecorder.start();   // Recording is now started
        } catch (IOException e) {
            Toast.makeText(this, "准备录制文件失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
    }

    public void stopRecording() {
        Isrecording = false;
        mWaveView.setVisibility(View.INVISIBLE);
        STATUS = STOPRECORDING;
        //说明正在录制,设置停止信息
        change.setBackgroundResource(R.drawable.record3);
        mediaRecorder.stop();
        delete.setVisibility(View.VISIBLE);
        ok_record.setVisibility(View.VISIBLE);
    }

    public void startPlay() {

        //设置音频播放器
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完设置
                change.setBackgroundResource(R.drawable.record3);
                STATUS=DISPLAY;
            }
        });
        try {
            mediaPlayer.setDataSource(PATH_NAME);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "录音文件已丢失", Toast.LENGTH_SHORT).show();
            finish();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                STATUS = PLAY;
                change.setBackgroundResource(R.drawable.record4);
                mediaPlayer.start();
            }
        });
    }

    public void stopPlay(){
        STATUS = DISPLAY;
        change.setBackgroundResource(R.drawable.record3);
        mediaPlayer.stop();
    }

    private void countTime() {
        while (Isrecording) {
            timeCount++;
            Message msg = Message.obtain();
            msg.what = TIME_COUNT;
            msg.obj = timeCount;
            myHandler.sendMessage(msg);
            try {
                timeThread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String FormatMiss(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_COUNT:
                    int count = (int) msg.obj;
                    time.setText(FormatMiss(count));
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (PLAY == STATUS) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (RECORDING == STATUS) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String [] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(note_activity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED&&
                            ContextCompat.checkSelfPermission(note_activity.this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED) {
                        init();
                    }
                }
                break;
            default:
                break;
        }
    }

    protected void init() {
        mWaveView = (WaveView) findViewById(R.id.wave);
        mWaveView.setVisibility(View.INVISIBLE);
        time = (TextView)findViewById(R.id.time);
        //?
        time.setText("00:00:00");
        mediaRecorder = new MediaRecorder();
        //设置到达最大录制长度时重头开始录制
        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                switch (what) {
                    case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
                        Toast.makeText(note_activity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        Toast.makeText(note_activity.this, "已达到最大录制长度，开始重新录制", Toast.LENGTH_SHORT).show();
                        startRecording();
                        break;
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                        Toast.makeText(note_activity.this, "空间不足，无法录制", Toast.LENGTH_SHORT).show();
                        mediaRecorder.stop();
                        break;

                }
            }
        });
        File file = new File(PATH_NAME);
        if (file.exists()){
            STATUS = DISPLAY;
            change.setBackgroundResource(R.drawable.record3);
        }
        else{
            change.setBackgroundResource(R.drawable.record1);
        }

        change = (ImageButton) findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STATUS == START) {
                    startRecording();
                } else if (STATUS == RECORDING) {
                    stopRecording();
                } else if (STATUS == STOPRECORDING || STATUS == DISPLAY) {
                    startPlay();
                }
                else if (PLAY==STATUS){
                    stopPlay();
                }
            }
        });
        delete = (ImageButton) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(PATH_NAME);
                if (STATUS==STOPRECORDING||STATUS==DISPLAY){
                    file.delete();
                    Isrecording = false;
                    STATUS = START;
                    change.setBackgroundResource(R.drawable.record1);
                    time.setText("00:00:00");
                    timeCount = 0;
                }
                else if (STATUS==START&&file.exists()){
                    file.delete();
                    Isrecording = false;
                    STATUS = START;
                    change.setBackgroundResource(R.drawable.record1);
                    time.setText("00:00:00");
                    timeCount=0;
                }
                else if (STATUS==PLAY){
                    stopPlay();
                    file.delete();
                    Isrecording = false;
                    STATUS = START;
                    change.setBackgroundResource(R.drawable.record1);
                    time.setText("00:00:00");
                    timeCount=0;
                }
                else if (STATUS==RECORDING){
                    stopRecording();
                    file.delete();
                    STATUS = START;
                    change.setBackgroundResource(R.drawable.record1);
                    time.setText("00:00:00");
                    timeCount=0;
                }
           delete.setVisibility(View.INVISIBLE);
                ok_record.setVisibility(View.INVISIBLE);
            }
        });
    }
}
