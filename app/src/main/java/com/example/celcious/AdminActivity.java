package com.example.celcious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    //for navigation bar
    private TextView nav_home;
    private TextView nav_cart;
    private TextView nav_about;
    private TextView nav_shipments;
    private TextView nav_help;
    private TextView nav_reviews;
    private TextView nav_messages;

    private TextView noData;

    private Button codButton;
    RecyclerView meetUpRecyclerView;
    AdminMeetUpRecViewAdapter meetUpActivityAdapter;
    ArrayList<Admin> admins_list;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference = rootRef.child("MeetUpShipments");

    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // for nav
        nav_home = findViewById(R.id.nav_home);
        nav_cart = findViewById(R.id.nav_cart);
        nav_about = findViewById(R.id.nav_about);
        nav_shipments = findViewById(R.id.nav_shipments);
        nav_help = findViewById(R.id.nav_help);
        nav_reviews = findViewById(R.id.nav_reviews);

        nav_messages = findViewById(R.id.nav_messages);

        noData = findViewById(R.id.no_item_in_meetup_text);
        noData.setVisibility(View.INVISIBLE);

        codButton = findViewById(R.id.cod_activity);

        meetUpRecyclerView = findViewById(R.id.meet_up_recycler_view);
        meetUpRecyclerView.setHasFixedSize(true);
        admins_list = new ArrayList<>();

        meetUpActivityAdapter = new AdminMeetUpRecViewAdapter(this);
        meetUpActivityAdapter.setAdmins(admins_list);
        meetUpRecyclerView.setAdapter(meetUpActivityAdapter);
        meetUpRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Admin admins = snapshot1.getValue(Admin.class);
                    admins_list.add(admins);
                }

                if(meetUpActivityAdapter.getItemCount() == 0){
                    noData.setVisibility(View.VISIBLE);
                }
                meetUpActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //For swipe refresh
        refreshLayout = findViewById(R.id.refresh_admin_meetup);
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

        codButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCOD();
            }
        });

        //for the admin
        nav_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAdminMessages();
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

    public void goToCOD(){
        Intent intent = new Intent(this, AdminCODActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToAdminMessages(){
        Intent intent = new Intent(this, AdminMessagesActivity.class);
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