package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.firebaseapp.Adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;



public class MessageActivity extends AppCompatActivity {

    TextView username;
    ImageView imageView;
    ImageButton ib_send;
    EditText ed_message;
    //Firebase
    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    RecyclerView rvMessage;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    Intent intent;
    String userid;
    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        imageView = findViewById(R.id.image_profile);
        username = findViewById(R.id.username);
        ib_send = findViewById(R.id.ib_send);
        ed_message = findViewById(R.id.ed_message);


        //Recyclerview
        rvMessage = findViewById(R.id.rvMessage);
        rvMessage.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rvMessage.setLayoutManager(linearLayoutManager);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);
                username.setText(users.getUsername());
                if(users.getImageURL().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getApplicationContext())
                            .load(users.getImageURL())
                            .into(imageView);
                }
                readMessage(firebaseUser.getUid(),userid,users.getImageURL(),users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ib_send.setOnClickListener(v -> {
            String msg = ed_message.getText().toString().trim();
            if(!msg.equals("")){
                sendMessage(firebaseUser.getUid(),userid,msg);
                ed_message.setText("");
            }else {
                Toast.makeText(MessageActivity.this,"Please send a non empty msg.",Toast.LENGTH_SHORT).show();
            }
        });
        seenMessage(userid);
    }

    private void seenMessage(String id){
        //顯示已讀功能
        myRef = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(id)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String msg) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //取得"台北時區"時間
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        Calendar calendar = Calendar.getInstance();
        String uptime = simpleDateFormat.format(calendar.getTime());
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("msg",msg);
        hashMap.put("uptime",uptime);
        hashMap.put("isseen",false);
        reference.child("Chats").push().setValue(hashMap);


        //Adding User to Chat Fragment: Latest Chats with Contacts
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessage(String myid,String userid,String imageURL, String userName){
        mChat = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference("Chats");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot dt :snapshot.getChildren()){
                    Chat chat = dt.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid)||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this,mChat,imageURL,userName);
                    rvMessage.setAdapter(messageAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkStatus(String status){
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("status",status);
        myRef.updateChildren(hashMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        myRef.removeEventListener(seenListener);
        checkStatus("Offline");
    }


}