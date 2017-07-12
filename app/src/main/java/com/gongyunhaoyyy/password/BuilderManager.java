package com.gongyunhaoyyy.password;

import android.graphics.Color;

import com.nightonke.boommenu.BoomButtons.HamButton;
import com.example.a99460.smartnote.R;
/**
 * Created by acer on 2017/7/12.
 */

public class BuilderManager {

    private static int[] imageResources = new int[]{
            R.drawable.bat,
            R.drawable.bear,
            R.drawable.bee,
            R.drawable.butterfly,
            R.drawable.cat,
            R.drawable.deer,
            R.drawable.dolphin,
            R.drawable.eagle,
            R.drawable.horse,
            R.drawable.elephant,
            R.drawable.owl,
            R.drawable.peacock,
            R.drawable.rat,
            R.drawable.squirrel
    };

    private static int imageResourceIndex = 0;

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
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
