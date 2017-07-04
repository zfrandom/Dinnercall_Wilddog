package com.zf.android.dinnercallwilddog;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zifeifeng on 6/23/17.
 */

public class SettingsFragment extends Fragment {
    private Button mTimeBtn;
    private Button mRingtoneBtn;
    private static final String DIALOG_TIME = "dt";
    public static final int REQUEST_TIME_CODE = 4;
    private static final int CHOOSE_RINGTONE= 5;
    private static final String TAG="SettingsFragment";


    public static SettingsFragment newInstance(){

        return new SettingsFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.setting_activity, container, false);
        mTimeBtn = (Button) view.findViewById(R.id.btn_set_time);
        Calendar t = Calendar.getInstance();
        t.set(Calendar.HOUR, 13);
        t.set(Calendar.MINUTE, 0);
        //t.set(Calendar.SECOND, 0);
        mRingtoneBtn = (Button) view.findViewById(R.id.btn_change_ringtone);
        final Long tmpTimefinal = Shared.getLong(getActivity().getApplicationContext(),
                Shared.DEFINE_TIME,
                t.getTimeInMillis());
        Log.e(TAG, "The hour and minutes of t are " + t.get(Calendar.HOUR) + t.get(Calendar.MINUTE));
        t.setTimeInMillis(tmpTimefinal);
        updateTime(t);
        mTimeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar tmp = Calendar.getInstance();
                 Long tmpTime =  Shared.getLong(getActivity().getApplicationContext(),
                        Shared.DEFINE_TIME,
                        tmpTimefinal);
                tmp.setTimeInMillis(tmpTime);
                TimePickerFragment dialog = TimePickerFragment.newInstance(tmp);
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_TIME_CODE);
                dialog.show(getFragmentManager(), DIALOG_TIME);
            }
        });

        mRingtoneBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Choose your favorite ringtone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_RINGTONE));
                startActivityForResult(intent, CHOOSE_RINGTONE);
            }
        });
        return view;
    }

    private void updateTime(Calendar time){
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("hh:mm aaa");
        mTimeBtn.setText(String.format(getResources().getString(R.string.current_time), simpleDateFormat.format(time.getTime())));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == CHOOSE_RINGTONE) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if(uri!=null){
                if(Settings.System.canWrite(getContext())) {
                    RingtoneManager.setActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM, uri);
                    Ringtone r = RingtoneManager.getRingtone(getContext(), uri);
                    mRingtoneBtn.setText(String.format(getResources().getString(R.string.current_ringtone), r.getTitle(getContext())));
                }
            }
        }
        else if(requestCode == REQUEST_TIME_CODE && resultCode == RESULT_OK){
            Calendar time = (Calendar) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);

            Shared.putLong(getActivity().getApplicationContext(),
                            Shared.DEFINE_TIME,
                            time.getTimeInMillis());
            Log.e(TAG, "The hour and minutes are " + time.get(Calendar.HOUR) +":"+ time.get(Calendar.MINUTE));
            updateTime(time);
        }
        else
        super.onActivityResult(requestCode, resultCode, data);
    }

}
