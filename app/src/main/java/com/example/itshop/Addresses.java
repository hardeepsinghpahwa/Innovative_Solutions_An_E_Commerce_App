package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class Addresses extends AppCompatActivity {


    FirebaseRecyclerAdapter<address, AddressViewHolder> firebaseRecyclerAdapter;
    RecyclerView recyclerView;
    TextView addnew, select;
    CircularProgressBar circularProgressBar;
    ImageView noaddresses;
    int posi = 0;
    ImageView back;
    ArrayList<String> ids,quans;
    String total,itemid;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        type=getIntent().getStringExtra("ac");

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if(args!=null)
        {ids = (ArrayList<String>) args.getSerializable("ARRAYLIST");}

        Bundle args2 = intent.getBundleExtra("BUNDLE2");
        if(args2!=null)
        {quans = (ArrayList<String>) args2.getSerializable("ARRAYLIST2");}

        total=getIntent().getStringExtra("total");
        itemid=getIntent().getStringExtra("itemid");

        recyclerView = findViewById(R.id.addressesrecyview);
        addnew = findViewById(R.id.addnew);
        select = findViewById(R.id.select);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        noaddresses = findViewById(R.id.noaddresses);
        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        if(type!=null)
        {
            select.setVisibility(View.GONE);
        }


        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Addresses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    circularProgressBar.setVisibility(View.GONE);
                    noaddresses.setVisibility(View.GONE);

                } else {
                    noaddresses.setVisibility(View.VISIBLE);
                    circularProgressBar.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText name, housenumber, street, city, phone, pincode;
                final TextView state,cancel,save;

                final Dialog dialog = new Dialog(Addresses.this);
                dialog.setContentView(R.layout.addaddress);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setWindowAnimations(R.style.AppTheme_UpDown);

                name = dialog.findViewById(R.id.name);
                housenumber = dialog.findViewById(R.id.housenum);
                street = dialog.findViewById(R.id.street);
                city = dialog.findViewById(R.id.city);
                state = dialog.findViewById(R.id.state);
                save = dialog.findViewById(R.id.save);
                phone = dialog.findViewById(R.id.phone);
                pincode = dialog.findViewById(R.id.pincode);
                cancel=dialog.findViewById(R.id.cancel);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                state.setKeyListener(null);

                final String[] listItems = {"Andaman and Nicobar Islands","Andra Pradesh", "Arunachal Pradesh", "Assam","Bihar","Chandigarh","Chhattisgarh","Dadar and Nagar Haveli","Daman and Diu","Delhi","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Ladakh","Lakshadeep","Madya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Orissa","Punjab","Pondicherry","Rajasthan","Sikkim","Tamil Nadu","Telagana","Tripura","Uttarakhand","Uttar Pradesh","West Bengal"};

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Addresses.this);
                builder.setTitle("Your State");

                builder.setItems(listItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        state.setText(listItems[which]);
                    }
                });

                final androidx.appcompat.app.AlertDialog dialog1 = builder.create();

                state.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        dialog1.show();

                        return false;
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (name.getText().toString().equals("")) {
                            Toast.makeText(Addresses.this, "Enter name", Toast.LENGTH_SHORT).show();
                        } else if (phone.getText().toString().equals("")) {
                            Toast.makeText(Addresses.this, "Enter phone", Toast.LENGTH_SHORT).show();
                        } else if (pincode.getText().toString().equals("")) {
                            Toast.makeText(Addresses.this, "Enter pincode", Toast.LENGTH_SHORT).show();
                        } else if (housenumber.getText().toString().equals("")) {
                            Toast.makeText(Addresses.this, "Enter house number", Toast.LENGTH_SHORT).show();
                        } else if (street.getText().toString().equals("")) {
                            Toast.makeText(Addresses.this, "Enter street,colony", Toast.LENGTH_SHORT).show();
                        } else if (city.getText().toString().equals("")) {
                            Toast.makeText(Addresses.this, "Select city", Toast.LENGTH_SHORT).show();
                        } else if (state.getText().toString().equals("")) {
                            Toast.makeText(Addresses.this, "Enter state", Toast.LENGTH_SHORT).show();
                        } else {
                            address address = new address(name.getText().toString(), phone.getText().toString(), pincode.getText().toString(), housenumber.getText().toString(), street.getText().toString(), city.getText().toString(), state.getText().toString());

                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Addresses").child(UUID.randomUUID().toString()).setValue(address).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Addresses.this, "Address Saved", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        firebaseRecyclerAdapter.notifyDataSetChanged();

                                    } else {
                                        Toast.makeText(Addresses.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                });


                dialog.show();

            }
        });


        Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Addresses");

        FirebaseRecyclerOptions<address> options = new FirebaseRecyclerOptions.Builder<address>()
                .setQuery(query, new SnapshotParser<address>() {
                    @NonNull
                    @Override
                    public address parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new address(snapshot.child("name").getValue(String.class), snapshot.child("phone").getValue(String.class), snapshot.child("pincode").getValue(String.class), snapshot.child("housenumber").getValue(String.class), snapshot.child("colony").getValue(String.class), snapshot.child("city").getValue(String.class), snapshot.child("state").getValue(String.class));
                    }
                }).build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<address, AddressViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AddressViewHolder holder, final int position, @NonNull address model) {

                if (posi == position) {
                    holder.check.setChecked(true);
                } else {
                    holder.check.setChecked(false);
                }

                if(type!=null)
                {
                    holder.check.setVisibility(View.GONE);
                }

                holder.name.setText(model.getName());
                holder.phone.setText(model.getPhone());
                holder.address.setText(model.housenumber + " " + model.getColony() + " " + model.city + " " + model.getState() + " " + model.getPincode());

                holder.check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        posi = position;
                        firebaseRecyclerAdapter.notifyDataSetChanged();
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder=new AlertDialog.Builder(Addresses.this);
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseRecyclerAdapter.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(Addresses.this, "Address Deleted", Toast.LENGTH_SHORT).show();
                                            firebaseRecyclerAdapter.notifyDataSetChanged();
                                        }
                                        else {
                                            Toast.makeText(Addresses.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setTitle("Delete Address?");

                        builder.show();

                    }
                });

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final TextView name, housenumber, street, city, state, phone, save, pincode,cancel;

                        final Dialog dialog = new Dialog(Addresses.this);
                        dialog.setContentView(R.layout.addaddress);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setWindowAnimations(R.style.AppTheme_UpDown);

                        name = dialog.findViewById(R.id.name);
                        housenumber = dialog.findViewById(R.id.housenum);
                        street = dialog.findViewById(R.id.street);
                        city = dialog.findViewById(R.id.city);
                        state = dialog.findViewById(R.id.state);
                        save = dialog.findViewById(R.id.save);
                        phone = dialog.findViewById(R.id.phone);
                        pincode = dialog.findViewById(R.id.pincode);
                        cancel=dialog.findViewById(R.id.cancel);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        name.setText(firebaseRecyclerAdapter.getItem(position).getName());
                        housenumber.setText(firebaseRecyclerAdapter.getItem(position).getHousenumber());
                        street.setText(firebaseRecyclerAdapter.getItem(position).getColony());
                        city.setText(firebaseRecyclerAdapter.getItem(position).getCity());
                        state.setText(firebaseRecyclerAdapter.getItem(position).getState());
                        phone.setText(firebaseRecyclerAdapter.getItem(position).getPhone());
                        pincode.setText(firebaseRecyclerAdapter.getItem(position).getPincode());



                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (name.getText().toString().equals("")) {
                                    Toast.makeText(Addresses.this, "Enter name", Toast.LENGTH_SHORT).show();
                                } else if (phone.getText().toString().equals("")) {
                                    Toast.makeText(Addresses.this, "Enter phone", Toast.LENGTH_SHORT).show();
                                } else if (pincode.getText().toString().equals("")) {
                                    Toast.makeText(Addresses.this, "Enter pincode", Toast.LENGTH_SHORT).show();
                                } else if (housenumber.getText().toString().equals("")) {
                                    Toast.makeText(Addresses.this, "Enter house number", Toast.LENGTH_SHORT).show();
                                } else if (street.getText().toString().equals("")) {
                                    Toast.makeText(Addresses.this, "Enter street,colony", Toast.LENGTH_SHORT).show();
                                } else if (city.getText().toString().equals("")) {
                                    Toast.makeText(Addresses.this, "Enter city", Toast.LENGTH_SHORT).show();
                                } else if (state.getText().toString().equals("")) {
                                    Toast.makeText(Addresses.this, "Enter state", Toast.LENGTH_SHORT).show();
                                } else {

                                    String n=name.getText().toString();
                                    String phn=phone.getText().toString();
                                    String pin=pincode.getText().toString();
                                    String house=housenumber.getText().toString();
                                    String str=street.getText().toString();
                                    String cit=city.getText().toString();
                                    String st=state.getText().toString();


                                    Map<String,Object> vals=new HashMap<>();
                                    vals.put("name",n);
                                    vals.put("phone",phn);
                                    vals.put("pincode",pin);
                                    vals.put("housenumber",house);
                                    vals.put("colony",str);
                                    vals.put("city",cit);
                                    vals.put("state",st);


                                    firebaseRecyclerAdapter.getRef(position).updateChildren(vals).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Addresses.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();

                                            } else {
                                                Toast.makeText(Addresses.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }
                        });


                        dialog.show();
                    }
                });


            }

            @NonNull
            @Override
            public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addressitem, parent, false);
                return new AddressViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(Addresses.this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseRecyclerAdapter.getItemCount() == 0) {
                    Toast.makeText(Addresses.this, "Add an address first", Toast.LENGTH_SHORT).show();
                } else if(itemid==null){
                    Intent intent = new Intent(Addresses.this, OrderDetails.class);
                    intent.putExtra("name", firebaseRecyclerAdapter.getItem(posi).getName());
                    intent.putExtra("address", firebaseRecyclerAdapter.getItem(posi).getHousenumber() + " " + firebaseRecyclerAdapter.getItem(posi).getColony() + " " + firebaseRecyclerAdapter.getItem(posi).city + " " + firebaseRecyclerAdapter.getItem(posi).getState() + " " + firebaseRecyclerAdapter.getItem(posi).getPincode());
                    intent.putExtra("phone", firebaseRecyclerAdapter.getItem(posi).getPhone());
                    intent.putExtra("state",firebaseRecyclerAdapter.getItem(posi).getState());
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST",(Serializable)ids);
                    intent.putExtra("BUNDLE",args);

                    Bundle args2 = new Bundle();
                    args2.putSerializable("ARRAYLIST2",(Serializable)quans);
                    intent.putExtra("BUNDLE2",args2);

                    intent.putExtra("total",total);

                    startActivity(intent);
                    customType(Addresses.this,"left-to-right");
                }
                else {
                    Intent intent = new Intent(Addresses.this, OrderDetails.class);
                    intent.putExtra("name", firebaseRecyclerAdapter.getItem(posi).getName());
                    intent.putExtra("address", firebaseRecyclerAdapter.getItem(posi).getHousenumber() + " " + firebaseRecyclerAdapter.getItem(posi).getColony() + " " + firebaseRecyclerAdapter.getItem(posi).city + " " + firebaseRecyclerAdapter.getItem(posi).getState() + " " + firebaseRecyclerAdapter.getItem(posi).getPincode());
                    intent.putExtra("phone", firebaseRecyclerAdapter.getItem(posi).getPhone());
                    intent.putExtra("state",firebaseRecyclerAdapter.getItem(posi).getState());
                    intent.putExtra("itemid",itemid);
                    intent.putExtra("total",total);

                    startActivity(intent);
                    customType(Addresses.this,"left-to-right");
                }
            }
        });

    }

    private class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView name, address, phone, delete, edit;
        CheckBox check;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            phone = itemView.findViewById(R.id.pincode);
            check = itemView.findViewById(R.id.check);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
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
        customType(Addresses.this,"right-to-left");
    }
}