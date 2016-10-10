package com.yahoohack.giveforhelp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.yahoohack.giveforhelp.Menu.EditProfile;
import com.yahoohack.giveforhelp.Login;
import com.yahoohack.giveforhelp.R;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonLogout;
    private Button buttonUpload;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference myStorage;
    private DatabaseReference myName;
    private DatabaseReference myAddress;
    private CircleImageView imageViewProfile;
    private TextView textViewName;
    private TextView textViewAddress;
    private TextView userCategory;
    private TextView userPointsVal;
    private TextView userDonatedVal;
    private TextView userLevelVal;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ProgressDialog progress;
    private DatabaseReference userInfo;
    private UploadTask uploadTask;
    private Uri uriMedia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        myStorage = storage.getReference();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login.class));
        }

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        imageViewProfile = (CircleImageView) findViewById(R.id.imageView);
        progress = new ProgressDialog(this);

        database = FirebaseDatabase.getInstance();
        myName = database.getReference("User/"+uid+"/Name");
        myAddress = database.getReference("User/"+uid+"/Address");

        textViewName = (TextView) findViewById(R.id.displayName);
        textViewAddress = (TextView) findViewById(R.id.userAddress);
        userCategory = (TextView) findViewById(R.id.userCategory);
        userPointsVal = (TextView) findViewById(R.id.userPointsVal);
        userDonatedVal = (TextView) findViewById(R.id.userDonatedVal);
        userLevelVal = (TextView) findViewById(R.id.userLevelVal);

        myStorage.child("Profile/"+uid+"/Profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ProfileActivity.this).load(uri).into(imageViewProfile);
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
                    textViewName.setText(user.getEmail());
                }
                else {
                    textViewName.setText(name);
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
                    textViewAddress.setText("Please set address!");
                }
                else {
                    textViewAddress.setText(address);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*userInfo = database.getReference(uid);
        userInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                String category = map.get("Category");
                String points = String.valueOf(map.get("Points"));
                String level = String.valueOf(map.get("Level"));
                String donated = String.valueOf(map.get("Donated"));

                userCategory.setText(category);
                userPointsVal.setText(points);
                userDonatedVal.setText(donated);
                userLevelVal.setText(level);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        buttonUpload = (Button) findViewById(R.id.upload);
        buttonUpload.setOnClickListener(this);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);
    }

    public String getUserId() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        return uid;
    }

    @Override
    public void onClick(View v) {
        if (v == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, Login.class));
        }
        if (v == buttonUpload) {
            finish();
            startActivity(new Intent(this, EditProfile.class));
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

                //RoundedBitmapDrawable img = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                //img.setCircular(true);
                //imageViewProfile.setImageDrawable(img);

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
        this.getParent().onBackPressed();
    }
}
