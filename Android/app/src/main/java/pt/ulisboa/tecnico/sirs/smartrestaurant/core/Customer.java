package pt.ulisboa.tecnico.sirs.smartrestaurant.core;

import android.util.ArrayMap;

/**
 * Created by Miguel on 16/11/2016.
 */

public class Customer {

    private static Menu menu = new Menu();
    private static Order order = new Order();
    private static int tableID = -1;
    private static int customerID;
    private static float valueToPay;
    private static ArrayMap<String, Float> foodToPay = new ArrayMap<String, Float>();
    private static String paymentCode;
    private static String cardNumber;
    private static String experationDate;
    private static String cardCSC;
    private static String name;
    private static String taxNumber;
    private static String email;

    public static Order getOrder() { return order; }

    public static void setTableID(int tableID) { Customer.tableID = tableID; }

    public static int getTableID() { return tableID; }

    public static int getCustomerID() { return customerID; }

    public static void setCustomerID(int customerID) { Customer.customerID = customerID; }

    public static Menu getMenu() { return menu; }

    public static float getValueToPay() { return valueToPay; }

    public static void setValueToPay(float valueToPay) { Customer.valueToPay = valueToPay; }

    public static ArrayMap<String, Float> getFoodToPay() { return foodToPay; }

    public static void setFoodToPay(ArrayMap<String, Float> foodToPay) { Customer.foodToPay = foodToPay; }

    public static String getPaymentCode() { return paymentCode; }

    public static void setPaymentCode(String paymentCode) { Customer.paymentCode = paymentCode; }

    public static String getCardNumber() { return cardNumber; }

    public static void setCardNumber(String cardNumber) { Customer.cardNumber = cardNumber; }

    public static String getName() { return name; }

    public static void setName(String name) { Customer.name = name; }

    public static String getExperationDate() { return experationDate; }

    public static void setExperationDate(String experationDate) { Customer.experationDate = experationDate; }

    public static String getCardCSC() { return cardCSC; }

    public static void setCardCSC(String cardCSC) { Customer.cardCSC = cardCSC; }

    public static String getTaxNumber() { return taxNumber; }

    public static void setTaxNumber(String taxNumber) { Customer.taxNumber = taxNumber; }

    public static String getEmail() { return email; }

    public static void setEmail(String email) { Customer.email = email; }

}
