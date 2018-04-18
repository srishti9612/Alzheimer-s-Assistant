package com.example.android.alzheimersassistant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "SignUp";
    //private String AccountKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        Button authen = (Button) findViewById(R.id.submitbutton);
        authen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Authenticate(view);
            }
        });

    }


    private void Authenticate(View view) {

        EditText EmailId = (EditText) findViewById(R.id.EmailId);

        String email = EmailId.getText().toString(); //email address

        EditText SignUpPassword = (EditText) findViewById(R.id.SignUpPassword);

        String password = SignUpPassword.getText().toString();  //password

        EditText AccountId = (EditText) findViewById(R.id.AccId);

        final String accountid = AccountId.getText().toString();

        EditText uchoice = (EditText) findViewById(R.id.choice);
        String tchoice = uchoice.getText().toString();
        final int choice = Integer.parseInt(tchoice);

        final int[] ret = new int[1];

        ret[0] = CheckOtherCredentials(accountid, choice);

       // Toast.makeText(getApplicationContext(),"before signup" + ret, Toast.LENGTH_SHORT);

        if(ret[0]>=0) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                Log.d(TAG, "CreateUserEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                AddToDatabase(user, accountid, choice, ret[0]);

                                String u = user.getEmail();

                                Toast.makeText(getApplicationContext(), "createUserEmail:success "
                                        + u, Toast.LENGTH_LONG).show();


                                Intent intent = new Intent(SignUp.this, SignedIn.class);
                                startActivity(intent);

                            } else {

                                Log.w(TAG, "CreateUserEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication Failed. ",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else if (ret[0] == -1) {
            Toast.makeText(getApplicationContext(), "account type wrong",
                    Toast.LENGTH_SHORT).show();
        }

    }



    private int CheckOtherCredentials(final String acc_id, final int choice) {

        final int[] ret = new int[1];

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("account_ids");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(acc_id)) {

                    Toast.makeText(getApplicationContext(),"inside on data changed :" + choice, Toast.LENGTH_SHORT).show();

                    if(choice==2) {

                        Integer ct = dataSnapshot.child(acc_id).child("c_t").getValue(Integer.class);
                        Toast.makeText(getApplicationContext(),"ct: " + ct, Toast.LENGTH_SHORT);
                        if(ct!=null) {
                            if (ct == 0) {
                                // care taker account can be created
                                ret[0] = 1;
                            } else {
                                //care taker account cannot be created
                                ret[0] = -1;
                            }
                        }

                    }else if(choice==3) {

                        Integer pt = dataSnapshot.child(acc_id).child("p_t").getValue(Integer.class);

                        if(pt!=null) {

                            if (pt == 0) {
                                //patient account can be created
                                ret[0] = 1;
                            } else {
                                //patient account cannot be created
                                ret[0] = -1;
                            }
                        }
                    }

                } else {
                    ret[0] = 0;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(getApplicationContext(), "cocre" + ret[0], Toast.LENGTH_SHORT).show();

        return ret[0];

    }


    private void AddToDatabase (FirebaseUser user, String accountid, int user_choice, int cond) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("users").child(user.getUid().toString()).child("account_ids").setValue(accountid);

       // Toast.makeText(getApplicationContext(),"cond: "+cond + " user_choice: "+user_choice ,
         //       Toast.LENGTH_SHORT).show();

        if(cond == 0) {

            Toast.makeText(getApplicationContext(),"entered cond", Toast.LENGTH_SHORT);
            if (user_choice == 2) {
                myRef.child("account_ids").child(accountid).child("c_t").setValue(1);
                myRef.child("account_ids").child(accountid).child("p_t").setValue(0);
            } else if(user_choice == 3) {
                myRef.child("account_ids").child(accountid).child("c_t").setValue(0);
                myRef.child("account_ids").child(accountid).child("p_t").setValue(1);
            }
        } else if(cond == 1) {

            if(user_choice== 2) {
                myRef.child("account_ids").child(accountid).child("c_t").setValue(1);
            } else if(user_choice== 3) {
                myRef.child("account_ids").child(accountid).child("p_t").setValue(1);
            }
        }
    }

}
