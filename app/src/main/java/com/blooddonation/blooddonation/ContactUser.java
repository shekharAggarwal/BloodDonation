package com.blooddonation.blooddonation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;

public class ContactUser extends AppCompatActivity {
    TextView userName, userGroup, userGender, userDonated, userRequested, userAddress, userPhone, userDOB;
    CircleImageView userImage;
    FButton btnCall;
    User user;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_user);

        userName = findViewById(R.id.userName);
        userGroup = findViewById(R.id.userGroup);
        userGender = findViewById(R.id.userGender);
        userDonated = findViewById(R.id.userDonated);
        userRequested = findViewById(R.id.userRequested);
        userAddress = findViewById(R.id.userAddress);
        userPhone = findViewById(R.id.userPhone);
        userDOB = findViewById(R.id.userDOB);
        userImage = findViewById(R.id.userImage);
        btnCall = findViewById(R.id.btnCall);


        if (getIntent() != null) {
            userId = getIntent().getStringExtra("ContactUser");
        }
        if (!userId.isEmpty()) {
            if (Common.isConnectedToInternet(getBaseContext())) {
                getDetails(userId);
            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }


    }

    private void getDetails(final String userId) {
        FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(userId).getValue(User.class);
                userName.setText(user.getName());
                userGroup.setText(user.getBloodGroup());
                userGender.setText(user.getGender());
                userDonated.setText(user.getDonated());
                userRequested.setText(user.getRequested());
                userAddress.setText(user.getCity() + " - " + user.getState());
                userPhone.setText(user.getPhone());
                userDOB.setText(user.getAge());
                Picasso.with(ContactUser.this).load(user.getImage()).into(userImage);


                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + "+91" + user.getPhone()));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
