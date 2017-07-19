package com.example.a99460.smartnote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.RelativeLayout;

import android.widget.TextView;

import android.widget.TimePicker;
import android.widget.Toast;

import com.gongyunhaoyyy.password.AboutUsActivity;
import com.gongyunhaoyyy.password.DeblockingActivity;
import com.gongyunhaoyyy.password.LockActivity;
import com.gongyunhaoyyy.password.LockToNoteActivity;
import com.gongyunhaoyyy.password.ThemeSelectActivity;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import com.nightonke.boommenu.BoomMenuButton;


import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {
    private ListView mLv;
    private List<Note> mDatas;
    private List<Note> searchData;
    private FloatingActionButton fab;
    private ListView searchLv;
    private TextView diandi;
    private RelativeLayout search_lo;
    private Button searchbt;
    private EditText search_et;
    private LinearLayout search_LL;
    String result = "";
    long triggerAtTime;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        final boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if(isFirstRun){
            LitePal.getDatabase();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initdata();
        NavigationView nav=(NavigationView)findViewById( R.id.nav_view );

        searchbt=(Button)findViewById( R.id.search_btn );


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        SharedPreferences typef=getSharedPreferences( "typeface",MODE_PRIVATE );
        String tftf=typef.getString( "typefacehaha","" );
        //onCreat中注册Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(tftf)
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        fab = (FloatingActionButton) findViewById(R.id.fab);
        diandi=(TextView)findViewById( R.id.diandi );
        search_lo=(RelativeLayout)findViewById( R.id.search_layout );
        searchLv = (ListView) findViewById( R.id.search_lv );
        searchLv.setTextFilterEnabled(true);
        search_LL=(LinearLayout)findViewById( R.id.find_note_linear ) ;

        searchbt.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (fab.getVisibility()==View.GONE){

                }else {
                    searchbt.setVisibility( View.GONE );
                    TranslateAnimation animation1 = new TranslateAnimation(0.0f, 0.0f, 0.0f, 400.0f);
                    TranslateAnimation animation2 = new TranslateAnimation(0.0f, -1100.0f, 0.0f, 0.0f);
                    TranslateAnimation animation3 = new TranslateAnimation(0.0f, 0.0f, 0.0f, -300.0f);
                    TranslateAnimation animation4 = new TranslateAnimation(1000.0f, 0.0f, 0.0f, 0.0f);
                    animation1.setDuration(330);
                    animation2.setDuration(330);
                    animation3.setDuration(330);
                    animation4.setDuration(330);
                    fab.startAnimation( animation1 );
                    mLv.startAnimation( animation2 );
                    diandi.startAnimation( animation3 );
                    search_LL.setVisibility( View.VISIBLE );
                    search_lo.setVisibility( View.VISIBLE );
                    search_lo.startAnimation( animation4 );
                    mLv.setVisibility( View.GONE );
                    fab.setVisibility(View.GONE);
                    diandi.setVisibility( View.GONE );
                }
            }
        } );

        nav.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener( ) {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_lock:
                        //设置新密码or修改密码
                        if (isDeadLock()){
                            Toast.makeText( MainActivity.this,"密码功能锁定中...",Toast.LENGTH_SHORT ).show();
                        }else {
                            startActivity(new Intent(MainActivity.this,LockActivity.class));
                        }
                        break;
                    case R.id.menu_typeface:
                        startActivity(new Intent(MainActivity.this,ThemeSelectActivity.class));
                        break;
                    case R.id.menu_aboutus:
                        startActivity(new Intent(MainActivity.this,AboutUsActivity.class));
                        Toast.makeText( MainActivity.this,"关于我们(待完成)",Toast.LENGTH_SHORT ).show();
                }
                return false;
            }
        } );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, note_activity.class);
                startActivity(intent);
            }
        });
        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
                    if (null != viewCache) {
                        viewCache.smoothClose();
                    }
                }
                return false;
            }
        });

        search_et=(EditText)findViewById( R.id.search_et );
        mLv = (ListView)findViewById(R.id.list);
        mLv.setAdapter(new CommonAdapter<Note>(this, mDatas, R.layout./*item_swipe_menu*/item_note) {
            @Override
            public void convert(final ViewHolder holder, final Note note, final int position) {
                //((CstSwipeDelMenu)holder.getConvertView()).setIos(false);//这句话关掉IOS阻塞式交互效果
                int iddd=note.id;
                Notedata nd=DataSupport.find( Notedata.class,iddd );
                boolean lock=nd.isLock();
                if(lock){
                    holder.setText(R.id.content1,"已上锁" );
                }else {
                    if(note.note!=null){
                    holder.setText(R.id.content1, note.note.trim());
                    }
                }

                holder.setText(R.id.content2,note.date);

                if (note.isalarm==true){
                holder.setVisible(R.id.content3,true);
                }
                else {
                    holder.setVisible(R.id.content3,false);
                }

                if (note.isrecord==true){
                    holder.setVisible(R.id.content4,true);
                }
                else
                {
                    holder.setVisible(R.id.content4,false);
                }

                holder.setOnClickListener(R.id.content, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((SwipeMenuLayout) holder.getConvertView()).quickClose();

                        int id=note.id;
                        Notedata notedata = DataSupport.find(Notedata.class, id);

                        boolean islock=notedata.isLock();
                        if (isDeadLock()){
                            Toast.makeText( MainActivity.this,"无法进入密码锁",Toast.LENGTH_SHORT ).show();
                        }else {
                            if (islock){
                                Intent lock=new Intent( MainActivity.this, LockToNoteActivity.class );
                                lock.putExtra( "in_data",id );
                                startActivity( lock);
                            }else {
                                Intent intent = new Intent(MainActivity.this, note_activity.class);
                                intent.putExtra("in_data",id);
                                startActivity(intent);
                            }
                        }
                    }
                });

                holder.setOnClickListener(R.id.btnDelete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //在ListView里，点击侧滑菜单上的选项时，如果想让擦花菜单同时关闭，调用这句话
                        ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                        mDatas.remove(position);
                        notifyDataSetChanged();
                        DataSupport.delete(Notedata.class,note.id);
                        File file = new File("/data/data/com.example.a99460.smartnote" + "/smartnote" + note.id + ".mp3");
                        file.delete();
                    }
                });

                holder.setOnClickListener(R.id.alarm,new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                        if(note.isalarm==true){
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("提醒");
                            dialog.setMessage("是否修改闹钟？");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("修改闹钟",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog,int which){
                                    final Calendar cale2 = Calendar.getInstance();

                                    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            result = "";
                                            result += "您选择的时间是:"+hourOfDay+"时"+minute+"分";
                                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                            Notedata notedata = DataSupport.find(Notedata.class,note.id);
                                            notedata.setHour(hourOfDay);
                                            notedata.setMinute(minute);
                                            notedata.setAlarm(true);
                                            note.isalarm=true;
                                            notedata.save();
                                            long setTime = (60*hourOfDay+minute)*60*1000;
                                            long currentTime = (60*cale2.get(Calendar.HOUR_OF_DAY)+cale2.get(Calendar.MINUTE))*60*1000;
                                            if (setTime>currentTime) {
                                                triggerAtTime = System.currentTimeMillis()+setTime-currentTime;
                                            }
                                            else {
                                                triggerAtTime = System.currentTimeMillis()+setTime-currentTime+24*60*60*1000;
                                            }
                                            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                            intent.putExtra("id",note.id);
                                            intent.setAction("com.example.alarmtest.ALARM_RECEIVER");
                                            intent.setClass(MainActivity.this, AlarmReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,note.id, intent, 0);
                                            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            am.setExact(AlarmManager.RTC_WAKEUP,triggerAtTime, pendingIntent);

                                        }
                                    };
                                    TimePickerDialog my = new TimePickerDialog(MainActivity.this,mTimeSetListener,cale2.get(Calendar.HOUR_OF_DAY), cale2.get(Calendar.MINUTE),true);
                                    my.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        public void onCancel(DialogInterface dialog) {
                                        }
                                    });
                                    my.show();
                                    holder.setVisible(R.id.content3,true);
                                }
                            });
                            dialog.setNegativeButton("取消该闹钟",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog,int which){
                                    Notedata notedata = DataSupport.find(Notedata.class,note.id);
                                    notedata.setAlarm(false);
                                    note.isalarm=false;
                                    notedata.save();
                                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                    intent.putExtra("id",note.id);
                                    intent.setAction("com.example.alarmtest.ALARM_RECEIVER");
                                    intent.setClass(MainActivity.this, AlarmReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,note.id, intent, 0);
                                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    am.cancel( pendingIntent);
                                    holder.setVisible(R.id.content3,false);
                                }
                            });
                            dialog.show();
                        }

                        else{
                            final Calendar cale2 = Calendar.getInstance();

                            TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    result = "";
                                    result += "您选择的时间是:"+hourOfDay+"时"+minute+"分";
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    Notedata notedata = DataSupport.find(Notedata.class,note.id);
                                    notedata.setHour(hourOfDay);
                                    notedata.setMinute(minute);
                                    notedata.setAlarm(true);
                                    note.isalarm=true;
                                    notedata.save();
                                    long setTime = (60*hourOfDay+minute)*60*1000;
                                    long currentTime = (60*cale2.get(Calendar.HOUR_OF_DAY)+cale2.get(Calendar.MINUTE))*60*1000;
                                    if (setTime>currentTime) {
                                        triggerAtTime = System.currentTimeMillis()+setTime-currentTime;
                                    }
                                    else {
                                        triggerAtTime = System.currentTimeMillis()+setTime-currentTime+24*60*60*1000;
                                    }
                                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                    intent.putExtra("id",note.id);
                                    intent.setAction("com.example.alarmtest.ALARM_RECEIVER");
                                    intent.setClass(MainActivity.this, AlarmReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,note.id, intent, 0);
                                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

                                    am.setExact(AlarmManager.RTC_WAKEUP,triggerAtTime, pendingIntent);
                                    holder.setVisible(R.id.content3,true);

                                }
                            };
                            TimePickerDialog my = new TimePickerDialog(MainActivity.this,mTimeSetListener,cale2.get(Calendar.HOUR_OF_DAY), cale2.get(Calendar.MINUTE),true);
                            my.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {

                                }
                            });
                            my.show();
                        }
                    }
                } );

                holder.setOnClickListener(R.id.btnLock, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                        SharedPreferences pref=getSharedPreferences( "data",MODE_PRIVATE );
                        final String opassword=pref.getString( "oldpassword","" );
                        int id=note.id;
                        Notedata notedata = DataSupport.find(Notedata.class, id);
                        boolean islock=notedata.isLock();
                        //判断是否设置了密码
                        if (opassword==null||opassword.length()<=0){
                            Toast.makeText( MainActivity.this,"未设置密码,点左下角设置",Toast.LENGTH_SHORT ).show();
                        }else{
                            if (!islock){//上锁
                                notedata.setLock( true );
                                notedata.save();
                                holder.setText(R.id.content1,"已上锁" );
                                Toast.makeText( MainActivity.this,"上锁成功",Toast.LENGTH_SHORT ).show();
                            }else {//解锁
                                if (isDeadLock()){
                                    Toast.makeText( MainActivity.this,"密码功能锁定中...",Toast.LENGTH_SHORT ).show();
                                }else {
                                    Intent intent=new Intent( MainActivity.this, DeblockingActivity.class );
                                    intent.putExtra("deblocking",id);
                                    startActivity( intent );
                                }
                            }
                        }
                    }
                });
            }
        });
        mLv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
                    if (null != viewCache) {
                        viewCache.smoothClose();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(fab.getVisibility()==View.GONE){
            TranslateAnimation animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
            );
            TranslateAnimation animation2 = new TranslateAnimation(-1100.0f, 0.0f, 0.0f, 0.0f);
            TranslateAnimation animation3 = new TranslateAnimation(0.0f, 1000.0f, 0.0f, 0.0f);
            TranslateAnimation animation4 = new TranslateAnimation(-1000.0f, 0.0f, 0.0f, 0.0f);
            TranslateAnimation animation5 = new TranslateAnimation(0.0f, 0.0f, -300.0f, 0.0f);
            animation2.setDuration(330);
            animation.setDuration(330);
            animation3.setDuration(330);
            animation4.setDuration(330);
            animation5.setDuration(330);
            search_LL.setVisibility( View.GONE );
            search_lo.startAnimation( animation3 );
            search_lo.setVisibility( View.GONE );
            fab.setVisibility( View.VISIBLE );
            fab.startAnimation(animation);
            diandi.setVisibility( View.VISIBLE );
            diandi.startAnimation( animation5 );
            mLv.setVisibility( View.VISIBLE );
            mLv.startAnimation( animation2 );
            searchbt.startAnimation( animation4 );
            searchbt.setVisibility( View.VISIBLE );
            search_et.setText( null );
        }else {
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext( CalligraphyContextWrapper.wrap(newBase));
    }

    //强制竖屏
    @Override
    protected void onResume() {
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences typef=getSharedPreferences( "typeface",MODE_PRIVATE );
        String tftf=typef.getString( "typefacehaha","" );
        //onStart中注册Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(tftf)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        searchLv = (ListView) findViewById( R.id.search_lv );
        searchLv.setTextFilterEnabled(true);
        mLv = (ListView) findViewById(R.id.list);
        initdata();
        initdata2( null );

        /*
         *---------------------搜索功能实现方法----------------------------
         */
        search_et=(EditText)findViewById( R.id.search_et );
        //搜索框的文本变化实时监听
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            //输入后调用该方法
            @Override
            public void afterTextChanged(Editable s) {
                String temp = search_et.getText().toString();
                if(TextUtils.isEmpty( temp )){
                    initdata2( null );
                }else {
                    initdata2(temp);
                }
                searchLv.setAdapter( new CommonAdapter<Note>(MainActivity.this, searchData, R.layout.item_note_search){
                    @Override
                    public void convert(final ViewHolder holder, final Note note, final int delete) {
                        int iddd=note.id;
                        Notedata nd=DataSupport.find( Notedata.class,iddd );
                        boolean lock=nd.isLock();
                        if(lock){
                            holder.setText(R.id.content1,"已上锁" );
                        }else {
                            holder.setText(R.id.content1, note.note.trim());
                        }

                        holder.setText(R.id.content2,note.date);
                        if (note.isalarm==true){
                            holder.setVisible(R.id.content3,true);
                        }
                        else {
                            holder.setVisible(R.id.content3,false);
                        }
                        holder.setOnClickListener(R.id.content, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                                int id=note.id;
                                Notedata notedata = DataSupport.find(Notedata.class, id);
                                boolean islock=notedata.isLock();
                                if (isDeadLock()){
                                    Toast.makeText( MainActivity.this,"无法进入密码锁",Toast.LENGTH_SHORT ).show();
                                }else {
                                    if (islock){
                                        Intent lock=new Intent( MainActivity.this, LockToNoteActivity.class );
                                        lock.putExtra( "in_data",id );
                                        startActivity( lock);
                                    }else {
                                        Intent intent = new Intent(MainActivity.this, note_activity.class);
                                        intent.putExtra("in_data",id);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, note_activity.class);
                startActivity(intent);
            }
        });
        mLv.setAdapter(new CommonAdapter<Note>(this, mDatas, R.layout./*item_swipe_menu*/item_note) {
            @Override
            public void convert(final ViewHolder holder, final Note note, final int position) {
                //((CstSwipeDelMenu)holder.getConvertView()).setIos(false);//这句话关掉IOS阻塞式交互效果
                int iddd=note.id;
                Notedata nd=DataSupport.find( Notedata.class,iddd );
                boolean lock=nd.isLock();
                if(lock){
                    holder.setText(R.id.content1,"已上锁" );
                }else {
                    if(note.note!=null){
                    holder.setText(R.id.content1, note.note.trim());
                    }
                }

                holder.setText(R.id.content2,note.date);

                if (note.isalarm==true){
                    holder.setVisible(R.id.content3,true);
                }
                else
                {
                    holder.setVisible(R.id.content3,false);
                }

                if (note.isrecord==true){
                    holder.setVisible(R.id.content4,true);
                }
                else
                {
                    holder.setVisible(R.id.content4,false);
                }

                holder.setOnClickListener(R.id.content, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                        int id=note.id;
                        Notedata notedata = DataSupport.find(Notedata.class, id);
                        boolean islock=notedata.isLock();
                        if (isDeadLock()){
                            Toast.makeText( MainActivity.this,"无法进入密码锁",Toast.LENGTH_SHORT ).show();
                        }else {
                            if (islock){
                                Intent lock=new Intent( MainActivity.this, LockToNoteActivity.class );
                                lock.putExtra( "in_data",id );
                                startActivity(lock);
                            }else {
                                Intent intent = new Intent(MainActivity.this, note_activity.class);
                                intent.putExtra("in_data",id);
                                startActivity(intent);
                            }
                        }
                    }
                });

                holder.setOnClickListener(R.id.btnDelete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //在ListView里，点击侧滑菜单上的选项时，如果想让擦花菜单同时关闭，调用这句话
                        ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                        mDatas.remove(position);
                        notifyDataSetChanged();
                        DataSupport.delete(Notedata.class,note.id);
                        File file = new File("/data/data/com.example.a99460.smartnote" + "/smartnote" + note.id + ".mp3");
                        file.delete();
                    }
                });


                holder.setOnClickListener(R.id.alarm,new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                        if(note.isalarm==true){
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("提醒");
                            dialog.setMessage("是否修改闹钟？");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("修改闹钟",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog,int which){
                                    final Calendar cale2 = Calendar.getInstance();

                                    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            result = "";
                                            result += "您选择的时间是:"+hourOfDay+"时"+minute+"分";
                                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                            Notedata notedata = DataSupport.find(Notedata.class,note.id);
                                            notedata.setHour(hourOfDay);
                                            notedata.setMinute(minute);
                                            notedata.setAlarm(true);
                                            note.isalarm=true;
                                            notedata.save();
                                            long setTime = (60*hourOfDay+minute)*60*1000;
                                            long currentTime = (60*cale2.get(Calendar.HOUR_OF_DAY)+cale2.get(Calendar.MINUTE))*60*1000;
                                            if (setTime>currentTime) {
                                                triggerAtTime = System.currentTimeMillis()+setTime-currentTime;
                                            }
                                            else {
                                                triggerAtTime = System.currentTimeMillis()+setTime-currentTime+24*60*60*1000;
                                            }
                                            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                            intent.putExtra("id",note.id);
                                            intent.setAction("com.example.alarmtest.ALARM_RECEIVER");
                                            intent.setClass(MainActivity.this, AlarmReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,note.id, intent, 0);
                                            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            am.setExact(AlarmManager.RTC_WAKEUP,triggerAtTime, pendingIntent);
                         }
                                    };
                                    TimePickerDialog my = new TimePickerDialog(MainActivity.this,mTimeSetListener,cale2.get(Calendar.HOUR_OF_DAY), cale2.get(Calendar.MINUTE),true);
                                    my.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        public void onCancel(DialogInterface dialog) {
                                        }
                                    });
                                    my.show();
                                    holder.setVisible(R.id.content3,true);
                                }
                            });
                            dialog.setNegativeButton("取消该闹钟",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog,int which){
                                    Notedata notedata = DataSupport.find(Notedata.class,note.id);
                                    notedata.setAlarm(false);
                                    note.isalarm=false;
                                    notedata.save();
                                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                    intent.putExtra("id",note.id);
                                    intent.setAction("com.example.alarmtest.ALARM_RECEIVER");
                                    intent.setClass(MainActivity.this, AlarmReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,note.id, intent, 0);
                                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    am.cancel( pendingIntent);
                                    holder.setVisible(R.id.content3,false);
                                }
                            });
                            dialog.show();
                        }

                        else{
                            final Calendar cale2 = Calendar.getInstance();

                            TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    result = "";
                                    result += "您选择的时间是:"+hourOfDay+"时"+minute+"分";
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    Notedata notedata = DataSupport.find(Notedata.class,note.id);
                                    notedata.setHour(hourOfDay);
                                    notedata.setMinute(minute);
                                    note.isalarm=true;
                                    notedata.setAlarm(true);
                                    notedata.save();
                                    long setTime = (60*hourOfDay+minute)*60*1000;
                                    long currentTime = (60*cale2.get(Calendar.HOUR_OF_DAY)+cale2.get(Calendar.MINUTE))*60*1000;
                                    if (setTime>currentTime ) {
                                         triggerAtTime = System.currentTimeMillis()+setTime-currentTime;
                                    }
                                    else {
                                         triggerAtTime = System.currentTimeMillis()+setTime-currentTime+24*60*60*1000;
                                    }
                                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                    intent.putExtra("id",note.id);
                                    intent.setAction("com.example.alarmtest.ALARM_RECEIVER");
                                    intent.setClass(MainActivity.this, AlarmReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,note.id, intent, 0);
                                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    am.setExact(AlarmManager.RTC_WAKEUP,triggerAtTime, pendingIntent);
                                    holder.setVisible(R.id.content3,true);

                                }
                            };
                            TimePickerDialog my = new TimePickerDialog(MainActivity.this,mTimeSetListener,cale2.get(Calendar.HOUR_OF_DAY), cale2.get(Calendar.MINUTE),true);
                            my.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                }
                            });
                            my.show();

                        }
                    }
                } );

                holder.setOnClickListener(R.id.btnLock, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                        SharedPreferences pref=getSharedPreferences( "data",MODE_PRIVATE );
                        final String opassword=pref.getString( "oldpassword","" );
                        int id=note.id;
                        Notedata notedata = DataSupport.find(Notedata.class, id);
                        boolean islock=notedata.isLock();
                        //判断是否设置了密码
                        if (opassword==null||opassword.length()<=0){
                            Toast.makeText( MainActivity.this,"未设置密码,点左上角设置",Toast.LENGTH_SHORT ).show();
                        }else{
                            if (!islock){//上锁
                                notedata.setLock( true );
                                notedata.save();
                                holder.setText(R.id.content1,"已上锁" );
                                Toast.makeText( MainActivity.this,"上锁成功",Toast.LENGTH_SHORT ).show();
                            }else {//解锁
                                if (isDeadLock()){
                                    Toast.makeText( MainActivity.this,"密码功能锁定中...",Toast.LENGTH_SHORT ).show();
                                }else {
                                    Intent intent=new Intent( MainActivity.this, DeblockingActivity.class );
                                    intent.putExtra("deblocking",id);
                                    startActivity( intent );
                                }
                            }
                        }
                    }
                });
            }
        });
        mLv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
                    if (null != viewCache) {
                        viewCache.smoothClose();
                    }
                }
                return false;
            }
        });
    }


    protected void initdata(){
        mDatas = new ArrayList<>();
        List<Notedata> notedatas = DataSupport.findAll(Notedata.class);
        for (Notedata notedata:notedatas){
            if (notedata.isEdit()||notedata.isRecord()) {
                mDatas.add(new Note(notedata.getDate(), notedata.getNote(), notedata.getId(), notedata.isAlarm(),notedata.isRecord()));
            }
        }
    }

    protected void initdata2(String temp){
        searchData = new ArrayList<>();
        List<Notedata> notedatas = DataSupport
                .where("note like ?","%"+temp+"%").find( Notedata.class );
        for (Notedata notedata:notedatas){
            searchData.add(new Note(notedata.getDate(),notedata.getNote(),notedata.getId(),notedata.isAlarm(),notedata.isRecord()));
        }

    }

    boolean isDeadLock(){
        SharedPreferences pref=getSharedPreferences( "time",MODE_PRIVATE );
        Long wt=pref.getLong( "wrongtime",0 );
        if (System.currentTimeMillis()-wt<=30000){
            return true;
        }else {
            return false;
        }
    }
}