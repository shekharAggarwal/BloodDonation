package com.bharatbloodbank.bharatbloodbank.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.Common.Common;
import com.bharatbloodbank.bharatbloodbank.Model.BloodBankNearBy;
import com.bharatbloodbank.bharatbloodbank.R;

import java.text.MessageFormat;


public class BloodBankAdapter extends RecyclerView.Adapter<BloodBankAdapter.ViewHolder> {

    private final BloodBankNearBy[] bloodBankNear;
    private final Context context;

    public BloodBankAdapter(BloodBankNearBy[] bloodBankNear, Context context) {
        this.bloodBankNear = bloodBankNear;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood_bank_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.BankName.setText(bloodBankNear[position].getName());
        holder.BankRating.setText(String.valueOf(bloodBankNear[position].getRating()));
        holder.BankRated.setText(MessageFormat.format("({0})", bloodBankNear[position].getUser_ratings_total()));
        holder.BankDistance.setText(Integer.parseInt(bloodBankNear[position].getDistance()) > 0 ? bloodBankNear[position].getDistance() : "" +
                " " +
                (Integer.parseInt(bloodBankNear[position].getTime()) > 0 ? "  (" + bloodBankNear[position].getTime() + ")" : ""));
        holder.BankOpening.setText(Common.CheckOpenOrClose(bloodBankNear[position].isOpen_Close()));
        holder.BankRatingBar.setRating((float) bloodBankNear[position].getRating());

        /*
         * THIS IS FOR SENDING USER TO GOOGLE MAP
         */
        holder.BankDirection.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + bloodBankNear[position].getBankLatLng().latitude + ","
                    + bloodBankNear[position].getBankLatLng().longitude + "&mode=" +
                    "d&location=" + bloodBankNear[position].getUser_latLng().latitude +
                    "," + bloodBankNear[position].getUser_latLng().longitude);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        });
    }

    @Override
    public int getItemCount() {
        if (bloodBankNear != null)
            return bloodBankNear.length;
        else
            return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView BankName, BankRating, BankRated, BankDistance, BankOpening;
        RatingBar BankRatingBar;
        ImageView BankDirection;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            BankName = itemView.findViewById(R.id.BankName);
            BankRating = itemView.findViewById(R.id.BankRating);
            BankRatingBar = itemView.findViewById(R.id.BankRatingBar);
            BankRated = itemView.findViewById(R.id.BankRated);
            BankDistance = itemView.findViewById(R.id.BankDistance);
            BankOpening = itemView.findViewById(R.id.BankOpening);
            BankDirection = itemView.findViewById(R.id.BankDirection);
        }

    }
}
