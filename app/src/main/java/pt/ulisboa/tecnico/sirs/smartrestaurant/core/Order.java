package pt.ulisboa.tecnico.sirs.smartrestaurant.core;

import android.util.ArrayMap;

/**
 * Created by Miguel on 16/11/2016.
 */

public class Order {
    private ArrayMap<String, Integer> orderList = new ArrayMap<String, Integer>();

    public void addMenuItem(String item) {
        if(orderList.containsKey(item)) {
            orderList.put(item, orderList.get(item) + 1);
        }
        else{
            orderList.put(item, 1);
        }
    }

    public void removeMenuItem(String item) {
        if(orderList.containsKey(item)) {
            if(orderList.get(item) <= 0) {
                orderList.put(item, 0);
            }
            else {
                orderList.put(item, orderList.get(item) - 1);
            }
        }
    }

    public int getOrderQuantity(String item) {
        if(orderList.containsKey(item) || orderList.get(item) != null) {
            return orderList.get(item);
        }
        else {
            return 0;
        }
    }

    public ArrayMap<String, Integer> getOrders() { return orderList; }
}
