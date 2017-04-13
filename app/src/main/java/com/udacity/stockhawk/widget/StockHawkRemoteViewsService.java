package com.udacity.stockhawk.widget;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.model.StockData;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by spoooon on 4/6/17.
 */

public class StockHawkRemoteViewsService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {

        Timber.v("On GET VIEW FACTORY");

        return new RemoteViewsFactory() {


            private Cursor data = null;
            private DecimalFormat dollarFormatWithPlus;
            private DecimalFormat dollarFormat;
            private DecimalFormat percentageFormat;




            @Override
            public void onCreate() {

                Timber.v("FACTORY ON CREATE");


            }

            @Override
            public void onDataSetChanged() {

                if (data != null) {
                    data.close();
                }


                Timber.v("FACTORY ON DATA SET CHANGED");
                final long identityToken = Binder.clearCallingIdentity();



                data = getContentResolver().query(
                        Contract.Quote.URI,
                        null,
                        null,
                        null,
                        null
                );

                Binder.restoreCallingIdentity(identityToken);


            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");

                String symbol = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));


                float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
                float rawPrice = data.getFloat(Contract.Quote.POSITION_PRICE);


                String price = dollarFormat.format(rawPrice);
                String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                String percentage = percentageFormat.format(percentageChange / 100);


                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_item);
                views.setTextViewText(R.id.widget_symbol, symbol);
                views.setTextViewText(R.id.widget_current_price, price);

                if (PrefUtils.getDisplayMode(getApplicationContext())
                        .equals(getApplicationContext().getString(R.string.pref_display_mode_absolute_key))) {
                    views.setTextViewText(R.id.widget_price_change, change);

                } else {
                    views.setTextViewText(R.id.widget_price_change, percentage);
                }



                if (rawAbsoluteChange > 0) {

                    views.setTextColor(R.id.widget_price_change, getResources().getColor(R.color.material_green_700));
                } else {
                    views.setTextColor(R.id.widget_price_change, getResources().getColor(R.color.material_red_700));
                }



                return views;
            }

            @Override
            public RemoteViews getLoadingView() {

                Timber.v("FACTORY getLoadingView");return null;
            }

            @Override
            public int getViewTypeCount() {
                Timber.v("FACTORY getViewTypeCount");return 1;
            }

            @Override
            public long getItemId(int position) {
                Timber.v("getItemId");return position;
            }

            @Override
            public boolean hasStableIds() {

                Timber.v("hasStableIds");return false;
            }
        };
    }

}


