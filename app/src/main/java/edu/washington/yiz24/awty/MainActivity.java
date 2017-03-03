package edu.washington.yiz24.awty;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private boolean startPressed; //determines whether alarm is currently on or off
    private AlarmManager am;
    private PendingIntent alarmIntent;
    private int interval; //milliseconds between messages
    private String message; //message user would like to send
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity", "onCreate fired");
        setContentView(R.layout.activity_main);
        startPressed = false;
        interval = -1;

        //auto-formats phone number for the user as they type
        EditText phone = (EditText) findViewById(R.id.phoneNumber);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    phoneNumber = phoneToDialable(phoneNumber);
                    SmsManager smsManager = SmsManager.getDefault();
                    Log.i("MainActivity", phoneNumber);
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Message could not send, please try again later!" + e.toString(),
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        };

        //registers BroadcastReceiver and adds filter so only alarm intent is received
        registerReceiver(alarmReceiver, new IntentFilter("SoundDaAlarm"));

        Intent i = new Intent();
        i.setAction("SoundDaAlarm"); //sets type of intent (alarm intent)
        alarmIntent = PendingIntent.getBroadcast(this, 0, i, 0);

        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startPressed) {
                    getUserInput();
                    if (message != null && phoneNumber != null && interval > 0) {
                        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() - interval,
                                interval, alarmIntent);
                        start.setText("Stop");
                        startPressed = !startPressed;
                    }
                } else {
                    start.setText("Start");
                    startPressed = !startPressed;
                    am.cancel(alarmIntent);
                }
            }
        });
    }

    public String phoneToDialable(String phone) {
        String dialablePhone = "";
        for (int i = 0; i < phone.length(); i++) {
            if (PhoneNumberUtils.is12Key(phone.charAt(i))) {
                dialablePhone += phone.charAt(i);
            }
        }
        return dialablePhone;
    }

    private void getUserInput() {
        EditText message = (EditText) findViewById(R.id.message);
        EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        EditText minutes = (EditText) findViewById(R.id.interval);

        this.message = message.getText().toString();
        this.phoneNumber = phoneNumber.getText().toString();
        try {
            this.interval = Integer.parseInt(minutes.getText().toString()) * 60000;
        } catch (NumberFormatException e) {
            this.interval = -1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "onDestroy Fired");
        am.cancel(alarmIntent);
        alarmIntent.cancel();
    }

}