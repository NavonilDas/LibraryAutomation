package com.ganesh.library;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


import java.util.logging.Logger;

public class QrScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView zsv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zsv = new ZXingScannerView(this);
        setContentView(zsv);

    }

    @Override
    protected void onResume() {
        super.onResume();
        zsv.setResultHandler(this);
        zsv.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        zsv.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Log.d("QRRESULT",result.getText());
        Intent i = new Intent();
        i.putExtra("QR_RESULT",result.getText());
        setResult(RESULT_OK,i);
        finish();
    }
}
