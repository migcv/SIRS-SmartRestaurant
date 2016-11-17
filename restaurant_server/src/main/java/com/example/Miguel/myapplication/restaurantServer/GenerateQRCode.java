package com.example.Miguel.myapplication.restaurantServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * The object model for the data we are sending through endpoints
 */
public class GenerateQRCode {

    private String myData;
    private int uniqueIDTable;
    private int seatsTable;
    private int verifyResult;
    //private HashMap<Integer, ArrayList<Object>> tableInfo = new HashMap<>();
    private ArrayList<Object[]> tableInfo = new ArrayList<Object[]>(); // { ID, QRCode, seats}
    public ArrayList<String> info = new ArrayList<>();

    public String getData() { return myData;}

    public void setData(String data) { myData = data;}

    public int getUniqueIDTable() { return uniqueIDTable;}

    public void setUniqueIDTable(int uniqueIDTable) { this.uniqueIDTable = uniqueIDTable;}

    public int getSeatsTable() { return seatsTable;}

    public void setSeatsTable(int seatsTable) { this.seatsTable = seatsTable;}

    public ArrayList<String> getInfo(){ return info;}

    public Object[] generateTableInfo(){
        Random r = new Random();
        int result = r.nextInt(10-2) + 2;
        int id = tableInfo.size() + 1;

        setUniqueIDTable(id);
        setSeatsTable(result);
        setData(UUID.randomUUID().toString());

        Object[] info = {getUniqueIDTable(), getData(), getSeatsTable()};
        tableInfo.add(info);
        return info;
    }

    public ArrayList<String> sendToTable(){
        generateTableInfo();
        info.add(getData());
        info.add(""+getUniqueIDTable());
        return info;
    }

    public boolean verifyQR(String name){
        for(int i = 0; i < tableInfo.size(); i++) {
            if(tableInfo.get(i)[1].equals(name)) {
                return true;
            }
        }
        return false;
    }


}