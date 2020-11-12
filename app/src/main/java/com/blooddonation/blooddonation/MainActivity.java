package com.blooddonation.blooddonation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

    FButton btnSignIn;
    TextView txtSlogan;

 /*   @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cf.otf")
                .setFontAttrId(R.attr.fontPath).build());*/
        setContentView(R.layout.activity_main);


        btnSignIn = findViewById(R.id.btnSignIn);
        txtSlogan = findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        Toast.makeText(this, ""+user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
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
                            if (Common.currentUser.getTypeUser().equals("Hostel") || Common.currentUser.getTypeUser().equals("Day Scholar")) {
                                int last = Integer.parseInt(Common.currentUser.getBatch()) + 5;
                                if (last == Calendar.getInstance().get(Calendar.YEAR)) {
                                    FirebaseDatabase.getInstance().getReference("User").child("+91" + Common.currentUser.getPhone()).removeValue();
                                    Toast.makeText(MainActivity.this, "Your Account Is Deleted", Toast.LENGTH_SHORT).show();
                                } else
                                    startActivity(new Intent(MainActivity.this, Home.class));
                            } else
                                startActivity(new Intent(MainActivity.this, Home.class));
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

                    startActivity(new Intent(MainActivity.this, SignIn.class));
                } else {
                    Toast.makeText(MainActivity.this, "Check Internet Connection !!", Toast.LENGTH_SHORT).show();
//                    return;
                }
            }
        });


    }

}