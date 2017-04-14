package com.udacity.stockhawk.sync;

import yahoofinance.Stock;

/**
 * Created by spoooon on 4/13/17.
 */

class StockNotFoundException extends Throwable {
    private String mSymbol;

    StockNotFoundException(String symbol){
        mSymbol = symbol;
    }

    public String getSymbol() {
        return mSymbol;
    }
}
