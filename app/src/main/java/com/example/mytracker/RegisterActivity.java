package com.example.mytracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText edtUsername, edtEmail, edtPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // ini
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        edtUsername = findViewById(R.id.edt_username);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        TextView tvLogin = findViewById(R.id.tv_login);
        Button btnReg = findViewById(R.id.btn_reg);

        // redirect to LoginActivity
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnReg.setOnClickListener(v -> {
            String email, password, username;
            email = String.valueOf(edtEmail.getText());
            password = String.valueOf(edtPassword.getText());
            username = String.valueOf(edtUsername.getText());
            // checks if input is empty
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(RegisterActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    DocumentReference documentReference = db.collection("users").document(userId);
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("email", email);
                    documentReference.set(user).addOnSuccessListener(unused -> Log.d("Registration", "user added with ID: " + documentReference.getId()))
                            .addOnFailureListener(e -> Log.w("Registration", "Error adding user", e));

                    Intent intent = new Intent(getApplicationContext(), ProfileSetupActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Sign Up Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}