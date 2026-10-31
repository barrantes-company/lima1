package co.barrantes.lima1;

public final class WeatherIcon {
    private WeatherIcon() {}

    public static int drawable(int code, boolean day) {
        if (code == 0 || code == 1) return day ? R.drawable.ic_sun : R.drawable.ic_moon;
        if (code == 2 || code == 3) return R.drawable.ic_cloud;
        if (code == 45 || code == 48) return R.drawable.ic_fog;
        if (code >= 95) return R.drawable.ic_storm;
        if ((code >= 71 && code <= 77) || code == 85 || code == 86) return R.drawable.ic_snow;
        if (code >= 51) return R.drawable.ic_rain;
        return R.drawable.ic_cloud;
    }

    public static String label(int code, boolean day) {
        if (code == 0 || code == 1) return day ? "Cielo despejado" : "Noche despejada";
        if (code == 2 || code == 3) return "Nublado";
        if (code == 45 || code == 48) return "Niebla";
        if (code >= 95) return "Tormenta";
        if ((code >= 71 && code <= 77) || code == 85 || code == 86) return "Nieve";
        if (code >= 51) return "Lluvia";
        return "Nublado";
    }
}
