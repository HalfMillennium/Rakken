package com.coincalc.anduril.rakken;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.coincalc.anduril.rakon.R;

public class ViewStories extends AppCompatActivity {

    private static final String TAG = "ViewStories";

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    public ViewPager mViewPager = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(MainActivity.backable_b)
                        setViewPager(0);
                    //AllStoryFrag.clearList();
                    return true;
                case R.id.navigation_dashboard:
                    if(MainActivity.backable_a)
                        setViewPager(1);
                    //YourStoryFrag.clearList();
                    return true;
                case R.id.navigation_search:
                    AllStoryFrag.clearList();
                    setViewPager(2);
                    return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stories);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        //setup the pager
        setupViewPager(mViewPager);
        setViewPager(0);

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllStoryFrag(), "AllStories");
        adapter.addFragment(new YourStoryFrag(), "YourStories");
        adapter.addFragment(new SearchFrag(), "Search");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }

    public static Intent makeIntent(Context context)
    {
        return new Intent(context, ViewStories.class);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.backable_a && MainActivity.backable_b)
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume()
    {
        mSectionsStatePagerAdapter.notifyDataSetChanged();
        setupViewPager(mViewPager);
        setViewPager(0);
        super.onResume();
    }
}