package com.zf.android.dinnercallwilddog.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zf.android.dinnercallwilddog.Activity.RepeatActivity;

import java.io.Serializable;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by zifeifeng on 7/11/17.
 */

public class MyAlarmManager  implements Serializable, RepeatActivity.setTmrAlarmCallBack {
    private PendingIntent pendingIntentToActivity;
    private Context mContext;
    private AlarmManager manager;
    private static MyAlarmManager mMyAlarmManager;


    public enum Weekday {Everyday, Mon2Fri, Mon, Tue, Wed, Thur, Fri, Sat, Sun};

    public static MyAlarmManager get(Context mContext){
        if(mMyAlarmManager==null)
            mMyAlarmManager = new MyAlarmManager(mContext);

        return mMyAlarmManager;
    }
    private MyAlarmManager (Context mContext){

        manager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        this.mContext = mContext;
    }


    public void setIntent(Intent intent){
        pendingIntentToActivity = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void setAlarm(){
        Calendar calendar = Calendar.getInstance();
        int hour = Shared.getInt(mContext, Shared.DEFINE_HOUR,12);
        int minute = Shared.getInt(mContext, Shared.DEFINE_MINUTE,00);
        if(!shouldring()){
            setTmr();
            return;
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        if(calendar.getTimeInMillis() > System.currentTimeMillis())
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentToActivity);
        else{
            calendar.setTimeInMillis( AlarmManager.INTERVAL_DAY+calendar.getTimeInMillis());
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentToActivity);
        }

    }

    @Override
    public void setTmr() {

        Calendar calendar = Calendar.getInstance();
        int hour = Shared.getInt(mContext, Shared.DEFINE_HOUR,12);
        int minute = Shared.getInt(mContext, Shared.DEFINE_MINUTE,00);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        int next = getNextDayToRing();
        calendar.setTimeInMillis( next*AlarmManager.INTERVAL_DAY+calendar.getTimeInMillis());
        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentToActivity);

    }

    private int getNextDayToRing(){
        String res = String.valueOf(Shared.getInt(mContext.getApplicationContext(), Shared.WEEKDAY_STORAGE, 1111100));
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int from = today;
        if(today==7){
            from = 0;
        }
        int index =res.indexOf("1", from);
        if(index==-1){
            index = res.indexOf("1", 0);
        }
        return (index-today)%7;
    }
    private boolean shouldring(){
        String res = String.valueOf(Shared.getInt(mContext.getApplicationContext(), Shared.WEEKDAY_STORAGE, 1111100));
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int from = today-1;
        return res.charAt(from)=='1';
    }
    public void cancel(){
        if(pendingIntentToActivity!=null)
            manager.cancel(pendingIntentToActivity);
    }
}
