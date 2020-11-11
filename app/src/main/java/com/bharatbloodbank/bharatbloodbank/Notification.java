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
import com.bharatbloodbank.bharatbloodbank.Model.MyResponse;
import com.bharatbloodbank.bharatbloodbank.Model.SendRequest;
import com.bharatbloodbank.bharatbloodbank.Model.Sender;
import com.bharatbloodbank.bharatbloodbank.Model.Token;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.bharatbloodbank.bharatbloodbank.Remote.APIService;
import com.bharatbloodbank.bharatbloodbank.ViewHolder.NotificationHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notification extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    FirebaseRecyclerAdapter<SendRequest, NotificationHolder> adapter;
    APIService mService;
    RecyclerView recyclerView;
    ConnectivityReceiver connectivityReceiver;

    Toolbar toolbar;
    LinearLayout ln1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.
                    TRANSPARENT);
        }
        Common.setTop(Notification.this);
       /* final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        ln1 = findViewById(R.id.ln1);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mService = Common.getFCMClient();
        recyclerView = findViewById(R.id.load_request);
        recyclerView.setLayoutManager(new LinearLayoutManager(Notification.this));
        recyclerView.setHasFixedSize(true);

        FirebaseDatabase.getInstance().getReference("Requests")
                .orderByChild("requestPhone").equalTo("+91" + Common.currentUser.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            ln1.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            ln1.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            loadList();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR", databaseError.getMessage());
                    }
                });

    }

    private void loadList() {
        //setting quary
        Query List = FirebaseDatabase.getInstance().getReference("Requests").orderByChild("requestPhone").equalTo("+91" + Common.currentUser.getPhone());
        //setting data for query
        final FirebaseRecyclerOptions<SendRequest> loadList = new FirebaseRecyclerOptions.Builder<SendRequest>().setQuery(List, SendRequest.class).build();
        //loading list
        adapter = new FirebaseRecyclerAdapter<SendRequest, NotificationHolder>(loadList) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationHolder holder, final int i,
                                            @NonNull final SendRequest model) {
                //setting Data into view

                if (model.getStatus().equals("1"))
                    holder.imgPhone.setVisibility(View.VISIBLE);

                holder.imgPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ActivityCompat.checkSelfPermission(Notification.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

                FirebaseDatabase.getInstance().getReference("User").child(model.getUserPhone())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                assert user != null;
                                holder.txtName.setText(user.getName());
                                holder.txtGroup.setText(user.getBloodGroup());
                                if (!user.getImage().equals(" ") && user.getImage() != null)
                                    Picasso.with(Notification.this).load(user.getImage()).into(holder.userImage);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("ERROR", databaseError.getMessage());
                            }
                        });

                holder.txtStatus.setText(Common.getStatus(model.getStatus()));

                //checking data
                FirebaseDatabase.getInstance().getReference("Requests").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            SendRequest sendRequest = snapshot.getValue(SendRequest.class);

                            assert sendRequest != null;
                            if (sendRequest.getUserPhone().equals(model.getUserPhone())
                                    && sendRequest.getRequestPhone().equals("+91" + Common.currentUser.getPhone())
                                    && sendRequest.getStatus().equals("1")) {
                                holder.btnAccept.setVisibility(View.GONE);
                                holder.btnCancel.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR", databaseError.getMessage());
                    }
                });

                //button action
                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.child("+91" + Common.currentUser.getPhone()).getValue(User.class);
                                user.setLastDonated("0");
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                user.setLastDonatedDate(String.valueOf(sdf.format(new Date())));
                                FirebaseDatabase.getInstance().getReference("User").child("+91" + Common.currentUser.getPhone()).setValue(user);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("ERROR", databaseError.getMessage());
                            }
                        });
                        FirebaseDatabase.getInstance().getReference("Requests")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            SendRequest sendRequest = snapshot.getValue(SendRequest.class);
                                            assert sendRequest != null;
                                            if (sendRequest.getUserPhone().equals(model.getUserPhone())
                                                    && sendRequest.getRequestPhone().equals("+91" + Common.currentUser.getPhone())) {
                                                sendRequest.setStatus("1");
                                                sendRequest.setState(false);
                                                FirebaseDatabase.getInstance().getReference("Requests").child(adapter.getRef(i).getKey()).setValue(sendRequest);
                                                holder.btnAccept.setVisibility(View.GONE);
                                                holder.btnCancel.setVisibility(View.GONE);
                                                sendNotification(model.getUserPhone(), Common.currentUser.getName());
                                            } else if (sendRequest.getUserPhone().equals(model.getUserPhone())) {
                                                FirebaseDatabase.getInstance().getReference("Requests").child(snapshot.getKey()).removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("ERROR", databaseError.getMessage());

                                    }
                                });
                    }
                });

                holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("Requests")
                                .child(adapter.getRef(i).getKey()).removeValue();
                        Toast.makeText(Notification.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(Notification.this).inflate(R.layout.item_notification_layout, parent, false);
                return new NotificationHolder(view);
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

    private void sendNotification(final String request, final String sender) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.child(request)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);
                            com.bharatbloodbank.bharatbloodbank.Model.Notification notification = new com.bharatbloodbank.bharatbloodbank.Model.Notification("Your Request is Accepted by " + sender, "UPDATE");
                            Sender content = new Sender(token.getToken(), notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            Log.d("response", "onResponse: " + response.body().failure);
                                            if (response.body().success == 1) {
                                                Toast.makeText(Notification.this, "Request Updated!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Notification.this, "Failed To Send Notification !", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });
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

        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
    }
}
