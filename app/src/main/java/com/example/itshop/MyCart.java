package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class MyCart extends AppCompatActivity {

    RecyclerView recyclerView;
    ConstraintLayout constraintLayout;
    CircularProgressBar circularProgressBar;
    ImageView noitems, back;
    TextView total, continuetpay, totaltxt, buynow;
    double t = 0;
    int l;
    FirebaseRecyclerAdapter<itemdetails, CartViewHolder> firebaseRecyclerAdapter;
    ArrayList<String> ids, quans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);


        recyclerView = findViewById(R.id.cartrecyclerview);
        constraintLayout = findViewById(R.id.constraintLayout7);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        noitems = findViewById(R.id.noitemsincart);
        total = findViewById(R.id.totalprice);
        continuetpay = findViewById(R.id.continuetopay);
        totaltxt = findViewById(R.id.totaltxt);
        back = findViewById(R.id.back);
        buynow = findViewById(R.id.continuetopay);

        ids = new ArrayList<>();
        quans = new ArrayList<>();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    circularProgressBar.setVisibility(View.GONE);
                    continuetpay.setVisibility(View.VISIBLE);
                    total.setVisibility(View.VISIBLE);
                    totaltxt.setVisibility(View.VISIBLE);

                } else {
                    noitems.setVisibility(View.VISIBLE);
                    circularProgressBar.setVisibility(View.GONE);
                    continuetpay.setVisibility(View.GONE);
                    total.setText("₹ 0");
                    total.setVisibility(View.GONE);
                    totaltxt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");

        FirebaseRecyclerOptions<itemdetails> options = new FirebaseRecyclerOptions.Builder<itemdetails>()
                .setQuery(query, new SnapshotParser<itemdetails>() {
                    @NonNull
                    @Override
                    public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new itemdetails(snapshot.child("itemid").getValue(String.class));
                    }
                }).build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<itemdetails, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder holder, final int position, @NonNull final itemdetails model) {

                FirebaseDatabase.getInstance().getReference().child("Items").child(model.getImage()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            holder.spinner.setKeyListener(null);

                            holder.minus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!holder.spinner.getText().toString().equals("1")) {
                                        holder.spinner.setText((Integer.valueOf(holder.spinner.getText().toString())) - 1 + "");
                                    }
                                }
                            });

                            holder.plus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!holder.spinner.getText().toString().equals(snapshot.child("stock").getValue(String.class))) {
                                        holder.spinner.setText((Integer.valueOf(holder.spinner.getText().toString())) + 1 + "");
                                    }else {
                                        Toast.makeText(MyCart.this, "Only "+(snapshot.child("stock").getValue(String.class))+" in stock", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    return false;

                                }
                            });

                            holder.spinner.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    if (!holder.spinner.getText().toString().equals(""))

                                        if (snapshot.child("discount").getValue(String.class) == null || snapshot.child("discount").getValue(String.class).equals("") || snapshot.child("discount").getValue(String.class).equals("0")) {
                                            t = t - (Integer.valueOf(snapshot.child("price").getValue(String.class)) * Integer.valueOf(holder.spinner.getText().toString()));

                                        } else {
                                            double am = (Integer.valueOf(snapshot.child("price").getValue(String.class))) - (((Double.valueOf(snapshot.child("discount").getValue(String.class))) * 0.01) * (Integer.valueOf(snapshot.child("price").getValue(String.class))));
                                            t = t - (am) * Integer.valueOf(holder.spinner.getText().toString());


                                        }
                                    //t=t-(Integer.valueOf(snapshot.child("price").getValue(String.class))*Integer.valueOf(holder.spinner.getText().toString()));
                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    if (!holder.spinner.getText().toString().equals("")) {
                                        if (snapshot.child("discount").getValue(String.class) == null || snapshot.child("discount").getValue(String.class).equals("") || snapshot.child("discount").getValue(String.class).equals("0")) {
                                            t = t + (Integer.valueOf(snapshot.child("price").getValue(String.class)) * Integer.valueOf(holder.spinner.getText().toString()));

                                        } else {
                                            double am = (Integer.valueOf(snapshot.child("price").getValue(String.class))) - (((Double.valueOf(snapshot.child("discount").getValue(String.class))) * 0.01) * (Integer.valueOf(snapshot.child("price").getValue(String.class))));
                                            t = t + (am) * Integer.valueOf(holder.spinner.getText().toString());


                                        }
                                    }
                                    //t=t + (Integer.valueOf(snapshot.child("price").getValue(String.class)) * Integer.valueOf(holder.spinner.getText().toString()));


                                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                    total.setText(format.format(new BigDecimal(String.valueOf(t))));

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });

                            if (snapshot.child("discount").getValue(String.class) == null || snapshot.child("discount").getValue(String.class).equals("") || snapshot.child("discount").getValue(String.class).equals("0")) {
                                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                holder.price.setText(format.format(new BigDecimal(snapshot.child("price").getValue(String.class))));

                            } else {
                                double am = (Integer.valueOf(snapshot.child("price").getValue(String.class))) - (((Double.valueOf(snapshot.child("discount").getValue(String.class))) * 0.01) * (Integer.valueOf(snapshot.child("price").getValue(String.class))));
                                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                holder.price.setText(format.format(new BigDecimal(String.valueOf(am))));


                            }

                            holder.name.setText(snapshot.child("name").getValue(String.class));
                            holder.category.setText(snapshot.child("category").getValue(String.class));


                            if (snapshot.child("discount").getValue(String.class) == null || snapshot.child("discount").getValue(String.class).equals("") || snapshot.child("discount").getValue(String.class).equals("0")) {
                                t = t + (Integer.valueOf(snapshot.child("price").getValue(String.class)) * Integer.valueOf(holder.spinner.getText().toString()));

                            } else {
                                double am = (Integer.valueOf(snapshot.child("price").getValue(String.class))) - (((Double.valueOf(snapshot.child("discount").getValue(String.class))) * 0.01) * (Integer.valueOf(snapshot.child("price").getValue(String.class))));
                                t = t + (am) * Integer.valueOf(holder.spinner.getText().toString());


                            }
                            //t=t+Integer.valueOf(snapshot.child("price").getValue(String.class)) * Integer.valueOf(holder.spinner.getText().toString());

                            if (position == (firebaseRecyclerAdapter.getItemCount() - 1)) {
                                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                total.setText(format.format(new BigDecimal(String.valueOf(t))));
                            }

                            snapshot.getRef().child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {

                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            Glide.with(MyCart.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
                                            break;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            holder.remove.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MyCart.this);
                                    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            firebaseRecyclerAdapter.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //t=t-((Integer.valueOf(snapshot.child("price").getValue(String.class))) * (Integer.valueOf(holder.spinner.getText().toString())));
                                                        firebaseRecyclerAdapter.notifyDataSetChanged();
                                                        t = 0;
                                                        Toast.makeText(MyCart.this, "Item Removed From Cart", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(MyCart.this, "Some error occured", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).setTitle("Remove Item From Cart?");

                                    builder.show();


                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Favourites").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {

                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.child("itemid").getValue(String.class).equals(firebaseRecyclerAdapter.getItem(position).getImage())) {
                                            l++;
                                        }
                                    }

                                    if (l == 0) {


                                        final DatabaseReference fromPath = firebaseRecyclerAdapter.getRef(position);
                                        final DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Favourites").child(UUID.randomUUID().toString());

                                        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                                l = 0;

                                                toPath.setValue(dataSnapshot1.getValue(), new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(@androidx.annotation.Nullable final DatabaseError databaseError, @NonNull final DatabaseReference databaseReference) {
                                                        if (databaseError != null) {
                                                            System.out.println("Copy failed");
                                                            Toast.makeText(MyCart.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            fromPath.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        firebaseRecyclerAdapter.notifyDataSetChanged();
                                                                        Snackbar snackBar = Snackbar.make(constraintLayout, "Item Moved To Wishlist", Snackbar.LENGTH_LONG).setAction("Go To Wishlist", new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View view) {
                                                                                Intent intent = new Intent(MyCart.this, MyWishlist.class);
                                                                                startActivity(intent);
                                                                                customType(MyCart.this, "fadein-to-fadeout");
                                                                            }
                                                                        });
                                                                        snackBar.setActionTextColor(getColor(R.color.colorPrimary));
                                                                        snackBar.show();

                                                                    } else {
                                                                        Toast.makeText(MyCart.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    } else {
                                        Toast.makeText(MyCart.this, "Item Already In Wishlist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartitem, parent, false);
                return new CartViewHolder(view);
            }
        };

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyCart.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);


        buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ids.clear();
                quans.clear();
                for (int j = 0; j < firebaseRecyclerAdapter.getItemCount(); j++) {
                    ids.add(firebaseRecyclerAdapter.getItem(j).getImage());
                    View view1 = linearLayoutManager.findViewByPosition(j);
                    EditText editText = view1.findViewById(R.id.spinner);
                    quans.add(editText.getText().toString());

                    if (j == (firebaseRecyclerAdapter.getItemCount() - 1)) {
                        Intent intent = new Intent(MyCart.this, Addresses.class);
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST", (Serializable) ids);
                        intent.putExtra("BUNDLE", args);

                        Bundle args2 = new Bundle();
                        args2.putSerializable("ARRAYLIST2", (Serializable) quans);
                        intent.putExtra("BUNDLE2", args2);

                        intent.putExtra("total", String.valueOf(t));
                        startActivity(intent);
                        customType(MyCart.this, "left-to-right");
                    }
                }


            }
        });

    }

    private class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, category, add, remove, delivery, price;
        EditText spinner;
        ImageView plus, minus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.itemimage);
            name = itemView.findViewById(R.id.itemname);
            category = itemView.findViewById(R.id.itemcategory);
            price = itemView.findViewById(R.id.itemprice);
            add = itemView.findViewById(R.id.addtowithlist);
            remove = itemView.findViewById(R.id.remove);
            delivery = itemView.findViewById(R.id.delivery);
            spinner = itemView.findViewById(R.id.spinner);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
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
        customType(MyCart.this, "fadein-to-fadeout");
    }
}