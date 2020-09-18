package com.example.itshop.Fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Layer;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.itshop.NetworkBroadcast;
import com.example.itshop.R;
import com.example.itshop.ViewItems;
import com.example.itshop.itemdetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import static maes.tech.intentanim.CustomIntent.customType;

public class Categories extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView back;
    FirebaseRecyclerAdapter<itemdetails,CategoriesViewHolder> firebaseRecyclerAdapter;
    CircularProgressBar circularProgressBar;
    NetworkBroadcast networkBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        recyclerView=findViewById(R.id.categoriesrecyview);
        back=findViewById(R.id.categoryback);
        circularProgressBar=findViewById(R.id.circularProgressBar);

        circularProgressBar.bringToFront();

        Query query = FirebaseDatabase.getInstance().getReference().child("Categories");

        FirebaseRecyclerOptions<itemdetails> options = new FirebaseRecyclerOptions.Builder<itemdetails>()
                .setQuery(query, new SnapshotParser<itemdetails>() {
                    @NonNull
                    @Override
                    public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new itemdetails(snapshot.child("image").getValue(String.class),snapshot.child("name").getValue(String.class));
                    }
                }).build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<itemdetails, CategoriesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CategoriesViewHolder holder, int position, @NonNull final itemdetails model) {
                Glide.with(Categories.this).load(model.getImage()).into(holder.imageView);
                if(position==1) {
                    circularProgressBar.setVisibility(View.GONE);

                    recyclerView.scheduleLayoutAnimation();
                }
                    holder.categoryname.setText(model.getName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(Categories.this, ViewItems.class);
                        intent.putExtra("category",model.getName());
                        startActivity(intent);
                        customType(Categories.this,"left-to-right");

                    }
                });

            }

            @NonNull
            @Override
            public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryitem,parent,false);
                return new CategoriesViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        customType(Categories.this,"fadein-to-fadeout");

    }

    private class CategoriesViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView categoryname,itemscount;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.categoryimage);
            categoryname=itemView.findViewById(R.id.categoryname);
            itemscount=itemView.findViewById(R.id.categoryitems);


        }

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
        this.unregisterReceiver(networkBroadcast);
    }

}