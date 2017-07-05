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
                startActivityForResult(intent, 1);
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
                        Intent intent = new Intent(MainActivity.this, note_activity.class);
                        intent.putExtra("in_data", note.note);
                        DataSupport.deleteAll(Notedata.class,"note==?",note.note);
                        startActivityForResult(intent, 1);
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

@Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
    switch (requestCode){
        case 1:
            if(resultCode==RESULT_OK)
            {
                String word = data.getStringExtra("data_return");
                if(word!=null&&Issave(word)){
                Notedata notedata = new Notedata();
                notedata.setNote(word);
                notedata.save();}
            }
            break;
        default:
    }
}
protected boolean Issave(String word){
    int length = word.length();
    int i,flag=0;
    for (i=0;i<length;i++){
        if(word.charAt(i)!=' '&&word.charAt(i)!='\n'){
            flag=1;
        }
    }
    if (flag==1){
        return true;
    }
    return false;
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
                    Intent intent = new Intent(MainActivity.this, note_activity.class);
                    intent.putExtra("in_data", note.note);
                    DataSupport.deleteAll(Notedata.class,"note==?",note.note);
                    startActivityForResult(intent, 1);
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
        mDatas.add(new Note(notedata.getNote()));
    }
}
}
