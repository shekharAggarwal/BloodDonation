package com.bharatbloodbank.bharatbloodbank;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.Adapter.BloodBankAdapter;
import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.BloodBankNearBy;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.bharatbloodbank.bharatbloodbank.Remote.IGoogleAPI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearBy extends AppCompatActivity implements LocationListener, ConnectivityReceiver.ConnectivityReceiverListener {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    LatLng myLatLng = null;
    IGoogleAPI mService;
    LatLng loc;
    boolean open_Close = false;
    RecyclerView blood_bank_list;
    String city;
    private ProgressDialog mDialog;
    BloodBankNearBy[] bloodBankNearBy;

    ConnectivityReceiver connectivityReceiver;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by);

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

        /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDialog = new ProgressDialog(NearBy.this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);

        if (getIntent() != null) {
            city = getIntent().getStringExtra("city");
        }
        mService = Common.getGoogleAPI();

        blood_bank_list = findViewById(R.id.blood_bank_list);
        blood_bank_list.setLayoutManager(new LinearLayoutManager(this));
        blood_bank_list.setHasFixedSize(true);


        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            buildLocationRequest();
                            buildLocationCallBack();

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(NearBy.this);

                            //start update location
//                            if (Build.VERSION.SDK_INT >= 23)
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(NearBy.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(NearBy.this, Home.class));
                    }
                }).check();


    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
                myLatLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                getLocations(city);

            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

    }

    private void getLocations(String City) {
        mDialog = new ProgressDialog(NearBy.this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);
        mDialog.show();

        String requestApi;
        if (myLatLng != null)
            requestApi = "https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                    "query=blood+bank+in+" + City +
                    "&location=" + myLatLng.latitude + "," + myLatLng.longitude +
                    "&radius=10000" +
                    "&key=<YOUR-KEY>";
        else
            requestApi = "https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                    "query=blood+bank+in+" + City +
                    "&key=<YOUR-KEY>";

        mService.getPath(requestApi)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        try {
                            assert response.body() != null;

                            JSONObject jsonObject = new JSONObject(response.body());

                            String status = jsonObject.getString("status");
                            if (!status.isEmpty() && status.equals("OK")) {

                                JSONArray results = jsonObject.getJSONArray("results");
                                bloodBankNearBy = new BloodBankNearBy[results.length()];

                                for (int i = 0; i < results.length(); i++) {
                                    //moving pointer
                                    JSONObject object = results.getJSONObject(i);
                                    //getting data from object
                                    String formatted_address = object.getString("formatted_address");
                                    //moving with help of object in geo
                                    JSONObject geometry = object.getJSONObject("geometry");
                                    //get location data
                                    JSONObject location = geometry.getJSONObject("location");
                                    loc = new LatLng(location.getDouble("lat"), location.getDouble("lng"));

                                    //get name by object
                                    String name = object.getString("name");
                                    //get ratings by object
                                    double rating = object.getDouble("rating");
                                    //get total users by object
                                    int user_ratings_total = object.getInt("user_ratings_total");
                                    JSONObject checkOpen;
                                    if (object.has("opening_hours") && !object.isNull("opening_hours")) {
                                        checkOpen = object.getJSONObject("opening_hours");

                                        if (checkOpen.has("open_now") && !checkOpen.isNull("open_now")) {
                                            open_Close = checkOpen.getBoolean("open_now");
                                        } else {
                                            open_Close = false;
                                        }
                                    } else {
                                        open_Close = false;
                                    }

                                    //setting data into class array
                                    bloodBankNearBy[i] = new BloodBankNearBy(formatted_address, name, rating, user_ratings_total, myLatLng, loc, open_Close, "0", "0");
                                }

                                BloodBankAdapter bloodBankAdapter = new BloodBankAdapter(bloodBankNearBy, NearBy.this);
                                blood_bank_list.setAdapter(bloodBankAdapter);
                                mDialog.dismiss();

                            } else if (!status.isEmpty() && status.equals("ZERO_RESULTS")) {
                                mDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(NearBy.this);
                                builder.setMessage("No Match Found");
                                builder.setTitle("ERROR !!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(NearBy.this, Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                });
                                builder.show();
                            } else if (!status.isEmpty() && status.equals("OVER_QUERY_LIMIT")) {
                                mDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(NearBy.this);
                                builder.setMessage("TRY AGAIN !");
                                builder.setTitle("ERROR !!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(NearBy.this, Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                });
                                builder.show();
                            } else {
                                mDialog.dismiss();
                                Log.d("Error", status);
                                Toast.makeText(NearBy.this, "Error while fetch data", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(NearBy.this, Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mDialog.dismiss();
                            Toast.makeText(NearBy.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        mDialog.dismiss();
                        Toast.makeText(NearBy.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onLocationChanged(Location location) {
        buildLocationRequest();
        buildLocationCallBack();
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
}
