package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class MyWishlist extends AppCompatActivity {

    FirebaseRecyclerAdapter<itemdetails, CartViewHolder> firebaseRecyclerAdapter;
    RecyclerView recyclerView;
    ConstraintLayout constraintLayout;
    int l=0;
    ImageView imageView,back;
    CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wishlist);

        recyclerView = findViewById(R.id.wishlistreycview);
        constraintLayout=findViewById(R.id.constraintLayout);
        circularProgressBar=findViewById(R.id.circularProgressBar);
        imageView=findViewById(R.id.nowishlist);
        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Favourites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                circularProgressBar.setVisibility(View.GONE);
                if(snapshot.exists())
                {

                }
                else {
                    imageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Favourites");

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
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                itemdetails itemdetails = snapshot.getValue(com.example.itshop.itemdetails.class);

                                holder.name.setText(itemdetails.getName());
                                holder.rating.setText(itemdetails.getRating());

                                snapshot.getRef().child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot:snapshot.getChildren())
                                        {
                                            Glide.with(MyWishlist.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
                                            break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                                if (snapshot.child("discount").getValue(String.class) == null || snapshot.child("discount").getValue(String.class).equals("") || snapshot.child("discount").getValue(String.class).equals("0")) {
                                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                    holder.price.setText(format.format(new BigDecimal(snapshot.child("price").getValue(String.class))));

                                } else {
                                    double am = (Integer.valueOf(snapshot.child("price").getValue(String.class))) - (((Integer.valueOf(snapshot.child("discount").getValue(String.class))) * 0.01) * (Integer.valueOf(snapshot.child("price").getValue(String.class))));
                                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                    holder.price.setText(format.format(new BigDecimal(String.valueOf(am))));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                        holder.move.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {


                                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot:snapshot.getChildren())
                                        {
                                            if(dataSnapshot.child("itemid").getValue(String.class).equals(firebaseRecyclerAdapter.getItem(position).getImage()))
                                            {
                                                l++;
                                            }
                                        }

                                        if(l==0)
                                        {


                                            final DatabaseReference fromPath = firebaseRecyclerAdapter.getRef(position);
                                            final DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(UUID.randomUUID().toString());

                                            fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot1) {
                                                    l=0;



                                                    toPath.setValue(dataSnapshot1.getValue(), new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@androidx.annotation.Nullable final DatabaseError databaseError, @NonNull final DatabaseReference databaseReference) {
                                                            if (databaseError != null) {
                                                                System.out.println("Copy failed");
                                                                Toast.makeText(MyWishlist.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                fromPath.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful())
                                                                        {
                                                                            firebaseRecyclerAdapter.notifyDataSetChanged();
                                                                            Snackbar snackBar = Snackbar.make(constraintLayout, "Item Moved To  Cart", Snackbar.LENGTH_LONG).setAction("Go To Cart", new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View view) {
                                                                                    Intent intent = new Intent(MyWishlist.this, MyCart.class);
                                                                                    startActivity(intent);
                                                                                    customType(MyWishlist.this, "fadein-to-fadeout");
                                                                                }
                                                                            });
                                                                            snackBar.setActionTextColor(getColor(R.color.colorPrimary));
                                                                            snackBar.show();

                                                                        }
                                                                        else
                                                                        {
                                                                            Toast.makeText(MyWishlist.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();

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

                                        }
                                        else {
                                            Toast.makeText(MyWishlist.this, "Item Already In Cart", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });


                        holder.remove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MyWishlist.this);
                                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        firebaseRecyclerAdapter.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    firebaseRecyclerAdapter.notifyDataSetChanged();
                                                    Toast.makeText(MyWishlist.this, "Item Removed From Wishlist", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(MyWishlist.this, "Some error occured", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).setTitle("Remove Item From Wishlist?");

                                builder.show();
                            }
                        });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlistitem, parent, false);
                return new CartViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(MyWishlist.this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    private class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, rating, price, move, remove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.viewitemimage);
            name = itemView.findViewById(R.id.viewitemname);
            price = itemView.findViewById(R.id.viewitemprice);
            move = itemView.findViewById(R.id.movetocart);
            remove = itemView.findViewById(R.id.remove);
            rating = itemView.findViewById(R.id.viewitemrating);
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
        customType(MyWishlist.this,"fadein-to-fadeout");

    }
}