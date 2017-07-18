package com.zf.android.dinnercallwilddog.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wilddog.client.ChildEventListener;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.Query;
import com.wilddog.client.SyncError;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.N;

/**
 * Created by zifeifeng on 7/3/17.
 */

public abstract class WilddogListAdapter<T> extends BaseAdapter {
    private Query mRef;
    private Class<T> mModelClass;
    private int mLayout;
    private LayoutInflater mInflater;
    private List<T> mModels;
    private List<String> mKeys;
    private ChildEventListener mListener;

    public WilddogListAdapter(Query mRef, final Class<T> mModelClass, int mLayout, Activity activity){
        this.mRef = mRef;
        this.mModelClass = mModelClass;
        this.mLayout = mLayout;
        mInflater = activity.getLayoutInflater();
        mModels = new ArrayList<T>();
        mKeys = new ArrayList<String>();
        mListener = this.mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                T model = (T) dataSnapshot.getValue(WilddogListAdapter.this.mModelClass);
                String key = dataSnapshot.getKey();
                if(previousChildName==null){
                    mModels.add(0, model);
                    mKeys.add(0, key);
                }
                else{
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex+1;
                    if(nextIndex == mModels.size()){
                        mModels.add(model);
                        mKeys.add(key);

                    }
                    else{
                        mModels.add(nextIndex, model);
                        mKeys.add(nextIndex, key);
                    }
                }
                sendNotificationToUser(model);
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                T newModel = (T) dataSnapshot.getValue(WilddogListAdapter.this.mModelClass);
                int index = mKeys.indexOf(key);
                mModels.set(index, newModel);
                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(SyncError syncError) {

            }
        });
    }
    public void cleanup(){
        mRef.removeEventListener(mListener);
        mModels.clear();
        mKeys.clear();
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public Object getItem(int i) {
        return mModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = mInflater.inflate(mLayout, viewGroup,false);
        }
        T model = mModels.get(i);
        populateView(view, model);
        return view;
    }

    protected abstract void populateView(View v, T model);
    protected abstract void sendNotificationToUser(T model);
}
