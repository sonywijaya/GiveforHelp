package com.yahoohack.giveforhelp;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahoohack.giveforhelp.Activity.Listing;
import com.yahoohack.giveforhelp.Activity.Reward;
import com.yahoohack.giveforhelp.Activity.HomeActivity;
import com.yahoohack.giveforhelp.Activity.ProfileActivity;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("deprecation")
public class InstitutionMainActivity extends TabActivity {

    TabHost TabHostWindow;
    boolean doubleBackToExitPressedOnce = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution_main);

        progress = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login.class));
        }

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User/"+uid+"/Type");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int type = Integer.parseInt(dataSnapshot.getValue(String.class));
                if (type == 1) {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),"You are not allowed to access Institution menu!", Toast.LENGTH_LONG).show();
                    changeUserType();
                }
                if (type == 2) {
                    progress.dismiss();
                    return;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Flurry Analytics
        FlurryAgent.setLogEnabled(true);
        try {
            FlurryAgent.setUserId(makeSHA1Hash(user.getEmail()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        FlurryAgent.init(this, "SMTR6YCB4D9NPZ9VKZG3");

        //Assign id to Tabhost.
        TabHostWindow = (TabHost)findViewById(android.R.id.tabhost);

        //Creating tab menu.
        TabHost.TabSpec TabMenu1 = TabHostWindow.newTabSpec("First Tab");
        TabHost.TabSpec TabMenu2 = TabHostWindow.newTabSpec("Second Tab");
        TabHost.TabSpec TabMenu3 = TabHostWindow.newTabSpec("Third Tab");
        TabHost.TabSpec TabMenu4 = TabHostWindow.newTabSpec("Fourth Tab");

        //Setting up tab 1 name.
        TabMenu1.setIndicator("Home");
        //Set tab 1 activity to tab 1 menu.
        TabMenu1.setContent(new Intent(this,HomeActivity.class));

        //Setting up tab 2 name.
        TabMenu2.setIndicator("Find");
        //Set tab 3 activity to tab 1 menu.
        TabMenu2.setContent(new Intent(this,Listing.class));

        //Setting up tab 2 name.
        TabMenu3.setIndicator("Reward");
        //Set tab 3 activity to tab 3 menu.
        TabMenu3.setContent(new Intent(this,Reward.class));

        TabMenu4.setIndicator("Profile");
        //Set tab 3 activity to tab 3 menu.
        TabMenu4.setContent(new Intent(this,ProfileActivity.class));

        //Adding tab1, tab2, tab3 to tabhost view.

        TabHostWindow.addTab(TabMenu1);
        TabHostWindow.addTab(TabMenu2);
        TabHostWindow.addTab(TabMenu3);
        TabHostWindow.addTab(TabMenu4);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("tab_index");
            if(value.equals("0")){
                TabHostWindow.setCurrentTab(Integer.valueOf(value));
            }
            if(value.equals("1")){
                TabHostWindow.setCurrentTab(Integer.valueOf(value));
            }
            if(value.equals("2")){
                TabHostWindow.setCurrentTab(Integer.valueOf(value));
            }
            if(value.equals("3")){
                TabHostWindow.setCurrentTab(Integer.valueOf(value));
            }
        }
    }

    public void changeUserType() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FlurryAgent.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            FlurryAgent.onEndSession(this);
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back button again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public class Sha1Hex {

        public String makeSHA1Hash(String input)
                throws NoSuchAlgorithmException, UnsupportedEncodingException
        {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.reset();
            byte[] buffer = input.getBytes("UTF-8");
            md.update(buffer);
            byte[] digest = md.digest();

            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
            return hexStr;
        }
    }

    public String makeSHA1Hash(String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = input.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return hexStr;
    }
}
