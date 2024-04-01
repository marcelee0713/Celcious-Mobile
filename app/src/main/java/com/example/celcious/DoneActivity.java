package com.example.celcious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
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

public class DoneActivity extends AppCompatActivity {
    //for navigation bar
    private TextView nav_home;
    private TextView nav_cart;
    private TextView nav_about;
    private TextView nav_help;
    private TextView nav_reviews;

    private TextView noData;

    private Button items;
    private TextView trackLink;

    String userName = HomeActivity.userName;
    String uid = HomeActivity.uid;
    RecyclerView doneRecyclerView;
    DoneRecViewAdapter doneActivityAdapter;
    ArrayList<Done> done_list;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference = rootRef.child("Users").child(uid).child(userName+"DoneShipments");

    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        // for nav
        nav_home = findViewById(R.id.nav_home);
        nav_cart = findViewById(R.id.nav_cart);
        nav_about = findViewById(R.id.nav_about);
        nav_help = findViewById(R.id.nav_help);
        nav_reviews = findViewById(R.id.nav_reviews);

        noData = findViewById(R.id.no_item_in_done_text);
        noData.setVisibility(View.INVISIBLE);

        items = findViewById(R.id.items_shipments_activity);
        trackLink = findViewById(R.id.tracking_link);

        doneRecyclerView = findViewById(R.id.done_recycler_view);
        doneRecyclerView.setHasFixedSize(true);
        done_list = new ArrayList<>();

        doneActivityAdapter = new DoneRecViewAdapter(this);
        doneActivityAdapter.setDone(done_list);
        doneRecyclerView.setAdapter(doneActivityAdapter);
        doneRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //For swipe refresh
        refreshLayout = findViewById(R.id.refresh_done);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                refreshDoneActivity();
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Done done = snapshot1.getValue(Done.class);
                    done_list.add(done);
                }

                if(doneActivityAdapter.getItemCount() == 0){
                    noData.setVisibility(View.VISIBLE);
                }
                doneActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        trackLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.lbcexpress.com/track/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //For navigation
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
        items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 goToItems();
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

    public void goToReviews(){
        Intent intent = new Intent(this, UserReviewsActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToRate(){
        Intent intent = new Intent(this, ReviewActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToItems(){
        Intent intent = new Intent(this, ShipmentsActivity.class);
        startActivity(intent);
        finish();
    }
    public void refreshDoneActivity(){
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}