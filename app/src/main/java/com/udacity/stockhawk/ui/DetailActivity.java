package com.udacity.stockhawk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Spinner;

import com.udacity.stockhawk.R;

public class DetailActivity extends AppCompatActivity {

    public static final String SYMBOL = "symbol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);


    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_activity_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) menuItem.getActionView();

        String stockSymbol = getIntent().getStringExtra(SYMBOL);

        GraphFragment graphFragment = (GraphFragment) getFragmentManager().findFragmentById(R.id.fragment_graph);
        graphFragment.setSymbol(stockSymbol);
        graphFragment.setSpinner(spinner);

        getSupportActionBar().setTitle(stockSymbol);
        graphFragment.buildDisplay(stockSymbol);

        return true;
    }
}