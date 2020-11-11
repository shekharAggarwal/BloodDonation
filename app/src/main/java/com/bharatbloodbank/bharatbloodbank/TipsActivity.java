package com.bharatbloodbank.bharatbloodbank;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bharatbloodbank.bharatbloodbank.Adapter.SliderAdapter;
import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.Slider;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TipsActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private ViewPager mSliderViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;
    ConnectivityReceiver connectivityReceiver;

    private static int p = 0, mCurrentPage;
    private static SliderAdapter sliderAdapter;
    private static Slider[] sliders;

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;
            if (mCurrentPage == 0) {
                mBtnNext.setEnabled(true);
                mBtnPrev.setEnabled(false);
                mBtnPrev.setVisibility(View.INVISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
                mBtnNext.setText("Next");
                mBtnPrev.setText("");
            } else if (position == mDots.length - 1) {
                mBtnNext.setEnabled(true);
                mBtnPrev.setEnabled(true);
                mBtnPrev.setVisibility(View.VISIBLE);
                mBtnNext.setText("Finish");
                mBtnPrev.setText("Back");
            } else {
                mBtnNext.setEnabled(true);
                mBtnPrev.setEnabled(true);
                mBtnPrev.setVisibility(View.VISIBLE);
                mBtnNext.setText("Next");
                mBtnPrev.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private Button mBtnNext, mBtnPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        Common.setBack(this);

        /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        mDotLayout = findViewById(R.id.linearLayout);
        mSliderViewPager = findViewById(R.id.slideViewPage);

        mBtnNext = findViewById(R.id.btnNext);
        mBtnPrev = findViewById(R.id.btnPrev);
        FirebaseDatabase.getInstance().getReference("Tips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                p = 0;
                sliders = new Slider[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Slider slider = snapshot.getValue(Slider.class);
                    sliders[p] = slider;
                    p++;
                }

                sliderAdapter = new SliderAdapter(TipsActivity.this, sliders);
                mSliderViewPager.setAdapter(sliderAdapter);
                addDotsIndicator(0);
                mSliderViewPager.addOnPageChangeListener(viewListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBtnNext.getText().equals("Finish")) {
                    Intent intent = new Intent(TipsActivity.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else
                    mSliderViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });

        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSliderViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });

    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[sliders.length];
        mDotLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorTitleWhite));
        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            setContentView(R.layout.layout_no_internet);
            findViewById(R.id.btnTry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);
        /*register connection status listener*/
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
    }

}
