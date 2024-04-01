package com.example.celcious;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {
    private List<AdminProduct> adminProducts = new ArrayList<>();
    private Context context;

    public AdminProductAdapter (Context context){this.context = context;}
    public void setAdminProducts (List<AdminProduct> adminProducts){
        this.adminProducts = adminProducts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView price;
        private TextView name;
        private TextView quantity;
        private ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.admin_product_price);
            name = itemView.findViewById(R.id.admin_product_name);
            quantity = itemView.findViewById(R.id.admin_product_quantity);
            image = itemView.findViewById(R.id.admin_product_image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.price.setText(adminProducts.get(position).getPrice());
        Glide.with(context)
                .asBitmap()
                .load(adminProducts.get(position).getImage())
                .into(holder.image);
        holder.name.setText(adminProducts.get(position).getName());
        holder.quantity.setText(String.valueOf(adminProducts.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        return adminProducts.size();
    }
}
