package com.example.task41;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    EditText inputTask;
    TextView timeView, taskView;
    SharedPreferences sharedPreferences;
    ImageButton startButton, pauseButton, stopButton;
    Chronometer chronometer;
    long stoppedTime = 0;
    boolean started = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set-up
        inputTask = findViewById(R.id.editTextTask);
        timeView = findViewById(R.id.viewLast);
        taskView = findViewById(R.id.taskView);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        timeView.setText("You spent 00:00 on .... last time");

        //shared preferences
        sharedPreferences = getSharedPreferences("com.example.task41", MODE_PRIVATE);
        getSharedPreferences();

        //Chronometer
        chronometer = (Chronometer) findViewById(R.id.Timer);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                chronometer.setText(t);
            }
        });
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setText("00:00:00");


        //set up time when change orientation
        if (savedInstanceState != null) {
            chronometer.setBase(savedInstanceState.getLong("BASE_TIME"));
            stoppedTime = savedInstanceState.getLong("STOP_TIME");
            if (savedInstanceState.getBoolean("TIME_STATE")) {
                chronometer.start();
                started = true;
            }
            else
            {
                chronometer.setBase(SystemClock.elapsedRealtime() - stoppedTime);
                chronometer.setText("00:" + chronometer.getText().toString());
                started = false;
            }

        }
    }

    //make sure keep counting when rotate screen
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("TIME_STATE", started);
        outState.putLong("STOP_TIME", stoppedTime);
        outState.putLong("BASE_TIME", chronometer.getBase());
    }

    public void onClick (View view) {
        switch (view.getId())
        {
            case R.id.startButton:
                if (!started) {
                    chronometer.setBase(SystemClock.elapsedRealtime() - stoppedTime);
                    chronometer.start();
                    started = true;
                }
            break;

            case R.id.pauseButton:
                if (started) {
                    chronometer.stop();
                    stoppedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                    started = false;
                }
            break;

            case R.id.stopButton:
                chronometer.setText("00:00:00");
                if (started) {
                    chronometer.stop();
                    stoppedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Task", inputTask.getText().toString());
                editor.putLong("Time_Spent", stoppedTime);
                editor.apply();

                stoppedTime = 0;
                started = false;

            break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    //Get shared preference and print
    public void getSharedPreferences ()
    {
        String taskname = sharedPreferences.getString("Task", "");
        long time = sharedPreferences.getLong("Time_Spent", 0);
        long minutes = (time / 1000)  / 60;
        String sMinute = String.valueOf(minutes);
        if(minutes < 10) {
            sMinute = "0" + sMinute;
        }
        int seconds = (int)((time / 1000) % 60);
        String sSeconds = String.valueOf(seconds);
        if(seconds < 10) {
            sSeconds = "0" + sSeconds;
        }
        if (!taskname.equals("") || time != 0) {
            timeView.setText("You spent " + sMinute + ":" + sSeconds + " on " + taskname + " last time.");
        }
    }
}