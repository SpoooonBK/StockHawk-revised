package com.udacity.stockhawk.model;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.utilities.DateConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Used to hold the stock history data and used by the GraphFragment to extract data and display on graph
 */

public class StockHistory {

    private static final String LOG_TAG = StockHistory.class.getSimpleName();


    private String mSymbol;

    private List<Entry> mFullHistoryData = new ArrayList<>();
    private Map<String, Date> mDateMap = new HashMap<>();
    private List<String> mDateList = new ArrayList<>();
    private List<Entry> mWeeklyHistoryData = new ArrayList<>();

    public StockHistory(String symbol, String history) {
        this.mSymbol = symbol;
        mFullHistoryData = parseHistoryString(history);
        setDates();

    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        mSymbol = symbol;
    }

    public List<Entry> getFullHistoryEntries() {
        return mFullHistoryData;
    }

    public String[] getHistoryDateArray(){
        return  mDateList.toArray(new String[mDateList.size()]);
    }

    public Date getDateFromDateString(String dateString){
        return mDateMap.get(dateString);

    }



    public List<Entry> parseHistoryString(String history){

        Timber.v("Parsing history: " + history);

        List<Entry> entries = new ArrayList<>();

        //Splits the historyString into an array of strings by new line
        String[] lines = history.split("[\\r\\n]+");
        for(String line: lines){
            //Parses each line by locating the "," and extracting the data substrings
            //The date will be the x axis and the endPrice will be the y axis of the GraphFragment Graph
            String dateSubString= line.substring(0, line.indexOf(","));
            long dateInMillis = Long.parseLong(dateSubString);
            float closePrice = Float.parseFloat(line.substring(line.indexOf(",")+1));
            entries.add(new Entry(dateInMillis, closePrice));
//            Collections.sort(entries, new Comparator<Entry>() {
//                @Override
//                public int compare(Entry o1, Entry o2) {
//                    if(o1.getX()>o2.getX()){
//                        return 1;
//                    } else return -1;
//                }
//            });
            Collections.reverse(entries);
        }
        return entries;
    }

    private void setDates(){
        for(Entry entry: mFullHistoryData){
            long dateInMillis = (long) entry.getX();
            String date = DateConverter.getMonthDateYearFormat(dateInMillis);
            mDateMap.put(date, new Date(dateInMillis));
            mDateList.add(date);
        }
    }




    public List<Entry> getWeeklyHistoryData() {
        return mWeeklyHistoryData;
    }

}
