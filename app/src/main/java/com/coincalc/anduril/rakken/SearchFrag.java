package com.coincalc.anduril.rakken;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.coincalc.anduril.rakon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Anduril on 4/29/18.
 */

public class SearchFrag extends Fragment {
    private static final String TAG = "SearchFrag";
    private String query;
    private EditText searchBar;
    private HashMap<String,String> item;
    private ArrayList<String> titles, genres, dateUsers;
    private SimpleAdapter sa;
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private ListView resultsView;
    private ImageView send;
    private int i = 0;

    private String username;
    private Intent intent;

    private View primView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.fragment_search, container, false);
        Log.d(TAG, "onCreateView: started.");

        primView = view;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ""))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username = dataSnapshot.getValue(String.class);
                        intent = StoryEntries.makeIntent(getContext());
                        intent.putExtra("username", username);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        send = (ImageView) view.findViewById(R.id.search_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(view);
            }
        });
        return view;
    }

    public void search(View view)
    {
        titles = new ArrayList<>();
        genres = new ArrayList<>();
        dateUsers = new ArrayList<>();

        searchBar = (EditText) primView.findViewById(R.id.search_bar1);
        if(!searchBar.getText().toString().replaceAll(" ", "").equals("")) {
            query = searchBar.getText().toString();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("stories").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot shot : dataSnapshot.getChildren()) {
                            Log.d("titles?", shot.getKey());
                            if (shot.getKey().replaceAll(" ", "").toLowerCase().contains(query.replaceAll(" ", "").toLowerCase())) {
                                i++;
                                titles.add(shot.getKey());
                                genres.add(shot.child("genre").getValue(String.class));
                                dateUsers.add(shot.child("user").getValue(String.class) + " | " + shot.child("created").getValue(String.class));
                                Log.d("i", "" + i);
                            }
                        }

                        if (list.size() != 0)
                            clearList();

                        //Load the data
                        for (int l = 0; l < i; l++) {
                            item = new HashMap<String, String>();
                            item.put("line1", titles.get(l));
                            item.put("line2", genres.get(l).toUpperCase());
                            item.put("line3", dateUsers.get(l));
                            list.add(item);
                        }

                        i = 0;

                        //Use an Adapter to link data to Views
                        sa = new SimpleAdapter(getActivity(), list,
                                R.layout.results_view,
                                new String[]{"line1", "line2", "line3"},
                                new int[]{R.id.title, R.id.genre, R.id.dateUser});

                        resultsView = (ListView) primView.findViewById(R.id.results);
                        resultsView.setAdapter(sa);

                        resultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
    }

    public void clearList()
    {
        list.clear();
        sa.notifyDataSetChanged();
        resultsView.setAdapter(sa);
    }
}
