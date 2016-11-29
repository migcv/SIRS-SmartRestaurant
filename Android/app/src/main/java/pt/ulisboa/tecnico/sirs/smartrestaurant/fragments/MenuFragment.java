package pt.ulisboa.tecnico.sirs.smartrestaurant.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;

public class MenuFragment extends Fragment {

    View view;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        Button foodButton = (Button) view.findViewById(R.id.foodButton);
        Button drinksButton = (Button) view.findViewById(R.id.drinksButton);
        Button desertsButton = (Button) view.findViewById(R.id.desertsButton);
        Button payButton = (Button) view.findViewById(R.id.payButton);
        foodButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new BurgersFragment();
                replaceFragment(fragment, "BURGERS_FRAGMENT");
            }
        });
        drinksButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new DrinksFragment();
                replaceFragment(fragment, "DRINKS_FRAGMENT");
            }
        });
        desertsButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new DesertsFragment();
                replaceFragment(fragment, "DESERTS_FRAGMENT");
            }
        });
        payButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new ToPayFragment();
                replaceFragment(fragment, "TO_PAY_FRAGMENT");
            }
        });
        return view;
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
