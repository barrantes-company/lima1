package co.barrantes.lima1;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.widget.RemoteViews;

public final class WidgetBinder {
    private WidgetBinder() {}

    public static RemoteViews bind(Context ctx, String style) {
        RemoteViews views = new RemoteViews(ctx.getPackageName(), layout(style));
        int accent = Palette.color(Prefs.palette(ctx, style));
        String temp = Prefs.tempText(ctx, usesUnit(style));
        Intent open = new Intent(ctx, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(
                ctx,
                style.hashCode(),
                open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.root, pending);
        views.setTextViewText(R.id.date, Prefs.dateLabel());
        views.setTextViewText(R.id.temp, temp);
        views.setTextColor(R.id.temp, accent);
        if (hasUnit(style)) views.setTextColor(R.id.unit, accent);
        if (hasSun(style)) views.setInt(R.id.sun, "setColorFilter", accent);
        if (hasDots(style)) {
            views.setInt(R.id.dot1, "setColorFilter", accent);
            views.setInt(R.id.dot2, "setColorFilter", accent);
        }
        if ("instrumento".equals(style)) {
            views.setProgressBar(R.id.day, 100, Prefs.dayProgress(), false);
            if (Build.VERSION.SDK_INT >= 31) {
                views.setColorStateList(R.id.day, "setProgressTintList", ColorStateList.valueOf(accent));
                views.setColorStateList(R.id.day, "setProgressBackgroundTintList", ColorStateList.valueOf(0x22FFFFFF));
            }
        }
        return views;
    }

    private static int layout(String style) {
        return switch (style) {
            case "instrumento" -> R.layout.widget_instrumento;
            case "senal" -> R.layout.widget_senal;
            case "cartel" -> R.layout.widget_cartel;
            default -> R.layout.widget_editorial;
        };
    }

    private static boolean usesUnit(String style) {
        return "editorial".equals(style) || "senal".equals(style);
    }

    private static boolean hasUnit(String style) {
        return "cartel".equals(style) || "instrumento".equals(style);
    }

    private static boolean hasSun(String style) {
        return !"instrumento".equals(style);
    }

    private static boolean hasDots(String style) {
        return !"cartel".equals(style);
    }
}
