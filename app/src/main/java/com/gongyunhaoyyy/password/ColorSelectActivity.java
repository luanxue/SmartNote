package com.gongyunhaoyyy.password;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a99460.smartnote.R;

public class ColorSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_color_select );

        final Button orange=(Button)findViewById( R.id.orange_btn );
        final Button blue=(Button)findViewById( R.id.blue_btn );
        final SharedPreferences.Editor color = getSharedPreferences( "themecolor", MODE_PRIVATE ).edit( );
        orange.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                color.putInt( "themecolorhaha", 0 );
                color.apply();
            }
        } );
        blue.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                color.putInt( "themecolorhaha", 1 );
                color.apply();
            }
        } );

    }
}
