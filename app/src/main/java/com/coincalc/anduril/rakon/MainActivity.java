package com.coincalc.anduril.rakon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            setContentView(R.layout.activity_main);
        else
            setContentView(R.layout.activity_main_alt);   // simple 'View Stories' button
        }

    public void viewStories(View view)
    {
        Intent intent = ViewStories.makeIntent(MainActivity.this);
        startActivity(intent);
    }

    public void signUp(View view)
    {
        Intent intent = SignUpActivity.makeIntent(MainActivity.this);
        startActivity(intent);
    }

    public void signIn(View view)
    {
        Intent intent = SignInActivity.makeIntent(MainActivity.this);
        startActivity(intent);
    }

    public void signOut(View view)
    {
        FirebaseAuth.getInstance().signOut();
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            setContentView(R.layout.activity_main_alt);
        }
    }
}
