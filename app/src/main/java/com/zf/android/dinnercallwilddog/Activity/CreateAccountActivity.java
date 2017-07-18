package com.zf.android.dinnercallwilddog.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.request.UserProfileChangeRequest;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.wilddog.wilddogauth.model.WilddogUser;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;
import com.zf.android.dinnercallwilddog.R;
import com.zf.android.dinnercallwilddog.Service.NetworkReceiver;
import com.zf.android.dinnercallwilddog.Utils.Shared;

/**
 * Created by zifeifeng on 7/1/17.
 */

public class CreateAccountActivity   extends AppCompatActivity implements NetworkReceiver.MyNetworkCallback{

    private Button mCreateBtn;
    private EditText mEmailET;
    private EditText mFirstPwdET;
    private EditText mSecondPwdET;
    private EditText mNickName;
    private WilddogAuth mAuth;
    private TextView mNoNetwork;
    private NetworkReceiver mReceiver;
    private static String TAG = "CreateAccountActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        mCreateBtn = (Button) findViewById(R.id.register_btn);
        mEmailET = (EditText) findViewById(R.id.email_register);
        mFirstPwdET = (EditText) findViewById(R.id.first_pwd);
        mSecondPwdET = (EditText) findViewById(R.id.second_pwd);
        mNickName = (EditText) findViewById(R.id.nickName_et);
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://3245.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        mAuth = WilddogAuth.getInstance();
        mNoNetwork = (TextView) findViewById(R.id.login_noNetwork_tv);
        mReceiver = new NetworkReceiver(this);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(CreateAccountActivity.this);
                progressDialog.setMessage("Loading...please wait");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(true);


                final String email = mEmailET.getText().toString();
                final String password = mFirstPwdET.getText().toString();
                final String nickName = mNickName.getText().toString();
                String password1 = mSecondPwdET.getText().toString();
                if(!password.equals(password1)) {
                    Toast.makeText(CreateAccountActivity.this,"The password needs to be same",Toast.LENGTH_LONG ).show();
                    return;
                }
                if(email.isEmpty()||password.isEmpty()||nickName.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Fill complete all the blank", Toast.LENGTH_SHORT);
                    return;
                }
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete( Task<AuthResult> task) {
                                progressDialog.cancel();
                                if (task.isSuccessful()) {
                                    // 获取用户
                                    WilddogUser user = task.getResult().getWilddogUser();
                                    UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                                    builder.setDisplayName(nickName);
                                    user.updateProfile(builder.build());
                                    Intent i = new Intent();
                                    i.putExtra("Data", email);
                                    setResult(RESULT_OK, i);
                                    Shared.putBoolean(getApplicationContext(), Shared.IS_FIRST_TIME, true);
                                    finish();
                                    // Toast.makeText(CreateAccountActivity.this, "user is created successfully", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    // 错误处理
                                    Toast.makeText(CreateAccountActivity.this, task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();

                                }
                            }
                        });
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mReceiver.isNetworkAvailable()){
            hasNetWork();
        }
        else{
            loseNetWork();
        }
    }

    @Override
    public void hasNetWork() {
        mNoNetwork.setVisibility(View.GONE);
        mCreateBtn.setEnabled(true);
    }

    @Override
    public void loseNetWork() {
        mNoNetwork.setVisibility(View.VISIBLE);
        mCreateBtn.setEnabled(true);
    }
}
