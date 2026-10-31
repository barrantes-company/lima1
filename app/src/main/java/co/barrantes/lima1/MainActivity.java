package co.barrantes.lima1;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int INK = 0xFFF1F0E8;
    private static final int MUTED = 0xFF94958C;
    private static final int HAIRLINE = 0x24FFFFFF;
    private static final int SURFACE = 0xD91A1B16;
    private static final int BACKGROUND = 0xFF12130F;
    private Typeface face;
    private Typeface timeFace;

    private static final String[] STYLES = {
            "ccentro", "ceditorial", "cinstrumento", "csenal", "ccartel",
            "instrumento", "senal", "cartel"
    };
    private static final String[] NAMES = {
            "Centro compacto", "Editorial compacto", "Instrumento compacto", "Señal compacto", "Cartel compacto",
            "Instrumento", "Señal", "Cartel"
    };
    private static final String[] DESCRIPTIONS = {
            "Caja alta. Hora arriba, fecha corta y grados.",
            "Caja alta. Reloj en una línea, fecha corta abajo.",
            "Caja alta. Hora, barra del día y fecha corta.",
            "Caja alta. Fecha a un lado, hora al otro.",
            "Caja alta. Hora, línea y minutos. Grados a un lado.",
            "El día convertido en escala, avance y temperatura.",
            "La fecha a un lado. La hora, la más grande, al otro.",
            "Hora y clima, con una línea entre la hora y los minutos."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BACKGROUND);
        getWindow().setNavigationBarColor(BACKGROUND);
        face = getResources().getFont(R.font.manrope);
        timeFace = getResources().getFont(R.font.manrope_time);
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
        frame.setBackgroundColor(BACKGROUND);
        frame.addView(new EditorialGrid(this), match());

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout page = column();
        int side = dp(20);
        page.setPadding(side, dp(22), side, dp(52));
        page.addView(masthead());
        page.addView(rule(dp(28), dp(8)));

        for (int i = 0; i < STYLES.length; i++) {
            if (i == 0) page.addView(group("Compactos", "Cinco piezas de 180 × 120"));
            if (i == 5) page.addView(group("Completos", "Tres piezas amplias"));
            page.addView(section(i));
        }

        page.addView(rule(dp(28), dp(18)));
        TextView note = text("La hora es el dígito más grande.", 16, INK, Typeface.BOLD);
        page.addView(note);
        TextView body = text("Ocho piezas. Cinco compactas y tres amplias. Elige el acento y colócala en el inicio.", 13, MUTED, Typeface.NORMAL);
        body.setPadding(0, dp(8), 0, 0);
        body.setLineSpacing(dp(3), 1f);
        page.addView(body);

        scroll.addView(page);
        frame.addView(scroll, match());
        setContentView(frame);
    }

    private LinearLayout masthead() {
        LinearLayout header = column();
        LinearLayout brandRow = new LinearLayout(this);
        brandRow.setOrientation(LinearLayout.HORIZONTAL);
        brandRow.setGravity(Gravity.CENTER_VERTICAL);
        brandRow.addView(label("BARRANTES CO", INK));
        brandRow.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1));
        brandRow.addView(label("LIMA  /  2026", MUTED));
        header.addView(brandRow);

        LinearLayout clock = new LinearLayout(this);
        clock.setOrientation(LinearLayout.HORIZONTAL);
        clock.setGravity(Gravity.BOTTOM);
        clock.setPadding(0, dp(26), 0, 0);
        TextClock hour = new TextClock(this);
        hour.setFormat12Hour("h");
        hour.setFormat24Hour("H");
        hour.setTextColor(INK);
        hour.setTextSize(TypedValue.COMPLEX_UNIT_SP, 64);
        hour.setTypeface(timeFace);
        hour.setIncludeFontPadding(false);
        clock.addView(hour);
        TextClock minute = new TextClock(this);
        minute.setFormat12Hour("mm");
        minute.setFormat24Hour("mm");
        minute.setTextColor(0xA6F1F0E8);
        minute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        minute.setTypeface(timeFace);
        minute.setIncludeFontPadding(false);
        minute.setPadding(dp(8), 0, 0, dp(8));
        clock.addView(minute);
        header.addView(clock);

        TextView title = text("Tiempo, en su lugar.", 28, INK, Typeface.BOLD);
        title.setIncludeFontPadding(false);
        title.setLineSpacing(dp(-3), 0.94f);
        title.setPadding(0, dp(34), 0, 0);
        header.addView(title);

        TextView lead = text("Relojes para la pantalla de inicio. Elige una pieza, define su acento y colócala en el día.", 15, 0xC9F1F0E8, Typeface.NORMAL);
        lead.setLineSpacing(dp(4), 1f);
        lead.setPadding(0, dp(14), dp(12), 0);
        header.addView(lead);

        TextView meta = label("08 PIEZAS    ·    03 PALETAS", MUTED);
        meta.setPadding(0, dp(18), 0, 0);
        header.addView(meta);
        return header;
    }

    private LinearLayout group(String titleText, String caption) {
        LinearLayout block = column();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(18);
        block.setLayoutParams(params);
        block.addView(label(titleText.toUpperCase(), INK));
        TextView line = text(caption, 13, MUTED, Typeface.NORMAL);
        line.setPadding(0, dp(4), 0, dp(2));
        block.addView(line);
        return block;
    }

    private LinearLayout section(int index) {
        String style = STYLES[index];
        int accent = Palette.color(Prefs.palette(this, style));
        LinearLayout card = column();
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.topMargin = dp(index == 0 ? 20 : 16);
        card.setLayoutParams(cardParams);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setBackground(surface(20, SURFACE, HAIRLINE));

        View accentLine = new View(this);
        accentLine.setBackgroundColor(accent);
        LinearLayout.LayoutParams accentParams = new LinearLayout.LayoutParams(dp(28), dp(2));
        accentParams.bottomMargin = dp(12);
        card.addView(accentLine, accentParams);

        TextView number = text(String.format("%02d", index + 1), 11, accent, Typeface.BOLD);
        number.setLetterSpacing(0.16f);
        card.addView(number);

        TextView name = text(NAMES[index], 22, INK, Typeface.BOLD);
        name.setIncludeFontPadding(false);
        name.setPadding(0, dp(4), 0, 0);
        card.addView(name);
        TextView description = text(DESCRIPTIONS[index], 13, MUTED, Typeface.NORMAL);
        description.setLineSpacing(dp(2), 1f);
        description.setPadding(0, dp(6), 0, 0);
        card.addView(description);

        card.addView(swatches(style));
        card.addView(preview(style, dp(168)));
        card.addView(install("Añadir a inicio", homeClass(style), accent));
        return card;
    }

    private LinearLayout swatches(String style) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(10), dp(10), dp(10));
        row.setBackground(surface(12, 0x9912130F, 0x16FFFFFF));
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = dp(18);
        row.setLayoutParams(rowParams);
        row.addView(label("ACENTO", MUTED), new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        String current = Prefs.palette(this, style);
        for (String id : Palette.ALL) {
            boolean selected = id.equals(current);
            FrameLayout target = new FrameLayout(this);
            LinearLayout.LayoutParams targetParams = new LinearLayout.LayoutParams(dp(44), dp(44));
            targetParams.setMarginStart(dp(2));
            target.setLayoutParams(targetParams);
            target.setContentDescription(selected
                    ? getString(R.string.palette_selected, Palette.label(id))
                    : "Paleta " + Palette.label(id));
            target.setClickable(true);
            target.setFocusable(true);
            target.setSelected(selected);

            Button chip = new Button(this);
            chip.setText("");
            chip.setMinWidth(0);
            chip.setMinHeight(0);
            chip.setPadding(0, 0, 0, 0);
            chip.setClickable(false);
            chip.setFocusable(false);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(Palette.color(id));
            if (selected) circle.setStroke(dp(2), INK);
            chip.setBackground(circle);
            target.addView(chip, new FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER));
            target.setOnClickListener(v -> {
                Prefs.setPalette(this, style, id);
                BaseWidget.push(this);
                Toast.makeText(this, getString(R.string.palette_applied, Palette.label(id), widgetName(style)), Toast.LENGTH_SHORT).show();
                render();
            });
            row.addView(target);
        }
        TextView selectedLabel = text(Palette.label(current), 12, INK, Typeface.BOLD);
        selectedLabel.setGravity(Gravity.END);
        LinearLayout.LayoutParams selectedParams = new LinearLayout.LayoutParams(dp(54), LinearLayout.LayoutParams.WRAP_CONTENT);
        selectedParams.setMarginStart(dp(8));
        row.addView(selectedLabel, selectedParams);
        return row;
    }

    private FrameLayout preview(String style, int height) {
        boolean compact = style.startsWith("c") && !"cartel".equals(style) && !"centro".equals(style);
        int frameHeight = compact ? dp(176) : height;
        FrameLayout host = new FrameLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, frameHeight);
        lp.topMargin = dp(14);
        host.setLayoutParams(lp);
        try {
            View view = WidgetBinder.bind(this, style).apply(this, host);
            int width = compact ? dp(180) : FrameLayout.LayoutParams.MATCH_PARENT;
            host.addView(view, new FrameLayout.LayoutParams(width, frameHeight, Gravity.CENTER_HORIZONTAL));
        } catch (RuntimeException error) {
            String detail = error.getMessage();
            if (detail == null || detail.isEmpty()) detail = error.getClass().getSimpleName();
            if (detail.length() > 110) detail = detail.substring(0, 110);
            TextView fail = text(detail, 12, 0xFFFF7A3C, Typeface.NORMAL);
            fail.setPadding(0, dp(12), 0, 0);
            host.addView(fail);
        }
        return host;
    }

    private Button install(String labelText, Class<?> type, int accent) {
        Button button = new Button(this);
        button.setText(labelText);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(accent);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        button.setTypeface(face, Typeface.BOLD);
        button.setBackground(surface(14, 0x00000000, accent));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(46));
        lp.topMargin = dp(10);
        button.setLayoutParams(lp);
        button.setOnClickListener(v -> pin(type));
        return button;
    }

    private void pin(Class<?> type) {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        if (Build.VERSION.SDK_INT >= 26 && manager.isRequestPinAppWidgetSupported()) {
            boolean requested = manager.requestPinAppWidget(new ComponentName(this, type), null, null);
            if (requested) {
                Toast.makeText(this, getString(R.string.pin_requested, widgetName(styleOf(type))), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        Toast.makeText(this, "Mantén pulsado el inicio, abre Widgets y busca lima1.", Toast.LENGTH_LONG).show();
    }

    private TextView label(String value, int color) {
        TextView view = text(value, 10, color, Typeface.BOLD);
        view.setLetterSpacing(0.14f);
        return view;
    }

    private TextView text(String value, int sp, int color, int style) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        view.setTextColor(color);
        view.setTypeface(face, style);
        return view;
    }

    private View rule(int top, int bottom) {
        View line = new View(this);
        line.setBackgroundColor(HAIRLINE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        lp.topMargin = top;
        lp.bottomMargin = bottom;
        line.setLayoutParams(lp);
        return line;
    }

    private GradientDrawable surface(int radius, int fill, int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radius));
        if (stroke != 0) drawable.setStroke(dp(1), stroke);
        return drawable;
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

    private static String widgetName(String style) {
        return switch (style) {
            case "cinstrumento" -> "Instrumento compacto";
            case "csenal" -> "Señal compacto";
            case "ccartel" -> "Cartel compacto";
            case "ccentro" -> "Centro compacto";
            case "ceditorial" -> "Editorial compacto";
            case "instrumento" -> "Instrumento";
            case "senal" -> "Señal";
            case "cartel" -> "Cartel";
            case "centro" -> "Centro";
            default -> "Editorial";
        };
    }

    private static String styleOf(Class<?> type) {
        if (type == CInstrumentoWidget.class) return "cinstrumento";
        if (type == CSenalWidget.class) return "csenal";
        if (type == CCartelWidget.class) return "ccartel";
        if (type == InstrumentoWidget.class) return "instrumento";
        if (type == SenalWidget.class) return "senal";
        if (type == CartelWidget.class) return "cartel";
        if (type == CentroWidget.class) return "ccentro";
        return "ceditorial";
    }

    private static Class<?> homeClass(String style) {
        return switch (style) {
            case "cinstrumento" -> CInstrumentoWidget.class;
            case "csenal" -> CSenalWidget.class;
            case "ccartel" -> CCartelWidget.class;
            case "ccentro" -> CentroWidget.class;
            case "ceditorial" -> EditorialWidget.class;
            case "instrumento" -> InstrumentoWidget.class;
            case "senal" -> SenalWidget.class;
            case "cartel" -> CartelWidget.class;
            case "centro" -> CentroWidget.class;
            default -> EditorialWidget.class;
        };
    }

    static final class EditorialGrid extends View {
        private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);

        EditorialGrid(Activity activity) {
            super(activity);
            line.setColor(0x0BFFFFFF);
            line.setStrokeWidth(activity.getResources().getDisplayMetrics().density);
            glow.setColor(0x0AC6FF3A);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float density = getResources().getDisplayMetrics().density;
            float column = 72f * density;
            for (float x = 20f * density; x < getWidth(); x += column) {
                canvas.drawLine(x, 0, x, getHeight(), line);
            }
            canvas.drawCircle(getWidth() * 0.86f, 118f * density, 90f * density, glow);
        }
    }
}
