package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.itshop.Fragments.MyOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class ItemDetail extends AppCompatActivity {

    String itemid, total;
    private List<String> SliderItems;
    SliderView sliderView;
    RecyclerView inforecyclerview, reviewsrecyclerview;
    CarouselView carouselView;
    RatingBar ratingBar;
    List<itemdetails> iteminfo;
    LikeButton fav;
    TextView specifications, stock;
    NestedScrollView nestedScrollView;
    ImageView back;
    Double rat;
    CircularProgressBar circularProgressBar;
    TextView name, rating, ratingsnum, price, spectext, rattext, discount, discountpercent, rating2, ratingnum2, category;
    TextView addtocart, buynow;
    ConstraintLayout constraintLayout;
    int i;
    RelativeLayout cart;
    CardView cardView8, cardView9;
    TextView itemsincart, seeall;
    ArrayList<itemdetails> similar;
    ArrayList<String> ids;
    FirebaseRecyclerAdapter<itemreview, ReviewViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        itemid = getIntent().getStringExtra("itemid");

        sliderView = findViewById(R.id.imageSlider);
        inforecyclerview = findViewById(R.id.inforecyclerview);
        fav = findViewById(R.id.favourite);
        specifications = findViewById(R.id.specifications);
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
        ratingsnum = findViewById(R.id.ratingsnum);
        reviewsrecyclerview = findViewById(R.id.reviewsrecyview);
        carouselView = findViewById(R.id.similarproductscyview);
        seeall = findViewById(R.id.seeall);
        ratingnum2 = findViewById(R.id.ratingnum);
        rating2 = findViewById(R.id.rating);
        ratingBar = findViewById(R.id.ratingBar);
        similar = new ArrayList<>();
        category = findViewById(R.id.category);
        ids = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Reviews").child(itemid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 3) {
                    seeall.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        seeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RecyclerView recyclerView;

                final Dialog dialog = new Dialog(ItemDetail.this);
                dialog.setContentView(R.layout.allreviewsdialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setWindowAnimations(R.style.AppTheme_UpDown);

                recyclerView = dialog.findViewById(R.id.allreviewsrecyview);

                Query query = FirebaseDatabase.getInstance().getReference().child("Reviews").child(itemid);

                FirebaseRecyclerOptions<itemreview> options = new FirebaseRecyclerOptions.Builder<itemreview>()
                        .setQuery(query, new SnapshotParser<itemreview>() {
                            @NonNull
                            @Override
                            public itemreview parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new itemreview(snapshot.getKey(), snapshot.child("review").getValue(String.class), snapshot.child("rating").getValue(String.class));
                            }
                        }).build();

                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<itemreview, ReviewViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ReviewViewHolder holder, int position, @NonNull itemreview model) {

                        holder.rating.setText(model.getRating());
                        if (model.getRating() != null)
                            holder.review.setText(model.getReview());

                        if (model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            holder.edit.setVisibility(View.VISIBLE);
                        }

                        holder.edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                final Dialog dialog = new Dialog(ItemDetail.this);
                                dialog.setContentView(R.layout.ratingdialog);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setWindowAnimations(R.style.AppTheme_UpDown);

                                final RecyclerView recyclerView1;

                                recyclerView1 = dialog.findViewById(R.id.raterecyview);


                                ids.clear();
                                ids.add(itemid);

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ItemDetail.this, LinearLayoutManager.HORIZONTAL, false);
                                recyclerView1.setLayoutManager(linearLayoutManager);
                                recyclerView1.setAdapter(new ItemsAdapter(ids, dialog));

                                dialog.show();

                            }
                        });


                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(model.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                holder.name.setText(snapshot.child("name").getValue(String.class));

                                Glide.with(getApplicationContext()).load(snapshot.child("profilepic").getValue(String.class)).into(holder.imageView);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewitem, parent, false);
                        return new ReviewViewHolder(v);
                    }
                };

                recyclerView.setLayoutManager(new LinearLayoutManager(ItemDetail.this));
                recyclerView.setAdapter(firebaseRecyclerAdapter);

                firebaseRecyclerAdapter.startListening();

                dialog.show();
            }
        });

        Query query = FirebaseDatabase.getInstance().getReference().child("Reviews").child(itemid).limitToFirst(3);

        FirebaseRecyclerOptions<itemreview> options = new FirebaseRecyclerOptions.Builder<itemreview>()
                .setQuery(query, new SnapshotParser<itemreview>() {
                    @NonNull
                    @Override
                    public itemreview parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new itemreview(snapshot.getKey(), snapshot.child("review").getValue(String.class), snapshot.child("rating").getValue(String.class));
                    }
                }).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<itemreview, ReviewViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ReviewViewHolder holder, int position, @NonNull itemreview model) {

                holder.rating.setText(model.getRating());
                if (model.getRating() != null)
                    holder.review.setText(model.getReview());

                if (model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    holder.edit.setVisibility(View.VISIBLE);
                }

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        final Dialog dialog = new Dialog(ItemDetail.this);
                        dialog.setContentView(R.layout.ratingdialog);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setWindowAnimations(R.style.AppTheme_UpDown);

                        final RecyclerView recyclerView1;

                        recyclerView1 = dialog.findViewById(R.id.raterecyview);


                        ids.clear();
                        ids.add(itemid);

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ItemDetail.this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerView1.setLayoutManager(linearLayoutManager);
                        recyclerView1.setAdapter(new ItemsAdapter(ids, dialog));

                        dialog.show();

                    }
                });

                FirebaseDatabase.getInstance().getReference().child("Profiles").child(model.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        holder.name.setText(snapshot.child("name").getValue(String.class));

                        Glide.with(getApplicationContext()).load(snapshot.child("profilepic").getValue(String.class)).into(holder.imageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewitem, parent, false);
                return new ReviewViewHolder(v);
            }
        };

        reviewsrecyclerview.setLayoutManager(new LinearLayoutManager(ItemDetail.this));
        reviewsrecyclerview.setAdapter(firebaseRecyclerAdapter);


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
                if (snapshot.exists()) {

                    if (!snapshot.child("stock").getValue(String.class).equals("0")) {
                        constraintLayout.setVisibility(View.VISIBLE);
                    }
                }
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
                        }

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

                        if (snapshot.exists()) {
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
                            }
                        }
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

                    if (snapshot.child("rating").getValue(String.class) != null) {
                        rating2.setText(snapshot.child("rating").getValue(String.class));
                        ratingBar.setRating(Float.valueOf(Objects.requireNonNull(snapshot.child("rating").getValue(String.class))));
                    } else {
                        rating2.setText("0");
                        ratingBar.setRating(0);
                        ratingnum2.setText("(0 ratings)");
                    }
                    circularProgressBar.setVisibility(View.GONE);
                    specifications.setText(snapshot.child("specifications").getValue(String.class));
                    name.setText(snapshot.child("name").getValue(String.class));
                    category.setText(snapshot.child("category").getValue(String.class));

                    if (snapshot.child("rating").getValue(String.class) != null && !snapshot.child("rating").getValue(String.class).equals("")) {
                        rating.setText(snapshot.child("rating").getValue(String.class));
                        rating.setVisibility(View.VISIBLE);


                        FirebaseDatabase.getInstance().getReference().child("Reviews").child(itemid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() == 0) {
                                    ratingsnum.setText("(No ratings)");
                                    ratingnum2.setText("(0 ratings)");
                                    ratingsnum.setVisibility(View.VISIBLE);


                                } else {
                                    ratingsnum.setText("(" + snapshot.getChildrenCount() + " ratings" + ")");
                                    ratingsnum.setVisibility(View.VISIBLE);
                                    ratingnum2.setText("(" + snapshot.getChildrenCount() + " ratings" + ")");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }


                    FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot snapshot2) {
                            for (DataSnapshot snapshot1 : snapshot2.getChildren()) {

                                if (!snapshot1.getKey().equals(itemid) && snapshot1.child("category").getValue(String.class).equals(snapshot.child("category").getValue(String.class))) {

                                    itemdetails itemdetails = new itemdetails(snapshot1.child("image").getValue(String.class), snapshot1.child("name").getValue(String.class), snapshot1.getKey(), snapshot1.child("price").getValue(String.class), snapshot1.child("discount").getValue(String.class));

                                    similar.add(itemdetails);
                                }
                            }

                            CarouselView carouselView = findViewById(R.id.similarproductscyview);

                            carouselView.setSize(similar.size());
                            carouselView.setResource(R.layout.image_carousel_item);
                            carouselView.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
                            carouselView.setCarouselOffset(OffsetType.START);
                            carouselView.hideIndicator(true);
                            carouselView.setCarouselViewListener(new CarouselViewListener() {
                                @Override
                                public void onBindView(View view, final int position) {
                                    // Example here is setting up a full image carousel
                                    final ImageView imageView = view.findViewById(R.id.carouselimg);
                                    TextView name = view.findViewById(R.id.itemname);
                                    TextView price = view.findViewById(R.id.itemprice);
                                    TextView discounttext = view.findViewById(R.id.discounttext);

                                    if (similar.get(position).getDiscount() == null || similar.get(position).getDiscount().equals("") || similar.get(position).getDiscount().equals("0")) {
                                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                        price.setText(format.format(new BigDecimal(similar.get(position).getPrice())));

                                    } else {
                                        double am = (Integer.valueOf(similar.get(position).getPrice())) - (((Integer.valueOf(similar.get(position).getDiscount())) * 0.01) * (Integer.valueOf(similar.get(position).getPrice())));
                                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                        price.setText(format.format(new BigDecimal(String.valueOf(am))));
                                        discounttext.setText(format.format(new BigDecimal(similar.get(position).getPrice())));
                                        discounttext.setVisibility(View.VISIBLE);
                                        discounttext.setPaintFlags(discounttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                    }

                                    name.setText(similar.get(position).getName());

                                    FirebaseDatabase.getInstance().getReference().child("Items").child(similar.get(position).getRating()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                Glide.with(ItemDetail.this).load(dataSnapshot.child("image").getValue(String.class)).into(imageView);
                                                break;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(ItemDetail.this, ItemDetail.class);
                                            intent.putExtra("itemid", similar.get(position).getRating());
                                            startActivity(intent);
                                            customType(ItemDetail.this, "left-to-right");

                                        }
                                    });

                                }
                            });

                            carouselView.show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


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

                            if (snapshot.exists()) {

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

    private class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView name, review, rating;
        ImageView imageView, edit;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            review = itemView.findViewById(R.id.review);
            rating = itemView.findViewById(R.id.rating);
            edit = itemView.findViewById(R.id.editreview);
            imageView = itemView.findViewById(R.id.image);

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
                    holder.num.setVisibility(View.GONE);

                    holder.circularProgressBar.setVisibility(View.GONE);

                    FirebaseDatabase.getInstance().getReference().child("Reviews").child(itemids.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
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

                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Glide.with(ItemDetail.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
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
                                    Toast.makeText(ItemDetail.this, "Provide some rating first", Toast.LENGTH_SHORT).show();
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
                                                rat = Double.valueOf(0);

                                                FirebaseDatabase.getInstance().getReference().child("Reviews").child(itemids.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                rat = rat + Double.valueOf(dataSnapshot.child("rating").getValue(String.class));
                                                            }

                                                            Map map1 = new HashMap();
                                                            map1.put("rating", String.valueOf(rat / snapshot.getChildrenCount()));
                                                            FirebaseDatabase.getInstance().getReference().child("Items").child(itemids.get(position)).updateChildren(map1).addOnCompleteListener(new OnCompleteListener() {
                                                                @Override
                                                                public void onComplete(@NonNull Task task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(ItemDetail.this, "Thanks For The Rating", Toast.LENGTH_SHORT).show();
                                                                        holder.rate.setText("Edit Rating");
                                                                        holder.progressBar.setVisibility(View.GONE);

                                                                    } else {
                                                                        Toast.makeText(ItemDetail.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                                                        holder.progressBar.setVisibility(View.GONE);

                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            } else {
                                                Toast.makeText(ItemDetail.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
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

    class ItemViewHolder extends RecyclerView.ViewHolder {

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
            progressBar = itemView.findViewById(R.id.progressbar);
        }

    }
}