package com.example.itshop.Fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.itshop.R;

import static maes.tech.intentanim.CustomIntent.customType;

public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        customType(Help.this,"fadein-to-fadeout");

    }
}