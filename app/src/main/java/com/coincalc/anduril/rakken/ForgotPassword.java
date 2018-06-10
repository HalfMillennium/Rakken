package com.coincalc.anduril.rakken;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.coincalc.anduril.rakon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private final String TAG = "ForgotPassword";
    private EditText emailAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    public void reset(View view)
    {
        emailAdd = (EditText) findViewById(R.id.emailAdd);
        if(!emailAdd.getText().toString().replaceAll(" ", "").equals("")) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailAdd.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                Toast.makeText(ForgotPassword.this, "Email sent.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ForgotPassword.this, "Unable to send reset email. Double-check email address.", Toast.LENGTH_SHORT).show();
                                Log.d("reset_email_error", task.getException().toString());
                            }
                        }
                    });
        }
    }

    public static Intent makeIntent(Context context)
    {
        return new Intent(context, ForgotPassword.class);
    }
}
