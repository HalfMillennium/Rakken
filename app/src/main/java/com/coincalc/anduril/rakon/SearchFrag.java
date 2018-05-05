package com.coincalc.anduril.rakon;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.coincalc.anduril.rakon.AllStoryFrag.switched;

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

    private View primView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.fragment_search, container, false);
        Log.d(TAG, "onCreateView: started.");

        primView = view;
        send = (ImageView) view.findViewById(R.id.search_button);
        /*
        send.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    send.setImageDrawable(getResources().getDrawable(R.drawable.send_white));
                    search(view);
                    Log.d("change", "white");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    send.setImageDrawable(getResources().getDrawable(R.drawable.send));
                    Log.d("return", "normal");
                }
                return true;
            }
        });*/
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
        query = searchBar.getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("stories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot shot : dataSnapshot.getChildren())
                    {
                        Log.d("titles?", shot.getKey());
                        if(shot.getKey().replaceAll(" ", "").contains(query.replaceAll(" ", "")))
                        {
                            i++;
                            titles.add(shot.getKey());
                            genres.add(shot.child("genre").getValue(String.class));
                            dateUsers.add(shot.child("user").getValue(String.class) + " | " + shot.child("created").getValue(String.class));
                            Log.d("i", "" + i);
                        }
                    }

                    if(list.size() != 0)
                        clearList();

                    //Load the data
                    for(int l = 0; l < i; l++){
                        item = new HashMap<String,String>();
                        item.put("line1", titles.get(l));
                        item.put("line2", genres.get(l).toUpperCase());
                        item.put("line3", dateUsers.get(l));
                        list.add(item);
                    }

                    i = 0;

                    //Use an Adapter to link data to Views
                    sa = new SimpleAdapter(getActivity(), list,
                            R.layout.results_view,
                            new String[] { "line1","line2", "line3"},
                            new int[] {R.id.title, R.id.genre, R.id.dateUser});

                    resultsView = (ListView) primView.findViewById(R.id.results);
                    resultsView.setAdapter(sa);

                    resultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = StoryEntries.makeIntent(getContext());
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

    public void clearList()
    {
        list.clear();
        sa.notifyDataSetChanged();
        resultsView.setAdapter(sa);
    }
}
