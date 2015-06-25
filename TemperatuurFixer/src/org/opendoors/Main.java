package org.opendoors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Main {

    // DEBUG
    public final static boolean DEBUG = false;
    public final static boolean NETWORK_REQUEST = true;

    // The URL
    public final static String REQUEST_URL = "http://uitdaging.yourilefers.nl:1026/ngsi10/updateContext";
    public final static String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&units=metric";

    public final static int TEN_MINUTES = 600000;

    public final static float LAT_ENS = 52.219623f;
    public final static float LON_ENS = 6.885874f;

    // The list of buildings
    public final static Building[] BUILDINGS = {
            new Building("ITC gebouw", (float) 52.223852, (float) 6.885874),
            new Building("Station", (float) 52.222387, (float) 6.890195),
            new Building("Glasbak", (float) 52.219623, (float) 6.889610)
    };

    // The last random item
    private static int lastItem = -1;

    public static void main(String[] args) {
        // Setup vars
        SendRequest request = new SendRequest();

        while(true) {

            // Current time
            long millis = System.currentTimeMillis();

            int temp = getTemperature();
            System.out.println(temp);
            // The item to send
            for (Building b : BUILDINGS) {
                //code to run
                b.setLastTemp(temp);
                System.out.print("Making call... " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + " for sensor: " + b );
                request.doRequest(b);
                System.out.println(" / Done... " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            }

            // DEBUG
            if(DEBUG) System.out.println("\n----------------------------------------------------------------------------------------------------\n");

            // Sleep
            try {
                Thread.sleep(TEN_MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Get the sensor that has to be updated (never the same twice)
     * @return sensor that has to be updated
     */
    @Deprecated
    private static Building getItem() {

        // The randomizer to use
        Random r = new Random();
        int random = r.nextInt(BUILDINGS.length);

        // DEBUG
        if(Main.DEBUG) System.out.println("Item int -> " + random);

        // Never return the same sensor twice (or more..)
        if(random == lastItem) return getItem();
        else lastItem = random;

        // Return a Building
        return BUILDINGS[random];
    }


    /**
     * Method that gets the current wheather data for LAT_ENS & LON_ENS
     * From the openwheathermaps api at WEATHER_URL
     * @return the current temperature for LAT_ENS & LON_ENS
     */
    private static int getTemperature() {
        int lastTemp = Integer.MIN_VALUE;
        try {
            //create http client to make a request
            HttpClient httpClient = HttpClientBuilder
                    .create()
                    .build();

            //create the get request
            HttpGet request = new HttpGet(Main.WEATHER_URL.replace("{lat}", "" + LAT_ENS).replace("{lon}", "" + LON_ENS));
            HttpResponse response = httpClient.execute(request);

            //read the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            //make a string of the result
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            try {

                //update the temps from json
                JSONObject weatherdata = new JSONObject(result.toString());
                lastTemp = (int) Math.round((weatherdata.getJSONObject("main").getDouble("temp")));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert (lastTemp != Integer.MIN_VALUE);
        return lastTemp;
    }
}
