package pt.ulisboa.tecnico.sirs.smartrestaurant.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;

public class DrinksFragment extends Fragment {

    View view;

    TextView waterTextView;
    TextView cokeTextView;
    TextView wineTextView;
    TextView beerTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_drinks, container, false);
        this.getActivity().findViewById(R.id.fab).setVisibility(view.VISIBLE);
        initializeElements();
        return view;
    }

    private void initializeElements() {
        waterTextView = (TextView) view.findViewById(R.id.waterTextView);
        cokeTextView = (TextView) view.findViewById(R.id.cokeTextView);
        wineTextView = (TextView) view.findViewById(R.id.wineTextView);
        beerTextView = (TextView) view.findViewById(R.id.beerTextView);
        int quantity = Customer.getOrder().getOrderQuantity("water");
        waterTextView.setText(""+quantity);
        quantity = Customer.getOrder().getOrderQuantity("coke");
        cokeTextView.setText(""+quantity);
        quantity = Customer.getOrder().getOrderQuantity("wine");
        wineTextView.setText(""+quantity);
        quantity = Customer.getOrder().getOrderQuantity("beer");
        beerTextView.setText(""+quantity);

        Button waterAddButton = (Button) view.findViewById(R.id.waterAddButton);
        Button waterSubButton = (Button) view.findViewById(R.id.waterSubButton);
        Button cokeAddButton = (Button) view.findViewById(R.id.cokeAddButton);
        Button cokeSubButton = (Button) view.findViewById(R.id.cokeSubButton);
        Button wineAddButton = (Button) view.findViewById(R.id.wineAddButton);
        Button wineSubButton = (Button) view.findViewById(R.id.wineSubButton);
        Button beerAddButton = (Button) view.findViewById(R.id.beerAddButton);
        Button beerSubButton = (Button) view.findViewById(R.id.beerSubButton);
        waterAddButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().addMenuItem("water");
                int quantity = Customer.getOrder().getOrderQuantity("water");
                waterTextView.setText(""+quantity);
            }
        });
        waterSubButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().removeMenuItem("water");
                int quantity = Customer.getOrder().getOrderQuantity("water");
                waterTextView.setText(""+quantity);
            }
        });
        cokeAddButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().addMenuItem("coke");
                int quantity = Customer.getOrder().getOrderQuantity("coke");
                cokeTextView.setText(""+quantity);
            }
        });
        cokeSubButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().removeMenuItem("coke");
                int quantity = Customer.getOrder().getOrderQuantity("coke");
                cokeTextView.setText(""+quantity);
            }
        });
        wineAddButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().addMenuItem("wine");
                int quantity = Customer.getOrder().getOrderQuantity("wine");
                wineTextView.setText(""+quantity);
            }
        });
        wineSubButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().removeMenuItem("wine");
                int quantity = Customer.getOrder().getOrderQuantity("wine");
                wineTextView.setText(""+quantity);
            }
        });
        beerAddButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().addMenuItem("beer");
                int quantity = Customer.getOrder().getOrderQuantity("beer");
                beerTextView.setText(""+quantity);
            }
        });
        beerSubButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().removeMenuItem("beer");
                int quantity = Customer.getOrder().getOrderQuantity("beer");
                beerTextView.setText(""+quantity);
            }
        });
        Button backButton = (Button) view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new MenuFragment();
                replaceFragment(fragment, "MENU_FRAGMENT");
            }
        });
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
