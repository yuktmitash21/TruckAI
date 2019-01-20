package com.example.yuktmitash.testingaws;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.comprehend.AmazonComprehendClient;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

//Secret API Key: Ub7kbmyklcyf8asfptaxYECx5ns/SkMGV87zdAPf
//API Key: AKIAJU4RXJ4D6HJGCC7Q
public class MainActivity extends AppCompatActivity {
    private static final String SecretAPIKEY = "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";
    private static final String APIKey = "AKIAJJFGGRDYDBBBQABA";
    private EditText email;
    private EditText password;
    private EditText username;
    private TextView loginScreen;
    private TextView makeAccount;
    private CardView cardView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/
        ref = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            updateUI(firebaseAuth.getCurrentUser());
        }

        email = findViewById(R.id.emailAdd);
        password = findViewById(R.id.passwordEnter);
        username = findViewById(R.id.usernameEnter);
        loginScreen = findViewById(R.id.LoginScreen);
        cardView = findViewById(R.id.cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userEmail = email.getText().toString();
                final String userPassword = password.getText().toString();
                final String userNameText = username.getText().toString();
                if (userEmail.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid e-mail", Toast.LENGTH_SHORT).show();
                } else if (userPassword.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
                } else if (userNameText.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid username", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if (firebaseAuth.getCurrentUser() != null) {
                                String uid = firebaseAuth.getCurrentUser().getUid();
                                final User user = new User(false, userNameText, userEmail, userPassword, uid);
                                ref.child("users").child(uid).setValue(user);
                                updateUI(firebaseAuth.getCurrentUser());
                            } else {
                                Toast.makeText(getApplicationContext(), "Oops... Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }
        });

        loginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });






    }

    private void updateUI(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        startActivity(new Intent(getApplicationContext(), userInterface.class));

    }
}
