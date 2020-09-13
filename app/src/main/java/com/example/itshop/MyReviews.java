package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static maes.tech.intentanim.CustomIntent.customType;

public class MyReviews extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView back,noratings;
    ArrayList<String>ids;
    Double rat;
    ArrayList<itemreview> reviews;
    CircularProgressBar circularProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        back=findViewById(R.id.back);
        recyclerView=findViewById(R.id.myreviewsrecyview);

        ids=new ArrayList<>();
        reviews=new ArrayList<>();
        noratings=findViewById(R.id.noratings);
        circularProgressBar=findViewById(R.id.circularProgressBar);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists())
                    {
                        reviews.add(new itemreview("",dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("review").getValue(String.class),dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rating").getValue(String.class)));
                        ids.add(dataSnapshot.getKey());
                    }
                }

                if(ids.size()==0)
                {
                    noratings.setVisibility(View.GONE);
                    circularProgressBar.setVisibility(View.GONE);
                }
                else {
                    circularProgressBar.setVisibility(View.GONE);
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(MyReviews.this));
                recyclerView.setAdapter(new ReviewAdapter(ids,reviews));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


     /*   firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<itemreview, ReviewViewHolder>(options) {
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


                        final Dialog dialog = new Dialog(MyReviews.this);
                        dialog.setContentView(R.layout.ratingdialog);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setWindowAnimations(R.style.AppTheme_UpDown);

                        final RecyclerView recyclerView1;

                        recyclerView1 = dialog.findViewById(R.id.raterecyview);


                        ids.clear();
                        ids.add(itemid);

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyReviews.this, LinearLayoutManager.HORIZONTAL, false);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(MyReviews.this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);

*/

    }

    private class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder>
    {
        ArrayList<String> itemids;
        ArrayList<itemreview>rvs;

        public ReviewAdapter(ArrayList<String> itemids, ArrayList<itemreview> rvs) {
            this.itemids = itemids;
            this.rvs = rvs;
        }

        @NonNull
        @Override
        public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewitem, parent, false);
            return new ReviewViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ReviewViewHolder holder, final int position) {


            if(position==1)
            {
                recyclerView.scheduleLayoutAnimation();
            }

            FirebaseDatabase.getInstance().getReference().child("Items").child(itemids.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    holder.rating.setText(rvs.get(position).getRating());

                    holder.review.setText(rvs.get(position).getReview());

                    holder.edit.setVisibility(View.VISIBLE);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(MyReviews.this,ItemDetail.class);
                            intent.putExtra("itemid",itemids.get(position));
                            startActivity(intent);
                            customType(MyReviews.this,"left-to-right");
                        }
                    });

                    holder.edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    final Dialog dialog = new Dialog(MyReviews.this);
                                    dialog.setContentView(R.layout.ratingdialog);
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.getWindow().setWindowAnimations(R.style.AppTheme_UpDown);

                                    final RecyclerView recyclerView1;

                                    recyclerView1 = dialog.findViewById(R.id.raterecyview);

                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyReviews.this, LinearLayoutManager.HORIZONTAL, false);
                                    recyclerView1.setLayoutManager(linearLayoutManager);
                                    recyclerView1.setAdapter(new ItemsAdapter(ids, dialog));

                                    dialog.show();

                                }
                            });
                        }
                    });


                    holder.name.setText(snapshot.child("name").getValue(String.class));
                   snapshot.getRef().child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren())
                            {
                                Glide.with(MyReviews.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
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
                                    Glide.with(MyReviews.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
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
                                    Toast.makeText(MyReviews.this, "Provide some rating first", Toast.LENGTH_SHORT).show();
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
                                                                        Toast.makeText(MyReviews.this, "Thanks For The Rating", Toast.LENGTH_SHORT).show();
                                                                        holder.rate.setText("Edit Rating");
                                                                        holder.progressBar.setVisibility(View.GONE);

                                                                    } else {
                                                                        Toast.makeText(MyReviews.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(MyReviews.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(MyReviews.this,"right-to-left");

    }
}