package hr.ferit.tomislavrekic.firstgrademathcheat;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class CameraActivity extends AppCompatActivity {

    static String TAG = "Cam123";

    static int CAMERA_REQUEST_CODE = 101;

    TextureView previewScreen;
    Button takePicture;
    Intent switchIntent;

    CameraController cameraController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Log.d(TAG, "created");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_screen);

        initViews();

        initListeners();

        switchIntent = new Intent(this  , MainActivity.class);
        switchIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }

        cameraController = new CameraController(this, previewScreen);

    }

    private void initViews() {
        previewScreen = findViewById(R.id.texVCameraPreview);
        takePicture = findViewById(R.id.btnTakePicture);
        takePicture.setEnabled(false);
    }

    public void broadcastIntent(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.BROADCAST_KEY1);
        broadcastIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(broadcastIntent);
        Log.d("Screen", "broadcastIntent: sent");
    }

    private void initListeners() {
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    takePicture.setEnabled(false);
                    Log.d("click", "clickCAM");
                    try {
                        createImageFromBitmap(cameraController.takePicture());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {
                        broadcastIntent();
                        startActivityIfNeeded(switchIntent,0);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }



    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = Constants.TEMP_IMAGE_KEY ;//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }


    @Override
    protected void onResume() {

        takePicture.setEnabled(true);

        Log.d(TAG, "resume");

        super.onResume();
        cameraController.resumeCamera();
    }

    @Override
    protected void onStop() {

        Log.d(TAG, "stop");
        super.onStop();
        cameraController.stopCamera();
    }

}
