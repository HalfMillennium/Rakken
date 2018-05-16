package com.coincalc.anduril.rakon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class StoryEntries extends AppCompatActivity {

    private DataSnapshot retSnap;
    private HashMap<String, String> item;
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private ArrayList<DataSnapshot> snapList = new ArrayList<>();
    private ArrayList<String> content;
    private SimpleAdapter sa;
    private ListView contentList;
    private TextView titleText;
    private String storyName;
    private final int fromStoryEntry = 1;
    private boolean clrForEntry = true;
    private int i = 0;
    private int q = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_entries);
        titleText = (TextView) findViewById(R.id.story_name);

        storyName = getIntent().getExtras().get("storyName").toString();
        titleText.setText(storyName);
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
        //ref.child("stories").child(title).child("content")
        Query query = ref.child("stories").child(title).child("content").orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snap : dataSnapshot.getChildren())
                    {
                        for(DataSnapshot shot : snap.getChildren()) {
                            snapList.add(snap);
                            content.add(shot.getValue(String.class));
                            Log.d("uh...", "hello?");

                            item = new HashMap<String,String>();
                            item.put("line1", content.get(i));
                            list.add(item);
                            //Collections.reverse(list);
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
            public void onCancelled(DatabaseError databaseError) {}
        });
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

    public void addStoryEntry(View view) {
        final Intent intent = NewEntry.makeIntent(StoryEntries.this);
        intent.putExtra("storyName", storyName);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("stories");

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = new Date();
        Log.d("storyName", storyName);
        final String username = getIntent().getExtras().get("username").toString();

        ref.child(storyName).child("content").child(dateFormat.format(date)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snaps : dataSnapshot.getChildren())
                    {
                        Log.d("snapsKey", snaps.getKey());
                        if(snaps.getKey().contains(username) && clrForEntry)
                        {
                            Toast.makeText(StoryEntries.this, "You can't post more than once a day! Come back later.", Toast.LENGTH_SHORT).show();
                            clrForEntry = false;
                        }

                        if((q == dataSnapshot.getChildrenCount() - 1) && clrForEntry)
                        {
                            startActivityForResult(intent, fromStoryEntry);
                        }

                        q++;
                    }
                } else {
                    //Log.d("wow", "dey he go");
                    startActivityForResult(intent, fromStoryEntry);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == fromStoryEntry) {
            Log.d("resultCode:" + resultCode, "" + Activity.RESULT_OK);
            if(resultCode == Activity.RESULT_OK){
                finish();
                startActivity(getIntent());
            }
        }
    }
}