package za.co.kierendavies.tempotapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TempoTapper extends Activity {
    private double minuteMillis = 60000;
    private String bpmSuffix;
    private int taps = 0;
    private long startTime;
    private long lastTime;
    private TextView bpmText;
    private SharedPreferences sharedPref;
    private int timesLaunched;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bpmText = (TextView) findViewById(R.id.text_bpm);
        bpmSuffix = getString(R.string.bpm_suffix);

        findViewById(R.id.button_tap).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        tap();
                        view.setPressed(true);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        view.setPressed(false);
                        return true;
                    default:
                        return false;
                }
            }
        });

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        timesLaunched = sharedPref.getInt("timesLaunched", 0);
        if (timesLaunched != -1) {
            ++timesLaunched;
            sharedPref.edit()
                    .putInt("timesLaunched", timesLaunched)
                    .commit();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    public void onBackPressed() {
        if (taps == 0) {
            if (timesLaunched >= 5) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.rate_title)
                        .setMessage(R.string.rate_message)
                        .setPositiveButton(R.string.rate_yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sharedPref.edit()
                                                .putInt("timesLaunched", -1)
                                                .commit();
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("market://details?id=" + getPackageName())));
                                        finish();
                                    }
                                })
                        .setNeutralButton(R.string.rate_later,  // ask next time
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                        .setNegativeButton(R.string.rate_no,  // don't ask again
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sharedPref.edit()
                                                .putInt("timesLaunched", -1)
                                                .commit();
                                        finish();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        finish();
                                    }
                                })
                        .create()
                        .show();
            } else {
                finish();
            }
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
