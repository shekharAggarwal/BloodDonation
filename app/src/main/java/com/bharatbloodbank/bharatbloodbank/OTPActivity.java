package com.bharatbloodbank.bharatbloodbank;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class OTPActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText edtOtp;
    Button btnOtp;
    TextView txtResend;
    CountDownTimer countDownTimer;
    TextView timer;
    ConnectivityReceiver connectivityReceiver;

    boolean isRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

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
        Common.StartFirebaseLogin(this);

        edtOtp = findViewById(R.id.edtOtp);
        btnOtp = findViewById(R.id.btnOtp);
        timer = findViewById(R.id.timer);
        txtResend = findViewById(R.id.txtResend);

        verifyOtp();
        btnOtp.setOnClickListener(view -> {
            edtOtp.setEnabled(false);
            if (edtOtp.getText().toString().length() < 6 || edtOtp.getText().toString().isEmpty()) {
                edtOtp.setError("Check OTP");
                edtOtp.setEnabled(true);
                edtOtp.requestFocus();
                return;
            }
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Common.verificationCode, edtOtp.getText().toString());
            SigninWithPhone(credential);
        });

        txtResend.setOnClickListener(view -> Common.sendOTP(OTPActivity.this, Common.Phone));
    }

    public void SigninWithPhone(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (Common.fromActivity.equals("reg")) {
                            if (isRunning)
                                countDownTimer.cancel();
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child("+91" + Common.regUser.getPhone()).setValue(Common.regUser);
                            Common.currentUser = Common.regUser;
                            Common.regUser = null;
                            Toast.makeText(OTPActivity.this,
                                    "SignIn successfully !", Toast.LENGTH_SHORT).show();
                            btnOtp.setEnabled(true);
                            finish();
                            Intent intent = new Intent(OTPActivity.this, Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else if (Common.fromActivity.equals("log")) {
                            if (isRunning)
                                countDownTimer.cancel();
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child("+91" + Common.Phone)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Common.currentUser = dataSnapshot.getValue(User.class);
                                            sendUserToMain();
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d("ERROR", databaseError.getMessage());
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(OTPActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        edtOtp.setEnabled(true);
                    }
                });
    }

    private void sendUserToMain() {
        Common.regUser = null;
        Toast.makeText(OTPActivity.this, "SignIn successfully !", Toast.LENGTH_SHORT).show();
        btnOtp.setEnabled(true);
        Intent intent = new Intent(OTPActivity.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
        edtOtp.setEnabled(true);
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
                Toast.makeText(OTPActivity.this, "Time Out!!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRunning)
            countDownTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isRunning)
            countDownTimer.cancel();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            setContentView(R.layout.layout_no_internet);
            findViewById(R.id.btnTry).setOnClickListener(view -> recreate());
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
