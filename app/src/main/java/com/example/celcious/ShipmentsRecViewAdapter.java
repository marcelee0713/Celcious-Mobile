package com.example.celcious;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ShipmentsRecViewAdapter extends RecyclerView.Adapter<ShipmentsRecViewAdapter.ViewHolder> {
    private String userName = HomeActivity.userName;
    private String uid = HomeActivity.uid;
    private ArrayList<Shipments> shipments = new ArrayList<>();

    private Context context;
    private Dialog dialogForAssurance;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference cancelReference = rootRef.child("Users").child(uid).child(userName+"Shipments");
    DatabaseReference productReference = rootRef.child("Products");
    DatabaseReference doneItemsReference = rootRef.child("Users").child(uid).child(userName+"DoneShipments");
    DatabaseReference meetUpReference = rootRef.child("MeetUpShipments");
    DatabaseReference codReference = rootRef.child("CODShipments");
    int getProductStock;
    int newTotalProductStock;

    public ShipmentsRecViewAdapter (Context context){this.context = context;}
    public void setShipments (ArrayList<Shipments> shipments){
        this.shipments = shipments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipments_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        private Button cancelButton;
        private RecyclerView productsRecyclerView;
        private List<ShippingProduct> shippingProducts_list;

        private TextView emailaddress;
        private TextView facebook;
        private TextView fulladdress;
        private TextView fullname;
        private TextView paymentmethod;
        private TextView phonenumber;
        private TextView postalcode;
        private TextView schedule;
        private TextView region;
        private TextView status;
        private TextView totalprice;
        private TextView tracknumber;
        private boolean btnEnable;
        private boolean done;

        private DatabaseReference databaseForProductsReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child(userName).child(userName+"Shipments");
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productsRecyclerView = itemView.findViewById(R.id.products_recyclerView);
            cancelButton = itemView.findViewById(R.id.cancel_btn);

            emailaddress = itemView.findViewById(R.id.detail_user_emailAddress);
            facebook = itemView.findViewById(R.id.detail_user_facebook);
            fulladdress = itemView.findViewById(R.id.detail_user_address);
            fullname = itemView.findViewById(R.id.detail_user_fullName);
            paymentmethod = itemView.findViewById(R.id.detail_user_paymentMethod);
            phonenumber = itemView.findViewById(R.id.detail_user_phoneNumber);
            postalcode = itemView.findViewById(R.id.detail_user_postalCode);
            schedule = itemView.findViewById(R.id.detail_user_schedule);
            region = itemView.findViewById(R.id.detail_user_region);
            status = itemView.findViewById(R.id.detail_user_shippingStatus);
            totalprice = itemView.findViewById(R.id.detail_user_total);
            tracknumber = itemView.findViewById(R.id.detail_user_shippingTrackingNumber);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Setting up the Nested RecView
        ShippingProductAdapter productAdapter = new ShippingProductAdapter(context);
        holder.shippingProducts_list = new ArrayList<>(shipments.get(position).getProducts().values());
        holder.productsRecyclerView.setHasFixedSize(true);
        holder.productsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.productsRecyclerView.setAdapter(productAdapter);
        productAdapter.setShippingProducts(holder.shippingProducts_list);
        productAdapter.notifyDataSetChanged();
        //For the shipping
        String homeNumberBuildingStreet = shipments.get(position).getFulladdress();
        String province = shipments.get(position).getProvince();
        String city = shipments.get(position).getCity();
        String barangay = shipments.get(position).getBarangay();
        holder.emailaddress.setText(shipments.get(position).getEmailaddress());
        holder.facebook.setText(shipments.get(position).getFacebook());
        holder.fulladdress.setText(homeNumberBuildingStreet + " " + barangay + " " + city + " " + province);
        holder.fullname.setText(shipments.get(position).getFullname());
        holder.phonenumber.setText(shipments.get(position).getPhonenumber());
        holder.postalcode.setText(shipments.get(position).getPostalcode());
        holder.totalprice.setText(String.valueOf(shipments.get(position).getTotalprice()));

        holder.tracknumber.setText(shipments.get(position).getTracknumber());
        holder.status.setText(shipments.get(position).getStatus());
        holder.region.setText(shipments.get(position).getRegion());
        holder.schedule.setText(shipments.get(position).getSchedule());
        holder.paymentmethod.setText(shipments.get(position).getPaymentmethod());

        String userSchedule = shipments.get(holder.getAdapterPosition()).getSchedule();

        holder.totalprice.setText("Total Payment: ₱"+shipments.get(position).getTotalprice());

        String selectedPaymentMethod = shipments.get(holder.getAdapterPosition()).getPaymentmethod();
        if(Objects.equals(selectedPaymentMethod, "COD")){
            holder.schedule.setVisibility(View.GONE);

            String tNumber = shipments.get(holder.getAdapterPosition()).getTracknumber();

            if(tNumber.isEmpty()){
                holder.tracknumber.setText("Tracking Number: Your tracking number will be here soon!");
            }
            else {
                holder.cancelButton.setEnabled(holder.btnEnable);
                holder.tracknumber.setText("Tracking Number: " + shipments.get(holder.getAdapterPosition()).getTracknumber());
                holder.status.setText("Your item(s) are now shipped!\nYou can not cancel them anymore.");
            }
        }
        else if(Objects.equals(selectedPaymentMethod, "Meet up")){
            holder.tracknumber.setVisibility(View.GONE);
            holder.status.setVisibility(View.GONE);
            holder.region.setVisibility(View.GONE);

            if(userSchedule.equals("Wednesday")) {
                holder.schedule.setText("Wednesday\n11:00am - 12:00pm\nInside McDonald's, PHINMA University of Pangasinan, Dagupan City, Pangasinan");
            }
            else if(userSchedule.equals("Thursday")){
                holder.schedule.setText("Thursday\n11:00am - 12:00pm\nInside McDonald's, PHINMA University of Pangasinan, Dagupan City, Pangasinan");
            }
            else if(userSchedule.equals("Saturday")){
                holder.schedule.setText("Saturday\n1:00pm - 2:00pm\nFood court on 2nd Floor,\n Robinsons Place Pangasinan, McArthur Highway, Calasiao, Pangasinan");
            }
            else if(userSchedule.equals("Sunday")){
                holder.schedule.setText("Sunday\n4:00pm - 5:00pm\nFood court in Public Plaza,\n Poblacion Norte, Public Plaza, Santa Barbara, Pangasinan");
            }
        }

        // Inorder to get the database boolean for the button
        // key name is setenablebutton
        holder.btnEnable = shipments.get(holder.getAdapterPosition()).isSetenablebutton();
        holder.cancelButton.setEnabled(holder.btnEnable);

        //when the user wants to cancel its order
        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAssurance(holder.getAdapterPosition(), productAdapter, holder.shippingProducts_list, shipments.get(holder.getAdapterPosition()).getPaymentmethod());
            }
        });

        //if the delivery or meet up is done
        holder.done = shipments.get(holder.getAdapterPosition()).isDone();

        String schedule = shipments.get(holder.getAdapterPosition()).getSchedule();

        String region = shipments.get(holder.getAdapterPosition()).getRegion();
        String trackNumber = shipments.get(holder.getAdapterPosition()).getTracknumber();
        String status = shipments.get(holder.getAdapterPosition()).getStatus();

        String fullName = shipments.get(holder.getAdapterPosition()).getFullname();
        String phoneNumber = shipments.get(holder.getAdapterPosition()).getPhonenumber();
        String fullAddress = shipments.get(holder.getAdapterPosition()).getFulladdress();
        String provinceAddress = shipments.get(holder.getAdapterPosition()).getProvince();
        String cityAddress = shipments.get(holder.getAdapterPosition()).getCity();
        String barangayAddress = shipments.get(holder.getAdapterPosition()).getBarangay();
        String postalCode = shipments.get(holder.getAdapterPosition()).getPostalcode();
        String emailAddress = shipments.get(holder.getAdapterPosition()).getEmailaddress();
        String facebook = shipments.get(holder.getAdapterPosition()).getFacebook();
        String paymentMethod = shipments.get(holder.getAdapterPosition()).getPaymentmethod();
        int totalPrice = shipments.get(holder.getAdapterPosition()).getTotalprice();
        String shipmentNum = shipments.get(holder.getAdapterPosition()).getShipmentNumber();

        List<ShippingProduct> products = new ArrayList<>(shipments.get(holder.getAdapterPosition()).getProducts().values());

        if (holder.done){
            for(int i = 0; i < products.size(); i++){
                String productsName = products.get(i).getName().toLowerCase().toString().replaceAll(" ", "");
                doneItemsReference.child(shipmentNum).child("products").child(userName+productsName).child("image").setValue(products.get(i).getImage());
                doneItemsReference.child(shipmentNum).child("products").child(userName+productsName).child("name").setValue(products.get(i).getName());
                doneItemsReference.child(shipmentNum).child("products").child(userName+productsName).child("price").setValue(products.get(i).getPrice());
                doneItemsReference.child(shipmentNum).child("products").child(userName+productsName).child("quantity").setValue(products.get(i).getQuantity());
            }
            if(Objects.equals(selectedPaymentMethod, "COD")){
                doneItemsReference.child(shipmentNum).child("region").setValue(region);
                doneItemsReference.child(shipmentNum).child("tracknumber").setValue(trackNumber);
                doneItemsReference.child(shipmentNum).child("status").setValue(status);

                doneItemsReference.child(shipmentNum).child("fullname").setValue(fullName);
                doneItemsReference.child(shipmentNum).child("phonenumber").setValue(phoneNumber);
                doneItemsReference.child(shipmentNum).child("fulladdress").setValue(fullAddress);
                doneItemsReference.child(shipmentNum).child("province").setValue(provinceAddress);
                doneItemsReference.child(shipmentNum).child("city").setValue(cityAddress);
                doneItemsReference.child(shipmentNum).child("barangay").setValue(barangayAddress);
                doneItemsReference.child(shipmentNum).child("postalcode").setValue(postalCode);
                doneItemsReference.child(shipmentNum).child("emailaddress").setValue(emailAddress);
                doneItemsReference.child(shipmentNum).child("facebook").setValue(facebook);
                doneItemsReference.child(shipmentNum).child("paymentmethod").setValue(paymentMethod);
                doneItemsReference.child(shipmentNum).child("totalprice").setValue(totalPrice);
                doneItemsReference.child(shipmentNum).child("setenablebutton").setValue(false);
                doneItemsReference.child(shipmentNum).child("done").setValue(true);
                doneItemsReference.child(shipmentNum).child("shipmentNumber").setValue(shipmentNum);

                cancelReference.child("products").removeValue();
                cancelReference.child(shipments.get(position).getShipmentNumber()).removeValue();
                shipments.remove(position);
                try{
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,getItemCount());
                }
                catch (Exception e){
                    Log.i("ERROR OF THE SHIT MAN", e.toString());
                }
                ((ShipmentsActivity)context).refreshShipmentActivity();
            }
            else if(Objects.equals(selectedPaymentMethod, "Meet up")){
                doneItemsReference.child(shipmentNum).child("schedule").setValue(schedule);

                doneItemsReference.child(shipmentNum).child("fullname").setValue(fullName);
                doneItemsReference.child(shipmentNum).child("phonenumber").setValue(phoneNumber);
                doneItemsReference.child(shipmentNum).child("fulladdress").setValue(fullAddress);
                doneItemsReference.child(shipmentNum).child("province").setValue(provinceAddress);
                doneItemsReference.child(shipmentNum).child("city").setValue(cityAddress);
                doneItemsReference.child(shipmentNum).child("barangay").setValue(barangayAddress);
                doneItemsReference.child(shipmentNum).child("postalcode").setValue(postalCode);
                doneItemsReference.child(shipmentNum).child("emailaddress").setValue(emailAddress);
                doneItemsReference.child(shipmentNum).child("facebook").setValue(facebook);
                doneItemsReference.child(shipmentNum).child("paymentmethod").setValue(paymentMethod);
                doneItemsReference.child(shipmentNum).child("totalprice").setValue(totalPrice);
                doneItemsReference.child(shipmentNum).child("setenablebutton").setValue(false);
                doneItemsReference.child(shipmentNum).child("done").setValue(true);
                doneItemsReference.child(shipmentNum).child("shipmentNumber").setValue(shipmentNum);

                cancelReference.child("products").removeValue();
                cancelReference.child(shipments.get(position).getShipmentNumber()).removeValue();
                shipments.remove(position);
                try{
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,getItemCount());
                }
                catch (Exception e){
                    Log.i("error", e.toString());
                }
                ((ShipmentsActivity)context).refreshShipmentActivity();
            }
        }
    }

    public void showDialogAssurance(int position, ShippingProductAdapter shippingProductAdapter, List<ShippingProduct> shippingProducts, String paymentMethod){
        Log.i("USERKEY", shipments.get(position).getShipmentNumber());
        Log.d("SHIPPING", String.valueOf(shippingProducts));
        dialogForAssurance = new Dialog(context);
        dialogForAssurance.setContentView(R.layout.cancel_dialog);
        dialogForAssurance.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogForAssurance.getWindow().setGravity(Gravity.CENTER);
        dialogForAssurance.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogForAssurance.setCancelable(false);
        Button userPressYes = dialogForAssurance.findViewById(R.id.user_press_yes);
        Button userPressNo = dialogForAssurance.findViewById(R.id.user_press_no);

        // When the user wants to cancel his order
        // So the product's stock will be added depends on the user's quantity on the item
        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // so add the product stock first based on the item quantity
                for (int i = 0; i < shippingProducts.size(); i++){
                    String getItemName = shippingProducts.get(i).getName().toLowerCase().replaceAll(" ", "");
                    Log.i("SHIPPINGNAME", getItemName);
                    int getItemQuantity = shippingProducts.get(i).getQuantity();
                    Log.i("SHIPPINGCOUNT", String.valueOf(getItemQuantity));


                    Log.i("SHIPPINGSIZE", String.valueOf(shippingProducts.size()));
                    productReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            getProductStock = Math.toIntExact((Long) snapshot.child(getItemName).child("stock").getValue());
                            newTotalProductStock = getItemQuantity + getProductStock;
                            Log.i("SHIPPINGNEWCOUNT", String.valueOf(newTotalProductStock));
                            productReference.child(getItemName).child("stock").setValue(newTotalProductStock);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //Remove the product's ordered in the database
                    // and remove it from the recyclerview
                    cancelReference.child("products").removeValue();
                    //shippingProducts.remove(i);
                    //shippingProductAdapter.notifyItemRemoved(i);
                    //shippingProductAdapter.notifyItemRangeChanged(i,shippingProductAdapter.getItemCount());
                }
                //Remove the items in the user's admin
                if(paymentMethod.equals("COD")){
                    codReference.child(shipments.get(position).getShipmentNumber()).removeValue();
                }
                else if (paymentMethod.equals("Meet up")){
                    meetUpReference.child(shipments.get(position).getShipmentNumber()).removeValue();
                }

                //Also remove the items in the database and in this recyclerview
                cancelReference.child(shipments.get(position).getShipmentNumber()).removeValue();
                shipments.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
                ((ShipmentsActivity)context).refreshShipmentActivity();

                dialogForAssurance.dismiss();
            }
        });
        userPressNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForAssurance.dismiss();
            }
        });
        dialogForAssurance.show();
    }

    @Override
    public int getItemCount() {
        return shipments.size();
    }
}
