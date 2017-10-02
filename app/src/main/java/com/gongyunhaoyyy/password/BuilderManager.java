package com.gongyunhaoyyy.password;

import android.graphics.Color;

import com.nightonke.boommenu.BoomButtons.HamButton;
import com.example.a99460.smartnote.R;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;

/**
 * Created by acer on 2017/7/12.
 */

public class BuilderManager {

    private static int[] imageResources = new int[]{
            R.drawable.lock_icon,      //0
            R.drawable.typeface,       //1
            R.drawable.us,             //2
            R.drawable.take_photo,     //3
            R.drawable.insert_photo,   //4
            R.drawable.record          //5
    };

    private static int imageResourceIndex = 0;

    public static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

    public static int getImageResourcenote(int i) {
        if(i>5) i=3;
        return imageResources[i];
    }


    public static int getHamButtonBuildertext(int i) {
        if(i==0){
            return R.string.password;
        }else if (i==1){
            return R.string.typeface;
        }else {
            return R.string.abutus;
        }
    }

    public static int getHamButtonBuildersubtext(int i) {
        if(i==0){
            return R.string.password_sub;
        }else if (i==1){
            return R.string.typeface_sub;
        }else {
            return R.string.aboutus_sub;
        }
    }

    public static HamButton.Builder getHamButtonBuilder(int i) {
        if(i==0){
            return new HamButton.Builder()
                    .normalImageRes(getImageResource())
                    .normalTextRes(R.string.newnote)
                    .subNormalTextRes(R.string.newnote_sub)
                    .pieceColor( Color.WHITE);
        }else if (i==1){
            return new HamButton.Builder()
                    .normalImageRes(getImageResource())
                    .normalTextRes(R.string.password)
                    .subNormalTextRes(R.string.password_sub)
                    .pieceColor(Color.WHITE);
        }else if(i==2){
            return new HamButton.Builder()
                    .normalImageRes(getImageResource())
                    .normalTextRes(R.string.typeface)
                    .subNormalTextRes(R.string.typeface_sub)
                    .pieceColor(Color.WHITE);
        }else {
            return new HamButton.Builder()
                    .normalImageRes(getImageResource())
                    .normalTextRes(R.string.abutus)
                    .subNormalTextRes(R.string.aboutus_sub)
                    .pieceColor(Color.WHITE);
        }
    }

    private static BuilderManager ourInstance = new BuilderManager();

    public static BuilderManager getInstance() {
        return ourInstance;
    }

    private BuilderManager() {}

}
