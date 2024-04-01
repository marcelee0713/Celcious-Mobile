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
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private TextView logInOption;
    private EditText userNameInput, emailAddressInput, passwordInput, cfmPasswordInput;
    private Button createAccountBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        logInOption = findViewById(R.id.account_log_in_option);
        createAccountBtn = findViewById(R.id.create_account_button);

        userNameInput = findViewById(R.id.ca_username_input);
        emailAddressInput = findViewById(R.id.ca_email_address_input);
        passwordInput = findViewById(R.id.ca_password_input);
        cfmPasswordInput = findViewById(R.id.ca_confirm_password_input);

        progressBar = findViewById(R.id.register_progress_bar);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userCreateAccount();
            }
        });

        logInOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogIn();
            }
        });
    }

    public void userCreateAccount(){
        String userName = userNameInput.getText().toString().trim();
        String emailAddress = emailAddressInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String cfmPassword = cfmPasswordInput.getText().toString().trim();

        String ifUsernameIsCelcious = userNameInput.getText().toString().trim().toLowerCase();

        if(userName.isEmpty()){
            userNameInput.setError("Username is required");
            userNameInput.requestFocus();
            return;
        }

        if(userName.length() < 6){
            userNameInput.setError("Username should have at least 6 characters!");
            userNameInput.requestFocus();
            return;
        }

        if(ifUsernameIsCelcious.equals("celcious")){
            userNameInput.setError("You really think you can do that?");
            userNameInput.requestFocus();
            return;
        }

        if(emailAddress.isEmpty()){
            emailAddressInput.setError("Email is required!");
            emailAddressInput.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
            emailAddressInput.setError("Provide a valid email!");
            emailAddressInput.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordInput.setError("Password is required!");
            passwordInput.requestFocus();
            return;
        }

        if(password.length() < 6){
            passwordInput.setError("Password should have at least 6 characters!");
            passwordInput.requestFocus();
            return;
        }

        if(password == "password"){
            passwordInput.setError("You can't set your password like that...");
            passwordInput.requestFocus();
            return;
        }

        if(cfmPassword.isEmpty()){
            cfmPasswordInput.setError("Confirm password is required!");
            cfmPasswordInput.requestFocus();
            return;
        }

        if(!password.equals(cfmPassword)){
            cfmPasswordInput.setError("Password is not matched!");
            cfmPasswordInput.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(userName, emailAddress, password);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        successfulRegister();
                                        progressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        failedRegister();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
                else{
                    failedRegister();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void successfulRegister(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf4fc");
        toastText.setText("You are now registered!");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    public void failedRegister(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.added_to_cart_toast, null);

        TextView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastIcon.setText("\uf235");
        toastText.setText("Failed to register, please try again.");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void goToLogIn(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}