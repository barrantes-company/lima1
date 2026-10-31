package co.barrantes.lima1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

public final class SplashActivity extends Activity {
    private static final int CANVAS = 0xFF12130F;
    private static final int INK = 0xFFF4F4F2;
    private static final int MUTED = 0xFF94958C;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean opened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(CANVAS);
        getWindow().setNavigationBarColor(CANVAS);
        setContentView(cover());
        handler.postDelayed(this::open, 1800);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private FrameLayout cover() {
        Typeface text = getResources().getFont(R.font.manrope);
        Typeface time = getResources().getFont(R.font.manrope_time);
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(CANVAS);
        root.setOnClickListener(v -> open());

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(28), dp(36), dp(28), dp(32));

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        ImageView mark = new ImageView(this);
        mark.setImageResource(R.drawable.logo_lima1);
        mark.setContentDescription(getString(R.string.app_name));
        top.addView(mark, new LinearLayout.LayoutParams(dp(36), dp(36)));
        TextView name = label("lima1", 18, INK, text, false);
        name.setPadding(dp(12), 0, 0, 0);
        name.setLetterSpacing(-0.03f);
        top.addView(name);
        top.addView(new android.view.View(this), new LinearLayout.LayoutParams(0, 1, 1));
        top.addView(label("LIMA", 11, MUTED, text, true));
        page.addView(top);

        page.addView(spacer());

        LinearLayout clock = new LinearLayout(this);
        clock.setOrientation(LinearLayout.HORIZONTAL);
        clock.setGravity(Gravity.BOTTOM);
        TextClock hour = new TextClock(this);
        hour.setFormat12Hour("h");
        hour.setFormat24Hour("H");
        hour.setTextColor(INK);
        hour.setTextSize(TypedValue.COMPLEX_UNIT_SP, 96);
        hour.setTypeface(time);
        hour.setIncludeFontPadding(false);
        clock.addView(hour);
        TextClock minute = new TextClock(this);
        minute.setFormat12Hour("mm");
        minute.setFormat24Hour("mm");
        minute.setTextColor(0xA6F4F4F2);
        minute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        minute.setTypeface(time);
        minute.setIncludeFontPadding(false);
        minute.setPadding(dp(10), 0, 0, dp(14));
        clock.addView(minute);
        page.addView(clock);

        android.view.View rule = new android.view.View(this);
        rule.setBackgroundColor(0xFFC6FF3A);
        LinearLayout.LayoutParams ruleParams = new LinearLayout.LayoutParams(dp(72), dp(3));
        ruleParams.topMargin = dp(14);
        page.addView(rule, ruleParams);

        TextView first = label("Tiempo,", 34, INK, text, true);
        first.setPadding(0, dp(22), 0, 0);
        first.setLetterSpacing(-0.03f);
        page.addView(first);
        TextView second = label("en su lugar.", 34, INK, text, false);
        second.setPadding(0, dp(2), 0, 0);
        second.setLetterSpacing(-0.03f);
        page.addView(second);

        page.addView(spacer());

        LinearLayout dots = new LinearLayout(this);
        dots.setOrientation(LinearLayout.HORIZONTAL);
        dots.setGravity(Gravity.CENTER_VERTICAL);
        dots.addView(dot(0xFFC6FF3A));
        dots.addView(dot(0xFFE8C48A));
        dots.addView(dot(0xFFFF7A3C));
        TextView meta = label("08 piezas", 13, MUTED, text, false);
        meta.setPadding(dp(12), 0, 0, 0);
        dots.addView(meta);
        page.addView(dots);

        TextView brand = label("BARRANTES CO", 11, MUTED, text, true);
        brand.setPadding(0, dp(16), 0, 0);
        brand.setLetterSpacing(0.16f);
        page.addView(brand);

        root.addView(page, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        return root;
    }

    private android.view.View spacer() {
        android.view.View view = new android.view.View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        return view;
    }

    private android.view.View dot(int color) {
        android.view.View view = new android.view.View(this);
        android.graphics.drawable.GradientDrawable circle = new android.graphics.drawable.GradientDrawable();
        circle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        circle.setColor(color);
        view.setBackground(circle);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(10), dp(10));
        params.setMarginEnd(dp(8));
        view.setLayoutParams(params);
        return view;
    }

    private TextView label(String value, int sp, int color, Typeface face, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextColor(color);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        view.setTypeface(face, bold ? Typeface.BOLD : Typeface.NORMAL);
        view.setIncludeFontPadding(false);
        return view;
    }

    private void open() {
        if (opened || isFinishing()) return;
        opened = true;
        startActivity(new Intent(this, MainActivity.class));
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
