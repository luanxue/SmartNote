package com.gongyunhaoyyy.password;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a99460.smartnote.R;

public class ThemeSelectActivity extends AppCompatActivity {
    private int COLOR;
    private ConstraintLayout aaaaa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_theme_select );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        aaaaa=(ConstraintLayout) findViewById( R.id.theme_se );
        SharedPreferences themeColor=getSharedPreferences( "themecolor",MODE_PRIVATE );
        COLOR=themeColor.getInt( "themecolorhaha",0 );
        /**
         * ---------------更换主题颜色----------------
         */
        if (COLOR==0){
            aaaaa.setBackgroundColor( Color.parseColor( "#fef4f3" ) );
        } else if (COLOR==1){
            aaaaa.setBackgroundColor( Color.parseColor( "#96f2f5f5" ) );
        }

        Button huawenxingkai=(Button)findViewById( R.id.huawenxingkai );
        Button katongjianti=(Button)findViewById( R.id.katongjianti );
        Button wawaziti=(Button)findViewById( R.id.wawaziti );
        Button youyuan=(Button)findViewById( R.id.youyuan );
        Button inittypeface=(Button)findViewById( R.id.initTypeface );
        final TextView tftv=(TextView)findViewById( R.id.typefacetextView );
        final SharedPreferences.Editor editor = getSharedPreferences( "typeface", MODE_PRIVATE ).edit( );

        SharedPreferences typef=getSharedPreferences( "typeface",MODE_PRIVATE );
        String tftf=typef.getString( "typefacehaha","" );
        if(tftf.length()<=0){
            tftv.setTypeface( Typeface.SANS_SERIF );
        }else {
            Typeface typeface =Typeface.createFromAsset(getAssets(),tftf);
            tftv.setTypeface( typeface );
        }

        final Typeface huawenxingkaitf =Typeface.createFromAsset(getAssets(),"fonts/huawenxingkai.ttf");
        final Typeface katongjiantitf =Typeface.createFromAsset(getAssets(),"fonts/katongjianti.ttf");
        final Typeface wawazititf =Typeface.createFromAsset(getAssets(),"fonts/wawati.TTF");
        final Typeface youyuantf =Typeface.createFromAsset(getAssets(),"fonts/youyuan.ttf");

        huawenxingkai.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                editor.putString( "typefacehaha","fonts/huawenxingkai.ttf" );
                editor.apply();
                tftv.setTypeface( huawenxingkaitf );
            }
        } );

        katongjianti.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                editor.putString( "typefacehaha","fonts/katongjianti.ttf" );
                editor.apply();
                tftv.setTypeface( katongjiantitf );
            }
        } );
        wawaziti.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                editor.putString( "typefacehaha","fonts/wawati.TTF" );
                editor.apply();
                tftv.setTypeface( wawazititf );
            }
        } );
        youyuan.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                editor.putString( "typefacehaha","fonts/youyuan.ttf" );
                editor.apply();
                tftv.setTypeface( youyuantf );
            }
        } );
        inittypeface.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                editor.putString( "typefacehaha","" );
                editor.apply();
                tftv.setTypeface( Typeface.SANS_SERIF );
            }
        } );

    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

}
