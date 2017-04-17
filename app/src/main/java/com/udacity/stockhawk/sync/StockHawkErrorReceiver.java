package com.udacity.stockhawk.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.udacity.stockhawk.R;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by spoooon on 4/13/17.
 */

public class StockHawkErrorReceiver extends BroadcastReceiver {
    private static ArrayList<StockNotFoundErrorListener> mStockNotFoundErrorListeners;

    @Override
    public void onReceive(Context context, Intent intent) {


        if (QuoteSyncJob.ACTION_STOCK_NOT_FOUND.equals(intent.getAction())) {
            String symbol = intent.getStringExtra(QuoteSyncJob.STOCK_SYMBOL);

            String message = context.getString(R.string.not_a_stock) + ": " + symbol;
            notifyListeners();
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            Timber.v("Intent Received: " + symbol);
        }
    }


    public static void registerStockNotFoundErrorListener(StockNotFoundErrorListener listener) {
        if (mStockNotFoundErrorListeners == null) {
            mStockNotFoundErrorListeners = new ArrayList<>();
        }
        mStockNotFoundErrorListeners.add(listener);
    }

    public void notifyListeners() {
        if (mStockNotFoundErrorListeners != null) {
            for (StockNotFoundErrorListener listener : mStockNotFoundErrorListeners) {
                listener.onStockNotFoundError();
            }
        }

    }

}
