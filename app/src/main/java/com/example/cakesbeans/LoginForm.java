package com.example.cakesbeans;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cakesbeans.databinding.ActivityLoginBinding;
import com.example.cakesbeans.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginForm extends AppCompatActivity {
    //View binding
    private ActivityLoginBinding binding;
    private String email="",password="";
    //progress dialog
    private ProgressDialog progressDialog;
    //firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging In");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        Button btnSignUp;
        btnSignUp = findViewById(R.id.bt_signUp);
        //if no account
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                    Intent next= new Intent(LoginForm.this,SignUp.class);
                    startActivity(next);
                    finish();
            }
        });
        binding.btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               validateData();
            }
        });

    }
    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser !=null){
            //user is logged in
            startActivity(new Intent(this, Menu.class));
            finish();
        }
    }

    private void validateData() {
        //get data
        email=binding.etEmail.getText().toString().trim();
        password=binding.etPassword.getText().toString().trim();
        //validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //email format ain't valid..don't proceed eme
            binding.etEmail.setError("Invalid email format!");
        } else if (TextUtils.isEmpty(password)) {
            //no password entered
            binding.etPassword.setError("Enter Password");
        }else{
            //data is valid; continue firebase signup
            firebaseLogin();
        }
    }

    private void firebaseLogin() {
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login success
                        //get user info
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String email = firebaseUser.getEmail();
                        Toast.makeText(LoginForm.this,"Logged In\n", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginForm.this, Menu.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //login failed, get and show error msg
                        progressDialog.dismiss();
                        Toast.makeText(LoginForm.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        //open profile activity
                        startActivity(new Intent(LoginForm.this, LoginForm.class));
                        finish();
                    }
                });

    }
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginForm.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Quit App?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}