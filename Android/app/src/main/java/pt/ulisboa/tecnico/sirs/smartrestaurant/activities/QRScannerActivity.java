package pt.ulisboa.tecnico.sirs.smartrestaurant.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.zxing.Result;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.NaiveTrustManager;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    public String qrcode;
    private boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        try{
            cThread.join();
        }catch(InterruptedException e){

        }

        if(Customer.getTableID() != -1){
            mScannerView.stopCamera();
            Intent activity = new Intent(this, FragmentActivity.class);
            startActivity(activity);
        }
        else{
            mScannerView.stopCamera();
            Intent activity = new Intent(this, MainActivity.class);
            startActivity(activity);
        }

        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
    }

    private static SSLSocketFactory sslSocketFactory;

    /**
     * Returns a SSL Factory instance that accepts all server certificates.
     * <pre>SSLSocket sock =
     *     (SSLSocket) getSocketFactory.createSocket ( host, 443 ); </pre>
     * @return  An SSL-specific socket factory.
     **/
    public SSLSocketFactory getSocketFactory() {
        if ( sslSocketFactory == null ) {
            try {
                TrustManager[] tm = new TrustManager[] { new NaiveTrustManager(this) };
                SSLContext context = SSLContext.getInstance ("TLSv1.2");
                context.init( new KeyManager[0], tm, new SecureRandom( ) );

                sslSocketFactory = (SSLSocketFactory) context.getSocketFactory ();

            } catch (KeyManagementException e) {
                //log.error ("No SSL algorithm support: " + e.getMessage(), e);
            } catch (NoSuchAlgorithmException e) {
                //log.error ("Exception when setting up the Naive key management.", e);
            }
        }
        return sslSocketFactory;
    }

    public class ClientThread implements Runnable {

        public void run() {
            try {
                System.out.println("Connecting!!!");
                InetAddress serverAddr = InetAddress.getByName("185.43.210.233"); //MANEL
                //InetAddress serverAddr = InetAddress.getByName("192.168.1.66"); //CASA

                // Create an instance of SSLSocket (TRUST ONLY OUR CERT)
                SSLSocketFactory sslSocketFactory = getSocketFactory();
                SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverAddr, 10001);

                // Set protocol (we want TLSv1.2)
                String[] protocols = socket.getEnabledProtocols(); // gets available protocols
                for(String s: protocols) {
                    if(s.equalsIgnoreCase("TLSv1.2")) {
                        socket.setEnabledProtocols(new String[] {s}); // set protocol to TLSv1.2
                        System.out.println("CIPHER: "+ socket.getEnabledCipherSuites()[0]);
                        System.out.println("Using: "+socket.getEnabledProtocols()[0]);
                    }
                }

                //Socket socket = new Socket(serverAddr, 10001);

                System.out.println("Connected!!!");
                connected = true;
                DataOutputStream dOut;

                String str = getQrcode();

                try {

                    System.out.println("Sending message!!!!");

                    //Send Service ReceiveQR
                    dOut = new DataOutputStream(socket.getOutputStream());
                    dOut.writeBytes("ReceiveQR");
                    dOut.flush();

                    //Send QRCode string
                    dOut = new DataOutputStream(socket.getOutputStream());
                    dOut.writeBytes(str);
                    dOut.flush(); // Send off the data

                    //Receive clientID & tableID
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s;
                    while ((s=in.readLine())!=null) {
                        String[] splitted = s.split(":");
                        if (Integer.parseInt(splitted[0]) > 0) {
                            Customer.setCustomerID(Integer.parseInt(splitted[0]));
                            Customer.setTableID(Integer.parseInt(splitted[1]));
                            System.out.println("<TabbleID>:MESSAGE Correct <" + splitted[0] + " | " + splitted[1] + ">");
                        } else
                            System.out.println("MESSAGE=" + s);
                    }
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
