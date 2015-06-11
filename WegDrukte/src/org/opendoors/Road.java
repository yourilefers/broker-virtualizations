package org.opendoors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ruben on 28-5-2015.
 */
public class Road {
    private Random r = new Random();
    private int maxSpeed;
    private int speed;
    private String name;
    private Coordinates[] coords;

    public Road(String name, int maxSpeed, Coordinates[] coords) {
        this.name = name;
        this.maxSpeed = maxSpeed;
        this.speed = maxSpeed;
        this.coords = coords;
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
                .put("type", "trafficDensity")
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

            // Add all locations to the array
            JSONArray jsonArrayLocations = new JSONArray();
            for(Coordinates coordinate : coords) {
                jsonArrayLocations.put(
                        new JSONArray()
                                .put(new JSONObject()
                                        .put("name", "latitude")
                                        .put("type", "float")
                                        .put("value", coordinate.getLat()))
                                .put(new JSONObject()
                                        .put("name", "longitude")
                                        .put("type", "float")
                                        .put("value", coordinate.getLng())
                        ));
            }

            // Add the location
            attributes.put(new JSONObject()
                    .put("name", "locations")
                    .put("type", "array")
                    .put("value", jsonArrayLocations));

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
            System.err.println("Could not create the JSON object to POST");
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
