package com.example.itshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.itshop.Fragments.MyOrders;

import static maes.tech.intentanim.CustomIntent.customType;

public class OrderPaymentStatus extends AppCompatActivity {

    TextView gohome,vieworder,ordertext,paymenttext;
    LottieAnimationView lottieAnimationView;
    String status;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment_status);

        gohome = findViewById(R.id.returnhome);
        vieworder = findViewById(R.id.vieworder);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        status = getIntent().getStringExtra("status");
        paymenttext = findViewById(R.id.paymenttext);
        ordertext = findViewById(R.id.ordertext);
        imageView=findViewById(R.id.paymentimage);

        if (status.equals("success"))
        {
            paymenttext.setText("Payment Success");
            ordertext.setText("Your Order has been placed");
            lottieAnimationView.setAnimation(R.raw.paymentsuccess);
            imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.success));
            gohome.setVisibility(View.VISIBLE);
            vieworder.setVisibility(View.VISIBLE);
        }
        else {
            paymenttext.setText("Payment Failed");
            ordertext.setText("Your Order has been cancelled");
            lottieAnimationView.setAnimation(R.raw.paymentfailed);
            imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.failed));
            gohome.setVisibility(View.VISIBLE);
        }

        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrderPaymentStatus.this,Home.class));
                finish();
                customType(OrderPaymentStatus.this,"fadein-to-fadeout");
            }
        });

        vieworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrderPaymentStatus.this,MyOrders.class);
                intent.putExtra("type","ok");
                startActivity(intent);
                finish();
                customType(OrderPaymentStatus.this,"fadein-to-fadeout");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OrderPaymentStatus.this,Home.class));
        customType(OrderPaymentStatus.this,"fadein-to-fadeout");
    }
}