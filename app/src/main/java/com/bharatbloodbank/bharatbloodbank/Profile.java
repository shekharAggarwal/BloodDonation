package com.bharatbloodbank.bharatbloodbank;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.City;
import com.bharatbloodbank.bharatbloodbank.Model.States;
import com.bharatbloodbank.bharatbloodbank.Model.Token;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
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

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    TextView userName, userGroup, userGender, userAddress, userPhone, userDOB;
    CircleImageView userImage;
    Uri filePath;
    User user;
    String updatePhoneNu;
    CountDownTimer countDownTimer;
    boolean isRunning = false;
    TextView timer;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    ConnectivityReceiver connectivityReceiver;


    ProgressDialog Dialog;
    Toolbar toolbar;

    ArrayList<String> DistrictList, StateList, CityList;
    AutoCompleteTextView edtDistrict, State, edtCity;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_2);

        Common.setBack(this);
       /* final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        StartFirebaseLogin();
        /**************************************************************************************************/

        userName = findViewById(R.id.userName);
        userGroup = findViewById(R.id.userGroup);
        userGender = findViewById(R.id.userGender);
        userAddress = findViewById(R.id.userAddress);
        userPhone = findViewById(R.id.userPhone);
        userDOB = findViewById(R.id.userDOB);
        userImage = findViewById(R.id.userImage);
        /**************************************************************************************************/

        userName.setText(Common.currentUser.getName());
        userGroup.setText(Common.currentUser.getBloodGroup());
        userGender.setText(Common.currentUser.getGender());
        if (!Common.currentUser.getCity().isEmpty())
            userAddress.setText(Common.currentUser.getCity() + "," + Common.currentUser.getDistrict() + "," + Common.currentUser.getState());
        else
            userAddress.setText(Common.currentUser.getDistrict() + "," + Common.currentUser.getState());

        userPhone.setText(Common.currentUser.getPhone());
        userDOB.setText(Common.currentUser.getAge());
        if (!Common.currentUser.getImage().equals(" "))
            Picasso.with(Profile.this).load(Common.currentUser.getImage()).into(userImage);
        /**************************************************************************************************/

        userGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGender();
            }
        });

        /**************************************************************************************************/

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateName();
            }
        });

        /**************************************************************************************************/

        userGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBlood();
            }
        });

        /**************************************************************************************************/

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        /**************************************************************************************************/

        userAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAddress();
            }
        });

        /**************************************************************************************************/

        userPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhone();
            }
        });

        /**************************************************************************************************/

    }

    /**************************************************************************************************/

    private void updateBlood() {
        LayoutInflater inflater = this.getLayoutInflater();
        View updateGender = inflater.inflate(R.layout.update_gender, null);
        final MaterialSpinner materialSpinner = updateGender.findViewById(R.id.Gender);
        materialSpinner.setItems(Common.Blood);

        final AlertDialog alertDialog = new AlertDialog.Builder(Profile.this)
                .setTitle("One more Step!")
                .setMessage("Update Gender")
                .setCancelable(false)
                .setView(updateGender)
                .setPositiveButton("UPDATE", null)
                .setNegativeButton("CANCEL", null)
                .show();
        Button postiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        postiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentUser.setBloodGroup(materialSpinner.getText().toString());
                FirebaseDatabase.getInstance().getReference("User")
                        .child("+91" + Common.currentUser.getPhone()).setValue(Common.currentUser);
                userGroup.setText(Common.currentUser.getBloodGroup());


            }
        });
    }

    /**************************************************************************************************/

    private void updateName() {
        LayoutInflater inflater = this.getLayoutInflater();
        View updateName = inflater.inflate(R.layout.update_name, null);
        final MaterialEditText edtName = updateName.findViewById(R.id.edtName);

        final AlertDialog alertDialog = new AlertDialog.Builder(Profile.this)
                .setTitle("One more Step!")
                .setMessage("Update Name")
                .setCancelable(false)
                .setView(updateName)
                .setIcon(R.drawable.ic_person_black_24dp)
                .setPositiveButton("UPDATE", null)
                .setNegativeButton("CANCEL", null)
                .show();
        Button postiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        postiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtName != null && !edtName.getText().toString().isEmpty() && !edtName.getText().toString().matches(" ")) {
                    Common.currentUser.setName(edtName.getText().toString());
                    FirebaseDatabase.getInstance().getReference("User")
                            .child("+91" + Common.currentUser.getPhone()).setValue(Common.currentUser);
                    userName.setText(Common.currentUser.getName());
                    alertDialog.dismiss();
                }

            }
        });
    }

    /**************************************************************************************************/

    private void updateGender() {
        LayoutInflater inflater = this.getLayoutInflater();
        View updateGender = inflater.inflate(R.layout.update_gender, null);
        final MaterialSpinner materialSpinner = updateGender.findViewById(R.id.Gender);
        materialSpinner.setItems(Common.gender);

        final AlertDialog alertDialog = new AlertDialog.Builder(Profile.this)
                .setTitle("One more Step!")
                .setMessage("Update Gender")
                .setCancelable(false)
                .setView(updateGender)
                .setIcon(R.drawable.ic_person_black_24dp)
                .setPositiveButton("UPDATE", null)
                .setNegativeButton("CANCEL", null)
                .show();
        Button postiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        postiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentUser.setGender(materialSpinner.getText().toString());
                FirebaseDatabase.getInstance().getReference("User")
                        .child("+91" + Common.currentUser.getPhone()).setValue(Common.currentUser);
                userGender.setText(Common.currentUser.getGender());
                alertDialog.dismiss();

            }
        });
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
                                edtPhone.setError("Phone Number Registered !!");
                            } else {
                                alertDialog.dismiss();
                                updatePhoneNu = edtPhone.getText().toString();
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        "+91" + edtPhone.getText().toString(), // Phone number to verify
                                        60,                           // Timeout duration
                                        TimeUnit.SECONDS,                // Unit of timeout
                                        Profile.this,              // Activity (for callback binding)
                                        mCallback);
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
        Token data = new Token(token, true, true);
        tokens.child("+91" + Common.currentUser.getPhone()).setValue(data);
    }

    private void UpdateAddress() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Profile.this);
        alertDialog.setTitle("One more Step!");
        alertDialog.setMessage("Enter your address");
        alertDialog.setCancelable(false);

        mDialog = new ProgressDialog(Profile.this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View updateAddress = inflater.inflate(R.layout.update_address, null);

        edtDistrict = updateAddress.findViewById(R.id.edtDistrict);
        State = updateAddress.findViewById(R.id.State);
        edtCity = updateAddress.findViewById(R.id.edtCity);

        edtDistrict.setText(Common.currentUser.getDistrict());
        if (!Common.currentUser.getCity().isEmpty())
            edtCity.setText(Common.currentUser.getCity());
        State.setText(Common.currentUser.getState());

        alertDialog.setView(updateAddress);
        alertDialog.setIcon(R.drawable.ic_home_black_24dp);


        settingDataOfState();

        edtDistrict.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mDialog.show();
                    setDistrictData(State.getText().toString());
                }
            }
        });
        edtCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mDialog.show();
                    setCityData(State.getText().toString());
                }
            }
        });

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (State.getText().toString().isEmpty()) {
                    State.setError("Enter State");
                    return;
                }
                if (edtDistrict.getText().toString().isEmpty()) {
                    edtDistrict.setError("Enter district");
                    return;
                }
                if (!State.getText().toString().isEmpty() && !edtDistrict.getText().toString().isEmpty()) {
                    User user = Common.currentUser;
                    user.setState(State.getText().toString());
                    user.setDistrict(edtDistrict.getText().toString());
                    user.setCity(edtCity.getText().toString());
                    FirebaseDatabase.getInstance().getReference("User").child("+91" + Common.currentUser.getPhone()).setValue(user);
                    if (!Common.currentUser.getCity().isEmpty())
                        userAddress.setText(Common.currentUser.getCity() + ",\n" + Common.currentUser.getDistrict() + "," + Common.currentUser.getState());
                    else
                        userAddress.setText(Common.currentUser.getDistrict() + ",\n" + Common.currentUser.getState());
                    Toast.makeText(Profile.this, "Address Is Updated", Toast.LENGTH_SHORT).show();
                    Common.currentUser = user;
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

    private void setCityData(final String state) {
        if (!state.equals("")) {
            CityList = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference("City").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        City cityData = snapshot.getValue(City.class);
                        if (cityData.getState().equals(state)) {
                            CityList.add(cityData.getName());
                        }
                    }
                    ArrayAdapter<String> city = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_spinner_dropdown_item, CityList);
                    edtCity.setAdapter(city);
                    mDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            CityList = new ArrayList<>();
            Toast.makeText(Profile.this, "select 1st State", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        }
    }

    private void setDistrictData(final String state) {
        if (!state.equals("")) {
            DistrictList = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference("states").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        States states = snapshot.getValue(States.class);
                        if (states.getState().equals(state)) {
                            DistrictList = states.getDistricts();
                            ArrayAdapter<String> district = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_spinner_dropdown_item, DistrictList);
                            edtDistrict.setAdapter(district);
                            break;
                        }
                        mDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            DistrictList = new ArrayList<>();
            Toast.makeText(Profile.this, "select 1st State", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        }
    }

    private void settingDataOfState() {
        mDialog.show();
        StateList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("states").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                States states = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    states = snapshot.getValue(States.class);
                    StateList.add(states.getState());
                }
                ArrayAdapter<String> StateData = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_spinner_dropdown_item, StateList);
                State.setAdapter(StateData);
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            setContentView(R.layout.layout_no_internet);
         /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
    /**************************************************************************************************/

}
