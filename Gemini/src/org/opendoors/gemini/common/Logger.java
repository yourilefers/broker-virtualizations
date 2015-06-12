package org.opendoors.gemini.common;

import java.io.*;

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
        String temp = prefix() + " [INFO] | " + message;
        writeToLog(temp);
        if(!Config.isInitialized() || !Config.getInstance().get("debug", "info").equals("error")) {
            System.out.println(temp);
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
        String temp = prefix() + " [ERROR] | " + message;
        writeToLog(temp);
        System.err.println(temp);
        if(exit) System.exit(1);
    }

    /**
     * Print a line of debug.
     *
     * @param message
     */
    public static void debug(String message) {
        String temp = prefix() + " [DEBUG] | " + message;
        writeToLog(temp);
        if(!Config.isInitialized() || Config.getInstance().get("debug", "info").equals("debug")) {
            System.out.println(temp);
        }
    }

    /**
     * Write to the log file.
     * @param message
     */
    private static void writeToLog(String message) {
        try {

            // Open the file
            File file = new File(Config.getInstance().get("log_file", "gemini.log"));

            // If the file does not exist, create it
            if(!file.exists()){
                file.createNewFile();
            }

            // Write the file
            BufferedWriter out = new BufferedWriter(new FileWriter(file.getName(), true));
            out.write(message + "\n");
            out.close();

        } catch(IOException e) {
            System.out.println("Could not write to log file: " + e.getLocalizedMessage());
        }
    }

}
