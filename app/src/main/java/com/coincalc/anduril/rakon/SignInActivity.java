package com.coincalc.anduril.rakon;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailAdd, pass;
    private String email, eNosp, password, pNosp;
    private final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
    }

    public void signInUser(View view)
    {
        emailAdd = (EditText) findViewById(R.id.emailAdd);
            email = emailAdd.getText().toString();
            eNosp = email.replace(" ", "");
        pass = (EditText) findViewById(R.id.pass);
            password = pass.getText().toString();
            pNosp = password.replace(" ", "");

        if(!(eNosp.equals("") || pNosp.equals(""))) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void getResetPrompt(View view)
    {
        Intent intent = ForgotPassword.makeIntent(SignInActivity.this);
        startActivity(intent);
    }

    public static Intent makeIntent(Context context)
    {
        return new Intent(context, SignInActivity.class);
    }
}
