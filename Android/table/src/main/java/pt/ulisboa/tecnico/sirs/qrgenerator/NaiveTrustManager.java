package pt.ulisboa.tecnico.sirs.qrgenerator;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Path;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate ;
import javax.net.ssl.X509TrustManager;

/**
 * This Trust Manager is "naive" because it trusts everyone.
 **/
public class NaiveTrustManager implements X509TrustManager {
    private Context context;

    public NaiveTrustManager(Context context) {
        this.context = context;
    }
    /**
     * Doesn't throw an exception, so this is how it approves a certificate.
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], String)
     **/
    public void checkClientTrusted ( X509Certificate[] cert, String authType )
            throws CertificateException
    {
    }

    /**
     * Doesn't throw an exception, so this is how it approves a certificate.
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], String)
     **/
    public void checkServerTrusted ( X509Certificate[] cert, String authType )
            throws CertificateException
    {
        System.out.println("CERTIFICATE Received: <" + cert[0].getIssuerDN().getName() + " | " + cert[0].getSerialNumber() + ">");
        X509Certificate[] acceptedCertificats = getAcceptedIssuers();
        for(int i = 0; i < acceptedCertificats.length; i++) {
            if(acceptedCertificats[i].getSerialNumber().equals(cert[0].getSerialNumber())) {
                System.out.println("CERTIFICATE received ACCEPTED");
                return;
            }
        }
        System.out.println("CERTIFICATE received REJECTED");
        throw new CertificateException();
     }

    /**
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     **/
    public X509Certificate[] getAcceptedIssuers()
    {
        //X509Certificate[] trustedAnchors = super.getAcceptedIssuers();

        /* Create a new array with room for an additional trusted certificate. */
        //X509Certificate[] myTrustedAnchors = new X509Certificate[trustedAnchors.length + 1];
        //System.arraycopy(trustedAnchors, 0, myTrustedAnchors, 0, trustedAnchors.length);

        X509Certificate[] myTrustedAnchors = new X509Certificate[1];

        /* Load your certificate.

            Thanks to http://stackoverflow.com/questions/11857417/x509trustmanager-override-without-allowing-all-certs
            for this bit.
        */
        X509Certificate cert = null;
        try {
            InputStream inStream = context.getResources().openRawResource(R.raw.server);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(inStream);
            inStream.close();
        } catch (Exception e) {
            System.out.println("ERROR reading local certificate");
        }

        /* Add your anchor cert as the last item in the array. */
        myTrustedAnchors[0] = cert;

        return myTrustedAnchors;
    }
}