package com.udacity.stockhawk.model;

import com.github.mikephil.charting.data.Entry;
import com.udacity.stockhawk.utilities.DateConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to hold the stock history data and used by the GraphFragment to extract data and display on graph
 */

public class StockHistory {

    private static final String LOG_TAG = StockHistory.class.getSimpleName();


    private String mSymbol;

//    private List<Entry> mFullHistoryData = new ArrayList<>();
//    private Map<String, Date> mDateMap = new HashMap<>();
//    private List<String> mDateList = new ArrayList<>();
    private List<Entry> mLastHistoryEntries = new ArrayList<>();

    public StockHistory(String symbol) {
        this.mSymbol = symbol;
//        mFullHistoryData = parseHistoryString(history);
//        setDates();

    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        mSymbol = symbol;
    }



    public List<Entry> getLastHistoryEntries() {
        return mLastHistoryEntries;
    }


    public List<Entry> parseHistoryString(String history){

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

        }
        Collections.reverse(entries); //to put in chronological order
        mLastHistoryEntries = entries;
        return entries;
    }

//    private void setDates(){
//
//        for(Entry entry: mFullHistoryData){
//            long dateInMillis = (long) entry.getX();
//            String date = DateConverter.getMonthDateYearFormat(dateInMillis);
//            mDateMap.put(date, new Date(dateInMillis));
//            mDateList.add(date);
//        }
//    }




}
