package com.example.itshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ViewImage extends AppCompatActivity {

    ArrayList<String> images;
    SliderView sliderView;
    int posi;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        images = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        sliderView=findViewById(R.id.imageSlider);

        SliderAdapter adapter = new SliderAdapter(ViewImage.this, images);

        sliderView.setSliderAdapter(adapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setVisibility(View.VISIBLE);

        posi=getIntent().getIntExtra("posi",0);

        sliderView.setCurrentPagePosition(posi);
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
        public SliderAdapter.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
            return new SliderAdapter.SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapter.SliderAdapterVH viewHolder, final int position) {

            String sliderItem = items.get(position);

            Glide.with(ViewImage.this)
                    .load(sliderItem)
                    .into(viewHolder.imageViewBackground);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            View itemView;
            PhotoView imageViewBackground;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.photoview);
                this.itemView = itemView;
            }
        }

    }

}