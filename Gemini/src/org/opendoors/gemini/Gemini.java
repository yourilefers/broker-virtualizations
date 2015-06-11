package org.opendoors.gemini;

import org.opendoors.gemini.common.Common;
import org.opendoors.gemini.common.Config;
import org.opendoors.gemini.common.Constants;
import org.opendoors.gemini.common.Logger;
import org.opendoors.gemini.network.Orion;
import org.opendoors.gemini.server.Server;
import org.opendoors.gemini.sinks.Ckan;

import java.io.IOException;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Gemini {

    /** The Gemini instance */
    private static Gemini instance;

    /** The Orion connection */
    private Orion orion;

    /** The CKAN connection */
    private Ckan ckan;

    /** The Server instance */
    private Server server;

    /**
     * Program starter.
     *
     * @param args
     */
    public static void main(String[] args) {

        // Start the main class
        Gemini gemini = Gemini.getInstance();
        gemini.startServer();

    }

    /**
     * Constructor for the Gemini class.
     */
    private Gemini() {

        // Catch
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {

                    // Unsubscribe
                    orion.unsubscribe();

                } catch(IOException e) {

                }
            }
        });

        // Setup everything
        setup();

    }

    /**
     * Return the (new) Gemini instance.
     *
     * @return
     */
    public static Gemini getInstance() {
        if(instance == null) {
            instance = new Gemini();
        }
        return instance;
    }

    /**
     * Setup Gemini
     */
    private void setup() {

        // Welcome
        Logger.info("Starting...");

        // Check the OS type
        if(Common.getOSType() < Constants.OS_MAC_OS_X) {
            Logger.error("Your system is not compatible! You need an Unix system (Linux or Mac OS X).", true);
        }

        // Initialize the config file
        Logger.info("Initializing config file...");
        if(Config.getInstance() == null) {

            // Oops?
            Logger.error("Could not get the config file.", true);

        }
        Logger.debug(Config.getInstance().toString());

        try {

            // Setup the orion connect
            Logger.info("Initializing Orion connection...");
            orion = new Orion();

        } catch(Exception e) {

            // Oops
            Logger.error(e.getLocalizedMessage(), true);

        }

        try {

            // Setup the orion connect
            Logger.info("Initializing CKAN connection...");
            ckan = new Ckan();
            ckan.setup();

        } catch(Exception e) {

            // Oops
            Logger.error(e.getLocalizedMessage(), true);

        }

    }

    /**
     * Start the internal server
     */
    private void startServer() {

        try {

            // Welcome
            Logger.info("Starting server...");

            // Create server
            server = new Server();
            server.start();

            // Index Orion types
            orion.indexTypes();
            orion.subscribe();

        } catch(IOException e) {
            Logger.error("Orion exception: " + e.getLocalizedMessage(), true);
        }

    }

    //
    // Getters
    //

    /**
     * Get the CKAN instance.
     *
     * @return
     */
    public Ckan getCkan() {
        return ckan;
    }

    /**
     * Get the Orion instance.
     *
     * @return
     */
    public Orion getOrion() {
        return orion;
    }

    /**
     * Get the Server instance.
     *
     * @return
     */
    public Server getServer() {
        return server;
    }

}
