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
}
