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
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;

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

                Fragment fragment = new FinalFragment();
                replaceFragment(fragment, "FINAL_FRAGMENT");
                Customer.getOrder().orderDone();
            }
        });
        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new ToPayFragment();
                replaceFragment(fragment, "TO_PAY_FRAGMENT");
            }
        });
    }



    public class ClientThread implements Runnable {

        public void run() {
            try {
                System.out.println("Connecting!!!");
                InetAddress serverAddr = InetAddress.getByName("185.43.210.233"); //MANEL
                //InetAddress serverAddr = InetAddress.getByName("192.168.1.66"); //CASA

                Socket socket = new Socket(serverAddr, 10003);

                System.out.println("Connected!!!");
                connected = true;
                DataOutputStream oos = null;
                String o = Customer.getPaymentCode() + "";
                System.out.println("Payment Code: " + o);
                try {
                    //Send the Payment Code
                    oos = new DataOutputStream(socket.getOutputStream());
                    oos.writeBytes(o);
                    oos.flush();

                    //Receive Payment Response
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s;
                    while ((s=in.readLine())!=null) {
                        System.out.println("Messagem recebida: " + s);
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
