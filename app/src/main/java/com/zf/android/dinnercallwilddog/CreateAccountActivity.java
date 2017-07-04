package com.zf.android.dinnercallwilddog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.request.UserProfileChangeRequest;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.wilddog.wilddogauth.model.WilddogUser;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

/**
 * Created by zifeifeng on 7/1/17.
 */

public class CreateAccountActivity   extends AppCompatActivity {

    private Button mCreateBtn;
    private EditText mEmailET;
    private EditText mFirstPwdET;
    private EditText mSecondPwdET;
    private EditText mNickName;
    private CheckBox mRmbUserCB;
    private WilddogAuth mAuth;
    private static String TAG = "CreateAccountActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        mCreateBtn = (Button) findViewById(R.id.register_btn);
        mEmailET = (EditText) findViewById(R.id.email_register);
        mFirstPwdET = (EditText) findViewById(R.id.first_pwd);
        mSecondPwdET = (EditText) findViewById(R.id.second_pwd);
        mRmbUserCB = (CheckBox) findViewById(R.id.rmb_user);
        mNickName = (EditText) findViewById(R.id.nickName_et);
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://3245.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        mAuth = WilddogAuth.getInstance();

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmailET.getText().toString();
                final String password = mFirstPwdET.getText().toString();
                String password1 = mSecondPwdET.getText().toString();
                if(!password.equals(password1)) {
                    Toast.makeText(CreateAccountActivity.this,"The password needs to be same",Toast.LENGTH_LONG ).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete( Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 获取用户
                                    if(mRmbUserCB.isChecked()){
                                        Shared.putBoolean(getApplicationContext(), Shared.DEFINE_IS_REMEMBER, true);
                                        Shared.putString(getApplicationContext(), Shared.DEFINE_EMAIL, email);
                                        Shared.putString(getApplicationContext(), Shared.DEFINE_PASSWORD, password);
                                    }
                                    WilddogUser user = task.getResult().getWilddogUser();
                                    UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                                    builder.setDisplayName(mNickName.getText().toString());
                                    user.updateProfile(builder.build());
                                    Log.d("result: ",user.toString());
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

        mRmbUserCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    Shared.putBoolean(getApplicationContext(), Shared.DEFINE_IS_REMEMBER, false);
                }
            }
        });
    }
}
