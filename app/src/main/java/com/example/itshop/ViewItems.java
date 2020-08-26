package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class ViewItems extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView title, itemsincart;
    ImageView back;
    RelativeLayout cart;
    FirebaseRecyclerAdapter<itemdetails, ViewItemsViewHolder> firebaseRecyclerAdapter;
    CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        recyclerView = findViewById(R.id.itemsrecyclerview);
        title = findViewById(R.id.itemtitle);
        back = findViewById(R.id.itemsback);
        itemsincart = findViewById(R.id.itemsincart);
        cart = findViewById(R.id.rl);
        circularProgressBar = findViewById(R.id.circularProgressBar);


        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewItems.this, MyCart.class);
                startActivity(intent);
                customType(ViewItems.this, "fadein-to-fadeout");

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemsincart.setText(String.valueOf(snapshot.getChildrenCount()));
                } else {
                    itemsincart.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        circularProgressBar.bringToFront();
        Query query = FirebaseDatabase.getInstance().getReference().child("Items");

        FirebaseRecyclerOptions<itemdetails> options = new FirebaseRecyclerOptions.Builder<itemdetails>()
                .setQuery(query, new SnapshotParser<itemdetails>() {
                    @NonNull
                    @Override
                    public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new itemdetails(snapshot.getKey(), snapshot.child("name").getValue(String.class), snapshot.child("rating").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.child("discount").getValue(String.class));
                    }
                }).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<itemdetails, ViewItemsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewItemsViewHolder holder, final int position, @NonNull final itemdetails model) {


                if (position == 1)
                    circularProgressBar.setVisibility(View.GONE);

                holder.name.setText(model.getName());
                holder.rating.setText(model.getRating());


                if (model.getDiscount() == null || model.getDiscount().equals("") || model.getDiscount().equals("0")) {
                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                    holder.price.setText(format.format(new BigDecimal(model.getPrice())));

                } else {
                    double am = (Integer.valueOf(model.getPrice())) - (((Integer.valueOf(model.getDiscount())) * 0.01) * (Integer.valueOf(model.getPrice())));
                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                    holder.price.setText(format.format(new BigDecimal(String.valueOf(am))));
                    holder.discount.setText(format.format(new BigDecimal(model.getPrice())));
                    holder.discount.setVisibility(View.VISIBLE);
                    holder.discount.setPaintFlags(holder.discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                }

                FirebaseDatabase.getInstance().getReference().child("Items").child(model.getImage()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Glide.with(ViewItems.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewItems.this, ItemDetail.class);
                        intent.putExtra("itemid", firebaseRecyclerAdapter.getRef(position).getKey());
                        startActivity(intent);
                        customType(ViewItems.this, "left-to-right");
                    }
                });
            }

            @NonNull
            @Override
            public ViewItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitemlayout, parent, false);
                return new ViewItemsViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    private class ViewItemsViewHolder extends RecyclerView.ViewHolder {

        TextView name, rating, price, discount;
        ImageView imageView;

        public ViewItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.viewitemimage);
            rating = itemView.findViewById(R.id.viewitemrating);
            price = itemView.findViewById(R.id.viewitemprice);
            name = itemView.findViewById(R.id.viewitemname);
            discount = itemView.findViewById(R.id.discount);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(ViewItems.this, "right-to-left");
    }
}