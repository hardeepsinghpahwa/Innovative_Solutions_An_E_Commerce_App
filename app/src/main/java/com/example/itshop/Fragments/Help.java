package com.example.itshop.Fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.itshop.Notifications.SendNoti;
import com.example.itshop.OrderDetails;
import com.example.itshop.R;

import static maes.tech.intentanim.CustomIntent.customType;

public class Help extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        button=findViewById(R.id.send);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendNoti sendNoti=new SendNoti();
                sendNoti.sendNotification(Help.this,"pj2RDmhgXEVjn3zrMaYYJ25vFvk1","New Order","You got a new order of "+ 6+" items");
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        customType(Help.this,"fadein-to-fadeout");

    }
}