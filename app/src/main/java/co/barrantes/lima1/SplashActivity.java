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
        handler.postDelayed(this::open, 1500);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private FrameLayout cover() {
        Typeface text = getResources().getFont(R.font.manrope);
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(CANVAS);
        root.setOnClickListener(v -> open());

        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView mark = new ImageView(this);
        mark.setImageResource(R.drawable.logo_lima1);
        mark.setContentDescription(getString(R.string.app_name));
        column.addView(mark, new LinearLayout.LayoutParams(dp(112), dp(112)));

        TextView name = new TextView(this);
        name.setText("lima1");
        name.setTextColor(INK);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        name.setTypeface(text, Typeface.BOLD);
        name.setLetterSpacing(-0.03f);
        name.setPadding(0, dp(22), 0, 0);
        column.addView(name);

        TextView line = new TextView(this);
        line.setText("Tiempo, en su lugar.");
        line.setTextColor(MUTED);
        line.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        line.setTypeface(text);
        line.setPadding(0, dp(8), 0, 0);
        column.addView(line);

        root.addView(column, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        TextView brand = new TextView(this);
        brand.setText("BARRANTES CO");
        brand.setTextColor(MUTED);
        brand.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        brand.setTypeface(text, Typeface.BOLD);
        brand.setLetterSpacing(0.16f);
        FrameLayout.LayoutParams brandParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        brandParams.bottomMargin = dp(36);
        root.addView(brand, brandParams);
        return root;
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
