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

public class StockHawkRemoteViewsService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {



        return null;
    }

}


