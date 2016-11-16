package pt.ulisboa.tecnico.sirs.smartrestaurant.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.example.miguel.myapplication.restaurantserver.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.zxing.Result;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pt.ulisboa.tecnico.sirs.smartrestaurant.R;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    public String text;
    public String serverQR = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

        //Intent activity = new Intent(this, FragmentActivity.class);
        //startActivity(activity);

    }

    public void QrScanner(View view){

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        setText(rawResult.getText());

        System.out.println("Esperando");
        //while(serverQR == null);
        EndpointsAsyncTask().execute(new Pair<Context, String>(this, "Mkvjdfkvkfvhjsdkjhfkdshfksdhfkh"));

        Intent activity = new Intent(this, FragmentActivity.class);
        startActivity(activity);

        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
    }


        @Override
        protected String doInBackground(String... params) {

            String name = params[0];

            try {
                return myApiService.verifyQRCode(name).execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            serverQR = result;
        }
    }


}
