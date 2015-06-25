package org.opendoors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by Ruben on 28-5-2015.
 */
public class Building {
    private Random r = new Random();
    private int lastTemp;
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
                    .put("name", "original_name")
                    .put("type", "String")
                    .put("value", name));

            // Temperature
            attributes.put(new JSONObject()
                    .put("name", "temperature")
                    .put("type", "float")
                    .put("value", lastTemp + ""));

            // Timestamp
            attributes.put(new JSONObject()
                    .put("name", "last_update")
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

    public void setLastTemp(int lastTemp) {
        this.lastTemp = lastTemp;
    }
}
