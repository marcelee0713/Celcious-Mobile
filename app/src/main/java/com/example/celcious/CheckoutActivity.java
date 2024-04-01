package com.example.celcious;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Random;
import java.util.regex.Pattern;

public class CheckoutActivity extends AppCompatActivity {
    //for popup
    private Dialog dialog;
    //for navigation bar
    private TextView nav_home;
    private TextView nav_shipments;
    private TextView nav_about;
    private TextView nav_help;
    private TextView nav_reviews;

    //For the spinner of each regions
    private Spinner regionSpinner;
    private ArrayList<String> regions;
    private TextView regionText;
    private TextView importantInformation;
    ArrayAdapter<String> regionAdapter;
    int regionPrice;
    String selectedRegion;

    //For the spinner of each schedules
    private Spinner meetUpSpinner;
    private ArrayList<String> meetUpSchedules;
    private TextView meetUpText;
    ArrayAdapter<String> meetUpAdapter;
    String selectedMeetup;

    //for the forms
    EditText fullName;
    EditText phoneNumber;
    EditText address;
    EditText province;
    EditText city;
    EditText barangay;
    EditText postalCode;
    EditText email;
    EditText fbName;

    //for The total price
    private TextView totalPrice;
    int totalPriceCheckout;
    final Handler handler = new Handler(Looper.getMainLooper());

    //for the payment methods
    private RadioGroup paymentRadioGroup;
    private RadioButton cod;
    String selectedPayment;

    //for the Checkout Button
    private Button checkOutBtn;
    String randomCode;

    // For putting the items in the recyclerview
    RecyclerView myCheckoutRecView;
    CheckoutRecViewAdapter myCheckoutAdapter;
    ArrayList<Checkout> checkout_list;
    DatabaseReference checkoutDatabaseReference;

    //for putting the bought items in the admin
    DatabaseReference userBoughtItemsDataReference = FirebaseDatabase.getInstance().getReference();

    // for putting the ALL THE INFORMATION in the Checkout Page
    DatabaseReference userShippingDataReference = FirebaseDatabase.getInstance().getReference("Users");
    String userName = HomeActivity.userName;
    String uid = HomeActivity.uid;

    // for remove the user's cart, that he just bought
    DatabaseReference userCartDataReference = FirebaseDatabase.getInstance().getReference("Users");

    // for remove the user's checkout, that he also just bought
    DatabaseReference userCheckoutDataReference = FirebaseDatabase.getInstance().getReference("Users");

    // for reducing the stock based on the user's product bought quantity
    DatabaseReference productDataReference = FirebaseDatabase.getInstance().getReference("Products");
    int userProductQuantity;
    int productStock;
    int newTotalProductStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        importantInformation = findViewById(R.id.important_information);
        importantInformation.setVisibility(View.GONE);

        // for nav
        nav_home = findViewById(R.id.nav_home);
        nav_shipments = findViewById(R.id.nav_shipments);
        nav_about = findViewById(R.id.nav_about);
        nav_help = findViewById(R.id.nav_help);
        nav_reviews = findViewById(R.id.nav_reviews);

        myCheckoutRecView = findViewById(R.id.checkout_recycler_view);
        checkoutDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(uid)
                .child(userName+"Checkout");
        myCheckoutRecView.setHasFixedSize(true);
        checkout_list = new ArrayList<>();

        myCheckoutAdapter = new CheckoutRecViewAdapter(this);
        myCheckoutAdapter.setCheckouts(checkout_list);
        myCheckoutRecView.setAdapter(myCheckoutAdapter);
        myCheckoutRecView.setLayoutManager(new LinearLayoutManager(this));

