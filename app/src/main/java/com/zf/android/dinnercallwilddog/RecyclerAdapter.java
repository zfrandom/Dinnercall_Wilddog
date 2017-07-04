package com.zf.android.dinnercallwilddog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zifeifeng on 7/1/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TextHolder> {
    private ArrayList<ChatMessage> mChatMessages;
    private Context mContext;
    public RecyclerAdapter(Context context){
        mContext = context;
    }
    public RecyclerAdapter(ArrayList<ChatMessage> list){
        mChatMessages = list;
    }
    @Override
    public TextHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            TextHolder textHolder = new TextHolder(layoutInflater, parent);
            return textHolder;

    }

    @Override
    public void onBindViewHolder(TextHolder holder, int position) {
        ChatMessage chatMessage = mChatMessages.get(position);
        holder.bind(chatMessage);
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }



    public static class TextHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //2
        private TextView mItemUser;
        private TextView mItemDate;
        private TextView mItemDescription;
        private ChatMessage mMessage;
        //3
        private static final String PHOTO_KEY = "PHOTO";
        public TextHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.message, parent, false));
            mItemDate = (TextView) itemView.findViewById(R.id.message_time);
            mItemUser = (TextView) itemView.findViewById(R.id.message_user);
            itemView.setOnClickListener(this);
            mItemUser = (TextView) itemView.findViewById(R.id.message_text);
        }


        public void bind(ChatMessage crime) {
            mMessage = crime;
            mItemUser.setText(mMessage.getMessageUser());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mMessage.getMessageTime());
            Date date = calendar.getTime();
            mItemDate.setText(simpleDateFormat.format(date));
            mItemDescription.setText(mMessage.getMessageText());
        }

        @Override
        public void onClick(View v) {
            Log.d("RecyclerView", "CLICK!");
        }
    }

}
