package com.gongyunhaoyyy.password;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a99460.smartnote.R;

public class ColorSelectActivity extends AppCompatActivity {
    public Drawable orange_title;
    public Drawable blue_title;
    public Drawable purple_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_color_select );
        blue_title=getResources().getDrawable( R.drawable.nav_skyblue );
        orange_title=getResources().getDrawable( R.drawable.nav_orange );
        purple_title=getResources().getDrawable( R.drawable.nav_purple );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        final RelativeLayout color_tit=(RelativeLayout)findViewById( R.id.color_title );
        final ConstraintLayout cons=(ConstraintLayout)findViewById( R.id.color_lo );
        final Button orange=(Button)findViewById( R.id.orange_btn );
        final Button blue=(Button)findViewById( R.id.blue_btn );
        final Button purple=(Button)findViewById( R.id.purple_btn );
        final SharedPreferences.Editor color = getSharedPreferences( "themecolor", MODE_PRIVATE ).edit( );
        orange.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                color.putInt( "themecolorhaha", 0 );
                color.apply();
                color_tit.setBackground( orange_title );
                cons.setBackgroundColor( Color.parseColor( "#fef4f3" ) );
            }
        } );
        blue.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                color.putInt( "themecolorhaha", 1 );
                color.apply();
                color_tit.setBackground( blue_title );
                cons.setBackgroundColor( Color.parseColor( "#96f2f5f5" ) );
            }
        } );
        purple.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                color.putInt( "themecolorhaha", 2 );
                color.apply();
                color_tit.setBackground( purple_title );
                cons.setBackgroundColor( Color.parseColor( "#96ece2fb" ) );
            }
        } );

    }
}
