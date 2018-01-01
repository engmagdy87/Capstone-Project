package com.mm.plume.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.mm.plume.LoginActivity;
import com.mm.plume.R;

import static com.mm.plume.widget.PlumeWidgetService.FROM_ACTIVITY_FAV_LIST;

/**
 * Implementation of App Widget functionality.
 */
public class PlumeWidgetProvider extends AppWidgetProvider {

    int favList;
    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = widgetManager.getAppWidgetIds(new ComponentName(context, PlumeWidgetProvider.class));

        final String action = intent.getAction();
        if (action.equals("android.appwidget.action.APPWIDGET_UPDATE_FAVLIST")) {
            favList = intent.getExtras().getInt(FROM_ACTIVITY_FAV_LIST);
            widgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.appwidget_text);
            PlumeWidgetProvider.updateFavListWidgets(context, widgetManager, widgetIds,favList);
            super.onReceive(context, intent);
        }

    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId,int favList) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plume_widget_provider);
        Intent intent = new Intent(context,LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);


        views.setOnClickPendingIntent(R.id.fv_books_wigdet,pendingIntent);
        views.setTextViewText(R.id.appwidget_text, favList+"");

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void updateFavListWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,int favList) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,favList);
        }
    }
}

