package com.blooddonation.blooddonation;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import info.hoang8f.widget.FButton;

public class SignIn extends AppCompatActivity {
    MaterialEditText edtPhone, edtOtp, edtName, edtDOB, edtRoom, edtBatch, edtAddress, edtCity, edtBatchYear, edtOutAddress, edtOutCity;
    MaterialSpinner Gender, bloodGroup, Block, State, OutState, typeUser;
    FButton btnSignIn, btnVerify, btnName, btnSubmit;
    FirebaseDatabase database;
    RelativeLayout hostel, day_scolr, out_sider;
    DatabaseReference table_user;
    ViewFlipper viewFlipper;
    private DatePickerDialog.OnDateSetListener mage;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth auth;
    TextView timer;
    private String verificationCode;
    User user;
    boolean isRunning = false;
    String age, lastBD;
    String[] gender = {" Male", "Female", "Others"},
            Blood = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"},
            block = {"Block A", "Block B", "Block C", "Block D"},
            state = {"Rajasthan", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
                    "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir",
                    "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya",
                    "Mizoram", "Nagaland", "Odisha", "Punjab", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
                    "Uttar Pradesh", "Uttarakhand", "West Bengal"},
            TypeUser = {"Hostel", "Day Scholar", "Outsider"};

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ConstraintLayout constraintLayout = findViewById(R.id.root_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();


        auth = FirebaseAuth.getInstance();

        viewFlipper = findViewById(R.id.viewFlipper);
        StartFirebaseLogin();

        hostel = findViewById(R.id.hostel);
        day_scolr = findViewById(R.id.dayScholar);
        out_sider = findViewById(R.id.outsider);

        edtPhone = findViewById(R.id.edtPhone);
        edtOtp = findViewById(R.id.edtOtp);
        edtName = findViewById(R.id.edtName);
        edtDOB = findViewById(R.id.edtDOB);
        edtRoom = findViewById(R.id.edtRoom);
        edtBatch = findViewById(R.id.edtBatch);
        edtAddress = findViewById(R.id.edtAddress);
        edtCity = findViewById(R.id.edtCity);
        edtBatchYear = findViewById(R.id.edtBatchYear);
        edtOutAddress = findViewById(R.id.edtOutAddress);
        edtOutCity = findViewById(R.id.edtOutCity);


        timer = findViewById(R.id.timer);
        Gender = findViewById(R.id.Gender);
        bloodGroup = findViewById(R.id.bloodGroup);
        Block = findViewById(R.id.Block);
        State = findViewById(R.id.State);
        OutState = findViewById(R.id.OutState);
        typeUser = findViewById(R.id.typeUser);

        Gender.setItems(gender);
        State.setItems(state);
        OutState.setItems(state);
        bloodGroup.setItems(Blood);
        Block.setItems(block);
        typeUser.setItems(TypeUser);
        if (typeUser.getItems().get(typeUser.getSelectedIndex()).toString().equals("Hostel")) {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            hostel.setLayoutParams(p);
            p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            day_scolr.setLayoutParams(p);
            out_sider.setLayoutParams(p);
        }

        typeUser.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (typeUser.getItems().get(typeUser.getSelectedIndex()).toString().equals("Hostel")) {
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    hostel.setLayoutParams(p);
                    p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    day_scolr.setLayoutParams(p);
                    out_sider.setLayoutParams(p);
                } else if (typeUser.getItems().get(typeUser.getSelectedIndex()).toString().equals("Day Scholar")) {
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    day_scolr.setLayoutParams(p);
                    p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    hostel.setLayoutParams(p);
                    out_sider.setLayoutParams(p);
                } else if (typeUser.getItems().get(typeUser.getSelectedIndex()).toString().equals("Outsider")) {
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    out_sider.setLayoutParams(p);
                    p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    day_scolr.setLayoutParams(p);
                    hostel.setLayoutParams(p);
                }
            }
        });


        btnSignIn = findViewById(R.id.btnSignIn);
        btnVerify = findViewById(R.id.btnOtp);
        btnName = findViewById(R.id.btnName);
        btnSubmit = findViewById(R.id.btnSubmit);

        edtDOB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Calendar ca = Calendar.getInstance();
                int ye = ca.get(Calendar.YEAR);
                int mo = ca.get(Calendar.MONTH);
                int da = ca.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SignIn.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mage,
                        ye, mo, da);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        edtDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar ca = Calendar.getInstance();
                    int ye = ca.get(Calendar.YEAR);
                    int mo = ca.get(Calendar.MONTH);
                    int da = ca.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(SignIn.this,
                            android.R.style.Theme_Holo_Dialog_MinWidth,
                            mage,
                            ye, mo, da);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            }
        });
        mage = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                m = m + 1;
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, y);
                c.set(Calendar.MONTH, m);
                c.set(Calendar.DAY_OF_MONTH, d);
                age = String.valueOf(Common.calAge(c.getTimeInMillis()));
                lastBD = d + "/" + m + "/" + y;
                edtDOB.setText(lastBD);
            }
        };
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        if (Common.isConnectedToInternet(getBaseContext())) {
            signIn();
        } else {
            Toast.makeText(this, "Check Internet Connection !!:(", Toast.LENGTH_SHORT).show();
//            return;
        }
    }

    private void signIn() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                btnSignIn.setEnabled(false);
                if (Objects.requireNonNull(edtPhone.getText()).toString().isEmpty() || edtPhone.getText().toString().length() != 10) {
                    btnSignIn.setEnabled(true);
                    edtPhone.setError("Enter Correct Phone");
                } else {
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder()
                                    .setPhoneNumber("+91" + edtPhone.getText().toString())      // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(SignIn.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });
    }

    private void verifyOtp() {
        countDownTimer = new CountDownTimer(60000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                isRunning = true;
                if (millisUntilFinished / 1000 == 10)
                    timer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                timer.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                viewFlipper.showPrevious();
                Toast.makeText(SignIn.this, "Time Out!!", Toast.LENGTH_SHORT).show();
            }
        };
        countDownTimer.start();
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                btnVerify.setEnabled(false);
                if (Objects.requireNonNull(edtOtp.getText()).toString().isEmpty() || edtOtp.getText().toString().length() != 6) {
                    btnVerify.setEnabled(true);
                    edtOtp.setError("Enter Otp");
                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, edtOtp.getText().toString());
                    SigninWithPhone(credential);
                }
            }
        });
    }

    private void enterName() {
        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnName.setEnabled(false);
                if (edtName.getText().toString().isEmpty()) {
                    btnName.setEnabled(true);
                    edtName.setError("Enter Name");
                } else if (edtDOB.getText().toString().isEmpty()) {
                    btnName.setEnabled(true);
                    edtDOB.setError("Enter DOB");
                } else if (Integer.parseInt(age) < 18) {
                    btnName.setEnabled(true);
                    edtDOB.setError("Under 18 Not Allowed");
                } else {
                    viewFlipper.showNext();
                    btnName.setEnabled(true);
                    enterBlood();
                }
            }
        });
    }

    private void enterBlood() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmit.setEnabled(false);
                if (typeUser.getItems().get(typeUser.getSelectedIndex()).toString().equals("Hostel")) {
                    if (edtRoom.getText().toString().isEmpty()) {
                        btnSubmit.setEnabled(true);
                        edtRoom.setError("Enter Room");
                    }
                    if (edtBatch.getText().toString().isEmpty()) {
                        btnSubmit.setEnabled(true);
                        edtBatch.setError("Enter Batch Year");
                    }
                    if (!edtBatch.getText().toString().isEmpty() && !edtRoom.getText().toString().isEmpty()) {
                        sendData();
                    }
                } else if (typeUser.getItems().get(typeUser.getSelectedIndex()).toString().equals("Day Scholar")) {
                    if (edtAddress.getText().toString().isEmpty()) {
                        btnSubmit.setEnabled(true);
                        edtAddress.setError("Enter Address");
                    }
                    if (edtCity.getText().toString().isEmpty()) {
                        btnSubmit.setEnabled(true);
                        edtCity.setError("Enter City");
                    }
                    if (edtBatchYear.getText().toString().isEmpty()) {
                        btnSubmit.setEnabled(true);
                        edtBatchYear.setError("Enter Batch Year");
                    }
                    if (!edtAddress.getText().toString().isEmpty() && !edtCity.getText().toString().isEmpty() && !edtBatchYear.getText().toString().isEmpty()) {
                        sendData();
                    }
                } else if (typeUser.getItems().get(typeUser.getSelectedIndex()).toString().equals("Outsider")) {
                    if (edtOutAddress.getText().toString().isEmpty()) {
                        btnSubmit.setEnabled(true);
                        edtOutAddress.setError("Enter Address");
                    }
                    if (edtOutCity.getText().toString().isEmpty()) {
                        btnSubmit.setEnabled(true);
                        edtOutCity.setError("Enter City");
                    }
                    if (!edtOutAddress.getText().toString().isEmpty() && !edtOutCity.getText().toString().isEmpty()) {
                        sendData();
                    }
                }
            }
        });
    }

    private void StartFirebaseLogin() {

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(SignIn.this, "verification completed", Toast.LENGTH_SHORT).show();
                SigninWithPhone(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(SignIn.this, "verification failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(SignIn.this, "OTP sent ", Toast.LENGTH_SHORT).show();
                viewFlipper.showNext();
                btnSignIn.setEnabled(true);
                verifyOtp();

            }
        };
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("+91" + edtPhone.getText().toString()).exists()) {
                                        if (isRunning)
                                            countDownTimer.cancel();
                                        Common.currentUser = dataSnapshot.child("+91" + edtPhone.getText().toString()).getValue(User.class);
                                        Toast.makeText(SignIn.this, "SignIn successfully !", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(SignIn.this, Home.class));
                                    } else {
                                        if (isRunning)
                                            countDownTimer.cancel();
                                        else
                                            viewFlipper.showNext();

                                        btnVerify.setEnabled(true);
                                        viewFlipper.showNext();
                                        enterName();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Toast.makeText(SignIn.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendData() {
        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);
        mDialog.show();
        table_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mDialog.dismiss();
                if (typeUser.getItems().get(typeUser.getSelectedIndex()).toString().equals("Hostel")) {
                    user = new User(typeUser.getItems().get(typeUser.getSelectedIndex()).toString(),
                            edtPhone.getText().toString(),
                            edtName.getText().toString(),
                            edtDOB.getText().toString(),
                            Gender.getItems().get(Gender.getSelectedIndex()).toString(),
                            bloodGroup.getItems().get(bloodGroup.getSelectedIndex()).toString(),
                            edtRoom.getText().toString().toLowerCase(),
                            Block.getItems().get(Block.getSelectedIndex()).toString(),
                            edtBatch.getText().toString(),
                            "0",
                            "0",
                            Block.getItems().get(Block.getSelectedIndex()).toString() + "_" + bloodGroup.getItems().get(bloodGroup.getSelectedIndex()).toString(),
                            "https://firebasestorage.googleapis.com/v0/b/blooddonation-f7325.appspot.com/o/baseline_person_black_18dp.png?alt=media&token=a65ff9e2-8797-4944-8b98-3df64fc51e50");
                    Common.currentUser = user;
                    table_user.child("+91" + edtPhone.getText().toString()).setValue(user);
                    Toast.makeText(SignIn.this, "SignIn successfully !", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                    finish();
                    startActivity(new Intent(SignIn.this, Home.class));
                } else if (typeUser.getItems().get(typeUser.getSelectedIndex()).toString().equals("Day Scholar")) {
                    user = new User(typeUser.getItems().get(typeUser.getSelectedIndex()).toString(),
                            edtPhone.getText().toString(),
                            edtName.getText().toString(),
                            edtDOB.getText().toString(),
                            Gender.getItems().get(Gender.getSelectedIndex()).toString(),
                            bloodGroup.getItems().get(bloodGroup.getSelectedIndex()).toString(),
                            edtBatchYear.getText().toString(),
                            edtAddress.getText().toString(),
                            edtCity.getText().toString(),
                            State.getItems().get(State.getSelectedIndex()).toString(),
                            "0",
                            "0",
                            typeUser.getItems().get(typeUser.getSelectedIndex()).toString() + "_" + bloodGroup.getItems().get(bloodGroup.getSelectedIndex()).toString(),
                            //State.getItems().get(State.getSelectedIndex()).toString() + "_" + edtCity.getText().toString() + "_" + bloodGroup.getItems().get(bloodGroup.getSelectedIndex()).toString(),
                            "https://firebasestorage.googleapis.com/v0/b/blooddonation-f7325.appspot.com/o/baseline_person_black_18dp.png?alt=media&token=a65ff9e2-8797-4944-8b98-3df64fc51e50");
                    Common.currentUser = user;
                    table_user.child("+91" + edtPhone.getText().toString()).setValue(user);
                    Toast.makeText(SignIn.this, "SignIn successfully !", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                    finish();
                    startActivity(new Intent(SignIn.this, Home.class));
                } else if (typeUser.getItems().get(typeUser.getSelectedIndex()).toString().equals("Outsider")) {
                    user = new User(typeUser.getItems().get(typeUser.getSelectedIndex()).toString(),
                            edtPhone.getText().toString(),
                            edtName.getText().toString(),
                            edtDOB.getText().toString(),
                            Gender.getItems().get(Gender.getSelectedIndex()).toString(),
                            bloodGroup.getItems().get(bloodGroup.getSelectedIndex()).toString(),
                            edtOutAddress.getText().toString(),
                            edtOutCity.getText().toString(),
                            State.getItems().get(State.getSelectedIndex()).toString(),
                            "0",
                            "0",
                            typeUser.getItems().get(typeUser.getSelectedIndex()).toString() + "_" + bloodGroup.getItems().get(bloodGroup.getSelectedIndex()).toString(),
                            /*State.getItems().get(State.getSelectedIndex()).toString() + "_" + edtCity.getText().toString() + "_" + bloodGroup.getItems().get(bloodGroup.getSelectedIndex()).toString(),*/
                            "https://firebasestorage.googleapis.com/v0/b/blooddonation-f7325.appspot.com/o/baseline_person_black_18dp.png?alt=media&token=a65ff9e2-8797-4944-8b98-3df64fc51e50",
                            false);
                    Common.currentUser = user;
                    table_user.child("+91" + edtPhone.getText().toString()).setValue(user);
                    Toast.makeText(SignIn.this, "SignIn successfully !", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                    finish();
                    startActivity(new Intent(SignIn.this, Home.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
