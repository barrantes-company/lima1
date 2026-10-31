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
    private WidgetBinder() {}

    private static final int SHORT = 0;
    private static final int NORMAL = 1;
    private static final int TALL = 2;

    public static RemoteViews bind(Context ctx, String style) {
        return fill(ctx, style, false, NORMAL);
    }

    public static RemoteViews bind(Context ctx, String style, Bundle options) {
        if (Build.VERSION.SDK_INT >= 31) {
            return new RemoteViews(Map.of(
                    new SizeF(110f, 100f), fill(ctx, style, true, SHORT),
                    new SizeF(220f, 100f), fill(ctx, style, false, SHORT),
                    new SizeF(110f, 130f), fill(ctx, style, true, NORMAL),
                    new SizeF(220f, 130f), fill(ctx, style, false, NORMAL)));
        }
        boolean narrow = true;
        int mode = NORMAL;
        if (options != null) {
            int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 220);
            int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 150);
            narrow = width < 220;
            mode = height < 125 ? SHORT : NORMAL;
        }
        return fill(ctx, style, narrow, mode);
    }

    private static RemoteViews fill(Context ctx, String style, boolean narrow, int mode) {
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
        if (!"ccartel".equals(style)) views.setTextViewText(R.id.date, isCompact(style) ? Prefs.shortDate() : Prefs.dateLabel());
        if ("senal".equals(style)) {
            views.setTextViewText(R.id.temp, Prefs.tempValue(ctx));
            views.setTextColor(R.id.temp, accent);
            views.setTextColor(R.id.unit, accent);
        } else if (!"csenal".equals(style)) {
            views.setTextViewText(R.id.temp, temp);
            views.setTextColor(R.id.temp, accent);
        }
        if (hasUnit(style)) views.setTextColor(R.id.unit, accent);
        if (hasSun(style)) {
            int code = Prefs.weatherCode(ctx);
            boolean day = Prefs.isDay(ctx);
            views.setImageViewResource(R.id.sun, WeatherIcon.drawable(code, day));
            views.setInt(R.id.sun, "setColorFilter", accent);
            views.setContentDescription(R.id.sun, WeatherIcon.label(code, day));
        }
        if (hasDots(style)) {
            views.setInt(R.id.dot1, "setColorFilter", accent);
            views.setInt(R.id.dot2, "setColorFilter", accent);
        }
        if ("instrumento".equals(style) || "cinstrumento".equals(style)) {
            views.setProgressBar(R.id.day, 100, Prefs.dayProgress(), false);
            if (Build.VERSION.SDK_INT >= 31) {
                views.setColorStateList(R.id.day, "setProgressTintList", ColorStateList.valueOf(accent));
                views.setColorStateList(R.id.day, "setProgressBackgroundTintList", ColorStateList.valueOf(0x22FFFFFF));
            }
        }
        if ("senal".equals(style) || "csenal".equals(style)) views.setInt(R.id.mark, "setBackgroundColor", accent);
        fit(ctx, views, style, narrow, mode);
        return views;
    }

    private static void fit(Context ctx, RemoteViews views, String style, boolean narrow, int mode) {
        float density = ctx.getResources().getDisplayMetrics().density;
        boolean compact = isCompact(style);
        int pad = Math.round(10 * density);
        views.setViewPadding(R.id.root, pad, pad, pad, pad);
        float hourSize = compact ? 56f : (narrow ? compactTime(style) : wideTime(style));
        float minuteSize = compact ? 26f : "centro".equals(style) ? hourSize * 0.4f : hourSize * 0.5f;
        views.setTextViewTextSize(R.id.hour, TypedValue.COMPLEX_UNIT_SP, hourSize);
        views.setTextViewTextSize(R.id.minute, TypedValue.COMPLEX_UNIT_SP, minuteSize);
        views.setTextColor(R.id.hour, 0xFFFFFFFF);
        views.setTextColor(R.id.minute, 0xFF8A8B84);
        if (hasDate(style)) views.setTextViewTextSize(R.id.date, TypedValue.COMPLEX_UNIT_SP, compact ? 13 : 12);
        if (hasTemp(style) && !"senal".equals(style)) {
            views.setTextViewTextSize(R.id.temp, TypedValue.COMPLEX_UNIT_SP, compact ? 13 : narrow ? 15 : 18);
        }
        if ("senal".equals(style)) {
            views.setTextViewTextSize(R.id.temp, TypedValue.COMPLEX_UNIT_SP, 15);
            views.setTextViewTextSize(R.id.unit, TypedValue.COMPLEX_UNIT_SP, 9);
        }
        boolean clockOnly = mode == SHORT;
        if (hasDate(style)) views.setViewVisibility(R.id.date, clockOnly ? View.GONE : View.VISIBLE);
        if (hasTemp(style)) views.setViewVisibility(R.id.temp, clockOnly ? View.GONE : View.VISIBLE);
        if (hasDay(style)) views.setViewVisibility(R.id.day, clockOnly ? View.GONE : View.VISIBLE);
        if (hasLogo(style)) views.setViewVisibility(R.id.logo, View.GONE);
        if (hasSun(style)) views.setViewVisibility(R.id.sun, clockOnly ? View.GONE : View.VISIBLE);
        if (hasUnitView(style)) views.setViewVisibility(R.id.unit, clockOnly ? View.GONE : View.VISIBLE);
    }

    private static float wideTime(String style) {
        if ("instrumento".equals(style)) return 52f;
        if ("cartel".equals(style)) return 52f;
        if ("centro".equals(style)) return 64f;
        if ("senal".equals(style)) return 48f;
        return 56f;
    }

    private static float compactTime(String style) {
        if ("instrumento".equals(style)) return 46f;
        if ("cartel".equals(style)) return 52f;
        if ("centro".equals(style)) return 78f;
        if ("senal".equals(style)) return 42f;
        return 56f;
    }

    private static int description(String style) {
        return switch (style) {
            case "cinstrumento" -> R.string.w_cinstrumento_desc;
            case "csenal" -> R.string.w_csenal_desc;
            case "ccartel" -> R.string.w_ccartel_desc;
            case "ccentro" -> R.string.w_centro_desc;
            case "ceditorial" -> R.string.w_editorial_desc;
            case "instrumento" -> R.string.w_instrumento_desc;
            case "senal" -> R.string.w_senal_desc;
            case "cartel" -> R.string.w_cartel_desc;
            case "centro" -> R.string.w_centro_desc;
            default -> R.string.w_editorial_desc;
        };
    }

    private static int layout(String style) {
        return switch (style) {
            case "cinstrumento" -> R.layout.widget_c_instrumento;
            case "csenal" -> R.layout.widget_c_senal;
            case "ccartel" -> R.layout.widget_c_cartel;
            case "ccentro" -> R.layout.widget_c_centro;
            case "ceditorial" -> R.layout.widget_c_editorial;
            case "instrumento" -> R.layout.widget_instrumento;
            case "senal" -> R.layout.widget_senal;
            case "cartel" -> R.layout.widget_cartel;
            case "centro" -> R.layout.widget_centro;
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
        return !isCompact(style);
    }

    private static boolean hasDots(String style) {
        return "ceditorial".equals(style) || "cinstrumento".equals(style) || "editorial".equals(style) || "instrumento".equals(style);
    }

    private static boolean hasDate(String style) {
        return !"ccartel".equals(style);
    }

    private static boolean hasTemp(String style) {
        return !"csenal".equals(style);
    }

    private static boolean hasDay(String style) {
        return "instrumento".equals(style) || "cinstrumento".equals(style);
    }

    private static boolean hasLogo(String style) {
        return !isCompact(style);
    }

    private static boolean hasUnitView(String style) {
        return "senal".equals(style) || "cartel".equals(style) || "instrumento".equals(style);
    }

    private static boolean isCompact(String style) {
        return style.startsWith("c") && !"cartel".equals(style) && !"centro".equals(style);
    }
}
