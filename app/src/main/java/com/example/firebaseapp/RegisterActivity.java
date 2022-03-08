package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    //Widgets
    EditText userET,passET,emailET;
    Button registerBtn;
    TextView text_Back;

    //Firebase
    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Init
        init();
    }

    private void init() {
        text_Back = findViewById(R.id.textBack);
        userET = findViewById(R.id.userEditText);
        passET =  findViewById(R.id.passwordEditText);
        emailET = findViewById(R.id.emailEditText2);
        registerBtn = findViewById(R.id.btn_Register);

        //text_Back setOnClick
        text_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Firebase Auth
        auth = FirebaseAuth.getInstance();

        //Adding Event Listener to Button Register
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userET.getText().toString().trim();
                String email = emailET.getText().toString().trim();
                String password = passET.getText().toString().trim();
                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"Please Fill All Fields",Toast.LENGTH_SHORT).show();
                }else {
                    RegisterNow(username,email,password);
                }
            }
        });

    }

    private  void RegisterNow(final  String username, String email, String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId= firebaseUser.getUid();
                            myRef = FirebaseDatabase.getInstance().getReference("MyUsers")
                                    .child(userId);
                            //HashMaps
                            HashMap<String, String> hashMap  = new HashMap<>();
                            hashMap.put("id",userId);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");
                            hashMap.put("intro","Intro");
                            hashMap.put("status","offline");

                            //Opening ths MainActivity after Success Registration
                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this,"Invalid Email or Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }
}