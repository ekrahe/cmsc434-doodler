package com.ekrahe.hcip1.testapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Eddie on 3/8/2016.
 * View for drawing in the MainActivity
 */
public class DoodleView extends View {

    // Static variables mean that we can remember everything when we come back
    // Plus, we can set values while in the SettingsActivity
    // More direct than resetting everything when we restore MainActivity
    private static Paint _paintDoodle;
    private static Path _path;
    private static ArrayList<Bitmap> _bgUndo = new ArrayList<>();
    private static ArrayList<Bitmap> _bgRedo = new ArrayList<>();

    private static Bitmap _photo = null;

    private boolean _clearFlag = false;

    public DoodleView(Context context) {
        super(context);
        init(null, 0);
    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Make sure we remember what the View looks like at any time, so we can share drawings
        setDrawingCacheEnabled(true);
        // No low-quality pictures here!
        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);

        // We want a white background for drawn images
        // BUT that doesn't matter when there's a photo acting as the background
        if(_photo == null) setDrawingCacheBackgroundColor(Color.WHITE);
        else setDrawingCacheBackgroundColor(Color.TRANSPARENT);

        _path = new Path();
        if (_paintDoodle == null) {
            // Set a new, random-colored Paint given that the app was just opened
            _paintDoodle = new Paint();
            _paintDoodle.setColor(Color.argb(255, (int) (Math.random() * 256),
                    (int) (Math.random() * 256), (int) (Math.random() * 256)));
            _paintDoodle.setAntiAlias(true);
            _paintDoodle.setDither(true);
            _paintDoodle.setStyle(Paint.Style.STROKE);
            _paintDoodle.setStrokeWidth(75);
            _paintDoodle.setStrokeCap(Paint.Cap.ROUND);
            // This gets rid of some nasty graphical glitches with the fun multitouch lines
            _paintDoodle.setPathEffect(new CornerPathEffect(5f));
        }

        if (!_bgUndo.isEmpty()) {
            // If we came back from somewhere and there's something previously drawn, draw it!
            drawBackground();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(_bgUndo.isEmpty()) {
            // If there's no drawing to show, set it to the photo, or white if there isn't one
            if (_photo == null) canvas.drawColor(Color.WHITE);
            else setBackground(new BitmapDrawable(getResources(), _photo));
        }
        // Draw the currently drawn Path
        canvas.drawPath(_path, _paintDoodle);
    }

    private void drawBackground() {
        // The background is the Bitmap of all previous paths, plus the photo if there is one

        // If there's no background, call onDraw()
        if(_bgUndo.isEmpty()) invalidate();
        // If there is, set it to be the most recent Bitmap in _bgUndo
        else setBackground(new BitmapDrawable(getResources(), _bgUndo.get(_bgUndo.size() - 1)));
    }

    // Keep track of the last x & y location for some nice, smooth drawing
    private float _lastX, _lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xpos = event.getX(), ypos = event.getY();

        boolean cleared = false;
        if (_clearFlag) {
            // Make sure _clearFlag doesn't stay set until the next ACTION_MOVE
            _clearFlag = false; cleared = true;
        }

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // There's no redoing as soon as the user draws something new
                if (_bgRedo.size() > 0) _bgRedo.clear();

                // Make a dot! Nice for tapping, plus it's good feedback to show the Path is being drawn
                makeDot(xpos, ypos);
                // Extra moveTo to avoid more weird graphical glitches
                _path.moveTo(xpos, ypos);

