package com.example.celcious;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class About extends AppCompatActivity {
    //for social links
    private TextView facebook;
    private TextView twitter;
    private TextView instagram;
    private TextView linkedin;

    private TextView nav_home;
    private TextView nav_cart;
    private TextView nav_shipments;
    private TextView nav_help;
    private TextView nav_reviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //for social links
        facebook = findViewById(R.id.social_facebook);
        twitter = findViewById(R.id.social_twitter);
        instagram = findViewById(R.id.social_instagram);
        linkedin = findViewById(R.id.social_linkedin);

        // for nav
        nav_home = findViewById(R.id.nav_home);
        nav_cart = findViewById(R.id.nav_cart);
        nav_help = findViewById(R.id.nav_help);
        nav_shipments = findViewById(R.id.nav_shipments);
        nav_reviews = findViewById(R.id.nav_reviews);

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
}