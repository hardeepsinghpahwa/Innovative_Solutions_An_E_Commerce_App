package com.example.itshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.example.itshop.Fragments.Categories;
import com.example.itshop.Fragments.Help;
import com.example.itshop.Fragments.HomeFragment;
import com.example.itshop.Fragments.MyOrders;
import com.example.itshop.Fragments.Notifications;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView name, email;
    ImageView propic;
    RelativeLayout cart;
    TextView itemsincart, logout;
    CoordinatorLayout coordinatorLayout;
    TextView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        itemsincart = findViewById(R.id.itemsincart);
        logout = findViewById(R.id.logout);
        coordinatorLayout = findViewById(R.id.coo);
        search = findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search.setEnabled(false);
                final Dialog dialog = new Dialog(Home.this);
                dialog.setContentView(R.layout.fragment_search);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setWindowAnimations(R.style.AppTheme_UpDown);

                final FloatingSearchView floatingSearchView;
                final List<item> items;
                final RecyclerView recyclerView;
                final TextView num;
                final ImageView noresults;
                final CircularProgressBar circularProgressBar;

                floatingSearchView = dialog.findViewById(R.id.searchview);
                floatingSearchView.bringToFront();
                recyclerView = dialog.findViewById(R.id.searchresultsrecyview);
                num = dialog.findViewById(R.id.searchnum);
                noresults = dialog.findViewById(R.id.noresults);
                circularProgressBar = dialog.findViewById(R.id.circularProgressBar);

                items = new ArrayList<>();

                floatingSearchView.requestFocus();
                floatingSearchView.setSearchFocused(true);

                recyclerView.setLayoutManager(new LinearLayoutManager(Home.this));
                final ItemAdapter adapter=new ItemAdapter(items);
                recyclerView.setAdapter(adapter);



                floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
                    @Override
                    public void onSearchTextChanged(final String oldQuery, final String newQuery) {

                        floatingSearchView.showProgress();
                        FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                items.clear();
                                floatingSearchView.hideProgress();
                                if (snapshot.exists()) {
                                    if (!newQuery.equals("")) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                            String currentQuery2 = newQuery.replaceAll(" ", "");

                                            if (dataSnapshot.child("name").getValue(String.class).toLowerCase().contains(currentQuery2.toLowerCase())) {
                                                items.add(new item(dataSnapshot.child("name").getValue(String.class), dataSnapshot.getKey()));
                                            }
                                        }

                                        adapter.notifyDataSetChanged();
                                    }

                                    if (items.size() == 0) {
                                        num.setVisibility(View.GONE);
                                        noresults.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                        circularProgressBar.setVisibility(View.GONE);

                                    } else {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        num.setText(items.size() + " result(s) found");
                                        num.setVisibility(View.VISIBLE);
                                        noresults.setVisibility(View.GONE);
                                        circularProgressBar.setVisibility(View.GONE);


                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
                    @Override
                    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

                    }

                    @Override
                    public void onSearchAction(final String currentQuery) {
                        hideKeyboard(floatingSearchView);
                    }
                });


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        search.setEnabled(true);
                    }
                }, 1000);


                dialog.show();

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Home.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navcontent, new HomeFragment())
                .commit();

        cart = findViewById(R.id.cart);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, MyCart.class);
                startActivity(intent);
                customType(Home.this, "fadein-to-fadeout");

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final LinearLayout holder = findViewById(R.id.holder);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                //this code is the real player behind this beautiful ui
                // basically, it's a mathemetical calculation which handles the shrinking of
                // our content view

                float scaleFactor = 9f;
                float slideX = drawerView.getWidth() * slideOffset;
                holder.setScaleX(1 - (slideOffset / scaleFactor));
                holder.setScaleY(1 - (slideOffset / scaleFactor));

                holder.setTranslationX(slideX);
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);// will remove all possible our aactivity's window bounds
        }

        drawer.addDrawerListener(toggle);
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.setDrawerElevation(0);

        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);

        name = headerView.findViewById(R.id.headname);
        propic = headerView.findViewById(R.id.headpropic);
        email = headerView.findViewById(R.id.heademail);


        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("name").getValue(String.class));
                email.setText(snapshot.child("email").getValue(String.class));

                Glide.with(Home.this).load(snapshot.child("profilepic").getValue(String.class)).into(propic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        Intent intent = null;

        switch (id) {
            case R.id.homeitem:
                fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.navcontent, fragment)
                        .commit();
                break;

            case R.id.categoriesitem:
                intent = new Intent(Home.this, Categories.class);
                startActivity(intent);
                customType(Home.this, "fadein-to-fadeout");
                break;

            case R.id.notificationitem:
                intent = new Intent(Home.this, Notifications.class);
                startActivity(intent);
                customType(Home.this, "fadein-to-fadeout");
                break;


            case R.id.helpitem:
                intent = new Intent(Home.this, Help.class);
                startActivity(intent);
                customType(Home.this, "fadein-to-fadeout");
                break;

            case R.id.myaccountitem:
                intent = new Intent(Home.this, MyAccount.class);
                startActivity(intent);
                customType(Home.this, "fadein-to-fadeout");
                break;

            case R.id.myordersitem:
                intent = new Intent(Home.this, MyOrders.class);
                startActivity(intent);
                customType(Home.this, "fadein-to-fadeout");
                break;

            case R.id.mywishlist:
                intent = new Intent(Home.this, MyWishlist.class);
                startActivity(intent);
                customType(Home.this, "fadein-to-fadeout");
                break;


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
    }


    private class ItemAdapter extends RecyclerView.Adapter<ViewItemsViewHolder> {

        List<item> results;

        public ItemAdapter(List<item> results) {
            this.results = results;
        }

        @NonNull
        @Override
        public ViewItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitemlayout, parent, false);
            return new ViewItemsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewItemsViewHolder holder, final int position) {

            FirebaseDatabase.getInstance().getReference().child("Items").child(results.get(position).getItemid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    itemdetails itemdetails = snapshot.getValue(com.example.itshop.itemdetails.class);

                    holder.name.setText(itemdetails.getName());
                    holder.rating.setText(itemdetails.getRating());


                    if (itemdetails.getDiscount() == null || itemdetails.getDiscount().equals("") || itemdetails.getDiscount().equals("0")) {
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        holder.price.setText(format.format(new BigDecimal(itemdetails.getPrice())));

                    } else {
                        double am = (Integer.valueOf(itemdetails.getPrice())) - (((Integer.valueOf(itemdetails.getDiscount())) * 0.01) * (Integer.valueOf(itemdetails.getPrice())));
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        holder.price.setText(format.format(new BigDecimal(String.valueOf(am))));
                        holder.discount.setText(format.format(new BigDecimal(itemdetails.getPrice())));
                        holder.discount.setVisibility(View.VISIBLE);
                        holder.discount.setPaintFlags(holder.discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    }

                    FirebaseDatabase.getInstance().getReference().child("Items").child(results.get(position).getItemid()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Glide.with(Home.this).load(dataSnapshot.child("image").getValue(String.class)).into(holder.imageView);
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(view);
                    Intent intent=new Intent(Home.this,ItemDetail.class);
                    intent.putExtra("itemid",results.get(position).getItemid());
                    startActivity(intent);
                    customType(Home.this,"left-to-right");
                }
            });
        }

        @Override
        public int getItemCount() {
            return results.size();
        }
    }


    private class ViewItemsViewHolder extends RecyclerView.ViewHolder {

        TextView name, rating, price, discount;
        ImageView imageView;

        public ViewItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.viewitemimage);
            rating = itemView.findViewById(R.id.viewitemrating);
            price = itemView.findViewById(R.id.viewitemprice);
            name = itemView.findViewById(R.id.viewitemname);
            discount = itemView.findViewById(R.id.discount);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}