package pt.ulisboa.tecnico.sirs.smartrestaurant.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.NaiveTrustManager;

public class ToPayFragment extends Fragment {

    View view;

    private boolean connected = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_to_pay, container, false);
        try {
            Thread cThread = new Thread(new ToPayFragment.ClientThread());
            cThread.start();
            cThread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        initializeElements();
        return view;
    }

    private void initializeElements() {
        Button payButton = (Button) view.findViewById(R.id.payButton);
        Button backButton = (Button) view.findViewById(R.id.backButton);
        payButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new PaymentInfoFragment();
                replaceFragment(fragment, "PAYMENT_INFO_FRAGMENT");
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
        TextView totalPrice = (TextView) view.findViewById(R.id.totalPrice);

        float price;
        int quantity;

        // Burgers
        if(Customer.getFoodToPay().containsKey("bPerfect")) {
            price = Customer.getFoodToPay().get("bPerfect");
            quantity = (int) (price / Customer.getMenu().getBurgersList().get("bPerfect"));
            bPerfectQ.setText("" + quantity);
            bPerfectP.setText("" + price + "€");
        }
        if(Customer.getFoodToPay().containsKey("bToque")) {
            price = Customer.getFoodToPay().get("bToque");
            quantity = (int) (price / Customer.getMenu().getBurgersList().get("bToque"));
            bToqueQ.setText("" + quantity);
            bToqueP.setText("" + price + "€");
        }
        if(Customer.getFoodToPay().containsKey("bCool")) {
            price = Customer.getFoodToPay().get("bCool");
            quantity = (int) (price / Customer.getMenu().getBurgersList().get("bCool"));
            bCoolQ.setText("" + quantity);
            bCoolP.setText("" + price + "€");
        }
        if(Customer.getFoodToPay().containsKey("bSpicy")) {
            price = Customer.getFoodToPay().get("bSpicy");
            quantity = (int) (price / Customer.getMenu().getBurgersList().get("bSpicy"));
            bSpicyQ.setText("" + quantity);
            bSpicyP.setText("" + price + "€");
        }
        // Drinks
        if(Customer.getFoodToPay().containsKey("water")) {
            price = Customer.getFoodToPay().get("water");
            quantity = (int) (price / Customer.getMenu().getDrinksList().get("water"));
            waterQ.setText("" + quantity);
            waterP.setText("" + price + "€");
        }
        if(Customer.getFoodToPay().containsKey("coke")) {
            price = Customer.getFoodToPay().get("coke");
            quantity = (int) (price / Customer.getMenu().getDrinksList().get("coke"));
            cokeQ.setText("" + quantity);
            cokeP.setText("" + price + "€");
        }
        if(Customer.getFoodToPay().containsKey("wine")) {
            price = Customer.getFoodToPay().get("wine");
            quantity = (int) (price / Customer.getMenu().getDrinksList().get("wine"));
            wineQ.setText("" + quantity);
            wineP.setText("" + price + "€");
        }
        if(Customer.getFoodToPay().containsKey("beer")) {
            price = Customer.getFoodToPay().get("beer");
            quantity = (int) (price / Customer.getMenu().getDrinksList().get("beer"));
            beerQ.setText("" + quantity);
            beerP.setText("" + price + "€");
        }
        // Desert
        if(Customer.getFoodToPay().containsKey("bBrownie")) {
            price = Customer.getFoodToPay().get("bBrownie");
            quantity = (int) (price / Customer.getMenu().getDesertsList().get("bBrownie"));
            bBrownieQ.setText("" + quantity);
            bBrownieP.setText("" + price + "€");
        }
        if(Customer.getFoodToPay().containsKey("bCheese")) {
            price = Customer.getFoodToPay().get("bCheese");
            quantity = (int) (price / Customer.getMenu().getDesertsList().get("bCheese"));
            bCheeseQ.setText("" + quantity);
            bCheeseP.setText("" + price + "€");
        }
        totalPrice.setText("" + Customer.getValueToPay() + "€");
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

                //Socket socket = new Socket(serverAddr, 10003);

                System.out.println("Connected!!!");
                connected = true;
                DataOutputStream oos = null;
                String o = Customer.getCustomerID() + " ";
                System.out.println("Client ID  " + o);
                try {
                    //Send Service ReceiveIDToPay
                    oos = new DataOutputStream(socket.getOutputStream());
                    oos.writeBytes("ReceiveIDToPay");
                    oos.flush();

                    //Send the customerID
                    oos = new DataOutputStream(socket.getOutputStream());
                    oos.writeBytes(o);
                    oos.flush();

                    //Receive food Payment
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s;
                    while ((s=in.readLine())!=null) {
                        System.out.println("Received Message: " + s);
                        String[] splitted = s.split(" . ");
                        Customer.setPaymentCode(splitted[2]);
                        Customer.setValueToPay(Float.parseFloat(splitted[1]));
                        ArrayMap<String, Float> aux5 = splitFoodToPay(splitted[0]);
                        Customer.setFoodToPay(aux5);
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


    public ArrayMap<String, Float> splitFoodToPay(String splitted){
        String[] aux1 = splitted.split("\\{");
        String[] aux2 = aux1[1].split("\\}");
        String[] aux3 = aux2[0].split(",");

        ArrayMap<String, Float> foodToPay = new ArrayMap<String, Float>();
        for(int i = 0; i < aux3.length; i++){
            String[] aux4 = aux3[i].split(": ");
            foodToPay.put(aux4[0].split("\'")[1], Float.parseFloat(aux4[1]));
        }
        return foodToPay;
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
