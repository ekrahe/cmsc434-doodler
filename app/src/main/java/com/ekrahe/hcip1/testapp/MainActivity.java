package com.ekrahe.hcip1.testapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener {

    private DoodleView _doodle;
    private Menu _toolMenu;

    private boolean _clearFlag = false;

    static final String _appDirectoryName = "Doodler";
    static final File _imageRoot = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), _appDirectoryName);

    private class forgetClearABDT extends ActionBarDrawerToggle {
        public forgetClearABDT(Activity a, DrawerLayout dL, Toolbar t, int oDCDR, int cDCDR) {
            super(a, dL, t, oDCDR, cDCDR);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);

            // If the clear button was pressed once (needs to be pressed again to confirm),
            // opening the drawer cancels the action
            if(_clearFlag) _clearFlag = false;

            // Fixes issue where tapping on the left side of _doodle draws a dot and then opens the drawer
            // Before fix, no ACTION_UP would be issued, so if the dot was the last drawn Path,
            // it could not be undone
            _doodle.interruptDraw();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _doodle = (DoodleView) findViewById(R.id.doodle);
        _doodle.setOnTouchListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        forgetClearABDT toggle = new forgetClearABDT(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create the directory for saved images
        if(!_imageRoot.exists()) _imageRoot.mkdirs();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if(_clearFlag) _clearFlag = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Contains undo, redo, clear, and random buttons
        getMenuInflater().inflate(R.menu.main, menu);
        _toolMenu = menu;
        boolean[] grays = _doodle.getGray();

        if (grays[0]) menu.getItem(1).setIcon(R.drawable.ic_menu_undo_gray);
        else menu.getItem(1).setIcon(R.drawable.ic_menu_undo);

        if (grays[1]) menu.getItem(3).setIcon(R.drawable.ic_menu_redo_gray);
        else menu.getItem(3).setIcon(R.drawable.ic_menu_redo);

        if (grays[0] && grays[1]) menu.getItem(2).setIcon(R.drawable.ic_menu_clear_gray);
        else menu.getItem(2).setIcon(R.drawable.ic_menu_clear);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        boolean cleared = _clearFlag;
        if(_clearFlag) _clearFlag = false;

        if (id == R.id.action_undo && !_doodle.getGray()[0]) {
            // User chose undo action, and there is something to undo

            if (_doodle.undoDraw()) {
                // If there is nothing else to undo, gray out the undo button
                _toolMenu.getItem(1).setIcon(R.drawable.ic_menu_undo_gray);
            }

            // After an undo, there is always something to redo
            _toolMenu.getItem(3).setIcon(R.drawable.ic_menu_redo);

            return true;
        } else if (id == R.id.action_clear &&
                (!_doodle.getGray()[0] || !_doodle.getGray()[1] || _doodle.hasPhoto())) {
            // If the clear button was chosen, given that there is a drawing still in the stack
            // Or, alternatively, if there is a photo that can be deleted

            if (cleared) {
                // If the clear button has been tapped once, and nothing else has been touched

                if (_doodle.getGray()[0] && _doodle.getGray()[1]) {
                    // If there is nothing to undo or redo, it must be a photo getting deleted
                    _doodle.clearPhoto();
                    _toolMenu.getItem(2).setIcon(R.drawable.ic_menu_clear_gray);
                } else {
                    // If there is, then the drawing should be cleared
                    _doodle.clearPath();
                    _toolMenu.getItem(1).setIcon(R.drawable.ic_menu_undo_gray);
                    _toolMenu.getItem(3).setIcon(R.drawable.ic_menu_redo_gray);

                    if (!_doodle.hasPhoto()) {
                        // The undo and redo buttons should always be grayed out,
                        // but if there's a photo, the clear button should stay active
                        _toolMenu.getItem(2).setIcon(R.drawable.ic_menu_clear_gray);
                    }
                }
                return true;
            } else {
                // Otherwise, the user should be asked if they really wanted to press the clear button

                _clearFlag = true;
                if (_doodle.getGray()[0] && _doodle.getGray()[1]) {
                    // Clearing the photo
                    Toast.makeText(this, "Press clear button again to delete photo", Toast.LENGTH_SHORT).show();
                } else {
                    // Clearing the drawing
                    Toast.makeText(this, "Press clear button again to delete drawing", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == R.id.action_redo && !_doodle.getGray()[1]) {
            // User chose redo option, and there's something to redo

            if(_doodle.redoDraw()){
                // If there's nothing left to redo, gray out the redo button
                _toolMenu.getItem(3).setIcon(R.drawable.ic_menu_redo_gray);
            }

            // After a redo, there's alwats something to undo
            _toolMenu.getItem(1).setIcon(R.drawable.ic_menu_undo);

            return true;
        } else if (id == R.id.action_random) {
            // Randomize the color using RGB:
            // Seems better at getting bright colors more often vs. HSB
            _doodle.set_paintDoodle((int) (Math.random() * 256), (int) (Math.random() * 256),
                    (int) (Math.random() * 256));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(_clearFlag) _clearFlag = false;

        if (id == R.id.nav_camera) {
            // User wants to take a photo
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                // User's phone actually has a camera
                dispatchTakePictureIntent();
            } else {
                // What are you doing?
                Toast.makeText(this, "System could not find camera", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_manage) {
            // Head to the settings page
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            // Save the image
            String imagePath = saveImage();
            if(imagePath == null) {
                // If unable to save image, let them know
                Toast.makeText(this, "Unable to take screenshot", Toast.LENGTH_LONG).show();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

            // Otherwise, get the image, and pass it along to the system-managed share interface
            Uri uriToImage = Uri.fromFile(new File(imagePath));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, "Share Drawing"));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String saveImage() {
        // Save the drawing so that it can be shared

        // Timestamps make for easy unique file names
        String fname = "drawing_" + (new SimpleDateFormat("yy_MM_dd_hh_mm_ss").format(new Date())) + ".jpeg";
        File image = new File(_imageRoot, fname);
        try {
            FileOutputStream fos = new FileOutputStream(image);
            // Get the Bitmap of the DoodleView, and put it into a .jpeg at the file address
            _doodle.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 95, fos);
            // Cache must be destroyed after each get call, in order for it to update properly
            _doodle.destroyDrawingCache();
            fos.flush();
            fos.close();

            // Make sure the system knows that the new file exists
            MediaScannerConnection.scanFile(this, new String[]{image.toString()}, null, null);
        } catch (Exception e) {
            // Doesn't matter what failed, the end result is that the image can't be shared
            return null;
        }
        return image.getAbsolutePath();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == _doodle.getId()) {
            // As soon as the user draws a line, there is something to undo, something to clear,
            // and the redo stack clears
            _toolMenu.getItem(1).setIcon(R.drawable.ic_menu_undo);
            _toolMenu.getItem(2).setIcon(R.drawable.ic_menu_clear);
            _toolMenu.getItem(3).setIcon(R.drawable.ic_menu_redo_gray);
        }
        // If the user hit the clear button once, they must have changed their mind
        if(_clearFlag) _clearFlag = false;

        // We don't want to consume the touch events: let them pass to our DoodleView
        return false;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    String _currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String fname = "photo_" + (new SimpleDateFormat("yy_MM_dd_hh_mm_ss").format(new Date()));
        File image = File.createTempFile(
                fname,  /* prefix */
                ".jpeg",         /* suffix */
                _imageRoot      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        _currentPhotoPath =  image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // If the picture was successfully taken, set it as the background of the DoodleView
            setPic();
        }
    }

    private void setPic() {
        // The drawing was cleared, but the photo can still be deleted
        _toolMenu.getItem(1).setIcon(R.drawable.ic_menu_undo_gray);
        _toolMenu.getItem(2).setIcon(R.drawable.ic_menu_clear);
        _toolMenu.getItem(3).setIcon(R.drawable.ic_menu_redo_gray);

        // Get the dimensions of the View
        int targetW = _doodle.getWidth();
        int targetH = _doodle.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(_currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Decode the image file into a Bitmap
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(_currentPhotoPath, bmOptions);

        // Determine how much to scale down the image
        double targetRatio = (1.0 * targetW) / targetH;
        double photoRatio = (1.0 * photoW) / photoH;
        if (photoRatio > targetRatio) {
            // Cropping extra width from photo
            int finalW = (int) (photoH * targetRatio);
            int cropX = (photoW - finalW) / 2;
            Bitmap cropped = Bitmap.createBitmap(bitmap, cropX, 0, finalW, photoH);
            _doodle.setPhoto(cropped);
        } else {
            // Cropping extra height from photo
            int finalH = (int) (photoW / targetRatio);
            int cropY = (photoH - finalH) / 2;
            Bitmap cropped = Bitmap.createBitmap(bitmap, 0, cropY, photoW, finalH);
            _doodle.setPhoto(cropped);
        }
    }

}
