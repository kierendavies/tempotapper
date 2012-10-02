package za.co.kierendavies.tempotapper;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class TempoTapper extends Activity {
    private double minuteMillis = 60000;
    private String bpmSuffix;
    private int taps = 0;
    private long startTime;
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

    public void onBackPressed() {
        if (taps == 0) {
            super.onBackPressed();
        } else {
            reset();
        }
    }

    public void tap() {
        double bpm = 0;
        if (taps == 0) {
            startTime = SystemClock.uptimeMillis();
        } else {  // do nothing on the first tap
            bpm = minuteMillis * taps / (SystemClock.uptimeMillis() - startTime);
            bpmText.setText(String.format("%.1f", bpm) + bpmSuffix);
        }
        taps++;
    }

    public void tap(View view) {
        tap();
    }

    public void reset() {
        taps = 0;
        bpmText.setText(R.string.bpm_default);
    }

    public void reset(View view) {
        reset();
    }
}
