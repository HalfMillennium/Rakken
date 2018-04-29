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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class NewEntry extends AppCompatActivity {

    private String storyName;
    private EditText content;
    private FirebaseAuth mAuthListener;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        storyName = getIntent().getExtras().get("storyName").toString();
    }

    public static Intent makeIntent(Context context)
    {
        return new Intent(context, NewEntry.class);
    }

    public void submit(View view)
    {
        content = (EditText) findViewById(R.id.content);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        if(!content.getText().toString().replaceAll(" ", "").equals("")) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("stories").child(storyName).child("content").child(dateFormat.format(date))
                    .child(username + "," + timeFormat.format(date))
                    .setValue(content.getText().toString());
            ref.push();
            Toast.makeText(this, "Entry submitted!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Your entry needs content!", Toast.LENGTH_SHORT).show();
        }
    }
}
