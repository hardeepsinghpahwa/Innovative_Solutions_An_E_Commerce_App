package com.example.itshop.Fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.itshop.OrderDetailStatus;
import com.example.itshop.R;
import com.example.itshop.itemdetails;
import com.example.itshop.orderdet;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class MyOrders extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<orderdet, OrderViewHolder> firebaseRecyclerAdapter;
    CircularProgressBar circularProgressBar;
    ImageView back;
    ArrayList<String> ids,ids2;
    int p = 0;
    Double rat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        recyclerView = findViewById(R.id.myordersrecyview);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        back = findViewById(R.id.back);
        ids = new ArrayList<>();
        ids2 = new ArrayList<>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Active Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    circularProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query = FirebaseDatabase.getInstance().getReference().child("Active Orders").orderByChild("timestamp");

        FirebaseRecyclerOptions<orderdet> options = new FirebaseRecyclerOptions.Builder<orderdet>()
                .setQuery(query, new SnapshotParser<orderdet>() {
                    @NonNull
                    @Override
                    public orderdet parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new orderdet(snapshot.child("orderid").getValue(String.class), snapshot.child("status").getValue(String.class));
                    }
                }).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<orderdet, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position, @NonNull final orderdet model) {

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MyOrders.this, OrderDetailStatus.class);
                        intent.putExtra("orderid", firebaseRecyclerAdapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });


                firebaseRecyclerAdapter.getRef(position).child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getChildrenCount() == 1) {
                                holder.num.setText("(" + snapshot.getChildrenCount() + " Product)");
                            } else {
                                holder.num.setText("(" + snapshot.getChildrenCount() + " Products)");
                            }

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                FirebaseDatabase.getInstance().getReference().child("Items").child(dataSnapshot.child("itemid").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        holder.name.setText(snapshot.child("name").getValue(String.class));

                                        snapshot.getRef().child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Glide.with(MyOrders.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
                                                    break;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                break;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                FirebaseDatabase.getInstance().getReference().child("Active Orders").child(firebaseRecyclerAdapter.getRef(position).getKey()).child("tracking").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.child("text").getValue(String.class).equals("Delivered")) {
                                    Log.i("orderid", firebaseRecyclerAdapter.getRef(position).getKey());
                                    FirebaseDatabase.getInstance().getReference().child("Reviews").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot snapshot1) {

                                            FirebaseDatabase.getInstance().getReference().child("Active Orders").child(firebaseRecyclerAdapter.getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull final DataSnapshot snapshot) {

                                                    if(snapshot.exists())
                                                    {
                                                    snapshot.getRef().child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                                                if (!snapshot1.child(dataSnapshot.child("itemid").getValue(String.class)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                                                                    holder.rate.setVisibility(View.VISIBLE);
                                                                    holder.rate.setBackgroundColor(getColor(R.color.lightgrey));
                                                                    holder.rate.setText("Rate");
                                                                    holder.rate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                                                    break;
                                                                } else {
                                                                    holder.rate.setVisibility(View.VISIBLE);
                                                                }
                                                            }


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                if (model.getStatus().equals("Payment Pending")) {
                    Drawable unwrappedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle);
                    unwrappedDrawable.setTint(getColor(R.color.yellow));
                    holder.status.setText("Payment Pending");
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                } else if (model.getStatus().equals("Payment Success")) {
                    Drawable unwrappedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle);
                    unwrappedDrawable.setTint(getColor(R.color.orange));
                    holder.status.setText("Order Placed");
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                } else if (model.getStatus().equals("Payment Failed") || model.getStatus().equals("Cancelled")) {
                    Drawable unwrappedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle);
                    unwrappedDrawable.setTint(getColor(R.color.red));

                    holder.status.setText(model.getStatus());
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                } else if (model.getStatus().equals("Delivered")) {
                    Drawable unwrappedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle);
                    unwrappedDrawable.setTint(getColor(R.color.green));

                    holder.status.setText(model.getStatus());
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);

                } else {
                    Drawable unwrappedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle);
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, getColor(R.color.orange));
                    holder.status.setText(model.getStatus());
                    holder.status.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null);
                }


                holder.rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        final Dialog dialog = new Dialog(MyOrders.this);
                        dialog.setContentView(R.layout.ratingdialog);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setWindowAnimations(R.style.AppTheme_UpDown);

                        final RecyclerView recyclerView1;

                        recyclerView1 = dialog.findViewById(R.id.raterecyview);

                        FirebaseDatabase.getInstance().getReference().child("Active Orders").child(firebaseRecyclerAdapter.getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {

                                ids.clear();
                                snapshot.getRef().child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                            ids.add(dataSnapshot.child("itemid").getValue(String.class));
                                        }

                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyOrders.this, LinearLayoutManager.HORIZONTAL, false);
                                        recyclerView1.setLayoutManager(linearLayoutManager);
                                        recyclerView1.setAdapter(new ItemsAdapter(ids, dialog));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }}

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        dialog.show();

                    }
                });


            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order, parent, false);
                return new OrderViewHolder(view);
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyOrders.this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private class OrderViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, status, rate, num;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            rate = itemView.findViewById(R.id.rateitem);
            num = itemView.findViewById(R.id.productsnum);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(MyOrders.this, "fadein-to-fadeout");
    }

    private String getFirst3Words(String myString) {
        String[] arr = myString.split("\\s+");
        //Splits words & assign to the arr[]  ex : arr[0] -> Copying ,arr[1] -> first


        int N = 3; // NUMBER OF WORDS THAT YOU NEED
        String nWords = "";

        // concatenating number of words that you required
        if (arr.length >= 3) {
            for (int i = 0; i < N; i++) {
                nWords = nWords + " " + arr[i];
            }
        } else {
            nWords = myString;
        }

        return nWords;
    }

    private class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        ArrayList<String> itemids;
        Dialog dialog;

        public ItemsAdapter(ArrayList<String> itemids, Dialog dialog) {
            this.itemids = itemids;
            this.dialog = dialog;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ratedialog, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

            FirebaseDatabase.getInstance().getReference().child("Items").child(itemids.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    final itemdetails itemdetails = snapshot.getValue(com.example.itshop.itemdetails.class);

                    holder.name.setText(itemdetails.getName());
                    holder.num.setText((position + 1) + " of  " + ids.size());

                    holder.circularProgressBar.setVisibility(View.GONE);

                    FirebaseDatabase.getInstance().getReference().child("Reviews").child(itemids.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                if (snapshot.exists() && snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                                    holder.review.setText(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("review").getValue(String.class));
                                    holder.rate.setText("Edit Rating");
                                    holder.ratingBar.setEnabled(false);
                                    holder.review.setEnabled(false);
                                    holder.ratingBar.setRating(Float.valueOf(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rating").getValue(String.class)));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    snapshot.child("images").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists())
                            {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Glide.with(MyOrders.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
                                break;
                            }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    holder.cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });

                    holder.rate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (holder.rate.getText().toString().equals("Edit Rating")) {
                                holder.rate.setText("Rate");
                                holder.ratingBar.setEnabled(true);
                                holder.review.setEnabled(true);
                            } else {
                                if (holder.ratingBar.getRating() == 0) {
                                    Toast.makeText(MyOrders.this, "Provide some rating first", Toast.LENGTH_SHORT).show();
                                } else {
                                    holder.progressBar.setVisibility(View.VISIBLE);

                                    String rev;
                                    if (holder.review.getText().toString().equals("")) {
                                        rev = "";
                                    } else {
                                        rev = holder.review.getText().toString();
                                    }


                                    final Map map = new HashMap();
                                    map.put("rating", String.valueOf(holder.ratingBar.getRating()));
                                    map.put("review", rev);

                                    FirebaseDatabase.getInstance().getReference().child("Reviews").child(itemids.get(position)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                rat= Double.valueOf(0);

                                                FirebaseDatabase.getInstance().getReference().child("Reviews").child(itemids.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.exists())
                                                        {
                                                        for(DataSnapshot dataSnapshot:snapshot.getChildren())
                                                        {
                                                            rat=rat+Double.valueOf(dataSnapshot.child("rating").getValue(String.class));
                                                        }

                                                        Map map1=new HashMap();
                                                        map1.put("rating",String.valueOf(rat/snapshot.getChildrenCount()));
                                                        FirebaseDatabase.getInstance().getReference().child("Items").child(itemids.get(position)).updateChildren(map1).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Toast.makeText(MyOrders.this, "Thanks For The Rating", Toast.LENGTH_SHORT).show();
                                                                    holder.rate.setText("Edit Rating");
                                                                    holder.progressBar.setVisibility(View.GONE);

                                                                }
                                                                else {
                                                                    Toast.makeText(MyOrders.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                                                    holder.progressBar.setVisibility(View.GONE);

                                                                }
                                                            }
                                                        });
                                                    }}

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            } else {
                                                Toast.makeText(MyOrders.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                                holder.progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                }

                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return itemids.size();
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, cross;
        TextView name, num;
        Button rate;
        EditText review;
        CircularProgressBar circularProgressBar;
        RatingBar ratingBar;
        ProgressBar progressBar;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
            rate = itemView.findViewById(R.id.ratebutton);
            cross = itemView.findViewById(R.id.ratecross);
            num = itemView.findViewById(R.id.num);
            ratingBar = itemView.findViewById(R.id.ratingBarrate);
            review = itemView.findViewById(R.id.review);
            circularProgressBar = itemView.findViewById(R.id.circularProgressBar);
            progressBar=itemView.findViewById(R.id.progressbar);
        }

    }
}