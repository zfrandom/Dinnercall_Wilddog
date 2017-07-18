package com.zf.android.dinnercallwilddog.Adapter;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import  com.wilddog.client.Query;
import com.wilddog.wilddogauth.WilddogAuth;
import com.zf.android.dinnercallwilddog.Utils.ChatMessage;
import com.zf.android.dinnercallwilddog.R;

/**
 * Created by zifeifeng on 7/3/17.
 */

public class ChatListAdapter extends WilddogListAdapter<ChatMessage> {
    private WilddogAuth mAuth;
    private Activity mActivity;
    private final long[] VIBRATION = {400L};
    public ChatListAdapter(Query ref, Activity activity, int layout){
        super(ref, ChatMessage.class, layout, activity);
        mAuth = WilddogAuth.getInstance();
        mActivity = activity;
    }

    @Override
    protected void populateView(View v, ChatMessage model) {
            TextView name = (TextView) v.findViewById(R.id.message_user);
            TextView time = (TextView) v.findViewById(R.id.message_time);
            TextView content = (TextView) v.findViewById(R.id.message_text);
            content.setText(model.getMessageText());
            name.setText(model.getMessageUser());
            time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                    model.getMessageTime()));

    }

    @Override
    protected void sendNotificationToUser(ChatMessage model) {
        if(mAuth.getCurrentUser()==null) return;
        if(!model.getMessageUser().equals( mAuth.getCurrentUser().getDisplayName())){
            String result = model.getMessageText().equals("YES")? " is coming home tonight": "is having dinner outside tonight";
            NotificationManager mNm =(NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(mActivity)
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle("New Message From Dinner Call!")
                    .setContentText(model.getMessageUser() + result)
                    .setAutoCancel(true)
                    .setVibrate(VIBRATION)
                    .build();
            mNm.notify(0, notification);

        }
    }
}
