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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;

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

                try {
                    //Send the customerID
                    oos = new DataOutputStream(socket.getOutputStream());
                    oos.writeBytes(o);
                    oos.flush();

                    //Receive food Payment
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s;
                    while ((s=in.readLine())!=null) {
                        String[] splitted = s.split(" . ");
                        Customer.setValueToPay(Float.parseFloat(splitted[1]));
                        String[] aux5 = splitFoodToPay(splitted[0]);
                        System.out.print(aux5);
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


    public String[] splitFoodToPay(String splitted){
        String[] aux1 = splitted.split("\\{");
        String[] aux2 = aux1[1].split("\\}");
        String[] aux3 = aux2[0].split(",");
        String[] aux4 = null;
        int i = 0;
        while(i < aux3.length){
            aux4 = aux3[i].split(" : ");
        }
        return aux4;

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
