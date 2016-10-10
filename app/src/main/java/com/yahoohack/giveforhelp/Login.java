package com.yahoohack.giveforhelp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn;
    private Button buttonSignIn2;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            //profile activity
            finish();
            overridePendingTransition(0, 0);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonSignIn2 = (Button) findViewById(R.id.buttonLogin2);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);

        buttonSignIn.setOnClickListener(this);
        buttonSignIn2.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

    }

    private void userLogin(final int userType) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Login...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            //profile activity
                            finish();
                            if (userType == 1) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                            else {
                                startActivity(new Intent(getApplicationContext(), InstitutionMainActivity.class));
                            }

                        }
                        else {
                            Toast.makeText(Login.this, "Login Failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSignIn) {
            userLogin(1);
        }

        if (v == buttonSignIn2) {
            userLogin(2);
        }

        if (v == textViewSignUp) {
            finish();
            startActivity(new Intent(this, SignUp.class));
        }
    }
}
