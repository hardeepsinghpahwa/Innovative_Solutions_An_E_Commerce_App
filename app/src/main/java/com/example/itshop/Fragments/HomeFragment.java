package com.example.itshop.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.itshop.ItemDetail;
import com.example.itshop.R;
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
import java.util.List;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    SliderView recyclerView;
    ArrayList<String> sliderImages;
    ArrayList<String> texts;
    TextView newproducts,popular;
    CircularProgressBar circularProgressBar;
    ArrayList<itemdetails> newlyadded,popularimgs;
    CarouselView carouselView,carouselView2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        newproducts=v.findViewById(R.id.newproductstext);
        popular=v.findViewById(R.id.populartext);
        recyclerView = v.findViewById(R.id.homerecyclerview);
        circularProgressBar=v.findViewById(R.id.circularProgressBar);

        carouselView= v.findViewById(R.id.similarproductscyview);
        carouselView2 = v.findViewById(R.id.carousel2);


        FirebaseDatabase.getInstance().getReference().child("HomeRecyclerView").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                popular.setVisibility(View.VISIBLE);
                newproducts.setVisibility(View.VISIBLE);
                circularProgressBar.setVisibility(View.GONE);

                carouselView.scheduleLayoutAnimation();
                carouselView2.scheduleLayoutAnimation();

                sliderImages=new ArrayList<>();
                texts=new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    sliderImages.add(dataSnapshot.child("image").getValue(String.class));
                    texts.add(dataSnapshot.child("text").getValue(String.class));
                }

                SliderAdapter adapter = new SliderAdapter(getActivity(), sliderImages,texts);

                recyclerView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                recyclerView.setAutoCycle(true);
                recyclerView.setScrollTimeInSec(4);
                recyclerView.startAutoCycle();

                if(sliderImages.size()>0) {

                    recyclerView.setSliderAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        FirebaseDatabase.getInstance().getReference().child("Items").orderByKey().limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                newlyadded=new ArrayList<>();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {

                    itemdetails itemdetails=new itemdetails(snapshot1.child("image").getValue(String.class),snapshot1.child("name").getValue(String.class),snapshot1.getKey(),snapshot1.child("price").getValue(String.class),snapshot1.child("discount").getValue(String.class));

                    newlyadded.add(itemdetails);
                }


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
                        TextView name=view.findViewById(R.id.itemname);
                        TextView price=view.findViewById(R.id.itemprice);
                        TextView discounttext=view.findViewById(R.id.discounttext);

                        if( newlyadded.get(position).getDiscount()==null || newlyadded.get(position).getDiscount().equals("") || newlyadded.get(position).getDiscount().equals("0"))
                        {
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(newlyadded.get(position).getPrice())));

                        }
                        else {
                            double am=(Integer.valueOf(newlyadded.get(position).getPrice()))-(((Integer.valueOf(newlyadded.get(position).getDiscount()))*0.01)*(Integer.valueOf(newlyadded.get(position).getPrice())));
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
                                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                                {
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
                                Intent intent=new Intent(getActivity(),ItemDetail.class);
                                intent.putExtra("itemid",newlyadded.get(position).getRating());
                                startActivity(intent);
                                customType(getActivity(),"left-to-right");

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


        FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                popularimgs=new ArrayList<>();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    itemdetails itemdetails=new itemdetails(snapshot1.child("image").getValue(String.class),snapshot1.child("name").getValue(String.class),snapshot1.getKey(),snapshot1.child("price").getValue(String.class),snapshot1.child("discount").getValue(String.class));

                    popularimgs.add(itemdetails);
                }


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
                        TextView name=view.findViewById(R.id.itemname);
                        TextView price=view.findViewById(R.id.itemprice);
                        TextView discounttext=view.findViewById(R.id.discounttext);

                        name.setText(popularimgs.get(position).getName());

                        if( popularimgs.get(position).getDiscount()==null || popularimgs.get(position).getDiscount().equals("") || popularimgs.get(position).getDiscount().equals("0"))
                        {
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(popularimgs.get(position).getPrice())));

                        }
                        else {
                            double am=(Integer.valueOf(popularimgs.get(position).getPrice()))-(((Integer.valueOf(popularimgs.get(position).getDiscount()))*0.01)*(Integer.valueOf(popularimgs.get(position).getPrice())));
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(String.valueOf(am))));
                            discounttext.setText(format.format(new BigDecimal(popularimgs.get(position).getPrice())));
                            discounttext.setVisibility(View.VISIBLE);
                            discounttext.setPaintFlags(discounttext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        }

                        FirebaseDatabase.getInstance().getReference().child("Items").child(popularimgs.get(position).getRating()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                                {
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
                                Intent intent=new Intent(getActivity(),ItemDetail.class);
                                intent.putExtra("itemid",popularimgs.get(position).getRating());
                                startActivity(intent);
                                customType(getActivity(),"left-to-right");

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



        return v;
    }

    public class SliderAdapter extends
            SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

        private Context context;
        private List<String> items,txts;

        public SliderAdapter(Context context, List<String> items,List<String> txts) {
            this.context = context;
            this.items = items;
            this.txts=txts;
        }

        @Override
        public SliderAdapter.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlayout, null);
            return new SliderAdapter.SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapter.SliderAdapterVH viewHolder, final int position) {

            String sliderItem = items.get(position);

            String slidertext=txts.get(position);

            Glide.with(viewHolder.itemView)
                    .load(sliderItem)
                    .into(viewHolder.imageViewBackground);

            viewHolder.itemtext.setText(slidertext);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


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
                itemtext=itemView.findViewById(R.id.itemtext);
                this.itemView = itemView;
            }
        }

    }
}