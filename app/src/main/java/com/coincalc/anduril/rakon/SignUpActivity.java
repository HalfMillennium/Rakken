package com.coincalc.anduril.rakon;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final String TAG = "SignUpActivity";
    private EditText emailAdd, user, pass;
    private String email, username, password;
    private String eNosp, uNosp, pNosp;
    private String errSpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        emailAdd = (EditText) findViewById(R.id.emailAdd);
            email = emailAdd.getText().toString();
            eNosp = email.replace(" ", "");
        user = (EditText) findViewById(R.id.user);
            username = user.getText().toString();
            uNosp = email.replace(" ", "");
        pass = (EditText) findViewById(R.id.pass);
            password = pass.getText().toString();
            pNosp = email.replace(" ", "");
    }

    public void createNewUser(View view)
    {
        if(!(eNosp.equals("") || uNosp.equals("") || pNosp.equals(""))) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("users").orderByChild(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        buildUser();
                    } else {
                        Toast.makeText(SignUpActivity.this, "This username has already been taken.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void buildUser()
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            finish();
                        } else {
                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                Log.d(TAG, "onComplete: weak_password");
                                Toast.makeText(SignUpActivity.this, "Password too weak!", Toast.LENGTH_SHORT).show();
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Log.d(TAG, "onComplete: malformed_email");
                                Toast.makeText(SignUpActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Log.d(TAG, "onComplete: exist_email");
                                Toast.makeText(SignUpActivity.this, "This email address already exists!", Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                                Toast.makeText(SignUpActivity.this, "Sign up failed. Try again later.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        // ...
                    }
                });
    }

    public static Intent makeIntent(Context context)
    {
        return new Intent(context, SignUpActivity.class);
    }
}
