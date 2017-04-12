package com.udacity.stockhawk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to hold the stock history data and used by the GraphFragment to extract data and display on graph
 */

public class StockData implements Parcelable {

    private static final String LOG_TAG = StockData.class.getSimpleName();


    private String mSymbol;
    private String mHistory;
    private Double mPrice;
    private Double mAbsoluteChange;
    private Double mPercentageChange;

    private List<Entry> mLastHistoryEntries;

    public StockData(String symbol) {
        this.mSymbol = symbol;
    }

    public StockData(String symbol, String history){
        mSymbol = symbol;
        parseHistoryString(history);
    }

    public boolean hasEntries(){
        if(mLastHistoryEntries != null){
            return true;
        }else return false;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        mSymbol = symbol;
    }

    public String getHistory() {
        return mHistory;
    }

    public List<Entry> getLastHistoryEntries() {
        return mLastHistoryEntries;
    }

    public void setHistory(String history) {
        mHistory = history;
    }

    public Double getPrice() {
        return mPrice;
    }

    public void setPrice(Double price) {
        mPrice = price;
    }

    public Double getAbsoluteChange() {
        return mAbsoluteChange;
    }

    public void setAbsoluteChange(Double absoluteChange) {
        mAbsoluteChange = absoluteChange;
    }

    public Double getPercentageChange() {
        return mPercentageChange;
    }

    public void setPercentageChange(Double percentageChange) {
        mPercentageChange = percentageChange;
    }

    public void setLastHistoryEntries(List<Entry> lastHistoryEntries) {
        mLastHistoryEntries = lastHistoryEntries;
    }

    public List<Entry> parseHistoryString(String history){

        mHistory = history;

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSymbol);
        dest.writeString(this.mHistory);
        dest.writeValue(this.mPrice);
        dest.writeValue(this.mAbsoluteChange);
        dest.writeValue(this.mPercentageChange);
    }

    protected StockData(Parcel in) {
        this.mSymbol = in.readString();
        this.mHistory = in.readString();
        this.mPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.mAbsoluteChange = (Double) in.readValue(Double.class.getClassLoader());
        this.mPercentageChange = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<StockData> CREATOR = new Parcelable.Creator<StockData>() {
        @Override
        public StockData createFromParcel(Parcel source) {
            return new StockData(source);
        }

        @Override
        public StockData[] newArray(int size) {
            return new StockData[size];
        }
    };
}
