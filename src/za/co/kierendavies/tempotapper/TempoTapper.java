package za.co.kierendavies.tempotapper;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

public class TempoTapper extends Activity {
    private double minuteMillis = 60000;
    private String bpmSuffix;
    private int taps = -1;
    private long startTime;
    private TextView bpmText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bpmText = (TextView) findViewById(R.id.bpm);
        bpmSuffix = getResources().getString(R.string.bpm_suffix);
    }

    public void tap(View view) {
        double bpm = 0;
        if (taps == 0) {
            startTime = SystemClock.uptimeMillis();
        } else {  // do nothing on the first tap
            bpm = minuteMillis * taps / (SystemClock.uptimeMillis() - startTime);
            bpmText.setText(String.format("%.1f", bpm) + bpmSuffix);
        }
        taps++;
    }

    public void reset(View view) {
        taps = 0;
        bpmText.setText(R.string.bpm_default);
    }
}
