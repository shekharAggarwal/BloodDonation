package com.bharatbloodbank.bharatbloodbank.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bharatbloodbank.bharatbloodbank.Home;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.OTPActivity;
import com.bharatbloodbank.bharatbloodbank.R;
import com.bharatbloodbank.bharatbloodbank.Remote.APIService;
import com.bharatbloodbank.bharatbloodbank.Remote.FCMRetrofitClient;
import com.bharatbloodbank.bharatbloodbank.Remote.IGoogleAPI;
import com.bharatbloodbank.bharatbloodbank.Remote.RetrofitClient;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Common {

    public static int Noti = 0;
    public static final int REQUEST_CODE = 1000;

    public static User currentUser, regUser;
    public static String verificationCode, fromActivity;
    public static boolean isChecked = false;
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final int REQUEST_CHECK_SETTINGS = 1071;
    public static ArrayList<String> userPhone = new ArrayList<>();
    public static String[] gender = {" Male", "Female", "Others"},
            Blood = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    public static String Phone;

    private static final String fcmURl = "https://fcm.googleapis.com/";
    private static final String baseURL = "https://maps.googleapis.com";

    /*
     *THIS IS FOR CHECKING INTERNET CONNECTION
     */
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     *THIS IS FOR SENDING NOTIFICATION
     */
    public static APIService getFCMClient() {
        return FCMRetrofitClient.getClient(fcmURl).create(APIService.class);

    }

    /*
     *THIS IS FOR SENDING MAP APIS
     */
    public static IGoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    /*
    * THIS FOR AGE CALCULATE
    */
    public static int calAge(long date) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);

        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
        return age;
    }

    public static int claMonth(long date) {

        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);

        Calendar today = Calendar.getInstance();

        int monthsBetween = 0;
        int dateDiff = today.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH);

        if (dateDiff < 0) {
            int borrrow = today.getActualMaximum(Calendar.DAY_OF_MONTH);
            dateDiff = (today.get(Calendar.DAY_OF_MONTH) + borrrow) - dob.get(Calendar.DAY_OF_MONTH);
            monthsBetween--;

            if (dateDiff > 0) {
                monthsBetween++;
            }
        } else {
            monthsBetween++;
        }
        monthsBetween += today.get(Calendar.MONTH) - dob.get(Calendar.MONTH);
        monthsBetween += (today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)) * 12;
        return monthsBetween;
    }

    public static int checkMonth(int year, int month, int day) {

        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);

        Calendar today = Calendar.getInstance();

        int monthsBetween = 0;
        int dateDiff = today.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH);

        if (dateDiff < 0) {
            int borrrow = today.getActualMaximum(Calendar.DAY_OF_MONTH);
            dateDiff = (today.get(Calendar.DAY_OF_MONTH) + borrrow) - dob.get(Calendar.DAY_OF_MONTH);
            monthsBetween--;

            if (dateDiff > 0) {
                monthsBetween++;
            }
        } else {
            monthsBetween++;
        }
        monthsBetween += today.get(Calendar.MONTH) - dob.get(Calendar.MONTH);
        monthsBetween += (today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)) * 12;
        return monthsBetween;
    }

    public static String getStatus(String status) {
        if (status.equals("0")) {
            return "Request Sent";
        } else if (status.equals("1")) {
            return "Accepted";
        } else
            return "Denied";
    }

    public static String CheckOpenOrClose(boolean check) {
        if (check)
            return "Open Now";
        else
            return "Close";

    }

    public static void setBack(Activity context) {
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void setTop(Activity context) {
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            context.getWindow().setStatusBarColor(context.getResources().getColor(R.color.bacgtop));
        }
    }

    private static PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    public static void sendOTP(Activity context, String phone) {
        if (phone != null){
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder()
                            .setPhoneNumber("+91" + phone)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(context)                 // Activity (for callback binding)
                            .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }

        else {
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder()
                            .setPhoneNumber("+91" + Phone)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(context)                 // Activity (for callback binding)
                            .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }
    }

    public static void StartFirebaseLogin(final Activity context) {

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(context, "verification completed", Toast.LENGTH_SHORT).show();
                checkPhoneAuth(phoneAuthCredential, context);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(context, "verification failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(context, "OTP sent", Toast.LENGTH_SHORT).show();
                start_Activity(context);
            }
        };
    }

    private static void start_Activity(Context context) {
        Intent intent = new Intent(context, OTPActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private static void checkPhoneAuth(PhoneAuthCredential credential, final Activity context) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (Common.fromActivity.equals("reg")) {
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child("+91" + Common.regUser.getPhone()).setValue(Common.regUser);
                            Common.currentUser = Common.regUser;
                            Common.regUser = null;
                            Toast.makeText(context,
                                    "SignIn successfully !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.finish();
                            context.startActivity(intent);
                        } else if (Common.fromActivity.equals("log")) {
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child("+91" + Common.Phone)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Common.currentUser = dataSnapshot.getValue(User.class);
                                            Common.regUser = null;
                                            Toast.makeText(context, "SignIn successfully !", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(context, Home.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            context.finish();
                                            context.startActivity(intent);
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d("ERROR", databaseError.getMessage());
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
