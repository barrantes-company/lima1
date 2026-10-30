package co.barrantes.lima1;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String[] STYLES = {"editorial", "instrumento", "senal", "cartel"};
    private static final String[] NAMES = {"01 · Reloj editorial", "02 · Instrumento", "03 · Señal", "04 · Cartel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        render();
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 7);
        }
        Weather.refresh(this, this::render);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        Weather.refresh(this, this::render);
    }

    private void render() {
        if (isFinishing()) return;
        FrameLayout frame = new FrameLayout(this);
        frame.addView(new Dots(this), match());
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout page = column();
        int pad = dp(20);
        page.setPadding(pad, dp(28), pad, dp(36));

        TextView title = text("lima1", 28, Color.WHITE, true);
        page.addView(title);
        TextView lead = text("Configura la paleta e instala cada widget. El reloj del sistema marca la hora.", 15, 0xB3FFFFFF, false);
        lead.setPadding(0, dp(6), 0, dp(8));
        page.addView(lead);

        for (int i = 0; i < STYLES.length; i++) {
            page.addView(section(STYLES[i], NAMES[i]));
        }

        scroll.addView(page);
        frame.addView(scroll, match());
        setContentView(frame);
    }

    private LinearLayout section(String style, String name) {
        LinearLayout box = column();
        box.setPadding(0, dp(22), 0, 0);
        TextView heading = text(name, 18, Color.WHITE, true);
        box.addView(heading);
        box.addView(swatches(style));
        box.addView(preview(style, dp(168)));
        box.addView(install("Instalar", homeClass(style)));
        return box;
    }

    private LinearLayout swatches(String style) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(10), 0, dp(4));
        String current = Prefs.palette(this, style);
        for (String id : Palette.ALL) {
            Button chip = new Button(this);
            chip.setText("");
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(Palette.color(id));
            if (id.equals(current)) {
                circle.setStroke(dp(2), Color.WHITE);
            }
            chip.setBackground(circle);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(22), dp(22));
            lp.setMarginEnd(dp(10));
            chip.setLayoutParams(lp);
            chip.setContentDescription(Palette.label(id));
            chip.setOnClickListener(v -> {
                Prefs.setPalette(this, style, id);
                BaseWidget.push(this);
                render();
            });
            row.addView(chip);
        }
        TextView label = text(Palette.label(current), 13, 0xB3FFFFFF, false);
        row.addView(label);
        return row;
    }

    private FrameLayout preview(String style, int height) {
        FrameLayout host = new FrameLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        lp.topMargin = dp(8);
        host.setLayoutParams(lp);
        try {
            View view = WidgetBinder.bind(this, style).apply(this, host);
            host.addView(view, match());
        } catch (RuntimeException error) {
            String detail = error.getMessage();
            if (detail == null || detail.isEmpty()) detail = error.getClass().getSimpleName();
            if (detail.length() > 110) detail = detail.substring(0, 110);
            TextView fail = text(detail, 12, 0xFFFF7A3C, false);
            fail.setPadding(0, dp(12), 0, 0);
            host.addView(fail);
        }
        return host;
    }

    private Button install(String label, Class<?> type) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(999));
        bg.setStroke(dp(1), 0x33FFFFFF);
        bg.setColor(0x14FFFFFF);
        button.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48));
        lp.topMargin = dp(10);
        button.setLayoutParams(lp);
        button.setOnClickListener(v -> pin(type));
        return button;
    }

    private void pin(Class<?> type) {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        if (Build.VERSION.SDK_INT >= 26 && manager.isRequestPinAppWidgetSupported()) {
            manager.requestPinAppWidget(new ComponentName(this, type), null, null);
            return;
        }
        Toast.makeText(this, "Mantén pulsado el inicio y elige Widgets. Busca lima1.", Toast.LENGTH_LONG).show();
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        view.setTextColor(color);
        if (bold) view.setTypeface(view.getTypeface(), android.graphics.Typeface.BOLD);
        return view;
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private FrameLayout.LayoutParams match() {
        return new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static Class<?> homeClass(String style) {
        return switch (style) {
            case "instrumento" -> InstrumentoWidget.class;
            case "senal" -> SenalWidget.class;
            case "cartel" -> CartelWidget.class;
            default -> EditorialWidget.class;
        };
    }

    static final class Dots extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Dots(Activity activity) {
            super(activity);
            paint.setColor(0x1CFFFFFF);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float step = 13f * getResources().getDisplayMetrics().density;
            float radius = 1.15f * getResources().getDisplayMetrics().density;
            for (float y = step; y < getHeight(); y += step) {
                for (float x = step; x < getWidth(); x += step) {
                    canvas.drawCircle(x, y, radius, paint);
                }
            }
        }
    }
}
