package com.example.celcious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    //for navigation bar
    private TextView nav_home;
    private TextView nav_shipments;
    private TextView nav_about;
    private TextView nav_help;
    private TextView nav_reviews;

    private TextView noData;

    RecyclerView myCartRecView;
    CartRecViewAdapter myCartAdapter;
    ArrayList<Cart> carts_list;
    DatabaseReference cartDatabaseReference;

    private Button checkedAllButton;
    private Button unCheckedAllButton;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // for nav
        nav_home = findViewById(R.id.nav_home);
        nav_shipments = findViewById(R.id.nav_shipments);
        nav_about = findViewById(R.id.nav_about);
        nav_help = findViewById(R.id.nav_help);
        nav_reviews = findViewById(R.id.nav_reviews);

        noData = findViewById(R.id.no_item_in_cart_text);
        noData.setVisibility(View.INVISIBLE);

        checkedAllButton = findViewById(R.id.cart_add_all_button);
        unCheckedAllButton = findViewById(R.id.cart_remove_all_button);
        checkoutButton = findViewById(R.id.cart_checkout_button);

        myCartRecView = findViewById(R.id.cart_recycler_view);
        cartDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(HomeActivity.uid)
                .child(HomeActivity.userName+"Cart");
        myCartRecView.setHasFixedSize(true);
        carts_list = new ArrayList<>();

        myCartAdapter = new CartRecViewAdapter(this);
        myCartAdapter.setCarts(carts_list);
        myCartRecView.setAdapter(myCartAdapter);

        cartDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Cart carts = snapshot1.getValue(Cart.class);
                    carts_list.add(carts);
                }
                myCartAdapter.notifyDataSetChanged();

                if(myCartAdapter.getItemCount() == 0){
                    Log.i("CARTITEMCOUNT" , String.valueOf(myCartAdapter.getItemCount()));
                    noData.setVisibility(View.VISIBLE);
                }
                else if (myCartAdapter.getItemCount() == 1){
                    Log.i("CARTITEMCOUNT" , String.valueOf(myCartAdapter.getItemCount()));
                    myCartRecView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                }
                else{
                    Log.i("CARTITEMCOUNT" , String.valueOf(myCartAdapter.getItemCount()));
                    myCartRecView.setLayoutManager(new GridLayoutManager(CartActivity.this, 2));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // When the user wants to check all of the checkboxes of an item
        checkedAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCartAdapter.selectAll();
            }
        });
        // When the user doesn't wants to check all of the checkboxes of an item
        unCheckedAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCartAdapter.unSelectAll();
            }
        });
        // Will go to the CheckoutPage
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCheckOut();
            }
        });
        //For navigation
        nav_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();
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
    }

    public void refreshActivity() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public void goToCheckOut(){
        Intent intent = new Intent(this ,CheckoutActivity.class);
        startActivity(intent);
        // Finishing the activity to avoid bugs
        finish();
    }

    public void goToHelp(){
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
        finish();
    }

    public void goToAbout(){
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
        finish();
    }

    public void goToShipments(){
        Intent intent = new Intent(this, ShipmentsActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToReviews(){
        Intent intent = new Intent(this, UserReviewsActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        // Finishing the activity to avoid bugs
        finishAffinity();
    }
}