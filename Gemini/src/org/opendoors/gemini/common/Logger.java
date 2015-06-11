package org.opendoors.gemini.common;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Logger {

    /**
     * The prefix for the logger
     * @return
     */
    private static String prefix() {
        return "Gemini " + Constants.VERSION_HUMAN + " (" + Constants.VERSION + ")";
    }

    /**
     * Print a line of info.
     *
     * @param message
     */
    public static void info(String message) {
        if(!Config.isInitialized() || !Config.getInstance().get("debug", "info").equals("error")) {
            System.out.println(prefix() + " [INFO] | " + message);
        }
    }

    /**
     * Print a line of error.
     *
     * @param message
     */
    public static void error(String message) {
        error(message, false);
    }

    /**
     * Print a line of error. You may exit.
     *
     * @param message
     */
    public static void error(String message, Boolean exit) {
        System.err.println(prefix() + " [ERROR] | " + message);
        if(exit) System.exit(1);
    }

    /**
     * Print a line of debug.
     *
     * @param message
     */
    public static void debug(String message) {
        if(!Config.isInitialized() || Config.getInstance().get("debug", "info").equals("debug")) {
            System.out.println(prefix() + " [DEBUG] | " + message);
        }
    }

}
