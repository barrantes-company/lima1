package co.barrantes.lima1;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.SizeF;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Map;

public final class WidgetBinder {
    static final int NARROW = 0;
    static final int MEDIUM = 1;
    static final int WIDE = 2;

    private WidgetBinder() {}

    public static RemoteViews bind(Context ctx, String style) {
        return fill(ctx, style, WIDE);
    }

    public static RemoteViews bind(Context ctx, String style, Bundle options) {
        if (Build.VERSION.SDK_INT >= 31) {
            return new RemoteViews(Map.of(
                    new SizeF(180f, 80f), fill(ctx, style, NARROW),
                    new SizeF(250f, 110f), fill(ctx, style, MEDIUM),
                    new SizeF(320f, 140f), fill(ctx, style, WIDE)));
        }
        return fill(ctx, style, sizeOf(options));
    }

    static int sizeOf(Bundle options) {
        if (options == null) return WIDE;
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 320);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 140);
        if (width < 200 || height < 90) return NARROW;
        if (width < 280 || height < 120) return MEDIUM;
        return WIDE;
    }

    private static RemoteViews fill(Context ctx, String style, int size) {
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
        views.setContentDescription(R.id.root, ctx.getString(description(style)));
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
        fit(ctx, views, style, size);
        return views;
    }

    private static void fit(Context ctx, RemoteViews views, String style, int size) {
        float density = ctx.getResources().getDisplayMetrics().density;
        int pad = Math.round((size == NARROW ? 8 : size == MEDIUM ? 10 : 14) * density);
        views.setViewPadding(R.id.root, pad, pad, pad, pad);
        float time = timeSize(style, size);
        views.setTextViewTextSize(R.id.hour, TypedValue.COMPLEX_UNIT_SP, time);
        views.setTextViewTextSize(R.id.minute, TypedValue.COMPLEX_UNIT_SP, time);
        views.setTextViewTextSize(R.id.temp, TypedValue.COMPLEX_UNIT_SP, size == NARROW ? 16 : size == MEDIUM ? 18 : 22);
        views.setTextViewTextSize(R.id.date, TypedValue.COMPLEX_UNIT_SP, size == WIDE ? 14 : 12);
        views.setViewVisibility(R.id.logo, hideLogo(style, size) ? View.GONE : View.VISIBLE);
        views.setViewVisibility(R.id.date, hideDate(style, size) ? View.GONE : View.VISIBLE);
    }

    private static float timeSize(String style, int size) {
        float wide = "instrumento".equals(style) ? 40f : "cartel".equals(style) ? 46f : 48f;
        if (size == WIDE) return wide;
        if (size == MEDIUM) return wide * 0.82f;
        return wide * 0.66f;
    }

    private static boolean hideLogo(String style, int size) {
        return size == NARROW && ("editorial".equals(style) || "instrumento".equals(style));
    }

    private static boolean hideDate(String style, int size) {
        return size == NARROW && ("senal".equals(style) || "cartel".equals(style));
    }

    private static int description(String style) {
        return switch (style) {
            case "instrumento" -> R.string.w_instrumento_desc;
            case "senal" -> R.string.w_senal_desc;
            case "cartel" -> R.string.w_cartel_desc;
            default -> R.string.w_editorial_desc;
        };
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
