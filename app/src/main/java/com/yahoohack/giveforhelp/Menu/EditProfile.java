package com.yahoohack.giveforhelp.Menu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yahoohack.giveforhelp.*;
import com.yahoohack.giveforhelp.R;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSave;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference myStorage;
    private DatabaseReference myName;
    private DatabaseReference myAddress;
    private DatabaseReference myPhone;
    private CircleImageView imageViewProfile;
    private TextView changePicture;
    private ProgressDialog progress;
    private EditText editName;
    private EditText editPhone;
    private EditText editAddress;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.yahoohack.giveforhelp.R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        myStorage = storage.getReference();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login.class));
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission();
        }

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myName = database.getReference("User/"+getUserId()+"/Name");
        myAddress = database.getReference("User/"+getUserId()+"/Address");
        myPhone = database.getReference("User/"+getUserId()+"/Phone");

        imageViewProfile = (CircleImageView) findViewById(R.id.imageView);
        editName = (EditText) findViewById(R.id.editName);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editAddress = (EditText) findViewById(R.id.editAddress);
        progress = new ProgressDialog(this);

        myStorage.child("Profile/"+getUserId()+"/Profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(EditProfile.this).load(uri).fit().centerCrop().into(imageViewProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        myName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.getValue(String.class);

                if (name == null) {
                    editName.setText(user.getEmail());
                }
                else {
                    editName.setText(name);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myPhone.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String address = dataSnapshot.getValue(String.class);

                if (address == null) {
                    editPhone.setText("Please add phone number");
                }
                else {
                    editPhone.setText(address);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myAddress.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String address = dataSnapshot.getValue(String.class);

                if (address == null) {
                    editAddress.setText("Please set address");
                }
                else {
                    editAddress.setText(address);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(this);
        changePicture = (TextView) findViewById(R.id.changePicture);
        changePicture.setOnClickListener(this);

    }

    public static final int MY_PERMISSIONS_CAMERA = 99;
    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_CAMERA);


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_CAMERA);
            }
            return false;
        } else {
            return true;
        }
    }

    public String getUserId() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        return uid;
    }

    @Override
    public void onClick(View v) {
        if (v == changePicture) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }

        if (v == buttonSave) {
            progress.setMessage("Saving...");
            progress.show();
            myName.setValue(editName.getText().toString());
            myAddress.setValue(editAddress.getText().toString());
            myPhone.setValue(editPhone.getText().toString());
            myName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),"Profile updated", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),"Profile update failed. Try again!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED){
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                progress.setMessage("Uploading...");
                progress.show();

                Bundle extras = data.getExtras();
                final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataBAOS = baos.toByteArray();

                StorageReference imagesRef = myStorage.child("/Profile/"+getUserId()+"/Profile.jpg");

                UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(),"Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progress.dismiss();
                        imageViewProfile.setImageBitmap(bitmap);
                        Toast.makeText(getApplicationContext(),"Upload success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("tab_index","3");
        startActivity(i);
    }
}
