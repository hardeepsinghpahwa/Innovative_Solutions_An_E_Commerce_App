package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.itshop.Fragments.MyOrders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import static maes.tech.intentanim.CustomIntent.customType;

public class MyAccount extends AppCompatActivity {

    TextView name,email,phone,myorders,myaddresses,mywishlist,myreviews;
    ImageView profilepic,edit;
    CircularProgressBar circularProgressBar;
    CardView cardView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        phone=findViewById(R.id.phone);
        myorders=findViewById(R.id.myorders);
        myaddresses=findViewById(R.id.myaddresses);
        mywishlist=findViewById(R.id.mywishlist);
        myreviews=findViewById(R.id.myreviews);
        profilepic=findViewById(R.id.profilepic);
        edit=findViewById(R.id.edit);
        circularProgressBar=findViewById(R.id.circularProgressBar);
        cardView=findViewById(R.id.cardView14);
        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profiledetails profiledetails = snapshot.getValue(com.example.itshop.profiledetails.class);

                    circularProgressBar.setVisibility(View.GONE);

                    name.setText(profiledetails.getName());
                    email.setText(profiledetails.getEmail());
                    phone.setText(profiledetails.getPhone());

                    name.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);

                    cardView.setVisibility(View.VISIBLE);

                    Glide.with(getApplicationContext()).load(profiledetails.getProfilepic()).into(profilepic);
                }
                else {
                    Toast.makeText(MyAccount.this, "Data Changed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        myaddresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MyAccount.this,Addresses.class);
                intent.putExtra("ac","view");
                startActivity(intent);
                customType(MyAccount.this,"left-to-right");
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MyAccount.this, SetupProfile.class);
                intent.putExtra("type","edit");
                startActivity(intent);

            }
        });

        mywishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccount.this,MyWishlist.class));
            }
        });

        myorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccount.this, MyOrders.class));
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(MyAccount.this,"fadein-to-fadeout");
    }
}