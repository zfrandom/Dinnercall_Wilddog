package com.zf.android.dinnercallwilddog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zifeifeng on 6/14/17.
 */

public class NotificationService extends BroadcastReceiver {
    public static final int REPEAT_ACTIVITY_REQUESTCODE = 123;
    @Override
    public void onReceive(Context context, Intent intent)
    {

        Intent service1 = new Intent(context, RingtonePlayService.class);
        service1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startService(service1);
    }




}
