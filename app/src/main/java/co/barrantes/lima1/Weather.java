package co.barrantes.lima1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class Weather {
    private Weather() {}

    public static void refresh(Context ctx, Runnable done) {
        Context app = ctx.getApplicationContext();
        new Thread(() -> {
            try {
                double[] point = location(app);
                if (point == null) point = ip();
                if (point != null) {
                    meteo(app, point[0], point[1]);
                    BaseWidget.push(app);
                }
            } catch (Exception ignored) {
                /* the widget keeps the last reading */
            }
            if (done != null) new Handler(Looper.getMainLooper()).post(done);
        }).start();
    }

    private static double[] location(Context ctx) {
        if (ctx.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        if (manager == null) return null;
        Location best = last(manager, LocationManager.NETWORK_PROVIDER);
        Location gps = last(manager, LocationManager.GPS_PROVIDER);
        if (gps != null && (best == null || gps.getTime() > best.getTime())) best = gps;
        if (best == null) return null;
        return new double[]{best.getLatitude(), best.getLongitude()};
    }

    private static Location last(LocationManager manager, String provider) {
        try {
            if (!manager.isProviderEnabled(provider)) return null;
            return manager.getLastKnownLocation(provider);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static double[] ip() throws Exception {
        try {
            JSONObject json = new JSONObject(get("https://get.geojs.io/v1/ip/geo.json"));
            return new double[]{Double.parseDouble(String.valueOf(json.get("latitude"))), Double.parseDouble(String.valueOf(json.get("longitude")))};
        } catch (Exception first) {
            JSONObject json = new JSONObject(get("https://api.bigdatacloud.net/data/reverse-geocode-client?localityLanguage=es"));
            return new double[]{json.getDouble("latitude"), json.getDouble("longitude")};
        }
    }

    private static void meteo(Context ctx, double lat, double lon) throws Exception {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + lat
                + "&longitude=" + lon
                + "&current=temperature_2m,weather_code,is_day";
        JSONObject current = new JSONObject(get(url)).getJSONObject("current");
        int celsius = Math.round((float) current.getDouble("temperature_2m"));
        int code = current.getInt("weather_code");
        boolean day = current.optInt("is_day", 1) == 1;
        Prefs.setReading(ctx, celsius, code, day);
    }

    private static String get(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);
        connection.setRequestProperty("User-Agent", "lima1");
        try (InputStream stream = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
            return body.toString();
        } finally {
            connection.disconnect();
        }
    }
}
