package com.udacity.stockhawk.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.model.StockData;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.utilities.DateManager;
import com.udacity.stockhawk.utilities.DateRange;
import com.udacity.stockhawk.utilities.DateRangeFactory;
import com.udacity.stockhawk.utilities.SpinnerPositionManager;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by spoooon on 3/23/17.
 */

public class GraphFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {


    private static final String SYMBOL = "symbol";
    private StockData mStockHistory;
    private LineChart mLineChart;
    private Spinner mSpinner;
    private TextView mTextViewPercentageChange;
    private TextView mTextViewStartValue;
    private TextView mTextViewEndValue;
    private ProgressDialog mProgress;
    private Bundle mSavedInstanceState;
    private String mSymbol;
    private final String SPINNER_POSITION = "spinner_item";
    private final String HISTORY = "history";
    private int mLastSpinnerItemSelected;
    private TextView mHighlightedDate;
    private TextView mHighlightedQuote;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        //Use saved history to build StockData instead of making an internet call on rotation
        if (savedInstanceState == null) {
            mSymbol = (getArguments().getString(DetailActivity.SYMBOL));
            mStockHistory = new StockData(mSymbol);
        } else {
            mSymbol = savedInstanceState.getString(SYMBOL);
            mLastSpinnerItemSelected = savedInstanceState.getInt(SPINNER_POSITION);
            mStockHistory = new StockData(savedInstanceState.getString(SYMBOL), savedInstanceState.getString(HISTORY));
            mSavedInstanceState = savedInstanceState;
        }


        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        mLineChart = (LineChart) rootView.findViewById(R.id.line_chart);


        mTextViewPercentageChange = (TextView) rootView.findViewById(R.id.text_percentage_change);
        mTextViewStartValue = (TextView) rootView.findViewById(R.id.text_start_value);
        mTextViewEndValue = (TextView) rootView.findViewById(R.id.text_end_value);
        mHighlightedDate = (TextView) rootView.findViewById(R.id.highlighted_date);
        mHighlightedQuote = (TextView) rootView.findViewById(R.id.highlighted_quote);


        return rootView;
    }


    public void updateGraph(final String symbol, final String dateString) {

        if (!networkUp()) {
            Toast.makeText(getActivity(), R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
            return;
        }

        Observable<String> observable = null;
        DateRangeFactory dateRangeFactory = new DateRangeFactory(getActivity());
        DateRange dateRange = null;


        if (dateString.startsWith("Q")) {   //get DateRange of the quarter if chosen
            String quarter = dateString.substring(0, 2);
            dateRange = dateRangeFactory.getDateRange(quarter);
        } else {
            dateRange = dateRangeFactory.getDateRange(dateString);
        }

        if (mSavedInstanceState != null && SpinnerPositionManager.isInOriginalPosition(mSpinner.getSelectedItemPosition())) {
            populateGraph(symbol, dateString, mStockHistory.getHistory());

        } else {
            observable = QuoteSyncJob.getHistoryStringObservable(symbol, dateRange);

            Subscription subscription = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {


                            mSavedInstanceState = null; //nulls Bundle so that progress dialog will show again
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(String history) {

                            populateGraph(symbol, dateString, history);

                        }
                    });
        }
    }

    private void populateGraph(String symbol, String dateString, String history) {

        List<Entry> entries = mStockHistory.parseHistoryString(history);

        LineDataSet lineDataSet = new LineDataSet(entries, symbol);
        lineDataSet.setColor(Color.BLACK);
        LineData lineData = new LineData(lineDataSet);
        mLineChart.setData(lineData);
        mLineChart.getXAxis().setValueFormatter(new GraphValueFormatter());
        Description description = new Description();
        description.setText(dateString);
        mLineChart.setDescription(description);
        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.setOnChartValueSelectedListener(this);
        mLineChart.invalidate();

        Float startValue = entries.get(0).getY();
        Float endValue = entries.get(entries.size() - 1).getY();


        mTextViewStartValue.setText("$" + startValue.toString());
        mTextViewEndValue.setText("$" + endValue.toString());

        if (endValue < startValue) {
            mTextViewEndValue.setBackground(getResources().getDrawable(R.drawable.percent_change_pill_red));
            mTextViewPercentageChange.setBackground(getResources().getDrawable(R.drawable.percent_change_pill_red));
        } else if (endValue > startValue) {
            mTextViewEndValue.setBackground(getResources().getDrawable(R.drawable.percent_change_pill_green));
            mTextViewPercentageChange.setBackground(getResources().getDrawable(R.drawable.percent_change_pill_green));
        }

        setPercentageChange(startValue, endValue);
        toggleProgress();

    }


    //Shows the percentage change from the start value and end value during the date range for the selected stock
    private void setPercentageChange(float startValue, float endValue) {


        double percentageChange;

        double gain = endValue - startValue;
        percentageChange = (gain / startValue) * 100;
        double round = (double) Math.round(percentageChange * 100) / 100;

        mTextViewPercentageChange.setText(round + "%");

    }


    //MENU ITEM METHODS
//
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_activity_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.spinner);
        mSpinner = (Spinner) menuItem.getActionView();
        ((DetailActivity) getActivity()).getSupportActionBar().setTitle(mSymbol);
        populateSpinner();

    }


    //The spinner will display date range choices
    private void populateSpinner() {


        List<String> dateList = new ArrayList<>();
        dateList.add(getString(R.string.last_thirty_days));
        dateList.add(getString(R.string.last_seven_days));
        dateList.add(getString(R.string.year_to_date));
        dateList.addAll(DateManager.getAvailableQuarters());

        String[] dates = dateList.toArray(new String[dateList.size()]);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dates);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);
        mSpinner.setOnItemSelectedListener(this);

        //Resets spinner position to last position on rotation
        if (mSavedInstanceState != null) {
            mSpinner.setSelection(mSavedInstanceState.getInt(SPINNER_POSITION));
        }

    }


    //When Item is selected from options spinner the graph will update with the selected date range
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        toggleProgress();
        SpinnerPositionManager.setNewSpinnerPosition(position);
        updateGraph(mStockHistory.getSymbol(), parent.getItemAtPosition(position).toString());
    }

    private void toggleProgress() {
        if (mProgress == null) {
            mProgress = new ProgressDialog(getActivity());
            mProgress.setMessage(getString(R.string.getting_quotes));
            mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgress.setIndeterminate(true);
        }

        if (mProgress.isShowing()) {
            mProgress.hide();
        } else mProgress.show();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putInt(SPINNER_POSITION, mSpinner.getSelectedItemPosition());
        outState.putString(HISTORY, mStockHistory.getHistory());
        outState.putString(SYMBOL, mSymbol);

    }


    @Override
    public void onStop() {
        super.onStop();
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }

    //Shows highlighted data from graph
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        mHighlightedDate.setText(DateManager.getMonthDateYearFormat((long) h.getX()));
        mHighlightedQuote.setText(((Float) h.getY()).toString());

    }

    @Override
    public void onNothingSelected() {

    }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
