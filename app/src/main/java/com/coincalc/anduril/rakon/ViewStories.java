package com.coincalc.anduril.rakon;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewStories extends AppCompatActivity {

    private TextView titleText;
    private ArrayList<String> titles, genres, dateUsers;
    private ArrayList<String> y_titles, y_genres, y_dateUsers;
    private HashMap<String,String> item;
    private SimpleAdapter sa;
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private int i = 0, q = 0;
    public static ArrayList<Integer> userMark;
    private ListView storyList;
    private boolean contrib = false;
    private boolean switchable = false;
    public View row;
    private DataSnapshot retSnap;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(switchable) {
                        buildList(null);
                        clearList();
                        titleText.setText(R.string.stories_text);
                    }
                    return true;
                case R.id.navigation_dashboard:
                    if(switchable) {
                        //FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                        buildList("TheFiveHundred");
                        clearList();
                        titleText.setText(R.string.ystories_text);
                        return true;
                    }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stories);

        titleText = (TextView) findViewById(R.id.titleText);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        titles = new ArrayList<>();
        genres = new ArrayList<>();
        dateUsers = new ArrayList<>();

        y_titles = new ArrayList<>();
        y_genres = new ArrayList<>();
        y_dateUsers = new ArrayList<>();

        userMark = new ArrayList<>();

        buildList(null);

        Log.d("contrib", "false");

    }

    public static Intent makeIntent(Context context)
    {
        return new Intent(context, ViewStories.class);
    }

    public void buildList(final String filter)
    {
        titles.clear();
        genres.clear();
        dateUsers.clear();
        i = 0;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("stories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(!(filter == null))
                    {
                        //(snapshot.child("user").getValue(String.class).equals(filter))
                        if(getContribs(filter, snapshot)) {
                            titles.add(snapshot.getKey());
                            genres.add(snapshot.child("genre").getValue(String.class));
                            dateUsers.add((snapshot.child("user").getValue(String.class)) + " | " + (snapshot.child("created").getValue(String.class)));
                            i++;
                        }
                    } else {
                        Log.d("title2", snapshot.getKey());
                        titles.add(snapshot.getKey());
                        genres.add(snapshot.child("genre").getValue(String.class));
                        dateUsers.add((snapshot.child("user").getValue(String.class)) + " | " + (snapshot.child("created").getValue(String.class)));
                        i++;
                    }

                    contrib = false;
                    Log.d("contrib", "false");
                }

                //Load the data
                for(int l = 0; l < i; l++){
                    item = new HashMap<String,String>();
                    item.put("line1", titles.get(l));
                    item.put("line2", genres.get(l).toUpperCase());
                    item.put("line3", dateUsers.get(l));
                    list.add(item);
                }

                //Use an Adapter to link data to Views
                sa = new SimpleAdapter(ViewStories.this, list,
                        R.layout.story_view,
                        new String[] { "line1","line2", "line3"},
                        new int[] {R.id.title, R.id.genre, R.id.dateUser});

                //Link the Adapter to the list
                storyList = (ListView)findViewById(R.id.all_stories);

                storyList.setAdapter(sa);
                switchable = true;

                storyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d("here","here");
                        Intent intent = StoryEntries.makeIntent(ViewStories.this);
                        intent.putExtra("storyName", titles.get(i));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewStories.this, "ERROR!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clearList()
    {
        list.clear();
        sa.notifyDataSetChanged();
        storyList.setAdapter(sa);
    }

    public boolean getContribs(final String user, final DataSnapshot snap)
    {

        String[] contr = snap.child("contribs").getValue(String.class).split(",");
        Log.d("check", contr[0]);
        for (int i = 0; i < contr.length; i++) {
            if (user.equals(contr[i])) {
                contrib = true;
                Log.d("contrib", "true");
            }
        }

        if(contrib) {
            Log.d("contrib", "is surely true");
            return true;
        }
        else {
            Log.d("contrib", "is surely false");
            return false;
        }

    }
}