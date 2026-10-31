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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int INK = 0xFFF1F0E8;
    private static final int MUTED = 0xFF94958C;
    private static final int HAIRLINE = 0x24FFFFFF;
    private static final int SURFACE = 0xD91A1B16;
    private static final int BACKGROUND = 0xFF12130F;

    private static final String[] STYLES = {"centro", "editorial", "instrumento", "senal", "cartel"};
    private static final String[] NAMES = {"Centro", "Editorial", "Instrumento", "Señal", "Cartel"};
    private static final String[] DESCRIPTIONS = {
            "La hora al centro. Los minutos, la fecha y los grados se quedan en el borde.",
            "Una lectura amplia y serena para el centro de la pantalla.",
            "El día convertido en escala, avance y temperatura.",
            "La fecha a un lado. La hora, la más grande, al otro.",
            "Hora y clima construidos como una pieza tipográfica."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BACKGROUND);
        getWindow().setNavigationBarColor(BACKGROUND);
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
        page.addView(rule(dp(30), dp(26)));
        page.addView(label("COLECCIÓN 01  /  CINCO FORMAS DE LEER EL DÍA", MUTED));

        for (int i = 0; i < STYLES.length; i++) {
            page.addView(section(i));
        }

        page.addView(rule(dp(34), dp(22)));
        TextView note = text("Al estrechar", 16, INK, Typeface.BOLD);
        page.addView(note);
        TextView body = text("Si falta ancho, se oculta el logo. Si falta alto, se oculta la fecha. La pieza amplia no cambia.", 13, MUTED, Typeface.NORMAL);
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

        TextView title = text("Tiempo,\nen su lugar.", 42, INK, Typeface.BOLD);
        title.setIncludeFontPadding(false);
        title.setLineSpacing(dp(-3), 0.94f);
        title.setPadding(0, dp(34), 0, 0);
        header.addView(title);

        TextView lead = text("Relojes para la pantalla de inicio. Elige una pieza, define su acento y colócala en el día.", 15, 0xC9F1F0E8, Typeface.NORMAL);
        lead.setLineSpacing(dp(4), 1f);
        lead.setPadding(0, dp(22), dp(18), 0);
        header.addView(lead);

        LinearLayout facts = new LinearLayout(this);
        facts.setOrientation(LinearLayout.HORIZONTAL);
        facts.setPadding(0, dp(28), 0, 0);
        facts.addView(fact("05", "DISEÑOS"), new LinearLayout.LayoutParams(0, dp(58), 1));
        LinearLayout.LayoutParams second = new LinearLayout.LayoutParams(0, dp(58), 1);
        second.setMarginStart(dp(10));
        facts.addView(fact("03", "PALETAS"), second);
        header.addView(facts);
        return header;
    }

    private LinearLayout fact(String value, String caption) {
        LinearLayout box = column();
        box.setPadding(dp(14), dp(9), dp(14), dp(8));
        box.setBackground(surface(10, 0x12FFFFFF, HAIRLINE));
        TextView number = text(value, 18, INK, Typeface.BOLD);
        number.setIncludeFontPadding(false);
        box.addView(number);
        TextView captionView = label(caption, MUTED);
        captionView.setPadding(0, dp(3), 0, 0);
        box.addView(captionView);
        return box;
    }

    private LinearLayout section(int index) {
        String style = STYLES[index];
        int accent = Palette.color(Prefs.palette(this, style));
        LinearLayout card = column();
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.topMargin = dp(index == 0 ? 20 : 16);
        card.setLayoutParams(cardParams);
        card.setPadding(dp(16), dp(16), dp(16), dp(18));
        card.setBackground(surface(18, SURFACE, HAIRLINE));

        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.HORIZONTAL);
        heading.setGravity(Gravity.TOP);
        TextView number = text(String.format("%02d", index + 1), 11, accent, Typeface.BOLD);
        number.setLetterSpacing(0.12f);
        heading.addView(number, new LinearLayout.LayoutParams(dp(38), LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout copy = column();
        TextView name = text(NAMES[index], 24, INK, Typeface.BOLD);
        name.setIncludeFontPadding(false);
        copy.addView(name);
        TextView description = text(DESCRIPTIONS[index], 13, MUTED, Typeface.NORMAL);
        description.setLineSpacing(dp(2), 1f);
        description.setPadding(0, dp(7), dp(4), 0);
        copy.addView(description);
        heading.addView(copy, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        card.addView(heading);

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
        FrameLayout host = new FrameLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        lp.topMargin = dp(12);
        host.setLayoutParams(lp);
        try {
            View view = WidgetBinder.bind(this, style).apply(this, host);
            host.addView(view, match());
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
        button.setText(labelText + "   →");
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(BACKGROUND);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        button.setTypeface(button.getTypeface(), Typeface.BOLD);
        button.setBackground(surface(12, accent, accent));
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
        view.setTypeface(view.getTypeface(), style);
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
            case "instrumento" -> "Instrumento";
            case "senal" -> "Señal";
            case "cartel" -> "Cartel";
            case "centro" -> "Centro";
            default -> "Editorial";
        };
    }

    private static String styleOf(Class<?> type) {
        if (type == InstrumentoWidget.class) return "instrumento";
        if (type == SenalWidget.class) return "senal";
        if (type == CartelWidget.class) return "cartel";
        if (type == CentroWidget.class) return "centro";
        return "editorial";
    }

    private static Class<?> homeClass(String style) {
        return switch (style) {
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
