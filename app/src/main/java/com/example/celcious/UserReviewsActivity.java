package com.example.celcious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UserReviewsActivity extends AppCompatActivity {
    //for navigation bar
    private TextView nav_home;
    private TextView nav_cart;
    private TextView nav_about;
    private TextView nav_shipments;
    private TextView nav_help;

    private TextView noData;

    RecyclerView userReviewsRecyclerView;
    UserReviewsRecViewAdapter userReviewActivityAdapter;
    ArrayList<Upload> uploads_list;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference = rootRef.child("Uploads");
    LinearLayoutManager manager;

    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_reviews);

        nav_home = findViewById(R.id.nav_home);
        nav_cart = findViewById(R.id.nav_cart);
        nav_about = findViewById(R.id.nav_about);
        nav_shipments = findViewById(R.id.nav_shipments);
        nav_help = findViewById(R.id.nav_help);

        noData = findViewById(R.id.no_item_in_user_reviews_text);
        noData.setVisibility(View.INVISIBLE);
        manager = new LinearLayoutManager(this);

        userReviewsRecyclerView = findViewById(R.id.user_reviews_recycler_view);
        userReviewsRecyclerView.setHasFixedSize(true);
        uploads_list = new ArrayList<>();

        userReviewActivityAdapter = new UserReviewsRecViewAdapter(this, manager);
        userReviewActivityAdapter.setUploads(uploads_list);
        userReviewsRecyclerView.setAdapter(userReviewActivityAdapter);
        userReviewsRecyclerView.setLayoutManager(manager);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Upload upload = snapshot1.getValue(Upload.class);
                    uploads_list.add(upload);
                }

                if(userReviewActivityAdapter.getItemCount() == 0){
                    noData.setVisibility(View.VISIBLE);
                }
                userReviewActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For swipe refresh
        refreshLayout = findViewById(R.id.refresh_user_reviews);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                refreshUserReviews();
            }
        });

        nav_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();
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
    }

    public void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        // Finishing the activity to avoid bugs
        finishAffinity();
    }

    public void goToCart(){
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
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

    public void refreshUserReviews(){
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public void focusAnItem(int position){
        userReviewsRecyclerView.scrollToPosition(position);
    }
}