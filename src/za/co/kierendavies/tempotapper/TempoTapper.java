package za.co.kierendavies.tempotapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class TempoTapper extends Activity {
    private double minuteMillis = 60000;
    private String bpmSuffix;
    private int taps = 0;
    private long startTime;
    private long lastTime;
    private TextView bpmText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bpmText = (TextView) findViewById(R.id.text_bpm);
        bpmSuffix = getResources().getString(R.string.bpm_suffix);

        findViewById(R.id.button_tap).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    tap();
                    view.setPressed(true);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.setPressed(false);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    public void onBackPressed() {
        if (taps == 0) {
            super.onBackPressed();
        } else {
            reset();
        }
    }

    public void tap() {
        long thisTime = SystemClock.uptimeMillis();
        if (taps > 1 && thisTime - lastTime > 2 * (lastTime - startTime) / (taps - 1)) {
            reset();
        }
        // after an automatic reset, keep going, treating that tap as the first one
        if (taps == 0) {
            startTime = thisTime;
        } else {
            double bpm = minuteMillis * taps / (thisTime - startTime);
            bpmText.setText(String.format("%.1f", bpm) + bpmSuffix);
            lastTime = thisTime;
        }
        taps++;
    }

    public void tap(View view) {
        tap();
    }

    public void reset() {
        taps = 0;
        bpmText.setText(R.string.bpm_default);
        startTime = 0;
        lastTime = 0;
    }

    public void reset(View view) {
        reset();
    }

    public void about(MenuItem menuItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.menu_about);
        builder.setMessage(R.string.about);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
