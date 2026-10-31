package co.barrantes.lima1;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class Prefs {
    private static final String FILE = "lima1";
    private static final int EMPTY = -999;

    private Prefs() {}

    private static SharedPreferences sp(Context ctx) {
        return ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static String palette(Context ctx, String style) {
        return sp(ctx).getString("palette_" + style, Palette.LIMA);
    }

    public static void setPalette(Context ctx, String style, String palette) {
        sp(ctx).edit().putString("palette_" + style, palette).apply();
    }

    public static void setReading(Context ctx, int celsius, int code, boolean day) {
        sp(ctx).edit()
                .putInt("temp", celsius)
                .putInt("code", code)
                .putBoolean("day", day)
                .putLong("temp_at", System.currentTimeMillis())
                .apply();
    }

    public static boolean tempStale(Context ctx) {
        long at = sp(ctx).getLong("temp_at", 0L);
        return System.currentTimeMillis() - at > 20 * 60 * 1000L;
    }

    public static String tempText(Context ctx, boolean withUnit) {
        int value = sp(ctx).getInt("temp", EMPTY);
        if (value == EMPTY) return withUnit ? "—°C" : "—°";
        return withUnit ? value + "°C" : value + "°";
    }

    public static int weatherCode(Context ctx) {
        return sp(ctx).getInt("code", 0);
    }

    public static boolean isDay(Context ctx) {
        return sp(ctx).getBoolean("day", true);
    }

    public static String dateLabel() {
        String raw = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "PE")).format(new Date());
        if (raw.isEmpty()) return raw;
        return Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
    }

    public static int dayProgress() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        return Math.min(100, Math.max(0, minutes * 100 / 1440));
    }
}
