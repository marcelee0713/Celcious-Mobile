package com.example.celcious;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class CommentRecViewAdapter extends RecyclerView.Adapter<CommentRecViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Comments> comments = new ArrayList<>();
    private String uid = HomeActivity.uid;

    public CommentRecViewAdapter(Context context){this.context = context;}
    public void setComments(ArrayList<Comments> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView username;
        private TextView comment;
        private TextView time;

        private TextView deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.comment_username);
            comment = itemView.findViewById(R.id.comment);
            time = itemView.findViewById(R.id.comment_time);

            deleteButton = itemView.findViewById(R.id.remove_user_review_comment_item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText(comments.get(position).getUsername());
        holder.comment.setText(comments.get(position).getMessage());
        holder.time.setText(comments.get(position).getTime());

        if(!Objects.equals(uid, "CtwgPRWYDZf9AowTWmTxGoNeYDD2")){
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference questionsReference = FirebaseDatabase.getInstance().getReference("Uploads");
                String reviewUID = comments.get(holder.getAdapterPosition()).getReviewUID();
                String commentCode = comments.get(holder.getAdapterPosition()).getCommentCode();
                questionsReference.child(reviewUID).child("comments").child(commentCode).removeValue();
                comments.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(),getItemCount());
                ((UserReviewsActivity)context).refreshUserReviews();
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
