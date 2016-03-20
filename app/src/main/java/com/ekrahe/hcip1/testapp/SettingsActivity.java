package com.ekrahe.hcip1.testapp;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingsActivity extends AppCompatActivity implements OnSeekBarChangeListener {

    private PaintSampleView _sampler;
    private SeekBar _hueBar, _satBar, _briBar, _opBar, _widthBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        // Back button goes back to parent activity, MainActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Paint currentPaint = new Paint();
        // Set the Paint preview to be the current brush the User is, well, using
        currentPaint.set(DoodleView.get_paintDoodle());

        _hueBar = (SeekBar) findViewById(R.id.hueBar);
        _hueBar.setOnSeekBarChangeListener(this);

        _satBar = (SeekBar) findViewById(R.id.satBar);
        _satBar.setOnSeekBarChangeListener(this);

        _briBar = (SeekBar) findViewById(R.id.briBar);
        _briBar.setOnSeekBarChangeListener(this);

        _opBar = (SeekBar) findViewById(R.id.opBar);
        _opBar.setOnSeekBarChangeListener(this);

        _widthBar = (SeekBar) findViewById(R.id.sizeBar);
        _widthBar.setOnSeekBarChangeListener(this);

        // Set the SeekBars to reflect the current brush's values
        float[] hsv = {0,0,0};
        Color.colorToHSV(currentPaint.getColor(), hsv);
        // Hue is out of 360, but Saturation and Brightness are naturally between 0 and 1
        _hueBar.setProgress((int) hsv[0]);
        _satBar.setProgress((int) (hsv[1] * 360));
        _briBar.setProgress((int) (hsv[2] * 360));
        // Minimum of 25 for Opacity and Size/Width
        _opBar.setProgress(currentPaint.getAlpha() - 25);
        _widthBar.setProgress((int) currentPaint.getStrokeWidth() - 25);

        _sampler = (PaintSampleView) findViewById(R.id.sampler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, which has the random button
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button
        int id = item.getItemId();

        if (id == R.id.settings_random) {
            // Randomize the color using RGB
            int r = (int) (Math.random() * 256);
            int g = (int) (Math.random() * 256);
            int b = (int) (Math.random() * 256);
            _sampler.set_paintDoodle(r, g, b);

            // Set the HSV SeekBars to reflect the new RGB color
            float[] hsb = new float[3];
            Color.RGBToHSV(r, g, b, hsb);
            _hueBar.setProgress((int) hsb[0]);
            _satBar.setProgress((int) (hsb[1] * 360));
            _briBar.setProgress((int) (hsb[2] * 360));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Ignore any input from the Activity, it's handled elsewhere
        if(!fromUser) return;

        // Update the PaintSampleView to show what the Paint looks like with its new values
        float[] hsv = {_hueBar.getProgress(), (float) (_satBar.getProgress() / 360.0),
                (float) (_briBar.getProgress() / 360.0)};
        _sampler.set_paintDoodle(25 + _widthBar.getProgress(), 25 + _opBar.getProgress(), hsv);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Psh
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // As if
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Make sure that the changes made in the settings menu are reflected in the MainActivity
        float[] hsv = {_hueBar.getProgress(), (float) (_satBar.getProgress() / 360.0),
                (float) (_briBar.getProgress() / 360.0)};
        DoodleView.set_paintDoodle(25 + _widthBar.getProgress(), 25 + _opBar.getProgress(), hsv);
    }

}
