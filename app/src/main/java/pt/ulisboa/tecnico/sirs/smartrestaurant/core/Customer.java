package pt.ulisboa.tecnico.sirs.smartrestaurant.core;

/**
 * Created by Miguel on 16/11/2016.
 */

public class Customer {

    private static Menu menu = new Menu();
    private static Order order = new Order();

    public static Order getOrder() { return order; }

}
