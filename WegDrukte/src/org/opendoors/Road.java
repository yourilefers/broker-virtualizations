package org.opendoors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Ruben on 28-5-2015.
 */
public class Road {
    private Random r = new Random();
    private int maxSpeed;
    private int speed;
    private String name;
    private float lat;
    private float lng;

    public Road(String name, int maxSpeed, float lat, float lng) {
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
        try {

            // The main array
            JSONArray array = new JSONArray();

            // Main item
            JSONObject item = new JSONObject()
                .put("type", "fuss")
                .put("isPattern", "false")
                .put("id", name.replaceAll(" ", "_").toLowerCase());

            // The list of attributes
            JSONArray attributes = new JSONArray();

            // The name
            attributes.put(new JSONObject()
                    .put("name", "originalName")
                    .put("type", "String")
                    .put("value", name));

            // Max speed
            attributes.put(new JSONObject()
                    .put("name", "maxSpeed")
                    .put("type", "int")
                    .put("value", maxSpeed));

            // Current speed
            attributes.put(new JSONObject()
                    .put("name", "currentSpeed")
                    .put("type", "int")
                    .put("value", getCurrentSpeed()));

            //  Timestamp
            attributes.put(new JSONObject()
                    .put("name", "lastUpdate")
                    .put("type", "unix timestamp")
                    .put("value", System.currentTimeMillis()));

            // Add the location
            attributes.put(new JSONObject()
                    .put("name", "location")
                    .put("type", "array")
                    .put("value", new JSONArray()
                            .put(new JSONObject()
                                    .put("name", "latitude")
                                    .put("type", "float")
                                    .put("value", lat))
                            .put(new JSONObject()
                                    .put("name", "longitude")
                                    .put("type", "float")
                                    .put("value", lng))
                    ));

            // Set attributes on item
            item.put("attributes", attributes);

            // Add the item
            array.put(item);

            // Create the main object
            JSONObject json = new JSONObject();
            json.put("contextElements", array);
            json.put("updateAction", "APPEND");

            // DEBUG
            if(org.opendoors.Main.DEBUG) System.out.println(json.toString());
            return json.toString();

        } catch (JSONException e) {
            System.out.println("Could not create the JSON object to POST");
            e.printStackTrace();
        }
        return null;
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
