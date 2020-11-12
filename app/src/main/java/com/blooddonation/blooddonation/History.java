package com.blooddonation.blooddonation;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Interface.ItemClickListener;
import com.blooddonation.blooddonation.Model.User;
import com.blooddonation.blooddonation.Model.sendRequest;
import com.blooddonation.blooddonation.ViewHolder.HistoryHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class History extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference requests;
    User user;
    User u1;
    RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<sendRequest, HistoryHolder> adapter;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");

        recyclerView = findViewById(R.id.recycler_history);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager((layoutManager));


        loadView();

    }

    private void loadView() {

        mDialog = new ProgressDialog(History.this);
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);
        mDialog.show();

        final Query list = FirebaseDatabase.getInstance().getReference("Request").orderByChild("phoneRequested").startAt("+91" + Common.currentUser.getPhone()).endAt("+91" + Common.currentUser.getPhone() + ":+91\uf8ff");
        FirebaseRecyclerOptions<sendRequest> loadList = new FirebaseRecyclerOptions.Builder<sendRequest>().setQuery(list, sendRequest.class).build();
        adapter = new FirebaseRecyclerAdapter<sendRequest, HistoryHolder>(loadList) {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull final HistoryHolder holder, final int position, @NonNull final sendRequest model) {
                String phone;

                if (model.getStatus().equals("2")) {
                    String[] as = model.getPhoneRequested().split(":", 2);
                    phone = as[1];
                } else {
                    phone = model.getPhoneReceived();
                }

                final String finalPhone = phone;
                FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        u1 = dataSnapshot.child(finalPhone).getValue(User.class);

                        holder.setOnClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                if (model.getStatus().equals("1")) {
                                    Intent foodDetail = new Intent(History.this, ContactUser.class);
                                    foodDetail.putExtra("ContactUser", "+91" + u1.getPhone());
                                    Pair[] pairs = new Pair[2];
                                    pairs[0] = new Pair<View, String>(holder.userImage, "imageTransition");
                                    pairs[1] = new Pair<View, String>(holder.txtName, "nameTransition");
                                    ActivityOptions options = null;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        options = ActivityOptions.makeSceneTransitionAnimation(History.this, pairs);
                                        startActivity(foodDetail, options.toBundle());
                                    } else
                                        startActivity(foodDetail);
                                }
                            }
                        });

                        if (model.getStatus().equals("1")) {
                            holder.txtName.setText(u1.getName());
                            holder.txtPhone.setText(u1.getPhone());
                            Picasso.with(History.this).load(u1.getImage()).into(holder.userImage);
                            holder.txtAddress.setText(u1.getCity() + " - " + u1.getState());
                            holder.txtDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                            holder.btnRemove.setText("Accepted");
                            holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
                            holder.btnRemove.setEnabled(false);
                        } else {
                            holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorDenied));
                            Picasso.with(History.this).load(u1.getImage()).into(holder.userImage);
                            holder.txtName.setText(u1.getName());
                            holder.txtDate.setText("Request Is  Rejected");
                            holder.btnRemove.setText("Rejected");
                            holder.btnRemove.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }

            @NonNull
            @Override
            public HistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.history_layout, viewGroup, false);
                return new HistoryHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        mDialog.dismiss();
    }

}
