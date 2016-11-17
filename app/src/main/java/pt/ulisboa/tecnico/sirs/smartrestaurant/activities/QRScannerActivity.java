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
import com.google.common.primitives.Booleans;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pt.ulisboa.tecnico.sirs.smartrestaurant.R;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    public String text;
    public String serverQR = null;
    public boolean responded = false;
    public boolean verRes;

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

    public void test() {
        new Endpoint().execute(new Pair<Context, String>(this, "Manfred"));
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        setText(rawResult.getText());

        System.out.println("Esperando");
        //while(serverQR == null);

        mScannerView.stopCamera();

        new Endpoint().execute(new Pair<Context, String>(this, rawResult.getText()));
        //while(!responded);
        try {
            Thread.sleep(4000);
        } catch (Exception e) {}
        System.out.println("Esperandsdasdasdasdo323" + verRes);
            Intent activity = new Intent(this, FragmentActivity.class);
            startActivity(activity);


        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
    }

    private class Endpoint extends AsyncTask<Pair<Context, String>, Void, Boolean> {
        private MyApi myApiService = null;
        private Context context;
        private String name;

        protected Boolean doInBackground(Pair<Context, String>... params) {
            if(myApiService == null) {
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://long-victor-147017.appspot.com/_ah/api/");

                /*MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver*/

                myApiService = builder.build();
            }

            context = params[0].first;
            name = params[0].second;
            try {
                return myApiService.verifyQRCode(name).execute().getResult();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //Toast.makeText(context, result, Toast.LENGTH_LONG);
            responded = true;
            verRes = result;
            System.out.println("Resultado: " + verRes + "|" + responded);
        }

    }
}
