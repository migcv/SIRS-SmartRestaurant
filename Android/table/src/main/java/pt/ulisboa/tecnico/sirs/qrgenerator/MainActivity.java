package pt.ulisboa.tecnico.sirs.qrgenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.Semaphore;

import javax.net.ssl.*;

public class MainActivity extends AppCompatActivity {
    ImageView qrCodeImageview;
    String QRcode = null;
    String newQRCode = null;
    String tableID;
    boolean QRCode_done = false;
    public final static int WIDTH=500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Table");
        System.out.println("Creating Thread!!");
        Thread cThread = new Thread(new ClientThread());
        cThread.start();
        try {
            cThread.join();
        } catch (Exception e) {}
        QRcode = newQRCode;
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
                                QRCode_done = true;
                            } // end of run method
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {}
        Thread updateT = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60 * 1000);
                        Thread cThread = new Thread(new ClientThread());
                        cThread.start();
                        cThread.join();
                    } catch (Exception e) {}
                }
            }
        });
        updateT.start();
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

    public void restartActivity() {
        Intent scanner = new Intent(this, MainActivity.class);
        startActivity(scanner);
    }

    public class ClientThread implements Runnable {
        public void run() {
            try {
                System.out.println("Connecting!");

                InetAddress serverAddr = InetAddress.getByName(Constants.IP);

                // Create an instance of SSLSocket (TRUST ONLY OUR CERT)
                SSLSocketFactory sslSocketFactory = getSocketFactory();
                SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverAddr, 10000);

                // Set protocol (we want TLSv1.2)
                String[] protocols = socket.getEnabledProtocols(); // gets available protocols
                for(String s: protocols) {
                    if(s.equalsIgnoreCase("TLSv1.2")) {
                        socket.setEnabledProtocols(new String[] {s}); // set protocol to TLSv1.2
                        System.out.println("CIPHER: "+ socket.getEnabledCipherSuites()[0]);
                        System.out.println("Using: "+socket.getEnabledProtocols()[0]);
                    }
                }
                System.out.println("Connected!");
                DataOutputStream oos = null;
                try {
                    if (QRcode != null) {
                        //Send Service UpdateQR
                        System.out.println("Service: UpdateQR!");
                        oos = new DataOutputStream(socket.getOutputStream());
                        oos.write("UpdateQR".getBytes());
                        oos.flush();
                        //Send the tableID
                        oos = new DataOutputStream(socket.getOutputStream());
                        oos.write((""+tableID).getBytes());
                        oos.flush();
                        //Receive Response
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String s;
                        while ((s = in.readLine()) != null) {
                            System.out.println("Received Message: " + s);
                            if(!s.equals("0")) {
                                restartActivity();
                            }
                        }
                    } else {
                        //Send Service SendQR
                        System.out.println("Service: SendQR!");
                        oos = new DataOutputStream(socket.getOutputStream());
                        oos.write("SendQR".getBytes());
                        oos.flush();
                        oos.close();
                        //Receive QRCode
                        System.out.println("Receiving QRCode!");
                        byte[] messageByte = new byte[1000];
                        int bytesRead = 0;
                        boolean end = false;
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        while (!end) {
                            bytesRead = in.read(messageByte);
                            System.out.println("Received message!");
                            String messageString = new String(messageByte, 0, bytesRead);
                            System.out.println("MESSAGE: " + messageString);
                            String[] messageSplitted = messageString.split(" : ");
                            newQRCode = messageSplitted[0];
                            tableID = messageSplitted[1];
                            System.out.println("QRCode: " + newQRCode + " | TableID: " + tableID);
                            if (messageString.length() == bytesRead) {
                                end = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);
                }
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
            }
        }
    }
}
