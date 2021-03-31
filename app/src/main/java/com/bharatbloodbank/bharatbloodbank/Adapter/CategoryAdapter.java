package com.bharatbloodbank.bharatbloodbank.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.ContactUs;
import com.bharatbloodbank.bharatbloodbank.Interface.ItemClickListener;
import com.bharatbloodbank.bharatbloodbank.LoadBlood;
import com.bharatbloodbank.bharatbloodbank.Model.City;
import com.bharatbloodbank.bharatbloodbank.Model.States;
import com.bharatbloodbank.bharatbloodbank.NearBy;
import com.bharatbloodbank.bharatbloodbank.R;
import com.bharatbloodbank.bharatbloodbank.ReviewsActivity;
import com.bharatbloodbank.bharatbloodbank.Service.GpsTracker;
import com.bharatbloodbank.bharatbloodbank.SettingActivity;
import com.bharatbloodbank.bharatbloodbank.TipsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final String[] category_text;
    private final int[] img;
    private final Context context;
    private ArrayList<String> DistrictList, StateList, CityList, CITY;
    private AutoCompleteTextView edtDistrict, State, edtCity, City;
    private ProgressDialog mDialog;

    public CategoryAdapter(String[] category_text, int[] img, Context context) {
        this.category_text = category_text;
        this.img = img;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = category_text[position];
        holder.txt_name.setText(name);
        holder.view.setImageResource(img[position]);

        holder.setItemClickListener((view, position1, isLongClick) -> {

            switch (category_text[position1]) {
                case "Need Blood":
                    openSearch();
                    break;
                case "Blood Bank":
                    try {
                        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    GpsTracker gpsTracker = new GpsTracker(context);
                    if (!gpsTracker.canGetLocation()) {
                        gpsTracker.showSettingsAlert();
                    } else
                        openDialog();
                    break;
                case "Tips":
                    context.startActivity(new Intent(context, TipsActivity.class));
                    break;
                case "Feedback":
                    context.startActivity(new Intent(context, ContactUs.class));
                    break;
                case "Reviews":
                    context.startActivity(new Intent(context, ReviewsActivity.class));
                    break;
                case "Settings":
                    context.startActivity(new Intent(context, SettingActivity.class));
                    break;
                default:
                    Toast.makeText(context, "" + category_text[position1], Toast.LENGTH_SHORT).show();
                    break;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (category_text != null)
            return category_text.length;
        else
            return 0;
    }

    private void openDialog() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("One more Step!");
        alertDialog.setMessage("Enter Location");
        alertDialog.setCancelable(false);

        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);

        final LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View searchAddress = inflater.inflate(R.layout.search_city, null);

        City = searchAddress.findViewById(R.id.edtCity);


        City.setOnFocusChangeListener((view, b) -> {
            if (b) {
                mDialog.show();
                setCity();
            }
        });

        alertDialog.setView(searchAddress);
        alertDialog.setIcon(R.drawable.ic_home_black_24dp);

        alertDialog.setPositiveButton("SEARCH", (dialog, which) -> {

            if (City.getText().toString().isEmpty()) {
                City.setError("Enter City");
                return;
            }
            if (!City.getText().toString().isEmpty()) {
                Intent intent = new Intent(context, NearBy.class);
                intent.putExtra("city", City.getText().toString());
                context.startActivity(intent);
            }

        });
        alertDialog.setNegativeButton("CANCEL", (dialog, which) -> {
            mDialog.dismiss();
            dialog.dismiss();
        });
        alertDialog.show();
    }

    private void setCity() {
        CITY = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("City").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    City cityData = snapshot.getValue(City.class);
                    assert cityData != null;
                    CITY.add(cityData.getName());

                }
                ArrayAdapter<String> city = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, CITY);
                City.setAdapter(city);
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void openSearch() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("One more Step!");
        alertDialog.setMessage("Enter Location");
        alertDialog.setCancelable(false);

        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Please waiting....");
//        mDialog.setV
        mDialog.setCancelable(false);

        final LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View searchAddress = inflater.inflate(R.layout.search_address, null);

        edtDistrict = searchAddress.findViewById(R.id.edtDistrict);
        State = searchAddress.findViewById(R.id.State);
        edtCity = searchAddress.findViewById(R.id.edtCity);

        final MaterialSpinner blood = searchAddress.findViewById(R.id.bloodGroup);
//        int i;
//        for (i = 0; i < Common.Blood.length - 1; i++) {
//            if (Common.Blood[i].equals(Common.currentUser.getBloodGroup()))
//                break;
//        }
        blood.setItems(Common.Blood);
        blood.setSelectedIndex(0);
        settingDataOfState();

        edtDistrict.setOnFocusChangeListener((view, b) -> {
            if (b) {
                mDialog.show();
                setDistrictData(State.getText().toString());
            }
        });
        edtCity.setOnFocusChangeListener((view, b) -> {
            if (b) {
                mDialog.show();
                setCityData(State.getText().toString());
            }
        });

        alertDialog.setView(searchAddress);
        alertDialog.setIcon(R.drawable.ic_home_black_24dp);

        alertDialog.setPositiveButton("SEARCH", (dialog, which) -> {
            if (State.getText().toString().isEmpty()) {
                State.setError("Enter State");
                return;
            }
            if (edtDistrict.getText().toString().isEmpty()) {
                edtDistrict.setError("Enter district");
                return;
            }
            if (!State.getText().toString().isEmpty() && !edtDistrict.getText().toString().isEmpty()) {
                Intent intent = new Intent(context, LoadBlood.class);
                intent.putExtra("State", State.getText().toString());
                intent.putExtra("District", edtDistrict.getText().toString());
                if (!edtCity.getText().toString().isEmpty())
                    intent.putExtra("City", edtCity.getText().toString());
                intent.putExtra("Blood", blood.getItems().get(blood.getSelectedIndex()).toString());
                context.startActivity(intent);
            }

        });
        alertDialog.setNegativeButton("CANCEL", (dialog, which) -> {
            mDialog.dismiss();
            dialog.dismiss();
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
                        assert cityData != null;
                        if (cityData.getState().equals(state)) {
                            CityList.add(cityData.getName());
                        }
                    }
                    ArrayAdapter<String> city = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, CityList);
                    edtCity.setAdapter(city);
                    mDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            CityList = new ArrayList<>();
            Toast.makeText(context, "select 1st State", Toast.LENGTH_SHORT).show();
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
                        assert states != null;
                        if (states.getState().equals(state)) {
                            DistrictList = states.getDistricts();
                            ArrayAdapter<String> district = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, DistrictList);
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
            Toast.makeText(context, "select 1st State", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        }
    }

    private void settingDataOfState() {
        mDialog.show();
        StateList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("states").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                States states;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    states = snapshot.getValue(States.class);
                    assert states != null;
                    StateList.add(states.getState());
                }
                ArrayAdapter<String> StateData = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, StateList);
                State.setAdapter(StateData);
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_name;
        ImageView view;

        private ItemClickListener itemClickListener;

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_category_name);
            view = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
