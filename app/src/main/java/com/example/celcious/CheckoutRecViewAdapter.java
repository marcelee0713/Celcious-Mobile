package com.example.celcious;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckoutRecViewAdapter extends RecyclerView.Adapter<CheckoutRecViewAdapter.ViewHolder> {
    private ArrayList<Checkout> checkouts = new ArrayList<>();
    private Context context;
    int priceToInt;
    int priceHolder;
    int priceTotal = 0;
    int productQuantity;

    public CheckoutRecViewAdapter (Context context){this.context = context;}
    public void setCheckouts (ArrayList<Checkout> checkouts){
        this.checkouts = checkouts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView price;
        private TextView name;
        private TextView quantity;
        private ImageView image;
        final Handler handler = new Handler(Looper.getMainLooper());
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.checkout_item_price);
            name = itemView.findViewById(R.id.checkout_name);
            quantity = itemView.findViewById(R.id.checkout_quantity);
            image = itemView.findViewById(R.id.checkout_image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.price.setText(checkouts.get(position).getPrice());
        Glide.with(context)
                .asBitmap()
                .load(checkouts.get(position).getImage())
                .into(holder.image);
        holder.name.setText(checkouts.get(position).getName());
        holder.quantity.setText(String.valueOf(checkouts.get(position).getQuantity()));

        // Price conversion
        String priceToStr = checkouts.get(position).getPrice().replaceAll("₱", "");
        int quantityCount = checkouts.get(holder.getAdapterPosition()).getQuantity();
        priceToInt = Integer.parseInt(priceToStr);
        priceToInt = priceToInt * quantityCount;
        priceHolder = priceToInt;
        priceTotal += priceHolder;


        productQuantity = checkouts.get(position).getQuantity();
        // Since getting the data is slower than the app
        // We used handler inorder to get that total price
        holder.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getThePrice();
                getTheQuantity();
            }
        }, 100);
    }

    public int getTheQuantity(){return productQuantity;}

    public int getThePrice(){
        return priceTotal;
    }

    @Override
    public int getItemCount() {
        return checkouts.size();
    }




}
