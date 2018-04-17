package com.coincalc.anduril.rakon;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Anduril on 4/9/18.
 */

public class CustomAdapter extends SimpleAdapter
{
    LayoutInflater inflater;
    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public CustomAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.arrayList = data;
        inflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        if(ViewStories.userMark != null) {
            for(int count = 0; count < ViewStories.userMark.size(); count++) {
                if (position == ViewStories.userMark.get(count)) {
                    view.setBackgroundColor(Color.parseColor("#6B00A8"));
                }
            }
        }
        return view;
    }


}
