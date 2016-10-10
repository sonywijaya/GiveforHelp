package com.yahoohack.giveforhelp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;
import com.yahoohack.giveforhelp.Login;
import com.yahoohack.giveforhelp.Menu.DonateForm;
import com.yahoohack.giveforhelp.R;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference myStorage;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myRefType;
    private TextView textViewUserEmail;
    private ImageView imageViewBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        imageViewBanner = (ImageView) findViewById(R.id.imageViewBanner);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login.class));
        }

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User/"+uid+"/Name");

        textViewUserEmail = (TextView) findViewById(R.id.userTextView);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                if (value == null) {
                    textViewUserEmail.setText("Welcome "+user.getEmail()+"!");
                }
                else {
                    textViewUserEmail.setText("Welcome "+value+"!");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        storage = FirebaseStorage.getInstance();
        myStorage = storage.getReference();

        myStorage.child("Banners/banner.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(HomeActivity.this).load(uri).into(imageViewBanner);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.getParent().onBackPressed();
    }
}
