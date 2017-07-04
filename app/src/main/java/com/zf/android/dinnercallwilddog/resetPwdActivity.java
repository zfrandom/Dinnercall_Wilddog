package com.zf.android.dinnercallwilddog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

/**
 * Created by zifeifeng on 7/1/17.
 */

public class resetPwdActivity extends AppCompatActivity {
    private EditText email_tv;
    private Button send;
    private WilddogAuth mAuth;
    private Button cancel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_pwd_fragment);
        mAuth = WilddogAuth.getInstance();
        email_tv = (EditText) findViewById(R.id.resend_email);
        send = (Button) findViewById(R.id.resend_btn);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_tv.getText().toString();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if(task.isSuccessful()){
                            onActivityResult(LoginActivity.FORGETPASSWORD_CODE,RESULT_OK, null);
                            finish();
                        }
                        else{

                            Toast.makeText(getApplicationContext(), "The email can't be sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        cancel = (Button) findViewById(R.id.cancel_resend);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}