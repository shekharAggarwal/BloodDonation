package com.bharatbloodbank.bharatbloodbank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class SplashActivity extends AppCompatActivity {

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Common.setTop(this);

        /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        text = findViewById(R.id.text);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    if (Common.isConnectedToInternet(getBaseContext())) {
                        final ProgressDialog mDialog = new ProgressDialog(SplashActivity.this);
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
                                    Intent intent = new Intent(SplashActivity.this, Home.class);
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
                        Toast.makeText(SplashActivity.this, "Check Internet Connection !!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        }, 5000);
    }
}
