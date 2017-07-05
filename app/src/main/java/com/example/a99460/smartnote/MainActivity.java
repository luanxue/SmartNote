package com.example.a99460.smartnote;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ListView mLv;
    private List<Note> mDatas;
    private FloatingActionButton fab;
    private FloatingActionButton menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                                Toast.makeText(MainActivity.this,"你点了小猪~",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.themeselect:
                                Toast.makeText(MainActivity.this,"你点了大猪~",
                                        Toast.LENGTH_SHORT).show();
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
                        //你有两个intent要进行修改，这是第一个
                        Intent intent = new Intent(MainActivity.this, note_activity.class);
                       // intent.putExtra("in_data", note.id);
                        DataSupport.deleteAll(Notedata.class,"note==?",note.note);
                        startActivity(intent);
                    }
                });

                holder.setOnClickListener(R.id.btnDelete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //在ListView里，点击侧滑菜单上的选项时，如果想让擦花菜单同时关闭，调用这句话
                        ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                        mDatas.remove(position);
                        notifyDataSetChanged();
                        DataSupport.deleteAll(Notedata.class,"note==?",note.note);
                    }
                });
            }
        });

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
            startActivity(intent);
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
                    //你有两个intent要进行修改，这是第一个
                    Intent intent = new Intent(MainActivity.this, note_activity.class);

                    //intent.putExtra("in_data", note.id);
                    DataSupport.deleteAll(Notedata.class,"note==?",note.note);
                    startActivity(intent);
                }
            });

            holder.setOnClickListener(R.id.btnDelete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //在ListView里，点击侧滑菜单上的选项时，如果想让擦花菜单同时关闭，调用这句话
                    ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                    mDatas.remove(position);
                    notifyDataSetChanged();
                    DataSupport.deleteAll(Notedata.class,"note==?",note.note);
                }
            });
        }
    });

    //setContentView(R.layout.activity_main);
}
protected void onRestart(){
        super.onRestart();
        initdata();
        //setContentView(R.layout.activity_main);
    }
    protected void onResume(){
        super.onResume();
        initdata();
        // setContentView(R.layout.activity_main);
    }

protected void initdata(){
    mDatas = new ArrayList<>();
    List<Notedata> notedatas = DataSupport.findAll(Notedata.class);
    for (Notedata notedata:notedatas){
        mDatas.add(new Note(notedata.getNote(),notedata.getId()));
    }
}
}
