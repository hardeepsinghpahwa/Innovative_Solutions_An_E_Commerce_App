package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
    String cat;
    FirebaseRecyclerAdapter<itemdetails, ViewItemsViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerPagingAdapter<itemdetails, ViewItemsViewHolder> firebaseRecyclerAdapter1;
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
        cat=getIntent().getStringExtra("category");


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

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Items");

        databaseReference.orderByChild("category").equalTo(cat);
        Query query = query=databaseReference.orderByChild("category").equalTo(cat);

        FirebaseRecyclerOptions<itemdetails> options = new FirebaseRecyclerOptions.Builder<itemdetails>()
                .setQuery(query, new SnapshotParser<itemdetails>() {
                    @NonNull
                    @Override
                    public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new itemdetails(snapshot.getKey(), snapshot.child("name").getValue(String.class), snapshot.child("rating").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.child("discount").getValue(String.class));
                    }
                }).build();


        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(5)
                .build();

        DatabasePagingOptions<itemdetails> options1=new DatabasePagingOptions.Builder<itemdetails>()
                .setQuery(query, config, new SnapshotParser<itemdetails>() {
                    @NonNull
                    @Override
                    public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new itemdetails(snapshot.getKey(), snapshot.child("name").getValue(String.class), snapshot.child("rating").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.child("discount").getValue(String.class));
                    }
                }).build();


        firebaseRecyclerAdapter1=new FirebaseRecyclerPagingAdapter<itemdetails, ViewItemsViewHolder>(options1) {
            @NonNull
            @Override
            public ViewItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitemlayout, parent, false);
                return new ViewItemsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ViewItemsViewHolder holder, final int position, @NonNull itemdetails model) {


                    if (position == 1)
                        circularProgressBar.setVisibility(View.GONE);

                    holder.name.setText(model.getName());

                    if (model.getRating() != null && !model.getRating().equals("")) {
                        holder.linearLayout.setVisibility(View.VISIBLE);
                        holder.rating.setText(model.getRating());
                        holder.rating.setVisibility(View.VISIBLE);

                        FirebaseDatabase.getInstance().getReference().child("Reviews").child(firebaseRecyclerAdapter1.getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getChildrenCount()==0)
                                {
                                    holder.ratingnum.setText("(No ratings)");
                                }
                                else {
                                    holder.ratingnum.setText("("+snapshot.getChildrenCount()+" ratings"+")");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }


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
                            if (snapshot.exists()) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Glide.with(ViewItems.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
                                    break;
                                }
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

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {

            }
        };



        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<itemdetails, ViewItemsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewItemsViewHolder holder, final int position, @NonNull final itemdetails model) {


                if (position == 1) {
                    circularProgressBar.setVisibility(View.GONE);
                    recyclerView.scheduleLayoutAnimation();
                }
                holder.name.setText(model.getName());

                if (model.getRating() != null && !model.getRating().equals("")) {
                    holder.linearLayout.setVisibility(View.VISIBLE);
                    holder.rating.setText(model.getRating());
                    holder.rating.setVisibility(View.VISIBLE);

                    FirebaseDatabase.getInstance().getReference().child("Reviews").child(firebaseRecyclerAdapter.getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getChildrenCount()==0)
                            {
                                holder.ratingnum.setText("(No ratings)");
                            }
                            else {
                                holder.ratingnum.setText("("+snapshot.getChildrenCount()+" ratings"+")");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


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
                        if (snapshot.exists()) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Glide.with(ViewItems.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
                                break;
                            }
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

        TextView name, rating, price, discount,ratingnum;
        ImageView imageView;
        LinearLayout linearLayout;


        public ViewItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.viewitemimage);
            rating = itemView.findViewById(R.id.viewitemrating);
            price = itemView.findViewById(R.id.viewitemprice);
            name = itemView.findViewById(R.id.viewitemname);
            discount = itemView.findViewById(R.id.discount);
            ratingnum=itemView.findViewById(R.id.ratingsnum);
            linearLayout=itemView.findViewById(R.id.ll);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
        firebaseRecyclerAdapter1.startListening();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
        firebaseRecyclerAdapter1.startListening();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(ViewItems.this, "right-to-left");
    }
}