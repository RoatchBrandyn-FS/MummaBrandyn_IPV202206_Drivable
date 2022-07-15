package com.example.drivable.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.drivable.R;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

public class BarcodeScannerActivity extends AppCompatActivity {

    private final String TAG = "BCScannerActivity.TAG";

    private final int REQUEST_CAMERA = 0x01001;
    private BarcodeDetector bDetector;
    private CameraSource cSource;
    SurfaceView surfaceView;
    private String barcodeString;
    private ArrayList<Vehicle> vehicles = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner_activity);

        surfaceView = findViewById(R.id.surfaceView);

        Intent currentIntent = getIntent();
        vehicles = (ArrayList<Vehicle>) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_LIST_VEHICLES);

    }

    @Override
    protected void onPause() {
        super.onPause();

        cSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources(){

        bDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cSource = new CameraSource.Builder(this, bDetector).setRequestedPreviewSize(1920, 1080).setAutoFocusEnabled(true).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                checkCameraPermissions();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                cSource.stop();
            }
        });

        bDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                progressbarOn();

                Log.i(TAG, "receiveDetections: " + barcodes.valueAt(0).displayValue);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searchVehicles(barcodes.valueAt(0).displayValue.trim());

                        progressbarOff();
                    }
                });

                thread.start();




            }
        });

    }

    private void progressbarOn(){
        RelativeLayout progressbarView = findViewById(R.id.barcode_progressbar_view);
        ProgressBar progressBar = findViewById(R.id.barcode_progressbar);

        progressbarView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        progressBar.setActivated(true);
    }

    private void progressbarOff(){
        RelativeLayout progressbarView = findViewById(R.id.barcode_progressbar_view);
        ProgressBar progressBar = findViewById(R.id.barcode_progressbar);

        progressBar.setActivated(false);
        progressbarView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void checkCameraPermissions() {

        try {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //Camera Permission Granted
                cSource.start(surfaceView.getHolder());

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //Camera Permission Granted
                    cSource.start(surfaceView.getHolder());

                } else {
                    finish();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void searchVehicles(String scannedCode){

        Vehicle scannedVehicle = null;

        for(Vehicle v: vehicles){

            if(v.getVinNum().equals(scannedCode)){

                scannedVehicle = v;
                Log.i(TAG, "receiveDetections: Vehicle Found");

            }

        }

        if(scannedVehicle != null){

            Intent resultIntent = new Intent();
            resultIntent.putExtra(IntentExtrasUtil.EXTRA_VEHICLE, scannedVehicle);
            setResult(RESULT_OK, resultIntent);
            finish();

        }
        else{
            Log.i(TAG, "searchVehicles: Vehicle not found");
        }

    }
}
