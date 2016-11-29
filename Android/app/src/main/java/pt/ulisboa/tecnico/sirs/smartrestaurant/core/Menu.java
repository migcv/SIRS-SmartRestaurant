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
        burgersList.put("bPerfect", 7.5);
        burgersList.put("bToque", 7.0);
        burgersList.put("bCool", 6.5);
        burgersList.put("bSpicy", 6.5);

        drinksList.put("water", 1.5);
        drinksList.put("coke", 1.5);
        drinksList.put("wine", 1.0);
        drinksList.put("beer", 1.0);

        desertsList.put("bBrownie", 3.0);
        desertsList.put("bCheese", 3.0);
    }

    public ArrayMap<String, Double> getBurgersList() { return burgersList; }

    public ArrayMap<String, Double> getDrinksList() { return drinksList; }

    public ArrayMap<String, Double> getDesertsList() { return desertsList; }
}
