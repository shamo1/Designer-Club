package com.example.designerclub.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.designerclub.Models.ProductReviewModel;
import com.example.designerclub.R;

import java.util.ArrayList;

public class ProductReviewAdaptor extends RecyclerView.Adapter<ProductReviewAdaptor.MyViewHolder> {

    Context context;
    ArrayList<ProductReviewModel> productReviewModels;

    public ProductReviewAdaptor(Context context, ArrayList<ProductReviewModel> productReviewModels) {
        this.context = context;
        this.productReviewModels = productReviewModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.userName.setText(productReviewModels.get(position).getName());
        holder.comment.setText(productReviewModels.get(position).getComment());
        Float rating = productReviewModels.get(position).getRating();
        holder.ratingBar.setRating(rating);
    }

    @Override
    public int getItemCount() {
        return productReviewModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, comment;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.commentUserName);
            comment = (TextView) itemView.findViewById(R.id.tvComment);
            ratingBar = (RatingBar) itemView.findViewById(R.id.commentRatingBar);
        }
    }
}
