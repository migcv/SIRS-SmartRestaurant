package com.example.Miguel.myapplication.restaurantServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * The object model for the data we are sending through endpoints
 */
public class GenerateQRCode {

    private String myData;
    private String uniqueIDTable;
    private int seatsTable;
    private HashMap<Integer, ArrayList<Object>> tableInfo = new HashMap<>();
    public ArrayList<String> info = new ArrayList<>();

    public String getData() { return myData;}

    public void setData(String data) { myData = data;}

    public String getUniqueIDTable() { return uniqueIDTable;}

    public void setUniqueIDTable(String uniqueIDTable) { this.uniqueIDTable = uniqueIDTable;}

    public int getSeatsTable() { return seatsTable;}

    public void setSeatsTable(int seatsTable) { this.seatsTable = seatsTable;}

    public ArrayList<String> getInfo(){ return info;}

    public HashMap generateTableInfo(){
        Random r = new Random();
        int result = r.nextInt(10-2) + 2;
        int id1 = tableInfo.size() + 1;
        String id = "" + id1;

        setUniqueIDTable(id);
        setSeatsTable(result);
        setData(UUID.randomUUID().toString());

        ArrayList<Object> otherInfo = new ArrayList<>();
        otherInfo.add(getSeatsTable());
        otherInfo.add(getData());

        tableInfo.put(id1, otherInfo);
        return tableInfo;
    }

    public ArrayList<String> sendToTable(){
        generateTableInfo();
        info.add(getData());
        info.add(getUniqueIDTable());
        return info;
    }

    public String sentQR(int id){
        if(tableInfo.containsKey(id)){
            ArrayList<Object> aux;
            aux = tableInfo.get(id);
            return aux.get(1).toString();
        }
        return null;
    }


    public String verifyQR(String name){
        for(int i = 0; i < tableInfo.size(); i++){
            if(tableInfo.get(i).get(1).toString().equals(name))
                return "TETAS!!";
        }
        return "no encontrei manu";
    }


}