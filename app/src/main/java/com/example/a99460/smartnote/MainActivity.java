package com.example.a99460.smartnote;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.RelativeLayout;

import android.widget.TextView;

import android.widget.TimePicker;
import android.widget.Toast;

import com.gongyunhaoyyy.password.AboutUsActivity;
import com.gongyunhaoyyy.password.ColorSelectActivity;
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
    private FrameLayout mainfl;
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
    private RelativeLayout mtitle;
    private RelativeLayout navHaed;
    public Drawable orange_title;
    public Drawable blue_title;
    public Drawable purple_title;
    public Drawable search_et_orange;
    public Drawable search_et_blue;
    public Drawable search_et_purple;
    public Drawable line_orange;
    public Drawable line_blue;
    public Drawable line_purple;
    String result = "";
    long triggerAtTime;
    int COLOR2;
    ImageButton open_navi;
    NavigationView nav;
    private DrawerLayout mdrawlayout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        final boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {
            LitePal.getDatabase();
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blue_title=getResources().getDrawable( R.drawable.nav_skyblue );
        orange_title=getResources().getDrawable( R.drawable.nav_orange );
        purple_title=getResources().getDrawable( R.drawable.nav_purple );
        search_et_orange=getResources().getDrawable( R.drawable.search_et_orange_bg );
        search_et_blue=getResources().getDrawable( R.drawable.search_et_blue_bg );
        search_et_purple=getResources().getDrawable( R.drawable.search_et_purple_bg );
        line_orange=getResources().getDrawable( R.drawable.line_orange );
        line_blue=getResources().getDrawable( R.drawable.line_blue );
        line_purple=getResources().getDrawable( R.drawable.line_purple );

        fab = (FloatingActionButton) findViewById(R.id.fab);
        mtitle=(RelativeLayout)findViewById( R.id.m_title );
        mainfl=(FrameLayout)findViewById( R.id.main_framelo );
        search_et=(EditText)findViewById( R.id.search_et );
        navHaed=(RelativeLayout)findViewById( R.id.nav_head );
        mdrawlayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        nav=(NavigationView)findViewById( R.id.nav_view );
        searchbt=(Button)findViewById( R.id.search_btn);
        mLv = (ListView)findViewById(R.id.list);
        initdata();

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                //将侧边栏顶部延伸至status bar
                mdrawlayout.setFitsSystemWindows(true);
                //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
                mdrawlayout.setClipToPadding(false);
            }
        }


        SharedPreferences typef=getSharedPreferences( "typeface",MODE_PRIVATE );
        String tftf=typef.getString( "typefacehaha","" );
        //onCreat中注册Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(tftf)
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        diandi=(TextView)findViewById( R.id.diandi );
        search_lo=(RelativeLayout)findViewById( R.id.search_layout );
        searchLv = (ListView) findViewById( R.id.search_lv );
        searchLv.setTextFilterEnabled(true);
        search_LL=(LinearLayout)findViewById( R.id.find_note_linear ) ;
        open_navi = (ImageButton)findViewById(R.id.open_navi);

        open_navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdrawlayout.openDrawer(Gravity.START);
            }
        });

        searchbt.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (fab.getVisibility()==View.GONE){

                }else {
                    searchbt.setVisibility( View.GONE );
                    TranslateAnimation animation1 = new TranslateAnimation(0.0f, 0.0f, 0.0f, 400.0f);
                    Animation animation2=new AlphaAnimation( 1.0f,0.0f );
//                    TranslateAnimation animation2 = new TranslateAnimation(0.0f, -1100.0f, 0.0f, 0.0f);
                    TranslateAnimation animation3 = new TranslateAnimation(0.0f, 0.0f, 0.0f, -300.0f);
                    TranslateAnimation animation4 = new TranslateAnimation(1000.0f, 0.0f, 0.0f, 0.0f);
                    TranslateAnimation animation5 =new TranslateAnimation(0.0f,-300.0f,0.0f,0.0f);
                    animation1.setDuration(330);
                    animation2.setDuration(300);
                    animation3.setDuration(330);
                    animation4.setDuration(330);
                    animation5.setDuration(330);
                    open_navi.startAnimation(animation5);
                    open_navi.setVisibility(View.GONE);
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
                    case R.id.menu_themecolor:
                        startActivity( new Intent(MainActivity.this,ColorSelectActivity.class) );
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
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(fab.getVisibility()==View.GONE){
            TranslateAnimation animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
            );
            Animation animation2=new AlphaAnimation( 0.0f,1.0f );
            TranslateAnimation animation3 = new TranslateAnimation(0.0f, 1000.0f, 0.0f, 0.0f);
            TranslateAnimation animation4 = new TranslateAnimation(-760.0f, 0.0f, 0.0f, 0.0f);
            TranslateAnimation animation5 = new TranslateAnimation(0.0f, 0.0f, -300.0f, 0.0f);
            TranslateAnimation animation6 = new TranslateAnimation(-300.0f,0.0f,0.0f,0.0f);
            animation6.setDuration(330);
            animation2.setDuration(300);
            animation.setDuration(330);
            animation3.setDuration(330);
            animation4.setDuration(330);
            animation5.setDuration(330);
            open_navi.setVisibility(View.VISIBLE);
            open_navi.startAnimation(animation6);
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
        }else if (mdrawlayout.isDrawerOpen( Gravity.START )){
            mdrawlayout.closeDrawer(Gravity.START);
        } else {
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
                .build());

        SharedPreferences themeColor=getSharedPreferences( "themecolor",MODE_PRIVATE );
        COLOR2=themeColor.getInt( "themecolorhaha",0 );

        searchLv = (ListView) findViewById( R.id.search_lv );
        searchLv.setTextFilterEnabled(true);
        mLv = (ListView) findViewById(R.id.list);
        initdata();
        initdata2( null );

        mtitle=(RelativeLayout)findViewById( R.id.m_title );
        fab = (FloatingActionButton) findViewById(R.id.fab);
        /**
         * -----------------更换主题颜色------------------
         */

            if (COLOR2==0){
                mLv.setDivider( line_orange );
                mainfl.setBackgroundColor( Color.parseColor( "#fef4f3" ) );
                mtitle.setBackground( orange_title );
                search_et.setBackground( search_et_orange );
                fab.setBackgroundTintList( ColorStateList.valueOf( Color.parseColor("#fb7a6a") ) );
                nav.getHeaderView( 0 ).setBackgroundColor( Color.parseColor( "#fb7a6a" ) );
            } else if (COLOR2==1){
                mLv.setDivider( line_blue );
                mainfl.setBackgroundColor( Color.parseColor( "#96f2f5f5" ) );
                mtitle.setBackground( blue_title );
                search_et.setBackground( search_et_blue );
                fab.setBackgroundTintList( ColorStateList.valueOf( Color.parseColor("#46bfe4") ) );
                nav.getHeaderView( 0 ).setBackgroundColor( Color.parseColor( "#46bfe4" ) );
            }else if (COLOR2==2){
                mLv.setDivider( line_purple );
                mainfl.setBackgroundColor( Color.parseColor( "#96ece2fb" ) );
                mtitle.setBackground( purple_title );
                search_et.setBackground( search_et_purple );
                fab.setBackgroundTintList( ColorStateList.valueOf( Color.parseColor("#b176f0") ) );
                nav.getHeaderView( 0 ).setBackgroundColor( Color.parseColor( "#b176f0" ) );
            }

        /**
         *---------------------搜索功能实现方法----------------------------
         */
        search_et=(EditText)findViewById( R.id.search_et );
        search_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //限制回车,当按回车时返回true
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
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
                    } else if((note.isphoto||note.isalbum)&&!note.isrecord){
                        holder.setText(R.id.content1,"照片文件");
                    }
                    else if (note.isrecord&&!(note.isphoto||note.isalbum)){
                        holder.setText(R.id.content1,"音频文件");
                    }
                    else if (note.isrecord&&(note.isphoto||note.isalbum)){
                        holder.setText(R.id.content1,"照片文件&&音频文件");
                    }
                }

                holder.setText(R.id.content2,note.date);

                if (note.isalarm==true){
                    holder.setVisible(R.id.content3,true);
                } else {
                    holder.setVisible(R.id.content3,false);
                }
                if (note.isrecord==true){
                    holder.setVisible(R.id.content4,true);
                } else {
                    holder.setVisible(R.id.content4,false);
                }
                if (note.isphoto==true||note.isalbum==true){
                    holder.setVisible(R.id.content5,true);
                } else {
                    holder.setVisible(R.id.content5,false);
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
        List<Notedata> notedatas = DataSupport.order( "date desc" ).find(Notedata.class);
        for (Notedata notedata:notedatas){
            if (notedata.isEdit()||notedata.isRecord()||notedata.isAlbum()||notedata.isAlarm()) {
                mDatas.add(new Note(notedata.getDate(), notedata.getNote(), notedata.getId(), notedata.isAlarm(),notedata.isRecord(),notedata.isPhoto(),notedata.isAlbum()));
            }
        }
    }

    protected void initdata2(String temp){
        searchData = new ArrayList<>();
        List<Notedata> notedatas = DataSupport
                .where("note like ?","%"+temp+"%").find( Notedata.class );
        for (Notedata notedata:notedatas){
            if (!notedata.isLock()){
                searchData.add(new Note(notedata.getDate(), notedata.getNote(), notedata.getId(), notedata.isAlarm(),notedata.isRecord(),notedata.isPhoto(),notedata.isAlarm()));
            }
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


    public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置内容布局属性
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
            contentLayout.setFitsSystemWindows(true);
            contentLayout.setClipToPadding(true);
            // 设置抽屉布局属性
            ViewGroup vg = (ViewGroup) drawerLayout.getChildAt(1);
            vg.setFitsSystemWindows(false);
            // 设置 DrawerLayout 属性
            drawerLayout.setFitsSystemWindows(false);
        }
    }

}