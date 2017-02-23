package edu.washington.yiz24.awty;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private boolean started;

    PendingIntent alarmIntent = null;
    BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String number = intent.getStringExtra("number");
            Toast.makeText(MainActivity.this, number + ": " + message, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button start = (Button) findViewById(R.id.start);
        final EditText message = (EditText) findViewById(R.id.message);
        final EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        final EditText interval = (EditText) findViewById(R.id.interval);

        final Activity currentActivity = this;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // makes sure each value is filled in the edit texts
                boolean validNumber = interval.getText().toString().matches("^[1-9]+[0-9]*$");
                boolean messageValue = !message.getText().toString().equals("");
                boolean numberValue = !phoneNumber.getText().toString().equals("");
                Log.i("onClick()", message.getText().toString() + ": " + phoneNumber.getText().toString());

                registerReceiver(alarmReceiver, new IntentFilter("receiveFeedBack"));
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if (started) {
                    started = false;
                    start.setText("Start");
                    start.setBackgroundColor(Color.rgb(0, 153, 0));

                    am.cancel(alarmIntent);
                    alarmIntent.cancel();

                } else if (validNumber && messageValue && numberValue) {
                    start.setText("Stop");
                    start.setBackgroundColor(Color.parseColor("#e74c3c"));
                    started = true;
                    int milliSeconds = Integer.parseInt(interval.getText().toString()) * 1000 * 60;

                    Intent i = new Intent();
                    i.putExtra("number", phoneNumber.getText().toString());
                    i.putExtra("message", message.getText().toString());
                    i.setAction("receiveFeedBack");

                    alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, i, 0);
                    am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + milliSeconds, milliSeconds, alarmIntent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}