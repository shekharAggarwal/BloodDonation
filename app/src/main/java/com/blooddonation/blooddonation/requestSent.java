package com.blooddonation.blooddonation;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Interface.ItemClickListener;
import com.blooddonation.blooddonation.Model.User;
import com.blooddonation.blooddonation.Model.sendRequest;
import com.blooddonation.blooddonation.ViewHolder.Sent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class requestSent extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference requests;
    User user;
    User u1;
    RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<sendRequest, Sent> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_sent);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");

        recyclerView = findViewById(R.id.recycler_request);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager((layoutManager));


        loadView();


    }

    private void loadView() {

        final Query list = FirebaseDatabase.getInstance().getReference("Request").orderByChild("phoneRequested").equalTo("+91" + Common.currentUser.getPhone() + ":0");
        FirebaseRecyclerOptions<sendRequest> loadList = new FirebaseRecyclerOptions.Builder<sendRequest>().setQuery(list, sendRequest.class).build();

        adapter = new FirebaseRecyclerAdapter<sendRequest, Sent>(loadList) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull final Sent holder, final int position, @NonNull final sendRequest model) {

                FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        u1 = dataSnapshot.child(model.getPhoneReceived()).getValue(User.class);
                        holder.txtName.setText(u1.getName());
                        Picasso.with(requestSent.this).load(u1.getImage()).into(holder.userImage);
                        holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorRequested));

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

                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                user = dataSnapshot.child(model.getPhoneReceived()).getValue(User.class);
                                user.setRequested(String.valueOf(Integer.parseInt(user.getRequested()) - 1));
                                FirebaseDatabase.getInstance().getReference("User").child("+91" + user.getPhone()).setValue(user);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });
            }

            @NonNull
            @Override
            public Sent onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.sent_layout, viewGroup, false);
                return new Sent(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void deleteOrder(final String key) {
        requests.child(key)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(requestSent.this, "request deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requestSent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
