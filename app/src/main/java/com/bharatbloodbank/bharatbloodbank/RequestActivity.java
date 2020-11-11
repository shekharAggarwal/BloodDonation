package com.bharatbloodbank.bharatbloodbank;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.SendRequest;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.bharatbloodbank.bharatbloodbank.Remote.APIService;
import com.bharatbloodbank.bharatbloodbank.ViewHolder.RequestViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class RequestActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    FirebaseRecyclerAdapter<SendRequest, RequestViewHolder> adapter;
    APIService mService;
    RecyclerView recyclerView;
    Toolbar toolbar;
    ConnectivityReceiver connectivityReceiver;
    LinearLayout ln_no_request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.
                    TRANSPARENT);
        }
        Common.setTop(RequestActivity.this);

        /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        mService = Common.getFCMClient();
        recyclerView = findViewById(R.id.load_request);
        recyclerView.setLayoutManager(new LinearLayoutManager(RequestActivity.this));
        recyclerView.setHasFixedSize(true);

        ln_no_request = findViewById(R.id.ln_no_request);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FirebaseDatabase.getInstance().getReference("Requests").orderByChild("userPhone")
                .equalTo("+91" + Common.currentUser.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            ln_no_request.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            ln_no_request.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            loadList();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Error", databaseError.getMessage());
                        Toast.makeText(RequestActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
    }

    private void loadList() {
        //setting quary
        Query List = FirebaseDatabase.getInstance().getReference("Requests").orderByChild("userPhone").equalTo("+91" + Common.currentUser.getPhone());
        //setting data for query
        final FirebaseRecyclerOptions<SendRequest> loadList = new FirebaseRecyclerOptions.Builder<SendRequest>().setQuery(List, SendRequest.class).build();
        //loading list
        adapter = new FirebaseRecyclerAdapter<SendRequest, RequestViewHolder>(loadList) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, final int i,
                                            @NonNull final SendRequest model) {
                //setting Data into view
                if (model.getStatus().equals("1")) {
                    holder.imgPhone.setVisibility(View.VISIBLE);
                    holder.imgShare.setVisibility(View.VISIBLE);
                }

                holder.imgPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ActivityCompat.checkSelfPermission(RequestActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{Manifest.permission.CALL_PHONE}, Common.REQUEST_CODE);
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + model.getRequestPhone()));
                            startActivity(intent);

                        }
                    }
                });

                final User[] user = new User[1];
                FirebaseDatabase.getInstance().getReference("User").child(model.getRequestPhone())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                user[0] = dataSnapshot.getValue(User.class);
                                holder.txtName.setText(user[0].getName());
                                holder.txtGroup.setText(user[0].getBloodGroup());
                                if (!user[0].getImage().equals(" ") && user[0].getImage() != null) {
                                    Picasso.with(RequestActivity.this).load(user[0].getImage()).into(holder.userImage);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                holder.txtStatus.setText(Common.getStatus(model.getStatus()));

                holder.imgShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Bharat Rakt Donation");
                            String shareMessage = "Bharat Rakt Donation\nName:- " + user[0].getName() + "\nPhone:- " +
                                    user[0].getPhone() + "\nBlood Group:- " + user[0].getBloodGroup();
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(RequestActivity.this).inflate(R.layout.item_request_layout, parent, false);
                return new RequestViewHolder(view);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.removeAllViews();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
