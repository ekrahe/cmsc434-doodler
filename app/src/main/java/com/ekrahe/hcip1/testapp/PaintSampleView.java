package com.ekrahe.hcip1.testapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Eddie on 3/8/2016.
 * Simplified version of the DoodleView
 * Draws a dot that updates to reflect the current values set in SettingsActivity
 */
public class PaintSampleView extends View {

    private Paint _paintDoodle = new Paint();
    private Path _path;

    public PaintSampleView(Context context) {
        super(context);
        init(null, 0);
    }

    public PaintSampleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PaintSampleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        _path = new Path();

        // Set the Paint to reflect the one being used in MainActivity
        _paintDoodle.set(DoodleView.get_paintDoodle());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (_path.isEmpty()) {
            // If the Path hasn't been drawn, compute the bounds and draw a dot in the center
            _path.moveTo(getWidth() / 2, getHeight() / 2);
            _path.lineTo((getWidth() / 2) - 1, (getHeight() / 2) - 1);
        }

        canvas.drawPath(_path, _paintDoodle);
    }

    public void set_paintDoodle(float width, int alpha, float[] hsv) {
        // Set from SettingsActivity with SeekBars
        _paintDoodle.setStrokeWidth(width);
        _paintDoodle.setColor(Color.HSVToColor(alpha, hsv));
        // Draw the new brush
        invalidate();
    }

    public void set_paintDoodle(int r, int g, int b) {
        // Set from SettingsActivity with random button - RGB values
        _paintDoodle.setColor(Color.argb(_paintDoodle.getAlpha(), r, g, b));
        // Draw the new brush
        invalidate();
    }

}
