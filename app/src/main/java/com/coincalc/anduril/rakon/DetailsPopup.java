package com.coincalc.anduril.rakon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class DetailsPopup extends Activity {

    private TextView user, stamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8),(int) (height * .25));

        user = (TextView) findViewById(R.id.username);
        stamp = (TextView) findViewById(R.id.stamp);

        String username = getIntent().getExtras().get("username").toString();
        String time = getIntent().getExtras().get("time").toString();
        String date = getIntent().getExtras().get("date").toString();

        user.setText(username);
        stamp.setText(time + " · " + date);

    }

    public void close(View view)
    {
        finish();
    }

    public static Intent makeIntent(Context context)
    {
        return new Intent(context, DetailsPopup.class);
    }
}
