package org.opendoors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class Main {

    // DEBUG
    public final static boolean DEBUG = true;
    public final static boolean NETWORK_REQUEST = true;

    // The URL
    public final static String REQUEST_URL = "http://uitdaging.yourilefers.nl:1026/ngsi10/updateContext";

    // Westerval coördinates
    private final static Coordinates[] WESTERVAL_COORDS = {
            new Coordinates((float) 52.218178, (float) 6.850748),
            new Coordinates((float) 52.219294, (float) 6.862204),
            new Coordinates((float) 52.218509, (float) 6.868716),
            new Coordinates((float) 52.218020, (float) 6.873274)
    };

    // Zuiderval coördinates
    private final static Coordinates[] ZUIDERVAL_COORDS = {
            new Coordinates((float) 52.200641, (float) 6.890805),
            new Coordinates((float) 52.209930, (float) 6.889208)
    };

    // Haaksbergerstraat coördinates
    private final static Coordinates[] HAAKSBERGERSTRAAT_COORDS = {
            new Coordinates((float) 52.205525, (float) 6.860787),
            new Coordinates((float) 52.207269, (float) 6.869969),
            new Coordinates((float) 52.208022, (float) 6.871729),
            new Coordinates((float) 52.211017, (float) 6.879057)
    };

    // Hengelosestraat coördinates
    private final static Coordinates[] HENGELOSESTRAAT_COORDS = {
            new Coordinates((float) 52.233093, (float) 6.862831),
            new Coordinates((float) 52.229996, (float) 6.870947),
            new Coordinates((float) 52.226461, (float) 6.880242)
    };

    // Deurningerstraat coördinates
    private final static Coordinates[] DEURNINGERSTRAAT_COORDS = {
            new Coordinates((float) 52.237652, (float) 6.884490),
            new Coordinates((float) 52.234935, (float) 6.885563),
            new Coordinates((float) 52.230601, (float) 6.888857),
            new Coordinates((float) 52.228446, (float) 6.889984)
    };

    // Tubantiasingel coördinates
    private final static Coordinates[] TUBANTIASINGEL_COORDS = {
            new Coordinates((float) 52.221218, (float) 6.878563),
            new Coordinates((float) 52.223119, (float) 6.878616),
            new Coordinates((float) 52.225482, (float) 6.879163),
            new Coordinates((float) 52.226297, (float) 6.880558)
    };

    // The list of roads
    public final static Road[] ROADS = {
            new Road("Westerval", 80, WESTERVAL_COORDS),
            new Road("Zuiderval", 80, ZUIDERVAL_COORDS),
            new Road("Haaksbergerstraat", 80, HAAKSBERGERSTRAAT_COORDS),
            new Road("Hengelosestraat", 50, HENGELOSESTRAAT_COORDS),
            new Road("Deurningerstraat", 50, DEURNINGERSTRAAT_COORDS),
            new Road("Tubantiasingel", 50, TUBANTIASINGEL_COORDS)
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

        // DEBUG
        if(Main.DEBUG) System.out.println("Item int -> " + random);

        // Never return the same sensor twice (or more..)
        if(random == lastItem) return getItem();
        else lastItem = random;

        // Return a garage
        return ROADS[random];

    }
}
