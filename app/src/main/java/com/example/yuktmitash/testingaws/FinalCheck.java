package com.example.yuktmitash.testingaws;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
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

public class FinalCheck extends AppCompatActivity {

    private Bitmap bitmap;
    private String hash;

    private ImageView imageView;
    private TextView makeTextView;
    private TextView yearTextView;

    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    private StorageReference storageReference;

    private long decider;
    private String make;
    private String model;
    private int year;

    private byte[] byteArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_check);

        hash = getIntent().getStringExtra("carHash");
        make = getIntent().getStringExtra("make");
        model = getIntent().getStringExtra("model");
        year = getIntent().getIntExtra("year", 0);
        byteArray = getIntent().getByteArrayExtra("pic");



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.imageView);
        makeTextView = findViewById(R.id.makeAndModel);
        yearTextView = findViewById(R.id.yearTextView);

        Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        imageView.setMinimumHeight(dm.heightPixels);
        imageView.setMinimumWidth(dm.widthPixels);
        imageView.setImageBitmap(bm);

        /*storageReference.child(hash).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                imageView.setMinimumHeight(dm.heightPixels);
                imageView.setMinimumWidth(dm.widthPixels);
                imageView.setImageBitmap(bm);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/




        makeTextView.setText(make + " " + model);
        yearTextView.setText(year + "");

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Is this okay?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "All set!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), userInterface.class));

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent oldIntent = new Intent(getApplicationContext(), Selling.class);
                        startActivity(oldIntent);



                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }
}
