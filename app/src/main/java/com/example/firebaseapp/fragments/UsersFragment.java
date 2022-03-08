package com.example.firebaseapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebaseapp.MainActivity;
import com.example.firebaseapp.MessageActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Users> mUsers;
    private Activity activity;
    public UsersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users,container,false);
        recyclerView = view.findViewById(R.id.rvUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        ReadUsers();
        return view;
    }



    private void ReadUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("MyUsers");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren() ){
                    Users user = snapshot.getValue(Users.class);
                    assert user != null;
                    if(!user.getId().equals(firebaseUser.getUid())){
                        mUsers.add(user);
                    }
                    userAdapter = new UserAdapter(getContext(),mUsers,true);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
        private Context context;
        private List<Users> mUsers;
        private boolean isChat;

        public UserAdapter(Context context,List<Users> mUsers,boolean isChat) {
            this.context = context;
            this.mUsers = mUsers;
            this.isChat = isChat;
        }

        @NonNull
        @Override
        public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
            return new UserAdapter.ViewHolder(view) ;
        }

        @Override
        public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {

            Users users = mUsers.get(position);
            holder.userName.setText(users.getUsername());
            if(users.getImageURL().equals("default")){
                holder.imageView.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(context)
                        .load(users.getImageURL())
                        .into(holder.imageView);
            }

            //Status check
            if(isChat){
                if(users.getStatus().equals("Online")){
                    holder.on.setVisibility(View.VISIBLE);
                    holder.off.setVisibility(View.GONE);
                }else {
                    holder.on.setVisibility(View.GONE);
                    holder.off.setVisibility(View.VISIBLE);
                }
            }else {
                holder.on.setVisibility(View.GONE);
                holder.off.setVisibility(View.GONE);
            }



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,MessageActivity.class);
                    i.putExtra("userid",users.getId());
                    context.startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }


        public class ViewHolder extends  RecyclerView.ViewHolder{
            public TextView userName;
            public CircleImageView imageView,on,off;

            public ViewHolder(View itemView){
                super(itemView);
                userName = itemView.findViewById(R.id.name);
                imageView = itemView.findViewById(R.id.user_image);
                on = itemView.findViewById(R.id.status_on);
                off = itemView.findViewById(R.id.status_off);
            }
        }
    }
}