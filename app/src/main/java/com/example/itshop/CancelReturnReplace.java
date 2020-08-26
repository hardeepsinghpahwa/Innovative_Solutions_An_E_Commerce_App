package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import io.grpc.Server;

public class CancelReturnReplace extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText reason;
    TextView action, title;
    String type, orderid;
    ArrayList<String> ids, quans;
    ArrayList<returnitem> items;
    CircularProgressBar circularProgressBar;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_return_replace);

        recyclerView = findViewById(R.id.recyclerView);
        action = findViewById(R.id.cancelorder);
        reason = findViewById(R.id.reason);
        title = findViewById(R.id.title);
        type = getIntent().getStringExtra("type");
        orderid = getIntent().getStringExtra("orderid");
        circularProgressBar = findViewById(R.id.circularProgressBar);

        ids = new ArrayList<>();
        quans = new ArrayList<>();
        items = new ArrayList<>();

        if (type.equals("cancel")) {
            title.setText("Cancel Order");
            recyclerView.setVisibility(View.GONE);
            action.setText("Cancel Order");

        } else if (type.equals("return")) {
            title.setText("Return Item(s)");
            action.setText("Request Return");
        } else if (type.equals("replace")) {
            title.setText("Replace Item(s)");
            action.setText("Request Replacement");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(CancelReturnReplace.this));
        recyclerView.setAdapter(new ItemsAdapter(ids, quans));


        FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (type.equals("return")) {
                    action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            count = 0;
                            while (count < ids.size()) {

                                View v = recyclerView.getLayoutManager().findViewByPosition(count);

                                CheckBox checkBox;
                                final TextView spinner;
                                checkBox = v.findViewById(R.id.checkBox);
                                spinner = v.findViewById(R.id.spinner);

                                if (checkBox.isChecked()) {

                                    final int finalI = count;
                                    count++;

                                    FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                                if (dataSnapshot.child("itemid").getValue(String.class).equals(ids.get(finalI))) {
                                                    items.add(new returnitem(ids.get(finalI), spinner.getText().toString(), dataSnapshot.child("price").getValue(String.class), dataSnapshot.child("discount").getValue(String.class)));
                                                }

                                            }

                                            if (finalI == ids.size() - 1) {
                                                if (items.size() == 0) {
                                                    Toast.makeText(CancelReturnReplace.this, "Select An Item First", Toast.LENGTH_SHORT).show();
                                                } else if (reason.getText().toString().equals("")) {
                                                    Toast.makeText(CancelReturnReplace.this, "Enter A Reason First", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("return").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            snapshot.getRef().child("reason").setValue(reason.getText().toString());
                                                            for (int l = 0; l < items.size(); l++) {
                                                                final Map map = new HashMap();
                                                                map.put("itemid", items.get(l).getId());
                                                                map.put("price", items.get(l).getPrice());
                                                                map.put("quantity", items.get(l).getQuantity());
                                                                map.put("discount", items.get(l).getDiscount());

                                                                final int finalL = l;
                                                                snapshot.getRef().child("items").child(UUID.randomUUID().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful() && finalL == items.size() - 1) {
                                                                            Map map1 = new HashMap();
                                                                            map1.put("status", "Return Requested");
                                                                            FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).updateChildren(map1).addOnCompleteListener(new OnCompleteListener() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Map map2=new HashMap();
                                                                                        map2.put("text","Return Requested");
                                                                                        map2.put("timestamp", ServerValue.TIMESTAMP);
                                                                                        FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("tracking").child(UUID.randomUUID().toString()).setValue(map2);
                                                                                        Toast.makeText(CancelReturnReplace.this, "Return Request Sent.", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        Toast.makeText(CancelReturnReplace.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                } else {

                                    if (count == ids.size() - 1) {
                                        if (items.size() == 0) {
                                            Toast.makeText(CancelReturnReplace.this, "Select An Item First", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    count++;
                                }

                            }


                        }
                    });
                } else if (type.equals("replace")) {
                    action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            count = 0;
                            while (count < ids.size()) {

                                View v = recyclerView.getLayoutManager().findViewByPosition(count);

                                CheckBox checkBox;
                                final TextView spinner;
                                checkBox = v.findViewById(R.id.checkBox);
                                spinner = v.findViewById(R.id.spinner);

                                if (checkBox.isChecked()) {

                                    Log.i("check", "checked");

                                    final int finalI = count;
                                    count++;

                                    FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Log.i("count", String.valueOf(count));
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                                if (dataSnapshot.child("itemid").getValue(String.class).equals(ids.get(finalI))) {
                                                    items.add(new returnitem(ids.get(finalI), spinner.getText().toString(), dataSnapshot.child("price").getValue(String.class), dataSnapshot.child("discount").getValue(String.class)));
                                                }

                                            }

                                            if (finalI == ids.size() - 1) {
                                                if (items.size() == 0) {
                                                    Toast.makeText(CancelReturnReplace.this, "Select An Item First", Toast.LENGTH_SHORT).show();
                                                } else if (reason.getText().toString().equals("")) {
                                                    Toast.makeText(CancelReturnReplace.this, "Enter A Reason First", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("replace").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            snapshot.getRef().child("reason").setValue(reason.getText().toString());
                                                            for (int l = 0; l < items.size(); l++) {
                                                                Map map = new HashMap();
                                                                map.put("itemid", items.get(l).getId());
                                                                map.put("price", items.get(l).getPrice());
                                                                map.put("quantity", items.get(l).getQuantity());
                                                                map.put("discount", items.get(l).getDiscount());

                                                                final int finalL = l;
                                                                snapshot.getRef().child("items").child(UUID.randomUUID().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful() && finalL == items.size() - 1) {
                                                                            Map map1 = new HashMap();
                                                                            map1.put("status", "Replacement Requested");
                                                                            FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).updateChildren(map1).addOnCompleteListener(new OnCompleteListener() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Map map2=new HashMap();
                                                                                        map2.put("text","Replacement Requested");
                                                                                        map2.put("timestamp", ServerValue.TIMESTAMP);
                                                                                        FirebaseDatabase.getInstance().getReference().child("Active Orders").child(orderid).child("tracking").child(UUID.randomUUID().toString()).setValue(map2);
                                                                                        Toast.makeText(CancelReturnReplace.this, "Replacement Request Sent.", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        Toast.makeText(CancelReturnReplace.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                } else {

                                    if (count == ids.size() - 1) {
                                        if (items.size() == 0) {
                                            Toast.makeText(CancelReturnReplace.this, "Select An Item First", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    count++;
                                }

                            }


                        }
                    });
                }

                snapshot.getRef().child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            if (type.equals("return")) {
                                FirebaseDatabase.getInstance().getReference().child("Items").child(dataSnapshot.child("itemid").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.child("returnable").getValue(String.class) != null || !snapshot.child("returnable").getValue(String.class).equals("0")) {
                                            ids.add(dataSnapshot.child("itemid").getValue(String.class));
                                            quans.add(dataSnapshot.child("quantity").getValue(String.class));
                                            recyclerView.getAdapter().notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else if (type.equals("replace")) {
                                FirebaseDatabase.getInstance().getReference().child("Items").child(dataSnapshot.child("itemid").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.child("replacement").getValue(String.class) != null || !snapshot.child("replacement").getValue(String.class).equals("0")) {
                                            ids.add(dataSnapshot.child("itemid").getValue(String.class));
                                            quans.add(dataSnapshot.child("quantity").getValue(String.class));
                                            recyclerView.getAdapter().notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
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

    private class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        ArrayList<String> itemids, nums;

        public ItemsAdapter(ArrayList<String> itemids, ArrayList<String> nums) {
            this.itemids = itemids;
            this.nums = nums;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.returnreplace, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

            FirebaseDatabase.getInstance().getReference().child("Items").child(itemids.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if (position == 0) {
                        circularProgressBar.setVisibility(View.GONE);
                    }

                    holder.name.setText(snapshot.child("name").getValue(String.class));

                    holder.minus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!holder.spinner.getText().toString().equals("1")) {
                                holder.spinner.setText((Integer.valueOf(holder.spinner.getText().toString())) - 1 + "");
                            }
                        }
                    });

                    holder.add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!holder.spinner.getText().toString().equals(nums.get(position))) {
                                holder.spinner.setText((Integer.valueOf(holder.spinner.getText().toString())) + 1 + "");
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

        ImageView add, minus;
        TextView name;
        EditText spinner;
        CheckBox checkBox;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            add = itemView.findViewById(R.id.plus);
            name = itemView.findViewById(R.id.itemname);
            minus = itemView.findViewById(R.id.minus);
            spinner = itemView.findViewById(R.id.spinner);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

    }

}