package com.zf.android.dinnercallwilddog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by zifeifeng on 6/23/17.
 */
public class TimePickerFragment extends DialogFragment {
    private static final String TIME_ARGS = "time";
    public static final String EXTRA_TIME="com.example.android.criminalintent.time";
    private TimePicker mTimePicker;
    private static final String TAG = "TimePickerFragment";

    public static TimePickerFragment newInstance(Calendar date){
        Bundle bundle = new Bundle();
        bundle.putSerializable(TIME_ARGS, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        Calendar date = (Calendar) getArguments().getSerializable(TIME_ARGS);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.getTime());
        int hour = calendar.get(Calendar.HOUR);
        if(hour>12) hour-=12;
        else hour+=12;
        int minute = calendar.get(Calendar.MINUTE);
        mTimePicker =(TimePicker) v.findViewById(R.id.time_picker_fragment);
        if (Build.VERSION.SDK_INT >= 23) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        }

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Pick a time")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            int hour = mTimePicker.getHour();
                            if(hour>12) hour-=12;
                            else hour+=12;
                            int minute = mTimePicker.getMinute();
                            Log.e(TAG, "this is the hour: minute  "+hour +":"+minute);
                            Calendar time = Calendar.getInstance();
                            time.set(Calendar.HOUR, hour);
                            time.set(Calendar.MINUTE, minute);
                            sendResult(Activity.RESULT_OK, time);
                        }

                    }
                }).create();

    }

    private void sendResult(int resultCode, Calendar date){
        if(resultCode!=Activity.RESULT_OK)
            return;
        else{
            Intent intent = new Intent();
            intent.putExtra(EXTRA_TIME, date);
            getTargetFragment().onActivityResult(SettingsFragment.REQUEST_TIME_CODE, resultCode, intent);
        }
    }
}
