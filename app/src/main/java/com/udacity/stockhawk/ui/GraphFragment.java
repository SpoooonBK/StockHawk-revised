package com.udacity.stockhawk.ui;

import android.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.model.StockHistory;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.utilities.DateRangeFactory;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by spoooon on 3/23/17.
 */

public class GraphFragment extends Fragment implements AdapterView.OnItemSelectedListener{


    private StockHistory mStockHistory;
    private LineChart mLineChart;
    private TextView mTextViewSymbolHeader;
    private Spinner mSpinner;
    private TextView mTextViewPercentageChange;
    private TextView mTextViewStartValue;
    private TextView mTextViewEndValue;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        mLineChart = (LineChart) rootView.findViewById(R.id.line_chart);
        mTextViewSymbolHeader = (TextView) rootView.findViewById(R.id.text_header_stock_symbol);


        mSpinner = (Spinner) rootView.findViewById(R.id.graph_spinner);
        mSpinner.setOnItemSelectedListener(this);

        mTextViewPercentageChange = (TextView) rootView.findViewById(R.id.text_percentage_change);
        mTextViewStartValue = (TextView) rootView.findViewById(R.id.text_start_value);
        mTextViewEndValue = (TextView) rootView.findViewById(R.id.text_end_value);


        return rootView;
    }

    //Sets the stockhistory, spinner and textView text
    public void buildDisplay(String symbol) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        String[] projection = {Contract.Quote.COLUMN_HISTORY};


        Cursor cursor = contentResolver.query(
                Contract.Quote.makeUriForStock(symbol),
                projection,
                null,
                null,
                null);

        cursor.moveToFirst();
        String history = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
        mStockHistory = new StockHistory(symbol, history);
        setSpinner();
        mTextViewSymbolHeader.setText(symbol);
    }

    public void updateGraph(final String symbol, @Nullable String dateString){

        Observable<String> observable = null;


        if(dateString != null){
            Date date = mStockHistory.getDateFromDateString(dateString);
            observable = QuoteSyncJob.getHistoryStringObservable(symbol, DateRangeFactory.getDateRange(dateString, date));
        } else {
            observable = QuoteSyncJob.getHistoryStringObservable(symbol, DateRangeFactory.getDateRange(dateString, null));
        }







        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String history) {

                        List<Entry> entries = mStockHistory.parseHistoryString(history);
                        LineDataSet lineDataSet = new LineDataSet(entries, symbol);
                        lineDataSet.setColor(Color.BLACK);
                        LineData lineData = new LineData(lineDataSet);
                        mLineChart.setData(lineData);
                        mLineChart.getXAxis().setValueFormatter(new GraphValueFormatter());
                        mLineChart.setBackgroundColor(Color.WHITE);
                        mLineChart.invalidate();

                        Float startValue = entries.get(0).getY();
                        Float endValue = entries.get(entries.size()-1).getY();


                        mTextViewStartValue.setText("$" + startValue.toString());
                        mTextViewEndValue.setText("$" + endValue.toString());

                        if(endValue < startValue){
                            mTextViewEndValue.setBackground(getResources().getDrawable(R.drawable.percent_change_pill_red));
                            mTextViewPercentageChange.setBackground(getResources().getDrawable(R.drawable.percent_change_pill_red));
                        }else if (endValue > startValue){
                                mTextViewEndValue.setBackground(getResources().getDrawable(R.drawable.percent_change_pill_green));
                                mTextViewPercentageChange.setBackground(getResources().getDrawable(R.drawable.percent_change_pill_green));
                        }

                        setPercentageChange(startValue,endValue);
                    }



                });





    }

    private void setPercentageChange(float startValue, float endValue) {


        double percentageChange;

        double gain = endValue - startValue;
        percentageChange= (gain/startValue)*100;
        double round = (double) Math.round(percentageChange *100)/100;

        mTextViewPercentageChange.setText(round + "%");

    }

    //The spinner will show the week in which data is available
    private void setSpinner(){


        String[] dates = mStockHistory.getHistoryDateArray();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, dates);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateGraph(mStockHistory.getSymbol(), parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
