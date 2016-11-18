package pt.ulisboa.tecnico.sirs.smartrestaurant.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;

public class DesertsFragment extends Fragment {

    View view;

    TextView bBrownieTextView;
    TextView bCheeseTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_deserts, container, false);
        initializeElements();
        return view;
    }

    private void initializeElements() {
        bBrownieTextView = (TextView) view.findViewById(R.id.bBrownieTextView);
        bCheeseTextView = (TextView) view.findViewById(R.id.bCheeseTextView);
        int quantity = Customer.getOrder().getOrderQuantity("bBronie");
        bBrownieTextView.setText(""+quantity);
        quantity = Customer.getOrder().getOrderQuantity("bCheese");
        bCheeseTextView.setText(""+quantity);

        Button bBrownieAddButton = (Button) view.findViewById(R.id.bBrownieAddButton);
        Button bBrownieSubButton = (Button) view.findViewById(R.id.bBrownieSubButton);
        Button bCheeseAddButton = (Button) view.findViewById(R.id.bCheeseAddButton);
        Button bCheeseSubButton = (Button) view.findViewById(R.id.bCheeseSubButton);
        bBrownieAddButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().addMenuItem("bBronie");
                int quantity = Customer.getOrder().getOrderQuantity("bBronie");
                bBrownieTextView.setText(""+quantity);
            }
        });
        bBrownieSubButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().removeMenuItem("bBronie");
                int quantity = Customer.getOrder().getOrderQuantity("bBronie");
                bBrownieTextView.setText(""+quantity);
            }
        });
        bCheeseAddButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().addMenuItem("bCheese");
                int quantity = Customer.getOrder().getOrderQuantity("bCheese");
                bCheeseTextView.setText(""+quantity);
            }
        });
        bCheeseSubButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().removeMenuItem("bCheese");
                int quantity = Customer.getOrder().getOrderQuantity("bCheese");
                bCheeseTextView.setText(""+quantity);
            }
        });
    }
}
