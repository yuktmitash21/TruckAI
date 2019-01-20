package com.example.yuktmitash.testingaws;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class Chat extends AppCompatActivity {

    private EditText messageWriter;
    private RecyclerView recyclerView;
    private ImageButton send;

    private DatabaseReference ref;
    private StorageReference storageReference;
    private User currentUser;
    private String otherUID;
    private MessageAdapter messageAdapter;

    private HashMap<String, Bitmap> map;
    private List<ChatObj> chats;

    private HashSet<ChatObj> set;
    private CardView makePayment;
    private Offer offer;



    String speacialId = "jUIY13QAG4PzBtbblPYeBz7BsGl1";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_view);
        messageWriter = findViewById(R.id.messageToSend1);
        send = findViewById(R.id.button_send);
        ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        otherUID = getIntent().getStringExtra("to");
        map = new HashMap<>();
        chats = new ArrayList<>();
        set = new HashSet<>();
        messageAdapter = new MessageAdapter(getApplicationContext(), chats, map);
        recyclerView.setAdapter(messageAdapter);
        makePayment = findViewById(R.id.paymentButton);



        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent paying = new Intent(getApplicationContext(), PaymentConfirm.class);
                paying.putExtra("cost", offer.getOfferNumber());
                paying.putExtra("receiving", offer.getTo());
                paying.putExtra("giving", offer.getTo());
                paying.putExtra("carSpecs", offer.getCarId());

                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(offer.getTo())) {
                    Toast.makeText(getApplicationContext(), "You cannot make a payment!", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(paying);
                }

            }
        });

        ref.child("offers").child(speacialId).child("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offer = dataSnapshot.getValue(Offer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        storageReference.child("profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                map.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), bm);
                messageAdapter.notifyDataSetChanged();

            }
        });

        storageReference.child("profiles").child(otherUID).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                map.put(otherUID, bm);
                messageAdapter.notifyDataSetChanged();

            }
        });

        ref.child("messages").child("objects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot dat: dataSnapshot.getChildren()) {
                    ChatObj chatObj = dat.getValue(ChatObj.class);
                    if (!set.contains(chatObj)) {
                        chats.add(chatObj);
                    }
                    set.add(chatObj);

                }
                messageAdapter = new MessageAdapter(getApplicationContext(), chats, map);
                recyclerView.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String txt = messageWriter.getText().toString();
                if(txt.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter some text", Toast.LENGTH_SHORT).show();
                } else {
                    ref.child("messages").child(speacialId).child("counter").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long numOfMessages1 = (long) dataSnapshot.getValue();
                            numOfMessages1 = numOfMessages1 + 1;
                            ref.child("messages").child(speacialId).child("counter").setValue(numOfMessages1);
                            ChatObj myChatObj = new ChatObj(currentUser, txt);
                            ref.child("messages").child("objects").child(numOfMessages1 + "").setValue(myChatObj);
                            messageWriter.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });





    }






}
