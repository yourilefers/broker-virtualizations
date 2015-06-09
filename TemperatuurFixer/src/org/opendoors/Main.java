package org.opendoors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Main {

    // DEBUG
    public final static boolean DEBUG = false;
    public final static boolean NETWORK_REQUEST = true;

    // The URL
    public final static String REQUEST_URL = "http://uitdaging.yourilefers.nl:1026/ngsi10/updateContext";


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
        Random r = new Random();

        // Timings
        int sec = 1000;
        int minSleep = 1 * sec;
        int maxSleep = 10 * sec;

        while(true) {

            // Current time
            long millis = System.currentTimeMillis();

            // The item to send
            Building item = getItem();

            //code to run
            System.out.print("Making call... " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + " for sensor: " + item);
            request.doRequest(item);
            System.out.println(" / Done... " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));

            // DEBUG
            if(DEBUG) System.out.println("\n----------------------------------------------------------------------------------------------------\n");

            // Sleep
            try {
                Thread.sleep(r.nextInt(maxSleep - minSleep) + minSleep - millis % 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Get the sensor that has to be updated (never the same twice)
     * @return sensor that has to be updated
     */
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
}
