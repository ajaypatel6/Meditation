package ca.ajaypatel.meditation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    private Button goBackBtn, resetBtn, saveBtn, yesBtn, deleteBtn, feedbackBtn, setNotificationBtn;
    private EditText editName;
    private Switch notificationsKey;
    private String nameStr;
    private boolean switchOnOff;
    private TextView yourName;

    private static final String TAG = "MainActivity";

    //public static final String Share
    // private static String T
    private static final String SHARED_PREFS = "sharedPreferences";
    private static final String NOTIFICATIONS_KEY = "notifications_key";
    private static final String NAME_KEY = "name_key";
    private static final String MEDITATIONS_TOTAL_KEY = "meditations_total_key";
    private static final String TOTAL_TIME_KEY = "total_time_key";
    private static final String AVERAGE_LENGTH_KEY = "average_length_key";
    private static final String STREAK_KEY = "streak_key";
    private static final String DATE_STARTED_KEY = "date_started_key";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private Vibrator vibrate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        createNotificationChannel();

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                nameStr = editName.getText().toString();

                saveData();
                //Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                //startActivity(intent);
                //pull info?

                Log.d(TAG, "onClick: name: " + nameStr);

            }
        });
        Log.d(TAG, "onCreate: name: " + nameStr);
        // RESET
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                resetBtn.setVisibility(View.INVISIBLE);
                Toast.makeText(SettingsActivity.this, "Are you sure? You will lose all your progress", Toast.LENGTH_SHORT).show();
                // make yes button next to it
                yesBtn.setVisibility(View.VISIBLE);
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                yesBtn.setVisibility(View.INVISIBLE);
                Toast.makeText(SettingsActivity.this, "Final warning, delete all stats?", Toast.LENGTH_SHORT).show();
                deleteBtn.setVisibility(View.VISIBLE);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                Toast.makeText(SettingsActivity.this, "Stats deleted", Toast.LENGTH_SHORT).show();
                deleteStats();
                deleteBtn.setVisibility(View.INVISIBLE);
                resetBtn.setVisibility(View.VISIBLE);

            }
        });
        //RESET

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Email : ajaypatel23623@gmail.com", Toast.LENGTH_SHORT).show();
            }
        });


        nameStr = mPreferences.getString(NAME_KEY, "");
        switchOnOff = mPreferences.getBoolean(NOTIFICATIONS_KEY, false);

        yourName.setText(nameStr);
        notificationsKey.setChecked(switchOnOff);


        if (switchOnOff) {
            //TODO: show notifications time set, and make notif
            // 10 am Notification

            //setNotificationBtn.setVisibility(View.VISIBLE);
            //setNotificationBtn.setOnClickListener(new View.OnClickListener() {
            //  @Override
            //public void onClick(View v) {
            //Toast.makeText(SettingsActivity.this, "Reminder is set for..", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, BroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 1);


            long timeAtButtonClick = System.currentTimeMillis();
            long tenAfter = 1000 * 10; //10 s after

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

            //alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + tenAfter, pendingIntent);

            // }
            //});
        }


    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "MeditationReminderChannel";
            String description = "Channel for meditation Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Notify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


    }

    private void checkSharedPreferences() {

    }

    private void deleteStats() {
        mEditor = mPreferences.edit();

        mEditor.putString(MEDITATIONS_TOTAL_KEY, "0");
        mEditor.putString(TOTAL_TIME_KEY, "0");
        mEditor.putString(STREAK_KEY, "0");
        mEditor.putString(AVERAGE_LENGTH_KEY, "1");
        mEditor.putString(DATE_STARTED_KEY, "0");
        //mEditor.clear(); // i think this is the only one needed?
        mEditor.commit();

    }

    private void saveData() {
        // tool to put items into database
        mEditor = mPreferences.edit();
        mEditor.putString(NAME_KEY, nameStr);
        mEditor.putBoolean(NOTIFICATIONS_KEY, notificationsKey.isChecked());
        mEditor.commit();
        Toast.makeText(this, "Saved settings", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        goBackBtn = findViewById(R.id.goBackBtn);
        resetBtn = findViewById(R.id.resetBtn);
        saveBtn = findViewById(R.id.saveBtn);
        yesBtn = findViewById(R.id.confirmBtn);
        feedbackBtn = findViewById(R.id.feedbackBtn);
        deleteBtn = findViewById(R.id.finalConfirmBtn);
        notificationsKey = findViewById(R.id.notificationsSwitch);
        editName = findViewById(R.id.editName);
        yourName = findViewById(R.id.yourName);
        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        setNotificationBtn = findViewById(R.id.setNotification);
        // init preference object, declaring database
        mPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);

        //mPreferences = PreferenceManager.getDefaultSharedPreferences(this); // NEW WAY TO DO IT
    }

}