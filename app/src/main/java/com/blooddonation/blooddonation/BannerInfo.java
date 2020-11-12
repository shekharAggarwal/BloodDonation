package com.blooddonation.blooddonation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blooddonation.blooddonation.Model.Banner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BannerInfo extends AppCompatActivity {

    ImageView bannerimg;
    String id,imgfull;
    TextView bannerName, bannerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banner_info);

        bannerimg = findViewById(R.id.bannerImage);
        bannerName = findViewById(R.id.bannerName);
        bannerInfo = findViewById(R.id.bannerDetail);
        if (getIntent() != null)
            id = getIntent().getStringExtra("id");

        FirebaseDatabase.getInstance().getReference("Banner").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Banner banner = dataSnapshot.child(id).getValue(Banner.class);
                bannerName.setText(banner.getBannerName());
                bannerInfo.setText(banner.getBannerDetail());
                imgfull = banner.getBannerImage();
                Picasso.with(BannerInfo.this).load(banner.getBannerImage()).error(getResources().getDrawable(R.drawable.android_gradent_list)).into(bannerimg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        bannerimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                @SuppressLint("InflateParams") View showImg = inflater.inflate(R.layout.show_img, null);

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(BannerInfo.this);
                alertDialog.setView(showImg);
                ImageView img = showImg.findViewById(R.id.bannerFull);
                Picasso.with(BannerInfo.this).load(imgfull).error(getResources().getDrawable(R.drawable.android_gradent_list)).into(img);
                alertDialog.show();


            }
        });
    }

}
