package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import timber.log.Timber;

/**
 * Created by spoooon on 4/6/17.
 */

public class StockHawkWidgetIntentService extends IntentService {


    public StockHawkWidgetIntentService(){
        super("Widget Intent Service");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public StockHawkWidgetIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.v("Widget Intent Service called");

    }
}
