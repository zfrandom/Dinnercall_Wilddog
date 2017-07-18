package com.zf.android.dinnercallwilddog.Fragment;


import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zf.android.dinnercallwilddog.R;
import com.zf.android.dinnercallwilddog.Utils.Shared;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zifeifeng on 6/23/17.
 */

public class SettingsFragment extends Fragment {
    private Button mTimeBtn;
    private Button mRingtoneBtn;
    private Button mDayBtn;
    private static final String DIALOG_TIME = "dt";
    private static final String DIALOG_DATE = "date";
    public static final int REQUEST_TIME_CODE = 4;
    public static final int REQUEST_DAY_CODE= 5;
    //private static final int CHOOSE_RINGTONE= 5;
    private static final String TAG="SettingsFragment";
    public static final int ALARM_CHANGE = 234;
    public static final int DAY_CHANGE=235;
    private static Button mFinishBtn;

    public static SettingsFragment newInstance(){

        return new SettingsFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.setting_activity, container, false);
        mTimeBtn = (Button) view.findViewById(R.id.btn_set_time);
        mFinishBtn = (Button) view.findViewById(R.id.finish_Setting_btn);
        mRingtoneBtn = (Button) view.findViewById(R.id.btn_change_ringtone);
        mDayBtn = (Button) view.findViewById(R.id.btn_set_day);
        String chooseDay = Shared.getString(getActivity().getApplicationContext(), Shared.WEEKDAY_STRING_STORAGE);
        chooseDay = chooseDay==null? getString(R.string.from_mon_to_fri):chooseDay;
        mDayBtn.setText(getString(R.string.choose_weekday) +"  "+ chooseDay);

        final int hour = Shared.getInt(getActivity().getApplicationContext(), Shared.DEFINE_HOUR,12);
        final int min = Shared.getInt(getActivity().getApplicationContext(), Shared.DEFINE_MINUTE, 12);
        updateTime(hour, min);
        mTimeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar tmp = Calendar.getInstance();
                tmp.set(Calendar.HOUR_OF_DAY,hour );
                tmp.set(Calendar.MINUTE, min);
                TimePickerFragment dialog = TimePickerFragment.newInstance(tmp);
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_TIME_CODE);
                dialog.show(getFragmentManager(), DIALOG_TIME);
            }
        });

        mDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseBasicDayFragment dialog = new ChooseBasicDayFragment();
                dialog.setTargetFragment(SettingsFragment.this, DAY_CHANGE);
                dialog.show(getFragmentManager(), DIALOG_DATE);
            }
        });

//        mRingtoneBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Choose your favorite ringtone");
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_RINGTONE));
//                startActivityForResult(intent, CHOOSE_RINGTONE);
//            }
//        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        return view;
    }



    private void updateTime(int hour, int min){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        mTimeBtn.setText("The current start time is " + simpleDateFormat.format(c.getTime()));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         if(requestCode == REQUEST_TIME_CODE && resultCode == RESULT_OK){
            Calendar time = (Calendar) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            int h = time.get(Calendar.HOUR_OF_DAY);
            int m = time.get(Calendar.MINUTE);
            updateTime(h, m);
            Shared.putInt(getActivity().getApplicationContext(),
                    Shared.DEFINE_HOUR,h
                    );
            Shared.putInt(getActivity().getApplicationContext(),
                    Shared.DEFINE_MINUTE,m
            );
            Intent t = new Intent();
            t.putExtra("Hour", h);
            t.putExtra("Min", m);
            getActivity().setResult(ALARM_CHANGE, t);
        }
        else if(requestCode ==REQUEST_DAY_CODE &&resultCode==RESULT_OK){
            String meg = getString(R.string.choose_weekday) +Shared.getString(getActivity().getApplicationContext(), Shared.WEEKDAY_STRING_STORAGE);
            mDayBtn.setText(meg);
             getActivity().setResult(DAY_CHANGE);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
