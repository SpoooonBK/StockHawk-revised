package com.udacity.stockhawk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.stockhawk.R;

public class DetailActivity extends AppCompatActivity {

    public static final String SYMBOL = "symbol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        GraphFragment graphFragment = (GraphFragment) getFragmentManager().findFragmentById(R.id.fragment_graph);
        graphFragment.buildDisplay(getIntent().getStringExtra(SYMBOL));
    }
}
