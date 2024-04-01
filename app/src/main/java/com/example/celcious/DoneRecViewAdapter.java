package com.example.celcious;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class DoneRecViewAdapter extends RecyclerView.Adapter<DoneRecViewAdapter.ViewHolder> {
    private String userName = HomeActivity.userName;
    private String uid = HomeActivity.uid;
    private ArrayList<Done> done = new ArrayList<>();
    private DatabaseReference databaseRefundsReference = FirebaseDatabase.getInstance().getReference("Refunds");
    private DatabaseReference databaseDoneReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child(userName+"DoneShipments");

    private Context context;

    private Dialog dialog;

    public DoneRecViewAdapter (Context context){this.context = context;}
    public void setDone (ArrayList<Done> done){
        this.done = done;
        notifyDataSetChanged();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        private Button reviewButton;
        private Button returnRefundButton;
        private Button deliveredButton;
        private RecyclerView productsRecyclerView;
        private List<DoneProduct> doneProducts_list;

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
        private TextView dateExpirationHeader;
        private TextView dateExpiration;
        private boolean btnEnable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productsRecyclerView = itemView.findViewById(R.id.products_recyclerView);
            returnRefundButton = itemView.findViewById(R.id.return_refund_btn);
            deliveredButton = itemView.findViewById(R.id.delivered_btn);
            reviewButton = itemView.findViewById(R.id.cancel_btn);

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
            dateExpirationHeader = itemView.findViewById(R.id.text_date_expire);
            dateExpiration = itemView.findViewById(R.id.text_date);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.done_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Setting up the Nested RecView
        DoneProductAdapter productAdapter = new DoneProductAdapter(context);
        holder.doneProducts_list = new ArrayList<>(done.get(position).getProducts().values());
        holder.productsRecyclerView.setHasFixedSize(true);
        holder.productsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.productsRecyclerView.setAdapter(productAdapter);
        productAdapter.setDoneProducts(holder.doneProducts_list);
        productAdapter.notifyDataSetChanged();
        //For the shipping
        String homeNumberBuildingStreet = done.get(position).getFulladdress();
        String province = done.get(position).getProvince();
        String city = done.get(position).getCity();
        String barangay = done.get(position).getBarangay();
        holder.emailaddress.setText(done.get(position).getEmailaddress());
        holder.facebook.setText(done.get(position).getFacebook());
        holder.fulladdress.setText(homeNumberBuildingStreet + " " + barangay + " " + city + " " + province);
        holder.fullname.setText(done.get(position).getFullname());
        holder.phonenumber.setText(done.get(position).getPhonenumber());
        holder.postalcode.setText(done.get(position).getPostalcode());
        holder.totalprice.setText(String.valueOf(done.get(position).getTotalprice()));
        holder.tracknumber.setText(done.get(position).getTracknumber());
        holder.status.setText(done.get(position).getStatus());
        holder.region.setText(done.get(position).getRegion());
        holder.schedule.setText(done.get(position).getSchedule());
        holder.paymentmethod.setText(done.get(position).getPaymentmethod());

        String userSchedule = done.get(holder.getAdapterPosition()).getSchedule();

        holder.totalprice.setText("Total Payment: ₱"+done.get(position).getTotalprice());

        String selectedPaymentMethod = done.get(holder.getAdapterPosition()).getPaymentmethod();
        if(Objects.equals(selectedPaymentMethod, "COD")){
            holder.schedule.setVisibility(View.GONE);
            holder.reviewButton.setVisibility(View.GONE);
            String tNumber = done.get(holder.getAdapterPosition()).getTracknumber();
            holder.dateExpiration.setText(done.get(position).getDate());

            if(tNumber.isEmpty()){
                holder.tracknumber.setText("Tracking Number: Your tracking number will be here soon!");
            }
            else {
                holder.reviewButton.setEnabled(holder.btnEnable);
                holder.tracknumber.setText("Tracking Number: " + done.get(holder.getAdapterPosition()).getTracknumber());
                if(!done.get(position).isDelivered()){
                    holder.status.setText("Your item(s) are now delivered!\n\nIf there is a problem in your parcel please press RETURN/FUND. In order to contact immediately in my Facebook Page!");
                }
                else if(done.get(position).isDelivered() && done.get(position).isRefunded()){
                    holder.status.setText("Our transaction may or may not be done. But you can always write a review.\n\n Even though we didn't meet your expectations and we are very sorry for the inconvenience. Feel free to send your message and criticisms!");
                    holder.reviewButton.setVisibility(View.VISIBLE);
                    holder.returnRefundButton.setVisibility(View.GONE);
                    holder.deliveredButton.setVisibility(View.GONE);
                    holder.dateExpirationHeader.setVisibility(View.GONE);
                    holder.dateExpiration.setVisibility(View.GONE);
                }
                else{
                    holder.status.setText("Your item(s) are now delivered!\n\nDon't forget to write a review!");
                    holder.reviewButton.setVisibility(View.VISIBLE);
                    holder.returnRefundButton.setVisibility(View.GONE);
                    holder.deliveredButton.setVisibility(View.GONE);
                    holder.dateExpirationHeader.setVisibility(View.GONE);
                    holder.dateExpiration.setVisibility(View.GONE);
                }
                //If the user is not satisfied but made a review.
                if(!done.get(position).isDone() && done.get(position).isRefunded()){
                    holder.status.setText("Our transaction may or may not be done. But you can always write a review.\n\n Even though we didn't meet your expectations and we are very sorry for the inconvenience. Feel free to send your message and criticisms!\n\nCheck your review in the star icon!");
                }
                else if(!done.get(position).isDone()){
                    holder.status.setText("Your item(s) are now delivered!\n\nCheck your review in the star icon!");
                }
            }
        }
        else if(Objects.equals(selectedPaymentMethod, "Meet up")){
            holder.tracknumber.setVisibility(View.GONE);
            holder.status.setVisibility(View.GONE);
            holder.region.setVisibility(View.GONE);
            holder.returnRefundButton.setVisibility(View.GONE);
            holder.deliveredButton.setVisibility(View.GONE);
            holder.dateExpirationHeader.setVisibility(View.GONE);
            holder.dateExpiration.setVisibility(View.GONE);

            if(userSchedule.equals("Wednesday")) {
                holder.schedule.setText("Wednesday\n11:00am - 12:00pm\nInside McDonald's, PHINMA University of Pangasinan, Dagupan City, Pangasinan" +
                        "\n\nMeet up is done!\nDon't forget to write a review!");
                if(!done.get(position).isDone()){
                    holder.schedule.setText("Wednesday\n11:00am - 12:00pm\nInside McDonald's, PHINMA University of Pangasinan, Dagupan City, Pangasinan" +
                            "\n\nMeet up is done!\nCheck your review in the star icon!");
                }
            }
            else if(userSchedule.equals("Thursday")){
                holder.schedule.setText("Thursday\n11:00am - 12:00pm\nInside McDonald's, PHINMA University of Pangasinan, Dagupan City, Pangasinan" +
                        "\n\nMeet up is done!\nDon't forget to write a review!");
                if(!done.get(position).isDone()){
                    holder.schedule.setText("Thursday\n11:00am - 12:00pm\nInside McDonald's, PHINMA University of Pangasinan, Dagupan City, Pangasinan" +
                            "\n\nMeet up is done!\nCheck your review in the star icon!");
                }
            }
            else if(userSchedule.equals("Saturday")){
                holder.schedule.setText("Saturday\n1:00pm - 2:00pm\nFood court on 2nd Floor,\n Robinsons Place Pangasinan, McArthur Highway, Calasiao, Pangasinan" +
                        "\n\nMeet up is done!\nDon't forget to write a review!");
                if(!done.get(position).isDone()){
                    holder.schedule.setText("Saturday\n1:00pm - 2:00pm\nFood court on 2nd Floor,\n Robinsons Place Pangasinan, McArthur Highway, Calasiao, Pangasinan" +
                            "\n\nMeet up is done!\nCheck your review in the star icon!");
                }
            }
            else if(userSchedule.equals("Sunday")){
                holder.schedule.setText("Sunday\n4:00pm - 5:00pm\nFood court in Public Plaza,\n Poblacion Norte, Public Plaza, Santa Barbara, Pangasinan" +
                        "\n\nMeet up is done!\nDon't forget to write a review!");
                if(!done.get(position).isDone()){
                    holder.schedule.setText("Sunday\n4:00pm - 5:00pm\nFood court in Public Plaza,\n Poblacion Norte, Public Plaza, Santa Barbara, Pangasinan" +
                            "\n\nMeet up is done!\nCheck your review in the star icon!");
                }
            }
        }

        // Inorder to get the database boolean for the button
        // key name is setenablebutton
        holder.btnEnable = done.get(holder.getAdapterPosition()).isDone();
        holder.reviewButton.setEnabled(holder.btnEnable);
        holder.reviewButton.setText("RATE");
        holder.reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReviewDialog(done.get(holder.getAdapterPosition()).getShipmentNumber(), holder.doneProducts_list);
            }
        });


        if(!done.get(position).isDelivered()){
            if(Objects.equals(selectedPaymentMethod, "COD")){
                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date strDate = sdf.parse(done.get(position).getDate());
                    if(new Date().after(strDate)){
                        databaseDoneReference.child(done.get(position).getShipmentNumber()).child("delivered").setValue(true);
                        ((DoneActivity)context).refreshDoneActivity();
                    }
                }
                catch (ParseException e){
                    e.getErrorOffset();
                }
            }
        }


        holder.deliveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeliveredDialog(done.get(holder.getAdapterPosition()).getShipmentNumber());
            }
        });

        holder.returnRefundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRefundReturnDialog(done.get(holder.getAdapterPosition()).getShipmentNumber());
            }
        });
    }

    SharedPreferences sp;

    public void showReviewDialog(String shipmentNumber, List<DoneProduct> list){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.go_to_review_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        sp = context.getSharedPreferences("userRatingInformation", Context.MODE_PRIVATE);
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                String boughtItems = "";
                for(int i = 0; i < list.size(); i++){
                    String itemName = list.get(i).getName();
                    boughtItems = boughtItems.concat(itemName+", ");
                }
                if(boughtItems.endsWith(", ")){
                    boughtItems = boughtItems.substring(0,boughtItems.length() - 2);
                }
                editor.putString("boughtItems", boughtItems);
                editor.putString("shipmentNumber",shipmentNumber);
                editor.apply();

                Intent intent = new Intent(context, ReviewActivity.class);
                context.startActivity(intent);
                ((DoneActivity)context).finish();
                dialog.dismiss();
            }
        });
    }

    public void showRefundReturnDialog(String shipmentNumber){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.go_to_review_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView description = dialog.findViewById(R.id.description);
        description.setText("Once you press yes, you will be redirected to Celcious's Facebook Page. We will talk about our transaction, since we have an issue about it.");
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        userPressYes.setText("YES");
        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseDoneReference.child(shipmentNumber).child("delivered").setValue(true);
                databaseDoneReference.child(shipmentNumber).child("refunded").setValue(true);
                ((DoneActivity)context).refreshDoneActivity();
                dialog.dismiss();
                Intent facebookIntent = getOpenFacebookIntent(context);
                context.startActivity(facebookIntent);
            }
        });
    }

    public void showDeliveredDialog(String shipmentNumber){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.go_to_review_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView description = dialog.findViewById(R.id.description);
        description.setText("Once you press yes, you won't be able to return/refund this item and can not be undone.");
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        userPressYes.setText("YES");
        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseDoneReference.child(shipmentNumber).child("delivered").setValue(true);
                showYouCanNowRate();
                ((DoneActivity)context).refreshDoneActivity();
                dialog.dismiss();
            }
        });
    }

    public void showYouCanNowRate(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf005");
        toastText.setText("You can now rate your parcel!");

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static Intent getOpenFacebookIntent(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/100085712929518"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/100085712929518"));
        }
    }


    @Override
    public int getItemCount() {
        return done.size();
    }
}
