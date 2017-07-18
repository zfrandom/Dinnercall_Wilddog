package com.zf.android.dinnercallwilddog.Activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.zf.android.dinnercallwilddog.Fragment.SettingsFragment;
import com.zf.android.dinnercallwilddog.Fragment.TimePickerFragment;
import com.zf.android.dinnercallwilddog.R;
import com.zf.android.dinnercallwilddog.Service.NetworkReceiver;
import com.zf.android.dinnercallwilddog.Utils.Shared;

import static com.zf.android.dinnercallwilddog.Fragment.SettingsFragment.REQUEST_TIME_CODE;

/**
 * Created by zifeifeng on 6/29/17.
 */

public class LoginActivity extends AppCompatActivity implements NetworkReceiver.MyNetworkCallback {
    private WilddogAuth mAuth;
    private EditText mPassword;
    private Button mRegister;
    private Button mSigninBtn;
    private CheckBox mRmCheckBox;
    private AutoCompleteTextView mEmailEditText;
    private static String TAG = "LoginActivity";
    private Button mForgetPwdBtn;
    public static int  REGISTER =333;
    public static int FORGETPASSWORD_CODE=123;
    private TextView mNoNetwork;
    private NetworkReceiver mReceiver;
    private static final String RESET_PASSWORD="reset password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mAuth = WilddogAuth.getInstance();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...please wait");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        mReceiver = new NetworkReceiver(this);
        mForgetPwdBtn = (Button) findViewById(R.id.forget_pwd_btn);
        mPassword = (EditText) findViewById(R.id.password);
        mEmailEditText = (AutoCompleteTextView) findViewById(R.id.email);
        mRmCheckBox=(CheckBox) findViewById(R.id.rmb_user);
        mNoNetwork = (TextView) findViewById(R.id.login_noNetwork_tv);
        mSigninBtn = (Button) findViewById(R.id.login_continue_btn);
        mSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String  email = mEmailEditText.getText().toString();
                final String password = mPassword.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(),"please enter email", Toast.LENGTH_SHORT);
                    return;
                }
                if(password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"please enter password", Toast.LENGTH_SHORT);
                    return;
                }
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete( Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                progressDialog.cancel();
                                if (!task.isSuccessful()) {

                                    Log.w(TAG, "signInWithEmail", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    setResult(RESULT_OK, null);
                                    finish();
                                    if(mRmCheckBox.isChecked()){
                                        Shared.putBoolean(getApplicationContext(), Shared.DEFINE_IS_REMEMBER, true);
                                        Shared.putString(getApplicationContext(), Shared.DEFINE_EMAIL, email);
                                        Shared.putString(getApplicationContext(), Shared.DEFINE_PASSWORD, password);
                                    }
                                }
                            }
                        });
            }
        });
        mRegister = (Button) findViewById(R.id.email_register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivityForResult(intent, REGISTER);
            }
        });
        mRmCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    Shared.putBoolean(getApplicationContext(), Shared.DEFINE_IS_REMEMBER, false);
                }
            }
        });
        if(Shared.getBoolean(getApplicationContext(), Shared.DEFINE_IS_REMEMBER)){
            mEmailEditText.setText(Shared.getString(getApplicationContext(), Shared.DEFINE_EMAIL));
            mPassword.setText(Shared.getString(getApplicationContext(), Shared.DEFINE_PASSWORD));
            mRmCheckBox.setChecked(true);
        }
        mForgetPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                resetPwdFragment dialog = new resetPwdFragment();
                dialog.show(fm, RESET_PASSWORD);
            }
        });
        mForgetPwdBtn.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FORGETPASSWORD_CODE){
            if(requestCode == RESULT_OK){
                Toast.makeText(LoginActivity.this, "The password reset email is sent", Toast.LENGTH_LONG).show();

            }
        }
        else if(requestCode == REGISTER){
            if(resultCode == RESULT_OK){
                String email = data.getStringExtra("Data");
                mEmailEditText.setText(email);
                mPassword.setText("");
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        mSigninBtn.setEnabled(true);
    }

    @Override
    public void loseNetWork() {
        mNoNetwork.setVisibility(View.VISIBLE);
        mSigninBtn.setEnabled(false);
    }
}





