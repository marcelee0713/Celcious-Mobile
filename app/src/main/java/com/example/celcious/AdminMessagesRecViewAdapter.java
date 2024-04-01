package com.example.celcious;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
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

public class AdminMessagesRecViewAdapter extends RecyclerView.Adapter<AdminMessagesRecViewAdapter.ViewHolder> {
    private ArrayList<AdminMessages> adminMessages = new ArrayList<>();
    private Context context;

    private Dialog dialog;

    public AdminMessagesRecViewAdapter(Context context){this.context = context;}
    public void setAdminMessages(ArrayList<AdminMessages> adminMessages) {
        this.adminMessages = adminMessages;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView fullName;
        private TextView email;
        private TextView message;
        private TextView time;

        private Button deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.messages_full_name);
            email = itemView.findViewById(R.id.messages_email);
            message = itemView.findViewById(R.id.messages_qc);
            time = itemView.findViewById(R.id.messages_time);

            deleteButton = itemView.findViewById(R.id.delete_message);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_messages_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fullName.setText(adminMessages.get(position).getFullName());
        holder.email.setText(adminMessages.get(position).getEmail());
        holder.message.setText(adminMessages.get(position).getMessage());
        holder.time.setText(adminMessages.get(position).getTime());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(holder.getAdapterPosition(), adminMessages.get(holder.getAdapterPosition()).getQuestionNumber());
            }
        });
    }

    public void showDeleteDialog(int position, String questionNumber){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_message_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        Button userPressNo = dialog.findViewById(R.id.user_press_no);
        DatabaseReference questionsReference = FirebaseDatabase.getInstance().getReference("Questions");

        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionsReference.child(questionNumber).removeValue();
                adminMessages.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
                ((AdminMessagesActivity)context).refreshAdminActivity();
                dialog.dismiss();
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

    @Override
    public int getItemCount() {
        return adminMessages.size();
    }
}
