package com.udacity.stockhawk.widget;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.data.Contract;

/**
 * Created by spoooon on 4/6/17.
 */

public class StockHawkRemoteViewsService extends RemoteViewsService implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {


        return null;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }
}


