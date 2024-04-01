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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShippingProductAdapter extends RecyclerView.Adapter<ShippingProductAdapter.ViewHolder> {
    private List<ShippingProduct> shippingProducts = new ArrayList<>();
    private Context context;

    public ShippingProductAdapter (Context context){this.context = context;}
    public void setShippingProducts (List<ShippingProduct> shippingProducts){
        this.shippingProducts = shippingProducts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView price;
        private TextView name;
        private TextView quantity;
        private ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.shipment_product_price);
            name = itemView.findViewById(R.id.shipment_product_name);
            quantity = itemView.findViewById(R.id.shipment_product_quantity);
            image = itemView.findViewById(R.id.shipment_product_image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipping_product_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.price.setText(shippingProducts.get(position).getPrice());
        Glide.with(context)
                .asBitmap()
                .load(shippingProducts.get(position).getImage())
                .into(holder.image);
        holder.name.setText(shippingProducts.get(position).getName());
        holder.quantity.setText(String.valueOf(shippingProducts.get(position).getQuantity()));
    }


    @Override
    public int getItemCount() {
        return shippingProducts.size();
    }

}
