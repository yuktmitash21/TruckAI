package com.example.yuktmitash.testingaws;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class Login extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private CardView cardView;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);
        cardView = findViewById(R.id.cardView);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        ref = FirebaseDatabase.getInstance().getReference();

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAdd = email.getText().toString();
                String passwordText = password.getText().toString();
                progressDialog.setMessage("Logging in user...");
                progressDialog.show();
                if (emailAdd.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid e-mail", Toast.LENGTH_SHORT).show();
                } else if (passwordText.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(emailAdd, passwordText).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            updateUI(firebaseAuth.getCurrentUser());
                            progressDialog.dismiss();

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
    }

    private void updateUI(FirebaseUser currentUser) {
        String uid = currentUser.getUid();
        ref.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.isAdmin()) {
                    startActivity(new Intent(getApplicationContext(), userInterface.class));

                } else {
                    startActivity(new Intent(getApplicationContext(), userInterface.class));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
