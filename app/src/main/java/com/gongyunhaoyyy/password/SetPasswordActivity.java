package com.gongyunhaoyyy.password;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.andrognito.rxpatternlockview.RxPatternLockView;
import com.andrognito.rxpatternlockview.events.PatternLockCompoundEvent;
import com.example.a99460.smartnote.R;

import java.util.List;

import io.reactivex.functions.Consumer;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SetPasswordActivity extends AppCompatActivity {
    private int COLOR;
    private LinearLayout spwln;
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
        setContentView( R.layout.activity_set_password );
        spwln=(LinearLayout)findViewById( R.id.setpwln );
        SharedPreferences themeColor=getSharedPreferences( "themecolor",MODE_PRIVATE );
        COLOR=themeColor.getInt( "themecolorhaha",0 );
        /**
         * ---------------更换主题颜色----------------
         */
        if (COLOR==0){
            spwln.setBackgroundColor( Color.parseColor( "#fef4f3" ) );
        } else if (COLOR==1){
            spwln.setBackgroundColor( Color.parseColor( "#96f2f5f5" ) );
        }else if (COLOR==2){
            spwln.setBackgroundColor( Color.parseColor( "#96ece2fb" ) );
        }

        SharedPreferences typef=getSharedPreferences( "typeface",MODE_PRIVATE );
        String tftf=typef.getString( "typefacehaha","" );
        //onCreat中注册Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(tftf)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //记录新密码次数
        final int[] i = {0};

        //记录输入错误次数
        final int[] wrongpw = {5};
        final TextView textView=(TextView)findViewById( R.id.set_title_TextView );
        final TextView test=(TextView)findViewById( R.id.set_hint_TextView );
        textView.setText( "输入新密码");

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
                            test.setText( "开始绘制" );
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_PROGRESS) {
                            test.setText( "松手后完成" );
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_COMPLETE) {
                            //获取得到当前输入的string形式的密码
                            String password= PatternLockUtils.patternToString(mPatternLockView, event.getPattern());
                            int passwordlength=password.length();
                            if (passwordlength<=3){
                                test.setText( "至少连接4个点,请重试" );
                            }else {
                                if (i[0] ==0){
                                    SharedPreferences.Editor editor=getSharedPreferences( "newpwfirst",MODE_PRIVATE ).edit();
                                    editor.putString( "passwordfirst", password);
                                    editor.apply();
                                    test.setText( "绘制成功，再次绘制以确认" );
                                    i[0]++;
                                }else if (i[0] ==1){
                                    SharedPreferences pref=getSharedPreferences( "newpwfirst",MODE_PRIVATE );
                                    String pwfirst=pref.getString( "passwordfirst","" );
                                    if (password.equals( pwfirst )){
                                        SharedPreferences.Editor editor=getSharedPreferences( "data",MODE_PRIVATE ).edit();
                                        editor.putString( "oldpassword", password);
                                        editor.apply();
                                        Toast.makeText( SetPasswordActivity.this,"密码设置成功",Toast.LENGTH_SHORT ).show();
                                        finish();
                                        i[0]=0;
                                    }else {
                                        test.setText( "密码不一致，请再次绘制" );
                                    }
                                }
                            }
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_CLEARED) {
                            test.setText( "密码清空" );
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
