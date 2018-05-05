package com.coincalc.anduril.rakon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.coincalc.anduril.rakon.R;
import com.coincalc.anduril.rakon.StoryEntries;
import com.coincalc.anduril.rakon.ViewStories;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User on 4/9/2017.
 */

public class AllStoryFrag extends Fragment {
    private static final String TAG = "AllStoryFrag";
    private TextView titleText;
    private ArrayList<String> titles, genres, dateUsers;
    private HashMap<String,String> item;
    private static SimpleAdapter sa;
    private static ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private int i = 0, q = 0;
    private static ListView storyList;
    private boolean contrib = false;
    public static boolean switched = false;
    public View row;
    private DataSnapshot retSnap;

    private View primView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_all_stories, container, false);
        Log.d(TAG, "onCreateView: started.");

        titles = new ArrayList<>();
        genres = new ArrayList<>();
        dateUsers = new ArrayList<>();

        buildList(null);

        primView = view;
        if(list.size() > 0)
            clearList();

        return view;
    }

    // filter --> the username of the current user
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
                sa = new SimpleAdapter(getActivity(), list,
                        R.layout.story_view,
                        new String[] { "line1","line2", "line3"},
                        new int[] {R.id.title, R.id.genre, R.id.dateUser});

                //Link the Adapter to the list
                storyList = (ListView)primView.findViewById(R.id.all_stories);

                storyList.setAdapter(sa);
                switched = true;

                storyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d("here","here");
                        Intent intent = StoryEntries.makeIntent(getContext());
                        intent.putExtra("storyName", titles.get(i));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "ERROR!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void clearList()
    {
        if(switched) {
            list.clear();
            sa.notifyDataSetChanged();
            storyList.setAdapter(sa);
        }
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