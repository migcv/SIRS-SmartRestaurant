package pt.ulisboa.tecnico.sirs.qrgenerator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class MainActivity extends AppCompatActivity {
    ImageView qrCodeImageview;
    String QRcode;
    public final static int WIDTH=500;
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("Creating Thread!!");
        Thread cThread = new Thread(new ClientThread());
        cThread.start();
        try {
            cThread.join();
        } catch (Exception e) {}
        setTitle("Table");

        getID();
        // create thread to avoid ANR Exception
        Thread t = new Thread(new Runnable() {
            public void run() {

                try {
                    synchronized (this) {
                        wait(5000);
                        // runOnUiThread method used to do UI task in main thread.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap bitmap = null;

                                    bitmap = encodeAsBitmap(QRcode);
                                    qrCodeImageview.setImageBitmap(bitmap);

                                } catch (WriterException e) {
                                    e.printStackTrace();
                                } // end of catch block

                            } // end of run method
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    private void getID() {
        qrCodeImageview=(ImageView) findViewById(R.id.img_qr_code_image);
    }

    // this is method call from on create and return bitmap image of QRCode.
    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    } /// end of this method

    private static SSLSocketFactory sslSocketFactory;

    /**
     * Returns a SSL Factory instance that accepts all server certificates.
     * <pre>SSLSocket sock =
     *     (SSLSocket) getSocketFactory.createSocket ( host, 443 ); </pre>
     * @return  An SSL-specific socket factory.
     **/
    public SSLSocketFactory getSocketFactory()
    {
        if ( sslSocketFactory == null ) {
            try {
                TrustManager[] tm = new TrustManager[] { new NaiveTrustManager(this) };
                SSLContext context = SSLContext.getInstance ("SSL");
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

                InetAddress serverAddr = InetAddress.getByName("185.43.210.233");
                //InetAddress serverAddr = InetAddress.getByName("192.168.1.66");

                // Create an instance of SSLSocket
                //SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                //SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverAddr, 10000);

                // Create an instance of SSLSocket (TRUSTING ANYTHING)
                SSLSocketFactory sslSocketFactory = (SSLSocketFactory) getSocketFactory();
                SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverAddr, 10000);

                // Set protocol (we want TLSv1.2)
                String[] protocols = socket.getEnabledProtocols(); // gets available protocols

               /* for(String s: protocols) {
                    if(s.equalsIgnoreCase("TLSv1.2")) {
                        socket.setEnabledProtocols(new String[] {s}); // set protocol to TLSv1.2
                        System.out.println("Using TLSv1.2");
                    }
                }*/

                // Socket socket = new Socket(serverAddr, 10000);
                System.out.println("Connected!!!");
                connected = true;

                try {
                    System.out.println("Receiving message!!!!");
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s;
                    //System.out.println("O in ta NULL!!! " + in.readLine());

                    while ((s = in.readLine()) != null) {
                        // this is the msg which will be encode in QRcode
                        System.out.println("MESSAGE=" + s);
                        QRcode = s;
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
