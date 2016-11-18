package pt.ulisboa.tecnico.sirs.smartrestaurant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.zxing.Result;

import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pt.ulisboa.tecnico.sirs.smartrestaurant.R;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    public String qrcode;
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

        //Para nao termos que ler o qrcode
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

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        setQrcode(rawResult.getText());

        System.out.println("Creating Thread!!");
        Thread cThread = new Thread(new QRScannerActivity.ClientThread());
        cThread.start();

        mScannerView.stopCamera();

        Intent activity = new Intent(this, FragmentActivity.class);
        startActivity(activity);

        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
    }

    public class ClientThread implements Runnable {

        public void run() {
            try {
                System.out.println("Connecting!!!");
                InetAddress serverAddr = InetAddress.getByName("185.43.210.233"); //MANEL
                //InetAddress serverAddr = InetAddress.getByName("192.168.1.66"); //CASA

                Socket socket = new Socket(serverAddr, 10001);

                System.out.println("Connected!!!");
                connected = true;
                DataOutputStream dOut;

                String str = getQrcode();

                try {
                    System.out.println("Sending message!!!!");
                    dOut = new DataOutputStream(socket.getOutputStream());
                    dOut.writeBytes(str);
                    dOut.flush(); // Send off the data
                    dOut.close();
                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);
                }

                socket.close();
                System.out.println("Socket closed!!!!");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }

}
