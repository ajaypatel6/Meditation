package ca.ajaypatel.meditation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTimeInput;

    private TextView timeLeft, welcomeText, nameText;
    private Button goBackBtn, historyBtn, settingsBtn, startBtn, resetBtn, setMinutesBtn;

    private CountDownTimer countdownTimer;

    private long timeLeftInMs; //10 minutes 600000
    private long timeLeftInMsOriginal = timeLeftInMs;
    private int timeInMinutes;

    private boolean timerRunning;

    private Vibrator vibrate;

    private static final String SHARED_PREFS = "sharedPreferences";
    private static final String NOTIFICATIONS_KEY = "notifications_key";
    private static final String NAME_KEY = "name_key";

    // not made yet
    private static final String MEDITATIONS_TOTAL_KEY = "meditations_total_key";
    private static final String TOTAL_TIME_KEY = "total_time_key";
    private static final String AVERAGE_LENGTH_KEY = "average_length_key";
    private static final String STREAK_KEY = "streak_key";
    private static final String DATE_STARTED_KEY = "date_started_key";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // overwrite load
        setTheme(R.style.Theme_Meditation);

        // remove notifications bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //before load
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        boolean isFirstOpen = preferences.getBoolean("isFirstOpen", true);


        SharedPreferences sp = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        int exactNumberOfPreferences = 7;

        if (isFirstOpen) {
            showFirstStart();
        }

        initViews();

        if ((sp.getAll().size() != exactNumberOfPreferences)) {
            mEditor = mPreferences.edit();

            mEditor.putString(MEDITATIONS_TOTAL_KEY, "0");
            mEditor.putString(TOTAL_TIME_KEY, "0");
            mEditor.putString(STREAK_KEY, "0");
            mEditor.putString(AVERAGE_LENGTH_KEY, "1");

            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            // 14-Jan-2021
            mEditor.putString(DATE_STARTED_KEY, formattedDate);

            //mEditor.clear(); // i think this is the only one needed?
            mEditor.commit();
        }

        // after load
        // On Click Listeners
        setMinutesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                String input = editTimeInput.getText().toString();


                if (input.length() == 0) {
                    Toast.makeText(MainActivity.this, "Please enter minutes", Toast.LENGTH_SHORT).show();
                    return;
                }
                timeInMinutes = Integer.valueOf(input);
                long msInput = Long.parseLong(input) * 60000;
                if (msInput == 0) {
                    Toast.makeText(MainActivity.this, "Positive numbers please", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(msInput);
                updateTimer();
                startBtn.setVisibility(View.VISIBLE);
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                startStop();
                setMinutesBtn.setVisibility(View.INVISIBLE);
                editTimeInput.setVisibility(View.INVISIBLE);
            }
        });
        updateTimer();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                resetTimer();
                startBtn.setVisibility(View.INVISIBLE);
                startBtn.setText("Meditate");
                setMinutesBtn.setVisibility(View.VISIBLE);
                editTimeInput.setVisibility(View.VISIBLE);

            }
        });
        updateTimer();

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        String nameStr = mPreferences.getString(NAME_KEY, "");
        nameText.setText(nameStr);

    }

    private void setTime(long milliseconds) {
        resetTimer();
        timeLeftInMs = milliseconds;

    }

    private void startStop() {
        if (timerRunning) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {

        countdownTimer = new CountDownTimer(timeLeftInMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMs = millisUntilFinished;
                updateTimer();
                hideWhileMeditating();
            }

            @Override
            public void onFinish() {
                // some sound, record data, some message
                Toast.makeText(MainActivity.this, "Meditation Complete", Toast.LENGTH_SHORT).show();

                // updateStats();!!!!!!!!!!!!!!
                String tot = mPreferences.getString(MEDITATIONS_TOTAL_KEY, "");
                int total = Integer.valueOf(tot);
                total++;
                //

                String totalTime = mPreferences.getString(TOTAL_TIME_KEY, "");
                int totalMinutes = Integer.valueOf(totalTime);
                //int minutes = (int) timeLeftInMsOriginal / 60000; // in ms
                totalMinutes = totalMinutes + (timeInMinutes);

                String totalStreak = mPreferences.getString(STREAK_KEY, "");
                int t = Integer.valueOf(totalStreak);
                t++;

                String averageLength = mPreferences.getString(AVERAGE_LENGTH_KEY, "");
                double average = Double.valueOf(averageLength);

                average = (double)totalMinutes / (double)total;

                //String dateStarted = mPreferences.getString(DATE_STARTED_KEY, "");

                mEditor = mPreferences.edit();

                mEditor.putString(MEDITATIONS_TOTAL_KEY, String.valueOf(total)); // good
                mEditor.putString(STREAK_KEY, String.valueOf(t)); // good

                mEditor.putString(TOTAL_TIME_KEY, String.valueOf(totalMinutes)); // NO
                mEditor.putString(AVERAGE_LENGTH_KEY, String.valueOf(average)); // NO

               // mEditor.putString(DATE_STARTED_KEY, "TODAYS DATE"); //NO

                mEditor.commit();

                // END UPDATE!!!!!!!!!!!!!!!!
                vibrate.vibrate(2000);
                //final MediaPlayer finishSound = MediaPlayer.create(this,R.raw.finish);
                MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.finish);
                player.start();

                //

                startBtn.setVisibility(View.INVISIBLE);
                showAfterMeditating();
                startBtn.setText("Meditate");
                setMinutesBtn.setVisibility(View.VISIBLE);
                editTimeInput.setVisibility(View.VISIBLE);
                resetTimer();


            }
        }.start();

        if (timeLeftInMs > 0) {
            startBtn.setText("Pause");
            timerRunning = true;
            resetBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void updateStats() {

    }

    private void updateTotalMeditations() {
    }

    private void updateTimer() {
        int minutes = (int) timeLeftInMs / 60000; // in ms
        int seconds = (int) timeLeftInMs % 60000 / 1000;

        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if (seconds < 10) {
            timeLeftText += "0";
        }
        timeLeftText += seconds;

        timeLeft.setText(timeLeftText);
    }

    private void stopTimer() {
        resetBtn.setVisibility(View.VISIBLE);
        showAfterMeditating();
        countdownTimer.cancel();
        startBtn.setText("Resume Meditation");
        timerRunning = false;
    }

    private void resetTimer() {
        timeLeftInMs = timeLeftInMsOriginal;
        updateTimer();
        resetBtn.setVisibility(View.INVISIBLE);
    }

    private void showFirstStart() {
        // remove this to test first start

        new AlertDialog.Builder(this)
                .setTitle("Hello!")
                .setMessage("What is your name?")
                .setPositiveButton("My Name is ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                })
                .create().show();

        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("isFirstOpen", false);
        editor.commit();
    }

    private void hideWhileMeditating() {
        welcomeText.setVisibility(View.INVISIBLE);
        historyBtn.setVisibility(View.INVISIBLE);
        settingsBtn.setVisibility(View.INVISIBLE);
        nameText.setVisibility(View.INVISIBLE);
    }

    private void showAfterMeditating() {
        welcomeText.setVisibility(View.VISIBLE);
        historyBtn.setVisibility(View.VISIBLE);
        settingsBtn.setVisibility(View.VISIBLE);
        nameText.setVisibility(View.VISIBLE);

    }

    private void initViews() {
        startBtn = findViewById(R.id.start_btn);
        goBackBtn = findViewById(R.id.goBackBtn);
        historyBtn = findViewById(R.id.historyBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        timeLeft = findViewById(R.id.timeLeft);
        resetBtn = findViewById(R.id.reset_btn);
        setMinutesBtn = findViewById(R.id.setMinutes_btn);
        editTimeInput = findViewById(R.id.editCountdown);
        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        welcomeText = findViewById(R.id.welcomeText);
        nameText = findViewById(R.id.nameText);
        mPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);

    }
}