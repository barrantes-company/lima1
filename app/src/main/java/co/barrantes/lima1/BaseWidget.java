package co.barrantes.lima1;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;

public abstract class BaseWidget extends AppWidgetProvider {
    public abstract String style();

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) {
            manager.updateAppWidget(id, WidgetBinder.bind(context, style(), manager.getAppWidgetOptions(id)));
        }
        if (Prefs.tempStale(context)) Weather.refresh(context, null);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager manager, int id, Bundle options) {
        manager.updateAppWidget(id, WidgetBinder.bind(context, style(), options));
    }

    public static void push(Context context) {
        push(context, EditorialWidget.class);
        push(context, InstrumentoWidget.class);
        push(context, SenalWidget.class);
        push(context, CartelWidget.class);
        push(context, CentroWidget.class);
    }

    private static void push(Context context, Class<? extends BaseWidget> type) {
        try {
            BaseWidget widget = type.getDeclaredConstructor().newInstance();
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, type));
            for (int id : ids) {
                manager.updateAppWidget(id, WidgetBinder.bind(context, widget.style(), manager.getAppWidgetOptions(id)));
            }
        } catch (Exception ignored) {
            /* a missing provider must not stop the others */
        }
    }
}
