package com.example.android.alzheimersassistant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    boolean status = false;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button login = (Button) findViewById(R.id.LoginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText email = (EditText) findViewById(R.id.LoginEmail);
                Editable x = email.getText();
                String lgemail = x.toString();

                EditText password = (EditText) findViewById(R.id.LoginPassword);
                Editable y = password.getText();
                String lgpass = y.toString();

                SignIn(lgemail, lgpass);
            }
        });

    }


    private void SignIn(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                          //  EditText Aid = (EditText) findViewById(R.id.AccountId);
                          //  String accountid = Aid.getText().toString();

                           // checkOtherCredentials(user, accountid, user.getUid());

                                if (user != null) {
                                    String em = user.getEmail();
                                    Toast.makeText(getApplicationContext(),
                                            em, Toast.LENGTH_SHORT).show();
                                }

                                Intent intent = new Intent(login.this, SignedIn.class);
                                startActivity(intent);

                        } else {

                            Log.w(TAG, "SignInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

   /* private void checkOtherCredentials(FirebaseUser user, String accountid, String uid) {

        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                     String  acc_id = dataSnapshot.child("account_id").getValue(String.class);
                     Toast.makeText(getApplicationContext(),acc_id,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }*/


}
