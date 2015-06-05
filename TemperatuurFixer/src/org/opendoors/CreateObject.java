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
    private int lastTemp = r.nextInt(25-0) + 0;
    private int id;
    private float lat;
    private float lng;

    public CreateObject(int id, float lat, float lng) {
        this.id = id;
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
            item.put("type", "temperature");
            item.put("isPattern", "false");
            item.put("id", "temperature" + id);

            JSONArray attributes = new JSONArray();
            JSONObject attrItem = new JSONObject();
            attrItem.put("name", "temperature" + id);
            attrItem.put("type", "float");
            attrItem.put("value", getTemperature() + "");

            JSONObject attrItem2 = new JSONObject();
            attrItem2.put("name", "lastUpdate");
            attrItem2.put("type", "unix timestamp");
            attrItem2.put("value", System.currentTimeMillis());

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
     * Get the current temperature from this object
     * @return temperature as integer
     */
    private int getTemperature() {
        int high;
        int low;

        if(lastTemp - 3 > - 3)
            low = lastTemp - 3;
        else
            low = 0;
        if(lastTemp + 3 < 30)
            high = lastTemp + 3;
        else
            high = 25;
        int R = r.nextInt(high-low) + low;
        return R;
    }
}
