package org.opendoors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Ruben on 28-5-2015.
 */
public class Building {
    private Random r = new Random();
    private int lastTemp = r.nextInt(25-0) + 0;
    private String name;
    private float lat;
    private float lng;

    public Building(String name, float lat, float lng) {
        this.name = name;
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
                    .put("type", "temperature")
                    .put("isPattern", "false")
                    .put("id", name.replaceAll(" ", "_").toLowerCase());

            // The list of attributes
            JSONArray attributes = new JSONArray();

            // The name
            attributes.put(new JSONObject()
                    .put("name", "originalName")
                    .put("type", "String")
                    .put("value", name));

            // Temperature
            attributes.put(new JSONObject()
                    .put("name", "temperature")
                    .put("type", "float")
                    .put("value", getTemperature() + ""));

            // Timestamp
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
            if(Main.DEBUG) System.out.println(json.toString());
            return json.toString();

        } catch (JSONException e) {
            System.err.println("Could not create the JSON object to POST");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the current temperature from this object
     * @return temperature as integer
     */
    private int getTemperature() {
        int high;
        int low;

        if(lastTemp - 3 > - 3) // If temp can be lower, set low to curr - 3
            low = lastTemp - 3;
        else // Else temp is already too low, set low to 0
            low = 0;
        if(lastTemp + 3 < 30) // If temp may be higher, set max to curr + 3
            high = lastTemp + 3;
        else // Else temp is already too high, set max to 25
            high = 25;

        lastTemp = r.nextInt(high-low) + low;
        return lastTemp;
    }
}
