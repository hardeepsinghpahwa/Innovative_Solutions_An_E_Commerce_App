package com.example.itshop.Fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.itshop.NetworkBroadcast;
import com.example.itshop.R;
import com.example.itshop.itemdetails;
import com.example.itshop.notiitem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static maes.tech.intentanim.CustomIntent.customType;

public class Notifications extends AppCompatActivity {

    ImageView nonotis,back;
    TextView markasread;
    RecyclerView recyclerView;
    NetworkBroadcast networkBroadcast;
    CircularProgressBar circularProgressBar;
    FirebaseRecyclerAdapter<notiitem,NotiViewHolder>firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        nonotis=findViewById(R.id.nonotifications);
        recyclerView=findViewById(R.id.notificationsrecyview);
        markasread=findViewById(R.id.markallasread);
        back=findViewById(R.id.back);
        circularProgressBar=findViewById(R.id.circularProgressBar);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                circularProgressBar.setVisibility(View.GONE);

                if(!snapshot.exists())
                {
                    nonotis.setVisibility(View.VISIBLE);
                }
                else {

                    markasread.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren())
                            {
                                if(dataSnapshot.child("read").getValue(String.class).equals("0"))
                                {
                                    dataSnapshot.getRef().child("read").setValue("1");
                                }
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notifications");

        FirebaseRecyclerOptions<notiitem> options = new FirebaseRecyclerOptions.Builder<notiitem>()
                .setQuery(query, new SnapshotParser<notiitem>() {
                    @NonNull
                    @Override
                    public notiitem parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new notiitem(snapshot.child("title").getValue(String.class),snapshot.child("body").getValue(String.class),snapshot.child("read").getValue(String.class),snapshot.child("timestamp").getValue(Long.class));
                    }
                }).build();


        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<notiitem, NotiViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotiViewHolder holder, final int position, @NonNull notiitem model) {
                holder.title.setText(model.getTitle());
                holder.body.setText(model.getBody());

                if(model.getRead().equals("0"))
                {
                    holder.read.setVisibility(View.VISIBLE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firebaseRecyclerAdapter.getRef(position).child("read").setValue("1");
                    }
                });

                DateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                DateFormat sdf2 = new SimpleDateFormat("hh:mm aa");
                DateFormat sdf3 = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");


                if(sdf.format(new Date()).equals(createDateString(model.getTimestamp())))
                {
                    holder.dateandtime.setText("Today, "+ sdf2.format(new Date()));
                }
                else {
                    holder.dateandtime.setText(sdf3.format(createDate(model.getTimestamp())));

                }

            }

            @NonNull
            @Override
            public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notiitem,parent,false);
                return new NotiViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(Notifications.this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        customType(Notifications.this,"fadein-to-fadeout");

    }

    private class NotiViewHolder extends RecyclerView.ViewHolder{

        TextView title,body,dateandtime;
        RelativeLayout read;
        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.title);
            body=itemView.findViewById(R.id.body);
            dateandtime=itemView.findViewById(R.id.dateandtime);
            read=itemView.findViewById(R.id.notiunread);
        }
    }

    public CharSequence createDateString(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        DateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        return sdf.format(d);
    }

    public Date createDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        return d;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
        this.unregisterReceiver(networkBroadcast);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        networkBroadcast=new NetworkBroadcast();
        this.registerReceiver(networkBroadcast, filter);

    }

}