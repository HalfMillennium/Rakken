package com.coincalc.anduril.rakon;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Search extends AppCompatActivity {

    private String query;
    private EditText searchBar;
    private HashMap<String,String> item;
    private ArrayList<String> titles, genres, dateUsers;
    private SimpleAdapter sa;
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private ListView resultsView;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void search(View view)
    {
        searchBar = (EditText) findViewById(R.id.search_bar1);
            query = searchBar.getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("stories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot shot : dataSnapshot.getChildren())
                    {
                        if(shot.getKey().contains(query))
                        {
                            titles.add(shot.getKey());
                            genres.add(shot.child("genre").getValue(String.class));
                            dateUsers.add(shot.child("user").getValue(String.class) + " | " + shot.child("created").getValue(String.class));
                            i++;
                        }
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
                    sa = new SimpleAdapter(Search.this, list,
                            R.layout.results_view,
                            new String[] { "line1","line2", "line3"},
                            new int[] {R.id.title, R.id.genre, R.id.dateUser});

                    resultsView = (ListView) findViewById(R.id.results);
                    resultsView.setAdapter(sa);

                    resultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = StoryEntries.makeIntent(Search.this);
                            intent.putExtra("storyName", titles.get(i));
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static Intent makeIntent(Context context)
    {
        return new Intent(context, Search.class);
    }
}
