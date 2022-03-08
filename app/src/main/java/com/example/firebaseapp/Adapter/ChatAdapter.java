package com.example.firebaseapp.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebaseapp.Chat;
import com.example.firebaseapp.MessageActivity;
import com.example.firebaseapp.R;
import com.example.firebaseapp.Users;
import com.example.firebaseapp.fragments.UsersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter  extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private List<Users> mUsers;
    private boolean isChat;
    private Dialog dialog;

    public ChatAdapter(Context context,List<Users> mUsers,boolean isChat) {
        this.context = context;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new ChatAdapter.ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

        Users users = mUsers.get(position);
        holder.userName.setText(users.getUsername());
        if(users.getImageURL().equals("default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context)
                    .load(users.getImageURL())
                    .into(holder.imageView);
        }
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showDialog();
//                return false;
//            }
//        });

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
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userid",users.getId());
                context.startActivity(i);
            }
        });
    }

//    private void showDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setIcon(R.drawable.icon) //設定標題圖片
//                .setTitle("提醒") //設定標題文字
//                .setMessage("你確定要刪除對話嗎?") //設定內容文字
//                .setPositiveButton("確定", new DialogInterface.OnClickListener()
//                { //設定確定按鈕
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//
//
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener()
//                { //設定取消按鈕
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        // TODO Auto-generated method stub
//                    }
//                });
//
//
//        dialog = builder.create(); //建立對話方塊並存成 dialog
//        dialog.show();
//    }

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
