package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.model.StockData;
import com.udacity.stockhawk.ui.MainActivity;

import java.util.ArrayList;

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



        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, StockHawkWidgetProvider.class));


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
        }

        if(cursor != null){
            cursor.close();
        }




        for(int appWidgetId: appWidgetIds){

            Intent launchAppIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, launchAppIntent, 0);

            RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widget_holder, pendingIntent);
            views.setEmptyView(R.id.widget_listview, R.id.widget_empty_view);
            views.setOnClickPendingIntent(R.id.widget_holder, pendingIntent);
            Intent updateIntent = new Intent(this, RemoteViewsService.class);
            updateIntent.putParcelableArrayListExtra(STOCK_DATA,stockList);
            views.setRemoteAdapter(R.id.widget_listview, updateIntent);
            views.setTextViewText(R.id.widget_symbol, stockList.get(0).getSymbol());


            appWidgetManager.updateAppWidget(appWidgetId, views);


        }

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);


















    }
}
