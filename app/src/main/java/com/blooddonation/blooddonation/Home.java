package com.blooddonation.blooddonation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Interface.ItemClickListener;
import com.blooddonation.blooddonation.Model.Banner;
import com.blooddonation.blooddonation.Model.Token;
import com.blooddonation.blooddonation.Model.User;
import com.blooddonation.blooddonation.Model.sendRequest;
import com.blooddonation.blooddonation.ViewHolder.LoadList;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<User, LoadList> adapter;
    sendRequest request;
    SliderLayout mSlider;
    SwipeRefreshLayout swipeRefreshLayout;
    String today;
    HashMap<String, String> image_list;
    String[] block = {"Block A", "Block B", "Block C", "Block D", "Day Scholar", "Outsider", "All"},
            Blood = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, Common.REQUEST_CODE);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager((layoutManager));
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    Common.toSearch = Common.currentUser.getState_city_blood();
                    if (Common.currentUser.isAdmin())
                        loadView();
                    else
                        notAdminLoadView();
                } else {
                    Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
//                    return;
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    Common.toSearch = Common.currentUser.getState_city_blood();
                    if (Common.currentUser.isAdmin())
                        loadView();
                    else
                        notAdminLoadView();

                } else {
                    Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
//                    return;
                }
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (Common.isConnectedToInternet(getBaseContext())) {

            Common.toSearch = Common.currentUser.getState_city_blood();
            if (Common.currentUser.isAdmin())
                loadView();
            else
                notAdminLoadView();
            setUpSlider();
            updateToken(FirebaseInstanceId.getInstance().getToken());
        } else {
            Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
//            return;
        }
    }

    private void setUpSlider() {
        mSlider = findViewById(R.id.slider);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" +
                month + "/" +
                Calendar.getInstance().get(Calendar.YEAR);

        FirebaseDatabase.getInstance().getReference("Banner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                image_list = new HashMap<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Banner banner = postSnapshot.getValue(Banner.class);
                    assert banner != null;
                    if (banner.getBannerLastDate().compareToIgnoreCase(today) == 0) {
                        FirebaseDatabase.getInstance().getReference("Banner").child(postSnapshot.getKey()).removeValue();
                    } else if (banner.getStatus().equals("1")) {
                        image_list.put(banner.getBannerName() + "@@@" + postSnapshot.getKey(), banner.getBannerImage());
                    }
                }
                mSlider.removeAllSliders();
                for (String key : image_list.keySet()) {
                    String[] keySplit = key.split("@@@");
                    String nameOfBanner = keySplit[0];
                    final String idOfBanner = keySplit[1];
                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView.bundle(new Bundle());
                    textSliderView.description(nameOfBanner)
                            .image(image_list.get(key))
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(Home.this, BannerInfo.class);
                                    intent.putExtra("id", idOfBanner);
                                    startActivity(intent);
                                }
                            });
                    mSlider.addSlider(textSliderView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);
    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, true, true, Common.currentUser.isAdmin());
        tokens.child("+91" + Common.currentUser.getPhone()).setValue(data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.top_search) {
            openSearch();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSearch() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("One more Step!");
        alertDialog.setMessage("Enter Location");
        alertDialog.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") View updateAddress = inflater.inflate(R.layout.search_address, null);

        final MaterialSpinner Block = updateAddress.findViewById(R.id.State);
        final MaterialSpinner blood = updateAddress.findViewById(R.id.bloodGroup);
        int i;
        for (i = 0; i < block.length - 1; i++) {
            if (block[i].equals(Common.currentUser.getBlock()))
                break;
        }

        Block.setItems(block);
        Block.setSelectedIndex(i);

        for (i = 0; i < Blood.length - 1; i++) {
            if (Blood[i].equals(Common.currentUser.getBloodGroup()))
                break;
        }
        blood.setItems(Blood);
        blood.setSelectedIndex(i);
        alertDialog.setView(updateAddress);
        alertDialog.setIcon(R.drawable.ic_home_black_24dp);

        alertDialog.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                Common.toSearch = Block.getItems().get(Block.getSelectedIndex()).toString() + "_" + blood.getItems().get(blood.getSelectedIndex()).toString();
                if (Common.currentUser.isAdmin())
                    loadView();
                else
                    notAdminLoadView();

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

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (Common.currentUser.isAdmin())
                loadView();
            else
                notAdminLoadView();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(Home.this, Profile.class));
        } else if (id == R.id.nav_banner) {
            startActivity(new Intent(Home.this, BannerActivity.class));
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(Home.this, Help.class));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(Home.this, History.class));
        } else if (id == R.id.nav_request_received) {
            startActivity(new Intent(Home.this, requestReceived.class));
        } else if (id == R.id.nav_request_send) {
            startActivity(new Intent(Home.this, requestSent.class));
        } else if (id == R.id.nav_logout) {
            FirebaseDatabase.getInstance().getReference("Tokens").child("+91" + Common.currentUser.getPhone()).removeValue();
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(Home.this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadView() {
        Query List;
        if (Common.toSearch.startsWith("All"))
            List = FirebaseDatabase.getInstance().getReference("User");
        else
            List = FirebaseDatabase.getInstance().getReference("User")
                    .orderByChild("state_city_blood").equalTo(Common.toSearch);

        final FirebaseRecyclerOptions<User> loadList = new FirebaseRecyclerOptions.Builder<User>().setQuery(List, User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, LoadList>(loadList) {
            @Override
            protected void onBindViewHolder(@NonNull final LoadList holder, final int position, @NonNull final User model) {

                holder.txtName.setText(model.getName());
                holder.txtPhone.setText(model.getPhone());
                holder.userStatus.setVisibility(View.VISIBLE);
                holder.userStatus.setImageResource(R.drawable.ic_call_black_24dp);
                holder.txtGroup.setText(model.getBloodGroup());
                Picasso.with(Home.this).load(model.getImage()).into(holder.userImage);

                FirebaseDatabase.getInstance().getReference("Request").addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            request = postSnapshot.getValue(sendRequest.class);
                            assert request != null;
                            if (request.getPhoneRequested().startsWith("+91" + Common.currentUser.getPhone())) {
                                if (request.getPhoneReceived() != null) {
                                    if (request.getPhoneReceived().equalsIgnoreCase("+91" + model.getPhone())) {
                                        if (request.getStatus().equals("0")) {
                                            holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorRequested));
                                        } else if (request.getStatus().equals("1")) {
                                            holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                holder.userStatus.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        FirebaseDatabase.getInstance().getReference("Request").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.M)
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                    request = postSnapshot.getValue(sendRequest.class);
//                                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                            requestPermissions(
//                                                    new String[]{Manifest.permission.CALL_PHONE}, Common.REQUEST_CODE);
//                                        }
//                                    } else {
//                                            if (request.getPhoneRequested().startsWith("+91" + Common.currentUser.getPhone())) {
//                                                if (request.getPhoneReceived().equalsIgnoreCase("+91" + model.getPhone())) {
//                                                    Intent intent = new Intent(Intent.ACTION_CALL);
//                                                    intent.setData(Uri.parse("tel:" + request.getPhoneReceived()));
//                                                    startActivity(intent);
//
//                                                }
//                                            } else if (request.getPhoneReceived().startsWith("+91" + Common.currentUser.getPhone())) {
//                                                if (request.getPhoneRequested().startsWith("+91" + model.getPhone())) {
//                                                    Intent intent = new Intent(Intent.ACTION_CALL);
//                                                    intent.setData(Uri.parse("tel:" + request.getPhoneRequested()));
//                                                    startActivity(intent);
//                                                }
//                                            }
//
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                });
                holder.userStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{Manifest.permission.CALL_PHONE}, Common.REQUEST_CODE);
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + "+91" + model.getPhone()));
                            startActivity(intent);
                        }
                    }
                });

                holder.setOnClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent foodDetail = new Intent(Home.this, Detailed_User.class);
                        foodDetail.putExtra("UserDetail", adapter.getRef(position).getKey());
                        Pair[] pairs = new Pair[3];
                        pairs[0] = new Pair<View, String>(holder.userImage, "imageTransition");
                        pairs[1] = new Pair<View, String>(holder.txtName, "nameTransition");
                        pairs[1] = new Pair<View, String>(holder.txtPhone, "phoneTransition");
                        pairs[2] = new Pair<View, String>(holder.txtGroup, "bloodTransition");
                        ActivityOptions options;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            options = ActivityOptions.makeSceneTransitionAnimation(Home.this, pairs);
                            startActivity(foodDetail, options.toBundle());
                        } else
                            startActivity(foodDetail);

                    }
                });

            }

            @NonNull
            @Override
            public LoadList onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.loadlist, viewGroup, false);
                return new LoadList(itemView);
            }


        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);


    }

    private void notAdminLoadView() {
        Query List;
        if (Common.toSearch.startsWith("All"))
            List = FirebaseDatabase.getInstance().getReference("User");
        else
            List = FirebaseDatabase.getInstance().getReference("User")
                    .orderByChild("state_city_blood").equalTo(Common.toSearch);

        final FirebaseRecyclerOptions<User> loadList = new FirebaseRecyclerOptions.Builder<User>().setQuery(List, User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, LoadList>(loadList) {
            @Override
            protected void onBindViewHolder(@NonNull final LoadList holder, final int position, @NonNull final User model) {

                holder.txtName.setText(model.getName());
                holder.txtPhone.setText(model.getPhone());
                holder.txtGroup.setText(model.getBloodGroup());
                Picasso.with(Home.this).load(model.getImage()).into(holder.userImage);

                FirebaseDatabase.getInstance().getReference("Request").addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            request = postSnapshot.getValue(sendRequest.class);
                            assert request != null;
                            if (request.getPhoneRequested().startsWith("+91" + Common.currentUser.getPhone())) {
                                if (request.getPhoneReceived() != null) {
                                    if (request.getPhoneReceived().equalsIgnoreCase("+91" + model.getPhone())) {
                                        if (request.getStatus().equals("0")) {
                                            holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorRequested));
                                        } else if (request.getStatus().equals("1")) {
                                            holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
                                            holder.userStatus.setVisibility(View.VISIBLE);
                                            holder.userStatus.setImageResource(R.drawable.ic_call_black_24dp);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.userStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference("Request").addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    request = postSnapshot.getValue(sendRequest.class);
                                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(
                                                    new String[]{Manifest.permission.CALL_PHONE}, Common.REQUEST_CODE);
                                        }
                                    } else {
                                        if (request.getPhoneRequested().startsWith("+91" + Common.currentUser.getPhone())) {
                                            if (request.getPhoneReceived().equalsIgnoreCase("+91" + model.getPhone())) {
                                                Intent intent = new Intent(Intent.ACTION_CALL);
                                                intent.setData(Uri.parse("tel:" + request.getPhoneReceived()));
                                                startActivity(intent);

                                            }
                                        } else if (request.getPhoneReceived().startsWith("+91" + Common.currentUser.getPhone())) {
                                            if (request.getPhoneRequested().startsWith("+91" + model.getPhone())) {
                                                Intent intent = new Intent(Intent.ACTION_CALL);
                                                intent.setData(Uri.parse("tel:" + request.getPhoneRequested()));
                                                startActivity(intent);
                                            }
                                        }

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                holder.setOnClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent foodDetail = new Intent(Home.this, Detailed_User.class);
                        foodDetail.putExtra("UserDetail", adapter.getRef(position).getKey());
                        Pair[] pairs = new Pair[3];
                        pairs[0] = new Pair<View, String>(holder.userImage, "imageTransition");
                        pairs[1] = new Pair<View, String>(holder.txtName, "nameTransition");
                        pairs[1] = new Pair<View, String>(holder.txtPhone, "phoneTransition");
                        pairs[2] = new Pair<View, String>(holder.txtGroup, "bloodTransition");
                        ActivityOptions options;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            options = ActivityOptions.makeSceneTransitionAnimation(Home.this, pairs);
                            startActivity(foodDetail, options.toBundle());
                        } else
                            startActivity(foodDetail);

                    }
                });

            }

            @NonNull
            @Override
            public LoadList onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.loadlist, viewGroup, false);
                return new LoadList(itemView);
            }


        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);


    }

    @Override
    protected void onStart() {
        if (Common.isConnectedToInternet(getBaseContext())) {
            if (Common.currentUser.isAdmin())
                loadView();
            else
                notAdminLoadView();
            setUpSlider();
            updateToken(FirebaseInstanceId.getInstance().getToken());
        } else {
            Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onStart();
    }
}
