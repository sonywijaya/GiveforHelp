package com.yahoohack.giveforhelp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahoohack.giveforhelp.Function.DividerItemDecoration;
import com.yahoohack.giveforhelp.Function.Item;
import com.yahoohack.giveforhelp.Function.ItemsAdapter;
import com.yahoohack.giveforhelp.Function.RecyclerTouchListener;
import com.yahoohack.giveforhelp.Menu.DonateForm;
import com.yahoohack.giveforhelp.R;

import java.util.ArrayList;
import java.util.List;

public class Listing extends AppCompatActivity {
    private List<Item> mItemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ItemsAdapter mAdapter;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DonateForm.class));
            }
        });

        mAdapter = new ItemsAdapter(mItemList);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Items/posts");

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Item item = mItemList.get(position);
                Toast.makeText(getApplicationContext(), "Please call " + item.getUserContact() + " to donate!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Item post = postSnapshot.getValue(Item.class);
                    Listing.this.prepareMovieData(post.getUserName(), post.getUserContact(), post.getPostType(), post.getItemName(), post.getItemDescription());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Get Detail:", "Error");
            }
        });
    }

    private void prepareMovieData(String userName, String userContact, String postType, String itemName, String itemDetail) {
        Item item = new Item(userName, userContact, postType, itemName, itemDetail);
        mItemList.add(item);
        mAdapter.notifyDataSetChanged();
    }

}
