package co.barrantes.lima1;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.widget.RemoteViews;

public final class WidgetBinder {
    private WidgetBinder() {}

    public static RemoteViews bind(Context ctx, String style, boolean lock) {
        int layout = layout(style, lock);
        RemoteViews views = new RemoteViews(ctx.getPackageName(), layout);
        int accent = Palette.color(Prefs.palette(ctx, style));
        boolean withUnit = usesUnit(style, lock);
        String temp = Prefs.tempText(ctx, withUnit);
        Intent open = new Intent(ctx, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(
                ctx,
                (style + lock).hashCode(),
                open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.root, pending);
        if (hasDate(style, lock)) views.setTextViewText(R.id.date, Prefs.dateLabel());
        if (hasTemp(style, lock)) {
            views.setTextViewText(R.id.temp, temp);
            views.setTextColor(R.id.temp, accent);
        }
        if (hasUnit(style, lock)) views.setTextColor(R.id.unit, accent);
        if (hasSun(style, lock)) views.setInt(R.id.sun, "setColorFilter", accent);
        if (hasDots(style, lock)) {
            views.setInt(R.id.dot1, "setColorFilter", accent);
            views.setInt(R.id.dot2, "setColorFilter", accent);
        }
        if (hasDay(style, lock)) {
            views.setProgressBar(R.id.day, 100, Prefs.dayProgress(), false);
            if (Build.VERSION.SDK_INT >= 31) {
                views.setColorStateList(R.id.day, "setProgressTintList", ColorStateList.valueOf(accent));
                views.setColorStateList(R.id.day, "setProgressBackgroundTintList", ColorStateList.valueOf(0x22FFFFFF));
            }
        }
        return views;
    }

    private static int layout(String style, boolean lock) {
        if (lock) {
            return switch (style) {
                case "instrumento" -> R.layout.widget_instrumento_lock;
                case "senal" -> R.layout.widget_senal_lock;
                case "cartel" -> R.layout.widget_cartel_lock;
                default -> R.layout.widget_editorial_lock;
            };
        }
        return switch (style) {
            case "instrumento" -> R.layout.widget_instrumento;
            case "senal" -> R.layout.widget_senal;
            case "cartel" -> R.layout.widget_cartel;
            default -> R.layout.widget_editorial;
        };
    }

    private static boolean usesUnit(String style, boolean lock) {
        if ("editorial".equals(style) || "senal".equals(style)) return true;
        if ("instrumento".equals(style) && lock) return true;
        return false;
    }

    private static boolean hasDate(String style, boolean lock) {
        return !("cartel".equals(style) && lock);
    }

    private static boolean hasTemp(String style, boolean lock) {
        return !("senal".equals(style) && lock);
    }

    private static boolean hasUnit(String style, boolean lock) {
        return "cartel".equals(style) || ("instrumento".equals(style) && !lock);
    }

    private static boolean hasSun(String style, boolean lock) {
        if ("instrumento".equals(style)) return false;
        return !("senal".equals(style) && lock);
    }

    private static boolean hasDots(String style, boolean lock) {
        return !("cartel".equals(style) && !lock);
    }

    private static boolean hasDay(String style, boolean lock) {
        return "instrumento".equals(style);
    }
}
