package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.ui.MainActivity;

import timber.log.Timber;

/**
 * Created by spoooon on 4/6/17.
 */

public class StockHawkWidgetProvider extends AppWidgetProvider {

    boolean mIsUpdated = false;




    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Timber.v("onUpdate");



        for(int appWidgetId: appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);


            Intent launchAppIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchAppIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_holder, pendingIntent);

            views.setOnClickPendingIntent(R.id.widget_holder, pendingIntent);
            views.setEmptyView(R.id.widget_listview, R.id.widget_empty_view);
            views.setPendingIntentTemplate(R.id.widget_listview, pendingIntent);



            views.setRemoteAdapter(R.id.widget_listview, new Intent(context, StockHawkRemoteViewsService.class));
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Timber.v("Received intent:  " + intent.getAction());

        if(QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())){

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
        }


    }


}