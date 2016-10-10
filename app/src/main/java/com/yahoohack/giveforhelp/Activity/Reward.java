package com.yahoohack.giveforhelp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.yahoohack.giveforhelp.Login;
import com.yahoohack.giveforhelp.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Reward extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference institutionNameRef;
    private DatabaseReference donorNameRef;
    private DatabaseReference pointsRef;
    private DatabaseReference itemsRef;
    private FirebaseStorage storage;
    private StorageReference myStorage;
    private EditText textInstitutionName;
    private EditText textDonorName;
    private EditText textPoints;
    private EditText editTextItems;
    private Button buttonAttach;
    private Button buttonSendReward;
    private ProgressDialog progressText;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final String timeStamp = getTimeStamp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        textInstitutionName = (EditText) findViewById(R.id.textInstitutionName);
        textDonorName = (EditText) findViewById(R.id.textDonorName);
        textPoints = (EditText) findViewById(R.id.textPoints);
        editTextItems = (EditText) findViewById(R.id.editTextItems);
        progressText = new ProgressDialog(this);
        storage = FirebaseStorage.getInstance();
        myStorage = storage.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login.class));
        }

        database = FirebaseDatabase.getInstance();
        institutionNameRef = database.getReference("RewardRequest/"+getUserId()+"/"+timeStamp+"/InstitutionName");
        donorNameRef = database.getReference("RewardRequest/"+getUserId()+"/"+timeStamp+"/DonorName");
        pointsRef = database.getReference("RewardRequest/"+getUserId()+"/"+timeStamp+"/Points");
        itemsRef = database.getReference("RewardRequest/"+getUserId()+"/"+timeStamp+"/Items");

        buttonAttach = (Button) findViewById(R.id.buttonAttach);
        buttonSendReward = (Button) findViewById(R.id.buttonSendReward);
        buttonAttach.setOnClickListener(this);
        buttonSendReward.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        if (v == buttonSendReward) {
            progressText.setMessage("Sending...");
            progressText.show();
            institutionNameRef.setValue(textInstitutionName.getText().toString());
            donorNameRef.setValue(textDonorName.getText().toString());
            pointsRef.setValue(textPoints.getText().toString());
            itemsRef.setValue(editTextItems.getText().toString());
            itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    progressText.dismiss();
                    Toast.makeText(getApplicationContext(),"Reward Request Sent!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressText.dismiss();
                    Toast.makeText(getApplicationContext(),"Failed to send. Try again!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (v == buttonAttach) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED){
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                progressText.setMessage("Uploading...");
                progressText.show();

                Bundle extras = data.getExtras();
                final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataBAOS = baos.toByteArray();

                StorageReference imagesRef = myStorage.child("/RewardRequest/"+getUserId()+"/"+timeStamp);

                UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressText.dismiss();
                        Toast.makeText(getApplicationContext(),"Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressText.dismiss();
                        Toast.makeText(getApplicationContext(),"Upload success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
