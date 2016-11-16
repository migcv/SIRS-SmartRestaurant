package pt.ulisboa.tecnico.sirs.smartrestaurant.core;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miguel on 16/11/2016.
 */

public class Menu {
    private ArrayMap<String, Double> burgersList = new ArrayMap<String, Double>();
    private ArrayMap<String, Double> drinksList = new ArrayMap<String, Double>();
    private ArrayMap<String, Double> desertsList = new ArrayMap<String, Double>();

    public Menu() {
        burgersList.put("b'Perfect", 7.5);
        burgersList.put("b'Toque", 7.0);
        burgersList.put("b'Happy", 7.5);
        burgersList.put("b'Cool", 6.5);
        burgersList.put("b'Smart", 6.0);
        burgersList.put("b'Spicy", 6.5);

        drinksList.put("Water", 1.5);
        drinksList.put("Coke", 1.5);
        drinksList.put("Lemonade", 1.5);
        drinksList.put("Wine", 1.0);
        drinksList.put("Beer", 1.0);

        desertsList.put("b'Brownie", 3.0);
        desertsList.put("b'Cheese", 3.0);
    }
}
