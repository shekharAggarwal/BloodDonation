package com.bharatbloodbank.bharatbloodbank.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName, txtStatus, txtGroup;
    public CircleImageView userImage;
    public CardView cardView;
    public CircleImageView imgPhone,imgShare;

    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.userName);
        txtGroup = itemView.findViewById(R.id.userBlood);
        txtStatus = itemView.findViewById(R.id.user_status);
        userImage = itemView.findViewById(R.id.userImage);
        imgPhone = itemView.findViewById(R.id.imgPhone);
        imgShare = itemView.findViewById(R.id.imgShare);
        cardView = itemView.findViewById(R.id.loadList);
    }


}
