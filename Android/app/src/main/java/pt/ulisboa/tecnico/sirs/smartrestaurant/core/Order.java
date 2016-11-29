package pt.ulisboa.tecnico.sirs.smartrestaurant.core;

import android.util.ArrayMap;

/**
 * Created by Miguel on 16/11/2016.
 */

public class Order {
    private ArrayMap<String, Integer> ordersList = new ArrayMap<String, Integer>();

    public void addMenuItem(String item) {
        if(ordersList.containsKey(item)) {
            ordersList.put(item, ordersList.get(item) + 1);
        }
        else{
            ordersList.put(item, 1);
        }
    }

    public void removeMenuItem(String item) {
        if(ordersList.containsKey(item)) {
            if(ordersList.get(item) - 1 <= 0) {
                ordersList.remove(item);
            }
            else {
                ordersList.put(item, ordersList.get(item) - 1);
            }
        }
    }

    public int getOrderQuantity(String item) {
        if(ordersList.containsKey(item) || ordersList.get(item) != null) {
            return ordersList.get(item);
        }
        else {
            return 0;
        }
    }

    public ArrayMap<String, Integer> getOrders() { return ordersList; }

    public void orderDone() {
        ordersList = new ArrayMap<String, Integer>();
    }
}
