package com.bharatbloodbank.bharatbloodbank;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.Adapter.CategoryAdapter;
import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.SendRequest;
import com.bharatbloodbank.bharatbloodbank.Model.Token;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nex3z.notificationbadge.NotificationBadge;

import java.io.Console;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {


    RecyclerView list_category;

    String[] CategoryName = {"Need Blood", "Tips", "Blood Bank", "Feedback", "Reviews", "Settings"};

    int[] CategoryIcon = {R.drawable.ic_search_black_24dp, R.drawable.i2, R.drawable.i6, R.drawable.i4, R.drawable.i10, R.drawable.i7};

    NotificationBadge badge;

    ImageView notification_icon;

    ConnectivityReceiver connectivityReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Common.setBack(Home.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateToken(FirebaseInstanceId.getInstance().getToken());

       /* final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        if (Common.currentUser == null) {
            FirebaseDatabase.getInstance().getReference("User")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Common.currentUser = dataSnapshot.getValue(User.class);
                            updateToken(FirebaseInstanceId.getInstance().getToken());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("ERROR", databaseError.getMessage());
                        }
                    });
        }
        if (!Common.isChecked && !Common.currentUser.getLastDonatedDate().equals("")) {
            int year, month, day;
            String[] arrOfStr = Common.currentUser.getLastDonatedDate().split("/", 3);
            day = Integer.parseInt(arrOfStr[0]);
            month = Integer.parseInt(arrOfStr[1]);
            year = Integer.parseInt(arrOfStr[2]);

            Common.currentUser.setLastDonated(String.valueOf(Common.checkMonth(year, month, day)));
            FirebaseDatabase.getInstance().getReference("User")
                    .child("+91" + Common.currentUser.getPhone()).setValue(Common.currentUser);
        }

        list_category = findViewById(R.id.recycler_main);
        list_category.setLayoutManager(new GridLayoutManager(this, 2));
        list_category.setHasFixedSize(true);

        CategoryAdapter categoryAdapter = new CategoryAdapter(CategoryName, CategoryIcon, this);
        list_category.setAdapter(categoryAdapter);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View nv = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseDatabase.getInstance().getReference("Share").setValue("http");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        View view = menu.findItem(R.id.action_notification).getActionView();
        badge = view.findViewById(R.id.badge);

        FirebaseDatabase.getInstance().getReference("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.Noti = 0;
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SendRequest sendRequest = snapshot.getValue(SendRequest.class);
                        assert sendRequest != null;
                        if (sendRequest.getRequestPhone().equals("+91" + Common.currentUser.getPhone()) && sendRequest.isState())
                            Common.Noti++;
                    }
                }
                updateNotification();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        notification_icon = view.findViewById(R.id.img_notification);
        notification_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Notification.class));
            }
        });
        return true;
    }

    private void updateNotification() {

        if (badge == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Common.Noti == 0) {
                    badge.setVisibility(View.INVISIBLE);
                } else {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(Common.Noti));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notification) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(Home.this, Profile.class));
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            FirebaseDatabase.getInstance().getReference("Tokens").child("+91" + Common.currentUser.getPhone()).removeValue();
            Intent intent = new Intent(Home.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_request) {
            startActivity(new Intent(Home.this, RequestActivity.class));
        } else if (id == R.id.nav_share) {
            shareLink();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        recreate();
        FirebaseDatabase.getInstance().getReference("Requests")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.Noti = 0;
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                SendRequest sendRequest = snapshot.getValue(SendRequest.class);
                                assert sendRequest != null;
                                if (sendRequest.getRequestPhone()
                                        .equals("+91" + Common.currentUser.getPhone())
                                        && sendRequest.isState())
                                    Common.Noti++;
                            }
                        }
                        updateNotification();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR", databaseError.getMessage());
                    }
                });
    }

    private void updateToken(String token) {
        try{
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token data = new Token(token, true, true);
            tokens.child("+91" + Common.currentUser.getPhone()).setValue(data);
        }catch(Exception e){
            Log.d("TOKEN ERROR HOME",e.getMessage());
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
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectivityReceiver);
    }

    void shareLink() {
        FirebaseDatabase.getInstance().getReference("Share")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Link = dataSnapshot.getValue(String.class);
                        try {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BharatRaktDonation");
                            String shareMessage = "\nLet me recommend you this application\n\n";
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage + Link);
                            startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR", databaseError.getMessage());
                    }
                });

    }
}
