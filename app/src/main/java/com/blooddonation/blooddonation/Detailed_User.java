package com.blooddonation.blooddonation;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Model.MyResponse;
import com.blooddonation.blooddonation.Model.Notification;
import com.blooddonation.blooddonation.Model.Sender;
import com.blooddonation.blooddonation.Model.Token;
import com.blooddonation.blooddonation.Model.User;
import com.blooddonation.blooddonation.Model.sendRequest;
import com.blooddonation.blooddonation.Remote.APIService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detailed_User extends AppCompatActivity {

    TextView userName, userGroup, userGender, userDonated, userRequested, userPhone;
    CircleImageView img;
    FirebaseDatabase database;
    DatabaseReference userDetail;
    String userId = "";
    Button btnRequest;
    User detailUser;
    sendRequest request;
    APIService mService;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed__user);

        mDialog = new ProgressDialog(Detailed_User.this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);

        mService = Common.getFCMClient();
        userName = findViewById(R.id.userName);
        userGroup = findViewById(R.id.userGroup);
        userPhone = findViewById(R.id.userPhone);
        userGender = findViewById(R.id.userGender);
        userDonated = findViewById(R.id.userDonated);
        userRequested = findViewById(R.id.userRequested);
        btnRequest = findViewById(R.id.btnRequest);
        img = findViewById(R.id.userImage);
        database = FirebaseDatabase.getInstance();
        userDetail = database.getReference("User");

        if (getIntent() != null) {
            userId = getIntent().getStringExtra("UserDetail");
        }
        if (!userId.isEmpty()) {
            if (Common.isConnectedToInternet(getBaseContext())) {

                mDialog.show();
                getDetails(userId);
            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }


    }

    private void getDetails(final String userId) {
        userDetail.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                detailUser = dataSnapshot.getValue(User.class);
                Picasso.with(Detailed_User.this).load(detailUser.getImage()).into(img);
                userName.setText(detailUser.getName());
                userGroup.setText(detailUser.getBloodGroup());
                userGender.setText(detailUser.getGender());
                userPhone.setText(detailUser.getPhone());
                userDonated.setText(detailUser.getDonated());
                userRequested.setText(detailUser.getRequested());

                FirebaseDatabase.getInstance().getReference("Request").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            request = postSnapshot.getValue(sendRequest.class);
                            if (request.getPhoneRequested().equals("+91" + Common.currentUser.getPhone())) {
                                if (request.getPhoneReceived().equals(userId)) {
                                    if (request.getStatus().equals("0")) {
                                        btnRequest.setText("Request Sent");
                                        btnRequest.setEnabled(false);
                                    } else if (request.getStatus().equals("1")) {
                                        btnRequest.setText("Request Accepted");
                                        btnRequest.setEnabled(false);
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                btnRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference("Request")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        request = new sendRequest("+91" + Common.currentUser.getPhone(),
                                                userId,
                                                "0",
                                                detailUser.getName(),
                                                detailUser.getImage());


                                        detailUser.setRequested(String.valueOf(Integer.parseInt(detailUser.getRequested()) + 1));
                                        FirebaseDatabase.getInstance().getReference("User")
                                                .child("+91" + detailUser.getPhone()).setValue(detailUser);
                                        String requested = String.valueOf(System.currentTimeMillis());

                                        FirebaseDatabase.getInstance().getReference("Request")
                                                .child(requested).setValue(request);
                                        sendNotification(userId);
                                        btnRequest.setText("Request Sent");
                                        btnRequest.setEnabled(false);
//                                Toast.makeText(Detailed_User.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDialog.dismiss();
    }

    private void sendNotification(final String shipperPhone) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.child(shipperPhone)
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
                                                Toast.makeText(Detailed_User.this, "Request Sent!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Detailed_User.this, "Failed To Send Notification !", Toast.LENGTH_SHORT).show();
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

                    }
                });
    }

}
