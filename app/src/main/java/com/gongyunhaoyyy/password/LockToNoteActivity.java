package com.gongyunhaoyyy.password;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.andrognito.rxpatternlockview.RxPatternLockView;
import com.andrognito.rxpatternlockview.events.PatternLockCompoundEvent;
import com.example.a99460.smartnote.Notedata;
import com.example.a99460.smartnote.R;
import com.example.a99460.smartnote.note_activity;

import org.litepal.crud.DataSupport;

import java.util.List;

import io.reactivex.functions.Consumer;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LockToNoteActivity extends AppCompatActivity {
    private PatternLockView mPatternLockView;
    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {}
        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {}
        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {}
        @Override
        public void onCleared() {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_lock_to_note );

        SharedPreferences typef=getSharedPreferences( "typeface",MODE_PRIVATE );
        String tftf=typef.getString( "typefacehaha","" );
        //onCreat中注册Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(tftf)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        TextView db_titlt_tv=(TextView)findViewById( R.id.ltn_title_TextView );
        final TextView db_hint_tv=(TextView)findViewById( R.id.ltn_hint_TextView );
        db_titlt_tv.setText( "请输入密码" );
        //记录输入错误次数
        final int[] wrongpw = {5};
        Intent it=getIntent();

        final int myid=it.getIntExtra( "in_data",-1 );


        //-----------------------------------------------------------
        mPatternLockView = (PatternLockView) findViewById(R.id.ltn_patter_lock_view);
        mPatternLockView.setDotCount(3);
        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.line));
        mPatternLockView.setNormalStateColor( ResourceUtils.getColor(this, R.color.line) );
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(false);
        mPatternLockView.setInputEnabled(true);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);

        RxPatternLockView.patternChanges(mPatternLockView)
                .subscribe(new Consumer<PatternLockCompoundEvent>() {
                    @Override
                    public void accept(PatternLockCompoundEvent event) throws Exception {
                        if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_STARTED) {
                            db_hint_tv.setText( "开始绘制" );
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_PROGRESS) {
                            db_hint_tv.setText( "松手后完成" );
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_COMPLETE) {
                            //获取得到string形式的密码
                            String password= PatternLockUtils.patternToString(mPatternLockView, event.getPattern());
                            int passwordlength=password.length();
                            if (passwordlength<=3){
                                db_hint_tv.setText( "至少连接4个点,请重试" );
                            }else {
                                db_hint_tv.setText( "绘制完成" );
                                SharedPreferences pref=getSharedPreferences( "data",MODE_PRIVATE );
                                String opassword=pref.getString( "oldpassword","" );
                                if (opassword.equals( password )){
                                    Intent intent=new Intent( LockToNoteActivity.this,note_activity.class );
                                    intent.putExtra( "in_data",myid );

                                    startActivity( intent);

                                    finish();
                                }else {
                                    wrongpw[0]-=1;
                                    if (wrongpw[0]>0){
                                        db_hint_tv.setText( "密码错误,还有"+ wrongpw[0] +"次机会！" );
                                    }else {
                                        db_hint_tv.setText( "警告！" );
                                        Toast.makeText( LockToNoteActivity.this,"密码功能锁定,请30秒后重试",Toast.LENGTH_LONG ).show();
                                        SharedPreferences.Editor timeeditor=getSharedPreferences( "time",MODE_PRIVATE ).edit();
                                        timeeditor.putLong( "wrongtime", System.currentTimeMillis());
                                        timeeditor.apply();
                                        finish();
                                    }
                                }
                            }
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext( CalligraphyContextWrapper.wrap(newBase));
    }

}
