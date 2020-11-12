package com.blooddonation.blooddonation.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import com.blooddonation.blooddonation.Model.User;
import com.blooddonation.blooddonation.Remote.APIService;
import com.blooddonation.blooddonation.Remote.FCMRetrofitClient;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static final int REQUEST_CODE = 1000;
    public static User currentUser;
    public static final int PICK_IMAGE_REQUEST = 71;

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static final String fcmURl = "https://fcm.googleapis.com/";
    public static String toSearch;

    public static APIService getFCMClient() {
        return FCMRetrofitClient.getClient(fcmURl).create(APIService.class);

    }

    public static String getDate(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString());
        return date.toString();
    }

    public static String IMGURL;

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

    public static int calDate(long date) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = dob.get(Calendar.DAY_OF_MONTH) - today.get(Calendar.DAY_OF_MONTH);
        return age;
    }

//    public static String Address=null;


}
