package com.yahoohack.giveforhelp.Function;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Sony Surya on 09/10/2016.
 */

public class SetUser {
    public void setUserType(int userType) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User/"+uid+"/Type");
        myRef.setValue(String.valueOf(userType));
    }
}
