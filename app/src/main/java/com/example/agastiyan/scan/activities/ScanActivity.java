package com.example.agastiyan.scan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.agastiyan.R;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class ScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    private static final String TAG = ScanActivity.class.getSimpleName();
    public static final String agaskey = "836";
    BarcodeReader barcodeReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity_scan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the barcode reader instance
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);
    }

    @Override
    public void onScanned(Barcode barcode) {

        String result = null;
        // playing barcode reader beep sound
        barcodeReader.playBeep();

        result = barcode.displayValue;
        // String result = barcode.displayValue;
        Log.e(TAG, "Product response: " + barcode.displayValue);

        if ((result != null) && (result.split("#").length == 3)){

            if(result.split("#")[1].equals(agaskey)) {

                Log.e(TAG, "Product response: " + result);
                // product details activity by passing barcode
                Intent intent = new Intent(ScanActivity.this, ProductResultActivity.class);
                intent.putExtra("code", result.split("#")[2]);
                startActivity(intent);
            }
        } else {

            Toast.makeText(getApplicationContext(),
                    "Not a valid QR code", Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public void onScannedMultiple(List<Barcode> list) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String s) {
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPermissionDenied() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
