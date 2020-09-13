package com.example.itshop.Fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itshop.Notifications.SendNoti;
import com.example.itshop.OrderDetails;
import com.example.itshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class Help extends AppCompatActivity {

    TextView button;
    EditText subject,desc;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        button=findViewById(R.id.sendissue);
        subject=findViewById(R.id.subject);
        desc=findViewById(R.id.body);
        progressBar=findViewById(R.id.progressbar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);

                if (!subject.getText().toString().equals("") && !desc.getText().toString().equals("")) {

                    AlertDialog.Builder builder=new AlertDialog.Builder(Help.this);
                    builder.setTitle("Send Issue request");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressBar.setVisibility(View.VISIBLE);
                            Map map = new HashMap();
                            map.put("subject", subject.getText().toString());
                            map.put("description", desc.getText().toString());
                            map.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            FirebaseDatabase.getInstance().getReference().child("Issues").child(UUID.randomUUID().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Help.this, "Issue Request Sent. Wait for a reply", Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase.getInstance().getReference().child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.child("token").exists()) {
                                                    progressBar.setVisibility(View.GONE);
                                                    SendNoti sendNoti = new SendNoti();
                                                    sendNoti.sendNotification(Help.this, snapshot.child("token").getValue(String.class), "New Issue Request", "You have a new issue. Check it out and try to resolve it");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                progressBar.setVisibility(View.VISIBLE);

                                            }
                                        });
                                        subject.setText("");
                                        desc.setText("");
                                    } else {
                                        progressBar.setVisibility(View.VISIBLE);

                                        Toast.makeText(Help.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.show();
                    button.setEnabled(true);
                }
                else {
                    Toast.makeText(Help.this, "Please enter subject and description", Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                }
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