package com.example.yuktmitash.testingaws;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class PeopleMessages extends AppCompatActivity {
    private ListView listView;
    private HashMap<User, Bitmap> map;
    private ArrayList<User> users;
    private LinearLayoutAdapter myAdapter;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private String uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_messages);

        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        listView = findViewById(R.id.myListView);
        users = new ArrayList<>();
        map = new HashMap<>();
        myAdapter = new LinearLayoutAdapter(users, map, getApplicationContext());
        listView.setAdapter(myAdapter);

        reference.child("chats").child(uid).child("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                users.add(user);
                storageReference.child("profiles").child(user.getFireid()).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        map.put(user, bm);
                        myAdapter.notifyDataSetChanged();

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = users.get(i);
                Intent chatIntent = new Intent(getApplicationContext(), Chat.class);
                chatIntent.putExtra("to", user.getFireid());
                startActivity(chatIntent);
            }
        });






    }
}
