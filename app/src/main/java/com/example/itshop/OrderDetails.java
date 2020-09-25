package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.itshop.Fragments.Policies;
import com.example.itshop.Notifications.SendNoti;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firestore.v1.StructuredQuery;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.smarteist.autoimageslider.SliderAnimations;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class OrderDetails extends AppCompatActivity implements PaymentResultListener {


    ArrayList<String> ids, quans, singleitem, discounts, deliveries, prices;
    String itemid, name, address, phone, total, email = "", phone2 = "", paycost, state, orderid;
    TextView addresss, price, deliverytotal, discount, totalprice, itemtext, backbutton, pay,viewpolicies;
    int pri;
    double dis, del = 0;
    ImageView back;
    RecyclerView recyclerView;
    CircularProgressBar circularProgressBar;
    ProgressDialog progressDialog;
    int pa = 0;
    NetworkBroadcast networkBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Checkout.preload(getApplicationContext());
        singleitem = new ArrayList<>();
        state = getIntent().getStringExtra("state");

        discounts = new ArrayList<>();
        deliveries = new ArrayList<>();
        prices = new ArrayList<>();


        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    email = snapshot.child("email").getValue(String.class);
                    phone2 = snapshot.child("phone").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //checkout.setKeyID("rzp_test_6gqCAmSMnS7C1R");

        Bundle args = getIntent().getBundleExtra("BUNDLE");
        if (args != null) {
            ids = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        }

        Bundle args2 = getIntent().getBundleExtra("BUNDLE2");
        if (args != null) {
            quans = (ArrayList<String>) args2.getSerializable("ARRAYLIST2");
        }

        itemid = getIntent().getStringExtra("itemid");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");

        singleitem.add(itemid);

        recyclerView = findViewById(R.id.itemsrecyview);
        addresss = findViewById(R.id.address);
        price = findViewById(R.id.price);
        totalprice = findViewById(R.id.totalprice);
        discount = findViewById(R.id.discount);
        deliverytotal = findViewById(R.id.deliverytotal);
        itemtext = findViewById(R.id.itemstext);
        pay = findViewById(R.id.pay);
        backbutton = findViewById(R.id.backbutton);
        back = findViewById(R.id.back);
        viewpolicies=findViewById(R.id.viewpolicies);
        circularProgressBar = findViewById(R.id.circularProgressBar);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (itemid == null) {
            itemtext.setText("Items(" + ids.size() + ")");
        } else {
            itemtext.setText("Items(" + singleitem.size() + ")");
        }

        addresss.setText(name + "\n" + address + "\n" + phone);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


        viewpolicies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrderDetails.this, Policies.class));
                customType(OrderDetails.this,"left-to-right");
            }
        });

        if (itemid == null) {
            FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    pri = 0;
                    dis = 0;
                    for (int j = 0; j < ids.size(); j++) {
                        final int finalJ = j;
                        snapshot.getRef().child(ids.get(j)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {


                                    if (state.equals("Himachal Pradesh") || state.equals("Haryana") || state.equals("Punjab") || state.equals("Chandigarh")) {
                                        del = del + ((Integer.valueOf(snapshot.child("delstate1").getValue(String.class))) * (Integer.valueOf(quans.get(finalJ))));
                                        deliveries.add(String.valueOf(((Integer.valueOf(snapshot.child("delstate1").getValue(String.class))))));

                                    } else if (state.equals("Uttarakhand") || state.equals("Uttar Pradesh") || state.equals("Rajasthan") || state.equals("Delhi")) {
                                        del = del + ((Integer.valueOf(snapshot.child("delstate2").getValue(String.class))) * (Integer.valueOf(quans.get(finalJ))));
                                        deliveries.add(String.valueOf(((Integer.valueOf(snapshot.child("delstate2").getValue(String.class))))));


                                    } else {
                                        del = del + ((Integer.valueOf(snapshot.child("delstate3").getValue(String.class))) * (Integer.valueOf(quans.get(finalJ))));
                                        deliveries.add(String.valueOf(((Integer.valueOf(snapshot.child("delstate3").getValue(String.class))))));

                                    }


                                    pri = pri + ((Integer.valueOf(snapshot.child("price").getValue(String.class)) * (Integer.valueOf(quans.get(finalJ)))));

                                    prices.add(String.valueOf(((Integer.valueOf(snapshot.child("price").getValue(String.class))))));

                                    if (snapshot.child("discount").exists() && !snapshot.child("discount").getValue(String.class).equals("") && !snapshot.child("discount").getValue(String.class).equals("0")) {
                                        dis = dis + (((((Double.valueOf(snapshot.child("discount").getValue(String.class))) * 0.01) * (Integer.valueOf(snapshot.child("price").getValue(String.class)))) * (Integer.valueOf(quans.get(finalJ)))));
                                        discounts.add(String.valueOf((((((Double.valueOf(snapshot.child("discount").getValue(String.class))) * 0.01) * (Integer.valueOf(snapshot.child("price").getValue(String.class))))))));
                                    } else {
                                        discounts.add("0");
                                    }
                                    if (finalJ == ids.size() - 1) {
                                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                        price.setText(format.format(new BigDecimal(String.valueOf(pri))));

                                        discount.setText(format.format(new BigDecimal(String.valueOf(dis))));
                                        deliverytotal.setText(format.format(new BigDecimal(String.valueOf(del))));

                                        total = String.valueOf(pri + del - dis);
                                        totalprice.setText(format.format(new BigDecimal(String.valueOf(pri + del - dis))));
                                        paycost = String.valueOf((Double.valueOf(total)) * 100);
                                        pay.setEnabled(true);


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(OrderDetails.this));
                    recyclerView.setAdapter(new ItemsAdapter(ids, quans));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {

            FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    pri = 0;
                    dis = 0;
                    snapshot.getRef().child(singleitem.get(0)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (state.equals("Himachal Pradesh") || state.equals("Haryana") || state.equals("Punjab") || state.equals("Chandigarh")) {
                                del = del + (Integer.valueOf(snapshot.child("delstate1").getValue(String.class)));
                            } else if (state.equals("Uttarakhand") || state.equals("Uttar Pradesh") || state.equals("Rajasthan") || state.equals("Delhi")) {
                                del = del + (Integer.valueOf(snapshot.child("delstate2").getValue(String.class)));
                            } else {
                                del = del + (Integer.valueOf(snapshot.child("delstate3").getValue(String.class)));
                            }

                            pri = pri + Integer.valueOf(snapshot.child("price").getValue(String.class));

                            if (snapshot.child("discount").exists() && !snapshot.child("discount").getValue(String.class).equals("") && !snapshot.child("discount").getValue(String.class).equals("0")) {
                                dis = dis + (((Double.valueOf(snapshot.child("discount").getValue(String.class))) * 0.01) * (Integer.valueOf(snapshot.child("price").getValue(String.class))));
                            }
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            price.setText(format.format(new BigDecimal(String.valueOf(pri))));

                            discount.setText(format.format(new BigDecimal(String.valueOf(dis))));
                            deliverytotal.setText(format.format(new BigDecimal(String.valueOf(del))));

                            total = String.valueOf(pri + del - dis);
                            totalprice.setText(format.format(new BigDecimal(String.valueOf(pri + del - dis))));
                            paycost = String.valueOf((Double.valueOf(total)) * 100);
                            pay.setEnabled(true);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    recyclerView.setLayoutManager(new LinearLayoutManager(OrderDetails.this));
                    recyclerView.setAdapter(new ItemsAdapter(singleitem, quans));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pa = 1;
                orderid = randomString();

                final Map order = new HashMap();
                order.put("name", name);
                order.put("address", address);
                order.put("phone", phone);
                order.put("email", email);
                order.put("price", String.valueOf(pri));
                order.put("discount", String.valueOf(dis));
                order.put("delivery", String.valueOf(del));
                order.put("total", total);
                order.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                order.put("timestamp", ServerValue.TIMESTAMP);
                order.put("status", "Payment Pending");
                order.put("orderid", orderid);
                order.put("type", "new");

                FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("items");

                            if (itemid == null) {

                                for (int m = 0; m < ids.size(); m++) {
                                    final int finalM = m;

                                    Map map = new HashMap();
                                    map.put("itemid", ids.get(m));
                                    map.put("quantity", quans.get(m));
                                    map.put("discount", discounts.get(m));
                                    map.put("delivery", deliveries.get(m));
                                    map.put("price", prices.get(m));

                                    databaseReference.child(UUID.randomUUID().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (finalM == ids.size() - 1) {

                                                    for (int l = 0; l < ids.size(); l++) {
                                                        final int finalL = l;
                                                        FirebaseDatabase.getInstance().getReference().child("Items").child(ids.get(l)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                snapshot.getRef().child("stock").setValue(String.valueOf((Integer.valueOf(snapshot.child("stock").getValue(String.class))) - (Integer.valueOf(quans.get(finalL)))));
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }

                                                    Checkout checkout = new Checkout();

                                                    try {
                                                        JSONObject options = new JSONObject();

                                                        options.put("name", "Merchant Name");
                                                        options.put("currency", "INR");
                                                        options.put("prefill.email", email);
                                                        options.put("prefill.contact", phone2);
                                                        options.put("amount", paycost);//pass amount in currency subunits
                                                        checkout.open(OrderDetails.this, options);


                                                    } catch (Exception e) {
                                                        Log.e("TAG", "Error in starting Razorpay Checkout", e);
                                                    }
                                                }
                                            } else {
                                                FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).removeValue();
                                                Toast.makeText(OrderDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("status", "failed");
                                                startActivity(intent);
                                                customType(OrderDetails.this, "left-to-right");
                                            }
                                        }
                                    });
                                }
                            } else {
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        Map map = new HashMap();
                                        map.put("itemid", itemid);
                                        map.put("quantity", "1");
                                        map.put("discount", String.valueOf(dis));
                                        map.put("delivery", String.valueOf(del));
                                        map.put("price", String.valueOf(pri));

                                        snapshot.getRef().child(UUID.randomUUID().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    FirebaseDatabase.getInstance().getReference().child("Items").child(itemid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            snapshot.getRef().child("stock").setValue(String.valueOf((Integer.valueOf(snapshot.child("stock").getValue(String.class))) - 1));
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    Checkout checkout = new Checkout();

                                                    try {
                                                        JSONObject options = new JSONObject();

                                                        options.put("name", "Merchant Name");
                                                        options.put("currency", "INR");
                                                        options.put("prefill.email", email);
                                                        options.put("prefill.contact", phone2);
                                                        options.put("amount", paycost);//pass amount in currency subunits
                                                        checkout.open(OrderDetails.this, options);


                                                    } catch (Exception e) {
                                                        Log.e("TAG", "Error in starting Razorpay Checkout", e);
                                                    }

                                                } else {
                                                    FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).removeValue();
                                                    Toast.makeText(OrderDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.putExtra("status", "failed");
                                                    startActivity(intent);
                                                    customType(OrderDetails.this, "left-to-right");
                                                }
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(OrderDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("status", "failed");
                                        startActivity(intent);
                                        customType(OrderDetails.this, "left-to-right");
                                    }

                                });
                            }


                        } else {
                            Toast.makeText(OrderDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("status", "failed");
                            startActivity(intent);
                            customType(OrderDetails.this, "left-to-right");
                        }
                    }
                });

                progressDialog = new ProgressDialog(OrderDetails.this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Processing");
                progressDialog.show();
            }

        });
    }

    @Override
    public void onPaymentSuccess(String s) {

        try {

            Map map = new HashMap();
            map.put("text", "Order Placed");
            map.put("timestamp", ServerValue.TIMESTAMP);

            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("tracking").child(UUID.randomUUID().toString()).setValue(map);

            if (itemid == null) {
                FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("status").setValue("Payment Success").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OrderDetails.this, "Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("status", "success");
                            startActivity(intent);
                            customType(OrderDetails.this, "left-to-right");

                            FirebaseDatabase.getInstance().getReference().child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.child("token").exists()) {
                                        SendNoti sendNoti = new SendNoti();
                                        sendNoti.sendNotification2(OrderDetails.this, snapshot.child("token").getValue(String.class), "New Order", "You got a new order");

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                });

            } else {

                FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("status").setValue("Payment Success").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OrderDetails.this, "Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("status", "success");
                            startActivity(intent);
                            customType(OrderDetails.this, "left-to-right");

                        }

                        FirebaseDatabase.getInstance().getReference().child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child("token").exists()) {
                                    SendNoti sendNoti = new SendNoti();
                                    sendNoti.sendNotification2(OrderDetails.this, snapshot.child("token").getValue(String.class), "New Order", "You got a new order");

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

            }
        } catch (Exception e) {
            Log.i("OrderDetails", e.getMessage());
        }

    }/*{

        if (itemid == null) {

            for (m = 0; m < ids.size(); m++) {


                final String uniqueid = UUID.randomUUID().toString();

                Map<String, String> order = new HashMap<>();
                order.put("name", name);
                order.put("address", address);
                order.put("phone", phone);
                order.put("email", email);
                order.put("price", String.valueOf(pri));
                order.put("discount", String.valueOf(dis));
                order.put("delivery", String.valueOf(del));
                order.put("total", total);
                order.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                order.put("timestamp", String.valueOf(ServerValue.TIMESTAMP));


                FirebaseDatabase.getInstance().getReference().child("Active Orders").child(uniqueid).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Active Orders").child(uniqueid).child("items");

                            databaseReference.child(ids.get(m)).setValue(quans.get(m)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                FirebaseDatabase.getInstance().getReference().child("Items").child(ids.get(m)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        snapshot.getRef().child("stock").setValue(String.valueOf((Integer.valueOf(snapshot.child("stock").getValue(String.class))) - (Integer.valueOf(quans.get(m)))));
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                                Toast.makeText(OrderDetails.this, "Success", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("status", "success");
                                                startActivity(intent);
                                                customType(OrderDetails.this, "left-to-right");

                                            }
                                        });

                                    } else {
                                        FirebaseDatabase.getInstance().getReference().child("Active Orders").child(uniqueid).removeValue();
                                        Toast.makeText(OrderDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("status", "failed");
                                        startActivity(intent);
                                        customType(OrderDetails.this, "left-to-right");
                                    }
                                }
                            });

                        }
                    }
                });
            }
        } else {

            final String uniqueid = UUID.randomUUID().toString();

            Map<String, String> order = new HashMap<>();
            order.put("name", name);
            order.put("address", address);
            order.put("phone", phone);
            order.put("email", email);
            order.put("price", String.valueOf(pri));
            order.put("discount", String.valueOf(dis));
            order.put("delivery", String.valueOf(del));
            order.put("total", total);
            order.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            order.put("timestamp", String.valueOf(ServerValue.TIMESTAMP));

            FirebaseDatabase.getInstance().getReference().child("Active Orders").child(uniqueid).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Active Orders").child(uniqueid).child("items");


                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().child(itemid).setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            FirebaseDatabase.getInstance().getReference().child("Items").child(itemid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    snapshot.getRef().child("stock").setValue(String.valueOf((Integer.valueOf(snapshot.child("stock").getValue(String.class))) - 1));
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            Toast.makeText(OrderDetails.this, "Success", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("status", "success");
                                            startActivity(intent);
                                            customType(OrderDetails.this, "left-to-right");
                                        } else {
                                            FirebaseDatabase.getInstance().getReference().child("Active Orders").child(uniqueid).removeValue();
                                            Toast.makeText(OrderDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("status", "failed");
                                            startActivity(intent);
                                            customType(OrderDetails.this, "left-to-right");
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(OrderDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("status", "failed");
                                startActivity(intent);
                                customType(OrderDetails.this, "left-to-right");
                            }
                        });

                    }

                }
            });

        }
    }*/


    @Override
    public void onPaymentError(int i, String s) {

        if (itemid == null) {
            FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("status").setValue("Payment Failed").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(OrderDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("status", "Failed");
                        startActivity(intent);
                        customType(OrderDetails.this, "left-to-right");
                    }
                }
            });
            for (int m = 0; m < ids.size(); m++) {
                {

                    final int finalM = m;
                    FirebaseDatabase.getInstance().getReference().child("Items").child(ids.get(m)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().child("stock").setValue(String.valueOf((Integer.valueOf(snapshot.child("stock").getValue(String.class))) + Integer.valueOf(quans.get(finalM))));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        } else {


            FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("status").setValue("Payment Failed").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(OrderDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OrderDetails.this, OrderPaymentStatus.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("status", "Failed");
                        startActivity(intent);
                        customType(OrderDetails.this, "left-to-right");
                    }

                }
            });

            FirebaseDatabase.getInstance().getReference().child("Items").child(itemid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    snapshot.getRef().child("stock").setValue(String.valueOf((Integer.valueOf(snapshot.child("stock").getValue(String.class))) + 1));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        ArrayList<String> itemids, nums;

        public ItemsAdapter(ArrayList<String> itemids, ArrayList<String> nums) {
            this.itemids = itemids;
            this.nums = nums;
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
                    if (snapshot.exists()) {

                        if (position == 0) {
                            circularProgressBar.setVisibility(View.GONE);
                        }
                        itemdetails itemdetails = snapshot.getValue(com.example.itshop.itemdetails.class);

                        holder.name.setText(itemdetails.getName());
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                        if (state.equals("Himachal Pradesh") || state.equals("Haryana") || state.equals("Punjab") || state.equals("Chandigarh")) {
                            holder.delivery.setText("+ " + (format.format(new BigDecimal(snapshot.child("delstate1").getValue(String.class)))) + " delivery");
                        } else if (state.equals("Uttarakhand") || state.equals("Uttar Pradesh") || state.equals("Rajasthan") || state.equals("Delhi")) {
                            holder.delivery.setText("+ " + (format.format(new BigDecimal(snapshot.child("delstate2").getValue(String.class)))) + " delivery");
                        } else {
                            holder.delivery.setText("+ " + (format.format(new BigDecimal(snapshot.child("delstate3").getValue(String.class)))) + " delivery");
                        }


                        if (snapshot.child("discount").getValue(String.class) == null || snapshot.child("discount").getValue(String.class).equals("") || snapshot.child("discount").getValue(String.class).equals("0")) {
                            holder.price.setText(format.format(new BigDecimal(snapshot.child("price").getValue(String.class))));

                        } else {
                            double am = (Integer.valueOf(snapshot.child("price").getValue(String.class))) - (((Double.valueOf(snapshot.child("discount").getValue(String.class))) * 0.01) * (Integer.valueOf(snapshot.child("price").getValue(String.class))));
                            holder.price.setText(format.format(new BigDecimal(String.valueOf(am))));
                            holder.discount.setText(format.format(new BigDecimal(snapshot.child("price").getValue(String.class))));
                            holder.discount.setVisibility(View.VISIBLE);
                            holder.discount.setPaintFlags(discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }


                        if (itemid == null)
                            holder.quantity.setText("Quantity: " + quans.get(position));
                        else holder.quantity.setText("Quantity: 1");

                        snapshot.child("images").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {

                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Glide.with(OrderDetails.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
                                        break;
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
        public int getItemCount() {
            return itemids.size();
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, discount, price, quantity, delivery;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            discount = itemView.findViewById(R.id.discount);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            delivery = itemView.findViewById(R.id.delivery);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(OrderDetails.this, "right-to-left");
    }

    public String randomString() {
        final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random RANDOM = new Random();
        StringBuilder sb = new StringBuilder(12);

        for (int i = 0; i < 12; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pa == 1) {
            if (progressDialog != null) {
                progressDialog.show();
            }
        }

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        networkBroadcast=new NetworkBroadcast();
        this.registerReceiver(networkBroadcast, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        this.unregisterReceiver(networkBroadcast);
    }
}