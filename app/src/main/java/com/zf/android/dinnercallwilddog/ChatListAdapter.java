package com.zf.android.dinnercallwilddog;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import  com.wilddog.client.Query;

/**
 * Created by zifeifeng on 7/3/17.
 */

public class ChatListAdapter extends WilddogListAdapter<ChatMessage> {
    public ChatListAdapter(Query ref, Activity activity, int layout){
        super(ref, ChatMessage.class, layout, activity);

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
}
