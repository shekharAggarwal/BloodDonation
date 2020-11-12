package com.blooddonation.blooddonation;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blooddonation.blooddonation.Common.Common;
import com.blooddonation.blooddonation.Model.Banner;
import com.blooddonation.blooddonation.ViewHolder.BannerRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class bannerRequested extends Fragment {

    RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Banner, BannerRequest> adapter;
    DatabaseReference requests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.requested_banner, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_request);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager((layoutManager));
        requests = FirebaseDatabase.getInstance().getReference("Banner");

        loadView();

        return v;
    }

    private void loadView() {
        Query List = FirebaseDatabase.getInstance().getReference("Banner")
                .orderByChild("userPhone").equalTo(Common.currentUser.getPhone());

        final FirebaseRecyclerOptions<Banner> loadList = new FirebaseRecyclerOptions.Builder<Banner>().setQuery(List, Banner.class).build();

        adapter = new FirebaseRecyclerAdapter<Banner, BannerRequest>(loadList) {
            @Override
            protected void onBindViewHolder(@NonNull BannerRequest holder, final int position, @NonNull final Banner model) {
                holder.txtBannerName.setText(model.getBannerName());
                holder.txtBannerDetail.setText(model.getBannerDetail());
                Picasso.with(getContext()).load(model.getBannerImage()).into(holder.bannerImage);
                holder.txtBannerDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));

                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                holder.btnAccepted.setLayoutParams(p);

                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey(), model.getBannerImage());
                    }
                });

                if (model.getStatus().equals("1")) {
                    holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
                    holder.txtBannerDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())) + "\n Accepted");
                } else if (model.getStatus().equals("2")) {
                    holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorDenied));
                } else {
                    holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorRequested));
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
    }


    private void deleteOrder(final String key, final String bannerImage) {

        FirebaseDatabase.getInstance().getReference("Banner").child(key)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseStorage.getInstance().getReferenceFromUrl(bannerImage).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Request Deleted", Toast.LENGTH_SHORT).show();

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
