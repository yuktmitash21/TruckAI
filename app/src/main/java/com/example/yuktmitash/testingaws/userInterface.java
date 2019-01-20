package com.example.yuktmitash.testingaws;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class userInterface extends AppCompatActivity {
    private CardView buyingAct;
    private CardView sellingAct;
    private CardView logOut;
    private FirebaseAuth firebaseAuth;
    private CardView swipingBuyer;
    private CardView messages;
    private CardView makingProfile;

    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private DatabaseReference reference;

    private static final int CAMERA_REQUEST_CODE = 1;

    private String usernumber;
    private User user;

    private ImageView imageView;
    private TextView textView;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);
        sellingAct = findViewById(R.id.cardView1);
        buyingAct = findViewById(R.id.buyingCardView);
        firebaseAuth = FirebaseAuth.getInstance();
        logOut = findViewById(R.id.LogoutCardView);
        swipingBuyer = findViewById(R.id.findingBuyer);
        messages = findViewById(R.id.texting);
        makingProfile = findViewById(R.id.setProfilePic);
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        usernumber = FirebaseAuth.getInstance().getCurrentUser().getUid();


        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.imageView2);
        textView = findViewById(R.id.textView27);

        storageReference.child("profiles").child(usernumber).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                imageView.setMinimumHeight(dm.heightPixels);
                imageView.setMinimumWidth(dm.widthPixels);
                imageView.setImageBitmap(bm);

            }
        });

        reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                textView.setText(user.getUsername());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        makingProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(picIntent, CAMERA_REQUEST_CODE);
            }
        });

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PeopleMessages.class);
                startActivity(intent);
            }
        });

        swipingBuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TinderPrices.class));
            }
        });

        buyingAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TinderCar.class);
                startActivity(intent);
            }
        });


        sellingAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Selling.class));

            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();

            Bundle extras = data.getExtras();
            final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBAOS = baos.toByteArray();




            final StorageReference filepath = storageReference.child("profiles").child(usernumber);

            filepath.putBytes(dataBAOS).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(userInterface.this, "Image Uploaded!", Toast.LENGTH_LONG).show();



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(userInterface.this, "Ooops.. Something went wrong", Toast.LENGTH_LONG).show();


                }
            });



        }
    }
}
