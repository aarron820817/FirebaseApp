package com.example.firebaseapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebaseapp.Adapter.ChatAdapter;
import com.example.firebaseapp.ChatList;
import com.example.firebaseapp.R;
import com.example.firebaseapp.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

  private ChatAdapter userAdapter;
  private List<Users> mUser;

  private RecyclerView rvChats;

  FirebaseUser fUser;
  DatabaseReference myRef;

  private  List<ChatList> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats,container,false);
        rvChats = view.findViewById(R.id.rvChatList);
        rvChats.setHasFixedSize(true);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(fUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                userList.clear();
                //Loop for all user
                for(DataSnapshot ds : snapshot.getChildren()){
                    ChatList chatList = ds.getValue(ChatList.class);
                    userList.add(chatList);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return  view;
    }

    private void chatList() {

        //Getting all recent chats:

        mUser = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot ds :snapshot.getChildren()){
                    Users users = ds.getValue(Users.class);
                  for (ChatList chatList: userList){
                      if(users.getId().equals(chatList.getId())){
                          mUser.add(users);
                      }
                  }
                }
                userAdapter = new ChatAdapter(getContext(),mUser,true);
                rvChats.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}