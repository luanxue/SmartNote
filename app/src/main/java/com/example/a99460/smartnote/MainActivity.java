package com.example.a99460.smartnote;

import android.content.Intent;

import android.os.Bundle;

import android.content.SharedPreferences;
import android.provider.CalendarContract;

import android.provider.ContactsContract;


import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.gongyunhaoyyy.password.DeblockingActivity;
import com.gongyunhaoyyy.password.LockActivity;
import com.gongyunhaoyyy.password.LockToNoteActivity;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;


public class MainActivity extends AppCompatActivity {
    private ListView mLv;
    private List<Note> mDatas;
    private FloatingActionButton fab;
    private FloatingActionButton menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        final boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if(isFirstRun){
            LitePal.getDatabase();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLv = (ListView) findViewById(R.id.list);
        initdata();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, note_activity.class);
                startActivity(intent);
            }
        });
        menu = (FloatingActionButton) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this,menu);
                popup.getMenuInflater().inflate(R.menu.menu_pop, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.passwordsetting:
                                //设置新密码or修改密码
                                if (isDeadLock()){
                                    Toast.makeText( MainActivity.this,"密码功能锁定中...",Toast.LENGTH_SHORT ).show();
                                }else {
                                    Intent it=new Intent( MainActivity.this,LockActivity.class );
                                    startActivity( it );
                                }
                                break;
                            case R.id.themeselect:
                                Toast.makeText(MainActivity.this,"你点了大猪~", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        mLv.setAdapter(new CommonAdapter<Note>(this, mDatas, R.layout./*item_swipe_menu*/item_note) {
            @Override
            public void convert(final ViewHolder holder, final Note note, final int position) {
                //((CstSwipeDelMenu)holder.getConvertView()).setIos(false);//这句话关掉IOS阻塞式交互效果
                holder.setText(R.id.content, note.note);

                holder.setOnClickListener(R.id.content, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        long id=note.id;
                        Notedata notedata = DataSupport.find(Notedata.class, id);
                        boolean islock=notedata.isLock();
                        if (isDeadLock()){
                            Toast.makeText( MainActivity.this,"无法进入密码锁",Toast.LENGTH_SHORT ).show();
                        }else {
                            if (islock){
                                Intent lock=new Intent( MainActivity.this, LockToNoteActivity.class );
                                lock.putExtra( "in_data",note.id );
                                startActivity( lock);
                            }else {
                                Intent intent = new Intent(MainActivity.this, note_activity.class);
                                intent.putExtra("in_data",note.id);
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
                    }
                });

                holder.setOnClickListener(R.id.btnLock, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences pref=getSharedPreferences( "data",MODE_PRIVATE );
                        final String opassword=pref.getString( "oldpassword","" );
                        long id=note.id;
                        Notedata notedata = DataSupport.find(Notedata.class, id);
                        boolean islock=notedata.isLock();
                        //判断是否设置了密码
                        if (opassword==null||opassword.length()<=0){
                            Toast.makeText( MainActivity.this,"未设置密码,点左下角设置",Toast.LENGTH_SHORT ).show();
                        }else{
                            if (!islock){//上锁
                                notedata.setLock( true );
                                notedata.save();
                                Toast.makeText( MainActivity.this,"上锁成功",Toast.LENGTH_SHORT ).show();
                            }else {//解锁
                                if (isDeadLock()){
                                    Toast.makeText( MainActivity.this,"密码功能锁定中...",Toast.LENGTH_SHORT ).show();
                                }else {
                                    Intent intent=new Intent( MainActivity.this, DeblockingActivity.class );
                                    intent.putExtra("deblocking",note.id);
                                    startActivity( intent );
                                }
                            }
                        }
                    }
                });

            }
        });

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



    protected void onStart(){
        super.onStart();
        mLv = (ListView) findViewById(R.id.list);
        initdata();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, note_activity.class);
                startActivityForResult(intent, 1);
            }
        });
        mLv.setAdapter(new CommonAdapter<Note>(this, mDatas, R.layout./*item_swipe_menu*/item_note) {
            @Override
            public void convert(final ViewHolder holder, final Note note, final int position) {
                //((CstSwipeDelMenu)holder.getConvertView()).setIos(false);//这句话关掉IOS阻塞式交互效果
                holder.setText(R.id.content, note.note);
                holder.setOnClickListener(R.id.content, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        long id=note.id;
                        Notedata notedata = DataSupport.find(Notedata.class, id);
                        boolean islock=notedata.isLock();
                        if (isDeadLock()){
                            Toast.makeText( MainActivity.this,"无法进入密码锁",Toast.LENGTH_SHORT ).show();
                        }else {
                            if (islock){
                                Intent lock=new Intent( MainActivity.this, LockToNoteActivity.class );
                                lock.putExtra( "in_data",note.id );
                                startActivity(lock);
                            }else {
                                Intent intent = new Intent(MainActivity.this, note_activity.class);
                                intent.putExtra("in_data",note.id);
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
                    }
                });

                holder.setOnClickListener(R.id.btnLock, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences pref=getSharedPreferences( "data",MODE_PRIVATE );
                        final String opassword=pref.getString( "oldpassword","" );
                        long id=note.id;
                        Notedata notedata = DataSupport.find(Notedata.class, id);
                        boolean islock=notedata.isLock();
                        //判断是否设置了密码
                        if (opassword==null||opassword.length()<=0){
                            Toast.makeText( MainActivity.this,"未设置密码,点左下角设置",Toast.LENGTH_SHORT ).show();
                        }else{
                            if (!islock){//上锁
                                notedata.setLock( true );
                                notedata.save();
                                Toast.makeText( MainActivity.this,"上锁成功",Toast.LENGTH_SHORT ).show();
                            }else {//解锁
                                if (isDeadLock()){
                                    Toast.makeText( MainActivity.this,"密码功能锁定中...",Toast.LENGTH_SHORT ).show();
                                }else {
                                    Intent intent=new Intent( MainActivity.this, DeblockingActivity.class );
                                    intent.putExtra("deblocking",note.id);
                                    startActivity( intent );
                                }
                            }
                        }
                    }
                });

            }
        });

        //setContentView(R.layout.activity_main);
    }

    protected void initdata(){
        mDatas = new ArrayList<>();
        List<Notedata> notedatas = DataSupport.findAll(Notedata.class);
        for (Notedata notedata:notedatas){
            mDatas.add(new Note(notedata.getNote(),notedata.getId()));
        }
    }
}