package com.example.celcious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView createAccountOption;
    private TextView forgotPasswordOption;

    private EditText logInEmail, logInPassword;
    private Button logInBtn;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    // In order to make the user still signed in whenever he closes
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()){
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAccountOption = findViewById(R.id.create_account_option);
        forgotPasswordOption = findViewById(R.id.forgot_password_option);
        logInBtn = findViewById(R.id.log_in_button);

        logInEmail = findViewById(R.id.email_address_input);
        logInPassword = findViewById(R.id.password_input);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.log_in_progress_bar);

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogIn();
            }
        });

        forgotPasswordOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToResetPassword();
            }
        });

        createAccountOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCreateAccount();
            }
        });
    }

    //This is where the user logs in
    public void userLogIn(){
        String emailAddress = logInEmail.getText().toString().trim();
        String password = logInPassword.getText().toString().trim();

        if(emailAddress.isEmpty()){
            logInEmail.setError("Email is required");
            logInEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
            logInEmail.setError("Enter a valid email!");
            logInEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            logInPassword.setError("Password is required!");
            logInPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            logInPassword.setError("Password should have at least 6 characters!");
            logInPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    }
                    else{
                        user.sendEmailVerification();
                        emailVerification();
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else{
                    failedLogin();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    public void failedLogin(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf071");
        toastText.setText("Failed to login!");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void emailVerification(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setVisibility(View.GONE);
        toastText.setText("An email verification has been sent to your email.");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void goToResetPassword(){
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    public void goToCreateAccount(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}