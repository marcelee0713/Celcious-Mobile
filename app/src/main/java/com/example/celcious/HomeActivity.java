package com.example.celcious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    //for logging out and how to buy popup
    private Dialog dialog;
    private TextView howTo;

    //for navigation bar
    private TextView nav_cart;
    private TextView nav_shipments;
    private TextView nav_about;
    private TextView nav_help;
    private RelativeLayout nav_admin_container;
    private RelativeLayout nav_messages_container;
    private TextView nav_admin;
    private TextView nav_messages;
    private TextView nav_reviews;
    private TextView logOut;

    // for getting the user
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    //for the real time database
    // for storing, getting, and etc..
    public static String uid;
    public static String userName;
    public static String emailAddress;

    //for social links
    private TextView facebook;
    private TextView twitter;
    private TextView instagram;
    private TextView linkedin;

    //RecView
    RecyclerView homeRecView;
    ProductRecViewAdapter productRecViewAdapter;
    ArrayList<Products> products_list;
    DatabaseReference databaseReference;

    //For refreshing
    SwipeRefreshLayout refreshLayout;

    //For progressbar
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // how to buy dialog
        howTo = findViewById(R.id.information);

        //for social links
        facebook = findViewById(R.id.social_facebook);
        twitter = findViewById(R.id.social_twitter);
        instagram = findViewById(R.id.social_instagram);
        linkedin = findViewById(R.id.social_linkedin);

        //way to get the user's data remember the username?
        // To get the UID, and userName as well
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        uid = user.getUid();

        // for nav
        nav_shipments = findViewById(R.id.nav_shipments);
        nav_cart = findViewById(R.id.nav_cart);
        nav_about = findViewById(R.id.nav_about);
        nav_help = findViewById(R.id.nav_help);
        nav_reviews = findViewById(R.id.nav_reviews);
        logOut = findViewById(R.id.sign_out);

        // for the Dev
        nav_admin = findViewById(R.id.nav_admin);
        nav_admin_container = findViewById(R.id.nav_admin_container);
        nav_messages = findViewById(R.id.nav_messages);
        nav_messages_container = findViewById(R.id.nav_messages_container);
        if(uid.equals("CtwgPRWYDZf9AowTWmTxGoNeYDD2")){
            nav_admin.setVisibility(View.VISIBLE);
            nav_messages.setVisibility(View.VISIBLE);
        }
        else{
            nav_admin.setVisibility(View.GONE);
            nav_admin_container.setVisibility(View.GONE);
            nav_messages.setVisibility(View.GONE);
            nav_messages_container.setVisibility(View.GONE);
        }

        //for progressbar
        progressBar = findViewById(R.id.home_progress_bar);

        // just to display and greeting.
        final TextView usernametext = findViewById(R.id.user_name);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String userNameInDatabase = userProfile.userName;
                    String userEmailAddress = userProfile.emailAddress;
                    usernametext.setText(userNameInDatabase);

                    userName = userNameInDatabase;
                    emailAddress = userEmailAddress;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //RecViewSetup
        homeRecView = findViewById(R.id.home_recycler_view);
        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Products");
        homeRecView.setHasFixedSize(true);
        homeRecView.setLayoutManager(new GridLayoutManager(this, 2));
        products_list = new ArrayList<>();

        productRecViewAdapter = new ProductRecViewAdapter(this);
        productRecViewAdapter.setProducts(products_list);
        homeRecView.setAdapter(productRecViewAdapter);

        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Products products = snapshot1.getValue(Products.class);
                    products_list.add(products);
                }
                productRecViewAdapter.notifyDataSetChanged();

                if(productRecViewAdapter.getItemCount() > 0){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        howTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHowToBuy();
            }
        });

        nav_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCart();
            }
        });
        nav_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAbout();
            }
        });
        nav_shipments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShipments();
            }
        });
        nav_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHelp();
            }
        });
        nav_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReviews();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });

        //For swipe refresh
        refreshLayout = findViewById(R.id.refresh_home);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                refreshHomeActivity();
            }
        });

        //for the admin
        nav_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAdmin();
            }
        });
        nav_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAdminMessages();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.facebook.com/people/Celcious/100085712929518/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://twitter.com/Marcelee13");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoInstagramCurrently();
            }
        });
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.linkedin.com/in/marcel-magbual-48570a218/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    public void refreshHomeActivity(){
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public void showNoInstagramCurrently(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("There is no Instagram currently!");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void openHowToBuy(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.how_to_buy_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        TextView userPressYes = (TextView) dialog.findViewById(R.id.close_how_to);
        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void goToCart(){
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void goToHelp(){
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }

    public void goToAbout(){
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    public void goToShipments(){
        Intent intent = new Intent(this, ShipmentsActivity.class);
        startActivity(intent);
    }

    public void goToReviews(){
        Intent intent = new Intent(this, UserReviewsActivity.class);
        startActivity(intent);
    }

    public void goToAdmin(){
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    public void goToAdminMessages(){
        Intent intent = new Intent(this, AdminMessagesActivity.class);
        startActivity(intent);
    }

    public void showPopup(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.logout_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        Button userPressYes = dialog.findViewById(R.id.user_press_yes);
        Button userPressNo = dialog.findViewById(R.id.user_press_no);
        userPressYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                goToLogOut();
            }
        });
        userPressNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void goToLogOut(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // IN ORDER TO FINISH ALL the ACTIVITIES
        finishAffinity();
        FirebaseAuth.getInstance().signOut();
    }
}