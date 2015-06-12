package org.opendoors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Ruben on 28-5-2015.
 */
public class Garage {
    private Random r = new Random();
    private int maxSpots;
    private int lastFreeSpotsInt;
    private String name;
    private float lat;
    private float lng;

    public Garage(String name, int maxSpots, float lat, float lng) {
        this.name = name;
        this.maxSpots = maxSpots;
        this.lat = lat;
        this.lng = lng;

        lastFreeSpotsInt = r.nextInt(maxSpots);
    }

    /**
     * Create a new object in NGS10 style
     * @return the JSON object as a String
     */
    public String newObject() {
        String message;
        JSONObject json = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            JSONObject item = new JSONObject();
            item.put("type", "parking");
            item.put("isPattern", "false");
            item.put("id", name.replaceAll(" ", "_").toLowerCase());

            JSONArray attributes = new JSONArray();
            JSONObject attrItem = new JSONObject();
            attrItem.put("name", "original_name");
            attrItem.put("type", "String");
            attrItem.put("value", name);


            JSONObject attrItem2 = new JSONObject();
            attrItem2.put("name", "max_spots");
            attrItem2.put("type", "int");
            attrItem2.put("value", maxSpots);

            JSONObject attrItem3 = new JSONObject();
            attrItem3.put("name", "free_spots");
            attrItem3.put("type", "int");
            attrItem3.put("value", getFreeSpots());

            JSONObject attrItem4 = new JSONObject();
            attrItem4.put("name", "last_update");
            attrItem4.put("type", "unix timestamp");
            attrItem4.put("value", System.currentTimeMillis());

            JSONArray location = new JSONArray();
            JSONObject locItem1 = new JSONObject();
            locItem1.put("name", "latitude");
            locItem1.put("type", "float");
            locItem1.put("value", lat);

            JSONObject locItem2 = new JSONObject();
            locItem2.put("name", "longitude");
            locItem2.put("type", "float");
            locItem2.put("value", lng);

            JSONObject locObj = new JSONObject();
            locObj.put("name", "location");
            locObj.put("type", "array");
            locObj.put("value", location);

            location.put(locItem1);
            location.put(locItem2);

            attributes.put(attrItem);
            attributes.put(attrItem2);
            attributes.put(attrItem3);
            attributes.put(attrItem4);
            attributes.put(locObj);
            item.put("attributes", attributes);

            array.put(item);
            json.put("contextElements", array);
            json.put("updateAction", "APPEND");

            message = json.toString();

            if(Main.DEBUG)
                System.out.println(message);
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get the free spots from this object
     * @return free spots as integer
     */
    private int getFreeSpots() {
        boolean up = r.nextBoolean();

        if(Main.DEBUG) {
            System.out.println();
            System.out.println(name + " Max plekken: " + maxSpots);
            System.out.println(name + " Vrije plekken: " + lastFreeSpotsInt);
        }

        if(lastFreeSpotsInt == maxSpots) // If max free spots reached, lower int
            lastFreeSpotsInt --;
        else if(lastFreeSpotsInt <= 0) // if there are no more spots left, increase int
            lastFreeSpotsInt ++;
        else if(up) // if boolean up = true increase int
            lastFreeSpotsInt ++;
        else // nothing else to do, just lower int
            lastFreeSpotsInt --;

        if(Main.DEBUG)
            System.out.println(name + " Vrije plekken: " + lastFreeSpotsInt);

        return lastFreeSpotsInt;
    }
}
