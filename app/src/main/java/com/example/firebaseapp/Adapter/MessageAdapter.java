package com.example.firebaseapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Chat> mChat;
    private String imageURL,userName;
    private FirebaseUser firebaseUser;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1 ;



    public MessageAdapter(Context context, List<Chat> mChat, String imageURL,String userName) {
        this.context = context;
        this.mChat = mChat;
        this.imageURL = imageURL;
        this.userName = userName;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.showMessage.setText(chat.getMsg());
        holder.upTime.setText(chat.getUptime());

        if(imageURL.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(imageURL).into(holder.profile_image);
        }

            if(chat.isIsseen()){
                holder.seen.setText("Seen");
            }




        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(userName,imageURL);
            }
        });
    }

    private void showDialog(String user,String imageURL) {
        //客製化物件
        final View item = LayoutInflater.from(context).inflate(R.layout.alertdialog_item, null); //要引入的頁面ID
        TextView  userName = item.findViewById(R.id.userName);
        CircleImageView userImage = item.findViewById(R.id.profileImage);
        userName.setText(user);
        Glide.with(context).load(imageURL).into(userImage);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(item); //加入客製化頁面
        //builder.setView(aTxtView); //加入客製化元件

        //NO 按鈕
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }

        });

        builder.show(); //建立出物件
    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
    public class ViewHolder extends  RecyclerView.ViewHolder {

        private TextView showMessage,upTime;
        private ImageView profile_image,photoForChat;
        private TextView seen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.item_image);
            upTime = itemView.findViewById(R.id.uptime);
            seen = itemView.findViewById(R.id.text_seen);
            photoForChat = itemView.findViewById(R.id.imageForChat);
        }
    }


}
