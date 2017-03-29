package com.udacity.stockhawk.ui;

import android.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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




        Timber.v("Fragment inflated");


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

        Date date = mStockHistory.getDateFromDateString(dateString);


        Observable<String> observable = QuoteSyncJob.getHistoryStringObservable(symbol, date);

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
//                        List<Entry> entries = new ArrayList<Entry>();
//                        entries.add( new Entry(1F, 46.400002F));
//                        entries.add(new Entry(2F, 46.400002F));
//                        entries.add(new Entry(3F, 46.599998F));
//                        entries.add(new Entry(4F, 46.060001F));
//                        entries.add(new Entry(5F, 45.77F));

                        LineDataSet lineDataSet = new LineDataSet(entries, symbol);
                        lineDataSet.setColor(Color.BLACK);
                        LineData lineData = new LineData(lineDataSet);
                        mLineChart.setData(lineData);
                        mLineChart.getXAxis().setValueFormatter(new GraphValueFormatter());
                        mLineChart.setBackgroundColor(Color.WHITE);
                        mLineChart.invalidate();
                    }

                });





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