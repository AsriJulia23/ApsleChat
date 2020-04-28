package com.example.chatrealtime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button UpdateAccountSetting;
    private EditText username, userstatus;
    private CircleImageView userFrofileImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingBar;
    private Toolbar settingTolbar;
    private String downloaedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile images");



        InitializeFields();


        UpdateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();

            }
        });

        RetriefeUserInfo();

        userFrofileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GalleryPick);
            }
        });
    }

    private void InitializeFields() {
        UpdateAccountSetting = (Button)findViewById(R.id.update);
        username =(EditText)findViewById(R.id.set_user_name);
        userstatus=(EditText)findViewById(R.id.set_profile_status);
        userFrofileImage =(CircleImageView) findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);

        settingTolbar = (Toolbar)findViewById(R.id.setting_toolbar);
        setSupportActionBar(settingTolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            loadingBar.setTitle("Set profile image");
            loadingBar.setMessage("please wait,your profile image is updating..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                loadingBar.setTitle("set profile image");
                loadingBar.setMessage("please wait, your profile image is updating .....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();


                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SettingActivity.this,"Profile image uploaded successfully...",Toast.LENGTH_SHORT).show();

                            downloaedUrl = task.getResult().getStorage().getDownloadUrl().toString();

                            RootRef.child("Users").child(currentUserID).child("image")
                                    .setValue(downloaedUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(SettingActivity.this, "Image save in Database, successfully ", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }else {
                                                String message = task.getException().toString();
                                                Toast.makeText(SettingActivity.this,"Error: " + message,Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }

                                        }
                                    });
                        }else {
                            String message = task.getException().toString();
                            Toast.makeText(SettingActivity.this,"Error: " + message,Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                });
            }
        }
    }

    private void UpdateSettings() {
        String setUserName = username.getText().toString();
        String setstatus = userstatus.getText().toString();


        if(TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this,"Please write your user name first...",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setstatus)){
            Toast.makeText(this,"Please write your status",Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,Object> profilemap = new HashMap<>();
                profilemap.put("uid",currentUserID);
                profilemap.put("name",setUserName);
                profilemap.put("status",setstatus);
            RootRef.child("Users").child(currentUserID).updateChildren(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SettingActivity.this,"Profile Update Successfully",Toast.LENGTH_SHORT).show();
                        //jika sukses akan menuju ke main activity
                        startActivity(new Intent(SettingActivity.this,MainActivity.class));
                    }else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingActivity.this,"Error" + message,Toast.LENGTH_SHORT).show();

                    }




                }
            });


        }


    }
    private void RetriefeUserInfo() {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image")))
                        {
                            String retriveUserName = dataSnapshot.child("name").getValue().toString();
                            String retriveStatus = dataSnapshot.child("status").getValue().toString();
                            String retriveProfileImage= dataSnapshot.child("image").getValue().toString();

                            username.setText(retriveUserName);
                            userstatus.setText(retriveStatus);
                            Picasso.get().load(retriveProfileImage).into(userFrofileImage);


                        }else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retriveUserName = dataSnapshot.child("name").getValue().toString();
                            String retriveStatus = dataSnapshot.child("status").getValue().toString();

                            username.setText(retriveUserName);
                            userstatus.setText(retriveStatus);

                        }else {
                            Toast.makeText(SettingActivity.this,"Please set & update your frofile information...",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
