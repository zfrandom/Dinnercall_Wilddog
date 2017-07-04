package com.zf.android.dinnercallwilddog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

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


import static com.zf.android.dinnercallwilddog.RepeatActivity.THIS_IS_RA;

public class MainActivity extends AppCompatActivity {
    public static final int SIGN_IN_REQUEST_CODE = 123;
    private FloatingActionButton sendButton;

    private ListView mListOfMessage;
    private AlarmManager alarmManager;
    PendingIntent pendingIntentToActivity;

    private SyncReference mWilddogRef;
    private ChatListAdapter mChatListAdapter;
    private ValueEventListener mConnectedListener;

    private WilddogAuth mAuth;

    private CountDownTimer mCountDownTimer = new CountDownTimer(10000, 2000) {
        @Override
        public void onTick(long l) {
            Toast.makeText(getApplicationContext(), "MainActivity for idk and ignorance is counting down"+l/1000, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish() {
            Toast.makeText(getApplicationContext(), "The alarm rings again", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(MainActivity.this, RepeatActivity.class);
            onNewIntent(i);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListOfMessage = (ListView) findViewById(R.id.list_of_messages);

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://3245.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);

        mWilddogRef = WilddogSync.getInstance().getReference().child("chat");
        mAuth = WilddogAuth.getInstance();

        sendButton = (FloatingActionButton) findViewById(R.id.fab);
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
            setAlarm();
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
            Intent setting = new Intent(MainActivity.this, SettingsClass.class);
            startActivity(setting);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    }



    private void setAlarm(){
        Log.i("MainActivity", "hi");
        Intent intentToActivity = new Intent(MainActivity.this , RepeatActivity.class);
        pendingIntentToActivity = PendingIntent.getActivity(getApplicationContext(),100 ,intentToActivity,PendingIntent.FLAG_UPDATE_CURRENT);
//        pendingIntentToPlayingTone = PendingIntent.getBroadcast(getApplicationContext(), 101, intentToPlayRingTone, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 3);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 18);
        calendar.set(Calendar.SECOND, 00);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntentToActivity);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                Shared.getLong(getApplicationContext(), Shared.DEFINE_TIME, 0L),AlarmManager.INTERVAL_DAY, pendingIntentToActivity);
        Log.i("MainActivity", "finish setting repeat");
    }



    @Override
    protected void onResume() {
        super.onResume();

        Log.e("apple", "in onResume");
        if(getIntent().getBooleanExtra(THIS_IS_RA, false)){
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
                boolean connected=(Boolean) dataSnapshot.getValue();
                if(connected){
                    Toast.makeText(MainActivity.this, "Connected to Wilddog", Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(MainActivity.this, "Disconnected from Wilddog", Toast.LENGTH_LONG).show();

                }
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
}

