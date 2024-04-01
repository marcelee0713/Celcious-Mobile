package com.example.celcious;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AdminCODRecViewAdapter extends RecyclerView.Adapter<AdminCODRecViewAdapter.ViewHolder> {
    private ArrayList<Admin> admins = new ArrayList<>();
    private Context context;

    private Dialog dialog;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference productReference = rootRef.child("Products");
    DatabaseReference CODReference = rootRef.child("CODShipments");
    int getProductStock;
    int newTotalProductStock;

    public AdminCODRecViewAdapter (Context context){this.context = context;}
    public void setAdmins (ArrayList<Admin> admins){
        this.admins = admins;
        notifyDataSetChanged();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        private Button setTrackNumberButton;
        private Button setBtnToFalseButton;
        private Button cancelButton;
        private Button doneButton;
        private Button changePriceButton;
        private RecyclerView productsRecyclerView;
        private List<AdminProduct> adminProducts_list;

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
        private TextView time;
        private TextView adminCODStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productsRecyclerView = itemView.findViewById(R.id.products_recyclerView);
            setTrackNumberButton = itemView.findViewById(R.id.set_track_number_btn);
            setBtnToFalseButton = itemView.findViewById(R.id.set_false_btn_cancel);
            doneButton = itemView.findViewById(R.id.done_btn);
            cancelButton = itemView.findViewById(R.id.cancel_btn);
            changePriceButton = itemView.findViewById(R.id.change_price_btn);

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
            time = itemView.findViewById(R.id.time);
            adminCODStatus = itemView.findViewById(R.id.admin_track_num_and_set_false_status);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Setting up the Nested RecView
        AdminProductAdapter adminProductAdapter = new AdminProductAdapter(context);
        holder.adminProducts_list = new ArrayList<>(admins.get(position).getProducts().values());
        holder.productsRecyclerView.setHasFixedSize(true);
        holder.productsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.productsRecyclerView.setAdapter(adminProductAdapter);
        adminProductAdapter.setAdminProducts(holder.adminProducts_list);
        adminProductAdapter.notifyDataSetChanged();
        //For the shipping
        String homeNumberBuildingStreet = admins.get(position).getFulladdress();
        String province = admins.get(position).getProvince();
        String city = admins.get(position).getCity();
        String barangay = admins.get(position).getBarangay();
        holder.emailaddress.setText(admins.get(position).getEmailaddress());
        holder.facebook.setText(admins.get(position).getFacebook());
        holder.fulladdress.setText(homeNumberBuildingStreet + " " + barangay + " " + city + " " + province);
        holder.fullname.setText(admins.get(position).getFullname());
        holder.phonenumber.setText(admins.get(position).getPhonenumber());
        holder.postalcode.setText(admins.get(position).getPostalcode());
        holder.totalprice.setText(String.valueOf(admins.get(position).getTotalprice()));

        holder.tracknumber.setText(admins.get(position).getTracknumber());
        holder.status.setText(admins.get(position).getStatus());
        holder.region.setText(admins.get(position).getRegion());
        holder.schedule.setText(admins.get(position).getSchedule());
        holder.paymentmethod.setText(admins.get(position).getPaymentmethod());
        holder.totalprice.setText("Total Payment: ₱"+admins.get(position).getTotalprice());
        holder.time.setText(admins.get(position).getTime());

        holder.schedule.setVisibility(View.GONE);
        String tNumber = admins.get(holder.getAdapterPosition()).getTracknumber();

        if (tNumber.isEmpty() && admins.get(position).isSetenablebutton()){
            holder.adminCODStatus.setText("NO TRACKING NUMBER AND BUTTON IS NOT DISABLED");
        }
        else if(tNumber.isEmpty() && !admins.get(position).isSetenablebutton()){
            holder.adminCODStatus.setText("NO TRACKING NUMBER AND BUTTON IS DISABLED");
            holder.tracknumber.setText("Tracking Number: Your tracking number will be here soon!");
        }
        else if(!tNumber.isEmpty() && admins.get(position).isSetenablebutton()){
            holder.adminCODStatus.setText("HAS A TRACKING NUMBER AND BUTTON IS NOT DISABLED");
            holder.tracknumber.setText("Tracking Number: " + admins.get(holder.getAdapterPosition()).getTracknumber());
        }
        else {
            holder.adminCODStatus.setText("HAS A TRACKING NUMBER AND BUTTON IS DISABLED");
            // Not setting the admin setenablebutton to FALSE because,
            // if something or anything happened bad to the shipment/parcel
            // I'll cancel it.
            //holder.cancelButton.setEnabled(holder.btnEnable);
            holder.tracknumber.setText("Tracking Number: " + admins.get(holder.getAdapterPosition()).getTracknumber());
            holder.status.setText("Your item(s) are now shipped!\nYou can not cancel them anymore.");
        }

        //when I want to cancel the users order
        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelDialog(holder.getAdapterPosition(),adminProductAdapter,holder.adminProducts_list,
                        admins.get(holder.getAdapterPosition()).getUid(), admins.get(holder.getAdapterPosition()).getUserName());
            }
        });

        //If the meet up is done
        holder.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trackingNumber = admins.get(holder.getAdapterPosition()).getTracknumber();
                boolean buttonIsEnabled = admins.get(holder.getAdapterPosition()).isSetenablebutton();
                if(trackingNumber.isEmpty() && buttonIsEnabled){
                    showThereIsNoTrackingNumberAndButtonIsTrue();
                }
                else if(!trackingNumber.isEmpty() && buttonIsEnabled){
                    showButtonIsStillTrue();
                }
                else if(trackingNumber.isEmpty()){
                    showThereIsNoTrackingNumber();
                }
                else{
                    showDoneDialog(holder.getAdapterPosition(), admins.get(holder.getAdapterPosition()).getUid(),
                            admins.get(holder.getAdapterPosition()).getUserName(), admins.get(holder.getAdapterPosition()).getShipmentNumber());
                }
            }
        });

        holder.setTrackNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTrackNumberDialog(holder.getAdapterPosition(), admins.get(holder.getAdapterPosition()).getUid(),
                        admins.get(holder.getAdapterPosition()).getUserName(), admins.get(holder.getAdapterPosition()).getShipmentNumber());
            }
        });

        holder.setBtnToFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showButtonToFalseDialog(holder.getAdapterPosition(), admins.get(holder.getAdapterPosition()).getUid(),
                        admins.get(holder.getAdapterPosition()).getUserName(), admins.get(holder.getAdapterPosition()).getShipmentNumber());
            }
        });

        holder.changePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangePrice(holder.getAdapterPosition(), admins.get(holder.getAdapterPosition()).getUid(),
                        admins.get(holder.getAdapterPosition()).getUserName(), admins.get(holder.getAdapterPosition()).getShipmentNumber());
            }
        });
    }

    public void showChangePrice(int position, String uid, String userName, String shipmentNumber){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.change_price_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        Button userPressNo = dialog.findViewById(R.id.user_press_no);
        DatabaseReference adminPriceReference = rootRef.child("CODShipments");
        DatabaseReference priceReference = rootRef.child("Users").child(uid).child(userName+"Shipments");
        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText getPriceInput = dialog.findViewById(R.id.amount_input);
                String priceStr = getPriceInput.getText().toString().trim();
                if(priceStr.isEmpty()){
                    getPriceInput.setError("You have to put the amount!");
                    getPriceInput.requestFocus();
                    return;
                }
                int price = Integer.parseInt(priceStr);
                adminPriceReference.child(shipmentNumber).child("totalprice").setValue(price);
                priceReference.child(shipmentNumber).child("totalprice").setValue(price);
                showChangingPriceIsDone();
                dialog.dismiss();
                ((AdminCODActivity)context).refreshAdminActivity();
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

    public void showCancelDialog(int position, AdminProductAdapter adminProductAdapter, List<AdminProduct> adminProducts, String uid, String userName){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.cancel_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        Button userPressNo = dialog.findViewById(R.id.user_press_no);
        DatabaseReference cancelReference = rootRef.child("Users").child(uid).child(userName+"Shipments");

        // When the I want to cancel an order of my user
        // So the product's stock will be added depends on the user's quantity on the item
        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // so add the product stock first based on the item quantity
                for (int i = 0; i < adminProducts.size(); i++){
                    String getItemName = adminProducts.get(i).getName().toLowerCase().replaceAll(" ", "");
                    Log.i("SHIPPINGNAME", getItemName);
                    int getItemQuantity = adminProducts.get(i).getQuantity();
                    Log.i("SHIPPINGCOUNT", String.valueOf(getItemQuantity));


                    Log.i("SHIPPINGSIZE", String.valueOf(adminProducts.size()));
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
                }

                //Remove the user's meetup item and also remove it from my admin monitoring activity
                cancelReference.child(admins.get(position).getShipmentNumber()).removeValue();
                CODReference.child(admins.get(position).getShipmentNumber()).removeValue();
                admins.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
                ((AdminCODActivity)context).refreshAdminActivity();

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

    public void showDoneDialog(int position, String uid, String userName, String shipmentNumber){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.admin_done_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        Button userPressNo = dialog.findViewById(R.id.user_press_no);
        DatabaseReference doneItemsReference = rootRef.child("Users").child(uid).child(userName+"DoneShipments");
        List<AdminProduct> products = new ArrayList<>(admins.get(position).getProducts().values());
        DatabaseReference cancelReference = rootRef.child("Users").child(uid).child(userName+"Shipments");

        String schedule = admins.get(position).getSchedule();

        String region = admins.get(position).getRegion();
        String trackNumber = admins.get(position).getTracknumber();
        String status = admins.get(position).getStatus();

        String fullName = admins.get(position).getFullname();
        String phoneNumber = admins.get(position).getPhonenumber();
        String fullAddress = admins.get(position).getFulladdress();
        String provinceAddress = admins.get(position).getProvince();
        String cityAddress = admins.get(position).getCity();
        String barangayAddress = admins.get(position).getBarangay();
        String postalCode = admins.get(position).getPostalcode();
        String emailAddress = admins.get(position).getEmailaddress();
        String facebook = admins.get(position).getFacebook();
        String paymentMethod = admins.get(position).getPaymentmethod();
        int totalPrice = admins.get(position).getTotalprice();
        String shipmentNum = admins.get(position).getShipmentNumber();

        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = "";
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                for(int i = 1; i <= 7; i++){
                    Calendar calendar = new GregorianCalendar();
                    calendar.add(Calendar.DATE, i);
                    date = df.format(calendar.getTime());
                    Log.i("DAY UNTIL 7", date);
                }
                for(int i = 0; i < products.size(); i++){
                    String productsName = products.get(i).getName().toLowerCase().toString().replaceAll(" ", "");
                    doneItemsReference.child(shipmentNum).child("products").child(userName+productsName).child("image").setValue(products.get(i).getImage());
                    doneItemsReference.child(shipmentNum).child("products").child(userName+productsName).child("name").setValue(products.get(i).getName());
                    doneItemsReference.child(shipmentNum).child("products").child(userName+productsName).child("price").setValue(products.get(i).getPrice());
                    doneItemsReference.child(shipmentNum).child("products").child(userName+productsName).child("quantity").setValue(products.get(i).getQuantity());
                }

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
                doneItemsReference.child(shipmentNum).child("delivered").setValue(false);
                doneItemsReference.child(shipmentNum).child("refunded").setValue(false);
                doneItemsReference.child(shipmentNum).child("shipmentNumber").setValue(shipmentNum);
                doneItemsReference.child(shipmentNum).child("date").setValue(date);

                CODReference.child(admins.get(position).getShipmentNumber()).removeValue();
                cancelReference.child(admins.get(position).getShipmentNumber()).removeValue();
                admins.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
                ((AdminCODActivity)context).refreshAdminActivity();
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

    public void showTrackNumberDialog(int position, String uid, String userName, String shipmentNumber){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.set_track_number_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        Button userPressNo = dialog.findViewById(R.id.user_press_no);
        DatabaseReference adminTrackingNumberReference = rootRef.child("CODShipments");
        DatabaseReference trackingNumberReference = rootRef.child("Users").child(uid).child(userName+"Shipments");

        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText getTrackingNumberInput = dialog.findViewById(R.id.tracking_number_input);
                String newTrackNumber = getTrackingNumberInput.getText().toString().trim();
                Log.i("THE NEW TRACK NUMBER", newTrackNumber);
                adminTrackingNumberReference.child(shipmentNumber).child("tracknumber").setValue(newTrackNumber);
                trackingNumberReference.child(shipmentNumber).child("tracknumber").setValue(newTrackNumber);
                showTrackingNumberIsDone();
                dialog.dismiss();
                ((AdminCODActivity)context).refreshAdminActivity();
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

    public void showButtonToFalseDialog(int position, String uid, String userName, String shipmentNumber){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.set_to_false_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        Button userPressNo = dialog.findViewById(R.id.user_press_no);
        DatabaseReference adminTrackingNumberReference = rootRef.child("CODShipments");
        DatabaseReference setButtonFalseReference = rootRef.child("Users").child(uid).child(userName+"Shipments");

        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonFalseReference.child(shipmentNumber).child("setenablebutton").setValue(false);
                adminTrackingNumberReference.child(shipmentNumber).child("setenablebutton").setValue(false);
                showSetButtonToFalseIsDone();
                dialog.dismiss();
                ((AdminCODActivity)context).refreshAdminActivity();
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

    public void showTrackingNumberIsDone(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("Successfully changed!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showThereIsNoTrackingNumberAndButtonIsTrue(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("No tracking number and button is true!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showThereIsNoTrackingNumber(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("No tracking number yet!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showButtonIsStillTrue(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("Have not set the button to false!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showChangingPriceIsDone(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("Successfully changed the price!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    public void showSetButtonToFalseIsDone(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("Set it to false!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public int getItemCount() {
        return admins.size();
    }
}
