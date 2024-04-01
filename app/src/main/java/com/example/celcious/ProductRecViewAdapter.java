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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProductRecViewAdapter extends RecyclerView.Adapter<ProductRecViewAdapter.ViewHolder>{
    //for popup
    private Dialog dialog;
    private ArrayList<Products> products = new ArrayList<>();
    private Context context;

    public ProductRecViewAdapter(Context context){this.context = context;}
    public void setProducts (ArrayList<Products> products){
        this.products = products;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView price;
        private TextView name;
        private TextView stock;
        private TextView quantity;
        private ImageView image;
        private Button cartButton;
        private Button addQuantityBtn;
        private Button subtractQuantityBtn;
        // These things are for placing a cart item(product), in the user's Cart.
        private String userName = HomeActivity.userName;
        private String uid = HomeActivity.uid;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            price = itemView.findViewById(R.id.home_price);
            name = itemView.findViewById(R.id.home_product_name);
            stock = itemView.findViewById(R.id.in_stock);
            quantity = itemView.findViewById(R.id.quantity_count);
            image = itemView.findViewById(R.id.home_image);
            cartButton = itemView.findViewById(R.id.home_add_to_cart_button);
            addQuantityBtn = itemView.findViewById(R.id.addQuantityBtn);
            subtractQuantityBtn = itemView.findViewById(R.id.subtractQuantityBtn);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.price.setText(products.get(position).getPrice());
        holder.stock.setText((String.valueOf(products.get(position).getStock())));
        Glide.with(context)
                .asBitmap()
                .load(products.get(position).getImage())
                .into(holder.image);
        holder.name.setText(products.get(position).getName());

        //if a product is out of stock
        int getOutOfStockNumber = products.get(holder.getAdapterPosition()).getStock();
        if(getOutOfStockNumber == 0){
            // also set the number of the quantity to 0
            // since the product is out of stock
            products.get(holder.getAdapterPosition()).setQuantity(getOutOfStockNumber);
            holder.quantity.setText((String.valueOf(products.get(holder.getAdapterPosition()).getQuantity())));
        }

        //when the user decrease quantity
        holder.subtractQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityCounter = products.get(holder.getAdapterPosition()).getQuantity();
                if(quantityCounter == 1){
                    showMinStock();
                }
                // when the user tries to reduce a quantity when the product is out stock
                else if(quantityCounter == 0){
                    showProductOutOfStock();
                }
                else{
                    quantityCounter -= 1;
                    products.get(holder.getAdapterPosition()).setQuantity(quantityCounter);

                    holder.quantity.setText((String.valueOf(products.get(holder.getAdapterPosition()).getQuantity())));
                }
                notifyDataSetChanged();
            }
        });

        //when the user adds quantity
        holder.addQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockCounter = products.get(holder.getAdapterPosition()).getStock();
                int quantityCounter = products.get(holder.getAdapterPosition()).getQuantity();
                // when the user tries to add a quantity when the product is out stock
                if(quantityCounter == 0){
                    showProductOutOfStock();

                }
                else if(quantityCounter == stockCounter){
                    showMaxStock();
                }
                else{
                    quantityCounter += 1;
                    products.get(holder.getAdapterPosition()).setQuantity(quantityCounter);

                    holder.quantity.setText((String.valueOf(products.get(holder.getAdapterPosition()).getQuantity())));
                }
                notifyDataSetChanged();
            }
        });

        holder.cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the user tries to add a product
                // that is out of stock
                if(getOutOfStockNumber == 0){
                    showProductOutOfStock();
                }
                // When the product is in stock
                // So the user can add it to the cart :)
                else{
                    String name = products.get(holder.getAdapterPosition()).getName();
                    String price =  products.get(holder.getAdapterPosition()).getPrice();
                    String image = products.get(holder.getAdapterPosition()).getImage();
                    int quantity = products.get(holder.getAdapterPosition()).getQuantity();
                    int stock = products.get(holder.getAdapterPosition()).getStock();

                    //get the name of the product
                    String itemName = products.get(holder.getAdapterPosition()).getName().toLowerCase().replaceAll(" ", "");

                    holder.databaseReference.child(holder.uid).child(holder.userName+"Cart").child(holder.userName+"Cart"+itemName).child("name").setValue(name);
                    holder.databaseReference.child(holder.uid).child(holder.userName+"Cart").child(holder.userName+"Cart"+itemName).child("price").setValue(price);
                    holder.databaseReference.child(holder.uid).child(holder.userName+"Cart").child(holder.userName+"Cart"+itemName).child("image").setValue(image);
                    holder.databaseReference.child(holder.uid).child(holder.userName+"Cart").child(holder.userName+"Cart"+itemName).child("quantity").setValue(quantity);
                    holder.databaseReference.child(holder.uid).child(holder.userName+"Cart").child(holder.userName+"Cart"+itemName).child("stock").setValue(stock);

                    showCartToast();
                }
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp(holder.getAdapterPosition(), holder.name);
                Glide.with(context)
                        .asBitmap()
                        .load(products.get(holder.getAdapterPosition()).getImage())
                        .into(holder.image);
            }
        });

    }

    public void showPopUp(int position, TextView productName){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.product_pop_up_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView newImage = dialog.findViewById(R.id.pop_up_image);
        TextView newText = dialog.findViewById(R.id.pop_up_name);

        String productText = productName.getText().toString();
        Glide.with(context)
                .asBitmap()
                .load(products.get(position).getImage())
                .into(newImage);
        newText.setText(productText);
        dialog.show();
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

    public void showCartToast(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf290");
        toastText.setText("Added to cart!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
