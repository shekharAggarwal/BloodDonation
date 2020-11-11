package com.bharatbloodbank.bharatbloodbank;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.City;
import com.bharatbloodbank.bharatbloodbank.Model.States;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.Network.ConnectivityReceiver;
import com.bharatbloodbank.bharatbloodbank.Network.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class RegisterActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    //declare var
    EditText edtPhone, edtName, edtDOB, edtLastDonated;

    ArrayList<String> DistrictList, StateList, CityList;
    Toolbar toolbar;

    AutoCompleteTextView edtDistrict, State, edtCity;
    MaterialSpinner Gender, bloodGroup;

    Button btnRegisterIn;
    //database
    FirebaseDatabase database;
    DatabaseReference table_user;
    FirebaseAuth auth;
    ConnectivityReceiver connectivityReceiver;


    private DatePickerDialog.OnDateSetListener mage, mLastDonated;

    String age = "", DOB = "", lastBD = "4", lastDBMonth = "";

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.
                    TRANSPARENT);
        }
        Common.StartFirebaseLogin(this);
        Common.setTop(this);

        /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);*/

        mDialog = new ProgressDialog(RegisterActivity.this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        edtPhone = findViewById(R.id.edtPhone);
        edtName = findViewById(R.id.edtName);
        edtDOB = findViewById(R.id.edtDOB);
        Gender = findViewById(R.id.Gender);
        edtLastDonated = findViewById(R.id.edtLastDonated);
        bloodGroup = findViewById(R.id.bloodGroup);

        edtCity = findViewById(R.id.edtCity);
        edtDistrict = findViewById(R.id.edtDistrict);
        State = findViewById(R.id.State);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //setting data
        mDialog.show();
        settingDataOfState();
        ArrayAdapter<String> state = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, StateList);
        State.setAdapter(state);

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

        Gender.setItems(Common.gender);
        bloodGroup.setItems(Common.Blood);


        btnRegisterIn = findViewById(R.id.btnSignIn);

        edtLastDonated.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mLastDonated,
                        year, month, day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        edtLastDonated.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                            android.R.style.Theme_Holo_Dialog_MinWidth,
                            mLastDonated,
                            year, month, day);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            }
        });
        edtDOB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Calendar ca = Calendar.getInstance();
                int ye = ca.get(Calendar.YEAR);
                int mo = ca.get(Calendar.MONTH);
                int da = ca.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
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

                    DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
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
                DOB = d + "/" + m + "/" + y;
                edtDOB.setText(DOB);
            }
        };
        mLastDonated = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                lastBD = String.valueOf(Common.claMonth(calendar.getTimeInMillis()));
                lastDBMonth = day + "/" + month + "/" + year;
                edtLastDonated.setText(lastDBMonth);
            }
        };
        table_user = FirebaseDatabase.getInstance().getReference("User");

        if (Common.isConnectedToInternet(getBaseContext())) {
            signIn();
        } else {
            Toast.makeText(this, "Check Internet Connection !!:(", Toast.LENGTH_SHORT).show();
//            return;
        }
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
                    ArrayAdapter<String> city = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, CityList);
                    edtCity.setAdapter(city);
                    mDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            CityList = new ArrayList<>();
            Toast.makeText(this, "select 1st State", Toast.LENGTH_SHORT).show();
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
                            ArrayAdapter<String> district = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, DistrictList);
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
            Toast.makeText(this, "select 1st State", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        }
    }

    private void settingDataOfState() {
        StateList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("states").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                States states = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    states = snapshot.getValue(States.class);
                    StateList.add(states.getState());
                }
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void signIn() {
        btnRegisterIn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                btnRegisterIn.setEnabled(false);

                if (edtName.getText().toString().isEmpty() || edtName.getText().toString().replaceAll(" ", "").length() == 0) {
                    edtName.setError("Enter Name");
                    edtName.requestFocus();
                    btnRegisterIn.setEnabled(true);
                    return;
                }

                if (Objects.requireNonNull(edtPhone.getText()).toString().isEmpty() || edtPhone.getText().toString().length() != 10) {
                    btnRegisterIn.setEnabled(true);
                    edtPhone.setError("Enter Correct Phone");
                    edtPhone.requestFocus();
                    return;
                }

                if (edtDOB.getText().toString().isEmpty() || edtDOB.getText().toString().replaceAll(" ", "").length() == 0) {
                    edtDOB.setError("Enter DOB");
                    edtDOB.requestFocus();
                    btnRegisterIn.setEnabled(true);
                    return;
                }

                if (State.getText().toString().isEmpty() || State.getText().toString().replaceAll(" ", "").length() == 0) {
                    State.setError("Enter State");
                    State.requestFocus();
                    btnRegisterIn.setEnabled(true);
                    return;
                }

                if (edtDistrict.getText().toString().isEmpty() || edtDistrict.getText().toString().replaceAll(" ", "").length() == 0) {
                    edtDistrict.setError("Enter District");
                    edtDistrict.requestFocus();
                    btnRegisterIn.setEnabled(true);
                    return;
                }

                FirebaseDatabase.getInstance().getReference("User").child("+91" + edtPhone.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            btnRegisterIn.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, "User Exits", Toast.LENGTH_SHORT).show();
                        } else {
                            Common.regUser = new User(edtPhone.getText().toString(),
                                    edtName.getText().toString(),
                                    edtDOB.getText().toString(),
                                    Gender.getItems().get(Gender.getSelectedIndex()).toString(),
                                    bloodGroup.getText().toString(),
                                    edtDistrict.getText().toString(),
                                    edtCity.getText().toString().isEmpty() ? "" : edtCity.getText().toString(),
                                    State.getText().toString(),
                                    " ",
                                    "4",
                                    lastDBMonth);
                            Common.fromActivity = "reg";
                            Common.Phone = edtPhone.getText().toString();
                            Common.sendOTP(RegisterActivity.this, edtPhone.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        btnRegisterIn.setEnabled(true);
                        Log.d("ERROR", databaseError.getMessage());
                    }
                });

            }
        });
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

        btnRegisterIn.setEnabled(true);
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
