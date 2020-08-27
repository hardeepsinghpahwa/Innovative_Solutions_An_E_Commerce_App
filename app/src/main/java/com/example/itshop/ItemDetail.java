package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class ItemDetail extends AppCompatActivity {

    String itemid, total;
    private List<String> SliderItems;
    SliderView sliderView;
    RecyclerView inforecyclerview;
    List<itemdetails> iteminfo;
    LikeButton fav;
    TextView specifications, stock;
    NestedScrollView nestedScrollView;
    RatingReviews ratingReviews;
    ImageView back;
    CircularProgressBar circularProgressBar;
    TextView name, rating, price, spectext, rattext, discount, discountpercent;
    TextView addtocart, buynow;
    ConstraintLayout constraintLayout;
    int i;
    RelativeLayout cart;
    CardView cardView8, cardView9;
    TextView itemsincart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        itemid = getIntent().getStringExtra("itemid");

        sliderView = findViewById(R.id.imageSlider);
        inforecyclerview = findViewById(R.id.inforecyclerview);
        fav = findViewById(R.id.favourite);
        specifications = findViewById(R.id.specifications);
        ratingReviews = findViewById(R.id.rating_reviews);
        name = findViewById(R.id.viewitemname);
        rating = findViewById(R.id.viewitemrating);
        price = findViewById(R.id.viewitemprice);
        back = findViewById(R.id.back);
        spectext = findViewById(R.id.specificationtext);
        rattext = findViewById(R.id.ratingtext);
        nestedScrollView = findViewById(R.id.nested);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        addtocart = findViewById(R.id.addtocart);
        buynow = findViewById(R.id.buynow);
        itemsincart = findViewById(R.id.itemsincart);
        cart = findViewById(R.id.cart);
        discount = findViewById(R.id.discount);
        discountpercent = findViewById(R.id.discountpercent);
        stock = findViewById(R.id.stock);
        cardView8 = findViewById(R.id.cardView8);
        cardView9 = findViewById(R.id.cardView9);
        constraintLayout = findViewById(R.id.constraintLayout9);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemDetail.this, MyCart.class);
                startActivity(intent);
                customType(ItemDetail.this, "fadein-to-fadeout");

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Items").child(itemid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {

                    if (!snapshot.child("stock").getValue(String.class).equals("0")) {
                    constraintLayout.setVisibility(View.VISIBLE);
                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Favourites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fav.setEnabled(true);
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("itemid").getValue(String.class).equals(itemid)) {
                            fav.setLiked(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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


        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                i = 0;
                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.child("itemid").getValue(String.class).equals(itemid)) {
                                i++;
                                Toast.makeText(ItemDetail.this, "Item Already in Cart", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                        if (i == 0) {
                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(UUID.randomUUID().toString()).child("itemid").setValue(itemid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar snackBar = Snackbar.make(view, "Item Added To  Cart", Snackbar.LENGTH_LONG).setAction("Go To Cart", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(ItemDetail.this, MyCart.class);
                                            startActivity(intent);
                                            customType(ItemDetail.this, "fadein-to-fadeout");
                                        }
                                    });
                                    snackBar.setActionTextColor(getColor(R.color.colorPrimary));
                                    snackBar.show();
                                }
                            });
                        }
                    }}

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        fav.setEnabled(true);

        fav.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Favourites").child(UUID.randomUUID().toString()).child("itemid").setValue(itemid).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ItemDetail.this, "Added Into WishList", Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void unLiked(LikeButton likeButton) {

                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Favourites").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())
                        {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.child("itemid").getValue(String.class).equals(itemid)) {
                                dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ItemDetail.this, "Removed From WishList", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ItemDetail.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Items").child(itemid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    circularProgressBar.setVisibility(View.GONE);
                    specifications.setText(snapshot.child("specifications").getValue(String.class));
                    name.setText(snapshot.child("name").getValue(String.class));
                    rating.setText(snapshot.child("rating").getValue(String.class));
                    rating.setVisibility(View.VISIBLE);

                    String st = snapshot.child("stock").getValue(String.class);

                    if (Double.valueOf(st) > 5) {
                        stock.setText("In Stock");
                        stock.setTextColor(getColor(R.color.green));
                    } else if (st.equals("0")) {
                        stock.setText("Currently Unavailable");
                        stock.setTextColor(getColor(R.color.red));
                    } else {
                        stock.setText("Only " + st + " left in stock");
                        stock.setTextColor(getColor(R.color.red));
                    }

                    if (snapshot.child("discount").getValue(String.class) == null || snapshot.child("discount").getValue(String.class).equals("") || snapshot.child("discount").getValue(String.class).equals("0")) {
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        price.setText(format.format(new BigDecimal(snapshot.child("price").getValue(String.class))));
                        total = snapshot.child("price").getValue(String.class);
                    } else {
                        double am = (Double.valueOf(snapshot.child("price").getValue(String.class))) - (((Double.valueOf(snapshot.child("discount").getValue(String.class))) * 0.01) * (Double.valueOf(snapshot.child("price").getValue(String.class))));
                        total = String.valueOf(am);
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        price.setText(format.format(new BigDecimal(String.valueOf(am))));
                        discount.setText(format.format(new BigDecimal(snapshot.child("price").getValue(String.class))));
                        discount.setVisibility(View.VISIBLE);
                        discountpercent.setVisibility(View.VISIBLE);
                        discountpercent.setText("(" + snapshot.child("discount").getValue(String.class) + "% discount" + ")");
                        discount.setPaintFlags(discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    }

                    rattext.setVisibility(View.VISIBLE);
                    spectext.setVisibility(View.VISIBLE);

                    buynow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ItemDetail.this, Addresses.class);
                            intent.putExtra("price", snapshot.child("price").getValue(String.class));
                            intent.putExtra("itemid", itemid);
                            intent.putExtra("total", total);
                            startActivity(intent);
                            customType(ItemDetail.this, "left-to-right");
                        }
                    });

                    snapshot.child("images").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()) {

                                SliderItems = new ArrayList<>();

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    SliderItems.add(dataSnapshot.child("image").getValue(String.class));
                                }

                                SliderAdapter adapter = new SliderAdapter(ItemDetail.this, SliderItems);

                                sliderView.setSliderAdapter(adapter);
                                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                                sliderView.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    iteminfo = new ArrayList<>();

                    int colors[] = new int[]{
                            Color.parseColor("#0e9d58"),
                            Color.parseColor("#bfd047"),
                            Color.parseColor("#ffc105"),
                            Color.parseColor("#ef7e14"),
                            Color.parseColor("#d36259")};

                    int raters[] = new int[]{
                            new Random().nextInt(100),
                            new Random().nextInt(100),
                            new Random().nextInt(100),
                            new Random().nextInt(100),
                            new Random().nextInt(100)
                    };

                    ratingReviews.createRatingBars(100, BarLabels.STYPE5, colors, raters);

                    if (snapshot.child("replacement").exists()) {
                        if (!snapshot.child("replacement").getValue(String.class).equals("0")) {
                            iteminfo.add(new itemdetails(snapshot.child("replacement").getValue(String.class) + " replacement", R.drawable.replace));
                        } else {
                        iteminfo.add(new itemdetails("No Replacement", R.drawable.replace));
                    }
                }

                if (snapshot.child("returnable").exists()) {
                    if (!snapshot.child("returnable").getValue(String.class).equals("0")) {
                        iteminfo.add(new itemdetails(snapshot.child("returnable").getValue(String.class) + " return", R.drawable.returnable));
                    } else {
                        iteminfo.add(new itemdetails("Non returnable", R.drawable.returnable));
                    }
                }

                if (snapshot.child("warranty").exists()) {
                    if (!snapshot.child("warranty").getValue(String.class).equals("0")) {
                        iteminfo.add(new itemdetails(snapshot.child("warranty").getValue(String.class) + " warranty", R.drawable.warranty));
                    } else {
                        iteminfo.add(new itemdetails("No Warranty", R.drawable.warranty));
                    }

                }
                    inforecyclerview.setLayoutManager(new GridLayoutManager(ItemDetail.this, 4));
                    inforecyclerview.setAdapter(new InfoAdapter(iteminfo));
                    ViewCompat.setNestedScrollingEnabled(inforecyclerview, false);
                } else {
                    Toast.makeText(ItemDetail.this, "Data has been Changed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


    }

    public class SliderAdapter extends
            SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

        private Context context;
        private List<String> items;

        public SliderAdapter(Context context, List<String> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslideritem, null);
            return new SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

            String sliderItem = items.get(position);

            Glide.with(viewHolder.itemView)
                    .load(sliderItem)
                    .into(viewHolder.imageViewBackground);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ItemDetail.this, ViewImage.class);
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST", (Serializable) items);
                    intent.putExtra("BUNDLE", args);
                    intent.putExtra("posi", position);
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getCount() {
            return items.size();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            View itemView;
            ImageView imageViewBackground;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.slideritemimage);
                this.itemView = itemView;
            }
        }

    }

    class InfoAdapter extends RecyclerView.Adapter<InfoViewHolder> {

        List<itemdetails> items;

        public InfoAdapter(List<itemdetails> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.infoitemlayout, parent, false);
            return new InfoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {

            itemdetails itemdetails = items.get(position);
            holder.textView.setText(itemdetails.getName());
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(ItemDetail.this, itemdetails.getDraw()));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }


    class InfoViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.infoimage);
            textView = itemView.findViewById(R.id.infotext);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(ItemDetail.this, "right-to-left");

    }
}