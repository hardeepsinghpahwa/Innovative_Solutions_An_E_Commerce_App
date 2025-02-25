package com.example.itshop.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.itshop.ItemDetail;
import com.example.itshop.R;
import com.example.itshop.ViewItems;
import com.example.itshop.itemdetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    SliderView recyclerView;
    ArrayList<String> sliderImages;
    ArrayList<String> texts;
    NestedScrollView nestedScrollView;
    TextView newproducts, popular, customtext1, customtext2, customtext3, customtext4, under5ktext, under1ktext;
    CircularProgressBar circularProgressBar;
    ArrayList<itemdetails> newlyadded, popularimgs, customs1, customs2, u5ks, u1ks, customs3, customs4;
    CarouselView carouselView, carouselView2, customcarousel1, customcarousel2, carouselView3, carouselView4, customcarousel3, customcarousel4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        newproducts = v.findViewById(R.id.newproductstext);
        popular = v.findViewById(R.id.populartext);
        recyclerView = v.findViewById(R.id.homerecyclerview);
        circularProgressBar = v.findViewById(R.id.circularProgressBar);

        carouselView = v.findViewById(R.id.carousel1);
        carouselView2 = v.findViewById(R.id.carousel2);
        customcarousel1 = v.findViewById(R.id.customcarousel1);
        customcarousel2 = v.findViewById(R.id.customcarousel2);
        customtext1 = v.findViewById(R.id.customtext1);
        customtext2 = v.findViewById(R.id.customtext2);
        customtext3 = v.findViewById(R.id.customtext3);
        customtext4 = v.findViewById(R.id.customtext4);
        nestedScrollView = v.findViewById(R.id.scrollView2);
        carouselView3 = v.findViewById(R.id.carousel3);
        under5ktext = v.findViewById(R.id.under5ktext);
        carouselView4 = v.findViewById(R.id.carousel4);
        under1ktext = v.findViewById(R.id.under1ktext);
        nestedScrollView.setSmoothScrollingEnabled(true);
        customcarousel3 = v.findViewById(R.id.customcarousel3);
        customcarousel4 = v.findViewById(R.id.customcarousel4);


        FirebaseDatabase.getInstance().getReference().child("HomeRecyclerView").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                popular.setVisibility(View.VISIBLE);
                newproducts.setVisibility(View.VISIBLE);
                circularProgressBar.setVisibility(View.GONE);

                carouselView.scheduleLayoutAnimation();
                carouselView2.scheduleLayoutAnimation();

                sliderImages = new ArrayList<>();
                texts = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    sliderImages.add(dataSnapshot.child("image").getValue(String.class));
                    texts.add(dataSnapshot.child("text").getValue(String.class));
                }

                SliderAdapter adapter = new SliderAdapter(getActivity(), sliderImages, texts);

                recyclerView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                recyclerView.setAutoCycle(true);
                recyclerView.setScrollTimeInSec(4);
                recyclerView.startAutoCycle();

                if (sliderImages.size() > 0) {

                    recyclerView.setSliderAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Custom1

        FirebaseDatabase.getInstance().getReference().child("Custom1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot1) {

                if (snapshot1.exists()) {
                    if (snapshot1.child("show").getValue(String.class).equals("1")) {
                        FirebaseDatabase.getInstance().getReference().child("Items").orderByChild("category").equalTo(snapshot1.child("category").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                customtext1.setText(snapshot1.child("category").getValue(String.class));
                                customtext1.setVisibility(View.VISIBLE);
                                customcarousel1.setVisibility(View.VISIBLE);

                                customs1 = new ArrayList<>();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    itemdetails itemdetails = new itemdetails(snapshot1.child("image").getValue(String.class), snapshot1.child("name").getValue(String.class), snapshot1.getKey(), snapshot1.child("price").getValue(String.class), snapshot1.child("discount").getValue(String.class));

                                    customs1.add(itemdetails);
                                }

                                customcarousel1.setSize(customs1.size());
                                customcarousel1.setResource(R.layout.image_carousel_item);
                                customcarousel1.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
                                customcarousel1.setCarouselOffset(OffsetType.START);
                                customcarousel1.hideIndicator(true);
                                customcarousel1.setCarouselViewListener(new CarouselViewListener() {
                                    @Override
                                    public void onBindView(View view, final int position) {
                                        // Example here is setting up a full image carousel
                                        final ImageView imageView = view.findViewById(R.id.carouselimg);
                                        TextView name = view.findViewById(R.id.itemname);
                                        TextView price = view.findViewById(R.id.itemprice);
                                        TextView discounttext = view.findViewById(R.id.discounttext);

                                        name.setText(customs1.get(position).getName());

                                        if (customs1.get(position).getDiscount() == null || customs1.get(position).getDiscount().equals("") || customs1.get(position).getDiscount().equals("0")) {
                                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                            price.setText(format.format(new BigDecimal(customs1.get(position).getPrice())));

                                        } else {
                                            double am = (Integer.valueOf(customs1.get(position).getPrice())) - (((Integer.valueOf(customs1.get(position).getDiscount())) * 0.01) * (Integer.valueOf(customs1.get(position).getPrice())));
                                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                            price.setText(format.format(new BigDecimal(String.valueOf(am))));
                                            discounttext.setText(format.format(new BigDecimal(customs1.get(position).getPrice())));
                                            discounttext.setVisibility(View.VISIBLE);
                                            discounttext.setPaintFlags(discounttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                        }

                                        FirebaseDatabase.getInstance().getReference().child("Items").child(customs1.get(position).getRating()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Glide.with(getActivity()).load(dataSnapshot.child("image").getValue(String.class)).into(imageView);
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
                                                Intent intent = new Intent(getActivity(), ItemDetail.class);
                                                intent.putExtra("itemid", customs1.get(position).getRating());
                                                startActivity(intent);
                                                customType(getActivity(), "left-to-right");

                                            }
                                        });

                                    }
                                });
                                customcarousel1.show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        customtext1.setVisibility(View.GONE);
                        customcarousel1.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Custom2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot1) {

                if (snapshot1.exists()) {
                    if (snapshot1.child("show").getValue(String.class).equals("1")) {
                        FirebaseDatabase.getInstance().getReference().child("Items").orderByChild("category").equalTo(snapshot1.child("category").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                customtext2.setText(snapshot1.child("category").getValue(String.class));
                                customtext2.setVisibility(View.VISIBLE);
                                customcarousel2.setVisibility(View.VISIBLE);

                                customs2 = new ArrayList<>();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    itemdetails itemdetails = new itemdetails(snapshot1.child("image").getValue(String.class), snapshot1.child("name").getValue(String.class), snapshot1.getKey(), snapshot1.child("price").getValue(String.class), snapshot1.child("discount").getValue(String.class));

                                    customs2.add(itemdetails);
                                }

                                customcarousel2.setSize(customs2.size());
                                customcarousel2.setResource(R.layout.image_carousel_item);
                                customcarousel2.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
                                customcarousel2.setCarouselOffset(OffsetType.START);
                                customcarousel2.hideIndicator(true);
                                customcarousel2.setCarouselViewListener(new CarouselViewListener() {
                                    @Override
                                    public void onBindView(View view, final int position) {
                                        // Example here is setting up a full image carousel
                                        final ImageView imageView = view.findViewById(R.id.carouselimg);
                                        TextView name = view.findViewById(R.id.itemname);
                                        TextView price = view.findViewById(R.id.itemprice);
                                        TextView discounttext = view.findViewById(R.id.discounttext);

                                        name.setText(customs2.get(position).getName());

                                        if (customs2.get(position).getDiscount() == null || customs2.get(position).getDiscount().equals("") || customs2.get(position).getDiscount().equals("0")) {
                                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                            price.setText(format.format(new BigDecimal(customs2.get(position).getPrice())));

                                        } else {
                                            double am = (Integer.valueOf(customs2.get(position).getPrice())) - (((Integer.valueOf(customs2.get(position).getDiscount())) * 0.01) * (Integer.valueOf(customs2.get(position).getPrice())));
                                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                            price.setText(format.format(new BigDecimal(String.valueOf(am))));
                                            discounttext.setText(format.format(new BigDecimal(customs2.get(position).getPrice())));
                                            discounttext.setVisibility(View.VISIBLE);
                                            discounttext.setPaintFlags(discounttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                        }

                                        FirebaseDatabase.getInstance().getReference().child("Items").child(customs2.get(position).getRating()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Glide.with(getActivity()).load(dataSnapshot.child("image").getValue(String.class)).into(imageView);
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
                                                Intent intent = new Intent(getActivity(), ItemDetail.class);
                                                intent.putExtra("itemid", customs2.get(position).getRating());
                                                startActivity(intent);
                                                customType(getActivity(), "left-to-right");

                                            }
                                        });

                                    }
                                });
                                customcarousel2.show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        customtext2.setVisibility(View.GONE);
                        customcarousel2.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Custom3").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot1) {

                if (snapshot1.exists()) {
                    if (snapshot1.child("show").getValue(String.class).equals("1")) {
                        FirebaseDatabase.getInstance().getReference().child("Items").orderByChild("category").equalTo(snapshot1.child("category").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                customtext3.setText(snapshot1.child("category").getValue(String.class));
                                customtext3.setVisibility(View.VISIBLE);
                                customcarousel3.setVisibility(View.VISIBLE);

                                customs3 = new ArrayList<>();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    itemdetails itemdetails = new itemdetails(snapshot1.child("image").getValue(String.class), snapshot1.child("name").getValue(String.class), snapshot1.getKey(), snapshot1.child("price").getValue(String.class), snapshot1.child("discount").getValue(String.class));

                                    customs3.add(itemdetails);
                                }

                                customcarousel3.setSize(customs3.size());
                                customcarousel3.setResource(R.layout.image_carousel_item);
                                customcarousel3.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
                                customcarousel3.setCarouselOffset(OffsetType.START);
                                customcarousel3.hideIndicator(true);
                                customcarousel3.setCarouselViewListener(new CarouselViewListener() {
                                    @Override
                                    public void onBindView(View view, final int position) {
                                        // Example here is setting up a full image carousel
                                        final ImageView imageView = view.findViewById(R.id.carouselimg);
                                        TextView name = view.findViewById(R.id.itemname);
                                        TextView price = view.findViewById(R.id.itemprice);
                                        TextView discounttext = view.findViewById(R.id.discounttext);

                                        name.setText(customs3.get(position).getName());

                                        if (customs3.get(position).getDiscount() == null || customs3.get(position).getDiscount().equals("") || customs3.get(position).getDiscount().equals("0")) {
                                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                            price.setText(format.format(new BigDecimal(customs3.get(position).getPrice())));

                                        } else {
                                            double am = (Integer.valueOf(customs3.get(position).getPrice())) - (((Integer.valueOf(customs3.get(position).getDiscount())) * 0.01) * (Integer.valueOf(customs3.get(position).getPrice())));
                                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                            price.setText(format.format(new BigDecimal(String.valueOf(am))));
                                            discounttext.setText(format.format(new BigDecimal(customs3.get(position).getPrice())));
                                            discounttext.setVisibility(View.VISIBLE);
                                            discounttext.setPaintFlags(discounttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                        }

                                        FirebaseDatabase.getInstance().getReference().child("Items").child(customs3.get(position).getRating()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Glide.with(getActivity()).load(dataSnapshot.child("image").getValue(String.class)).into(imageView);
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
                                                Intent intent = new Intent(getActivity(), ItemDetail.class);
                                                intent.putExtra("itemid", customs3.get(position).getRating());
                                                startActivity(intent);
                                                customType(getActivity(), "left-to-right");

                                            }
                                        });

                                    }
                                });
                                customcarousel3.show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        customtext3.setVisibility(View.GONE);
                        customcarousel3.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Custom4").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot1) {
                if (snapshot1.exists()) {
                    if (snapshot1.child("show").getValue(String.class).equals("1")) {
                        FirebaseDatabase.getInstance().getReference().child("Items").orderByChild("category").equalTo(snapshot1.child("category").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                customtext4.setText(snapshot1.child("category").getValue(String.class));
                                customtext4.setVisibility(View.VISIBLE);
                                customcarousel4.setVisibility(View.VISIBLE);

                                customs4 = new ArrayList<>();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    itemdetails itemdetails = new itemdetails(snapshot1.child("image").getValue(String.class), snapshot1.child("name").getValue(String.class), snapshot1.getKey(), snapshot1.child("price").getValue(String.class), snapshot1.child("discount").getValue(String.class));

                                    customs4.add(itemdetails);
                                }

                                customcarousel4.setSize(customs4.size());
                                customcarousel4.setResource(R.layout.image_carousel_item);
                                customcarousel4.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
                                customcarousel4.setCarouselOffset(OffsetType.START);
                                customcarousel4.hideIndicator(true);
                                customcarousel4.setCarouselViewListener(new CarouselViewListener() {
                                    @Override
                                    public void onBindView(View view, final int position) {
                                        // Example here is setting up a full image carousel
                                        final ImageView imageView = view.findViewById(R.id.carouselimg);
                                        TextView name = view.findViewById(R.id.itemname);
                                        TextView price = view.findViewById(R.id.itemprice);
                                        TextView discounttext = view.findViewById(R.id.discounttext);

                                        name.setText(customs4.get(position).getName());

                                        if (customs4.get(position).getDiscount() == null || customs4.get(position).getDiscount().equals("") || customs4.get(position).getDiscount().equals("0")) {
                                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                            price.setText(format.format(new BigDecimal(customs4.get(position).getPrice())));

                                        } else {
                                            double am = (Integer.valueOf(customs4.get(position).getPrice())) - (((Integer.valueOf(customs4.get(position).getDiscount())) * 0.01) * (Integer.valueOf(customs4.get(position).getPrice())));
                                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                            price.setText(format.format(new BigDecimal(String.valueOf(am))));
                                            discounttext.setText(format.format(new BigDecimal(customs4.get(position).getPrice())));
                                            discounttext.setVisibility(View.VISIBLE);
                                            discounttext.setPaintFlags(discounttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                        }

                                        FirebaseDatabase.getInstance().getReference().child("Items").child(customs4.get(position).getRating()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Glide.with(getActivity()).load(dataSnapshot.child("image").getValue(String.class)).into(imageView);
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
                                                Intent intent = new Intent(getActivity(), ItemDetail.class);
                                                intent.putExtra("itemid", customs4.get(position).getRating());
                                                startActivity(intent);
                                                customType(getActivity(), "left-to-right");

                                            }
                                        });

                                    }
                                });
                                customcarousel4.show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        customtext4.setVisibility(View.GONE);
                        customcarousel4.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Items").orderByKey().limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                newlyadded = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    itemdetails itemdetails = new itemdetails(snapshot1.child("image").getValue(String.class), snapshot1.child("name").getValue(String.class), snapshot1.getKey(), snapshot1.child("price").getValue(String.class), snapshot1.child("discount").getValue(String.class));

                    newlyadded.add(itemdetails);
                }

                Collections.reverse(newlyadded);
                carouselView.setSize(newlyadded.size());
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

                        if (newlyadded.get(position).getDiscount() == null || newlyadded.get(position).getDiscount().equals("") || newlyadded.get(position).getDiscount().equals("0")) {
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(newlyadded.get(position).getPrice())));

                        } else {
                            double am = (Integer.valueOf(newlyadded.get(position).getPrice())) - (((Integer.valueOf(newlyadded.get(position).getDiscount())) * 0.01) * (Integer.valueOf(newlyadded.get(position).getPrice())));
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(String.valueOf(am))));
                            discounttext.setText(format.format(new BigDecimal(newlyadded.get(position).getPrice())));
                            discounttext.setVisibility(View.VISIBLE);
                            discounttext.setPaintFlags(discounttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        }

                        name.setText(newlyadded.get(position).getName());

                        FirebaseDatabase.getInstance().getReference().child("Items").child(newlyadded.get(position).getRating()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Glide.with(getActivity()).load(dataSnapshot.child("image").getValue(String.class)).into(imageView);
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
                                Intent intent = new Intent(getActivity(), ItemDetail.class);
                                intent.putExtra("itemid", newlyadded.get(position).getRating());
                                startActivity(intent);
                                customType(getActivity(), "left-to-right");

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


        FirebaseDatabase.getInstance().getReference().child("Items").limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                popularimgs = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    itemdetails itemdetails = new itemdetails(snapshot1.child("image").getValue(String.class), snapshot1.child("name").getValue(String.class), snapshot1.getKey(), snapshot1.child("price").getValue(String.class), snapshot1.child("discount").getValue(String.class));

                    popularimgs.add(itemdetails);
                }

                Collections.shuffle(popularimgs);
                carouselView2.setSize(popularimgs.size());
                carouselView2.setResource(R.layout.image_carousel_item);
                carouselView2.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
                carouselView2.setCarouselOffset(OffsetType.START);
                carouselView2.hideIndicator(true);
                carouselView2.setCarouselViewListener(new CarouselViewListener() {
                    @Override
                    public void onBindView(View view, final int position) {
                        // Example here is setting up a full image carousel
                        final ImageView imageView = view.findViewById(R.id.carouselimg);
                        TextView name = view.findViewById(R.id.itemname);
                        TextView price = view.findViewById(R.id.itemprice);
                        TextView discounttext = view.findViewById(R.id.discounttext);

                        name.setText(popularimgs.get(position).getName());

                        if (popularimgs.get(position).getDiscount() == null || popularimgs.get(position).getDiscount().equals("") || popularimgs.get(position).getDiscount().equals("0")) {
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(popularimgs.get(position).getPrice())));

                        } else {
                            double am = (Integer.valueOf(popularimgs.get(position).getPrice())) - (((Integer.valueOf(popularimgs.get(position).getDiscount())) * 0.01) * (Integer.valueOf(popularimgs.get(position).getPrice())));
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(String.valueOf(am))));
                            discounttext.setText(format.format(new BigDecimal(popularimgs.get(position).getPrice())));
                            discounttext.setVisibility(View.VISIBLE);
                            discounttext.setPaintFlags(discounttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        }

                        FirebaseDatabase.getInstance().getReference().child("Items").child(popularimgs.get(position).getRating()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Glide.with(getActivity()).load(dataSnapshot.child("image").getValue(String.class)).into(imageView);
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
                                Intent intent = new Intent(getActivity(), ItemDetail.class);
                                intent.putExtra("itemid", popularimgs.get(position).getRating());
                                startActivity(intent);
                                customType(getActivity(), "left-to-right");

                            }
                        });

                    }
                });
                carouselView2.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u5ks = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (Integer.valueOf(Objects.requireNonNull(snapshot1.child("price").getValue(String.class))) < 5000) {
                        itemdetails itemdetails = new itemdetails(snapshot1.child("image").getValue(String.class), snapshot1.child("name").getValue(String.class), snapshot1.getKey(), snapshot1.child("price").getValue(String.class), snapshot1.child("discount").getValue(String.class));

                        u5ks.add(itemdetails);
                    }
                }
                if (u5ks.size() > 0) {
                    under5ktext.setVisibility(View.VISIBLE);
                }
                carouselView3.setSize(u5ks.size());
                carouselView3.setResource(R.layout.image_carousel_item);
                carouselView3.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
                carouselView3.setCarouselOffset(OffsetType.START);
                carouselView3.hideIndicator(true);
                carouselView3.setCarouselViewListener(new CarouselViewListener() {
                    @Override
                    public void onBindView(View view, final int position) {
                        // Example here is setting up a full image carousel
                        final ImageView imageView = view.findViewById(R.id.carouselimg);
                        TextView name = view.findViewById(R.id.itemname);
                        TextView price = view.findViewById(R.id.itemprice);
                        TextView discounttext = view.findViewById(R.id.discounttext);

                        name.setText(u5ks.get(position).getName());

                        if (u5ks.get(position).getDiscount() == null || u5ks.get(position).getDiscount().equals("") || u5ks.get(position).getDiscount().equals("0")) {
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(u5ks.get(position).getPrice())));

                        } else {
                            double am = (Integer.valueOf(u5ks.get(position).getPrice())) - (((Integer.valueOf(u5ks.get(position).getDiscount())) * 0.01) * (Integer.valueOf(u5ks.get(position).getPrice())));
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(String.valueOf(am))));
                            discounttext.setText(format.format(new BigDecimal(u5ks.get(position).getPrice())));
                            discounttext.setVisibility(View.VISIBLE);
                            discounttext.setPaintFlags(discounttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        }

                        FirebaseDatabase.getInstance().getReference().child("Items").child(u5ks.get(position).getRating()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Glide.with(getActivity()).load(dataSnapshot.child("image").getValue(String.class)).into(imageView);
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
                                Intent intent = new Intent(getActivity(), ItemDetail.class);
                                intent.putExtra("itemid", u5ks.get(position).getRating());
                                startActivity(intent);
                                customType(getActivity(), "left-to-right");

                            }
                        });

                    }
                });
                carouselView3.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u1ks = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (Integer.valueOf(Objects.requireNonNull(snapshot1.child("price").getValue(String.class))) < 1000) {
                        itemdetails itemdetails = new itemdetails(snapshot1.child("image").getValue(String.class), snapshot1.child("name").getValue(String.class), snapshot1.getKey(), snapshot1.child("price").getValue(String.class), snapshot1.child("discount").getValue(String.class));

                        u1ks.add(itemdetails);
                    }
                }

                if (u1ks.size() > 0) {
                    under1ktext.setVisibility(View.VISIBLE);
                }
                carouselView4.setSize(u1ks.size());
                carouselView4.setResource(R.layout.image_carousel_item);
                carouselView4.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
                carouselView4.setCarouselOffset(OffsetType.START);
                carouselView4.hideIndicator(true);
                carouselView4.setCarouselViewListener(new CarouselViewListener() {
                    @Override
                    public void onBindView(View view, final int position) {
                        // Example here is setting up a full image carousel
                        final ImageView imageView = view.findViewById(R.id.carouselimg);
                        TextView name = view.findViewById(R.id.itemname);
                        TextView price = view.findViewById(R.id.itemprice);
                        TextView discounttext = view.findViewById(R.id.discounttext);

                        name.setText(u1ks.get(position).getName());

                        if (u1ks.get(position).getDiscount() == null || u1ks.get(position).getDiscount().equals("") || u1ks.get(position).getDiscount().equals("0")) {
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(u1ks.get(position).getPrice())));

                        } else {
                            double am = (Integer.valueOf(u1ks.get(position).getPrice())) - (((Integer.valueOf(u1ks.get(position).getDiscount())) * 0.01) * (Integer.valueOf(u1ks.get(position).getPrice())));
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(String.valueOf(am))));
                            discounttext.setText(format.format(new BigDecimal(u1ks.get(position).getPrice())));
                            discounttext.setVisibility(View.VISIBLE);
                            discounttext.setPaintFlags(discounttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        }

                        FirebaseDatabase.getInstance().getReference().child("Items").child(u1ks.get(position).getRating()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Glide.with(getActivity()).load(dataSnapshot.child("image").getValue(String.class)).into(imageView);
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
                                Intent intent = new Intent(getActivity(), ItemDetail.class);
                                intent.putExtra("itemid", u1ks.get(position).getRating());
                                startActivity(intent);
                                customType(getActivity(), "left-to-right");

                            }
                        });

                    }
                });
                carouselView4.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;
    }

    public class SliderAdapter extends
            SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

        private Context context;
        private List<String> items, txts;

        public SliderAdapter(Context context, List<String> items, List<String> txts) {
            this.context = context;
            this.items = items;
            this.txts = txts;
        }

        @Override
        public SliderAdapter.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlayout, null);
            return new SliderAdapter.SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapter.SliderAdapterVH viewHolder, final int position) {

            String sliderItem = items.get(position);

            String slidertext = txts.get(position);

            Glide.with(viewHolder.itemView)
                    .load(sliderItem)
                    .into(viewHolder.imageViewBackground);

            viewHolder.itemtext.setText(slidertext);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ViewItems.class);
                    intent.putExtra("category", txts.get(position));
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
            TextView itemtext;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.itemimage);
                itemtext = itemView.findViewById(R.id.itemtext);
                this.itemView = itemView;
            }
        }

    }
}