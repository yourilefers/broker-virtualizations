package org.opendoors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Ruben on 28-5-2015.
 */
public class CreateObject {
    private Random r = new Random();
    private int maxSpeed;
    private int speed;
    private String name;
    private float lat;
    private float lng;

    public CreateObject(String name, int maxSpeed, float lat, float lng) {
        this.name = name;
        this.maxSpeed = maxSpeed;
        this.speed = maxSpeed;
        this.lat = lat;
        this.lng = lng;
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
            item.put("type", "fuss");
            item.put("isPattern", "false");
            item.put("id", name.replaceAll(" ", "_").toLowerCase());

            JSONArray attributes = new JSONArray();
            JSONObject attrItem = new JSONObject();
            attrItem.put("name", "originalName");
            attrItem.put("type", "String");
            attrItem.put("value", name);

            JSONObject attrItem2 = new JSONObject();
            attrItem2.put("name", "maxSpeed");
            attrItem2.put("type", "int");
            attrItem2.put("value", maxSpeed);

            JSONObject attrItem3 = new JSONObject();
            attrItem3.put("name", "currentSpeed");
            attrItem3.put("type", "int");
            attrItem3.put("value", getCurrentSpeed());

            JSONObject attrItem4 = new JSONObject();
            attrItem4.put("name", "lastUpdate");
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

            if(org.opendoors.Main.DEBUG)
                System.out.println(message);
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get the current speed that cars drive on this road
     * @return current speed as an integer
     */
    private int getCurrentSpeed() {
        boolean up = r.nextBoolean();

        if(org.opendoors.Main.DEBUG) {
            System.out.println();
            System.out.println(name + " Max snelheid: " + maxSpeed);
            System.out.println(name + " Huidige snelheid: " + speed);
        }

        if(speed == maxSpeed) // If max speed reached, lower int
            speed --;
        else if(speed <= 0) // if speed is zero, increase int
            speed ++;
        else if(up) // if boolean up = true increase int
            speed ++;
        else // nothing else to do, just lower int
            speed --;

        if(Main.DEBUG)
            System.out.println(name + " Huidige snelheid: " + speed);

        return speed;
    }
}
