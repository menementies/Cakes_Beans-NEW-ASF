package com.example.cakesbeans;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cakesbeans.databinding.ActivitySignUpBinding;
import com.example.cakesbeans.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private FirebaseAuth firebaseAuth;
    //progress dialog
    private ProgressDialog progressDialog;
    private String email ="", password="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        //configure progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Creating your account...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        ImageView btnBack, btnGoogle, btnFb;
        btnGoogle = findViewById(R.id.btn_gogol);
        btnGoogle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUp.this, "Sorry, not working. Try Signing Up", Toast.LENGTH_SHORT).show();
            }
        });

        btnFb=findViewById(R.id.btn_fb);
        btnFb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUp.this, "Sorry, not working. Try Signing Up", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next= new Intent(SignUp.this, com.example.cakesbeans.LoginForm.class);
                startActivity(next);
                finish();
            }
        });
    }

    private void validateData() {
        //get data
        email=binding.etEmail.getText().toString().trim();
        password=binding.etPassword.getText().toString().trim();

        //validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //email format aint valid..dont proceed eme
            binding.etEmail.setError("Invalid email format!");
        } else if (TextUtils.isEmpty(password)) {
            //no password entered
            binding.etPassword.setError("Enter Password");
        } else if (password.length() < 6) {
            //password not tough
            binding.etEmail.setError("Password must be at least 6 characters long");
        }else{
            //data is valid; continue firebase signup
            firebaseSignUp();
        }
    }
    private void firebaseSignUp() {
        //show progress
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //signup success
                        progressDialog.dismiss();
                        //get user info
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String email = firebaseUser.getEmail();
                        Toast.makeText(SignUp.this, "Account Created\n"+email, Toast.LENGTH_SHORT).show();

                        //open profile activity
                        startActivity(new Intent(SignUp.this, com.example.cakesbeans.LoginForm.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //signup failed
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onBackPressed(){
        Intent next= new Intent(SignUp.this, com.example.cakesbeans.LoginForm.class);
        startActivity(next);
        finish();
    }
}