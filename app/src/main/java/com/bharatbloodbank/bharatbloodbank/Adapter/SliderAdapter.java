package com.bharatbloodbank.bharatbloodbank.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bharatbloodbank.bharatbloodbank.Model.Slider;
import com.bharatbloodbank.bharatbloodbank.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SliderAdapter extends PagerAdapter {

    private final Context context;
    private final Slider[] sliders;

    public SliderAdapter(Context context, Slider[] sliders) {
        this.context = context;
        this.sliders = sliders;
    }

    @Override
    public int getCount() {
        return sliders.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        CircleImageView slideImageView = view.findViewById(R.id.slider_image);
        TextView slideHeading = view.findViewById(R.id.slider_heading);
        TextView slideData = view.findViewById(R.id.slider_data);

        Picasso.with(context).load(sliders[position].getImage()).into(slideImageView);
        slideHeading.setText(sliders[position].getHeading());
        slideData.setText(sliders[position].getData());
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
