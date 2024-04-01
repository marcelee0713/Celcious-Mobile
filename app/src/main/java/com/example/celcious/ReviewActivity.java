package com.example.celcious;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.appsearch.StorageInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReviewActivity extends AppCompatActivity {

    // for uploads
    private String userName = HomeActivity.userName;
    private String uid = HomeActivity.uid;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button chooseImageButton;
    private Button uploadButton;
    private EditText ratingMessage;
    private ImageView ratingImage;
    private ProgressBar progressBar;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask uploadTask;
    private DatabaseReference databaseDoneReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child(userName+"DoneShipments");

    //for rating(related to uploads)
    private TextView ratingStar1;
    private TextView ratingStar2;
    private TextView ratingStar3;
    private TextView ratingStar4;
    private TextView ratingStar5;
    private TextView itemsBought;
    int ratingNum = 0;


    //for navigation bar
    private TextView nav_home;
    private TextView nav_cart;
    private TextView nav_about;
    private TextView nav_shipments;
    private TextView nav_help;

    private String shipmentNumber;
    private String boughtItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //for getting the rating information
        SharedPreferences sp = getApplicationContext().getSharedPreferences("userRatingInformation", Context.MODE_PRIVATE);
        shipmentNumber = sp.getString("shipmentNumber", "");
        boughtItems = sp.getString("boughtItems", "");
        itemsBought = findViewById(R.id.boughItemText);
        itemsBought.setText(boughtItems);

        // for uploads
        chooseImageButton = findViewById(R.id.select_image_btn);
        uploadButton = findViewById(R.id.upload_btn);
        ratingMessage = findViewById(R.id.rating_message);
        ratingImage = findViewById(R.id.rating_image);
        progressBar = findViewById(R.id.review_progress_bar);

        // for ratings
        ratingStar1 = findViewById(R.id.rating_star_1);
        ratingStar2 = findViewById(R.id.rating_star_2);
        ratingStar3 = findViewById(R.id.rating_star_3);
        ratingStar4 = findViewById(R.id.rating_star_4);
        ratingStar5 = findViewById(R.id.rating_star_5);

        // for nav
        nav_home = findViewById(R.id.nav_home);
        nav_cart = findViewById(R.id.nav_cart);
        nav_about = findViewById(R.id.nav_about);
        nav_shipments = findViewById(R.id.nav_shipments);
        nav_help = findViewById(R.id.nav_help);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");

        ratingStar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingNum = 1;
                ratingStar1.setTextColor(getResources().getColor(R.color.accent));
                ratingStar2.setTextColor(getResources().getColor(R.color.primary));
                ratingStar3.setTextColor(getResources().getColor(R.color.primary));
                ratingStar4.setTextColor(getResources().getColor(R.color.primary));
                ratingStar5.setTextColor(getResources().getColor(R.color.primary));
            }
        });
        ratingStar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingNum = 2;
                ratingStar1.setTextColor(getResources().getColor(R.color.accent));
                ratingStar2.setTextColor(getResources().getColor(R.color.accent));
                ratingStar3.setTextColor(getResources().getColor(R.color.primary));
                ratingStar4.setTextColor(getResources().getColor(R.color.primary));
                ratingStar5.setTextColor(getResources().getColor(R.color.primary));
            }
        });
        ratingStar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingNum = 3;
                ratingStar1.setTextColor(getResources().getColor(R.color.accent));
                ratingStar2.setTextColor(getResources().getColor(R.color.accent));
                ratingStar3.setTextColor(getResources().getColor(R.color.accent));
                ratingStar4.setTextColor(getResources().getColor(R.color.primary));
                ratingStar5.setTextColor(getResources().getColor(R.color.primary));
            }
        });
        ratingStar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingNum = 4;
                ratingStar1.setTextColor(getResources().getColor(R.color.accent));
                ratingStar2.setTextColor(getResources().getColor(R.color.accent));
                ratingStar3.setTextColor(getResources().getColor(R.color.accent));
                ratingStar4.setTextColor(getResources().getColor(R.color.accent));
                ratingStar5.setTextColor(getResources().getColor(R.color.primary));
            }
        });
        ratingStar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingNum = 5;
                ratingStar1.setTextColor(getResources().getColor(R.color.accent));
                ratingStar2.setTextColor(getResources().getColor(R.color.accent));
                ratingStar3.setTextColor(getResources().getColor(R.color.accent));
                ratingStar4.setTextColor(getResources().getColor(R.color.accent));
                ratingStar5.setTextColor(getResources().getColor(R.color.accent));
            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uploadTask != null && uploadTask.isInProgress()){
                    showUploadInProgress();
                }
                else if(ratingNum == 0){
                    showNoRatingAdded();
                }
                else{
                    uploadFile();
                }
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

    public void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null & data.getData() != null){
            imageUri = data.getData();

            Glide.with(this).load(imageUri).into(ratingImage);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void uploadFile(){
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String timeInput = df.format(Calendar.getInstance().getTime());
        String message = ratingMessage.getText().toString().trim();
        if (message.isEmpty()){
            ratingMessage.setError("Your message is required!");
            ratingMessage.requestFocus();
            return;
        }

        if (imageUri != null){
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
            + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 500);

                    showUploadSuccessful();
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    String uploadId = databaseReference.push().getKey();
                    Upload upload = new Upload(userName,ratingMessage.getText().toString().trim(),downloadUrl.toString(),timeInput,uploadId,boughtItems,ratingNum);
                    databaseReference.child(uploadId).setValue(upload);
                    databaseDoneReference.child(shipmentNumber).child("done").setValue(false);
                    goToUserReviews();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showUploadFailure();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        }
        else{
            showNoImageAttach();
        }
    }

    public void showUploadInProgress(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("Upload in progress!");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showNoImageAttach(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf1c5");
        toastText.setText("Please attach an image!");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showUploadSuccessful(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("Upload successful!");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showUploadFailure(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("Upload failed!");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showNoRatingAdded(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf05a");
        toastText.setText("Add a star rating!");

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

    public void goToUserReviews(){
        Intent intent = new Intent(this, UserReviewsActivity.class);
        startActivity(intent);
        finish();
    }
}