package com.bharatbloodbank.bharatbloodbank.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.MyResponse;
import com.bharatbloodbank.bharatbloodbank.Model.Notification;
import com.bharatbloodbank.bharatbloodbank.Model.SendRequest;
import com.bharatbloodbank.bharatbloodbank.Model.Sender;
import com.bharatbloodbank.bharatbloodbank.Model.Token;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.R;
import com.bharatbloodbank.bharatbloodbank.Remote.APIService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BloodDonorAdapter extends RecyclerView.Adapter<BloodDonorAdapter.ViewHolder> {

    private final User[] bloodDonorUser;
    private final Context context;
    private final APIService mService = Common.getFCMClient();

    public BloodDonorAdapter(User[] bloodDonorUser, Context context) {
        this.bloodDonorUser = bloodDonorUser;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.loadlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //setting data into CardView
        holder.txtName.setText(bloodDonorUser[position].getName());
        holder.txtGroup.setText(bloodDonorUser[position].getBloodGroup());
        holder.sendRequest.setVisibility(View.VISIBLE);
        holder.sendRequest.setImageResource(R.drawable.ic_arrow_forward_black_24dp);
        holder.txtGroup.setText(bloodDonorUser[position].getBloodGroup());

        if (!bloodDonorUser[position].getImage().equals(" "))
            Picasso.with(context).load(bloodDonorUser[position].getImage()).into(holder.userImage);

        FirebaseDatabase.getInstance().getReference("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SendRequest sendRequest = snapshot.getValue(SendRequest.class);
                        if (sendRequest.getUserPhone().equalsIgnoreCase("+91" + Common.currentUser.getPhone())
                                && sendRequest.getRequestPhone().equalsIgnoreCase("+91" + bloodDonorUser[position].getPhone())) {
                            holder.sendRequest.setVisibility(View.INVISIBLE);
                            holder.txtStatus.setText(Common.getStatus(sendRequest.getStatus()));
                            for (int p = 0; p < Common.userPhone.size(); p++) {
                                if (Common.userPhone.get(p).equals("+91" + bloodDonorUser[position].getPhone())) {
                                    Common.userPhone.remove(p);
                                    break;
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

        holder.sendRequest.setOnClickListener(view -> {
            SendRequest send_Request = new SendRequest("+91" + Common.currentUser.getPhone(),
                    "+91" + bloodDonorUser[position].getPhone(),
                    "0",
                    true);
            sendNotification("+91" + bloodDonorUser[position].getPhone());

            for (int p = 0; p < Common.userPhone.size(); p++) {
                if (Common.userPhone.get(p).equals("+91" + bloodDonorUser[position].getPhone())) {
                    Common.userPhone.remove(p);
                    break;
                }
            }

            holder.txtStatus.setText(Common.getStatus("0"));
            FirebaseDatabase.getInstance().getReference("Requests").child(String.valueOf(UUID.randomUUID())).setValue(send_Request);
            holder.sendRequest.setVisibility(View.INVISIBLE);

        });


    }

    @Override
    public int getItemCount() {
        return bloodDonorUser.length;
    }

    private void sendNotification(final String request) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.child(request)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);
                            Notification notification = new Notification("Message", "Need Your Blood");
                            Sender content = new Sender(token.getToken(), notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            Log.d("response", "onResponse: " + response.body().failure);
                                            if (response.body().success == 1) {
                                                Toast.makeText(context, "Request Sent!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Failed To Send Notification !", Toast.LENGTH_SHORT).show();
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
                        Log.d("ERROR", databaseError.getMessage());
                    }
                });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtStatus, txtGroup;
        public CircleImageView userImage, sendRequest;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.userName);
            txtGroup = itemView.findViewById(R.id.userBlood);
            txtStatus = itemView.findViewById(R.id.user_status);
            sendRequest = itemView.findViewById(R.id.userRequest);
            userImage = itemView.findViewById(R.id.userImage);
            cardView = itemView.findViewById(R.id.loadList);
        }
    }
}
