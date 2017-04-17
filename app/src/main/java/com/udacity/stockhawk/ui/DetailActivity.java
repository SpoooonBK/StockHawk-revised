package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

public class DetailActivity extends AppCompatActivity {

    //Keys to get Intent Extras
    public static final String SYMBOL = "symbol";
    private static final String DETAIL_ACTIVITY_TAG = "detail_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            GraphFragment graphFragment = new GraphFragment();
            graphFragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_placeholder, graphFragment, DETAIL_ACTIVITY_TAG)
                    .show(graphFragment)
                    .commit();
        }
    }
}