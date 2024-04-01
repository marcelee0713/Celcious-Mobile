package com.example.celcious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminMessagesActivity extends AppCompatActivity {
    //for navigation bar
    private TextView nav_home;
    private TextView nav_cart;
    private TextView nav_about;
    private TextView nav_shipments;
    private TextView nav_help;
    private TextView nav_admin;
    private TextView nav_reviews;

    private TextView noData;

    RecyclerView qcRecyclerView;
    AdminMessagesRecViewAdapter qcActivityAdapter;
    ArrayList<AdminMessages> admins_messages_list;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference = rootRef.child("Questions");

    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_messages);

        nav_home = findViewById(R.id.nav_home);
        nav_cart = findViewById(R.id.nav_cart);
        nav_about = findViewById(R.id.nav_about);
        nav_shipments = findViewById(R.id.nav_shipments);
        nav_help = findViewById(R.id.nav_help);
        nav_reviews = findViewById(R.id.nav_reviews);

        nav_admin = findViewById(R.id.nav_admin);

        noData = findViewById(R.id.no_item_in_qc_text);
        noData.setVisibility(View.INVISIBLE);

        qcRecyclerView = findViewById(R.id.qc_recycler_view);
        qcRecyclerView.setHasFixedSize(true);
        admins_messages_list = new ArrayList<>();

        qcActivityAdapter = new AdminMessagesRecViewAdapter(this);
        qcActivityAdapter.setAdminMessages(admins_messages_list);
        qcRecyclerView.setAdapter(qcActivityAdapter);
        qcRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    AdminMessages adminsMessages = snapshot1.getValue(AdminMessages.class);
                    admins_messages_list.add(adminsMessages);
                }

                if(qcActivityAdapter.getItemCount() == 0){
                    noData.setVisibility(View.VISIBLE);
                }
                qcActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For swipe refresh
        refreshLayout = findViewById(R.id.refresh_admin_messages);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                refreshAdminActivity();
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
        nav_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReviews();
            }
        });

        //for the admin
        nav_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAdmin();
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

    public void goToReviews(){
        Intent intent = new Intent(this, UserReviewsActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToAdmin(){
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
        finish();
    }

    public void refreshAdminActivity(){
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}