package com.example.roadtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.opencv.tracking.TrackerKCF;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
