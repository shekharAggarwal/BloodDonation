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

public class BannerRequest extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtBannerName, txtBannerDate, txtBannerDetail;
    public CircleImageView bannerImage;
    public CardView cardView;
    public FButton btnRemove, btnAccepted;
    private ItemClickListener itemClickListener;

    public BannerRequest(@NonNull View itemView) {
        super(itemView);

        txtBannerName = itemView.findViewById(R.id.BannerName);
        txtBannerDate = itemView.findViewById(R.id.BannerDate);
        txtBannerDetail = itemView.findViewById(R.id.BannerDetail);
        bannerImage = itemView.findViewById(R.id.bannerImage);
        btnRemove = itemView.findViewById(R.id.btnRemove);
        btnAccepted = itemView.findViewById(R.id.btnAccepted);
        cardView = itemView.findViewById(R.id.loadBanner);
    }


    public void setOnClickListener(ItemClickListener ItemClickListener) {
        this.itemClickListener = ItemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
