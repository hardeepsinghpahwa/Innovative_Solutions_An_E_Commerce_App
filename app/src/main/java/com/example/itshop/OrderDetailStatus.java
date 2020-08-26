package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class OrderDetailStatus extends AppCompatActivity {

    String orderid;
    TextView addresss, price2, delievrytotal, discount, totalprice, orderidd,trackingtext,orderoptionstxt,trackingid,copyandgo,cancelitems,replaceitems,returnitems;
    RecyclerView recyclerView, trackingrecyview;
    ArrayList<String> ids, quans,prices,discounts,deliveries;
    ArrayList<tracking>tracks;
    ConstraintLayout constraintLayout;
    CircularProgressBar circularProgressBar;
    CardView can,retu,repl;
    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_status);

        orderid = getIntent().getStringExtra("orderid");

        ids = new ArrayList<>();
        quans = new ArrayList<>();
        tracks = new ArrayList<>();
        prices=new ArrayList<>();
        discounts=new ArrayList<>();
        deliveries=new ArrayList<>();

        addresss = findViewById(R.id.address);
        price2 = findViewById(R.id.pricee);
        delievrytotal = findViewById(R.id.delievrytotal);
        discount = findViewById(R.id.discount);
        totalprice = findViewById(R.id.totalprice);
        orderidd = findViewById(R.id.orderid);
        recyclerView = findViewById(R.id.itemsrecyview);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        trackingrecyview = findViewById(R.id.trackingrecyview);
        trackingtext=findViewById(R.id.trackingtext);
        trackingid=findViewById(R.id.trackingid);
        constraintLayout=findViewById(R.id.trackinglayout);
        copyandgo=findViewById(R.id.copyandgo);
        cancelitems=findViewById(R.id.cancelorder);
        replaceitems=findViewById(R.id.replaceitems);
        returnitems=findViewById(R.id.returnitems);
        can=findViewById(R.id.cardView23);
        repl=findViewById(R.id.cardView21);
        retu=findViewById(R.id.cardView20);
        orderoptionstxt=findViewById(R.id.orderoptionstext);


        cancelitems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrderDetailStatus.this,CancelReturnReplace.class);
                intent.putExtra("orderid",orderid);
                intent.putExtra("type","cancel");
                startActivity(intent);
            }
        });

        returnitems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrderDetailStatus.this,CancelReturnReplace.class);
                intent.putExtra("orderid",orderid);
                intent.putExtra("type","return");
                startActivity(intent);
            }
        });


        replaceitems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrderDetailStatus.this,CancelReturnReplace.class);
                intent.putExtra("orderid",orderid);
                intent.putExtra("type","replace");
                startActivity(intent);
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {

                if(snapshot.child("status").getValue(String.class).equals("Payment Pending"))
                {
                    trackingtext.setText("Your payment for this order is still pending. Please wait for the confirmation.");
                }
                else {
                    snapshot.getRef().child("tracking").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                tracks.add(new tracking((dataSnapshot.child("text").getValue(String.class)),dataSnapshot.child("timestamp").getValue(Long.class)));
                            }

                            Collections.sort(tracks);

                            trackingrecyview.setLayoutManager(new LinearLayoutManager(OrderDetailStatus.this));
                            trackingrecyview.setAdapter(new TrackingAdapter(tracks));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                if(snapshot.child("status").getValue(String.class).equals("Delievered"))
                {
                    retu.setVisibility(View.VISIBLE);
                    repl.setVisibility(View.VISIBLE);
                }
                if(snapshot.child("status").getValue(String.class).equals("Cancelled"))
                {
                    retu.setVisibility(View.GONE);
                    repl.setVisibility(View.GONE);
                    can.setVisibility(View.GONE);
                    orderoptionstxt.setVisibility(View.GONE);
                }

                if(snapshot.child("trackingid").exists())
                {
                    constraintLayout.setVisibility(View.VISIBLE);
                    trackingid.setText("Tracking ID : "+snapshot.child("trackingid").getValue(String.class));

                    copyandgo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Tracking ID",snapshot.child("trackingid").getValue(String.class));
                            clipboard.setPrimaryClip(clip);

                            String url = "http://trackoncourier.com/Tracking/t2/MultipleTracking";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });
                }


                snapshot.getRef().child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ids.add(dataSnapshot.child("itemid").getValue(String.class));
                            quans.add(dataSnapshot.child("quantity").getValue(String.class));
                            prices.add(dataSnapshot.child("price").getValue(String.class));
                            deliveries.add(dataSnapshot.child("delievry").getValue(String.class));
                            discounts.add(dataSnapshot.child("discount").getValue(String.class));
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(OrderDetailStatus.this));
                        recyclerView.setAdapter(new ItemsAdapter(ids, quans,prices,deliveries,discounts));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                delievrytotal.setText(format.format(new BigDecimal(snapshot.child("delievry").getValue(String.class))));
                orderidd.setText("Order ID : " + snapshot.getKey());
                discount.setText(format.format(new BigDecimal(snapshot.child("discount").getValue(String.class))));
                addresss.setText(snapshot.child("name").getValue(String.class) + "\n" + snapshot.child("address").getValue(String.class) + "\n" + snapshot.child("phone").getValue(String.class));
                totalprice.setText(format.format(new BigDecimal(String.valueOf((Double.valueOf(snapshot.child("price").getValue(String.class))) + (Double.valueOf(snapshot.child("delievry").getValue(String.class))) - (Double.valueOf((snapshot.child("discount").getValue(String.class))))))));
                price2.setText(format.format(new BigDecimal(snapshot.child("price").getValue(String.class))));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        ArrayList<String> itemids, nums,pris,dels,diss;

        public ItemsAdapter(ArrayList<String> itemids, ArrayList<String> nums,ArrayList<String> pris,ArrayList<String> dels,ArrayList<String> diss) {
            this.itemids = itemids;
            this.nums = nums;
            this.pris=pris;
            this.dels=dels;
            this.diss=diss;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderitem, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

            FirebaseDatabase.getInstance().getReference().child("Items").child(itemids.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Log.i("items",pris.get(0)+" "+diss.get(0)+" "+dels.get(0));

                    if (position == 0) {
                        circularProgressBar.setVisibility(View.GONE);
                    }
                    itemdetails itemdetails = snapshot.getValue(com.example.itshop.itemdetails.class);

                    holder.name.setText(itemdetails.getName());
                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                    if (diss.get(position) == null || diss.get(position).equals("") || diss.get(position).equals("0") || diss.get(position).equals("0.0")) {
                        holder.price.setText(format.format(new BigDecimal(snapshot.child("price").getValue(String.class))));

                    } else {
                        double am = (Double.valueOf(pris.get(position))) - (((Double.valueOf(diss.get(position))) * 0.01) * (Double.valueOf(pris.get(position))));
                        holder.price.setText(format.format(new BigDecimal(String.valueOf(am))));
                        holder.discount.setText(format.format(new BigDecimal(snapshot.child("price").getValue(String.class))));
                        holder.discount.setVisibility(View.VISIBLE);
                        holder.discount.setPaintFlags(discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }

                    holder.quantity.setText("Quantity: " + nums.get(position));

                    snapshot.child("images").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Glide.with(OrderDetailStatus.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
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

        }

        @Override
        public int getItemCount() {
            return itemids.size();
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, discount, price, quantity, delievry;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            discount = itemView.findViewById(R.id.discount);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            delievry = itemView.findViewById(R.id.delievry);
        }

    }

    private class TrackingAdapter extends RecyclerView.Adapter<TrackingViewHolder> {

        ArrayList<tracking> itemids;

        public TrackingAdapter(ArrayList<tracking> itemids) {
            this.itemids = itemids;
        }

        @NonNull
        @Override
        public TrackingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trackitem, parent, false);
            return new TrackingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final TrackingViewHolder holder, final int position) {

            holder.text.setText(itemids.get(position).getTrack());

            if (itemids.get(position).getTrack().equals("Payment Pending")) {
                Drawable unwrappedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle);
                unwrappedDrawable.setTint(getColor(R.color.yellow));
                holder.text.setText(itemids.get(position).getTrack()+", "+createDate(itemids.get(position).getTimestamp()));
                holder.text.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
            } else if (itemids.get(position).getTrack().equals("Payment Success")) {
                Drawable unwrappedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle);
                unwrappedDrawable.setTint(getColor(R.color.orange));
                holder.text.setText(itemids.get(position).getTrack()+", "+createDate(itemids.get(position).getTimestamp()));
                holder.text.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
            } else if (itemids.get(position).getTrack().equals("Payment Failed") || itemids.get(position).getTrack().equals("Cancelled")) {
                Drawable unwrappedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle);
                unwrappedDrawable.setTint(getColor(R.color.red));

                holder.text.setText(itemids.get(position).getTrack()+", "+createDate(itemids.get(position).getTimestamp()));
                holder.text.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
            } else if (itemids.get(position).getTrack().equals("Delievered")) {
                Drawable unwrappedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle);
                unwrappedDrawable.setTint(getColor(R.color.green));

                holder.text.setText(itemids.get(position).getTrack()+", "+createDate(itemids.get(position).getTimestamp()));
                holder.text.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);

            } else {
                Drawable unwrappedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, getColor(R.color.orange));
                holder.text.setText(itemids.get(position).getTrack()+", "+createDate(itemids.get(position).getTimestamp()));
                holder.text.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null);
            }
        }

        @Override
        public int getItemCount() {
            return itemids.size();
        }
    }

    private class TrackingViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        public TrackingViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.trackinfo);

        }

    }

    public CharSequence createDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        DateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");
        return sdf.format(d);
    }

}