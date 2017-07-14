package com.example.a99460.smartnote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.litepal.crud.DataSupport;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager manager;

    @Override
    public void onReceive(Context context,Intent intent){
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        // String id = intent.getStringExtra("id");
        int id = intent.getIntExtra("id",-1);
        Notedata notedata = DataSupport.find(Notedata.class,id);
        String word = notedata.getNote();
        Intent  playIntent = new Intent(context,MainActivity.class);
        //  playIntent.putExtra("id",id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentText(word).setSmallIcon(R.drawable.app_icon).setDefaults(Notification.DEFAULT_ALL).setContentIntent(pendingIntent);
        manager.notify(1,builder.build());
    }
}
