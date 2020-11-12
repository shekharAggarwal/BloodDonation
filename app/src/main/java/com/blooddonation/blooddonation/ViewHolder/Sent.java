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

public class Sent extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtName, txtDate, txtPhone, txtAddress;
    public CircleImageView userImage;
    public FButton btnRemove;
    public CardView cardView;
    private ItemClickListener itemClickListener;

    public Sent(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.txt_name);
        txtDate = itemView.findViewById(R.id.txt_date);
        txtPhone = itemView.findViewById(R.id.txt_phone);
        txtAddress = itemView.findViewById(R.id.txt_address);
        btnRemove = itemView.findViewById(R.id.btnRemove);
        userImage = itemView.findViewById(R.id.user_img);
        cardView = itemView.findViewById(R.id.sent_layout);
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
