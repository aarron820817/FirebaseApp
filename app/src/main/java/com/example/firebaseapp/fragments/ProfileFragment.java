package com.example.firebaseapp.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebaseapp.R;
import com.example.firebaseapp.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.POWER_SERVICE;

public class ProfileFragment extends Fragment {
    //個人檔案
    CircleImageView userImage;
    TextView userName,textIn;
    EditText ed_userName,ed_intro;
    Button btn_Save,btn_Cancel;
    ImageButton ib_edit;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask storageTask;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=  inflater.inflate(R.layout.fragment_profile, container, false);
        userImage = view.findViewById(R.id.profileImage);
        userName = view.findViewById(R.id.userName);
        textIn = view.findViewById(R.id.textIntroduction);
        ed_userName = view.findViewById(R.id.edit_username);
        btn_Save = view.findViewById(R.id.btn_save);
        btn_Cancel = view.findViewById(R.id.btn_cancel);
        ib_edit = view.findViewById(R.id.ib_edit);
        ed_intro = view.findViewById(R.id.ed_intro);


        ib_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_Save.setVisibility(View.VISIBLE);
                ed_userName.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                textIn.setVisibility(View.GONE);
                btn_Cancel.setVisibility(View.VISIBLE);
                ed_intro.setVisibility(View.VISIBLE);

                reference = FirebaseDatabase.getInstance().getReference("MyUsers");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Users users = ds.getValue(Users.class);
                            if(users.getId().equals(firebaseUser.getUid())){
                             ed_intro.setText(users.getIntro());
                             ed_userName.setText(users.getUsername());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                btn_Save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference = FirebaseDatabase.getInstance().getReference("MyUsers");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    Users users = ds.getValue(Users.class);
                                    if(users.getId().equals(firebaseUser.getUid())){
                                        String name = ed_userName.getText().toString();
                                        String intro = ed_intro.getText().toString();
                                        HashMap<String ,Object> hashMap = new HashMap<>();
                                        hashMap.put("username",name);
                                        hashMap.put("intro",intro);
                                        ds.getRef().updateChildren(hashMap);
                                    }
                                    btn_Save.setVisibility(View.GONE);
                                    ed_userName.setVisibility(View.GONE);
                                    userName.setVisibility(View.VISIBLE);
                                    btn_Cancel.setVisibility(View.GONE);
                                    textIn.setVisibility(View.VISIBLE);
                                    ed_intro.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                });
                btn_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_Save.setVisibility(View.GONE);
                        ed_userName.setVisibility(View.GONE);
                        userName.setVisibility(View.VISIBLE);
                        btn_Cancel.setVisibility(View.GONE);
                        ed_intro.setVisibility(View.GONE);
                        textIn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });




        //Profile Image reference in storage
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                userName.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    userImage.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(view.getContext().getApplicationContext()).load(user.getImageURL()).into(userImage);
                }
                textIn.setText(user.getIntro());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //連接Storage 指向uploads資料夾
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        userImage.setOnClickListener(new View.OnClickListener() {
            //點擊大圖更改個人頭像
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        return view;


    }

    private void openImage() {
        //開啟手機image資料夾
        Intent intent = new Intent();
        //選資料類型
        intent.setType("image/*");
        //指向資料夾
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        //解析取得的檔案是何種格式
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        //上傳圖片至Storage
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("資料處理中 請稍候");
        progressDialog.show();

        if (imageUri != null){
            //用系統時間為檔案命名 +"."+ 檔案類型
            final StorageReference mStorageReference = storageReference.child(System.currentTimeMillis() +"."+getFileExtension(imageUri));

            storageTask = mStorageReference.putFile(imageUri);

            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return mStorageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        //將圖檔資料更新到Database
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());
                        HashMap<String , Object> map = new HashMap<>();
                        map.put("imageURL",mUri);
                        reference.updateChildren(map);

                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(getContext(), "上傳失敗",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "沒有選擇圖片",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (storageTask != null && storageTask.isInProgress()){
                Toast.makeText(getContext(), "Uploading",Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }
        }
    }
}