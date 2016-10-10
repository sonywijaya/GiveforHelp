package com.yahoohack.giveforhelp.Menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yahoohack.giveforhelp.Login;
import com.yahoohack.giveforhelp.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DonateForm extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference myStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;
    private DatabaseReference myName;
    private ImageButton imageButton;
    private EditText editTextName;
    private EditText editTextContact;
    private EditText editTextDescription;
    private Button buttonSubmit;
    private ProgressDialog progress;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final String timeStamp = getTimeStamp();
    private String postType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_form);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myName = database.getReference("User/"+getUserId()+"/Name");
        mDatabase = database.getReference("Items");
        storage = FirebaseStorage.getInstance();
        myStorage = storage.getReference();
        progress = new ProgressDialog(this);

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login.class));
        }

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        String mail = user.getEmail();
        myRef = database.getReference("User/"+uid+"/Type");

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextContact = (EditText) findViewById(R.id.editTextContact);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);
        imageButton.setOnClickListener(this);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int value = Integer.parseInt(dataSnapshot.getValue(String.class));
                if (value == 1) {
                    buttonSubmit.setText("Submit Donation Offer");
                }
                else {
                    buttonSubmit.setText("Submit Donation Request");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void submitPost() {
        final String itemName = editTextName.getText().toString().trim();
        final String userContact = editTextContact.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();

        if(TextUtils.isEmpty(itemName)) {
            Toast.makeText(this, "Please enter item name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userContact)) {
            Toast.makeText(this, "Please enter your email or phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please enter item description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (buttonSubmit.getText().toString() == "Submit Donation Offer") {
            DonateForm.this.postType = "Offer";
        }

        if (buttonSubmit.getText().toString() == "Submit Donation Request") {
            DonateForm.this.postType = "Request";
        }

        myName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.getValue(String.class);

                if (name == null) {
                    writeNewPost(getUserMail(), itemName, userContact, description, timeStamp, postType);
                }
                else {
                    writeNewPost(name, itemName, userContact, description, timeStamp, postType);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public String getTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    public String getUserId() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        return uid;
    }

    public String getUserMail() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String mail = user.getEmail();
        return mail;
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSubmit) {
            submitPost();
        }
        if (v == imageButton) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
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
                StorageReference imagesRef = myStorage.child("/Post/"+getUserId()+"/"+timeStamp+"/item.jpg");

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
                        imageButton.setImageBitmap(bitmap);
                        Toast.makeText(getApplicationContext(),"Upload success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @IgnoreExtraProperties
    public class Post {

        public String userName;
        public String itemName;
        public String userContact;
        public String itemDescription;
        public String keyTimeStamp;
        public String postType;

        public Post() {

        }

        public Post(String userName, String itemName, String userContact, String itemDescription, String keyTimeStamp, String postType) {
            this.userName = userName;
            this.itemName = itemName;
            this.userContact = userContact;
            this.itemDescription = itemDescription;
            this.keyTimeStamp = keyTimeStamp;
            this.postType = postType;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("userName", userName);
            result.put("itemName", itemName);
            result.put("userContact", userContact);
            result.put("itemDescription", itemDescription);
            result.put("keyTimeStamp", keyTimeStamp);
            result.put("postType", postType);
            return result;
        }

    }

    private void writeNewPost(String userId, String itemName, String userContact, String itemDescription, String keyTimeStamp, String postType) {

        DonateForm.Post post = new DonateForm.Post(userId, itemName, userContact, itemDescription, keyTimeStamp, postType);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + timeStamp, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + timeStamp, postValues);

        mDatabase.updateChildren(childUpdates);
    }
}
