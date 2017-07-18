package com.zf.android.dinnercallwilddog.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.RadioButton;

import com.zf.android.dinnercallwilddog.R;
import com.zf.android.dinnercallwilddog.Utils.Shared;

import java.util.zip.Inflater;

import static android.os.Build.VERSION_CODES.N;
import static com.zf.android.dinnercallwilddog.Fragment.ChooseBasicDayFragment.DAY_PICK_EXTRA;

/**
 * Created by zifeifeng on 7/16/17.
 */

public class ChooseDIYFragment extends DialogFragment {
    private CheckBox[] arr = new CheckBox[7];
    public static final String DIY_DAY_EXTRA="DIY REQUEST";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         final View v = LayoutInflater.from(getActivity()).inflate(R.layout.diy_day_fragment, null);
        initializeVariables(v);
        initializeCheckTextViews();
        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Pick a time")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        String res ="";
                        int store = 0;
                        for(int i = 0; i < arr.length; i++){
                            if(arr[i].isChecked()) {
                                res += arr[i].getText() + " ";
                                store+= Math.pow(10, 6-i);
                            }
                        }
                        Shared.putInt(getActivity().getApplicationContext(), Shared.WEEKDAY_STORAGE, store);
                       // Shared.putString(getActivity().getApplicationContext(), Shared.WEEKDAY_STRING_STORAGE, res);
                        if(store==0)
                            sendResult(Activity.RESULT_CANCELED, res);
                        sendResult(Activity.RESULT_OK, res);

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(CheckBox a : arr){
                            a.setChecked(false);
                            sendResult(Activity.RESULT_CANCELED, "");
                        }
                    }
                }).create();
    }

    private void initializeVariables(View v){
        arr[0] = (CheckBox) v.findViewById(R.id.tv_drugcycle_1);
        arr[1] = (CheckBox) v.findViewById(R.id.tv_drugcycle_2);
        arr[2] = (CheckBox) v.findViewById(R.id.tv_drugcycle_3);
        arr[3] = (CheckBox) v.findViewById(R.id.tv_drugcycle_4);
        arr[4] = (CheckBox) v.findViewById(R.id.tv_drugcycle_5);
        arr[5] = (CheckBox) v.findViewById(R.id.tv_drugcycle_6);
        arr[6] = (CheckBox) v.findViewById(R.id.tv_drugcycle_7);

    }

    private void initializeCheckTextViews(){
        int res = Shared.getInt(getActivity().getApplicationContext(), Shared.WEEKDAY_STORAGE, 0);
        if(res == 0||res == 1111111||res==1111100){
            return;
        }
        for(int i = 0; i < 7; i++){
            double tmp = res/Math.pow(10, 6-i);
            arr[i].setChecked(tmp>0);
            res %= Math.pow(10, 6-i);
        }
    }

    private void sendResult(int resultCode, String result){
        Intent intent = new Intent();
        intent.putExtra(DIY_DAY_EXTRA, result);
        getTargetFragment().onActivityResult(ChooseBasicDayFragment.DIY_PICK_REQUEST, resultCode, intent);
    }
}
