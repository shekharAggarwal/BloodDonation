package com.blooddonation.blooddonation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Model.Banner;
import com.blooddonation.blooddonation.Model.Token;
import com.blooddonation.blooddonation.Model.User;
import com.blooddonation.blooddonation.Model.sendRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    TextView userName, userGroup, userGender, userDonated, userRequested, userAddress, userPhone, userDOB;
    CircleImageView userImage;
    Uri filePath;
    LinearLayout l1, l2;
    User user;
    String[] block = {"Block A", "Block B", "Block C", "Block D"},
            state = {"Rajasthan", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
                    "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir",
                    "Mizoram", "Nagaland", "Odisha", "Punjab", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
                    "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya",
                    "Uttar Pradesh", "Uttarakhand", "West Bengal"};
    String updatePhoneNu;
    CountDownTimer countDownTimer;
    boolean isRunning = false;
    TextView timer;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    ProgressDialog Dialog;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        StartFirebaseLogin();
        /**************************************************************************************************/

        userName = findViewById(R.id.userName);
        userGroup = findViewById(R.id.userGroup);
        userGender = findViewById(R.id.userGender);
        userDonated = findViewById(R.id.userDonated);
        userRequested = findViewById(R.id.userRequested);
        userAddress = findViewById(R.id.userAddress);
        userPhone = findViewById(R.id.userPhone);
        userDOB = findViewById(R.id.userDOB);
        userImage = findViewById(R.id.userImage);
        l1 = findViewById(R.id.Address);
        l2 = findViewById(R.id.Phone);
        /**************************************************************************************************/

        userName.setText(Common.currentUser.getName());
        userGroup.setText(Common.currentUser.getBloodGroup());
        userGender.setText(Common.currentUser.getGender());
        userDonated.setText(Common.currentUser.getDonated());
        userRequested.setText(Common.currentUser.getRequested());
        if (Common.currentUser.getTypeUser().equals("Hostel"))
            userAddress.setText(Common.currentUser.getRoom() + "\n" + Common.currentUser.getBlock());
        else if (Common.currentUser.getTypeUser().equals("Day Scholar"))
            userAddress.setText(Common.currentUser.getAddress() + ",\n" + Common.currentUser.getCity() + "," + Common.currentUser.getState());
        else if (Common.currentUser.getTypeUser().equals("Outsider"))
            userAddress.setText(Common.currentUser.getAddress() + ",\n" + Common.currentUser.getCity() + "," + Common.currentUser.getState());
        userPhone.setText(Common.currentUser.getPhone());
        userDOB.setText(Common.currentUser.getAge());
        Picasso.with(Profile.this).load(Common.currentUser.getImage()).into(userImage);
        /**************************************************************************************************/

        /**************************************************************************************************/

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        /**************************************************************************************************/

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.currentUser.getTypeUser().equals("Hostel"))
                    UpdateHostelAddress();
                else
                    UpdateDatScrAddress();
            }
        });

        /**************************************************************************************************/

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhone();
            }
        });

        /**************************************************************************************************/

    }


    /**************************************************************************************************/

    private void updatePhone() {
        LayoutInflater inflater = this.getLayoutInflater();
        View updatePhone = inflater.inflate(R.layout.update_phone, null);

        final MaterialEditText edtPhone = updatePhone.findViewById(R.id.edtPhone);


        final AlertDialog alertDialog = new AlertDialog.Builder(Profile.this)
                .setTitle("One more Step!")
                .setMessage("Update Phone Number")
                .setCancelable(false)
                .setView(updatePhone)
                .setIcon(R.drawable.ic_call_black_24dp)
                .setPositiveButton("UPDATE", null)
                .setNegativeButton("CANCEL", null)
                .show();
        Button postiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        postiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPhone.getText().toString().isEmpty() || edtPhone.getText().toString().length() != 10) {
                    edtPhone.setError("Enter Correct Phone");
                } else {
                    FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("+91" + edtPhone.getText().toString()).exists()) {
                                Toast.makeText(Profile.this, "Phone Number Registered !!", Toast.LENGTH_SHORT).show();
                                edtPhone.setError("Enter Correct Phone");
                            } else {
                                alertDialog.dismiss();
                                updatePhoneNu = edtPhone.getText().toString();
                                PhoneAuthOptions options =
                                        PhoneAuthOptions.newBuilder()
                                                .setPhoneNumber("+91" + edtPhone.getText().toString())      // Phone number to verify
                                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                .setActivity(Profile.this)                 // Activity (for callback binding)
                                                .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                                                .build();
                                PhoneAuthProvider.verifyPhoneNumber(options);
                                Dialog = new ProgressDialog(Profile.this);
                                Dialog.setMessage("Please waiting....");
                                Dialog.setCancelable(false);
                                Dialog.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    private void updatePhoneOTP() {
        LayoutInflater inflater = this.getLayoutInflater();
        View updatePhoneOTP = inflater.inflate(R.layout.update_phone_otp, null);

        final MaterialEditText edtOtp = updatePhoneOTP.findViewById(R.id.edtOtp);
        timer = updatePhoneOTP.findViewById(R.id.timer);


        final AlertDialog alertDialog = new AlertDialog.Builder(Profile.this)
                .setTitle("One more Step!")
                .setMessage("Enter OTP")
                .setCancelable(false)
                .setView(updatePhoneOTP)
                .setIcon(R.drawable.ic_call_black_24dp)
                .setPositiveButton("UPDATE", null)
                .setNegativeButton("CANCEL", null)
                .show();
        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                isRunning = true;
                if (millisUntilFinished / 1000 == 10)
                    timer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                timer.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                alertDialog.dismiss();
                Toast.makeText(Profile.this, "Update Failed !!", Toast.LENGTH_SHORT).show();
            }
        };
        countDownTimer.start();
        Button postiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        postiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtOtp.getText().toString().isEmpty() || edtOtp.getText().toString().length() != 6) {
                    edtOtp.setError("Enter Otp");
                } else {
                    alertDialog.dismiss();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, edtOtp.getText().toString());
                    SigninWithPhone(credential);
                }
            }
        });
    }

    private void StartFirebaseLogin() {

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(Profile.this, "verification completed", Toast.LENGTH_SHORT).show();
                Dialog.dismiss();
                SigninWithPhone(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Profile.this, "verification failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(Profile.this, "OTP sent ", Toast.LENGTH_SHORT).show();
                Dialog.dismiss();
                updatePhoneOTP();
            }
        };
    }

    private void SigninWithPhone(final PhoneAuthCredential credential) {

        final ProgressDialog mDialog = new ProgressDialog(Profile.this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);
        mDialog.show();

        FirebaseAuth.getInstance().getCurrentUser().updatePhoneNumber(credential)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (isRunning)
                                countDownTimer.cancel();
                            FirebaseDatabase.getInstance().getReference("Tokens")
                                    .child("+91" + Common.currentUser.getPhone()).removeValue();

                            FirebaseDatabase.getInstance().getReference("Request")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                                sendRequest request = postSnapshot.getValue(sendRequest.class);
                                                if (request.getPhoneReceived() != null) {
                                                    if (request.getPhoneReceived().equals("+91" + Common.currentUser.getPhone())) {
                                                        request.setPhoneReceived("+91" + updatePhoneNu);
                                                    }
                                                }
                                                if (request.getStatus().equals("0")) {
                                                    if (request.getPhoneRequested().startsWith("+91" + Common.currentUser.getPhone())) {
                                                        request.setPhoneRequested("+91" + updatePhoneNu + ":0");
                                                    }
                                                } else if ((request.getStatus().equals("1"))) {
                                                    if (request.getPhoneRequested().startsWith("+91" + Common.currentUser.getPhone())) {
                                                        request.setPhoneRequested("+91" + updatePhoneNu);
                                                    }
                                                } else {
                                                    String[] arr = request.getPhoneRequested().split(":", 2);
                                                    String phone = arr[1];
                                                    if (request.getPhoneRequested().startsWith("+91" + Common.currentUser.getPhone())) {
                                                        request.setPhoneRequested("+91" + updatePhoneNu + ":" + phone);
                                                    } else if (phone.equals("+91" + Common.currentUser.getPhone()))
                                                        request.setPhoneRequested(arr[0] + ":" + "+91" + updatePhoneNu);
                                                }
                                                FirebaseDatabase.getInstance().getReference("Request").child(postSnapshot.getKey()).setValue(request);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                            FirebaseDatabase.getInstance().getReference("Banner")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                Banner banner = postSnapshot.getValue(Banner.class);

                                                if (banner.getUserPhone().equals(Common.currentUser.getPhone()))
                                                    banner.setUserPhone(updatePhoneNu);
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    FirebaseDatabase.getInstance().getReference("Banner").child(Objects.requireNonNull(postSnapshot.getKey())).setValue(banner);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                            FirebaseDatabase.getInstance().getReference("User")
                                    .child("+91" + Common.currentUser.getPhone()).removeValue();
                            Common.currentUser.setPhone(updatePhoneNu);
                            FirebaseDatabase.getInstance().getReference("User").child("+91" + updatePhoneNu).setValue(Common.currentUser);
                            FirebaseAuth.getInstance().signInWithCredential(credential);
                            userPhone.setText(updatePhoneNu);
                            updateToken(FirebaseInstanceId.getInstance().getToken());
                            Toast.makeText(Profile.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(Profile.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**************************************************************************************************/
    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, true, true, Common.currentUser.isAdmin());
        tokens.child("+91" + Common.currentUser.getPhone()).setValue(data);
    }

    /**************************************************************************************************/
    private void UpdateHostelAddress() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Profile.this);
        alertDialog.setTitle("One more Step!");
        alertDialog.setMessage("Enter your address");
        alertDialog.setCancelable(false);


        LayoutInflater inflater = this.getLayoutInflater();
        View updateAddress = inflater.inflate(R.layout.update_hostel_address, null);

        final MaterialEditText Room = updateAddress.findViewById(R.id.edtCity);
        final MaterialSpinner Block = updateAddress.findViewById(R.id.State);
        int i;
        for (i = 0; i < block.length - 1; i++) {
            if (block[i].equals(Common.currentUser.getBlock()))
                break;
        }
        Block.setItems(block);
        Block.setSelectedIndex(i);
        Room.setText(Common.currentUser.getRoom());

        alertDialog.setView(updateAddress);
        alertDialog.setIcon(R.drawable.ic_home_black_24dp);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (!Common.currentUser.getRoom().equals(Room.getText().toString().toLowerCase()))
                    Common.currentUser.setRoom(Room.getText().toString().toLowerCase());
                if (!Common.currentUser.getBlock().equals(Block.getItems().get(Block.getSelectedIndex()).toString()))
                    Common.currentUser.setBlock(Block.getItems().get(Block.getSelectedIndex()).toString());
                if (!Room.getText().toString().toLowerCase().isEmpty()) {
                    Common.currentUser.setState_city_blood(Common.currentUser.getBlock() + "_" + Common.currentUser.getBloodGroup());
                    FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            FirebaseDatabase.getInstance().getReference("User").child("+91" + Common.currentUser.getPhone()).setValue(Common.currentUser);
                            userAddress.setText(Common.currentUser.getRoom() + "\n" + Common.currentUser.getBlock());
                            Toast.makeText(Profile.this, "Address Is Updated !!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void UpdateDatScrAddress() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Profile.this);
        alertDialog.setTitle("One more Step!");
        alertDialog.setMessage("Enter your address");
        alertDialog.setCancelable(false);


        LayoutInflater inflater = this.getLayoutInflater();
        View updateAddress = inflater.inflate(R.layout.update_day_address, null);

        final MaterialEditText Address = updateAddress.findViewById(R.id.edtAddress);
        final MaterialEditText City = updateAddress.findViewById(R.id.edtCity);
        final MaterialSpinner State = updateAddress.findViewById(R.id.State);
        int i;
        for (i = 0; i < state.length - 1; i++) {
            if (state[i].equals(Common.currentUser.getState()))
                break;
        }
        State.setItems(state);
        State.setSelectedIndex(i);
        City.setText(Common.currentUser.getCity());
        Address.setText(Common.currentUser.getAddress());

        alertDialog.setView(updateAddress);
        alertDialog.setIcon(R.drawable.ic_home_black_24dp);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (!Common.currentUser.getCity().equals(City.getText().toString().toLowerCase()))
                    Common.currentUser.setCity(City.getText().toString().toLowerCase());
                if (!Common.currentUser.getAddress().equals(Address.getText().toString().toLowerCase()))
                    Common.currentUser.setAddress(Address.getText().toString().toLowerCase());
                if (!Common.currentUser.getState().equals(State.getItems().get(State.getSelectedIndex()).toString()))
                    Common.currentUser.setState(State.getItems().get(State.getSelectedIndex()).toString());
                if (!City.getText().toString().toLowerCase().isEmpty()) {
                    Common.currentUser.setState_city_blood(Common.currentUser.getState() + "_" + Common.currentUser.getCity() + "_" + Common.currentUser.getBloodGroup());
                    FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            FirebaseDatabase.getInstance().getReference("User").child("+91" + Common.currentUser.getPhone()).setValue(Common.currentUser);
                            userAddress.setText(Common.currentUser.getAddress() + ",\n" + Common.currentUser.getCity() + "," + Common.currentUser.getState());
                            Toast.makeText(Profile.this, "Address Is Updated !!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading....");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = FirebaseStorage.getInstance().getReference().child("image/" + Common.currentUser.getPhone());
            imageFolder.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(Profile.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //set vale for newCategory if image upload and we can get download link
                            user = new User();
                            user = Common.currentUser;
                            user.setImage(uri.toString());
                            if (!Common.currentUser.getImage().equals(" "))
                                Picasso.with(Profile.this).load(Common.currentUser.getImage()).into(userImage);

                            FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    FirebaseDatabase.getInstance().getReference("User").child("+91" + Common.currentUser.getPhone()).setValue(user);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Profile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded (" + progress + "%)");
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadImage();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
    /**************************************************************************************************/

}