                // Remember your place
                _lastX = xpos; _lastY = ypos;
                break;
            case MotionEvent.ACTION_MOVE:
                if (cleared) {
                    // Gets rid of a glitch where the user clears with one finger
                    // while drawing with another
                    _path.moveTo(xpos, ypos);
                    _lastX = xpos; _lastY = ypos;
                } else {
                    float dx = Math.abs(xpos - _lastX);
                    float dy = Math.abs(ypos - _lastY);
                    double radius = Math.sqrt((dx*dx) + (dy*dy));

                    if (radius >= 15) {
                        // Nice drawing technique I found
                        // while looking for something else on StackOverflow
                        _path.quadTo(_lastX, _lastY, (xpos + _lastX)/2, (ypos + _lastY)/2);

                        if (radius > 150) {
                            // Support multitouch lines, for fun
                            // Otherwise, quadTo() only goes half the way (obviously)
                            _path.lineTo(xpos, ypos);
                        }

                        _lastX = xpos;
                        _lastY = ypos;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // Add the new drawing to the undoable Bitmap cache: this is now the one we draw
                _bgUndo.add(getDrawingCache().copy(Bitmap.Config.ARGB_8888, true));

                // Only remember 15, to keep size cost down
                // Plus, if you realize you messed up a whole 15 lines later, shame on you
                if (_bgUndo.size() > 15) _bgUndo.remove(0);

                // Destroy drawing cache to balance out the get call
                // That's just how it works
                destroyDrawingCache();

                // Draw the new stuff / Make it official
                drawBackground();
                invalidate();

                _path = new Path();
                break;
        }

        invalidate();
        return true;
    }

    private void makeDot(float xpos, float ypos) {
        // Draw a line that's 1 pixel long. So basically a dot.
        // I make sure that I draw towards the center, just for the highly unlikely corner cases
        float dotX, dotY;
        if (xpos > (this.getWidth() / 2)) dotX = xpos - 1;
        else dotX = xpos + 1;
        if (ypos > (this.getHeight() / 2)) dotY = ypos - 1;
        else dotY = ypos + 1;

        // Call moveTo first, or else the line is drawn from the origin
        _path.moveTo(dotX, dotY);
        _path.lineTo(xpos, ypos);
    }

    public void clearPath() {
        // Clear both stacks and the current Path
        _bgUndo.clear();
        _bgRedo.clear();
        _path.rewind();

        // Get rid of the Background if there was one before
        if(_photo == null) setBackground(null);

        // Set the flag to get rid of the multitouch clear issue
        _clearFlag = true;

        // Redraw
        invalidate();
    }

    public void clearPhoto() {
        // Easy peasy
        _photo = null;
        setBackground(null);
        invalidate();
    }

    public boolean hasPhoto() {
        // Lemon squeezy
        return (_photo != null);
    }

    public boolean undoDraw() {
        // Returns true if _bgRedo full or _bgUndo empty: can't undo anymore
        // Hard maximum of 15 states remembered: Bitmaps are large,
        // so any more than that can crash the app. On my phone, the number was 29.
        if (!_bgUndo.isEmpty() && _bgRedo.size() < 14) {
            // Pop one drawn path from the undo stack, if there are any
            // Then push it into the redo stack
            _bgRedo.add(_bgUndo.remove(_bgUndo.size() - 1));
            drawBackground();
            return (_bgUndo.isEmpty() || _bgRedo.size() == 14);
        }
        return true;
    }

    public boolean redoDraw() {
        // Returns true if _bgRedo empty: can't redo anymore
        if (_bgRedo.size() > 0) {
            // If there's something to redo, pop the most recently undone draw
            // from the redo stack, then push it into the undo stack
            _bgUndo.add(_bgRedo.remove(_bgRedo.size() - 1));
            drawBackground();
            return _bgRedo.isEmpty();
        }
        return true;
    }

    public boolean[] getGray() {
        // Lets the MainActivity know which of the undo, redo, and clear buttons are inactive
        boolean[] ret = {(_bgUndo.isEmpty() || _bgRedo.size() == 14), _bgRedo.isEmpty()};
        return ret;
    }

    public static Paint get_paintDoodle() {
        // We want this for the SettingsActivity to preview the current Paint when it's opened
        return _paintDoodle;
    }

    public static void set_paintDoodle(float width, int alpha, float[] hsv) {
        // Set HSB values, plus width & opacity, to reflect changes in settings screen
        // Static method, can be called from SettingsActivity
        _paintDoodle.setStrokeWidth(width);
        _paintDoodle.setColor(Color.HSVToColor(alpha, hsv));
    }

    public static void set_paintDoodle(int r, int g, int b) {
        // Set RGB values from the random button in either activity: Main or Settings
        _paintDoodle.setColor(Color.argb(_paintDoodle.getAlpha(), r, g, b));
    }

    public void setPhoto(Bitmap photo) {
        // If we have a photo, clear the current drawing and throw the photo on as the background
        clearPath();
        _photo = photo;
        setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        invalidate();
    }

}
