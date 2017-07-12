package com.example.a99460.smartnote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a99460.smartnote.R;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Record extends AppCompatActivity {
    static final int RECORDING = 1;
    static final int PLAYING = 2;
    static final int PAUSING = 3;

    static int STATUS ;
    //用于音频录制
    MediaRecorder mediaRecorder;
    //用于音频播放
    MediaPlayer mediaPlayer;
    //录制按钮
    ImageButton btnRecord;
    //播放按钮
    ImageButton btnPlay;
    //提示信息
    TextView tvTips;
    //吹一吹小音箱
    // ImageView imvSound;
    //播放进度条
    static String PATH_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        STATUS=PAUSING;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);

        Intent intent = getIntent();
        int myid = intent.getIntExtra("id",-1);
        PATH_NAME="/data/data/com.example.a99460.smartnote" + "/smartnote"+myid+".mp3";


        if (ContextCompat.checkSelfPermission(Record.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Record.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            init();
        }
        else{
            ActivityCompat.requestPermissions(Record.this, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }

        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(PATH_NAME);
                if (file.exists()) {
                    file.delete();
                }
                else
                    Toast.makeText(Record.this,"没有音频文件",Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String [] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(Record.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED&&
                            ContextCompat.checkSelfPermission(Record.this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED) {
                        init();
                    }
                }

                break;

            default:
                break;
        }
    }

    public void init(){
        //控件初始化
        btnPlay = (ImageButton)findViewById(R.id.fab);
        btnRecord = (ImageButton)findViewById(R.id.btn_control);

        tvTips = (TextView)findViewById(R.id.tv_record_tips);
        // imvSound = (ImageView)findViewById(R.id.imv_sound);
        //mediaplayerPreparingDialog = new ProgressDialog(this);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(PATH_NAME);
                if(file.exists()){
                    if (STATUS == RECORDING ){
                        //如果是在录制，点击则停止录制并且播放
                        stopRecording();
                        startPlay();
                    }else if (STATUS == PAUSING){
                        startPlay();
                    } else {
                        //如果是在播放，点击则暂停
                        pausePlay();
                    }
                }
                else{
                    Toast.makeText(Record.this,"没有文件",Toast.LENGTH_SHORT).show();

                }

            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STATUS == PLAYING || STATUS == PAUSING){
                    //如果是在播放或者暂停，点击开始录制
                    startRecording();
                }else {
                    //如果在录制，点击开始播放
                    stopRecording();
                    startPlay();
                }
            }
        });

        mediaRecorder = new MediaRecorder();
        //设置到达最大录制长度时重头开始录制


        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                switch (what){
                    case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
                        Toast.makeText(Record.this, "未知错误", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        Toast.makeText(Record.this, "已达到最大录制长度，开始重新录制", Toast.LENGTH_SHORT).show( );
                        startRecording();
                        break;
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                        Toast.makeText(Record.this, "空间不足，无法录制", Toast.LENGTH_SHORT).show();
                        mediaRecorder.stop();
                        break;

                }
            }
        });
        //默认开始录制
        //   startRecording();
        //  btnRecord.setBackgroundResource(R.drawable.ic_mic_black_24dp);
        //默认开始吹一吹检测以及播放进度检测
        //startCheckSound();
    }




    public void startRecording(){
        if (PLAYING == STATUS){
            stopPlay();
        }
        STATUS = RECORDING;
        //设置为录制状态
        tvTips.setText("正在录制，点击播放按钮或者麦克风停止录制");
        btnRecord.setBackgroundResource(R.drawable.stop_record);
        //开始录制的设置
        mediaRecorder.reset();  // You can reuse the object by going back to setAudioSource() step
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try{
            //设置储存路径
            mediaRecorder.setOutputFile(PATH_NAME);
            mediaRecorder.prepare();
            mediaRecorder.start();   // Recording is now started
        }catch (IOException e){
            Toast.makeText(this, "准备录制文件失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }

    }

    public void stopRecording(){
        if (RECORDING == STATUS){
            //说明正在录制,设置停止信息
            tvTips.setText("已停止录制，开始播放");
            btnRecord.setBackgroundResource(R.drawable.start_record);

            mediaRecorder.stop();
        }
    }



/*    Handler handler= new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if ((Double)msg.obj > 70){
                imvSound.setImageResource(R.drawable.ic_volume_mute_valid_24dp);
            }else {
                imvSound.setImageResource(R.drawable.ic_volume_mute_gray_24dp);
            }
            return false;
        }
    });*/



  /*  public void startCheckSound(){
        //定时检测峰值,以及检测播放进度
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mediaRecorder != null) {
                    double amplitude = (double)mediaRecorder.getMaxAmplitude();
                    double db = 0;
                    //计算分贝
                    if (amplitude > 1)
                        db = 20 * Math.log10(amplitude);
                    Message msg = new Message();
                    msg.obj = db;
                //    handler.sendMessage(msg);
                    //如果需要检测播放进度可以使用
                    //mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();
                }
            }
        },0,100);
    }*/


    //  ProgressDialog mediaplayerPreparingDialog;

    public void startPlay(){
        if (RECORDING == STATUS){
            //如果是从录制状态开始播放，则重新读取新的录制文件
            STATUS = PLAYING;
            //设置音频播放器
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //播放完设置
                    tvTips.setText("播放完毕，可点击麦克风重新录制");
                    btnPlay.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                }
            });
            try {
                //

                mediaPlayer.setDataSource(PATH_NAME);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "录音文件已丢失", Toast.LENGTH_SHORT).show();
                finish();
            }
            //   mediaplayerPreparingDialog.setTitle("正在准备播放录音");
            //  mediaplayerPreparingDialog.show();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //    mediaplayerPreparingDialog.dismiss();

                    mediaPlayer.start();
                }
            });

        }else if(PAUSING == STATUS){
            //从暂停状态开始播放则直接播放
            STATUS = PLAYING;
            //设置音频播放器
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //播放完设置
                  //  tvTips.setText("播放完毕，可点击麦克风重新录制");
                    btnPlay.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                }
            });
            try {
                //

                mediaPlayer.setDataSource(PATH_NAME);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "录音文件已丢失", Toast.LENGTH_SHORT).show();
                finish();
            }
            //   mediaplayerPreparingDialog.setTitle("正在准备播放录音");
            //  mediaplayerPreparingDialog.show();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //    mediaplayerPreparingDialog.dismiss();

                    mediaPlayer.start();
                }
            });
        }


        //开始播放，设置按钮为暂停
        btnPlay.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);

    }

    public void pausePlay(){
        if (PLAYING == STATUS) {
            //暂停播放，设置按钮为开始播放
            mediaPlayer.pause();
            btnPlay.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
            STATUS = PAUSING;
        }
    }
    public void stopPlay(){
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File file = new File(PATH_NAME);
        if (PLAYING == STATUS){
            mediaPlayer.stop();
            mediaPlayer.release();

        }
        if (RECORDING == STATUS){
            mediaRecorder.stop();
            mediaRecorder.release();
        }

        //为了防止Activity结束后有时候这个timer还在定时执行任务（很坑）
    }
}