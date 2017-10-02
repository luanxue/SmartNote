
package com.example.a99460.smartnote;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gongyunhaoyyy.password.BuilderManager;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private Drawable orange_tit;
    private Drawable blue_tit;
    private Drawable purple_tit;
    private RelativeLayout et_title;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String PATH_NAME;
    ImageButton change;
    ImageButton delete;
    ImageButton record_ok;
    Thread timeThread; // 记录录音时长的线程
    int timeCount;
    final int TIME_COUNT = 0x101;
    TextView time;
    boolean Issave;
    boolean Isedit;
    int COLOR;
    boolean Isphoto;
    boolean IsAlbum;
    String imagePath;
    Button back;
    public static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;
    public static final int CHOOSE_PHOTO = 2;
    View dark_view;
    ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_activity);

    
        SharedPreferences themeColor=getSharedPreferences( "themecolor",MODE_PRIVATE );
        COLOR=themeColor.getInt( "themecolorhaha",0 );
        blue_tit=getResources().getDrawable( R.drawable.nav_skyblue );
        orange_tit=getResources().getDrawable( R.drawable.nav_orange );
        purple_tit=getResources().getDrawable( R.drawable.nav_purple );

        Issave = false;
        Isedit = false;
        Isphoto = false;
        IsAlbum = false;
        picture = (ImageView)findViewById(R.id.picture);
        delete = (ImageButton) findViewById(R.id.delete);
        change = (ImageButton) findViewById(R.id.change);
        Button sendText = (Button) findViewById(R.id.share_button);
        STATUS = START;
        change.setBackgroundResource(R.drawable.record1);
        time = (TextView)findViewById(R.id.time);
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
        //处理是否有保存
        if(myid!=-1){
            Notedata notedata = DataSupport.find(Notedata.class,myid);
            Issave = notedata.isRecord();
            Isedit = notedata.isEdit();
            Isphoto = notedata.isPhoto();
            IsAlbum = notedata.isAlbum();
            imagePath = notedata.getImagepath();
        }
        if (Isphoto==true||IsAlbum==true){
            Bitmap bitmap = getImageThumbnail(imagePath,630,500);
            picture.setVisibility(View.VISIBLE);
            picture.setImageBitmap(bitmap);
        }
        et_title=(RelativeLayout)findViewById( R.id.include );
        /**
         * ---------------更换主题颜色----------------
         */
        if (COLOR==0){
            et_title.setBackground( orange_tit );
            editText.setBackgroundColor( Color.parseColor( "#fef4f3" ) );
        } else if (COLOR==1){
            et_title.setBackground( blue_tit );
            editText.setBackgroundColor( Color.parseColor( "#96f2f5f5" ) );
        }else if (COLOR==2){
            et_title.setBackground( purple_tit );
            editText.setBackgroundColor( Color.parseColor( "#96ece2fb" ) );
        }

        record_ok=(ImageButton)findViewById( R.id.ok_record );
        time = (TextView)findViewById(R.id.time);
        mWaveView = (WaveView) findViewById(R.id.wave);
        change = (ImageButton) findViewById(R.id.change);
        delete = (ImageButton) findViewById(R.id.delete);
        back = (Button)findViewById(R.id.cancle);
        dark_view = (View)findViewById(R.id.dark_view);
        scrollView=(ScrollView)findViewById(R.id.scrollView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        SharedPreferences typef=getSharedPreferences( "typeface",MODE_PRIVATE );
        String tftf=typef.getString( "typefacehaha","" );

        if(tftf.length()<=0){
            editText.setTypeface( Typeface.MONOSPACE );
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

 
        picture.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(note_activity.this);
                dialog.setTitle("是否删除照片");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是",new DialogInterface.
                        OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        Notedata notedata = DataSupport.find(Notedata.class,myid);
                        notedata.setAlbum(false);
                        notedata.setPhoto(false);
                        IsAlbum = false;
                        Isphoto = false;
                        notedata.save();
                        picture.setVisibility(View.GONE);
                        if(!Isedit&&!Isrecording){
                            notedata.delete();
                        }
                    }
                });
                dialog.setNegativeButton("否",new DialogInterface.
                        OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){

                    }
                });
                dialog.show();
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String wordsecond = editText.getText().toString();
                //空笔记或者没有改变笔记都不会弹dialog
                RelativeLayout recordlayoutback = (RelativeLayout)findViewById(R.id.record_layout);
                if(recordlayoutback.getVisibility()==View.VISIBLE&&STATUS==RECORDING){
                    Notedata notedata = DataSupport.find(Notedata.class,myid);
                    notedata.setRecord(false);
                    notedata.save();
                    Issave = false;
                    stopRecording();
                }
                else{
                if (wordsecond.equals(wordfirst) || wordsecond == null || !Issave(wordsecond)) {
                    finish();
                } else {
                    if (wordfirst == null && Issave(wordsecond)) {
                        String word1 = editText.getText().toString();
                        if (myid == -1) {
                            Notedata notedata = new Notedata();
                            notedata.setDate(GetDate());
                            notedata.setNote(word1);
                            notedata.setEdit(true);
                            notedata.save();
                            myid = notedata.getId();
                            Isedit = true;
                            finish();
                        } else {
                            Notedata notedata = DataSupport.find(Notedata.class, myid);
                            notedata.setDate(GetDate());
                            notedata.setNote(word1);
                            notedata.setEdit(true);
                            notedata.save();
                            Isedit = true;
                            finish();

                        }
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(note_activity.this);
                        dialog.setTitle("提醒");
                        dialog.setMessage("是否保存当前更改？");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Notedata notedata = DataSupport.find(Notedata.class, myid);
                                String word1 = editText.getText().toString();
                                if (word1 != null && Issave(word1)) {
                                    notedata.setDate(GetDate());
                                    notedata.setNote(word1);
                                    notedata.save();
                                }
                                finish();
                            }
                        });
                        dialog.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        dialog.show();
                    }
                }
            }
            }
        });


        /*
        ----------------------------------分享功能的实现----------------------------------------------
         */
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = savePic(compressImage(getBitmapByView(scrollView)));
                // 分享照片
                Intent imageIntent = new Intent(Intent.ACTION_SEND);
                imageIntent.setType("image/jpeg");
                imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fname));
                startActivity(Intent.createChooser(imageIntent, "分享"));
            }
        });

        dark_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout recordlayoutback = (RelativeLayout)findViewById(R.id.record_layout);
                if(recordlayoutback.getVisibility()==View.VISIBLE&&STATUS!=RECORDING&&STATUS!=PLAY){
                    TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 700.0f);
                    animation.setDuration(400);
                    recordlayoutback.startAnimation(animation);
                    recordlayoutback.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    dark_view.setVisibility(View.GONE);
                }
                else if(recordlayoutback.getVisibility()==View.VISIBLE&&STATUS==PLAY){
                    stopPlay();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                }
                else if(recordlayoutback.getVisibility()==View.VISIBLE&&STATUS==RECORDING){
                    Notedata notedata = DataSupport.find(Notedata.class,myid);
                    notedata.setRecord(false);
                    notedata.save();
                    Issave = false;
                    stopRecording();

                    // closeSoftKeybord(editText,note_activity.this);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
                                if (myid==-1){
                                    Notedata notedata = new Notedata();
                                    notedata.save();
                                    myid = notedata.getId();
                                }
                                File outputImage = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                        myid+"output_image.jpg");
                                try{
                                    if (outputImage.exists()){
                                        outputImage.delete();
                                    }
                                    outputImage.createNewFile();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                if (Build.VERSION.SDK_INT >= 24){
                                    imageUri = FileProvider.getUriForFile(note_activity.this,
                                            "com.example.cameraalbumtest.fileprovider",outputImage);
                                }else{
                                    imageUri = Uri.fromFile(outputImage);
                                }
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                startActivityForResult(intent,TAKE_PHOTO);
                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(note_activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(note_activity.this,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },2);
                                }else{
                                    openAlbum();
                                }
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
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            dark_view.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });


                                    if (ContextCompat.checkSelfPermission(note_activity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                                            ContextCompat.checkSelfPermission(note_activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                        init();
                                    } else {
                                        ActivityCompat.requestPermissions(note_activity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }

                                }

                                break;
                            default:
                        }
                    }
                }));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onBackPressed(){
        final String wordsecond = editText.getText().toString();
        RelativeLayout recordlayoutback = (RelativeLayout)findViewById(R.id.record_layout);
        if(recordlayoutback.getVisibility()==View.VISIBLE&&STATUS!=RECORDING&&STATUS!=PLAY){
            TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 700.0f);
            animation.setDuration(400);
            recordlayoutback.startAnimation(animation);
            recordlayoutback.setVisibility(View.GONE);
            dark_view.setVisibility(View.GONE);

        }
        else if(recordlayoutback.getVisibility()==View.VISIBLE&&STATUS==PLAY){
            stopPlay();
        }
        else if(recordlayoutback.getVisibility()==View.VISIBLE&&STATUS==RECORDING){
            Notedata notedata = DataSupport.find(Notedata.class,myid);
            notedata.setRecord(false);
            notedata.save();
            Issave = false;
            stopRecording();
        } else {
            //空笔记或者没有改变笔记都不会弹dialog
            if (wordsecond.equals( wordfirst ) || wordsecond == null || !Issave( wordsecond )) {
                finish( );
            } else {
                //这是第一次不用询问的时候
                if (wordfirst == null && Issave( wordsecond )) {
                    String word1 = editText.getText( ).toString( );
                    if (myid==-1){
                    Notedata notedata = new Notedata( );
                    notedata.setDate( GetDate( ) );
                    notedata.setNote( word1 );
                    notedata.setEdit(true);
                    notedata.save( );
                        Isedit = true;
                        myid = notedata.getId();
                    finish( );
                    }else{
                        Notedata notedata = DataSupport.find(Notedata.class,myid);
                        notedata.setDate( GetDate( ) );
                        notedata.setNote( word1 );
                        notedata.setEdit(true);
                        notedata.save( );
                        Isedit = true;
                        finish( );
                    }
                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(note_activity.this);
                    dialog.setTitle("提醒");
                    dialog.setMessage("是否保存修改？");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Notedata notedata = DataSupport.find(Notedata.class, myid);
                            String word1 = editText.getText().toString();
                            if (word1 != null && Issave(word1)) {
                                notedata.setDate(GetDate());
                                notedata.setNote(word1);
                                notedata.save();
                            }
                            finish();
                        }
                    });
                    dialog.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish( );

                        }
                    });
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

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch(requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                        Notedata notedata = DataSupport.find(Notedata.class,myid);
                        notedata.setPhoto(true);
                        notedata.setAlbum(false);
                        notedata.setDate(GetDate());
                        notedata.setImagepath(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+myid+"output_image.jpg");
                        notedata.save();
                        Isphoto = true;
                        IsAlbum = false;
                        imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+myid+"output_image.jpg";
                        Bitmap bitmap = getImageThumbnail(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+myid+"output_image.jpg",630,500);
                picture.setVisibility(View.VISIBLE);
                picture.setImageBitmap(bitmap);
        }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    protected String GetDate(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd E kk:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
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
            //设置储存路径，这里新建了数据库
            if (myid==-1){
                Notedata notedata = new Notedata();
                notedata.save();
                myid = notedata.getId();
                PATH_NAME = "/data/data/com.example.a99460.smartnote/smartnote"+notedata.getId()+".mp3";
            }else{
                PATH_NAME = "/data/data/com.example.a99460.smartnote/smartnote"+myid+".mp3";
            }
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
        mWaveView.setVisibility(View.GONE);
        STATUS = STOPRECORDING;
        //说明正在录制,设置停止信息
        change.setBackgroundResource(R.drawable.record3);
        mediaRecorder.stop();
        delete.setVisibility(View.VISIBLE);
        record_ok.setVisibility(View.VISIBLE);
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

    //这个要关注一下,这真他妈是一个好东西。
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
        //我感觉这里的逻辑有问题
        if(!Issave&&Isedit){
            Notedata notedata = DataSupport.find(Notedata.class,myid);
            File file = new File("/data/data/com.example.a99460.smartnote/smartnote"+notedata.getId()+".mp3");
            file.delete();
        }
        //这里不对
         if(PATH_NAME!=null) {
             File file = new File(PATH_NAME);
             if (!Issave && !Isedit && file.exists()) {
                DataSupport.delete(Notedata.class,myid);
                 Toast.makeText(note_activity.this,"删除了哦",Toast.LENGTH_SHORT).show();
                 file.delete();
             }
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
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    protected void init() {
        PATH_NAME = "/data/data/com.example.a99460.smartnote/smartnote"+myid+".mp3";
        mWaveView.setVisibility(View.GONE);
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
           // change.setBackgroundResource(R.drawable.record1);
        if( (STATUS==STOPRECORDING||STATUS==DISPLAY||STATUS==PLAY||STATUS==START)&&Issave){
            delete.setVisibility(View.VISIBLE);
            record_ok.setVisibility(View.GONE);
            STATUS = STOPRECORDING;
            change.setBackgroundResource(R.drawable.record3);
            Notedata notedata = DataSupport.find(Notedata.class, myid);
            time.setText(FormatMiss( notedata.getRecordTime()));
        }


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


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(PATH_NAME);
                if (STATUS==STOPRECORDING||STATUS==DISPLAY||STATUS==START&&file.exists()){
                    file.delete();
                    Isrecording = false;
                    STATUS = START;
                    change.setBackgroundResource(R.drawable.record1);
                    time.setText("00:00:00");
                    timeCount = 0;
                } else if (STATUS==PLAY){
                    stopPlay();
                    file.delete();
                    Isrecording = false;
                    STATUS = START;
                    change.setBackgroundResource(R.drawable.record1);
                    time.setText("00:00:00");
                    timeCount=0;
                } else if (STATUS==RECORDING){
                    stopRecording();
                    file.delete();
                    STATUS = START;
                    change.setBackgroundResource(R.drawable.record1);
                    time.setText("00:00:00");
                    timeCount=0;
                }
                delete.setVisibility(View.GONE);
                record_ok.setVisibility(View.GONE);
                    Notedata notedata = DataSupport.find(Notedata.class,myid);
                    notedata.setRecordTime(0);
                    notedata.setRecord(false);
                    notedata.save();
                    Issave = false;
            }
        });


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
                dark_view.setVisibility(View.GONE);
                    Notedata notedata = DataSupport.find(Notedata.class,myid);
                    notedata.setRecordTime(timeCount);
                    notedata.setRecord(true);
                    notedata.setDate(GetDate());
                    notedata.save();
                Issave = true;
            }
        } );
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if (imagePath != null){
            if (myid==-1){
                Notedata notedata = new Notedata();
                notedata.setPhoto(false);
                notedata.setAlbum(true);
                notedata.setDate(GetDate());
                notedata.setImagepath(imagePath);
                notedata.save();
                IsAlbum = true;
                Isphoto = false;
                myid = notedata.getId();
            } else{
                Notedata notedata = DataSupport.find(Notedata.class,myid);
                notedata.setPhoto(false);
                notedata.setAlbum(true);
                notedata.setDate(GetDate());
                notedata.setImagepath(imagePath);
                notedata.save();
                IsAlbum = true;
                Isphoto = false;
            }
            Bitmap bitmap = getImageThumbnail(imagePath,630,500);
            picture.setVisibility(View.VISIBLE);
            picture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * 截取scrollview的屏幕
     * @param scrollView
     * @return
     */
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(
                    Color.parseColor("#ffffff"));
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * 压缩图片
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            // 重置baos
            baos.reset();
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            // 每次都减少10
            options -= 10;
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * 保存到sdcard
     * @param b
     * @return
     */
    public static String savePic(Bitmap b) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
                Locale.US);
        File outfile = new File("/sdcard/image");
        // 如果文件不存在，则创建一个新文件
        if (!outfile.isDirectory()) {
            try {
                outfile.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String fname = outfile + "/" + sdf.format(new Date()) + ".png";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fname);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fname;
    }

    // 分享照片

}
