package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.test.ServiceTestCase;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.model.StockData;
import com.udacity.stockhawk.ui.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuotesData;

/**
 * Created by spoooon on 4/6/17.
 */

public class StockHawkWidgetIntentService extends IntentService {

    public static final String STOCK_DATA = "stock_data";


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

        Intent launchAppIntent = new Intent(getApplicationContext(), MainActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, launchAppIntent, 0);


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, StockHawkWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_gridview);

        Cursor cursor =getContentResolver().query(
                Contract.Quote.URI,
                null,
                null,
                null,
                null
        );

        if(cursor == null){
            return;
        }
        if(!cursor.moveToFirst()){
            cursor.close();
            return;
        }


        ArrayList<StockData> stockList = new ArrayList<>();

        while(cursor.moveToNext()){
            StockData stockData = new StockData(cursor.getString(Contract.Quote.POSITION_SYMBOL));
            stockData.setPrice(cursor.getDouble((Contract.Quote.POSITION_PRICE)));
            stockData.setAbsoluteChange(cursor.getDouble(Contract.Quote.POSITION_ABSOLUTE_CHANGE));
            stockList.add(stockData);
            Timber.v(stockData.getSymbol() + "received");
        }


        for(int appWidgetId: appWidgetIds){
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);
            Intent remoteViewsintent = new Intent(this, RemoteViewsService.class);
            remoteViewsintent.putParcelableArrayListExtra(STOCK_DATA,stockList);

            views.setOnClickPendingIntent(R.id.widget_holder, pendingIntent);
            views.setRemoteAdapter(R.id.widget_gridview, remoteViewsintent);
            views.setTextViewText(R.id.widget_symbol, stockList.get(0).getSymbol());
            views.setEmptyView(R.id.widget_gridview, R.id.widget_empty_view);
            views.setOnClickPendingIntent(R.id.widget_holder, pendingIntent);


            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

        if(cursor != null){
            cursor.close();
        }


    }
}
