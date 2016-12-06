package pt.ulisboa.tecnico.sirs.smartrestaurant.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;
import pt.testing.security.Signatures;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.NaiveTrustManager;

public class PaymentInfoFragment extends Fragment {

    View view;

    private boolean connected = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActivity().setTitle("PayDal");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment_info, container, false);
        initializeElements();
        return view;
    }

    private void initializeElements() {
        Button payButton = (Button) view.findViewById(R.id.payButton);
        Button backButton = (Button) view.findViewById(R.id.backButton);
        payButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                EditText cardNumber = (EditText) view.findViewById(R.id.cardNumber);
                EditText cardExperation = (EditText) view.findViewById(R.id.cardExperation);
                EditText cardCSC = (EditText) view.findViewById(R.id.cardCSC);
                EditText customerName = (EditText) view.findViewById(R.id.customerName);
                EditText customerTaxNumber = (EditText) view.findViewById(R.id.customerTaxNumber);
                EditText customerEmail = (EditText) view.findViewById(R.id.customerEmail);
                Customer.setCardNumber(cardNumber.getText().toString());
                Customer.setExperationDate(cardExperation.getText().toString());
                Customer.setCardCSC(cardCSC.getText().toString());
                Customer.setName(customerName.getText().toString());
                Customer.setTaxNumber(customerTaxNumber.getText().toString());
                Customer.setEmail(customerEmail.getText().toString());

                try {
                    Thread cThread = new Thread(new PaymentInfoFragment.ClientThread());
                    cThread.start();
                    cThread.join();
                } catch (Exception e) {
                }

                Fragment fragment = new FinalFragment();
                replaceFragment(fragment, "FINAL_FRAGMENT");
                Customer.getOrder().orderDone();
            }
        });
    }

    private static SSLSocketFactory sslSocketFactory;

    /**
     * Returns a SSL Factory instance that accepts all server certificates.
     * <pre>SSLSocket sock =
     *     (SSLSocket) getSocketFactory.createSocket ( host, 443 ); </pre>
     *
     * @return An SSL-specific socket factory.
     **/
    public SSLSocketFactory getSocketFactory() {
        if (sslSocketFactory == null) {
            try {
                TrustManager[] tm = new TrustManager[]{new NaiveTrustManager(this.getActivity())};
                SSLContext context = SSLContext.getInstance("TLSv1.2");
                context.init(new KeyManager[0], tm, new SecureRandom());

                sslSocketFactory = (SSLSocketFactory) context.getSocketFactory();

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
                SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverAddr, 10003);

                // Set protocol (we want TLSv1.2)
                String[] protocols = socket.getEnabledProtocols(); // gets available protocols
                for (String s : protocols) {
                    if (s.equalsIgnoreCase("TLSv1.2")) {
                        socket.setEnabledProtocols(new String[]{s}); // set protocol to TLSv1.2
                        System.out.println("CIPHER: " + socket.getEnabledCipherSuites()[0]);
                        System.out.println("Using: " + socket.getEnabledProtocols()[0]);
                    }
                }

                System.out.println("Connected!!!");
                connected = true;
                DataOutputStream oos = null;
                String o = Customer.getPaymentCode();
                System.out.println("Payment Code: " + o);
                try {
                    //Request Service RandomID
                    oos = new DataOutputStream(socket.getOutputStream());
                    oos.writeBytes("RandomID");
                    oos.flush();


                    //Send the Payment Code
                    oos = new DataOutputStream(socket.getOutputStream());
                    oos.writeBytes(o);
                    oos.flush();

                    //Receive Payment Response
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s, hash = null, msg = null;
                    while ((s = in.readLine()) != null) {
                        String[] tokens = s.split(" ");
                        msg = tokens[0];
                        hash = tokens[1];
                        System.out.println("Mensagem recebida: <" + msg + "><" + hash + ">");
                    }

                    // get hash and message


                    // load Pay Dal certificate
                    //InputStream inStream = getActivity().getResources().openRawResource(R.raw.server);
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    InputStream inStream = getActivity().getResources().openRawResource(R.raw.pay_dal);
                    X509Certificate certPayDal = (X509Certificate) cf.generateCertificate(inStream);
                    PublicKey pubKeyPayDal = certPayDal.getPublicKey();
                    boolean isValid;
                    Signature sig = Signature.getInstance("SHA256WithRSA");
                    sig.initVerify(pubKeyPayDal);
                    sig.update(msg.getBytes());
                    isValid = sig.verify(hash.getBytes());
                    // get Pay Dal public key

                    inStream.close();



                    // verify signature
                    /*System.out.println("Verifying signature... ");
                    if (hash != null && msg != null) {
                        isValid = verifyDigitalSignature(hash.getBytes("UTF-8"), msg.getBytes("UTF-8"), pubKeyPayDal, certPayDal);
                    }*/
                    if (isValid) {
                        System.out.println("The digital signature is valid!");
                    } else {
                        // return false;
                        throw new RuntimeException("********** The digital signature is NOT valid! **********");
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

    public void replaceFragment(Fragment fragment, String fragmentTag) {
        String backStateName = fragment.getClass().getName();

        FragmentManager fm = getFragmentManager();
        fm.popBackStack(MenuFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fm.beginTransaction();
            //ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            ft.replace(R.id.content_frame, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, PublicKey publicKey, X509Certificate cert){
        boolean verify = false;
        try {
            // verify the signature with the public key
            Signature sig = Signature.getInstance("SHA256WithRSA");
            sig.initVerify(cert);
            sig.update(bytes);
            verify = sig.verify(cipherDigest);
            return verify;
        } catch(Exception se) {
            System.err.println("Caught exception while verifying signature " + se);
            return verify;

        }
    }
}
