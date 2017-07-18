package com.zf.android.dinnercallwilddog.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.zf.android.dinnercallwilddog.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zifeifeng on 7/1/17.
 */

public class resetPwdFragment extends DialogFragment {
    private EditText email_tv;
    private Button send;
    private WilddogAuth mAuth;
    private Button cancel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.reset_pwd_fragment, container);
        email_tv = (EditText) v.findViewById(R.id.resend_email);
        send = (Button) v.findViewById(R.id.resend_btn);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_tv.getText().toString();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if(task.isSuccessful()){
                            onActivityResult(LoginActivity.FORGETPASSWORD_CODE,RESULT_OK, null);

                        }
                        else{

                            Toast.makeText(getActivity().getApplicationContext(), "The email can't be sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        cancel = (Button) v.findViewById(R.id.cancel_resend);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = WilddogAuth.getInstance();

    }
}