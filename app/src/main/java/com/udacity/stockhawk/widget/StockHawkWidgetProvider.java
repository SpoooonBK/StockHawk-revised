package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteIntentService;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;

import timber.log.Timber;

/**
 * Created by spoooon on 4/6/17.
 */

public class StockHawkWidgetProvider extends AppWidgetProvider {




    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);





//        for(int appWidgetId : appWidgetIds )
//        {
//
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_gridview);
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
//            views.setOnClickPendingIntent(R.id.widget_holder, pendingIntent);
//
//            views.setRemoteAdapter(R.id.widget_gridview, new Intent(context, StockHawkRemoteViewsService.class));
//
//
//            views.setEmptyView(R.id.widget_gridview, R.id.widget_empty_view);
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//
//        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Timber.v("Received intent:  " + intent.getAction());




        if(QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())){


            context.startService(new Intent(context, StockHawkWidgetIntentService.class));
        }
    }

}