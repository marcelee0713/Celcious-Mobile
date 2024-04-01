package com.example.celcious;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class CartRecViewAdapter extends RecyclerView.Adapter<CartRecViewAdapter.ViewHolder>{
    private ArrayList<Cart> carts = new ArrayList<>();
    private Context context;
    private boolean isSelectedAll;

    //This is for the Add all and Remove all button
    public void selectAll(){
        isSelectedAll = true;
        notifyDataSetChanged();
    }
    public void unSelectAll(){
        isSelectedAll = false;
        notifyDataSetChanged();
    }

    public CartRecViewAdapter(Context context){this.context = context;}
    public void setCarts (ArrayList<Cart> carts){
        this.carts = carts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView price;
        private TextView name;
        private TextView stock;
        private TextView quantity;
        private ImageView image;
        private Button addQuantityBtn;
        private Button subtractQuantityBtn;
        private TextView removeCartItemBtn;
        private CheckBox checkBox;
        private String userName = HomeActivity.userName;
        private String uid = HomeActivity.uid;
        // This is for getting the updated stocks :)
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        // This one is for getting the user's cart and sending data to the user's Checkout
        DatabaseReference databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.cart_price);
            name = itemView.findViewById(R.id.cart_name);
            stock = itemView.findViewById(R.id.cart_inStock);
            quantity = itemView.findViewById(R.id.cart_quantity_count);
            image = itemView.findViewById(R.id.cart_image);
            addQuantityBtn = itemView.findViewById(R.id.cartAddQuantityBtn);
            subtractQuantityBtn = itemView.findViewById(R.id.cartSubtractQuantityBtn);
            removeCartItemBtn = itemView.findViewById(R.id.cart_remove_btn);
            checkBox = itemView.findViewById(R.id.cart_product_checkbox);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.price.setText(carts.get(position).getPrice());
        Glide.with(context)
                .asBitmap()
                .load(carts.get(position).getImage())
                .into(holder.image);
        holder.name.setText(carts.get(position).getName());
        holder.quantity.setText(String.valueOf(carts.get(position).getQuantity()));

        //This is for the RemoveAll and AddAll Button
        if (!isSelectedAll){
            holder.checkBox.setChecked(false);
        }
        else{
            holder.checkBox.setChecked(true);
        }

        //Adds or remove the item in Checkout Page when the item's checkbox is checked
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // for creating nodes and parents
                String itemName = carts.get(holder.getAdapterPosition()).getName().toLowerCase().replaceAll(" ", "");
                String getUserName = holder.userName;

                // for the child
                String name = carts.get(holder.getAdapterPosition()).getName();
                String price =  carts.get(holder.getAdapterPosition()).getPrice();
                String image = carts.get(holder.getAdapterPosition()).getImage();
                int getUpdatedQuantity = Integer.parseInt((String) holder.quantity.getText());
                if(b){
                    // This adds the item in the checkout
                    holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("name").setValue(name);
                    holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("price").setValue(price);
                    holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("image").setValue(image);
                    holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("quantity").setValue(getUpdatedQuantity);
                }
                else{
                    //This removes the item in the Checkout
                    holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("name").removeValue();
                    holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("price").removeValue();
                    holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("image").removeValue();
                    holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("quantity").removeValue();
                }
            }
        });
        // This will remove everything in the Checkout Data
        // When the item that is NOT CHECKED in the User's cart
        if(!holder.checkBox.isChecked()){
            // for creating nodes and parents
            String itemName = carts.get(holder.getAdapterPosition()).getName().toLowerCase().replaceAll(" ", "");
            String getUserName = holder.userName;
            holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("name").removeValue();
            holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("price").removeValue();
            holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("image").removeValue();
            holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("quantity").removeValue();
        }

        //This removes a specific item in the users cart
        holder.removeCartItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = carts.get(holder.getAdapterPosition()).getName().toLowerCase().replaceAll(" ", "");
                String getUserName = holder.userName;
                holder.databaseUserReference
                        .child(holder.uid)
                        .child(getUserName+"Cart")
                        .child(getUserName+"Cart"+itemName)
                        .removeValue();
                carts.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(),getItemCount());
                // Also remove that item in the Database
                holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("name").removeValue();
                holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("price").removeValue();
                holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("image").removeValue();
                holder.databaseUserReference.child(holder.uid).child(getUserName+"Checkout").child(getUserName+"Checkout"+itemName).child("quantity").removeValue();
                ((CartActivity)context).refreshActivity();
                showUserRemovesItem();
            }
        });


        //This gets the updated stock
        holder.databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String itemName = carts.get(holder.getAdapterPosition()).getName().toLowerCase().replaceAll(" ", "");
                int getUpdateStock = Math.toIntExact((Long) snapshot.child(itemName).child("stock").getValue());
                carts.get(holder.getAdapterPosition()).setStock(getUpdateStock);
                holder.stock.setText((String.valueOf(carts.get(holder.getAdapterPosition()).getStock())));

                //if a product is out of stock
                if(getUpdateStock == 0){
                    // also set the number of the quantity to 0
                    // since the product is out of stock
                    // and make the visibility of the checkbox gone
                    carts.get(holder.getAdapterPosition()).setQuantity(getUpdateStock);
                    holder.quantity.setText((String.valueOf(carts.get(holder.getAdapterPosition()).getQuantity())));
                    holder.checkBox.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //when the user decrease quantity
        holder.subtractQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityCounter = carts.get(holder.getAdapterPosition()).getQuantity();
                if(quantityCounter == 1){
                    showMinStock();
                }
                // when the user tries to reduce a quantity when the product is out stock
                else if(quantityCounter == 0){
                    showProductOutOfStock();
                }
                else{
                    quantityCounter -= 1;
                    carts.get(holder.getAdapterPosition()).setQuantity(quantityCounter);

                    holder.quantity.setText((String.valueOf(carts.get(holder.getAdapterPosition()).getQuantity())));
                }
                holder.checkBox.setChecked(false);
                notifyDataSetChanged();
            }
        });

        //when the user adds quantity
        holder.addQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockCounter = carts.get(holder.getAdapterPosition()).getStock();
                int quantityCounter = carts.get(holder.getAdapterPosition()).getQuantity();
                // when the user tries to add a quantity when the product is out stock
                if(quantityCounter == 0){
                    showProductOutOfStock();
                }
                else if(quantityCounter == stockCounter){
                    showMaxStock();
                }
                else{
                    quantityCounter += 1;
                    carts.get(holder.getAdapterPosition()).setQuantity(quantityCounter);

                    holder.quantity.setText((String.valueOf(carts.get(holder.getAdapterPosition()).getQuantity())));
                }
                holder.checkBox.setChecked(false);
                notifyDataSetChanged();
            }
        });
    }

    public void showProductOutOfStock(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf00d");
        toastText.setText("The product is out of stock!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showMinStock(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("That's the minimum quantity");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showMaxStock(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("That's the maximum quantity");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showUserRemovesItem(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf2ed");
        toastText.setText("Successfully remove an item.");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

}
