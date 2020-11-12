package com.blooddonation.blooddonation.ViewHolder;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blooddonation.blooddonation.Interface.ItemClickListener;
import com.blooddonation.blooddonation.R;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;

public class Requests extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtName, txtDate, txtPhone, txtAddress;
    public CircleImageView userImage;
    public CardView cardView;
    public FButton btnAccept, btnDenied;
    private ItemClickListener itemClickListener;

    public Requests(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.txt_name);
        txtDate = itemView.findViewById(R.id.txt_date);
        txtPhone = itemView.findViewById(R.id.txt_phone);
        txtAddress = itemView.findViewById(R.id.txt_address);
        btnAccept = itemView.findViewById(R.id.btnAccepted);
        btnDenied = itemView.findViewById(R.id.btnDenied);
        userImage = itemView.findViewById(R.id.user_img);
        cardView = itemView.findViewById(R.id.requested_layout);
        itemView.setOnClickListener(this);
    }


    public void setOnClickListener(ItemClickListener ItemClickListener) {
        this.itemClickListener = ItemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
