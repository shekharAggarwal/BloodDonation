package com.bharatbloodbank.bharatbloodbank.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationHolder extends RecyclerView.ViewHolder {

    public TextView txtName, txtStatus, txtGroup;
    public CircleImageView userImage;
    public CardView cardView;
    public ImageButton btnAccept, btnCancel;
    public ImageView imgPhone;

    public NotificationHolder(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.userName);
        txtGroup = itemView.findViewById(R.id.userBlood);
        txtStatus = itemView.findViewById(R.id.user_status);
        userImage = itemView.findViewById(R.id.userImage);
        cardView = itemView.findViewById(R.id.loadList);
        imgPhone = itemView.findViewById(R.id.imgPhone);
        btnAccept = itemView.findViewById(R.id.btnAccept);
        btnCancel = itemView.findViewById(R.id.btnCancel);
    }


}
