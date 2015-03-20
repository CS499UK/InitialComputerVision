package makemachine.android.examples;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoCaptureExample extends Activity {
    protected Button _button;
    protected ImageView _image;
    protected TextView _field;
    protected String _path;
    protected boolean _taken;

    protected static final String PHOTO_TAKEN = "photo_taken";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _image = (ImageView) findViewById(R.id.image);
        _field = (TextView) findViewById(R.id.field);
        _button = (Button) findViewById(R.id.button);
        _button.setOnClickListener(new ButtonClickHandler());

        //For SD Cards:
        _path = Environment.getExternalStorageDirectory() + "/make_machine_example.jpg";

    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Log.i("MakeMachine", "ButtonClickHandler.onClick()");
            startCameraActivity();
        }
    }

    protected void startCameraActivity() {
        Log.i("MakeMachine", "startCameraActivity()");
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:
                onPhotoTaken();
                break;
        }
    }

    protected void onPhotoTaken() {
        Log.i("MakeMachine", "onPhotoTaken");

        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        _image.setImageBitmap(bitmap);

        _field.setVisibility(View.GONE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("MakeMachine", "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(PhotoCaptureExample.PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(PhotoCaptureExample.PHOTO_TAKEN, _taken);
    }

    /*
    protected void ocr() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        try {
            ExifInterface exif = new ExifInterface(_path);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Log.v(LOG_TAG, "Orient: " + exifOrientation);

            int rotate = 0;
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(LOG_TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                // tesseract req. ARGB_8888
                //bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            }
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            ImageView iv = (ImageView) findViewById(R.id.image);
            iv.setImageBitmap(bitmap);
            iv.setVisibility(View.VISIBLE);

            Log.v(LOG_TAG, "Before baseApi");

            TessBaseAPI baseApi = new TessBaseAPI();
            //DATA_PATH = //path to storage
            LANG = "eng"
            baseApi.init(_path, LANG);
            baseApi.setImage(bitmap);
            String recognizedText = baseApi.getUTF8Text();
            baseApi.end();

            Log.v(LOG_TAG, "OCR Result: " + recognizedText);

            // clean up and show
            if (LANG.equalsIgnoreCase("eng")) {
                recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
            }
            if (recognizedText.length() != 0) {
                ((TextView) findViewById(R.id.field)).setText(recognizedText.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}