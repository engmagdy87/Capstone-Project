package com.mm.plume.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by MM on 1/1/2018.
 */

public class PlumeWidgetService extends IntentService {

    public static String FROM_ACTIVITY_FAV_LIST = "FROM_ACTIVITY_FAV_LIST";

    public PlumeWidgetService() {
        super("PlumeWidgetService");
    }
    public static void startFavListService(Context context, int fromActivityFavList) {
        Intent intent = new Intent(context, PlumeWidgetService.class);
        intent.putExtra(FROM_ACTIVITY_FAV_LIST, fromActivityFavList);
        context.startService(intent);
    }
    private void handleUpdateWidgets(int favList) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE_FAVLIST");
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE_FAVLIST");
        intent.putExtra(FROM_ACTIVITY_FAV_LIST, favList);
        sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            int fromActivityFavoriteList = intent.getExtras().getInt(FROM_ACTIVITY_FAV_LIST);
            handleUpdateWidgets(fromActivityFavoriteList);
        }
    }
}
