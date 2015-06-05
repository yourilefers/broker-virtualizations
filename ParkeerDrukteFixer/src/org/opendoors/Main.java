package org.opendoors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Main {
    public final static boolean DEBUG = false;
    public final static boolean NETWORK_REQUEST = true;
    public final static String REQUEST_URL = "http://uitdaging.yourilefers.nl:1026/NGSI10/updateContext";

    public final static int ITEMS = 6;
    public final static String GARAGE_MST = "Medisch Spectrum Twente";
    public final static String GARAGE_LEDEBOER = "Ledeboerpark";
    public final static String GARAGE_HEEKPLEIN = "Van Heekgarage";
    public final static String GARAGE_QPARK_ENSCHEDE = "Q-park Enschede";
    public final static String GARAGE_QPARK_STATION = "Q-park Stationsplein";
    public final static String GARAGE_IRENE = "Irene";

    private static int lastItem = -1;

    public static void main(String[] args) {
        SendRequest request = new SendRequest();

        Random r = new Random();
        int sec = 1000;
        int minSleep = 1 * sec;
        int maxSleep = 10 * sec;
        while(true) {
            long millis = System.currentTimeMillis();
            String item = getItem();
            //code to run
            System.out.print("Making call... " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + " for sensor: " + item);
            request.doRequest(item);
            System.out.println(" / Done... " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            if(DEBUG)
                System.out.println("\n----------------------------------------------------------------------------------------------------\n");
            try {
                int sleep = r.nextInt(maxSleep - minSleep) + minSleep;
                Thread.sleep(sleep - millis % 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }//While
    }

    /**
     * Get the sensor that has to be updated (never the same twice)
     * @return sensor that has to be updated
     */
    private static String getItem() {
        Random r = new Random();
        int random = r.nextInt(ITEMS);

        if(Main.DEBUG)
            System.out.println("Item int -> " + random);

        //Never return the same sensor twice (or more..)
        if(random == lastItem)
            return getItem();
        else
            lastItem = random;

        switch (random) {
            case 0: return GARAGE_MST;
            case 1: return GARAGE_LEDEBOER;
            case 2: return GARAGE_HEEKPLEIN;
            case 3: return GARAGE_QPARK_ENSCHEDE;
            case 4: return GARAGE_QPARK_STATION;
            default: return GARAGE_IRENE;
        }
    }
}
