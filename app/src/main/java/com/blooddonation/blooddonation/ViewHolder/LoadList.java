package com.blooddonation.blooddonation.ViewHolder;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blooddonation.blooddonation.Interface.ItemClickListener;
import com.blooddonation.blooddonation.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoadList extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtName, txtPhone, txtGroup;
    public CircleImageView userImage, userStatus;
    public CardView cardView;
    private ItemClickListener itemClickListener;

    public LoadList(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.userName);
        txtPhone = itemView.findViewById(R.id.userPhone);
        txtGroup = itemView.findViewById(R.id.userBlood);
        userStatus = itemView.findViewById(R.id.userRequest);
        userImage = itemView.findViewById(R.id.userImage);
        cardView = itemView.findViewById(R.id.loadList);
        itemView.setOnClickListener(this);
    }

//    public OrderViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
//        super(itemView);
//        this.itemClickListener = itemClickListener;
//    }

    public void setOnClickListener(ItemClickListener ItemClickListener) {
        this.itemClickListener = ItemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
