package com.udacity.stockhawk.widget;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.model.StockData;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by spoooon on 4/6/17.
 */

public class StockHawkRemoteViewsService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {

        Timber.v("On GET VIEW FACTORY");

        return new RemoteViewsFactory() {


            ArrayList<StockData> mStockData;


            @Override
            public void onCreate() {

                Timber.v("FACTORY ON CREATE");


            }

            @Override
            public void onDataSetChanged() {

                mStockData = intent.getParcelableArrayListExtra(StockHawkWidgetIntentService.STOCK_DATA);

                Timber.v("FACTORY ON DATA SET CHANGED");

                final long identityToken = Binder.clearCallingIdentity();
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return mStockData == null ? 0 : mStockData.size();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                StockData stockData = mStockData.get(position);

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_item);
                views.setTextViewText(R.id.widget_symbol, stockData.getSymbol());
                views.setTextViewText(R.id.widget_current_price, stockData.getPrice().toString());
                views.setTextViewText(R.id.widget_price_change, stockData.getPercentageChange().toString());



                return views;
            }

            @Override
            public RemoteViews getLoadingView() {

                Timber.v("FACTORY getLoadingView");return null;
            }

            @Override
            public int getViewTypeCount() {
                Timber.v("FACTORY getViewTypeCount");return 0;
            }

            @Override
            public long getItemId(int position) {

                Timber.v("getItemId");return 0;
            }

            @Override
            public boolean hasStableIds() {

                Timber.v("hasStableIds");return false;
            }
        };
    }

}


