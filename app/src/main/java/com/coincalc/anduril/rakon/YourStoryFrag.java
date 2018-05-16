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
import com.google.firebase.auth.FirebaseAuth;
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

public class YourStoryFrag extends Fragment {
    private static final String TAG = "YourStoryFrag";
    private TextView titleText;
    private ArrayList<String> titles, genres, dateUsers;
    private HashMap<String,String> item;
    private static SimpleAdapter sa;
    private static ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private int i = 0, q = 0;
    private static ListView storyList;
    private boolean contrib = false;
    private boolean switchable = false;
    public View row;
    private DataSnapshot retSnap;
    private Button startNew;

    Intent intent;

    private View primView;
    private String username;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_your_stories, container, false);
        Log.d(TAG, "onCreateView: started.");

        titles = new ArrayList<>();
        genres = new ArrayList<>();
        dateUsers = new ArrayList<>();

        startNew = (Button) view.findViewById(R.id.start_new);
        startNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewStory(view);
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ""))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username = dataSnapshot.getValue(String.class);
                        intent = StoryEntries.makeIntent(getContext());
                        intent.putExtra("username", username);
                        buildList(username);
                        //Toast.makeText(getActivity(), username, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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

                /*** temporary disable and reenable back button **/
                MainActivity.backable_a = false;

                sa = new SimpleAdapter(getActivity(), list,
                        R.layout.story_view,
                        new String[] { "line1","line2", "line3"},
                        new int[] {R.id.title, R.id.genre, R.id.dateUser});

                storyList = (ListView)primView.findViewById(R.id.your_stories);

                storyList.setAdapter(sa);
                switchable = true;

                storyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        intent.putExtra("storyName", titles.get(i));
                        startActivity(intent);
                    }
                });

                MainActivity.backable_a = true;
                /*** temporary disable and reenable back button **/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "ERROR!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void clearList()
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

    public void createNewStory(View view)
    {
        Intent intent = CreateNew.makeIntent(getActivity());
        startActivity(intent);
    }
}