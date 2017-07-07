package com.gongyunhaoyyy.password;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.andrognito.rxpatternlockview.RxPatternLockView;
import com.andrognito.rxpatternlockview.events.PatternLockCompoundEvent;
import com.example.a99460.smartnote.R;
import com.example.a99460.smartnote.note_activity;

import java.util.List;

import io.reactivex.functions.Consumer;

public class LockActivity extends AppCompatActivity {
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
        setContentView( R.layout.activity_lock );
        TextView titlt_tv=(TextView)findViewById( R.id.title_TextView );
        final TextView hint_tv=(TextView)findViewById( R.id.hint_TextView );
        //记录输入错误次数
        final int[] wrongpw = {5};
        //记录新密码次数
        final int[] i = {0};
        SharedPreferences pref=getSharedPreferences( "data",MODE_PRIVATE );
        final String opassword=pref.getString( "oldpassword","" );

        if (opassword==null||opassword.length()<=0){
            titlt_tv.setText( "第一次使用，录入新密码" );
        }else {
            titlt_tv.setText( "输入旧密码" );
        }

        //-----------------------------------------------------------

        mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);
        mPatternLockView.setDotCount(3);
        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.white));
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);


        RxPatternLockView.patternChanges(mPatternLockView)
                .subscribe(new Consumer<PatternLockCompoundEvent>() {
                    @Override
                    public void accept(PatternLockCompoundEvent event) throws Exception {
                        if (opassword == null || opassword.length( ) <= 0) {
                            if (event.getEventType( ) == PatternLockCompoundEvent.EventType.PATTERN_STARTED) {
                                hint_tv.setText( "开始绘制" );
                            } else if (event.getEventType( ) == PatternLockCompoundEvent.EventType.PATTERN_PROGRESS) {
                                hint_tv.setText( "松手后完成" );
                            } else if (event.getEventType( ) == PatternLockCompoundEvent.EventType.PATTERN_COMPLETE) {
                                //获取得到string形式的密码
                                String password = PatternLockUtils.patternToString( mPatternLockView, event.getPattern( ) );
                                int passwordlength = password.length( );
                                if (passwordlength <= 3) {
                                    hint_tv.setText( "至少连接4个点,请重试" );
                                } else {
                                    if (i[0] == 0) {
                                        SharedPreferences.Editor editor = getSharedPreferences( "newpwfirst", MODE_PRIVATE ).edit( );
                                        editor.putString( "passwordfirst", password );
                                        editor.apply( );
                                        hint_tv.setText( "绘制成功，再次绘制以确认" );
                                        i[0]++;
                                    } else if (i[0] == 1) {
                                        SharedPreferences pref = getSharedPreferences( "newpwfirst", MODE_PRIVATE );
                                        String pwfirst = pref.getString( "passwordfirst", "" );
                                        if (password.equals( pwfirst )) {
                                            SharedPreferences.Editor editor = getSharedPreferences( "data", MODE_PRIVATE ).edit( );
                                            editor.putString( "oldpassword", password );
                                            editor.apply( );
                                            Toast.makeText( LockActivity.this, "密码设置成功", Toast.LENGTH_SHORT ).show( );
                                            finish( );
                                            i[0] = 0;
                                        } else {
                                            hint_tv.setText( "密码不一致，请再次绘制" );
                                        }
                                    }
                                }
                            }
                        }else {
                            SharedPreferences pref=getSharedPreferences( "data",MODE_PRIVATE );
                            String opassword=pref.getString( "oldpassword","" );
                            if (event.getEventType( ) == PatternLockCompoundEvent.EventType.PATTERN_STARTED) {
                                hint_tv.setText( "开始绘制" );
                            } else if (event.getEventType( ) == PatternLockCompoundEvent.EventType.PATTERN_PROGRESS) {
                                hint_tv.setText( "绘制中..." );
                            } else if (event.getEventType( ) == PatternLockCompoundEvent.EventType.PATTERN_COMPLETE) {
                                //获取得到string形式的密码
                                String password = PatternLockUtils.patternToString( mPatternLockView, event.getPattern( ) );
                                int passwordlength = password.length( );
                                if (passwordlength <= 3) {
                                    hint_tv.setText( "至少连接4个点,请重试" );
                                } else {
                                    if (opassword.equals( password)) {
                                        Intent intent=new Intent( LockActivity.this,SetPasswordActivity.class );
                                        startActivity( intent );
                                        finish();
                                    } else{
                                        wrongpw[0]-=1;
                                        if (wrongpw[0]>0){
                                            hint_tv.setText( "密码错误,还有"+ wrongpw[0] +"次机会！" );
                                        }else {
                                            hint_tv.setText( "警告！" );
                                            Toast.makeText( LockActivity.this,"密码功能锁定,请30秒后重试",Toast.LENGTH_LONG ).show();
                                            SharedPreferences.Editor timeeditor=getSharedPreferences( "time",MODE_PRIVATE ).edit();
                                            timeeditor.putLong( "wrongtime", System.currentTimeMillis());
                                            timeeditor.apply();
                                            finish();
                                        }
                                    }
                                }
                            }
                        }
                    }

                });

    }
}
