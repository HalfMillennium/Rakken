package com.coincalc.anduril.rakon;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public static boolean backable_a = true, backable_b = true;
    ConstraintLayout layout;
    AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            setContentView(R.layout.activity_main);
            layout = (ConstraintLayout) findViewById(R.id.layout);
        } else {
            setContentView(R.layout.activity_main_alt);   // simple 'View Stories' button
            layout = (ConstraintLayout) findViewById(R.id.layout_alt);
        }

        animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
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
        Toast.makeText(this, "You've been signed out!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            setContentView(R.layout.activity_main_alt);
        }
    }

    @Override
    public void onBackPressed() {
        if(backable_a && backable_b)
        {
            super.onBackPressed();
        }
    }
}
