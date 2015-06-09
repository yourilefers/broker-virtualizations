package org.opendoors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Main {

    // Debug?
    public final static boolean DEBUG = false;
    public final static boolean NETWORK_REQUEST = true;

    // The request URL
    public final static String REQUEST_URL = "http://uitdaging.yourilefers.nl:1026/NGSI10/updateContext";

    // All garages
    public final static Garage[] GARAGES = {
            new Garage("Medisch Spectrum Twente", 1000, (float) 52.215253, (float) 6.890297),
            new Garage("Ledeboerpark", 1000, (float) 52.216039, (float) 6.899552),
            new Garage("Van Heekgarage", 1650, (float) 52.217144, (float) 6.898072),
            new Garage("Q-park Enschede", 1000, (float) 52.218925, (float) 6.892128),
            new Garage("Q-park Stationsplein", 1000, (float) 52.221723, (float) 6.892316),
            new Garage("Irene", 525, (float) 52.222361, (float) 6.898219)
    };

    // The last item
    private static int lastItem = -1;

    public static void main(String[] args) {

        // The request object to be used
        SendRequest request = new SendRequest();

        // The randomizer
        Random r = new Random();

        // Waiting times
        int sec = 1000;
        int minSleep = 1 * sec;
        int maxSleep = 10 * sec;

        while(true) {

            // Current time
            long millis = System.currentTimeMillis();

            // Get a new (random) item
            Garage item = getItem();

            // Make the request
            System.out.print("Making call... " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + " for sensor: " + item);
            request.doRequest(item);
            System.out.println(" / Done... " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));

            // Debug
            if(DEBUG) System.out.println("\n----------------------------------------------------------------------------------------------------\n");

            // Try to sleep
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
    private static Garage getItem() {

        // The randomizer to use
        Random r = new Random();
        int random = r.nextInt(GARAGES.length);

        // Debug
        if(Main.DEBUG) System.out.println("Item int -> " + random);

        // Never return the same sensor twice (or more..)
        if(random == lastItem) return getItem();
        else lastItem = random;

        // Return a garage
        return GARAGES[random];
    }
}
