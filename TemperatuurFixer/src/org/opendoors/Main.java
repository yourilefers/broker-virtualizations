package org.opendoors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Main {
    public final static boolean DEBUG = false;
    public final static String REQUEST_URL = "http://uitdaging.yourilefers.nl:1026/ngsi10/updateContext";

    public final static int ITEMS = 3;
    public final static String ITC = "ITC gebouw";
    public final static String STATION = "Station";
    public final static String GLASBAK = "Glasbak";

    private static int lastItem = -1;

    public static void main(String[] args) {
	    SendRequest request = new SendRequest();

        while(true) {
            long millis = System.currentTimeMillis();
            String item = getItem();
            //code to run
            System.out.print("Making call... " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + " for sensor: " + item);
            request.doRequest(item);
            System.out.println(" / Done... " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            try {
                Thread.sleep(10000 - millis % 1000);
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

        if(random == 0)
            return ITC;
        else if(random == 1)
            return STATION;
        else
            return GLASBAK;
    }
}
