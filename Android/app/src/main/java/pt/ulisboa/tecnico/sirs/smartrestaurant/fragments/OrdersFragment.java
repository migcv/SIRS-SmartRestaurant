package pt.ulisboa.tecnico.sirs.smartrestaurant.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.activities.FragmentActivity;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.NaiveTrustManager;

public class OrdersFragment extends Fragment {

    View view;

    private boolean connected = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orders, container, false);
        initializeElements();
        return view;
    }

    private void initializeElements() {
        Button sendOrderButton = (Button) view.findViewById(R.id.sendOrderButton);
        Button backButton = (Button) view.findViewById(R.id.backButton);
        sendOrderButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Creating Thread!!");
                try {
                    Thread cThread = new Thread(new OrdersFragment.ClientThread());
                    cThread.start();
                    cThread.join();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                Fragment fragment = new MenuFragment();
                replaceFragment(fragment, "MENU_FRAGMENT");
                Customer.getOrder().orderDone();
            }
        });
        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new MenuFragment();
                replaceFragment(fragment, "MENU_FRAGMENT");
            }
        });
        TextView bPerfectQ = (TextView) view.findViewById(R.id.bPerfectQuantity);
        TextView bPerfectP = (TextView) view.findViewById(R.id.bPerfectPrice);
        TextView bToqueQ = (TextView) view.findViewById(R.id.bToqueQuantity);
        TextView bToqueP = (TextView) view.findViewById(R.id.bToquePrice);
        TextView bCoolQ = (TextView) view.findViewById(R.id.bCoolQuantity);
        TextView bCoolP = (TextView) view.findViewById(R.id.bCoolPrice);
        TextView bSpicyQ = (TextView) view.findViewById(R.id.bSpicyQuantity);
        TextView bSpicyP = (TextView) view.findViewById(R.id.bSpicyPrice);
        TextView waterQ = (TextView) view.findViewById(R.id.waterQuantity);
        TextView waterP = (TextView) view.findViewById(R.id.waterPrice);
        TextView cokeQ = (TextView) view.findViewById(R.id.cokeQuantity);
        TextView cokeP = (TextView) view.findViewById(R.id.cokePrice);
        TextView wineQ = (TextView) view.findViewById(R.id.wineQuantity);
        TextView wineP = (TextView) view.findViewById(R.id.winePrice);
        TextView beerQ = (TextView) view.findViewById(R.id.beerQuantity);
        TextView beerP = (TextView) view.findViewById(R.id.beerPrice);
        TextView bBrownieQ = (TextView) view.findViewById(R.id.bBrownieQuantity);
        TextView bBrownieP = (TextView) view.findViewById(R.id.bBrowniePrice);
        TextView bCheeseQ = (TextView) view.findViewById(R.id.bCheeseQuantity);
        TextView bCheeseP = (TextView) view.findViewById(R.id.bCheesePrice);
        // Burgers
        int quantity = Customer.getOrder().getOrderQuantity("bPerfect");
        double price = Customer.getMenu().getBurgersList().get("bPerfect");
        bPerfectQ.setText(""+quantity);
        bPerfectP.setText(""+price*quantity+"€");
        quantity = Customer.getOrder().getOrderQuantity("bToque");
        price = Customer.getMenu().getBurgersList().get("bToque");
        bToqueQ.setText(""+quantity);
        bToqueP.setText(""+price*quantity+"€");
        quantity = Customer.getOrder().getOrderQuantity("bCool");
        price = Customer.getMenu().getBurgersList().get("bCool");
        bCoolQ.setText(""+quantity);
        bCoolP.setText(""+price*quantity+"€");
        quantity = Customer.getOrder().getOrderQuantity("bSpicy");
        price = Customer.getMenu().getBurgersList().get("bSpicy");
        bSpicyQ.setText(""+quantity);
        bSpicyP.setText(""+price*quantity+"€");
        // Drinks
        quantity = Customer.getOrder().getOrderQuantity("water");
        price = Customer.getMenu().getDrinksList().get("water");
        waterQ.setText(""+quantity);
        waterP.setText(""+price*quantity+"€");
        quantity = Customer.getOrder().getOrderQuantity("coke");
        price = Customer.getMenu().getDrinksList().get("coke");
        cokeQ.setText(""+quantity);
        cokeP.setText(""+price*quantity+"€");
        quantity = Customer.getOrder().getOrderQuantity("wine");
        price = Customer.getMenu().getDrinksList().get("wine");
        wineQ.setText(""+quantity);
        wineP.setText(""+price*quantity+"€");
        quantity = Customer.getOrder().getOrderQuantity("beer");
        price = Customer.getMenu().getDrinksList().get("beer");
        beerQ.setText(""+quantity);
        beerP.setText(""+price*quantity+"€");
        // Desert
        quantity = Customer.getOrder().getOrderQuantity("bBrownie");
        price = Customer.getMenu().getDesertsList().get("bBrownie");
        bBrownieQ.setText(""+quantity);
        bBrownieP.setText(""+price*quantity+"€");
        quantity = Customer.getOrder().getOrderQuantity("bCheese");
        price = Customer.getMenu().getDesertsList().get("bCheese");
        bCheeseQ.setText(""+quantity);
        bCheeseP.setText(""+price*quantity+"€");
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
                TrustManager[] tm = new TrustManager[] { new NaiveTrustManager(this.getActivity()) };
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
                InetAddress serverAddr = InetAddress.getByName("185.43.210.233"); //MANEL
                //InetAddress serverAddr = InetAddress.getByName("192.168.1.66"); //CASA

                // Create an instance of SSLSocket (TRUST ONLY OUR CERT)
                SSLSocketFactory sslSocketFactory = getSocketFactory();
                SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverAddr, 10002);

                // Set protocol (we want TLSv1.2)
                String[] protocols = socket.getEnabledProtocols(); // gets available protocols
                for(String s: protocols) {
                    if(s.equalsIgnoreCase("TLSv1.2")) {
                        socket.setEnabledProtocols(new String[] {s}); // set protocol to TLSv1.2
                        System.out.println("Using TLSv1.2");
                    }
                }

                //Socket socket = new Socket(serverAddr, 10002);

                System.out.println("Connected!!!");
                connected = true;
                ArrayMap<String, Integer> order = Customer.getOrder().getOrders();
                DataOutputStream oos = null;
                //With the order the customer send the customerID
                String o = Customer.getCustomerID() + ":";

                try {
                    //Send Order
                    for (Map.Entry<String,Integer> entry : order.entrySet()) {
                        String food = entry.getKey();
                        int quantity = entry.getValue();
                        o += food + " " + quantity + ",";
                    }
                    oos = new DataOutputStream(socket.getOutputStream());
                    oos.writeBytes(o);
                    oos.flush();
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

    public void replaceFragment(Fragment fragment, String fragmentTag){
        String backStateName = fragment.getClass().getName();

        FragmentManager fm = getFragmentManager();
        fm.popBackStack(MenuFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        boolean fragmentPopped = fm.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = fm.beginTransaction();
            //ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            ft.replace(R.id.content_frame, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
