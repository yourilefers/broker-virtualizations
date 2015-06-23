package org.opendoors.gemini;

import org.opendoors.gemini.common.*;
import org.opendoors.gemini.network.Orion;
import org.opendoors.gemini.server.Server;
import org.opendoors.gemini.network.Ckan;

import java.io.IOException;
import java.util.Arrays;

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

    /** The menu instance */
    private Menu menu;

    /**
     * Program starter.
     *
     * @param args
     */
    public static void main(String[] args) {

        // Start the main class
        Gemini.getInstance();

    }

    /**
     * Constructor for the Gemini class.
     */
    private Gemini() {

        // Catch
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Logger.debug("SHUTDOWN HOOK");

                // Exit
                exit();

            }
        });

        // Setup everything
        setup();

        // Go to the home menu
        menu.showMenu();

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
     * Exit gemini.
     */
    public void exit() {

        if(server != null) {
            try {

                Logger.debug("SHUTDOWN HOOK | UNSUBSCRIBING");

                // Unsubscribe
                orion.unsubscribe();
                server.interrupt();
                server = null;

            } catch (IOException e) {
                Logger.error("Could not stop orion: " + e.getLocalizedMessage());
            }
        }

        // Exiting?
        if(Config.getInstance().isExiting()) {

            // Shutdown
            Logger.debug("SHUTDOWN HOOK : STOPPING");

        } else {

            // Go to the home menu
            Logger.debug("SHUTDOWN HOOK : SHOW MENU");
            menu.showMenu();

        }

    }

    //
    // SETUP
    //

    /**
     * Setup Gemini
     */
    private void setup() {

        // Welcome
        Logger.info("Welcome to Gemini!");
        Logger.info("Starting...");

        // Check the OS type
        checkSystem();

        // Initialize the config file
        initializeConfig();

        // Initialize Orion
        initializeOrion();

        // Initialize CKAN
        initializeCkan();

        // Setup menu
        initializeMenu();

    }

    /**
     * Check the system type and status.
     *
     * @return
     */
    private boolean checkSystem() {

        // Check the OS type
        if(Common.getOSType() < Constants.OS_MAC_OS_X) {
            Logger.error("Your system is not compatible! You need an Unix system (Linux or Mac OS X).", true);
            return false;
        }

        return true;

    }

    /**
     * Initialize the config system.
     *
     * @return
     */
    private boolean initializeConfig() {

        Logger.info("Initializing config file...");
        if(Config.getInstance() == null) {

            // Oops?
            Logger.error("Could not get the config file.", true);
            return false;

        }
        Logger.debug("Current config:\n" + Config.getInstance().toString());
        return true;

    }

    /**
     * Initialize Orion for usage.
     *
     * @return
     */
    private boolean initializeOrion() {

        try {

            // Setup the orion connect
            Logger.info("Initializing Orion connection...");
            orion = new Orion();
            return true;

        } catch(Exception e) {

            // Oops
            e.printStackTrace();
            Logger.error(e.getLocalizedMessage(), true);
            return false;

        }

    }

    /**
     * Initialize CKAN for usage.
     *
     * @return
     */
    private boolean initializeCkan() {

        try {

            // Setup the orion connect
            Logger.info("Initializing CKAN connection...");
            ckan = new Ckan();
            ckan.setup();
            return true;

        } catch(Exception e) {

            // Oops
            e.printStackTrace();
            Logger.error(e.getLocalizedMessage(), true);
            return false;

        }

    }

    /**
     * Setup the menu.
     * @return
     */
    private boolean initializeMenu() {

        // Setup the menu
        menu = new Menu(this);
        return true;

    }

    //
    // START SEVER
    //

    /**
     * Start the internal server
     */
    public void startServer() {

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
            e.printStackTrace();
            Logger.error("Orion exception: " + e.getLocalizedMessage(), true);
        }

    }

    /**
     * Index types action.
     *
     * This action will index all types.
     */
    public void actionIndexTypes() {

        try {

            // Welcome
            Logger.debug("Indexing all types...");

            // Index Orion types
            orion.indexTypes();

            // Print all types
            Logger.info("All registered types in orion:\n" + Arrays.toString(orion.typeNames().toArray()));

        } catch(IOException e) {
            e.printStackTrace();
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
