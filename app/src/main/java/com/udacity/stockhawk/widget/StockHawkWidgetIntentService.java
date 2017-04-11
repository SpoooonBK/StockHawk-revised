package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.test.ServiceTestCase;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.util.Set;

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

        Context context = getBaseContext();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent remoteViewsintent = new Intent(context, RemoteViewsService.class);
        views.setRemoteAdapter(R.id.widget_gridview, remoteViewsintent);


        ContentResolver contentResolver = getContentResolver();
        Set<String> stockSet = PrefUtils.getStocks(getBaseContext());


        for(String stock: stockSet){


            Cursor cursor =contentResolver.query(
                    Contract.Quote.makeUriForStock(stock),
                    null,
                    stock,
                    null,
                    null);
            Timber.v("Cursor retrieved: ");
            String[] cols = cursor.getColumnNames();
            for(String column: cols){
                int index = cursor.getColumnIndex(column);

            }
            cursor.close();
        }

    }
}
