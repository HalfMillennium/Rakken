package com.coincalc.anduril.rakon;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class StoryEntries extends AppCompatActivity {

    private DataSnapshot retSnap;
    private HashMap<String, String> item;
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private ArrayList<String> content;
    private SimpleAdapter sa;
    private ListView contentList;
    private TextView titleText;
    private int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_entries);
        titleText = (TextView) findViewById(R.id.story_name);
        titleText.setText(getIntent().getExtras().get("storyName").toString());
        getEntries(getIntent().getExtras().get("storyName").toString());
        content = new ArrayList<>();
    }

    public static Intent makeIntent(Context context)
    {
        return new Intent(context, StoryEntries.class);
    }

    public void getEntries(final String title)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("stories").child(title).child("content").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snap : dataSnapshot.getChildren())
                    {
                        //Log.d("title/snap", title + " | " + snap.child("content").child("4-7-18").getValue(String.class));
                        for(DataSnapshot shot : snap.getChildren()) {
                            content.add(shot.getValue(String.class));
                            Log.d("uh...", "hello?");

                            item = new HashMap<String,String>();
                            item.put("line1", content.get(i));
                            list.add(item);
                            Collections.reverse(list);
                            i++;
                        }
                    }

                    //Use an Adapter to link data to Views
                    sa = new SimpleAdapter(StoryEntries.this, list,
                            R.layout.entry_view_layout,
                            new String[] { "line1"},
                            new int[] {R.id.content});

                    //Link the Adapter to the list
                    contentList = (ListView)findViewById(R.id.entry_list);
                    contentList.setAdapter(sa);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // i really shouldn't be using this top for look, but... idrc
        /*
        DataSnapshot snap = getSnap(title);
        for(DataSnapshot shot : snap.child("content").getChildren())
        {
            for(DataSnapshot shot2 : shot.getChildren())
                i++;
        }

        Log.d("i2", "" + i);
        //Load the data
        for(int l = 0; l < 2; l++){
            item = new HashMap<String,String>();
            item.put("line1", content.get(l));
            list.add(item);
        }

        //Use an Adapter to link data to Views
        sa = new SimpleAdapter(StoryEntries.this, list,
                R.layout.entry_view_layout,
                new String[] { "line1"},
                new int[] {R.id.content});

        //Link the Adapter to the list
        contentList = (ListView)findViewById(R.id.entry_list);
        contentList.setAdapter(sa); */
    }

    public DataSnapshot getSnap(final String storyName)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("stories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    if(snap.getKey().equals(storyName))
                    {
                        retSnap = snap;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return retSnap;
    }
}
