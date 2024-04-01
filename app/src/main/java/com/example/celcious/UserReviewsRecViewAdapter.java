package com.example.celcious;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class UserReviewsRecViewAdapter extends RecyclerView.Adapter<UserReviewsRecViewAdapter.ViewHolder> {
    private Dialog dialog;
    private ArrayList<Upload> uploads = new ArrayList<>();
    private Context context;
    private LinearLayoutManager manager;
    private String userUID = HomeActivity.uid;
    private String userName = HomeActivity.userName;

    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference = rootRef.child("Uploads");

    public UserReviewsRecViewAdapter(Context context, LinearLayoutManager manager) {
        this.context = context;
        this.manager = manager;
    }
    public void setUploads(ArrayList<Upload> uploads) {
        this.uploads = uploads;
        notifyDataSetChanged();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        //for comments
        private RecyclerView commentsRecyclerView;
        private ArrayList<Comments> comments_list;

        private TextView userName;
        private TextView boughtItems;
        private TextView ratingStar1;
        private TextView ratingStar2;
        private TextView ratingStar3;
        private TextView ratingStar4;
        private TextView ratingStar5;
        private TextView message;
        private ImageView ratingImage;
        private TextView ratingDate;
        private int ratingCount;

        //for likes
        private TextView likeButton;
        private TextView likeCounter;

        //can only be seen by The Dev
        private TextView deleteItem;
        private TextView commentBtn;
        private String uid = HomeActivity.uid;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentsRecyclerView = itemView.findViewById(R.id.comments_recycler_view);

            userName = itemView.findViewById(R.id.user_name_user_reviews);
            boughtItems = itemView.findViewById(R.id.bought_items);
            ratingStar1 = itemView.findViewById(R.id.rating_star_1);
            ratingStar2 = itemView.findViewById(R.id.rating_star_2);
            ratingStar3 = itemView.findViewById(R.id.rating_star_3);
            ratingStar4 = itemView.findViewById(R.id.rating_star_4);
            ratingStar5 = itemView.findViewById(R.id.rating_star_5);
            message = itemView.findViewById(R.id.message_user_reviews);
            ratingImage = itemView.findViewById(R.id.image_user_reviews);
            ratingDate = itemView.findViewById(R.id.date_user_reviews);

            deleteItem = itemView.findViewById(R.id.remove_user_review_item);
            commentBtn = itemView.findViewById(R.id.comment_btn);
            likeButton = itemView.findViewById(R.id.like_btn);
            likeCounter = itemView.findViewById(R.id.like_counter);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_reviews_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Setting up the Nested RecView
        String thisItemUID = uploads.get(holder.getAdapterPosition()).getUid();
        CommentRecViewAdapter commentRecViewAdapter = new CommentRecViewAdapter(context);
        holder.comments_list = new ArrayList<>();
        commentRecViewAdapter.setComments(holder.comments_list);
        holder.commentsRecyclerView.setHasFixedSize(true);
        holder.commentsRecyclerView.setAdapter(commentRecViewAdapter);
        holder.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(thisItemUID).child("comments").exists()){
                    for (DataSnapshot snapshot1 : snapshot.child(thisItemUID).child("comments").getChildren()){
                        Comments comments = snapshot1.getValue(Comments.class);
                        holder.comments_list.add(comments);
                    }
                    commentRecViewAdapter.notifyDataSetChanged();
                }
                else{
                    holder.commentsRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.userName.setText(uploads.get(position).getUserName());
        holder.boughtItems.setText(uploads.get(position).getBought());
        holder.ratingCount = uploads.get(position).getRating();
        holder.message.setText(uploads.get(position).getMessage());
        Glide.with(context)
                .asBitmap()
                .load(uploads.get(position).getImage())
                .into(holder.ratingImage);
        holder.ratingDate.setText(uploads.get(position).getDate());

        if(!Objects.equals(holder.uid, "CtwgPRWYDZf9AowTWmTxGoNeYDD2")){
            holder.deleteItem.setVisibility(View.GONE);
            holder.commentBtn.setVisibility(View.GONE);
        }

        // setting the stars depending on how many counts(stars) the user rated.
        if(holder.ratingCount == 1){
            holder.ratingStar1.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar2.setTextColor(context.getResources().getColor(R.color.primary));
            holder.ratingStar3.setTextColor(context.getResources().getColor(R.color.primary));
            holder.ratingStar4.setTextColor(context.getResources().getColor(R.color.primary));
            holder.ratingStar5.setTextColor(context.getResources().getColor(R.color.primary));
        }
        else if(holder.ratingCount == 2){
            holder.ratingStar1.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar2.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar3.setTextColor(context.getResources().getColor(R.color.primary));
            holder.ratingStar4.setTextColor(context.getResources().getColor(R.color.primary));
            holder.ratingStar5.setTextColor(context.getResources().getColor(R.color.primary));
        }
        else if(holder.ratingCount == 3){
            holder.ratingStar1.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar2.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar3.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar4.setTextColor(context.getResources().getColor(R.color.primary));
            holder.ratingStar5.setTextColor(context.getResources().getColor(R.color.primary));
        }
        else if(holder.ratingCount == 4){
            holder.ratingStar1.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar2.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar3.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar4.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar5.setTextColor(context.getResources().getColor(R.color.primary));
        }
        else if(holder.ratingCount == 5){
            holder.ratingStar1.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar2.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar3.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar4.setTextColor(context.getResources().getColor(R.color.accent));
            holder.ratingStar5.setTextColor(context.getResources().getColor(R.color.accent));
        }

        holder.ratingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp(holder.getAdapterPosition());
                Glide.with(context)
                        .asBitmap()
                        .load(uploads.get(holder.getAdapterPosition()).getImage())
                        .into(holder.ratingImage);
            }
        });

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteReviewItem(holder.getAdapterPosition(), uploads.get(holder.getAdapterPosition()).getUid());
            }
        });

        // If you liked this item before, it will save it in the database
        // Therefore, it'll fill the like button.
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String thisItemUID = uploads.get(holder.getAdapterPosition()).getUid();
                if(snapshot.child(thisItemUID).child("likes").exists()){
                    for(DataSnapshot uidInsideLikes : snapshot.child(thisItemUID).child("likes").getChildren()){
                        if(Objects.equals(uidInsideLikes.getKey(), userUID)){
                            Typeface typeface = ResourcesCompat.getFont(context, R.font.fontawesomefreesolid);
                            holder.likeButton.setTypeface(typeface);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Get the amount of likes on a review.
        databaseReference.child(uploads.get(position).getUid()).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long likeCount = snapshot.getChildrenCount();
                holder.likeCounter.setText(String.valueOf(likeCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //If the user is EITHER liking the button or removing the like.
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((UserReviewsActivity)context).focusAnItem(holder.getAdapterPosition());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String thisItemUID = uploads.get(holder.getAdapterPosition()).getUid();
                        String likeCounterStr = holder.likeCounter.getText().toString().trim();
                        // If the user have not liked the review yet.
                        if(!snapshot.child(thisItemUID).child("likes").child(userUID).exists()){
                            databaseReference.child(thisItemUID).child("likes").child(userUID).setValue(true);
                            int likeCounter = Integer.parseInt(likeCounterStr);
                            int newLikeCounter = likeCounter + 1;
                            likeCounterStr = String.valueOf(newLikeCounter);
                            ((UserReviewsActivity)context).refreshUserReviews();
                            showYouLikedTheReview();
                        }
                        // If the user wants to remove the like of this review.
                        else if(snapshot.child(thisItemUID).child("likes").exists()){
                            for(DataSnapshot uidInsideLikes : snapshot.child(thisItemUID).child("likes").getChildren()){
                                if(Objects.equals(uidInsideLikes.getKey(), userUID)){
                                    boolean likedOrNot = (boolean) uidInsideLikes.getValue();
                                    if(likedOrNot){
                                        databaseReference.child(thisItemUID).child("likes").child(userUID).removeValue();
                                        int likeCounter = Integer.parseInt(likeCounterStr);
                                        int newLikeCounter = likeCounter - 1;
                                        likeCounterStr = String.valueOf(newLikeCounter);
                                        ((UserReviewsActivity)context).refreshUserReviews();
                                        showYouUnlikedTheReview();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCommentDialog(uploads.get(holder.getAdapterPosition()).getUid());
            }
        });
    }

    public void deleteReviewItem(int position, String reviewItemID){
        StorageReference imageRef = storageRef.getReferenceFromUrl(uploads.get(position).getImage());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseReference.child(reviewItemID).removeValue();
                uploads.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                ((UserReviewsActivity)context).refreshUserReviews();
            }
        });
    }

    public void showAddCommentDialog(String itemUID){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.set_comment_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        EditText userInput = dialog.findViewById(R.id.comment_input);
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        Button userPressNo = dialog.findViewById(R.id.user_press_no);

        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                String timeInput = df.format(Calendar.getInstance().getTime());
                String userInputString = userInput.getText().toString().trim();
                if(userInputString.isEmpty()){
                    userInput.setError("Your message is required!");
                    userInput.requestFocus();
                    return;
                }

                //Creating random numbers that will be named in
                Random rnd = new Random();
                String rndStr = "";
                int rndInt = rnd.nextInt(999);
                int rndIntForLetters = rnd.nextInt(75);

                if(rndIntForLetters <= 25){
                    rndStr = "a";
                }
                else if(rndIntForLetters <= 50 && rndIntForLetters >= 25){
                    rndStr = "b";
                }
                else if(rndIntForLetters <= 75 && rndIntForLetters >= 50){
                    rndStr = "c";
                }
                String randomCode = rndStr + String.valueOf(rndInt);

                databaseReference.child(itemUID).child("comments").child(randomCode).child("message").setValue(userInputString);
                databaseReference.child(itemUID).child("comments").child(randomCode).child("username").setValue(userName);
                databaseReference.child(itemUID).child("comments").child(randomCode).child("userUID").setValue(userUID);
                databaseReference.child(itemUID).child("comments").child(randomCode).child("reviewUID").setValue(itemUID);
                databaseReference.child(itemUID).child("comments").child(randomCode).child("commentCode").setValue(randomCode);
                databaseReference.child(itemUID).child("comments").child(randomCode).child("time").setValue(timeInput);
                dialog.dismiss();
                ((UserReviewsActivity)context).refreshUserReviews();
                showYouEnteredAComment();
            }
        });
        userPressNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showPopUp(int position){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.product_pop_up_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView newImage = dialog.findViewById(R.id.pop_up_image);
        TextView newText = dialog.findViewById(R.id.pop_up_name);
        TextView sizes = dialog.findViewById(R.id.sizes);
        TextView quality = dialog.findViewById(R.id.quality);
        TextView collar = dialog.findViewById(R.id.collar);
        newText.setVisibility(View.GONE);
        sizes.setVisibility(View.GONE);
        quality.setVisibility(View.GONE);
        collar.setVisibility(View.GONE);

        Glide.with(context)
                .asBitmap()
                .load(uploads.get(position).getImage())
                .into(newImage);
        dialog.show();
    }

    public void showYouLikedTheReview(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf164");
        toastText.setText("You liked a review!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showYouUnlikedTheReview(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf165");
        toastText.setText("You unliked a review!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showYouEnteredAComment(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf7f5");
        toastText.setText("You added a comment to a review!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }
}
