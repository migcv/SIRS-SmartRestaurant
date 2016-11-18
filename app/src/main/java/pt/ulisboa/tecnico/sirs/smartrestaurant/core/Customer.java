package pt.ulisboa.tecnico.sirs.smartrestaurant.core;

/**
 * Created by Miguel on 16/11/2016.
 */

public class Customer {

    private static Menu menu = new Menu();
    private static Order order = new Order();
    private static int tableID = -1;
    private static int customerID;

    public static Order getOrder() { return order; }

    public static void setTableID(int tableID) { Customer.tableID = tableID; }

    public static int getTableID() { return tableID; }

    public static int getCustomerID() { return customerID; }

    public static void setCustomerID(int customerID) { Customer.customerID = customerID; }

}
