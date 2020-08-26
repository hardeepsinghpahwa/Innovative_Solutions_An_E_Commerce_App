package com.example.itshop.Fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.itshop.R;

import static maes.tech.intentanim.CustomIntent.customType;

public class Notifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        customType(Notifications.this,"fadein-to-fadeout");

    }
}