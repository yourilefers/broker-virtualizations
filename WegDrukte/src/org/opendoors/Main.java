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

    // The list of roads
    public final static Road[] ROADS = {
            new Road("Westerval", 80,(float) 52.218364, (float) 6.869316),
            new Road("Zuiderval", 80, (float) 52.208590, (float) 6.889442),
            new Road("Haaksbergerstraat", 80, (float) 52.210550, (float) 6.878260),
            new Road("Hengelosestraat", 50, (float) 52.227166, (float) 6.878294),
            new Road("Deurningerstraat", 50, (float) 52.229364, (float) 6.889527)
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
            Road item = getItem();

            // Make the call
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
    private static Road getItem() {

        // The randomizer to use
        Random r = new Random();
        int random = r.nextInt(ROADS.length);

        // Debug
        if(Main.DEBUG) System.out.println("Item int -> " + random);

        // Never return the same sensor twice (or more..)
        if(random == lastItem) return getItem();
        else lastItem = random;

        // Return a garage
        return ROADS[random];

    }
}
