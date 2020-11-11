package com.bharatbloodbank.bharatbloodbank;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.Adapter.ReviewsAdapter;
import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.Contact;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ReviewsActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Toolbar toolbar;
    RecyclerView recycler_reviews;
    ConnectivityReceiver connectivityReceiver;
    List<Contact> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.
                    TRANSPARENT);
        }
        Common.setTop(ReviewsActivity.this);

        /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        toolbar = findViewById(R.id.toolbar);
        recycler_reviews = findViewById(R.id.recycler_reviews);
        recycler_reviews.setLayoutManager(new LinearLayoutManager(this));
        recycler_reviews.setHasFixedSize(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LoadList();
    }

    private void LoadList() {
        FirebaseDatabase.getInstance().getReference("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        list.add(snapshot.getValue(Contact.class));
                    }
                    ReviewsAdapter reviewsAdapter = new ReviewsAdapter(list, ReviewsActivity.this);
                    recycler_reviews.removeAllViews();
                    recycler_reviews.setAdapter(reviewsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });
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
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
