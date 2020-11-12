package com.blooddonation.blooddonation;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Model.Banner;
import com.blooddonation.blooddonation.Model.MyResponse;
import com.blooddonation.blooddonation.Model.Notification;
import com.blooddonation.blooddonation.Model.Sender;
import com.blooddonation.blooddonation.Model.Token;
import com.blooddonation.blooddonation.Remote.APIService;
import com.blooddonation.blooddonation.ViewHolder.BannerRequest;
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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminBannerRequest extends Fragment {

    RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Banner, BannerRequest> adapter;
    APIService mService;
    ProgressDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.requested_banner, container, false);

        mService = Common.getFCMClient();
        recyclerView = v.findViewById(R.id.recycler_request);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager((layoutManager));

        mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Please waiting....");
        mDialog.setCancelable(false);
        mDialog.show();
        loadView();

        return v;
    }

    private void loadView() {
        Query List = FirebaseDatabase.getInstance().getReference("Banner");

        final FirebaseRecyclerOptions<Banner> loadList = new FirebaseRecyclerOptions.Builder<Banner>().setQuery(List, Banner.class).build();

        adapter = new FirebaseRecyclerAdapter<Banner, BannerRequest>(loadList) {
            @Override
            protected void onBindViewHolder(@NonNull final BannerRequest holder, final int position, @NonNull final Banner model) {

                holder.txtBannerName.setText(model.getBannerName());

                holder.txtBannerDetail.setText(model.getBannerDetail());

                Picasso.with(getContext()).load(model.getBannerImage()).into(holder.bannerImage);

                holder.txtBannerDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));

                holder.btnRemove.setText("Denied");

                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.setStatus("2");
                        holder.btnAccepted.setText("Remove");
                        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                        holder.btnRemove.setLayoutParams(p);
                        sendNotification("+91" + model.getUserPhone());
                        FirebaseDatabase.getInstance().getReference("Banner").child(adapter.getRef(position).getKey()).setValue(model);
                    }
                });

                holder.btnAccepted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (model.getStatus().equals("1")) {
                            deleteOrder(adapter.getRef(position).getKey(), model.getBannerImage());
                            Toast.makeText(getContext(), "Banner Removed", Toast.LENGTH_SHORT).show();
                        } else if (model.getStatus().equals("2")) {

                        } else {
                            model.setStatus("1");
                            Toast.makeText(getContext(), "Banner Request Accepted", Toast.LENGTH_SHORT).show();
                            sendNotification("+91" + model.getUserPhone());
                            FirebaseDatabase.getInstance().getReference("Banner").child(adapter.getRef(position).getKey()).setValue(model);
                        }
                    }
                });

                if (model.getStatus().equals("1")) {
                    holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
                    holder.btnAccepted.setText("Remove");
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    holder.btnRemove.setLayoutParams(p);
                } else if (model.getStatus().equals("2")) {
                    holder.btnAccepted.setText("Remove");
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    holder.btnRemove.setLayoutParams(p);
                    holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorDenied));
                }
            }

            @NonNull
            @Override
            public BannerRequest onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.loadbanner, viewGroup, false);
                return new BannerRequest(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        mDialog.dismiss();
    }

    private void sendNotification(final String shipperPhone) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.child(shipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);
                            Notification notification = new Notification("Message", "Banner Request Is Updated");
                            Sender content = new Sender(token.getToken(), notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                            if (response.body().success == 1) {
                                                Toast.makeText(getContext(), "Request Updated!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Failed To Send Notification !", Toast.LENGTH_SHORT).show();
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

    private void deleteOrder(final String key, final String bannerImage) {

        FirebaseDatabase.getInstance().getReference("Banner").child(key)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseStorage.getInstance().getReferenceFromUrl(bannerImage).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Banner Deleted", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
