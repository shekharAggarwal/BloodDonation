package com.blooddonation.blooddonation;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Interface.ItemClickListener;
import com.blooddonation.blooddonation.Model.MyResponse;
import com.blooddonation.blooddonation.Model.Notification;
import com.blooddonation.blooddonation.Model.RequestDenied;
import com.blooddonation.blooddonation.Model.Sender;
import com.blooddonation.blooddonation.Model.Token;
import com.blooddonation.blooddonation.Model.User;
import com.blooddonation.blooddonation.Model.sendRequest;
import com.blooddonation.blooddonation.Remote.APIService;
import com.blooddonation.blooddonation.ViewHolder.Requests;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class requestReceived extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference requests;
    User u1;
    RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<sendRequest, Requests> adapter;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_received);

        mService = Common.getFCMClient();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");

        recyclerView = findViewById(R.id.recycler_request);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager((layoutManager));

        loadView();
    }

    private void loadView() {
        final Query list = FirebaseDatabase.getInstance().getReference("Request").orderByChild("phoneReceived").equalTo("+91" + Common.currentUser.getPhone());
        FirebaseRecyclerOptions<sendRequest> loadList = new FirebaseRecyclerOptions.Builder<sendRequest>().setQuery(list, sendRequest.class).build();

        adapter = new FirebaseRecyclerAdapter<sendRequest, Requests>(loadList) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull final Requests holder, final int position, @NonNull final sendRequest model) {
                String[] sa = model.getPhoneRequested().split(":");
                model.setPhoneRequested(sa[0]);
                FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        u1 = dataSnapshot.child(model.getPhoneRequested()).getValue(User.class);
                        holder.txtName.setText(u1.getName());
                        holder.txtPhone.setText(u1.getPhone());
                        Picasso.with(requestReceived.this).load(u1.getImage()).into(holder.userImage);
                        holder.txtAddress.setText(u1.getCity() + "\n" + u1.getState());
                        holder.txtDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                        if (model.getStatus().equals("1")) {
                            holder.btnAccept.setText("Accepted");
                            holder.btnAccept.setEnabled(false);
                            holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
                            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                            holder.btnDenied.setLayoutParams(p);
                        }
                        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adapter.getItem(position).setStatus("1");
                                holder.btnAccept.setText("Accepted");
                                holder.cardView.setBackgroundColor(getColor(R.color.colorAccepted));
                                holder.btnAccept.setEnabled(false);
                                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                                holder.btnDenied.setLayoutParams(p);
                                Common.currentUser.setDonated(String.valueOf(Integer.parseInt(Common.currentUser.getDonated()) + 1));
                                FirebaseDatabase.getInstance().getReference("User").child("+91" + Common.currentUser.getPhone()).setValue(Common.currentUser);
                                FirebaseDatabase.getInstance().getReference("Request").child(adapter.getRef(position).getKey()).setValue(adapter.getItem(position));
                                sendNotification(model.getPhoneRequested());
                                Toast.makeText(requestReceived.this, "Accepted", Toast.LENGTH_SHORT).show();
                            }
                        });

                        holder.btnDenied.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestDenied denied = new RequestDenied("+91" + u1.getPhone() + ":" +
                                        "+91" + Common.currentUser.getPhone(),
                                        "2");

                                FirebaseDatabase.getInstance().getReference("Request").child(adapter.getRef(position).getKey()).setValue(denied);
                                sendNotification(model.getPhoneRequested());
                                Toast.makeText(requestReceived.this, "Denied", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.setOnClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }

            @NonNull
            @Override
            public Requests onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.requested_layout, viewGroup, false);
                return new Requests(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void sendNotification(final String shipperPhone) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.child(shipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);
                            Notification notification = new Notification("Message", "Donor Updated Your Request");
                            Sender content = new Sender(token.getToken(), notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                            if (response.body().success == 1) {
                                                Toast.makeText(requestReceived.this, "Request Updated!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(requestReceived.this, "Failed To Send Notification !", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
