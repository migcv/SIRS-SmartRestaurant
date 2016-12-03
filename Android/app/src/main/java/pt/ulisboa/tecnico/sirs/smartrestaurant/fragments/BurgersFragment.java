package pt.ulisboa.tecnico.sirs.smartrestaurant.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;

public class BurgersFragment extends Fragment {

    View view;

    TextView bPerfectTextView;
    TextView bToqueTextView;
    TextView bCoolTextView;
    TextView bSpicyTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_burgers, container, false);
        this.getActivity().findViewById(R.id.fab).setVisibility(view.VISIBLE);
        initializeElements();
        return view;
    }

    private void initializeElements() {
        bPerfectTextView = (TextView) view.findViewById(R.id.bPerfectTextView);
        bToqueTextView = (TextView) view.findViewById(R.id.bToqueTextView);
        bCoolTextView = (TextView) view.findViewById(R.id.bCoolTextView);
        bSpicyTextView = (TextView) view.findViewById(R.id.bSpicyTextView);
        int quantity = Customer.getOrder().getOrderQuantity("bPerfect");
        bPerfectTextView.setText(""+quantity);
        quantity = Customer.getOrder().getOrderQuantity("bToque");
        bToqueTextView.setText(""+quantity);
        quantity = Customer.getOrder().getOrderQuantity("bCool");
        bCoolTextView.setText(""+quantity);
        quantity = Customer.getOrder().getOrderQuantity("bSpicy");
        bSpicyTextView.setText(""+quantity);

        Button bPerfectAddButton = (Button) view.findViewById(R.id.bPerfectAddButton);
        Button bPerfectSubButton = (Button) view.findViewById(R.id.bPerfectSubButton);
        Button bCoolAddButton = (Button) view.findViewById(R.id.bCoolAddButton);
        Button bCoolSubButton = (Button) view.findViewById(R.id.bCoolSubButton);
        Button bToqueAddButton = (Button) view.findViewById(R.id.bToqueAddButton);
        Button bToqueSubButton = (Button) view.findViewById(R.id.bToqueSubButton);
        Button bSpicyAddButton = (Button) view.findViewById(R.id.bSpicyAddButton);
        Button bSpicySubButton = (Button) view.findViewById(R.id.bSpicySubButton);
        bPerfectAddButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().addMenuItem("bPerfect");
                int quantity = Customer.getOrder().getOrderQuantity("bPerfect");
                bPerfectTextView.setText(""+quantity);
            }
        });
        bPerfectSubButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().removeMenuItem("bPerfect");
                int quantity = Customer.getOrder().getOrderQuantity("bPerfect");
                bPerfectTextView.setText(""+quantity);
            }
        });
        bToqueAddButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().addMenuItem("bToque");
                int quantity = Customer.getOrder().getOrderQuantity("bToque");
                bToqueTextView.setText(""+quantity);
            }
        });
        bToqueSubButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().removeMenuItem("bToque");
                int quantity = Customer.getOrder().getOrderQuantity("bToque");
                bToqueTextView.setText(""+quantity);
            }
        });
        bCoolAddButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().addMenuItem("bCool");
                int quantity = Customer.getOrder().getOrderQuantity("bCool");
                bCoolTextView.setText(""+quantity);
            }
        });
        bCoolSubButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().removeMenuItem("bCool");
                int quantity = Customer.getOrder().getOrderQuantity("bCool");
                bCoolTextView.setText(""+quantity);
            }
        });
        bSpicyAddButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().addMenuItem("bSpicy");
                int quantity = Customer.getOrder().getOrderQuantity("bSpicy");
                bSpicyTextView.setText(""+quantity);
            }
        });
        bSpicySubButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Customer.getOrder().removeMenuItem("bSpicy");
                int quantity = Customer.getOrder().getOrderQuantity("bSpicy");
                bSpicyTextView.setText(""+quantity);
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
