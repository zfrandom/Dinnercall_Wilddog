package com.zf.android.dinnercallwilddog.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.SupportActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zf.android.dinnercallwilddog.R;
import com.zf.android.dinnercallwilddog.Utils.Shared;

import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.fasterxml.jackson.core.JsonParser.NumberType.INT;
import static com.zf.android.dinnercallwilddog.Fragment.SettingsFragment.DAY_CHANGE;

/**
 * Created by zifeifeng on 7/14/17.
 */

public class ChooseBasicDayFragment extends DialogFragment {
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    public static final String DAY_PICK_EXTRA = "extra day";
    public static final int DIY_PICK_REQUEST= 225;
    private static final int CODE_FOR_TAR_REAG= 226;


    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.basic_day_fragment, null);
        mRadioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        mRadioButton = (RadioButton) v.findViewById(R.id.diy_rb);

        initBtn(v);
        mRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseDIYFragment dialog = new ChooseDIYFragment();
                dialog.setTargetFragment(ChooseBasicDayFragment.this,CODE_FOR_TAR_REAG );
                dialog.show(getFragmentManager(),"a");
            }
        });

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Pick a time")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            RadioButton rb = (RadioButton) v.findViewById(mRadioGroup.getCheckedRadioButtonId());
                            if(rb.getId()==R.id.everyday_rb){
                                Shared.putInt(getActivity().getApplicationContext(), Shared.WEEKDAY_STORAGE, 1111111);
                            }
                            else if(rb.getId() == R.id.mon_fri_rb){
                                Shared.putInt(getActivity().getApplicationContext(), Shared.WEEKDAY_STORAGE, 1111100);
                            }
                            Shared.putString(getActivity().getApplicationContext(), Shared.WEEKDAY_STRING_STORAGE, rb.getText().toString());
                            sendResult(Activity.RESULT_OK, rb.getText().toString());
                    }
                }).create();


    }

    private void initBtn(View v){
        int res = Shared.getInt(getActivity().getApplicationContext(), Shared.WEEKDAY_STORAGE, 1111100);
        if(res==1111100) {
            mRadioGroup.check(R.id.mon_fri_rb);
        }
        else if(res == 1111111){
            mRadioGroup.check(R.id.everyday_rb);
        }
        else{
            mRadioGroup.check(R.id.diy_rb);
            ((RadioButton)v.findViewById(R.id.diy_rb)).setText(getString(R.string.diy_day) + Shared.getString(getActivity().getApplicationContext(),Shared.WEEKDAY_STRING_STORAGE));
        }
    }
    private void initBtn(){
        int res = Shared.getInt(getActivity().getApplicationContext(), Shared.WEEKDAY_STORAGE, 1111100);
        if(res==1111100) {
            mRadioGroup.check(R.id.mon_fri_rb);
        }
        else if(res == 1111111){
            mRadioGroup.check(R.id.everyday_rb);
        }
        else{
            mRadioGroup.check(R.id.diy_rb);
        }
    }
    private void sendResult(int resultCode, String result) {
        Intent intent = new Intent();
        intent.putExtra(DAY_PICK_EXTRA, result);
        getTargetFragment().onActivityResult(SettingsFragment.REQUEST_DAY_CODE, resultCode, intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode ==DIY_PICK_REQUEST ){
            if(resultCode==RESULT_OK){
                mRadioButton.setChecked(true);
                String res = data.getStringExtra(ChooseDIYFragment.DIY_DAY_EXTRA);
                String ori = getString(R.string.diy_day);
                mRadioButton.setText(ori + "  " + res);

            }
            else if(resultCode == RESULT_CANCELED){
                initBtn();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
