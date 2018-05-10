package com.coincalc.anduril.rakon;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateNew extends AppCompatActivity {

    private String selectedGenre, title, content;
    private Spinner spinner;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private EditText title_field, content_field;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);

        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> genAdapt = new ArrayAdapter<String>(CreateNew.this,
               R.layout.spinner_item, getResources().getStringArray(R.array.genres));
        genAdapt.setDropDownViewResource(R.layout.spinner_item);

        spinner.setAdapter(genAdapt);

        title_field = (EditText) findViewById(R.id.title);
        content_field = (EditText) findViewById(R.id.first_entry);

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewStory(view);
            }
        });
    }

    public void makeNewStory(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        title = title_field.getText().toString();
        content = content_field.getText().toString();
        selectedGenre = spinner.getSelectedItem().toString();

        ref.child("users").child(user.getEmail().replace(".", ""))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.getValue(String.class);
                        // dont forget ref.push()
                        DatabaseReference mRef = ref.child("stories").child(title);
                        mRef.child("contribs").setValue(username);
                        mRef.child("user").setValue(username);
                        mRef.child("genre").setValue(selectedGenre);

                        DateFormat dateFormatProper = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat dateFormatFancy = new SimpleDateFormat("MM.dd.yyyy");
                        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        Date date = new Date();

                        mRef.child("created").setValue(dateFormatFancy.format(date));
                        mRef.child("content").child(dateFormatProper.format(date))
                                .child(username + "," + timeFormat.format(date))
                                .setValue(content);

                        mRef.push();

                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static Intent makeIntent (Context context)
    {
        return new Intent(context, CreateNew.class);
    }
}
