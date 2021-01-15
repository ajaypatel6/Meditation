package ca.ajaypatel.meditation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends AppCompatActivity {
    
    private Button goBackBtn;
    private TextView nameText, meditationsTotalCount, totalTimeCount, streakCount, averageLengthCount, dateStarted;

    private static final String SHARED_PREFS = "sharedPreferences";
    private static final String NAME_KEY = "name_key";

    private static final String MEDITATIONS_TOTAL_KEY = "meditations_total_key";
    private static final String TOTAL_TIME_KEY = "total_time_key";
    private static final String AVERAGE_LENGTH_KEY = "average_length_key";
    //private static final String STREAK_KEY = "streak_key";
    private static final String DATE_STARTED_KEY = "date_started_key";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private Vibrator vibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // name WORKING
        //setData();


        String nameStr = mPreferences.getString(NAME_KEY, "");
        nameText.setText(nameStr);

        //total meditations
        String totalMeditationsStr = mPreferences.getString(MEDITATIONS_TOTAL_KEY, "");

        meditationsTotalCount.setText(String.valueOf(totalMeditationsStr));

        //total time
        String totalTimeStr = mPreferences.getString(TOTAL_TIME_KEY, "");
        totalTimeCount.setText(totalTimeStr);

        //streak
        //String totalStreakStr = mPreferences.getString(STREAK_KEY, "");
        //streakCount.setText(String.valueOf(totalStreakStr));

        //avg length
        String averageLengthStr = mPreferences.getString(AVERAGE_LENGTH_KEY, "");
        averageLengthCount.setText(averageLengthStr);

        // date started
        String dateStartedStr = mPreferences.getString(DATE_STARTED_KEY, "");
        dateStarted.setText(dateStartedStr);

    }

    private void setData() {
        mEditor = mPreferences.edit();
        mEditor.putString(MEDITATIONS_TOTAL_KEY,"0");
        mEditor.putString(TOTAL_TIME_KEY,"0");
       // mEditor.putString(STREAK_KEY, "1");
        mEditor.putString(AVERAGE_LENGTH_KEY,"0");
        //mEditor.putString(DATE_STARTED_KEY,"0");

        mEditor.commit();
        //Toast.makeText(this, "Set Datas", Toast.LENGTH_SHORT).show();
    }


    private void initViews() {
        goBackBtn = findViewById(R.id.goBackBtnH);
        nameText = findViewById(R.id.nameText);
        meditationsTotalCount = findViewById(R.id.meditationsTotalCount);
        totalTimeCount = findViewById(R.id.totalTimeCount);
        //streakCount = findViewById(R.id.streakCount);
        averageLengthCount = findViewById(R.id.averageLengthCount);
        dateStarted = findViewById(R.id.dateStarted);
        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        mPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
    }
}