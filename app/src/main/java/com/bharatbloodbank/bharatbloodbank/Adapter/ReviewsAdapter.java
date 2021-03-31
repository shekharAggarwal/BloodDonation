package com.bharatbloodbank.bharatbloodbank.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bharatbloodbank.bharatbloodbank.Model.Contact;
import com.bharatbloodbank.bharatbloodbank.Model.User;
import com.bharatbloodbank.bharatbloodbank.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final List<Contact> contactList;
    private final Context context;

    public ReviewsAdapter(List<Contact> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reviews_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.UserName.setText(contactList.get(position).getName());
        holder.UserRating.setText(MessageFormat.format("( {0} )", contactList.get(position).getRating()));
        holder.UserReview.setText(contactList.get(position).getReview());
        holder.UserRatingBar.setRating(Float.parseFloat(contactList.get(position).getRating()) == 0.0 ?
                0 : Float.parseFloat(contactList.get(position).getRating()));
        FirebaseDatabase.getInstance().getReference("User")
                .child("+91" + contactList.get(position).getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null)
                            if (user.getImage() != null && !user.getImage().equals(" "))
                                Picasso.with(context).load(user.getImage()).fit()
                                        .error(context.getResources().getDrawable(R.drawable.download)).into(holder.UserImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR", databaseError.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView UserImage;
        TextView UserName, UserRating, UserReview;
        RatingBar UserRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            UserImage = itemView.findViewById(R.id.UserImage);
            UserName = itemView.findViewById(R.id.UserName);
            UserRating = itemView.findViewById(R.id.UserRating);
            UserReview = itemView.findViewById(R.id.UserReview);
            UserRatingBar = itemView.findViewById(R.id.UserRatingBar);
        }
    }
}
