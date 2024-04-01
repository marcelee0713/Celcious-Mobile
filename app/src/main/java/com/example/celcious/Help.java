package com.example.celcious;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Help extends AppCompatActivity {

    // for submitting the message and putting it on the database
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference = rootRef.child("Users");
    DatabaseReference adminDatabaseReference = rootRef.child("Questions");

    private Dialog dialog;

    private String uid = HomeActivity.uid;
    private String userName = HomeActivity.userName;

    // for nav
    private TextView nav_home;
    private TextView nav_cart;
    private TextView nav_shipments;
    private TextView nav_about;
    private TextView nav_reviews;

    //for the report form
    private EditText qcName, qcEmail, qcMessage;
    private Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // for nav
        nav_home = findViewById(R.id.nav_home);
        nav_cart = findViewById(R.id.nav_cart);
        nav_about = findViewById(R.id.nav_about);
        nav_shipments = findViewById(R.id.nav_shipments);
        nav_reviews = findViewById(R.id.nav_reviews);

        qcName = findViewById(R.id.q_or_c_fullName);
        qcEmail = findViewById(R.id.q_or_c_email);
        qcMessage = findViewById(R.id.q_or_c_message);
        submitButton = findViewById(R.id.q_or_c_submit_btn);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitQuestion();
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
        nav_shipments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShipments();
            }
        });
        nav_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAbout();
            }
        });
        nav_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReviews();
            }
        });
    }

    public void submitQuestion(){
        String fullName = qcName.getText().toString().trim();
        String email = qcEmail.getText().toString().trim();
        String message = qcMessage.getText().toString().trim();
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        String timeInput = df.format(Calendar.getInstance().getTime());

        if(fullName.isEmpty()){
            qcName.setError("Full name is required!");
            qcName.requestFocus();
            return;
        }
        if(fullName.length() < 6){
            qcName.setError("Full name should have at least 6 characters!");
            qcName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            qcEmail.setError("Email is required!");
            qcEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            qcEmail.setError("Enter a valid email!");
            qcEmail.requestFocus();
            return;
        }
        if(message.isEmpty()){
            qcMessage.setError("Your question is required!");
            qcMessage.requestFocus();
            return;
        }
        if(message.length() < 50){
            qcMessage.setError("Make your message a bit more longer, specific, and sensible.");
            qcMessage.requestFocus();
            return;
        }

        //for creating a random key
        Random rnd = new Random();
        String rndStr = "";
        int rndInt = rnd.nextInt(999);
        int rndIntForLetters = rnd.nextInt(75);

        if(rndIntForLetters <= 25){
            rndStr = "a";
        }
        else if(rndIntForLetters <= 50 && rndIntForLetters >= 25){
            rndStr = "b";
        }
        else if(rndIntForLetters <= 75 && rndIntForLetters >= 50){
            rndStr = "c";
        }
        String randomCode = rndStr + String.valueOf(rndInt);

        adminDatabaseReference.child(randomCode).child("fullName").setValue(fullName);
        adminDatabaseReference.child(randomCode).child("email").setValue(email);
        adminDatabaseReference.child(randomCode).child("message").setValue(message);
        adminDatabaseReference.child(randomCode).child("time").setValue(timeInput);
        adminDatabaseReference.child(randomCode).child("questionNumber").setValue(randomCode);

        showPopup();
    }

    public void showPopup(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.submit_successful_popup);
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
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });
        userPressNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                goToHome();
            }
        });
    }

    public void goToCart(){
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
        finish();
    }
    public void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        // Finishing the activity to avoid bugs
        finishAffinity();
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
    public void goToShipments(){
        Intent intent = new Intent(this, ShipmentsActivity.class);
        startActivity(intent);
        finish();
    }
}