        //Get the data from the database
        checkoutDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Checkout checkouts = snapshot1.getValue(Checkout.class);
                    checkout_list.add(checkouts);
                }
                myCheckoutAdapter.notifyDataSetChanged();
                // If the user pressed checkouts even
                // though he/she doesn't have any item that's selected
                if(myCheckoutAdapter.getItemCount() == 0){
                    Log.i("DATACOUNTISNIDE", String.valueOf(myCheckoutAdapter.getItemCount()));
                    whenNoItemSelected();
                    goBackToCart();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Setting up the Regions
        regionText = findViewById(R.id.regionText);
        regionSpinner = findViewById(R.id.regionSpinner);
        regions = new ArrayList<>();
        regions.add("Luzon");
        regions.add("NCR");
        regions.add("Visayas");
        regions.add("Mindanao");
        regionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, regions){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                // Set the text color of spinner item
                tv.setTextColor(getResources().getColor(R.color.accent));

                // Return the view
                return tv;
            }
        };
        regionSpinner.setAdapter(regionAdapter);

        // Setting up the Meetup
        meetUpText = findViewById(R.id.meet_upText);
        meetUpSpinner = findViewById(R.id.meet_upSpinner);
        meetUpSchedules = new ArrayList<>();
        meetUpSchedules.add("Wednesday");
        meetUpSchedules.add("Thursday");
        meetUpSchedules.add("Saturday");
        meetUpSchedules.add("Sunday");
        meetUpAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, meetUpSchedules){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                // Set the text color of spinner item
                tv.setTextColor(getResources().getColor(R.color.accent));

                // Return the view
                return tv;
            }
        };
        meetUpSpinner.setAdapter(meetUpAdapter);


        //To get the user meet up schedule
        meetUpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(meetUpSchedules.get(i) == "Wednesday"){
                    selectedMeetup = "Wednesday";
                }
                else if(meetUpSchedules.get(i) == "Thursday"){
                    selectedMeetup = "Thursday";
                }
                else if(meetUpSchedules.get(i) == "Saturday"){
                    selectedMeetup = "Saturday";
                }
                else if(meetUpSchedules.get(i) == "Sunday"){
                    selectedMeetup = "Sunday";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //for defaulting and visual bug issues
        totalPrice = findViewById(R.id.total_price);

        //Appear the Region Spinner when the COD is checked
        // or
        //Appear the Schedule Spinner when the Meet-up is also checked
        regionText.setVisibility(View.GONE);
        regionSpinner.setVisibility(View.GONE);
        meetUpText.setVisibility(View.GONE);
        meetUpSpinner.setVisibility(View.GONE);
        cod = findViewById(R.id.COD);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cod.setChecked(true);
            }
        },100);

        paymentRadioGroup = findViewById(R.id.payment_method_radio);
        paymentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.COD:
                        selectedPayment = "COD";
                        importantInformation.setVisibility(View.VISIBLE);
                        totalPrice.setVisibility(View.VISIBLE);
                        regionText.setVisibility(View.VISIBLE);
                        regionSpinner.setVisibility(View.VISIBLE);
                        meetUpText.setVisibility(View.INVISIBLE);
                        meetUpSpinner.setVisibility(View.INVISIBLE);
                        totalPrice = findViewById(R.id.total_price);
                        int totalPriceIntCOD = myCheckoutAdapter.getThePrice() + regionPrice;
                        totalPriceCheckout = totalPriceIntCOD;
                        totalPrice.setText("₱"+String.valueOf(totalPriceIntCOD).toString());

                        //Prices may change
                        //This is to get the total price and
                        // All of the product's price with the quantity and the Region are totaled;
                        // we used handler because the runtime to the database is slower than the app
                        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if(regions.get(i) == "Luzon"){
                                    selectedRegion = "Luzon";
                                    regionPrice = 175;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            totalPrice = findViewById(R.id.total_price);
                                            int totalPriceInt = myCheckoutAdapter.getThePrice() + regionPrice;
                                            totalPriceCheckout = totalPriceInt;
                                            totalPrice.setText("₱"+String.valueOf(totalPriceInt).toString());
                                        }
                                    },100);
                                }
                                else if(regions.get(i) == "NCR"){
                                    selectedRegion = "NCR";
                                    regionPrice = 165;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            totalPrice = findViewById(R.id.total_price);
                                            int totalPriceInt = myCheckoutAdapter.getThePrice() + regionPrice;
                                            totalPriceCheckout = totalPriceInt;
                                            totalPrice.setText("₱"+String.valueOf(totalPriceInt).toString());
                                        }
                                    },100);
                                }
                                else if(regions.get(i) == "Visayas"){
                                    selectedRegion = "Visayas";
                                    regionPrice = 180;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            totalPrice = findViewById(R.id.total_price);
                                            int totalPriceInt = myCheckoutAdapter.getThePrice() + regionPrice;
                                            totalPriceCheckout = totalPriceInt;
                                            totalPrice.setText("₱"+String.valueOf(totalPriceInt).toString());
                                        }
                                    },100);
                                }
                                else if(regions.get(i) == "Mindanao"){
                                    selectedRegion = "Mindanao";
                                    regionPrice = 180;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            totalPrice = findViewById(R.id.total_price);
                                            int totalPriceInt = myCheckoutAdapter.getThePrice() + regionPrice;
                                            totalPriceCheckout = totalPriceInt;
                                            totalPrice.setText("₱"+String.valueOf(totalPriceInt).toString());
                                        }
                                    },100);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case R.id.meet_up:
                        selectedPayment = "Meet up";
                        totalPrice.setVisibility(View.VISIBLE);
                        meetUpText.setVisibility(View.VISIBLE);
                        meetUpSpinner.setVisibility(View.VISIBLE);
                        regionText.setVisibility(View.INVISIBLE);
                        regionSpinner.setVisibility(View.INVISIBLE);
                        importantInformation.setVisibility(View.GONE);
                        totalPrice = findViewById(R.id.total_price);
                        int totalPriceIntMeetup = myCheckoutAdapter.getThePrice();
                        totalPriceCheckout = totalPriceIntMeetup;
                        totalPrice.setText("₱"+String.valueOf(totalPriceIntMeetup).toString());
                        break;
                    default:
                        break;
                }
            }
        });



        fullName = findViewById(R.id.full_name);
        phoneNumber = findViewById(R.id.phone_number);
        address = findViewById(R.id.full_address);
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        barangay = findViewById(R.id.barangay);
        postalCode = findViewById(R.id.postal_code);
        email = findViewById(R.id.email);
        fbName = findViewById(R.id.user_facebook);

        //Creating random numbers that will be named in
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
        randomCode = rndStr + String.valueOf(rndInt);

        // FOR CHECKOUT, also when the user didn't pressed any payment methods.
        checkOutBtn = findViewById(R.id.checkout);
        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SEND THE DATA INTO THE DATABASE
                if(selectedPayment == "COD"){
                    formValidation();
                }
                else if(selectedPayment == "Meet up"){
                    formValidation();
                }
                else{
                    userDidNotSelectPaymentMethod();
                }
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

    public void formValidation(){
        String name = fullName.getText().toString().trim();
        String number = phoneNumber.getText().toString().trim();
        String addressInput = address.getText().toString().trim();
        String provinceInput = province.getText().toString().trim();
        String cityInput = city.getText().toString().trim();
        String barangayInput = barangay.getText().toString().trim();
        String postal = postalCode.getText().toString().trim();
        String emailAddress = email.getText().toString().trim();
        String facebook = fbName.getText().toString().trim();

        if(name.isEmpty()){
            fullName.setError("Full name is required!");
            fullName.requestFocus();
            return;
        }

        if(name.length() < 6){
            fullName.setError("Is that even a full name?");
            fullName.requestFocus();
            return;
        }

        if(number.isEmpty()){
            phoneNumber.setError("Phone number is required!");
            phoneNumber.requestFocus();
            return;
        }

        if(!Pattern.compile("^(09|\\+639)\\d{9}$").matcher(number).matches()){
            phoneNumber.setError("Enter a PH Dial Code Phone Number");
            phoneNumber.requestFocus();
            return;
        }

        if(postal.isEmpty()){
            postalCode.setError("Your postal code is required!");
            postalCode.requestFocus();
            return;
        }
        if(postal.length() < 4){
            postalCode.setError("Invalid postal code.");
            postalCode.requestFocus();
            return;
        }
        if(postal.length() > 4){
            postalCode.setError("You're putting too much numbers.");
            postalCode.requestFocus();
            return;
        }

        if(addressInput.isEmpty()){
            address.setError("Your house number, building, and street name is required!");
            address.requestFocus();
            return;
        }
        if (addressInput.length() < 5){
            address.setError("Please input at least 5 characters!");
            address.requestFocus();
            return;
        }

        if (provinceInput.isEmpty()){
            province.setError("Your province is required!");
            province.requestFocus();
            return;
        }

        if (cityInput.isEmpty()){
            city.setError("Your city is required!");
            city.requestFocus();
            return;
        }

        if (barangayInput.isEmpty()){
            barangay.setError("Your barangay is required!");
            barangay.requestFocus();
            return;
        }

        if(emailAddress.isEmpty()){
            email.setError("Your email is required!");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
            email.setError("Enter a valid email!");
            email.requestFocus();
            return;
        }

        if(facebook.isEmpty()){
            fbName.setError("Your Facebook name is required!");
            fbName.requestFocus();
            return;
        }
        if (facebook.length() < 2){
            fbName.setError("When did facebook allowed you to have 1 character of your name?");
            fbName.requestFocus();
            return;
        }
        putDataInDatabase();
        showPopUp();
    }

    public void whenNoItemSelected(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("At least select one item.");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void userDidNotSelectPaymentMethod(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf09d");
        toastText.setText("Select one payment method.");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showPopUp(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.checkout_dialog);
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
                goToShipmentsFinishActivity();
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

    public void putDataInDatabase (){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String fullNameInput = fullName.getText().toString();
                String phoneNumberInput = phoneNumber.getText().toString();
                String addressInput = address.getText().toString();
                String provinceInput = province.getText().toString();
                String cityInput = city.getText().toString();
                String barangayInput = barangay.getText().toString();
                String postalCodeInput = postalCode.getText().toString();
                String emailInput = email.getText().toString();
                String facebookInput = fbName.getText().toString();
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                String timeInput = df.format(Calendar.getInstance().getTime());
                //ADDING ALL THE INFORMATION
                for (int i = 0; i < checkout_list.size(); i++){
                    String getItemName = String.valueOf(checkout_list.get(i).getName()).toLowerCase().replaceAll(" ", "");
                    userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("products").child(userName+getItemName).setValue(checkout_list.get(i));

                    if(selectedPayment == "COD"){
                        userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("products").child(userName+getItemName).setValue(checkout_list.get(i));
                    }
                    else if(selectedPayment == "Meet up"){
                        userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("products").child(userName+getItemName).setValue(checkout_list.get(i));
                    }
                    // Also remove the user's cart items since he bought the items
                    userCartDataReference.child(uid).child(userName+"Cart").child(userName+"Cart"+getItemName).removeValue();
                    // And Also reduce the stock
                    productDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            productStock = Math.toIntExact((Long) snapshot.child(getItemName).child("stock").getValue());
                            userProductQuantity = myCheckoutAdapter.productQuantity;
                            newTotalProductStock = productStock - userProductQuantity;
                            productDataReference.child(getItemName).child("stock").setValue(newTotalProductStock);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                if(selectedPayment == "COD"){
                    userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("region").setValue(selectedRegion);
                    userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("tracknumber").setValue("");
                    userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("status").setValue("Your item(s) are still processing!");

                    //Send it to the Admin
                    //In order to see and monitor your COD
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("uid").setValue(uid);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("userName").setValue(userName);

                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("region").setValue(selectedRegion);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("tracknumber").setValue("");
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("status").setValue("Your item(s) are still processing!");

                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("fullname").setValue(fullNameInput);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("phonenumber").setValue(phoneNumberInput);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("fulladdress").setValue(addressInput);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("province").setValue(provinceInput);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("city").setValue(cityInput);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("barangay").setValue(barangayInput);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("postalcode").setValue(postalCodeInput);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("emailaddress").setValue(emailInput);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("facebook").setValue(facebookInput);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("paymentmethod").setValue(selectedPayment);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("totalprice").setValue(totalPriceCheckout);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("setenablebutton").setValue(true);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("done").setValue(false);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("shipmentNumber").setValue(userName+randomCode);
                    userBoughtItemsDataReference.child("CODShipments").child(userName+randomCode).child("time").setValue(timeInput);
                }
                else if(selectedPayment == "Meet up"){
                    userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("schedule").setValue(selectedMeetup);

                    //Send it to the Admin
                    //In order to see and monitor your Meet ups
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("uid").setValue(uid);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("userName").setValue(userName);

                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("schedule").setValue(selectedMeetup);

                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("fullname").setValue(fullNameInput);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("phonenumber").setValue(phoneNumberInput);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("fulladdress").setValue(addressInput);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("province").setValue(provinceInput);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("city").setValue(cityInput);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("barangay").setValue(barangayInput);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("postalcode").setValue(postalCodeInput);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("emailaddress").setValue(emailInput);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("facebook").setValue(facebookInput);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("paymentmethod").setValue(selectedPayment);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("totalprice").setValue(totalPriceCheckout);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("setenablebutton").setValue(true);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("done").setValue(false);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("shipmentNumber").setValue(userName+randomCode);
                    userBoughtItemsDataReference.child("MeetUpShipments").child(userName+randomCode).child("time").setValue(timeInput);
                }
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("fullname").setValue(fullNameInput);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("phonenumber").setValue(phoneNumberInput);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("fulladdress").setValue(addressInput);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("province").setValue(provinceInput);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("city").setValue(cityInput);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("barangay").setValue(barangayInput);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("postalcode").setValue(postalCodeInput);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("emailaddress").setValue(emailInput);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("facebook").setValue(facebookInput);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("paymentmethod").setValue(selectedPayment);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("totalprice").setValue(totalPriceCheckout);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("setenablebutton").setValue(true);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("done").setValue(false);
                userShippingDataReference.child(uid).child(userName+"Shipments").child(userName+randomCode).child("shipmentNumber").setValue(userName+randomCode);

                //REMOVING THE CHECKOUT ITEMS AS WELL
                userCheckoutDataReference.child(uid).child(userName+"Checkout").removeValue();
            }
        }, 1500);
    }

    // For navigation
    public void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }
    public void goToShipments(){
        Intent intent = new Intent(this, ShipmentsActivity.class);
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
    public void goToReviews(){
        Intent intent = new Intent(this, UserReviewsActivity.class);
        startActivity(intent);
    }
    // Will go back because the checkout's item is empty
    public void goBackToCart(){
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
        finish();
    }
    public void goToShipmentsFinishActivity(){
        Intent intent = new Intent(this, ShipmentsActivity.class);
        startActivity(intent);
        finish();
    }
}