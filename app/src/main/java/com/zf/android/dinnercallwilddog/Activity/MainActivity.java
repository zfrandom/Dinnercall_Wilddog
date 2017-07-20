package com.zf.android.dinnercallwilddog.Activity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import android.support.design.widget.FloatingActionButton;

import com.wilddog.client.DataSnapshot;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.ValueEventListener;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogauth.WilddogAuth;

import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;
import com.zf.android.dinnercallwilddog.Adapter.ChatListAdapter;
import com.zf.android.dinnercallwilddog.Service.NetworkReceiver;
import com.zf.android.dinnercallwilddog.Utils.ChatMessage;
import com.zf.android.dinnercallwilddog.Utils.MyAlarmManager;
import com.zf.android.dinnercallwilddog.R;
import com.zf.android.dinnercallwilddog.Fragment.SettingsFragment;
import com.zf.android.dinnercallwilddog.Utils.Shared;


import static com.zf.android.dinnercallwilddog.Activity.RepeatActivity.THIS_IS_RA;

public class MainActivity extends AppCompatActivity implements NetworkReceiver.MyNetworkCallback{
    public static final int SIGN_IN_REQUEST_CODE = 123;
    private FloatingActionButton sendButton;
    private TextView mNoNetwork;

    private ListView mListOfMessage;
    private MyAlarmManager manager;

    private SyncReference mWilddogRef;
    private ChatListAdapter mChatListAdapter;
    private ValueEventListener mConnectedListener;
    private int SETTING_CODE = 124;


    private boolean isFirstTime;
    private WilddogAuth mAuth;
    private NetworkReceiver mReceiver;
    private ConnectivityManager connectivityManager;
    private CountDownTimer mCountDownTimer = new CountDownTimer(360*10000, 1000) {
        @Override
        public void onTick(long l) {
            Toast.makeText(getApplicationContext(), "MainActivity for idk and ignorance is counting down"+l/1000, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish() {
            //Toast.makeText(getApplicationContext(), "The alarm rings again", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(MainActivity.this, RepeatActivity.class);
            onNewIntent(i);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkNetWork()) {
            initializeVariables();
        }

    }

    private void initializeVariables(){
        mListOfMessage = (ListView) findViewById(R.id.list_of_messages);

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("wilddog database url").build();
        WilddogApp.initializeApp(this, options);

        mWilddogRef = WilddogSync.getInstance().getReference().child("chat");
        mAuth = WilddogAuth.getInstance();
        manager = MyAlarmManager.get(getApplicationContext());

        mReceiver=new NetworkReceiver(this);
        isFirstTime= Shared.getBoolean(getApplicationContext(), Shared.IS_FIRST_TIME);
        sendButton = (FloatingActionButton) findViewById(R.id.fab);
        mNoNetwork=(TextView)findViewById(R.id.no_network_tv);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        if (mAuth.getCurrentUser() == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(i, SIGN_IN_REQUEST_CODE);
        } else {
            Toast.makeText(this,
                    "Welcome " + mAuth
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
            if(isFirstTime) {
                setAlarm();
                Shared.putBoolean(getApplicationContext(), Shared.IS_FIRST_TIME, false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out){
            mAuth.signOut();
            mChatListAdapter.cleanup();
            mWilddogRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
            mCountDownTimer.cancel();
            finish();
        }
        else if(item.getItemId() == R.id.menu_settings){
            Intent setting = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(setting, SETTING_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == SIGN_IN_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                setAlarm();
            }
            else{
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }
        else if(requestCode == SETTING_CODE){
            if(resultCode == SettingsFragment.ALARM_CHANGE ||resultCode == SettingsFragment.DAY_CHANGE){
                if(manager!=null)
                    manager.cancel();
                setAlarm();
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
        boolean a = getIntent().getBooleanExtra(THIS_IS_RA, false);
        if(a&&Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18){
            mCountDownTimer.start();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.e("apple", "called onNewIntent");
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ListView listView = mListOfMessage;
        mChatListAdapter = new ChatListAdapter(mWilddogRef.limitToLast(12), this, R.layout.message );
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount()-1);
            }
        });

        mConnectedListener = mWilddogRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(SyncError syncError) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        mWilddogRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    private void sendMessage(){
        EditText input = (EditText) findViewById(R.id.input);
        String  content = input.getText().toString();
        if(!input.equals("")){
            ChatMessage chat = new ChatMessage(content, mAuth.getCurrentUser().getDisplayName() );
            mWilddogRef.push().setValue(chat);
            input.setText("");
        }
    }

    private void setAlarm(){
        Intent i = new Intent(this, RepeatActivity.class);
        manager.setIntent(i);
        manager.setAlarm();
    }
    private boolean checkNetWork(){
        return true;
    }

    public void hasNetWork(){
        mNoNetwork.setVisibility(View.GONE);

    }

    @Override
    public void loseNetWork() {
        mNoNetwork.setVisibility(View.VISIBLE);
    }


}

