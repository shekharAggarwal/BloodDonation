package com.bharatbloodbank.bharatbloodbank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    FButton btnSignIn, btnReg;
    TextView txtSlogan;

    ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Common.setTop(this);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnReg = findViewById(R.id.btnReg);
        txtSlogan = findViewById(R.id.txtSlogan);

    /*    final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (Common.isConnectedToInternet(getBaseContext())) {
                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Please waiting....");
                mDialog.setCancelable(false);
                mDialog.show();
                FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Objects.requireNonNull(user.getPhoneNumber())).exists()) {
                            mDialog.dismiss();
                            finish();
                            Common.currentUser = dataSnapshot.child(user.getPhoneNumber()).getValue(User.class);
                            assert Common.currentUser != null;
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            FirebaseAuth.getInstance().signOut();
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(this, "Check Internet Connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    startActivity(new Intent(MainActivity.this, LoginInActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Check Internet Connection !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Check Internet Connection !!", Toast.LENGTH_SHORT).show();
                }
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
    }


}