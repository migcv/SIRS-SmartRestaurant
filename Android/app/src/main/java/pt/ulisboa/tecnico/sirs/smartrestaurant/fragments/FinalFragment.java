package pt.ulisboa.tecnico.sirs.smartrestaurant.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.activities.FragmentActivity;
import pt.ulisboa.tecnico.sirs.smartrestaurant.activities.MainActivity;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;

public class FinalFragment extends Fragment {

    View view;

    private boolean connected = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActivity().setTitle("Smart Restaurant");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_final, container, false);
        initializeElements();
        return view;
    }

    private void initializeElements() {
        Button endButton = (Button) view.findViewById(R.id.endButton);
        endButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                goToMainActivity();
            }
        });
        TextView cardNumber = (TextView) view.findViewById(R.id.cardNumber);
        TextView taxNumber = (TextView) view.findViewById(R.id.taxNumber);
        TextView customerName = (TextView) view.findViewById(R.id.customerName);
        TextView totalPaid = (TextView) view.findViewById(R.id.totalPaid);
        cardNumber.setText("Card Number: **** **** ***** " + Customer.getCardNumber().substring(12,16));
        customerName.setText("Name: " + Customer.getName());
        taxNumber.setText("Tax Number: " + Customer.getTaxNumber());
        totalPaid.setText("Total Paid: " + Customer.getValueToPay() + "€");
    }

    public void goToMainActivity() {
        Intent activity = new Intent(this.getActivity(), MainActivity.class);
        startActivity(activity);
        new Customer();
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
                String o = Customer.getCustomerID() + " ";
                System.out.println("Client ID  " + o);
                try {
                    //Send the customerID
                    System.out.println("Enviando Mambos -->");
                    oos = new DataOutputStream(socket.getOutputStream());
                    oos.writeBytes(o);
                    oos.flush();
                    System.out.println("Mambos Enviados!!");

                    //Receive food Payment
                    System.out.println("Recebendo Mambos <--");
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s;
                    while ((s=in.readLine())!=null) {
                        System.out.println("Messagem recebida: " + s);
                        String[] splitted = s.split(" . ");
                        System.out.println("MAMAS: " + splitted[1]);
                        Customer.setValueToPay(Float.parseFloat(splitted[1]));
                        System.out.println("TETAS: " + Customer.getValueToPay());
                        ArrayMap<String, Float> aux5 = splitFoodToPay(splitted[0]);
                        System.out.println("SEIOS: " + aux5);
                        Customer.setFoodToPay(aux5);
                    }
                    System.out.println("Mambos Recebidos!!");
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
        System.out.println("Aux1: " + aux1[1]);
        String[] aux2 = aux1[1].split("\\}");
        System.out.println("Aux2: " + aux2[0]);
        String[] aux3 = aux2[0].split(",");

        ArrayMap<String, Float> foodToPay = new ArrayMap<String, Float>();
        for(int i = 0; i < aux3.length; i++){
            System.out.println("i= " + i + " Aux3: " + aux3[i]);
            String[] aux4 = aux3[i].split(": ");
            System.out.println("BOAS MALTA! " + aux4[0].split("\'")[1] + " | " + aux4[1]);
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
