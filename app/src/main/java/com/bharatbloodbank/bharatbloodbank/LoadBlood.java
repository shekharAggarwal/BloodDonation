package com.bharatbloodbank.bharatbloodbank;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.Adapter.BloodDonorAdapter;
import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.MyResponse;
import com.bharatbloodbank.bharatbloodbank.Model.Notification;
import com.bharatbloodbank.bharatbloodbank.Model.SendRequest;
import com.bharatbloodbank.bharatbloodbank.Model.Sender;
import com.bharatbloodbank.bharatbloodbank.Model.Token;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.bharatbloodbank.bharatbloodbank.Remote.APIService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadBlood extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    String State, District, City, Blood;
    User[] users;
    int i = 0;

    ConnectivityReceiver connectivityReceiver;


    ArrayList<User> values = new ArrayList<User>();
    RecyclerView recyclerView;
    MenuItem item;
    APIService mService;
    private ProgressDialog mDialog;
    boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_blood);

        /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.
                    TRANSPARENT);
        }

        Common.setTop(this);

        mService = Common.getFCMClient();

        if (getIntent() != null) {
            State = getIntent().getStringExtra("State");
            District = getIntent().getStringExtra("District");
            if (getIntent().getStringExtra("City") != null)
                City = getIntent().getStringExtra("City");
            Blood = getIntent().getStringExtra("Blood");

        }

        recyclerView = findViewById(R.id.recycler_sort);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        mDialog = new ProgressDialog(LoadBlood.this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);

        loadList(State, District, City, Blood);

    }

    private void loadList(final String state, final String district, final String city, final String blood) {
        mDialog.show();
        i = 0;
        users = null;
        FirebaseDatabase.getInstance().getReference("User")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        users = new User[(int) dataSnapshot.getChildrenCount() - 1];

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final User user = snapshot.getValue(User.class);
                            assert user != null;
                            if (user.getState().equalsIgnoreCase(state)
                                    && user.getDistrict().equalsIgnoreCase(district)
                                    && Integer.parseInt(user.getLastDonated()) > 3
                                    && user.getBloodGroup().equalsIgnoreCase(blood)
                                    && !user.getPhone().equals(Common.currentUser.getPhone())) {

                                if (!user.getCity().isEmpty()) {
                                    if (city != null) {
                                        if (user.getCity().equalsIgnoreCase(city)) {
                                            users[i] = user;
                                            Common.userPhone.add("+91" + user.getPhone());
                                            i++;
                                        }
                                    } else {
                                        users[i] = user;
                                        Common.userPhone.add("+91" + user.getPhone());
                                        i++;

                                    }
                                } else {
                                    users[i] = user;
                                    Common.userPhone.add("+91" + user.getPhone());
                                    i++;
                                }
                            }
                        }
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
                        if (dataSnapshot.hasChildren() && users.length != 0) {
                            for (int q = 0; q < users.length; q++) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    SendRequest sendRequest = snapshot.getValue(SendRequest.class);
                                    assert sendRequest != null;
                                    if (users[q] != null) {
                                        if (sendRequest.getUserPhone().equals("+91" + Common.currentUser.getPhone())
                                                && sendRequest.getRequestPhone().equals("+91" + users[q].getPhone())) {
                                            check = false;
                                            for (int p = 0; p < Common.userPhone.size(); p++) {
                                                if (Common.userPhone.get(p).equals("+91" + users[q].getPhone())) {
                                                    Common.userPhone.remove(p);
                                                    break;
                                                }//end of if
                                            }//end of for
                                        }/*end of if*/
                                    }//end of if
                                }//end of for
                                if (check) {
                                    values.add(users[q]);
                                }
                            }//end of for
                            users = null;
                            int d = 0;
                            users = new User[values.size()];
                            for (int i = 0; i < values.size(); i++) {
                                if (values.get(i) != null) {
                                    users[d] = values.get(i);
                                    d++;
                                }
                            }
                        }
                        if (Common.userPhone.size() == 0) {
                            mDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoadBlood.this);
                            builder.setMessage("No Match Found");
                            builder.setCancelable(false);
                            builder.setTitle("ERROR !!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(LoadBlood.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                            builder.show();
                        } else {
                            mDialog.dismiss();
                            values = new ArrayList<>();
                            for (int n = 0; n < users.length; n++) {
                                if (users[n] != null)
                                    values.add(users[n]);
                            }
                            users = null;
                            users = new User[values.size()];
                            int o = 0;
                            for (int i = 0; i < values.size(); i++) {
                                if (values.get(i) != null) {
                                    users[o] = values.get(i);
                                    o++;
                                }
                            }
                            BloodDonorAdapter bloodDonorAdapter = new BloodDonorAdapter(users, LoadBlood.this);
                            recyclerView.setAdapter(bloodDonorAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR", databaseError.getMessage());
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.load_list, menu);
        item = menu.findItem(R.id.action_send_to_all);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_to_all) {
            sendToAll();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendToAll() {

        if (Common.userPhone.size() != 0) {
            for (int i = 0; i < Common.userPhone.size(); i++) {
                SendRequest send_Request = new SendRequest("+91" + Common.currentUser.getPhone(),
                        Common.userPhone.get(i),
                        "0",
                        true);
                FirebaseDatabase.getInstance().getReference("Requests").child(String.valueOf(UUID.randomUUID())).setValue(send_Request);
                sendAllNotification(Common.userPhone.get(i));
            }
            Toast.makeText(this, "Notification Sent To All", Toast.LENGTH_SHORT).show();
            item.setVisible(false);
            BloodDonorAdapter bloodDonorAdapter = new BloodDonorAdapter(users, LoadBlood.this);
            recyclerView.setAdapter(bloodDonorAdapter);
        } else {
            item.setVisible(false);
            Toast.makeText(this, "No Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAllNotification(final String request) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.child(request)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);
                            Notification notification = new Notification("Message", "Need Your Blood");
                            Sender content = new Sender(token.getToken(), notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                            if (response.body().success == 1) {
                                                Log.d("Sent", String.valueOf(response.body().success));
                                            } else {
                                                Toast.makeText(LoadBlood.this, "Failed To Send Notification !", Toast.LENGTH_SHORT).show();
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
                        Log.e("ERROR", databaseError.getMessage());

                    }
                });
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            setContentView(R.layout.layout_no_internet);
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.
                        FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.
                        FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.
                        TRANSPARENT);
            }
            Common.setTop(this);*/

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
